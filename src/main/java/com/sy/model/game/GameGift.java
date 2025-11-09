package com.sy.model.game;

import lombok.Data;

import java.util.Date;
@Data
public class GameGift {
    private Long giftId;

    private String giftCode;

    private String giftName;

    private Integer giftType;

    private Integer totalQuantity;

    private Integer remainingQuantity;

    private Date startTime;

    private Date endTime;

    private Integer isActive;

    private Date createTime;

    private Date updateTime;

    private String description;


}