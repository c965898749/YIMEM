package com.sy.entity;

import lombok.Data;

import java.util.Date;

@Data
public class ActivationKey {

    private String code;
    private String type;
    private String actCode;
    private Date time;
    private String status;
    private String openId;
}
