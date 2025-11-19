package com.sy.model.game;

import lombok.Data;

import java.util.Date;
@Data
public class ActivityReward {
    private Long id;

    private String detailCode;

    private Byte starLevel;

    private String difficultyLevel;

    private String rewardType;

    private Integer rewardAmount;

    private String rewardDesc;

    private Byte status;

    private Date createTime;

    private Date updateTime;

    private Integer itemId;
}