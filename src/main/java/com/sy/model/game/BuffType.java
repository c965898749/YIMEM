package com.sy.model.game;

public enum BuffType {
    STUNNED("眩晕", true), POISONED("中毒", true), SILENCED("沉默", true),
    DISEASED("疾病", true), WEAKENED("衰弱", true), BLOODLUST("嗜血", false),
    DEFENSE_UP("防御提升", false), ATTACK_UP("攻击提升", false), SPEED_UP("速度提升", false),
    HP_UP("生命上限提升", false), FIRE_RESIST_UP("火焰抗性提升", false);

    private final String desc;
    private final boolean isDebuff;
    BuffType(String desc, boolean isDebuff) {
        this.desc = desc;
        this.isDebuff = isDebuff;
    }
    public String getDesc() { return desc; }
    public boolean isDebuff() { return isDebuff; }
}
