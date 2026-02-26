package com.sy.model.game;

public enum EffectType {
   
    SILENCE("沉默"),
    SILENCE_IMMUNE("沉默免疫"),
    STUN("眩晕"),
    STUN_IMMUNE("眩晕免疫"),
    BLOODTHIRST("嗜血"),
    CRAZY("疯狂"),
    CHARGE_UP("蓄力"),
    DRAIN("吸血"),
    TRUE_DAMAGE("真实伤害"),

    //治疗类
    HP_RECOVER("生命恢复"),
    XU_HEAL("续命治疗"),
    HEAL("治疗"),
    HEAL_BOOST("受到治疗提升"),
    HEAL_BOOST_PRET("受到治疗提升"),
    HEAL_DOWN("受到治疗下降"),
    HEAL_DOWNT_PRET("受到治疗下降"),
    XU_HEAL_BOOST("续命治疗提升"),
    XU_HEAL_BOOST_PRET("续命治疗提升"),
    XU_HEAL_DOWN("续命治疗提升"),
    XU_HEAL_DOWN_PRET("续命治疗提升"),
    
    //物理攻击
    DAMAGE("伤害"),
    ATTACK_UP("攻击提升"),
    ATTACK_DOWN("攻击下降"),
    ATTACK_UP_PRET("攻击提升"),
    ATTACK_DOWN_PRET("攻击下降"),
    ATTACK_RESIST_BOOST("物理抗性提升"),
    ATTACK_RESIST_BOOST_PRET("物理抗性提升"),
    ATTACK_RESIST_DOWN("物理抗性下降"),
    ATTACK_RESIST_DOWN_PRET("物理抗性下降"),
    
    
    
    //火焰伤害类
    BURN("灼烧"),
    FIRE_DAMAGE("火焰伤害"),
    FIRE_BOOST("火焰伤害提升"),
    FIRE_BOOST_PRET("火焰伤害提升"),
    FIRE_DOWN("火焰伤害下降"),
    FIRE_DOWN_PRET("火焰伤害下降"),
    FIRE_RESIST_BOOST("火焰抗性提升"),
    FIRE_RESIST_BOOST_PRET("火焰抗性提升"),
    FIRE_RESIST_DOWN("火焰抗性下降"),
    FIRE_RESIST_DOWN_PRET("火焰抗性下降"),


    //中毒类的
    POISON("中毒"),
    POISON_BOOST("中毒伤害提升"),
    POISON_BOOST_PRET("中毒伤害提升"),
    POISON_DOWN("中毒伤害下降"),
    POISON_DOWN_PRET("中毒伤害下降"),
    POISON_RESIST_BOOST("中毒抗性提升"),
    POISON_RESIST_BOOST_PRET("中毒抗性提升"),
    POISON_RESIST_DOWN("中毒抗性下降"),
    POISON_RESIST_DOWN_PRET("中毒抗性下降"),
    
    
    //飞弹类的
    MISSILE_DAMAGE("飞弹伤害"),
    MISSILE_BOOST("飞弹伤害提升"),
    MISSILE_BOOST_PRET("飞弹伤害提升"),
    MISSILE_DOWN("飞弹伤害下降"),
    MISSILE_DOWN_PRET("飞弹伤害下降"),
    MISSILE_RESIST_BOOST("飞弹抗性提升"),
    MISSILE_RESIST_BOOST_PRET("飞弹抗性提升"),
    MISSILE_RESIST_DOWN("飞弹抗性下降"),
    MISSILE_RESIST_DOWN_PRET("飞弹抗性下降"),

    //生命上限类
    HP_UP("生命上限提升"),
    HP_UP_PRET("生命上限提升"),
    MAX_HP_DOWN("生命上限下降"),
    MAX_HP_DOWN_PRET("生命上限下降"),
    MAX_HP_NO_DOWN("生命上限不下降"),
    
    //速度上限类
    SPEED_UP("速度提升"),
    SPEED_UP_PRET("速度提升"),
    SPEED_DOWN("速度下降"),
    SPEED_DOWN_PRET("速度下降");
    private String desc;
    EffectType(String desc) { this.desc = desc; }
}