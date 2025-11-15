package com.sy.model.game;

public class GameItemShop {
    private Integer itemId;

    private String itemName;

    private String quality;

    private Integer goldEdgePrice;

    private Integer gemPrice;

    private Integer stock;

    private String itemDesc;

    public GameItemShop(Integer itemId, String itemName, String quality, Integer goldEdgePrice, Integer gemPrice, Integer stock) {
        this.itemId = itemId;
        this.itemName = itemName;
        this.quality = quality;
        this.goldEdgePrice = goldEdgePrice;
        this.gemPrice = gemPrice;
        this.stock = stock;
    }

    public GameItemShop(Integer itemId, String itemName, String quality, Integer goldEdgePrice, Integer gemPrice, Integer stock, String itemDesc) {
        this.itemId = itemId;
        this.itemName = itemName;
        this.quality = quality;
        this.goldEdgePrice = goldEdgePrice;
        this.gemPrice = gemPrice;
        this.stock = stock;
        this.itemDesc = itemDesc;
    }

    public GameItemShop() {
        super();
    }

    public Integer getItemId() {
        return itemId;
    }

    public void setItemId(Integer itemId) {
        this.itemId = itemId;
    }

    public String getItemName() {
        return itemName;
    }

    public void setItemName(String itemName) {
        this.itemName = itemName == null ? null : itemName.trim();
    }

    public String getQuality() {
        return quality;
    }

    public void setQuality(String quality) {
        this.quality = quality == null ? null : quality.trim();
    }

    public Integer getGoldEdgePrice() {
        return goldEdgePrice;
    }

    public void setGoldEdgePrice(Integer goldEdgePrice) {
        this.goldEdgePrice = goldEdgePrice;
    }

    public Integer getGemPrice() {
        return gemPrice;
    }

    public void setGemPrice(Integer gemPrice) {
        this.gemPrice = gemPrice;
    }

    public Integer getStock() {
        return stock;
    }

    public void setStock(Integer stock) {
        this.stock = stock;
    }

    public String getItemDesc() {
        return itemDesc;
    }

    public void setItemDesc(String itemDesc) {
        this.itemDesc = itemDesc == null ? null : itemDesc.trim();
    }
}