package com.sy.model.game;

import lombok.Data;

import java.math.BigDecimal;
import java.util.Date;
import java.util.List;

@Data
public class StarSynthesisMain {
    private String id;

    private BigDecimal targetStar;

    private BigDecimal successProbability;

    private String failPenalty;

    private String extraItemRequired;

    private BigDecimal extraCost;

    private String unlockCondition;

    private Date createTime;

    private Date updateTime;

    private List<StarSynthesisMaterials> materials;

}