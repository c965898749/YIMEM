package com.sy.model.game;

import lombok.Data;

import java.math.BigDecimal;
import java.util.Date;

@Data
public class Characters {
    private Integer uuid;//主键
    private Integer userId;
    private String id;//卡牌编码
    private Integer lv;
    private BigDecimal star;
    private Integer stackCount;//叠加
    private Date createTime;
    private Date updateTime;
    private Integer goIntoNum;//位置，是否上阵0
    //关联数据
    private String name;
    private Integer onStage;
    private String camp;
}
