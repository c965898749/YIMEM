package com.sy.model.game;

import lombok.AllArgsConstructor;
import lombok.Data;
import java.util.List;

@Data
@AllArgsConstructor
public class BattleLog {
    private String battleId;
    private int round;
    private String eventType;               // 事件类型/技能名称
    private String sourceUnit;              // 来源单位名称
    private Camp sourceCamp;                // 来源阵营
    private int sourcePosition;             // 来源单位位置
    private int sourceHpBefore;             // 来源单位作用前血量
    private int sourceHpAfter;              // 来源单位作用后血量
    private int sourceAttackBefore;         // 来源单位作用前攻击
    private int sourceAttackAfter;          // 来源单位作用后攻击
    private int sourceSpeedBefore;          // 来源单位作用前速度
    private int sourceSpeedAfter;           // 来源单位作用后速度
    private String targetUnit;              // 目标单位名称（单个）
    private List<String> targetUnitList;    // 目标单位列表（群体技能）
    private Camp targetCamp;                // 目标阵营
    private int targetPosition;             // 目标单位位置
    private int targetHpBefore;             // 目标单位作用前血量
    private int targetHpAfter;              // 目标单位作用后血量
    private int targetAttackBefore;         // 目标单位作用前攻击
    private int targetAttackAfter;          // 目标单位作用后攻击
    private int targetSpeedBefore;          // 目标单位作用前速度
    private int targetSpeedAfter;           // 目标单位作用后速度
    private String fieldUnitsStatus;        // 在场单位状态信息
    private int value;                      // 数值（伤害、治疗、加成等）
    private EffectType effectType;          // 效果类型
    private DamageType damageType;          // 伤害类型（如果适用）
    private String extraDesc;               // 额外描述信息
}