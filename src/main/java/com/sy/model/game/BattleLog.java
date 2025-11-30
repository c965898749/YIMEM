package com.sy.model.game;

/**
 * 战斗日志类
 * 记录战斗过程中的每一个事件，包括攻击、治疗、Buff/Debuff等
 */
public class BattleLog {
    private String battleId;      // 战斗ID
    private int round;            // 回合数
    private String eventType;     // 事件类型（攻击、治疗、技能等）
    private String source;        // 事件来源（施法者/攻击者）
    private String target;        // 事件目标（单个或多个目标，用、分隔）
    private int value;            // 事件数值（伤害、治疗量等）
    private EffectType effectType;  // 效果类型（伤害、加血、增益等）
    private DamageType damageType;  // 伤害类型（物理、魔法、火焰等）
    private String detail;        // 详细描述（包含剩余回合数、叠加层数等信息）

    /**
     * 基础构造函数
     */
    public BattleLog(String battleId, int round, String eventType, String source,
                     String target, int value, EffectType effectType,
                     DamageType damageType) {
        this(battleId, round, eventType, source, target, value, effectType, damageType, "");
    }

    /**
     * 带详细描述的构造函数
     */
    public BattleLog(String battleId, int round, String eventType, String source,
                     String target, int value, EffectType effectType,
                     DamageType damageType, String detail) {
        this.battleId = battleId;
        this.round = round;
        this.eventType = eventType;
        this.source = source;
        this.target = target;
        this.value = value;
        this.effectType = effectType;
        this.damageType = damageType;
        this.detail = detail;
    }

    /**
     * 多目标构造函数
     */
    public BattleLog(String battleId, int round, String eventType, String source,
                     java.util.List<String> targets, int value,
                     EffectType effectType, DamageType damageType) {
        this(battleId, round, eventType, source,
                targets != null ? String.join("、", targets) : "",
                value, effectType, damageType, "");
    }

    /**
     * 多目标+详细描述构造函数
     */
    public BattleLog(String battleId, int round, String eventType, String source,
                     java.util.List<String> targets, int value,
                     EffectType effectType, DamageType damageType, String detail) {
        this.battleId = battleId;
        this.round = round;
        this.eventType = eventType;
        this.source = source;
        this.target = targets != null ? String.join("、", targets) : "";
        this.value = value;
        this.effectType = effectType;
        this.damageType = damageType;
        this.detail = detail;
    }

    /**
     * 重写toString方法，格式化显示日志信息
     */
    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append(String.format("[第%02d回合] ", round));

        if (source != null && !source.isEmpty()) {
            sb.append(source).append("：");
        }

        sb.append(eventType);

        if (target != null && !target.isEmpty()) {
            sb.append(" > ").append(target);
        }

        if (value != 0) {
            sb.append(String.format("（%d）", value));
        }

        if (damageType != null) {
            sb.append("[").append(damageType.getDesc()).append("]");
        }

        if (detail != null && !detail.isEmpty()) {
            sb.append(" - ").append(detail);
        }

        return sb.toString();
    }

    // Getter方法
    public String getBattleId() { return battleId; }
    public int getRound() { return round; }
    public String getEventType() { return eventType; }
    public String getSource() { return source; }
    public String getTarget() { return target; }
    public int getValue() { return value; }
    public EffectType getEffectType() { return effectType; }
    public DamageType getDamageType() { return damageType; }
    public String getDetail() { return detail; }

    // Setter方法（用于日志内容更新）
    public void setDetail(String detail) { this.detail = detail; }
    public void setValue(int value) { this.value = value; }
    public void setTarget(String target) { this.target = target; }
    public void setTarget(java.util.List<String> targets) {
        this.target = targets != null ? String.join("、", targets) : "";
    }
}