package com.sy.model;

import lombok.Data;

@Data
public class Information {
    private Integer id;
    private Integer blogId;
    private Data time;
    private Integer status;
    private String content;
    private Integer replayUserId;
}
