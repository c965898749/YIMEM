package com.sy.service.impl;

import cn.hutool.json.JSONUtil;
import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.sy.entity.ActivationKey;
import com.sy.mapper.ActivationKeyMapper;
import com.sy.mapper.BlogReplayMapper;
import com.sy.mapper.UserMapper;
import com.sy.model.*;
import com.sy.model.resp.BaseResp;
import com.sy.model.weixin.*;
import com.sy.service.*;
import com.sy.tool.*;
import com.thoughtworks.xstream.XStream;
import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.util.EntityUtils;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.ValueOperations;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;
import java.io.*;
import java.net.HttpURLConnection;
import java.net.URL;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.concurrent.*;
import java.util.stream.Collectors;

@Service
public class WeixinPostServiceImpl implements WeixinPostService {
    @Autowired
    private UserServic userServic;
    @Autowired
    private SearchService searchService;
    @Autowired
    private ActivationKeyMapper activationKeyMapper;
    @Autowired
    private BlogReplayMapper blogReplayService;
    private Logger log = Logger.getLogger(WeixinPostServiceImpl.class.getName());
    @Autowired
    public RedisTemplate redisTemplate;
    @Autowired
    private UserMapper userMapper;
    /**
     * 处理微信发来的请求
     *
     * @param request
     * @return
     */
    public String weixinPost(HttpServletRequest request) {
        String respMessage = null;
        log.info("处理微信发来的请求");
        try {

            // xml请求解析
            Map<String, String> requestMap = MessageUtil.xmlToMap(request);

            // 发送方帐号（open_id）
            String fromUserName = requestMap.get("FromUserName");
            // 公众帐号
            String toUserName = requestMap.get("ToUserName");
            // 消息类型
            String msgType = requestMap.get("MsgType");
            // 消息内容
            String content = requestMap.get("Content");
            // 事件KEY值，是一个32位无符号整数，即创建二维码时的二维码scene_id
            String EventKey = requestMap.get("EventKey");

            log.info("FromUserName is:" + fromUserName + ", ToUserName is:" + toUserName + ", MsgType is:" + msgType);

            // 文本消息
            if (msgType.equals(MessageUtil.REQ_MESSAGE_TYPE_TEXT)) {
                //这里根据关键字执行相应的逻辑，只有你想不到的，没有做不到的
                //自动回复
                TextMessage text = new TextMessage();
                text.setMsgType(msgType);
                System.out.println(content);
                if (content.contains("天卡")||content.contains("月卡")||content.contains("永久卡")||content.contains("机器码")) {
                    ActivationKey key=new ActivationKey();
                    text.setContent("激活码已过期，请重新启动辅助点确认");
                    text.setMsgType(MessageUtil.RESP_MESSAGE_TYPE_TEXT);
                    if (content.contains("天卡")){
                        int index = content.indexOf("天卡");
                        content = content.substring(0, index);
                        content=content.trim();
                        key.setCode(content);
                        key.setType("1");
                        ActivationKey activationKey=activationKeyMapper.queryBytype(key);
                        if (activationKey!=null&&Xtool.isNotNull(activationKey.getActCode())){
                            text.setContent(activationKey.getActCode());
                        }
                    }
                    if (content.contains("月卡")){
                        int index = content.indexOf("月卡");
                        content = content.substring(0, index);
                        content=content.trim();
                        key.setCode(content);
                        key.setType("2");
                        ActivationKey activationKey=activationKeyMapper.queryBytype(key);
                        if (activationKey!=null&&Xtool.isNotNull(activationKey.getActCode())){
                            text.setContent(activationKey.getActCode());
                        }
                    }

                    if (content.contains("机器码")) {
                        int index = content.indexOf("机器码");
                        content = content.substring(0, index);
                        content = content.trim();
                        key.setCode(content);
                        key.setType("2");
                        ActivationKey cc = new ActivationKey();
                        cc.setOpenId(fromUserName);
                        cc.setType("2");
                        ActivationKey activationKeyOld = activationKeyMapper.queryByOpenId(cc);
                        if (activationKeyOld != null && !activationKeyOld.getCode().equals(content)) {
                            text.setContent("该微信已绑定【 " + activationKeyOld.getCode() + " 】机器码\n请勿再绑定其他机器码");
                        } else if (activationKeyOld != null && activationKeyOld.getCode().equals(content)){
                            text.setContent(activationKeyOld.getActCode());
                        }else {
                            cc.setCode(content);
                            ActivationKey activationKey=activationKeyMapper.queryBytype(key);
                            if (activationKey!=null&&Xtool.isNotNull(activationKey.getActCode())){
                                text.setContent(activationKey.getActCode());
                                activationKey.setOpenId(fromUserName);
                                activationKey.setStatus("1");
                                activationKeyMapper.updateOpenId(activationKey);
                            } else {
                                text.setContent("激活码已经使用有效期.\n请启动辅助点击【启动浮窗】！弹出机器码后再来获取激活码");
                            }

                        }

                    }
                    if (content.contains("删码")){
                        int index = content.indexOf("删码");
                        content = content.substring(0, index);
                        content = content.trim();
                        ActivationKey cc = new ActivationKey();
                        cc.setCode(content);
                        cc.setType("2");
                        activationKeyMapper.remove(cc);
                        text.setContent("已成功删除机器码");
                    }
                    if (content.contains("永久卡")){
                        int index = content.indexOf("永久卡");
                        content = content.substring(0, index);
                        content=content.trim();
                        key.setCode(content);
                        key.setType("3");
                        ActivationKey activationKey=activationKeyMapper.queryBytype(key);
                        if (activationKey!=null&&Xtool.isNotNull(activationKey.getActCode())){
                            text.setContent(activationKey.getActCode());
                        }
                    }
                }else if (content.contains("再次激活")) {
                    ActivationKey key=new ActivationKey();
                    int index = content.indexOf("再次激活");
                    content = content.substring(0, index);
                    content = content.trim();
                    key.setRandomCode(content);
                    key.setType("2");
                    key.setOpenId(fromUserName);
                    if (activationKeyMapper.activationUpdate(key)>0){
                        text.setContent("谢谢使用");
                    }else {
                        text.setContent("激活失败或已激活");
                    }
                    text.setMsgType(MessageUtil.RESP_MESSAGE_TYPE_TEXT);
                }else if (content.contains("今日四位码")) {
                    ActivationKey activationKeyOld = activationKeyMapper.queryNew();
                    text.setContent(activationKeyOld.getRandomCode()+"再次激活");
                    text.setMsgType(MessageUtil.RESP_MESSAGE_TYPE_TEXT);
                }else if (content.contains("请复制本消息并打开VMOS。防盗密钥")) {
                    // 找到字符'A'的位置
                    int index = content.indexOf(":");
                    content = content.substring(index + 1);
                    // 找到字符'A'的位置
                    index = content.indexOf("WeiXin");
                    if (index>0){
                        content = content.substring(0, index);
                    }else {
                        index = content.indexOf("Now");
                        if (index>0){
                            content = content.substring(0, index);
                        }
                    }
                    content=content.trim();
                    text.setContent(content);
                    text.setMsgType(MessageUtil.RESP_MESSAGE_TYPE_TEXT);
                    BlogReplay replay=new BlogReplay();
                    replay.setBlogid(119);
                    replay.setComment(content);
                    replay.setCommentuserid(1);
                    replay.setTime(new Date());
                    replay.setStatus(0);
                    replay.setBlogReplayId(0);
                    replay.setSonreplaycount(0);
                    blogReplayService.addReplay(replay);
                }else if (content.contains("@126.com")) {
                    // 找到字符'A'的位置
                    content=content.trim();
                    content=content+"----c866971331@";
                    text.setContent(content);
                    text.setMsgType(MessageUtil.RESP_MESSAGE_TYPE_TEXT);
                    BlogReplay replay=new BlogReplay();
                    replay.setBlogid(119);
                    replay.setComment(content);
                    replay.setCommentuserid(1);
                    replay.setTime(new Date());
                    replay.setStatus(0);
                    replay.setBlogReplayId(0);
                    replay.setSonreplaycount(0);
                    blogReplayService.addReplay(replay);
                } else if (content.equals("广告") || content.equals("广告租用")) {
                    String tt = "ଘ(੭ˊᵕˋ)੭* ੈ✩如需本网站黄金c位广告位\n可联系电话:---------\n或加微信:----------";
                    text.setContent(tt);
                } else if (content.equals("App资源") || content.equals("app资源")) {
                    String message = null;
                    Image image = new Image();
                    AccessToken token = this.getAccessToken(toUserName);
//                        log.info("access_token为---------"+token.getToken());
                    String path = request.getSession().getServletContext().getRealPath("/imgs/saoma/app.png");
                    image.setMediaId(this.upload(path, token.getToken(), "image"));
//                        log.info("MediaId为---------"+image.getMediaId());
                    ImageMessage imageMessage = new ImageMessage();
                    imageMessage.setFromUserName(toUserName);
                    imageMessage.setToUserName(fromUserName);
                    imageMessage.setMsgType("image");
                    imageMessage.setCreateTime(System.currentTimeMillis()/1000);
                    imageMessage.setImage(image);
                    message = MessageUtil.textMessageToXml(imageMessage);
                    return message;
                } else if (content.equals("我的音乐")||content.equals("6")) {
                    text.setContent("<a href='http://www.yimem.com/Cell.html?type='mymusic''>点击播放</a>");
                    text.setToUserName(fromUserName);
                    text.setFromUserName(toUserName);
                    text.setCreateTime(System.currentTimeMillis()/1000);
                    text.setMsgType(MessageUtil.RESP_MESSAGE_TYPE_TEXT);
                    respMessage = MessageUtil.textMessageToXml(text);

                }else if (content.equals("笑话大全")||content.equals("3")) {
                    text.setContent(JokesUtil.printC());
                    text.setToUserName(fromUserName);
                    text.setFromUserName(toUserName);
                    text.setCreateTime(System.currentTimeMillis()/1000);
                    text.setMsgType(MessageUtil.RESP_MESSAGE_TYPE_TEXT);
                    respMessage = MessageUtil.textMessageToXml(text);

                }else if (content.equals("小梦")||content.equals("1")) {
                    NewsMessage newsMessage = new NewsMessage();
                    newsMessage.setToUserName(fromUserName);
                    newsMessage.setFromUserName(toUserName);
                    newsMessage.setMsgType("news");
                    newsMessage.setCreateTime(System.currentTimeMillis()/1000);
                    newsMessage.setArticleCount(1);
                    List<Article> articles = new ArrayList<>();
                    Article article = new Article();
                    article.setTitle("YIMEM个人网站");
                    article.setDescription("小梦的个人中心，个人笔记、学习记录");
                    article.setUrl("http://www.yimem.com");
                    article.setPicUrl("http://www.yimem.com/imgs/gz/gz.jpg");
                    articles.add(article);
                    newsMessage.setArticles(articles);
                    //XStream将Java对象转换成xml字符串
                    XStream xStream = new XStream();
                    xStream.processAnnotations(NewsMessage.class);
                    String xml = xStream.toXML(newsMessage);
                    return xml;
                }else if (content.equals("图片文字识别")||content.equals("2")) {
                    text.setContent("您发送一个带文字的图片，小梦就可以帮您识别文字！");
                    text.setToUserName(fromUserName);
                    text.setFromUserName(toUserName);
                    text.setCreateTime(System.currentTimeMillis()/1000);
                    text.setMsgType(MessageUtil.RESP_MESSAGE_TYPE_TEXT);
                    respMessage = MessageUtil.textMessageToXml(text);
                }else if (content.equals("谜语大全")||content.equals("4")) {
                    text.setContent(RddleUtil.getRddle());
                    text.setToUserName(fromUserName);
                    text.setFromUserName(toUserName);
                    text.setCreateTime(System.currentTimeMillis()/1000);
                    text.setMsgType(MessageUtil.RESP_MESSAGE_TYPE_TEXT);
                    respMessage = MessageUtil.textMessageToXml(text);
                }else if (content.equals("心灵鸡汤")||content.equals("5")) {
                    text.setContent(SoupUtil.getSoup());
                    text.setToUserName(fromUserName);
                    text.setFromUserName(toUserName);
                    text.setCreateTime(System.currentTimeMillis()/1000);
                    text.setMsgType(MessageUtil.RESP_MESSAGE_TYPE_TEXT);
                    respMessage = MessageUtil.textMessageToXml(text);
                }else if (content.equals("激活码")||content.equals("7")) {
                    text.setContent("输入格式: XXXXXXXXX机器码");
                    text.setToUserName(fromUserName);
                    text.setFromUserName(toUserName);
                    text.setCreateTime(System.currentTimeMillis()/1000);
                    text.setMsgType(MessageUtil.RESP_MESSAGE_TYPE_TEXT);
                    respMessage = MessageUtil.textMessageToXml(text);
                }else {
                    String reslut= getTextMessage(content).toString();
                    if (Xtool.isNull(reslut)){
                        text.setContent(this.getResult(content, fromUserName));
                    }else {
                        text.setContent(reslut);
                    }
                }
                text.setToUserName(fromUserName);
                text.setFromUserName(toUserName);
                text.setCreateTime(System.currentTimeMillis()/1000);

                respMessage = MessageUtil.textMessageToXml(text);


            }
            //开启微信声音识别测试 2015-3-30
            //语音识别微信已经下架
            else if (msgType.equals("voice")) {
                String recvMessage = requestMap.get("Recognition");
                //respContent = "收到的语音解析结果："+recvMessage;
                log.info("微信声音"+recvMessage);
                TextMessage text = new TextMessage();
                text.setMsgType(MessageUtil.REQ_MESSAGE_TYPE_TEXT);
                if (Xtool.isNotNull(recvMessage)) {
                    log.info("微信声音识别--------" + recvMessage);
                    recvMessage = recvMessage.substring(0, recvMessage.length() - 1);
//                    respContent = TulingApiProcess.getTulingResult(recvMessage);
                    if (recvMessage.equals("账号绑定") || recvMessage.equals("账号") || recvMessage.equals("绑定") || recvMessage.equals("绑账号") || recvMessage.equals("绑")) {
                        if (Constants.TO_USER_NAME.equals(toUserName)) {
                            String tt = "请点击下方菜单 绑定账号";
                            text.setContent(tt);
                            text.setToUserName(fromUserName);
                            text.setFromUserName(toUserName);
                            text.setCreateTime(System.currentTimeMillis()/1000);
                            text.setMsgType(MessageUtil.RESP_MESSAGE_TYPE_TEXT);
                            respMessage = MessageUtil.textMessageToXml(text);
                        } else {
                            String message = null;
                            Image image = new Image();
                            AccessToken token = this.getAccessToken(toUserName);
//                        log.info("access_token为---------"+token.getToken());
                            String path = request.getSession().getServletContext().getRealPath("/imgs/gz/eduwxfix.png");
                            image.setMediaId(this.upload(path, token.getToken(), "image"));
//                        log.info("MediaId为---------"+image.getMediaId());
                            ImageMessage imageMessage = new ImageMessage();
                            imageMessage.setFromUserName(toUserName);
                            imageMessage.setToUserName(fromUserName);
                            imageMessage.setMsgType("image");
                            imageMessage.setCreateTime(System.currentTimeMillis()/1000);
                            imageMessage.setImage(image);
                            message = MessageUtil.textMessageToXml(imageMessage);
                            return message;
                        }
                    } else if (recvMessage.equals("广告") || recvMessage.equals("广告租用")) {
                        String tt = "ଘ(੭ˊᵕˋ)੭* ੈ✩如需本网站黄金c位广告位\n可联系电话:--------\n或加微信:---------";
                        text.setContent(tt);
                        text.setToUserName(fromUserName);
                        text.setFromUserName(toUserName);
                        text.setCreateTime(System.currentTimeMillis()/1000);
                        text.setMsgType(MessageUtil.RESP_MESSAGE_TYPE_TEXT);
                        respMessage = MessageUtil.textMessageToXml(text);
                    } else if (recvMessage.equals("App资源") || recvMessage.equals("app资源")) {
                        String message = null;
                        Image image = new Image();
                        AccessToken token = this.getAccessToken(toUserName);
//                        log.info("access_token为---------"+token.getToken());
                        String path = request.getSession().getServletContext().getRealPath("/imgs/saoma/app.png");
                        image.setMediaId(this.upload(path, token.getToken(), "image"));
//                        log.info("MediaId为---------"+image.getMediaId());
                        ImageMessage imageMessage = new ImageMessage();
                        imageMessage.setFromUserName(toUserName);
                        imageMessage.setToUserName(fromUserName);
                        imageMessage.setMsgType("image");
                        imageMessage.setCreateTime(System.currentTimeMillis()/1000);
                        imageMessage.setImage(image);
                        message = MessageUtil.textMessageToXml(imageMessage);
                        return message;
                    } else if (recvMessage.equals("我的音乐")||recvMessage.equals("6")) {
                        text.setContent("<a href='http://www.yimem.com/Cell.html?type='mymusic''>点击播放</a>");
                        text.setToUserName(fromUserName);
                        text.setFromUserName(toUserName);
                        text.setCreateTime(System.currentTimeMillis()/1000);
                        text.setMsgType(MessageUtil.RESP_MESSAGE_TYPE_TEXT);
                        respMessage = MessageUtil.textMessageToXml(text);

                    }else if (recvMessage.equals("笑话大全")||recvMessage.equals("3")) {
                        text.setContent(JokesUtil.printC());
                        text.setToUserName(fromUserName);
                        text.setFromUserName(toUserName);
                        text.setCreateTime(System.currentTimeMillis()/1000);
                        text.setMsgType(MessageUtil.RESP_MESSAGE_TYPE_TEXT);
                        respMessage = MessageUtil.textMessageToXml(text);

                    }else if (recvMessage.equals("小梦")||recvMessage.equals("1")) {
                        NewsMessage newsMessage = new NewsMessage();
                        newsMessage.setToUserName(fromUserName);
                        newsMessage.setFromUserName(toUserName);
                        newsMessage.setMsgType("news");
                        newsMessage.setCreateTime(System.currentTimeMillis()/1000);
                        newsMessage.setArticleCount(1);
                        List<Article> articles = new ArrayList<>();
                        Article article = new Article();
                        article.setTitle("YIMEM个人网站");
                        article.setDescription("小梦的个人中心，个人笔记、学习记录");
                        article.setUrl("http://www.yimem.com");
                        article.setPicUrl("http://www.yimem.com/imgs/gz/gz.jpg");
                        articles.add(article);
                        newsMessage.setArticles(articles);
                        //XStream将Java对象转换成xml字符串
                        XStream xStream = new XStream();
                        xStream.processAnnotations(NewsMessage.class);
                        String xml = xStream.toXML(newsMessage);
                        return xml;
                    }else if (recvMessage.equals("图片文字识别")||recvMessage.equals("2")) {
                        text.setContent("您发送一个带文字的图片，小梦就可以帮您识别文字！");
                        text.setToUserName(fromUserName);
                        text.setFromUserName(toUserName);
                        text.setCreateTime(System.currentTimeMillis()/1000);
                        text.setMsgType(MessageUtil.RESP_MESSAGE_TYPE_TEXT);
                        respMessage = MessageUtil.textMessageToXml(text);
                    }else if (recvMessage.equals("谜语大全")||recvMessage.equals("4")) {
                        text.setContent(RddleUtil.getRddle());
                        text.setToUserName(fromUserName);
                        text.setFromUserName(toUserName);
                        text.setCreateTime(System.currentTimeMillis()/1000);
                        text.setMsgType(MessageUtil.RESP_MESSAGE_TYPE_TEXT);
                        respMessage = MessageUtil.textMessageToXml(text);
                    }else if (recvMessage.equals("心灵鸡汤")||recvMessage.equals("5")) {
                        text.setContent(SoupUtil.getSoup());
                        text.setToUserName(fromUserName);
                        text.setFromUserName(toUserName);
                        text.setCreateTime(System.currentTimeMillis()/1000);
                        text.setMsgType(MessageUtil.RESP_MESSAGE_TYPE_TEXT);
                        respMessage = MessageUtil.textMessageToXml(text);
                    }else if (recvMessage.equals("激活码")||recvMessage.equals("7")) {
                        text.setContent("输入格式: XXXXXXXXX机器码");
                        text.setToUserName(fromUserName);
                        text.setFromUserName(toUserName);
                        text.setCreateTime(System.currentTimeMillis()/1000);
                        text.setMsgType(MessageUtil.RESP_MESSAGE_TYPE_TEXT);
                        respMessage = MessageUtil.textMessageToXml(text);
                    }else {
                        text.setContent(this.getResult(content, fromUserName));
                    }
                } else {
                    text.setToUserName(fromUserName);
                    text.setFromUserName(toUserName);
                    text.setCreateTime(System.currentTimeMillis()/1000);
                    text.setContent("小梦没听清，能不能重新说下呢？");
                    respMessage = MessageUtil.textMessageToXml(text);
                }
            }
            //拍照功能
            else if (msgType.equals("pic_sysphoto")) {

            } else if (msgType.equals("image"))
            {
                TextMessage text = new TextMessage();
                text.setMsgType(MessageUtil.REQ_MESSAGE_TYPE_TEXT);
                text.setContent(ImageUtil.handleImage(requestMap));
                text.setToUserName(fromUserName);
                text.setFromUserName(toUserName);
                text.setCreateTime(System.currentTimeMillis()/1000);
                text.setMsgType(MessageUtil.RESP_MESSAGE_TYPE_TEXT);
                respMessage = MessageUtil.textMessageToXml(text);
            }
            // 事件推送
            else if (msgType.equals(MessageUtil.REQ_MESSAGE_TYPE_EVENT)) {
                String eventType = requestMap.get("Event");// 事件类型
                log.info("事件类型---" + eventType + "--EventKey值--" + EventKey);
                // 订阅
                if (eventType.equals(MessageUtil.EVENT_TYPE_SUBSCRIBE)) {

                    TextMessage text = new TextMessage();
                    text.setContent("欢迎关注，我是小梦\n\n(>‿◠)✌小梦可以为您服务的有：\r\n（1）小梦：小梦简介。" +
                                    "\r\n（2）图片文字识别：您发送一个带文字的图片，小梦就可以帮您识别文字！\r\n（3）笑话大全：可能不好笑，但小梦还是希望大佬能哈哈哈哈！" +
                                    "\r\n（4）谜语大全：快点来猜猜看吧，不能偷看答案哦！\r\n（5）心灵鸡汤：让小弟用鸡汤来安抚您吧，当然鸡汤可能不咋地！" +
                                    "\r\n（6）我的音乐：快乐点歌！\r\n（7）激活码：输入格式: XXXXXXXXX机器码" +
                                    "\r\n大佬注意了：给小梦一个关注好不好，小梦求个关注，谢谢大佬！");
                    text.setToUserName(fromUserName);
                    text.setFromUserName(toUserName);
                    text.setCreateTime(System.currentTimeMillis()/1000);
                    text.setMsgType(MessageUtil.RESP_MESSAGE_TYPE_TEXT);
                    respMessage = MessageUtil.textMessageToXml(text);
                    //20250212 关注公众默认注册账号并支持后续的扫码登录
                    User userOld = userServic.getUserByopenid(fromUserName);
                    if (userOld == null) {
                        User user = new User();
                        //设置昵称
                        String nickname = null;
                        Integer flag = 1;
                        Integer len = 4;
                        List<User> userList = userMapper.SelectAllUser();
                        List<String> usernamelist=userList.stream().filter(x->Xtool.isNotNull(x.getNickname())).map(User::getNickname).collect(Collectors.toList());
                        while (flag != 0) {
                            nickname = RandomName.randomName(false, len);
                            if (!usernamelist.contains(nickname)) {
                                flag = 0;
                            } else {
                                flag++;
                                if (flag > 1100000000 && flag < 2100000000) {
                                    len++;
                                    flag = 1;
                                }
                            }
                        }
                        user.setNickname(nickname);
                        int max=6,min=1;
                        int ran2 = (int) (Math.random()*(max-min)+min);
                        String url="/imgs/headimg/"+ran2+".jpg";
                        user.setHeadImg(url);
                        user.setDownloadmoney((double)0);
                        user.setRanking(9999);
                        user.setLevel(2);
                        user.setCollectCount(0);
                        user.setBlogCount(0);
                        user.setAttentionCount(0);
                        user.setFansCount(0);
                        user.setResourceCount(0);
                        user.setForumCount(0);
                        user.setAskCount(0);
                        user.setCommentCount(0);
                        user.setLikeCount(0);
                        user.setVisitorCount(0);
                        user.setDownCount(0);
                        user.setUnreadreplaycount(0);
                        user.setReadquerylikecount(0);
                        user.setUnreadfanscount(0);
                        user.setIsEmil("0");
                        user.setStatus(1);
                        int result = userMapper.insertUser(user);
                    }
                }
                // TODO 2020/8/10 扫码登录方案
                else if (eventType.equals(MessageUtil.SCAN)) {
//                    HttpSession session = MySessionContext.getSession(EventKey);
                    String token=EventKey;
                    User user = userServic.getUserByopenid(fromUserName);

                    if (user == null) {
                        TextMessage text = new TextMessage();
                        text.setContent("您的账号异常\n\n请取消关注并重新关注");
                        text.setToUserName(fromUserName);
                        text.setFromUserName(toUserName);
                        text.setCreateTime(System.currentTimeMillis()/1000);
                        text.setMsgType(MessageUtil.RESP_MESSAGE_TYPE_TEXT);
                        respMessage = MessageUtil.textMessageToXml(text);
                    } else {
                        ValueOperations opsForValue = redisTemplate.opsForValue();
                        opsForValue.set(token, JSONUtil.toJsonStr(user), 3600, TimeUnit.SECONDS);
                        Date day = new Date();
                        SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
                        this.sendTemplate(fromUserName, user.getUsername(), df.format(day));
                        return null;
                    }
                }
                // TODO 取消订阅后用户再收不到公众号发送的消息，因此不需要回复消息
                else if (eventType.equals(MessageUtil.EVENT_TYPE_UNSUBSCRIBE)) {// 取消订阅


                }
                // 自定义菜单点击事件
                else if (eventType.equals(MessageUtil.EVENT_TYPE_CLICK)) {
                    String eventKey = requestMap.get("EventKey");// 事件KEY值，与创建自定义菜单时指定的KEY值对应
                    if (eventKey.equals("customer_telephone")) {
                        TextMessage text = new TextMessage();
                        String tt = "ଘ(੭ˊᵕˋ)੭* ੈ✩如需本网站黄金c位广告位\n可联系电话:--------\n或加微信:--------";
                        text.setContent(tt);
                        text.setToUserName(fromUserName);
                        text.setFromUserName(toUserName);
                        text.setCreateTime(System.currentTimeMillis()/1000);
                        text.setMsgType(MessageUtil.RESP_MESSAGE_TYPE_TEXT);
                        respMessage = MessageUtil.textMessageToXml(text);
                    } else if (eventKey.equals("menu")) {
                        TextMessage text = new TextMessage();
                        text.setContent("嗨，我是小梦\n\n(>‿◠)✌小梦可以为您服务的有：\r\n（1）小梦：小梦简介。" +
                                "\r\n（2）图片文字识别：您发送一个带文字的图片，小梦就可以帮您识别文字！\r\n（3）笑话大全：可能不好笑，但小梦还是希望大佬能哈哈哈哈！" +
                                "\r\n（4）谜语大全：快点来猜猜看吧，不能偷看答案哦！\r\n（5）心灵鸡汤：让小弟用鸡汤来安抚您吧，当然鸡汤可能不咋地！" +
                                "\r\n（6）我的音乐：快乐点歌！\r\n（7）激活码：输入格式: XXXXXXXXX机器码" +
                                "\r\n大佬注意了：给小梦一个关注好不好，小梦求个关注，谢谢大佬！");
                        text.setToUserName(fromUserName);
                        text.setFromUserName(toUserName);
                        text.setCreateTime(System.currentTimeMillis()/1000);
                        text.setMsgType(MessageUtil.RESP_MESSAGE_TYPE_TEXT);
                        respMessage = MessageUtil.textMessageToXml(text);
                    } else {
                        User user = userServic.getUserByopenid(fromUserName);
                        System.out.println(user);
                        if (user != null) {
                            request.getSession().setAttribute("user", user);
                        }
                    }

                }
            }
        } catch (
                Exception e) {
            System.out.println("error......");
            log.info("微信异常---------------" + e.getMessage());
        }
        return respMessage;
    }

    public static void main(String[] args) {
        getResult("我是谁?", "");
    }

    private static String getResult(String question, String fromUserName) {
        String url = Constants.tokenUrl+"?client_id="+Constants.apibuKey+"&client_secret="+Constants.secretKey+"&grant_type=client_credentials";
        //向access_token接口发送POST请求，获取响应结果
        Map<String, String> paramMap = new HashMap<>();
        String response = null;
        try {
            response = HttpClientUtil.doPost(url, paramMap);
        } catch (IOException e) {
            e.printStackTrace();
        }
        //将响应结果中的access_token获取出来
        JSONObject jsonObject = JSON.parseObject(response);
        String token = jsonObject.getString("access_token");
        System.out.println("token:---------"+token);

        //下面携带access_token请求文心服务器
        //编写请求体，把前端传进来的问题拼入
        String paramJson = String.format("{\"messages\": [{\"role\": \"user\", \"content\": \"%s\"}]}", question);
        String request = null;
        //发送POST请求，获取请求结果字符串
        try {
            request = HttpClientUtil.doPostWithJson(Constants.chatUrl+"?access_token=" + token, paramJson);
        } catch (IOException e) {
            e.printStackTrace();
        }
        //截取请求结果中文心一言的回答部分
        JSONObject jsonResponse = JSON.parseObject(request);
        String result = jsonResponse.getString("result");
        //打印输出
        System.out.println("输出结果:" + result);
        //返回结果
        return result;
    }


    public StringBuffer getTextMessage(String recvMessage) {
        StringBuffer stringBuffer = new StringBuffer();
        BaseResp baseResp = new BaseResp();
        stringBuffer.append("小梦，为您搜寻:" + "  " + recvMessage + "  相关资源");
        int count;
        baseResp = searchService.queryAll(recvMessage);
        log.info("查询结果-------------" + baseResp.toString());
        Map<String, Object> map = (Map<String, Object>) baseResp.getData();
        if (!CollectionUtils.isEmpty(map)) {
            List<Blog> blogList = (List<Blog>) map.get("Blog");
            List<Ask> askList = (List<Ask>) map.get("Ask");
            List<Invitation> forumList = (List<Invitation>) map.get("Invitation");
            List<Upload> uploadList = (List<Upload>) map.get("Upload");
            List<Video> videos = (List<Video>) map.get("Video");
            List<T8DocManage> t8DocManages = (List<T8DocManage>) map.get("t8DocManages");
            if (Xtool.isNotNull(blogList)) {
                stringBuffer.append("\n\n博客类:");
                count = 0;
                for (Blog blog : blogList) {
                    count++;
                    String tile = blog.getTitle();
                    String src = "http://www.yimem.com/details.html?blog_id=" + blog.getId();
                    stringBuffer.append("\n\n<a href='" + src + "'>" + tile + "</a>");
                    if (count >= 5) {
                        break;
                    }
                }
                stringBuffer.append("\n\n<a href='http://www.yimem.com/searchResult_page.html?key=" + recvMessage + "'>点击查询更多</a>");
            }
            if (Xtool.isNotNull(videos)) {
                stringBuffer.append("\n\n视频类:");
                count = 0;
                for (Video video : videos) {
                    count++;
                    String tile = video.getTitle();
                    String src = "http://www.yimem.com/appDetail.html?videoid=" + video.getVideoid();
                    stringBuffer.append("\n\n<a href='" + src + "'>" + tile + "</a>");
                    if (count >= 5) {
                        break;
                    }
                }
                stringBuffer.append("\n\n<a href='http://www.yimem.com/searchResult_page.html?key=" + recvMessage + "'>点击查询更多</a>");
            }
            if (Xtool.isNotNull(uploadList)) {
                stringBuffer.append("\n\n资源类:");
                count = 0;
                for (Upload upload : uploadList) {
                    count++;
                    String tile = upload.getTitle();
                    String src = "http://www.yimem.com/detailpage.html?id=" + upload.getId();
                    stringBuffer.append("\n\n<a href='" + src + "'>" + tile + "</a>");
                    if (count >= 5) {
                        break;
                    }
                }
                stringBuffer.append("\n\n<a href='http://www.yimem.com/searchResult_page.html?key=" + recvMessage + "'>点击查询更多</a>");
            }
            if (Xtool.isNotNull(t8DocManages)) {
                stringBuffer.append("\n\n面试类:");
                count = 0;
                for (T8DocManage t8DocManage : t8DocManages) {
                    count++;
                    String tile = t8DocManage.getFolderName();
                    String src = "http://www.yimem.com/web/viewer.html?file=" + t8DocManage.getSrc();
                    stringBuffer.append("\n\n<a href='" + src + "'>" + tile + "</a>");
                    if (count >= 5) {
                        break;
                    }
                }
                stringBuffer.append("\n\n<a href='http://www.yimem.com/searchResult_page.html?key=" + recvMessage + "'>点击查询更多</a>");
            }
        }
        return stringBuffer;
    }


    public String upload(String filePath, String accessToken, String type) throws IOException {
        File file = new File(filePath);
        if (!file.exists() || !file.isFile()) {
            throw new IOException("文件不存在");
        }
        String url = Constants.UPLOAD_URL.replace("ACCESS_TOKEN", accessToken).replace("TYPE", type);
        System.out.println(accessToken);
        URL urlObj = new URL(url);
        //连接
        HttpURLConnection con = (HttpURLConnection) urlObj.openConnection();

        con.setRequestMethod("POST");
        con.setDoInput(true);
        con.setDoOutput(true);
        con.setUseCaches(false);

        //设置请求头信息
        con.setRequestProperty("Connection", "Keep-Alive");
        con.setRequestProperty("Charset", "UTF-8");

        //设置边界
        String BOUNDARY = "----------" + System.currentTimeMillis();
        con.setRequestProperty("Content-Type", "multipart/form-data; boundary=" + BOUNDARY);

        StringBuilder sb = new StringBuilder();
        sb.append("--");
        sb.append(BOUNDARY);
        sb.append("\r\n");
        sb.append("Content-Disposition: form-data;name=\"file\";filename=\"" + file.getName() + "\"\r\n");
        sb.append("Content-Type:application/octet-stream\r\n\r\n");

        byte[] head = sb.toString().getBytes("utf-8");

        //获得输出流
        OutputStream out = new DataOutputStream(con.getOutputStream());
        //输出表头
        out.write(head);

        //文件正文部分
        //把文件已流文件的方式 推入到url中
        DataInputStream in = new DataInputStream(new FileInputStream(file));
        int bytes = 0;
        byte[] bufferOut = new byte[1024];
        while ((bytes = in.read(bufferOut)) != -1) {
            out.write(bufferOut, 0, bytes);
        }
        in.close();

        //结尾部分
        byte[] foot = ("\r\n--" + BOUNDARY + "--\r\n").getBytes("utf-8");//定义最后数据分隔线

        out.write(foot);

        out.flush();
        out.close();

        StringBuffer buffer = new StringBuffer();
        BufferedReader reader = null;
        String result = null;
        try {
            //定义BufferedReader输入流来读取URL的响应
            reader = new BufferedReader(new InputStreamReader(con.getInputStream()));
            String line = null;
            while ((line = reader.readLine()) != null) {
                buffer.append(line);
            }
            if (result == null) {
                result = buffer.toString();
            }
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            if (reader != null) {
                reader.close();
            }
        }

        JSONObject jsonObj = JSONObject.parseObject(result);
        System.out.println(jsonObj);
        String typeName = "media_id";
        if (!"image".equals(type)) {
            typeName = type + "_media_id";
        }
        String mediaId = jsonObj.getString(typeName);
        System.out.println("mId" + mediaId);
        return mediaId;
    }

    /**
     * 获取accessToken
     *
     * @return
     * @throws
     * @throws IOException
     */
    public AccessToken getAccessToken(String toUserName) throws IOException {
        AccessToken token = new AccessToken();
        String url = null;
        if (Constants.TO_USER_NAME.equals(toUserName)) {
            url = Constants.ACCESS_TOKEN_URL.replace("APPID", Constants.APPID).replace("APPSECRET", Constants.APPSECRET);
        } else {
            url = Constants.ACCESS_TOKEN_URL.replace("APPID", Constants.APPID_2).replace("APPSECRET", Constants.APPSECRET_2);

        }
        JSONObject jsonObject = doGetStr(url);
        if (jsonObject != null) {
            token.setToken(jsonObject.getString("access_token"));
            token.setExpiresIn(jsonObject.getString("expires_in"));
        }
        return token;
    }

    public JSONObject doGetStr(String url) throws IOException {
        DefaultHttpClient client = new DefaultHttpClient();
        HttpGet httpGet = new HttpGet(url);
        JSONObject jsonObject = null;
        HttpResponse httpResponse = client.execute(httpGet);
        HttpEntity entity = httpResponse.getEntity();
        if (entity != null) {
//            String result = EntityUtils.toString(entity,"UTF-8");
            String result = EntityUtils.toString(entity, "UTF-8");
            jsonObject = JSONObject.parseObject(result);
        }
        return jsonObject;
    }

    /**
     * POST请求
     *
     * @param url
     * @param outStr
     * @return
     * @throws
     * @throws IOException
     */
    public JSONObject doPostStr(String url, String outStr) throws IOException {
        DefaultHttpClient client = new DefaultHttpClient();
        HttpPost httpost = new HttpPost(url);
        JSONObject jsonObject = null;
//        httpost.setEntity(new StringEntity(outStr,"UTF-8"));
        httpost.setEntity(new StringEntity(outStr, "UTF-8"));
        HttpResponse response = client.execute(httpost);
        String result = EntityUtils.toString(response.getEntity(), "UTF-8");
        jsonObject = JSONObject.parseObject(result);
        return jsonObject;
    }


    /**
     * 获取生成二维码Ticke值（临时二维码）
     *
     * @param SessionId
     * @return
     */
    public String getTicketData(String SessionId) throws IOException {
        AccessToken token = new AccessToken();
        String src = Constants.ACCESS_TOKEN_URL.replace("APPID", Constants.APPID).replace("APPSECRET", Constants.APPSECRET);
        JSONObject jsonObject = doGetStr(src);
        if (jsonObject != null) {
            token.setToken(jsonObject.getString("access_token"));
            token.setExpiresIn(jsonObject.getString("expires_in"));
        }
        //临时字符二维码url
        String url = Constants.TICKET_URL.replace("ACCESS_TOKEN", token.getToken());
        //二维码参数，以及过期时间
        String data = "{\"expire_seconds\": 600000000, \"action_name\": \"QR_STR_SCENE\", \"action_info\": {\"scene\": {\"scene_str\":\"" + SessionId + "\"}}}";
        JSONObject object = doPostStr(url, data);
        String ticket = null;
        if (object != null) {
            ticket = object.getString("ticket");
        }
        return ticket;
    }


    public void sendTemplate(String fromUserName, String nickName, String time) throws IOException {
        AccessToken token = new AccessToken();
        String src = Constants.ACCESS_TOKEN_URL.replace("APPID", Constants.APPID).replace("APPSECRET", Constants.APPSECRET);
        JSONObject jsonObject = doGetStr(src);
        if (jsonObject != null) {
            token.setToken(jsonObject.getString("access_token"));
            token.setExpiresIn(jsonObject.getString("expires_in"));
        }
        //调用消息模板url
        String url = Constants.TEMPLATE.replace("ACCESS_TOKEN", token.getToken());
        //生成消息模板
        String data = "{\"touser\":\"" + fromUserName + "\",\"template_id\":\"" + Constants.TEMPLATE_ID + "\",\"url\":\"http://www.yimem.com\",\"topcolor\":\"#FF0000\",\"data\":{\"first\":{\"value\":\"" + nickName + "\",\"color\":\"#173177\"},\"four\":{\"value\":\"" + time + "\",\"color\":\"#173177\"}}}";
        doPostStr(url, data);
    }

    public void sendTemplate2(String fromUserName, String username, String nickname, String time) throws IOException {
        AccessToken token = new AccessToken();
        String src = Constants.ACCESS_TOKEN_URL.replace("APPID", Constants.APPID).replace("APPSECRET", Constants.APPSECRET);
        JSONObject jsonObject = doGetStr(src);
        if (jsonObject != null) {
            token.setToken(jsonObject.getString("access_token"));
            token.setExpiresIn(jsonObject.getString("expires_in"));
        }
        //调用消息模板url
        String url = Constants.TEMPLATE.replace("ACCESS_TOKEN", token.getToken());
        //生成消息模板
        String data = "{\"touser\":\"" + fromUserName + "\",\"template_id\":\"" + Constants.TEMPLATE_ID2 + "\",\"url\":\"http://www.yimem.com\",\"topcolor\":\"#FF0000\",\"data\":{\"first\":{\"value\":\"" + username + "\",\"color\":\"#173177\"},\"sec\":{\"value\":\"" + time + "\",\"color\":\"#173177\"},\"four\":{\"value\":\"" + nickname + "\",\"color\":\"#173177\"}}}";
        doPostStr(url, data);
    }


    public void sendTemplate3(String fromUserName, String nickname, String username, String time) throws IOException {
        AccessToken token = new AccessToken();
        String src = Constants.ACCESS_TOKEN_URL.replace("APPID", Constants.APPID).replace("APPSECRET", Constants.APPSECRET);
        JSONObject jsonObject = doGetStr(src);
        if (jsonObject != null) {
            token.setToken(jsonObject.getString("access_token"));
            token.setExpiresIn(jsonObject.getString("expires_in"));
        }
        //调用消息模板url
        String url = Constants.TEMPLATE.replace("ACCESS_TOKEN", token.getToken());
        //生成消息模板
        String data = "{\"touser\":\"" + fromUserName + "\",\"template_id\":\"" + Constants.TEMPLATE_ID3 + "\",\"url\":\"http://www.yimem.com\",\"topcolor\":\"#FF0000\",\"data\":{\"first\":{\"value\":\"" + username + "\",\"color\":\"#173177\"},\"four\":{\"value\":\"" + time + "\",\"color\":\"#173177\"},\"sec\":{\"value\":\"" + nickname + "\",\"color\":\"#173177\"}}}";
        doPostStr(url, data);
    }

    @Override
    public void sendTemplate4(String fromUserName, String nickname, String outTradeNo, String money, String totalAmount) throws IOException {
        AccessToken token = new AccessToken();
        String src = Constants.ACCESS_TOKEN_URL.replace("APPID", Constants.APPID).replace("APPSECRET", Constants.APPSECRET);
        JSONObject jsonObject = doGetStr(src);
        if (jsonObject != null) {
            token.setToken(jsonObject.getString("access_token"));
            token.setExpiresIn(jsonObject.getString("expires_in"));
        }
        //调用消息模板url
        String url = Constants.TEMPLATE.replace("ACCESS_TOKEN", token.getToken());
        //生成消息模板
        Date currentTime = new Date();
        SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        String dateString = formatter.format(currentTime);
        String data = "{\"touser\":\"" + fromUserName + "\",\"template_id\":\"" + Constants.TEMPLATE_ID4 + "\",\"url\":\"http://www.yimem.com\",\"topcolor\":\"#FF0000\",\"data\":{\"first\":{\"value\":\"" + nickname + "\",\"color\":\"#173177\"},\"second\":{\"value\":\"" + outTradeNo + "\",\"color\":\"#173177\"},\"six\":{\"value\":\"" + money + "\",\"color\":\"#173177\"},\"third\":{\"value\":\"" + totalAmount + "\",\"color\":\"#173177\"},\"four\":{\"value\":\"" + dateString + "\",\"color\":\"#173177\"}}}";
        doPostStr(url, data);
    }


}
