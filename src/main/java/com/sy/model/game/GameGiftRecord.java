package com.sy.model.game;

import lombok.Data;

import java.util.Date;
@Data
public class GameGiftRecord {
    private Long recordId;

    private Long userId;

    private Long giftId;

    private String giftCode;

    private Date getTime;

    private Integer status;

    private String failReason;

    private String platform;

    private String ipAddress;


}