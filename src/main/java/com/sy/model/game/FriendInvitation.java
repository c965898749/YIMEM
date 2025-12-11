package com.sy.model.game;

import lombok.Data;

import java.util.Date;
@Data
public class FriendInvitation {
    private Long id;

    private Long inviterId;

    private Long inviteeId;

    private Byte type;

    private String content;

    private Byte status;

    private Date createTime;

    private Date updateTime;


}