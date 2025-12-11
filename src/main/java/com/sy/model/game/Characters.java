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
    private Integer maxLv;//最大等级
    //关联数据
    private String name;
    private Integer onStage;
    private String camp;
    private BigDecimal HpGrowth;
    private BigDecimal AttackGrowth;
    private BigDecimal DefenceGrowth;
    private BigDecimal PierceGrowth;
    private BigDecimal SpeedGrowth;
    private String passiveIntroduceOne;
    private String passiveIntroduceTwo;
    private String passiveIntroduceThree;
    private Integer exp;
    private String isDelete;
    private String profession;
}
