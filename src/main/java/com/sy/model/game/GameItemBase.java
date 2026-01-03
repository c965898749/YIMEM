package com.sy.model.game;

import com.baomidou.mybatisplus.annotations.TableField;
import com.baomidou.mybatisplus.annotations.TableId;
import com.baomidou.mybatisplus.annotations.TableName;
import com.baomidou.mybatisplus.enums.IdType;
import lombok.Data;

import java.util.Date;
@Data
@TableName("game_item_base")
public class GameItemBase {
    @TableId(value = "item_id", type = IdType.AUTO)
    private Integer itemId;
    @TableField("item_name")
    private String itemName;
    @TableField("item_type")
    private Byte itemType;
    @TableField("sub_type")
    private Byte subType;
    @TableField("quality")
    private Byte quality;
    @TableField("icon")
    private String icon;
    @TableField("description")
    private String description;
    @TableField("max_stack")
    private Integer maxStack;
    @TableField("use_level")
    private Byte useLevel;
    @TableField("is_bind")
    private Byte isBind;
    @TableField("use_effect")
    private String useEffect;
    @TableField("sell_price")
    private Integer sellPrice;
    @TableField("buy_price")
    private Integer buyPrice;
    @TableField("is_valid")
    private Byte isValid;
    @TableField("create_time")
    private Date createTime;
    @TableField("update_time")
    private Date updateTime;


}