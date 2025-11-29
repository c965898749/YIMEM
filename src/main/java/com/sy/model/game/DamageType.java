package com.sy.model.game;

public enum DamageType {
    PHYSICAL("物理"), FIRE("火焰"), MISSILE("飞弹"), POISON("毒素"), TRUE_DAMAGE("真实");
    private final String desc;
    DamageType(String desc) { this.desc = desc; }
    public String getDesc() { return desc; }
}
