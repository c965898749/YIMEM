package com.sy.model.game;

import lombok.Data;

import java.util.ArrayList;
import java.util.List;

@Data
public class Character {
    private String id;
    private String name;
    private int hp;
    private int attack;
    private int defense;
    private boolean isAlive = true;
    private List<Buff> buffs = new ArrayList<>();
    private Integer position; // 场上/场下
}
