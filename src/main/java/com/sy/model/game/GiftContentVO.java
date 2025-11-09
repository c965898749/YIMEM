package com.sy.model.game;

import lombok.Data;

@Data
public class GiftContentVO {
    private String itemName; // 物品名称（如“钻石”“回血药”）
    private Integer itemQuantity; // 数量
    private Integer itemType; // 物品类型
}