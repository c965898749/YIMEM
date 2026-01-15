package com.sy.model.game;

import com.baomidou.mybatisplus.annotations.TableField;
import com.baomidou.mybatisplus.annotations.TableId;
import com.baomidou.mybatisplus.annotations.TableName;
import com.baomidou.mybatisplus.enums.IdType;
import lombok.Data;

@Data
@TableName("bronze_boss_detail")
public class BronzeBossDetail {
    @TableId(value = "id", type = IdType.AUTO)
    private Integer id;
    @TableField("daily_max_times")
    private Byte dailyMaxTimes;
    @TableField("day")
    private Byte day;
    @TableField("activity_code")
    private String activityCode;
    @TableField("difficulty_level")
    private Integer difficultyLevel;
    @TableField("boss_id")
    private Integer bossId;
    @TableField("boss_name")
    private String bossName;
    @TableField("detail_code")
    private String detailCode;
    @TableField("go_into_num")
    private Integer goIntoNum;

}