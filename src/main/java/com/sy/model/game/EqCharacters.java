package com.sy.model.game;

import com.baomidou.mybatisplus.annotations.TableField;
import com.baomidou.mybatisplus.annotations.TableId;
import com.baomidou.mybatisplus.annotations.TableName;
import com.baomidou.mybatisplus.enums.IdType;
import lombok.Data;

import java.math.BigDecimal;
import java.util.Date;
@Data
@TableName("eq_characters")
public class EqCharacters {
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
    @TableField(exist = false)
    private Integer fireRes;
    @TableField(exist = false)
    private Integer poisonRes;
    @TableField(exist = false)
    private Integer projectileRes;
    @TableField(exist = false)
    private Integer fireGrowth;
    @TableField(exist = false)
    private Integer poisonGrowth;
    @TableField(exist = false)
    private Integer projectileGrowth;
    @TableField(exist = false)
    private String img;
    @TableField(exist = false)
    private String camp;
    @TableField(exist = false)
    private BigDecimal star;
    @TableField(exist = false)
    private String name;
    @TableField(exist = false)
    private String introduce;
    @TableField(exist = false)
    private Integer eqType;
    @TableField(exist = false)
    private String profession;
    @TableField(exist = false)
    private Integer wlAtk;
    @TableField(exist = false)
    private Integer hyAtk;
    @TableField(exist = false)
    private Integer dsAtk;
    @TableField(exist = false)
    private Integer fdAtk;
    @TableField(exist = false)
    private Integer wlDef;
    @TableField(exist = false)
    private Integer hyDef;
    @TableField(exist = false)
    private Integer dsDef;
    @TableField(exist = false)
    private Integer fdDef;
    @TableField(exist = false)
    private Integer zlDef;
}