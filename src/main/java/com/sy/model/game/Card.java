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
}