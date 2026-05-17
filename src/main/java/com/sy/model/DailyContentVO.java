package com.sy.model;

import lombok.Data;

@Data
public class DailyContentVO {
    private String itemName; // 物品名称（如“灵石”“回血药”）
    private Integer itemQuantity; // 数量
    private Integer itemType; // 物品类型
    private String icon;
}