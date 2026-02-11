package com.sy.model.game;

import lombok.Data;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

@Data
public class Character {
    private String id;
    private String name;
    private Integer hp;
    private Integer maxHp;
    private Integer attack;
    private Integer speed;
    private Integer goIntoNum;//位置
    private String goON;//是否在场上
    private String passiveIntroduceOne;
    private String passiveIntroduceTwo;
    private String passiveIntroduceThree;
    private String passiveIntroduceThreee;
    private String passiveIntroduceFour;
    private String passiveIntroduceOneStr;
    private String passiveIntroduceTwoStr;
    private String passiveIntroduceThreeStr;
    private String passiveIntroduceFourStr;
    private String direction;//左0右1
    private List<Buff> buff;
    private String isAction="1";//我能不能动//0不能1能
    private String isDead="0";
    private String camp;
    private Integer uuid;//主键
    private Integer lv;
    private Integer userId;
    private BigDecimal star;
    private Integer stackCount;//叠加
    private Date createTime;
    private Date updateTime;
    private Integer maxLv;//最大等级
    //关联数据
    private Integer onStage;
    private BigDecimal HpGrowth;
    private BigDecimal AttackGrowth;
    private BigDecimal DefenceGrowth;
    private BigDecimal PierceGrowth;
    private BigDecimal SpeedGrowth;
    private Integer exp;
    private String isDelete;
    private String profession;
    private Integer collAttack;
    private Integer collSpeed;
    private Integer collHp;

    private Integer wlAtk;
    private Integer hyAtk;
    private Integer dsAtk;
    private Integer fdAtk;
    private Integer wlDef;
    private Integer hyDef;
    private Integer dsDef;
    private Integer fdDef;
    private Integer zlDef;

    public Character(){

    };
    public Character(String id,String name, Camp camp, int position, Profession profession, Race race,
                    int maxHp, int attack, int speed) {
        this.id = id;
        this.name = name;
//        this.camp = camp;
//        this.position = position;
        this.goIntoNum = position;
//        this.race = race;
//        this.level = 1;
        this.maxHp = maxHp;
        this.attack = attack;
        this.speed = speed;
//        this.isDead = false;
//        this.isOnField = false;
    }
}
