package com.sy.model.game;

import lombok.Data;

import java.math.BigDecimal;

@Data
public class Card {
    private Integer uuid;

    private String name;

    private Double weight;

    private String id;

    private BigDecimal star;

    private String camp;

    private BigDecimal HpGrowth;

    private BigDecimal AttackGrowth;

    private BigDecimal DefenceGrowth;

    private BigDecimal PierceGrowth;

    private BigDecimal SpeedGrowth;

    private String passiveIntroduceOne;

    private String passiveIntroduceTwo;

    private String passiveIntroduceThree;

    private String passiveIntroduceThreee;

    private String passiveIntroduceFour;

    private String profession;

    private Integer collAttack;
    private Integer collSpeed;
    private Integer collHp;

    private String passiveIntroduceOneStr;

    private String passiveIntroduceTwoStr;

    private String passiveIntroduceThreeStr;

    private String passiveIntroduceFourStr;

    private Integer wlAtk;
    private Integer hyAtk;
    private Integer dsAtk;
    private Integer fdAtk;
    private Integer wlDef;
    private Integer hyDef;
    private Integer dsDef;
    private Integer fdDef;
    private Integer zlDef;
}