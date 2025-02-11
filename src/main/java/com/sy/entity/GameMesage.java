package com.sy.entity;

import lombok.Data;

import java.util.Date;

@Data
public class GameMesage {

    private Integer id;
    private String status;
    private String uid;
    private Date time;
    private String content;
    private String type;
    private String appToken;
    private String verifyPay;
    private String summary;
    private String contentType;
}
