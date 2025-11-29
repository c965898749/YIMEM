package com.sy.model.game;

/**
 * 战斗事件实体（用于触发被动技能）
 */
public class BattleEvent {
    private EventType type;
    private String sourceId; // 事件源ID（如造成伤害的单位ID）
    private String targetId; // 事件目标ID（如受伤/死亡的单位ID）
    private int value; // 事件数值（如伤害量）

    public BattleEvent(EventType type, String sourceId, String targetId, int value) {
        this.type = type;
        this.sourceId = sourceId;
        this.targetId = targetId;
        this.value = value;
    }

    // getter
    public EventType getType() { return type; }
    public String getSourceId() { return sourceId; }
    public String getTargetId() { return targetId; }
    public int getValue() { return value; }
}