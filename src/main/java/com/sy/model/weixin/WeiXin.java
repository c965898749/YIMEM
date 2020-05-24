package com.sy.model.weixin;

import lombok.Data;

@Data
public class WeiXin {
    private String access_token;
    private Integer expires_in;
    private String refresh_token;
    private String openid;
    private String scope;
}
