package com.sy.model.game;

import com.baomidou.mybatisplus.annotations.TableField;
import com.baomidou.mybatisplus.annotations.TableId;
import com.baomidou.mybatisplus.annotations.TableName;
import com.baomidou.mybatisplus.enums.IdType;
import lombok.Data;

import java.util.Date;
@Data
@TableName("game_player_bag")
public class GamePlayerBag {
    @TableId(value = "bag_id", type = IdType.AUTO)
    private Integer bagId;
    @TableField("user_id")
    private Integer userId;
    @TableField("item_id")
    private Integer itemId;
    @TableField("item_count")
    private Integer itemCount;
    @TableField("grid_index")
    private Integer gridIndex;
    @TableField("bind_status")
    private Byte bindStatus;
    @TableField("valid_start_time")
    private Date validStartTime;
    @TableField("valid_end_time")
    private Date validEndTime;
    @TableField("is_used")
    private Byte isUsed;
    @TableField("extra_attr")
    private String extraAttr;
    @TableField("obtain_time")
    private Date obtainTime;
    @TableField("create_time")
    private Date createTime;
    @TableField("update_time")
    private Date updateTime;
    @TableField(exist = false)
    private String itemName;
    @TableField(exist = false)
    private String description;
    @TableField(exist = false)
    private String icon;
    @TableField("is_delete")
    private String isDelete;
    @TableField(exist = false)
    private Integer itemType;

}