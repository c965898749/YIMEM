package com.sy.model.game;

import java.util.*;
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

    // 获取在场单位状态描述
    private String getFieldUnitsStatus() {
        StringBuilder sb = new StringBuilder();
        if (fieldA != null) {
            sb.append(String.format("A[%s:HP=%d/%d,ATK=%d,SPEED=%d] ",
                    fieldA.getName(), fieldA.getCurrentHp(), fieldA.getMaxHp(),
                    fieldA.getAttack(), fieldA.getSpeed()));
        }
        if (fieldB != null) {
            sb.append(String.format("B[%s:HP=%d/%d,ATK=%d,SPEED=%d] ",
                    fieldB.getName(), fieldB.getCurrentHp(), fieldB.getMaxHp(),
                    fieldB.getAttack(), fieldB.getSpeed()));
        }
        return sb.toString().trim();
    }

    // 开始战斗
    public void startBattle() {
        while (currentRound < 100 && !isBattleEnd()) {
            currentRound++;
            addLog("ROUND_START", "SYSTEM", null,
                    0, 0, 0, 0, 0, 0,
                    null, null,
                    0, 0, 0, 0, 0, 0,
                    getFieldUnitsStatus(),
                    0, null, null, "回合开始");

            // 回合开始处理
            processRoundStartEffects();

            // 场上战斗
            if (fieldA != null && fieldB != null) {
                processAttack(fieldA, fieldB);
                if (!fieldB.isDead()) {
                    processAttack(fieldB, fieldA);
                }
            }

            // 检查阵亡替换
            checkAndReplaceGuardians();

            // 回合结束处理
            processRoundEndEffects();
        }

        // 战斗结束判定
        endBattle();
    }

    // 处理攻击流程
    private void processAttack(Guardian attacker, Guardian defender) {
        // 记录攻击前状态
        int attackerHpBefore = attacker.getCurrentHp();
        int attackerAttackBefore = attacker.getAttack();
        int attackerSpeedBefore = attacker.getSpeed();

        int defenderHpBefore = defender.getCurrentHp();
        int defenderAttackBefore = defender.getAttack();
        int defenderSpeedBefore = defender.getSpeed();

        // 攻击前技能触发
        triggerPreAttackSkills(attacker, defender);

        if (defender.isDead()) return;

        // 普通攻击
        int damage = attacker.getAttack();
        defender.setCurrentHp(defender.getCurrentHp() - damage);

        addLog("NORMAL_ATTACK",
                attacker.getName(), attacker.getCamp(),
                attackerHpBefore, attacker.getCurrentHp(),
                attackerAttackBefore, attacker.getAttack(),
                attackerSpeedBefore, attacker.getSpeed(),
                defender.getName(), defender.getCamp(),
                defenderHpBefore, defender.getCurrentHp(),
                defenderAttackBefore, defender.getAttack(),
                defenderSpeedBefore, defender.getSpeed(),
                getFieldUnitsStatus(),
                damage, EffectType.DAMAGE, DamageType.PHYSICAL,
                attacker.getName() + "对" + defender.getName() + "造成物理伤害");

        // 触发受击技能
        triggerOnAttackedSkills(defender, attacker);

        // 检查阵亡
        if (defender.getCurrentHp() <= 0) {
            defender.setDead(true);
            defender.setOnField(false);

            addLog("UNIT_DEATH",
                    defender.getName(), defender.getCamp(),
                    defender.getCurrentHp(), 0,
                    defender.getAttack(), defender.getAttack(),
                    defender.getSpeed(), defender.getSpeed(),
                    defender.getName(), defender.getCamp(),
                    defender.getCurrentHp(), 0,
                    defender.getAttack(), defender.getAttack(),
                    defender.getSpeed(), defender.getSpeed(),
                    getFieldUnitsStatus(),
                    0, null, null, defender.getName() + "阵亡");

            // 触发死亡相关技能
            triggerOnDeathSkills(defender);
        }

        // 攻击后技能触发
        triggerPostAttackSkills(attacker, defender);
    }

    // 触发登场技能
    private void triggerOnEnterSkills(Guardian guardian) {
        int sourceHpBefore = guardian.getCurrentHp();
        int sourceAttackBefore = guardian.getAttack();
        int sourceSpeedBefore = guardian.getSpeed();

        switch (guardian.getName()) {
            case "托塔天王":
                // 镇妖塔：对敌方场上造成飞弹伤害
                Guardian enemy = guardian.getCamp() == Camp.A ? fieldB : fieldA;
                if (enemy != null) {
                    int enemyHpBefore = enemy.getCurrentHp();
                    int damage = 69 * guardian.getLevel();
                    enemy.setCurrentHp(enemy.getCurrentHp() - damage);

                    addLog("镇妖塔",
                            guardian.getName(), guardian.getCamp(),
                            sourceHpBefore, guardian.getCurrentHp(),
                            sourceAttackBefore, guardian.getAttack(),
                            sourceSpeedBefore, guardian.getSpeed(),
                            enemy.getName(), enemy.getCamp(),
                            enemyHpBefore, enemy.getCurrentHp(),
                            enemy.getAttack(), enemy.getAttack(),
                            enemy.getSpeed(), enemy.getSpeed(),
                            getFieldUnitsStatus(),
                            damage, EffectType.DAMAGE, DamageType.MISSILE,
                            guardian.getName() + "登场触发镇妖塔");
                }
                break;

            case "齐天大圣":
                // 大圣降临：回复自身20%生命
                int heal = (int) (guardian.getMaxHp() * 0.2 * guardian.getLevel());
                guardian.setCurrentHp(guardian.getCurrentHp() + heal);

                addLog("大圣降临",
                        guardian.getName(), guardian.getCamp(),
                        sourceHpBefore, guardian.getCurrentHp(),
                        sourceAttackBefore, guardian.getAttack(),
                        sourceSpeedBefore, guardian.getSpeed(),
                        guardian.getName(), guardian.getCamp(),
                        sourceHpBefore, guardian.getCurrentHp(),
                        sourceAttackBefore, guardian.getAttack(),
                        sourceSpeedBefore, guardian.getSpeed(),
                        getFieldUnitsStatus(),
                        heal, EffectType.HEAL, null,
                        guardian.getName() + "登场触发大圣降临");
                break;

            case "厚土娘娘":
                // 大地净化：驱散自身减益
                guardian.getEffects().clear();

                addLog("大地净化",
                        guardian.getName(), guardian.getCamp(),
                        sourceHpBefore, guardian.getCurrentHp(),
                        sourceAttackBefore, guardian.getAttack(),
                        sourceSpeedBefore, guardian.getSpeed(),
                        guardian.getName(), guardian.getCamp(),
                        sourceHpBefore, guardian.getCurrentHp(),
                        sourceAttackBefore, guardian.getAttack(),
                        sourceSpeedBefore, guardian.getSpeed(),
                        getFieldUnitsStatus(),
                        0, EffectType.SILENCE_IMMUNE, null,
                        guardian.getName() + "登场驱散减益");
                break;

            case "烛龙":
                // 致命衰竭：登场目标攻击减少10%
                Guardian target = guardian.getCamp() == Camp.A ? fieldB : fieldA;
                if (target != null) {
                    int targetAttackBefore = target.getAttack();
                    int weaken = (int) (target.getAttack() * 0.1);
                    target.setAttack(target.getAttack() - weaken);

                    addLog("致命衰竭",
                            guardian.getName(), guardian.getCamp(),
                            sourceHpBefore, guardian.getCurrentHp(),
                            sourceAttackBefore, guardian.getAttack(),
                            sourceSpeedBefore, guardian.getSpeed(),
                            target.getName(), target.getCamp(),
                            target.getCurrentHp(), target.getCurrentHp(),
                            targetAttackBefore, target.getAttack(),
                            target.getSpeed(), target.getSpeed(),
                            getFieldUnitsStatus(),
                            weaken, EffectType.WEAKEN, null,
                            target.getName() + "攻击降低10%");
                }
                break;

            case "阎王":
                // 幽冥审判：令随机敌方中毒
                List<Guardian> enemies = guardian.getCamp() == Camp.A ? campB : campA;
                if (!enemies.isEmpty()) {
                    Guardian randomEnemy = enemies.get(random.nextInt(enemies.size()));
                    int enemyHpBefore = randomEnemy.getCurrentHp();
                    int poisonValue = 73 * guardian.getLevel();
                    randomEnemy.getEffects().put(EffectType.POISON, poisonValue);

                    addLog("幽冥审判",
                            guardian.getName(), guardian.getCamp(),
                            sourceHpBefore, guardian.getCurrentHp(),
                            sourceAttackBefore, guardian.getAttack(),
                            sourceSpeedBefore, guardian.getSpeed(),
                            randomEnemy.getName(), randomEnemy.getCamp(),
                            enemyHpBefore, randomEnemy.getCurrentHp(),
                            randomEnemy.getAttack(), randomEnemy.getAttack(),
                            randomEnemy.getSpeed(), randomEnemy.getSpeed(),
                            getFieldUnitsStatus(),
                            poisonValue, EffectType.POISON, DamageType.POISON,
                            guardian.getName() + "触发幽冥审判");
                }
                break;
        }
    }

    // 触发攻击前技能
    private void triggerPreAttackSkills(Guardian attacker, Guardian defender) {
        int attackerHpBefore = attacker.getCurrentHp();
        int attackerAttackBefore = attacker.getAttack();
        int attackerSpeedBefore = attacker.getSpeed();

        switch (attacker.getName()) {
            case "齐天大圣":
                // 定海神针：当前生命值6%伤害
                if (1==1){
                    int defenderHpBefore = defender.getCurrentHp();
                    int damage = (int) (defender.getCurrentHp() * 0.06 * attacker.getLevel());
                    defender.setCurrentHp(defender.getCurrentHp() - damage);

                    addLog("定海神针",
                            attacker.getName(), attacker.getCamp(),
                            attackerHpBefore, attacker.getCurrentHp(),
                            attackerAttackBefore, attacker.getAttack(),
                            attackerSpeedBefore, attacker.getSpeed(),
                            defender.getName(), defender.getCamp(),
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
                if (random.nextDouble() < 0.13 * attacker.getLevel()) {
                    int defenderHpBefore = defender.getCurrentHp();
                    int burnDamage = 220 * attacker.getLevel();
                    defender.setCurrentHp(defender.getCurrentHp() - burnDamage);

                    addLog("斩杀",
                            attacker.getName(), attacker.getCamp(),
                            attackerHpBefore, attacker.getCurrentHp(),
                            attackerAttackBefore, attacker.getAttack(),
                            attackerSpeedBefore, attacker.getSpeed(),
                            defender.getName(), defender.getCamp(),
                            defenderHpBefore, defender.getCurrentHp(),
                            defender.getAttack(), defender.getAttack(),
                            defender.getSpeed(), defender.getSpeed(),
                            getFieldUnitsStatus(),
                            burnDamage, EffectType.FIRE_DAMAGE, DamageType.FIRE,
                            attacker.getName() + "触发斩杀");
                }
                break;
        }
    }

    // 触发受击技能
    private void triggerOnAttackedSkills(Guardian defender, Guardian attacker) {
        int defenderHpBefore = defender.getCurrentHp();
        int defenderAttackBefore = defender.getAttack();
        int defenderSpeedBefore = defender.getSpeed();

        switch (defender.getName()) {
            case "聂小倩":
                // 幽灵毒击：令攻击者中毒
                if (1==1){
                    int attackerHpBefore = attacker.getCurrentHp();
                    int poisonValue = 8 * defender.getLevel();
                    attacker.getEffects().put(EffectType.POISON, poisonValue);

                    addLog("幽灵毒击",
                            defender.getName(), defender.getCamp(),
                            defenderHpBefore, defender.getCurrentHp(),
                            defenderAttackBefore, defender.getAttack(),
                            defenderSpeedBefore, defender.getSpeed(),
                            attacker.getName(), attacker.getCamp(),
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
                List<Guardian> enemies = defender.getCamp() == Camp.A ? campB : campA;
                List<Guardian> aliveEnemies = enemies.stream()
                        .filter(g -> !g.isDead())
                        .collect(Collectors.toList());

                if (!aliveEnemies.isEmpty()) {
                    int fireDamage = 54 * defender.getLevel();
                    List<String> targetNames = new ArrayList<>();
                    Map<String, int[]> targetStatus = new HashMap<>();

                    // 记录目标状态并执行伤害
                    aliveEnemies.forEach(g -> {
                        targetNames.add(g.getName());
                        int hpBefore = g.getCurrentHp();
                        g.setCurrentHp(g.getCurrentHp() - fireDamage);
                        targetStatus.put(g.getName(), new int[]{hpBefore, g.getCurrentHp()});
                    });

                    // 单条日志记录多目标
                    addMultiTargetLog("烛火燎原",
                            defender.getName(), defender.getCamp(),
                            defenderHpBefore, defender.getCurrentHp(),
                            defenderAttackBefore, defender.getAttack(),
                            defenderSpeedBefore, defender.getSpeed(),
                            targetNames, defender.getCamp() == Camp.A ? Camp.B : Camp.A,
                            targetStatus,
                            getFieldUnitsStatus(),
                            fireDamage, EffectType.FIRE_DAMAGE, DamageType.FIRE,
                            defender.getName() + "触发烛火燎原，对全体敌方造成火焰伤害");
                }
                break;

            case "真武大帝":
                // 绝地反击：造成敌方攻击10%的伤害
                if (1==1){
                    int attackerHpBefore = attacker.getCurrentHp();
                    int counterDamage = (int) (attacker.getAttack() * 0.1);
                    attacker.setCurrentHp(attacker.getCurrentHp() - counterDamage);

                    addLog("绝地反击",
                            defender.getName(), defender.getCamp(),
                            defenderHpBefore, defender.getCurrentHp(),
                            defenderAttackBefore, defender.getAttack(),
                            defenderSpeedBefore, defender.getSpeed(),
                            attacker.getName(), attacker.getCamp(),
                            attackerHpBefore, attacker.getCurrentHp(),
                            attacker.getAttack(), attacker.getAttack(),
                            attacker.getSpeed(), attacker.getSpeed(),
                            getFieldUnitsStatus(),
                            counterDamage, EffectType.DAMAGE, DamageType.TRUE,
                            defender.getName() + "触发绝地反击");
                }
                break;
        }
    }

    // 触发攻击后技能
    private void triggerPostAttackSkills(Guardian attacker, Guardian defender) {
        int attackerHpBefore = attacker.getCurrentHp();
        int attackerAttackBefore = attacker.getAttack();
        int attackerSpeedBefore = attacker.getSpeed();

        switch (attacker.getName()) {
            case "铁扇公主":
                // 芭蕉扇：造成火焰伤害
                int defenderHpBefore = defender.getCurrentHp();
                int fireDamage = 36 * attacker.getLevel();
                defender.setCurrentHp(defender.getCurrentHp() - fireDamage);

                addLog("芭蕉扇",
                        attacker.getName(), attacker.getCamp(),
                        attackerHpBefore, attacker.getCurrentHp(),
                        attackerAttackBefore, attacker.getAttack(),
                        attackerSpeedBefore, attacker.getSpeed(),
                        defender.getName(), defender.getCamp(),
                        defenderHpBefore, defender.getCurrentHp(),
                        defender.getAttack(), defender.getAttack(),
                        defender.getSpeed(), defender.getSpeed(),
                        getFieldUnitsStatus(),
                        fireDamage, EffectType.FIRE_DAMAGE, DamageType.FIRE,
                        attacker.getName() + "触发芭蕉扇");
                break;

            case "牛魔王":
                // 熔岩爆发：对场下敌方造成火焰伤害（多目标整合）
                List<Guardian> offFieldEnemies = defender.getCamp() == Camp.A ?
                        campA.stream().filter(g -> !g.isOnField() && !g.isDead()).collect(Collectors.toList()) :
                        campB.stream().filter(g -> !g.isOnField() && !g.isDead()).collect(Collectors.toList());

                if (!offFieldEnemies.isEmpty()) {
                    int lavaDamage = 62 * attacker.getLevel();
                    List<String> targetNames = new ArrayList<>();
                    Map<String, int[]> targetStatus = new HashMap<>();

                    offFieldEnemies.forEach(g -> {
                        targetNames.add(g.getName());
                        int hpBefore = g.getCurrentHp();
                        g.setCurrentHp(g.getCurrentHp() - lavaDamage);
                        targetStatus.put(g.getName(), new int[]{hpBefore, g.getCurrentHp()});
                    });

                    // 单条日志记录多目标
                    addMultiTargetLog("熔岩爆发",
                            attacker.getName(), attacker.getCamp(),
                            attackerHpBefore, attacker.getCurrentHp(),
                            attackerAttackBefore, attacker.getAttack(),
                            attackerSpeedBefore, attacker.getSpeed(),
                            targetNames, defender.getCamp(),
                            targetStatus,
                            getFieldUnitsStatus(),
                            lavaDamage, EffectType.FIRE_DAMAGE, DamageType.FIRE,
                            attacker.getName() + "触发熔岩爆发，对场下敌方造成火焰伤害");
                }
                break;
        }
    }

    // 触发死亡相关技能
    private void triggerOnDeathSkills(Guardian deadGuardian) {
        // 牛魔王鲜血盛宴
        if (deadGuardian.getName().equals("牛魔王") && deadGuardian.getBuffStacks() < 3) {
            int hpBefore = deadGuardian.getCurrentHp();
            int maxHpBefore = deadGuardian.getMaxHp();
            int attackBefore = deadGuardian.getAttack();

            deadGuardian.setBuffStacks(deadGuardian.getBuffStacks() + 1);
            deadGuardian.setMaxHp(deadGuardian.getMaxHp() + 117);

            addLog("鲜血盛宴",
                    deadGuardian.getName(), deadGuardian.getCamp(),
                    hpBefore, deadGuardian.getCurrentHp(),
                    attackBefore, deadGuardian.getAttack(),
                    deadGuardian.getSpeed(), deadGuardian.getSpeed(),
                    deadGuardian.getName(), deadGuardian.getCamp(),
                    hpBefore, deadGuardian.getCurrentHp(),
                    attackBefore, deadGuardian.getAttack(),
                    deadGuardian.getSpeed(), deadGuardian.getSpeed(),
                    getFieldUnitsStatus(),
                    117, EffectType.HP_UP, null,
                    deadGuardian.getName() + "触发鲜血盛宴，生命上限提升");
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
                int heal = 90;
                List<String> targetNames = new ArrayList<>();
                Map<String, int[]> targetStatus = new HashMap<>();

                immortalAllies.forEach(g -> {
                    targetNames.add(g.getName());
                    int hpBefore = g.getCurrentHp();
                    g.setCurrentHp(g.getCurrentHp() + heal);
                    targetStatus.put(g.getName(), new int[]{hpBefore, g.getCurrentHp()});
                });

                addMultiTargetLog("生生不息",
                        changsheng.getName(), Camp.A,
                        changsheng.getCurrentHp(), changsheng.getCurrentHp(),
                        changsheng.getAttack(), changsheng.getAttack(),
                        changsheng.getSpeed(), changsheng.getSpeed(),
                        targetNames, Camp.A,
                        targetStatus,
                        getFieldUnitsStatus(),
                        heal, EffectType.HEAL, null,
                        "长生大帝触发生生不息，治疗我方仙界单位");
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
                int heal = 90;
                List<String> targetNames = new ArrayList<>();
                Map<String, int[]> targetStatus = new HashMap<>();

                immortalAllies.forEach(g -> {
                    targetNames.add(g.getName());
                    int hpBefore = g.getCurrentHp();
                    g.setCurrentHp(g.getCurrentHp() + heal);
                    targetStatus.put(g.getName(), new int[]{hpBefore, g.getCurrentHp()});
                });

                addMultiTargetLog("生生不息",
                        changsheng.getName(), Camp.B,
                        changsheng.getCurrentHp(), changsheng.getCurrentHp(),
                        changsheng.getAttack(), changsheng.getAttack(),
                        changsheng.getSpeed(), changsheng.getSpeed(),
                        targetNames, Camp.B,
                        targetStatus,
                        getFieldUnitsStatus(),
                        heal, EffectType.HEAL, null,
                        "长生大帝触发生生不息，治疗我方仙界单位");
            }
        }
    }

    // 处理回合开始效果
    private void processRoundStartEffects() {
        // 厚土娘娘后土聚能
        if (fieldA != null && fieldA.getName().equals("厚土娘娘") && fieldA.getBuffStacks() < 99) {
            int hpBefore = fieldA.getCurrentHp();
            int maxHpBefore = fieldA.getMaxHp();
            int attackBefore = fieldA.getAttack();

            fieldA.setBuffStacks(fieldA.getBuffStacks() + 1);
            fieldA.setMaxHp(fieldA.getMaxHp() + 197);
            fieldA.setAttack(fieldA.getAttack() + 67);

            addLog("后土聚能",
                    fieldA.getName(), fieldA.getCamp(),
                    hpBefore, fieldA.getCurrentHp(),
                    attackBefore, fieldA.getAttack(),
                    fieldA.getSpeed(), fieldA.getSpeed(),
                    fieldA.getName(), fieldA.getCamp(),
                    hpBefore, fieldA.getCurrentHp(),
                    attackBefore, fieldA.getAttack(),
                    fieldA.getSpeed(), fieldA.getSpeed(),
                    getFieldUnitsStatus(),
                    197, EffectType.HP_UP, null,
                    fieldA.getName() + "触发后土聚能，生命上限+197，攻击+67");
        }

        if (fieldB != null && fieldB.getName().equals("厚土娘娘") && fieldB.getBuffStacks() < 99) {
            int hpBefore = fieldB.getCurrentHp();
            int maxHpBefore = fieldB.getMaxHp();
            int attackBefore = fieldB.getAttack();

            fieldB.setBuffStacks(fieldB.getBuffStacks() + 1);
            fieldB.setMaxHp(fieldB.getMaxHp() + 197);
            fieldB.setAttack(fieldB.getAttack() + 67);

            addLog("后土聚能",
                    fieldB.getName(), fieldB.getCamp(),
                    hpBefore, fieldB.getCurrentHp(),
                    attackBefore, fieldB.getAttack(),
                    fieldB.getSpeed(), fieldB.getSpeed(),
                    fieldB.getName(), fieldB.getCamp(),
                    hpBefore, fieldB.getCurrentHp(),
                    attackBefore, fieldB.getAttack(),
                    fieldB.getSpeed(), fieldB.getSpeed(),
                    getFieldUnitsStatus(),
                    197, EffectType.HP_UP, null,
                    fieldB.getName() + "触发后土聚能，生命上限+197，攻击+67");
        }

        // 阎王生死簿（多目标整合）
        if (campA.stream().anyMatch(g -> g.getName().equals("阎王") && !g.isDead())) {
            Guardian yanwang = campA.stream()
                    .filter(g -> g.getName().equals("阎王") && !g.isDead())
                    .findFirst().get();

            List<Guardian> allUnits = new ArrayList<>();
            allUnits.addAll(campA);
            allUnits.addAll(campB);

            List<Guardian> aliveUnits = allUnits.stream()
                    .filter(g -> !g.isDead())
                    .collect(Collectors.toList());

            if (!aliveUnits.isEmpty()) {
                int reduce = 100;
                List<String> targetNames = new ArrayList<>();
                Map<String, int[]> targetStatus = new HashMap<>();

                aliveUnits.forEach(g -> {
                    targetNames.add(g.getName());
                    int maxHpBefore = g.getMaxHp();
                    g.setMaxHp(g.getMaxHp() - reduce);
                    targetStatus.put(g.getName(), new int[]{maxHpBefore, g.getMaxHp()});
                });

                addMultiTargetLog("生死簿",
                        yanwang.getName(), Camp.A,
                        yanwang.getCurrentHp(), yanwang.getCurrentHp(),
                        yanwang.getAttack(), yanwang.getAttack(),
                        yanwang.getSpeed(), yanwang.getSpeed(),
                        targetNames, null,
                        targetStatus,
                        getFieldUnitsStatus(),
                        reduce, EffectType.MAX_HP_DOWN, null,
                        "阎王触发生死簿，降低全体单位生命上限");
            }
        }

        if (campB.stream().anyMatch(g -> g.getName().equals("阎王") && !g.isDead())) {
            Guardian yanwang = campB.stream()
                    .filter(g -> g.getName().equals("阎王") && !g.isDead())
                    .findFirst().get();

            List<Guardian> allUnits = new ArrayList<>();
            allUnits.addAll(campA);
            allUnits.addAll(campB);

            List<Guardian> aliveUnits = allUnits.stream()
                    .filter(g -> !g.isDead())
                    .collect(Collectors.toList());

            if (!aliveUnits.isEmpty()) {
                int reduce = 100;
                List<String> targetNames = new ArrayList<>();
                Map<String, int[]> targetStatus = new HashMap<>();

                aliveUnits.forEach(g -> {
                    targetNames.add(g.getName());
                    int maxHpBefore = g.getMaxHp();
                    g.setMaxHp(g.getMaxHp() - reduce);
                    targetStatus.put(g.getName(), new int[]{maxHpBefore, g.getMaxHp()});
                });

                addMultiTargetLog("生死簿",
                        yanwang.getName(), Camp.B,
                        yanwang.getCurrentHp(), yanwang.getCurrentHp(),
                        yanwang.getAttack(), yanwang.getAttack(),
                        yanwang.getSpeed(), yanwang.getSpeed(),
                        targetNames, null,
                        targetStatus,
                        getFieldUnitsStatus(),
                        reduce, EffectType.MAX_HP_DOWN, null,
                        "阎王触发生死簿，降低全体单位生命上限");
            }
        }

        // 场下中毒效果（批量处理）
        processPoisonEffects();

        // 妲己场下技能
        processDajiSkills();
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
            Map<String, int[]> targetStatus = new HashMap<>();
            List<String> deadUnits = new ArrayList<>();

            poisonedUnits.forEach(g -> {
                targetNames.add(g.getName());
                int hpBefore = g.getCurrentHp();
                int poisonDamage = g.getEffects().get(EffectType.POISON);
                g.setCurrentHp(g.getCurrentHp() - poisonDamage);
                targetStatus.put(g.getName(), new int[]{hpBefore, g.getCurrentHp()});

                if (g.getCurrentHp() <= 0) {
                    g.setDead(true);
                    g.setOnField(false);
                    deadUnits.add(g.getName());
                }
            });

            // 中毒伤害日志（批量）
            addMultiTargetLog("POISON",
                    "SYSTEM", null,
                    0, 0, 0, 0, 0, 0,
                    targetNames, null,
                    targetStatus,
                    getFieldUnitsStatus(),
                    0, EffectType.POISON, DamageType.POISON,
                    "中毒效果触发");

            // 中毒阵亡日志
            if (!deadUnits.isEmpty()) {
                Map<String, int[]> deadStatus = new HashMap<>();
                deadUnits.forEach(unit -> {
                    deadStatus.put(unit, new int[]{0, 0});
                });

                addMultiTargetLog("UNIT_DEATH",
                        "SYSTEM", null,
                        0, 0, 0, 0, 0, 0,
                        deadUnits, null,
                        deadStatus,
                        getFieldUnitsStatus(),
                        0, null, null,
                        "单位因中毒阵亡");
            }
        }
    }

    // 处理妲己场下技能
    private void processDajiSkills() {
        // A队妲己
        if (campA.stream().anyMatch(g -> g.getName().equals("妲己") && !g.isDead() && !g.isOnField())) {
            Guardian daji = campA.stream()
                    .filter(g -> g.getName().equals("妲己") && !g.isDead() && !g.isOnField())
                    .findFirst().get();

            int dajiHpBefore = daji.getCurrentHp();
            int dajiAttackBefore = daji.getAttack();
            int dajiSpeedBefore = daji.getSpeed();

            // 妖狐蔽天：3%几率眩晕当前敌人
            if (random.nextDouble() < 0.03 && fieldB != null) {
                int targetHpBefore = fieldB.getCurrentHp();
                fieldB.getEffects().put(EffectType.STUN, 1);

                addLog("妖狐蔽天",
                        daji.getName(), Camp.A,
                        dajiHpBefore, daji.getCurrentHp(),
                        dajiAttackBefore, daji.getAttack(),
                        dajiSpeedBefore, daji.getSpeed(),
                        fieldB.getName(), Camp.B,
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

            if (!enemies.isEmpty()) {
                Guardian randomEnemy = enemies.get(random.nextInt(enemies.size()));
                int targetHpBefore = randomEnemy.getCurrentHp();
                int poisonValue = 7;
                randomEnemy.getEffects().put(EffectType.POISON, poisonValue);

                addLog("谄媚噬魂",
                        daji.getName(), Camp.A,
                        dajiHpBefore, daji.getCurrentHp(),
                        dajiAttackBefore, daji.getAttack(),
                        dajiSpeedBefore, daji.getSpeed(),
                        randomEnemy.getName(), Camp.B,
                        targetHpBefore, randomEnemy.getCurrentHp(),
                        randomEnemy.getAttack(), randomEnemy.getAttack(),
                        randomEnemy.getSpeed(), randomEnemy.getSpeed(),
                        getFieldUnitsStatus(),
                        poisonValue, EffectType.POISON, DamageType.POISON,
                        "妲己触发谄媚噬魂，使敌人中毒");
            }
        }

        // B队妲己
        if (campB.stream().anyMatch(g -> g.getName().equals("妲己") && !g.isDead() && !g.isOnField())) {
            Guardian daji = campB.stream()
                    .filter(g -> g.getName().equals("妲己") && !g.isDead() && !g.isOnField())
                    .findFirst().get();

            int dajiHpBefore = daji.getCurrentHp();
            int dajiAttackBefore = daji.getAttack();
            int dajiSpeedBefore = daji.getSpeed();

            // 妖狐蔽天：3%几率眩晕当前敌人
            if (random.nextDouble() < 0.03 && fieldA != null) {
                int targetHpBefore = fieldA.getCurrentHp();
                fieldA.getEffects().put(EffectType.STUN, 1);

                addLog("妖狐蔽天",
                        daji.getName(), Camp.B,
                        dajiHpBefore, daji.getCurrentHp(),
                        dajiAttackBefore, daji.getAttack(),
                        dajiSpeedBefore, daji.getSpeed(),
                        fieldA.getName(), Camp.A,
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

            if (!enemies.isEmpty()) {
                Guardian randomEnemy = enemies.get(random.nextInt(enemies.size()));
                int targetHpBefore = randomEnemy.getCurrentHp();
                int poisonValue = 7;
                randomEnemy.getEffects().put(EffectType.POISON, poisonValue);

                addLog("谄媚噬魂",
                        daji.getName(), Camp.B,
                        dajiHpBefore, daji.getCurrentHp(),
                        dajiAttackBefore, daji.getAttack(),
                        dajiSpeedBefore, daji.getSpeed(),
                        randomEnemy.getName(), Camp.A,
                        targetHpBefore, randomEnemy.getCurrentHp(),
                        randomEnemy.getAttack(), randomEnemy.getAttack(),
                        randomEnemy.getSpeed(), randomEnemy.getSpeed(),
                        getFieldUnitsStatus(),
                        poisonValue, EffectType.POISON, DamageType.POISON,
                        "妲己触发谄媚噬魂，使敌人中毒");
            }
        }
    }

    // 处理回合结束效果
    private void processRoundEndEffects() {
        // 托塔天王仙塔庇护
        if (fieldA != null && fieldA.getName().equals("托塔天王")) {
            int hpBefore = fieldA.getCurrentHp();
            int heal = 25 * fieldA.getLevel();
            fieldA.setCurrentHp(fieldA.getCurrentHp() + heal);

            addLog("仙塔庇护",
                    fieldA.getName(), fieldA.getCamp(),
                    hpBefore, fieldA.getCurrentHp(),
                    fieldA.getAttack(), fieldA.getAttack(),
                    fieldA.getSpeed(), fieldA.getSpeed(),
                    fieldA.getName(), fieldA.getCamp(),
                    hpBefore, fieldA.getCurrentHp(),
                    fieldA.getAttack(), fieldA.getAttack(),
                    fieldA.getSpeed(), fieldA.getSpeed(),
                    getFieldUnitsStatus(),
                    heal, EffectType.HEAL, null,
                    fieldA.getName() + "触发仙塔庇护，恢复生命值");
        }

        if (fieldB != null && fieldB.getName().equals("托塔天王")) {
            int hpBefore = fieldB.getCurrentHp();
            int heal = 25 * fieldB.getLevel();
            fieldB.setCurrentHp(fieldB.getCurrentHp() + heal);

            addLog("仙塔庇护",
                    fieldB.getName(), fieldB.getCamp(),
                    hpBefore, fieldB.getCurrentHp(),
                    fieldB.getAttack(), fieldB.getAttack(),
                    fieldB.getSpeed(), fieldB.getSpeed(),
                    fieldB.getName(), fieldB.getCamp(),
                    hpBefore, fieldB.getCurrentHp(),
                    fieldB.getAttack(), fieldB.getAttack(),
                    fieldB.getSpeed(), fieldB.getSpeed(),
                    getFieldUnitsStatus(),
                    heal, EffectType.HEAL, null,
                    fieldB.getName() + "触发仙塔庇护，恢复生命值");
        }
    }

    // 检查并替换阵亡护法
    private void checkAndReplaceGuardians() {
        if (fieldA != null && fieldA.isDead()) {
            Guardian newA = getNextGuardian(campA);
            if (newA != null) {
                fieldA = newA;
                fieldA.setOnField(true);

                addLog("UNIT_ENTER",
                        fieldA.getName(), fieldA.getCamp(),
                        fieldA.getCurrentHp(), fieldA.getCurrentHp(),
                        fieldA.getAttack(), fieldA.getAttack(),
                        fieldA.getSpeed(), fieldA.getSpeed(),
                        fieldA.getName(), fieldA.getCamp(),
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
                        fieldB.getName(), fieldB.getCamp(),
                        fieldB.getCurrentHp(), fieldB.getCurrentHp(),
                        fieldB.getAttack(), fieldB.getAttack(),
                        fieldB.getSpeed(), fieldB.getSpeed(),
                        fieldB.getName(), fieldB.getCamp(),
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
                "SYSTEM", null,
                0, 0, 0, 0, 0, 0,
                null, null,
                0, 0, 0, 0, 0, 0,
                getFieldUnitsStatus(),
                0, null, null,
                "战斗结束：" + result +
                        " A队剩余总血量：" + totalHpA +
                        " B队剩余总血量：" + totalHpB);
    }

    // 添加单目标日志
    private void addLog(String eventType,
                        String sourceUnit, Camp sourceCamp,
                        int sourceHpBefore, int sourceHpAfter,
                        int sourceAttackBefore, int sourceAttackAfter,
                        int sourceSpeedBefore, int sourceSpeedAfter,
                        String targetUnit, Camp targetCamp,
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
                sourceHpBefore,
                sourceHpAfter,
                sourceAttackBefore,
                sourceAttackAfter,
                sourceSpeedBefore,
                sourceSpeedAfter,
                targetUnit,
                null,
                targetCamp,
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
                extraDesc
        ));
    }

    // 添加多目标日志
    private void addMultiTargetLog(String eventType,
                                   String sourceUnit, Camp sourceCamp,
                                   int sourceHpBefore, int sourceHpAfter,
                                   int sourceAttackBefore, int sourceAttackAfter,
                                   int sourceSpeedBefore, int sourceSpeedAfter,
                                   List<String> targetUnitList, Camp targetCamp,
                                   Map<String, int[]> targetStatus,
                                   String fieldUnitsStatus,
                                   int value, EffectType effectType, DamageType damageType, String extraDesc) {
        // 构建目标状态描述
        StringBuilder targetDesc = new StringBuilder();
        targetStatus.forEach((unit, status) -> {
            targetDesc.append(String.format("%s[HP:%d→%d],", unit, status[0], status[1]));
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
                fieldUnitsStatus,
                value,
                effectType,
                damageType,
                extraDesc + " - " + targetDesc.toString()
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