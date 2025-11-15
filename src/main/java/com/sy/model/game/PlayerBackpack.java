package com.sy.model.game;

import java.util.Date;

public class PlayerBackpack {
    private Integer backpackId;

    private Integer playerId;

    private Integer itemId;

    private Integer itemQuantity;

    private Date getTime;

    private Integer isBound;

    private Integer durability;

    public PlayerBackpack(Integer backpackId, Integer playerId, Integer itemId, Integer itemQuantity, Date getTime, Integer isBound, Integer durability) {
        this.backpackId = backpackId;
        this.playerId = playerId;
        this.itemId = itemId;
        this.itemQuantity = itemQuantity;
        this.getTime = getTime;
        this.isBound = isBound;
        this.durability = durability;
    }

    public PlayerBackpack() {
        super();
    }

    public Integer getBackpackId() {
        return backpackId;
    }

    public void setBackpackId(Integer backpackId) {
        this.backpackId = backpackId;
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

    public Integer getItemQuantity() {
        return itemQuantity;
    }

    public void setItemQuantity(Integer itemQuantity) {
        this.itemQuantity = itemQuantity;
    }

    public Date getGetTime() {
        return getTime;
    }

    public void setGetTime(Date getTime) {
        this.getTime = getTime;
    }

    public Integer getIsBound() {
        return isBound;
    }

    public void setIsBound(Integer isBound) {
        this.isBound = isBound;
    }

    public Integer getDurability() {
        return durability;
    }

    public void setDurability(Integer durability) {
        this.durability = durability;
    }
}