package com.sy.model.game;

import com.sy.tool.BuffType;
import lombok.Data;



@Data
public class Buff {
    // 每回合扣多少血
    private Integer roundReduceBleed;
    private String name;
    private Boolean isDeBuff;
    //每回合开始造成指定伤害
    private String introduce;
    private Integer roundNum;//可持续多少回合
}
