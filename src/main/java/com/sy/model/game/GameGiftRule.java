package com.sy.model.game;

import lombok.Data;

import java.util.Date;
@Data
public class GameGiftRule {
    private Long ruleId;

    private Long giftId;

    private Integer minLevel;

    private Integer maxLevel;

    private Integer isNewUser;

    private Integer maxGetCount;

    private String platformLimit;

    private Date createTime;


}