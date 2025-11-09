package com.sy.model.game;

import lombok.Data;

import java.util.Date;
@Data
public class GameGiftExchangeCode {
    private Long exchangeId;

    private Long giftId;

    private String exchangeCode;

    private Integer isUsed;

    private Long useUserId;

    private Date useTime;

    private Date createTime;


}