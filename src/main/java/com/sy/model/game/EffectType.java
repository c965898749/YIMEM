package com.sy.model.game;

public enum EffectType {
    DAMAGE("伤害"), HEAL("加血"), BUFF("增益"), DEBUFF("减益"),
    HP_UP("生命上限提升"), HP_DOWN("生命上限降低"), ATTACK_UP("攻击提升"), SPEED_UP("速度提升"),
    SYNERGY("协同加成"), PROTECT("保护效果");;
    private final String desc;
    EffectType(String desc) { this.desc = desc; }
    public String getDesc() { return desc; }
}
