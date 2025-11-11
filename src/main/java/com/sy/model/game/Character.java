package com.sy.model.game;

import lombok.Data;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

@Data
public class Character {
    private String id;
    private String name;
    private Integer hp;
    private Integer maxHp;
    private Integer attack;
    private Integer speed;
    private Integer goIntoNum;//位置
    private String goON;//是否在场上
    private String passiveIntroduceOne;
    private String passiveIntroduceTwo;
    private String passiveIntroduceThree;
    private String direction;//左0右1
    private List<Buff> buff;
    private String isAction="1";//我能不能动//0不能1能
    private String isDead="0";
    private String camp;
    private Integer uuid;//主键
    private Integer lv;
}
