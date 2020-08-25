package com.sy.model;

import java.math.BigDecimal;

public class UserOrder {
    private Integer orderId;

    private String userId;

    private String orderNo;

    private BigDecimal orderAmount;

    private Integer orderStatus;

    private String createTime;

    private String lastUpdateTime;

    public UserOrder(Integer orderId, String userId, String orderNo, BigDecimal orderAmount, Integer orderStatus, String createTime, String lastUpdateTime) {
        this.orderId = orderId;
        this.userId = userId;
        this.orderNo = orderNo;
        this.orderAmount = orderAmount;
        this.orderStatus = orderStatus;
        this.createTime = createTime;
        this.lastUpdateTime = lastUpdateTime;
    }

    public UserOrder() {
        super();
    }

    public Integer getOrderId() {
        return orderId;
    }

    public void setOrderId(Integer orderId) {
        this.orderId = orderId;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId == null ? null : userId.trim();
    }

    public String getOrderNo() {
        return orderNo;
    }

    public void setOrderNo(String orderNo) {
        this.orderNo = orderNo == null ? null : orderNo.trim();
    }

    public BigDecimal getOrderAmount() {
        return orderAmount;
    }

    public void setOrderAmount(BigDecimal orderAmount) {
        this.orderAmount = orderAmount;
    }

    public Integer getOrderStatus() {
        return orderStatus;
    }

    public void setOrderStatus(Integer orderStatus) {
        this.orderStatus = orderStatus;
    }

    public String getCreateTime() {
        return createTime;
    }

    public void setCreateTime(String createTime) {
        this.createTime = createTime == null ? null : createTime.trim();
    }

    public String getLastUpdateTime() {
        return lastUpdateTime;
    }

    public void setLastUpdateTime(String lastUpdateTime) {
        this.lastUpdateTime = lastUpdateTime == null ? null : lastUpdateTime.trim();
    }
}