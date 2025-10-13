package com.sy.model.game;

import com.sy.tool.BuffType;
import lombok.Data;



@Data
public class Buff {
    private BuffType type;
    private int value;
    private int duration;
    private String scope; // 影响范围(场上/场下/全体)
}
