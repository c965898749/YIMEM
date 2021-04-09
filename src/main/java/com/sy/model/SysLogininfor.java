package com.sy.model;

import java.util.Date;

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

    public SysLogininfor(Long infoId, String userName, String ipaddr, String loginLocation, String browser, String os, String status, String msg, Date loginTime) {
        this.infoId = infoId;
        this.userName = userName;
        this.ipaddr = ipaddr;
        this.loginLocation = loginLocation;
        this.browser = browser;
        this.os = os;
        this.status = status;
        this.msg = msg;
        this.loginTime = loginTime;
    }

    public SysLogininfor() {
        super();
    }

    public Long getInfoId() {
        return infoId;
    }

    public void setInfoId(Long infoId) {
        this.infoId = infoId;
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName == null ? null : userName.trim();
    }

    public String getIpaddr() {
        return ipaddr;
    }

    public void setIpaddr(String ipaddr) {
        this.ipaddr = ipaddr == null ? null : ipaddr.trim();
    }

    public String getLoginLocation() {
        return loginLocation;
    }

    public void setLoginLocation(String loginLocation) {
        this.loginLocation = loginLocation == null ? null : loginLocation.trim();
    }

    public String getBrowser() {
        return browser;
    }

    public void setBrowser(String browser) {
        this.browser = browser == null ? null : browser.trim();
    }

    public String getOs() {
        return os;
    }

    public void setOs(String os) {
        this.os = os == null ? null : os.trim();
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status == null ? null : status.trim();
    }

    public String getMsg() {
        return msg;
    }

    public void setMsg(String msg) {
        this.msg = msg == null ? null : msg.trim();
    }

    public Date getLoginTime() {
        return loginTime;
    }

    public void setLoginTime(Date loginTime) {
        this.loginTime = loginTime;
    }
}