package com.sy.model.game;

import lombok.Data;

import java.util.Date;
@Data
public class FriendBlessing {
    private Long id;

    private Long senderId;

    private Long receiverId;

    private String content;

    private Date sendTime;

    private Byte isRead;

}