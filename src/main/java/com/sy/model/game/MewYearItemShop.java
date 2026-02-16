package com.sy.model.game;

import com.baomidou.mybatisplus.annotations.TableField;
import com.baomidou.mybatisplus.annotations.TableName;
import lombok.Data;

@Data
@TableName("new_year_item_shop")
public class MewYearItemShop {
    @TableField("item_id")
    private Integer itemId;
    @TableField("item_name")
    private String itemName;
    @TableField("quality")
    private String quality;
    @TableField("stock")
    private Integer stock;
    @TableField("type")
    private String type;
    @TableField("num")
    private Integer num;
    @TableField("item_desc")
    private String itemDesc;
    @TableField("img")
    private String img;
}