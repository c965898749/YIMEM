package com.sy.model.weixin;
import lombok.Data;

@Data
public class WeiXinUser {
    private  String openid;
    private String nickname;
    private String sex;
    private String province;
    private String city;
    private String headimgurl;
}
