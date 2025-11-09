package com.sy.model.game;

import lombok.Data;

import java.util.Date;
@Data
public class StarSynthesisMaterials {
    private Integer id;

    private Integer synthesisId;

    private Integer materialStar;

    private Integer materialQuantity;

    private String materialType;

    private Date createTime;


}