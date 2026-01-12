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
    @TableField("hp_growth")
    private Integer hpGrowth;
    @TableField("attack_growth")
    private Integer attackGrowth;
    @TableField("defence_growth")
    private Integer defenceGrowth;
    @TableField("pierce_growth")
    private Integer pierceGrowth;
    @TableField("speed_growth")
    private Integer speedGrowth;
    @TableField("passive_introduce_one")
    private String passiveIntroduceOne;
    @TableField("passive_introduce_two")
    private String passiveIntroduceTwo;
    @TableField("passive_introduce_three")
    private String passiveIntroduceThree;
    @TableField("profession")
    private String profession;
    @TableField("coll_attack")
    private Integer collAttack;
    @TableField("coll_speed")
    private Integer collSpeed;
    @TableField("coll_hp")
    private Integer collHp;
    @TableField("img")
    private String img;
    @TableField("fire_res")
    private Integer fireRes;
    @TableField("poison_res")
    private Integer poisonRes;
    @TableField("projectile_res")
    private Integer projectileRes;
    @TableField("fire_growth")
    private Integer fireGrowth;
    @TableField("poison_growth")
    private Integer poisonGrowth;
    @TableField("projectile_growth")
    private Integer projectileGrowth;
}