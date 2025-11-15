package com.sy.model.game;

import java.util.Date;

public class ItemPurchaseRecord {
    private Integer recordId;

    private Integer playerId;

    private Integer itemId;

    private Date purchaseTime;

    private String payType;

    private Integer payAmount;

    private Integer purchaseQuantity;

    public ItemPurchaseRecord(Integer recordId, Integer playerId, Integer itemId, Date purchaseTime, String payType, Integer payAmount, Integer purchaseQuantity) {
        this.recordId = recordId;
        this.playerId = playerId;
        this.itemId = itemId;
        this.purchaseTime = purchaseTime;
        this.payType = payType;
        this.payAmount = payAmount;
        this.purchaseQuantity = purchaseQuantity;
    }

    public ItemPurchaseRecord() {
        super();
    }

    public Integer getRecordId() {
        return recordId;
    }

    public void setRecordId(Integer recordId) {
        this.recordId = recordId;
    }

    public Integer getPlayerId() {
        return playerId;
    }

    public void setPlayerId(Integer playerId) {
        this.playerId = playerId;
    }

    public Integer getItemId() {
        return itemId;
    }

    public void setItemId(Integer itemId) {
        this.itemId = itemId;
    }

    public Date getPurchaseTime() {
        return purchaseTime;
    }

    public void setPurchaseTime(Date purchaseTime) {
        this.purchaseTime = purchaseTime;
    }

    public String getPayType() {
        return payType;
    }

    public void setPayType(String payType) {
        this.payType = payType == null ? null : payType.trim();
    }

    public Integer getPayAmount() {
        return payAmount;
    }

    public void setPayAmount(Integer payAmount) {
        this.payAmount = payAmount;
    }

    public Integer getPurchaseQuantity() {
        return purchaseQuantity;
    }

    public void setPurchaseQuantity(Integer purchaseQuantity) {
        this.purchaseQuantity = purchaseQuantity;
    }
}