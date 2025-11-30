package com.sy.model.game;

public enum DamageType {
    PHYSICAL("物理"), MAGIC("魔法"), TRUE("真实"), FIRE("火焰"), POISON("毒素"), MISSILE("飞弹");
    private String name;
    DamageType(String name) { this.name = name; }
}