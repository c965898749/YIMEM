package com.sy.model.game;

import com.baomidou.mybatisplus.annotations.TableField;
import com.baomidou.mybatisplus.annotations.TableId;
import com.baomidou.mybatisplus.annotations.TableName;
import com.baomidou.mybatisplus.enums.IdType;
import lombok.Data;

import java.util.Date;
@Data
@TableName("revenge_record")
public class RevengeRecord {
    @TableId(value = "id", type = IdType.AUTO)
    private Long id;
    @TableField("original_rob_id")
    private Long originalRobId;
    @TableField("revenge_player_id")
    private Integer revengePlayerId;
    @TableField("target_player_id")
    private Integer targetPlayerId;
    @TableField("revenge_time")
    private Date revengeTime;
    @TableField("revenge_result")
    private Byte revengeResult;
    @TableField("recover_pill_num")
    private Integer recoverPillNum;
    @TableField("recover_material")
    private String recoverMaterial;
    @TableField("is_free")
    private Byte isFree;
    @TableField("expire_time")
    private Date expireTime;
    @TableField("create_time")
    private Date createTime;
    @TableField("update_time")
    private Date updateTime;


}