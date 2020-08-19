package com.sy.model;

public class PaymentRecord {
    private Integer id;

    private String gmtCreate;

    private String charset;

    private String sellerEmail;

    private String notifyTime;

    private String subject;

    private String body;

    private String buyerId;

    private String version;

    private String notifyId;

    private String notifyType;

    private String outTradeNo;

    private String totalAmount;

    private String tradeStatus;

    private String tradeNo;

    private String authAppId;

    private String buyerLogonId;

    private String appId;

    private String sellerId;

    private String pointAmount;

    public PaymentRecord(Integer id, String gmtCreate, String charset, String sellerEmail, String notifyTime, String subject, String body, String buyerId, String version, String notifyId, String notifyType, String outTradeNo, String totalAmount, String tradeStatus, String tradeNo, String authAppId, String buyerLogonId, String appId, String sellerId, String pointAmount) {
        this.id = id;
        this.gmtCreate = gmtCreate;
        this.charset = charset;
        this.sellerEmail = sellerEmail;
        this.notifyTime = notifyTime;
        this.subject = subject;
        this.body = body;
        this.buyerId = buyerId;
        this.version = version;
        this.notifyId = notifyId;
        this.notifyType = notifyType;
        this.outTradeNo = outTradeNo;
        this.totalAmount = totalAmount;
        this.tradeStatus = tradeStatus;
        this.tradeNo = tradeNo;
        this.authAppId = authAppId;
        this.buyerLogonId = buyerLogonId;
        this.appId = appId;
        this.sellerId = sellerId;
        this.pointAmount = pointAmount;
    }

    public PaymentRecord() {
        super();
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getGmtCreate() {
        return gmtCreate;
    }

    public void setGmtCreate(String gmtCreate) {
        this.gmtCreate = gmtCreate == null ? null : gmtCreate.trim();
    }

    public String getCharset() {
        return charset;
    }

    public void setCharset(String charset) {
        this.charset = charset == null ? null : charset.trim();
    }

    public String getSellerEmail() {
        return sellerEmail;
    }

    public void setSellerEmail(String sellerEmail) {
        this.sellerEmail = sellerEmail == null ? null : sellerEmail.trim();
    }

    public String getNotifyTime() {
        return notifyTime;
    }

    public void setNotifyTime(String notifyTime) {
        this.notifyTime = notifyTime == null ? null : notifyTime.trim();
    }

    public String getSubject() {
        return subject;
    }

    public void setSubject(String subject) {
        this.subject = subject == null ? null : subject.trim();
    }

    public String getBody() {
        return body;
    }

    public void setBody(String body) {
        this.body = body == null ? null : body.trim();
    }

    public String getBuyerId() {
        return buyerId;
    }

    public void setBuyerId(String buyerId) {
        this.buyerId = buyerId == null ? null : buyerId.trim();
    }

    public String getVersion() {
        return version;
    }

    public void setVersion(String version) {
        this.version = version == null ? null : version.trim();
    }

    public String getNotifyId() {
        return notifyId;
    }

    public void setNotifyId(String notifyId) {
        this.notifyId = notifyId == null ? null : notifyId.trim();
    }

    public String getNotifyType() {
        return notifyType;
    }

    public void setNotifyType(String notifyType) {
        this.notifyType = notifyType == null ? null : notifyType.trim();
    }

    public String getOutTradeNo() {
        return outTradeNo;
    }

    public void setOutTradeNo(String outTradeNo) {
        this.outTradeNo = outTradeNo == null ? null : outTradeNo.trim();
    }

    public String getTotalAmount() {
        return totalAmount;
    }

    public void setTotalAmount(String totalAmount) {
        this.totalAmount = totalAmount == null ? null : totalAmount.trim();
    }

    public String getTradeStatus() {
        return tradeStatus;
    }

    public void setTradeStatus(String tradeStatus) {
        this.tradeStatus = tradeStatus == null ? null : tradeStatus.trim();
    }

    public String getTradeNo() {
        return tradeNo;
    }

    public void setTradeNo(String tradeNo) {
        this.tradeNo = tradeNo == null ? null : tradeNo.trim();
    }

    public String getAuthAppId() {
        return authAppId;
    }

    public void setAuthAppId(String authAppId) {
        this.authAppId = authAppId == null ? null : authAppId.trim();
    }

    public String getBuyerLogonId() {
        return buyerLogonId;
    }

    public void setBuyerLogonId(String buyerLogonId) {
        this.buyerLogonId = buyerLogonId == null ? null : buyerLogonId.trim();
    }

    public String getAppId() {
        return appId;
    }

    public void setAppId(String appId) {
        this.appId = appId == null ? null : appId.trim();
    }

    public String getSellerId() {
        return sellerId;
    }

    public void setSellerId(String sellerId) {
        this.sellerId = sellerId == null ? null : sellerId.trim();
    }

    public String getPointAmount() {
        return pointAmount;
    }

    public void setPointAmount(String pointAmount) {
        this.pointAmount = pointAmount == null ? null : pointAmount.trim();
    }
}