package com.sy.model.game;

import com.baomidou.mybatisplus.annotations.TableField;
import com.baomidou.mybatisplus.annotations.TableId;
import com.baomidou.mybatisplus.annotations.TableName;
import com.baomidou.mybatisplus.enums.IdType;
import lombok.Data;

@Data
@TableName("ceremonial_gift")
public class CeremonialGift {
    @TableId(value = "item_id", type = IdType.AUTO)
    private Integer itemId;
    @TableField("item_type")
    private Integer itemType;
    @TableField("weight")
    private Integer weight;
    @TableField("award")
    private Integer award;
    @TableField("txt")
    private String txt;
    @TableField("icon")
    private String icon;
    @TableField(exist = false)
    private String isSign;
    @TableField(exist = false)
    private Integer index;
}