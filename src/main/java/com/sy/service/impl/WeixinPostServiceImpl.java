package com.sy.service.impl;

import com.alibaba.fastjson.JSONObject;
import com.sy.model.*;
import com.sy.service.UserServic;
import com.sy.service.WeixinPostService;
import com.sy.tool.Constants;
import com.sy.tool.MessageUtil;
import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.util.EntityUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.*;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.Date;
import java.util.Map;

@Service
public class WeixinPostServiceImpl implements WeixinPostService {
    @Autowired
    private UserServic userServic;

    /**
     * 处理微信发来的请求
     *
     * @param request
     * @return
     */
    public String weixinPost(HttpServletRequest request) {
        String respMessage = null;

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

            System.out.println("FromUserName is:" + fromUserName + ", ToUserName is:" + toUserName + ", MsgType is:" + msgType);

            // 文本消息
            if (msgType.equals(MessageUtil.REQ_MESSAGE_TYPE_TEXT)) {
                //这里根据关键字执行相应的逻辑，只有你想不到的，没有做不到的
                //自动回复
                TextMessage text = new TextMessage();
                text.setMsgType(msgType);
                System.out.println(content);
                String kk = "【账号绑定】";
                int count = content.indexOf(kk);
                System.out.println(count);
                if (count > -1&& Constants.TO_USER_NAME.equals(toUserName)) {
                    String er = "你好， 绑定格式有问题!\n\n请仔细参照一下格式定格式\n【账号绑定】+账号+，+密码\n例如：\n【账号绑定】123456,123456";
                    User user3 = userServic.getUserByopenid(fromUserName);
                    if (user3 == null) {
                        int count2 = content.indexOf(",");
                        if (count2 > -1) {
                            String usrname = content.substring(kk.length(), count2);
                            String password = content.substring(count2 + 1, content.length());
                            User user2 = new User();
                            user2.setUsername(usrname);
                            user2.setUserpassword(password);
                            System.out.println(usrname);
                            System.out.println(password);
                            User user = userServic.getLoginUser(user2);
                            if (user != null) {
                                System.out.println(user.getUsername());
                                user.setOpenid(fromUserName);
                                userServic.updateuser(user);
                                text.setContent("你好， 绑定成功~");
                            } else {
                                text.setContent("你好， 这账号密码不正确~");
                            }
                        } else {
                            text.setContent("你好， 该微信绑定过账号~");
                        }
                    } else {
                        text.setContent(er);
                    }

                }else {
                    text.setContent("~~");
                    if (content.equals("账号绑定")||content.equals("账号")||content.equals("绑定")||content.equals("绑账号")||content.equals("绑")) {
                        String message = null;
                        Image image = new Image();
                        AccessToken token =this.getAccessToken(toUserName);
                        String path= request.getSession().getServletContext().getRealPath("/imgs/gz/eduwxfix.png");
                        image.setMediaId(this.upload(path,token.getToken(),"image"));
                        ImageMessage imageMessage = new ImageMessage();
                        imageMessage.setFromUserName(toUserName);
                        imageMessage.setToUserName(fromUserName);
                        imageMessage.setMsgType("image");
                        imageMessage.setCreateTime(new Date().getTime()+"");
                        imageMessage.setImage(image);
                        message = MessageUtil.textMessageToXml(imageMessage);
                        return message;

                    }
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
            }
            //开启微信声音识别测试 2015-3-30
            else if(msgType.equals("voice"))
            {
                String recvMessage = requestMap.get("Recognition");
                //respContent = "收到的语音解析结果："+recvMessage;
                if(recvMessage!=null){
                    respContent = TulingApiProcess.getTulingResult(recvMessage);
                }else{
                    respContent = "您说的太模糊了，能不能重新说下呢？";
                }
                return MessageResponse.getTextMessage(fromUserName , toUserName , respContent);
            }
            //拍照功能
            else if(msgType.equals("pic_sysphoto"))
            {

            }
            else
            {
                return MessageResponse.getTextMessage(fromUserName , toUserName , "返回为空");
            }*/
            // 事件推送
            else if (msgType.equals(MessageUtil.REQ_MESSAGE_TYPE_EVENT)) {
                String eventType = requestMap.get("Event");// 事件类型
                // 订阅
                if (eventType.equals(MessageUtil.EVENT_TYPE_SUBSCRIBE)) {

                    TextMessage text = new TextMessage();
                    text.setContent("欢迎关注，我是小梦");
                    text.setToUserName(fromUserName);
                    text.setFromUserName(toUserName);
                    text.setCreateTime(new Date().getTime() + "");
                    text.setMsgType(MessageUtil.RESP_MESSAGE_TYPE_TEXT);
                    respMessage = MessageUtil.textMessageToXml(text);

                }
                // TODO 取消订阅后用户再收不到公众号发送的消息，因此不需要回复消息
                else if (eventType.equals(MessageUtil.EVENT_TYPE_UNSUBSCRIBE)) {// 取消订阅


                }
                // 自定义菜单点击事件
                else if (eventType.equals(MessageUtil.EVENT_TYPE_CLICK)) {
                    String eventKey = requestMap.get("EventKey");// 事件KEY值，与创建自定义菜单时指定的KEY值对应
                    if (eventKey.equals("customer_telephone")) {
                        TextMessage text = new TextMessage();
                        text.setContent("18932200163");
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
                    } else if (eventKey.equals("bangding")) {
                        TextMessage text = new TextMessage();
                        String tt = "账号绑定格式\n【账号绑定】+账号+，+密码\n例如：\n【账号绑定】123456,123456";
                        text.setContent(tt);
                        text.setToUserName(fromUserName);
                        text.setFromUserName(toUserName);
                        text.setCreateTime(new Date().getTime() + "");
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
        } catch (Exception e) {
            System.out.println("error......");
        }
        return respMessage;
    }



    public  String upload(String filePath, String accessToken,String type) throws IOException {
        File file = new File(filePath);
        if (!file.exists() || !file.isFile()) {
            throw new IOException("文件不存在");
        }
        System.out.println(111111);
        String url = Constants.UPLOAD_URL.replace("ACCESS_TOKEN", accessToken).replace("TYPE",type);
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
        if(!"image".equals(type)){
            typeName = type + "_media_id";
        }
        String mediaId = jsonObj.getString(typeName);
        System.out.println("mId"+mediaId);
        return mediaId;
    }

    /**
     * 获取accessToken
     * @return
     * @throws
     * @throws IOException
     */
    public  AccessToken getAccessToken(String toUserName) throws  IOException{
        AccessToken token = new AccessToken();
        String url=null;
        if (Constants.TO_USER_NAME.equals(toUserName)){
            url = Constants.ACCESS_TOKEN_URL.replace("APPID", Constants.APPID).replace("APPSECRET", Constants.APPSECRET);
        }else {
            url = Constants.ACCESS_TOKEN_URL.replace("APPID", Constants.APPID_2).replace("APPSECRET", Constants.APPSECRET_2);

        }
        JSONObject jsonObject = doGetStr(url);
        if(jsonObject!=null){
            token.setToken(jsonObject.getString("access_token"));
            token.setExpiresIn(jsonObject.getString("expires_in"));
        }
        System.out.println(token.getToken());
        return token;
    }

    public  JSONObject doGetStr(String url) throws  IOException{
        DefaultHttpClient client = new DefaultHttpClient();
        HttpGet httpGet = new HttpGet(url);
        JSONObject jsonObject = null;
        HttpResponse httpResponse = client.execute(httpGet);
        HttpEntity entity = httpResponse.getEntity();
        if(entity != null){
//            String result = EntityUtils.toString(entity,"UTF-8");
            String result = EntityUtils.toString(entity,"UTF-8");
            jsonObject = JSONObject.parseObject(result);
        }
        return jsonObject;
    }
    /**
     * POST请求
     * @param url
     * @param outStr
     * @return
     * @throws
     * @throws IOException
     */
    public  JSONObject doPostStr(String url,String outStr) throws IOException{
        DefaultHttpClient client = new DefaultHttpClient();
        HttpPost httpost = new HttpPost(url);
        JSONObject jsonObject = null;
//        httpost.setEntity(new StringEntity(outStr,"UTF-8"));
        httpost.setEntity(new StringEntity(outStr,"UTF-8"));
        HttpResponse response = client.execute(httpost);
        String result = EntityUtils.toString(response.getEntity(),"UTF-8");
        jsonObject = JSONObject.parseObject(result);
        return jsonObject;
    }

}
