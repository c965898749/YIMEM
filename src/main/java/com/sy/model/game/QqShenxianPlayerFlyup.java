package com.sy.model.game;

import com.baomidou.mybatisplus.annotations.TableField;
import com.baomidou.mybatisplus.annotations.TableId;
import com.baomidou.mybatisplus.annotations.TableName;
import com.baomidou.mybatisplus.enums.IdType;
import lombok.Data;

import java.util.Date;
@Data
@TableName("qq_shenxian_player_flyup")
public class QqShenxianPlayerFlyup {
    @TableId(value = "id", type = IdType.AUTO)
    private Long id;
    @TableField("player_id")
    private Integer playerId;
    @TableField("flyup_times")
    private Integer flyupTimes;
    @TableField("total_level")
    private Integer totalLevel;
    @TableField("total_dan_consume")
    private Integer totalDanConsume;
    @TableField("flyup_status")
    private Byte flyupStatus;
    @TableField("flyup_time")
    private Date flyupTime;

}