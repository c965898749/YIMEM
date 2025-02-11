//package com.sy.service.impl;
//
//import com.alibaba.fastjson.JSON;
//import com.alibaba.fastjson.JSONObject;
//import com.alibaba.fastjson.serializer.SerializerFeature;
//
//import com.sy.entity.ChatGptToken;
//import com.sy.entity.MessageResponseBody;
//import com.sy.entity.MessageSendBody;
//import com.sy.service.ChatGptService;
//import com.sy.service.ChatGptTokenService;
//import com.sy.tool.Constants;
//import com.sy.tool.HttpUtil;
////import com.sy.tool.RedisUtil;
//import com.sy.tool.Xtool;
//import lombok.extern.slf4j.Slf4j;
//import org.springframework.beans.factory.annotation.Value;
//import org.springframework.stereotype.Service;
//import org.springframework.util.CollectionUtils;
//
//import javax.annotation.Resource;
//import java.util.ArrayList;
//import java.util.HashMap;
//import java.util.List;
//import java.util.Map;
//import java.util.concurrent.TimeUnit;
//
///**
// * @author honghu
// */
//@Slf4j
//@Service
//public class ChatGptServiceImpl implements ChatGptService {
//
////    @Value("${openapi.key}")
////    private String apiKey;
//
//    @Resource
//    private ChatGptTokenService chatGptTokenService;
//    /**
//     * 接口请求地址
//     */
//    private final String url = "https://api.openai.com/v1/completions";
//
//
//
//    private final String human = "Human:";
//    /**
//     * 定义ai的名字
//     */
//    private final String Ai = "小小鹏:";
//
//
//    @Override
//    public String reply(String messageContent, String userKey) {
//        // 默认信息
//        String message = "Human:你好\n小小鹏:你好\n";
//        if (RedisUtil.getJedisInstance().exists(userKey)) {
//            // 如果存在key，拿出来
//            message =  RedisUtil.getJedisInstance().get(userKey);
//        }
//        if ("1".equals(messageContent)){
//           return message.substring(message.lastIndexOf("小小鹏:")+1).replace("小鹏:", "小梦，");
//        }
//        // 拼接字符,设置回去
//        message = message + human + messageContent + "\n";
//        RedisUtil.getJedisInstance().set(userKey, message);
//        // 调用接口获取数据
//        JSONObject obj = getReplyFromGPT(message);
//        MessageResponseBody messageResponseBody = JSONObject.toJavaObject(obj, MessageResponseBody.class);
//        // 存储对话内容，让机器人更加智能
//        if (messageResponseBody != null) {
//            if (!CollectionUtils.isEmpty(messageResponseBody.getChoices())) {
//                String replyText = messageResponseBody.getChoices().get(0).getText();
//                // 拼接字符,设置回去
//                new Thread(() -> {
//                    String msg="";
//                    if (RedisUtil.getJedisInstance().exists(userKey)) {
//                        // 如果存在key，拿出来
//                        msg =  RedisUtil.getJedisInstance().get(userKey);
//                    }
//                    msg = msg  + replyText + "\n";
//                    RedisUtil.getJedisInstance().set(userKey, msg);
//                }).start();
//                RedisUtil.getJedisInstance().expire(userKey, 60);
//                RedisUtil.closeJedisInstance();
//                return replyText.replace("小小鹏:", "小梦，");
//            }
//        }
//        RedisUtil.getJedisInstance().expire(userKey, 60);
//        RedisUtil.closeJedisInstance();
//        return "暂时不明白你说什么!";
//    }
//
//
//
//
////    @Override
////    public String czx(String recvMessage,String fromUserName) {
////        // 默认信息
////        String message = "小梦:你好\n小小鹏:你好\n"+recvMessage+ "\n";
////
////        // 调用接口获取数据
////        JSONObject obj = getReplyFromGPT(message);
////        MessageResponseBody messageResponseBody = JSONObject.toJavaObject(obj, MessageResponseBody.class);
////        // 存储对话内容，让机器人更加智能
////        if (messageResponseBody != null) {
////            if (!CollectionUtils.isEmpty(messageResponseBody.getChoices())) {
////                String replyText = messageResponseBody.getChoices().get(0).getText();
////                // 拼接字符,设置回去
////                return replyText.replace("小小鹏:", "");
////            }
////        }
////        return "暂时不明白你说什么!";
////    }
//
//    private JSONObject getReplyFromGPT(String message) {
//        String url = this.url;
//        Map<String, String> header = new HashMap();
////        header.put("Authorization", "Bearer " + apiKey);
//        List<ChatGptToken> chatGptTokens=chatGptTokenService.selectALL();
//        if (Xtool.isNull(chatGptTokens)){
//            return null;
//        }
////        header.put("Authorization", "Bearer " + Constants.apiKey);
//        header.put("Authorization", "Bearer " + chatGptTokens.get(0).getToken());
//        header.put("Content-Type", "application/json");
//        MessageSendBody messageSendBody = buildConfig();
//        messageSendBody.setPrompt(message);
//        String body = JSON.toJSONString(messageSendBody, SerializerFeature.PrettyFormat, SerializerFeature.WriteMapNullValue, SerializerFeature.WriteDateUseDateFormat);
//        log.info("发送的数据：" + body);
//        // 发送请求
//        String data = HttpUtil.doPostJson(url, body, header);
//        JSONObject obj = JSON.parseObject(data);
//        log.info("接受数据："+obj.toJSONString());
//        return obj;
//    }
//
//    /**
//     * 构建请求体
//     *
//     * @return
//     */
//    private MessageSendBody buildConfig() {
//        MessageSendBody messageSendBody = new MessageSendBody();
//        messageSendBody.setModel("text-davinci-003");
//        messageSendBody.setTemperature(0.9);
//        messageSendBody.setMaxTokens(1000);
//        messageSendBody.setTopP(1);
//        messageSendBody.setFrequencyPenalty(0.0);
//        messageSendBody.setPresencePenalty(0.6);
//        List<String> stop = new ArrayList<>();
//        stop.add(" 小小鹏:");
//        stop.add(" 小梦:");
//        messageSendBody.setStop(stop);
//        return messageSendBody;
//    }
//
//    /**
//     * 解决大文章问题超5秒问题，但是目前事个人订阅号，没有客服接口权限，暂时没用
//     *
//     * @param messageContent
//     * @param userKey
//     * @return
//     */
//    public String getArticle(String messageContent, String userKey) {
//        String url = "https://api.openai.com/v1/completions";
//        Map<String, String> header = new HashMap();
////        header.put("Authorization", "Bearer " + apiKey);
//        header.put("Authorization", "Bearer " + Constants.apiKey);
//        header.put("Content-Type", "application/json");
//
//        MessageSendBody messageSendBody = buildConfig();
//        messageSendBody.setMaxTokens(150);
//        messageSendBody.setPrompt(messageContent);
//        String body = JSON.toJSONString(messageSendBody, SerializerFeature.PrettyFormat, SerializerFeature.WriteMapNullValue, SerializerFeature.WriteDateUseDateFormat);
//        String data = HttpUtil.doPostJson(url, body, header);
//        log.info("返回的数据：" + data);
//        JSONObject obj = JSON.parseObject(data);
//        MessageResponseBody messageResponseBody = JSONObject.toJavaObject(obj, MessageResponseBody.class);
//        if (messageResponseBody != null) {
//            if (!CollectionUtils.isEmpty(messageResponseBody.getChoices())) {
//                String replyText = messageResponseBody.getChoices().get(0).getText();
//                return replyText.replace("小小鹏:", "");
//            }
//        }
//        return "暂时不明白你说什么!";
//    }
//}
