package com.sy.model;

import lombok.Data;

import java.util.Date;

@Data
public class Information {
    private Integer id;
    private Integer blogId;
    private Date time;
    private Integer status;
    private String content;
    private Integer replayUserId;
    private Integer userId;
}
