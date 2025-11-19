package com.sy.model.game;

import lombok.Data;

import java.util.Date;
@Data
public class UserActivityRecords {
    private Long id;

    private String userId;

    private String detailCode;

    private Date participationDate;

    private Integer starLevel;

    private String difficultyLevel;

    private Date participationTime;

    private Integer status;


}