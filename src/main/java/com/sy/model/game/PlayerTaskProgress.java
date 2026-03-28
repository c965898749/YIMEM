package com.sy.model.game;

import com.baomidou.mybatisplus.annotations.TableField;
import com.baomidou.mybatisplus.annotations.TableId;
import com.baomidou.mybatisplus.annotations.TableName;
import com.baomidou.mybatisplus.enums.IdType;
import lombok.Data;

import java.util.Date;
@Data
@TableName("player_task_progress")
public class PlayerTaskProgress {
    @TableId(value = "id", type = IdType.AUTO)
    private Long id;
    @TableField("player_id")
    private Long playerId;
    @TableField("task_id")
    private Integer taskId;
    @TableField("task_type")
    private Byte taskType;
    @TableField("status")
    private Byte status;
    @TableField("active_received")
    private Byte activeReceived;
    @TableField("cycle_tag")
    private String cycleTag;
    @TableField("accept_time")
    private Date acceptTime;
    @TableField("complete_time")
    private Date completeTime;
    @TableField("reward_time")
    private Date rewardTime;
    @TableField("create_time")
    private Date createTime;
    @TableField("update_time")
    private Date updateTime;
    @TableField("progress")
    private String progress;


}