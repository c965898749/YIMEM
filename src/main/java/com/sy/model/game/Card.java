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
}