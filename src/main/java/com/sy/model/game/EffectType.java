package com.sy.model.game;

public enum EffectType {
    POISON("中毒"),
    BURN("灼烧"),
    SILENCE("沉默"),
    STUN("眩晕"),
    WEAKEN("衰弱"),
    BLOODTHIRST("嗜血"),
    HEAL_BOOST("治疗加成"),
    FIRE_BOOST("火焰伤害加成"),
    POISON_BOOST("中毒伤害加成"),
    MISSILE_BOOST("飞弹伤害加成"),
    HEAL_DOWN("治疗降低"),
    FIRE_RESIST("火焰抗性"),
    POISON_RESIST("中毒抗性"),
    MISSILE_RESIST("飞弹抗性"),
    ATTACK_UP("攻击提升"),
    ATTACK_DOWN("攻击降低"),
    HP_UP("生命上限提升"),
    MAX_HP_DOWN("生命上限降低"),
    SPEED_UP("速度提升"),
    SPEED_DOWN("速度降低"),
    ATTACK_SPEED_UP("攻击和速度提升"),
    HEAL("治疗"),
    XU_HEAL("续命治疗"),
    DAMAGE("伤害"),
    DRAIN("吸血"),
    TRUE_DAMAGE("真实伤害"),
    FIRE_DAMAGE("火焰伤害"),
    MISSILE_DAMAGE("飞弹伤害"),
    HP_RECOVER("生命恢复"),
    SILENCE_IMMUNE("沉默免疫");
    private String desc;
    EffectType(String desc) { this.desc = desc; }
}