package com.sy.model;

import java.math.BigDecimal;
import java.util.Date;

public class ScanRecord {
    private Integer id;

    private BigDecimal totalamount;

    private String outtradeno;

    private String subject;

    private String undiscountableamount;

    private String sellerid;

    private String body;

    private Date createTime;

    private String qrcode;

    private Integer userid;

    private String status;

    private Date gmtCreate;

    private Date gmtPayment;

    private String notifyId;

    private String buyerLogonId;

    private String buyerId;

    public ScanRecord(Integer id, BigDecimal totalamount, String outtradeno, String subject, String undiscountableamount, String sellerid, String body, Date createTime, String qrcode, Integer userid, String status, Date gmtCreate, Date gmtPayment, String notifyId, String buyerLogonId, String buyerId) {
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
        this.status = status;
        this.gmtCreate = gmtCreate;
        this.gmtPayment = gmtPayment;
        this.notifyId = notifyId;
        this.buyerLogonId = buyerLogonId;
        this.buyerId = buyerId;
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

    public BigDecimal getTotalamount() {
        return totalamount;
    }

    public void setTotalamount(BigDecimal totalamount) {
        this.totalamount = totalamount;
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

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status == null ? null : status.trim();
    }

    public Date getGmtCreate() {
        return gmtCreate;
    }

    public void setGmtCreate(Date gmtCreate) {
        this.gmtCreate = gmtCreate;
    }

    public Date getGmtPayment() {
        return gmtPayment;
    }

    public void setGmtPayment(Date gmtPayment) {
        this.gmtPayment = gmtPayment;
    }

    public String getNotifyId() {
        return notifyId;
    }

    public void setNotifyId(String notifyId) {
        this.notifyId = notifyId == null ? null : notifyId.trim();
    }

    public String getBuyerLogonId() {
        return buyerLogonId;
    }

    public void setBuyerLogonId(String buyerLogonId) {
        this.buyerLogonId = buyerLogonId == null ? null : buyerLogonId.trim();
    }

    public String getBuyerId() {
        return buyerId;
    }

    public void setBuyerId(String buyerId) {
        this.buyerId = buyerId == null ? null : buyerId.trim();
    }
}