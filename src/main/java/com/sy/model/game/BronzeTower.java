package com.sy.model.game;

import com.baomidou.mybatisplus.annotations.TableField;
import com.baomidou.mybatisplus.annotations.TableId;
import com.baomidou.mybatisplus.annotations.TableName;
import com.baomidou.mybatisplus.enums.IdType;
import lombok.Data;

@Data
@TableName("bronze_tower")
public class BronzeTower {
    @TableId(value = "id", type = IdType.AUTO)
    private Integer id;
    @TableField("floor_num")
    private Byte floorNum;
    @TableField("boss_name")
    private String bossName;
    @TableField("boss_level")
    private Byte bossLevel;
    @TableField("reward_gold")
    private Integer rewardGold;
    @TableField("reward_diamond")
    private Integer rewardDiamond;
    @TableField("reward_exp")
    private Integer rewardExp;
    @TableField("reward_item1")
    private String rewardItem1;
    @TableField("reward_item1_num")
    private Byte rewardItem1Num;
    @TableField("reward_item2")
    private String rewardItem2;
    @TableField("reward_item2_num")
    private Byte rewardItem2Num;
    @TableField("reward_desc")
    private String rewardDesc;


}