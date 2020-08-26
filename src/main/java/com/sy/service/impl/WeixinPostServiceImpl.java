package com.sy.service.impl;

import com.alibaba.fastjson.JSONObject;
import com.sy.model.*;
import com.sy.model.resp.BaseResp;
import com.sy.model.weixin.Image;
import com.sy.model.weixin.ImageMessage;
import com.sy.model.weixin.TextMessage;
import com.sy.service.SearchService;
import com.sy.service.UserServic;
import com.sy.service.WeixinPostService;
import com.sy.tool.Constants;
import com.sy.tool.MessageUtil;
import com.sy.tool.MySessionContext;
import com.sy.tool.Xtool;
import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.util.EntityUtils;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;
import java.io.*;
import java.net.HttpURLConnection;
import java.net.URL;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;

@Service
public class WeixinPostServiceImpl implements WeixinPostService {
    @Autowired
    private UserServic userServic;
    @Autowired
    private SearchService searchService;
    private Logger log = Logger.getLogger(WeixinPostServiceImpl.class.getName());

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
            String EventKey=requestMap.get("EventKey");

            log.info("FromUserName is:" + fromUserName + ", ToUserName is:" + toUserName + ", MsgType is:" + msgType);

            // 文本消息
            if (msgType.equals(MessageUtil.REQ_MESSAGE_TYPE_TEXT)) {
                //这里根据关键字执行相应的逻辑，只有你想不到的，没有做不到的
                //自动回复
                TextMessage text = new TextMessage();
                text.setMsgType(msgType);
                System.out.println(content);
                    text.setContent(this.getTextMessage(content).toString());
                    if (content.equals("账号绑定") || content.equals("账号") || content.equals("绑定") || content.equals("绑账号") || content.equals("绑")) {
                        if (Constants.TO_USER_NAME.equals(toUserName)){
                            text.setContent("请点击下方菜单 绑定账号");
                            text.setToUserName(fromUserName);
                            text.setFromUserName(toUserName);
                            text.setCreateTime(new Date().getTime() + "");
                            text.setMsgType(MessageUtil.RESP_MESSAGE_TYPE_TEXT);
                            respMessage = MessageUtil.textMessageToXml(text);
                        }else {
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
                            imageMessage.setCreateTime(new Date().getTime() + "");
                            imageMessage.setImage(image);
                            message = MessageUtil.textMessageToXml(imageMessage);
                            return message;
                        }
                    } else if (content.equals("广告") || content.equals("广告租用")) {
                        String tt = "ଘ(੭ˊᵕˋ)੭* ੈ✩如需本网站黄金c位广告位\n可联系电话:18932200163\n或加微信:c965898749";
                        text.setContent(tt);
                    }
                text.setToUserName(fromUserName);
                text.setFromUserName(toUserName);
                text.setCreateTime(new Date().getTime() + "");

                respMessage = MessageUtil.textMessageToXml(text);


            } /*else if (msgType.equals(MessageUtil.REQ_MESSAGE_TYPE_EVENT)) {// 事件推送
                String eventType = requestMap.get("Event");// 事件类型

                if (eventType.equals(MessageUtil.EVENT_TYPE_SUBSCRIBE)) {// 订阅
                    respContent = "欢迎关注xxx公众号！";
                    return MessageResponse.getTextMessage(fromUserName , toUserName , respContent);
                } else if (eventType.equals(MessageUtil.EVENT_TYPE_CLICK)) {// 自定义菜单点击事件
                    String eventKey = requestMap.get("EventKey");// 事件KEY值，与创建自定义菜单时指定的KEY值对应
                     System.out.println("eventKey is:" +eventKey);
                    return xxx;
                }
            }*/
            //开启微信声音识别测试 2015-3-30
            else if (msgType.equals("voice")) {
                String recvMessage = requestMap.get("Recognition");
                //respContent = "收到的语音解析结果："+recvMessage;
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
                            text.setCreateTime(new Date().getTime() + "");
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
                            imageMessage.setCreateTime(new Date().getTime() + "");
                            imageMessage.setImage(image);
                            message = MessageUtil.textMessageToXml(imageMessage);
                            return message;
                        }
                    } else if (recvMessage.equals("广告") || recvMessage.equals("广告租用")) {
                        String tt = "ଘ(੭ˊᵕˋ)੭* ੈ✩如需本网站黄金c位广告位\n可联系电话:18932200163\n或加微信:c965898749";
                        text.setContent(tt);
                        text.setToUserName(fromUserName);
                        text.setFromUserName(toUserName);
                        text.setCreateTime(new Date().getTime() + "");
                        text.setMsgType(MessageUtil.RESP_MESSAGE_TYPE_TEXT);
                        respMessage = MessageUtil.textMessageToXml(text);
                    } else {
                        StringBuffer stringBuffer = this.getTextMessage(recvMessage);
                        log.info("微信输出信息-------------" + stringBuffer.toString());
                        text.setToUserName(fromUserName);
                        text.setFromUserName(toUserName);
                        text.setCreateTime(new Date().getTime() + "");
                        text.setContent(stringBuffer.toString());
                        respMessage = MessageUtil.textMessageToXml(text);
                    }
                } else {
                    text.setToUserName(fromUserName);
                    text.setFromUserName(toUserName);
                    text.setCreateTime(new Date().getTime() + "");
                    text.setContent("小梦没听清，能不能重新说下呢？");
                    respMessage = MessageUtil.textMessageToXml(text);
//                    respContent = "您说的太模糊了，能不能重新说下呢？";
                }
//                return MessageResponse.getTextMessage(fromUserName , toUserName , respContent);
            }
            //拍照功能
            else if (msgType.equals("pic_sysphoto")) {

            }
//            else if ()
//            {
//                return MessageResponse.getTextMessage(fromUserName , toUserName , "返回为空");
//            }
            // 事件推送
            else if (msgType.equals(MessageUtil.REQ_MESSAGE_TYPE_EVENT)) {
                String eventType = requestMap.get("Event");// 事件类型
                log.info("事件类型---"+eventType+"--EventKey值--"+EventKey);
                // 订阅
                if (eventType.equals(MessageUtil.EVENT_TYPE_SUBSCRIBE)) {

                    TextMessage text = new TextMessage();
                    text.setContent("欢迎关注，我是小梦\n\n您可以对我语音或者文字\n\n(>‿◠)✌我将为您查询本站相关内容\n\n如需微信号绑定登录请说 \n绑定账号\n\n如需网站广告租用请说 \n广告租用");
                    text.setToUserName(fromUserName);
                    text.setFromUserName(toUserName);
                    text.setCreateTime(new Date().getTime() + "");
                    text.setMsgType(MessageUtil.RESP_MESSAGE_TYPE_TEXT);
                    respMessage = MessageUtil.textMessageToXml(text);

                }
                // TODO 2020/8/10 扫码登录方案
                else if (eventType.equals(MessageUtil.SCAN)) {
                    HttpSession session = MySessionContext.getSession(EventKey);

                    User user = userServic.getUserByopenid(fromUserName);

                    if (user == null) {
                        TextMessage text = new TextMessage();
                        text.setContent("您的账号还未绑定\n\n请点击下方菜单 绑定账号");
                        text.setToUserName(fromUserName);
                        text.setFromUserName(toUserName);
                        text.setCreateTime(new Date().getTime() + "");
                        text.setMsgType(MessageUtil.RESP_MESSAGE_TYPE_TEXT);
                        respMessage = MessageUtil.textMessageToXml(text);
                    }else {
                        session.setAttribute("user", user);
                        Date day = new Date();
                        SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
                        this.sendTemplate(fromUserName,user.getUsername(),df.format(day));
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
                        String tt = "ଘ(੭ˊᵕˋ)੭* ੈ✩如需本网站黄金c位广告位\n可联系电话:18932200163\n或加微信:c965898749";
                        text.setContent(tt);
                        text.setToUserName(fromUserName);
                        text.setFromUserName(toUserName);
                        text.setCreateTime(new Date().getTime() + "");
                        text.setMsgType(MessageUtil.RESP_MESSAGE_TYPE_TEXT);
                        respMessage = MessageUtil.textMessageToXml(text);
                    } else if (eventKey.equals("jiechu")) {
                        TextMessage text = new TextMessage();
                        User user = userServic.getUserByopenid(fromUserName);
                        if (user != null) {
                            try {
                                userServic.delUserByopenid(fromUserName);
                                text.setContent("解绑成功");
                            } catch (Exception e) {
                                e.printStackTrace();
                            }
                        } else {
                            text.setContent("该微信未绑定账号");
                        }
                        text.setToUserName(fromUserName);
                        text.setFromUserName(toUserName);
                        text.setCreateTime(new Date().getTime() + "");
                        text.setMsgType(MessageUtil.RESP_MESSAGE_TYPE_TEXT);
                        respMessage = MessageUtil.textMessageToXml(text);
                    }  else {
                        User user = userServic.getUserByopenid(fromUserName);
                        System.out.println(user);
                        if (user != null) {
                            request.getSession().setAttribute("user", user);
                        }
                    }

                }
            }
        } catch (Exception e) {
            System.out.println("error......");
            log.info("微信异常---------------" + e.getMessage());
        }
        return respMessage;
    }

    public StringBuffer getTextMessage(String recvMessage) {
        StringBuffer stringBuffer = new StringBuffer();
        BaseResp baseResp = new BaseResp();
        stringBuffer.append("小梦，为您搜寻:" + "  " + recvMessage + "  相关资源");
        int count;
        baseResp = searchService.queryAll(recvMessage);
//                    log.info("查询结果-------------"+baseResp.toString());
        Map<String, Object> map = (Map<String, Object>) baseResp.getData();
        if (!CollectionUtils.isEmpty(map)) {
            List<Blog> blogList = (List<Blog>) map.get("Blog");
            List<Ask> askList = (List<Ask>) map.get("Ask");
            List<Invitation> forumList = (List<Invitation>) map.get("Invitation");
            List<Upload> uploadList = (List<Upload>) map.get("Upload");
            List<Video> videos = (List<Video>) map.get("Video");
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
            if (Xtool.isNull(blogList) && Xtool.isNull(videos) && Xtool.isNull(uploadList)) {
                stringBuffer.append("\n\n抱歉暂未找到你想要的资源T_T");
            }
        } else {
            stringBuffer.append("\n\n抱歉暂未找到你想要的资源T_T");
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
        String  url = Constants.TICKET_URL.replace("ACCESS_TOKEN",token.getToken());
        //二维码参数，以及过期时间
        String data = "{\"expire_seconds\": 600000000, \"action_name\": \"QR_STR_SCENE\", \"action_info\": {\"scene\": {\"scene_str\":\""+ SessionId +"\"}}}";
        JSONObject object = doPostStr(url,data);
        String ticket=null;
        if (object != null) {
            ticket=object.getString("ticket");
        }
        return ticket;
    }


    public void sendTemplate(String fromUserName,String nickName,String time)throws IOException{
        AccessToken token = new AccessToken();
        String src = Constants.ACCESS_TOKEN_URL.replace("APPID", Constants.APPID).replace("APPSECRET", Constants.APPSECRET);
        JSONObject jsonObject = doGetStr(src);
        if (jsonObject != null) {
            token.setToken(jsonObject.getString("access_token"));
            token.setExpiresIn(jsonObject.getString("expires_in"));
        }
        //调用消息模板url
        String  url = Constants.TEMPLATE.replace("ACCESS_TOKEN",token.getToken());
        //生成消息模板
        String data ="{\"touser\":\""+fromUserName+"\",\"template_id\":\""+Constants.TEMPLATE_ID+"\",\"url\":\"http://www.yimem.com\",\"topcolor\":\"#FF0000\",\"data\":{\"first\":{\"value\":\""+nickName+"\",\"color\":\"#173177\"},\"four\":{\"value\":\""+time+"\",\"color\":\"#173177\"}}}";
        doPostStr(url,data);
    }

    public void sendTemplate2(String fromUserName,String username,String nickname,String time)throws IOException{
        AccessToken token = new AccessToken();
        String src = Constants.ACCESS_TOKEN_URL.replace("APPID", Constants.APPID).replace("APPSECRET", Constants.APPSECRET);
        JSONObject jsonObject = doGetStr(src);
        if (jsonObject != null) {
            token.setToken(jsonObject.getString("access_token"));
            token.setExpiresIn(jsonObject.getString("expires_in"));
        }
        //调用消息模板url
        String  url = Constants.TEMPLATE.replace("ACCESS_TOKEN",token.getToken());
        //生成消息模板
        String data ="{\"touser\":\""+fromUserName+"\",\"template_id\":\""+Constants.TEMPLATE_ID2+"\",\"url\":\"http://www.yimem.com\",\"topcolor\":\"#FF0000\",\"data\":{\"first\":{\"value\":\""+username+"\",\"color\":\"#173177\"},\"sec\":{\"value\":\""+time+"\",\"color\":\"#173177\"},\"four\":{\"value\":\""+nickname+"\",\"color\":\"#173177\"}}}";
        doPostStr(url,data);
    }


    public void sendTemplate3(String fromUserName,String nickname,String username,String time)throws IOException{
        AccessToken token = new AccessToken();
        String src = Constants.ACCESS_TOKEN_URL.replace("APPID", Constants.APPID).replace("APPSECRET", Constants.APPSECRET);
        JSONObject jsonObject = doGetStr(src);
        if (jsonObject != null) {
            token.setToken(jsonObject.getString("access_token"));
            token.setExpiresIn(jsonObject.getString("expires_in"));
        }
        //调用消息模板url
        String  url = Constants.TEMPLATE.replace("ACCESS_TOKEN",token.getToken());
        //生成消息模板
        String data ="{\"touser\":\""+fromUserName+"\",\"template_id\":\""+Constants.TEMPLATE_ID3+"\",\"url\":\"http://www.yimem.com\",\"topcolor\":\"#FF0000\",\"data\":{\"first\":{\"value\":\""+username+"\",\"color\":\"#173177\"},\"four\":{\"value\":\""+time+"\",\"color\":\"#173177\"},\"sec\":{\"value\":\""+nickname+"\",\"color\":\"#173177\"}}}";
        doPostStr(url,data);
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
        String  url = Constants.TEMPLATE.replace("ACCESS_TOKEN",token.getToken());
        //生成消息模板
        Date currentTime = new Date();
        SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        String dateString = formatter.format(currentTime);
        String data ="{\"touser\":\""+fromUserName+"\",\"template_id\":\""+Constants.TEMPLATE_ID4+"\",\"url\":\"http://www.yimem.com\",\"topcolor\":\"#FF0000\",\"data\":{\"first\":{\"value\":\""+nickname+"\",\"color\":\"#173177\"},\"second\":{\"value\":\""+outTradeNo+"\",\"color\":\"#173177\"},\"six\":{\"value\":\""+money+"\",\"color\":\"#173177\"},\"third\":{\"value\":\""+totalAmount+"\",\"color\":\"#173177\"},\"four\":{\"value\":\""+dateString+"\",\"color\":\"#173177\"}}}";
        doPostStr(url,data);
    }


}
