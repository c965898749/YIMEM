package com.sy.model.game;

import lombok.Data;

import java.math.BigDecimal;
import java.util.Date;
@Data
public class StarSynthesisMaterials {
    private String id;

    private String synthesisId;

    private BigDecimal materialStar;

    private Integer materialQuantity;

    private String materialType;

    private Date createTime;

}