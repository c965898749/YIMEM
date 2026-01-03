package com.sy.model.game;

import com.baomidou.mybatisplus.annotations.TableField;
import com.baomidou.mybatisplus.annotations.TableId;
import com.baomidou.mybatisplus.annotations.TableName;
import com.baomidou.mybatisplus.enums.IdType;
import lombok.Data;

import java.util.Date;
@Data
@TableName("game_arena_signup")
public class GameArenaSignup {
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
    @TableField("week_start_date")
    private Date weekStartDate;
    @TableField("week_end_date")
    private Date weekEndDate;
    @TableField("sign_up_time")
    private Date signUpTime;
    @TableField("is_sign_up")
    private Integer isSignUp;
    @TableField("count")
    private Integer count;
    @TableField("arena_score")
    private Integer arenaScore;
    @TableField("win_num")
    private Integer winNum;
    @TableField("lose_num")
    private Integer loseNum;

}