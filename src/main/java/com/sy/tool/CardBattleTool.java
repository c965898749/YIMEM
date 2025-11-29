package com.sy.tool;

import com.sy.model.game.*;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;

/**
 * 卡牌5v5战斗核心工具类（优化日志输出）
 */
public class CardBattleTool {
    private static final Map<String, List<BattleLog>> BATTLE_LOG_CACHE = new ConcurrentHashMap<>();
    private static final int MAX_ROUNDS = 100;
    private Random random;

    public CardBattleTool() {
        this.random = new Random();
    }

    public String startBattle(List<Guardian> teamA, List<Guardian> teamB) {
        if (teamA == null || teamB == null || teamA.isEmpty() || teamB.isEmpty()) {
            throw new IllegalArgumentException("战斗队伍不能为空");
        }

        String battleId = UUID.randomUUID().toString().replace("-", "").substring(0, 8);
        List<BattleLog> battleLogs = new ArrayList<>();
        BATTLE_LOG_CACHE.put(battleId, battleLogs);

        initTeam(teamA, battleLogs, battleId, "A");
        initTeam(teamB, battleLogs, battleId, "B");

        int round = 0;
        BattleStatus status = BattleStatus.ONGOING;

        Guardian fieldA = getFirstAliveGuardian(teamA);
        Guardian fieldB = getFirstAliveGuardian(teamB);

        if (fieldA != null) {
            fieldA.setOnField(true);
            battleLogs.add(new BattleLog(battleId, 0, "登场", fieldA.getName(), "场上",
                    0, EffectType.BUFF));
            triggerOnEnterSkill(fieldA, teamA, teamB, battleLogs, battleId, 0);
        }
        if (fieldB != null) {
            fieldB.setOnField(true);
            battleLogs.add(new BattleLog(battleId, 0, "登场", fieldB.getName(), "场上",
                    0, EffectType.BUFF));
            triggerOnEnterSkill(fieldB, teamB, teamA, battleLogs, battleId, 0);
        }

        while (round < MAX_ROUNDS && status == BattleStatus.ONGOING) {
            round++;
            battleLogs.add(new BattleLog(battleId, round, "回合开始", "系统", "",
                    0, EffectType.BUFF));

            triggerOffFieldStartSkill(teamA, teamB, battleLogs, battleId, round);
            triggerOffFieldStartSkill(teamB, teamA, battleLogs, battleId, round);

            fieldA = replaceDeadFieldGuardian(teamA, fieldA, teamB, battleLogs, battleId, round);
            fieldB = replaceDeadFieldGuardian(teamB, fieldB, teamA, battleLogs, battleId, round);

            if (isTeamAllDead(teamA)) {
                status = BattleStatus.LOSE;
                battleLogs.add(new BattleLog(battleId, round, "战斗结束", "系统", "A队全灭",
                        0, EffectType.BUFF));
                break;
            }
            if (isTeamAllDead(teamB)) {
                status = BattleStatus.WIN;
                battleLogs.add(new BattleLog(battleId, round, "战斗结束", "系统", "B队全灭",
                        0, EffectType.BUFF));
                break;
            }
            if (fieldA == null || fieldB == null) break;

            handleTurnBuffs(teamA, teamB, battleLogs, battleId, round);
            handleTurnBuffs(teamB, teamA, battleLogs, battleId, round);

            triggerHoutuSkill(fieldA, battleLogs, battleId, round);
            triggerHoutuSkill(fieldB, battleLogs, battleId, round);

            Guardian attacker = fieldA.getCurrentSpeed() >= fieldB.getCurrentSpeed() ? fieldA : fieldB;
            Guardian defender = attacker == fieldA ? fieldB : fieldA;
            List<Guardian> attackerTeam = attacker == fieldA ? teamA : teamB;
            List<Guardian> defenderTeam = defender == fieldA ? teamA : teamB;

            triggerEvent(new BattleEvent(EventType.ATTACK, attacker.getId(), defender.getId(), 0),
                    attackerTeam, defenderTeam, battleLogs, battleId, round);

            attackProcess(attacker, defender, attackerTeam, defenderTeam, battleLogs, battleId, round);

            triggerEvent(new BattleEvent(EventType.TURN_END, null, null, 0),
                    teamA, teamB, battleLogs, battleId, round);
            triggerEvent(new BattleEvent(EventType.TURN_END, null, null, 0),
                    teamB, teamA, battleLogs, battleId, round);

            teamA.forEach(Guardian::cleanExpiredBuffs);
            teamB.forEach(Guardian::cleanExpiredBuffs);
        }

        if (status == BattleStatus.ONGOING) {
            int totalHpA = teamA.stream()
                    .filter(Objects::nonNull)
                    .mapToInt(Guardian::getCurrentHp)
                    .sum();
            int totalHpB = teamB.stream()
                    .filter(Objects::nonNull)
                    .mapToInt(Guardian::getCurrentHp)
                    .sum();

            if (totalHpA > totalHpB) {
                status = BattleStatus.WIN;
                battleLogs.add(new BattleLog(battleId, round, "回合上限结束", "系统", "A队胜利（总血量高）",
                        totalHpA - totalHpB, EffectType.BUFF));
            } else if (totalHpB > totalHpA) {
                status = BattleStatus.LOSE;
                battleLogs.add(new BattleLog(battleId, round, "回合上限结束", "系统", "B队胜利（总血量高）",
                        totalHpB - totalHpA, EffectType.BUFF));
            } else {
                status = BattleStatus.DRAW;
                battleLogs.add(new BattleLog(battleId, round, "回合上限结束", "系统", "平局（总血量相同）",
                        0, EffectType.BUFF));
            }
        }

        return battleId;
    }

    private void addMultiTargetLog(List<BattleLog> battleLogs, String battleId, int round,
                                   String skillName, String source, List<String> targets,
                                   int value, EffectType effectType, DamageType damageType) {
        if (targets == null || targets.isEmpty()) return;

        battleLogs.add(new BattleLog(battleId, round, skillName, source, targets,
                value, effectType, damageType));
    }

    private void triggerEvent(BattleEvent event, List<Guardian> ownTeam, List<Guardian> enemyTeam,
                              List<BattleLog> battleLogs, String battleId, int round) {
        if (event == null || ownTeam == null || battleLogs == null || battleId == null) {
            return;
        }

        List<String> skillSources = new ArrayList<>();
        List<String> affectedTargets = new ArrayList<>();

        ownTeam.stream()
                .filter(Objects::nonNull)
                .filter(g -> !g.isOnField() && g.isAlive())
                .forEach(g -> {
                    g.handleEvent(event, ownTeam, enemyTeam, battleLogs, battleId, round);

                    if (!skillSources.contains(g.getName())) {
                        skillSources.add(g.getName());
                    }

                    if (event.getTargetId() != null && enemyTeam != null) {
                        enemyTeam.stream()
                                .filter(e -> e.getId().equals(event.getTargetId()))
                                .findFirst()
                                .ifPresent(e -> {
                                    if (!affectedTargets.contains(e.getName())) {
                                        affectedTargets.add(e.getName());
                                    }
                                });
                    }
                });

        if (event.getType() == EventType.UNIT_DEATH && !skillSources.isEmpty() && !affectedTargets.isEmpty()) {
            addMultiTargetLog(battleLogs, battleId, round, "死亡触发技能",
                    String.join("、", skillSources), affectedTargets,
                    0, EffectType.BUFF, null);
        }
    }

    private int takeDamageWithEvent(Guardian target, int damage, DamageType type,
                                    List<Guardian> attackerTeam, List<Guardian> defenderTeam,
                                    List<BattleLog> battleLogs, String battleId, int round,
                                    String sourceId) {
        if (target == null || !target.isAlive() || damage <= 0) {
            return 0;
        }

        int oldHp = target.getCurrentHp();
        int finalDamage = target.takeDamage(damage, type);

        if (oldHp > target.getCurrentHp() && finalDamage > 0) {
            triggerEvent(new BattleEvent(EventType.UNIT_DAMAGED, sourceId, target.getId(), finalDamage),
                    attackerTeam, defenderTeam, battleLogs, battleId, round);

            if (defenderTeam != null) {
                triggerEvent(new BattleEvent(EventType.UNIT_DAMAGED, sourceId, target.getId(), finalDamage),
                        defenderTeam, attackerTeam, battleLogs, battleId, round);
            }
        }

        if (!target.isAlive() && oldHp > 0) {
            triggerEvent(new BattleEvent(EventType.UNIT_DEATH, sourceId, target.getId(), 0),
                    attackerTeam, defenderTeam, battleLogs, battleId, round);

            if (defenderTeam != null) {
                triggerEvent(new BattleEvent(EventType.UNIT_DEATH, sourceId, target.getId(), 0),
                        defenderTeam, attackerTeam, battleLogs, battleId, round);
            }
        }

        return finalDamage;
    }

    private void initTeam(List<Guardian> team, List<BattleLog> battleLogs, String battleId, String teamName) {
        if (team == null || battleLogs == null || battleId == null) {
            return;
        }

        team.sort(Comparator.comparingInt(Guardian::getPosition));

        List<String> synergizedUnits = new ArrayList<>();
        Map<String, List<String>> synergyMap = new HashMap<>();

        team.forEach(g -> {
            if (g != null) {
                g.checkSynergy(team, battleLogs, battleId, 0);

                if (!g.getSynergyTargets().isEmpty()) {
                    synergizedUnits.add(g.getName());
                    synergyMap.put(g.getName(), g.getSynergyTargets());
                }
            }
        });

        if (!synergizedUnits.isEmpty()) {
            addMultiTargetLog(battleLogs, battleId, 0, "协同效果激活",
                    String.join("、", synergizedUnits),
                    synergyMap.values().stream()
                            .flatMap(List::stream)
                            .distinct()
                            .collect(Collectors.toList()),
                    0, EffectType.SYNERGY, null);
        }
    }

    private Guardian getFirstAliveGuardian(List<Guardian> team) {
        if (team == null) {
            return null;
        }

        return team.stream()
                .filter(Objects::nonNull)
                .filter(Guardian::isAlive)
                .findFirst()
                .orElse(null);
    }

    private Guardian replaceDeadFieldGuardian(List<Guardian> team, Guardian currentField, List<Guardian> enemyTeam,
                                              List<BattleLog> battleLogs, String battleId, int round) {
        if (team == null || battleLogs == null || battleId == null) {
            return null;
        }

        if (currentField != null && !currentField.isAlive()) {
            triggerEvent(new BattleEvent(EventType.UNIT_DEATH, null, currentField.getId(), 0),
                    team, enemyTeam, battleLogs, battleId, round);

            battleLogs.add(new BattleLog(battleId, round, "单位阵亡", currentField.getName(), "战场",
                    0, EffectType.BUFF));
        }

        if (currentField == null || !currentField.isAlive()) {
            Guardian newField = team.stream()
                    .filter(Objects::nonNull)
                    .filter(g -> !g.isOnField() && g.isAlive())
                    .findFirst()
                    .orElse(null);

            if (newField != null) {
                newField.setOnField(true);
                battleLogs.add(new BattleLog(battleId, round, "登场", newField.getName(), "场上",
                        0, EffectType.BUFF));
                triggerOnEnterSkill(newField, team, enemyTeam, battleLogs, battleId, round);
                return newField;
            }
        }

        return currentField;
    }

    private boolean isTeamAllDead(List<Guardian> team) {
        if (team == null) {
            return true;
        }

        return team.stream()
                .filter(Objects::nonNull)
                .noneMatch(Guardian::isAlive);
    }

    private void triggerOnEnterSkill(Guardian guardian, List<Guardian> ownTeam, List<Guardian> enemyTeam,
                                     List<BattleLog> battleLogs, String battleId, int round) {
        if (guardian == null || battleLogs == null || battleId == null) {
            return;
        }

        switch (guardian.getName()) {
            case "托塔天王":
                if (enemyTeam != null) {
                    Guardian enemyField = enemyTeam.stream()
                            .filter(Objects::nonNull)
                            .filter(Guardian::isOnField)
                            .findFirst()
                            .orElse(null);

                    if (enemyField != null) {
                        int damage = 69 * guardian.getLevel();
                        int finalDamage = takeDamageWithEvent(enemyField, damage, DamageType.MISSILE,
                                ownTeam, enemyTeam, battleLogs, battleId, round,
                                guardian.getId());
                        battleLogs.add(new BattleLog(battleId, round, "镇妖塔（登场）", guardian.getName(), enemyField.getName(),
                                finalDamage, EffectType.DAMAGE, DamageType.MISSILE));
                    }
                }
                battleLogs.add(new BattleLog(battleId, round, "仙塔庇护（被动）", guardian.getName(), "自身",
                        25 * guardian.getLevel(), EffectType.HEAL));
                break;

            case "牛魔王":
                if (enemyTeam != null) {
                    List<String> tauntedTargets = enemyTeam.stream()
                            .filter(Objects::nonNull)
                            .filter(Guardian::isAlive)
                            .map(Guardian::getName)
                            .collect(Collectors.toList());

                    if (!tauntedTargets.isEmpty()) {
                        addMultiTargetLog(battleLogs, battleId, round, "群体嘲讽",
                                guardian.getName(), tauntedTargets,
                                0, EffectType.DEBUFF, null);
                    }
                }
                break;

            case "厚土娘娘":
                int debuffCount = (int) guardian.getBuffs().stream()
                        .filter(Objects::nonNull)
                        .filter(b -> b.getType().isDebuff())
                        .count();

                guardian.getBuffs().removeIf(buff -> buff != null && buff.getType().isDebuff());
                battleLogs.add(new BattleLog(battleId, round, "大地净化（登场）", guardian.getName(), "自身",
                        debuffCount, EffectType.BUFF));
                break;

            case "长生大帝":
                if (ownTeam != null) {
                    List<String> healedTargets = ownTeam.stream()
                            .filter(Objects::nonNull)
                            .filter(Guardian::isAlive)
                            .map(Guardian::getName)
                            .collect(Collectors.toList());

                    if (!healedTargets.isEmpty()) {
                        addMultiTargetLog(battleLogs, battleId, round, "群体治疗（登场）",
                                guardian.getName(), healedTargets,
                                50 * guardian.getLevel(), EffectType.HEAL, null);

                        ownTeam.stream()
                                .filter(Objects::nonNull)
                                .filter(Guardian::isAlive)
                                .forEach(g -> g.heal(50 * guardian.getLevel()));
                    }
                }
                break;

            default:
                break;
        }
    }

    private void triggerOffFieldStartSkill(List<Guardian> ownTeam, List<Guardian> enemyTeam,
                                           List<BattleLog> battleLogs, String battleId, int round) {
        if (ownTeam == null || battleLogs == null || battleId == null) {
            return;
        }

        Map<String, List<String>> aoeSkills = new HashMap<>();

        for (Guardian guardian : ownTeam) {
            if (guardian == null || guardian.isOnField() || !guardian.isAlive()) {
                continue;
            }

            switch (guardian.getName()) {
                case "妲己":
                    if (enemyTeam != null && guardian.getRandom().nextDouble() < 0.1 * guardian.getLevel()) {
                        List<String> charmedTargets = enemyTeam.stream()
                                .filter(Objects::nonNull)
                                .filter(Guardian::isAlive)
                                .map(Guardian::getName)
                                .collect(Collectors.toList());

                        if (!charmedTargets.isEmpty()) {
                            aoeSkills.put("群体魅惑（场下）-" + guardian.getName(), charmedTargets);

                            enemyTeam.stream()
                                    .filter(Objects::nonNull)
                                    .filter(Guardian::isAlive)
                                    .forEach(g -> g.addBuff(new Buff(BuffType.STUNNED, 1, 0)));
                        }
                    }
                    break;

                case "玄冥":
                    if (enemyTeam != null) {
                        List<String> poisonedTargets = enemyTeam.stream()
                                .filter(Objects::nonNull)
                                .filter(Guardian::isAlive)
                                .map(Guardian::getName)
                                .collect(Collectors.toList());

                        if (!poisonedTargets.isEmpty()) {
                            aoeSkills.put("瘟疫扩散（场下）-" + guardian.getName(), poisonedTargets);

                            int poisonDamage = 16 * guardian.getLevel();
                            enemyTeam.stream()
                                    .filter(Objects::nonNull)
                                    .filter(Guardian::isAlive)
                                    .forEach(g -> g.addBuff(new Buff(BuffType.POISONED, -1, poisonDamage)));
                        }
                    }
                    break;

                case "阎罗王":
                    if (enemyTeam != null) {
                        List<String> judgedTargets = enemyTeam.stream()
                                .filter(Objects::nonNull)
                                .filter(Guardian::isAlive)
                                .map(Guardian::getName)
                                .collect(Collectors.toList());

                        if (!judgedTargets.isEmpty()) {
                            aoeSkills.put("生死判决（场下）-" + guardian.getName(), judgedTargets);
                        }
                    }
                    break;

                case "孟婆":
                    if (enemyTeam != null) {
                        List<String> forgottenTargets = enemyTeam.stream()
                                .filter(Objects::nonNull)
                                .filter(Guardian::isAlive)
                                .map(Guardian::getName)
                                .collect(Collectors.toList());

                        if (!forgottenTargets.isEmpty()) {
                            aoeSkills.put("群体遗忘（场下）-" + guardian.getName(), forgottenTargets);

                            enemyTeam.stream()
                                    .filter(Objects::nonNull)
                                    .filter(Guardian::isAlive)
                                    .forEach(g -> {
                                        g.getBuffs().removeIf(b -> !b.getType().isDebuff());
                                    });
                        }
                    }
                    break;

                default:
                    if (guardian.getName().equals("镇元子") && enemyTeam != null) {
                        Guardian enemyField = enemyTeam.stream()
                                .filter(Objects::nonNull)
                                .filter(Guardian::isOnField)
                                .findFirst()
                                .orElse(null);

                        if (enemyField != null) {
                            int damage = 178 * guardian.getLevel();
                            int finalDamage = takeDamageWithEvent(enemyField, damage, DamageType.MISSILE,
                                    ownTeam, enemyTeam, battleLogs, battleId, round,
                                    guardian.getId());
                            battleLogs.add(new BattleLog(battleId, round, "魂力飞弹（场下）", guardian.getName(), enemyField.getName(),
                                    finalDamage, EffectType.DAMAGE, DamageType.MISSILE));
                        }
                    }
                    break;
            }
        }

        for (Map.Entry<String, List<String>> entry : aoeSkills.entrySet()) {
            String skillInfo = entry.getKey();
            String[] parts = skillInfo.split("-");
            String skillName = parts[0];
            String source = parts.length > 1 ? parts[1] : "未知";

            addMultiTargetLog(battleLogs, battleId, round, skillName, source,
                    entry.getValue(), 0, EffectType.DEBUFF, null);
        }
    }

    private void handleTurnBuffs(List<Guardian> ownTeam, List<Guardian> enemyTeam,
                                 List<BattleLog> battleLogs, String battleId, int round) {
        if (enemyTeam == null || battleLogs == null || battleId == null) {
            return;
        }

        Map<String, List<String>> dotEffects = new HashMap<>();
        Map<String, Integer> dotDamage = new HashMap<>();

        for (Guardian guardian : enemyTeam) {
            if (guardian == null || !guardian.isAlive()) {
                continue;
            }

            Optional<Buff> poisonBuff = guardian.getBuffs().stream()
                    .filter(Objects::nonNull)
                    .filter(b -> b.getType() == BuffType.POISONED)
                    .findFirst();

            if (poisonBuff.isPresent()) {
                int damage = poisonBuff.get().getValue();
                int finalDamage = takeDamageWithEvent(guardian, damage, DamageType.POISON,
                        ownTeam, enemyTeam, battleLogs, battleId, round,
                        "system");

                String key = "中毒效果";
                if (!dotEffects.containsKey(key)) {
                    dotEffects.put(key, new ArrayList<>());
                    dotDamage.put(key, damage);
                }
                dotEffects.get(key).add(guardian.getName());
            }

            Optional<Buff> diseaseBuff = guardian.getBuffs().stream()
                    .filter(Objects::nonNull)
                    .filter(b -> b.getType() == BuffType.DISEASED)
                    .findFirst();

            if (diseaseBuff.isPresent()) {
                int damage = diseaseBuff.get().getValue();
                int finalDamage = takeDamageWithEvent(guardian, damage, DamageType.POISON,
                        ownTeam, enemyTeam, battleLogs, battleId, round,
                        "system");

                String key = "疾病效果";
                if (!dotEffects.containsKey(key)) {
                    dotEffects.put(key, new ArrayList<>());
                    dotDamage.put(key, damage);
                }
                dotEffects.get(key).add(guardian.getName());

                diseaseBuff.get().reduceDuration();
            }
        }

        for (Map.Entry<String, List<String>> entry : dotEffects.entrySet()) {
            String effectName = entry.getKey();
            List<String> targets = entry.getValue();

            if (!targets.isEmpty()) {
                addMultiTargetLog(battleLogs, battleId, round, effectName, "系统",
                        targets, dotDamage.get(effectName),
                        EffectType.DAMAGE, DamageType.POISON);
            }
        }

        if (ownTeam != null) {
            for (Guardian guardian : ownTeam) {
                if (guardian != null && guardian.isOnField() && guardian.isAlive() &&
                        guardian.getName().equals("托塔天王")) {

                    int healAmount = 25 * guardian.getLevel();
                    int actualHeal = guardian.heal(healAmount);
                    battleLogs.add(new BattleLog(battleId, round, "仙塔庇护（回合中）", guardian.getName(), "自身",
                            actualHeal, EffectType.HEAL));
                }
            }
        }
    }

    private void triggerHoutuSkill(Guardian guardian, List<BattleLog> battleLogs, String battleId, int round) {
        if (guardian == null || !guardian.getName().equals("厚土娘娘") ||
                !guardian.isOnField() || !guardian.isAlive() || battleLogs == null || battleId == null) {
            return;
        }

        int stack = guardian.getSkillTriggers().getOrDefault("后土聚能", 0);
        if (stack < 99) {
            int addHp = 197 * guardian.getLevel();
            int addAtk = 67 * guardian.getLevel();

            guardian.addMaxHp(addHp);
            int actualHeal = guardian.heal(addHp);
            guardian.addAttack(addAtk);

            guardian.getSkillTriggers().put("后土聚能", stack + 1);

            battleLogs.add(new BattleLog(battleId, round, "后土聚能", guardian.getName(), "自身",
                    0, EffectType.BUFF));
        }
    }

    private void attackProcess(Guardian attacker, Guardian defender, List<Guardian> attackerTeam,
                               List<Guardian> defenderTeam, List<BattleLog> battleLogs, String battleId, int round) {
        if (attacker == null || defender == null || battleLogs == null || battleId == null) {
            return;
        }

        triggerPreAttackSkill(attacker, defender, defenderTeam, attackerTeam, battleLogs, battleId, round);

        boolean isStunned = attacker.getBuffs().stream()
                .filter(Objects::nonNull)
                .anyMatch(b -> b.getType() == BuffType.STUNNED);

        if (isStunned) {
            battleLogs.add(new BattleLog(battleId, round, "眩晕效果", attacker.getName(), "自身",
                    0, EffectType.DEBUFF));
            attacker.getBuffs().removeIf(buff -> buff != null && buff.getType() == BuffType.STUNNED);
            return;
        }

        int baseDamage = attacker.getCurrentAttack();
        int finalDamage = takeDamageWithEvent(defender, baseDamage, DamageType.PHYSICAL,
                attackerTeam, defenderTeam, battleLogs, battleId, round,
                attacker.getId());

        battleLogs.add(new BattleLog(battleId, round, "普通攻击", attacker.getName(), defender.getName(),
                finalDamage, EffectType.DAMAGE, DamageType.PHYSICAL));

        if (attacker.getName().equals("牛魔王") && defenderTeam != null) {
            List<String> splashTargets = new ArrayList<>();
            int splashDamage = (int) (baseDamage * 0.3);

            defenderTeam.stream()
                    .filter(Objects::nonNull)
                    .filter(g -> !g.getId().equals(defender.getId()) && g.isAlive())
                    .forEach(g -> {
                        takeDamageWithEvent(g, splashDamage, DamageType.PHYSICAL,
                                attackerTeam, defenderTeam, battleLogs, battleId, round,
                                attacker.getId());
                        splashTargets.add(g.getName());
                    });

            if (!splashTargets.isEmpty()) {
                addMultiTargetLog(battleLogs, battleId, round, "溅射伤害",
                        attacker.getName(), splashTargets,
                        splashDamage, EffectType.DAMAGE, DamageType.PHYSICAL);
            }
        }

        triggerPostAttackSkill(attacker, defender, attackerTeam, defenderTeam, battleLogs, battleId, round);

        if (defender.getName().equals("聂小倩") && defender.isAlive()) {
            int poisonDamage = 8 * defender.getLevel();
            attacker.addBuff(new Buff(BuffType.POISONED, -1, poisonDamage));
            battleLogs.add(new BattleLog(battleId, round, "幽灵毒击（受击）", defender.getName(), attacker.getName(),
                    poisonDamage, EffectType.DEBUFF, DamageType.POISON));
        }

        if (defender.getName().equals("刑天") && defender.isAlive()) {
            int addAtk = 118 * defender.getLevel();
            int addSpd = 20 * defender.getLevel();
            defender.addBuff(new Buff(BuffType.BLOODLUST, -1, addAtk));
            defender.addBuff(new Buff(BuffType.SPEED_UP, -1, addSpd));

            battleLogs.add(new BattleLog(battleId, round, "嗜血（受击）", defender.getName(), "自身",
                    addAtk, EffectType.ATTACK_UP));
        }
    }

    // 修复triggerPreAttackSkill方法签名，添加defenderTeam参数
    private void triggerPreAttackSkill(Guardian attacker, Guardian defender, List<Guardian> defenderTeam,
                                       List<Guardian> attackerTeam, List<BattleLog> battleLogs,
                                       String battleId, int round) {
        if (attacker == null || defender == null || !defender.isAlive() || battleLogs == null || battleId == null) {
            return;
        }

        switch (attacker.getName()) {
            case "齐天大圣":
                // 大闹天宫（对敌方全体造成伤害）
                if (defenderTeam != null) {
                    List<String> monkeyTargets = new ArrayList<>();
                    int monkeyDamage = (int) (defender.getCurrentHp() * 0.03);

                    defenderTeam.stream()
                            .filter(Objects::nonNull)
                            .filter(Guardian::isAlive)
                            .forEach(g -> {
                                takeDamageWithEvent(g, monkeyDamage, DamageType.TRUE_DAMAGE,
                                        attackerTeam, defenderTeam, battleLogs, battleId, round,
                                        attacker.getId());
                                monkeyTargets.add(g.getName());
                            });

                    if (!monkeyTargets.isEmpty()) {
                        addMultiTargetLog(battleLogs, battleId, round, "大闹天宫（攻击前）",
                                attacker.getName(), monkeyTargets,
                                monkeyDamage, EffectType.DAMAGE, DamageType.TRUE_DAMAGE);
                    }
                }
                break;

            case "白骨精":
                if (attacker.getRandom().nextDouble() < 0.5 * attacker.getLevel()) {
                    int boneDamage = 80 * attacker.getLevel();
                    int finalDamage = takeDamageWithEvent(defender, boneDamage, DamageType.PHYSICAL,
                            attackerTeam, defenderTeam, battleLogs, battleId, round,
                            attacker.getId());
                    battleLogs.add(new BattleLog(battleId, round, "骨刺突袭（攻击前）", attacker.getName(), defender.getName(),
                            finalDamage, EffectType.DAMAGE, DamageType.PHYSICAL));
                }
                break;

            default:
                break;
        }
    }

    private void triggerPostAttackSkill(Guardian attacker, Guardian defender, List<Guardian> attackerTeam,
                                        List<Guardian> defenderTeam, List<BattleLog> battleLogs, String battleId, int round) {
        if (attacker == null || battleLogs == null || battleId == null) {
            return;
        }

        switch (attacker.getName()) {
            case "铁扇公主":
                if (defenderTeam != null) {
                    List<String> fanTargets = defenderTeam.stream()
                            .filter(Objects::nonNull)
                            .filter(Guardian::isAlive)
                            .map(Guardian::getName)
                            .collect(Collectors.toList());

                    if (!fanTargets.isEmpty()) {
                        addMultiTargetLog(battleLogs, battleId, round, "芭蕉扇（攻击后）",
                                attacker.getName(), fanTargets,
                                36 * attacker.getLevel(), EffectType.DAMAGE, DamageType.FIRE);

                        defenderTeam.stream()
                                .filter(Objects::nonNull)
                                .filter(Guardian::isAlive)
                                .forEach(g -> takeDamageWithEvent(g, 36 * attacker.getLevel(),
                                        DamageType.FIRE, attackerTeam,
                                        defenderTeam, battleLogs, battleId,
                                        round, attacker.getId()));
                    }
                }
                break;

            case "聂小倩":
                if (defenderTeam != null && defender.getBuffs().stream()
                        .filter(Objects::nonNull)
                        .anyMatch(b -> b.getType() == BuffType.POISONED)) {

                    List<String> poisonTargets = new ArrayList<>();
                    int extraDamage = 60 * attacker.getLevel();

                    defenderTeam.stream()
                            .filter(Objects::nonNull)
                            .filter(Guardian::isAlive)
                            .forEach(g -> {
                                g.addBuff(new Buff(BuffType.POISONED, -1, extraDamage));
                                poisonTargets.add(g.getName());
                            });

                    if (!poisonTargets.isEmpty()) {
                        addMultiTargetLog(battleLogs, battleId, round, "剧毒蔓延（攻击后）",
                                attacker.getName(), poisonTargets,
                                extraDamage, EffectType.DEBUFF, DamageType.POISON);
                    }
                }
                break;

            case "燃灯道人":
                if (attackerTeam != null) {
                    List<String> blessTargets = attackerTeam.stream()
                            .filter(Objects::nonNull)
                            .filter(g -> g.getPosition() > attacker.getPosition() && g.isAlive())
                            .map(Guardian::getName)
                            .collect(Collectors.toList());

                    if (!blessTargets.isEmpty()) {
                        addMultiTargetLog(battleLogs, battleId, round, "仙人指路（群体祝福）",
                                attacker.getName(), blessTargets,
                                66 * attacker.getLevel(), EffectType.ATTACK_UP, null);

                        attackerTeam.stream()
                                .filter(Objects::nonNull)
                                .filter(g -> g.getPosition() > attacker.getPosition() && g.isAlive())
                                .forEach(g -> g.addAttack(66 * attacker.getLevel()));
                    }
                }
                break;

            default:
                break;
        }
    }

    public List<BattleLog> getBattleLogs(String battleId) {
        return BATTLE_LOG_CACHE.getOrDefault(battleId, new ArrayList<>());
    }

    public void printBattleLogs(String battleId) {
        if (battleId == null) {
            System.out.println("无效的战斗ID");
            return;
        }

        List<BattleLog> logs = getBattleLogs(battleId);
        System.out.println("\n===== 战斗记录 [" + battleId + "] =====");

        Map<Integer, List<BattleLog>> logsByRound = logs.stream()
                .collect(Collectors.groupingBy(BattleLog::getRound));

        logsByRound.forEach((round, roundLogs) -> {
            System.out.println(String.format("\n--- 第%d回合 ---", round));
            roundLogs.forEach(log -> System.out.println(log.toString()));
        });

        System.out.println("\n==============================\n");
    }

    public static void main(String[] args) {
        try {
            CardBattleTool battleTool = new CardBattleTool();

            List<Guardian> teamA = new ArrayList<>();
            teamA.add(new Guardian("牛魔王", Profession.WARRIOR, Race.DEMON_RACE, 3, 1));
            teamA.add(new Guardian("厚土娘娘", Profession.IMMORTAL, Race.IMMORTAL_RACE, 3, 2));
            teamA.add(new Guardian("铁扇公主", Profession.IMMORTAL, Race.DEMON_RACE, 3, 3));
            teamA.add(new Guardian("聂小倩", Profession.IMMORTAL, Race.DEMON_RACE, 3, 4));
            teamA.add(new Guardian("燃灯道人", Profession.GOD, Race.IMMORTAL_RACE, 3, 5));

            List<Guardian> teamB = new ArrayList<>();
            teamB.add(new Guardian("齐天大圣", Profession.GOD, Race.DEMON_RACE, 3, 1));
            teamB.add(new Guardian("玄冥", Profession.IMMORTAL, Race.DEMON_RACE, 3, 2));
            teamB.add(new Guardian("长生大帝", Profession.IMMORTAL, Race.IMMORTAL_RACE, 3, 3));
            teamB.add(new Guardian("阎罗王", Profession.GOD, Race.DEMON_RACE, 3, 4));
            teamB.add(new Guardian("妲己", Profession.IMMORTAL, Race.DEMON_RACE, 3, 5));

            String battleId = battleTool.startBattle(teamA, teamB);
            battleTool.printBattleLogs(battleId);

        } catch (Exception e) {
            System.err.println("战斗过程中发生错误: " + e.getMessage());
            e.printStackTrace();
        }
    }
}