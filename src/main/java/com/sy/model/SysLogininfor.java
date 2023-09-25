package com.sy.model;

import lombok.Data;

import java.util.Date;
@Data
public class SysLogininfor {
    private Long infoId;

    private String userName;

    private String ipaddr;

    private String loginLocation;

    private String browser;

    private String os;

    private String status;

    private String msg;

    private Date loginTime;

    private String country;

    private String region;

    private String province;

    private String city;

    private String isp;
}