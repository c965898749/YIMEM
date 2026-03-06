package com.sy.model.game;

import com.baomidou.mybatisplus.annotations.TableField;
import com.baomidou.mybatisplus.annotations.TableId;
import com.baomidou.mybatisplus.annotations.TableName;
import com.baomidou.mybatisplus.enums.IdType;
import lombok.Data;

import java.util.Date;
@Data
@TableName("pill_rob_record")
public class PillRobRecord {
    @TableId(value = "id", type = IdType.AUTO)
    private Long id;
    @TableField("robber_id")
    private Integer robberId;
    @TableField(exist = false)
    private String robberName;
    @TableField("victim_id")
    private Integer victimId;
    @TableField(exist = false)
    private String victimName;
    @TableField("rob_time")
    private Date robTime;
    @TableField("rob_date")
    private Date robDate;
    @TableField("rob_result")
    private Integer robResult;
    @TableField("rob_pill_num")
    private Integer robPillNum;
    @TableField("rob_material")
    private String robMaterial;
    @TableField("free_rob_count")
    private Integer freeRobCount;
    @TableField("create_time")
    private Date createTime;
    @TableField("update_time")
    private Date updateTime;
    @TableField(exist = false)
    private String timeStr;
    @TableField(exist = false)
    private String nickname;
    @TableField(exist = false)
    private String nickname2;
    @TableField(exist = false)
    private String gameImg;
    @TableField("fight_id")
    private String fightId;
}