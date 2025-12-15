package com.sy.model.game;

import com.baomidou.mybatisplus.annotations.TableField;
import com.baomidou.mybatisplus.annotations.TableId;
import com.baomidou.mybatisplus.annotations.TableName;
import com.baomidou.mybatisplus.enums.IdType;
import lombok.Data;

import java.math.BigDecimal;
import java.util.Date;
@Data
@TableName("pve_reward")
public class PveReward {
    @TableId(value = "id", type = IdType.AUTO)
    private Long id;
    @TableField("detail_code")
    private String detailCode;
    @TableField("star_level")
    private Byte starLevel;
    @TableField("difficulty_level")
    private String difficultyLevel;
    @TableField("reward_type")
    private String rewardType;
    @TableField("reward_amount")
    private Integer rewardAmount;
    @TableField("reward_desc")
    private String rewardDesc;
    @TableField("status")
    private Byte status;
    @TableField("create_time")
    private Date createTime;
    @TableField("update_time")
    private Date updateTime;
    @TableField("item_id")
    private Integer itemId;
    @TableField("prent")
    private Integer prent;

}