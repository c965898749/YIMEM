package com.sy.model;

import java.util.Date;

public class ScanRecord {
    private Integer id;

    private String totalamount;

    private String outtradeno;

    private String subject;

    private String undiscountableamount;

    private String sellerid;

    private String body;

    private Date createTime;

    private String qrcode;

    private Integer userid;

    public ScanRecord(Integer id, String totalamount, String outtradeno, String subject, String undiscountableamount, String sellerid, String body, Date createTime, String qrcode, Integer userid) {
        this.id = id;
        this.totalamount = totalamount;
        this.outtradeno = outtradeno;
        this.subject = subject;
        this.undiscountableamount = undiscountableamount;
        this.sellerid = sellerid;
        this.body = body;
        this.createTime = createTime;
        this.qrcode = qrcode;
        this.userid = userid;
    }

    public ScanRecord() {
        super();
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getTotalamount() {
        return totalamount;
    }

    public void setTotalamount(String totalamount) {
        this.totalamount = totalamount == null ? null : totalamount.trim();
    }

    public String getOuttradeno() {
        return outtradeno;
    }

    public void setOuttradeno(String outtradeno) {
        this.outtradeno = outtradeno == null ? null : outtradeno.trim();
    }

    public String getSubject() {
        return subject;
    }

    public void setSubject(String subject) {
        this.subject = subject == null ? null : subject.trim();
    }

    public String getUndiscountableamount() {
        return undiscountableamount;
    }

    public void setUndiscountableamount(String undiscountableamount) {
        this.undiscountableamount = undiscountableamount == null ? null : undiscountableamount.trim();
    }

    public String getSellerid() {
        return sellerid;
    }

    public void setSellerid(String sellerid) {
        this.sellerid = sellerid == null ? null : sellerid.trim();
    }

    public String getBody() {
        return body;
    }

    public void setBody(String body) {
        this.body = body == null ? null : body.trim();
    }

    public Date getCreateTime() {
        return createTime;
    }

    public void setCreateTime(Date createTime) {
        this.createTime = createTime;
    }

    public String getQrcode() {
        return qrcode;
    }

    public void setQrcode(String qrcode) {
        this.qrcode = qrcode == null ? null : qrcode.trim();
    }

    public Integer getUserid() {
        return userid;
    }

    public void setUserid(Integer userid) {
        this.userid = userid;
    }
}