package com.sy.model.game;

/**
 * 状态效果实体（增益/减益）
 */


/**
 * 状态效果实体（增益/减益）
 import BattleEnums.BuffType;

 /**
 * 状态效果实体（增益/减益）
 */
public class Buff {
    private BuffType type;
    private int duration;
    private int value;
    private int stackCount;

    public Buff(BuffType type, int duration, int value) {
        this(type, duration, value, 1);
    }

    public Buff(BuffType type, int duration, int value, int stackCount) {
        this.type = type;
        this.duration = duration;
        this.value = value;
        this.stackCount = stackCount;
    }

    public void reduceDuration() {
        if (duration > 0) {
            this.duration--;
        }
    }

    public void stack(int addValue) {
        this.stackCount++;
        this.value += addValue;
    }

    // getter/setter
    public BuffType getType() { return type; }
    public int getDuration() { return duration; }
    public int getValue() { return value; }
    public int getStackCount() { return stackCount; }
    public void setDuration(int duration) { this.duration = duration; }
}