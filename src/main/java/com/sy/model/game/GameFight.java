package com.sy.model.game;

import lombok.Data;

import java.util.Date;

@Data
public class GameFight {
    private String id;

    private Integer userId;

    private Integer toUserId;

    private String fightter;

    private String userName;

    private String toUserName;

    private String  type;//0pve、1竞技场

    private Date createtime;

    private Integer isWin;//0赢1输
}