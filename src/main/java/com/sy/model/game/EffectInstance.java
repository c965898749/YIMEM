package com.sy.model.game;


/**
 * 真正的Buff实例（同一个效果可以来多个，互不覆盖）
 */
public class EffectInstance {

    private EffectType type;        // 效果类型：中毒/灼烧/攻击提升等
    private int value;              // 数值：伤害、治疗、加成、抗性
    private int remainRound;        // 剩余回合
    private String casterId;          // 谁放的（A还是B）


    public EffectInstance(EffectType type, int value, int remainRound, String casterId) {
        this.type = type;
        this.value = value;
        this.remainRound = remainRound;
        this.casterId = casterId;
    }

    // 每回合-1
    public void tickRound() {
        if (remainRound > 0) {
            remainRound--;
        }
    }

    // GETTER / SETTER
    public EffectType getType() { return type; }
    public int getValue() { return value; }
    public int getRemainRound() { return remainRound; }
    public String getCasterId() { return casterId; }


}