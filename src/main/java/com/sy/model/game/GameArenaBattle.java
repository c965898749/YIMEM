package com.sy.model.game;

import com.baomidou.mybatisplus.annotations.TableField;
import com.baomidou.mybatisplus.annotations.TableId;
import com.baomidou.mybatisplus.annotations.TableName;
import com.baomidou.mybatisplus.enums.IdType;
import lombok.Data;

import java.util.Date;
@Data
@TableName("game_arena_battle")
public class GameArenaBattle {

    @TableId(value = "id", type = IdType.AUTO)
    private Integer id;
    @TableField("user_id")
    private Integer userId;
    @TableField("arena_level")
    private Integer arenaLevel;
    @TableField("week_num")
    private Integer weekNum;
    @TableField("arena_score")
    private Integer arenaScore;
    @TableField("win_num")
    private Integer winNum;
    @TableField("lose_num")
    private Integer loseNum;
    @TableField("kill_num")
    private Integer killNum;
    @TableField("battle_last_time")
    private Date battleLastTime;
    @TableField("game_fight_id")
    private String gameFightId;
    @TableField("to_user_id")
    private Integer toUserId;
    @TableField("user_name")
    private String userName;
    @TableField("to_user_name")
    private String toUserName;
    @TableField("is_win")
    private Integer isWin;//0赢1输
    @TableField("img")
    private String img;
    @TableField("createtime")
    private Date createtime;
    @TableField(exist = false)
    private String timeStr;
}