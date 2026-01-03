package com.sy.model.game;

import com.baomidou.mybatisplus.annotations.TableField;
import com.baomidou.mybatisplus.annotations.TableId;
import com.baomidou.mybatisplus.annotations.TableName;
import com.baomidou.mybatisplus.enums.IdType;
import lombok.Data;

import java.util.Date;
@Data
@TableName("game_arena_battlecharacters")
public class GameArenaBattlecharacters {
    @TableId(value = "uuid", type = IdType.AUTO)
    private Integer uuid;
    @TableField("user_id")
    private Integer userId;
    @TableField("id")
    private String id;
    @TableField("lv")
    private Integer lv;
    @TableField("stack_count")
    private Integer stackCount;
    @TableField("create_time")
    private Date createTime;
    @TableField("update_time")
    private Date updateTime;
    @TableField("go_into_num")
    private Integer goIntoNum;
    @TableField("max_lv")
    private Integer maxLv;
    @TableField("exp")
    private Integer exp;
    @TableField("is_delete")
    private String isDelete;
    @TableField("week_num")
    private Integer weekNum;
    @TableField("arena_level")
    private String arenaLevel;
}