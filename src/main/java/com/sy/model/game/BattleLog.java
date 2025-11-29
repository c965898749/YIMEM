package com.sy.model.game;

import java.util.List;

/**
 * 战斗记录实体（支持多目标）
 */
public class BattleLog {
    private String battleId;
    private int round;
    private String skillName;
    private String source;
    private List<String> targets; // 支持多目标
    private String singleTarget; // 兼容单目标
    private int value;
    private DamageType damageType;
    private EffectType effectType;
    private boolean isMultiTarget; // 是否多目标技能

    // 单目标构造函数
    public BattleLog(String battleId, int round, String skillName, String source, String target,
                     int value, EffectType effectType, DamageType damageType) {
        this.battleId = battleId;
        this.round = round;
        this.skillName = skillName;
        this.source = source != null ? source : "系统";
        this.singleTarget = target != null ? target : "无目标";
        this.targets = null;
        this.value = value;
        this.effectType = effectType;
        this.damageType = damageType;
        this.isMultiTarget = false;
    }

    // 多目标构造函数
    public BattleLog(String battleId, int round, String skillName, String source, List<String> targets,
                     int value, EffectType effectType, DamageType damageType) {
        this.battleId = battleId;
        this.round = round;
        this.skillName = skillName;
        this.source = source != null ? source : "系统";
        this.targets = targets;
        this.singleTarget = null;
        this.value = value;
        this.effectType = effectType;
        this.damageType = damageType;
        this.isMultiTarget = true;
    }

    // 单目标简化构造
    public BattleLog(String battleId, int round, String skillName, String source, String target,
                     int value, EffectType effectType) {
        this(battleId, round, skillName, source, target, value, effectType, null);
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append(String.format("[回合%d] %s: %s ", round, skillName, source));

        if (isMultiTarget && targets != null && !targets.isEmpty()) {
            sb.append("对");
            if (targets.size() > 3) {
                sb.append(String.format("【%s】等%d个目标", targets.get(0), targets.size()));
            } else {
                sb.append(String.format("【%s】", String.join("、", targets)));
            }
        } else {
            sb.append(String.format("对【%s】", singleTarget != null ? singleTarget : "无目标"));
        }

        sb.append(String.format("触发%s", effectType.getDesc()));

        if (value > 0) {
            sb.append(String.format("（数值：%d）", value));
        }

        if (damageType != null) {
            sb.append(String.format("【%s】", damageType.getDesc()));
        }

        return sb.toString();
    }

    // getter
    public String getBattleId() { return battleId; }
    public int getRound() { return round; }
    public String getSkillName() { return skillName; }
    public String getSource() { return source; }
    public List<String> getTargets() { return targets; }
    public String getSingleTarget() { return singleTarget; }
    public int getValue() { return value; }
    public EffectType getEffectType() { return effectType; }
    public DamageType getDamageType() { return damageType; }
    public boolean isMultiTarget() { return isMultiTarget; }
}