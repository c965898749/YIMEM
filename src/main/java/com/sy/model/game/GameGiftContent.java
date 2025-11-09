package com.sy.model.game;

import lombok.Data;

import java.util.Date;
@Data
public class GameGiftContent {
    private Long contentId;

    private Long giftId;

    private Integer itemType;

    private Long itemId;

    private Integer itemQuantity;

    private Date createTime;

}