package com.sy.model.game;

import com.baomidou.mybatisplus.annotations.TableField;
import com.baomidou.mybatisplus.annotations.TableId;
import com.baomidou.mybatisplus.annotations.TableName;
import com.baomidou.mybatisplus.enums.IdType;
import lombok.Data;

@Data
@TableName("game_arena_rank")
public class GameArenaRank {
    @TableId(value = "id", type = IdType.AUTO)
    private Integer id;
    @TableField("user_id")
    private Integer userId;
    @TableField("user_name")
    private String userName;
    @TableField("arena_level")
    private String arenaLevel;
    @TableField("week_num")
    private Integer weekNum;
    @TableField("current_rank")
    private Integer currentRank;
    @TableField("last_week_rank")
    private Integer lastWeekRank;
    @TableField("is_settle")
    private Integer isSettle;
    @TableField("reward_status")
    private Integer rewardStatus;
    @TableField("img")
    private String img;
    @TableField("arena_score")
    private Integer arenaScore;

}