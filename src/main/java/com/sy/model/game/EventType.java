package com.sy.model.game;

public enum EventType {
    UNIT_DEATH("单位死亡"), UNIT_DAMAGED("单位受伤"), TURN_END("回合结束"),
    SKILL_CAST("技能释放"), ATTACK("普通攻击");
    private final String desc;
    EventType(String desc) { this.desc = desc; }
    public String getDesc() { return desc; }
}
