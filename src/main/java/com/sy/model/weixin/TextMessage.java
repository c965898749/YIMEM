package com.sy.model.weixin;

import lombok.Data;

@Data
public class TextMessage {
    private String ToUserName;
    private String FromUserName;
    private  String CreateTime;
    private  String MsgType;
    private String Content;
    private String MsgId;

}
