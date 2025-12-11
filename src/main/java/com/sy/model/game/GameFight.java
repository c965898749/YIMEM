package com.sy.model.game;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;
import org.springframework.format.annotation.DateTimeFormat;

import java.util.Date;

@Data
public class GameFight {
    private String id;

    private Integer userId;

    private Integer toUserId;

    private String fightter;

    private String userName;

    private String toUserName;

    private String  type;//0pve、1竞技场2、好有pk

    private Date createtime;

    private Integer isWin;//0赢1输

    private String img;

    private String timeStr;
}