package com.sy.model.game;

import lombok.Data;

import java.util.Date;
import java.util.List;

@Data
public class ActivityConfig {
    private Long id;

    private String activityCode;

    private String activityName;

    private Date startDate;

    private Date endDate;

    private Byte dailyMaxTimes;

    private Byte starCount;

    private Byte status;

    private String description;

    private Date createTime;

    private Date updateTime;

    private Integer isPermanent;

    List<ActivityDetail> details;
}