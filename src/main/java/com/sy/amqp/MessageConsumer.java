package com.sy.amqp;

import com.alibaba.fastjson.JSONObject;
import com.sy.service.UpdateMessage;
import com.sy.tool.Xtool;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.amqp.core.Message;
import org.springframework.amqp.core.MessageListener;
import org.springframework.beans.factory.annotation.Autowired;

import java.io.UnsupportedEncodingException;
import java.util.Map;

public class MessageConsumer implements MessageListener {
    @Autowired
    private UpdateMessage updateMessage;

    private Logger logger = LoggerFactory.getLogger(MessageConsumer.class);

    public void onMessage(Message msg) {
        String routingKey = msg.getMessageProperties().getReceivedRoutingKey();
        if ("message.messge".equals(routingKey)) {
            String result = null;
            try {
                result = new String(msg.getBody(), "UTF-8");
            } catch (UnsupportedEncodingException e) {
                e.printStackTrace();
            }
            System.out.println("收到消息");
            if (Xtool.isNotNull(result)) {
                Integer userId = Integer.parseInt(result);
                updateMessage.delRediskey(userId);
                if (updateMessage.delRediskey(userId) == 1) {
                 updateMessage.updateUserInfo(userId);
                }
            }
        }
    }

}
