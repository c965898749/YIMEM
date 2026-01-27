package com.sy.model.game;

import com.sy.tool.CardSkillLevelUtil;
import com.sy.tool.CardSkillLevelUtil;
import com.sy.tool.Xtool;

import java.util.*;
import java.util.concurrent.LinkedTransferQueue;
import java.util.stream.Collectors;

import java.util.*;
import java.util.stream.Collectors;

public class BattleManager {
    private String battleId;
    private List<Guardian> campA = new ArrayList<>();
    private List<Guardian> campB = new ArrayList<>();
    private Guardian fieldA;
    private Guardian fieldB;
    private int currentRound = 0;
    private List<BattleLog> battleLogs = new ArrayList<>();
    private Random random = new Random();

    public BattleManager(String battleId, List<Guardian> campA, List<Guardian> campB) {
        this.battleId = battleId;
        this.campA = campA;
        this.campB = campB;
        initBattle();
    }

    // 初始化战斗
    private void initBattle() {
        fieldA = getNextGuardian(campA);
        fieldB = getNextGuardian(campB);
        if (fieldA != null) {
            fieldA.setOnField(true);
            triggerOnEnterSkills(fieldA);
        }
        if (fieldB != null) {
            fieldB.setOnField(true);
            triggerOnEnterSkills(fieldB);
        }
    }

    // 获取下一个可上场的护法
    private Guardian getNextGuardian(List<Guardian> camp) {
        return camp.stream()
                .filter(g -> !g.isDead() && !g.isOnField())
                .findFirst()
                .orElse(null);
    }

    // 获取在场单位状态描述（包含位置）
    private String getFieldUnitsStatus() {
        StringBuilder sb = new StringBuilder();
        if (fieldA != null) {
            sb.append(String.format("A[%d号位:%s:HP=%d/%d,ATK=%d,SPEED=%d] ",
                    fieldA.getPosition(), fieldA.getName(),
                    fieldA.getCurrentHp(), fieldA.getMaxHp(),
                    fieldA.getAttack(), fieldA.getSpeed()));
        }
        if (fieldB != null) {
            sb.append(String.format("B[%d号位:%s:HP=%d/%d,ATK=%d,SPEED=%d] ",
                    fieldB.getPosition(), fieldB.getName(),
                    fieldB.getCurrentHp(), fieldB.getMaxHp(),
                    fieldB.getAttack(), fieldB.getSpeed()));
        }
        return sb.toString().trim();
    }

    // 开始战斗
    public void startBattle() {
        while (currentRound < 100 && !isBattleEnd()) {
            currentRound++;
            addLog("ROUND_START", "SYSTEM", null, 0,
                    0, 0, 0, 0, 0, 0,
                    null, null, 0,
                    0, 0, 0, 0, 0, 0,
                    getFieldUnitsStatus(),
                    0, null, null, "回合开始");
            //1. 登场并检查阵亡替换
            checkAndReplaceGuardians();
            //2. 回合开始处理
            processRoundStartEffects();

            // 场上战斗
            if (fieldA != null && fieldB != null) {
                processAttack(fieldA, fieldB);
                if (!fieldB.isDead()) {
                    processAttack(fieldB, fieldA);
                }
            }

            //3.处理场下技能
            processOnFieldSkills2();

            //4. 回合结束处理
            processRoundEndEffects();
        }

        // 战斗结束判定
        endBattle();
    }

    // 处理攻击流程
    private void processAttack(Guardian attacker, Guardian defender) {
        // 记录攻击前状态
        int attackerHpBefore = attacker.getMaxHp();
        int attackerAttackBefore = attacker.getAttack();
        int attackerSpeedBefore = attacker.getSpeed();

        int defenderHpBefore = defender.getMaxHp();
        int defenderAttackBefore = defender.getAttack();
        int defenderSpeedBefore = defender.getSpeed();

        // 攻击前技能触发
        triggerPreAttackSkills(attacker, defender);

        if (defender.isDead()) return;

        // 普通攻击
        int damage = attacker.getAttack();
        //瑶池仙女物理抗性
        if (defender.getName().equals("瑶池仙女") && defender.getPosition() == 2) {
            int[] skillLevel = CardSkillLevelUtil.calculateSkillLevels(defender.getLevel(), defender.getStar().doubleValue());
            if (skillLevel[1] > 0) {
                damage = damage - 32 * skillLevel[1];
                if (damage < 0) {
                    damage = 0;
                }
            }

        }
        defender.setCurrentHp(defender.getCurrentHp() - damage);

        addLog("NORMAL_ATTACK",
                attacker.getName(), attacker.getCamp(), attacker.getPosition(),
                attackerHpBefore, attacker.getCurrentHp(),
                attackerAttackBefore, attacker.getAttack(),
                attackerSpeedBefore, attacker.getSpeed(),
                defender.getName(), defender.getCamp(), defender.getPosition(),
                defenderHpBefore, defender.getCurrentHp(),
                defenderAttackBefore, defender.getAttack(),
                defenderSpeedBefore, defender.getSpeed(),
                getFieldUnitsStatus(),
                damage, EffectType.DAMAGE, DamageType.PHYSICAL,
                attacker.getName() + "对" + defender.getName() + "造成物理伤害");
        // 检查阵亡
        if (defender.getCurrentHp() <= 0) {
            defender.setDead(true);
            defender.setOnField(false);

            addLog("UNIT_DEATH",
                    defender.getName(), defender.getCamp(), defender.getPosition(),
                    defender.getCurrentHp(), 0,
                    defender.getAttack(), defender.getAttack(),
                    defender.getSpeed(), defender.getSpeed(),
                    defender.getName(), defender.getCamp(), defender.getPosition(),
                    defender.getCurrentHp(), 0,
                    defender.getAttack(), defender.getAttack(),
                    defender.getSpeed(), defender.getSpeed(),
                    getFieldUnitsStatus(),
                    0, null, null, defender.getName() + "阵亡");

            // 触发死亡相关技能
            triggerOnDeathSkills(defender);
        }
        // 触发受击技能
        triggerOnAttackedSkills(defender, attacker);

        // 攻击后技能触发
        triggerPostAttackSkills(attacker, defender);


    }

    // 触发登场技能
    private void triggerOnEnterSkills(Guardian guardian) {
        int sourceHpBefore = guardian.getMaxHp();
        int sourceAttackBefore = guardian.getAttack();
        int sourceSpeedBefore = guardian.getSpeed();
        int[] skillLevel = CardSkillLevelUtil.calculateSkillLevels(guardian.getLevel(), guardian.getStar().doubleValue());
        //TODO 对方登场优先在场先触发技能
        if (1 == 1) {
            Guardian enemy = guardian.getCamp() == Camp.A ? fieldB : fieldA;
            if (Xtool.isNull(enemy.getEffects().get(EffectType.SILENCE)) && enemy != null) {
                switch (enemy.getName()) {
                    case "托塔天王":
                        // 镇妖塔：对敌方场上造成飞弹伤害
                        if (guardian != null) {
                            int enemyHpBefore = guardian.getCurrentHp();
                            int damage = 69 * skillLevel[0] + zhongyuedadiHuan(enemy);
                            guardian.setCurrentHp(guardian.getCurrentHp() - damage);

                            addLog("镇妖塔",
                                    enemy.getName(), enemy.getCamp(), enemy.getPosition(),
                                    enemy.getMaxHp(), enemy.getCurrentHp(),
                                    enemy.getAttack(), enemy.getAttack(),
                                    enemy.getSpeed(), enemy.getSpeed(),
                                    guardian.getName(), guardian.getCamp(), guardian.getPosition(),
                                    guardian.getMaxHp(), guardian.getCurrentHp(),
                                    guardian.getAttack(), guardian.getAttack(),
                                    guardian.getSpeed(), guardian.getSpeed(),
                                    getFieldUnitsStatus(),
                                    damage, EffectType.MISSILE_DAMAGE, DamageType.MISSILE,
                                    enemy.getName() + "登场触发镇妖塔");
                        }
                        break;
                    case "厚土娘娘":
                        // 大地净化：驱散自身减益
                        enemy.getEffects().clear();
                        addLog("大地净化",
                                enemy.getName(), enemy.getCamp(), enemy.getPosition(),
                                enemy.getMaxHp(), enemy.getCurrentHp(),
                                enemy.getAttack(), enemy.getAttack(),
                                sourceSpeedBefore, enemy.getSpeed(),
                                enemy.getName(), enemy.getCamp(), enemy.getPosition(),
                                enemy.getMaxHp(), enemy.getCurrentHp(),
                                enemy.getAttack(), enemy.getAttack(),
                                enemy.getSpeed(), enemy.getSpeed(),
                                getFieldUnitsStatus(),
                                0, EffectType.SILENCE_IMMUNE, null,
                                guardian.getName() + "登场驱散减益");
                        break;
                    case "天蓬元帅":
                        // 满目桃花Lv1场上遇到女性敌人时，自身攻击降低50%，速度增加50%，
                        enemy.setAttack((int) (enemy.getAttack() * 0.5));
                        enemy.setSpeed((int) (enemy.getSpeed() * 1.5));
                        addLog("满目桃花",
                                enemy.getName(), enemy.getCamp(), enemy.getPosition(),
                                enemy.getMaxHp(), enemy.getCurrentHp(),
                                enemy.getAttack(), enemy.getAttack(),
                                sourceSpeedBefore, enemy.getSpeed(),
                                enemy.getName(), enemy.getCamp(), enemy.getPosition(),
                                enemy.getMaxHp(), enemy.getCurrentHp(),
                                enemy.getAttack(), enemy.getAttack(),
                                enemy.getSpeed(), enemy.getSpeed(),
                                getFieldUnitsStatus(),
                                0, EffectType.ATTACK_DOWN_SPEED_UP, null,
                                enemy.getName() + ",攻击降低50%，速度增加50%");
                        break;
                }
            }
        }


        //TODO 登场，再触发场下
        processOnFieldSkills0(guardian);


        //TODO 最后触发自己登场触发技能
        switch (guardian.getName()) {
            case "黑山老妖":
//                百毒感染Lv1登场时令敌方全体中毒，每回合损失40；
                List<Guardian> offFieldEnemies = guardian.getCamp() == Camp.A ?
                        campB.stream().filter(g -> !g.isDead()).collect(Collectors.toList()) :
                        campA.stream().filter(g -> !g.isDead()).collect(Collectors.toList());
                if (!offFieldEnemies.isEmpty()) {
                    List<String> targetNames = new ArrayList<>();
                    Map<String, Object[]> targetStatus = new HashMap<>();
                    for (Guardian g : offFieldEnemies) {
                        int poisonValue = 40 * skillLevel[0];
                        targetNames.add(g.getName());
                        int hpBefore = g.getMaxHp();
                        int poisonDamage = g.getEffects().getOrDefault(EffectType.POISON, 0);
                        g.getEffects().put(EffectType.POISON, poisonValue + poisonDamage);
                        targetStatus.put(g.getCamp() + g.getName(), new Object[]{g.getPosition(), hpBefore, g.getCurrentHp()});
                    }
                    // 单条日志记录多目标
                    addMultiTargetLog("百毒感染",
                            guardian.getName(), guardian.getCamp(), guardian.getPosition(),
                            guardian.getMaxHp(), guardian.getCurrentHp(),
                            guardian.getAttack(), guardian.getAttack(),
                            guardian.getSpeed(), guardian.getSpeed(),
                            targetNames, offFieldEnemies.get(0).getCamp(),
                            targetStatus,
                            getFieldUnitsStatus(),
                            0, EffectType.POISON, DamageType.POISON,
                            guardian.getName() + "触发百毒感染，攻击后对全体造成中毒效果");
                }
                break;
            case "大鹏金翅雕":
//                大鹏金翅雕，鹏程万里Lv1登场时提高我方全体妖界生物的速度130点
                if (1 == 1) {
                    List<Guardian> enemies = guardian.getCamp() == Camp.A ?
                            campA.stream().filter(g -> !g.isDead() && g.getRace() == Race.DEMON).collect(Collectors.toList()) :
                            campB.stream().filter(g -> !g.isDead() && g.getRace() == Race.DEMON).collect(Collectors.toList());
                    if (!enemies.isEmpty()) {
                        int speed = 130 * skillLevel[0];
                        List<String> targetNames = new ArrayList<>();
                        Map<String, Object[]> targetStatus = new HashMap<>();

                        enemies.forEach(g -> {
                            targetNames.add(g.getName());
                            int hpBefore = g.getMaxHp();
                            g.setSpeed(g.getSpeed() + speed);
                            targetStatus.put(g.getCamp() + g.getName(), new Object[]{g.getPosition(), hpBefore, g.getCurrentHp()});
                        });

                        addMultiTargetLog("鹏程万里",
                                guardian.getName(), guardian.getCamp(), guardian.getPosition(),
                                guardian.getCurrentHp(), guardian.getCurrentHp(),
                                guardian.getAttack(), guardian.getAttack(),
                                guardian.getSpeed(), guardian.getSpeed(),
                                targetNames, guardian.getCamp(),
                                targetStatus,
                                getFieldUnitsStatus(),
                                speed, EffectType.SPEED_UP, null,
                                "触发鹏程万里，提高我方全体妖界生物的速度");
                    }
                }
                break;
            case "白天君":
//
                if (1 == 1) {
                    //烈焰阵Lv1登场时，令敌方全体收到火焰伤害420点；
                    List<String> deadUnits = new ArrayList<>();
                    List<Guardian> deadGuardians = new ArrayList<>();
                    int lavaDamage = 420 * skillLevel[0];
                    List<String> targetNames = new ArrayList<>();
                    Map<String, Object[]> targetStatus = new HashMap<>();
                    List<Guardian> enemies = guardian.getCamp() == Camp.A ?
                            campB.stream().filter(g -> !g.isDead()).collect(Collectors.toList()) :
                            campA.stream().filter(g -> !g.isDead()).collect(Collectors.toList());
                    enemies.forEach(g -> {
                        targetNames.add(g.getName());
                        int hpBefore = g.getMaxHp();
                        g.setCurrentHp(g.getCurrentHp() - lavaDamage);
                        targetStatus.put(g.getCamp() + g.getName(), new Object[]{g.getPosition(), hpBefore, g.getCurrentHp()});
                        if (g.isDead()) {
                            deadUnits.add(g.getCamp() + g.getName() + "_" + g.getPosition());
                            deadGuardians.add(g);
                        }
                    });

                    // 单条日志记录多目标
                    addMultiTargetLog("烈焰阵",
                            guardian.getName(), guardian.getCamp(), guardian.getPosition(),
                            guardian.getMaxHp(), guardian.getCurrentHp(),
                            guardian.getAttack(), guardian.getAttack(),
                            guardian.getSpeed(), guardian.getSpeed(),
                            targetNames, enemies.get(0).getCamp(),
                            targetStatus,
                            getFieldUnitsStatus(),
                            lavaDamage, EffectType.FIRE_DAMAGE, DamageType.FIRE,
                            guardian.getName() + "触发烈焰阵，登场时令敌方全体收到火焰伤害");
                    // 死亡日志
                    if (!deadUnits.isEmpty()) {
                        Map<String, Object[]> deadStatus = new HashMap<>();
                        deadUnits.forEach(x -> {
                            List<String> strings = Arrays.asList(x.split("_"));
                            deadStatus.put(strings.get(0), new Object[]{Integer.parseInt(strings.get(1)), 0, 0});
                        });

                        addMultiTargetLog("UNIT_DEATH",
                                "SYSTEM", null, 0,
                                0, 0, 0, 0, 0, 0,
                                deadUnits, null,
                                deadStatus,
                                getFieldUnitsStatus(),
                                0, null, null,
                                "火焰伤害");
                        //触发死亡技能
                        for (Guardian g : deadGuardians) {
                            triggerOnDeathSkills(g);
                        }

                    }
                    //触发受击技能
                    enemies.forEach(g -> {
                        //触发受到任意伤害技能
                        triggerOnAttackedSkills(g, EffectType.FIRE_DAMAGE);
                    });
                }
                break;
            case "天狗":
                if (1 == 1) {
//                    天兆神火Lv1登场时对对方全体造成35点火焰伤害；
                    List<String> deadUnits = new ArrayList<>();
                    List<Guardian> deadGuardians = new ArrayList<>();
                    int lavaDamage = 35 * skillLevel[0];
                    List<String> targetNames = new ArrayList<>();
                    Map<String, Object[]> targetStatus = new HashMap<>();
                    List<Guardian> enemies = guardian.getCamp() == Camp.A ?
                            campB.stream().filter(g -> !g.isDead()).collect(Collectors.toList()) :
                            campA.stream().filter(g -> !g.isDead()).collect(Collectors.toList());
                    enemies.forEach(g -> {
                        targetNames.add(g.getName());
                        int hpBefore = g.getMaxHp();
                        g.setCurrentHp(g.getCurrentHp() - lavaDamage);
                        targetStatus.put(g.getCamp() + g.getName(), new Object[]{g.getPosition(), hpBefore, g.getCurrentHp()});
                        if (g.isDead()) {
                            deadUnits.add(g.getCamp() + g.getName() + "_" + g.getPosition());
                            deadGuardians.add(g);
                        }
                    });

                    // 单条日志记录多目标
                    addMultiTargetLog("天兆神火",
                            guardian.getName(), guardian.getCamp(), guardian.getPosition(),
                            guardian.getMaxHp(), guardian.getCurrentHp(),
                            guardian.getAttack(), guardian.getAttack(),
                            guardian.getSpeed(), guardian.getSpeed(),
                            targetNames, enemies.get(0).getCamp(),
                            targetStatus,
                            getFieldUnitsStatus(),
                            lavaDamage, EffectType.FIRE_DAMAGE, DamageType.FIRE,
                            guardian.getName() + "登场时对对方全体造成点火焰伤害");
                    // 死亡日志
                    if (!deadUnits.isEmpty()) {
                        Map<String, Object[]> deadStatus = new HashMap<>();
                        deadUnits.forEach(x -> {
                            List<String> strings = Arrays.asList(x.split("_"));
                            deadStatus.put(strings.get(0), new Object[]{Integer.parseInt(strings.get(1)), 0, 0});
                        });

                        addMultiTargetLog("UNIT_DEATH",
                                "SYSTEM", null, 0,
                                0, 0, 0, 0, 0, 0,
                                deadUnits, null,
                                deadStatus,
                                getFieldUnitsStatus(),
                                0, null, null,
                                "火焰伤害");
                        //触发死亡技能
                        for (Guardian g : deadGuardians) {
                            triggerOnDeathSkills(g);
                        }

                    }
                    //触发受击技能
                    enemies.forEach(g -> {
                        //触发受到任意伤害技能
                        triggerOnAttackedSkills(g, EffectType.FIRE_DAMAGE);
                    });
                }
                break;
            case "太乙真人":
                // ，禁术咒Lv1登场时,让对面场上英雄沉默99回合；
                if (1 == 1) {
                    Guardian enemy = guardian.getCamp() == Camp.A ? fieldB : fieldA;
                    if (enemy != null) {
                        int enemyHpBefore = enemy.getCurrentHp();
//                    int damage = 69 * skillLevel;
//                    enemy.setCurrentHp(enemy.getCurrentHp() - damage);
                        enemy.getEffects().put(EffectType.SILENCE, 99);
                        addLog("禁术咒",
                                guardian.getName(), guardian.getCamp(), guardian.getPosition(),
                                sourceHpBefore, guardian.getCurrentHp(),
                                sourceAttackBefore, guardian.getAttack(),
                                sourceSpeedBefore, guardian.getSpeed(),
                                enemy.getName(), enemy.getCamp(), enemy.getPosition(),
                                enemyHpBefore, enemy.getCurrentHp(),
                                enemy.getAttack(), enemy.getAttack(),
                                enemy.getSpeed(), enemy.getSpeed(),
                                getFieldUnitsStatus(),
                                0, EffectType.SILENCE, DamageType.MAGIC,
                                guardian.getName() + "登场触发禁术咒");
                    }
                }
                break;
            case "紫薇大帝":
                // ，北极剑意Lv1登场时对场上敌方造成最大生命4%的真实伤害；
                if (1 == 1) {
                    Guardian enemy = guardian.getCamp() == Camp.A ? fieldB : fieldA;
                    if (enemy != null) {
                        int enemyHpBefore = enemy.getCurrentHp();
                        int damage = (int) (enemy.getCurrentHp() * 0.04 * skillLevel[0]);
                        enemy.setCurrentHp(enemy.getCurrentHp() - damage);
                        addLog("北极剑意",
                                guardian.getName(), guardian.getCamp(), guardian.getPosition(),
                                sourceHpBefore, guardian.getCurrentHp(),
                                sourceAttackBefore, guardian.getAttack(),
                                sourceSpeedBefore, guardian.getSpeed(),
                                enemy.getName(), enemy.getCamp(), enemy.getPosition(),
                                enemyHpBefore, enemy.getCurrentHp(),
                                enemy.getAttack(), enemy.getAttack(),
                                enemy.getSpeed(), enemy.getSpeed(),
                                getFieldUnitsStatus(),
                                damage, EffectType.TRUE_DAMAGE, DamageType.MAGIC,
                                guardian.getName() + "登场触发北极剑意");
                    }
                }
                break;
            case "齐天大圣":
                // 大圣降临：回复自身20%生命
                int heal = (int) (guardian.getMaxHp() * 0.2 * skillLevel[0]);
                guardian.setCurrentHp(guardian.getCurrentHp() + heal);

                addLog("大圣降临",
                        guardian.getName(), guardian.getCamp(), guardian.getPosition(),
                        sourceHpBefore, guardian.getCurrentHp(),
                        sourceAttackBefore, guardian.getAttack(),
                        sourceSpeedBefore, guardian.getSpeed(),
                        guardian.getName(), guardian.getCamp(), guardian.getPosition(),
                        sourceHpBefore, guardian.getCurrentHp(),
                        sourceAttackBefore, guardian.getAttack(),
                        sourceSpeedBefore, guardian.getSpeed(),
                        getFieldUnitsStatus(),
                        heal, EffectType.HEAL, null,
                        guardian.getName() + "登场触发大圣降临");
                break;

            case "烛龙":
                // 致命衰竭：登场目标攻击减少10%
                if (skillLevel[1] > 0) {
                    Guardian target = guardian.getCamp() == Camp.A ? fieldB : fieldA;
                    if (target != null) {
                        int targetAttackBefore = target.getAttack();
                        int weaken = (int) (target.getAttack() * 0.1 * skillLevel[1]);
                        target.setAttack(target.getAttack() - weaken);

                        addLog("致命衰竭",
                                guardian.getName(), guardian.getCamp(), guardian.getPosition(),
                                sourceHpBefore, guardian.getCurrentHp(),
                                sourceAttackBefore, guardian.getAttack(),
                                sourceSpeedBefore, guardian.getSpeed(),
                                target.getName(), target.getCamp(), target.getPosition(),
                                target.getCurrentHp(), target.getCurrentHp(),
                                targetAttackBefore, target.getAttack(),
                                target.getSpeed(), target.getSpeed(),
                                getFieldUnitsStatus(),
                                weaken, EffectType.WEAKEN, null,
                                target.getName() + "攻击降低" + skillLevel + "0%");
                    }
                }
                break;
        }
    }

    // 触发攻击前技能
    private void triggerPreAttackSkills(Guardian attacker, Guardian defender) {
        int attackerHpBefore = attacker.getMaxHp();
        int attackerAttackBefore = attacker.getAttack();
        int attackerSpeedBefore = attacker.getSpeed();
        int[] skillLevel = CardSkillLevelUtil.calculateSkillLevels(attacker.getLevel(), attacker.getStar().doubleValue());
        if (Xtool.isNotNull(attacker.getEffects().get(EffectType.SILENCE))) {
            return;
        }
        switch (attacker.getName()) {
            case "齐天大圣":
                // 定海神针：当前生命值6%伤害
                if (skillLevel[1] > 0) {
                    int defenderHpBefore = defender.getCurrentHp();
                    int damage = (int) (defender.getCurrentHp() * 0.06 * skillLevel[1]);
                    defender.setCurrentHp(defender.getCurrentHp() - damage);

                    addLog("定海神针",
                            attacker.getName(), attacker.getCamp(), attacker.getPosition(),
                            attackerHpBefore, attacker.getCurrentHp(),
                            attackerAttackBefore, attacker.getAttack(),
                            attackerSpeedBefore, attacker.getSpeed(),
                            defender.getName(), defender.getCamp(), defender.getPosition(),
                            defenderHpBefore, defender.getCurrentHp(),
                            defender.getAttack(), defender.getAttack(),
                            defender.getSpeed(), defender.getSpeed(),
                            getFieldUnitsStatus(),
                            damage, EffectType.TRUE_DAMAGE, DamageType.TRUE,
                            attacker.getName() + "触发定海神针");
                }
                break;

            case "刑天":
                // 斩杀：13%几率造成火焰伤害
                if (random.nextDouble() < 0.13 * skillLevel[0]) {
                    int defenderHpBefore = defender.getCurrentHp();
                    int burnDamage = 220 * skillLevel[0];
                    defender.setCurrentHp(defender.getCurrentHp() - burnDamage);
                    // 武圣判定
                    List<String> deadUnits = new ArrayList<>();
//                    如果目标是武圣则有几率一击必杀，替代普通攻击；0.05; // 对武圣5%一击必杀（可配置）
                    if (defender.getProfession() == Profession.WARRIOR) {
                        if (random.nextDouble() <= 0.05) {
                            defender.setCurrentHp(0);
                            defender.setDead(true);
                            defender.setOnField(false);
                            deadUnits.add(defender.getCamp() + defender.getName() + "_" + defender.getPosition());
                        }
                    }
                    addLog("斩杀",
                            attacker.getName(), attacker.getCamp(), attacker.getPosition(),
                            attackerHpBefore, attacker.getCurrentHp(),
                            attackerAttackBefore, attacker.getAttack(),
                            attackerSpeedBefore, attacker.getSpeed(),
                            defender.getName(), defender.getCamp(), defender.getPosition(),
                            defenderHpBefore, defender.getCurrentHp(),
                            defender.getAttack(), defender.getAttack(),
                            defender.getSpeed(), defender.getSpeed(),
                            getFieldUnitsStatus(),
                            burnDamage, EffectType.FIRE_DAMAGE, DamageType.FIRE,
                            attacker.getName() + "触发斩杀");
                    // 死亡日志
                    if (!deadUnits.isEmpty()) {
                        Map<String, Object[]> deadStatus = new HashMap<>();
                        deadUnits.forEach(x -> {
                            List<String> strings = Arrays.asList(x.split("_"));
                            deadStatus.put(strings.get(0), new Object[]{Integer.parseInt(strings.get(1)), 0, 0});
                        });

                        addMultiTargetLog("UNIT_DEATH",
                                "SYSTEM", null, 0,
                                0, 0, 0, 0, 0, 0,
                                deadUnits, null,
                                deadStatus,
                                getFieldUnitsStatus(),
                                0, null, null,
                                "斩杀");
                        //触发死亡技能
                        triggerOnDeathSkills(defender);

                    }
                }
                break;
            case "大鹏金翅雕":
                // 斩杀：13%几率造成火焰伤害
                if (skillLevel[1] > 0) {
                    if (random.nextDouble() < 0.75) {
                        int defenderHpBefore = defender.getCurrentHp();
                        int burnDamage = 220 * skillLevel[1];
                        defender.setCurrentHp(defender.getCurrentHp() - burnDamage);
                        // 武圣判定
                        List<String> deadUnits = new ArrayList<>();
//                    如果目标是武圣则有几率一击必杀，替代普通攻击；0.05; // 对武圣5%一击必杀（可配置）
                        if (defender.getProfession() == Profession.GOD) {
                            if (random.nextDouble() <= 0.05) {
                                defender.setCurrentHp(0);
                                defender.setDead(true);
                                defender.setOnField(false);
                                deadUnits.add(defender.getCamp() + defender.getName() + "_" + defender.getPosition());
                            }
                        }
                        addLog("屠杀",
                                attacker.getName(), attacker.getCamp(), attacker.getPosition(),
                                attackerHpBefore, attacker.getCurrentHp(),
                                attackerAttackBefore, attacker.getAttack(),
                                attackerSpeedBefore, attacker.getSpeed(),
                                defender.getName(), defender.getCamp(), defender.getPosition(),
                                defenderHpBefore, defender.getCurrentHp(),
                                defender.getAttack(), defender.getAttack(),
                                defender.getSpeed(), defender.getSpeed(),
                                getFieldUnitsStatus(),
                                burnDamage, EffectType.FIRE_DAMAGE, DamageType.FIRE,
                                attacker.getName() + "触发屠杀");
                        // 死亡日志
                        if (!deadUnits.isEmpty()) {
                            Map<String, Object[]> deadStatus = new HashMap<>();
                            deadUnits.forEach(x -> {
                                List<String> strings = Arrays.asList(x.split("_"));
                                deadStatus.put(strings.get(0), new Object[]{Integer.parseInt(strings.get(1)), 0, 0});
                            });

                            addMultiTargetLog("UNIT_DEATH",
                                    "SYSTEM", null, 0,
                                    0, 0, 0, 0, 0, 0,
                                    deadUnits, null,
                                    deadStatus,
                                    getFieldUnitsStatus(),
                                    0, null, null,
                                    "屠杀");
                            //触发死亡技能
                            triggerOnDeathSkills(defender);

                        }
                    }
                }
                break;
        }
    }

    // 触发受击技能
    private void triggerOnAttackedSkills(Guardian defender, Guardian attacker) {
        int defenderHpBefore = defender.getMaxHp();
        int defenderAttackBefore = defender.getAttack();
        int defenderSpeedBefore = defender.getSpeed();
        int[] skillLevel = CardSkillLevelUtil.calculateSkillLevels(defender.getLevel(), defender.getStar().doubleValue());
        if (Xtool.isNotNull(defender.getEffects().get(EffectType.SILENCE))) {
            return;
        }
        switch (defender.getName()) {
            case "瑶池仙女":
                // 长生大帝生生不息（多目标整合）
                if (!defender.isDead()) {
                    List<Guardian> enemies = defender.getCamp() == Camp.A ? campA : campB;
                    if (!enemies.isEmpty()) {
                        List<Guardian> immortalAllies = enemies.stream()
                                .filter(g -> !g.getName().equals("瑶池仙女") && g.getRace() == Race.IMMORTAL && !g.isDead())
                                .collect(Collectors.toList());

                        if (!immortalAllies.isEmpty()) {
                            int heal = 90 * skillLevel[0];
                            List<String> targetNames = new ArrayList<>();
                            Map<String, Object[]> targetStatus = new HashMap<>();

                            immortalAllies.forEach(g -> {
                                targetNames.add(g.getName());
                                int hpBefore = g.getMaxHp();
                                g.setCurrentHp(g.getCurrentHp() + heal);
                                targetStatus.put(g.getCamp() + g.getName(), new Object[]{g.getPosition(), hpBefore, g.getCurrentHp()});
                            });

                            addMultiTargetLog("瑶池仙露",
                                    defender.getName(), defender.getCamp(), defender.getPosition(),
                                    defender.getCurrentHp(), defender.getCurrentHp(),
                                    defender.getAttack(), defender.getAttack(),
                                    defender.getSpeed(), defender.getSpeed(),
                                    targetNames, defender.getCamp(),
                                    targetStatus,
                                    getFieldUnitsStatus(),
                                    heal, EffectType.HEAL, null,
                                    "瑶池仙女触发瑶池仙露，治疗我方仙界单位");
                            immortalAllies.forEach(g -> {
                                triggerOnHelSkills(g);
                            });
                        }
                    }
                }
                break;
            case "杨戬":
                if (skillLevel[1] > 0) {
                    // 顽强战意Lv1受到普通攻击时，提高自身生命上限141点，最多叠加20层；
                    if (!defender.isDead() && defender.getBuffStacks() < 20) {
                        defender.setBuffStacks(defender.getBuffStacks() + 1);
                        int hel = 0;
                        if (duoBaoGuanHuan()) {
                            hel = 141 * skillLevel[1];
                        }
                        defender.setMaxHp(defender.getMaxHp() + hel);
                        defender.setCurrentHp(defender.getCurrentHp() + hel);

                        addLog("顽强战意",
                                defender.getName(), defender.getCamp(), defender.getPosition(),
                                defender.getMaxHp(), defender.getCurrentHp(),
                                defender.getAttack(), defender.getAttack(),
                                defender.getSpeed(), defender.getSpeed(),
                                defender.getName(), defender.getCamp(), defender.getPosition(),
                                defender.getMaxHp(), defender.getCurrentHp(),
                                defender.getAttack(), defender.getAttack(),
                                defender.getSpeed(), defender.getSpeed(),
                                getFieldUnitsStatus(),
                                hel, EffectType.HP_UP, null,
                                defender.getName() + "触发顽强战意，生命上限+" + hel);

                    }
                }
                break;
            case "圣婴大王":
                if (skillLevel[1] > 0) {
                    // 顽强战意Lv1受到普通攻击时，提高自身生命上限141点，最多叠加20层；
                    if (!defender.isDead() && defender.getBuffStacks() < 99) {
                        defender.setBuffStacks(defender.getBuffStacks() + 1);
                        addLog("赤炎臂膀",
                                defender.getName(), defender.getCamp(), defender.getPosition(),
                                defender.getMaxHp(), defender.getCurrentHp(),
                                defender.getAttack(), defender.getAttack(),
                                defender.getSpeed(), defender.getSpeed(),
                                defender.getName(), defender.getCamp(), defender.getPosition(),
                                defender.getMaxHp(), defender.getCurrentHp(),
                                defender.getAttack(), defender.getAttack(),
                                defender.getSpeed(), defender.getSpeed(),
                                getFieldUnitsStatus(),
                                0, EffectType.HP_UP, null,
                                defender.getName() + "触发赤炎臂膀，火焰伤害+" + skillLevel[1]*106);

                    }
                }
                break;
            case "聂小倩":
                // 幽灵毒击：令攻击者中毒
                if (!defender.isDead()) {
                    int attackerHpBefore = attacker.getCurrentHp();
                    int poisonValue = 8 * skillLevel[0];
                    int poisonDamage = attacker.getEffects().getOrDefault(EffectType.POISON, 0);
                    attacker.getEffects().put(EffectType.POISON, poisonValue + poisonDamage);

                    addLog("幽灵毒击",
                            defender.getName(), defender.getCamp(), defender.getPosition(),
                            defenderHpBefore, defender.getCurrentHp(),
                            defenderAttackBefore, defender.getAttack(),
                            defenderSpeedBefore, defender.getSpeed(),
                            attacker.getName(), attacker.getCamp(), attacker.getPosition(),
                            attackerHpBefore, attacker.getCurrentHp(),
                            attacker.getAttack(), attacker.getAttack(),
                            attacker.getSpeed(), attacker.getSpeed(),
                            getFieldUnitsStatus(),
                            poisonValue, EffectType.POISON, DamageType.POISON,
                            defender.getName() + "触发幽灵毒击");
                }
                break;
            case "黄牙老象":
                // 幽灵毒击：令攻击者中毒
                if (!defender.isDead()) {
                    int attackerHpBefore = attacker.getCurrentHp();
                    int poisonValue = 8 * skillLevel[0];
                    int poisonDamage = attacker.getEffects().getOrDefault(EffectType.POISON, 0);
                    attacker.getEffects().put(EffectType.POISON, poisonValue + poisonDamage);

                    addLog("幽灵毒击",
                            defender.getName(), defender.getCamp(), defender.getPosition(),
                            defenderHpBefore, defender.getCurrentHp(),
                            defenderAttackBefore, defender.getAttack(),
                            defenderSpeedBefore, defender.getSpeed(),
                            attacker.getName(), attacker.getCamp(), attacker.getPosition(),
                            attackerHpBefore, attacker.getCurrentHp(),
                            attacker.getAttack(), attacker.getAttack(),
                            attacker.getSpeed(), attacker.getSpeed(),
                            getFieldUnitsStatus(),
                            poisonValue, EffectType.POISON, DamageType.POISON,
                            defender.getName() + "触发幽灵毒击");
                }
                break;
            case "白眼魔君":
                // 幽灵毒击：令攻击者中毒
                if (!defender.isDead()) {
                    int attackerHpBefore = attacker.getCurrentHp();
                    int poisonValue = 8 * skillLevel[0];
                    int poisonDamage = attacker.getEffects().getOrDefault(EffectType.POISON, 0);
                    attacker.getEffects().put(EffectType.POISON, poisonValue + poisonDamage);

                    addLog("幽灵毒击",
                            defender.getName(), defender.getCamp(), defender.getPosition(),
                            defenderHpBefore, defender.getCurrentHp(),
                            defenderAttackBefore, defender.getAttack(),
                            defenderSpeedBefore, defender.getSpeed(),
                            attacker.getName(), attacker.getCamp(), attacker.getPosition(),
                            attackerHpBefore, attacker.getCurrentHp(),
                            attacker.getAttack(), attacker.getAttack(),
                            attacker.getSpeed(), attacker.getSpeed(),
                            getFieldUnitsStatus(),
                            poisonValue, EffectType.POISON, DamageType.POISON,
                            defender.getName() + "触发幽灵毒击");
                }
                break;

            case "烛龙":
                // 烛火燎原：对全体敌方造成火焰伤害（多目标整合日志）
                if (!defender.isDead()) {
                    List<Guardian> enemies = defender.getCamp() == Camp.A ? campB : campA;
                    List<Guardian> guardians = defender.getCamp() == Camp.A ? campA : campB;
                    List<Guardian> juMan = guardians.stream().filter(x -> "句芒".equals(x.getName())).collect(Collectors.toList());
                    List<Guardian> aliveEnemies = enemies.stream()
                            .filter(g -> !g.isDead())
                            .collect(Collectors.toList());

                    if (!aliveEnemies.isEmpty()) {
                        Integer addFire = 0;
                        //协同火焰加成
                        if (Xtool.isNotNull(juMan)) {
                            addFire = 99 * skillLevel[0];
                        }
                        int fireDamage = 54 * skillLevel[0] + addFire;
                        List<String> targetNames = new ArrayList<>();
                        Map<String, Object[]> targetStatus = new HashMap<>();

                        // 记录目标状态并执行伤害
                        aliveEnemies.forEach(g -> {
                            targetNames.add(g.getName());
                            int hpBefore = g.getMaxHp();
                            g.setCurrentHp(g.getCurrentHp() - fireDamage);
                            targetStatus.put(g.getCamp() + g.getName(), new Object[]{g.getPosition(), hpBefore, g.getCurrentHp()});
                        });

                        // 单条日志记录多目标
                        addMultiTargetLog("烛火燎原",
                                defender.getName(), defender.getCamp(), defender.getPosition(),
                                defenderHpBefore, defender.getCurrentHp(),
                                defenderAttackBefore, defender.getAttack(),
                                defenderSpeedBefore, defender.getSpeed(),
                                targetNames, defender.getCamp() == Camp.A ? Camp.B : Camp.A,
                                targetStatus,
                                getFieldUnitsStatus(),
                                fireDamage, EffectType.FIRE_DAMAGE, DamageType.FIRE,
                                defender.getName() + "触发烛火燎原，对全体敌方造成火焰伤害");
                    }
                }
                break;
            case "萌年兽":
                // 烛火燎原：对全体敌方造成火焰伤害（多目标整合日志）
                if (!defender.isDead() && skillLevel[1] > 0) {
                    List<Guardian> enemies = defender.getCamp() == Camp.A ? campB : campA;
                    List<Guardian> aliveEnemies = enemies.stream()
                            .filter(g -> !g.isDead())
                            .collect(Collectors.toList());

                    if (!aliveEnemies.isEmpty()) {
                        int fireDamage = 54 * skillLevel[1];
                        List<String> targetNames = new ArrayList<>();
                        Map<String, Object[]> targetStatus = new HashMap<>();

                        // 记录目标状态并执行伤害
                        aliveEnemies.forEach(g -> {
                            targetNames.add(g.getName());
                            int hpBefore = g.getMaxHp();
                            g.setCurrentHp(g.getCurrentHp() - fireDamage);
                            targetStatus.put(g.getCamp() + g.getName(), new Object[]{g.getPosition(), hpBefore, g.getCurrentHp()});
                        });

                        // 单条日志记录多目标
                        addMultiTargetLog("爆竹送给你",
                                defender.getName(), defender.getCamp(), defender.getPosition(),
                                defenderHpBefore, defender.getCurrentHp(),
                                defenderAttackBefore, defender.getAttack(),
                                defenderSpeedBefore, defender.getSpeed(),
                                targetNames, defender.getCamp() == Camp.A ? Camp.B : Camp.A,
                                targetStatus,
                                getFieldUnitsStatus(),
                                fireDamage, EffectType.FIRE_DAMAGE, DamageType.FIRE,
                                defender.getName() + "触发爆竹送给你，对全体敌方造成火焰伤害");
                    }
                }
                break;
            case "将臣":
                if (1 == 1) {
                    // 剧毒皮肤：令随机敌方中毒
                    List<Guardian> enemies = defender.getCamp() == Camp.A ?
                            campB.stream().filter(g -> !g.isDead()).collect(Collectors.toList()) :
                            campA.stream().filter(g -> !g.isDead()).collect(Collectors.toList());
                    if (!enemies.isEmpty()) {
                        Guardian randomEnemy = enemies.get(random.nextInt(enemies.size()));
                        int enemyHpBefore = randomEnemy.getCurrentHp();
                        int poisonValue = 73 * skillLevel[0];
                        int poisonDamage = randomEnemy.getEffects().getOrDefault(EffectType.POISON, 0);
                        randomEnemy.getEffects().put(EffectType.POISON, poisonValue + poisonDamage);

                        addLog("剧毒皮肤",
                                defender.getName(), defender.getCamp(), defender.getPosition(),
                                defenderHpBefore, defender.getCurrentHp(),
                                defenderAttackBefore, defender.getAttack(),
                                defenderSpeedBefore, defender.getSpeed(),
                                randomEnemy.getName(), randomEnemy.getCamp(), randomEnemy.getPosition(),
                                enemyHpBefore, randomEnemy.getCurrentHp(),
                                randomEnemy.getAttack(), randomEnemy.getAttack(),
                                randomEnemy.getSpeed(), randomEnemy.getSpeed(),
                                getFieldUnitsStatus(),
                                poisonValue, EffectType.POISON, DamageType.POISON,
                                defender.getName() + "触发剧毒皮肤");
                    }
                }
                break;
            case "辟寒大王":
                if (1 == 1) {
                    // 剧毒皮肤：令随机敌方中毒
                    List<Guardian> enemies = defender.getCamp() == Camp.A ?
                            campB.stream().filter(g -> !g.isDead()).collect(Collectors.toList()) :
                            campA.stream().filter(g -> !g.isDead()).collect(Collectors.toList());
                    if (!enemies.isEmpty()) {
                        Guardian randomEnemy = enemies.get(random.nextInt(enemies.size()));
                        int enemyHpBefore = randomEnemy.getCurrentHp();
                        int poisonValue = 8 * skillLevel[0];
                        int poisonDamage = randomEnemy.getEffects().getOrDefault(EffectType.POISON, 0);
                        randomEnemy.getEffects().put(EffectType.POISON, poisonValue + poisonDamage);

                        addLog("毒素反击",
                                defender.getName(), defender.getCamp(), defender.getPosition(),
                                defenderHpBefore, defender.getCurrentHp(),
                                defenderAttackBefore, defender.getAttack(),
                                defenderSpeedBefore, defender.getSpeed(),
                                randomEnemy.getName(), randomEnemy.getCamp(), randomEnemy.getPosition(),
                                enemyHpBefore, randomEnemy.getCurrentHp(),
                                randomEnemy.getAttack(), randomEnemy.getAttack(),
                                randomEnemy.getSpeed(), randomEnemy.getSpeed(),
                                getFieldUnitsStatus(),
                                poisonValue, EffectType.POISON, DamageType.POISON,
                                defender.getName() + "触发毒素反击");
                    }
                }
                break;
            case "句芒":
                // 句芒，残酷收割Lv1每当有生物死亡时，回复自身6%最大生命；嗜血Lv1受到普通攻击时，为自身添加嗜血效果，提高攻击118点，速度20点；大圣协同Lv1与齐天大圣在同一队伍时，增加自身453点生命上限，158点攻击，158点速度。
                if (!defender.isDead() && skillLevel[1] > 0) {
                    defender.setAttack(defender.getAttack() + 118 * skillLevel[1]);
                    defender.setSpeed(defender.getSpeed() + 20 * skillLevel[1]);
                    addLog("嗜血",
                            defender.getName(), defender.getCamp(), defender.getPosition(),
                            defenderHpBefore, defender.getCurrentHp(),
                            defenderAttackBefore, defender.getAttack(),
                            defenderSpeedBefore, defender.getSpeed(),
                            defender.getName(), defender.getCamp(), defender.getPosition(),
                            defenderHpBefore, defender.getCurrentHp(),
                            defender.getAttack(), defender.getAttack(),
                            defender.getSpeed(), defender.getSpeed(),
                            getFieldUnitsStatus(),
                            118, EffectType.ATTACK_SPEED_UP, null,
                            defender.getName() + "触发嗜血");
                }
                break;
            case "刑天":
                // 刑天，斩杀Lv1普通攻击有13%几率对目标造成220点火焰伤害，如果目标是武圣则有几率一击必杀，替代普通攻击；嗜血Lv1受到普通攻击时，为自身添加嗜血效果，提高攻击118点，速度20点；金翅雕协同Lv1与大鹏金翅雕在同一队伍时，增加自身453点生命上限，158点攻击，158点速度                if (!defender.isDead()) {
                if (!defender.isDead() && skillLevel[1] > 0) {
                    defender.setSpeed(defender.getSpeed() + 20 * skillLevel[1]);
                    defender.setAttack(defender.getAttack() + 118 * skillLevel[1]);
                    addLog("嗜血",
                            defender.getName(), defender.getCamp(), defender.getPosition(),
                            defenderHpBefore, defender.getCurrentHp(),
                            defenderAttackBefore, defender.getAttack(),
                            defenderSpeedBefore, defender.getSpeed(),
                            defender.getName(), defender.getCamp(), defender.getPosition(),
                            defenderHpBefore, defender.getCurrentHp(),
                            defender.getAttack(), defender.getAttack(),
                            defender.getSpeed(), defender.getSpeed(),
                            getFieldUnitsStatus(),
                            118, EffectType.ATTACK_SPEED_UP, null,
                            defender.getName() + "触发嗜血");
                }
                break;

            case "真武大帝":
                // 绝地反击：造成敌方攻击10%的伤害
                if (!defender.isDead()) {
                    int counterDamage = (int) (attacker.getAttack() * 0.1 * skillLevel[1]);
                    attacker.setCurrentHp(attacker.getCurrentHp() - counterDamage);

                    addLog("绝地反击",
                            defender.getName(), defender.getCamp(), defender.getPosition(),
                            defenderHpBefore, defender.getCurrentHp(),
                            defenderAttackBefore, defender.getAttack(),
                            defenderSpeedBefore, defender.getSpeed(),
                            attacker.getName(), attacker.getCamp(), attacker.getPosition(),
                            attacker.getMaxHp(), attacker.getCurrentHp(),
                            attacker.getAttack(), attacker.getAttack(),
                            attacker.getSpeed(), attacker.getSpeed(),
                            getFieldUnitsStatus(),
                            counterDamage, EffectType.DAMAGE, DamageType.TRUE,
                            defender.getName() + "触发绝地反击");
                    List<String> deadUnits = new ArrayList<>();

                    if (attacker.getCurrentHp() <= 0) {
                        attacker.setDead(true);
                        attacker.setOnField(false);
                        deadUnits.add(attacker.getCamp() + attacker.getName() + "_" + attacker.getPosition());
                    }
                    // 死亡日志
                    if (!deadUnits.isEmpty()) {
                        Map<String, Object[]> deadStatus = new HashMap<>();
                        deadUnits.forEach(x -> {
                            List<String> strings = Arrays.asList(x.split("_"));
                            deadStatus.put(strings.get(0), new Object[]{Integer.parseInt(strings.get(1)), 0, 0});
                        });

                        addMultiTargetLog("UNIT_DEATH",
                                "SYSTEM", null, 0,
                                0, 0, 0, 0, 0, 0,
                                deadUnits, null,
                                deadStatus,
                                getFieldUnitsStatus(),
                                0, null, null,
                                "飞弹伤害");
                        //触发死亡技能
                        triggerOnDeathSkills(attacker);

                    } else {
                        //触发受击技能
                        triggerOnAttackedSkills(attacker, defender);
                    }

                }
                break;
        }
    }

    // 受到任意治疗触发技能
    private void triggerOnHelSkills(Guardian defender) {
        //东岳大帝，大帝威慑Lv1普通攻击降低敌方的攻击20点，最多叠加3层；圣灵瀑Lv1场上，每当受到治疗时，对场上敌方造成146点飞弹伤害；大帝协同Lv1与南岳大帝在同一队伍时，增加自身423点生命上限，141点攻击，150点速度。
        int defenderHpBefore = defender.getMaxHp();
        int defenderAttackBefore = defender.getAttack();
        int defenderSpeedBefore = defender.getSpeed();
        if (Xtool.isNotNull(defender.getEffects().get(EffectType.SILENCE))) {
            return;
        }
        int[] skillLevel = CardSkillLevelUtil.calculateSkillLevels(defender.getLevel(), defender.getStar().doubleValue());
        switch (defender.getName()) {
            case "东岳大帝":
                if (defender.isOnField() && !defender.isDead() && skillLevel[1] > 0) {
                    // 镇妖塔：对敌方场上造成飞弹伤害
                    Guardian enemy = defender.getCamp() == Camp.A ? fieldB : fieldA;
                    if (enemy != null && !enemy.isDead()) {
                        int enemyHpBefore = enemy.getCurrentHp();
                        int damage = 69 * skillLevel[1] + zhongyuedadiHuan(defender);
                        enemy.setCurrentHp(enemy.getCurrentHp() - damage);
                        List<String> deadUnits = new ArrayList<>();

                        if (enemy.getCurrentHp() <= 0) {
                            enemy.setDead(true);
                            enemy.setOnField(false);
                            deadUnits.add(enemy.getCamp() + enemy.getName() + "_" + enemy.getPosition());
                        }
                        addLog("圣灵瀑",
                                defender.getName(), defender.getCamp(), defender.getPosition(),
                                defenderHpBefore, defender.getCurrentHp(),
                                defenderAttackBefore, defender.getAttack(),
                                defenderSpeedBefore, defender.getSpeed(),
                                enemy.getName(), enemy.getCamp(), enemy.getPosition(),
                                enemyHpBefore, enemy.getCurrentHp(),
                                enemy.getAttack(), enemy.getAttack(),
                                enemy.getSpeed(), enemy.getSpeed(),
                                getFieldUnitsStatus(),
                                damage, EffectType.MISSILE_DAMAGE, DamageType.MISSILE,
                                defender.getName() + "治疗触发圣灵瀑");
                        // 死亡日志
                        if (!deadUnits.isEmpty()) {
                            Map<String, Object[]> deadStatus = new HashMap<>();
                            deadUnits.forEach(x -> {
                                List<String> strings = Arrays.asList(x.split("_"));
                                deadStatus.put(strings.get(0), new Object[]{Integer.parseInt(strings.get(1)), 0, 0});
                            });

                            addMultiTargetLog("UNIT_DEATH",
                                    "SYSTEM", null, 0,
                                    0, 0, 0, 0, 0, 0,
                                    deadUnits, null,
                                    deadStatus,
                                    getFieldUnitsStatus(),
                                    0, null, null,
                                    "飞弹伤害");
                            //触发死亡技能
                            triggerOnDeathSkills(enemy);

                        } else {
                            //触发受击技能
                            triggerOnAttackedSkills(enemy, EffectType.MISSILE_DAMAGE);
                        }
                    }
                }
                break;
            case "驱魔真君":
                if (!defender.isDead()) {
//                    圣灵泉涌Lv1受到治疗时，对场上敌方造成32点飞弹伤害；顽强体魄Lv1受到治疗的效果提升10%。
                    Guardian enemy = defender.getCamp() == Camp.A ? fieldB : fieldA;
                    if (enemy != null && !enemy.isDead()) {
                        int enemyHpBefore = enemy.getCurrentHp();
                        int damage = 32 * skillLevel[0] + zhongyuedadiHuan(defender);
                        enemy.setCurrentHp(enemy.getCurrentHp() - damage);
                        List<String> deadUnits = new ArrayList<>();

                        if (enemy.getCurrentHp() <= 0) {
                            enemy.setDead(true);
                            enemy.setOnField(false);
                            deadUnits.add(enemy.getCamp() + enemy.getName() + "_" + enemy.getPosition());
                        }
                        addLog("圣灵泉涌",
                                defender.getName(), defender.getCamp(), defender.getPosition(),
                                defenderHpBefore, defender.getCurrentHp(),
                                defenderAttackBefore, defender.getAttack(),
                                defenderSpeedBefore, defender.getSpeed(),
                                enemy.getName(), enemy.getCamp(), enemy.getPosition(),
                                enemyHpBefore, enemy.getCurrentHp(),
                                enemy.getAttack(), enemy.getAttack(),
                                enemy.getSpeed(), enemy.getSpeed(),
                                getFieldUnitsStatus(),
                                damage, EffectType.MISSILE_DAMAGE, DamageType.MISSILE,
                                defender.getName() + "治疗触发圣灵泉涌");
                        // 死亡日志
                        if (!deadUnits.isEmpty()) {
                            Map<String, Object[]> deadStatus = new HashMap<>();
                            deadUnits.forEach(x -> {
                                List<String> strings = Arrays.asList(x.split("_"));
                                deadStatus.put(strings.get(0), new Object[]{Integer.parseInt(strings.get(1)), 0, 0});
                            });

                            addMultiTargetLog("UNIT_DEATH",
                                    "SYSTEM", null, 0,
                                    0, 0, 0, 0, 0, 0,
                                    deadUnits, null,
                                    deadStatus,
                                    getFieldUnitsStatus(),
                                    0, null, null,
                                    "飞弹伤害");
                            //触发死亡技能
                            triggerOnDeathSkills(enemy);

                        } else {
                            //触发受击技能
                            triggerOnAttackedSkills(enemy, EffectType.MISSILE_DAMAGE);
                        }

                    }
                }
                break;
        }
    }

    // 受到任意技能伤害触发技能
    private void triggerOnAttackedSkills(Guardian defender,EffectType effectType) {
        int[] skillLevel = CardSkillLevelUtil.calculateSkillLevels(defender.getLevel(), defender.getStar().doubleValue());
//        烛龙，烛火燎原Lv1受到任意伤害时对全体敌方造成54点火焰伤害；致命衰竭Lv1场上，有单位登场时为目标添加衰弱状态，攻击减少10%，持续99回合；句芒协同Lv1与句芒在同一队伍时增加自身197点生命上限，99点火焰伤害，197点速度。
        int defenderHpBefore = defender.getMaxHp();
        int defenderAttackBefore = defender.getAttack();
        int defenderSpeedBefore = defender.getSpeed();
        if (Xtool.isNotNull(defender.getEffects().get(EffectType.SILENCE))) {
            return;
        }
        switch (defender.getName()) {
            case "烛龙":
                // 烛火燎原：对全体敌方造成火焰伤害（多目标整合日志）
                //烛龙必须在场
                if (defender.isOnField() && !defender.isDead()) {
                    List<Guardian> enemies = defender.getCamp() == Camp.A ? campB : campA;
                    List<Guardian> guardians = defender.getCamp() == Camp.A ? campA : campB;
                    List<Guardian> aliveEnemies = enemies.stream()
                            .filter(g -> !g.isDead())
                            .collect(Collectors.toList());
                    List<Guardian> juMan = guardians.stream().filter(x -> "句芒".equals(x.getName())).collect(Collectors.toList());
                    if (!aliveEnemies.isEmpty()) {
                        List<String> deadUnits = new ArrayList<>();
                        List<Guardian> deadGuardians = new ArrayList<>();
                        Integer addFire = 0;
                        //协同火焰加成
                        if (Xtool.isNotNull(juMan)) {
                            addFire = 99 * skillLevel[1];
                        }
                        int fireDamage = 54 * skillLevel[1] + addFire;
                        List<String> targetNames = new ArrayList<>();
                        Map<String, Object[]> targetStatus = new HashMap<>();

                        // 记录目标状态并执行伤害
                        aliveEnemies.forEach(g -> {
                            targetNames.add(g.getName());
                            int hpBefore = g.getMaxHp();
                            g.setCurrentHp(g.getCurrentHp() - fireDamage);
                            targetStatus.put(g.getCamp() + g.getName(), new Object[]{g.getPosition(), hpBefore, g.getCurrentHp()});
                            if (g.isDead()) {
                                deadUnits.add(g.getCamp() + g.getName() + "_" + g.getPosition());
                                deadGuardians.add(g);
                            }
                        });
                        // 单条日志记录多目标
                        addMultiTargetLog("烛火燎原",
                                defender.getName(), defender.getCamp(), defender.getPosition(),
                                defenderHpBefore, defender.getCurrentHp(),
                                defenderAttackBefore, defender.getAttack(),
                                defenderSpeedBefore, defender.getSpeed(),
                                targetNames, defender.getCamp() == Camp.A ? Camp.B : Camp.A,
                                targetStatus,
                                getFieldUnitsStatus(),
                                fireDamage, EffectType.FIRE_DAMAGE, DamageType.FIRE,
                                defender.getName() + "触发烛火燎原，对全体敌方造成火焰伤害");
                        // 死亡日志
                        if (!deadUnits.isEmpty()) {
                            Map<String, Object[]> deadStatus = new HashMap<>();
                            deadUnits.forEach(x -> {
                                List<String> strings = Arrays.asList(x.split("_"));
                                deadStatus.put(strings.get(0), new Object[]{Integer.parseInt(strings.get(1)), 0, 0});
                            });

                            addMultiTargetLog("UNIT_DEATH",
                                    "SYSTEM", null, 0,
                                    0, 0, 0, 0, 0, 0,
                                    deadUnits, null,
                                    deadStatus,
                                    getFieldUnitsStatus(),
                                    0, null, null,
                                    "火焰伤害");
                            //触发死亡技能
                            for (Guardian g : deadGuardians) {
                                triggerOnDeathSkills(g);
                            }

                        }
                        //触发受击技能
//                        aliveEnemies.forEach(g -> {
//                            //触发受到任意伤害技能
//                            triggerOnAttackedSkills(g);
//                        });
                    }
                }
                break;
            //萌年兽，爆竹送给你Lv1场上，受到任意攻击后有100%几率对全体敌方造成35~140点随机火焰伤害；幸运年糕Lv1每回合增加自身76点生命点，15点火焰伤害，最多香加5层；不动如山Lv1位居2号位置时，增加自身243点生命上限，24点攻击，12点速度。
            case "萌年兽":
                // 爆竹送给你：对全体敌方造成火焰伤害（多目标整合日志）
                //萌年兽在场
                if (defender.isOnField() && !defender.isDead()) {
                    List<Guardian> enemies = defender.getCamp() == Camp.A ? campB : campA;
                    List<Guardian> aliveEnemies = enemies.stream()
                            .filter(g -> !g.isDead())
                            .collect(Collectors.toList());

                    if (!aliveEnemies.isEmpty()) {
                        List<String> deadUnits = new ArrayList<>();
                        List<Guardian> deadGuardians = new ArrayList<>();
                        int fireDamage = 140 * skillLevel[0];
                        List<String> targetNames = new ArrayList<>();
                        Map<String, Object[]> targetStatus = new HashMap<>();

                        // 记录目标状态并执行伤害
                        aliveEnemies.forEach(g -> {
                            targetNames.add(g.getName());
                            int hpBefore = g.getMaxHp();
                            g.setCurrentHp(g.getCurrentHp() - fireDamage);
                            targetStatus.put(g.getCamp() + g.getName(), new Object[]{g.getPosition(), hpBefore, g.getCurrentHp()});
                            if (g.isDead()) {
                                deadUnits.add(g.getCamp() + g.getName() + "_" + g.getPosition());
                                deadGuardians.add(g);
                            }
                        });
                        // 单条日志记录多目标
                        addMultiTargetLog("爆竹送给你",
                                defender.getName(), defender.getCamp(), defender.getPosition(),
                                defenderHpBefore, defender.getCurrentHp(),
                                defenderAttackBefore, defender.getAttack(),
                                defenderSpeedBefore, defender.getSpeed(),
                                targetNames, defender.getCamp() == Camp.A ? Camp.B : Camp.A,
                                targetStatus,
                                getFieldUnitsStatus(),
                                fireDamage, EffectType.FIRE_DAMAGE, DamageType.FIRE,
                                defender.getName() + "触发爆竹送给你，对全体敌方造成火焰伤害");
                        // 死亡日志
                        if (!deadUnits.isEmpty()) {
                            Map<String, Object[]> deadStatus = new HashMap<>();
                            deadUnits.forEach(x -> {
                                List<String> strings = Arrays.asList(x.split("_"));
                                deadStatus.put(strings.get(0), new Object[]{Integer.parseInt(strings.get(1)), 0, 0});
                            });

                            addMultiTargetLog("UNIT_DEATH",
                                    "SYSTEM", null, 0,
                                    0, 0, 0, 0, 0, 0,
                                    deadUnits, null,
                                    deadStatus,
                                    getFieldUnitsStatus(),
                                    0, null, null,
                                    "火焰伤害");
                            //触发死亡技能
                            for (Guardian g : deadGuardians) {
                                triggerOnDeathSkills(g);
                            }

                        }
                        //触发受击技能
//                        aliveEnemies.forEach(g -> {
//                            //触发受到任意伤害技能
//                            triggerOnAttackedSkills(g);
//                        });
                    }
                }
                break;
            //将臣，剧毒皮肤Lv1受到任意伤害时对随机敌方施放毒素，每回合损失30点生命，持续到战斗结束；腐败虹吸Lv1攻击中毒目标时吸血118点；玄冥协同Lv1与玄冥在同一队伍时，增加自身453点生命上限，158点攻击，158点速度。
            case "将臣":
                if (!defender.isDead()) {
                    List<Guardian> enemies = defender.getCamp() == Camp.A ?
                            campB.stream().filter(g -> !g.isDead()).collect(Collectors.toList()) :
                            campA.stream().filter(g -> !g.isDead()).collect(Collectors.toList());
                    if (!enemies.isEmpty()) {
                        Guardian randomEnemy = enemies.get(random.nextInt(enemies.size()));
                        int enemyHpBefore = randomEnemy.getCurrentHp();
                        int poisonValue = 30 * skillLevel[0];
                        int poisonDamage = randomEnemy.getEffects().getOrDefault(EffectType.POISON, 0);
                        randomEnemy.getEffects().put(EffectType.POISON, poisonValue + poisonDamage);

                        addLog("剧毒皮肤",
                                defender.getName(), defender.getCamp(), defender.getPosition(),
                                defenderHpBefore, defender.getCurrentHp(),
                                defenderAttackBefore, defender.getAttack(),
                                defenderSpeedBefore, defender.getSpeed(),
                                randomEnemy.getName(), randomEnemy.getCamp(), randomEnemy.getPosition(),
                                enemyHpBefore, randomEnemy.getCurrentHp(),
                                randomEnemy.getAttack(), randomEnemy.getAttack(),
                                randomEnemy.getSpeed(), randomEnemy.getSpeed(),
                                getFieldUnitsStatus(),
                                poisonValue, EffectType.POISON, DamageType.POISON,
                                defender.getName() + "触发剧毒皮肤");
                    }

                }
                break;
            case "白素贞":
//                燥热蛇毒Lv1场下，受到火焰伤害，为当前敌人添加蛇毒效果，每回合损失12生命；众妖皆狂Lv1与妲己在同一队伍时，增加自身453点生命上限，158点攻击，158点速度。
                if (!defender.isDead()&&effectType==EffectType.FIRE_DAMAGE) {
                    List<Guardian> enemies = defender.getCamp() == Camp.A ?
                            campB.stream().filter(g -> !g.isDead()).collect(Collectors.toList()) :
                            campA.stream().filter(g -> !g.isDead()).collect(Collectors.toList());
                    if (!enemies.isEmpty()) {
                        Guardian randomEnemy = enemies.get(random.nextInt(enemies.size()));
                        int enemyHpBefore = randomEnemy.getCurrentHp();
                        int poisonValue = 30 * skillLevel[0];
                        int poisonDamage = randomEnemy.getEffects().getOrDefault(EffectType.POISON, 0);
                        randomEnemy.getEffects().put(EffectType.POISON, poisonValue + poisonDamage);

                        addLog("燥热蛇毒",
                                defender.getName(), defender.getCamp(), defender.getPosition(),
                                defenderHpBefore, defender.getCurrentHp(),
                                defenderAttackBefore, defender.getAttack(),
                                defenderSpeedBefore, defender.getSpeed(),
                                randomEnemy.getName(), randomEnemy.getCamp(), randomEnemy.getPosition(),
                                enemyHpBefore, randomEnemy.getCurrentHp(),
                                randomEnemy.getAttack(), randomEnemy.getAttack(),
                                randomEnemy.getSpeed(), randomEnemy.getSpeed(),
                                getFieldUnitsStatus(),
                                poisonValue, EffectType.POISON, DamageType.POISON,
                                defender.getName() + "触发燥热蛇毒");
                    }

                }
                break;
//            case "南岳大帝":
////                燥热蛇毒Lv1场下，受到火焰伤害，为当前敌人添加蛇毒效果，每回合损失12生命；众妖皆狂Lv1与妲己在同一队伍时，增加自身453点生命上限，158点攻击，158点速度。
//                if (!defender.isDead()&&effectType==EffectType.FIRE_DAMAGE) {
//                    List<Guardian> enemies = defender.getCamp() == Camp.A ?
//                            campB.stream().filter(g -> !g.isDead()).collect(Collectors.toList()) :
//                            campA.stream().filter(g -> !g.isDead()).collect(Collectors.toList());
//                    if (!enemies.isEmpty()) {
//                        Guardian randomEnemy = enemies.get(random.nextInt(enemies.size()));
//                        int enemyHpBefore = randomEnemy.getCurrentHp();
//                        int poisonValue = 30 * skillLevel[0];
//                        int poisonDamage = randomEnemy.getEffects().getOrDefault(EffectType.POISON, 0);
//                        randomEnemy.getEffects().put(EffectType.POISON, poisonValue + poisonDamage);
//
//                        addLog("草船借箭",
//                                defender.getName(), defender.getCamp(), defender.getPosition(),
//                                defenderHpBefore, defender.getCurrentHp(),
//                                defenderAttackBefore, defender.getAttack(),
//                                defenderSpeedBefore, defender.getSpeed(),
//                                randomEnemy.getName(), randomEnemy.getCamp(), randomEnemy.getPosition(),
//                                enemyHpBefore, randomEnemy.getCurrentHp(),
//                                randomEnemy.getAttack(), randomEnemy.getAttack(),
//                                randomEnemy.getSpeed(), randomEnemy.getSpeed(),
//                                getFieldUnitsStatus(),
//                                poisonValue, EffectType.POISON, DamageType.POISON,
//                                defender.getName() + "触发草船借箭");
//                    }
//
//                }
//                break;
            case "长生大帝":
//                南极祝福Lv1场下，受到任意伤害时提升自身56点生命值上限；
                if (!defender.isDead() && !defender.isOnField() && skillLevel[1] > 0) {
                    int hel = 0;
                    if (duoBaoGuanHuan()) {
                        hel = 56 * skillLevel[1];
                    }
                    defender.setMaxHp(defender.getMaxHp() + hel);
                    defender.setCurrentHp(defender.getCurrentHp() + hel);

                    addLog("南极祝福",
                            defender.getName(), defender.getCamp(), defender.getPosition(),
                            defender.getMaxHp(), defender.getCurrentHp(),
                            defender.getAttack(), defender.getAttack(),
                            defender.getSpeed(), defender.getSpeed(),
                            defender.getName(), defender.getCamp(), defender.getPosition(),
                            defender.getMaxHp(), defender.getCurrentHp(),
                            defender.getAttack(), defender.getAttack(),
                            defender.getSpeed(), defender.getSpeed(),
                            getFieldUnitsStatus(),
                            hel, EffectType.HP_UP, null,
                            defender.getName() + "触发南极祝福，生命上限+" + hel);

                }
                break;
        }

    }

    // 触发攻击后技能
    private void triggerPostAttackSkills(Guardian attacker, Guardian defender) {
        int attackerHpBefore = attacker.getMaxHp();
        int attackerAttackBefore = attacker.getAttack();
        int attackerSpeedBefore = attacker.getSpeed();
        int[] skillLevel = CardSkillLevelUtil.calculateSkillLevels(attacker.getLevel(), attacker.getStar().doubleValue());
        if (Xtool.isNotNull(attacker.getEffects().get(EffectType.SILENCE))) {
            return;
        }
        switch (attacker.getName()) {
            case "燃灯道人":
                if (1 == 1) {
                    // 仙人指路Lv1每次攻击后增加自身后方单位的攻击66点，最多叠加3次；
                    List<Guardian> enemies = attacker.getCamp() == Camp.A ?
                            campA.stream().filter(g -> !g.isDead() && !g.isOnField()).collect(Collectors.toList()) :
                            campB.stream().filter(g -> !g.isDead() && !g.isOnField()).collect(Collectors.toList());
                    if (!enemies.isEmpty()) {
                        int attack = 66 * skillLevel[0];
                        Guardian guardian = enemies.get(0);
                        if (guardian.getBuffRandengs() < 3) {
                            guardian.setBuffRandengs(guardian.getBuffRandengs() + 1);
                            guardian.setAttack(guardian.getAttack() + attack);
                            addLog("仙人指路",
                                    attacker.getName(), attacker.getCamp(), attacker.getPosition(),
                                    attacker.getMaxHp(), attacker.getCurrentHp(),
                                    attacker.getAttack(), attacker.getAttack(),
                                    attacker.getSpeed(), attacker.getSpeed(),
                                    guardian.getName(), guardian.getCamp(), guardian.getPosition(),
                                    guardian.getMaxHp(), guardian.getCurrentHp(),
                                    guardian.getAttack(), guardian.getAttack(),
                                    guardian.getSpeed(), guardian.getSpeed(),
                                    getFieldUnitsStatus(),
                                    attack, EffectType.ATTACK_UP, null,
                                    attacker.getName() + "触发仙人指路，攻击力提升");
                        }
                    }
                }
                break;
            case "禺绒王":
                if (1 == 1) {
                    // 仙人指路Lv1每次攻击后增加自身后方单位的攻击66点，最多叠加3次；
                    List<Guardian> enemies = attacker.getCamp() == Camp.A ?
                            campA.stream().filter(g -> !g.isDead() && !g.isOnField()).collect(Collectors.toList()) :
                            campB.stream().filter(g -> !g.isDead() && !g.isOnField()).collect(Collectors.toList());
                    if (!enemies.isEmpty()) {
                        int attack = 42 * skillLevel[0];
                        Guardian guardian = enemies.get(0);
                        if (guardian.getBuffLiuers() < 5) {
                            guardian.setBuffRandengs(guardian.getBuffLiuers() + 1);
                            guardian.setAttack(guardian.getAttack() + attack);
                            addLog("妖力聚集",
                                    attacker.getName(), attacker.getCamp(), attacker.getPosition(),
                                    attacker.getMaxHp(), attacker.getCurrentHp(),
                                    attacker.getAttack(), attacker.getAttack(),
                                    attacker.getSpeed(), attacker.getSpeed(),
                                    guardian.getName(), guardian.getCamp(), guardian.getPosition(),
                                    guardian.getMaxHp(), guardian.getCurrentHp(),
                                    guardian.getAttack(), guardian.getAttack(),
                                    guardian.getSpeed(), guardian.getSpeed(),
                                    getFieldUnitsStatus(),
                                    attack, EffectType.ATTACK_UP, null,
                                    attacker.getName() + "触发妖力聚集，攻击力提升");
                        }
                    }
                }
                break;
            case "普贤真人":
                if (1 == 1) {
                    // 仙人指路Lv1每次攻击后增加自身后方单位的攻击66点，最多叠加3次；
                    List<Guardian> enemies = attacker.getCamp() == Camp.A ?
                            campA.stream().filter(g -> !g.isDead() && !g.isOnField()).collect(Collectors.toList()) :
                            campB.stream().filter(g -> !g.isDead() && !g.isOnField()).collect(Collectors.toList());
                    if (!enemies.isEmpty()) {
                        int hel = 66 * skillLevel[0];
                        Guardian guardian = enemies.get(0);
                        guardian.setMaxHp(guardian.getMaxHp() + hel);
                        guardian.setCurrentHp(guardian.getCurrentHp() + hel);
                        addLog("仙人剑法",
                                attacker.getName(), attacker.getCamp(), attacker.getPosition(),
                                attacker.getMaxHp(), attacker.getCurrentHp(),
                                attacker.getAttack(), attacker.getAttack(),
                                attacker.getSpeed(), attacker.getSpeed(),
                                guardian.getName(), guardian.getCamp(), guardian.getPosition(),
                                guardian.getMaxHp(), guardian.getCurrentHp(),
                                guardian.getAttack(), guardian.getAttack(),
                                guardian.getSpeed(), guardian.getSpeed(),
                                getFieldUnitsStatus(),
                                hel, EffectType.HP_UP, null,
                                attacker.getName() + "触发仙人剑法，生命上限提升");
                    }
                }
                break;
            case "鲤鱼精":
                if (1 == 1) {
                    //鲤鱼精，鱼跃龙门Lv1攻击后对场下敌方造成62点火焰伤害
                    List<Guardian> offFieldEnemies = defender.getCamp() == Camp.A ?
                            campA.stream().filter(g -> !g.isDead() && !g.isOnField()).collect(Collectors.toList()) :
                            campB.stream().filter(g -> !g.isDead() && !g.isOnField()).collect(Collectors.toList());

                    if (!offFieldEnemies.isEmpty()) {
                        List<String> deadUnits = new ArrayList<>();
                        List<Guardian> deadGuardians = new ArrayList<>();
                        int lavaDamage = 62 * skillLevel[0];
                        List<String> targetNames = new ArrayList<>();
                        Map<String, Object[]> targetStatus = new HashMap<>();

                        offFieldEnemies.forEach(g -> {
                            targetNames.add(g.getName());
                            int hpBefore = g.getMaxHp();
                            g.setCurrentHp(g.getCurrentHp() - lavaDamage);
                            targetStatus.put(g.getCamp() + g.getName(), new Object[]{g.getPosition(), hpBefore, g.getCurrentHp()});
                            if (g.isDead()) {
                                deadUnits.add(g.getCamp() + g.getName() + "_" + g.getPosition());
                                deadGuardians.add(g);
                            }
                        });

                        // 单条日志记录多目标
                        addMultiTargetLog("鱼跃龙门",
                                attacker.getName(), attacker.getCamp(), attacker.getPosition(),
                                attackerHpBefore, attacker.getCurrentHp(),
                                attackerAttackBefore, attacker.getAttack(),
                                attackerSpeedBefore, attacker.getSpeed(),
                                targetNames, defender.getCamp(),
                                targetStatus,
                                getFieldUnitsStatus(),
                                lavaDamage, EffectType.FIRE_DAMAGE, DamageType.FIRE,
                                attacker.getName() + "触发鱼跃龙门，攻击后对场下敌方造成62点火焰伤害");
                        // 死亡日志
                        if (!deadUnits.isEmpty()) {
                            Map<String, Object[]> deadStatus = new HashMap<>();
                            deadUnits.forEach(x -> {
                                List<String> strings = Arrays.asList(x.split("_"));
                                deadStatus.put(strings.get(0), new Object[]{Integer.parseInt(strings.get(1)), 0, 0});
                            });

                            addMultiTargetLog("UNIT_DEATH",
                                    "SYSTEM", null, 0,
                                    0, 0, 0, 0, 0, 0,
                                    deadUnits, null,
                                    deadStatus,
                                    getFieldUnitsStatus(),
                                    0, null, null,
                                    "火焰伤害");
                            //触发死亡技能
                            for (Guardian g : deadGuardians) {
                                triggerOnDeathSkills(g);
                            }

                        }
                        //触发受击技能
                        offFieldEnemies.forEach(g -> {
                            //触发受到任意伤害技能
                            triggerOnAttackedSkills(g, EffectType.FIRE_DAMAGE);
                        });
                    }
                }
                break;
            case "将臣":
                // 腐败虹吸Lv1攻击中毒目标时吸血118点；
                if (Xtool.isNotNull(defender.getEffects().get(EffectType.POISON)) && skillLevel[1] > 0) {
                    int hel = 118 * skillLevel[1];
                    attacker.setCurrentHp(attacker.getCurrentHp() + hel);

                    addLog("腐败虹吸",
                            attacker.getName(), attacker.getCamp(), attacker.getPosition(),
                            attackerHpBefore, attacker.getCurrentHp(),
                            attackerAttackBefore, attacker.getAttack(),
                            attackerSpeedBefore, attacker.getSpeed(),
                            defender.getName(), attacker.getCamp(), attacker.getPosition(),
                            attacker.getMaxHp(), attacker.getCurrentHp(),
                            attacker.getAttack(), attacker.getAttack(),
                            attacker.getSpeed(), attacker.getSpeed(),
                            getFieldUnitsStatus(),
                            hel, EffectType.HEAL, DamageType.MAGIC,
                            attacker.getName() + "触发腐败虹吸");
                }
                break;
            case "太岁灵君":
                // 腐败虹吸Lv1攻击中毒目标时吸血118点；
                if (skillLevel[1]>0&&random.nextDouble() < 0.07*skillLevel[1]) {
                    int hel = (int)(attacker.getAttack()*0.87);
                    attacker.setCurrentHp(attacker.getCurrentHp() + hel);

                    addLog("虹吸打击",
                            attacker.getName(), attacker.getCamp(), attacker.getPosition(),
                            attackerHpBefore, attacker.getCurrentHp(),
                            attackerAttackBefore, attacker.getAttack(),
                            attackerSpeedBefore, attacker.getSpeed(),
                            defender.getName(), attacker.getCamp(), attacker.getPosition(),
                            attacker.getMaxHp(), attacker.getCurrentHp(),
                            attacker.getAttack(), attacker.getAttack(),
                            attacker.getSpeed(), attacker.getSpeed(),
                            getFieldUnitsStatus(),
                            hel, EffectType.HEAL, DamageType.MAGIC,
                            attacker.getName() + "触发虹吸打击");
                }
                break;
            case "金钩大王":
                //毒伤迸发Lv1攻击中毒敌人时，额外造成247点物理伤害；
                if (Xtool.isNotNull(defender.getEffects().get(EffectType.POISON))) {
                    int damage = 247 * skillLevel[0];
                    List<String> deadUnits = new ArrayList<>();
                    List<Guardian> deadGuardians = new ArrayList<>();
                    defender.setCurrentHp(defender.getCurrentHp() + damage);
                    if (defender.getCurrentHp() <= 0) {
                        defender.setDead(true);
                        defender.setOnField(false);
                        deadUnits.add(defender.getCamp() + defender.getName() + "_" + defender.getPosition());
                        deadGuardians.add(defender);
                    }
                    addLog("毒伤迸发",
                            attacker.getName(), attacker.getCamp(), attacker.getPosition(),
                            attackerHpBefore, attacker.getCurrentHp(),
                            attackerAttackBefore, attacker.getAttack(),
                            attackerSpeedBefore, attacker.getSpeed(),
                            defender.getName(), defender.getCamp(), defender.getPosition(),
                            defender.getMaxHp(), defender.getCurrentHp(),
                            defender.getAttack(), defender.getAttack(),
                            defender.getSpeed(), defender.getSpeed(),
                            getFieldUnitsStatus(),
                            damage, EffectType.DAMAGE, DamageType.PHYSICAL,
                            attacker.getName() + "触发毒伤迸发");
                    // 死亡日志
                    if (!deadUnits.isEmpty()) {
                        Map<String, Object[]> deadStatus = new HashMap<>();
                        deadUnits.forEach(x -> {
                            List<String> strings = Arrays.asList(x.split("_"));
                            deadStatus.put(strings.get(0), new Object[]{Integer.parseInt(strings.get(1)), 0, 0});
                        });

                        addMultiTargetLog("UNIT_DEATH",
                                "SYSTEM", null, 0,
                                0, 0, 0, 0, 0, 0,
                                deadUnits, null,
                                deadStatus,
                                getFieldUnitsStatus(),
                                0, null, null,
                                "真实伤害");
                        //触发死亡技能
                        for (Guardian g : deadGuardians) {
                            triggerOnDeathSkills(g);
                        }

                    } else {
                        //触发受击技能
                        triggerOnAttackedSkills(defender,EffectType.DAMAGE);
                    }
                }
                break;
            case "蛟魔王":
                //剧毒痛击Lv1攻击中毒单位额外造成80伤害；；
                if (Xtool.isNotNull(defender.getEffects().get(EffectType.POISON))) {
                    int damage = 80 * skillLevel[0];
                    List<String> deadUnits = new ArrayList<>();
                    List<Guardian> deadGuardians = new ArrayList<>();
                    defender.setCurrentHp(defender.getCurrentHp() + damage);
                    if (defender.getCurrentHp() <= 0) {
                        defender.setDead(true);
                        defender.setOnField(false);
                        deadUnits.add(defender.getCamp() + defender.getName() + "_" + defender.getPosition());
                        deadGuardians.add(defender);
                    }
                    addLog("剧毒痛击",
                            attacker.getName(), attacker.getCamp(), attacker.getPosition(),
                            attackerHpBefore, attacker.getCurrentHp(),
                            attackerAttackBefore, attacker.getAttack(),
                            attackerSpeedBefore, attacker.getSpeed(),
                            defender.getName(), defender.getCamp(), defender.getPosition(),
                            defender.getMaxHp(), defender.getCurrentHp(),
                            defender.getAttack(), defender.getAttack(),
                            defender.getSpeed(), defender.getSpeed(),
                            getFieldUnitsStatus(),
                            damage, EffectType.DAMAGE, DamageType.PHYSICAL,
                            attacker.getName() + "触发剧毒痛击");
                    // 死亡日志
                    if (!deadUnits.isEmpty()) {
                        Map<String, Object[]> deadStatus = new HashMap<>();
                        deadUnits.forEach(x -> {
                            List<String> strings = Arrays.asList(x.split("_"));
                            deadStatus.put(strings.get(0), new Object[]{Integer.parseInt(strings.get(1)), 0, 0});
                        });

                        addMultiTargetLog("UNIT_DEATH",
                                "SYSTEM", null, 0,
                                0, 0, 0, 0, 0, 0,
                                deadUnits, null,
                                deadStatus,
                                getFieldUnitsStatus(),
                                0, null, null,
                                "真实伤害");
                        //触发死亡技能
                        for (Guardian g : deadGuardians) {
                            triggerOnDeathSkills(g);
                        }

                    } else {
                        //触发受击技能
                        triggerOnAttackedSkills(defender,EffectType.DAMAGE);
                    }
                }
                break;
            case "铁扇公主":
                if (1 == 1) {
                    // 芭蕉扇：造成火焰伤害
                    int defenderHpBefore = defender.getCurrentHp();
                    int fireDamage = 36 * skillLevel[0];
                    List<String> deadUnits = new ArrayList<>();
                    List<Guardian> deadGuardians = new ArrayList<>();
                    defender.setCurrentHp(defender.getCurrentHp() - fireDamage);
                    if (defender.getCurrentHp() <= 0) {
                        defender.setDead(true);
                        defender.setOnField(false);
                        deadUnits.add(defender.getCamp() + defender.getName() + "_" + defender.getPosition());
                        deadGuardians.add(defender);
                    }
                    addLog("芭蕉扇",
                            attacker.getName(), attacker.getCamp(), attacker.getPosition(),
                            attackerHpBefore, attacker.getCurrentHp(),
                            attackerAttackBefore, attacker.getAttack(),
                            attackerSpeedBefore, attacker.getSpeed(),
                            defender.getName(), defender.getCamp(), defender.getPosition(),
                            defenderHpBefore, defender.getCurrentHp(),
                            defender.getAttack(), defender.getAttack(),
                            defender.getSpeed(), defender.getSpeed(),
                            getFieldUnitsStatus(),
                            fireDamage, EffectType.FIRE_DAMAGE, DamageType.FIRE,
                            attacker.getName() + "触发芭蕉扇");
                    // 死亡日志
                    if (!deadUnits.isEmpty()) {
                        Map<String, Object[]> deadStatus = new HashMap<>();
                        deadUnits.forEach(x -> {
                            List<String> strings = Arrays.asList(x.split("_"));
                            deadStatus.put(strings.get(0), new Object[]{Integer.parseInt(strings.get(1)), 0, 0});
                        });

                        addMultiTargetLog("UNIT_DEATH",
                                "SYSTEM", null, 0,
                                0, 0, 0, 0, 0, 0,
                                deadUnits, null,
                                deadStatus,
                                getFieldUnitsStatus(),
                                0, null, null,
                                "真实伤害");
                        //触发死亡技能
                        for (Guardian g : deadGuardians) {
                            triggerOnDeathSkills(g);
                        }

                    } else {
                        //触发受击技能
                        triggerOnAttackedSkills(defender,EffectType.FIRE_DAMAGE);
                    }
                }
                break;
            case "应龙":
                if (1 == 1) {
                    // 龙息lv1攻击后随机对场上单位造成250点火焰伤害。
                    if (random.nextDouble() < 0.5) {
                        int defenderHpBefore = attacker.getCurrentHp();
                        int fireDamage = 36 * skillLevel[0];
                        List<String> deadUnits = new ArrayList<>();
                        List<Guardian> deadGuardians = new ArrayList<>();
                        attacker.setCurrentHp(attacker.getCurrentHp() - fireDamage);
                        if (attacker.getCurrentHp() <= 0) {
                            attacker.setDead(true);
                            attacker.setOnField(false);
                            deadUnits.add(attacker.getCamp() + attacker.getName() + "_" + attacker.getPosition());
                            deadGuardians.add(attacker);
                        }
                        addLog("龙息",
                                attacker.getName(), attacker.getCamp(), attacker.getPosition(),
                                attackerHpBefore, attacker.getCurrentHp(),
                                attackerAttackBefore, attacker.getAttack(),
                                attackerSpeedBefore, attacker.getSpeed(),
                                attacker.getName(), attacker.getCamp(), attacker.getPosition(),
                                defenderHpBefore, attacker.getCurrentHp(),
                                attacker.getAttack(), attacker.getAttack(),
                                attacker.getSpeed(), attacker.getSpeed(),
                                getFieldUnitsStatus(),
                                fireDamage, EffectType.FIRE_DAMAGE, DamageType.FIRE,
                                attacker.getName() + "触发龙息");
                        // 死亡日志
                        if (!deadUnits.isEmpty()) {
                            Map<String, Object[]> deadStatus = new HashMap<>();
                            deadUnits.forEach(x -> {
                                List<String> strings = Arrays.asList(x.split("_"));
                                deadStatus.put(strings.get(0), new Object[]{Integer.parseInt(strings.get(1)), 0, 0});
                            });

                            addMultiTargetLog("UNIT_DEATH",
                                    "SYSTEM", null, 0,
                                    0, 0, 0, 0, 0, 0,
                                    deadUnits, null,
                                    deadStatus,
                                    getFieldUnitsStatus(),
                                    0, null, null,
                                    "火焰伤害");

                        }
                    } else {
                        int defenderHpBefore = defender.getCurrentHp();
                        int fireDamage = 36 * skillLevel[0];
                        List<String> deadUnits = new ArrayList<>();
                        List<Guardian> deadGuardians = new ArrayList<>();
                        defender.setCurrentHp(defender.getCurrentHp() - fireDamage);
                        if (defender.getCurrentHp() <= 0) {
                            defender.setDead(true);
                            defender.setOnField(false);
                            deadUnits.add(defender.getCamp() + defender.getName() + "_" + defender.getPosition());
                            deadGuardians.add(defender);
                        }
                        addLog("龙息",
                                attacker.getName(), attacker.getCamp(), attacker.getPosition(),
                                attackerHpBefore, attacker.getCurrentHp(),
                                attackerAttackBefore, attacker.getAttack(),
                                attackerSpeedBefore, attacker.getSpeed(),
                                defender.getName(), defender.getCamp(), defender.getPosition(),
                                defenderHpBefore, defender.getCurrentHp(),
                                defender.getAttack(), defender.getAttack(),
                                defender.getSpeed(), defender.getSpeed(),
                                getFieldUnitsStatus(),
                                fireDamage, EffectType.FIRE_DAMAGE, DamageType.FIRE,
                                attacker.getName() + "触发龙息");
                        // 死亡日志
                        if (!deadUnits.isEmpty()) {
                            Map<String, Object[]> deadStatus = new HashMap<>();
                            deadUnits.forEach(x -> {
                                List<String> strings = Arrays.asList(x.split("_"));
                                deadStatus.put(strings.get(0), new Object[]{Integer.parseInt(strings.get(1)), 0, 0});
                            });

                            addMultiTargetLog("UNIT_DEATH",
                                    "SYSTEM", null, 0,
                                    0, 0, 0, 0, 0, 0,
                                    deadUnits, null,
                                    deadStatus,
                                    getFieldUnitsStatus(),
                                    0, null, null,
                                    "火焰伤害");
                            //触发死亡技能
                            for (Guardian g : deadGuardians) {
                                triggerOnDeathSkills(g);
                            }

                        } else {
                            //触发受击技能
                            triggerOnAttackedSkills(defender,EffectType.FIRE_DAMAGE);
                        }
                    }

                }
                break;
            case "聂小倩":
                if (skillLevel[1] > 0) {
                    // 芭蕉扇：造成火焰伤害
                    int defenderHpBefore = defender.getCurrentHp();
                    int fireDamage = 60 * skillLevel[1];
                    defender.setCurrentHp(defender.getCurrentHp() - fireDamage);
                    List<String> deadUnits = new ArrayList<>();
                    List<Guardian> deadGuardians = new ArrayList<>();
                    if (defender.getCurrentHp() <= 0) {
                        defender.setDead(true);
                        defender.setOnField(false);
                        deadUnits.add(defender.getCamp() + defender.getName() + "_" + defender.getPosition());
                        deadGuardians.add(defender);
                    }
//                    剧毒痛击lv1攻击中毒单位时，额外造成60点伤害。
                    addLog("剧毒痛击",
                            attacker.getName(), attacker.getCamp(), attacker.getPosition(),
                            attackerHpBefore, attacker.getCurrentHp(),
                            attackerAttackBefore, attacker.getAttack(),
                            attackerSpeedBefore, attacker.getSpeed(),
                            defender.getName(), defender.getCamp(), defender.getPosition(),
                            defenderHpBefore, defender.getCurrentHp(),
                            defender.getAttack(), defender.getAttack(),
                            defender.getSpeed(), defender.getSpeed(),
                            getFieldUnitsStatus(),
                            fireDamage, EffectType.DAMAGE, DamageType.PHYSICAL,
                            attacker.getName() + "触发剧毒痛击");
                    // 死亡日志
                    if (!deadUnits.isEmpty()) {
                        Map<String, Object[]> deadStatus = new HashMap<>();
                        deadUnits.forEach(x -> {
                            List<String> strings = Arrays.asList(x.split("_"));
                            deadStatus.put(strings.get(0), new Object[]{Integer.parseInt(strings.get(1)), 0, 0});
                        });

                        addMultiTargetLog("UNIT_DEATH",
                                "SYSTEM", null, 0,
                                0, 0, 0, 0, 0, 0,
                                deadUnits, null,
                                deadStatus,
                                getFieldUnitsStatus(),
                                0, null, null,
                                "真实伤害");
                        //触发死亡技能
                        for (Guardian g : deadGuardians) {
                            triggerOnDeathSkills(g);
                        }

                    } else {
                        //触发受击技能
                        triggerOnAttackedSkills(defender, EffectType.DAMAGE);
                    }
                }
                break;
            case "天蓬元帅":
                if (skillLevel[1] > 0) {
                    if (random.nextDouble() < 0.1 * skillLevel[1]) {
//                    醉钉耙Lv1场上，攻击后有10%几率对随机敌方造成真实伤害，数值等同于目标力量的50%；
                        int fireDamage = (int) (0.5 * defender.getAttack());
                        List<String> deadUnits = new ArrayList<>();
                        List<Guardian> deadGuardians = new ArrayList<>();
                        defender.setCurrentHp(defender.getCurrentHp() - fireDamage);
                        if (defender.getCurrentHp() <= 0) {
                            defender.setDead(true);
                            defender.setOnField(false);
                            deadUnits.add(defender.getCamp() + defender.getName() + "_" + defender.getPosition());
                            deadGuardians.add(defender);
                        }
                        addLog("醉钉耙",
                                attacker.getName(), attacker.getCamp(), attacker.getPosition(),
                                attackerHpBefore, attacker.getCurrentHp(),
                                attackerAttackBefore, attacker.getAttack(),
                                attackerSpeedBefore, attacker.getSpeed(),
                                defender.getName(), defender.getCamp(), defender.getPosition(),
                                defender.getMaxHp(), defender.getCurrentHp(),
                                defender.getAttack(), defender.getAttack(),
                                defender.getSpeed(), defender.getSpeed(),
                                getFieldUnitsStatus(),
                                fireDamage, EffectType.TRUE_DAMAGE, DamageType.PHYSICAL,
                                attacker.getName() + "触发醉钉耙");
                        // 死亡日志
                        if (!deadUnits.isEmpty()) {
                            Map<String, Object[]> deadStatus = new HashMap<>();
                            deadUnits.forEach(x -> {
                                List<String> strings = Arrays.asList(x.split("_"));
                                deadStatus.put(strings.get(0), new Object[]{Integer.parseInt(strings.get(1)), 0, 0});
                            });

                            addMultiTargetLog("UNIT_DEATH",
                                    "SYSTEM", null, 0,
                                    0, 0, 0, 0, 0, 0,
                                    deadUnits, null,
                                    deadStatus,
                                    getFieldUnitsStatus(),
                                    0, null, null,
                                    "真实伤害");
                            //触发死亡技能
                            for (Guardian g : deadGuardians) {
                                triggerOnDeathSkills(g);
                            }

                        } else {
                            //触发受击技能
                            triggerOnAttackedSkills(defender,EffectType.TRUE_DAMAGE);
                        }
                    }
                }
                break;
            case "哪吒":
                if (1 == 1) {
//                    哪吒，穿云斩Lv1普通攻击后，对当前敌方身后一个单位造成125点真实伤害；
                    List<Guardian> enemieList = attacker.getCamp() == Camp.A ?
                            campB.stream().filter(g -> !g.isDead() && !g.isOnField()).collect(Collectors.toList()) :
                            campA.stream().filter(g -> !g.isDead() && !g.isOnField()).collect(Collectors.toList());
                    if (Xtool.isNotNull(enemieList)) {
                        List<String> deadUnits = new ArrayList<>();
                        List<Guardian> deadGuardians = new ArrayList<>();
                        Guardian guardian = enemieList.get(0);
                        int fireDamage = 125 * skillLevel[0];
                        guardian.setCurrentHp(guardian.getCurrentHp() - fireDamage);
                        if (guardian.getCurrentHp() <= 0) {
                            guardian.setDead(true);
                            guardian.setOnField(false);
                            deadUnits.add(guardian.getCamp() + guardian.getName() + "_" + guardian.getPosition());
                            deadGuardians.add(guardian);
                        }
                        addLog("穿云斩",
                                attacker.getName(), attacker.getCamp(), attacker.getPosition(),
                                attackerHpBefore, attacker.getCurrentHp(),
                                attackerAttackBefore, attacker.getAttack(),
                                attackerSpeedBefore, attacker.getSpeed(),
                                guardian.getName(), guardian.getCamp(), guardian.getPosition(),
                                guardian.getMaxHp(), guardian.getCurrentHp(),
                                guardian.getAttack(), guardian.getAttack(),
                                guardian.getSpeed(), guardian.getSpeed(),
                                getFieldUnitsStatus(),
                                fireDamage, EffectType.TRUE_DAMAGE, DamageType.PHYSICAL,
                                attacker.getName() + "触发穿云斩");
                        // 死亡日志
                        if (!deadUnits.isEmpty()) {
                            Map<String, Object[]> deadStatus = new HashMap<>();
                            deadUnits.forEach(x -> {
                                List<String> strings = Arrays.asList(x.split("_"));
                                deadStatus.put(strings.get(0), new Object[]{Integer.parseInt(strings.get(1)), 0, 0});
                            });

                            addMultiTargetLog("UNIT_DEATH",
                                    "SYSTEM", null, 0,
                                    0, 0, 0, 0, 0, 0,
                                    deadUnits, null,
                                    deadStatus,
                                    getFieldUnitsStatus(),
                                    0, null, null,
                                    "真实伤害");
                            //触发死亡技能
                            for (Guardian g : deadGuardians) {
                                triggerOnDeathSkills(g);
                            }

                        } else {
                            //触发受击技能
                            triggerOnAttackedSkills(guardian,EffectType.TRUE_DAMAGE);
                        }
                    }
                }
                break;
            case "玉鼎真人":
                if (1 == 1) {
//                   1普通攻击后，对场上敌方身后一个单位造成100点真实伤害；
                    List<Guardian> enemieList = attacker.getCamp() == Camp.A ?
                            campB.stream().filter(g -> !g.isDead() && !g.isOnField()).collect(Collectors.toList()) :
                            campA.stream().filter(g -> !g.isDead() && !g.isOnField()).collect(Collectors.toList());
                    if (Xtool.isNotNull(enemieList)) {
                        List<String> deadUnits = new ArrayList<>();
                        List<Guardian> deadGuardians = new ArrayList<>();
                        Guardian guardian = enemieList.get(0);
                        int fireDamage = 100 * skillLevel[0];
                        guardian.setCurrentHp(guardian.getCurrentHp() - fireDamage);
                        if (guardian.getCurrentHp() <= 0) {
                            guardian.setDead(true);
                            guardian.setOnField(false);
                            deadUnits.add(guardian.getCamp() + guardian.getName() + "_" + guardian.getPosition());
                            deadGuardians.add(guardian);
                        }
                        addLog("穿云剑法",
                                attacker.getName(), attacker.getCamp(), attacker.getPosition(),
                                attackerHpBefore, attacker.getCurrentHp(),
                                attackerAttackBefore, attacker.getAttack(),
                                attackerSpeedBefore, attacker.getSpeed(),
                                guardian.getName(), guardian.getCamp(), guardian.getPosition(),
                                guardian.getMaxHp(), guardian.getCurrentHp(),
                                guardian.getAttack(), guardian.getAttack(),
                                guardian.getSpeed(), guardian.getSpeed(),
                                getFieldUnitsStatus(),
                                fireDamage, EffectType.TRUE_DAMAGE, DamageType.PHYSICAL,
                                attacker.getName() + "触发穿云剑法");
                        // 死亡日志
                        if (!deadUnits.isEmpty()) {
                            Map<String, Object[]> deadStatus = new HashMap<>();
                            deadUnits.forEach(x -> {
                                List<String> strings = Arrays.asList(x.split("_"));
                                deadStatus.put(strings.get(0), new Object[]{Integer.parseInt(strings.get(1)), 0, 0});
                            });

                            addMultiTargetLog("UNIT_DEATH",
                                    "SYSTEM", null, 0,
                                    0, 0, 0, 0, 0, 0,
                                    deadUnits, null,
                                    deadStatus,
                                    getFieldUnitsStatus(),
                                    0, null, null,
                                    "真实伤害");
                            //触发死亡技能
                            for (Guardian g : deadGuardians) {
                                triggerOnDeathSkills(g);
                            }

                        } else {
                            //触发受击技能
                            triggerOnAttackedSkills(guardian,EffectType.TRUE_DAMAGE);
                        }
                    }
                }
                break;
            case "九天玄女":
                if (1 == 1) {
//                   穿云剑Lv1普通攻击后，对场上敌方身后一个单位造成52点真实伤害；；
                    List<Guardian> enemieList = attacker.getCamp() == Camp.A ?
                            campB.stream().filter(g -> !g.isDead() && !g.isOnField()).collect(Collectors.toList()) :
                            campA.stream().filter(g -> !g.isDead() && !g.isOnField()).collect(Collectors.toList());
                    if (Xtool.isNotNull(enemieList)) {
                        List<String> deadUnits = new ArrayList<>();
                        List<Guardian> deadGuardians = new ArrayList<>();
                        Guardian guardian = enemieList.get(0);
                        int fireDamage = 125 * skillLevel[0];
                        guardian.setCurrentHp(guardian.getCurrentHp() - fireDamage);
                        if (guardian.getCurrentHp() <= 0) {
                            guardian.setDead(true);
                            guardian.setOnField(false);
                            deadUnits.add(guardian.getCamp() + guardian.getName() + "_" + guardian.getPosition());
                            deadGuardians.add(guardian);
                        }
                        addLog("穿云剑",
                                attacker.getName(), attacker.getCamp(), attacker.getPosition(),
                                attackerHpBefore, attacker.getCurrentHp(),
                                attackerAttackBefore, attacker.getAttack(),
                                attackerSpeedBefore, attacker.getSpeed(),
                                guardian.getName(), guardian.getCamp(), guardian.getPosition(),
                                guardian.getMaxHp(), guardian.getCurrentHp(),
                                guardian.getAttack(), guardian.getAttack(),
                                guardian.getSpeed(), guardian.getSpeed(),
                                getFieldUnitsStatus(),
                                fireDamage, EffectType.TRUE_DAMAGE, DamageType.PHYSICAL,
                                attacker.getName() + "触发穿云剑");
                        // 死亡日志
                        if (!deadUnits.isEmpty()) {
                            Map<String, Object[]> deadStatus = new HashMap<>();
                            deadUnits.forEach(x -> {
                                List<String> strings = Arrays.asList(x.split("_"));
                                deadStatus.put(strings.get(0), new Object[]{Integer.parseInt(strings.get(1)), 0, 0});
                            });

                            addMultiTargetLog("UNIT_DEATH",
                                    "SYSTEM", null, 0,
                                    0, 0, 0, 0, 0, 0,
                                    deadUnits, null,
                                    deadStatus,
                                    getFieldUnitsStatus(),
                                    0, null, null,
                                    "真实伤害");
                            //触发死亡技能
                            for (Guardian g : deadGuardians) {
                                triggerOnDeathSkills(g);
                            }

                        } else {
                            //触发受击技能
                            triggerOnAttackedSkills(guardian,EffectType.TRUE_DAMAGE);
                        }
                    }
                }
                break;
            case "圣灵天将":
                if (skillLevel[1] > 0) {
//                    圣灵斩Lv1攻击武圣单位时，额外造成150点真实伤害；；
                    List<Guardian> enemieList = attacker.getCamp() == Camp.A ?
                            campB.stream().filter(g -> !g.isDead() && !g.isOnField()).collect(Collectors.toList()) :
                            campA.stream().filter(g -> !g.isDead() && !g.isOnField()).collect(Collectors.toList());
                    if (Xtool.isNotNull(enemieList)) {
                        List<String> deadUnits = new ArrayList<>();
                        List<Guardian> deadGuardians = new ArrayList<>();
                        Guardian guardian = enemieList.get(0);
                        int fireDamage = 150 * skillLevel[1];
                        guardian.setCurrentHp(guardian.getCurrentHp() - fireDamage);
                        if (guardian.getCurrentHp() <= 0) {
                            guardian.setDead(true);
                            guardian.setOnField(false);
                            deadUnits.add(guardian.getCamp() + guardian.getName() + "_" + guardian.getPosition());
                            deadGuardians.add(guardian);
                        }
                        addLog("圣灵斩",
                                attacker.getName(), attacker.getCamp(), attacker.getPosition(),
                                attackerHpBefore, attacker.getCurrentHp(),
                                attackerAttackBefore, attacker.getAttack(),
                                attackerSpeedBefore, attacker.getSpeed(),
                                guardian.getName(), guardian.getCamp(), guardian.getPosition(),
                                guardian.getMaxHp(), guardian.getCurrentHp(),
                                guardian.getAttack(), guardian.getAttack(),
                                guardian.getSpeed(), guardian.getSpeed(),
                                getFieldUnitsStatus(),
                                fireDamage, EffectType.TRUE_DAMAGE, DamageType.PHYSICAL,
                                attacker.getName() + "触发圣灵斩");
                        // 死亡日志
                        if (!deadUnits.isEmpty()) {
                            Map<String, Object[]> deadStatus = new HashMap<>();
                            deadUnits.forEach(x -> {
                                List<String> strings = Arrays.asList(x.split("_"));
                                deadStatus.put(strings.get(0), new Object[]{Integer.parseInt(strings.get(1)), 0, 0});
                            });

                            addMultiTargetLog("UNIT_DEATH",
                                    "SYSTEM", null, 0,
                                    0, 0, 0, 0, 0, 0,
                                    deadUnits, null,
                                    deadStatus,
                                    getFieldUnitsStatus(),
                                    0, null, null,
                                    "真实伤害");
                            //触发死亡技能
                            for (Guardian g : deadGuardians) {
                                triggerOnDeathSkills(g);
                            }

                        } else {
                            //触发受击技能
                            triggerOnAttackedSkills(guardian,EffectType.TRUE_DAMAGE);
                        }
                    }
                }
                break;
            case "杨戬":
                if (1 == 1) {
//                    哪吒，穿云斩Lv1普通攻击后，对当前敌方身后一个单位造成125点真实伤害；
                    List<Guardian> enemieList = attacker.getCamp() == Camp.A ?
                            campB.stream().filter(g -> !g.isDead() && !g.isOnField()).collect(Collectors.toList()) :
                            campA.stream().filter(g -> !g.isDead() && !g.isOnField()).collect(Collectors.toList());
                    if (Xtool.isNotNull(enemieList)) {
                        List<String> deadUnits = new ArrayList<>();
                        List<Guardian> deadGuardians = new ArrayList<>();
                        Guardian guardian = enemieList.get(0);
                        int fireDamage = 100 * skillLevel[0];
                        guardian.setCurrentHp(guardian.getCurrentHp() - fireDamage);
                        if (guardian.getCurrentHp() <= 0) {
                            guardian.setDead(true);
                            guardian.setOnField(false);
                            deadUnits.add(guardian.getCamp() + guardian.getName() + "_" + guardian.getPosition());
                            deadGuardians.add(guardian);
                        }
                        addLog("穿云长枪",
                                attacker.getName(), attacker.getCamp(), attacker.getPosition(),
                                attackerHpBefore, attacker.getCurrentHp(),
                                attackerAttackBefore, attacker.getAttack(),
                                attackerSpeedBefore, attacker.getSpeed(),
                                guardian.getName(), guardian.getCamp(), guardian.getPosition(),
                                guardian.getMaxHp(), guardian.getCurrentHp(),
                                guardian.getAttack(), guardian.getAttack(),
                                guardian.getSpeed(), guardian.getSpeed(),
                                getFieldUnitsStatus(),
                                fireDamage, EffectType.TRUE_DAMAGE, DamageType.PHYSICAL,
                                attacker.getName() + "触发穿云长枪");
                        // 死亡日志
                        if (!deadUnits.isEmpty()) {
                            Map<String, Object[]> deadStatus = new HashMap<>();
                            deadUnits.forEach(x -> {
                                List<String> strings = Arrays.asList(x.split("_"));
                                deadStatus.put(strings.get(0), new Object[]{Integer.parseInt(strings.get(1)), 0, 0});
                            });

                            addMultiTargetLog("UNIT_DEATH",
                                    "SYSTEM", null, 0,
                                    0, 0, 0, 0, 0, 0,
                                    deadUnits, null,
                                    deadStatus,
                                    getFieldUnitsStatus(),
                                    0, null, null,
                                    "真实伤害");
                            //触发死亡技能
                            for (Guardian g : deadGuardians) {
                                triggerOnDeathSkills(g);
                            }

                        } else {
                            //触发受击技能
                            triggerOnAttackedSkills(guardian,EffectType.TRUE_DAMAGE);
                        }

                    }
                }
                break;
            case "牛魔王":
                if (1 == 1) {
                    // 熔岩爆发：对敌方全体造成火焰伤害（多目标整合）
                    List<Guardian> offFieldEnemies = defender.getCamp() == Camp.A ?
                            campA.stream().filter(g -> !g.isDead()).collect(Collectors.toList()) :
                            campB.stream().filter(g -> !g.isDead()).collect(Collectors.toList());

                    if (!offFieldEnemies.isEmpty()) {
                        List<String> deadUnits = new ArrayList<>();
                        List<Guardian> deadGuardians = new ArrayList<>();
                        int lavaDamage = 62 * skillLevel[0];
                        List<String> targetNames = new ArrayList<>();
                        Map<String, Object[]> targetStatus = new HashMap<>();

                        offFieldEnemies.forEach(g -> {
                            targetNames.add(g.getName());
                            int hpBefore = g.getMaxHp();
                            g.setCurrentHp(g.getCurrentHp() - lavaDamage);
                            targetStatus.put(g.getCamp() + g.getName(), new Object[]{g.getPosition(), hpBefore, g.getCurrentHp()});
                            if (g.isDead()) {
                                deadUnits.add(g.getCamp() + g.getName() + "_" + g.getPosition());
                                deadGuardians.add(g);
                            }
                        });

                        // 单条日志记录多目标
                        addMultiTargetLog("熔岩爆发",
                                attacker.getName(), attacker.getCamp(), attacker.getPosition(),
                                attackerHpBefore, attacker.getCurrentHp(),
                                attackerAttackBefore, attacker.getAttack(),
                                attackerSpeedBefore, attacker.getSpeed(),
                                targetNames, defender.getCamp(),
                                targetStatus,
                                getFieldUnitsStatus(),
                                lavaDamage, EffectType.FIRE_DAMAGE, DamageType.FIRE,
                                attacker.getName() + "触发熔岩爆发，攻击后对全体造成火焰伤害");
                        // 死亡日志
                        if (!deadUnits.isEmpty()) {
                            Map<String, Object[]> deadStatus = new HashMap<>();
                            deadUnits.forEach(x -> {
                                List<String> strings = Arrays.asList(x.split("_"));
                                deadStatus.put(strings.get(0), new Object[]{Integer.parseInt(strings.get(1)), 0, 0});
                            });

                            addMultiTargetLog("UNIT_DEATH",
                                    "SYSTEM", null, 0,
                                    0, 0, 0, 0, 0, 0,
                                    deadUnits, null,
                                    deadStatus,
                                    getFieldUnitsStatus(),
                                    0, null, null,
                                    "火焰伤害");
                            //触发死亡技能
                            for (Guardian g : deadGuardians) {
                                triggerOnDeathSkills(g);
                            }

                        }
                        //触发受击技能
                        offFieldEnemies.forEach(g -> {
                            //触发受到任意伤害技能
                            triggerOnAttackedSkills(g,EffectType.FIRE_DAMAGE);
                        });
                    }
                }
                break;
            case "圣婴大王":
                if (1 == 1) {
                    //三昧真火Lv1攻击时对敌我双方所有单位造成62火焰伤害；
                    List<Guardian> allUnits = new ArrayList<>();
                    allUnits.addAll(campA);
                    allUnits.addAll(campB);

                    List<Guardian> aliveUnits = allUnits.stream()
                            .filter(g -> !g.isDead())
                            .collect(Collectors.toList());

                    if (!aliveUnits.isEmpty()) {
                        List<String> deadUnits = new ArrayList<>();
                        List<Guardian> deadGuardians = new ArrayList<>();
                        int reduce = 0;
                        reduce = 100 * skillLevel[0]+ attacker.getBuffStacks()*106;
                        List<String> targetNames = new ArrayList<>();
                        Map<String, Object[]> targetStatus = new HashMap<>();

                        int lavaDamage = reduce;
                        aliveUnits.forEach(g -> {
                            targetNames.add(g.getName());
                            int hpBefore = g.getMaxHp();
                            g.setCurrentHp(g.getCurrentHp() - lavaDamage);
                            targetStatus.put(g.getCamp() + g.getName(), new Object[]{g.getPosition(), hpBefore, g.getCurrentHp()});
                            if (g.isDead()) {
                                deadUnits.add(g.getCamp() + g.getName() + "_" + g.getPosition());
                                deadGuardians.add(g);
                            }
                        });

                        addMultiTargetLog("三昧真火",
                                attacker.getName(), attacker.getCamp(), attacker.getPosition(),
                                attacker.getCurrentHp(), attacker.getCurrentHp(),
                                attacker.getAttack(), attacker.getAttack(),
                                attacker.getSpeed(), attacker.getSpeed(),
                                targetNames, null,
                                targetStatus,
                                getFieldUnitsStatus(),
                                lavaDamage, EffectType.FIRE_DAMAGE, DamageType.FIRE,
                                attacker.getName() + "触发熔岩爆发，攻击后对全体造成火焰伤害");
                        // 死亡日志
                        if (!deadUnits.isEmpty()) {
                            Map<String, Object[]> deadStatus = new HashMap<>();
                            deadUnits.forEach(x -> {
                                List<String> strings = Arrays.asList(x.split("_"));
                                deadStatus.put(strings.get(0), new Object[]{Integer.parseInt(strings.get(1)), 0, 0});
                            });

                            addMultiTargetLog("UNIT_DEATH",
                                    "SYSTEM", null, 0,
                                    0, 0, 0, 0, 0, 0,
                                    deadUnits, null,
                                    deadStatus,
                                    getFieldUnitsStatus(),
                                    0, null, null,
                                    "火焰伤害");
                            //触发死亡技能
                            for (Guardian g : deadGuardians) {
                                triggerOnDeathSkills(g);
                            }
                        }
                        //触发受击技能
                        aliveUnits.forEach(g -> {
                            //触发受到任意伤害技能
                            triggerOnAttackedSkills(g,EffectType.FIRE_DAMAGE);
                        });
                    }
                }
                break;
            case "青狮王":
                if (1 == 1) {
                    // 焚烧lv1攻击后对全体敌方造成12点火焰伤害
                    List<Guardian> offFieldEnemies = defender.getCamp() == Camp.A ?
                            campA.stream().filter(g -> !g.isDead()).collect(Collectors.toList()) :
                            campB.stream().filter(g -> !g.isDead()).collect(Collectors.toList());

                    if (!offFieldEnemies.isEmpty()) {
                        List<String> deadUnits = new ArrayList<>();
                        List<Guardian> deadGuardians = new ArrayList<>();
                        int lavaDamage = 12 * skillLevel[0];
                        List<String> targetNames = new ArrayList<>();
                        Map<String, Object[]> targetStatus = new HashMap<>();

                        offFieldEnemies.forEach(g -> {
                            targetNames.add(g.getName());
                            int hpBefore = g.getMaxHp();
                            g.setCurrentHp(g.getCurrentHp() - lavaDamage);
                            targetStatus.put(g.getCamp() + g.getName(), new Object[]{g.getPosition(), hpBefore, g.getCurrentHp()});
                            if (g.isDead()) {
                                deadUnits.add(g.getCamp() + g.getName() + "_" + g.getPosition());
                                deadGuardians.add(g);
                            }
                        });

                        // 单条日志记录多目标
                        addMultiTargetLog("妖火",
                                attacker.getName(), attacker.getCamp(), attacker.getPosition(),
                                attackerHpBefore, attacker.getCurrentHp(),
                                attackerAttackBefore, attacker.getAttack(),
                                attackerSpeedBefore, attacker.getSpeed(),
                                targetNames, defender.getCamp(),
                                targetStatus,
                                getFieldUnitsStatus(),
                                lavaDamage, EffectType.FIRE_DAMAGE, DamageType.FIRE,
                                attacker.getName() + "触发妖火，攻击后对全体造成火焰伤害");
                        // 死亡日志
                        if (!deadUnits.isEmpty()) {
                            Map<String, Object[]> deadStatus = new HashMap<>();
                            deadUnits.forEach(x -> {
                                List<String> strings = Arrays.asList(x.split("_"));
                                deadStatus.put(strings.get(0), new Object[]{Integer.parseInt(strings.get(1)), 0, 0});
                            });

                            addMultiTargetLog("UNIT_DEATH",
                                    "SYSTEM", null, 0,
                                    0, 0, 0, 0, 0, 0,
                                    deadUnits, null,
                                    deadStatus,
                                    getFieldUnitsStatus(),
                                    0, null, null,
                                    "火焰伤害");
                            //触发死亡技能
                            for (Guardian g : deadGuardians) {
                                triggerOnDeathSkills(g);
                            }

                        }
                        //触发受击技能
                        offFieldEnemies.forEach(g -> {
                            //触发受到任意伤害技能
                            triggerOnAttackedSkills(g,EffectType.FIRE_DAMAGE);
                        });
                    }
                }
                break;
            case "赛太岁":
                if (1 == 1) {
                    // 焚烧lv1攻击后对全体敌方造成12点火焰伤害
                    List<Guardian> offFieldEnemies = defender.getCamp() == Camp.A ?
                            campA.stream().filter(g -> !g.isDead()).collect(Collectors.toList()) :
                            campB.stream().filter(g -> !g.isDead()).collect(Collectors.toList());

                    if (!offFieldEnemies.isEmpty()) {
                        List<String> deadUnits = new ArrayList<>();
                        List<Guardian> deadGuardians = new ArrayList<>();
                        int lavaDamage = 12 * skillLevel[0];
                        List<String> targetNames = new ArrayList<>();
                        Map<String, Object[]> targetStatus = new HashMap<>();

                        offFieldEnemies.forEach(g -> {
                            targetNames.add(g.getName());
                            int hpBefore = g.getMaxHp();
                            g.setCurrentHp(g.getCurrentHp() - lavaDamage);
                            targetStatus.put(g.getCamp() + g.getName(), new Object[]{g.getPosition(), hpBefore, g.getCurrentHp()});
                            if (g.isDead()) {
                                deadUnits.add(g.getCamp() + g.getName() + "_" + g.getPosition());
                                deadGuardians.add(g);
                            }
                        });

                        // 单条日志记录多目标
                        addMultiTargetLog("焚烧",
                                attacker.getName(), attacker.getCamp(), attacker.getPosition(),
                                attackerHpBefore, attacker.getCurrentHp(),
                                attackerAttackBefore, attacker.getAttack(),
                                attackerSpeedBefore, attacker.getSpeed(),
                                targetNames, defender.getCamp(),
                                targetStatus,
                                getFieldUnitsStatus(),
                                lavaDamage, EffectType.FIRE_DAMAGE, DamageType.FIRE,
                                attacker.getName() + "触发焚烧，攻击后对全体造成火焰伤害");
                        // 死亡日志
                        if (!deadUnits.isEmpty()) {
                            Map<String, Object[]> deadStatus = new HashMap<>();
                            deadUnits.forEach(x -> {
                                List<String> strings = Arrays.asList(x.split("_"));
                                deadStatus.put(strings.get(0), new Object[]{Integer.parseInt(strings.get(1)), 0, 0});
                            });

                            addMultiTargetLog("UNIT_DEATH",
                                    "SYSTEM", null, 0,
                                    0, 0, 0, 0, 0, 0,
                                    deadUnits, null,
                                    deadStatus,
                                    getFieldUnitsStatus(),
                                    0, null, null,
                                    "火焰伤害");
                            //触发死亡技能
                            for (Guardian g : deadGuardians) {
                                triggerOnDeathSkills(g);
                            }

                        }
                        //触发受击技能
                        offFieldEnemies.forEach(g -> {
                            //触发受到任意伤害技能
                            triggerOnAttackedSkills(g,EffectType.FIRE_DAMAGE);
                        });
                    }
                }
                break;
            case "琵琶精":
                if (2 == 2) {
                    //妖火lv1攻击后对全体敌方造成12点 火焰伤害
                    List<Guardian> offFieldEnemies = defender.getCamp() == Camp.A ?
                            campA.stream().filter(g -> !g.isDead()).collect(Collectors.toList()) :
                            campB.stream().filter(g -> !g.isDead()).collect(Collectors.toList());

                    if (!offFieldEnemies.isEmpty()) {
                        List<String> deadUnits = new ArrayList<>();
                        List<Guardian> deadGuardians = new ArrayList<>();
                        int lavaDamage = 12 * skillLevel[0];
                        List<String> targetNames = new ArrayList<>();
                        Map<String, Object[]> targetStatus = new HashMap<>();

                        offFieldEnemies.forEach(g -> {
                            targetNames.add(g.getName());
                            int hpBefore = g.getMaxHp();
                            g.setCurrentHp(g.getCurrentHp() - lavaDamage);
                            targetStatus.put(g.getCamp() + g.getName(), new Object[]{g.getPosition(), hpBefore, g.getCurrentHp()});
                            if (g.isDead()) {
                                deadUnits.add(g.getCamp() + g.getName() + "_" + g.getPosition());
                                deadGuardians.add(g);
                            }
                        });

                        // 单条日志  记录多目标
                        addMultiTargetLog("妖火",
                                attacker.getName(), attacker.getCamp(), attacker.getPosition(),
                                attackerHpBefore, attacker.getCurrentHp(),
                                attackerAttackBefore, attacker.getAttack(),
                                attackerSpeedBefore, attacker.getSpeed(),
                                targetNames, defender.getCamp(),
                                targetStatus,
                                getFieldUnitsStatus(),
                                lavaDamage, EffectType.FIRE_DAMAGE, DamageType.FIRE,
                                attacker.getName() + "触发妖火，攻击后对全体造成火焰伤害");
                        // 死亡日志
                        if (!deadUnits.isEmpty()) {
                            Map<String, Object[]> deadStatus = new HashMap<>();
                            deadUnits.forEach(x -> {
                                List<String> strings = Arrays.asList(x.split("_"));
                                deadStatus.put(strings.get(0), new Object[]{Integer.parseInt(strings.get(1)), 0, 0});
                            });

                            addMultiTargetLog("UNIT_DEATH",
                                    "SYSTEM", null, 0,
                                    0, 0, 0, 0, 0, 0,
                                    deadUnits, null,
                                    deadStatus,
                                    getFieldUnitsStatus(),
                                    0, null, null,
                                    "火焰伤害");
                            //触发死亡技能
                            for (Guardian g : deadGuardians) {
                                triggerOnDeathSkills(g);
                            }

                        }
                        //触发受击技能
                        offFieldEnemies.forEach(g -> {
                            //触发受到任意伤害技能
                            triggerOnAttackedSkills(g,EffectType.FIRE_DAMAGE);
                        });
                    }
                }
                break;
        }
    }


    // 触发死亡相关技能
    private void triggerOnDeathSkills(Guardian v) {
        if (Xtool.isNull(v.getEffects().get(EffectType.SILENCE)) && !v.isDead() && v.getName().equals("燃灯道人")) {
            List<Guardian> offFieldEnemies = v.getCamp() == Camp.A ?
                    campB.stream().filter(g -> !g.isDead()).collect(Collectors.toList()) :
                    campA.stream().filter(g -> !g.isDead()).collect(Collectors.toList());
            if (Xtool.isNotNull(offFieldEnemies)) {
                Guardian minHpPerson = offFieldEnemies.get(0); // 先默认第一个为最大
                for (Guardian p : offFieldEnemies) {
                    // 如果当前对象年龄大于已记录的最大年龄，更新
                    if (p.getCurrentHp() > minHpPerson.getCurrentHp()) {
                        minHpPerson = p;
                    }
                }
                List<Guardian> deadGuardians = new ArrayList<>();
                List<String> deadUnits = new ArrayList<>();
                int[] skillLevel = CardSkillLevelUtil.calculateSkillLevels(v.getLevel(), v.getStar().doubleValue());
                if (skillLevel[1] > 0) {
                    int damage = 325 * skillLevel[1] + zhongyuedadiHuan(v);
                    minHpPerson.setCurrentHp(minHpPerson.getCurrentHp() - damage);
                    if (minHpPerson.isDead()) {
                        deadUnits.add(minHpPerson.getCamp() + minHpPerson.getName() + "_" + minHpPerson.getPosition());
                        deadGuardians.add(minHpPerson);
                    }
                    //；信念报偿Lv1死亡时，对敌方血量最小者造成325点飞弹伤害；
                    addLog("信念报偿",
                            v.getName(), v.getCamp(), v.getPosition(),
                            v.getMaxHp(), v.getCurrentHp(),
                            v.getAttack(), v.getAttack(),
                            v.getSpeed(), v.getSpeed(),
                            minHpPerson.getName(), minHpPerson.getCamp(), minHpPerson.getPosition(),
                            minHpPerson.getMaxHp(), minHpPerson.getCurrentHp(),
                            minHpPerson.getAttack(), minHpPerson.getAttack(),
                            minHpPerson.getSpeed(), minHpPerson.getSpeed(),
                            getFieldUnitsStatus(),
                            damage, EffectType.MISSILE_DAMAGE, DamageType.MISSILE,
                            v.getName() + "死亡触发信念报偿");
                    // 死亡日志
                    if (!deadUnits.isEmpty()) {
                        Map<String, Object[]> deadStatus = new HashMap<>();
                        deadUnits.forEach(x -> {
                            List<String> strings = Arrays.asList(x.split("_"));
                            deadStatus.put(strings.get(0), new Object[]{Integer.parseInt(strings.get(1)), 0, 0});
                        });

                        addMultiTargetLog("UNIT_DEATH",
                                "SYSTEM", null, 0,
                                0, 0, 0, 0, 0, 0,
                                deadUnits, null,
                                deadStatus,
                                getFieldUnitsStatus(),
                                0, null, null,
                                "飞弹死亡");
                        //触发死亡技能
                        for (Guardian g : deadGuardians) {
                            triggerOnDeathSkills(g);
                        }
                    } else {
                        triggerOnAttackedSkills(minHpPerson,EffectType.MISSILE_DAMAGE);
                    }
                }


            }

        }

        if (Xtool.isNull(v.getEffects().get(EffectType.SILENCE)) && !v.isDead() && v.getName().equals("鹏魔王")) {
            List<Guardian> offFieldEnemies = v.getCamp() == Camp.A ?
                    campB.stream().filter(g -> !g.isDead()).collect(Collectors.toList()) :
                    campA.stream().filter(g -> !g.isDead()).collect(Collectors.toList());
            if (!offFieldEnemies.isEmpty()) {
                List<String> targetNames = new ArrayList<>();
                Map<String, Object[]> targetStatus = new HashMap<>();
                int[] skillLevel = CardSkillLevelUtil.calculateSkillLevels(v.getLevel(), v.getStar().doubleValue());
                for (Guardian g : offFieldEnemies) {
                    int poisonValue = 10 * skillLevel[0];
                    targetNames.add(g.getName());
                    int hpBefore = g.getMaxHp();
                    int poisonDamage = g.getEffects().getOrDefault(EffectType.POISON, 0);
                    g.getEffects().put(EffectType.POISON, poisonValue + poisonDamage);
                    targetStatus.put(g.getCamp() + g.getName(), new Object[]{g.getPosition(), hpBefore, g.getCurrentHp()});
                }
                // 单条日志记录多目标
                addMultiTargetLog("死雨风暴",
                        v.getName(), v.getCamp(), v.getPosition(),
                        v.getMaxHp(), v.getCurrentHp(),
                        v.getAttack(), v.getAttack(),
                        v.getSpeed(), v.getSpeed(),
                        targetNames, offFieldEnemies.get(0).getCamp(),
                        targetStatus,
                        getFieldUnitsStatus(),
                        0, EffectType.POISON, DamageType.POISON,
                        v.getName() + "触发死雨风暴，死亡时，敌方所有生物中毒每");

                Guardian enemie = v.getCamp() == Camp.A ? fieldB : fieldA;
                if (!enemie.isDead() && skillLevel[1] > 0) {
                    int poisonDamage = enemie.getEffects().getOrDefault(EffectType.POISON, 0);
                    int poisonValue = 70 * skillLevel[1];
                    enemie.getEffects().put(EffectType.POISON, poisonValue + poisonDamage);
                    addLog("死亡徘徊",
                            v.getName(), v.getCamp(), v.getPosition(),
                            v.getMaxHp(), v.getCurrentHp(),
                            v.getAttack(), v.getAttack(),
                            v.getSpeed(), v.getSpeed(),
                            enemie.getName(), enemie.getCamp(), enemie.getPosition(),
                            enemie.getMaxHp(), enemie.getCurrentHp(),
                            enemie.getAttack(), enemie.getAttack(),
                            enemie.getSpeed(), enemie.getSpeed(),
                            getFieldUnitsStatus(),
                            poisonValue, EffectType.POISON, DamageType.POISON,
                            v.getName() + "触发死亡徘徊");
                }
            }
        }

        if (Xtool.isNull(v.getEffects().get(EffectType.SILENCE)) && v.getName().equals("陆压道君")) {
            List<Guardian> immortalAllies = v.getCamp() == Camp.A ?
                    campA.stream().filter(g -> !g.isDead() && g.getRace() == Race.IMMORTAL).collect(Collectors.toList()) :
                    campB.stream().filter(g -> !g.isDead() && g.getRace() == Race.IMMORTAL).collect(Collectors.toList());
            if (Xtool.isNotNull(immortalAllies)) {
                int[] skillLevel = CardSkillLevelUtil.calculateSkillLevels(v.getLevel(), v.getStar().doubleValue());
                if (!immortalAllies.isEmpty() && skillLevel[1] > 0) {
                    int heal = 70 * skillLevel[1];
                    List<String> targetNames = new ArrayList<>();
                    Map<String, Object[]> targetStatus = new HashMap<>();

                    immortalAllies.forEach(g -> {
                        targetNames.add(g.getName());
                        int hpBefore = g.getMaxHp();
                        g.setCurrentHp(g.getCurrentHp() + heal);
                        targetStatus.put(g.getCamp() + g.getName(), new Object[]{g.getPosition(), hpBefore, g.getCurrentHp()});
                    });

                    addMultiTargetLog("舍身取义",
                            v.getName(), v.getCamp(), v.getPosition(),
                            v.getCurrentHp(), v.getCurrentHp(),
                            v.getAttack(), v.getAttack(),
                            v.getSpeed(), v.getSpeed(),
                            targetNames, v.getCamp(),
                            targetStatus,
                            getFieldUnitsStatus(),
                            heal, EffectType.HEAL, null,
                            "陆压触发舍身取义，治疗我方仙界单位");
                    immortalAllies.forEach(g -> {
                        triggerOnHelSkills(g);

                    });
                }
            }

        }
        //句芒，残酷收割Lv1每当有生物死亡时，回复自身6%最大生命；
        if (campA.stream().anyMatch(g -> g.getName().equals("句芒") && !g.isDead())) {
            Guardian changsheng = campA.stream()
                    .filter(g -> g.getName().equals("句芒") && !g.isDead())
                    .findFirst().get();
            if (Xtool.isNull(changsheng.getEffects().get(EffectType.SILENCE))) {
                int hpBefore = changsheng.getMaxHp();
                int attackBefore = changsheng.getAttack();
                int[] skillLevel = CardSkillLevelUtil.calculateSkillLevels(changsheng.getLevel(), changsheng.getStar().doubleValue());
                if (skillLevel[1] > 0) {
                    int hel = (int) (0.06 * skillLevel[1] * changsheng.getMaxHp());
                    changsheng.setCurrentHp(changsheng.getCurrentHp() + hel);
                    addLog("残酷收割",
                            changsheng.getName(), changsheng.getCamp(), changsheng.getPosition(),
                            hpBefore, changsheng.getCurrentHp(),
                            attackBefore, changsheng.getAttack(),
                            changsheng.getSpeed(), changsheng.getSpeed(),
                            changsheng.getName(), changsheng.getCamp(), changsheng.getPosition(),
                            hpBefore, changsheng.getCurrentHp(),
                            attackBefore, changsheng.getAttack(),
                            changsheng.getSpeed(), changsheng.getSpeed(),
                            getFieldUnitsStatus(),
                            hel, EffectType.HP_UP, null,
                            changsheng.getName() + "触发残酷收割，回复自身6%最大生命");
                }
            }

        }

        if (campB.stream().anyMatch(g -> g.getName().equals("句芒") && !g.isDead())) {
            Guardian changsheng = campB.stream()
                    .filter(g -> g.getName().equals("句芒") && !g.isDead())
                    .findFirst().get();
            if (Xtool.isNull(changsheng.getEffects().get(EffectType.SILENCE))) {
                int hpBefore = changsheng.getMaxHp();
                int attackBefore = changsheng.getAttack();
                int[] skillLevel = CardSkillLevelUtil.calculateSkillLevels(changsheng.getLevel(), changsheng.getStar().doubleValue());
                if (skillLevel[1] > 0) {
                    int hel = (int) (0.06 * skillLevel[1] * changsheng.getMaxHp());
                    changsheng.setCurrentHp(changsheng.getCurrentHp() + hel);
                    addLog("残酷收割",
                            changsheng.getName(), changsheng.getCamp(), changsheng.getPosition(),
                            hpBefore, changsheng.getCurrentHp(),
                            attackBefore, changsheng.getAttack(),
                            changsheng.getSpeed(), changsheng.getSpeed(),
                            changsheng.getName(), changsheng.getCamp(), changsheng.getPosition(),
                            hpBefore, changsheng.getCurrentHp(),
                            attackBefore, changsheng.getAttack(),
                            changsheng.getSpeed(), changsheng.getSpeed(),
                            getFieldUnitsStatus(),
                            hel, EffectType.HP_UP, null,
                            changsheng.getName() + "触发残酷收割，回复自身6%最大生命");
                }
            }
        }


        // 牛魔王鲜血盛宴
        if (campA.stream().anyMatch(g -> g.getName().equals("牛魔王") && !g.isDead() && g.getBuffStacks() < 3)) {
            Guardian changsheng = campA.stream()
                    .filter(g -> g.getName().equals("牛魔王") && !g.isDead())
                    .findFirst().get();
            if (Xtool.isNull(changsheng.getEffects().get(EffectType.SILENCE))) {
                int hpBefore = changsheng.getMaxHp();
                int attackBefore = changsheng.getAttack();
                int[] skillLevel = CardSkillLevelUtil.calculateSkillLevels(changsheng.getLevel(), changsheng.getStar().doubleValue());
                if (skillLevel[1] > 0) {
                    int hel = 0;
                    if (duoBaoGuanHuan()) {
                        hel = 117 * skillLevel[1];
                    }
                    changsheng.setBuffStacks(changsheng.getBuffStacks() + 1);
                    changsheng.setMaxHp(changsheng.getMaxHp() + hel);
                    changsheng.setCurrentHp(changsheng.getCurrentHp() + hel);

                    addLog("鲜血盛宴",
                            changsheng.getName(), changsheng.getCamp(), changsheng.getPosition(),
                            hpBefore, changsheng.getCurrentHp(),
                            attackBefore, changsheng.getAttack(),
                            changsheng.getSpeed(), changsheng.getSpeed(),
                            changsheng.getName(), changsheng.getCamp(), changsheng.getPosition(),
                            hpBefore, changsheng.getCurrentHp(),
                            attackBefore, changsheng.getAttack(),
                            changsheng.getSpeed(), changsheng.getSpeed(),
                            getFieldUnitsStatus(),
                            hel, EffectType.HP_UP, null,
                            changsheng.getName() + "触发鲜血盛宴，生命上限提升");
                }

            }

        }

        if (campB.stream().anyMatch(g -> g.getName().equals("牛魔王") && !g.isDead() && g.getBuffStacks() < 3)) {
            Guardian changsheng = campB.stream()
                    .filter(g -> g.getName().equals("牛魔王") && !g.isDead())
                    .findFirst().get();
            if (Xtool.isNull(changsheng.getEffects().get(EffectType.SILENCE))) {
                int hpBefore = changsheng.getMaxHp();
                int attackBefore = changsheng.getAttack();
                int[] skillLevel = CardSkillLevelUtil.calculateSkillLevels(changsheng.getLevel(), changsheng.getStar().doubleValue());
                if (skillLevel[1] > 0) {
                    int hel = 0;
                    if (duoBaoGuanHuan()) {
                        hel = 117 * skillLevel[1];
                    }
                    changsheng.setBuffStacks(changsheng.getBuffStacks() + 1);
                    changsheng.setMaxHp(changsheng.getMaxHp() + hel);
                    changsheng.setCurrentHp(changsheng.getCurrentHp() + hel);


                    addLog("鲜血盛宴",
                            changsheng.getName(), changsheng.getCamp(), changsheng.getPosition(),
                            hpBefore, changsheng.getCurrentHp(),
                            attackBefore, changsheng.getAttack(),
                            changsheng.getSpeed(), changsheng.getSpeed(),
                            changsheng.getName(), changsheng.getCamp(), changsheng.getPosition(),
                            hpBefore, changsheng.getCurrentHp(),
                            attackBefore, changsheng.getAttack(),
                            changsheng.getSpeed(), changsheng.getSpeed(),
                            getFieldUnitsStatus(),
                            hel, EffectType.HP_UP, null,
                            changsheng.getName() + "触发鲜血盛宴，生命上限提升");
                }

            }

        }

        if (campA.stream().anyMatch(g -> g.getName().equals("鲤鱼精") && !g.isDead() && g.getBuffStacks() < 3)) {
            Guardian changsheng = campA.stream()
                    .filter(g -> g.getName().equals("鲤鱼精") && !g.isDead())
                    .findFirst().get();
            if (Xtool.isNull(changsheng.getEffects().get(EffectType.SILENCE))) {
                int hpBefore = changsheng.getMaxHp();
                int attackBefore = changsheng.getAttack();
                int[] skillLevel = CardSkillLevelUtil.calculateSkillLevels(changsheng.getLevel(), changsheng.getStar().doubleValue());
                if (skillLevel[1] > 0) {
                    int hel = 0;
                    if (duoBaoGuanHuan()) {
                        hel = 117 * skillLevel[1];
                    }
                    changsheng.setBuffStacks(changsheng.getBuffStacks() + 1);
                    changsheng.setMaxHp(changsheng.getMaxHp() + hel);
                    changsheng.setCurrentHp(changsheng.getCurrentHp() + hel);

                    addLog("如鱼得水",
                            changsheng.getName(), changsheng.getCamp(), changsheng.getPosition(),
                            hpBefore, changsheng.getCurrentHp(),
                            attackBefore, changsheng.getAttack(),
                            changsheng.getSpeed(), changsheng.getSpeed(),
                            changsheng.getName(), changsheng.getCamp(), changsheng.getPosition(),
                            hpBefore, changsheng.getCurrentHp(),
                            attackBefore, changsheng.getAttack(),
                            changsheng.getSpeed(), changsheng.getSpeed(),
                            getFieldUnitsStatus(),
                            hel, EffectType.HP_UP, null,
                            changsheng.getName() + "触发如鱼得水，生命上限提升");
                }

            }

        }

        if (campB.stream().anyMatch(g -> g.getName().equals("鲤鱼精") && !g.isDead() && g.getBuffStacks() < 3)) {
            Guardian changsheng = campB.stream()
                    .filter(g -> g.getName().equals("鲤鱼精") && !g.isDead())
                    .findFirst().get();
            if (Xtool.isNull(changsheng.getEffects().get(EffectType.SILENCE))) {
                int hpBefore = changsheng.getMaxHp();
                int attackBefore = changsheng.getAttack();
                int[] skillLevel = CardSkillLevelUtil.calculateSkillLevels(changsheng.getLevel(), changsheng.getStar().doubleValue());
                if (skillLevel[1] > 0) {
                    int hel = 0;
                    if (duoBaoGuanHuan()) {
                        hel = 117 * skillLevel[1];
                    }
                    changsheng.setBuffStacks(changsheng.getBuffStacks() + 1);
                    changsheng.setMaxHp(changsheng.getMaxHp() + hel);
                    changsheng.setCurrentHp(changsheng.getCurrentHp() + hel);


                    addLog("如鱼得水",
                            changsheng.getName(), changsheng.getCamp(), changsheng.getPosition(),
                            hpBefore, changsheng.getCurrentHp(),
                            attackBefore, changsheng.getAttack(),
                            changsheng.getSpeed(), changsheng.getSpeed(),
                            changsheng.getName(), changsheng.getCamp(), changsheng.getPosition(),
                            hpBefore, changsheng.getCurrentHp(),
                            attackBefore, changsheng.getAttack(),
                            changsheng.getSpeed(), changsheng.getSpeed(),
                            getFieldUnitsStatus(),
                            hel, EffectType.HP_UP, null,
                            changsheng.getName() + "触发如鱼得水，生命上限提升");
                }

            }

        }


        // 紫薇大帝，背水一战Lv1我方单位死亡时，增加自身攻击50，最多叠加4次；
        if (v.getCamp() == Camp.A && campA.stream().anyMatch(g -> g.getName().equals("紫薇大帝") && !g.isDead() && g.getBuffStacks() < 4)) {
            Guardian changsheng = campA.stream()
                    .filter(g -> g.getName().equals("紫薇大帝") && !g.isDead())
                    .findFirst().get();
            if (Xtool.isNull(changsheng.getEffects().get(EffectType.SILENCE))) {
                int hpBefore = changsheng.getMaxHp();
                int attackBefore = changsheng.getAttack();
                int[] skillLevel = CardSkillLevelUtil.calculateSkillLevels(changsheng.getLevel(), changsheng.getStar().doubleValue());
                if (skillLevel[1] > 0) {
                    changsheng.setBuffStacks(changsheng.getBuffStacks() + 1);
                    changsheng.setAttack(changsheng.getAttack() + 50 * skillLevel[1]);

                    addLog("背水一战",
                            changsheng.getName(), changsheng.getCamp(), changsheng.getPosition(),
                            hpBefore, changsheng.getCurrentHp(),
                            attackBefore, changsheng.getAttack(),
                            changsheng.getSpeed(), changsheng.getSpeed(),
                            changsheng.getName(), changsheng.getCamp(), changsheng.getPosition(),
                            hpBefore, changsheng.getCurrentHp(),
                            attackBefore, changsheng.getAttack(),
                            changsheng.getSpeed(), changsheng.getSpeed(),
                            getFieldUnitsStatus(),
                            50 * skillLevel[1], EffectType.ATTACK_UP, null,
                            changsheng.getName() + "触发背水一战，攻击提升");
                }

            }

        }

        if (v.getCamp() == Camp.B && campB.stream().anyMatch(g -> g.getName().equals("紫薇大帝") && !g.isDead() && g.getBuffStacks() < 4)) {
            Guardian changsheng = campB.stream()
                    .filter(g -> g.getName().equals("紫薇大帝") && !g.isDead())
                    .findFirst().get();
            if (Xtool.isNull(changsheng.getEffects().get(EffectType.SILENCE))) {
                int hpBefore = changsheng.getMaxHp();
                int attackBefore = changsheng.getAttack();
                int[] skillLevel = CardSkillLevelUtil.calculateSkillLevels(changsheng.getLevel(), changsheng.getStar().doubleValue());
                if (skillLevel[1] > 0) {
                    changsheng.setBuffStacks(changsheng.getBuffStacks() + 1);
                    changsheng.setAttack(changsheng.getAttack() + 50 * skillLevel[1]);


                    addLog("背水一战",
                            changsheng.getName(), changsheng.getCamp(), changsheng.getPosition(),
                            hpBefore, changsheng.getCurrentHp(),
                            attackBefore, changsheng.getAttack(),
                            changsheng.getSpeed(), changsheng.getSpeed(),
                            changsheng.getName(), changsheng.getCamp(), changsheng.getPosition(),
                            hpBefore, changsheng.getCurrentHp(),
                            attackBefore, changsheng.getAttack(),
                            changsheng.getSpeed(), changsheng.getSpeed(),
                            getFieldUnitsStatus(),
                            50 * skillLevel[1], EffectType.ATTACK_UP, null,
                            changsheng.getName() + "触发背水一战，攻击提升");
                }

            }

        }

        // 洛水歌声Lv1每当敌方死亡时，提高我方场上单位的攻击104点，最多叠加3层
        if (v.getCamp() == Camp.B && campA.stream().anyMatch(g -> g.getName().equals("洛神") && !g.isDead() && fieldA.getBuffLuoShens() < 3)) {
            int attack = 104;
            Guardian luoshen = campA.stream()
                    .filter(g -> g.getName().equals("洛神") && !g.isDead())
                    .findFirst().get();
            int[] skillLevel = CardSkillLevelUtil.calculateSkillLevels(luoshen.getLevel(), luoshen.getStar().doubleValue());
            if (skillLevel[1] > 0) {
                attack = attack * skillLevel[1];
                if (fieldA != null && !fieldA.isDead()) {
                    int hpBefore = fieldA.getMaxHp();
                    int attackBefore = fieldA.getAttack();

                    fieldA.setBuffLuoShens(fieldA.getBuffLuoShens() + 1);
                    fieldA.setAttack(fieldA.getAttack() + attack);

                    addLog("洛水歌声",
                            luoshen.getName(), luoshen.getCamp(), luoshen.getPosition(),
                            luoshen.getMaxHp(), luoshen.getCurrentHp(),
                            luoshen.getAttack(), fieldA.getAttack(),
                            luoshen.getSpeed(), luoshen.getSpeed(),
                            fieldA.getName(), fieldA.getCamp(), fieldA.getPosition(),
                            hpBefore, fieldA.getCurrentHp(),
                            attackBefore, fieldA.getAttack(),
                            fieldA.getSpeed(), fieldA.getSpeed(),
                            getFieldUnitsStatus(),
                            attack, EffectType.ATTACK_UP, null,
                            fieldA.getName() + "触发洛水歌声，攻击力提升");
                }
            }

        }
        if (v.getCamp() == Camp.A && campB.stream().anyMatch(g -> g.getName().equals("洛神") && !g.isDead() && fieldB.getBuffLuoShens() < 3)) {
            int attack = 104;
            Guardian luoshen = campB.stream()
                    .filter(g -> g.getName().equals("洛神") && !g.isDead())
                    .findFirst().get();
            int[] skillLevel = CardSkillLevelUtil.calculateSkillLevels(luoshen.getLevel(), luoshen.getStar().doubleValue());
            if (skillLevel[1] > 0) {
                attack = attack * skillLevel[1];
                if (fieldB != null && !fieldB.isDead()) {
                    int hpBefore = fieldB.getMaxHp();
                    int attackBefore = fieldB.getAttack();

                    fieldB.setBuffLuoShens(fieldB.getBuffLuoShens() + 1);
                    fieldB.setAttack(fieldB.getAttack() + attack);

                    addLog("洛水歌声",
                            luoshen.getName(), luoshen.getCamp(), luoshen.getPosition(),
                            luoshen.getMaxHp(), luoshen.getCurrentHp(),
                            luoshen.getAttack(), fieldB.getAttack(),
                            luoshen.getSpeed(), luoshen.getSpeed(),
                            fieldB.getName(), fieldB.getCamp(), fieldB.getPosition(),
                            hpBefore, fieldB.getCurrentHp(),
                            attackBefore, fieldB.getAttack(),
                            fieldB.getSpeed(), fieldB.getSpeed(),
                            getFieldUnitsStatus(),
                            attack, EffectType.ATTACK_UP, null,
                            fieldB.getName() + "触发洛水歌声，攻击力提升");
                }
            }


        }

        if (v.getCamp() == Camp.B && campA.stream().anyMatch(g -> g.getName().equals("芙蓉仙子") && !g.isOnField() && !g.isDead())) {
            Guardian luoshen = campA.stream()
                    .filter(g -> g.getName().equals("芙蓉仙子") && !g.isDead())
                    .findFirst().get();
            if (fieldA != null && !fieldA.isDead()) {
                int[] skillLevel = CardSkillLevelUtil.calculateSkillLevels(luoshen.getLevel(), luoshen.getStar().doubleValue());
                int heal = 104 * skillLevel[0];
                int hpBefore = fieldA.getMaxHp();
                fieldA.setCurrentHp(fieldA.getCurrentHp() + heal);

                addLog("百花酿",
                        luoshen.getName(), luoshen.getCamp(), luoshen.getPosition(),
                        hpBefore, luoshen.getCurrentHp(),
                        luoshen.getAttack(), luoshen.getAttack(),
                        luoshen.getSpeed(), luoshen.getSpeed(),
                        fieldA.getName(), fieldA.getCamp(), fieldA.getPosition(),
                        hpBefore, fieldA.getCurrentHp(),
                        fieldA.getAttack(), fieldA.getAttack(),
                        fieldA.getSpeed(), fieldA.getSpeed(),
                        getFieldUnitsStatus(),
                        heal, EffectType.HEAL, null,
                        fieldA.getName() + "，每当地方单位死亡，治疗我方场上单位104生命");
                triggerOnHelSkills(fieldA);
            }
        }

        if (v.getCamp() == Camp.A && campB.stream().anyMatch(g -> g.getName().equals("芙蓉仙子") && !g.isOnField() && !g.isDead())) {
            Guardian luoshen = campB.stream()
                    .filter(g -> g.getName().equals("芙蓉仙子") && !g.isDead())
                    .findFirst().get();
            if (fieldB != null && !fieldB.isDead()) {
                int[] skillLevel = CardSkillLevelUtil.calculateSkillLevels(luoshen.getLevel(), luoshen.getStar().doubleValue());
                int heal = 104 * skillLevel[0];
                int hpBefore = fieldB.getMaxHp();
                fieldB.setCurrentHp(fieldB.getCurrentHp() + heal);

                addLog("百花酿",
                        luoshen.getName(), luoshen.getCamp(), luoshen.getPosition(),
                        hpBefore, luoshen.getCurrentHp(),
                        luoshen.getAttack(), luoshen.getAttack(),
                        luoshen.getSpeed(), luoshen.getSpeed(),
                        fieldB.getName(), fieldB.getCamp(), fieldB.getPosition(),
                        hpBefore, fieldB.getCurrentHp(),
                        fieldB.getAttack(), fieldB.getAttack(),
                        fieldB.getSpeed(), fieldB.getSpeed(),
                        getFieldUnitsStatus(),
                        heal, EffectType.HEAL, null,
                        fieldB.getName() + "，每当地方单位死亡，治疗我方场上单位104生命");
                triggerOnHelSkills(fieldB);
            }
        }


        // 长生大帝生生不息（多目标整合）
        if (campA.stream().anyMatch(g -> g.getName().equals("长生大帝") && !g.isDead())) {
            Guardian changsheng = campA.stream()
                    .filter(g -> g.getName().equals("长生大帝") && !g.isDead())
                    .findFirst().get();

            List<Guardian> immortalAllies = campA.stream()
                    .filter(g -> g.getRace() == Race.IMMORTAL && !g.isDead())
                    .collect(Collectors.toList());

            if (!immortalAllies.isEmpty()) {
                int[] skillLevel = CardSkillLevelUtil.calculateSkillLevels(changsheng.getLevel(), changsheng.getStar().doubleValue());
                int heal = 90 * skillLevel[0];
                List<String> targetNames = new ArrayList<>();
                Map<String, Object[]> targetStatus = new HashMap<>();

                immortalAllies.forEach(g -> {
                    targetNames.add(g.getName());
                    int hpBefore = g.getMaxHp();
                    g.setCurrentHp(g.getCurrentHp() + heal);
                    targetStatus.put(g.getCamp() + g.getName(), new Object[]{g.getPosition(), hpBefore, g.getCurrentHp()});
                });

                addMultiTargetLog("生生不息",
                        changsheng.getName(), Camp.A, changsheng.getPosition(),
                        changsheng.getCurrentHp(), changsheng.getCurrentHp(),
                        changsheng.getAttack(), changsheng.getAttack(),
                        changsheng.getSpeed(), changsheng.getSpeed(),
                        targetNames, Camp.A,
                        targetStatus,
                        getFieldUnitsStatus(),
                        heal, EffectType.HEAL, null,
                        "长生大帝触发生生不息，治疗我方仙界单位");
                immortalAllies.forEach(g -> {
                    triggerOnHelSkills(g);

                });
            }
        }

        if (campB.stream().anyMatch(g -> g.getName().equals("长生大帝") && !g.isDead())) {
            Guardian changsheng = campB.stream()
                    .filter(g -> g.getName().equals("长生大帝") && !g.isDead())
                    .findFirst().get();

            List<Guardian> immortalAllies = campB.stream()
                    .filter(g -> g.getRace() == Race.IMMORTAL && !g.isDead())
                    .collect(Collectors.toList());

            if (!immortalAllies.isEmpty()) {
                int[] skillLevel = CardSkillLevelUtil.calculateSkillLevels(changsheng.getLevel(), changsheng.getStar().doubleValue());
                int heal = 90 * skillLevel[0];
                List<String> targetNames = new ArrayList<>();
                Map<String, Object[]> targetStatus = new HashMap<>();

                immortalAllies.forEach(g -> {
                    targetNames.add(g.getName());
                    int hpBefore = g.getMaxHp();
                    g.setCurrentHp(g.getCurrentHp() + heal);
                    targetStatus.put(g.getCamp() + g.getName(), new Object[]{g.getPosition(), hpBefore, g.getCurrentHp()});
                });

                addMultiTargetLog("生生不息",
                        changsheng.getName(), Camp.B, changsheng.getPosition(),
                        changsheng.getCurrentHp(), changsheng.getCurrentHp(),
                        changsheng.getAttack(), changsheng.getAttack(),
                        changsheng.getSpeed(), changsheng.getSpeed(),
                        targetNames, Camp.B,
                        targetStatus,
                        getFieldUnitsStatus(),
                        heal, EffectType.HEAL, null,
                        "长生大帝触发生生不息，治疗我方仙界单位");
                immortalAllies.forEach(g -> {
                    triggerOnHelSkills(g);
                });
            }
        }
    }

    // 处理回合开始效果
    private void processRoundStartEffects() {
        // 场下中毒效果（批量处理）
        processPoisonEffects();
        // 厚土娘娘后土聚能
        if (Xtool.isNull(fieldA.getEffects().get(EffectType.SILENCE)) && fieldA != null && fieldA.getName().equals("厚土娘娘") && fieldA.getBuffStacks() < 99) {
            int attackBefore = fieldA.getAttack();
            int[] skillLevel = CardSkillLevelUtil.calculateSkillLevels(fieldA.getLevel(), fieldA.getStar().doubleValue());
            if (skillLevel[1] > 0) {
                fieldA.setBuffStacks(fieldA.getBuffStacks() + 1);
                int hel = 0;
                if (duoBaoGuanHuan()) {
                    hel = 197 * skillLevel[1];
                }
                fieldA.setMaxHp(fieldA.getMaxHp() + hel);
                fieldA.setCurrentHp(fieldA.getCurrentHp() + hel);
                fieldA.setAttack(fieldA.getAttack() + 67 * skillLevel[1]);

                addLog("后土聚能",
                        fieldA.getName(), fieldA.getCamp(), fieldA.getPosition(),
                        fieldA.getMaxHp(), fieldA.getCurrentHp(),
                        attackBefore, fieldA.getAttack(),
                        fieldA.getSpeed(), fieldA.getSpeed(),
                        fieldA.getName(), fieldA.getCamp(), fieldA.getPosition(),
                        fieldA.getMaxHp(), fieldA.getCurrentHp(),
                        attackBefore, fieldA.getAttack(),
                        fieldA.getSpeed(), fieldA.getSpeed(),
                        getFieldUnitsStatus(),
                        hel, EffectType.HP_UP, null,
                        fieldA.getName() + "触发后土聚能，生命上限+" + hel + "，攻击+" + skillLevel[1] * 67);
            }

        }

        if (Xtool.isNull(fieldB.getEffects().get(EffectType.SILENCE)) && fieldB != null && fieldB.getName().equals("厚土娘娘") && fieldB.getBuffStacks() < 99) {
            int attackBefore = fieldB.getAttack();
            int[] skillLevel = CardSkillLevelUtil.calculateSkillLevels(fieldB.getLevel(), fieldB.getStar().doubleValue());
            if (skillLevel[1] > 0) {
                fieldB.setBuffStacks(fieldB.getBuffStacks() + 1);
                int hel = 0;
                if (duoBaoGuanHuan()) {
                    hel = 197 * skillLevel[1];
                }
                fieldB.setMaxHp(fieldB.getMaxHp() + hel);
                fieldB.setCurrentHp(fieldB.getCurrentHp() + hel);
                fieldB.setAttack(fieldB.getAttack() + 67 * skillLevel[1]);

                addLog("后土聚能",
                        fieldB.getName(), fieldB.getCamp(), fieldB.getPosition(),
                        fieldB.getMaxHp(), fieldB.getCurrentHp(),
                        attackBefore, fieldB.getAttack(),
                        fieldB.getSpeed(), fieldB.getSpeed(),
                        fieldB.getName(), fieldB.getCamp(), fieldB.getPosition(),
                        fieldB.getMaxHp(), fieldB.getCurrentHp(),
                        attackBefore, fieldB.getAttack(),
                        fieldB.getSpeed(), fieldB.getSpeed(),
                        getFieldUnitsStatus(),
                        hel, EffectType.HP_UP, null,
                        fieldB.getName() + "触发后土聚能，生命上限+" + hel + "，攻击+" + skillLevel[1] * 67);
            }
        }

        // 阎王生死簿（多目标整合）
        if (campA.stream().anyMatch(g -> g.getName().equals("阎王") && !g.isDead())) {
            Guardian yanwang = campA.stream()
                    .filter(g -> g.getName().equals("阎王") && !g.isDead())
                    .findFirst().get();
            if (Xtool.isNull(yanwang.getEffects().get(EffectType.SILENCE))) {
                int[] skillLevel = CardSkillLevelUtil.calculateSkillLevels(yanwang.getLevel(), yanwang.getStar().doubleValue());
                List<Guardian> allUnits = new ArrayList<>();
                allUnits.addAll(campA);
                allUnits.addAll(campB);

                List<Guardian> aliveUnits = allUnits.stream()
                        .filter(g -> !g.isDead())
                        .collect(Collectors.toList());

                if (!aliveUnits.isEmpty()) {
                    List<String> deadUnits = new ArrayList<>();
                    List<Guardian> deadGuardians = new ArrayList<>();
                    int reduce = 0;
                    if (duoBaoGuanHuan()) {
                        reduce = 100 * skillLevel[0];
                    }
                    List<String> targetNames = new ArrayList<>();
                    Map<String, Object[]> targetStatus = new HashMap<>();

                    int finalReduce = reduce;
                    aliveUnits.forEach(g -> {
                        targetNames.add(g.getName());
                        g.setMaxHp(g.getMaxHp() - finalReduce);
                        int maxHpBefore = g.getMaxHp();
                        targetStatus.put(g.getCamp() + g.getName(), new Object[]{g.getPosition(), maxHpBefore, g.getCurrentHp()});
                        if (g.isDead()) {
                            deadUnits.add(g.getCamp() + g.getName() + "_" + g.getPosition());
                            deadGuardians.add(g);
                        }
                    });

                    addMultiTargetLog("生死簿",
                            yanwang.getName(), Camp.A, yanwang.getPosition(),
                            yanwang.getCurrentHp(), yanwang.getCurrentHp(),
                            yanwang.getAttack(), yanwang.getAttack(),
                            yanwang.getSpeed(), yanwang.getSpeed(),
                            targetNames, null,
                            targetStatus,
                            getFieldUnitsStatus(),
                            reduce, EffectType.MAX_HP_DOWN, null,
                            "阎王触发生死簿，降低全体单位生命上限");
                    // 死亡日志
                    if (!deadUnits.isEmpty()) {
                        Map<String, Object[]> deadStatus = new HashMap<>();
                        deadUnits.forEach(x -> {
                            List<String> strings = Arrays.asList(x.split("_"));
                            deadStatus.put(strings.get(0), new Object[]{Integer.parseInt(strings.get(1)), 0, 0});
                        });

                        addMultiTargetLog("UNIT_DEATH",
                                "SYSTEM", null, 0,
                                0, 0, 0, 0, 0, 0,
                                deadUnits, null,
                                deadStatus,
                                getFieldUnitsStatus(),
                                0, null, null,
                                "降低单位生命上限");
                        //触发死亡技能
                        for (Guardian g : deadGuardians) {
                            triggerOnDeathSkills(g);
                        }
                    }
                    //触发受击技能
                    aliveUnits.forEach(g -> {
                        //触发受到任意伤害技能
                        triggerOnAttackedSkills(g,EffectType.MAX_HP_DOWN);
                    });

                }
            }


        }

        if (campB.stream().anyMatch(g -> g.getName().equals("阎王") && !g.isDead())) {
            Guardian yanwang = campB.stream()
                    .filter(g -> g.getName().equals("阎王") && !g.isDead())
                    .findFirst().get();
            if (Xtool.isNull(yanwang.getEffects().get(EffectType.SILENCE))) {
                int[] skillLevel = CardSkillLevelUtil.calculateSkillLevels(yanwang.getLevel(), yanwang.getStar().doubleValue());
                List<Guardian> allUnits = new ArrayList<>();
                allUnits.addAll(campA);
                allUnits.addAll(campB);

                List<Guardian> aliveUnits = allUnits.stream()
                        .filter(g -> !g.isDead())
                        .collect(Collectors.toList());

                if (!aliveUnits.isEmpty()) {
                    List<String> deadUnits = new ArrayList<>();
                    List<Guardian> deadGuardians = new ArrayList<>();
                    int reduce = 0;
                    if (duoBaoGuanHuan()) {
                        reduce = 100 * skillLevel[0];
                    }
                    List<String> targetNames = new ArrayList<>();
                    Map<String, Object[]> targetStatus = new HashMap<>();

                    int finalReduce = reduce;
                    aliveUnits.forEach(g -> {
                        targetNames.add(g.getName());
                        g.setMaxHp(g.getMaxHp() - finalReduce);
                        int maxHpBefore = g.getMaxHp();
                        targetStatus.put(g.getCamp() + g.getName(), new Object[]{g.getPosition(), maxHpBefore, g.getCurrentHp()});
                        if (g.isDead()) {
                            deadUnits.add(g.getCamp() + g.getName() + "_" + g.getPosition());
                            deadGuardians.add(g);
                        }
                    });

                    addMultiTargetLog("生死簿",
                            yanwang.getName(), Camp.B, yanwang.getPosition(),
                            yanwang.getCurrentHp(), yanwang.getCurrentHp(),
                            yanwang.getAttack(), yanwang.getAttack(),
                            yanwang.getSpeed(), yanwang.getSpeed(),
                            targetNames, null,
                            targetStatus,
                            getFieldUnitsStatus(),
                            reduce, EffectType.MAX_HP_DOWN, null,
                            "阎王触发生死簿，降低全体单位生命上限");
                    // 死亡日志
                    if (!deadUnits.isEmpty()) {
                        Map<String, Object[]> deadStatus = new HashMap<>();
                        deadUnits.forEach(x -> {
                            List<String> strings = Arrays.asList(x.split("_"));
                            deadStatus.put(strings.get(0), new Object[]{Integer.parseInt(strings.get(1)), 0, 0});
                        });

                        addMultiTargetLog("UNIT_DEATH",
                                "SYSTEM", null, 0,
                                0, 0, 0, 0, 0, 0,
                                deadUnits, null,
                                deadStatus,
                                getFieldUnitsStatus(),
                                0, null, null,
                                "降低单位生命上限");
                        //触发死亡技能
                        for (Guardian g : deadGuardians) {
                            triggerOnDeathSkills(g);
                        }
                    }
                    //触发受击技能
                    aliveUnits.forEach(g -> {
                        //触发受到任意伤害技能
                        triggerOnAttackedSkills(g,EffectType.MAX_HP_DOWN);
                    });

                }
            }


        }
        if (campA.stream().anyMatch(g -> g.getName().equals("萌年兽") && !g.isDead())) {
            Guardian nianshou = campA.stream()
                    .filter(g -> g.getName().equals("萌年兽") && !g.isDead())
                    .findFirst().get();
            if (Xtool.isNull(nianshou.getEffects().get(EffectType.SILENCE)) && nianshou.getBuffNianShous() < 5) {
                int attackBefore = nianshou.getAttack();
                int[] skillLevel = CardSkillLevelUtil.calculateSkillLevels(nianshou.getLevel(), nianshou.getStar().doubleValue());
                if (skillLevel[1] > 0) {
                    nianshou.setBuffStacks(nianshou.getBuffNianShous() + 1);
                    int hel = 0;
                    if (duoBaoGuanHuan()) {
                        hel = 76 * skillLevel[1];
                    }
                    nianshou.setMaxHp(nianshou.getMaxHp() + hel);
                    nianshou.setCurrentHp(nianshou.getCurrentHp() + hel);
                    if (Xtool.isNotNull(nianshou.getEffects().get(EffectType.FIRE_BOOST))) {
                        nianshou.getEffects().put(EffectType.FIRE_BOOST, nianshou.getEffects().get(EffectType.FIRE_BOOST) + 15 * skillLevel[1]);
                    } else {
                        nianshou.getEffects().put(EffectType.FIRE_BOOST, 15 * skillLevel[1]);
                    }

                    addLog("幸运年糕",
                            nianshou.getName(), nianshou.getCamp(), nianshou.getPosition(),
                            nianshou.getMaxHp(), nianshou.getCurrentHp(),
                            attackBefore, nianshou.getAttack(),
                            nianshou.getSpeed(), nianshou.getSpeed(),
                            nianshou.getName(), nianshou.getCamp(), nianshou.getPosition(),
                            nianshou.getMaxHp(), nianshou.getCurrentHp(),
                            attackBefore, nianshou.getAttack(),
                            nianshou.getSpeed(), nianshou.getSpeed(),
                            getFieldUnitsStatus(),
                            hel, EffectType.HP_UP, null,
                            nianshou.getName() + "触发萌年兽幸运年糕，生命上限+" + hel + "，火焰伤害+" + skillLevel[1] * 15);
                }

            }


        }

        if (campA.stream().anyMatch(g -> g.getName().equals("西岳大帝") && !g.isDead()&&!g.isOnField())) {
            Guardian nianshou = campA.stream()
                    .filter(g -> g.getName().equals("西岳大帝") && !g.isDead())
                    .findFirst().get();
            if (Xtool.isNull(nianshou.getEffects().get(EffectType.SILENCE)) && nianshou.getBuffStacks() < 5) {
                int attackBefore = nianshou.getAttack();
                int[] skillLevel = CardSkillLevelUtil.calculateSkillLevels(nianshou.getLevel(), nianshou.getStar().doubleValue());
                if (skillLevel[1] > 0) {
                    nianshou.setBuffStacks(nianshou.getBuffStacks() + 1);
                    addLog("策兵奇袭",
                            nianshou.getName(), nianshou.getCamp(), nianshou.getPosition(),
                            nianshou.getMaxHp(), nianshou.getCurrentHp(),
                            attackBefore, nianshou.getAttack(),
                            nianshou.getSpeed(), nianshou.getSpeed(),
                            nianshou.getName(), nianshou.getCamp(), nianshou.getPosition(),
                            nianshou.getMaxHp(), nianshou.getCurrentHp(),
                            attackBefore, nianshou.getAttack(),
                            nianshou.getSpeed(), nianshou.getSpeed(),
                            getFieldUnitsStatus(),
                            0, EffectType.HP_UP, null,
                            nianshou.getName() + "触发策兵奇袭，飞弹伤害+" + skillLevel[1] * 16);
                }

            }


        }

        if (campB.stream().anyMatch(g -> g.getName().equals("西岳大帝") && !g.isDead()&&!g.isOnField())) {
            Guardian nianshou = campB.stream()
                    .filter(g -> g.getName().equals("西岳大帝") && !g.isDead())
                    .findFirst().get();
            if (Xtool.isNull(nianshou.getEffects().get(EffectType.SILENCE)) && nianshou.getBuffStacks() < 5) {
                int attackBefore = nianshou.getAttack();
                int[] skillLevel = CardSkillLevelUtil.calculateSkillLevels(nianshou.getLevel(), nianshou.getStar().doubleValue());
                if (skillLevel[1] > 0) {
                    nianshou.setBuffStacks(nianshou.getBuffStacks() + 1);
                    addLog("策兵奇袭",
                            nianshou.getName(), nianshou.getCamp(), nianshou.getPosition(),
                            nianshou.getMaxHp(), nianshou.getCurrentHp(),
                            attackBefore, nianshou.getAttack(),
                            nianshou.getSpeed(), nianshou.getSpeed(),
                            nianshou.getName(), nianshou.getCamp(), nianshou.getPosition(),
                            nianshou.getMaxHp(), nianshou.getCurrentHp(),
                            attackBefore, nianshou.getAttack(),
                            nianshou.getSpeed(), nianshou.getSpeed(),
                            getFieldUnitsStatus(),
                            0, EffectType.HP_UP, null,
                            nianshou.getName() + "触发策兵奇袭，飞弹伤害+" + skillLevel[1] * 16);
                }

            }


        }
        //
        if (campB.stream().anyMatch(g -> g.getName().equals("萌年兽") && !g.isDead())) {

            Guardian nianshou = campB.stream()
                    .filter(g -> g.getName().equals("萌年兽") && !g.isDead())
                    .findFirst().get();
            if (Xtool.isNull(nianshou.getEffects().get(EffectType.SILENCE)) && nianshou.getBuffNianShous() < 5) {
                int attackBefore = nianshou.getAttack();
                int[] skillLevel = CardSkillLevelUtil.calculateSkillLevels(nianshou.getLevel(), nianshou.getStar().doubleValue());
                if (skillLevel[1] > 0) {
                    nianshou.setBuffStacks(nianshou.getBuffNianShous() + 1);
                    int hel = 0;
                    if (duoBaoGuanHuan()) {
                        hel = 76 * skillLevel[1];
                    }
                    nianshou.setMaxHp(nianshou.getMaxHp() + hel);
                    nianshou.setCurrentHp(nianshou.getCurrentHp() + hel);
                    if (Xtool.isNotNull(nianshou.getEffects().get(EffectType.FIRE_BOOST))) {
                        nianshou.getEffects().put(EffectType.FIRE_BOOST, nianshou.getEffects().get(EffectType.FIRE_BOOST) + 15 * skillLevel[1]);
                    } else {
                        nianshou.getEffects().put(EffectType.FIRE_BOOST, 15 * skillLevel[1]);
                    }
                    addLog("幸运年糕",
                            nianshou.getName(), nianshou.getCamp(), nianshou.getPosition(),
                            nianshou.getMaxHp(), nianshou.getCurrentHp(),
                            attackBefore, nianshou.getAttack(),
                            nianshou.getSpeed(), nianshou.getSpeed(),
                            nianshou.getName(), nianshou.getCamp(), nianshou.getPosition(),
                            nianshou.getMaxHp(), nianshou.getCurrentHp(),
                            attackBefore, nianshou.getAttack(),
                            nianshou.getSpeed(), nianshou.getSpeed(),
                            getFieldUnitsStatus(),
                            hel, EffectType.HP_UP, null,
                            nianshou.getName() + "触发萌年兽幸运年糕，生命上限+" + hel + "，火焰伤害+" + skillLevel[1] * 15);
                }

            }

        }
        // A玄冥
//            玄冥，毒入骨髓Lv1场下，每回合令随机敌方中毒每回损失16点生命；
        if (campA.stream().anyMatch(g -> g.getName().equals("玄冥") && !g.isDead() && !g.isOnField())) {
            Guardian daji = campA.stream()
                    .filter(g -> g.getName().equals("玄冥") && !g.isDead() && !g.isOnField())
                    .findFirst().get();
            int[] skillLevel = CardSkillLevelUtil.calculateSkillLevels(daji.getLevel(), daji.getStar().doubleValue());
            int dajiHpBefore = daji.getCurrentHp();
            int dajiAttackBefore = daji.getAttack();
            int dajiSpeedBefore = daji.getSpeed();

            // 毒入骨髓：随机敌方中毒
            List<Guardian> enemies = campB.stream()
                    .filter(g -> !g.isDead())
                    .collect(Collectors.toList());

            if (!enemies.isEmpty()) {
                Guardian randomEnemy = enemies.get(random.nextInt(enemies.size()));
                int targetHpBefore = randomEnemy.getMaxHp();
                int poisonValue = 16 * skillLevel[0];
                int poisonDamage = randomEnemy.getEffects().getOrDefault(EffectType.POISON, 0);
                randomEnemy.getEffects().put(EffectType.POISON, poisonValue + poisonDamage);

                addLog("毒入骨髓",
                        daji.getName(), Camp.A, daji.getPosition(),
                        dajiHpBefore, daji.getCurrentHp(),
                        dajiAttackBefore, daji.getAttack(),
                        dajiSpeedBefore, daji.getSpeed(),
                        randomEnemy.getName(), randomEnemy.getCamp(), randomEnemy.getPosition(),
                        targetHpBefore, randomEnemy.getCurrentHp(),
                        randomEnemy.getAttack(), randomEnemy.getAttack(),
                        randomEnemy.getSpeed(), randomEnemy.getSpeed(),
                        getFieldUnitsStatus(),
                        poisonValue, EffectType.POISON, DamageType.POISON,
                        "玄冥触发毒入骨髓，使敌人中毒");
            }
        }

        // B队玄冥
        if (campB.stream().anyMatch(g -> g.getName().equals("玄冥") && !g.isDead() && !g.isOnField())) {
            Guardian daji = campB.stream()
                    .filter(g -> g.getName().equals("玄冥") && !g.isDead() && !g.isOnField())
                    .findFirst().get();
            int[] skillLevel = CardSkillLevelUtil.calculateSkillLevels(daji.getLevel(), daji.getStar().doubleValue());
            int dajiHpBefore = daji.getCurrentHp();
            int dajiAttackBefore = daji.getAttack();
            int dajiSpeedBefore = daji.getSpeed();


            // 谄媚噬魂：随机敌方中毒
            List<Guardian> enemies = campA.stream()
                    .filter(g -> !g.isDead())
                    .collect(Collectors.toList());

            if (!enemies.isEmpty()) {
                Guardian randomEnemy = enemies.get(random.nextInt(enemies.size()));
                int targetHpBefore = randomEnemy.getMaxHp();
                int poisonValue = 16 * skillLevel[0];
                int poisonDamage = randomEnemy.getEffects().getOrDefault(EffectType.POISON, 0);
                randomEnemy.getEffects().put(EffectType.POISON, poisonValue + poisonDamage);

                addLog("毒入骨髓",
                        daji.getName(), Camp.B, daji.getPosition(),
                        dajiHpBefore, daji.getCurrentHp(),
                        dajiAttackBefore, daji.getAttack(),
                        dajiSpeedBefore, daji.getSpeed(),
                        randomEnemy.getName(), randomEnemy.getCamp(), randomEnemy.getPosition(),
                        targetHpBefore, randomEnemy.getCurrentHp(),
                        randomEnemy.getAttack(), randomEnemy.getAttack(),
                        randomEnemy.getSpeed(), randomEnemy.getSpeed(),
                        getFieldUnitsStatus(),
                        poisonValue, EffectType.POISON, DamageType.POISON,
                        "玄冥触发毒入骨髓，使敌人中毒");
            }
        }

        // A玄冥
//            任意位置，若场上敌方有疾病则每回合令其中毒，受到40点毒素伤害
        if (campA.stream().anyMatch(g -> g.getName().equals("金钩大王") && !g.isDead() && !g.isOnField())) {
            if (!fieldB.isDead() && fieldB.getEffects() != null) {
                Guardian daji = campA.stream()
                        .filter(g -> g.getName().equals("金钩大王") && !g.isDead() && !g.isOnField())
                        .findFirst().get();
                int[] skillLevel = CardSkillLevelUtil.calculateSkillLevels(daji.getLevel(), daji.getStar().doubleValue());
                if (skillLevel[1] > 0) {
                    int dajiHpBefore = daji.getCurrentHp();
                    int dajiAttackBefore = daji.getAttack();
                    int dajiSpeedBefore = daji.getSpeed();
                    int poisonValue = 40 * skillLevel[1];
                    int poisonDamage = fieldB.getEffects().getOrDefault(EffectType.POISON, 0);
                    fieldB.getEffects().put(EffectType.POISON, poisonValue + poisonDamage);

                    addLog("疾病传染",
                            daji.getName(), Camp.A, daji.getPosition(),
                            dajiHpBefore, daji.getCurrentHp(),
                            dajiAttackBefore, daji.getAttack(),
                            dajiSpeedBefore, daji.getSpeed(),
                            fieldB.getName(), fieldB.getCamp(), fieldB.getPosition(),
                            fieldB.getMaxHp(), fieldB.getCurrentHp(),
                            fieldB.getAttack(), fieldB.getAttack(),
                            fieldB.getSpeed(), fieldB.getSpeed(),
                            getFieldUnitsStatus(),
                            poisonValue, EffectType.POISON, DamageType.POISON,
                            "任意位置，若场上敌方有疾病则每回合令其中毒，受到40点毒素伤害");
                }

            }

        }

        // B队玄冥
        if (campB.stream().anyMatch(g -> g.getName().equals("金钩大王") && !g.isDead() && !g.isOnField())) {
            if (!fieldA.isDead() && fieldA.getEffects() != null) {
                Guardian daji = campB.stream()
                        .filter(g -> g.getName().equals("金钩大王") && !g.isDead() && !g.isOnField())
                        .findFirst().get();
                int[] skillLevel = CardSkillLevelUtil.calculateSkillLevels(daji.getLevel(), daji.getStar().doubleValue());
                if (skillLevel[1] > 0) {
                    int dajiHpBefore = daji.getCurrentHp();
                    int dajiAttackBefore = daji.getAttack();
                    int dajiSpeedBefore = daji.getSpeed();
                    int poisonValue = 40 * skillLevel[1];
                    int poisonDamage = fieldA.getEffects().getOrDefault(EffectType.POISON, 0);
                    fieldA.getEffects().put(EffectType.POISON, poisonValue + poisonDamage);

                    addLog("疾病传染",
                            daji.getName(), Camp.B, daji.getPosition(),
                            dajiHpBefore, daji.getCurrentHp(),
                            dajiAttackBefore, daji.getAttack(),
                            dajiSpeedBefore, daji.getSpeed(),
                            fieldA.getName(), fieldA.getCamp(), fieldA.getPosition(),
                            fieldA.getMaxHp(), fieldA.getCurrentHp(),
                            fieldA.getAttack(), fieldA.getAttack(),
                            fieldA.getSpeed(), fieldA.getSpeed(),
                            getFieldUnitsStatus(),
                            poisonValue, EffectType.POISON, DamageType.POISON,
                            "任意位置，若场上敌方有疾病则每回合令其中毒，受到40点毒素伤害");
                }

            }

        }


//        南华真人，魂力飞弹Lv1场下，每当新单位入场时，对场上敌人造成178点飞弹伤害；报复神箭Lv1场下，每回合对场上敌方造成106；镇元子协同Lv1与镇元子在同一队伍时，增加自身453点生命上限，158点攻击，158点速度。
        if (campA.stream().anyMatch(g -> g.getName().equals("南华真人") && !g.isDead() && !g.isOnField())) {
            if (!fieldB.isDead() && fieldB.getEffects() != null) {
                Guardian guardian = campA.stream()
                        .filter(g -> g.getName().equals("南华真人") && !g.isDead() && !g.isOnField())
                        .findFirst().get();
                int[] skillLevel = CardSkillLevelUtil.calculateSkillLevels(guardian.getLevel(), guardian.getStar().doubleValue());
                if (fieldB != null && !fieldB.isDead()) {
                    int damage = 106 * skillLevel[1] + zhongyuedadiHuan(guardian);
                    fieldB.setCurrentHp(fieldB.getCurrentHp() - damage);
                    List<String> deadUnits = new ArrayList<>();

                    if (fieldB.getCurrentHp() <= 0) {
                        fieldB.setDead(true);
                        fieldB.setOnField(false);
                        deadUnits.add(fieldB.getCamp() + fieldB.getName() + "_" + fieldB.getPosition());
                    }
                    addLog("报复神箭",
                            guardian.getName(), guardian.getCamp(), guardian.getPosition(),
                            guardian.getMaxHp(), guardian.getCurrentHp(),
                            guardian.getAttack(), guardian.getAttack(),
                            guardian.getSpeed(), guardian.getSpeed(),
                            fieldB.getName(), fieldB.getCamp(), fieldB.getPosition(),
                            fieldB.getMaxHp(), fieldB.getCurrentHp(),
                            fieldB.getAttack(), fieldB.getAttack(),
                            fieldB.getSpeed(), fieldB.getSpeed(),
                            getFieldUnitsStatus(),
                            damage, EffectType.MISSILE_DAMAGE, DamageType.MISSILE,
                            guardian.getName() + "报复神箭");
                    // 死亡日志
                    if (!deadUnits.isEmpty()) {
                        Map<String, Object[]> deadStatus = new HashMap<>();
                        deadUnits.forEach(x -> {
                            List<String> strings = Arrays.asList(x.split("_"));
                            deadStatus.put(strings.get(0), new Object[]{Integer.parseInt(strings.get(1)), 0, 0});
                        });

                        addMultiTargetLog("UNIT_DEATH",
                                "SYSTEM", null, 0,
                                0, 0, 0, 0, 0, 0,
                                deadUnits, null,
                                deadStatus,
                                getFieldUnitsStatus(),
                                0, null, null,
                                "报复神箭");
                        //触发死亡技能
                        triggerOnDeathSkills(fieldB);

                    } else {
                        //触发受击技能
                        triggerOnAttackedSkills(fieldB,EffectType.MISSILE_DAMAGE);
                    }
                }
            }

        }


        //        南华真人，魂力飞弹Lv1场下，每当新单位入场时，对场上敌人造成178点飞弹伤害；报复神箭Lv1场下，每回合对场上敌方造成106；镇元子协同Lv1与镇元子在同一队伍时，增加自身453点生命上限，158点攻击，158点速度。
        if (campB.stream().anyMatch(g -> g.getName().equals("南华真人") && !g.isDead() && !g.isOnField())) {
            if (!fieldA.isDead() && fieldA.getEffects() != null) {
                Guardian guardian = campB.stream()
                        .filter(g -> g.getName().equals("南华真人") && !g.isDead() && !g.isOnField())
                        .findFirst().get();
                int[] skillLevel = CardSkillLevelUtil.calculateSkillLevels(guardian.getLevel(), guardian.getStar().doubleValue());
                if (fieldA != null && !fieldA.isDead()) {
                    int damage = 106 * skillLevel[1] + zhongyuedadiHuan(guardian);
                    fieldA.setCurrentHp(fieldA.getCurrentHp() - damage);
                    List<String> deadUnits = new ArrayList<>();

                    if (fieldA.getCurrentHp() <= 0) {
                        fieldA.setDead(true);
                        fieldA.setOnField(false);
                        deadUnits.add(fieldA.getCamp() + fieldA.getName() + "_" + fieldA.getPosition());
                    }
                    addLog("报复神箭",
                            guardian.getName(), guardian.getCamp(), guardian.getPosition(),
                            guardian.getMaxHp(), guardian.getCurrentHp(),
                            guardian.getAttack(), guardian.getAttack(),
                            guardian.getSpeed(), guardian.getSpeed(),
                            fieldA.getName(), fieldA.getCamp(), fieldA.getPosition(),
                            fieldA.getMaxHp(), fieldA.getCurrentHp(),
                            fieldA.getAttack(), fieldA.getAttack(),
                            fieldA.getSpeed(), fieldA.getSpeed(),
                            getFieldUnitsStatus(),
                            damage, EffectType.MISSILE_DAMAGE, DamageType.MISSILE,
                            guardian.getName() + "报复神箭");
                    // 死亡日志
                    if (!deadUnits.isEmpty()) {
                        Map<String, Object[]> deadStatus = new HashMap<>();
                        deadUnits.forEach(x -> {
                            List<String> strings = Arrays.asList(x.split("_"));
                            deadStatus.put(strings.get(0), new Object[]{Integer.parseInt(strings.get(1)), 0, 0});
                        });

                        addMultiTargetLog("UNIT_DEATH",
                                "SYSTEM", null, 0,
                                0, 0, 0, 0, 0, 0,
                                deadUnits, null,
                                deadStatus,
                                getFieldUnitsStatus(),
                                0, null, null,
                                "报复神箭");
                        //触发死亡技能
                        triggerOnDeathSkills(fieldA);

                    } else {
                        //触发受击技能
                        triggerOnAttackedSkills(fieldA,EffectType.MISSILE_DAMAGE);
                    }
                }
            }

        }

//        南岳大帝，通灵神箭Lv1每回合对同位置敌方造成42点飞弹伤害；草船借箭Lv1受到火焰伤害时，增加自身飞弹伤害13点，最多叠加99层；大帝协同Lv8与西岳大帝在同一队伍时，增加自身604点生命上限，158点攻击，211点速度。
        if (campA.stream().anyMatch(g -> g.getName().equals("南岳大帝") && !g.isDead() && !g.isOnField())) {
            Guardian guardian = campA.stream()
                    .filter(g -> g.getName().equals("南岳大帝") && !g.isDead() && !g.isOnField())
                    .findFirst().get();
            List<Guardian> aliveUnits = campB.stream()
                    .filter(g -> !g.isDead()&&guardian.getPosition()==g.getPosition())
                    .collect(Collectors.toList());
            if (Xtool.isNotNull(aliveUnits)) {
                Guardian  defender=aliveUnits.get(0);
                int[] skillLevel = CardSkillLevelUtil.calculateSkillLevels(guardian.getLevel(), guardian.getStar().doubleValue());
                if (defender != null && !defender.isDead()) {
                    int damage = 42 * skillLevel[0] + zhongyuedadiHuan(guardian);
                    defender.setCurrentHp(defender.getCurrentHp() - damage);
                    List<String> deadUnits = new ArrayList<>();

                    if (defender.getCurrentHp() <= 0) {
                        defender.setDead(true);
                        defender.setOnField(false);
                        deadUnits.add(defender.getCamp() + defender.getName() + "_" + defender.getPosition());
                    }
                    addLog("通灵神箭",
                            guardian.getName(), guardian.getCamp(), guardian.getPosition(),
                            guardian.getMaxHp(), guardian.getCurrentHp(),
                            guardian.getAttack(), guardian.getAttack(),
                            guardian.getSpeed(), guardian.getSpeed(),
                            defender.getName(), defender.getCamp(), defender.getPosition(),
                            defender.getMaxHp(), defender.getCurrentHp(),
                            defender.getAttack(), defender.getAttack(),
                            defender.getSpeed(), defender.getSpeed(),
                            getFieldUnitsStatus(),
                            damage, EffectType.MISSILE_DAMAGE, DamageType.MISSILE,
                            guardian.getName() + "通灵神箭");
                    // 死亡日志
                    if (!deadUnits.isEmpty()) {
                        Map<String, Object[]> deadStatus = new HashMap<>();
                        deadUnits.forEach(x -> {
                            List<String> strings = Arrays.asList(x.split("_"));
                            deadStatus.put(strings.get(0), new Object[]{Integer.parseInt(strings.get(1)), 0, 0});
                        });

                        addMultiTargetLog("UNIT_DEATH",
                                "SYSTEM", null, 0,
                                0, 0, 0, 0, 0, 0,
                                deadUnits, null,
                                deadStatus,
                                getFieldUnitsStatus(),
                                0, null, null,
                                "通灵神箭");
                        //触发死亡技能
                        triggerOnDeathSkills(defender);

                    } else {
                        //触发受击技能
                        triggerOnAttackedSkills(defender,EffectType.MISSILE_DAMAGE);
                    }
                }
            }

        }


//        南岳大帝，通灵神箭Lv1每回合对同位置敌方造成42点飞弹伤害；草船借箭Lv1受到火焰伤害时，增加自身飞弹伤害13点，最多叠加99层；大帝协同Lv8与西岳大帝在同一队伍时，增加自身604点生命上限，158点攻击，211点速度。
        if (campB.stream().anyMatch(g -> g.getName().equals("南岳大帝") && !g.isDead() && !g.isOnField())) {
            Guardian guardian = campB.stream()
                    .filter(g -> g.getName().equals("南岳大帝") && !g.isDead() && !g.isOnField())
                    .findFirst().get();
            List<Guardian> aliveUnits = campA.stream()
                    .filter(g -> !g.isDead()&&guardian.getPosition()==g.getPosition())
                    .collect(Collectors.toList());
            if (Xtool.isNotNull(aliveUnits)) {
                Guardian  defender=aliveUnits.get(0);
                int[] skillLevel = CardSkillLevelUtil.calculateSkillLevels(guardian.getLevel(), guardian.getStar().doubleValue());
                if (defender != null && !defender.isDead()) {
                    int damage = 42 * skillLevel[0] + zhongyuedadiHuan(guardian);
                    defender.setCurrentHp(defender.getCurrentHp() - damage);
                    List<String> deadUnits = new ArrayList<>();

                    if (defender.getCurrentHp() <= 0) {
                        defender.setDead(true);
                        defender.setOnField(false);
                        deadUnits.add(defender.getCamp() + defender.getName() + "_" + defender.getPosition());
                    }
                    addLog("通灵神箭",
                            guardian.getName(), guardian.getCamp(), guardian.getPosition(),
                            guardian.getMaxHp(), guardian.getCurrentHp(),
                            guardian.getAttack(), guardian.getAttack(),
                            guardian.getSpeed(), guardian.getSpeed(),
                            defender.getName(), defender.getCamp(), defender.getPosition(),
                            defender.getMaxHp(), defender.getCurrentHp(),
                            defender.getAttack(), defender.getAttack(),
                            defender.getSpeed(), defender.getSpeed(),
                            getFieldUnitsStatus(),
                            damage, EffectType.MISSILE_DAMAGE, DamageType.MISSILE,
                            guardian.getName() + "通灵神箭");
                    // 死亡日志
                    if (!deadUnits.isEmpty()) {
                        Map<String, Object[]> deadStatus = new HashMap<>();
                        deadUnits.forEach(x -> {
                            List<String> strings = Arrays.asList(x.split("_"));
                            deadStatus.put(strings.get(0), new Object[]{Integer.parseInt(strings.get(1)), 0, 0});
                        });

                        addMultiTargetLog("UNIT_DEATH",
                                "SYSTEM", null, 0,
                                0, 0, 0, 0, 0, 0,
                                deadUnits, null,
                                deadStatus,
                                getFieldUnitsStatus(),
                                0, null, null,
                                "通灵神箭");
                        //触发死亡技能
                        triggerOnDeathSkills(defender);

                    } else {
                        //触发受击技能
                        triggerOnAttackedSkills(defender,EffectType.MISSILE_DAMAGE);
                    }
                }
            }

        }

        //        南岳大帝，通灵神箭Lv1每回合对同位置敌方造成42点飞弹伤害；草船借箭Lv1受到火焰伤害时，增加自身飞弹伤害13点，最多叠加99层；大帝协同Lv8与西岳大帝在同一队伍时，增加自身604点生命上限，158点攻击，211点速度。
        if (campA.stream().anyMatch(g -> g.getName().equals("太岁灵君") && !g.isDead() && !g.isOnField())) {
            Guardian guardian = campA.stream()
                    .filter(g -> g.getName().equals("太岁灵君") && !g.isDead() && !g.isOnField())
                    .findFirst().get();
            List<Guardian> aliveUnits = campB.stream()
                    .filter(g -> !g.isDead()&&guardian.getPosition()==g.getPosition())
                    .collect(Collectors.toList());
            if (Xtool.isNotNull(aliveUnits)) {
                Guardian  defender=aliveUnits.get(0);
                int[] skillLevel = CardSkillLevelUtil.calculateSkillLevels(guardian.getLevel(), guardian.getStar().doubleValue());
                if (defender != null && !defender.isDead()) {
                    int damage = 35 * skillLevel[0] + zhongyuedadiHuan(guardian);
                    defender.setCurrentHp(defender.getCurrentHp() - damage);
                    List<String> deadUnits = new ArrayList<>();

                    if (defender.getCurrentHp() <= 0) {
                        defender.setDead(true);
                        defender.setOnField(false);
                        deadUnits.add(defender.getCamp() + defender.getName() + "_" + defender.getPosition());
                    }
                    addLog("苦痛箭",
                            guardian.getName(), guardian.getCamp(), guardian.getPosition(),
                            guardian.getMaxHp(), guardian.getCurrentHp(),
                            guardian.getAttack(), guardian.getAttack(),
                            guardian.getSpeed(), guardian.getSpeed(),
                            defender.getName(), defender.getCamp(), defender.getPosition(),
                            defender.getMaxHp(), defender.getCurrentHp(),
                            defender.getAttack(), defender.getAttack(),
                            defender.getSpeed(), defender.getSpeed(),
                            getFieldUnitsStatus(),
                            damage, EffectType.MISSILE_DAMAGE, DamageType.MISSILE,
                            guardian.getName() + "苦痛箭");
                    // 死亡日志
                    if (!deadUnits.isEmpty()) {
                        Map<String, Object[]> deadStatus = new HashMap<>();
                        deadUnits.forEach(x -> {
                            List<String> strings = Arrays.asList(x.split("_"));
                            deadStatus.put(strings.get(0), new Object[]{Integer.parseInt(strings.get(1)), 0, 0});
                        });

                        addMultiTargetLog("UNIT_DEATH",
                                "SYSTEM", null, 0,
                                0, 0, 0, 0, 0, 0,
                                deadUnits, null,
                                deadStatus,
                                getFieldUnitsStatus(),
                                0, null, null,
                                "苦痛箭");
                        //触发死亡技能
                        triggerOnDeathSkills(defender);

                    } else {
                        //触发受击技能
                        triggerOnAttackedSkills(defender,EffectType.MISSILE_DAMAGE);
                    }
                }
            }

        }


//        南岳大帝，通灵神箭Lv1每回合对同位置敌方造成42点飞弹伤害；草船借箭Lv1受到火焰伤害时，增加自身飞弹伤害13点，最多叠加99层；大帝协同Lv8与西岳大帝在同一队伍时，增加自身604点生命上限，158点攻击，211点速度。
        if (campB.stream().anyMatch(g -> g.getName().equals("太岁灵君") && !g.isDead() && !g.isOnField())) {
            Guardian guardian = campB.stream()
                    .filter(g -> g.getName().equals("太岁灵君") && !g.isDead() && !g.isOnField())
                    .findFirst().get();
            List<Guardian> aliveUnits = campA.stream()
                    .filter(g -> !g.isDead()&&guardian.getPosition()==g.getPosition())
                    .collect(Collectors.toList());
            if (Xtool.isNotNull(aliveUnits)) {
                Guardian  defender=aliveUnits.get(0);
                int[] skillLevel = CardSkillLevelUtil.calculateSkillLevels(guardian.getLevel(), guardian.getStar().doubleValue());
                if (defender != null && !defender.isDead()) {
                    int damage = 35 * skillLevel[0] + zhongyuedadiHuan(guardian);
                    defender.setCurrentHp(defender.getCurrentHp() - damage);
                    List<String> deadUnits = new ArrayList<>();

                    if (defender.getCurrentHp() <= 0) {
                        defender.setDead(true);
                        defender.setOnField(false);
                        deadUnits.add(defender.getCamp() + defender.getName() + "_" + defender.getPosition());
                    }
                    addLog("苦痛箭",
                            guardian.getName(), guardian.getCamp(), guardian.getPosition(),
                            guardian.getMaxHp(), guardian.getCurrentHp(),
                            guardian.getAttack(), guardian.getAttack(),
                            guardian.getSpeed(), guardian.getSpeed(),
                            defender.getName(), defender.getCamp(), defender.getPosition(),
                            defender.getMaxHp(), defender.getCurrentHp(),
                            defender.getAttack(), defender.getAttack(),
                            defender.getSpeed(), defender.getSpeed(),
                            getFieldUnitsStatus(),
                            damage, EffectType.MISSILE_DAMAGE, DamageType.MISSILE,
                            guardian.getName() + "苦痛箭");
                    // 死亡日志
                    if (!deadUnits.isEmpty()) {
                        Map<String, Object[]> deadStatus = new HashMap<>();
                        deadUnits.forEach(x -> {
                            List<String> strings = Arrays.asList(x.split("_"));
                            deadStatus.put(strings.get(0), new Object[]{Integer.parseInt(strings.get(1)), 0, 0});
                        });

                        addMultiTargetLog("UNIT_DEATH",
                                "SYSTEM", null, 0,
                                0, 0, 0, 0, 0, 0,
                                deadUnits, null,
                                deadStatus,
                                getFieldUnitsStatus(),
                                0, null, null,
                                "苦痛箭");
                        //触发死亡技能
                        triggerOnDeathSkills(defender);

                    } else {
                        //触发受击技能
                        triggerOnAttackedSkills(defender,EffectType.MISSILE_DAMAGE);
                    }
                }
            }

        }

        //        南岳大帝，通灵神箭Lv1每回合对同位置敌方造成42点飞弹伤害；草船借箭Lv1受到火焰伤害时，增加自身飞弹伤害13点，最多叠加99层；大帝协同Lv8与西岳大帝在同一队伍时，增加自身604点生命上限，158点攻击，211点速度。
        if (campA.stream().anyMatch(g -> g.getName().equals("黄梅老佛") && !g.isDead() && !g.isOnField())) {
            Guardian guardian = campA.stream()
                    .filter(g -> g.getName().equals("黄梅老佛") && !g.isDead() && !g.isOnField())
                    .findFirst().get();
            List<Guardian> aliveUnits = campB.stream()
                    .filter(g -> !g.isDead()&&4==g.getPosition())
                    .collect(Collectors.toList());
            if (Xtool.isNotNull(aliveUnits)) {
                Guardian  defender=aliveUnits.get(0);
                int[] skillLevel = CardSkillLevelUtil.calculateSkillLevels(guardian.getLevel(), guardian.getStar().doubleValue());
                if (defender != null && !defender.isDead()) {
                    int damage = 42 * skillLevel[0] + zhongyuedadiHuan(guardian);
                    defender.setCurrentHp(defender.getCurrentHp() - damage);
                    List<String> deadUnits = new ArrayList<>();

                    if (defender.getCurrentHp() <= 0) {
                        defender.setDead(true);
                        defender.setOnField(false);
                        deadUnits.add(defender.getCamp() + defender.getName() + "_" + defender.getPosition());
                    }
                    addLog("群鸦箭",
                            guardian.getName(), guardian.getCamp(), guardian.getPosition(),
                            guardian.getMaxHp(), guardian.getCurrentHp(),
                            guardian.getAttack(), guardian.getAttack(),
                            guardian.getSpeed(), guardian.getSpeed(),
                            defender.getName(), defender.getCamp(), defender.getPosition(),
                            defender.getMaxHp(), defender.getCurrentHp(),
                            defender.getAttack(), defender.getAttack(),
                            defender.getSpeed(), defender.getSpeed(),
                            getFieldUnitsStatus(),
                            damage, EffectType.MISSILE_DAMAGE, DamageType.MISSILE,
                            guardian.getName() + "群鸦箭");
                    // 死亡日志
                    if (!deadUnits.isEmpty()) {
                        Map<String, Object[]> deadStatus = new HashMap<>();
                        deadUnits.forEach(x -> {
                            List<String> strings = Arrays.asList(x.split("_"));
                            deadStatus.put(strings.get(0), new Object[]{Integer.parseInt(strings.get(1)), 0, 0});
                        });

                        addMultiTargetLog("UNIT_DEATH",
                                "SYSTEM", null, 0,
                                0, 0, 0, 0, 0, 0,
                                deadUnits, null,
                                deadStatus,
                                getFieldUnitsStatus(),
                                0, null, null,
                                "群鸦箭");
                        //触发死亡技能
                        triggerOnDeathSkills(defender);

                    } else {
                        //触发受击技能
                        triggerOnAttackedSkills(defender,EffectType.MISSILE_DAMAGE);
                    }
                }
            }

        }


//        南岳大帝，通灵神箭Lv1每回合对同位置敌方造成42点飞弹伤害；草船借箭Lv1受到火焰伤害时，增加自身飞弹伤害13点，最多叠加99层；大帝协同Lv8与西岳大帝在同一队伍时，增加自身604点生命上限，158点攻击，211点速度。
        if (campB.stream().anyMatch(g -> g.getName().equals("黄梅老佛") && !g.isDead() && !g.isOnField())) {
            Guardian guardian = campB.stream()
                    .filter(g -> g.getName().equals("黄梅老佛") && !g.isDead() && !g.isOnField())
                    .findFirst().get();
            List<Guardian> aliveUnits = campA.stream()
                    .filter(g -> !g.isDead()&&4==g.getPosition())
                    .collect(Collectors.toList());
            if (Xtool.isNotNull(aliveUnits)) {
                Guardian  defender=aliveUnits.get(0);
                int[] skillLevel = CardSkillLevelUtil.calculateSkillLevels(guardian.getLevel(), guardian.getStar().doubleValue());
                if (defender != null && !defender.isDead()) {
                    int damage = 42 * skillLevel[0] + zhongyuedadiHuan(guardian);
                    defender.setCurrentHp(defender.getCurrentHp() - damage);
                    List<String> deadUnits = new ArrayList<>();

                    if (defender.getCurrentHp() <= 0) {
                        defender.setDead(true);
                        defender.setOnField(false);
                        deadUnits.add(defender.getCamp() + defender.getName() + "_" + defender.getPosition());
                    }
                    addLog("群鸦箭",
                            guardian.getName(), guardian.getCamp(), guardian.getPosition(),
                            guardian.getMaxHp(), guardian.getCurrentHp(),
                            guardian.getAttack(), guardian.getAttack(),
                            guardian.getSpeed(), guardian.getSpeed(),
                            defender.getName(), defender.getCamp(), defender.getPosition(),
                            defender.getMaxHp(), defender.getCurrentHp(),
                            defender.getAttack(), defender.getAttack(),
                            defender.getSpeed(), defender.getSpeed(),
                            getFieldUnitsStatus(),
                            damage, EffectType.MISSILE_DAMAGE, DamageType.MISSILE,
                            guardian.getName() + "群鸦箭");
                    // 死亡日志
                    if (!deadUnits.isEmpty()) {
                        Map<String, Object[]> deadStatus = new HashMap<>();
                        deadUnits.forEach(x -> {
                            List<String> strings = Arrays.asList(x.split("_"));
                            deadStatus.put(strings.get(0), new Object[]{Integer.parseInt(strings.get(1)), 0, 0});
                        });

                        addMultiTargetLog("UNIT_DEATH",
                                "SYSTEM", null, 0,
                                0, 0, 0, 0, 0, 0,
                                deadUnits, null,
                                deadStatus,
                                getFieldUnitsStatus(),
                                0, null, null,
                                "群鸦箭");
                        //触发死亡技能
                        triggerOnDeathSkills(defender);

                    } else {
                        //触发受击技能
                        triggerOnAttackedSkills(defender,EffectType.MISSILE_DAMAGE);
                    }
                }
            }

        }

        //        南岳大帝，通灵神箭Lv1每回合对同位置敌方造成42点飞弹伤害；草船借箭Lv1受到火焰伤害时，增加自身飞弹伤害13点，最多叠加99层；大帝协同Lv8与西岳大帝在同一队伍时，增加自身604点生命上限，158点攻击，211点速度。
        if (campA.stream().anyMatch(g -> g.getName().equals("六耳猕猴") && !g.isDead() && !g.isOnField())) {
            Guardian guardian = campA.stream()
                    .filter(g -> g.getName().equals("六耳猕猴") && !g.isDead() && !g.isOnField())
                    .findFirst().get();
            List<Guardian> aliveUnits = campB.stream()
                    .filter(g -> !g.isDead()&&3==g.getPosition())
                    .collect(Collectors.toList());
            if (Xtool.isNotNull(aliveUnits)) {
                Guardian  defender=aliveUnits.get(0);
                int[] skillLevel = CardSkillLevelUtil.calculateSkillLevels(guardian.getLevel(), guardian.getStar().doubleValue());
                if (defender != null && !defender.isDead()) {
                    int damage = 42 * skillLevel[0] + zhongyuedadiHuan(guardian);
                    defender.setCurrentHp(defender.getCurrentHp() - damage);
                    List<String> deadUnits = new ArrayList<>();

                    if (defender.getCurrentHp() <= 0) {
                        defender.setDead(true);
                        defender.setOnField(false);
                        deadUnits.add(defender.getCamp() + defender.getName() + "_" + defender.getPosition());
                    }
                    addLog("幻影箭",
                            guardian.getName(), guardian.getCamp(), guardian.getPosition(),
                            guardian.getMaxHp(), guardian.getCurrentHp(),
                            guardian.getAttack(), guardian.getAttack(),
                            guardian.getSpeed(), guardian.getSpeed(),
                            defender.getName(), defender.getCamp(), defender.getPosition(),
                            defender.getMaxHp(), defender.getCurrentHp(),
                            defender.getAttack(), defender.getAttack(),
                            defender.getSpeed(), defender.getSpeed(),
                            getFieldUnitsStatus(),
                            damage, EffectType.MISSILE_DAMAGE, DamageType.MISSILE,
                            guardian.getName() + "幻影箭");
                    // 死亡日志
                    if (!deadUnits.isEmpty()) {
                        Map<String, Object[]> deadStatus = new HashMap<>();
                        deadUnits.forEach(x -> {
                            List<String> strings = Arrays.asList(x.split("_"));
                            deadStatus.put(strings.get(0), new Object[]{Integer.parseInt(strings.get(1)), 0, 0});
                        });

                        addMultiTargetLog("UNIT_DEATH",
                                "SYSTEM", null, 0,
                                0, 0, 0, 0, 0, 0,
                                deadUnits, null,
                                deadStatus,
                                getFieldUnitsStatus(),
                                0, null, null,
                                "幻影箭");
                        //触发死亡技能
                        triggerOnDeathSkills(defender);

                    } else {
                        //触发受击技能
                        triggerOnAttackedSkills(defender,EffectType.MISSILE_DAMAGE);
                    }
                }
            }

        }


//        南岳大帝，通灵神箭Lv1每回合对同位置敌方造成42点飞弹伤害；草船借箭Lv1受到火焰伤害时，增加自身飞弹伤害13点，最多叠加99层；大帝协同Lv8与西岳大帝在同一队伍时，增加自身604点生命上限，158点攻击，211点速度。
        if (campB.stream().anyMatch(g -> g.getName().equals("六耳猕猴") && !g.isDead() && !g.isOnField())) {
            Guardian guardian = campB.stream()
                    .filter(g -> g.getName().equals("六耳猕猴") && !g.isDead() && !g.isOnField())
                    .findFirst().get();
            List<Guardian> aliveUnits = campA.stream()
                    .filter(g -> !g.isDead()&&3==g.getPosition())
                    .collect(Collectors.toList());
            if (Xtool.isNotNull(aliveUnits)) {
                Guardian  defender=aliveUnits.get(0);
                int[] skillLevel = CardSkillLevelUtil.calculateSkillLevels(guardian.getLevel(), guardian.getStar().doubleValue());
                if (defender != null && !defender.isDead()) {
                    int damage = 42 * skillLevel[0] + zhongyuedadiHuan(guardian);
                    defender.setCurrentHp(defender.getCurrentHp() - damage);
                    List<String> deadUnits = new ArrayList<>();

                    if (defender.getCurrentHp() <= 0) {
                        defender.setDead(true);
                        defender.setOnField(false);
                        deadUnits.add(defender.getCamp() + defender.getName() + "_" + defender.getPosition());
                    }
                    addLog("幻影箭",
                            guardian.getName(), guardian.getCamp(), guardian.getPosition(),
                            guardian.getMaxHp(), guardian.getCurrentHp(),
                            guardian.getAttack(), guardian.getAttack(),
                            guardian.getSpeed(), guardian.getSpeed(),
                            defender.getName(), defender.getCamp(), defender.getPosition(),
                            defender.getMaxHp(), defender.getCurrentHp(),
                            defender.getAttack(), defender.getAttack(),
                            defender.getSpeed(), defender.getSpeed(),
                            getFieldUnitsStatus(),
                            damage, EffectType.MISSILE_DAMAGE, DamageType.MISSILE,
                            guardian.getName() + "幻影箭");
                    // 死亡日志
                    if (!deadUnits.isEmpty()) {
                        Map<String, Object[]> deadStatus = new HashMap<>();
                        deadUnits.forEach(x -> {
                            List<String> strings = Arrays.asList(x.split("_"));
                            deadStatus.put(strings.get(0), new Object[]{Integer.parseInt(strings.get(1)), 0, 0});
                        });

                        addMultiTargetLog("UNIT_DEATH",
                                "SYSTEM", null, 0,
                                0, 0, 0, 0, 0, 0,
                                deadUnits, null,
                                deadStatus,
                                getFieldUnitsStatus(),
                                0, null, null,
                                "幻影箭");
                        //触发死亡技能
                        triggerOnDeathSkills(defender);

                    } else {
                        //触发受击技能
                        triggerOnAttackedSkills(defender,EffectType.MISSILE_DAMAGE);
                    }
                }
            }

        }


        //        南岳大帝，通灵神箭Lv1每回合对同位置敌方造成42点飞弹伤害；草船借箭Lv1受到火焰伤害时，增加自身飞弹伤害13点，最多叠加99层；大帝协同Lv8与西岳大帝在同一队伍时，增加自身604点生命上限，158点攻击，211点速度。
        if (campA.stream().anyMatch(g -> g.getName().equals("九天玄女") && !g.isDead())) {
            Guardian guardian = campA.stream()
                    .filter(g -> g.getName().equals("九天玄女") && !g.isDead())
                    .findFirst().get();
            List<Guardian> aliveUnits = campB.stream()
                    .filter(g -> !g.isDead())
                    // 按血量升序排序
                    .sorted(Comparator.comparingInt(Guardian::getCurrentHp))
                    .collect(Collectors.toList());
            // 查找血量最低的存活守卫
            if (Xtool.isNotNull(aliveUnits)) {
                Guardian  defender=aliveUnits.get(0);
                int[] skillLevel = CardSkillLevelUtil.calculateSkillLevels(guardian.getLevel(), guardian.getStar().doubleValue());
                if (defender != null && !defender.isDead()) {
                    int damage = 42 * skillLevel[0] + zhongyuedadiHuan(guardian);
                    defender.setCurrentHp(defender.getCurrentHp() - damage);
                    List<String> deadUnits = new ArrayList<>();

                    if (defender.getCurrentHp() <= 0) {
                        defender.setDead(true);
                        defender.setOnField(false);
                        deadUnits.add(defender.getCamp() + defender.getName() + "_" + defender.getPosition());
                    }
                    addLog("觅心神箭",
                            guardian.getName(), guardian.getCamp(), guardian.getPosition(),
                            guardian.getMaxHp(), guardian.getCurrentHp(),
                            guardian.getAttack(), guardian.getAttack(),
                            guardian.getSpeed(), guardian.getSpeed(),
                            defender.getName(), defender.getCamp(), defender.getPosition(),
                            defender.getMaxHp(), defender.getCurrentHp(),
                            defender.getAttack(), defender.getAttack(),
                            defender.getSpeed(), defender.getSpeed(),
                            getFieldUnitsStatus(),
                            damage, EffectType.MISSILE_DAMAGE, DamageType.MISSILE,
                            guardian.getName() + "觅心神箭");
                    // 死亡日志
                    if (!deadUnits.isEmpty()) {
                        Map<String, Object[]> deadStatus = new HashMap<>();
                        deadUnits.forEach(x -> {
                            List<String> strings = Arrays.asList(x.split("_"));
                            deadStatus.put(strings.get(0), new Object[]{Integer.parseInt(strings.get(1)), 0, 0});
                        });

                        addMultiTargetLog("UNIT_DEATH",
                                "SYSTEM", null, 0,
                                0, 0, 0, 0, 0, 0,
                                deadUnits, null,
                                deadStatus,
                                getFieldUnitsStatus(),
                                0, null, null,
                                "觅心神箭");
                        //触发死亡技能
                        triggerOnDeathSkills(defender);

                    } else {
                        //触发受击技能
                        triggerOnAttackedSkills(defender, EffectType.MISSILE_DAMAGE);
                    }
                }
            }

        }


//        南岳大帝，通灵神箭Lv1每回合对同位置敌方造成42点飞弹伤害；草船借箭Lv1受到火焰伤害时，增加自身飞弹伤害13点，最多叠加99层；大帝协同Lv8与西岳大帝在同一队伍时，增加自身604点生命上限，158点攻击，211点速度。
        if (campB.stream().anyMatch(g -> g.getName().equals("九天玄女") && !g.isDead())) {
            Guardian guardian = campB.stream()
                    .filter(g -> g.getName().equals("九天玄女") && !g.isDead())
                    .findFirst().get();
            List<Guardian> aliveUnits = campA.stream()
                    .filter(g -> !g.isDead())
                    // 按血量升序排序
                    .sorted(Comparator.comparingInt(Guardian::getCurrentHp))
                    .collect(Collectors.toList());
            if (Xtool.isNotNull(aliveUnits)) {
                Guardian  defender=aliveUnits.get(0);
                int[] skillLevel = CardSkillLevelUtil.calculateSkillLevels(guardian.getLevel(), guardian.getStar().doubleValue());
                if (defender != null && !defender.isDead()) {
                    int damage = 42 * skillLevel[0] + zhongyuedadiHuan(guardian);
                    defender.setCurrentHp(defender.getCurrentHp() - damage);
                    List<String> deadUnits = new ArrayList<>();

                    if (defender.getCurrentHp() <= 0) {
                        defender.setDead(true);
                        defender.setOnField(false);
                        deadUnits.add(defender.getCamp() + defender.getName() + "_" + defender.getPosition());
                    }
                    addLog("觅心神箭",
                            guardian.getName(), guardian.getCamp(), guardian.getPosition(),
                            guardian.getMaxHp(), guardian.getCurrentHp(),
                            guardian.getAttack(), guardian.getAttack(),
                            guardian.getSpeed(), guardian.getSpeed(),
                            defender.getName(), defender.getCamp(), defender.getPosition(),
                            defender.getMaxHp(), defender.getCurrentHp(),
                            defender.getAttack(), defender.getAttack(),
                            defender.getSpeed(), defender.getSpeed(),
                            getFieldUnitsStatus(),
                            damage, EffectType.MISSILE_DAMAGE, DamageType.MISSILE,
                            guardian.getName() + "觅心神箭");
                    // 死亡日志
                    if (!deadUnits.isEmpty()) {
                        Map<String, Object[]> deadStatus = new HashMap<>();
                        deadUnits.forEach(x -> {
                            List<String> strings = Arrays.asList(x.split("_"));
                            deadStatus.put(strings.get(0), new Object[]{Integer.parseInt(strings.get(1)), 0, 0});
                        });

                        addMultiTargetLog("UNIT_DEATH",
                                "SYSTEM", null, 0,
                                0, 0, 0, 0, 0, 0,
                                deadUnits, null,
                                deadStatus,
                                getFieldUnitsStatus(),
                                0, null, null,
                                "觅心神箭");
                        //触发死亡技能
                        triggerOnDeathSkills(defender);

                    } else {
                        //触发受击技能
                        triggerOnAttackedSkills(defender, EffectType.MISSILE_DAMAGE);
                    }
                }
            }

        }

        //        南岳大帝，通灵神箭Lv1每回合对同位置敌方造成42点飞弹伤害；草船借箭Lv1受到火焰伤害时，增加自身飞弹伤害13点，最多叠加99层；大帝协同Lv8与西岳大帝在同一队伍时，增加自身604点生命上限，158点攻击，211点速度。
        if (campA.stream().anyMatch(g -> g.getName().equals("白天君") && !g.isDead()&&g.isOnField())) {
            Guardian guardian = campA.stream()
                    .filter(g -> g.getName().equals("白天君") && !g.isDead())
                    .findFirst().get();
            int xuli= guardian.getBuffStacks();
            guardian.setBuffStacks(guardian.getBuffStacks()+1);
            // 查找血量最低的存活守卫
            if (xuli>2) {
                guardian.setBuffStacks(0);
                int[] skillLevel = CardSkillLevelUtil.calculateSkillLevels(guardian.getLevel(), guardian.getStar().doubleValue());
                //烈焰阵Lv1登场时，令敌方全体收到火焰伤害420点；
                List<String> deadUnits = new ArrayList<>();
                List<Guardian> deadGuardians = new ArrayList<>();
                int lavaDamage = 355 * skillLevel[0];
                List<String> targetNames = new ArrayList<>();
                Map<String, Object[]> targetStatus = new HashMap<>();
                List<Guardian> enemies = campB.stream().filter(g -> !g.isDead()).collect(Collectors.toList());
                enemies.forEach(g -> {
                    targetNames.add(g.getName());
                    int hpBefore = g.getMaxHp();
                    g.setCurrentHp(g.getCurrentHp() - lavaDamage);
                    targetStatus.put(g.getCamp() + g.getName(), new Object[]{g.getPosition(), hpBefore, g.getCurrentHp()});
                    if (g.isDead()) {
                        deadUnits.add(g.getCamp() + g.getName() + "_" + g.getPosition());
                        deadGuardians.add(g);
                    }
                });

                // 单条日志记录多目标
                addMultiTargetLog("三火齐飞",
                        guardian.getName(), guardian.getCamp(), guardian.getPosition(),
                        guardian.getMaxHp(), guardian.getCurrentHp(),
                        guardian.getAttack(), guardian.getAttack(),
                        guardian.getSpeed(), guardian.getSpeed(),
                        targetNames, enemies.get(0).getCamp(),
                        targetStatus,
                        getFieldUnitsStatus(),
                        lavaDamage, EffectType.FIRE_DAMAGE, DamageType.FIRE,
                        guardian.getName() + "触发三火齐飞令敌方全体收到火焰伤害");
                // 死亡日志
                if (!deadUnits.isEmpty()) {
                    Map<String, Object[]> deadStatus = new HashMap<>();
                    deadUnits.forEach(x -> {
                        List<String> strings = Arrays.asList(x.split("_"));
                        deadStatus.put(strings.get(0), new Object[]{Integer.parseInt(strings.get(1)), 0, 0});
                    });

                    addMultiTargetLog("UNIT_DEATH",
                            "SYSTEM", null, 0,
                            0, 0, 0, 0, 0, 0,
                            deadUnits, null,
                            deadStatus,
                            getFieldUnitsStatus(),
                            0, null, null,
                            "火焰伤害");
                    //触发死亡技能
                    for (Guardian g : deadGuardians) {
                        triggerOnDeathSkills(g);
                    }

                }
                //触发受击技能
                enemies.forEach(g -> {
                    //触发受到任意伤害技能
                    triggerOnAttackedSkills(g,EffectType.FIRE_DAMAGE);
                });
            }else {
                String[] str={"白天君蓄力·一","白天君蓄力·二","白天君蓄力·三"};
                addLog(str[xuli],
                        guardian.getName(), guardian.getCamp(), guardian.getPosition(),
                        guardian.getMaxHp(), guardian.getCurrentHp(),
                        guardian.getAttack(), guardian.getAttack(),
                        guardian.getSpeed(), guardian.getSpeed(),
                        guardian.getName(), guardian.getCamp(), guardian.getPosition(),
                        guardian.getMaxHp(), guardian.getCurrentHp(),
                        guardian.getAttack(), guardian.getAttack(),
                        guardian.getSpeed(), guardian.getSpeed(),
                        getFieldUnitsStatus(),
                        0, null, null,
                        guardian.getName() + "触发白天君蓄力");
            }

        }

        if (campB.stream().anyMatch(g -> g.getName().equals("白天君") && !g.isDead()&&g.isOnField())){
            Guardian guardian = campB.stream()
                    .filter(g -> g.getName().equals("白天君") && !g.isDead())
                    .findFirst().get();
            int xuli= guardian.getBuffStacks();
            guardian.setBuffStacks(guardian.getBuffStacks()+1);
            // 查找血量最低的存活守卫
            if (xuli>2) {
                guardian.setBuffStacks(0);
                int[] skillLevel = CardSkillLevelUtil.calculateSkillLevels(guardian.getLevel(), guardian.getStar().doubleValue());
                //烈焰阵Lv1登场时，令敌方全体收到火焰伤害420点；
                List<String> deadUnits = new ArrayList<>();
                List<Guardian> deadGuardians = new ArrayList<>();
                int lavaDamage = 355 * skillLevel[0];
                List<String> targetNames = new ArrayList<>();
                Map<String, Object[]> targetStatus = new HashMap<>();
                List<Guardian> enemies = campA.stream().filter(g -> !g.isDead()).collect(Collectors.toList());
                enemies.forEach(g -> {
                    targetNames.add(g.getName());
                    int hpBefore = g.getMaxHp();
                    g.setCurrentHp(g.getCurrentHp() - lavaDamage);
                    targetStatus.put(g.getCamp() + g.getName(), new Object[]{g.getPosition(), hpBefore, g.getCurrentHp()});
                    if (g.isDead()) {
                        deadUnits.add(g.getCamp() + g.getName() + "_" + g.getPosition());
                        deadGuardians.add(g);
                    }
                });

                // 单条日志记录多目标
                addMultiTargetLog("三火齐飞",
                        guardian.getName(), guardian.getCamp(), guardian.getPosition(),
                        guardian.getMaxHp(), guardian.getCurrentHp(),
                        guardian.getAttack(), guardian.getAttack(),
                        guardian.getSpeed(), guardian.getSpeed(),
                        targetNames, enemies.get(0).getCamp(),
                        targetStatus,
                        getFieldUnitsStatus(),
                        lavaDamage, EffectType.FIRE_DAMAGE, DamageType.FIRE,
                        guardian.getName() + "触发三火齐飞令敌方全体收到火焰伤害");
                // 死亡日志
                if (!deadUnits.isEmpty()) {
                    Map<String, Object[]> deadStatus = new HashMap<>();
                    deadUnits.forEach(x -> {
                        List<String> strings = Arrays.asList(x.split("_"));
                        deadStatus.put(strings.get(0), new Object[]{Integer.parseInt(strings.get(1)), 0, 0});
                    });

                    addMultiTargetLog("UNIT_DEATH",
                            "SYSTEM", null, 0,
                            0, 0, 0, 0, 0, 0,
                            deadUnits, null,
                            deadStatus,
                            getFieldUnitsStatus(),
                            0, null, null,
                            "火焰伤害");
                    //触发死亡技能
                    for (Guardian g : deadGuardians) {
                        triggerOnDeathSkills(g);
                    }

                }
                //触发受击技能
                enemies.forEach(g -> {
                    //触发受到任意伤害技能
                    triggerOnAttackedSkills(g,EffectType.FIRE_DAMAGE);
                });
            }else {
                String[] str={"白天君蓄力·一","白天君蓄力·二","白天君蓄力·三"};
                addLog(str[xuli],
                        guardian.getName(), guardian.getCamp(), guardian.getPosition(),
                        guardian.getMaxHp(), guardian.getCurrentHp(),
                        guardian.getAttack(), guardian.getAttack(),
                        guardian.getSpeed(), guardian.getSpeed(),
                        guardian.getName(), guardian.getCamp(), guardian.getPosition(),
                        guardian.getMaxHp(), guardian.getCurrentHp(),
                        guardian.getAttack(), guardian.getAttack(),
                        guardian.getSpeed(), guardian.getSpeed(),
                        getFieldUnitsStatus(),
                        0, null, null,
                        guardian.getName() + "触发白天君蓄力");
            }

        }
        // 妲己场下技能
        processOnFieldSkills();
    }

    // 批量处理中毒效果
    private void processPoisonEffects() {
        List<Guardian> poisonedUnits = new ArrayList<>();
        campA.forEach(g -> {
            if (g.getEffects().containsKey(EffectType.POISON) && !g.isDead()) {
                poisonedUnits.add(g);
            }
        });
        campB.forEach(g -> {
            if (g.getEffects().containsKey(EffectType.POISON) && !g.isDead()) {
                poisonedUnits.add(g);
            }
        });

        if (!poisonedUnits.isEmpty()) {
            List<String> targetNames = new ArrayList<>();
            Map<String, Object[]> targetStatus = new HashMap<>();
            List<String> deadUnits = new ArrayList<>();

            poisonedUnits.forEach(g -> {
                targetNames.add(g.getName());
                int hpBefore = g.getMaxHp();
                int poisonDamage = g.getEffects().getOrDefault(EffectType.POISON, 0);
                g.setCurrentHp(g.getCurrentHp() - poisonDamage);
                targetStatus.put(g.getCamp() + g.getName(), new Object[]{g.getPosition(), hpBefore, g.getCurrentHp()});

                if (g.getCurrentHp() <= 0) {
                    g.setDead(true);
                    g.setOnField(false);
                    deadUnits.add(g.getCamp() + g.getName() + "_" + g.getPosition());
                }
            });

            // 中毒伤害日志（批量）
            addMultiTargetLog("POISON",
                    "SYSTEM", null, 0,
                    0, 0, 0, 0, 0, 0,
                    targetNames, null,
                    targetStatus,
                    getFieldUnitsStatus(),
                    0, EffectType.POISON, DamageType.POISON,
                    "中毒效果触发");

            // 中毒阵亡日志
            if (!deadUnits.isEmpty()) {
                Map<String, Object[]> deadStatus = new HashMap<>();
                deadUnits.forEach(x -> {
                    List<String> strings = Arrays.asList(x.split("_"));
                    deadStatus.put(strings.get(0), new Object[]{Integer.parseInt(strings.get(1)), 0, 0});
                });

                addMultiTargetLog("UNIT_DEATH",
                        "SYSTEM", null, 0,
                        0, 0, 0, 0, 0, 0,
                        deadUnits, null,
                        deadStatus,
                        getFieldUnitsStatus(),
                        0, null, null,
                        "单位因中毒阵亡");
            }
        }
    }

    // 处理登场场下技能
    private void processOnFieldSkills0(Guardian defender) {
        for (int i = 1; i < 6; i++) {
            int position = i;
            if (defender.getCamp() == Camp.B && campA.stream().anyMatch(g -> g.getName().equals("阎王") && g.getPosition() == position && !g.isDead() && !g.isOnField())) {
                Guardian guardian = campA.stream()
                        .filter(g -> g.getName().equals("阎王") && g.getPosition() == position && !g.isDead() && !g.isOnField())
                        .findFirst().get();
                int[] skillLevel = CardSkillLevelUtil.calculateSkillLevels(guardian.getLevel(), guardian.getStar().doubleValue());
                if (skillLevel[1] > 0) {
                    //幽冥审判Lv1每当有敌方登场，令随机敌方中毒73；
                    List<Guardian> enemies = guardian.getCamp() == Camp.A ?
                            campB.stream().filter(g -> !g.isDead()).collect(Collectors.toList()) :
                            campA.stream().filter(g -> !g.isDead()).collect(Collectors.toList());
                    if (!enemies.isEmpty()) {
                        Guardian randomEnemy = enemies.get(random.nextInt(enemies.size()));
                        int enemyHpBefore = randomEnemy.getCurrentHp();
                        int poisonValue = 73 * skillLevel[1];
                        int poisonDamage = randomEnemy.getEffects().getOrDefault(EffectType.POISON, 0);
                        randomEnemy.getEffects().put(EffectType.POISON, poisonValue + poisonDamage);

                        addLog("幽冥审判",
                                guardian.getName(), guardian.getCamp(), guardian.getPosition(),
                                guardian.getMaxHp(), guardian.getCurrentHp(),
                                guardian.getAttack(), guardian.getAttack(),
                                guardian.getSpeed(), guardian.getSpeed(),
                                randomEnemy.getName(), randomEnemy.getCamp(), randomEnemy.getPosition(),
                                enemyHpBefore, randomEnemy.getCurrentHp(),
                                randomEnemy.getAttack(), randomEnemy.getAttack(),
                                randomEnemy.getSpeed(), randomEnemy.getSpeed(),
                                getFieldUnitsStatus(),
                                poisonValue, EffectType.POISON, DamageType.POISON,
                                guardian.getName() + "触发幽冥审判");
                    }
                }

            }

            if (defender.getCamp() == Camp.A && campB.stream().anyMatch(g -> g.getName().equals("阎王") && g.getPosition() == position && !g.isDead() && !g.isOnField())) {
                Guardian guardian = campB.stream()
                        .filter(g -> g.getName().equals("阎王") && g.getPosition() == position && !g.isDead() && !g.isOnField())
                        .findFirst().get();
                int[] skillLevel = CardSkillLevelUtil.calculateSkillLevels(guardian.getLevel(), guardian.getStar().doubleValue());
                if (skillLevel[1] > 0) {
                    //幽冥审判Lv1每当有敌方登场，令随机敌方中毒73；
                    List<Guardian> enemies = guardian.getCamp() == Camp.B ?
                            campA.stream().filter(g -> !g.isDead()).collect(Collectors.toList()) :
                            campB.stream().filter(g -> !g.isDead()).collect(Collectors.toList());
                    if (!enemies.isEmpty()) {
                        Guardian randomEnemy = enemies.get(random.nextInt(enemies.size()));
                        int enemyHpBefore = randomEnemy.getCurrentHp();
                        int poisonValue = 73 * skillLevel[1];
                        int poisonDamage = randomEnemy.getEffects().getOrDefault(EffectType.POISON, 0);
                        randomEnemy.getEffects().put(EffectType.POISON, poisonValue + poisonDamage);

                        addLog("幽冥审判",
                                guardian.getName(), guardian.getCamp(), guardian.getPosition(),
                                guardian.getMaxHp(), guardian.getCurrentHp(),
                                guardian.getAttack(), guardian.getAttack(),
                                guardian.getSpeed(), guardian.getSpeed(),
                                randomEnemy.getName(), randomEnemy.getCamp(), randomEnemy.getPosition(),
                                enemyHpBefore, randomEnemy.getCurrentHp(),
                                randomEnemy.getAttack(), randomEnemy.getAttack(),
                                randomEnemy.getSpeed(), randomEnemy.getSpeed(),
                                getFieldUnitsStatus(),
                                poisonValue, EffectType.POISON, DamageType.POISON,
                                guardian.getName() + "触发幽冥审判");
                    }
                }

            }
//            疫病侵染Lv1场下，我方单位登场时为场上敌人收到疾病效果，疾病令其受到治疗减少2%；
            if (defender.getCamp() == Camp.A && campA.stream().anyMatch(g -> g.getName().equals("玄冥") && g.getPosition() == position && !g.isDead() && !g.isOnField())) {
                Guardian guardian = campA.stream()
                        .filter(g -> g.getName().equals("玄冥") && g.getPosition() == position && !g.isDead() && !g.isOnField())
                        .findFirst().get();
                int[] skillLevel = CardSkillLevelUtil.calculateSkillLevels(guardian.getLevel(), guardian.getStar().doubleValue());
                if (fieldB != null && skillLevel[1] > 0) {
                    int healDow = fieldB.getEffects().getOrDefault(EffectType.HEAL_DOWN, 0);
                    int healBoost = fieldB.getEffects().getOrDefault(EffectType.HEAL_BOOST, 0);
                    int healDowNew = skillLevel[1] * 2;
                    fieldB.getEffects().put(EffectType.HEAL_DOWN, healDowNew + healDow - healBoost);

                    addLog("疫病侵染",
                            guardian.getName(), guardian.getCamp(), guardian.getPosition(),
                            guardian.getMaxHp(), guardian.getCurrentHp(),
                            guardian.getAttack(), guardian.getAttack(),
                            guardian.getSpeed(), guardian.getSpeed(),
                            fieldB.getName(), fieldB.getCamp(), fieldB.getPosition(),
                            fieldB.getMaxHp(), fieldB.getCurrentHp(),
                            fieldB.getAttack(), fieldB.getAttack(),
                            fieldB.getSpeed(), fieldB.getSpeed(),
                            getFieldUnitsStatus(),
                            healDowNew, EffectType.HEAL_DOWN, DamageType.MAGIC,
                            guardian.getName() + "触发我方单位登场时为场上敌人收到疾病效果");
                }
            }

            if (defender.getCamp() == Camp.B && campB.stream().anyMatch(g -> g.getName().equals("玄冥") && g.getPosition() == position && !g.isDead() && !g.isOnField())) {
                Guardian guardian = campB.stream()
                        .filter(g -> g.getName().equals("玄冥") && g.getPosition() == position && !g.isDead() && !g.isOnField())
                        .findFirst().get();
                int[] skillLevel = CardSkillLevelUtil.calculateSkillLevels(guardian.getLevel(), guardian.getStar().doubleValue());

                if (fieldA != null && skillLevel[1] > 0) {
                    int healDow = fieldA.getEffects().getOrDefault(EffectType.HEAL_DOWN, 0);
                    int healBoost = fieldA.getEffects().getOrDefault(EffectType.HEAL_BOOST, 0);
                    int healDowNew = skillLevel[1] * 2;
                    fieldA.getEffects().put(EffectType.HEAL_DOWN, healDowNew + healDow - healBoost);

                    addLog("疫病侵染",
                            guardian.getName(), guardian.getCamp(), guardian.getPosition(),
                            guardian.getMaxHp(), guardian.getCurrentHp(),
                            guardian.getAttack(), guardian.getAttack(),
                            guardian.getSpeed(), guardian.getSpeed(),
                            fieldA.getName(), fieldA.getCamp(), fieldA.getPosition(),
                            fieldA.getMaxHp(), fieldA.getCurrentHp(),
                            fieldA.getAttack(), fieldA.getAttack(),
                            fieldA.getSpeed(), fieldA.getSpeed(),
                            getFieldUnitsStatus(),
                            healDowNew, EffectType.HEAL_DOWN, DamageType.MAGIC,
                            guardian.getName() + "触发我方单位登场时为场上敌人收到疾病效果");
                }
            }

//            镇元子，魂力飞弹Lv1场下，每当新单位入场时，对场上敌人造成178点飞弹伤害；禁心咒Lv1场下，每当有单位登场，有17%几率令场上英雄沉默2回合；多宝道人同Lv1与多宝道人在同一队伍时，增加自身453点生命上限，158点攻击，158点速度。
            if (campA.stream().anyMatch(g -> g.getName().equals("镇元子") && g.getPosition() == position && !g.isDead() && !g.isOnField())) {
                Guardian guardian = campA.stream()
                        .filter(g -> g.getName().equals("镇元子") && g.getPosition() == position && !g.isDead() && !g.isOnField())
                        .findFirst().get();
                int[] skillLevel = CardSkillLevelUtil.calculateSkillLevels(guardian.getLevel(), guardian.getStar().doubleValue());
                if (fieldB != null && !fieldB.isDead()) {
                    int damage = 178 * skillLevel[1] + zhongyuedadiHuan(defender);
                    fieldB.setCurrentHp(fieldB.getCurrentHp() - damage);
                    List<String> deadUnits = new ArrayList<>();

                    if (fieldB.getCurrentHp() <= 0) {
                        fieldB.setDead(true);
                        fieldB.setOnField(false);
                        deadUnits.add(fieldB.getCamp() + fieldB.getName() + "_" + fieldB.getPosition());
                    }
                    addLog("魂力飞弹",
                            guardian.getName(), guardian.getCamp(), guardian.getPosition(),
                            guardian.getMaxHp(), guardian.getCurrentHp(),
                            guardian.getAttack(), guardian.getAttack(),
                            guardian.getSpeed(), guardian.getSpeed(),
                            fieldB.getName(), fieldB.getCamp(), fieldB.getPosition(),
                            fieldB.getMaxHp(), fieldB.getCurrentHp(),
                            fieldB.getAttack(), fieldB.getAttack(),
                            fieldB.getSpeed(), fieldB.getSpeed(),
                            getFieldUnitsStatus(),
                            damage, EffectType.MISSILE_DAMAGE, DamageType.MISSILE,
                            guardian.getName() + "魂力飞弹");
                    // 死亡日志
                    if (!deadUnits.isEmpty()) {
                        Map<String, Object[]> deadStatus = new HashMap<>();
                        deadUnits.forEach(x -> {
                            List<String> strings = Arrays.asList(x.split("_"));
                            deadStatus.put(strings.get(0), new Object[]{Integer.parseInt(strings.get(1)), 0, 0});
                        });

                        addMultiTargetLog("UNIT_DEATH",
                                "SYSTEM", null, 0,
                                0, 0, 0, 0, 0, 0,
                                deadUnits, null,
                                deadStatus,
                                getFieldUnitsStatus(),
                                0, null, null,
                                "飞弹伤害");
                        //触发死亡技能
                        triggerOnDeathSkills(fieldB);

                    } else {
                        //触发受击技能
                        triggerOnAttackedSkills(fieldB, EffectType.MISSILE_DAMAGE);
                    }
                }
            }
//            禁心咒Lv1场下，每当有单位登场，有17%几率令场上英雄沉默2回合；
            if (campA.stream().anyMatch(g -> g.getName().equals("镇元子") && g.getPosition() == position && !g.isDead() && !g.isOnField())) {
                Guardian guardian = campA.stream()
                        .filter(g -> g.getName().equals("镇元子") && g.getPosition() == position && !g.isDead() && !g.isOnField())
                        .findFirst().get();
                int[] skillLevel = CardSkillLevelUtil.calculateSkillLevels(guardian.getLevel(), guardian.getStar().doubleValue());
                //  禁心咒Lv1场下
                if (skillLevel[1] > 0 && random.nextDouble() < 0.17 * skillLevel[1] && fieldB != null) {
                    int silence = fieldB.getEffects().getOrDefault(EffectType.SILENCE, 0);
                    if (silence < 2) {
                        fieldB.getEffects().put(EffectType.SILENCE, 2);
                    }
                    addLog("禁心咒",
                            guardian.getName(), guardian.getCamp(), guardian.getPosition(),
                            guardian.getMaxHp(), guardian.getCurrentHp(),
                            guardian.getAttack(), guardian.getAttack(),
                            guardian.getSpeed(), guardian.getSpeed(),
                            fieldB.getName(), fieldB.getCamp(), fieldB.getPosition(),
                            fieldB.getMaxHp(), fieldB.getCurrentHp(),
                            fieldB.getAttack(), fieldB.getAttack(),
                            fieldB.getSpeed(), fieldB.getSpeed(),
                            getFieldUnitsStatus(),
                            0, EffectType.SILENCE, DamageType.MAGIC,
                            guardian.getName() + "登场触发禁术咒");
                }
            }

//            镇元子，魂力飞弹Lv1场下，每当新单位入场时，对场上敌人造成178点飞弹伤害；禁心咒Lv1场下，每当有单位登场，有17%几率令场上英雄沉默2回合；多宝道人同Lv1与多宝道人在同一队伍时，增加自身453点生命上限，158点攻击，158点速度。
            if (campB.stream().anyMatch(g -> g.getName().equals("镇元子") && g.getPosition() == position && !g.isDead() && !g.isOnField())) {
                Guardian guardian = campB.stream()
                        .filter(g -> g.getName().equals("镇元子") && g.getPosition() == position && !g.isDead() && !g.isOnField())
                        .findFirst().get();
                int[] skillLevel = CardSkillLevelUtil.calculateSkillLevels(guardian.getLevel(), guardian.getStar().doubleValue());
                if (fieldA != null && !fieldA.isDead()) {
                    int damage = 178 * skillLevel[1] + zhongyuedadiHuan(defender);
                    fieldA.setCurrentHp(fieldA.getCurrentHp() - damage);
                    List<String> deadUnits = new ArrayList<>();

                    if (fieldA.getCurrentHp() <= 0) {
                        fieldA.setDead(true);
                        fieldA.setOnField(false);
                        deadUnits.add(fieldA.getCamp() + fieldA.getName() + "_" + fieldA.getPosition());
                    }
                    addLog("魂力飞弹",
                            guardian.getName(), guardian.getCamp(), guardian.getPosition(),
                            guardian.getMaxHp(), guardian.getCurrentHp(),
                            guardian.getAttack(), guardian.getAttack(),
                            guardian.getSpeed(), guardian.getSpeed(),
                            fieldA.getName(), fieldA.getCamp(), fieldA.getPosition(),
                            fieldA.getMaxHp(), fieldA.getCurrentHp(),
                            fieldA.getAttack(), fieldA.getAttack(),
                            fieldA.getSpeed(), fieldA.getSpeed(),
                            getFieldUnitsStatus(),
                            damage, EffectType.MISSILE_DAMAGE, DamageType.MISSILE,
                            guardian.getName() + "魂力飞弹");
                    // 死亡日志
                    if (!deadUnits.isEmpty()) {
                        Map<String, Object[]> deadStatus = new HashMap<>();
                        deadUnits.forEach(x -> {
                            List<String> strings = Arrays.asList(x.split("_"));
                            deadStatus.put(strings.get(0), new Object[]{Integer.parseInt(strings.get(1)), 0, 0});
                        });

                        addMultiTargetLog("UNIT_DEATH",
                                "SYSTEM", null, 0,
                                0, 0, 0, 0, 0, 0,
                                deadUnits, null,
                                deadStatus,
                                getFieldUnitsStatus(),
                                0, null, null,
                                "飞弹伤害");
                        //触发死亡技能
                        triggerOnDeathSkills(fieldA);

                    } else {
                        //触发受击技能
                        triggerOnAttackedSkills(fieldA, EffectType.MISSILE_DAMAGE);
                    }
                }
            }
//            禁心咒Lv1场下，每当有单位登场，有17%几率令场上英雄沉默2回合；
            if (campB.stream().anyMatch(g -> g.getName().equals("镇元子") && g.getPosition() == position && !g.isDead() && !g.isOnField())) {
                Guardian guardian = campB.stream()
                        .filter(g -> g.getName().equals("镇元子") && g.getPosition() == position && !g.isDead() && !g.isOnField())
                        .findFirst().get();
                int[] skillLevel = CardSkillLevelUtil.calculateSkillLevels(guardian.getLevel(), guardian.getStar().doubleValue());
                //  禁心咒Lv1场下
                if (skillLevel[1] > 0 && random.nextDouble() < 0.17 * skillLevel[1] && fieldA != null) {
                    int silence = fieldA.getEffects().getOrDefault(EffectType.SILENCE, 0);
                    if (silence < 2) {
                        fieldA.getEffects().put(EffectType.SILENCE, 2);
                    }
                    addLog("禁术咒",
                            guardian.getName(), guardian.getCamp(), guardian.getPosition(),
                            guardian.getMaxHp(), guardian.getCurrentHp(),
                            guardian.getAttack(), guardian.getAttack(),
                            guardian.getSpeed(), guardian.getSpeed(),
                            fieldA.getName(), fieldA.getCamp(), fieldA.getPosition(),
                            fieldA.getMaxHp(), fieldA.getCurrentHp(),
                            fieldA.getAttack(), fieldA.getAttack(),
                            fieldA.getSpeed(), fieldA.getSpeed(),
                            getFieldUnitsStatus(),
                            0, EffectType.SILENCE, DamageType.MAGIC,
                            guardian.getName() + "登场触发禁术咒");
                }
            }

//            南华真人，魂力飞弹Lv1场下，每当新单位入场时，对场上敌人造成178点飞弹伤害；报复神箭Lv1场下，每回合对场上敌方造成106；
            if (campA.stream().anyMatch(g -> g.getName().equals("南华真人") && g.getPosition() == position && !g.isDead() && !g.isOnField())) {
                Guardian guardian = campA.stream()
                        .filter(g -> g.getName().equals("南华真人") && g.getPosition() == position && !g.isDead() && !g.isOnField())
                        .findFirst().get();
                int[] skillLevel = CardSkillLevelUtil.calculateSkillLevels(guardian.getLevel(), guardian.getStar().doubleValue());
                if (fieldB != null && !fieldB.isDead()) {
                    int damage = 178 * skillLevel[1] + zhongyuedadiHuan(defender);
                    fieldB.setCurrentHp(fieldB.getCurrentHp() - damage);
                    List<String> deadUnits = new ArrayList<>();

                    if (fieldB.getCurrentHp() <= 0) {
                        fieldB.setDead(true);
                        fieldB.setOnField(false);
                        deadUnits.add(fieldB.getCamp() + fieldB.getName() + "_" + fieldB.getPosition());
                    }
                    addLog("魂力飞弹",
                            guardian.getName(), guardian.getCamp(), guardian.getPosition(),
                            guardian.getMaxHp(), guardian.getCurrentHp(),
                            guardian.getAttack(), guardian.getAttack(),
                            guardian.getSpeed(), guardian.getSpeed(),
                            fieldB.getName(), fieldB.getCamp(), fieldB.getPosition(),
                            fieldB.getMaxHp(), fieldB.getCurrentHp(),
                            fieldB.getAttack(), fieldB.getAttack(),
                            fieldB.getSpeed(), fieldB.getSpeed(),
                            getFieldUnitsStatus(),
                            damage, EffectType.MISSILE_DAMAGE, DamageType.MISSILE,
                            guardian.getName() + "魂力飞弹");
                    // 死亡日志
                    if (!deadUnits.isEmpty()) {
                        Map<String, Object[]> deadStatus = new HashMap<>();
                        deadUnits.forEach(x -> {
                            List<String> strings = Arrays.asList(x.split("_"));
                            deadStatus.put(strings.get(0), new Object[]{Integer.parseInt(strings.get(1)), 0, 0});
                        });

                        addMultiTargetLog("UNIT_DEATH",
                                "SYSTEM", null, 0,
                                0, 0, 0, 0, 0, 0,
                                deadUnits, null,
                                deadStatus,
                                getFieldUnitsStatus(),
                                0, null, null,
                                "飞弹伤害");
                        //触发死亡技能
                        triggerOnDeathSkills(fieldB);

                    } else {
                        //触发受击技能
                        triggerOnAttackedSkills(fieldB, EffectType.MISSILE_DAMAGE);
                    }
                }
            }
//            镇元子，魂力飞弹Lv1场下，每当新单位入场时，对场上敌人造成178点飞弹伤害；禁心咒Lv1场下，每当有单位登场，有17%几率令场上英雄沉默2回合；多宝道人同Lv1与多宝道人在同一队伍时，增加自身453点生命上限，158点攻击，158点速度。
            if (campB.stream().anyMatch(g -> g.getName().equals("南华真人") && g.getPosition() == position && !g.isDead() && !g.isOnField())) {
                Guardian guardian = campB.stream()
                        .filter(g -> g.getName().equals("南华真人") && g.getPosition() == position && !g.isDead() && !g.isOnField())
                        .findFirst().get();
                int[] skillLevel = CardSkillLevelUtil.calculateSkillLevels(guardian.getLevel(), guardian.getStar().doubleValue());
                if (fieldA != null && !fieldA.isDead()) {
                    int damage = 178 * skillLevel[1] + zhongyuedadiHuan(defender);
                    fieldA.setCurrentHp(fieldA.getCurrentHp() - damage);
                    List<String> deadUnits = new ArrayList<>();

                    if (fieldA.getCurrentHp() <= 0) {
                        fieldA.setDead(true);
                        fieldA.setOnField(false);
                        deadUnits.add(fieldA.getCamp() + fieldA.getName() + "_" + fieldA.getPosition());
                    }
                    addLog("魂力飞弹",
                            guardian.getName(), guardian.getCamp(), guardian.getPosition(),
                            guardian.getMaxHp(), guardian.getCurrentHp(),
                            guardian.getAttack(), guardian.getAttack(),
                            guardian.getSpeed(), guardian.getSpeed(),
                            fieldA.getName(), fieldA.getCamp(), fieldA.getPosition(),
                            fieldA.getMaxHp(), fieldA.getCurrentHp(),
                            fieldA.getAttack(), fieldA.getAttack(),
                            fieldA.getSpeed(), fieldA.getSpeed(),
                            getFieldUnitsStatus(),
                            damage, EffectType.MISSILE_DAMAGE, DamageType.MISSILE,
                            guardian.getName() + "魂力飞弹");
                    // 死亡日志
                    if (!deadUnits.isEmpty()) {
                        Map<String, Object[]> deadStatus = new HashMap<>();
                        deadUnits.forEach(x -> {
                            List<String> strings = Arrays.asList(x.split("_"));
                            deadStatus.put(strings.get(0), new Object[]{Integer.parseInt(strings.get(1)), 0, 0});
                        });

                        addMultiTargetLog("UNIT_DEATH",
                                "SYSTEM", null, 0,
                                0, 0, 0, 0, 0, 0,
                                deadUnits, null,
                                deadStatus,
                                getFieldUnitsStatus(),
                                0, null, null,
                                "飞弹伤害");
                        //触发死亡技能
                        triggerOnDeathSkills(fieldA);

                    } else {
                        //触发受击技能
                        triggerOnAttackedSkills(fieldA, EffectType.MISSILE_DAMAGE);
                    }
                }
            }


            //            南华真人，魂力飞弹Lv1场下，每当新单位入场时，对场上敌人造成178点飞弹伤害；报复神箭Lv1场下，每回合对场上敌方造成106；
            if (campA.stream().anyMatch(g -> g.getName().equals("北岳大帝") && g.getPosition() == position && !g.isDead() && !g.isOnField())) {
                Guardian guardian = campA.stream()
                        .filter(g -> g.getName().equals("北岳大帝") && g.getPosition() == position && !g.isDead() && !g.isOnField())
                        .findFirst().get();
                int[] skillLevel = CardSkillLevelUtil.calculateSkillLevels(guardian.getLevel(), guardian.getStar().doubleValue());
                if (fieldB != null && !fieldB.isDead()) {
                    int damage = 169 * skillLevel[1] + zhongyuedadiHuan(defender);
                    fieldB.setCurrentHp(fieldB.getCurrentHp() - damage);
                    List<String> deadUnits = new ArrayList<>();

                    if (fieldB.getCurrentHp() <= 0) {
                        fieldB.setDead(true);
                        fieldB.setOnField(false);
                        deadUnits.add(fieldB.getCamp() + fieldB.getName() + "_" + fieldB.getPosition());
                    }
                    addLog("魂力飞弹",
                            guardian.getName(), guardian.getCamp(), guardian.getPosition(),
                            guardian.getMaxHp(), guardian.getCurrentHp(),
                            guardian.getAttack(), guardian.getAttack(),
                            guardian.getSpeed(), guardian.getSpeed(),
                            fieldB.getName(), fieldB.getCamp(), fieldB.getPosition(),
                            fieldB.getMaxHp(), fieldB.getCurrentHp(),
                            fieldB.getAttack(), fieldB.getAttack(),
                            fieldB.getSpeed(), fieldB.getSpeed(),
                            getFieldUnitsStatus(),
                            damage, EffectType.MISSILE_DAMAGE, DamageType.MISSILE,
                            guardian.getName() + "魂力飞弹");
                    // 死亡日志
                    if (!deadUnits.isEmpty()) {
                        Map<String, Object[]> deadStatus = new HashMap<>();
                        deadUnits.forEach(x -> {
                            List<String> strings = Arrays.asList(x.split("_"));
                            deadStatus.put(strings.get(0), new Object[]{Integer.parseInt(strings.get(1)), 0, 0});
                        });

                        addMultiTargetLog("UNIT_DEATH",
                                "SYSTEM", null, 0,
                                0, 0, 0, 0, 0, 0,
                                deadUnits, null,
                                deadStatus,
                                getFieldUnitsStatus(),
                                0, null, null,
                                "飞弹伤害");
                        //触发死亡技能
                        triggerOnDeathSkills(fieldB);

                    } else {
                        //触发受击技能
                        triggerOnAttackedSkills(fieldB, EffectType.MISSILE_DAMAGE);
                    }
                }
            }
//            镇元子，魂力飞弹Lv1场下，每当新单位入场时，对场上敌人造成178点飞弹伤害；禁心咒Lv1场下，每当有单位登场，有17%几率令场上英雄沉默2回合；多宝道人同Lv1与多宝道人在同一队伍时，增加自身453点生命上限，158点攻击，158点速度。
            if (campB.stream().anyMatch(g -> g.getName().equals("北岳大帝") && g.getPosition() == position && !g.isDead() && !g.isOnField())) {
                Guardian guardian = campB.stream()
                        .filter(g -> g.getName().equals("北岳大帝") && g.getPosition() == position && !g.isDead() && !g.isOnField())
                        .findFirst().get();
                int[] skillLevel = CardSkillLevelUtil.calculateSkillLevels(guardian.getLevel(), guardian.getStar().doubleValue());
                if (fieldA != null && !fieldA.isDead()) {
                    int damage = 169 * skillLevel[1] + zhongyuedadiHuan(defender);
                    fieldA.setCurrentHp(fieldA.getCurrentHp() - damage);
                    List<String> deadUnits = new ArrayList<>();

                    if (fieldA.getCurrentHp() <= 0) {
                        fieldA.setDead(true);
                        fieldA.setOnField(false);
                        deadUnits.add(fieldA.getCamp() + fieldA.getName() + "_" + fieldA.getPosition());
                    }
                    addLog("魂力飞弹",
                            guardian.getName(), guardian.getCamp(), guardian.getPosition(),
                            guardian.getMaxHp(), guardian.getCurrentHp(),
                            guardian.getAttack(), guardian.getAttack(),
                            guardian.getSpeed(), guardian.getSpeed(),
                            fieldA.getName(), fieldA.getCamp(), fieldA.getPosition(),
                            fieldA.getMaxHp(), fieldA.getCurrentHp(),
                            fieldA.getAttack(), fieldA.getAttack(),
                            fieldA.getSpeed(), fieldA.getSpeed(),
                            getFieldUnitsStatus(),
                            damage, EffectType.MISSILE_DAMAGE, DamageType.MISSILE,
                            guardian.getName() + "魂力飞弹");
                    // 死亡日志
                    if (!deadUnits.isEmpty()) {
                        Map<String, Object[]> deadStatus = new HashMap<>();
                        deadUnits.forEach(x -> {
                            List<String> strings = Arrays.asList(x.split("_"));
                            deadStatus.put(strings.get(0), new Object[]{Integer.parseInt(strings.get(1)), 0, 0});
                        });

                        addMultiTargetLog("UNIT_DEATH",
                                "SYSTEM", null, 0,
                                0, 0, 0, 0, 0, 0,
                                deadUnits, null,
                                deadStatus,
                                getFieldUnitsStatus(),
                                0, null, null,
                                "飞弹伤害");
                        //触发死亡技能
                        triggerOnDeathSkills(fieldA);

                    } else {
                        //触发受击技能
                        triggerOnAttackedSkills(fieldA, EffectType.MISSILE_DAMAGE);
                    }
                }
            }

            //            南华真人，魂力飞弹Lv1场下，每当新单位入场时，对场上敌人造成178点飞弹伤害；报复神箭Lv1场下，每回合对场上敌方造成106；
            if (campA.stream().anyMatch(g -> g.getName().equals("西岳大帝") && g.getPosition() == position && !g.isDead() && !g.isOnField())) {
                Guardian guardian = campA.stream()
                        .filter(g -> g.getName().equals("西岳大帝") && g.getPosition() == position && !g.isDead() && !g.isOnField())
                        .findFirst().get();
                int[] skillLevel = CardSkillLevelUtil.calculateSkillLevels(guardian.getLevel(), guardian.getStar().doubleValue());
                if (fieldB != null && !fieldB.isDead()) {
                    int silence = guardian.getBuffStacks();
                    int damage = 84 * skillLevel[1] + zhongyuedadiHuan(defender)+16*silence;
                    fieldB.setCurrentHp(fieldB.getCurrentHp() - damage);
                    List<String> deadUnits = new ArrayList<>();

                    if (fieldB.getCurrentHp() <= 0) {
                        fieldB.setDead(true);
                        fieldB.setOnField(false);
                        deadUnits.add(fieldB.getCamp() + fieldB.getName() + "_" + fieldB.getPosition());
                    }
                    addLog("魂力飞弹",
                            guardian.getName(), guardian.getCamp(), guardian.getPosition(),
                            guardian.getMaxHp(), guardian.getCurrentHp(),
                            guardian.getAttack(), guardian.getAttack(),
                            guardian.getSpeed(), guardian.getSpeed(),
                            fieldB.getName(), fieldB.getCamp(), fieldB.getPosition(),
                            fieldB.getMaxHp(), fieldB.getCurrentHp(),
                            fieldB.getAttack(), fieldB.getAttack(),
                            fieldB.getSpeed(), fieldB.getSpeed(),
                            getFieldUnitsStatus(),
                            damage, EffectType.MISSILE_DAMAGE, DamageType.MISSILE,
                            guardian.getName() + "魂力飞弹");
                    // 死亡日志
                    if (!deadUnits.isEmpty()) {
                        Map<String, Object[]> deadStatus = new HashMap<>();
                        deadUnits.forEach(x -> {
                            List<String> strings = Arrays.asList(x.split("_"));
                            deadStatus.put(strings.get(0), new Object[]{Integer.parseInt(strings.get(1)), 0, 0});
                        });

                        addMultiTargetLog("UNIT_DEATH",
                                "SYSTEM", null, 0,
                                0, 0, 0, 0, 0, 0,
                                deadUnits, null,
                                deadStatus,
                                getFieldUnitsStatus(),
                                0, null, null,
                                "飞弹伤害");
                        //触发死亡技能
                        triggerOnDeathSkills(fieldB);

                    } else {
                        //触发受击技能
                        triggerOnAttackedSkills(fieldB, EffectType.MISSILE_DAMAGE);
                    }
                }
            }
//            镇元子，魂力飞弹Lv1场下，每当新单位入场时，对场上敌人造成178点飞弹伤害；禁心咒Lv1场下，每当有单位登场，有17%几率令场上英雄沉默2回合；多宝道人同Lv1与多宝道人在同一队伍时，增加自身453点生命上限，158点攻击，158点速度。
            if (campB.stream().anyMatch(g -> g.getName().equals("西岳大帝") && g.getPosition() == position && !g.isDead() && !g.isOnField())) {
                Guardian guardian = campB.stream()
                        .filter(g -> g.getName().equals("西岳大帝") && g.getPosition() == position && !g.isDead() && !g.isOnField())
                        .findFirst().get();
                int[] skillLevel = CardSkillLevelUtil.calculateSkillLevels(guardian.getLevel(), guardian.getStar().doubleValue());
                if (fieldA != null && !fieldA.isDead()) {
                    int silence = guardian.getBuffStacks();
                    int damage = 84 * skillLevel[1] + zhongyuedadiHuan(defender)+16*silence;
                    fieldA.setCurrentHp(fieldA.getCurrentHp() - damage);
                    List<String> deadUnits = new ArrayList<>();

                    if (fieldA.getCurrentHp() <= 0) {
                        fieldA.setDead(true);
                        fieldA.setOnField(false);
                        deadUnits.add(fieldA.getCamp() + fieldA.getName() + "_" + fieldA.getPosition());
                    }
                    addLog("魂力飞弹",
                            guardian.getName(), guardian.getCamp(), guardian.getPosition(),
                            guardian.getMaxHp(), guardian.getCurrentHp(),
                            guardian.getAttack(), guardian.getAttack(),
                            guardian.getSpeed(), guardian.getSpeed(),
                            fieldA.getName(), fieldA.getCamp(), fieldA.getPosition(),
                            fieldA.getMaxHp(), fieldA.getCurrentHp(),
                            fieldA.getAttack(), fieldA.getAttack(),
                            fieldA.getSpeed(), fieldA.getSpeed(),
                            getFieldUnitsStatus(),
                            damage, EffectType.MISSILE_DAMAGE, DamageType.MISSILE,
                            guardian.getName() + "魂力飞弹");
                    // 死亡日志
                    if (!deadUnits.isEmpty()) {
                        Map<String, Object[]> deadStatus = new HashMap<>();
                        deadUnits.forEach(x -> {
                            List<String> strings = Arrays.asList(x.split("_"));
                            deadStatus.put(strings.get(0), new Object[]{Integer.parseInt(strings.get(1)), 0, 0});
                        });

                        addMultiTargetLog("UNIT_DEATH",
                                "SYSTEM", null, 0,
                                0, 0, 0, 0, 0, 0,
                                deadUnits, null,
                                deadStatus,
                                getFieldUnitsStatus(),
                                0, null, null,
                                "飞弹伤害");
                        //触发死亡技能
                        triggerOnDeathSkills(fieldA);

                    } else {
                        //触发受击技能
                        triggerOnAttackedSkills(fieldA, EffectType.MISSILE_DAMAGE);
                    }
                }
            }

            //            南华真人，魂力飞弹Lv1场下，每当新单位入场时，对场上敌人造成178点飞弹伤害；报复神箭Lv1场下，每回合对场上敌方造成106；
            if (campA.stream().anyMatch(g -> g.getName().equals("西海龙王") && g.getPosition() == position && !g.isDead() && !g.isOnField())) {
                Guardian guardian = campA.stream()
                        .filter(g -> g.getName().equals("西海龙王") && g.getPosition() == position && !g.isDead() && !g.isOnField())
                        .findFirst().get();
                int[] skillLevel = CardSkillLevelUtil.calculateSkillLevels(guardian.getLevel(), guardian.getStar().doubleValue());
                if (fieldB != null && !fieldB.isDead()) {
                    int silence = guardian.getBuffStacks();
                    int damage = 50 * skillLevel[1] + zhongyuedadiHuan(defender);
                    fieldB.setCurrentHp(fieldB.getCurrentHp() - damage);
                    List<String> deadUnits = new ArrayList<>();

                    if (fieldB.getCurrentHp() <= 0) {
                        fieldB.setDead(true);
                        fieldB.setOnField(false);
                        deadUnits.add(fieldB.getCamp() + fieldB.getName() + "_" + fieldB.getPosition());
                    }
                    addLog("魂力飞弹",
                            guardian.getName(), guardian.getCamp(), guardian.getPosition(),
                            guardian.getMaxHp(), guardian.getCurrentHp(),
                            guardian.getAttack(), guardian.getAttack(),
                            guardian.getSpeed(), guardian.getSpeed(),
                            fieldB.getName(), fieldB.getCamp(), fieldB.getPosition(),
                            fieldB.getMaxHp(), fieldB.getCurrentHp(),
                            fieldB.getAttack(), fieldB.getAttack(),
                            fieldB.getSpeed(), fieldB.getSpeed(),
                            getFieldUnitsStatus(),
                            damage, EffectType.MISSILE_DAMAGE, DamageType.MISSILE,
                            guardian.getName() + "魂力飞弹");
                    // 死亡日志
                    if (!deadUnits.isEmpty()) {
                        Map<String, Object[]> deadStatus = new HashMap<>();
                        deadUnits.forEach(x -> {
                            List<String> strings = Arrays.asList(x.split("_"));
                            deadStatus.put(strings.get(0), new Object[]{Integer.parseInt(strings.get(1)), 0, 0});
                        });

                        addMultiTargetLog("UNIT_DEATH",
                                "SYSTEM", null, 0,
                                0, 0, 0, 0, 0, 0,
                                deadUnits, null,
                                deadStatus,
                                getFieldUnitsStatus(),
                                0, null, null,
                                "飞弹伤害");
                        //触发死亡技能
                        triggerOnDeathSkills(fieldB);

                    } else {
                        //触发受击技能
                        triggerOnAttackedSkills(fieldB, EffectType.MISSILE_DAMAGE);
                    }
                }
            }
//            镇元子，魂力飞弹Lv1场下，每当新单位入场时，对场上敌人造成178点飞弹伤害；禁心咒Lv1场下，每当有单位登场，有17%几率令场上英雄沉默2回合；多宝道人同Lv1与多宝道人在同一队伍时，增加自身453点生命上限，158点攻击，158点速度。
            if (campB.stream().anyMatch(g -> g.getName().equals("西海龙王") && g.getPosition() == position && !g.isDead() && !g.isOnField())) {
                Guardian guardian = campB.stream()
                        .filter(g -> g.getName().equals("西海龙王") && g.getPosition() == position && !g.isDead() && !g.isOnField())
                        .findFirst().get();
                int[] skillLevel = CardSkillLevelUtil.calculateSkillLevels(guardian.getLevel(), guardian.getStar().doubleValue());
                if (fieldA != null && !fieldA.isDead()) {
                    int silence = guardian.getBuffStacks();
                    int damage = 50 * skillLevel[1] + zhongyuedadiHuan(defender);
                    fieldA.setCurrentHp(fieldA.getCurrentHp() - damage);
                    List<String> deadUnits = new ArrayList<>();

                    if (fieldA.getCurrentHp() <= 0) {
                        fieldA.setDead(true);
                        fieldA.setOnField(false);
                        deadUnits.add(fieldA.getCamp() + fieldA.getName() + "_" + fieldA.getPosition());
                    }
                    addLog("魂力飞弹",
                            guardian.getName(), guardian.getCamp(), guardian.getPosition(),
                            guardian.getMaxHp(), guardian.getCurrentHp(),
                            guardian.getAttack(), guardian.getAttack(),
                            guardian.getSpeed(), guardian.getSpeed(),
                            fieldA.getName(), fieldA.getCamp(), fieldA.getPosition(),
                            fieldA.getMaxHp(), fieldA.getCurrentHp(),
                            fieldA.getAttack(), fieldA.getAttack(),
                            fieldA.getSpeed(), fieldA.getSpeed(),
                            getFieldUnitsStatus(),
                            damage, EffectType.MISSILE_DAMAGE, DamageType.MISSILE,
                            guardian.getName() + "魂力飞弹");
                    // 死亡日志
                    if (!deadUnits.isEmpty()) {
                        Map<String, Object[]> deadStatus = new HashMap<>();
                        deadUnits.forEach(x -> {
                            List<String> strings = Arrays.asList(x.split("_"));
                            deadStatus.put(strings.get(0), new Object[]{Integer.parseInt(strings.get(1)), 0, 0});
                        });

                        addMultiTargetLog("UNIT_DEATH",
                                "SYSTEM", null, 0,
                                0, 0, 0, 0, 0, 0,
                                deadUnits, null,
                                deadStatus,
                                getFieldUnitsStatus(),
                                0, null, null,
                                "飞弹伤害");
                        //触发死亡技能
                        triggerOnDeathSkills(fieldA);

                    } else {
                        //触发受击技能
                        triggerOnAttackedSkills(fieldA, EffectType.MISSILE_DAMAGE);
                    }
                }
            }

            //            南华真人，魂力飞弹Lv1场下，每当新单位入场时，对场上敌人造成178点飞弹伤害；报复神箭Lv1场下，每回合对场上敌方造成106；
            if (campA.stream().anyMatch(g -> g.getName().equals("东海龙王") && g.getPosition() == position && !g.isDead() && !g.isOnField())) {
                Guardian guardian = campA.stream()
                        .filter(g -> g.getName().equals("东海龙王") && g.getPosition() == position && !g.isDead() && !g.isOnField())
                        .findFirst().get();
                int[] skillLevel = CardSkillLevelUtil.calculateSkillLevels(guardian.getLevel(), guardian.getStar().doubleValue());
                if (fieldB != null && !fieldB.isDead()) {
                    int silence = guardian.getBuffStacks();
                    int damage = 50 * skillLevel[1] + zhongyuedadiHuan(defender);
                    fieldB.setCurrentHp(fieldB.getCurrentHp() - damage);
                    List<String> deadUnits = new ArrayList<>();

                    if (fieldB.getCurrentHp() <= 0) {
                        fieldB.setDead(true);
                        fieldB.setOnField(false);
                        deadUnits.add(fieldB.getCamp() + fieldB.getName() + "_" + fieldB.getPosition());
                    }
                    addLog("魂力飞弹",
                            guardian.getName(), guardian.getCamp(), guardian.getPosition(),
                            guardian.getMaxHp(), guardian.getCurrentHp(),
                            guardian.getAttack(), guardian.getAttack(),
                            guardian.getSpeed(), guardian.getSpeed(),
                            fieldB.getName(), fieldB.getCamp(), fieldB.getPosition(),
                            fieldB.getMaxHp(), fieldB.getCurrentHp(),
                            fieldB.getAttack(), fieldB.getAttack(),
                            fieldB.getSpeed(), fieldB.getSpeed(),
                            getFieldUnitsStatus(),
                            damage, EffectType.MISSILE_DAMAGE, DamageType.MISSILE,
                            guardian.getName() + "魂力飞弹");
                    // 死亡日志
                    if (!deadUnits.isEmpty()) {
                        Map<String, Object[]> deadStatus = new HashMap<>();
                        deadUnits.forEach(x -> {
                            List<String> strings = Arrays.asList(x.split("_"));
                            deadStatus.put(strings.get(0), new Object[]{Integer.parseInt(strings.get(1)), 0, 0});
                        });

                        addMultiTargetLog("UNIT_DEATH",
                                "SYSTEM", null, 0,
                                0, 0, 0, 0, 0, 0,
                                deadUnits, null,
                                deadStatus,
                                getFieldUnitsStatus(),
                                0, null, null,
                                "飞弹伤害");
                        //触发死亡技能
                        triggerOnDeathSkills(fieldB);

                    } else {
                        //触发受击技能
                        triggerOnAttackedSkills(fieldB, EffectType.MISSILE_DAMAGE);
                    }
                }
            }
//            镇元子，魂力飞弹Lv1场下，每当新单位入场时，对场上敌人造成178点飞弹伤害；禁心咒Lv1场下，每当有单位登场，有17%几率令场上英雄沉默2回合；多宝道人同Lv1与多宝道人在同一队伍时，增加自身453点生命上限，158点攻击，158点速度。
            if (campB.stream().anyMatch(g -> g.getName().equals("东海龙王") && g.getPosition() == position && !g.isDead() && !g.isOnField())) {
                Guardian guardian = campB.stream()
                        .filter(g -> g.getName().equals("东海龙王") && g.getPosition() == position && !g.isDead() && !g.isOnField())
                        .findFirst().get();
                int[] skillLevel = CardSkillLevelUtil.calculateSkillLevels(guardian.getLevel(), guardian.getStar().doubleValue());
                if (fieldA != null && !fieldA.isDead()) {
                    int silence = guardian.getBuffStacks();
                    int damage = 50 * skillLevel[1] + zhongyuedadiHuan(defender);
                    fieldA.setCurrentHp(fieldA.getCurrentHp() - damage);
                    List<String> deadUnits = new ArrayList<>();

                    if (fieldA.getCurrentHp() <= 0) {
                        fieldA.setDead(true);
                        fieldA.setOnField(false);
                        deadUnits.add(fieldA.getCamp() + fieldA.getName() + "_" + fieldA.getPosition());
                    }
                    addLog("魂力飞弹",
                            guardian.getName(), guardian.getCamp(), guardian.getPosition(),
                            guardian.getMaxHp(), guardian.getCurrentHp(),
                            guardian.getAttack(), guardian.getAttack(),
                            guardian.getSpeed(), guardian.getSpeed(),
                            fieldA.getName(), fieldA.getCamp(), fieldA.getPosition(),
                            fieldA.getMaxHp(), fieldA.getCurrentHp(),
                            fieldA.getAttack(), fieldA.getAttack(),
                            fieldA.getSpeed(), fieldA.getSpeed(),
                            getFieldUnitsStatus(),
                            damage, EffectType.MISSILE_DAMAGE, DamageType.MISSILE,
                            guardian.getName() + "魂力飞弹");
                    // 死亡日志
                    if (!deadUnits.isEmpty()) {
                        Map<String, Object[]> deadStatus = new HashMap<>();
                        deadUnits.forEach(x -> {
                            List<String> strings = Arrays.asList(x.split("_"));
                            deadStatus.put(strings.get(0), new Object[]{Integer.parseInt(strings.get(1)), 0, 0});
                        });

                        addMultiTargetLog("UNIT_DEATH",
                                "SYSTEM", null, 0,
                                0, 0, 0, 0, 0, 0,
                                deadUnits, null,
                                deadStatus,
                                getFieldUnitsStatus(),
                                0, null, null,
                                "飞弹伤害");
                        //触发死亡技能
                        triggerOnDeathSkills(fieldA);

                    } else {
                        //触发受击技能
                        triggerOnAttackedSkills(fieldA, EffectType.MISSILE_DAMAGE);
                    }
                }
            }

            //            南华真人，魂力飞弹Lv1场下，每当新单位入场时，对场上敌人造成178点飞弹伤害；报复神箭Lv1场下，每回合对场上敌方造成106；
            if (campA.stream().anyMatch(g -> g.getName().equals("南海龙王") && g.getPosition() == position && !g.isDead() && !g.isOnField())) {
                Guardian guardian = campA.stream()
                        .filter(g -> g.getName().equals("南海龙王") && g.getPosition() == position && !g.isDead() && !g.isOnField())
                        .findFirst().get();
                int[] skillLevel = CardSkillLevelUtil.calculateSkillLevels(guardian.getLevel(), guardian.getStar().doubleValue());
                if (fieldB != null && !fieldB.isDead()) {
                    int silence = guardian.getBuffStacks();
                    int damage = 50 * skillLevel[1] + zhongyuedadiHuan(defender);
                    fieldB.setCurrentHp(fieldB.getCurrentHp() - damage);
                    List<String> deadUnits = new ArrayList<>();

                    if (fieldB.getCurrentHp() <= 0) {
                        fieldB.setDead(true);
                        fieldB.setOnField(false);
                        deadUnits.add(fieldB.getCamp() + fieldB.getName() + "_" + fieldB.getPosition());
                    }
                    addLog("魂力飞弹",
                            guardian.getName(), guardian.getCamp(), guardian.getPosition(),
                            guardian.getMaxHp(), guardian.getCurrentHp(),
                            guardian.getAttack(), guardian.getAttack(),
                            guardian.getSpeed(), guardian.getSpeed(),
                            fieldB.getName(), fieldB.getCamp(), fieldB.getPosition(),
                            fieldB.getMaxHp(), fieldB.getCurrentHp(),
                            fieldB.getAttack(), fieldB.getAttack(),
                            fieldB.getSpeed(), fieldB.getSpeed(),
                            getFieldUnitsStatus(),
                            damage, EffectType.MISSILE_DAMAGE, DamageType.MISSILE,
                            guardian.getName() + "魂力飞弹");
                    // 死亡日志
                    if (!deadUnits.isEmpty()) {
                        Map<String, Object[]> deadStatus = new HashMap<>();
                        deadUnits.forEach(x -> {
                            List<String> strings = Arrays.asList(x.split("_"));
                            deadStatus.put(strings.get(0), new Object[]{Integer.parseInt(strings.get(1)), 0, 0});
                        });

                        addMultiTargetLog("UNIT_DEATH",
                                "SYSTEM", null, 0,
                                0, 0, 0, 0, 0, 0,
                                deadUnits, null,
                                deadStatus,
                                getFieldUnitsStatus(),
                                0, null, null,
                                "飞弹伤害");
                        //触发死亡技能
                        triggerOnDeathSkills(fieldB);

                    } else {
                        //触发受击技能
                        triggerOnAttackedSkills(fieldB, EffectType.MISSILE_DAMAGE);
                    }
                }
            }
//            镇元子，魂力飞弹Lv1场下，每当新单位入场时，对场上敌人造成178点飞弹伤害；禁心咒Lv1场下，每当有单位登场，有17%几率令场上英雄沉默2回合；多宝道人同Lv1与多宝道人在同一队伍时，增加自身453点生命上限，158点攻击，158点速度。
            if (campB.stream().anyMatch(g -> g.getName().equals("南海龙王") && g.getPosition() == position && !g.isDead() && !g.isOnField())) {
                Guardian guardian = campB.stream()
                        .filter(g -> g.getName().equals("南海龙王") && g.getPosition() == position && !g.isDead() && !g.isOnField())
                        .findFirst().get();
                int[] skillLevel = CardSkillLevelUtil.calculateSkillLevels(guardian.getLevel(), guardian.getStar().doubleValue());
                if (fieldA != null && !fieldA.isDead()) {
                    int silence = guardian.getBuffStacks();
                    int damage = 50 * skillLevel[1] + zhongyuedadiHuan(defender);
                    fieldA.setCurrentHp(fieldA.getCurrentHp() - damage);
                    List<String> deadUnits = new ArrayList<>();

                    if (fieldA.getCurrentHp() <= 0) {
                        fieldA.setDead(true);
                        fieldA.setOnField(false);
                        deadUnits.add(fieldA.getCamp() + fieldA.getName() + "_" + fieldA.getPosition());
                    }
                    addLog("魂力飞弹",
                            guardian.getName(), guardian.getCamp(), guardian.getPosition(),
                            guardian.getMaxHp(), guardian.getCurrentHp(),
                            guardian.getAttack(), guardian.getAttack(),
                            guardian.getSpeed(), guardian.getSpeed(),
                            fieldA.getName(), fieldA.getCamp(), fieldA.getPosition(),
                            fieldA.getMaxHp(), fieldA.getCurrentHp(),
                            fieldA.getAttack(), fieldA.getAttack(),
                            fieldA.getSpeed(), fieldA.getSpeed(),
                            getFieldUnitsStatus(),
                            damage, EffectType.MISSILE_DAMAGE, DamageType.MISSILE,
                            guardian.getName() + "魂力飞弹");
                    // 死亡日志
                    if (!deadUnits.isEmpty()) {
                        Map<String, Object[]> deadStatus = new HashMap<>();
                        deadUnits.forEach(x -> {
                            List<String> strings = Arrays.asList(x.split("_"));
                            deadStatus.put(strings.get(0), new Object[]{Integer.parseInt(strings.get(1)), 0, 0});
                        });

                        addMultiTargetLog("UNIT_DEATH",
                                "SYSTEM", null, 0,
                                0, 0, 0, 0, 0, 0,
                                deadUnits, null,
                                deadStatus,
                                getFieldUnitsStatus(),
                                0, null, null,
                                "飞弹伤害");
                        //触发死亡技能
                        triggerOnDeathSkills(fieldA);

                    } else {
                        //触发受击技能
                        triggerOnAttackedSkills(fieldA, EffectType.MISSILE_DAMAGE);
                    }
                }
            }

            //            南华真人，魂力飞弹Lv1场下，每当新单位入场时，对场上敌人造成178点飞弹伤害；报复神箭Lv1场下，每回合对场上敌方造成106；
            if (campA.stream().anyMatch(g -> g.getName().equals("北海龙王") && g.getPosition() == position && !g.isDead() && !g.isOnField())) {
                Guardian guardian = campA.stream()
                        .filter(g -> g.getName().equals("北海龙王") && g.getPosition() == position && !g.isDead() && !g.isOnField())
                        .findFirst().get();
                int[] skillLevel = CardSkillLevelUtil.calculateSkillLevels(guardian.getLevel(), guardian.getStar().doubleValue());
                if (fieldB != null && !fieldB.isDead()) {
                    int silence = guardian.getBuffStacks();
                    int damage = 50 * skillLevel[1] + zhongyuedadiHuan(defender);
                    fieldB.setCurrentHp(fieldB.getCurrentHp() - damage);
                    List<String> deadUnits = new ArrayList<>();

                    if (fieldB.getCurrentHp() <= 0) {
                        fieldB.setDead(true);
                        fieldB.setOnField(false);
                        deadUnits.add(fieldB.getCamp() + fieldB.getName() + "_" + fieldB.getPosition());
                    }
                    addLog("魂力飞弹",
                            guardian.getName(), guardian.getCamp(), guardian.getPosition(),
                            guardian.getMaxHp(), guardian.getCurrentHp(),
                            guardian.getAttack(), guardian.getAttack(),
                            guardian.getSpeed(), guardian.getSpeed(),
                            fieldB.getName(), fieldB.getCamp(), fieldB.getPosition(),
                            fieldB.getMaxHp(), fieldB.getCurrentHp(),
                            fieldB.getAttack(), fieldB.getAttack(),
                            fieldB.getSpeed(), fieldB.getSpeed(),
                            getFieldUnitsStatus(),
                            damage, EffectType.MISSILE_DAMAGE, DamageType.MISSILE,
                            guardian.getName() + "魂力飞弹");
                    // 死亡日志
                    if (!deadUnits.isEmpty()) {
                        Map<String, Object[]> deadStatus = new HashMap<>();
                        deadUnits.forEach(x -> {
                            List<String> strings = Arrays.asList(x.split("_"));
                            deadStatus.put(strings.get(0), new Object[]{Integer.parseInt(strings.get(1)), 0, 0});
                        });

                        addMultiTargetLog("UNIT_DEATH",
                                "SYSTEM", null, 0,
                                0, 0, 0, 0, 0, 0,
                                deadUnits, null,
                                deadStatus,
                                getFieldUnitsStatus(),
                                0, null, null,
                                "飞弹伤害");
                        //触发死亡技能
                        triggerOnDeathSkills(fieldB);

                    } else {
                        //触发受击技能
                        triggerOnAttackedSkills(fieldB, EffectType.MISSILE_DAMAGE);
                    }
                }
            }
//            镇元子，魂力飞弹Lv1场下，每当新单位入场时，对场上敌人造成178点飞弹伤害；禁心咒Lv1场下，每当有单位登场，有17%几率令场上英雄沉默2回合；多宝道人同Lv1与多宝道人在同一队伍时，增加自身453点生命上限，158点攻击，158点速度。
            if (campB.stream().anyMatch(g -> g.getName().equals("北海龙王") && g.getPosition() == position && !g.isDead() && !g.isOnField())) {
                Guardian guardian = campB.stream()
                        .filter(g -> g.getName().equals("北海龙王") && g.getPosition() == position && !g.isDead() && !g.isOnField())
                        .findFirst().get();
                int[] skillLevel = CardSkillLevelUtil.calculateSkillLevels(guardian.getLevel(), guardian.getStar().doubleValue());
                if (fieldA != null && !fieldA.isDead()) {
                    int silence = guardian.getBuffStacks();
                    int damage = 50 * skillLevel[1] + zhongyuedadiHuan(defender);
                    fieldA.setCurrentHp(fieldA.getCurrentHp() - damage);
                    List<String> deadUnits = new ArrayList<>();

                    if (fieldA.getCurrentHp() <= 0) {
                        fieldA.setDead(true);
                        fieldA.setOnField(false);
                        deadUnits.add(fieldA.getCamp() + fieldA.getName() + "_" + fieldA.getPosition());
                    }
                    addLog("魂力飞弹",
                            guardian.getName(), guardian.getCamp(), guardian.getPosition(),
                            guardian.getMaxHp(), guardian.getCurrentHp(),
                            guardian.getAttack(), guardian.getAttack(),
                            guardian.getSpeed(), guardian.getSpeed(),
                            fieldA.getName(), fieldA.getCamp(), fieldA.getPosition(),
                            fieldA.getMaxHp(), fieldA.getCurrentHp(),
                            fieldA.getAttack(), fieldA.getAttack(),
                            fieldA.getSpeed(), fieldA.getSpeed(),
                            getFieldUnitsStatus(),
                            damage, EffectType.MISSILE_DAMAGE, DamageType.MISSILE,
                            guardian.getName() + "魂力飞弹");
                    // 死亡日志
                    if (!deadUnits.isEmpty()) {
                        Map<String, Object[]> deadStatus = new HashMap<>();
                        deadUnits.forEach(x -> {
                            List<String> strings = Arrays.asList(x.split("_"));
                            deadStatus.put(strings.get(0), new Object[]{Integer.parseInt(strings.get(1)), 0, 0});
                        });

                        addMultiTargetLog("UNIT_DEATH",
                                "SYSTEM", null, 0,
                                0, 0, 0, 0, 0, 0,
                                deadUnits, null,
                                deadStatus,
                                getFieldUnitsStatus(),
                                0, null, null,
                                "飞弹伤害");
                        //触发死亡技能
                        triggerOnDeathSkills(fieldA);

                    } else {
                        //触发受击技能
                        triggerOnAttackedSkills(fieldA, EffectType.MISSILE_DAMAGE);
                    }
                }
            }

            //            南华真人，魂力飞弹Lv1场下，每当新单位入场时，对场上敌人造成178点飞弹伤害；报复神箭Lv1场下，每回合对场上敌方造成106；
            if (campA.stream().anyMatch(g -> g.getName().equals("白鹤童子") && g.getPosition() == position && !g.isDead() && !g.isOnField())) {
                Guardian guardian = campA.stream()
                        .filter(g -> g.getName().equals("白鹤童子") && g.getPosition() == position && !g.isDead() && !g.isOnField())
                        .findFirst().get();
                int[] skillLevel = CardSkillLevelUtil.calculateSkillLevels(guardian.getLevel(), guardian.getStar().doubleValue());
                if (fieldB != null && !fieldB.isDead()) {
                    int silence = guardian.getBuffStacks();
                    int damage = 30 * skillLevel[1] + zhongyuedadiHuan(defender);
                    fieldB.setCurrentHp(fieldB.getCurrentHp() - damage);
                    List<String> deadUnits = new ArrayList<>();

                    if (fieldB.getCurrentHp() <= 0) {
                        fieldB.setDead(true);
                        fieldB.setOnField(false);
                        deadUnits.add(fieldB.getCamp() + fieldB.getName() + "_" + fieldB.getPosition());
                    }
                    addLog("魂力飞弹",
                            guardian.getName(), guardian.getCamp(), guardian.getPosition(),
                            guardian.getMaxHp(), guardian.getCurrentHp(),
                            guardian.getAttack(), guardian.getAttack(),
                            guardian.getSpeed(), guardian.getSpeed(),
                            fieldB.getName(), fieldB.getCamp(), fieldB.getPosition(),
                            fieldB.getMaxHp(), fieldB.getCurrentHp(),
                            fieldB.getAttack(), fieldB.getAttack(),
                            fieldB.getSpeed(), fieldB.getSpeed(),
                            getFieldUnitsStatus(),
                            damage, EffectType.MISSILE_DAMAGE, DamageType.MISSILE,
                            guardian.getName() + "魂力飞弹");
                    // 死亡日志
                    if (!deadUnits.isEmpty()) {
                        Map<String, Object[]> deadStatus = new HashMap<>();
                        deadUnits.forEach(x -> {
                            List<String> strings = Arrays.asList(x.split("_"));
                            deadStatus.put(strings.get(0), new Object[]{Integer.parseInt(strings.get(1)), 0, 0});
                        });

                        addMultiTargetLog("UNIT_DEATH",
                                "SYSTEM", null, 0,
                                0, 0, 0, 0, 0, 0,
                                deadUnits, null,
                                deadStatus,
                                getFieldUnitsStatus(),
                                0, null, null,
                                "飞弹伤害");
                        //触发死亡技能
                        triggerOnDeathSkills(fieldB);

                    } else {
                        //触发受击技能
                        triggerOnAttackedSkills(fieldB, EffectType.MISSILE_DAMAGE);
                    }
                }
            }
//            镇元子，魂力飞弹Lv1场下，每当新单位入场时，对场上敌人造成178点飞弹伤害；禁心咒Lv1场下，每当有单位登场，有17%几率令场上英雄沉默2回合；多宝道人同Lv1与多宝道人在同一队伍时，增加自身453点生命上限，158点攻击，158点速度。
            if (campB.stream().anyMatch(g -> g.getName().equals("白鹤童子") && g.getPosition() == position && !g.isDead() && !g.isOnField())) {
                Guardian guardian = campB.stream()
                        .filter(g -> g.getName().equals("白鹤童子") && g.getPosition() == position && !g.isDead() && !g.isOnField())
                        .findFirst().get();
                int[] skillLevel = CardSkillLevelUtil.calculateSkillLevels(guardian.getLevel(), guardian.getStar().doubleValue());
                if (fieldA != null && !fieldA.isDead()) {
                    int silence = guardian.getBuffStacks();
                    int damage = 30 * skillLevel[1] + zhongyuedadiHuan(defender);
                    fieldA.setCurrentHp(fieldA.getCurrentHp() - damage);
                    List<String> deadUnits = new ArrayList<>();

                    if (fieldA.getCurrentHp() <= 0) {
                        fieldA.setDead(true);
                        fieldA.setOnField(false);
                        deadUnits.add(fieldA.getCamp() + fieldA.getName() + "_" + fieldA.getPosition());
                    }
                    addLog("魂力飞弹",
                            guardian.getName(), guardian.getCamp(), guardian.getPosition(),
                            guardian.getMaxHp(), guardian.getCurrentHp(),
                            guardian.getAttack(), guardian.getAttack(),
                            guardian.getSpeed(), guardian.getSpeed(),
                            fieldA.getName(), fieldA.getCamp(), fieldA.getPosition(),
                            fieldA.getMaxHp(), fieldA.getCurrentHp(),
                            fieldA.getAttack(), fieldA.getAttack(),
                            fieldA.getSpeed(), fieldA.getSpeed(),
                            getFieldUnitsStatus(),
                            damage, EffectType.MISSILE_DAMAGE, DamageType.MISSILE,
                            guardian.getName() + "魂力飞弹");
                    // 死亡日志
                    if (!deadUnits.isEmpty()) {
                        Map<String, Object[]> deadStatus = new HashMap<>();
                        deadUnits.forEach(x -> {
                            List<String> strings = Arrays.asList(x.split("_"));
                            deadStatus.put(strings.get(0), new Object[]{Integer.parseInt(strings.get(1)), 0, 0});
                        });

                        addMultiTargetLog("UNIT_DEATH",
                                "SYSTEM", null, 0,
                                0, 0, 0, 0, 0, 0,
                                deadUnits, null,
                                deadStatus,
                                getFieldUnitsStatus(),
                                0, null, null,
                                "飞弹伤害");
                        //触发死亡技能
                        triggerOnDeathSkills(fieldA);

                    } else {
                        //触发受击技能
                        triggerOnAttackedSkills(fieldA, EffectType.MISSILE_DAMAGE);
                    }
                }
            }


            //            南华真人，魂力飞弹Lv1场下，每当新单位入场时，对场上敌人造成178点飞弹伤害；报复神箭Lv1场下，每回合对场上敌方造成106；
            if (campA.stream().anyMatch(g -> g.getName().equals("金霞童子") && g.getPosition() == position && !g.isDead() && !g.isOnField())) {
                Guardian guardian = campA.stream()
                        .filter(g -> g.getName().equals("金霞童子") && g.getPosition() == position && !g.isDead() && !g.isOnField())
                        .findFirst().get();
                int[] skillLevel = CardSkillLevelUtil.calculateSkillLevels(guardian.getLevel(), guardian.getStar().doubleValue());
                if (fieldB != null && !fieldB.isDead()) {
                    int damage = 35 * skillLevel[1] + zhongyuedadiHuan(defender);
                    fieldB.setCurrentHp(fieldB.getCurrentHp() - damage);
                    List<String> deadUnits = new ArrayList<>();

                    if (fieldB.getCurrentHp() <= 0) {
                        fieldB.setDead(true);
                        fieldB.setOnField(false);
                        deadUnits.add(fieldB.getCamp() + fieldB.getName() + "_" + fieldB.getPosition());
                    }
                    addLog("魂力飞弹",
                            guardian.getName(), guardian.getCamp(), guardian.getPosition(),
                            guardian.getMaxHp(), guardian.getCurrentHp(),
                            guardian.getAttack(), guardian.getAttack(),
                            guardian.getSpeed(), guardian.getSpeed(),
                            fieldB.getName(), fieldB.getCamp(), fieldB.getPosition(),
                            fieldB.getMaxHp(), fieldB.getCurrentHp(),
                            fieldB.getAttack(), fieldB.getAttack(),
                            fieldB.getSpeed(), fieldB.getSpeed(),
                            getFieldUnitsStatus(),
                            damage, EffectType.MISSILE_DAMAGE, DamageType.MISSILE,
                            guardian.getName() + "魂力飞弹");
                    // 死亡日志
                    if (!deadUnits.isEmpty()) {
                        Map<String, Object[]> deadStatus = new HashMap<>();
                        deadUnits.forEach(x -> {
                            List<String> strings = Arrays.asList(x.split("_"));
                            deadStatus.put(strings.get(0), new Object[]{Integer.parseInt(strings.get(1)), 0, 0});
                        });

                        addMultiTargetLog("UNIT_DEATH",
                                "SYSTEM", null, 0,
                                0, 0, 0, 0, 0, 0,
                                deadUnits, null,
                                deadStatus,
                                getFieldUnitsStatus(),
                                0, null, null,
                                "飞弹伤害");
                        //触发死亡技能
                        triggerOnDeathSkills(fieldB);

                    } else {
                        //触发受击技能
                        triggerOnAttackedSkills(fieldB, EffectType.MISSILE_DAMAGE);
                    }
                }
            }
//            镇元子，魂力飞弹Lv1场下，每当新单位入场时，对场上敌人造成178点飞弹伤害；禁心咒Lv1场下，每当有单位登场，有17%几率令场上英雄沉默2回合；多宝道人同Lv1与多宝道人在同一队伍时，增加自身453点生命上限，158点攻击，158点速度。
            if (campB.stream().anyMatch(g -> g.getName().equals("金霞童子") && g.getPosition() == position && !g.isDead() && !g.isOnField())) {
                Guardian guardian = campB.stream()
                        .filter(g -> g.getName().equals("金霞童子") && g.getPosition() == position && !g.isDead() && !g.isOnField())
                        .findFirst().get();
                int[] skillLevel = CardSkillLevelUtil.calculateSkillLevels(guardian.getLevel(), guardian.getStar().doubleValue());
                if (fieldA != null && !fieldA.isDead()) {
                    int damage = 35 * skillLevel[1] + zhongyuedadiHuan(defender);
                    fieldA.setCurrentHp(fieldA.getCurrentHp() - damage);
                    List<String> deadUnits = new ArrayList<>();

                    if (fieldA.getCurrentHp() <= 0) {
                        fieldA.setDead(true);
                        fieldA.setOnField(false);
                        deadUnits.add(fieldA.getCamp() + fieldA.getName() + "_" + fieldA.getPosition());
                    }
                    addLog("魂力飞弹",
                            guardian.getName(), guardian.getCamp(), guardian.getPosition(),
                            guardian.getMaxHp(), guardian.getCurrentHp(),
                            guardian.getAttack(), guardian.getAttack(),
                            guardian.getSpeed(), guardian.getSpeed(),
                            fieldA.getName(), fieldA.getCamp(), fieldA.getPosition(),
                            fieldA.getMaxHp(), fieldA.getCurrentHp(),
                            fieldA.getAttack(), fieldA.getAttack(),
                            fieldA.getSpeed(), fieldA.getSpeed(),
                            getFieldUnitsStatus(),
                            damage, EffectType.MISSILE_DAMAGE, DamageType.MISSILE,
                            guardian.getName() + "魂力飞弹");
                    // 死亡日志
                    if (!deadUnits.isEmpty()) {
                        Map<String, Object[]> deadStatus = new HashMap<>();
                        deadUnits.forEach(x -> {
                            List<String> strings = Arrays.asList(x.split("_"));
                            deadStatus.put(strings.get(0), new Object[]{Integer.parseInt(strings.get(1)), 0, 0});
                        });

                        addMultiTargetLog("UNIT_DEATH",
                                "SYSTEM", null, 0,
                                0, 0, 0, 0, 0, 0,
                                deadUnits, null,
                                deadStatus,
                                getFieldUnitsStatus(),
                                0, null, null,
                                "飞弹伤害");
                        //触发死亡技能
                        triggerOnDeathSkills(fieldA);

                    } else {
                        //触发受击技能
                        triggerOnAttackedSkills(fieldA, EffectType.MISSILE_DAMAGE);
                    }
                }
            }
        }
    }

    private void processOnFieldSkills() {
        //续命技能
        for (int i = 1; i < 6; i++) {
            int position = i;

            // A队妲己
            if (campA.stream().anyMatch(g -> g.getName().equals("妲己") && g.getPosition() == position && !g.isDead() && !g.isOnField())) {
                Guardian daji = campA.stream()
                        .filter(g -> g.getName().equals("妲己") && g.getPosition() == position && !g.isDead() && !g.isOnField())
                        .findFirst().get();
                int[] skillLevel = CardSkillLevelUtil.calculateSkillLevels(daji.getLevel(), daji.getStar().doubleValue());
                int dajiHpBefore = daji.getCurrentHp();
                int dajiAttackBefore = daji.getAttack();
                int dajiSpeedBefore = daji.getSpeed();

                // 妖狐蔽天：3%几率眩晕当前敌人
                if (random.nextDouble() < 0.03 * skillLevel[0] && fieldB != null) {
                    int targetHpBefore = fieldB.getMaxHp();
                    fieldB.getEffects().put(EffectType.STUN, 1);

                    addLog("妖狐蔽天",
                            daji.getName(), Camp.A, daji.getPosition(),
                            dajiHpBefore, daji.getCurrentHp(),
                            dajiAttackBefore, daji.getAttack(),
                            dajiSpeedBefore, daji.getSpeed(),
                            fieldB.getName(), fieldB.getCamp(), fieldB.getPosition(),
                            targetHpBefore, fieldB.getCurrentHp(),
                            fieldB.getAttack(), fieldB.getAttack(),
                            fieldB.getSpeed(), fieldB.getSpeed(),
                            getFieldUnitsStatus(),
                            0, EffectType.STUN, null,
                            "妲己触发妖狐蔽天，眩晕敌人");
                }

                // 谄媚噬魂：随机敌方中毒
                List<Guardian> enemies = campB.stream()
                        .filter(g -> !g.isDead())
                        .collect(Collectors.toList());

                if (!enemies.isEmpty() && skillLevel[1] > 0) {
                    Guardian randomEnemy = enemies.get(random.nextInt(enemies.size()));
                    int targetHpBefore = randomEnemy.getMaxHp();
                    int poisonValue = 7 * skillLevel[1];
                    int poisonDamage = randomEnemy.getEffects().getOrDefault(EffectType.POISON, 0);
                    randomEnemy.getEffects().put(EffectType.POISON, poisonValue + poisonDamage);

                    addLog("谄媚噬魂",
                            daji.getName(), Camp.A, daji.getPosition(),
                            dajiHpBefore, daji.getCurrentHp(),
                            dajiAttackBefore, daji.getAttack(),
                            dajiSpeedBefore, daji.getSpeed(),
                            randomEnemy.getName(), randomEnemy.getCamp(), randomEnemy.getPosition(),
                            targetHpBefore, randomEnemy.getCurrentHp(),
                            randomEnemy.getAttack(), randomEnemy.getAttack(),
                            randomEnemy.getSpeed(), randomEnemy.getSpeed(),
                            getFieldUnitsStatus(),
                            poisonValue, EffectType.POISON, DamageType.POISON,
                            "妲己触发谄媚噬魂，使敌人中毒");
                }
            }

            // B队妲己
            if (campB.stream().anyMatch(g -> g.getName().equals("妲己") && g.getPosition() == position && !g.isDead() && !g.isOnField())) {
                Guardian daji = campB.stream()
                        .filter(g -> g.getName().equals("妲己") && g.getPosition() == position && !g.isDead() && !g.isOnField())
                        .findFirst().get();
                int[] skillLevel = CardSkillLevelUtil.calculateSkillLevels(daji.getLevel(), daji.getStar().doubleValue());
                int dajiHpBefore = daji.getCurrentHp();
                int dajiAttackBefore = daji.getAttack();
                int dajiSpeedBefore = daji.getSpeed();

                // 妖狐蔽天：3%几率眩晕当前敌人
                if (random.nextDouble() < 0.03 * skillLevel[0] && fieldA != null) {
                    int targetHpBefore = fieldA.getMaxHp();
                    fieldA.getEffects().put(EffectType.STUN, 1);

                    addLog("妖狐蔽天",
                            daji.getName(), Camp.B, daji.getPosition(),
                            dajiHpBefore, daji.getCurrentHp(),
                            dajiAttackBefore, daji.getAttack(),
                            dajiSpeedBefore, daji.getSpeed(),
                            fieldA.getName(), fieldA.getCamp(), fieldA.getPosition(),
                            targetHpBefore, fieldA.getCurrentHp(),
                            fieldA.getAttack(), fieldA.getAttack(),
                            fieldA.getSpeed(), fieldA.getSpeed(),
                            getFieldUnitsStatus(),
                            0, EffectType.STUN, null,
                            "妲己触发妖狐蔽天，眩晕敌人");
                }

                // 谄媚噬魂：随机敌方中毒
                List<Guardian> enemies = campA.stream()
                        .filter(g -> !g.isDead())
                        .collect(Collectors.toList());

                if (!enemies.isEmpty() && skillLevel[1] > 0) {
                    Guardian randomEnemy = enemies.get(random.nextInt(enemies.size()));
                    int targetHpBefore = randomEnemy.getMaxHp();
                    int poisonValue = 7 * skillLevel[1];
                    int poisonDamage = randomEnemy.getEffects().getOrDefault(EffectType.POISON, 0);
                    randomEnemy.getEffects().put(EffectType.POISON, poisonValue + poisonDamage);

                    addLog("谄媚噬魂",
                            daji.getName(), Camp.B, daji.getPosition(),
                            dajiHpBefore, daji.getCurrentHp(),
                            dajiAttackBefore, daji.getAttack(),
                            dajiSpeedBefore, daji.getSpeed(),
                            randomEnemy.getName(), randomEnemy.getCamp(), randomEnemy.getPosition(),
                            targetHpBefore, randomEnemy.getCurrentHp(),
                            randomEnemy.getAttack(), randomEnemy.getAttack(),
                            randomEnemy.getSpeed(), randomEnemy.getSpeed(),
                            getFieldUnitsStatus(),
                            poisonValue, EffectType.POISON, DamageType.POISON,
                            "妲己触发谄媚噬魂，使敌人中毒");
                }
            }


            // A队妲己
            if (campA.stream().anyMatch(g -> g.getName().equals("白晶晶") && g.getPosition() == position && !g.isDead() && !g.isOnField())) {
                Guardian daji = campA.stream()
                        .filter(g -> g.getName().equals("白晶晶") && g.getPosition() == position && !g.isDead() && !g.isOnField())
                        .findFirst().get();
                int[] skillLevel = CardSkillLevelUtil.calculateSkillLevels(daji.getLevel(), daji.getStar().doubleValue());
                int dajiHpBefore = daji.getCurrentHp();
                int dajiAttackBefore = daji.getAttack();
                int dajiSpeedBefore = daji.getSpeed();

                // 谄媚噬魂：随机敌方中毒
                List<Guardian> enemies = campB.stream()
                        .filter(g -> !g.isDead())
                        .collect(Collectors.toList());

                if (!enemies.isEmpty()) {
                    Guardian randomEnemy = enemies.get(random.nextInt(enemies.size()));
                    int targetHpBefore = randomEnemy.getMaxHp();
                    int poisonValue = 7 * skillLevel[0];
                    int poisonDamage = randomEnemy.getEffects().getOrDefault(EffectType.POISON, 0);
                    randomEnemy.getEffects().put(EffectType.POISON, poisonValue + poisonDamage);

                    addLog("谄媚噬魂",
                            daji.getName(), Camp.A, daji.getPosition(),
                            dajiHpBefore, daji.getCurrentHp(),
                            dajiAttackBefore, daji.getAttack(),
                            dajiSpeedBefore, daji.getSpeed(),
                            randomEnemy.getName(), randomEnemy.getCamp(), randomEnemy.getPosition(),
                            targetHpBefore, randomEnemy.getCurrentHp(),
                            randomEnemy.getAttack(), randomEnemy.getAttack(),
                            randomEnemy.getSpeed(), randomEnemy.getSpeed(),
                            getFieldUnitsStatus(),
                            poisonValue, EffectType.POISON, DamageType.POISON,
                            "白晶晶触发谄媚噬魂，使敌人中毒");
                }
            }

            // B队妲己
            if (campB.stream().anyMatch(g -> g.getName().equals("白晶晶") && g.getPosition() == position && !g.isDead() && !g.isOnField())) {
                Guardian daji = campB.stream()
                        .filter(g -> g.getName().equals("白晶晶") && g.getPosition() == position && !g.isDead() && !g.isOnField())
                        .findFirst().get();
                int[] skillLevel = CardSkillLevelUtil.calculateSkillLevels(daji.getLevel(), daji.getStar().doubleValue());
                int dajiHpBefore = daji.getCurrentHp();
                int dajiAttackBefore = daji.getAttack();
                int dajiSpeedBefore = daji.getSpeed();

                // 谄媚噬魂：随机敌方中毒
                List<Guardian> enemies = campA.stream()
                        .filter(g -> !g.isDead())
                        .collect(Collectors.toList());

                if (!enemies.isEmpty()) {
                    Guardian randomEnemy = enemies.get(random.nextInt(enemies.size()));
                    int targetHpBefore = randomEnemy.getMaxHp();
                    int poisonValue = 7 * skillLevel[0];
                    int poisonDamage = randomEnemy.getEffects().getOrDefault(EffectType.POISON, 0);
                    randomEnemy.getEffects().put(EffectType.POISON, poisonValue + poisonDamage);

                    addLog("谄媚噬魂",
                            daji.getName(), Camp.B, daji.getPosition(),
                            dajiHpBefore, daji.getCurrentHp(),
                            dajiAttackBefore, daji.getAttack(),
                            dajiSpeedBefore, daji.getSpeed(),
                            randomEnemy.getName(), randomEnemy.getCamp(), randomEnemy.getPosition(),
                            targetHpBefore, randomEnemy.getCurrentHp(),
                            randomEnemy.getAttack(), randomEnemy.getAttack(),
                            randomEnemy.getSpeed(), randomEnemy.getSpeed(),
                            getFieldUnitsStatus(),
                            poisonValue, EffectType.POISON, DamageType.POISON,
                            "白晶晶触发谄媚噬魂，使敌人中毒");
                }
            }
        }


    }

    private void processOnFieldSkills2() {
        //续命技能
        String[] xuminHero = {"小龙女", "洛神", "瑶姬", "中岳大帝", "陆压道君", "多宝道人", "河伯", "赤精子", "广成子", "宫女", "玉兔"};
        List<String> xuminHeroList = Arrays.asList(xuminHero);
        for (int i = 1; i < 6; i++) {
            int position = i;
            if (campA.stream().anyMatch(g -> xuminHeroList.contains(g.getName()) && g.getPosition() == position && !g.isDead() && !g.isOnField())) {
                Guardian guardian = campA.stream()
                        .filter(g -> xuminHeroList.contains(g.getName()) && g.getPosition() == position && !g.isDead() && !g.isOnField())
                        .findFirst().get();
                int[] skillLevel = CardSkillLevelUtil.calculateSkillLevels(guardian.getLevel(), guardian.getStar().doubleValue());
                //疾病效果或加成效果
                int healDow = fieldA.getEffects().getOrDefault(EffectType.HEAL_DOWN, 0);
                int healBoost = fieldA.getEffects().getOrDefault(EffectType.HEAL_BOOST, 0);
                if (!fieldA.isDead() && fieldA.getRace() == Race.IMMORTAL) {
                    int hel = 20;
                    if ("小龙女".equals(guardian.getName())) {
                        hel = 39 * skillLevel[0];
                    } else if ("洛神".equals(guardian.getName())) {
                        hel = 104 * skillLevel[0];
                    } else if ("瑶姬".equals(guardian.getName())) {
                        hel = 160 * skillLevel[0];
                    } else if ("中岳大帝".equals(guardian.getName())) {
                        hel = 125 * skillLevel[0];
                    } else if ("陆压道君".equals(guardian.getName())) {
                        hel = 100 * skillLevel[0];
                    } else if ("多宝道人".equals(guardian.getName())) {
                        hel = 125 * skillLevel[0];
                    } else if ("河伯".equals(guardian.getName())) {
                        hel = 39 * skillLevel[0];
                    } else if ("赤精子".equals(guardian.getName())) {
                        hel = 100 * skillLevel[0];
                    } else if ("广成子".equals(guardian.getName())) {
                        hel = 100 * skillLevel[0];
                    } else if ("宫女".equals(guardian.getName())) {
                        hel = 60 * skillLevel[0];
                    } else if ("玉兔".equals(guardian.getName())) {
                        hel = 20 * skillLevel[0];
                    }
                    guardian.setCurrentHp(guardian.getCurrentHp() - hel);
                    hel = hel * (1 + (healBoost - healDow) / 100);
                    if (hel < 0) {
                        hel = 0;
                    }
                    fieldA.setCurrentHp(fieldA.getCurrentHp() + hel);
                    List<String> deadUnits = new ArrayList<>();

                    if (guardian.getCurrentHp() <= 0) {
                        guardian.setDead(true);
                        guardian.setOnField(false);
                        deadUnits.add(guardian.getCamp() + guardian.getName() + "_" + guardian.getPosition());
                    }
                    addLog("续命",
                            guardian.getName(), guardian.getCamp(), guardian.getPosition(),
                            guardian.getMaxHp(), guardian.getCurrentHp(),
                            guardian.getAttack(), guardian.getAttack(),
                            guardian.getSpeed(), guardian.getSpeed(),
                            fieldA.getName(), fieldA.getCamp(), fieldA.getPosition(),
                            fieldA.getMaxHp(), fieldA.getCurrentHp(),
                            fieldA.getAttack(), fieldA.getAttack(),
                            fieldA.getSpeed(), fieldA.getSpeed(),
                            getFieldUnitsStatus(),
                            hel, EffectType.XU_HEAL, null,
                            fieldA.getName() + "触发续命，生命+" + hel);
                    // 死亡日志
                    if (!deadUnits.isEmpty()) {
                        Map<String, Object[]> deadStatus = new HashMap<>();
                        deadUnits.forEach(x -> {
                            List<String> strings = Arrays.asList(x.split("_"));
                            deadStatus.put(strings.get(0), new Object[]{Integer.parseInt(strings.get(1)), 0, 0});
                        });

                        addMultiTargetLog("UNIT_DEATH",
                                "SYSTEM", null, 0,
                                0, 0, 0, 0, 0, 0,
                                deadUnits, null,
                                deadStatus,
                                getFieldUnitsStatus(),
                                0, null, null,
                                "续命生命值为0");
                        //触发死亡技能
                        triggerOnDeathSkills(guardian);

                    }
                    triggerOnHelSkills(fieldA);
                }

            }

            if (campB.stream().anyMatch(g -> xuminHeroList.contains(g.getName()) && g.getPosition() == position && !g.isDead() && !g.isOnField())) {
                Guardian guardian = campB.stream()
                        .filter(g -> xuminHeroList.contains(g.getName()) && g.getPosition() == position && !g.isDead() && !g.isOnField())
                        .findFirst().get();
                int[] skillLevel = CardSkillLevelUtil.calculateSkillLevels(guardian.getLevel(), guardian.getStar().doubleValue());
                if (!fieldB.isDead() && fieldB.getRace() == Race.IMMORTAL) {
                    //疾病效果或加成效果
                    int healDow = fieldA.getEffects().getOrDefault(EffectType.HEAL_DOWN, 0);
                    int healBoost = fieldA.getEffects().getOrDefault(EffectType.HEAL_BOOST, 0);
                    int hel = 20;
                    if ("小龙女".equals(guardian.getName())) {
                        hel = 39 * skillLevel[0];
                    } else if ("洛神".equals(guardian.getName())) {
                        hel = 104 * skillLevel[0];
                    } else if ("瑶姬".equals(guardian.getName())) {
                        hel = 160 * skillLevel[0];
                    } else if ("中岳大帝".equals(guardian.getName())) {
                        hel = 125 * skillLevel[0];
                    } else if ("陆压道君".equals(guardian.getName())) {
                        hel = 100 * skillLevel[0];
                    } else if ("多宝道人".equals(guardian.getName())) {
                        hel = 125 * skillLevel[0];
                    } else if ("河伯".equals(guardian.getName())) {
                        hel = 39 * skillLevel[0];
                    } else if ("赤精子".equals(guardian.getName())) {
                        hel = 100 * skillLevel[0];
                    } else if ("广成子".equals(guardian.getName())) {
                        hel = 100 * skillLevel[0];
                    } else if ("宫女".equals(guardian.getName())) {
                        hel = 60 * skillLevel[0];
                    } else if ("玉兔".equals(guardian.getName())) {
                        hel = 20 * skillLevel[0];
                    }
                    guardian.setCurrentHp(guardian.getCurrentHp() - hel);
                    hel = hel * (1 + (healBoost - healDow) / 100);
                    if (hel < 0) {
                        hel = 0;
                    }
                    fieldB.setCurrentHp(fieldB.getCurrentHp() + hel);
                    List<String> deadUnits = new ArrayList<>();

                    if (guardian.getCurrentHp() <= 0) {
                        guardian.setDead(true);
                        guardian.setOnField(false);
                        deadUnits.add(guardian.getCamp() + guardian.getName() + "_" + guardian.getPosition());
                    }
                    addLog("续命",
                            guardian.getName(), guardian.getCamp(), guardian.getPosition(),
                            guardian.getMaxHp(), guardian.getCurrentHp(),
                            guardian.getAttack(), guardian.getAttack(),
                            guardian.getSpeed(), guardian.getSpeed(),
                            fieldB.getName(), fieldB.getCamp(), fieldB.getPosition(),
                            fieldB.getMaxHp(), fieldB.getCurrentHp(),
                            fieldB.getAttack(), fieldB.getAttack(),
                            fieldB.getSpeed(), fieldB.getSpeed(),
                            getFieldUnitsStatus(),
                            hel, EffectType.XU_HEAL, null,
                            fieldB.getName() + "触发续命，生命+" + hel);
                    // 死亡日志
                    if (!deadUnits.isEmpty()) {
                        Map<String, Object[]> deadStatus = new HashMap<>();
                        deadUnits.forEach(x -> {
                            List<String> strings = Arrays.asList(x.split("_"));
                            deadStatus.put(strings.get(0), new Object[]{Integer.parseInt(strings.get(1)), 0, 0});
                        });

                        addMultiTargetLog("UNIT_DEATH",
                                "SYSTEM", null, 0,
                                0, 0, 0, 0, 0, 0,
                                deadUnits, null,
                                deadStatus,
                                getFieldUnitsStatus(),
                                0, null, null,
                                "续命生命值为0");
                        //触发死亡技能
                        triggerOnDeathSkills(guardian);

                    }
                    triggerOnHelSkills(fieldB);
                }

            }

        }


    }

    // 处理回合结束效果
    private void processRoundEndEffects() {
        // 托塔天王仙塔庇护
        if (fieldA != null && fieldA.getName().equals("托塔天王")) {
            int hpBefore = fieldA.getCurrentHp();
            int[] skillLevel = CardSkillLevelUtil.calculateSkillLevels(fieldA.getLevel(), fieldA.getStar().doubleValue());
            if (skillLevel[1] > 0) {
                int heal = 25 * skillLevel[1];
                fieldA.setCurrentHp(fieldA.getCurrentHp() + heal);

                addLog("仙塔庇护",
                        fieldA.getName(), fieldA.getCamp(), fieldA.getPosition(),
                        hpBefore, fieldA.getCurrentHp(),
                        fieldA.getAttack(), fieldA.getAttack(),
                        fieldA.getSpeed(), fieldA.getSpeed(),
                        fieldA.getName(), fieldA.getCamp(), fieldA.getPosition(),
                        hpBefore, fieldA.getCurrentHp(),
                        fieldA.getAttack(), fieldA.getAttack(),
                        fieldA.getSpeed(), fieldA.getSpeed(),
                        getFieldUnitsStatus(),
                        heal, EffectType.HEAL, null,
                        fieldA.getName() + "触发仙塔庇护，恢复生命值");
            }

        }

        if (fieldB != null && fieldB.getName().equals("托塔天王")) {
            int hpBefore = fieldB.getCurrentHp();
            int[] skillLevel = CardSkillLevelUtil.calculateSkillLevels(fieldB.getLevel(), fieldB.getStar().doubleValue());
            if (skillLevel[1] > 0) {
                int heal = 25 * skillLevel[1];
                fieldB.setCurrentHp(fieldB.getCurrentHp() + heal);

                addLog("仙塔庇护",
                        fieldB.getName(), fieldB.getCamp(), fieldB.getPosition(),
                        hpBefore, fieldB.getCurrentHp(),
                        fieldB.getAttack(), fieldB.getAttack(),
                        fieldB.getSpeed(), fieldB.getSpeed(),
                        fieldB.getName(), fieldB.getCamp(), fieldB.getPosition(),
                        hpBefore, fieldB.getCurrentHp(),
                        fieldB.getAttack(), fieldB.getAttack(),
                        fieldB.getSpeed(), fieldB.getSpeed(),
                        getFieldUnitsStatus(),
                        heal, EffectType.HEAL, null,
                        fieldB.getName() + "触发仙塔庇护，恢复生命值");
            }

        }
        //批量处理各种角色buff回合数
//        fieldB.getEffects().put(EffectType.STUN, 1);
//        List<Guardian> allUnits = new ArrayList<>();
//        allUnits.addAll(campA);
//        allUnits.addAll(campB);
//
//        List<Guardian> aliveUnits = allUnits.stream()
//                .filter(g -> !g.isDead())
//                .collect(Collectors.toList());
//        for (Guardian aliveUnit : aliveUnits) {
//            //处理眩晕buff
//            aliveUnit
//        }
//        List<Guardian> poisonedUnits = new ArrayList<>();
//        campA.forEach(g -> {
//            if (g.getEffects().containsKey(EffectType.POISON) && !g.isDead()) {
//                poisonedUnits.add(g);
//            }
//        });
//        campB.forEach(g -> {
//            if (g.getEffects().containsKey(EffectType.POISON) && !g.isDead()) {
//                poisonedUnits.add(g);
//            }
//        });
//
//        if (!poisonedUnits.isEmpty()) {
//            List<String> targetNames = new ArrayList<>();
//            Map<String, Object[]> targetStatus = new HashMap<>();
//            List<String> deadUnits = new ArrayList<>();
//
//            poisonedUnits.forEach(g -> {
//                targetNames.add(g.getName());
//                int hpBefore = g.getMaxHp();
//                int poisonDamage = g.getEffects().get(EffectType.POISON);
//                g.setCurrentHp(g.getCurrentHp() - poisonDamage);
//                targetStatus.put(g.getCamp() + g.getName(), new Object[]{g.getPosition(), hpBefore, g.getCurrentHp()});
//
//                if (g.getCurrentHp() <= 0) {
//                    g.setDead(true);
//                    g.setOnField(false);
//                    deadUnits.add(g.getCamp() + g.getName() + "_" + g.getPosition());
//                }
//            });
//
//            // 中毒伤害日志（批量）
//            addMultiTargetLog("POISON",
//                    "SYSTEM", null, 0,
//                    0, 0, 0, 0, 0, 0,
//                    targetNames, null,
//                    targetStatus,
//                    getFieldUnitsStatus(),
//                    0, EffectType.POISON, DamageType.POISON,
//                    "中毒效果触发");
//
//            // 中毒阵亡日志
//            if (!deadUnits.isEmpty()) {
//                Map<String, Object[]> deadStatus = new HashMap<>();
//                deadUnits.forEach(x -> {
//                    List<String> strings = Arrays.asList(x.split("_"));
//                    deadStatus.put(strings.get(0), new Object[]{Integer.parseInt(strings.get(1)), 0, 0});
//                });
//
//                addMultiTargetLog("UNIT_DEATH",
//                        "SYSTEM", null, 0,
//                        0, 0, 0, 0, 0, 0,
//                        deadUnits, null,
//                        deadStatus,
//                        getFieldUnitsStatus(),
//                        0, null, null,
//                        "单位因中毒阵亡");
//            }
//        }
    }


    // 检查并替换阵亡护法
    private void checkAndReplaceGuardians() {
        if (fieldA != null && fieldA.isDead()) {
            Guardian newA = getNextGuardian(campA);
            if (newA != null) {
                fieldA = newA;
                fieldA.setOnField(true);

                addLog("UNIT_ENTER",
                        fieldA.getName(), fieldA.getCamp(), fieldA.getPosition(),
                        fieldA.getCurrentHp(), fieldA.getCurrentHp(),
                        fieldA.getAttack(), fieldA.getAttack(),
                        fieldA.getSpeed(), fieldA.getSpeed(),
                        fieldA.getName(), fieldA.getCamp(), fieldA.getPosition(),
                        fieldA.getCurrentHp(), fieldA.getCurrentHp(),
                        fieldA.getAttack(), fieldA.getAttack(),
                        fieldA.getSpeed(), fieldA.getSpeed(),
                        getFieldUnitsStatus(),
                        0, null, null,
                        fieldA.getName() + "登场");

                triggerOnEnterSkills(fieldA);
            }
        }

        if (fieldB != null && fieldB.isDead()) {
            Guardian newB = getNextGuardian(campB);
            if (newB != null) {
                fieldB = newB;
                fieldB.setOnField(true);

                addLog("UNIT_ENTER",
                        fieldB.getName(), fieldB.getCamp(), fieldB.getPosition(),
                        fieldB.getCurrentHp(), fieldB.getCurrentHp(),
                        fieldB.getAttack(), fieldB.getAttack(),
                        fieldB.getSpeed(), fieldB.getSpeed(),
                        fieldB.getName(), fieldB.getCamp(), fieldB.getPosition(),
                        fieldB.getCurrentHp(), fieldB.getCurrentHp(),
                        fieldB.getAttack(), fieldB.getAttack(),
                        fieldB.getSpeed(), fieldB.getSpeed(),
                        getFieldUnitsStatus(),
                        0, null, null,
                        fieldB.getName() + "登场");

                triggerOnEnterSkills(fieldB);
            }
        }
    }

    private boolean duoBaoGuanHuan() {
        //血量上限不改变光环
        boolean campAHasAlive = campA.stream().anyMatch(g -> g.getName().equals("多宝道人") && !g.isDead());
        boolean campBHasAlive = campB.stream().anyMatch(g -> g.getName().equals("多宝道人") && !g.isDead());
        return !campAHasAlive && !campBHasAlive;
    }

    private int jiaomowang(Guardian guardian, int fireDamage) {
        //血量上限不改变光环
        List<Guardian> jiaomowang = guardian.getCamp() == Camp.A ?
                campA.stream().filter(g -> g.getName().equals("蛟魔王") && !g.isDead()).collect(Collectors.toList()) :
                campB.stream().filter(g -> g.getName().equals("蛟魔王") && !g.isDead()).collect(Collectors.toList());
        if (Xtool.isNotNull(jiaomowang)) return (int) (fireDamage * 0.5);
        return fireDamage;
    }

    private Integer zhongyuedadiHuan(Guardian guardian) {
        //飞弹伤害增加光环
        List<Guardian> zhongyues = guardian.getCamp() == Camp.A ?
                campA.stream().filter(g -> g.getName().equals("中岳大帝") && !g.isDead()).collect(Collectors.toList()) :
                campB.stream().filter(g -> g.getName().equals("中岳大帝") && !g.isDead()).collect(Collectors.toList());
        Integer damage = 0;
        if (Xtool.isNotNull(zhongyues)) {
            Guardian zhongyue = zhongyues.get(0);
            int[] skillLevel = CardSkillLevelUtil.calculateSkillLevels(zhongyue.getLevel(), zhongyue.getStar().doubleValue());
            if (skillLevel[1] > 0) {
                //            五岳灵脉Lv1光环-增加我方全体飞弹伤害20点；
                damage = skillLevel[1] * 20;
            }

        }
        return damage;
    }

    // 判断战斗是否结束
    private boolean isBattleEnd() {
        boolean campAHasAlive = campA.stream().anyMatch(g -> !g.isDead());
        boolean campBHasAlive = campB.stream().anyMatch(g -> !g.isDead());
        return !campAHasAlive || !campBHasAlive;
    }

    // 结束战斗判定胜负
    private void endBattle() {
        int totalHpA = campA.stream().filter(g -> !g.isDead()).mapToInt(Guardian::getCurrentHp).sum();
        int totalHpB = campB.stream().filter(g -> !g.isDead()).mapToInt(Guardian::getCurrentHp).sum();

        String result;
        if (totalHpA > totalHpB) {
            result = "A队胜利";
        } else if (totalHpB > totalHpA) {
            result = "B队胜利";
        } else {
            result = "平局";
        }

        addLog("BATTLE_END",
                "SYSTEM", null, 0,
                0, 0, 0, 0, 0, 0,
                null, null, 0,
                0, 0, 0, 0, 0, 0,
                getFieldUnitsStatus(),
                0, null, null,
                "战斗结束：" + result +
                        " A队剩余总血量：" + totalHpA +
                        " B队剩余总血量：" + totalHpB);
    }

    // 添加单目标日志（包含位置）
    private void addLog(String eventType,
                        String sourceUnit, Camp sourceCamp, int sourcePosition,
                        int sourceHpBefore, int sourceHpAfter,
                        int sourceAttackBefore, int sourceAttackAfter,
                        int sourceSpeedBefore, int sourceSpeedAfter,
                        String targetUnit, Camp targetCamp, int targetPosition,
                        int targetHpBefore, int targetHpAfter,
                        int targetAttackBefore, int targetAttackAfter,
                        int targetSpeedBefore, int targetSpeedAfter,
                        String fieldUnitsStatus,
                        int value, EffectType effectType, DamageType damageType, String extraDesc) {
        battleLogs.add(new BattleLog(
                battleId,
                currentRound,
                eventType,
                sourceUnit,
                sourceCamp,
                sourcePosition,
                sourceHpBefore,
                sourceHpAfter,
                sourceAttackBefore,
                sourceAttackAfter,
                sourceSpeedBefore,
                sourceSpeedAfter,
                targetUnit,
                null,
                targetCamp,
                targetPosition,
                targetHpBefore,
                targetHpAfter,
                targetAttackBefore,
                targetAttackAfter,
                targetSpeedBefore,
                targetSpeedAfter,
                fieldUnitsStatus,
                value,
                effectType,
                damageType,
                extraDesc,
                "0"
        ));
    }

    // 添加多目标日志（包含位置）
    private void addMultiTargetLog(String eventType,
                                   String sourceUnit, Camp sourceCamp, int sourcePosition,
                                   int sourceHpBefore, int sourceHpAfter,
                                   int sourceAttackBefore, int sourceAttackAfter,
                                   int sourceSpeedBefore, int sourceSpeedAfter,
                                   List<String> targetUnitList, Camp targetCamp,
                                   Map<String, Object[]> targetStatus,
                                   String fieldUnitsStatus,
                                   int value, EffectType effectType, DamageType damageType, String extraDesc) {
        // 构建目标状态描述（包含位置）
        StringBuilder targetDesc = new StringBuilder();
        targetStatus.forEach((unit, status) -> {
            if (status.length >= 3) {
                targetDesc.append(String.format("%s[%d号位:HP:%d→%d],",
                        unit, status[0], status[1], status[2]));
            }
        });

        if (targetDesc.length() > 0) {
            targetDesc.setLength(targetDesc.length() - 1);
        }

        battleLogs.add(new BattleLog(
                battleId,
                currentRound,
                eventType,
                sourceUnit,
                sourceCamp,
                sourcePosition,
                sourceHpBefore,
                sourceHpAfter,
                sourceAttackBefore,
                sourceAttackAfter,
                sourceSpeedBefore,
                sourceSpeedAfter,
                null,
                targetUnitList,
                targetCamp,
                0,
                0,
                0,
                0,
                0,
                0,
                0,
                fieldUnitsStatus,
                value,
                effectType,
                damageType,
                extraDesc + " - " + targetDesc.toString(),
                "1"
        ));
    }

    // 获取战斗日志
    public List<BattleLog> getBattleLogs() {
        return battleLogs;
    }

    // 根据战斗ID查询日志
    public static List<BattleLog> getBattleLogsById(String battleId, Map<String, BattleManager> battleCache) {
        return battleCache.getOrDefault(battleId, new BattleManager(battleId, new ArrayList<>(), new ArrayList<>()))
                .getBattleLogs();
    }
}