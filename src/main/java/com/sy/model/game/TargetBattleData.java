package com.sy.model.game;

public class TargetBattleData {
    private int hpBefore;          // 目标作用前血量
    private int hpAfter;           // 目标作用后血量
    private Integer value;         // 该目标对应的数值（伤害/治疗/加成等，null表示无）
    private boolean fieldStatus; // 目标单位作用时的状态（场上/场下）
    private boolean isStunned;
    private boolean isSilence;
    private boolean isPoison;
    private boolean isFireBoost;
    private boolean isHealDown;

    public boolean isHealDown() {
        return isHealDown;
    }

    public void setHealDown(boolean healDown) {
        isHealDown = healDown;
    }

    public boolean isStunned() {
        return isStunned;
    }

    public void setStunned(boolean stunned) {
        isStunned = stunned;
    }

    public boolean isSilence() {
        return isSilence;
    }

    public void setSilence(boolean silence) {
        isSilence = silence;
    }

    public boolean isPoison() {
        return isPoison;
    }

    public void setPoison(boolean poison) {
        isPoison = poison;
    }

    public boolean isFireBoost() {
        return isFireBoost;
    }

    public void setFireBoost(boolean fireBoost) {
        isFireBoost = fireBoost;
    }


    // 构造方法（包含新增的fieldStatus参数）
    public TargetBattleData(int hpBefore, int hpAfter, Integer value, boolean fieldStatus) {
        this.hpBefore = hpBefore;
        this.hpAfter = hpAfter;
        this.value = value;
        this.fieldStatus = fieldStatus;
    }

    // Getter（日志场景仅提供Getter，保证数据不可变）
    public int getHpBefore() {
        return hpBefore;
    }

    public int getHpAfter() {
        return hpAfter;
    }

    public Integer getValue() {
        return value;
    }

    public boolean getFieldStatus() {
        return fieldStatus;
    }
}
