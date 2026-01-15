package com.sy.model.game;

import com.baomidou.mybatisplus.annotations.TableField;
import com.baomidou.mybatisplus.annotations.TableId;
import com.baomidou.mybatisplus.annotations.TableName;
import com.baomidou.mybatisplus.enums.IdType;
import lombok.Data;

import java.util.Date;
@Data
@TableName("game_notice")
public class GameNotice {
    @TableId(value = "id", type = IdType.AUTO)
    private Integer id;
    @TableField("create_time")
    private Date createTime;
    @TableField("description")
    private String description;
    @TableField("game_type")
    private String gameType;
}