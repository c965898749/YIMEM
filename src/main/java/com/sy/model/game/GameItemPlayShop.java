package com.sy.model.game;

import com.baomidou.mybatisplus.annotations.TableField;
import com.baomidou.mybatisplus.annotations.TableId;
import com.baomidou.mybatisplus.annotations.TableName;
import com.baomidou.mybatisplus.enums.IdType;
import lombok.Data;

@Data
@TableName("game_item_play_shop")
public class GameItemPlayShop {
    @TableId(value = "item_id", type = IdType.AUTO)
    private Integer itemId;
    @TableField("item_name")
    private String itemName;
    @TableField("quality")
    private String quality;
    @TableField("gold_edge_price")
    private Integer goldEdgePrice;
    @TableField("gem_price")
    private Integer gemPrice;
    @TableField("stock")
    private Integer stock;
    @TableField("type")
    private String type;
    @TableField("item_desc")
    private String itemDesc;
    @TableField(exist = false)
    private   String icon;
    @TableField(exist = false)
    private String description;
}