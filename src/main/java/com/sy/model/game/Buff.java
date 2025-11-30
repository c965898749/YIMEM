package com.sy.model.game;

/**
 * 状态效果实体（增益/减益）
 */


/**
 * 状态效果实体（增益/减益）
 import BattleEnums.BuffType;

 /**
 * Buff效果类
 */
public class Buff {
    public enum BuffType {
        POISONED("中毒"),
        DISEASED("疾病"),
        STUNNED("眩晕"),
        BLOODLUST("嗜血"),
        SPEED_UP("加速"),
        ATTACK_UP("攻击提升"),
        HP_UP("生命提升"),
        DEFENSE_UP("防御提升"),
        CRIT_UP("暴击提升"),
        SHIELD("护盾"),
        REGENERATION("回血"),
        SILENCED("沉默"),
        BLEEDING("流血"),
        BURN("灼烧");

        private String desc;

        BuffType(String desc) {
            this.desc = desc;
        }

        public String getDesc() {
            return desc;
        }

        public boolean isDebuff() {
            return this == POISONED || this == DISEASED || this == STUNNED ||
                    this == SILENCED || this == BLEEDING || this == BURN;
        }

        public boolean isBuff() {
            return !isDebuff();
        }
    }

    private BuffType type;
    private int duration; // 持续回合数，-1表示永久
    private int value;    // 效果数值（伤害、加成值等）
    private int remainingTurns; // 剩余回合数
    private boolean isStackable; // 是否可叠加
    private int stackCount;      // 叠加层数

    public Buff(BuffType type, int duration, int value) {
        this(type, duration, value, true);
    }

    public Buff(BuffType type, int duration, int value, boolean isStackable) {
        this.type = type;
        this.duration = duration;
        this.value = value;
        this.remainingTurns = duration;
        this.isStackable = isStackable;
        this.stackCount = 1;
    }

    /**
     * 更新剩余回合数，返回是否已过期
     */
    public boolean updateTurn() {
        if (remainingTurns > 0) {
            remainingTurns--;
            return remainingTurns == 0;
        }
        return false; // 永久buff或已过期
    }

    /**
     * 减少持续时间（用于驱散效果）
     */
    public void reduceDuration(int amount) {
        if (remainingTurns > 0) {
            remainingTurns = Math.max(0, remainingTurns - amount);
        }
    }

    /**
     * 叠加buff效果
     */
    public void stack(int value) {
        if (isStackable) {
            this.value += value;
            this.stackCount++;

            // 刷新持续时间（可选策略）
            if (remainingTurns < duration) {
                remainingTurns = duration;
            }
        } else {
            // 不可叠加时刷新持续时间
            this.remainingTurns = duration;
            this.value = Math.max(this.value, value); // 取最大值
        }
    }

    /**
     * 重置持续时间
     */
    public void refreshDuration() {
        this.remainingTurns = duration;
    }

    /**
     * 检查是否为永久buff
     */
    public boolean isPermanent() {
        return duration == -1;
    }

    /**
     * 检查是否已过期
     */
    public boolean isExpired() {
        return !isPermanent() && remainingTurns <= 0;
    }

    // Getter和Setter方法
    public BuffType getType() {
        return type;
    }

    public int getDuration() {
        return duration;
    }

    public void setDuration(int duration) {
        this.duration = duration;
    }

    public int getValue() {
        return value;
    }

    public void setValue(int value) {
        this.value = value;
    }

    public int getRemainingTurns() {
        return remainingTurns;
    }

    public void setRemainingTurns(int remainingTurns) {
        this.remainingTurns = remainingTurns;
    }

    public boolean isStackable() {
        return isStackable;
    }

    public void setStackable(boolean stackable) {
        isStackable = stackable;
    }

    public int getStackCount() {
        return stackCount;
    }

    public void setStackCount(int stackCount) {
        this.stackCount = stackCount;
    }

    /**
     * 获取buff的描述信息
     */
    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append(type.getDesc());

        if (value != 0) {
            sb.append("+").append(value);
        }

        if (stackCount > 1) {
            sb.append("(").append(stackCount).append("层)");
        }

        if (isPermanent()) {
            sb.append("[永久]");
        } else {
            sb.append("[").append(remainingTurns).append("回合]");
        }

        return sb.toString();
    }
}