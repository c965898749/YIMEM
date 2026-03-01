package com.sy.model.game;

import com.baomidou.mybatisplus.annotations.TableField;
import com.baomidou.mybatisplus.annotations.TableId;
import com.baomidou.mybatisplus.annotations.TableName;
import com.baomidou.mybatisplus.enums.IdType;
import lombok.Data;

@Data
@TableName("game_eq_record")
public class GameEqRecord {
    @TableId(value = "record_id", type = IdType.AUTO)
    private Integer recordId;
    @TableField("user_id")
    private Integer userId;
    @TableField("picked")
    private String picked;


}