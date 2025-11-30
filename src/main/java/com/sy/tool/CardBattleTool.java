package com.sy.tool;

import com.sy.model.game.*;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;



/**
 * 卡牌5v5战斗核心工具类
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
                    0, EffectType.BUFF, null));
            triggerOffFieldEnterSkill(teamA, teamB, battleLogs, battleId, 0);
            triggerOnEnterSkill(fieldA, teamA, teamB, battleLogs, battleId, 0);
            triggerEvent(new BattleEvent(EventType.UNIT_ENTER, fieldA.getId(), null, 0),
                    teamA, teamB, battleLogs, battleId, 0);

            // 触发敌方厚土娘娘大地净化
            teamB.stream()
                    .filter(Objects::nonNull)
                    .filter(g -> g.getName().equals("厚土娘娘") && g.isOnField() && g.isAlive())
                    .forEach(g -> g.triggerEarthPurification(battleLogs, battleId, 0));
        }

        if (fieldB != null) {
            fieldB.setOnField(true);
            battleLogs.add(new BattleLog(battleId, 0, "登场", fieldB.getName(), "场上",
                    0, EffectType.BUFF, null));
            triggerOffFieldEnterSkill(teamB, teamA, battleLogs, battleId, 0);
            triggerOnEnterSkill(fieldB, teamB, teamA, battleLogs, battleId, 0);
            triggerEvent(new BattleEvent(EventType.UNIT_ENTER, fieldB.getId(), null, 0),
                    teamB, teamA, battleLogs, battleId, 0);

            // 触发敌方厚土娘娘大地净化
            teamA.stream()
                    .filter(Objects::nonNull)
                    .filter(g -> g.getName().equals("厚土娘娘") && g.isOnField() && g.isAlive())
                    .forEach(g -> g.triggerEarthPurification(battleLogs, battleId, 0));
        }

        while (round < MAX_ROUNDS && status == BattleStatus.ONGOING) {
            round++;
            battleLogs.add(new BattleLog(battleId, round, "回合开始", "系统", "",
                    0, EffectType.BUFF, null));

            triggerLuoshenLifeTransfer(teamA, battleLogs, battleId, round);
            triggerLuoshenLifeTransfer(teamB, battleLogs, battleId, round);

            triggerYanluoWangLifeBook(teamA, teamB, battleLogs, battleId, round);

            triggerOffFieldStartSkill(teamA, teamB, battleLogs, battleId, round);
            triggerOffFieldStartSkill(teamB, teamA, battleLogs, battleId, round);

            fieldA = replaceDeadFieldGuardian(teamA, fieldA, teamB, battleLogs, battleId, round);
            fieldB = replaceDeadFieldGuardian(teamB, fieldB, teamA, battleLogs, battleId, round);

            if (isTeamAllDead(teamA)) {
                status = BattleStatus.LOSE;
                battleLogs.add(new BattleLog(battleId, round, "战斗结束", "系统", "A队全灭",
                        0, EffectType.BUFF, null));
                break;
            }
            if (isTeamAllDead(teamB)) {
                status = BattleStatus.WIN;
                battleLogs.add(new BattleLog(battleId, round, "战斗结束", "系统", "B队全灭",
                        0, EffectType.BUFF, null));
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

            if (attacker.getName().equals("圣灵天将")) {
                attacker.triggerHolyArray(teamA, teamB, battleLogs, battleId, round);
            }

            triggerEvent(new BattleEvent(EventType.ATTACK, attacker.getId(), defender.getId(), 0),
                    attackerTeam, defenderTeam, battleLogs, battleId, round);

            attackProcess(attacker, defender, attackerTeam, defenderTeam, battleLogs, battleId, round);

            triggerPostAttackSkills(attacker, defender, attackerTeam, defenderTeam, battleLogs, battleId, round);

            triggerEvent(new BattleEvent(EventType.TURN_END, null, null, 0),
                    teamA, teamB, battleLogs, battleId, round);
            triggerEvent(new BattleEvent(EventType.TURN_END, null, null, 0),
                    teamB, teamA, battleLogs, battleId, round);

            teamA.forEach(g -> g.cleanExpiredBuffs());
            teamB.forEach(g -> g.cleanExpiredBuffs());
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
                        totalHpA - totalHpB, EffectType.BUFF, null));
            } else if (totalHpB > totalHpA) {
                status = BattleStatus.LOSE;
                battleLogs.add(new BattleLog(battleId, round, "回合上限结束", "系统", "B队胜利（总血量高）",
                        totalHpB - totalHpA, EffectType.BUFF, null));
            } else {
                status = BattleStatus.DRAW;
                battleLogs.add(new BattleLog(battleId, round, "回合上限结束", "系统", "平局（总血量相同）",
                        0, EffectType.BUFF, null));
            }
        }

        return battleId;
    }

    private void triggerOffFieldEnterSkill(List<Guardian> ownTeam, List<Guardian> enemyTeam,
                                           List<BattleLog> battleLogs, String battleId, int round) {
        ownTeam.stream()
                .filter(Objects::nonNull)
                .filter(g -> !g.isOnField() && g.isAlive())
                .forEach(g -> {
                    if (g.getName().equals("阎罗王")) {
                        g.triggerNetherJudgment(enemyTeam, battleLogs, battleId, round);
                    }
                    else if (g.getName().equals("妲己")) {
                        if (enemyTeam != null && g.getRandom().nextDouble() < 0.1 * g.getLevel()) {
                            List<Guardian> aliveEnemies = enemyTeam.stream()
                                    .filter(Objects::nonNull)
                                    .filter(Guardian::isAlive)
                                    .collect(Collectors.toList());

                            if (!aliveEnemies.isEmpty()) {
                                Guardian target = aliveEnemies.get(g.getRandom().nextInt(aliveEnemies.size()));
                                Buff stunBuff = new Buff(Buff.BuffType.STUNNED, 1, 0);
                                target.addBuff(stunBuff);

                                String remainingTurns = stunBuff.isPermanent() ? "永久" : String.valueOf(stunBuff.getRemainingTurns());
                                battleLogs.add(new BattleLog(battleId, round, "九尾魅惑（妲己）", g.getName(), target.getName(),
                                        0, EffectType.DEBUFF, null,
                                        "眩晕，剩余回合：" + remainingTurns));
                            }
                        }
                    }
                });
    }

    private void triggerPostAttackSkills(Guardian attacker, Guardian defender, List<Guardian> attackerTeam,
                                         List<Guardian> defenderTeam, List<BattleLog> battleLogs, String battleId, int round) {
        if (attacker.getName().equals("牛魔王")) {
            attacker.triggerLavaBurst(defenderTeam, battleLogs, battleId, round);
        }

        if (attacker.getName().equals("铁扇公主") && defenderTeam != null) {
            List<String> fanTargets = defenderTeam.stream()
                    .filter(Objects::nonNull)
                    .filter(Guardian::isAlive)
                    .map(Guardian::getName)
                    .collect(Collectors.toList());

            if (!fanTargets.isEmpty()) {
                int fireDamage = 36 * attacker.getLevel();
                addMultiTargetLog(battleLogs, battleId, round, "芭蕉扇（铁扇公主）",
                        attacker.getName(), fanTargets, fireDamage,
                        EffectType.DAMAGE, DamageType.FIRE);

                defenderTeam.stream()
                        .filter(Objects::nonNull)
                        .filter(Guardian::isAlive)
                        .forEach(g -> takeDamageWithEvent(g, fireDamage, DamageType.FIRE,
                                attackerTeam, defenderTeam, battleLogs,
                                battleId, round, attacker.getId()));
            }
        }

        if (attacker.getName().equals("聂小倩") && defenderTeam != null && defender.getBuffs().stream()
                .filter(Objects::nonNull)
                .anyMatch(b -> b.getType() == Buff.BuffType.POISONED)) {

            List<String> poisonTargets = new ArrayList<>();
            int extraDamage = 60 * attacker.getLevel();

            defenderTeam.stream()
                    .filter(Objects::nonNull)
                    .filter(Guardian::isAlive)
                    .forEach(g -> {
                        Buff poisonBuff = new Buff(Buff.BuffType.POISONED, 2, extraDamage);
                        g.addBuff(poisonBuff);
                        poisonTargets.add(g.getName());

                        String remainingTurns = poisonBuff.isPermanent() ? "永久" : String.valueOf(poisonBuff.getRemainingTurns());
                        battleLogs.add(new BattleLog(battleId, round, "剧毒蔓延（聂小倩）", attacker.getName(), g.getName(),
                                extraDamage, EffectType.DEBUFF, DamageType.POISON,
                                "中毒+"+extraDamage+"，剩余回合：" + remainingTurns));
                    });
        }

        if (attacker.getName().equals("燃灯道人") && attackerTeam != null) {
            List<String> blessTargets = attackerTeam.stream()
                    .filter(Objects::nonNull)
                    .filter(g -> g.getPosition() > attacker.getPosition() && g.isAlive())
                    .map(Guardian::getName)
                    .collect(Collectors.toList());

            if (!blessTargets.isEmpty()) {
                int attackBoost = 66 * attacker.getLevel();
                addMultiTargetLog(battleLogs, battleId, round, "仙人指路（燃灯道人）",
                        attacker.getName(), blessTargets, attackBoost,
                        EffectType.ATTACK_UP, null);

                attackerTeam.stream()
                        .filter(Objects::nonNull)
                        .filter(g -> g.getPosition() > attacker.getPosition() && g.isAlive())
                        .forEach(g -> {
                            g.addAttack(attackBoost);
                            Buff attackBuff = new Buff(Buff.BuffType.ATTACK_UP, 2, attackBoost);
                            g.addBuff(attackBuff);

                            String remainingTurns = attackBuff.isPermanent() ? "永久" : String.valueOf(attackBuff.getRemainingTurns());
                            battleLogs.add(new BattleLog(battleId, round, "攻击提升", attacker.getName(), g.getName(),
                                    attackBoost, EffectType.BUFF, null,
                                    "攻击+"+attackBoost+"，剩余回合：" + remainingTurns));
                        });
            }
        }
    }

    private void triggerLuoshenLifeTransfer(List<Guardian> team, List<BattleLog> battleLogs,
                                            String battleId, int round) {
        team.stream()
                .filter(Objects::nonNull)
                .filter(Guardian::isAlive)
                .filter(g -> g.getName().equals("洛神"))
                .forEach(g -> g.triggerLifeTransfer(team, battleLogs, battleId, round));
    }

    private void triggerYanluoWangLifeBook(List<Guardian> teamA, List<Guardian> teamB,
                                           List<BattleLog> battleLogs, String battleId, int round) {
        teamA.stream()
                .filter(Objects::nonNull)
                .filter(Guardian::isAlive)
                .filter(g -> g.getName().equals("阎罗王"))
                .forEach(g -> g.triggerLifeBook(teamA, teamB, battleLogs, battleId, round));

        teamB.stream()
                .filter(Objects::nonNull)
                .filter(Guardian::isAlive)
                .filter(g -> g.getName().equals("阎罗王"))
                .forEach(g -> g.triggerLifeBook(teamA, teamB, battleLogs, battleId, round));
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

    private void handleUnitDeath(Guardian deadUnit, List<Guardian> deadUnitTeam, List<Guardian> otherTeam,
                                 boolean isEnemyDeathForA, boolean isEnemyDeathForB,
                                 List<BattleLog> battleLogs, String battleId, int round) {
        deadUnitTeam.stream()
                .filter(Objects::nonNull)
                .filter(Guardian::isAlive)
                .forEach(g -> g.handleDeathEvent(new BattleEvent(EventType.UNIT_DEATH, null, deadUnit.getId(), 0),
                        deadUnitTeam, otherTeam, !isEnemyDeathForA, battleLogs, battleId, round));

        otherTeam.stream()
                .filter(Objects::nonNull)
                .filter(Guardian::isAlive)
                .forEach(g -> g.handleDeathEvent(new BattleEvent(EventType.UNIT_DEATH, null, deadUnit.getId(), 0),
                        otherTeam, deadUnitTeam, isEnemyDeathForB, battleLogs, battleId, round));
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
            boolean isEnemyDeathForAttacker = defenderTeam != null && defenderTeam.contains(target);
            boolean isEnemyDeathForDefender = attackerTeam != null && attackerTeam.contains(target);

            handleUnitDeath(target, defenderTeam, attackerTeam,
                    isEnemyDeathForAttacker, isEnemyDeathForDefender,
                    battleLogs, battleId, round);

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
            handleUnitDeath(currentField, team, enemyTeam, true, true,
                    battleLogs, battleId, round);

            triggerEvent(new BattleEvent(EventType.UNIT_DEATH, null, currentField.getId(), 0),
                    team, enemyTeam, battleLogs, battleId, round);

            battleLogs.add(new BattleLog(battleId, round, "单位阵亡", currentField.getName(), "战场",
                    0, EffectType.BUFF, null));
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
                        0, EffectType.BUFF, null));

                // 触发敌方厚土娘娘大地净化
                enemyTeam.stream()
                        .filter(Objects::nonNull)
                        .filter(g -> g.getName().equals("厚土娘娘") && g.isOnField() && g.isAlive())
                        .forEach(g -> g.triggerEarthPurification(battleLogs, battleId, round));

                triggerOffFieldEnterSkill(team, enemyTeam, battleLogs, battleId, round);

                triggerOnEnterSkill(newField, team, enemyTeam, battleLogs, battleId, round);

                triggerEvent(new BattleEvent(EventType.UNIT_ENTER, newField.getId(), null, 0),
                        team, enemyTeam, battleLogs, battleId, round);

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
                        25 * guardian.getLevel(), EffectType.HEAL, null));
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
                        debuffCount, EffectType.BUFF, null));
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
                                    .forEach(g -> {
                                        Buff poisonBuff = new Buff(Buff.BuffType.POISONED, 2, poisonDamage);
                                        g.addBuff(poisonBuff);

                                        String remainingTurns = poisonBuff.isPermanent() ? "永久" : String.valueOf(poisonBuff.getRemainingTurns());
                                        battleLogs.add(new BattleLog(battleId, round, "瘟疫扩散（玄冥）", guardian.getName(), g.getName(),
                                                poisonDamage, EffectType.DEBUFF, DamageType.POISON,
                                                "中毒+"+poisonDamage+"，剩余回合：" + remainingTurns));
                                    });
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

                case "镇元子":
                    if (enemyTeam != null) {
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

        // 触发厚土娘娘后土聚能
        ownTeam.stream()
                .filter(Objects::nonNull)
                .filter(g -> g.getName().equals("厚土娘娘") && g.isOnField() && g.isAlive())
                .forEach(g -> g.triggerHoutuAccumulation(battleLogs, battleId, round));

        for (Guardian guardian : enemyTeam) {
            if (guardian == null || !guardian.isAlive()) {
                continue;
            }

            Optional<Buff> poisonBuff = guardian.getBuffs().stream()
                    .filter(Objects::nonNull)
                    .filter(b -> b.getType() == Buff.BuffType.POISONED)
                    .findFirst();

            if (poisonBuff.isPresent()) {
                Buff buff = poisonBuff.get();
                int damage = buff.getValue();
                int finalDamage = takeDamageWithEvent(guardian, damage, DamageType.POISON,
                        ownTeam, enemyTeam, battleLogs, battleId, round,
                        "system");

                String remainingTurns = buff.isPermanent() ? "永久" : String.valueOf(buff.getRemainingTurns());
                battleLogs.add(new BattleLog(battleId, round, "中毒效果", "系统", guardian.getName(),
                        finalDamage, EffectType.DAMAGE, DamageType.POISON,
                        "剩余回合：" + remainingTurns));

                String key = "中毒效果";
                if (!dotEffects.containsKey(key)) {
                    dotEffects.put(key, new ArrayList<>());
                    dotDamage.put(key, damage);
                }
                dotEffects.get(key).add(guardian.getName());
            }

            Optional<Buff> diseaseBuff = guardian.getBuffs().stream()
                    .filter(Objects::nonNull)
                    .filter(b -> b.getType() == Buff.BuffType.DISEASED)
                    .findFirst();

            if (diseaseBuff.isPresent()) {
                Buff buff = diseaseBuff.get();
                int damage = buff.getValue();
                int finalDamage = takeDamageWithEvent(guardian, damage, DamageType.POISON,
                        ownTeam, enemyTeam, battleLogs, battleId, round,
                        "system");

                String remainingTurns = buff.isPermanent() ? "永久" : String.valueOf(buff.getRemainingTurns());
                battleLogs.add(new BattleLog(battleId, round, "疾病效果", "系统", guardian.getName(),
                        finalDamage, EffectType.DAMAGE, DamageType.POISON,
                        "剩余回合：" + remainingTurns));

                String key = "疾病效果";
                if (!dotEffects.containsKey(key)) {
                    dotEffects.put(key, new ArrayList<>());
                    dotDamage.put(key, damage);
                }
                dotEffects.get(key).add(guardian.getName());
            }
        }
    }

    private void triggerHoutuSkill(Guardian guardian, List<BattleLog> battleLogs, String battleId, int round) {
        if (guardian == null || !guardian.isAlive() || !guardian.getName().equals("厚土娘娘")) {
            return;
        }

        int currentStacks = guardian.getSkillTriggers().getOrDefault("后土聚能", 0);
        currentStacks++;
        guardian.getSkillTriggers().put("后土聚能", currentStacks);

        if (currentStacks >= 3) {
            int healAmount = 200 * guardian.getLevel();
            guardian.heal(healAmount);
            battleLogs.add(new BattleLog(battleId, round, "后土之愈（厚土娘娘）", guardian.getName(), "自身",
                    healAmount, EffectType.HEAL, null));
            guardian.getSkillTriggers().put("后土聚能", 0);
        }
    }

    private void attackProcess(Guardian attacker, Guardian defender, List<Guardian> attackerTeam,
                               List<Guardian> defenderTeam, List<BattleLog> battleLogs, String battleId, int round) {
        if (attacker == null || defender == null || !attacker.isAlive() || !defender.isAlive()) {
            return;
        }

        // 检查是否被眩晕
        boolean isStunned = attacker.getBuffs().stream()
                .filter(Objects::nonNull)
                .anyMatch(b -> b.getType() == Buff.BuffType.STUNNED);

        if (isStunned) {
            battleLogs.add(new BattleLog(battleId, round, "被眩晕无法攻击", attacker.getName(), "",
                    0, EffectType.DEBUFF, null));
            return;
        }

        int baseDamage = attacker.getCurrentAttack();
        int finalDamage = takeDamageWithEvent(defender, baseDamage, DamageType.PHYSICAL,
                attackerTeam, defenderTeam, battleLogs, battleId, round,
                attacker.getId());

        battleLogs.add(new BattleLog(battleId, round, "普通攻击", attacker.getName(), defender.getName(),
                finalDamage, EffectType.DAMAGE, DamageType.PHYSICAL));

        // 触发圣灵斩
        if (attacker.getName().equals("圣灵天将")) {
            attacker.triggerHolySlash(defender, battleLogs, battleId, round);
        }
    }

    // 获取战斗日志
    public List<BattleLog> getBattleLogs(String battleId) {
        return BATTLE_LOG_CACHE.getOrDefault(battleId, new ArrayList<>());
    }

    // 清理战斗日志缓存
    public void clearBattleLogs(String battleId) {
        BATTLE_LOG_CACHE.remove(battleId);
    }

    // 清理所有战斗日志缓存
    public void clearAllBattleLogs() {
        BATTLE_LOG_CACHE.clear();
    }
}