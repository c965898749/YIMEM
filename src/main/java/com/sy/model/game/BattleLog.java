package com.sy.model.game;

import lombok.AllArgsConstructor;
import lombok.Data;
import java.util.Map;

@Data
@AllArgsConstructor
public class BattleLog {
    private String battleId;                // 战斗ID（核心标识）
    private int round;                      // 回合数（核心时序信息）
    private String eventType;               // 事件类型/技能名称（核心事件标识）
    private Integer flyup;               // 事件类型/技能名称（核心事件标识）

    // 来源单位核心信息
    private String sourceUnitId;            // 来源单位唯一ID
    private int sourceHpBefore;             // 来源单位作用前血量
    private int sourceHpAfter;              // 来源单位作用后血量
    private int sourceSelfValue;        // 来源单位自身数值（自疗/自损等，null表示无）
    private boolean sourceFieldStatus; // 单目标的场上/场下状态

    // 单目标场景专属字段（与多目标字段二选一使用）
    private String targetUnitId;            // 单个目标单位唯一ID
    private int targetHpBefore;             // 单个目标作用前血量
    private int targetHpAfter;              // 单个目标作用后血量
    private int singleTargetValue;      // 单个目标对应的数值（伤害/治疗等）
    private boolean targetFieldStatus; // 单目标的场上/场下状态

    // 多目标场景专属字段（合并后的唯一Map）
    private Map<String, TargetBattleData> multiTargetDataMap;

    private EffectType effectType;          // 效果类型（核心分类）
    private DamageType damageType;          // 伤害类型（如果适用）
    private String extraDesc;               // 额外描述信息（可选）
    private int aoe;
}