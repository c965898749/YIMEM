package com.sy.model.game;

import com.baomidou.mybatisplus.annotations.TableField;
import com.baomidou.mybatisplus.annotations.TableId;
import com.baomidou.mybatisplus.annotations.TableName;
import com.baomidou.mybatisplus.enums.IdType;
import lombok.Data;

import java.math.BigDecimal;
@Data
@TableName("eq_card")
public class EqCard {
    @TableId(value = "uuid", type = IdType.AUTO)
    private Integer uuid;
    @TableField("name")
    private String name;
    @TableField("weight")
    private Double weight;
    @TableField("star")
    private BigDecimal star;
    @TableField("id")
    private String id;
    @TableField("camp")
    private String camp;
    @TableField("passive_introduce_one")
    private String passiveIntroduceOne;
    @TableField("passive_introduce_two")
    private String passiveIntroduceTwo;
    @TableField("passive_introduce_three")
    private String passiveIntroduceThree;
    @TableField("profession")
    private String profession;
    @TableField("introduce")
    private String introduce;
    //0兵刃1防具2法器3宝具
    @TableField("eq_type")
    private Integer eqType;
    //
    @TableField("eq_type2")
    private Integer eqType2;
    @TableField("wl_atk")
    private Integer wlAtk;
    @TableField("hy_atk")
    private Integer hyAtk;
    @TableField("ds_atk")
    private Integer dsAtk;
    @TableField("fd_atk")
    private Integer fdAtk;
    @TableField("wl_def")
    private Integer wlDef;
    @TableField("hy_def")
    private Integer hyDef;
    @TableField("ds_def")
    private Integer dsDef;
    @TableField("fd_def")
    private Integer fdDef;
    @TableField("zl_def")
    private Integer zlDef;
    @TableField(exist = false)
    private String img;
}