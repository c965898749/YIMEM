package com.sy.model.game;

public enum EffectType {
    DAMAGE("伤害"), HEAL("加血"), ATTACK_UP("加攻"), HP_UP("加生命上限"), SPEED_UP("加速"),
    BUFF("增益"), DEBUFF("减益"), SYNERGY("协同加成");
    private final String desc;
    EffectType(String desc) { this.desc = desc; }
    public String getDesc() { return desc; }
}
