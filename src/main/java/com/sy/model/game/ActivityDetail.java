package com.sy.model.game;

import lombok.Data;

import java.util.List;

@Data
public class ActivityDetail {
    private Integer id;

    private Byte dailyMaxTimes;

    private Byte day;

    private String activityCode;

    private String difficultyLevel;

    private Integer bossId;

    private String bossName;

    private String detailCode;

    List<ActivityReward> rewardList;

    List<UserActivityRecords> records;
}