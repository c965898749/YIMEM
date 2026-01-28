package com.sy.model.game;

import lombok.Data;

@Data
public class GameItemShop {
    private Integer itemId;

    private String itemName;

    private String quality;

    private Integer goldEdgePrice;

    private Integer gemPrice;

    private Integer stock;

    private String itemDesc;

    private String type;

    private Integer id;

    private Integer isBuy;
}