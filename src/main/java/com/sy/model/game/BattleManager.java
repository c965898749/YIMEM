package com.sy.model.game;

import com.sy.tool.CardSkillLevelUtil;
import com.sy.tool.CardSkillLevelUtil;
import com.sy.tool.ProbabilityBooleanUtils;
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
    }

    //比较速度大小
    private Boolean getSpeed(Guardian guardianA, Guardian guardianB) {
        // 1. 计算所有中毒效果的总伤害（累加 POISON 类型的 value）
        int speedA = guardianA.getSpeed();
        // 2. 计算毒抗相关（直接基于你现有 EffectInstance 计算，不新增 Guardian 方法）
        // 物理攻击增益：所有 POISON_RESIST 类型效果的 value 总和
        int resistUp = calculateTotalVaule(guardianA, EffectType.SPEED_UP);
        // 物理攻击增益百分比：所有 POISON_RESIST 类型效果的 value 乘积
        double resistUpPret = calculateTotalUpPretVaule(guardianA, EffectType.SPEED_UP_PRET);
        // 物理攻击降低：所有 POISON_RESIST_DOWN 类型效果的 value 总和
        int resistDown = calculateTotalVaule(guardianA, EffectType.SPEED_DOWN);
        // 物理攻击降低百分比：所有 POISON_RESIST 类型效果的 value 乘积
        double resistDownPret = calculateTotalDownPretVaule(guardianA, EffectType.SPEED_DOWN_PRET);

        int speedB = guardianB.getSpeed();
        // 2. 计算毒抗相关（直接基于你现有 EffectInstance 计算，不新增 Guardian 方法）
        // 物理抗性增益：所有 POISON_RESIST 类型效果的 value 总和
        int targetUp = calculateTotalVaule(guardianB, EffectType.SPEED_UP);
        //  物理抗性增益百分比：所有 POISON_RESIST 类型效果的 value 乘积
        double targetUpPret = calculateTotalUpPretVaule(guardianB, EffectType.SPEED_UP_PRET);
        // 物理抗性降低：所有 POISON_RESIST_DOWN 类型效果的 value 总和
        int targetDown = calculateTotalVaule(guardianB, EffectType.SPEED_DOWN);
        // 物理抗性降低百分比：所有 POISON_RESIST 类型效果的 value 乘积
        double targetDownPret = calculateTotalDownPretVaule(guardianB, EffectType.SPEED_DOWN_PRET);
        // 最终（仅基于 buff 计算，无新增方法）
        speedA=(int)(speedA*resistDownPret*resistDownPret+resistUp-resistDown);
        speedB=(int)(speedB*targetUpPret*targetDownPret+targetUp-targetDown);
        // 物理攻击
        return speedA-speedB >= 0;
    }

    // 初始化战斗
    private void initBattle() {
        fieldA = getNextGuardian(campA);
        fieldB = getNextGuardian(campB);
        if (getSpeed(fieldA, fieldB)) {
            if (fieldA != null) {
                fieldA.setOnField(true);
                Map<String, TargetBattleData> multiTargetDataMap = new HashMap<>();
                TargetBattleData data = new TargetBattleData(fieldA.getMaxHp(), fieldA.getCurrentHp(), 0, fieldA.isOnField());
                if (fieldA.isSilence()) {
                    data.setSilence(true);
                } else {
                    data.setSilence(false);
                }
                if (fieldA.isFireBoost()) {
                    data.setFireBoost(true);
                } else {
                    data.setFireBoost(false);
                }
                if (fieldA.isPoison()) {
                    data.setPoison(true);
                } else {
                    data.setPoison(false);
                }
                if (fieldA.isStunned()) {
                    data.setStunned(true);
                } else {
                    data.setStunned(false);
                }
                multiTargetDataMap.put(fieldA.getId(), data);
                addLogEnter(fieldA.getFlyup(), "UNIT_ENTER",
                        fieldA.getId(),
                        fieldA.getMaxHp(), fieldA.getCurrentHp(),
                        fieldA.isOnField(),
                        multiTargetDataMap,
                        fieldA.getName() + "登场");

            }
            if (fieldB != null) {
                fieldB.setOnField(true);
                Map<String, TargetBattleData> multiTargetDataMap = new HashMap<>();
                TargetBattleData data = new TargetBattleData(fieldB.getMaxHp(), fieldB.getCurrentHp(), 0, fieldB.isOnField());
                if (fieldB.isSilence()) {
                    data.setSilence(true);
                } else {
                    data.setSilence(false);
                }
                if (fieldB.isFireBoost()) {
                    data.setFireBoost(true);
                } else {
                    data.setFireBoost(false);
                }
                if (fieldB.isPoison()) {
                    data.setPoison(true);
                } else {
                    data.setPoison(false);
                }
                if (fieldB.isStunned()) {
                    data.setStunned(true);
                } else {
                    data.setStunned(false);
                }
                multiTargetDataMap.put(fieldB.getId(), data);
                addLogEnter(fieldB.getFlyup(), "UNIT_ENTER",
                        fieldB.getId(),
                        fieldB.getMaxHp(), fieldB.getCurrentHp(),
                        fieldB.isOnField(),
                        multiTargetDataMap,
                        fieldB.getName() + "登场");

            }
            triggerOnEnterSkills(fieldA);
            triggerOnEnterSkills(fieldB);
            triggerOnEnterSkills2(fieldA);
            triggerOnEnterSkills2(fieldB);
        } else {
            if (fieldB != null) {
                fieldB.setOnField(true);
                Map<String, TargetBattleData> multiTargetDataMap = new HashMap<>();
                TargetBattleData data = new TargetBattleData(fieldB.getMaxHp(), fieldB.getCurrentHp(), 0, fieldB.isOnField());
                if (fieldB.isSilence()) {
                    data.setSilence(true);
                } else {
                    data.setSilence(false);
                }
                if (fieldB.isFireBoost()) {
                    data.setFireBoost(true);
                } else {
                    data.setFireBoost(false);
                }
                if (fieldB.isPoison()) {
                    data.setPoison(true);
                } else {
                    data.setPoison(false);
                }
                if (fieldB.isStunned()) {
                    data.setStunned(true);
                } else {
                    data.setStunned(false);
                }
                multiTargetDataMap.put(fieldB.getId(), data);
                addLogEnter(fieldB.getFlyup(), "UNIT_ENTER",
                        fieldB.getId(),
                        fieldB.getMaxHp(), fieldB.getCurrentHp(),
                        fieldB.isOnField(),
                        multiTargetDataMap,
                        fieldB.getName() + "登场");
            }
            if (fieldA != null) {
                fieldA.setOnField(true);
                Map<String, TargetBattleData> multiTargetDataMap = new HashMap<>();
                TargetBattleData data = new TargetBattleData(fieldA.getMaxHp(), fieldA.getCurrentHp(), 0, fieldA.isOnField());
                if (fieldA.isSilence()) {
                    data.setSilence(true);
                } else {
                    data.setSilence(false);
                }
                if (fieldA.isFireBoost()) {
                    data.setFireBoost(true);
                } else {
                    data.setFireBoost(false);
                }
                if (fieldA.isPoison()) {
                    data.setPoison(true);
                } else {
                    data.setPoison(false);
                }
                if (fieldA.isStunned()) {
                    data.setStunned(true);
                } else {
                    data.setStunned(false);
                }
                multiTargetDataMap.put(fieldA.getId(), data);
                addLogEnter(fieldA.getFlyup(), "UNIT_ENTER",
                        fieldA.getId(),
                        fieldA.getMaxHp(), fieldA.getCurrentHp(),
                        fieldA.isOnField(),
                        multiTargetDataMap,
                        fieldA.getName() + "登场");

            }
            triggerOnEnterSkills(fieldB);
            triggerOnEnterSkills(fieldA);
            triggerOnEnterSkills2(fieldB);
            triggerOnEnterSkills2(fieldA);

        }
    }

    // 获取下一个可上场的护法
    private Guardian getNextGuardian(List<Guardian> camp) {
        return camp.stream()
                .filter(g -> !g.isDead() && !g.isOnField())
                .findFirst()
                .orElse(null);
    }


    // 开始战斗
    public void startBattle() {
        //初始化光环
        List<Guardian> zhongyues = this.campA.stream().filter(g -> g.getName().equals("中岳大帝") && !g.isDead()).collect(Collectors.toList());
        if (Xtool.isNotNull(zhongyues)) {
            Guardian zhongyue = zhongyues.get(0);
            int[] skillLevel = CardSkillLevelUtil.calculateSkillLevels(zhongyue.getLevel(), zhongyue.getStar().doubleValue());
            if (skillLevel[1] > 0) {
//                五岳灵脉Lv1光环-增加我方全体飞弹伤害20点；
                int heal = 25 * skillLevel[1];
                for (Guardian guardian : this.campA) {
                    guardian.setFdAtk(guardian.getFdAtk() + heal);
                }
            }

        }
        //初始化光环
        List<Guardian> zhongyues2 = this.campB.stream().filter(g -> g.getName().equals("中岳大帝") && !g.isDead()).collect(Collectors.toList());
        if (Xtool.isNotNull(zhongyues2)) {
            Guardian zhongyue = zhongyues2.get(0);
            int[] skillLevel = CardSkillLevelUtil.calculateSkillLevels(zhongyue.getLevel(), zhongyue.getStar().doubleValue());
            if (skillLevel[1] > 0) {
//                五岳灵脉Lv1光环-增加我方全体飞弹伤害20点；
                int heal = 25 * skillLevel[1];
                for (Guardian guardian : this.campB) {
                    guardian.setFdAtk(guardian.getFdAtk() + heal);
                }
            }

        }
        List<Guardian> baisuzhens = this.campB.stream().filter(g -> g.getName().equals("白素贞") && !g.isDead()).collect(Collectors.toList());
        if (Xtool.isNotNull(baisuzhens)) {
            Guardian baisuzhen = baisuzhens.get(0);
            int[] skillLevel = CardSkillLevelUtil.calculateSkillLevels(baisuzhen.getLevel(), baisuzhen.getStar().doubleValue());
//            白素贞， 水漫金山Lv1减免自身收到火焰伤害10%；
            baisuzhen.addEffect(EffectType.FIRE_RESIST_BOOST_PRET, skillLevel[0] * 10, 990, baisuzhen.getId());

        }

        List<Guardian> baisuzhens2 = this.campA.stream().filter(g -> g.getName().equals("白素贞") && !g.isDead()).collect(Collectors.toList());
        if (Xtool.isNotNull(baisuzhens2)) {
            Guardian baisuzhen = baisuzhens2.get(0);
            int[] skillLevel = CardSkillLevelUtil.calculateSkillLevels(baisuzhen.getLevel(), baisuzhen.getStar().doubleValue());
//            白素贞， 水漫金山Lv1减免自身收到火焰伤害10%；
            baisuzhen.addEffect(EffectType.FIRE_RESIST_BOOST_PRET, skillLevel[0] * 10, 990, baisuzhen.getId());

        }

        //初始化光环
        List<Guardian> beiyues = this.campA.stream().filter(g -> g.getName().equals("北岳大帝") && !g.isDead()).collect(Collectors.toList());
        if (Xtool.isNotNull(beiyues)) {
            Guardian beiyue = beiyues.get(0);
            int[] skillLevel = CardSkillLevelUtil.calculateSkillLevels(beiyue.getLevel(), beiyue.getStar().doubleValue());
            if (skillLevel[1] > 0) {
//                五岳庇护Lv1光环-减少我方全体受到的火焰伤害20点
                for (Guardian guardian : this.campA) {
                    guardian.addEffect(EffectType.FIRE_RESIST_BOOST, skillLevel[0] * 20, 990, guardian.getId());
                }
            }

        }
        //初始化光环
        List<Guardian> beiyues2 = this.campB.stream().filter(g -> g.getName().equals("北岳大帝") && !g.isDead()).collect(Collectors.toList());
        if (Xtool.isNotNull(beiyues2)) {
            Guardian beiyue = beiyues2.get(0);
            int[] skillLevel = CardSkillLevelUtil.calculateSkillLevels(beiyue.getLevel(), beiyue.getStar().doubleValue());
            if (skillLevel[1] > 0) {
//                五岳庇护Lv1光环-减少我方全体受到的火焰伤害20点
                for (Guardian guardian : this.campB) {
                    guardian.addEffect(EffectType.FIRE_RESIST_BOOST, skillLevel[0] * 20, 990, guardian.getId());
                }
            }

        }

        List<Guardian> qumozhenjuns = this.campB.stream().filter(g -> g.getName().equals("驱魔真君") && !g.isDead()).collect(Collectors.toList());
        if (Xtool.isNotNull(qumozhenjuns)) {
            Guardian qumozhenjun = qumozhenjuns.get(0);
            int[] skillLevel = CardSkillLevelUtil.calculateSkillLevels(qumozhenjun.getLevel(), qumozhenjun.getStar().doubleValue());
//            顽强体魄Lv1受到治疗的效果提升10%。
            if (skillLevel[1] > 0) {
                qumozhenjun.addEffect(EffectType.HEAL_BOOST_PRET, skillLevel[1] * 10, 990, qumozhenjun.getId());

            }
        }

        List<Guardian> qumozhenjun2 = this.campA.stream().filter(g -> g.getName().equals("驱魔真君") && !g.isDead()).collect(Collectors.toList());
        if (Xtool.isNotNull(qumozhenjun2)) {
            Guardian qumozhenjun = qumozhenjun2.get(0);
            int[] skillLevel = CardSkillLevelUtil.calculateSkillLevels(qumozhenjun.getLevel(), qumozhenjun.getStar().doubleValue());
//            顽强体魄Lv1受到治疗的效果提升10%。
            if (skillLevel[1] > 0) {
                qumozhenjun.addEffect(EffectType.HEAL_BOOST_PRET, skillLevel[1] * 10, 990, qumozhenjun.getId());

            }
        }

        List<Guardian> jingjiashens = this.campB.stream().filter(g -> g.getName().equals("金甲神") && !g.isDead()).collect(Collectors.toList());
        if (Xtool.isNotNull(jingjiashens)) {
            Guardian jingjiashen = jingjiashens.get(0);
            int[] skillLevel = CardSkillLevelUtil.calculateSkillLevels(jingjiashen.getLevel(), jingjiashen.getStar().doubleValue());
//            顽强体魄Lv1受到治疗的效果提升10%。
            jingjiashen.addEffect(EffectType.HEAL_BOOST_PRET, skillLevel[0] * 5, 990, jingjiashen.getId());
        }

        List<Guardian> jingjiashens2 = this.campA.stream().filter(g -> g.getName().equals("金甲神") && !g.isDead()).collect(Collectors.toList());
        if (Xtool.isNotNull(jingjiashens2)) {
            Guardian jingjiashen = jingjiashens2.get(0);
            int[] skillLevel = CardSkillLevelUtil.calculateSkillLevels(jingjiashen.getLevel(), jingjiashen.getStar().doubleValue());
//            顽强体魄Lv1受到治疗的效果提升10%。
            jingjiashen.addEffect(EffectType.HEAL_BOOST_PRET, skillLevel[0] * 5, 990, jingjiashen.getId());
        }

        List<Guardian> nezhas = this.campB.stream().filter(g -> g.getName().equals("哪吒") && !g.isDead()).collect(Collectors.toList());
        if (Xtool.isNotNull(nezhas)) {
            Guardian nezha = nezhas.get(0);
            int[] skillLevel = CardSkillLevelUtil.calculateSkillLevels(nezha.getLevel(), nezha.getStar().doubleValue());
            if (skillLevel[1] > 0) {
//                莲花圣体Lv1减免受到的火焰伤害、毒素伤害、飞弹伤害各10%；
                nezha.addEffect(EffectType.FIRE_RESIST_BOOST_PRET, skillLevel[1] * 10, 990, nezha.getId());
                nezha.addEffect(EffectType.POISON_RESIST_BOOST_PRET, skillLevel[1] * 10, 990, nezha.getId());
                nezha.addEffect(EffectType.MISSILE_RESIST_BOOST_PRET, skillLevel[1] * 10, 990, nezha.getId());
            }


        }

        List<Guardian> nezhas2 = this.campA.stream().filter(g -> g.getName().equals("哪吒") && !g.isDead()).collect(Collectors.toList());
        if (Xtool.isNotNull(nezhas2)) {
            Guardian nezha = nezhas2.get(0);
            int[] skillLevel = CardSkillLevelUtil.calculateSkillLevels(nezha.getLevel(), nezha.getStar().doubleValue());
            if (skillLevel[1] > 0) {
//                莲花圣体Lv1减免受到的火焰伤害、毒素伤害、飞弹伤害各10%；
                nezha.addEffect(EffectType.FIRE_RESIST_BOOST_PRET, skillLevel[1] * 10, 990, nezha.getId());
                nezha.addEffect(EffectType.POISON_RESIST_BOOST_PRET, skillLevel[1] * 10, 990, nezha.getId());
                nezha.addEffect(EffectType.MISSILE_RESIST_BOOST_PRET, skillLevel[1] * 10, 990, nezha.getId());
            }

        }

        //初始化光环
        List<Guardian> jiaomowans = this.campA.stream().filter(g -> g.getName().equals("蛟魔王") && !g.isDead()).collect(Collectors.toList());
        if (Xtool.isNotNull(jiaomowans)) {
            Guardian jiaomowan = jiaomowans.get(0);
            int[] skillLevel = CardSkillLevelUtil.calculateSkillLevels(jiaomowan.getLevel(), jiaomowan.getStar().doubleValue());
            if (skillLevel[1] > 0) {
//                防火阵法Lv1光环-我方全体增加火焰减伤50%；
                for (Guardian guardian : this.campA) {
                    guardian.addEffect(EffectType.FIRE_RESIST_BOOST_PRET, 50, 990, guardian.getId());
                }
            }

        }
        //初始化光环
        List<Guardian> jiaomowans2 = this.campB.stream().filter(g -> g.getName().equals("蛟魔王") && !g.isDead()).collect(Collectors.toList());
        if (Xtool.isNotNull(jiaomowans2)) {
            Guardian jiaomowan = jiaomowans2.get(0);
            int[] skillLevel = CardSkillLevelUtil.calculateSkillLevels(jiaomowan.getLevel(), jiaomowan.getStar().doubleValue());
            if (skillLevel[1] > 0) {
//               防火阵法Lv1光环-我方全体增加火焰减伤50%；
                for (Guardian guardian : this.campB) {
                    guardian.addEffect(EffectType.FIRE_RESIST_BOOST_PRET, 50, 990, guardian.getId());
                }
            }

        }
        while (currentRound < 100 && !isBattleEnd()) {
            currentRound++;
            addLog("ROUND_START",
                    null,
                    0, 0,
                    0,
                    false,
                    null,
                    0, 0,
                    0, false, null,
                    null, "回合开始");
            //1. 登场并检查阵亡替换
            checkAndReplaceGuardians();
            //2. 回合开始处理
            processRoundStartEffects();
            // 场上战斗
            if (fieldA != null && !fieldA.isDead() && fieldB != null && !fieldB.isDead()) {
                if (getSpeed(fieldA, fieldB)) {
                    processAttack(fieldA, fieldB);
                    if (!fieldB.isDead()) {
                        processAttack(fieldB, fieldA);
                    }
                } else {
                    processAttack(fieldB, fieldA);
                    if (!fieldA.isDead()) {
                        processAttack(fieldA, fieldB);
                    }
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

    // 检查并替换阵亡护法
    private void checkAndReplaceGuardians() {
        if (fieldA == null && fieldB == null) {
            initBattle();
        } else {
            if (fieldA != null && fieldA.isDead()) {
                Guardian newA = getNextGuardian(campA);
                if (newA != null) {
                    fieldA = newA;
                    fieldA.setOnField(true);
                    Map<String, TargetBattleData> multiTargetDataMap = new HashMap<>();
                    TargetBattleData data = new TargetBattleData(fieldA.getMaxHp(), fieldA.getCurrentHp(), 0, fieldA.isOnField());
                    if (fieldA.isSilence()) {
                        data.setSilence(true);
                    } else {
                        data.setSilence(false);
                    }
                    if (fieldA.isFireBoost()) {
                        data.setFireBoost(true);
                    } else {
                        data.setFireBoost(false);
                    }
                    if (fieldA.isPoison()) {
                        data.setPoison(true);
                    } else {
                        data.setPoison(false);
                    }
                    if (fieldA.isStunned()) {
                        data.setStunned(true);
                    } else {
                        data.setStunned(false);
                    }
                    multiTargetDataMap.put(fieldA.getId(), data);
                    addLogEnter(fieldA.getFlyup(), "UNIT_ENTER",
                            fieldA.getId(),
                            fieldA.getMaxHp(), fieldA.getCurrentHp(),
                            fieldA.isOnField(),
                            multiTargetDataMap,
                            fieldA.getName() + "登场");

                    triggerOnEnterSkills(fieldA);
                    triggerOnEnterSkills2(fieldA);
                }
            }

            if (fieldB != null && fieldB.isDead()) {
                Guardian newB = getNextGuardian(campB);
                if (newB != null) {
                    fieldB = newB;
                    fieldB.setOnField(true);
                    Map<String, TargetBattleData> multiTargetDataMap = new HashMap<>();
                    TargetBattleData data = new TargetBattleData(fieldB.getMaxHp(), fieldB.getCurrentHp(), 0, fieldB.isOnField());
                    if (fieldB.isSilence()) {
                        data.setSilence(true);
                    } else {
                        data.setSilence(false);
                    }
                    if (fieldB.isFireBoost()) {
                        data.setFireBoost(true);
                    } else {
                        data.setFireBoost(false);
                    }
                    if (fieldB.isPoison()) {
                        data.setPoison(true);
                    } else {
                        data.setPoison(false);
                    }
                    if (fieldB.isStunned()) {
                        data.setStunned(true);
                    } else {
                        data.setStunned(false);
                    }
                    multiTargetDataMap.put(fieldB.getId(), data);
                    addLogEnter(fieldB.getFlyup(), "UNIT_ENTER",
                            fieldB.getId(),
                            fieldB.getMaxHp(), fieldB.getCurrentHp(),
                            fieldB.isOnField(),
                            multiTargetDataMap,
                            fieldB.getName() + "登场");

                    triggerOnEnterSkills(fieldB);
                    triggerOnEnterSkills2(fieldB);
                }
            }
        }

    }

    // 处理回合结束效果
    private void processRoundEndEffects() {
        // 托塔天王仙塔庇护
        if (fieldA != null && !fieldA.isDead() && fieldA.getName().equals("托塔天王")) {
            int[] skillLevel = CardSkillLevelUtil.calculateSkillLevels(fieldA.getLevel(), fieldA.getStar().doubleValue());
            if (skillLevel[1] > 0) {
                int heal = 25 * skillLevel[1];
                fieldA.setCurrentHp(fieldA.getCurrentHp() + heal);
                addLog("仙塔庇护",
                        fieldA.getId(),
                        fieldA.getMaxHp(), fieldA.getCurrentHp(),
                        heal,
                        fieldA.isOnField(),
                        fieldA.getId(),
                        fieldA.getMaxHp(), fieldA.getCurrentHp(),
                        heal, fieldA.isOnField(), EffectType.HEAL,
                        DamageType.MAGIC,
                        "+" + heal);
            }

        }

        if (fieldB != null && !fieldB.isDead() && fieldB.getName().equals("托塔天王")) {
            int[] skillLevel = CardSkillLevelUtil.calculateSkillLevels(fieldB.getLevel(), fieldB.getStar().doubleValue());
            if (skillLevel[1] > 0) {
                int heal = 25 * skillLevel[1];
                fieldB.setCurrentHp(fieldB.getCurrentHp() + heal);

                addLog("仙塔庇护",
                        fieldB.getId(),
                        fieldB.getMaxHp(), fieldB.getCurrentHp(),
                        heal,
                        fieldB.isOnField(),
                        fieldB.getId(),
                        fieldB.getMaxHp(), fieldB.getCurrentHp(),
                        heal, fieldB.isOnField(), EffectType.HEAL,
                        DamageType.MAGIC,
                        "+" + heal);
            }

        }
        List<Guardian> allUnits = new ArrayList<>();
        allUnits.addAll(campA);
        allUnits.addAll(campB);

        List<Guardian> aliveUnits = allUnits.stream()
                .collect(Collectors.toList());
        Map<String, TargetBattleData> multiTargetDataMap = new HashMap<>();
        for (Guardian guardian : aliveUnits) {
            guardian.onRoundEndBuffTick();
            TargetBattleData data = new TargetBattleData(guardian.getMaxHp(), guardian.getCurrentHp(), 0, guardian.isOnField());
            if (guardian.isSilence()) {
                data.setSilence(true);
            } else {
                data.setSilence(false);
            }
            if (guardian.isFireBoost()) {
                data.setFireBoost(true);
            } else {
                data.setFireBoost(false);
            }
            if (guardian.isPoison()) {
                data.setPoison(true);
            } else {
                data.setPoison(false);
            }
            if (guardian.isStunned()) {
                data.setStunned(true);
            } else {
                data.setStunned(false);
            }
            multiTargetDataMap.put(guardian.getId(), data);
        }
        // 中毒伤害日志（批量）
        addMultiTargetLog("BUFF_END",
                null, 0,
                0, false,
                multiTargetDataMap, null,
                null,
                "回合结束结算buff");
    }

    // 处理攻击流程
    private void processAttack(Guardian attacker, Guardian defender) {
        if (attacker.isStunned()) {
            return;
        }

        // 攻击前技能触发
        triggerPreAttackSkills(attacker, defender);

        if (defender.isDead()) return;


        // 1. 计算所有中毒效果的总伤害（累加 POISON 类型的 value）
        int totalPoisonDamage = attacker.getAttack();
        // 2. 计算毒抗相关（直接基于你现有 EffectInstance 计算，不新增 Guardian 方法）
        // 物理攻击增益：所有 POISON_RESIST 类型效果的 value 总和
        int resistUp = calculateTotalVaule(attacker, EffectType.ATTACK_UP);
        // 物理攻击增益百分比：所有 POISON_RESIST 类型效果的 value 乘积
        double resistUpPret = calculateTotalUpPretVaule(attacker, EffectType.ATTACK_UP_PRET);
        // 物理攻击降低：所有 POISON_RESIST_DOWN 类型效果的 value 总和
        int resistDown = calculateTotalVaule(attacker, EffectType.ATTACK_DOWN);
        // 物理攻击降低百分比：所有 POISON_RESIST 类型效果的 value 乘积
        double resistDownPret = calculateTotalDownPretVaule(attacker, EffectType.ATTACK_DOWN_PRET);


        // 2. 计算毒抗相关（直接基于你现有 EffectInstance 计算，不新增 Guardian 方法）
        // 物理抗性增益：所有 POISON_RESIST 类型效果的 value 总和
        int targetUp = calculateTotalVaule(defender, EffectType.ATTACK_RESIST_BOOST);
        //  物理抗性增益百分比：所有 POISON_RESIST 类型效果的 value 乘积
        double targetUpPret = calculateTotalDownPretVaule(defender, EffectType.ATTACK_RESIST_BOOST_PRET);
        // 物理抗性降低：所有 POISON_RESIST_DOWN 类型效果的 value 总和
        int targetDown = calculateTotalVaule(defender, EffectType.ATTACK_RESIST_DOWN);
        // 物理抗性降低百分比：所有 POISON_RESIST 类型效果的 value 乘积
        double targetDownPret = calculateTotalUpPretVaule(defender, EffectType.ATTACK_RESIST_DOWN_PRET);
        // 最终（仅基于 buff 计算，无新增方法）

        int burnDamage = (int) (totalPoisonDamage * resistUpPret * resistDownPret * targetUpPret * targetDownPret
                + (resistUp - resistDown + attacker.getWlAtk() - defender.getWlDef() - targetUp + targetDown));
        // 物理攻击
        if (burnDamage < 0) {
            burnDamage = 0;
        }


        defender.setCurrentHp(defender.getCurrentHp() - burnDamage);
        addLog("NORMAL_ATTACK",
                attacker.getId(),
                attacker.getMaxHp(), attacker.getCurrentHp(),
                0,
                attacker.isOnField(),
                defender.getId(),
                defender.getMaxHp(), defender.getCurrentHp(),
                burnDamage, defender.isOnField(),
                EffectType.DAMAGE, DamageType.PHYSICAL,
                "-" + defender.getName());
        // 检查阵亡
        if (defender.getCurrentHp() <= 0) {
            defender.setDead(true);
            defender.setOnField(false);
            addLog("UNIT_DEATH",
                    defender.getId(),
                    defender.getMaxHp(), 0,
                    0,
                    defender.isOnField(),
                    defender.getId(),
                    defender.getMaxHp(), 0,
                    0, defender.isOnField(),
                    null, null,
                    defender.getName() + "阵亡");

            // 触发死亡相关技能
            triggerOnDeathSkills(defender);
        }
        // 触发受击技能
        triggerOnAttackedSkills(defender, attacker);

        // 攻击后技能触发
        triggerPostAttackSkills(attacker, defender);


    }

    // 触发登场技能
//   TODO  登场释放 大于速度 大于厂下速度 大于挨打释放 大于厂下后手排序队列释放
    private void triggerOnEnterSkills(Guardian guardian) {
        int[] skillLevel = CardSkillLevelUtil.calculateSkillLevels(guardian.getLevel(), guardian.getStar().doubleValue());

        //TODO 登场触发技能
        switch (guardian.getName()) {
            case "黑山老妖":
//                百毒感染Lv1登场时令敌方全体中毒，每回合损失40；
                List<Guardian> offFieldEnemies = guardian.getCamp() == Camp.A ?
                        campB.stream().filter(g -> !g.isDead()).collect(Collectors.toList()) :
                        campA.stream().filter(g -> !g.isDead()).collect(Collectors.toList());
                if (!offFieldEnemies.isEmpty()) {

                    Map<String, TargetBattleData> targetStatus = new HashMap<>();
                    for (Guardian g : offFieldEnemies) {
                        int totalPoisonDamage = 40 * skillLevel[0];
                        // 1. 计算所有中毒效果的总伤害（累加 POISON 类型的 value）
                        // 2. 计算毒抗相关（直接基于你现有 EffectInstance 计算，不新增 Guardian 方法）
                        // 中毒增益：所有 POISON_RESIST 类型效果的 value 总和
                        int resistUp = calculateTotalVaule(guardian, EffectType.POISON_BOOST);
                        // 中毒增益百分比：所有 POISON_RESIST 类型效果的 value 乘积
                        double resistUpPret = calculateTotalUpPretVaule(guardian, EffectType.POISON_BOOST_PRET);
                        // 中毒降低：所有 POISON_RESIST_DOWN 类型效果的 value 总和
                        int resistDown = calculateTotalVaule(guardian, EffectType.POISON_DOWN);
                        // 中毒降低百分比：所有 POISON_RESIST 类型效果的 value 乘积
                        double resistDownPret = calculateTotalDownPretVaule(guardian, EffectType.POISON_DOWN_PRET);

                        int poisonValue = (int) (totalPoisonDamage * resistUpPret * resistDownPret + (resistUp + guardian.getDsAtk() - resistDown));
                        // 计算本次中毒伤害
                        g.addEffect(EffectType.POISON, poisonValue + guardian.getDsAtk(), 99, guardian.getId());
                        TargetBattleData data = new TargetBattleData(g.getMaxHp(), g.getCurrentHp(), poisonValue, g.isOnField());
                        targetStatus.put(g.getId(), data);
                    }
                    // 中毒伤害日志（批量）
                    addMultiTargetLog("百毒感染",
                            guardian.getId(),
                            guardian.getMaxHp(),
                            guardian.getCurrentHp(),
                            guardian.isOnField(),
                            targetStatus,
                            EffectType.POISON,
                            DamageType.POISON,
                            "对全体造成中毒效果");
                }
                break;
            case "王天君":
                if (1 == 1) {
                    List<Guardian> enemies = guardian.getCamp() == Camp.A ?
                            campB.stream().filter(g -> !g.isDead()).collect(Collectors.toList()) :
                            campA.stream().filter(g -> !g.isDead()).collect(Collectors.toList());
                    if (!enemies.isEmpty()) {

                        Map<String, TargetBattleData> targetStatus = new HashMap<>();

                        enemies.forEach(g -> {
                            int totalPoisonDamage = 39 * skillLevel[0];;
                            // 1. 计算所有中毒效果的总伤害（累加 POISON 类型的 value）
                            // 2. 计算毒抗相关（直接基于你现有 EffectInstance 计算，不新增 Guardian 方法）
                            // 中毒增益：所有 POISON_RESIST 类型效果的 value 总和
                            int resistUp = calculateTotalVaule(guardian, EffectType.MISSILE_BOOST);
                            // 中毒增益百分比：所有 POISON_RESIST 类型效果的 value 乘积
                            double resistUpPret = calculateTotalUpPretVaule(guardian, EffectType.MISSILE_BOOST_PRET);
                            // 中毒降低：所有 POISON_RESIST_DOWN 类型效果的 value 总和
                            int resistDown = calculateTotalVaule(guardian, EffectType.MISSILE_DOWN);
                            // 中毒降低百分比：所有 POISON_RESIST 类型效果的 value 乘积
                            double resistDownPret = calculateTotalDownPretVaule(guardian, EffectType.MISSILE_DOWN_PRET);

                            int poisonValue = (int) (totalPoisonDamage * resistUpPret * resistDownPret + (resistUp + guardian.getDsAtk() - resistDown));
                            g.addEffect(EffectType.MISSILE_RESIST_DOWN, poisonValue, 6, guardian.getId());
                            TargetBattleData data = new TargetBattleData(g.getMaxHp(), g.getCurrentHp(), totalPoisonDamage, g.isOnField());
                            targetStatus.put(g.getId(), data);
                        });

                        // 中毒伤害日志（批量）
                        addMultiTargetLog("洪水法阵",
                                guardian.getId(),
                                guardian.getMaxHp(),
                                guardian.getCurrentHp(),
                                guardian.isOnField(),
                                targetStatus,
                                EffectType.MAX_HP_DOWN,
                                DamageType.BUFF,
                                "洪水法阵");
                    }
                }
                break;
            case "大鹏金翅雕":
//                大鹏金翅雕，鹏程万里Lv1登场时提高我方全体妖界生物的速度130点
                if (1 == 1) {
                    List<Guardian> enemies = guardian.getCamp() == Camp.A ?
                            campA.stream().filter(g -> !g.isDead() && g.getRace() == Race.DEMON).collect(Collectors.toList()) :
                            campB.stream().filter(g -> !g.isDead() && g.getRace() == Race.DEMON).collect(Collectors.toList());
                    if (!enemies.isEmpty()) {

                        Map<String, TargetBattleData> targetStatus = new HashMap<>();

                        enemies.forEach(g -> {
                            int totalPoisonDamage = 130 * skillLevel[0];
                            // 1. 计算所有中毒效果的总伤害（累加 POISON 类型的 value）
                            // 2. 计算毒抗相关（直接基于你现有 EffectInstance 计算，不新增 Guardian 方法）
                            // 中毒增益：所有 POISON_RESIST 类型效果的 value 总和
                            double resistUpPret = calculateTotalUpPretVaule(guardian, EffectType.SPEED_UP_PRET);
                            int speed = (int) (totalPoisonDamage * resistUpPret);
                            TargetBattleData data = new TargetBattleData(g.getMaxHp(), g.getCurrentHp(), speed, g.isOnField());
                            targetStatus.put(g.getId(), data);
                        });

                        // 中毒伤害日志（批量）
                        addMultiTargetLog("鹏程万里",
                                guardian.getId(),
                                guardian.getMaxHp(),
                                guardian.getCurrentHp(),
                                guardian.isOnField(),
                                targetStatus,
                                EffectType.SPEED_UP,
                                DamageType.BUFF,
                                "提高我方全体妖界生物的速度");
                    }
                }
                break;
            case "白天君":
//
                if (1 == 1) {
                    //烈焰阵Lv1登场时，令敌方全体收到火焰伤害420点；
                    Map<String, TargetBattleData> deadUnits = new HashMap<>();
                    List<Guardian> deadGuardians = new ArrayList<>();

                    Map<String, TargetBattleData> targetStatus = new HashMap<>();
                    List<Guardian> enemies = guardian.getCamp() == Camp.A ?
                            campB.stream().filter(g -> !g.isDead()).collect(Collectors.toList()) :
                            campA.stream().filter(g -> !g.isDead()).collect(Collectors.toList());
                    if (Xtool.isNotNull(enemies)) {
                        enemies.forEach(g -> {
                            // 1. 计算所有中毒效果的总伤害（累加 POISON 类型的 value）
                            int totalPoisonDamage = 420 * skillLevel[0];
                            // 2. 计算毒抗相关（直接基于你现有 EffectInstance 计算，不新增 Guardian 方法）
                            // 火伤增益：所有 POISON_RESIST 类型效果的 value 总和
                            int resistUp = calculateTotalVaule(guardian, EffectType.FIRE_BOOST);
                            // 火伤增益百分比：所有 POISON_RESIST 类型效果的 value 乘积
                            double resistUpPret = calculateTotalUpPretVaule(guardian, EffectType.FIRE_BOOST_PRET);
                            // 火伤降低：所有 POISON_RESIST_DOWN 类型效果的 value 总和
                            int resistDown = calculateTotalVaule(guardian, EffectType.FIRE_DOWN);
                            // 火伤降低百分比：所有 POISON_RESIST 类型效果的 value 乘积
                            double resistDownPret = calculateTotalDownPretVaule(guardian, EffectType.FIRE_DOWN_PRET);


                            // 2. 计算毒抗相关（直接基于你现有 EffectInstance 计算，不新增 Guardian 方法）
                            // 火抗增益：所有 POISON_RESIST 类型效果的 value 总和
                            int targetUp = calculateTotalVaule(g, EffectType.FIRE_RESIST_BOOST);
                            // 火抗增益百分比：所有 POISON_RESIST 类型效果的 value 乘积
                            double targetUpPret = calculateTotalDownPretVaule(g, EffectType.FIRE_RESIST_BOOST_PRET);
                            // 火抗降低：所有 POISON_RESIST_DOWN 类型效果的 value 总和
                            int targetDown = calculateTotalVaule(g, EffectType.FIRE_RESIST_DOWN);
                            // 火抗降低百分比：所有 POISON_RESIST 类型效果的 value 乘积
                            double targetDownPret = calculateTotalUpPretVaule(g, EffectType.FIRE_RESIST_DOWN_PRET);
                            // 最终（仅基于 buff 计算，无新增方法）

                            int burnDamage = (int) (totalPoisonDamage * resistUpPret * resistDownPret * targetUpPret * targetDownPret
                                    + (resistUp - resistDown + guardian.getHyAtk() - g.getHyDef() - targetUp + targetDown));

                            if (burnDamage < 0) {
                                burnDamage = 0;
                            }

                            // 4. 扣除伤害
                            g.setCurrentHp(g.getCurrentHp() - burnDamage);
                            TargetBattleData data = new TargetBattleData(g.getMaxHp(), g.getCurrentHp(), burnDamage, g.isOnField());
                            targetStatus.put(g.getId(), data);
                            if (g.isDead()) {

                                deadGuardians.add(g);
                                deadUnits.put(g.getId(), data);
                            }
                        });

                        // 单条日志记录多目标
                        addMultiTargetLog("烈焰阵",
                                guardian.getId(),
                                guardian.getMaxHp(),
                                guardian.getCurrentHp(),
                                guardian.isOnField(),
                                targetStatus,
                                EffectType.FIRE_DAMAGE,
                                DamageType.FIRE,
                                "敌方全体收到火焰伤害");
                        // 死亡日志
                        if (!deadUnits.isEmpty()) {
                            addMultiTargetLog("UNIT_DEATH",
                                    null,
                                    0,
                                    0,
                                    false,
                                    deadUnits,
                                    null,
                                    null,
                                    "死亡");
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
                }
                break;
            case "天狗":
                if (1 == 1) {
//                    天兆神火Lv1登场时对对方全体造成35点火焰伤害；
                    Map<String, TargetBattleData> deadUnits = new HashMap<>();
                    List<Guardian> deadGuardians = new ArrayList<>();
                    int lavaDamage = 35 * skillLevel[0];

                    Map<String, TargetBattleData> targetStatus = new HashMap<>();
                    List<Guardian> enemies = guardian.getCamp() == Camp.A ?
                            campB.stream().filter(g -> !g.isDead()).collect(Collectors.toList()) :
                            campA.stream().filter(g -> !g.isDead()).collect(Collectors.toList());
                    enemies.forEach(g -> {
                        // 1. 计算所有中毒效果的总伤害（累加 POISON 类型的 value）
                        int totalPoisonDamage = 236 * skillLevel[1];
                        // 2. 计算毒抗相关（直接基于你现有 EffectInstance 计算，不新增 Guardian 方法）
                        // 火伤增益：所有 POISON_RESIST 类型效果的 value 总和
                        int resistUp = calculateTotalVaule(guardian, EffectType.FIRE_BOOST);
                        // 火伤增益百分比：所有 POISON_RESIST 类型效果的 value 乘积
                        double resistUpPret = calculateTotalUpPretVaule(guardian, EffectType.FIRE_BOOST_PRET);
                        // 火伤降低：所有 POISON_RESIST_DOWN 类型效果的 value 总和
                        int resistDown = calculateTotalVaule(guardian, EffectType.FIRE_DOWN);
                        // 火伤降低百分比：所有 POISON_RESIST 类型效果的 value 乘积
                        double resistDownPret = calculateTotalDownPretVaule(guardian, EffectType.FIRE_DOWN_PRET);


                        // 2. 计算毒抗相关（直接基于你现有 EffectInstance 计算，不新增 Guardian 方法）
                        // 火抗增益：所有 POISON_RESIST 类型效果的 value 总和
                        int targetUp = calculateTotalVaule(g, EffectType.FIRE_RESIST_BOOST);
                        // 火抗增益百分比：所有 POISON_RESIST 类型效果的 value 乘积
                        double targetUpPret = calculateTotalDownPretVaule(g, EffectType.FIRE_RESIST_BOOST_PRET);
                        // 火抗降低：所有 POISON_RESIST_DOWN 类型效果的 value 总和
                        int targetDown = calculateTotalVaule(g, EffectType.FIRE_RESIST_DOWN);
                        // 火抗降低百分比：所有 POISON_RESIST 类型效果的 value 乘积
                        double targetDownPret = calculateTotalUpPretVaule(g, EffectType.FIRE_RESIST_DOWN_PRET);
                        // 最终（仅基于 buff 计算，无新增方法）

                        int burnDamage = (int) (totalPoisonDamage * resistUpPret * resistDownPret * targetUpPret * targetDownPret
                                + (resistUp - resistDown + guardian.getHyAtk() - g.getHyDef() - targetUp + targetDown));

                        if (burnDamage < 0) {
                            burnDamage = 0;
                        }

                        // 4. 扣除伤害
                        g.setCurrentHp(g.getCurrentHp() - burnDamage);
                        TargetBattleData data = new TargetBattleData(g.getMaxHp(), g.getCurrentHp(), burnDamage, g.isOnField());
                        targetStatus.put(g.getId(), data);
                        if (g.isDead()) {

                            deadUnits.put(g.getId(), data);
                            deadGuardians.add(g);
                        }
                    });
                    // 单条日志记录多目标
                    addMultiTargetLog("天兆神火",
                            guardian.getId(),
                            guardian.getMaxHp(),
                            guardian.getCurrentHp(),
                            guardian.isOnField(),
                            targetStatus,
                            EffectType.FIRE_DAMAGE,
                            DamageType.FIRE,
                            "对方全体造成点火焰伤害");
                    // 死亡日志
                    if (!deadUnits.isEmpty()) {
                        addMultiTargetLog("UNIT_DEATH",
                                null,
                                0,
                                0,
                                false,
                                deadUnits,
                                null,
                                null,
                                "死亡");
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
                        enemy.addEffect(EffectType.SILENCE, 0, 99, guardian.getId());
                        addLog("禁术咒",
                                guardian.getId(),
                                guardian.getMaxHp(),
                                guardian.getCurrentHp(),
                                0,
                                guardian.isOnField(),
                                enemy.getId(),
                                enemy.getMaxHp(),
                                enemy.getCurrentHp(),
                                0,
                                enemy.isOnField(),
                                EffectType.SILENCE,
                                DamageType.BUFF,
                                "沉默99回合");
                    }
                }
                break;
            case "紫薇大帝":
                // ，北极剑意Lv1登场时对场上敌方造成最大生命4%的真实伤害；
                if (1 == 1) {
                    Guardian enemy = guardian.getCamp() == Camp.A ? fieldB : fieldA;
                    if (enemy != null) {
                        int damage = (int) (enemy.getCurrentHp() * 0.04 * skillLevel[0]);
                        enemy.setCurrentHp(enemy.getCurrentHp() - damage);
                        addLog("北极剑意",
                                guardian.getId(),
                                guardian.getMaxHp(),
                                guardian.getCurrentHp(),
                                0,
                                guardian.isOnField(),
                                enemy.getId(),
                                enemy.getMaxHp(),
                                enemy.getCurrentHp(),
                                damage,
                                enemy.isOnField(),
                                EffectType.DAMAGE,
                                DamageType.TRUE,
                                "-" + damage);
                    }
                }
                break;
            case "齐天大圣":
                // 大圣降临：回复自身20%生命
                int heal = (int) (guardian.getMaxHp() * 0.2 * skillLevel[0]);
                guardian.setCurrentHp(guardian.getCurrentHp() + heal);
                addLog("大圣降临",
                        guardian.getId(),
                        guardian.getMaxHp(),
                        guardian.getCurrentHp(),
                        heal,
                        guardian.isOnField(),
                        guardian.getId(),
                        guardian.getMaxHp(),
                        guardian.getCurrentHp(),
                        heal,
                        guardian.isOnField(),
                        EffectType.HEAL,
                        DamageType.BUFF,
                        "+" + heal);
                break;

            case "烛龙":
                // 致命衰竭：登场目标攻击减少10%
                if (skillLevel[1] > 0) {
                    Guardian target = guardian.getCamp() == Camp.A ? fieldB : fieldA;
                    if (target != null) {
                        int weaken = (int) (target.getAttack() * 0.1 * skillLevel[1]);
                        target.setAttack(target.getAttack() - weaken);
                        addLog("致命衰竭",
                                guardian.getId(),
                                guardian.getMaxHp(),
                                guardian.getCurrentHp(),
                                0,
                                guardian.isOnField(),
                                target.getId(),
                                target.getMaxHp(),
                                target.getCurrentHp(),
                                weaken,
                                target.isOnField(),
                                EffectType.ATTACK_DOWN,
                                DamageType.BUFF,
                                "攻击降低" + skillLevel[0] + "0%");
                    }
                }
                break;
            case "怨书生":
                // 每当敌人登场，降低其力量15点。
//                每当敌人登场：只要对面新出来一个敌人（召唤、进场、回合开始上场等），就触发一次。
//                降低其力量 15 点：
//                「力量」= 攻击力 / 物理输出能力
//                「其」= 刚登场的那个敌人
//                直接永久或暂时减少 15 点攻击力
                if (1 == 1) {
                    Guardian target = guardian.getCamp() == Camp.A ? fieldB : fieldA;
                    if (target != null) {
                        int weaken = target.getAttack() - 15 * skillLevel[0];
                        target.setAttack(target.getAttack() - weaken);
                        addLog("怨气冲天",
                                guardian.getId(),
                                guardian.getMaxHp(),
                                guardian.getCurrentHp(),
                                0,
                                guardian.isOnField(),
                                target.getId(),
                                target.getMaxHp(),
                                target.getCurrentHp(),
                                weaken,
                                target.isOnField(),
                                EffectType.ATTACK_DOWN,
                                DamageType.BUFF,
                                "攻击降低" + 15 * skillLevel[0]);
                    }
                }
                break;
        }
    }

    private void triggerOnEnterSkills2(Guardian guardian) {
        int[] skillLevel = CardSkillLevelUtil.calculateSkillLevels(guardian.getLevel(), guardian.getStar().doubleValue());
        //TODO 对方登场优先在场先触发技能
        if (1 == 1) {
            Guardian enemy = guardian.getCamp() == Camp.A ? fieldB : fieldA;
            if (!enemy.isSilence() && enemy != null) {
                switch (enemy.getName()) {
                    case "托塔天王":
                        // 镇妖塔：对敌方场上造成飞弹伤害
                        if (guardian != null) {
                            // 1. 计算所有中毒效果的总伤害（累加 POISON 类型的 value）
                            int totalPoisonDamage = 69 * skillLevel[1];
                            // 2. 计算毒抗相关（直接基于你现有 EffectInstance 计算，不新增 Guardian 方法）
                            // 飞弹增益：所有 POISON_RESIST 类型效果的 value 总和
                            int resistUp = calculateTotalVaule(guardian, EffectType.MISSILE_BOOST);
                            // 飞弹增益百分比：所有 POISON_RESIST 类型效果的 value 乘积
                            double resistUpPret = calculateTotalUpPretVaule(guardian, EffectType.MISSILE_BOOST_PRET);
                            // 飞弹降低：所有 POISON_RESIST_DOWN 类型效果的 value 总和
                            int resistDown = calculateTotalVaule(guardian, EffectType.MISSILE_DOWN);
                            // 飞弹降低百分比：所有 POISON_RESIST 类型效果的 value 乘积
                            double resistDownPret = calculateTotalDownPretVaule(guardian, EffectType.MISSILE_DOWN_PRET);


                            // 2. 计算毒抗相关（直接基于你现有 EffectInstance 计算，不新增 Guardian 方法）
                            // 弹抗增益：所有 POISON_RESIST 类型效果的 value 总和
                            int targetUp = calculateTotalVaule(enemy, EffectType.MISSILE_RESIST_BOOST);
                            // 弹抗增益百分比：所有 POISON_RESIST 类型效果的 value 乘积
                            double targetUpPret = calculateTotalDownPretVaule(enemy, EffectType.MISSILE_RESIST_BOOST_PRET);
                            // 弹抗降低：所有 POISON_RESIST_DOWN 类型效果的 value 总和
                            int targetDown = calculateTotalVaule(enemy, EffectType.MISSILE_RESIST_DOWN);
                            // 弹抗降低百分比：所有 POISON_RESIST 类型效果的 value 乘积
                            double targetDownPret = calculateTotalUpPretVaule(enemy, EffectType.MISSILE_RESIST_DOWN_PRET);
                            // 最终（仅基于 buff 计算，无新增方法）

                            int burnDamage = (int) (totalPoisonDamage * resistUpPret * resistDownPret * targetUpPret * targetDownPret
                                    + (resistUp - resistDown + guardian.getFdAtk() - enemy.getFdDef() - targetUp + targetDown));

                            if (burnDamage < 0) {
                                burnDamage = 0;
                            }

                            // 4. 扣除伤害
                            enemy.setCurrentHp(enemy.getCurrentHp() - burnDamage);
                            addLog("镇妖塔",
                                    enemy.getId(),
                                    enemy.getMaxHp(),
                                    enemy.getCurrentHp(),
                                    0,
                                    enemy.isOnField(),
                                    guardian.getId(),
                                    guardian.getMaxHp(),
                                    guardian.getCurrentHp(),
                                    burnDamage,
                                    guardian.isOnField(),
                                    EffectType.MISSILE_DAMAGE,
                                    DamageType.MISSILE,
                                    "-" + burnDamage);
                        }
                        break;
                    case "厚土娘娘":
                        // 大地净化：驱散自身减益
//                        enemy.getEffects().clear();
                        //只驱散负面buff
                        enemy.remove(EffectType.HEAL_DOWN);
                        enemy.remove(EffectType.HEAL_DOWNT_PRET);
                        enemy.remove(EffectType.ATTACK_DOWN);
                        enemy.remove(EffectType.ATTACK_DOWN_PRET);
                        enemy.remove(EffectType.POISON);
                        enemy.remove(EffectType.ATTACK_RESIST_DOWN);
                        enemy.remove(EffectType.ATTACK_RESIST_DOWN_PRET);
                        enemy.remove(EffectType.FIRE_DOWN);
                        enemy.remove(EffectType.FIRE_DOWN_PRET);
                        enemy.remove(EffectType.FIRE_RESIST_DOWN);
                        enemy.remove(EffectType.FIRE_RESIST_DOWN_PRET);
                        enemy.remove(EffectType.POISON_DOWN);
                        enemy.remove(EffectType.POISON_DOWN_PRET);
                        enemy.remove(EffectType.POISON_RESIST_DOWN);
                        enemy.remove(EffectType.POISON_RESIST_DOWN_PRET);
                        enemy.remove(EffectType.FIRE_RESIST_DOWN_PRET);
                        enemy.remove(EffectType.MISSILE_DOWN);
                        enemy.remove(EffectType.MISSILE_DOWN_PRET);
                        enemy.remove(EffectType.MISSILE_RESIST_DOWN);
                        enemy.remove(EffectType.MISSILE_RESIST_DOWN_PRET);
                        enemy.remove(EffectType.MAX_HP_DOWN_PRET);
                        enemy.remove(EffectType.SPEED_DOWN);
                        enemy.remove(EffectType.SPEED_DOWN_PRET);
                        addLog("大地净化",
                                enemy.getId(),
                                enemy.getMaxHp(),
                                enemy.getCurrentHp(),
                                0,
                                enemy.isOnField(),
                                enemy.getId(),
                                enemy.getMaxHp(),
                                enemy.getCurrentHp(),
                                0,
                                enemy.isOnField(),
                                EffectType.SILENCE_IMMUNE,
                                DamageType.MAGIC,
                                "驱散减益");
                        break;
                    case "天蓬元帅":
//                        天蓬元帅，满目桃花Lv1场上遇到女性敌人时，自身攻击降低50%，速度增加50%，持续6回合
                        enemy.addEffect(EffectType.ATTACK_DOWN_PRET, 50, 6, enemy.getId());
                        enemy.addEffect(EffectType.SPEED_UP_PRET, 50, 6, enemy.getId());
                        addLog("满目桃花",
                                enemy.getId(),
                                enemy.getMaxHp(),
                                enemy.getCurrentHp(),
                                0,
                                enemy.isOnField(),
                                enemy.getId(),
                                enemy.getMaxHp(),
                                enemy.getCurrentHp(),
                                0,
                                enemy.isOnField(),
                                null,
                                DamageType.MAGIC,
                                "攻击-50%，速度+50%");
                        break;
                }
            }
        }


        //TODO 登场，再触发场下
        processOnFieldSkills0(guardian);


    }

    // 触发攻击前技能
    private void triggerPreAttackSkills(Guardian attacker, Guardian defender) {
        int[] skillLevel = CardSkillLevelUtil.calculateSkillLevels(attacker.getLevel(), attacker.getStar().doubleValue());
        if (attacker.isSilence() || defender.isDead()) {
            return;
        }
        switch (attacker.getName()) {
            case "齐天大圣":
                // 定海神针：当前生命值6%伤害
                if (skillLevel[1] > 0) {
                    int damage = (int) (defender.getCurrentHp() * 0.06 * skillLevel[1]);
                    defender.setCurrentHp(defender.getCurrentHp() - damage);

                    addLog("定海神针",
                            attacker.getId(),
                            attacker.getMaxHp(),
                            attacker.getCurrentHp(),
                            0,
                            attacker.isOnField(),
                            defender.getId(),
                            defender.getMaxHp(),
                            defender.getCurrentHp(),
                            damage,
                            defender.isOnField(),
                            EffectType.DAMAGE,
                            DamageType.TRUE,
                            "-" + damage);
                }
                break;
            case "圣灵天将":
                // 定海神针：当前生命值6%伤害
                if (attacker.getPosition() == 1) {
//                    圣灵天将，圣灵法阵Lv1位居1号位置时，每次攻击前布置圣灵法阵，令敌我双方生命上限不会降低持续2同会；
                    //三昧真火Lv1攻击时对敌我双方所有单位造成62火焰伤害；
                    List<Guardian> allUnits = new ArrayList<>();
                    allUnits.addAll(campA);
                    allUnits.addAll(campB);

                    List<Guardian> aliveUnits = allUnits.stream()
                            .filter(g -> !g.isDead())
                            .collect(Collectors.toList());

                    if (!aliveUnits.isEmpty()) {
                        Map<String, TargetBattleData> targetStatus = new HashMap<>();
                        for (Guardian g : aliveUnits) {
                            g.addEffect(EffectType.MAX_HP_NO_DOWN, 0, 2, attacker.getId());
                            TargetBattleData data = new TargetBattleData(g.getMaxHp(), g.getCurrentHp(), 0, g.isOnField());
                            targetStatus.put(g.getId(), data);
                        }
                        addMultiTargetLog("圣灵法阵",
                                attacker.getId(),
                                attacker.getMaxHp(),
                                attacker.getCurrentHp(),
                                attacker.isOnField(),
                                targetStatus,
                                EffectType.HP_UP,
                                DamageType.BUFF,
                                "圣灵法阵");
                    }
                }
                break;
            case "王天君":
                // 斩杀：13%几率造成火焰伤害
                if (!defender.isDead() && defender.getRace() == Race.DEMON && skillLevel[1] > 0) {
                    if (ProbabilityBooleanUtils.randomByProbability(0.35 * skillLevel[1])) {
                        // 计算中毒总伤害
                        double rawPoisonDamage = defender.getCurrentHp() * 0.4;
                        // 转为整数（可根据需求选择四舍五入或直接截断）
                        int totalPoisonDamage = (int) Math.round(rawPoisonDamage); // 四舍五入
                        // 4. 扣除伤害
                        defender.setCurrentHp(defender.getCurrentHp() - totalPoisonDamage);
                        Map<String, TargetBattleData> deadUnits = new HashMap<>();
                        if (attacker.getCurrentHp() <= 0) {
                            attacker.setDead(true);
                            attacker.setOnField(false);
                            TargetBattleData data = new TargetBattleData(attacker.getMaxHp(), attacker.getCurrentHp(), totalPoisonDamage, attacker.isOnField());
                            deadUnits.put(attacker.getId(), data);
                        }
                        addLog("斩妖剑",
                                attacker.getId(),
                                attacker.getMaxHp(),
                                attacker.getCurrentHp(),
                                0,
                                attacker.isOnField(),
                                defender.getId(),
                                defender.getMaxHp(),
                                defender.getCurrentHp(),
                                totalPoisonDamage,
                                defender.isOnField(),
                                EffectType.DAMAGE,
                                DamageType.TRUE,
                                "-" + totalPoisonDamage);
                        // 死亡日志
                        if (!deadUnits.isEmpty()) {
                            addMultiTargetLog("UNIT_DEATH",
                                    null,
                                    0,
                                    0,
                                    false,
                                    deadUnits,
                                    null,
                                    null,
                                    "死亡");
                            //触发死亡技能
                            triggerOnDeathSkills(defender);

                        }
                    }
                }
                break;
            case "刑天":
                // 斩杀：13%几率造成火焰伤害
                if (ProbabilityBooleanUtils.randomByProbability(0.13 * skillLevel[0])) {
                    // 1. 计算所有中毒效果的总伤害（累加 POISON 类型的 value）
                    int totalPoisonDamage = 220 * skillLevel[1];
                    // 2. 计算毒抗相关（直接基于你现有 EffectInstance 计算，不新增 Guardian 方法）
                    // 火伤增益：所有 POISON_RESIST 类型效果的 value 总和
                    int resistUp = calculateTotalVaule(attacker, EffectType.FIRE_BOOST);
                    // 火伤增益百分比：所有 POISON_RESIST 类型效果的 value 乘积
                    double resistUpPret = calculateTotalUpPretVaule(attacker, EffectType.FIRE_BOOST_PRET);
                    // 火伤降低：所有 POISON_RESIST_DOWN 类型效果的 value 总和
                    int resistDown = calculateTotalVaule(attacker, EffectType.FIRE_DOWN);
                    // 火伤降低百分比：所有 POISON_RESIST 类型效果的 value 乘积
                    double resistDownPret = calculateTotalDownPretVaule(attacker, EffectType.FIRE_DOWN_PRET);


                    // 2. 计算毒抗相关（直接基于你现有 EffectInstance 计算，不新增 Guardian 方法）
                    // 火抗增益：所有 POISON_RESIST 类型效果的 value 总和
                    int targetUp = calculateTotalVaule(defender, EffectType.FIRE_RESIST_BOOST);
                    // 火抗增益百分比：所有 POISON_RESIST 类型效果的 value 乘积
                    double targetUpPret = calculateTotalDownPretVaule(defender, EffectType.FIRE_RESIST_BOOST_PRET);
                    // 火抗降低：所有 POISON_RESIST_DOWN 类型效果的 value 总和
                    int targetDown = calculateTotalVaule(defender, EffectType.FIRE_RESIST_DOWN);
                    // 火抗降低百分比：所有 POISON_RESIST 类型效果的 value 乘积
                    double targetDownPret = calculateTotalUpPretVaule(defender, EffectType.FIRE_RESIST_DOWN_PRET);
                    // 最终（仅基于 buff 计算，无新增方法）

                    int burnDamage = (int) (totalPoisonDamage * resistUpPret * resistDownPret * targetUpPret * targetDownPret
                            + (resistUp - resistDown + attacker.getHyAtk() - defender.getHyDef() - targetUp + targetDown));

                    if (burnDamage < 0) {
                        burnDamage = 0;
                    }

                    // 4. 扣除伤害
                    defender.setCurrentHp(defender.getCurrentHp() - burnDamage);
                    // 武圣判定
                    Map<String, TargetBattleData> deadUnits = new HashMap<>();
//                    如果目标是武圣则有几率一击必杀，替代普通攻击；0.05; // 对武圣5%一击必杀（可配置）
                    if (defender.getProfession() == Profession.WARRIOR) {
                        if (ProbabilityBooleanUtils.randomByProbability(0.05)) {
                            TargetBattleData data = new TargetBattleData(defender.getMaxHp(), defender.getCurrentHp(), defender.getCurrentHp(), defender.isOnField());
                            defender.setCurrentHp(0);
                            defender.setDead(true);
                            defender.setOnField(false);
                            deadUnits.put(defender.getId(), data);
                        }
                    }
                    addLog("斩杀",
                            attacker.getId(),
                            attacker.getMaxHp(),
                            attacker.getCurrentHp(),
                            0,
                            attacker.isOnField(),
                            defender.getId(),
                            defender.getMaxHp(),
                            defender.getCurrentHp(),
                            burnDamage,
                            defender.isOnField(),
                            EffectType.FIRE_DAMAGE,
                            DamageType.FIRE,
                            "-" + burnDamage);
                    // 死亡日志
                    if (!deadUnits.isEmpty()) {
                        addMultiTargetLog("UNIT_DEATH",
                                null,
                                0,
                                0,
                                false,
                                deadUnits,
                                null,
                                null,
                                "死亡");
                        //触发死亡技能
                        triggerOnDeathSkills(defender);

                    }
                }
                break;
            case "大鹏金翅雕":
                // 斩杀：13%几率造成火焰伤害
                if (skillLevel[1] > 0) {
                    if (ProbabilityBooleanUtils.randomByProbability(0.75)) {
                        // 1. 计算所有中毒效果的总伤害（累加 POISON 类型的 value）
                        int totalPoisonDamage = 236 * skillLevel[1];
                        // 2. 计算毒抗相关（直接基于你现有 EffectInstance 计算，不新增 Guardian 方法）
                        // 火伤增益：所有 POISON_RESIST 类型效果的 value 总和
                        int resistUp = calculateTotalVaule(attacker, EffectType.FIRE_BOOST);
                        // 火伤增益百分比：所有 POISON_RESIST 类型效果的 value 乘积
                        double resistUpPret = calculateTotalUpPretVaule(attacker, EffectType.FIRE_BOOST_PRET);
                        // 火伤降低：所有 POISON_RESIST_DOWN 类型效果的 value 总和
                        int resistDown = calculateTotalVaule(attacker, EffectType.FIRE_DOWN);
                        // 火伤降低百分比：所有 POISON_RESIST 类型效果的 value 乘积
                        double resistDownPret = calculateTotalDownPretVaule(attacker, EffectType.FIRE_DOWN_PRET);


                        // 2. 计算毒抗相关（直接基于你现有 EffectInstance 计算，不新增 Guardian 方法）
                        // 火抗增益：所有 POISON_RESIST 类型效果的 value 总和
                        int targetUp = calculateTotalVaule(defender, EffectType.FIRE_RESIST_BOOST);
                        // 火抗增益百分比：所有 POISON_RESIST 类型效果的 value 乘积
                        double targetUpPret = calculateTotalDownPretVaule(defender, EffectType.FIRE_RESIST_BOOST_PRET);
                        // 火抗降低：所有 POISON_RESIST_DOWN 类型效果的 value 总和
                        int targetDown = calculateTotalVaule(defender, EffectType.FIRE_RESIST_DOWN);
                        // 火抗降低百分比：所有 POISON_RESIST 类型效果的 value 乘积
                        double targetDownPret = calculateTotalUpPretVaule(defender, EffectType.FIRE_RESIST_DOWN_PRET);
                        // 最终（仅基于 buff 计算，无新增方法）

                        int burnDamage = (int) (totalPoisonDamage * resistUpPret * resistDownPret * targetUpPret * targetDownPret
                                + (resistUp - resistDown + attacker.getHyAtk() - defender.getHyDef() - targetUp + targetDown));

                        if (burnDamage < 0) {
                            burnDamage = 0;
                        }

                        // 4. 扣除伤害
                        defender.setCurrentHp(defender.getCurrentHp() - burnDamage);
                        // 武圣判定
                        Map<String, TargetBattleData> deadUnits = new HashMap<>();
//                    如果目标是武圣则有几率一击必杀，替代普通攻击；0.05; // 对武圣5%一击必杀（可配置）
                        if (defender.getProfession() == Profession.GOD) {
                            if (ProbabilityBooleanUtils.randomByProbability(0.05)) {
                                TargetBattleData data = new TargetBattleData(defender.getMaxHp(), defender.getCurrentHp(), defender.getCurrentHp(), defender.isOnField());
                                defender.setCurrentHp(0);
                                defender.setDead(true);
                                defender.setOnField(false);
                                deadUnits.put(defender.getId(), data);
                            }
                        }
                        addLog("屠杀",
                                attacker.getId(),
                                attacker.getMaxHp(),
                                attacker.getCurrentHp(),
                                0,
                                attacker.isOnField(),
                                defender.getId(),
                                defender.getMaxHp(),
                                defender.getCurrentHp(),
                                burnDamage,
                                defender.isOnField(),
                                EffectType.FIRE_DAMAGE,
                                DamageType.FIRE,
                                "-" + burnDamage);
                        // 死亡日志
                        if (!deadUnits.isEmpty()) {
                            addMultiTargetLog("UNIT_DEATH",
                                    null,
                                    0,
                                    0,
                                    false,
                                    deadUnits,
                                    null,
                                    null,
                                    "死亡");
                            //触发死亡技能
                            triggerOnDeathSkills(defender);

                        }
                    }
                }
                break;
            case "九尾狐":
                // 欺诈宝珠Lv1每次攻击有50%几率令敌方场上英雄定身，持续2回合
                if (ProbabilityBooleanUtils.randomByProbability(0.5) && defender != null && !defender.isDead()) {
                    defender.addEffect(EffectType.STUN, 0, 2, defender.getId());
                    addLog("欺诈宝珠",
                            attacker.getId(),
                            attacker.getMaxHp(),
                            attacker.getCurrentHp(),
                            0,
                            attacker.isOnField(),
                            defender.getId(),
                            defender.getMaxHp(),
                            defender.getCurrentHp(),
                            0,
                            defender.isOnField(),
                            EffectType.STUN,
                            DamageType.BUFF,
                            "眩晕2回合");
                }
                break;
        }
    }

    // 触发受击技能
    private void triggerOnAttackedSkills(Guardian defender, Guardian attacker) {
        int[] skillLevel = CardSkillLevelUtil.calculateSkillLevels(defender.getLevel(), defender.getStar().doubleValue());
        if (defender.isSilence()) {
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

                            Map<String, TargetBattleData> targetStatus = new HashMap<>();

                            immortalAllies.forEach(g -> {

                                g.setCurrentHp(g.getCurrentHp() + heal);
                                TargetBattleData data = new TargetBattleData(g.getMaxHp(), g.getCurrentHp(), heal, g.isOnField());
                                targetStatus.put(g.getId(), data);
                            });
                            addMultiTargetLog("瑶池仙露",
                                    defender.getId(),
                                    defender.getMaxHp(),
                                    defender.getCurrentHp(),
                                    defender.isOnField(),
                                    targetStatus,
                                    EffectType.HEAL,
                                    DamageType.BUFF,
                                    "治疗我方仙界单位");
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
                        int hel = 141 * skillLevel[1];
                        if (duoBaoGuanHuan()) {
                            defender.setCurrentHp(defender.getCurrentHp() + hel);
                        } else {
                            defender.setMaxHp(defender.getMaxHp() + hel);
                            defender.setCurrentHp(defender.getCurrentHp() + hel);
                        }

                        addLog("顽强战意",
                                defender.getId(),
                                defender.getMaxHp(),
                                defender.getCurrentHp(),
                                hel,
                                defender.isOnField(),
                                defender.getId(),
                                defender.getMaxHp(),
                                defender.getCurrentHp(),
                                hel,
                                defender.isOnField(),
                                EffectType.HP_UP,
                                DamageType.BUFF,
                                "生命上限+" + hel);

                    }
                }
                break;
            case "九尾狐":
                if (skillLevel[1] > 0) {
                    // 睚眦必报Lv1受到普通攻击时增加攻击38点。
                    if (!defender.isDead() && skillLevel[1] > 0) {
                        int value = 38 * skillLevel[1];
                        defender.setAttack(defender.getAttack() + value);

                        addLog("睚眦必报",
                                defender.getId(),
                                defender.getMaxHp(),
                                defender.getCurrentHp(),
                                value,
                                defender.isOnField(),
                                defender.getId(),
                                defender.getMaxHp(),
                                defender.getCurrentHp(),
                                value,
                                defender.isOnField(),
                                EffectType.ATTACK_UP,
                                DamageType.MAGIC,
                                "攻击提升+" + value);

                    }
                }
                break;
            case "圣婴大王":
                if (skillLevel[1] > 0) {
                    //赤炎臂膀Lv1受到普通攻击时，提高自身火焰伤害106点，最多可叠加99层
                    if (!defender.isDead() && defender.getBuffStacks() < 99) {
                        defender.setBuffStacks(defender.getBuffStacks() + 1);
                        int value = 106 * skillLevel[1];
                        defender.addEffect(EffectType.FIRE_BOOST, value, 99, defender.getId());
                        addLog("赤炎臂膀",
                                defender.getId(),
                                defender.getMaxHp(),
                                defender.getCurrentHp(),
                                value,
                                defender.isOnField(),
                                defender.getId(),
                                defender.getMaxHp(),
                                defender.getCurrentHp(),
                                value,
                                defender.isOnField(),
                                EffectType.FIRE_BOOST,
                                DamageType.BUFF,
                                "火焰伤害+" + value);

                    }
                }
                break;
            case "聂小倩":
                // 幽灵毒击：令攻击者中毒
                if (!defender.isDead()) {
                    int totalPoisonDamage = 8 * skillLevel[0];
                    // 1. 计算所有中毒效果的总伤害（累加 POISON 类型的 value）
                    // 2. 计算毒抗相关（直接基于你现有 EffectInstance 计算，不新增 Guardian 方法）
                    // 中毒增益：所有 POISON_RESIST 类型效果的 value 总和
                    int resistUp = calculateTotalVaule(defender, EffectType.POISON_BOOST);
                    // 中毒增益百分比：所有 POISON_RESIST 类型效果的 value 乘积
                    double resistUpPret = calculateTotalUpPretVaule(defender, EffectType.POISON_BOOST_PRET);
                    // 中毒降低：所有 POISON_RESIST_DOWN 类型效果的 value 总和
                    int resistDown = calculateTotalVaule(defender, EffectType.POISON_DOWN);
                    // 中毒降低百分比：所有 POISON_RESIST 类型效果的 value 乘积
                    double resistDownPret = calculateTotalDownPretVaule(defender, EffectType.POISON_DOWN_PRET);

                    int poisonValue = (int) (totalPoisonDamage * resistUpPret * resistDownPret + (resistUp + defender.getDsAtk() - resistDown));
                    attacker.addEffect(EffectType.POISON, poisonValue, 99, defender.getId());
                    addLog("幽灵毒击",
                            defender.getId(),
                            defender.getMaxHp(),
                            defender.getCurrentHp(),
                            0,
                            defender.isOnField(),
                            attacker.getId(),
                            attacker.getMaxHp(),
                            attacker.getCurrentHp(),
                            poisonValue,
                            attacker.isOnField(),
                            EffectType.POISON,
                            DamageType.POISON,
                            "中毒+" + poisonValue);
                }
                break;
            case "黄牙老象":
                // 幽灵毒击：令攻击者中毒
                if (!defender.isDead()) {
                    int totalPoisonDamage = 8 * skillLevel[0];
                    // 1. 计算所有中毒效果的总伤害（累加 POISON 类型的 value）
                    // 2. 计算毒抗相关（直接基于你现有 EffectInstance 计算，不新增 Guardian 方法）
                    // 中毒增益：所有 POISON_RESIST 类型效果的 value 总和
                    int resistUp = calculateTotalVaule(defender, EffectType.POISON_BOOST);
                    // 中毒增益百分比：所有 POISON_RESIST 类型效果的 value 乘积
                    double resistUpPret = calculateTotalUpPretVaule(defender, EffectType.POISON_BOOST_PRET);
                    // 中毒降低：所有 POISON_RESIST_DOWN 类型效果的 value 总和
                    int resistDown = calculateTotalVaule(defender, EffectType.POISON_DOWN);
                    // 中毒降低百分比：所有 POISON_RESIST 类型效果的 value 乘积
                    double resistDownPret = calculateTotalDownPretVaule(defender, EffectType.POISON_DOWN_PRET);

                    int poisonValue = (int) (totalPoisonDamage * resistUpPret * resistDownPret + (resistUp + defender.getDsAtk() - resistDown));
                    attacker.addEffect(EffectType.POISON, poisonValue, 99, defender.getId());
                    addLog("幽灵毒击",
                            defender.getId(),
                            defender.getMaxHp(),
                            defender.getCurrentHp(),
                            poisonValue,
                            defender.isOnField(),
                            attacker.getId(),
                            attacker.getMaxHp(),
                            attacker.getCurrentHp(),
                            poisonValue,
                            attacker.isOnField(),
                            EffectType.POISON,
                            DamageType.POISON,
                            "中毒+" + poisonValue);

                }
                break;
            case "烛龙":
                // 烛火燎原：对全体敌方造成火焰伤害（多目标整合日志）
                if (!defender.isDead()) {
                    List<Guardian> enemies = defender.getCamp() == Camp.A ? campB : campA;
                    List<Guardian> aliveEnemies = enemies.stream()
                            .filter(g -> !g.isDead())
                            .collect(Collectors.toList());

                    if (!aliveEnemies.isEmpty() && skillLevel[1] > 0) {
                        Map<String, TargetBattleData> deadUnits = new HashMap<>();
                        List<Guardian> deadGuardians = new ArrayList<>();

                        Map<String, TargetBattleData> targetStatus = new HashMap<>();

                        // 记录目标状态并执行伤害
                        aliveEnemies.forEach(g -> {
                            // 1. 计算所有中毒效果的总伤害（累加 POISON 类型的 value）
                            int totalPoisonDamage = 54 * skillLevel[1];
                            // 2. 计算毒抗相关（直接基于你现有 EffectInstance 计算，不新增 Guardian 方法）
                            // 火伤增益：所有 POISON_RESIST 类型效果的 value 总和
                            int resistUp = calculateTotalVaule(defender, EffectType.FIRE_BOOST);
                            // 火伤增益百分比：所有 POISON_RESIST 类型效果的 value 乘积
                            double resistUpPret = calculateTotalUpPretVaule(defender, EffectType.FIRE_BOOST_PRET);
                            // 火伤降低：所有 POISON_RESIST_DOWN 类型效果的 value 总和
                            int resistDown = calculateTotalVaule(defender, EffectType.FIRE_DOWN);
                            // 火伤降低百分比：所有 POISON_RESIST 类型效果的 value 乘积
                            double resistDownPret = calculateTotalDownPretVaule(defender, EffectType.FIRE_DOWN_PRET);


                            // 2. 计算毒抗相关（直接基于你现有 EffectInstance 计算，不新增 Guardian 方法）
                            // 火抗增益：所有 POISON_RESIST 类型效果的 value 总和
                            int targetUp = calculateTotalVaule(g, EffectType.FIRE_RESIST_BOOST);
                            // 火抗增益百分比：所有 POISON_RESIST 类型效果的 value 乘积
                            double targetUpPret = calculateTotalDownPretVaule(g, EffectType.FIRE_RESIST_BOOST_PRET);
                            // 火抗降低：所有 POISON_RESIST_DOWN 类型效果的 value 总和
                            int targetDown = calculateTotalVaule(g, EffectType.FIRE_RESIST_DOWN);
                            // 火抗降低百分比：所有 POISON_RESIST 类型效果的 value 乘积
                            double targetDownPret = calculateTotalUpPretVaule(g, EffectType.FIRE_RESIST_DOWN_PRET);
                            // 最终（仅基于 buff 计算，无新增方法）

                            int finalDamage = (int) (totalPoisonDamage * resistUpPret * resistDownPret * targetUpPret * targetDownPret
                                    + (resistUp - resistDown + defender.getHyAtk() - g.getHyDef() - targetUp + targetDown));

                            if (finalDamage < 0) {
                                finalDamage = 0;
                            }

                            // 4. 扣除伤害
                            g.setCurrentHp(g.getCurrentHp() - finalDamage);
                            TargetBattleData data = new TargetBattleData(g.getMaxHp(), g.getCurrentHp(), finalDamage, g.isOnField());
                            targetStatus.put(g.getId(), data);
                            if (g.isDead()) {

                                deadUnits.put(g.getId(), data);
                                deadGuardians.add(g);
                            }
                        });

                        // 单条日志记录多目标
                        addMultiTargetLog("烛火燎原",
                                defender.getId(),
                                defender.getMaxHp(),
                                defender.getCurrentHp(),
                                defender.isOnField(),
                                targetStatus,
                                EffectType.FIRE_DAMAGE,
                                DamageType.FIRE,
                                "全体敌方造成火焰伤害");
                        // 死亡日志
                        if (!deadUnits.isEmpty()) {
                            addMultiTargetLog("UNIT_DEATH",
                                    null,
                                    0,
                                    0,
                                    false,
                                    deadUnits,
                                    null,
                                    null,
                                    "死亡");
                            //触发死亡技能
                            for (Guardian g : deadGuardians) {
                                triggerOnDeathSkills(g);
                            }

                        }
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
                        Map<String, TargetBattleData> deadUnits = new HashMap<>();
                        List<Guardian> deadGuardians = new ArrayList<>();

                        Map<String, TargetBattleData> targetStatus = new HashMap<>();

                        // 记录目标状态并执行伤害
                        aliveEnemies.forEach(g -> {
                            // 1. 计算所有中毒效果的总伤害（累加 POISON 类型的 value）
                            int totalPoisonDamage = (random.nextInt(140 - 35 + 1) + 35)*skillLevel[1];
                            ;
                            // 2. 计算毒抗相关（直接基于你现有 EffectInstance 计算，不新增 Guardian 方法）
                            // 火伤增益：所有 POISON_RESIST 类型效果的 value 总和
                            int resistUp = calculateTotalVaule(defender, EffectType.FIRE_BOOST);
                            // 火伤增益百分比：所有 POISON_RESIST 类型效果的 value 乘积
                            double resistUpPret = calculateTotalUpPretVaule(defender, EffectType.FIRE_BOOST_PRET);
                            // 火伤降低：所有 POISON_RESIST_DOWN 类型效果的 value 总和
                            int resistDown = calculateTotalVaule(defender, EffectType.FIRE_DOWN);
                            // 火伤降低百分比：所有 POISON_RESIST 类型效果的 value 乘积
                            double resistDownPret = calculateTotalDownPretVaule(defender, EffectType.FIRE_DOWN_PRET);


                            // 2. 计算毒抗相关（直接基于你现有 EffectInstance 计算，不新增 Guardian 方法）
                            // 火抗增益：所有 POISON_RESIST 类型效果的 value 总和
                            int targetUp = calculateTotalVaule(g, EffectType.FIRE_RESIST_BOOST);
                            // 火抗增益百分比：所有 POISON_RESIST 类型效果的 value 乘积
                            double targetUpPret = calculateTotalDownPretVaule(g, EffectType.FIRE_RESIST_BOOST_PRET);
                            // 火抗降低：所有 POISON_RESIST_DOWN 类型效果的 value 总和
                            int targetDown = calculateTotalVaule(g, EffectType.FIRE_RESIST_DOWN);
                            // 火抗降低百分比：所有 POISON_RESIST 类型效果的 value 乘积
                            double targetDownPret = calculateTotalUpPretVaule(g, EffectType.FIRE_RESIST_DOWN_PRET);
                            // 最终（仅基于 buff 计算，无新增方法）

                            int finalDamage = (int) (totalPoisonDamage * resistUpPret * resistDownPret * targetUpPret * targetDownPret
                                    + (resistUp - resistDown + defender.getHyAtk() - g.getHyDef() - targetUp + targetDown));

                            if (finalDamage < 0) {
                                finalDamage = 0;
                            }

                            // 4. 扣除伤害
                            g.setCurrentHp(g.getCurrentHp() - finalDamage);
                            TargetBattleData data = new TargetBattleData(g.getMaxHp(), g.getCurrentHp(), finalDamage, g.isOnField());

                            targetStatus.put(g.getId(), data);
                            if (g.isDead()) {

                                deadUnits.put(g.getId(), data);
                                deadGuardians.add(g);
                            }
                        });

                        // 单条日志记录多目标
                        addMultiTargetLog("爆竹送给你",
                                defender.getId(),
                                defender.getMaxHp(),
                                defender.getCurrentHp(),
                                defender.isOnField(),
                                targetStatus,
                                EffectType.FIRE_DAMAGE,
                                DamageType.FIRE,
                                "全体敌方造成火焰伤害");
                        // 死亡日志
                        if (!deadUnits.isEmpty()) {
                            addMultiTargetLog("UNIT_DEATH",
                                    null,
                                    0,
                                    0,
                                    false,
                                    deadUnits,
                                    null,
                                    null,
                                    "死亡");
                            //触发死亡技能
                            for (Guardian g : deadGuardians) {
                                triggerOnDeathSkills(g);
                            }

                        }
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
                        int totalPoisonDamage = 73 * skillLevel[0];
                        // 1. 计算所有中毒效果的总伤害（累加 POISON 类型的 value）
                        // 2. 计算毒抗相关（直接基于你现有 EffectInstance 计算，不新增 Guardian 方法）
                        // 中毒增益：所有 POISON_RESIST 类型效果的 value 总和
                        int resistUp = calculateTotalVaule(defender, EffectType.POISON_BOOST);
                        // 中毒增益百分比：所有 POISON_RESIST 类型效果的 value 乘积
                        double resistUpPret = calculateTotalUpPretVaule(defender, EffectType.POISON_BOOST_PRET);
                        // 中毒降低：所有 POISON_RESIST_DOWN 类型效果的 value 总和
                        int resistDown = calculateTotalVaule(defender, EffectType.POISON_DOWN);
                        // 中毒降低百分比：所有 POISON_RESIST 类型效果的 value 乘积
                        double resistDownPret = calculateTotalDownPretVaule(defender, EffectType.POISON_DOWN_PRET);

                        int poisonValue = (int) (totalPoisonDamage * resistUpPret * resistDownPret + (resistUp + defender.getDsAtk() - resistDown));
                        randomEnemy.addEffect(EffectType.POISON, poisonValue, 99, defender.getId());
                        addLog("剧毒皮肤",
                                defender.getId(),
                                defender.getMaxHp(),
                                defender.getCurrentHp(),
                                poisonValue,
                                defender.isOnField(),
                                randomEnemy.getId(),
                                randomEnemy.getMaxHp(),
                                randomEnemy.getCurrentHp(),
                                poisonValue,
                                randomEnemy.isOnField(),
                                EffectType.POISON,
                                DamageType.POISON,
                                "中毒+" + poisonValue);
                    }
                }
                break;
            case "白眼魔君":
                // 幽灵毒击：令攻击者中毒
                if (!defender.isDead()) {
                    int totalPoisonDamage = 8 * skillLevel[0];
                    // 1. 计算所有中毒效果的总伤害（累加 POISON 类型的 value）
                    // 2. 计算毒抗相关（直接基于你现有 EffectInstance 计算，不新增 Guardian 方法）
                    // 中毒增益：所有 POISON_RESIST 类型效果的 value 总和
                    int resistUp = calculateTotalVaule(attacker, EffectType.POISON_BOOST);
                    // 中毒增益百分比：所有 POISON_RESIST 类型效果的 value 乘积
                    double resistUpPret = calculateTotalUpPretVaule(attacker, EffectType.POISON_BOOST_PRET);
                    // 中毒降低：所有 POISON_RESIST_DOWN 类型效果的 value 总和
                    int resistDown = calculateTotalVaule(attacker, EffectType.POISON_DOWN);
                    // 中毒降低百分比：所有 POISON_RESIST 类型效果的 value 乘积
                    double resistDownPret = calculateTotalDownPretVaule(attacker, EffectType.POISON_DOWN_PRET);

                    int poisonValue = (int) (totalPoisonDamage * resistUpPret * resistDownPret + (resistUp + attacker.getDsAtk() - resistDown));
                    attacker.addEffect(EffectType.POISON, poisonValue, 99, defender.getId());
                    addLog("幽灵毒击",
                            attacker.getId(),
                            attacker.getMaxHp(),
                            attacker.getCurrentHp(),
                            poisonValue,
                            attacker.isOnField(),
                            defender.getId(),
                            defender.getMaxHp(),
                            defender.getCurrentHp(),
                            poisonValue,
                            defender.isOnField(),
                            EffectType.POISON,
                            DamageType.POISON,
                            "中毒" + poisonValue);
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
                        int totalPoisonDamage = 8 * skillLevel[0];
                        // 1. 计算所有中毒效果的总伤害（累加 POISON 类型的 value）
                        // 2. 计算毒抗相关（直接基于你现有 EffectInstance 计算，不新增 Guardian 方法）
                        // 中毒增益：所有 POISON_RESIST 类型效果的 value 总和
                        int resistUp = calculateTotalVaule(defender, EffectType.POISON_BOOST);
                        // 中毒增益百分比：所有 POISON_RESIST 类型效果的 value 乘积
                        double resistUpPret = calculateTotalUpPretVaule(defender, EffectType.POISON_BOOST_PRET);
                        // 中毒降低：所有 POISON_RESIST_DOWN 类型效果的 value 总和
                        int resistDown = calculateTotalVaule(defender, EffectType.POISON_DOWN);
                        // 中毒降低百分比：所有 POISON_RESIST 类型效果的 value 乘积
                        double resistDownPret = calculateTotalDownPretVaule(defender, EffectType.POISON_DOWN_PRET);

                        int poisonValue = (int) (totalPoisonDamage * resistUpPret * resistDownPret + (resistUp + defender.getDsAtk() - resistDown));
                        randomEnemy.addEffect(EffectType.POISON, poisonValue, 99, defender.getId());
                        addLog("毒素反击",
                                defender.getId(),
                                defender.getMaxHp(),
                                defender.getCurrentHp(),
                                poisonValue,
                                defender.isOnField(),
                                randomEnemy.getId(),
                                randomEnemy.getMaxHp(),
                                randomEnemy.getCurrentHp(),
                                poisonValue,
                                randomEnemy.isOnField(),
                                EffectType.POISON,
                                DamageType.POISON,
                                "中毒+" + poisonValue);
                    }
                }
                break;
            case "句芒":
                // 句芒，残酷收割Lv1每当有生物死亡时，回复自身6%最大生命；嗜血Lv1受到普通攻击时，为自身添加嗜血效果，提高攻击118点，速度20点；大圣协同Lv1与齐天大圣在同一队伍时，增加自身453点生命上限，158点攻击，158点速度。
                if (!defender.isDead() && skillLevel[1] > 0) {
                    int value = 118 * skillLevel[1];
                    int value2 = 20 * skillLevel[1];
                    defender.addEffect(EffectType.ATTACK_UP, value, 99, defender.getId());
                    defender.addEffect(EffectType.SPEED_UP, value2, 99, defender.getId());
                    addLog("嗜血",
                            defender.getId(),
                            defender.getMaxHp(),
                            defender.getCurrentHp(),
                            value,
                            defender.isOnField(),
                            defender.getId(),
                            defender.getMaxHp(),
                            defender.getCurrentHp(),
                            value,
                            defender.isOnField(),
                            EffectType.BLOODTHIRST,
                            DamageType.BUFF,
                            "攻击提高+" + value + ",速度提高+" + value2);
                }
                break;
            case "刑天":
                // 刑天，斩杀Lv1普通攻击有13%几率对目标造成220点火焰伤害，如果目标是武圣则有几率一击必杀，替代普通攻击；嗜血Lv1受到普通攻击时，为自身添加嗜血效果，提高攻击118点，速度20点；金翅雕协同Lv1与大鹏金翅雕在同一队伍时，增加自身453点生命上限，158点攻击，158点速度                if (!defender.isDead()) {
                if (!defender.isDead() && skillLevel[1] > 0) {
                    int value = 118 * skillLevel[1];
                    int value2 = 20 * skillLevel[1];
                    defender.addEffect(EffectType.ATTACK_UP, value, 99, defender.getId());
                    defender.addEffect(EffectType.SPEED_UP, value2, 99, defender.getId());
                    addLog("嗜血",
                            defender.getId(),
                            defender.getMaxHp(),
                            defender.getCurrentHp(),
                            118 * skillLevel[1],
                            defender.isOnField(),
                            defender.getId(),
                            defender.getMaxHp(),
                            defender.getCurrentHp(),
                            118 * skillLevel[1],
                            defender.isOnField(),
                            EffectType.BLOODTHIRST,
                            DamageType.BUFF,
                            "攻击提高+" + value + ",速度提高+" + value2);
                }
                break;

            case "真武大帝":
                // 绝地反击：造成敌方攻击10%的伤害
                if (!defender.isDead()) {
                    double skillpret = 0.1 * skillLevel[1];
                    // 1. 计算所有中毒效果的总伤害（累加 POISON 类型的 value）
                    int totalPoisonDamage = attacker.getAttack();
                    // 2. 计算毒抗相关（直接基于你现有 EffectInstance 计算，不新增 Guardian 方法）
                    // 物理攻击增益：所有 POISON_RESIST 类型效果的 value 总和
                    int resistUp = calculateTotalVaule(attacker, EffectType.ATTACK_UP);
                    // 物理攻击增益百分比：所有 POISON_RESIST 类型效果的 value 乘积
                    double resistUpPret = calculateTotalUpPretVaule(attacker, EffectType.ATTACK_UP_PRET);
                    // 物理攻击降低：所有 POISON_RESIST_DOWN 类型效果的 value 总和
                    int resistDown = calculateTotalVaule(attacker, EffectType.ATTACK_DOWN);
                    // 物理攻击降低百分比：所有 POISON_RESIST 类型效果的 value 乘积
                    double resistDownPret = calculateTotalDownPretVaule(attacker, EffectType.ATTACK_DOWN_PRET);


                    // 2. 计算毒抗相关（直接基于你现有 EffectInstance 计算，不新增 Guardian 方法）
                    // 物理抗性增益：所有 POISON_RESIST 类型效果的 value 总和
                    int targetUp = calculateTotalVaule(defender, EffectType.ATTACK_RESIST_BOOST);
                    //  物理抗性增益百分比：所有 POISON_RESIST 类型效果的 value 乘积
                    double targetUpPret = calculateTotalDownPretVaule(defender, EffectType.ATTACK_RESIST_BOOST_PRET);
                    // 物理抗性降低：所有 POISON_RESIST_DOWN 类型效果的 value 总和
                    int targetDown = calculateTotalVaule(defender, EffectType.ATTACK_RESIST_DOWN);
                    // 物理抗性降低百分比：所有 POISON_RESIST 类型效果的 value 乘积
                    double targetDownPret = calculateTotalUpPretVaule(defender, EffectType.ATTACK_RESIST_DOWN_PRET);
                    // 最终（仅基于 buff 计算，无新增方法）

                    int burnDamage = (int) (totalPoisonDamage * resistUpPret * resistDownPret * targetUpPret * targetDownPret * skillpret
                            + (resistUp - resistDown + attacker.getWlAtk() - defender.getWlDef() - targetUp + targetDown));
                    // 物理攻击
                    if (burnDamage < 0) {
                        burnDamage = 0;
                    }


                    attacker.setCurrentHp(attacker.getCurrentHp() - burnDamage);


                    addLog("绝地反击",
                            defender.getId(),
                            defender.getMaxHp(),
                            defender.getCurrentHp(),
                            0,
                            defender.isOnField(),
                            attacker.getId(),
                            attacker.getMaxHp(),
                            attacker.getCurrentHp(),
                            burnDamage,
                            attacker.isOnField(),
                            EffectType.DAMAGE,
                            DamageType.PHYSICAL,
                            "-" + burnDamage);
                    Map<String, TargetBattleData> deadUnits = new HashMap<>();

                    if (attacker.getCurrentHp() <= 0) {
                        attacker.setDead(true);
                        attacker.setOnField(false);
                        TargetBattleData data = new TargetBattleData(attacker.getMaxHp(), attacker.getCurrentHp(), burnDamage, attacker.isOnField());
                        deadUnits.put(attacker.getId(), data);
                    }
                    // 死亡日志
                    if (!deadUnits.isEmpty()) {
                        addMultiTargetLog("UNIT_DEATH",
                                null,
                                0,
                                0,
                                false,
                                deadUnits,
                                null,
                                null,
                                "死亡");
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
        if (defender.isSilence()) {
            return;
        }
        int[] skillLevel = CardSkillLevelUtil.calculateSkillLevels(defender.getLevel(), defender.getStar().doubleValue());
        switch (defender.getName()) {
            case "东岳大帝":
                if (defender.isOnField() && !defender.isDead() && skillLevel[1] > 0) {
                    // 镇妖塔：对敌方场上造成飞弹伤害
                    Guardian enemy = defender.getCamp() == Camp.A ? fieldB : fieldA;
                    if (enemy != null && !enemy.isDead()) {
                        // 1. 计算所有中毒效果的总伤害（累加 POISON 类型的 value）
                        int totalPoisonDamage = 146 * skillLevel[1];
                        // 2. 计算毒抗相关（直接基于你现有 EffectInstance 计算，不新增 Guardian 方法）
                        // 飞弹增益：所有 POISON_RESIST 类型效果的 value 总和
                        int resistUp = calculateTotalVaule(defender, EffectType.MISSILE_BOOST);
                        // 飞弹增益百分比：所有 POISON_RESIST 类型效果的 value 乘积
                        double resistUpPret = calculateTotalUpPretVaule(defender, EffectType.MISSILE_BOOST_PRET);
                        // 飞弹降低：所有 POISON_RESIST_DOWN 类型效果的 value 总和
                        int resistDown = calculateTotalVaule(defender, EffectType.MISSILE_DOWN);
                        // 飞弹降低百分比：所有 POISON_RESIST 类型效果的 value 乘积
                        double resistDownPret = calculateTotalDownPretVaule(defender, EffectType.MISSILE_DOWN_PRET);


                        // 2. 计算毒抗相关（直接基于你现有 EffectInstance 计算，不新增 Guardian 方法）
                        // 弹抗增益：所有 POISON_RESIST 类型效果的 value 总和
                        int targetUp = calculateTotalVaule(enemy, EffectType.MISSILE_RESIST_BOOST);
                        // 弹抗增益百分比：所有 POISON_RESIST 类型效果的 value 乘积
                        double targetUpPret = calculateTotalDownPretVaule(enemy, EffectType.MISSILE_RESIST_BOOST_PRET);
                        // 弹抗降低：所有 POISON_RESIST_DOWN 类型效果的 value 总和
                        int targetDown = calculateTotalVaule(enemy, EffectType.MISSILE_RESIST_DOWN);
                        // 弹抗降低百分比：所有 POISON_RESIST 类型效果的 value 乘积
                        double targetDownPret = calculateTotalUpPretVaule(enemy, EffectType.MISSILE_RESIST_DOWN_PRET);
                        // 最终（仅基于 buff 计算，无新增方法）

                        int burnDamage = (int) (totalPoisonDamage * resistUpPret * resistDownPret * targetUpPret * targetDownPret
                                + (resistUp - resistDown + defender.getFdAtk() - enemy.getFdDef() - targetUp + targetDown));

                        if (burnDamage < 0) {
                            burnDamage = 0;
                        }

                        // 4. 扣除伤害
                        enemy.setCurrentHp(enemy.getCurrentHp() - burnDamage);
                        Map<String, TargetBattleData> deadUnits = new HashMap<>();

                        if (enemy.getCurrentHp() <= 0) {
                            enemy.setDead(true);
                            enemy.setOnField(false);
                            TargetBattleData data = new TargetBattleData(enemy.getMaxHp(), enemy.getCurrentHp(), burnDamage, enemy.isOnField());
                            deadUnits.put(enemy.getId(), data);
                        }
                        addLog("圣灵瀑",
                                defender.getId(),
                                defender.getMaxHp(),
                                defender.getCurrentHp(),
                                0,
                                defender.isOnField(),
                                enemy.getId(),
                                enemy.getMaxHp(),
                                enemy.getCurrentHp(),
                                burnDamage,
                                enemy.isOnField(),
                                EffectType.MISSILE_DAMAGE,
                                DamageType.MISSILE,
                                "-" + burnDamage);
                        // 死亡日志
                        if (!deadUnits.isEmpty()) {
                            addMultiTargetLog("UNIT_DEATH",
                                    null,
                                    0,
                                    0,
                                    false,
                                    deadUnits,
                                    null,
                                    null,
                                    "死亡");
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
                        // 1. 计算所有中毒效果的总伤害（累加 POISON 类型的 value）
                        int totalPoisonDamage = 32 * skillLevel[0];
                        // 2. 计算毒抗相关（直接基于你现有 EffectInstance 计算，不新增 Guardian 方法）
                        // 飞弹增益：所有 POISON_RESIST 类型效果的 value 总和
                        int resistUp = calculateTotalVaule(defender, EffectType.MISSILE_BOOST);
                        // 飞弹增益百分比：所有 POISON_RESIST 类型效果的 value 乘积
                        double resistUpPret = calculateTotalUpPretVaule(defender, EffectType.MISSILE_BOOST_PRET);
                        // 飞弹降低：所有 POISON_RESIST_DOWN 类型效果的 value 总和
                        int resistDown = calculateTotalVaule(defender, EffectType.MISSILE_DOWN);
                        // 飞弹降低百分比：所有 POISON_RESIST 类型效果的 value 乘积
                        double resistDownPret = calculateTotalDownPretVaule(defender, EffectType.MISSILE_DOWN_PRET);


                        // 2. 计算毒抗相关（直接基于你现有 EffectInstance 计算，不新增 Guardian 方法）
                        // 弹抗增益：所有 POISON_RESIST 类型效果的 value 总和
                        int targetUp = calculateTotalVaule(enemy, EffectType.MISSILE_RESIST_BOOST);
                        // 弹抗增益百分比：所有 POISON_RESIST 类型效果的 value 乘积
                        double targetUpPret = calculateTotalDownPretVaule(enemy, EffectType.MISSILE_RESIST_BOOST_PRET);
                        // 弹抗降低：所有 POISON_RESIST_DOWN 类型效果的 value 总和
                        int targetDown = calculateTotalVaule(enemy, EffectType.MISSILE_RESIST_DOWN);
                        // 弹抗降低百分比：所有 POISON_RESIST 类型效果的 value 乘积
                        double targetDownPret = calculateTotalUpPretVaule(enemy, EffectType.MISSILE_RESIST_DOWN_PRET);
                        // 最终（仅基于 buff 计算，无新增方法）

                        int burnDamage = (int) (totalPoisonDamage * resistUpPret * resistDownPret * targetUpPret * targetDownPret
                                + (resistUp - resistDown + defender.getFdAtk() - enemy.getFdDef() - targetUp + targetDown));

                        if (burnDamage < 0) {
                            burnDamage = 0;
                        }

                        // 4. 扣除伤害
                        enemy.setCurrentHp(enemy.getCurrentHp() - burnDamage);
                        Map<String, TargetBattleData> deadUnits = new HashMap<>();

                        if (enemy.getCurrentHp() <= 0) {
                            enemy.setDead(true);
                            enemy.setOnField(false);
                            TargetBattleData data = new TargetBattleData(enemy.getMaxHp(), enemy.getCurrentHp(), burnDamage, enemy.isOnField());
                            deadUnits.put(enemy.getId(), data);
                        }
                        addLog("圣灵泉涌",
                                defender.getId(),
                                defender.getMaxHp(),
                                defender.getCurrentHp(),
                                0,
                                defender.isOnField(),
                                enemy.getId(),
                                enemy.getMaxHp(),
                                enemy.getCurrentHp(),
                                burnDamage,
                                enemy.isOnField(),
                                EffectType.MISSILE_DAMAGE,
                                DamageType.MISSILE,
                                "-" + burnDamage);
                        // 死亡日志
                        if (!deadUnits.isEmpty()) {
                            addMultiTargetLog("UNIT_DEATH",
                                    null,
                                    0,
                                    0,
                                    false,
                                    deadUnits,
                                    null,
                                    null,
                                    "死亡");
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
    private void triggerOnAttackedSkills(Guardian defender, EffectType effectType) {
        int[] skillLevel = CardSkillLevelUtil.calculateSkillLevels(defender.getLevel(), defender.getStar().doubleValue());
//        烛龙，烛火燎原Lv1受到任意伤害时对全体敌方造成54点火焰伤害；致命衰竭Lv1场上，有单位登场时为目标添加衰弱状态，攻击减少10%，持续99回合；句芒协同Lv1与句芒在同一队伍时增加自身197点生命上限，99点火焰伤害，197点速度。
        if (defender.isSilence()) {
            return;
        }
        switch (defender.getName()) {
            case "烛龙":
                // 烛火燎原：对全体敌方造成火焰伤害（多目标整合日志）
                //烛龙必须在场
                if (defender.isOnField() && !defender.isDead()) {
                    List<Guardian> enemies = defender.getCamp() == Camp.A ? campB : campA;
                    List<Guardian> aliveEnemies = enemies.stream()
                            .filter(g -> !g.isDead())
                            .collect(Collectors.toList());
                    if (!aliveEnemies.isEmpty() && skillLevel[1] > 0) {
                        Map<String, TargetBattleData> deadUnits = new HashMap<>();
                        List<Guardian> deadGuardians = new ArrayList<>();

                        Map<String, TargetBattleData> targetStatus = new HashMap<>();

                        // 记录目标状态并执行伤害
                        aliveEnemies.forEach(g -> {
                            // 1. 计算所有中毒效果的总伤害（累加 POISON 类型的 value）
                            int totalPoisonDamage = 54 * skillLevel[1];
                            // 2. 计算毒抗相关（直接基于你现有 EffectInstance 计算，不新增 Guardian 方法）
                            // 火伤增益：所有 POISON_RESIST 类型效果的 value 总和
                            int resistUp = calculateTotalVaule(defender, EffectType.FIRE_BOOST);
                            // 火伤增益百分比：所有 POISON_RESIST 类型效果的 value 乘积
                            double resistUpPret = calculateTotalUpPretVaule(defender, EffectType.FIRE_BOOST_PRET);
                            // 火伤降低：所有 POISON_RESIST_DOWN 类型效果的 value 总和
                            int resistDown = calculateTotalVaule(defender, EffectType.FIRE_DOWN);
                            // 火伤降低百分比：所有 POISON_RESIST 类型效果的 value 乘积
                            double resistDownPret = calculateTotalDownPretVaule(defender, EffectType.FIRE_DOWN_PRET);


                            // 2. 计算毒抗相关（直接基于你现有 EffectInstance 计算，不新增 Guardian 方法）
                            // 火抗增益：所有 POISON_RESIST 类型效果的 value 总和
                            int targetUp = calculateTotalVaule(g, EffectType.FIRE_RESIST_BOOST);
                            // 火抗增益百分比：所有 POISON_RESIST 类型效果的 value 乘积
                            double targetUpPret = calculateTotalDownPretVaule(g, EffectType.FIRE_RESIST_BOOST_PRET);
                            // 火抗降低：所有 POISON_RESIST_DOWN 类型效果的 value 总和
                            int targetDown = calculateTotalVaule(g, EffectType.FIRE_RESIST_DOWN);
                            // 火抗降低百分比：所有 POISON_RESIST 类型效果的 value 乘积
                            double targetDownPret = calculateTotalUpPretVaule(g, EffectType.FIRE_RESIST_DOWN_PRET);
                            // 最终（仅基于 buff 计算，无新增方法）

                            int finalDamage = (int) (totalPoisonDamage * resistUpPret * resistDownPret * targetUpPret * targetDownPret
                                    + (resistUp - resistDown + defender.getHyAtk() - g.getHyDef() - targetUp + targetDown));

                            if (finalDamage < 0) {
                                finalDamage = 0;
                            }

                            // 4. 扣除伤害
                            g.setCurrentHp(g.getCurrentHp() - finalDamage);
                            TargetBattleData data = new TargetBattleData(g.getMaxHp(), g.getCurrentHp(), finalDamage, g.isOnField());
                            targetStatus.put(g.getId(), data);
                            if (g.isDead()) {

                                deadUnits.put(g.getId(), data);
                                deadGuardians.add(g);
                            }
                        });
                        // 单条日志记录多目标
                        addMultiTargetLog("烛火燎原",
                                defender.getId(),
                                defender.getMaxHp(),
                                defender.getCurrentHp(),
                                defender.isOnField(),
                                targetStatus,
                                EffectType.FIRE_DAMAGE,
                                DamageType.FIRE,
                                "全体敌方造成火焰伤害");
                        // 死亡日志
                        if (!deadUnits.isEmpty()) {
                            addMultiTargetLog("UNIT_DEATH",
                                    null,
                                    0,
                                    0,
                                    false,
                                    deadUnits,
                                    null,
                                    null,
                                    "死亡");
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
                        Map<String, TargetBattleData> deadUnits = new HashMap<>();
                        List<Guardian> deadGuardians = new ArrayList<>();


                        Map<String, TargetBattleData> targetStatus = new HashMap<>();

                        // 记录目标状态并执行伤害
                        aliveEnemies.forEach(g -> {
                            // 1. 计算所有中毒效果的总伤害（累加 POISON 类型的 value）
                            int totalPoisonDamage = (random.nextInt(140 - 35 + 1) + 35)*skillLevel[1];
                            ;
                            // 2. 计算毒抗相关（直接基于你现有 EffectInstance 计算，不新增 Guardian 方法）
                            // 火伤增益：所有 POISON_RESIST 类型效果的 value 总和
                            int resistUp = calculateTotalVaule(defender, EffectType.FIRE_BOOST);
                            // 火伤增益百分比：所有 POISON_RESIST 类型效果的 value 乘积
                            double resistUpPret = calculateTotalUpPretVaule(defender, EffectType.FIRE_BOOST_PRET);
                            // 火伤降低：所有 POISON_RESIST_DOWN 类型效果的 value 总和
                            int resistDown = calculateTotalVaule(defender, EffectType.FIRE_DOWN);
                            // 火伤降低百分比：所有 POISON_RESIST 类型效果的 value 乘积
                            double resistDownPret = calculateTotalDownPretVaule(defender, EffectType.FIRE_DOWN_PRET);


                            // 2. 计算毒抗相关（直接基于你现有 EffectInstance 计算，不新增 Guardian 方法）
                            // 火抗增益：所有 POISON_RESIST 类型效果的 value 总和
                            int targetUp = calculateTotalVaule(g, EffectType.FIRE_RESIST_BOOST);
                            // 火抗增益百分比：所有 POISON_RESIST 类型效果的 value 乘积
                            double targetUpPret = calculateTotalDownPretVaule(g, EffectType.FIRE_RESIST_BOOST_PRET);
                            // 火抗降低：所有 POISON_RESIST_DOWN 类型效果的 value 总和
                            int targetDown = calculateTotalVaule(g, EffectType.FIRE_RESIST_DOWN);
                            // 火抗降低百分比：所有 POISON_RESIST 类型效果的 value 乘积
                            double targetDownPret = calculateTotalUpPretVaule(g, EffectType.FIRE_RESIST_DOWN_PRET);
                            // 最终（仅基于 buff 计算，无新增方法）

                            int finalDamage = (int) (totalPoisonDamage * resistUpPret * resistDownPret * targetUpPret * targetDownPret
                                    + (resistUp - resistDown + defender.getHyAtk() - g.getHyDef() - targetUp + targetDown));

                            if (finalDamage < 0) {
                                finalDamage = 0;
                            }

                            // 4. 扣除伤害
                            g.setCurrentHp(g.getCurrentHp() - finalDamage);
                            TargetBattleData data = new TargetBattleData(g.getMaxHp(), g.getCurrentHp(), finalDamage, g.isOnField());
                            targetStatus.put(g.getId(), data);
                            if (g.isDead()) {

                                deadUnits.put(g.getId(), data);
                                deadGuardians.add(g);
                            }
                        });
                        // 单条日志记录多目标
                        addMultiTargetLog("爆竹送给你",
                                defender.getId(),
                                defender.getMaxHp(),
                                defender.getCurrentHp(),
                                defender.isOnField(),
                                targetStatus,
                                EffectType.FIRE_DAMAGE,
                                DamageType.FIRE,
                                "对全体敌方造成火焰伤害");
                        // 死亡日志
                        if (!deadUnits.isEmpty()) {
                            addMultiTargetLog("UNIT_DEATH",
                                    null,
                                    0,
                                    0,
                                    false,
                                    deadUnits,
                                    null,
                                    null,
                                    "死亡");
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
                        int totalPoisonDamage = 30 * skillLevel[0];
                        // 1. 计算所有中毒效果的总伤害（累加 POISON 类型的 value）
                        // 2. 计算毒抗相关（直接基于你现有 EffectInstance 计算，不新增 Guardian 方法）
                        // 中毒增益：所有 POISON_RESIST 类型效果的 value 总和
                        int resistUp = calculateTotalVaule(defender, EffectType.POISON_BOOST);
                        // 中毒增益百分比：所有 POISON_RESIST 类型效果的 value 乘积
                        double resistUpPret = calculateTotalUpPretVaule(defender, EffectType.POISON_BOOST_PRET);
                        // 中毒降低：所有 POISON_RESIST_DOWN 类型效果的 value 总和
                        int resistDown = calculateTotalVaule(defender, EffectType.POISON_DOWN);
                        // 中毒降低百分比：所有 POISON_RESIST 类型效果的 value 乘积
                        double resistDownPret = calculateTotalDownPretVaule(defender, EffectType.POISON_DOWN_PRET);

                        int poisonValue = (int) (totalPoisonDamage * resistUpPret * resistDownPret + (resistUp + defender.getDsAtk() - resistDown));
                        randomEnemy.addEffect(EffectType.POISON, poisonValue, 99, defender.getId());
                        addLog("剧毒皮肤",
                                defender.getId(),
                                defender.getMaxHp(),
                                defender.getCurrentHp(),
                                poisonValue,
                                defender.isOnField(),
                                randomEnemy.getId(),
                                randomEnemy.getMaxHp(),
                                randomEnemy.getCurrentHp(),
                                poisonValue,
                                randomEnemy.isOnField(),
                                EffectType.POISON,
                                DamageType.POISON,
                                "中毒+" + poisonValue);
                    }

                }
                break;
            case "白素贞":
//                燥热蛇毒Lv1场下，受到火焰伤害，为当前敌人添加蛇毒效果，每回合损失12生命；众妖皆狂Lv1与妲己在同一队伍时，增加自身453点生命上限，158点攻击，158点速度。
                if (!defender.isDead() && effectType == EffectType.FIRE_DAMAGE && !defender.isOnField()) {
                    List<Guardian> enemies = defender.getCamp() == Camp.A ?
                            campB.stream().filter(g -> !g.isDead()).collect(Collectors.toList()) :
                            campA.stream().filter(g -> !g.isDead()).collect(Collectors.toList());
                    if (!enemies.isEmpty()) {
                        Guardian randomEnemy = enemies.get(random.nextInt(enemies.size()));
                        int totalPoisonDamage = 30 * skillLevel[0];
                        // 1. 计算所有中毒效果的总伤害（累加 POISON 类型的 value）
                        // 2. 计算毒抗相关（直接基于你现有 EffectInstance 计算，不新增 Guardian 方法）
                        // 中毒增益：所有 POISON_RESIST 类型效果的 value 总和
                        int resistUp = calculateTotalVaule(defender, EffectType.POISON_BOOST);
                        // 中毒增益百分比：所有 POISON_RESIST 类型效果的 value 乘积
                        double resistUpPret = calculateTotalUpPretVaule(defender, EffectType.POISON_BOOST_PRET);
                        // 中毒降低：所有 POISON_RESIST_DOWN 类型效果的 value 总和
                        int resistDown = calculateTotalVaule(defender, EffectType.POISON_DOWN);
                        // 中毒降低百分比：所有 POISON_RESIST 类型效果的 value 乘积
                        double resistDownPret = calculateTotalDownPretVaule(defender, EffectType.POISON_DOWN_PRET);

                        int poisonValue = (int) (totalPoisonDamage * resistUpPret * resistDownPret + (resistUp + defender.getDsAtk() - resistDown));
                        randomEnemy.addEffect(EffectType.POISON, poisonValue, 99, defender.getId());
                        addLog("燥热蛇毒",
                                defender.getId(),
                                defender.getMaxHp(),
                                defender.getCurrentHp(),
                                poisonValue,
                                defender.isOnField(),
                                randomEnemy.getId(),
                                randomEnemy.getMaxHp(),
                                randomEnemy.getCurrentHp(),
                                poisonValue,
                                randomEnemy.isOnField(),
                                EffectType.POISON,
                                DamageType.POISON,
                                "中毒+" + poisonValue);
                    }

                }
                break;
            case "月刃夫人":
//                新月反击Lv1场上，每当受到飞弹伤害时对场上敌方造成155点物理伤害
                if (!defender.isDead() && effectType == EffectType.MISSILE_DAMAGE && defender.isOnField()) {
                    Guardian enemie = defender.getCamp() == Camp.A ?
                            fieldB : fieldA;
                    if (enemie != null && !enemie.isDead()) {

                        // 1. 计算所有中毒效果的总伤害（累加 POISON 类型的 value）
                        int totalPoisonDamage = 155 * skillLevel[0];
//                        // 2. 计算毒抗相关（直接基于你现有 EffectInstance 计算，不新增 Guardian 方法）
//                        // 物理攻击增益：所有 POISON_RESIST 类型效果的 value 总和
//                        int resistUp = calculateTotalVaule(attacker, EffectType.ATTACK_UP);
//                        // 物理攻击增益百分比：所有 POISON_RESIST 类型效果的 value 乘积
//                        double resistUpPret = calculateTotalUpPretVaule(attacker, EffectType.ATTACK_UP_PRET);
//                        // 物理攻击降低：所有 POISON_RESIST_DOWN 类型效果的 value 总和
//                        int resistDown = calculateTotalVaule(attacker, EffectType.ATTACK_DOWN);
//                        // 物理攻击降低百分比：所有 POISON_RESIST 类型效果的 value 乘积
//                        double resistDownPret = calculateTotalDownPretVaule(attacker, EffectType.ATTACK_DOWN_PRET);


                        // 2. 计算毒抗相关（直接基于你现有 EffectInstance 计算，不新增 Guardian 方法）
                        // 物理抗性增益：所有 POISON_RESIST 类型效果的 value 总和
                        int targetUp = calculateTotalVaule(defender, EffectType.ATTACK_RESIST_BOOST);
                        //  物理抗性增益百分比：所有 POISON_RESIST 类型效果的 value 乘积
                        double targetUpPret = calculateTotalDownPretVaule(defender, EffectType.ATTACK_RESIST_BOOST_PRET);
                        // 物理抗性降低：所有 POISON_RESIST_DOWN 类型效果的 value 总和
                        int targetDown = calculateTotalVaule(defender, EffectType.ATTACK_RESIST_DOWN);
                        // 物理抗性降低百分比：所有 POISON_RESIST 类型效果的 value 乘积
                        double targetDownPret = calculateTotalUpPretVaule(defender, EffectType.ATTACK_RESIST_DOWN_PRET);
                        // 最终（仅基于 buff 计算，无新增方法）

                        int burnDamage = (int) (totalPoisonDamage * targetUpPret * targetDownPret
                                + (-defender.getWlDef() - targetUp + targetDown));
                        // 物理攻击
                        if (burnDamage < 0) {
                            burnDamage = 0;
                        }


                        enemie.setCurrentHp(enemie.getCurrentHp() - burnDamage);

                        addLog("新月反击",
                                defender.getId(),
                                defender.getMaxHp(),
                                defender.getCurrentHp(),
                                0,
                                defender.isOnField(),
                                enemie.getId(),
                                enemie.getMaxHp(),
                                enemie.getCurrentHp(),
                                burnDamage,
                                enemie.isOnField(),
                                EffectType.DAMAGE,
                                DamageType.PHYSICAL,
                                "-" + burnDamage);
                        Map<String, TargetBattleData> deadUnits = new HashMap<>();

                        if (enemie.getCurrentHp() <= 0) {
                            enemie.setDead(true);
                            enemie.setOnField(false);
                            TargetBattleData data = new TargetBattleData(enemie.getMaxHp(), enemie.getCurrentHp(), burnDamage, enemie.isOnField());
                            deadUnits.put(defender.getId(), data);
                        }
                        // 死亡日志
                        if (!deadUnits.isEmpty()) {
                            addMultiTargetLog("UNIT_DEATH",
                                    null,
                                    0,
                                    0,
                                    false,
                                    deadUnits,
                                    null,
                                    null,
                                    "死亡");
                            //触发死亡技能
                            triggerOnDeathSkills(defender);

                        } else {
                            //触发受击技能
                            triggerOnAttackedSkills(enemie, defender);
                        }
                    }

                }
                break;
            case "南岳大帝":
//                草船借箭Lv1受到火焰伤害时，增加自身飞弹伤害13点，最多叠加99层；
                if (!defender.isDead() && effectType == EffectType.FIRE_DAMAGE) {
                    if (1 == 1) {
                        int poisonValue = 13 * skillLevel[0] + defender.getDsAtk();
                        defender.addEffect(EffectType.MISSILE_BOOST, poisonValue, 99, defender.getId());
                        addLog("草船借箭",
                                defender.getId(),
                                defender.getMaxHp(),
                                defender.getCurrentHp(),
                                poisonValue + defender.getDsAtk(),
                                defender.isOnField(),
                                defender.getId(),
                                defender.getMaxHp(),
                                defender.getCurrentHp(),
                                poisonValue + defender.getDsAtk(),
                                defender.isOnField(),
                                EffectType.MISSILE_BOOST,
                                DamageType.BUFF,
                                "飞弹伤害+" + poisonValue);
                    }

                }
                break;
            case "长生大帝":
//                南极祝福Lv1场下，受到任意伤害时提升自身56点生命值上限；
                if (!defender.isDead() && !defender.isOnField() && skillLevel[1] > 0) {
                    int hel = 56 * skillLevel[1];
                    if (duoBaoGuanHuan()) {
                        defender.setCurrentHp(defender.getCurrentHp() + hel);
                    } else {
                        defender.setMaxHp(defender.getMaxHp() + hel);
                        defender.setCurrentHp(defender.getCurrentHp() + hel);
                    }


                    addLog("南极祝福",
                            defender.getId(),
                            defender.getMaxHp(),
                            defender.getCurrentHp(),
                            hel,
                            defender.isOnField(),
                            defender.getId(),
                            defender.getMaxHp(),
                            defender.getCurrentHp(),
                            hel,
                            defender.isOnField(),
                            EffectType.HP_UP,
                            DamageType.MAGIC,
                            "生命上限+" + hel);

                }
                break;
        }

    }

    // 触发攻击后技能
    private void triggerPostAttackSkills(Guardian attacker, Guardian defender) {
        int[] skillLevel = CardSkillLevelUtil.calculateSkillLevels(attacker.getLevel(), attacker.getStar().doubleValue());
        if (attacker.isSilence()) {
            return;
        }
        switch (attacker.getName()) {
            case "东岳大帝":
                // 致命衰竭：登场目标攻击减少10%
                if (1 == 1) {
                    Guardian target = attacker.getCamp() == Camp.A ? fieldB : fieldA;
                    if (target != null && !target.isDead()) {
                        int weaken = skillLevel[0] * 20;
                        if (weaken < 0) {
                            weaken = 0;
                        }
                        target.setAttack(target.getAttack() - weaken);
                        addLog("大帝威慑",
                                attacker.getId(),
                                attacker.getMaxHp(),
                                attacker.getCurrentHp(),
                                0,
                                attacker.isOnField(),
                                target.getId(),
                                target.getMaxHp(),
                                target.getCurrentHp(),
                                weaken,
                                target.isOnField(),
                                EffectType.ATTACK_DOWN,
                                DamageType.BUFF,
                                "攻击降低-" + weaken);
                    }
                }
                break;
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
                                    attacker.getId(),
                                    attacker.getMaxHp(),
                                    attacker.getCurrentHp(),
                                    0,
                                    attacker.isOnField(),
                                    guardian.getId(),
                                    guardian.getMaxHp(),
                                    guardian.getCurrentHp(),
                                    attack,
                                    guardian.isOnField(),
                                    EffectType.ATTACK_UP,
                                    DamageType.BUFF,
                                    "攻击力提升+" + attack);
                        }
                    }
                }
                break;
            case "禺绒王":
                if (1 == 1) {
                    // ，剧毒加深Lv1攻击中毒敌人时，为其增加1层毒素效果，每回合损失35点生命；
                    if (defender.isPoison()) {
                        int totalPoisonDamage = 30 * skillLevel[0];
                        // 1. 计算所有中毒效果的总伤害（累加 POISON 类型的 value）
                        // 2. 计算毒抗相关（直接基于你现有 EffectInstance 计算，不新增 Guardian 方法）
                        // 中毒增益：所有 POISON_RESIST 类型效果的 value 总和
                        int resistUp = calculateTotalVaule(attacker, EffectType.POISON_BOOST);
                        // 中毒增益百分比：所有 POISON_RESIST 类型效果的 value 乘积
                        double resistUpPret = calculateTotalUpPretVaule(attacker, EffectType.POISON_BOOST_PRET);
                        // 中毒降低：所有 POISON_RESIST_DOWN 类型效果的 value 总和
                        int resistDown = calculateTotalVaule(attacker, EffectType.POISON_DOWN);
                        // 中毒降低百分比：所有 POISON_RESIST 类型效果的 value 乘积
                        double resistDownPret = calculateTotalDownPretVaule(attacker, EffectType.POISON_DOWN_PRET);

                        int poisonValue = (int) (totalPoisonDamage * resistUpPret * resistDownPret + (resistUp + defender.getDsAtk() - resistDown));
                        attacker.addEffect(EffectType.POISON, poisonValue, 99, defender.getId());
                        addLog("剧毒加深",
                                attacker.getId(),
                                attacker.getMaxHp(),
                                attacker.getCurrentHp(),
                                poisonValue,
                                attacker.isOnField(),
                                defender.getId(),
                                defender.getMaxHp(),
                                defender.getCurrentHp(),
                                poisonValue,
                                defender.isOnField(),
                                EffectType.POISON,
                                DamageType.POISON,
                                "中毒+" + poisonValue);
                    }
                    //妖力聚集Lv1每次攻击后增加自身后方单位的攻击42点，最多叠加5层；
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
                                    attacker.getId(),
                                    attacker.getMaxHp(),
                                    attacker.getCurrentHp(),
                                    0,
                                    attacker.isOnField(),
                                    guardian.getId(),
                                    guardian.getMaxHp(),
                                    guardian.getCurrentHp(),
                                    attack,
                                    guardian.isOnField(),
                                    EffectType.ATTACK_UP,
                                    DamageType.BUFF,
                                    "攻击力提升+" + attack);
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
                                attacker.getId(),
                                attacker.getMaxHp(),
                                attacker.getCurrentHp(),
                                0,
                                attacker.isOnField(),
                                guardian.getId(),
                                guardian.getMaxHp(),
                                guardian.getCurrentHp(),
                                hel,
                                guardian.isOnField(),
                                EffectType.HP_UP,
                                DamageType.BUFF,
                                "生命上限+" + hel);
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
                        Map<String, TargetBattleData> deadUnits = new HashMap<>();
                        List<Guardian> deadGuardians = new ArrayList<>();
                        int lavaDamage = 62 * skillLevel[0];

                        Map<String, TargetBattleData> targetStatus = new HashMap<>();

                        offFieldEnemies.forEach(g -> {
                            // 1. 计算所有中毒效果的总伤害（累加 POISON 类型的 value）
                            int totalPoisonDamage = lavaDamage;
                            // 2. 计算毒抗相关（直接基于你现有 EffectInstance 计算，不新增 Guardian 方法）
                            // 火伤增益：所有 POISON_RESIST 类型效果的 value 总和
                            int resistUp = calculateTotalVaule(attacker, EffectType.FIRE_BOOST);
                            // 火伤增益百分比：所有 POISON_RESIST 类型效果的 value 乘积
                            double resistUpPret = calculateTotalUpPretVaule(attacker, EffectType.FIRE_BOOST_PRET);
                            // 火伤降低：所有 POISON_RESIST_DOWN 类型效果的 value 总和
                            int resistDown = calculateTotalVaule(attacker, EffectType.FIRE_DOWN);
                            // 火伤降低百分比：所有 POISON_RESIST 类型效果的 value 乘积
                            double resistDownPret = calculateTotalDownPretVaule(attacker, EffectType.FIRE_DOWN_PRET);


                            // 2. 计算毒抗相关（直接基于你现有 EffectInstance 计算，不新增 Guardian 方法）
                            // 火抗增益：所有 POISON_RESIST 类型效果的 value 总和
                            int targetUp = calculateTotalVaule(g, EffectType.FIRE_RESIST_BOOST);
                            // 火抗增益百分比：所有 POISON_RESIST 类型效果的 value 乘积
                            double targetUpPret = calculateTotalDownPretVaule(g, EffectType.FIRE_RESIST_BOOST_PRET);
                            // 火抗降低：所有 POISON_RESIST_DOWN 类型效果的 value 总和
                            int targetDown = calculateTotalVaule(g, EffectType.FIRE_RESIST_DOWN);
                            // 火抗降低百分比：所有 POISON_RESIST 类型效果的 value 乘积
                            double targetDownPret = calculateTotalUpPretVaule(g, EffectType.FIRE_RESIST_DOWN_PRET);
                            // 最终（仅基于 buff 计算，无新增方法）

                            int finalDamage = (int) (totalPoisonDamage * resistUpPret * resistDownPret * targetUpPret * targetDownPret
                                    + (resistUp - resistDown + attacker.getHyAtk() - g.getHyDef() - targetUp + targetDown));

                            if (finalDamage < 0) {
                                finalDamage = 0;
                            }

                            // 4. 扣除伤害
                            g.setCurrentHp(g.getCurrentHp() - finalDamage);
                            TargetBattleData data = new TargetBattleData(g.getMaxHp(), g.getCurrentHp(), finalDamage, g.isOnField());
                            targetStatus.put(g.getId(), data);
                            if (g.isDead()) {

                                deadUnits.put(g.getId(), data);
                                deadGuardians.add(g);
                            }
                        });

                        // 单条日志记录多目标
                        addMultiTargetLog("鱼跃龙门",
                                attacker.getId(),
                                attacker.getMaxHp(),
                                attacker.getCurrentHp(),
                                attacker.isOnField(),
                                deadUnits,
                                EffectType.FIRE_DAMAGE,
                                DamageType.FIRE,
                                "敌方造成火焰伤害");
                        // 死亡日志
                        if (!deadUnits.isEmpty()) {
                            addMultiTargetLog("UNIT_DEATH",
                                    null,
                                    0,
                                    0,
                                    false,
                                    deadUnits,
                                    null,
                                    null,
                                    "死亡");
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
                if (defender.isPoison() && skillLevel[1] > 0) {
                    int hel = 118 * skillLevel[1];
                    attacker.setCurrentHp(attacker.getCurrentHp() + hel);

                    addLog("腐败虹吸",
                            attacker.getId(),
                            attacker.getMaxHp(),
                            attacker.getCurrentHp(),
                            hel,
                            attacker.isOnField(),
                            attacker.getId(),
                            attacker.getMaxHp(),
                            attacker.getCurrentHp(),
                            hel,
                            attacker.isOnField(),
                            EffectType.HEAL,
                            DamageType.MAGIC,
                            "+" + hel);

                }
                break;
            case "太岁灵君":
                // 腐败虹吸Lv1攻击中毒目标时吸血118点；
                if (skillLevel[1] > 0 && ProbabilityBooleanUtils.randomByProbability(0.07 * skillLevel[1])) {
                    int hel = (int) (attacker.getAttack() * 0.87);
                    attacker.setCurrentHp(attacker.getCurrentHp() + hel);
                    addLog("虹吸打击",
                            attacker.getId(),
                            attacker.getMaxHp(),
                            attacker.getCurrentHp(),
                            hel,
                            attacker.isOnField(),
                            attacker.getId(),
                            attacker.getMaxHp(),
                            attacker.getCurrentHp(),
                            hel,
                            attacker.isOnField(),
                            EffectType.HEAL,
                            DamageType.MAGIC,
                            "+" + hel);
                }
                break;
            case "金钩大王":
                //毒伤迸发Lv1攻击中毒敌人时，额外造成247点物理伤害；
                if (defender.isPoison()) {
                    int damage = 247 * skillLevel[0];
                    Map<String, TargetBattleData> deadUnits = new HashMap<>();
                    List<Guardian> deadGuardians = new ArrayList<>();
                    defender.setCurrentHp(defender.getCurrentHp() - damage);
                    if (defender.getCurrentHp() <= 0) {
                        defender.setDead(true);
                        defender.setOnField(false);
                        TargetBattleData data = new TargetBattleData(defender.getMaxHp(), defender.getCurrentHp(), damage, defender.isOnField());
                        deadUnits.put(defender.getId(), data);
                        deadGuardians.add(defender);
                    }
                    addLog("毒伤迸发",
                            attacker.getId(),
                            attacker.getMaxHp(),
                            attacker.getCurrentHp(),
                            0,
                            attacker.isOnField(),
                            defender.getId(),
                            defender.getMaxHp(),
                            defender.getCurrentHp(),
                            damage,
                            defender.isOnField(),
                            EffectType.DAMAGE,
                            DamageType.PHYSICAL,
                            "-" + damage);
                    // 死亡日志
                    if (!deadUnits.isEmpty()) {
                        addMultiTargetLog("UNIT_DEATH",
                                null,
                                0,
                                0,
                                false,
                                deadUnits,
                                null,
                                null,
                                "死亡");
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
            case "蛟魔王":
                //剧毒痛击Lv1攻击中毒单位额外造成80伤害；；
                if (defender.isPoison()) {
                    int damage = 80 * skillLevel[0];
                    Map<String, TargetBattleData> deadUnits = new HashMap<>();
                    List<Guardian> deadGuardians = new ArrayList<>();
                    defender.setCurrentHp(defender.getCurrentHp() + damage);
                    if (defender.getCurrentHp() <= 0) {
                        defender.setDead(true);
                        defender.setOnField(false);
                        TargetBattleData data = new TargetBattleData(defender.getMaxHp(), defender.getCurrentHp(), damage, defender.isOnField());
                        deadUnits.put(defender.getId(), data);
                        deadGuardians.add(defender);
                    }
                    addLog("剧毒痛击",
                            attacker.getId(),
                            attacker.getMaxHp(),
                            attacker.getCurrentHp(),
                            0,
                            attacker.isOnField(),
                            defender.getId(),
                            defender.getMaxHp(),
                            defender.getCurrentHp(),
                            damage,
                            defender.isOnField(),
                            EffectType.DAMAGE,
                            DamageType.PHYSICAL,
                            "-" + damage);
                    // 死亡日志
                    if (!deadUnits.isEmpty()) {
                        addMultiTargetLog("UNIT_DEATH",
                                null,
                                0,
                                0,
                                false,
                                deadUnits,
                                null,
                                null,
                                "死亡");
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
            case "铁扇公主":
                if (1 == 1) {
                    // 芭蕉扇：造成火焰伤害
                    Map<String, TargetBattleData> deadUnits = new HashMap<>();
                    List<Guardian> deadGuardians = new ArrayList<>();
                    // 1. 计算所有中毒效果的总伤害（累加 POISON 类型的 value）
                    int totalPoisonDamage = 36 * skillLevel[0];
                    // 2. 计算毒抗相关（直接基于你现有 EffectInstance 计算，不新增 Guardian 方法）
                    // 火伤增益：所有 POISON_RESIST 类型效果的 value 总和
                    int resistUp = calculateTotalVaule(attacker, EffectType.FIRE_BOOST);
                    // 火伤增益百分比：所有 POISON_RESIST 类型效果的 value 乘积
                    double resistUpPret = calculateTotalUpPretVaule(attacker, EffectType.FIRE_BOOST_PRET);
                    // 火伤降低：所有 POISON_RESIST_DOWN 类型效果的 value 总和
                    int resistDown = calculateTotalVaule(attacker, EffectType.FIRE_DOWN);
                    // 火伤降低百分比：所有 POISON_RESIST 类型效果的 value 乘积
                    double resistDownPret = calculateTotalDownPretVaule(attacker, EffectType.FIRE_DOWN_PRET);


                    // 2. 计算毒抗相关（直接基于你现有 EffectInstance 计算，不新增 Guardian 方法）
                    // 火抗增益：所有 POISON_RESIST 类型效果的 value 总和
                    int targetUp = calculateTotalVaule(defender, EffectType.FIRE_RESIST_BOOST);
                    // 火抗增益百分比：所有 POISON_RESIST 类型效果的 value 乘积
                    double targetUpPret = calculateTotalDownPretVaule(defender, EffectType.FIRE_RESIST_BOOST_PRET);
                    // 火抗降低：所有 POISON_RESIST_DOWN 类型效果的 value 总和
                    int targetDown = calculateTotalVaule(defender, EffectType.FIRE_RESIST_DOWN);
                    // 火抗降低百分比：所有 POISON_RESIST 类型效果的 value 乘积
                    double targetDownPret = calculateTotalUpPretVaule(defender, EffectType.FIRE_RESIST_DOWN_PRET);
                    // 最终（仅基于 buff 计算，无新增方法）

                    int finalDamage = (int) (totalPoisonDamage * resistUpPret * resistDownPret * targetUpPret * targetDownPret
                            + (resistUp - resistDown + attacker.getHyAtk() - defender.getHyDef() - targetUp + targetDown));

                    if (finalDamage < 0) {
                        finalDamage = 0;
                    }

                    // 4. 扣除伤害
                    defender.setCurrentHp(defender.getCurrentHp() - finalDamage);
                    if (defender.getCurrentHp() <= 0) {
                        defender.setDead(true);
                        defender.setOnField(false);
                        TargetBattleData data = new TargetBattleData(defender.getMaxHp(), defender.getCurrentHp(), finalDamage, defender.isOnField());

                        deadUnits.put(defender.getId(), data);
                        deadGuardians.add(defender);
                    }
                    addLog("芭蕉扇",
                            attacker.getId(),
                            attacker.getMaxHp(),
                            attacker.getCurrentHp(),
                            0,
                            attacker.isOnField(),
                            defender.getId(),
                            defender.getMaxHp(),
                            defender.getCurrentHp(),
                            finalDamage,
                            defender.isOnField(),
                            EffectType.FIRE_DAMAGE,
                            DamageType.FIRE,
                            "-" + finalDamage);
                    // 死亡日志
                    if (!deadUnits.isEmpty()) {
                        addMultiTargetLog("UNIT_DEATH",
                                null,
                                0,
                                0,
                                false,
                                deadUnits,
                                null,
                                null,
                                "死亡");
                        //触发死亡技能
                        for (Guardian g : deadGuardians) {
                            triggerOnDeathSkills(g);
                        }

                    } else {
                        //触发受击技能
                        triggerOnAttackedSkills(defender, EffectType.FIRE_DAMAGE);
                    }
                }
                break;
            case "应龙":
                if (1 == 1) {
                    // 龙息lv1攻击后随机对场上单位造成250点火焰伤害。
                    if (ProbabilityBooleanUtils.randomByProbability(0.5)) {
                        Map<String, TargetBattleData> deadUnits = new HashMap<>();
                        List<Guardian> deadGuardians = new ArrayList<>();


                        // 1. 计算所有中毒效果的总伤害（累加 POISON 类型的 value）
                        int totalPoisonDamage = 250 * skillLevel[0];
                        // 2. 计算毒抗相关（直接基于你现有 EffectInstance 计算，不新增 Guardian 方法）
                        // 火伤增益：所有 POISON_RESIST 类型效果的 value 总和
                        int resistUp = calculateTotalVaule(attacker, EffectType.FIRE_BOOST);
                        // 火伤增益百分比：所有 POISON_RESIST 类型效果的 value 乘积
                        double resistUpPret = calculateTotalUpPretVaule(attacker, EffectType.FIRE_BOOST_PRET);
                        // 火伤降低：所有 POISON_RESIST_DOWN 类型效果的 value 总和
                        int resistDown = calculateTotalVaule(attacker, EffectType.FIRE_DOWN);
                        // 火伤降低百分比：所有 POISON_RESIST 类型效果的 value 乘积
                        double resistDownPret = calculateTotalDownPretVaule(attacker, EffectType.FIRE_DOWN_PRET);


                        // 2. 计算毒抗相关（直接基于你现有 EffectInstance 计算，不新增 Guardian 方法）
                        // 火抗增益：所有 POISON_RESIST 类型效果的 value 总和
                        int targetUp = calculateTotalVaule(attacker, EffectType.FIRE_RESIST_BOOST);
                        // 火抗增益百分比：所有 POISON_RESIST 类型效果的 value 乘积
                        double targetUpPret = calculateTotalDownPretVaule(attacker, EffectType.FIRE_RESIST_BOOST_PRET);
                        // 火抗降低：所有 POISON_RESIST_DOWN 类型效果的 value 总和
                        int targetDown = calculateTotalVaule(attacker, EffectType.FIRE_RESIST_DOWN);
                        // 火抗降低百分比：所有 POISON_RESIST 类型效果的 value 乘积
                        double targetDownPret = calculateTotalUpPretVaule(attacker, EffectType.FIRE_RESIST_DOWN_PRET);
                        // 最终（仅基于 buff 计算，无新增方法）

                        int fireDamage = (int) (totalPoisonDamage * resistUpPret * resistDownPret * targetUpPret * targetDownPret
                                + (resistUp - resistDown + attacker.getHyAtk() - attacker.getHyDef() - targetUp + targetDown));

                        if (fireDamage < 0) {
                            fireDamage = 0;
                        }

                        // 4. 扣除伤害

                        attacker.setCurrentHp(attacker.getCurrentHp() - fireDamage);
                        if (attacker.getCurrentHp() <= 0) {
                            attacker.setDead(true);
                            attacker.setOnField(false);
                            TargetBattleData data = new TargetBattleData(attacker.getMaxHp(), attacker.getCurrentHp(), fireDamage, attacker.isOnField());

                            deadUnits.put(attacker.getId(), data);
                            deadGuardians.add(attacker);
                        }
                        addLog("龙息",
                                attacker.getId(),
                                attacker.getMaxHp(),
                                attacker.getCurrentHp(),
                                0,
                                attacker.isOnField(),
                                attacker.getId(),
                                attacker.getMaxHp(),
                                attacker.getCurrentHp(),
                                fireDamage,
                                defender.isOnField(),
                                EffectType.FIRE_DAMAGE,
                                DamageType.FIRE,
                                "-" + fireDamage);

                        // 死亡日志
                        if (!deadUnits.isEmpty()) {
                            addMultiTargetLog("UNIT_DEATH",
                                    null,
                                    0,
                                    0,
                                    false,
                                    deadUnits,
                                    null,
                                    null,
                                    "死亡");

                        }
                    } else {
                        Map<String, TargetBattleData> deadUnits = new HashMap<>();
                        List<Guardian> deadGuardians = new ArrayList<>();

                        // 1. 计算所有中毒效果的总伤害（累加 POISON 类型的 value）
                        int totalPoisonDamage = 250 * skillLevel[0];
                        // 2. 计算毒抗相关（直接基于你现有 EffectInstance 计算，不新增 Guardian 方法）
                        // 火伤增益：所有 POISON_RESIST 类型效果的 value 总和
                        int resistUp = calculateTotalVaule(attacker, EffectType.FIRE_BOOST);
                        // 火伤增益百分比：所有 POISON_RESIST 类型效果的 value 乘积
                        double resistUpPret = calculateTotalUpPretVaule(attacker, EffectType.FIRE_BOOST_PRET);
                        // 火伤降低：所有 POISON_RESIST_DOWN 类型效果的 value 总和
                        int resistDown = calculateTotalVaule(attacker, EffectType.FIRE_DOWN);
                        // 火伤降低百分比：所有 POISON_RESIST 类型效果的 value 乘积
                        double resistDownPret = calculateTotalDownPretVaule(attacker, EffectType.FIRE_DOWN_PRET);


                        // 2. 计算毒抗相关（直接基于你现有 EffectInstance 计算，不新增 Guardian 方法）
                        // 火抗增益：所有 POISON_RESIST 类型效果的 value 总和
                        int targetUp = calculateTotalVaule(defender, EffectType.FIRE_RESIST_BOOST);
                        // 火抗增益百分比：所有 POISON_RESIST 类型效果的 value 乘积
                        double targetUpPret = calculateTotalDownPretVaule(defender, EffectType.FIRE_RESIST_BOOST_PRET);
                        // 火抗降低：所有 POISON_RESIST_DOWN 类型效果的 value 总和
                        int targetDown = calculateTotalVaule(defender, EffectType.FIRE_RESIST_DOWN);
                        // 火抗降低百分比：所有 POISON_RESIST 类型效果的 value 乘积
                        double targetDownPret = calculateTotalUpPretVaule(defender, EffectType.FIRE_RESIST_DOWN_PRET);
                        // 最终（仅基于 buff 计算，无新增方法）

                        int fireDamage = (int) (totalPoisonDamage * resistUpPret * resistDownPret * targetUpPret * targetDownPret
                                + (resistUp - resistDown + attacker.getHyAtk() - defender.getHyDef() - targetUp + targetDown));

                        if (fireDamage < 0) {
                            fireDamage = 0;
                        }

                        // 4. 扣除伤害

                        attacker.setCurrentHp(attacker.getCurrentHp() - fireDamage);
                        if (defender.getCurrentHp() <= 0) {
                            defender.setDead(true);
                            defender.setOnField(false);
                            TargetBattleData data = new TargetBattleData(defender.getMaxHp(), defender.getCurrentHp(), fireDamage, defender.isOnField());

                            deadUnits.put(defender.getId(), data);
                            deadGuardians.add(defender);
                        }
                        addLog("龙息",
                                attacker.getId(),
                                attacker.getMaxHp(),
                                attacker.getCurrentHp(),
                                0,
                                attacker.isOnField(),
                                defender.getId(),
                                defender.getMaxHp(),
                                defender.getCurrentHp(),
                                fireDamage,
                                defender.isOnField(),
                                EffectType.FIRE_DAMAGE,
                                DamageType.FIRE,
                                "-" + fireDamage);
                        // 死亡日志
                        if (!deadUnits.isEmpty()) {
                            addMultiTargetLog("UNIT_DEATH",
                                    null,
                                    0,
                                    0,
                                    false,
                                    deadUnits,
                                    null,
                                    null,
                                    "死亡");
                            //触发死亡技能
                            for (Guardian g : deadGuardians) {
                                triggerOnDeathSkills(g);
                            }

                        } else {
                            //触发受击技能
                            triggerOnAttackedSkills(defender, EffectType.FIRE_DAMAGE);
                        }
                    }

                }
                break;
            case "聂小倩":
                if (skillLevel[1] > 0&&defender.isPoison()) {
                    // 芭蕉扇：造成火焰伤害
                    int fireDamage = 60 * skillLevel[1];
                    defender.setCurrentHp(defender.getCurrentHp() - fireDamage);
                    Map<String, TargetBattleData> deadUnits = new HashMap<>();
                    List<Guardian> deadGuardians = new ArrayList<>();
                    if (defender.getCurrentHp() <= 0) {
                        defender.setDead(true);
                        defender.setOnField(false);
                        TargetBattleData data = new TargetBattleData(defender.getMaxHp(), defender.getCurrentHp(), fireDamage, defender.isOnField());
                        deadUnits.put(defender.getId(), data);
                        deadGuardians.add(defender);
                    }
//                    剧毒痛击lv1攻击中毒单位时，额外造成60点伤害。
                    addLog("剧毒痛击",
                            attacker.getId(),
                            attacker.getMaxHp(),
                            attacker.getCurrentHp(),
                            0,
                            attacker.isOnField(),
                            defender.getId(),
                            defender.getMaxHp(),
                            defender.getCurrentHp(),
                            fireDamage,
                            defender.isOnField(),
                            EffectType.DAMAGE,
                            DamageType.PHYSICAL,
                            "-" + fireDamage);

                    // 死亡日志
                    if (!deadUnits.isEmpty()) {
                        addMultiTargetLog("UNIT_DEATH",
                                null,
                                0,
                                0,
                                false,
                                deadUnits,
                                null,
                                null,
                                "死亡");
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
            case "狮驼王":
                if (ProbabilityBooleanUtils.randomByProbability(0.5)) {
                    List<Guardian> enemies = defender.getCamp() == Camp.A ? campB : campA;
                    if (!enemies.isEmpty()) {
                        Guardian randomEnemy = enemies.get(random.nextInt(enemies.size()));
                        int totalPoisonDamage = 142 * skillLevel[0];
                        // 1. 计算所有中毒效果的总伤害（累加 POISON 类型的 value）
                        // 2. 计算毒抗相关（直接基于你现有 EffectInstance 计算，不新增 Guardian 方法）
                        // 中毒增益：所有 POISON_RESIST 类型效果的 value 总和
                        int resistUp = calculateTotalVaule(attacker, EffectType.POISON_BOOST);
                        // 中毒增益百分比：所有 POISON_RESIST 类型效果的 value 乘积
                        double resistUpPret = calculateTotalUpPretVaule(attacker, EffectType.POISON_BOOST_PRET);
                        // 中毒降低：所有 POISON_RESIST_DOWN 类型效果的 value 总和
                        int resistDown = calculateTotalVaule(attacker, EffectType.POISON_DOWN);
                        // 中毒降低百分比：所有 POISON_RESIST 类型效果的 value 乘积
                        double resistDownPret = calculateTotalDownPretVaule(attacker, EffectType.POISON_DOWN_PRET);

                        int poisonValue = (int) (totalPoisonDamage * resistUpPret * resistDownPret + (resistUp + attacker.getDsAtk() - resistDown));
                        randomEnemy.addEffect(EffectType.POISON, poisonValue, 99, attacker.getId());
                        addLog("毒气阻碍",
                                attacker.getId(),
                                attacker.getMaxHp(),
                                attacker.getCurrentHp(),
                                poisonValue,
                                attacker.isOnField(),
                                randomEnemy.getId(),
                                randomEnemy.getMaxHp(),
                                randomEnemy.getCurrentHp(),
                                poisonValue,
                                randomEnemy.isOnField(),
                                EffectType.POISON,
                                DamageType.POISON,
                                "中毒+" + poisonValue);
                    }
                }
                break;
            case "辟暑大王":
                // 毒素打击Lv1攻击后令敌方中毒，每回合损失47点生命值;辟尘大王协同Lv1与辟尘大王在同一队伍时，增加自身200点生命上限，50点攻击，50点速度。
                if (!defender.isDead()) {
                    int totalPoisonDamage = 47 * skillLevel[0];
                    // 1. 计算所有中毒效果的总伤害（累加 POISON 类型的 value）
                    // 2. 计算毒抗相关（直接基于你现有 EffectInstance 计算，不新增 Guardian 方法）
                    // 中毒增益：所有 POISON_RESIST 类型效果的 value 总和
                    int resistUp = calculateTotalVaule(attacker, EffectType.POISON_BOOST);
                    // 中毒增益百分比：所有 POISON_RESIST 类型效果的 value 乘积
                    double resistUpPret = calculateTotalUpPretVaule(attacker, EffectType.POISON_BOOST_PRET);
                    // 中毒降低：所有 POISON_RESIST_DOWN 类型效果的 value 总和
                    int resistDown = calculateTotalVaule(attacker, EffectType.POISON_DOWN);
                    // 中毒降低百分比：所有 POISON_RESIST 类型效果的 value 乘积
                    double resistDownPret = calculateTotalDownPretVaule(attacker, EffectType.POISON_DOWN_PRET);

                    int poisonValue = (int) (totalPoisonDamage * resistUpPret * resistDownPret + (resistUp + attacker.getDsAtk() - resistDown));
                    defender.addEffect(EffectType.POISON, poisonValue + attacker.getDsAtk(), 99, attacker.getId());
                    addLog("毒素打击",
                            attacker.getId(),
                            attacker.getMaxHp(),
                            attacker.getCurrentHp(),
                            poisonValue,
                            attacker.isOnField(),
                            defender.getId(),
                            defender.getMaxHp(),
                            defender.getCurrentHp(),
                            poisonValue,
                            defender.isOnField(),
                            EffectType.POISON,
                            DamageType.POISON,
                            "中毒" + poisonValue);

                }
                break;
            case "天蓬元帅":
                if (skillLevel[1] > 0) {
                    if (ProbabilityBooleanUtils.randomByProbability(0.1 * skillLevel[1])) {
//                    醉钉耙Lv1场上，攻击后有10%几率对随机敌方造成真实伤害，数值等同于目标力量的50%；
                        int fireDamage = (int) (0.5 * defender.getAttack());
                        Map<String, TargetBattleData> deadUnits = new HashMap<>();
                        List<Guardian> deadGuardians = new ArrayList<>();
                        defender.setCurrentHp(defender.getCurrentHp() - fireDamage);
                        if (defender.getCurrentHp() <= 0) {
                            defender.setDead(true);
                            defender.setOnField(false);
                            TargetBattleData data = new TargetBattleData(defender.getMaxHp(), defender.getCurrentHp(), fireDamage, defender.isOnField());
                            deadUnits.put(defender.getId(), data);
                            deadGuardians.add(defender);
                        }
                        addLog("醉钉耙",
                                attacker.getId(),
                                attacker.getMaxHp(),
                                attacker.getCurrentHp(),
                                0,
                                attacker.isOnField(),
                                defender.getId(),
                                defender.getMaxHp(),
                                defender.getCurrentHp(),
                                fireDamage,
                                defender.isOnField(),
                                EffectType.DAMAGE,
                                DamageType.TRUE,
                                "-" + fireDamage);
                        // 死亡日志
                        if (!deadUnits.isEmpty()) {
                            addMultiTargetLog("UNIT_DEATH",
                                    null,
                                    0,
                                    0,
                                    false,
                                    deadUnits,
                                    null,
                                    null,
                                    "死亡");
                            //触发死亡技能
                            for (Guardian g : deadGuardians) {
                                triggerOnDeathSkills(g);
                            }

                        } else {
                            //触发受击技能
                            triggerOnAttackedSkills(defender, EffectType.TRUE_DAMAGE);
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
                        Map<String, TargetBattleData> deadUnits = new HashMap<>();
                        List<Guardian> deadGuardians = new ArrayList<>();
                        Guardian guardian = enemieList.get(0);
                        int fireDamage = 125 * skillLevel[0];
                        guardian.setCurrentHp(guardian.getCurrentHp() - fireDamage);
                        if (guardian.getCurrentHp() <= 0) {
                            guardian.setDead(true);
                            guardian.setOnField(false);
                            TargetBattleData data = new TargetBattleData(guardian.getMaxHp(), guardian.getCurrentHp(), fireDamage, guardian.isOnField());
                            deadUnits.put(guardian.getId(), data);
                            deadGuardians.add(guardian);
                        }
                        addLog("穿云斩",
                                attacker.getId(),
                                attacker.getMaxHp(),
                                attacker.getCurrentHp(),
                                0,
                                attacker.isOnField(),
                                guardian.getId(),
                                guardian.getMaxHp(),
                                guardian.getCurrentHp(),
                                fireDamage,
                                guardian.isOnField(),
                                EffectType.DAMAGE,
                                DamageType.TRUE,
                                "-" + fireDamage);
                        // 死亡日志
                        if (!deadUnits.isEmpty()) {
                            addMultiTargetLog("UNIT_DEATH",
                                    null,
                                    0,
                                    0,
                                    false,
                                    deadUnits,
                                    null,
                                    null,
                                    "死亡");
                            //触发死亡技能
                            for (Guardian g : deadGuardians) {
                                triggerOnDeathSkills(g);
                            }

                        } else {
                            //触发受击技能
                            triggerOnAttackedSkills(guardian, EffectType.TRUE_DAMAGE);
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
                        Map<String, TargetBattleData> deadUnits = new HashMap<>();
                        List<Guardian> deadGuardians = new ArrayList<>();
                        Guardian guardian = enemieList.get(0);
                        int fireDamage = 100 * skillLevel[0];
                        guardian.setCurrentHp(guardian.getCurrentHp() - fireDamage);
                        if (guardian.getCurrentHp() <= 0) {
                            guardian.setDead(true);
                            guardian.setOnField(false);
                            TargetBattleData data = new TargetBattleData(guardian.getMaxHp(), guardian.getCurrentHp(), fireDamage, guardian.isOnField());
                            deadUnits.put(guardian.getId(), data);
                            deadGuardians.add(guardian);
                        }

                        addLog("穿云剑法",
                                attacker.getId(),
                                attacker.getMaxHp(),
                                attacker.getCurrentHp(),
                                0,
                                attacker.isOnField(),
                                guardian.getId(),
                                guardian.getMaxHp(),
                                guardian.getCurrentHp(),
                                fireDamage,
                                guardian.isOnField(),
                                EffectType.DAMAGE,
                                DamageType.TRUE,
                                "-" + fireDamage);
                        // 死亡日志
                        if (!deadUnits.isEmpty()) {
                            addMultiTargetLog("UNIT_DEATH",
                                    null,
                                    0,
                                    0,
                                    false,
                                    deadUnits,
                                    null,
                                    null,
                                    "死亡");
                            //触发死亡技能
                            for (Guardian g : deadGuardians) {
                                triggerOnDeathSkills(g);
                            }

                        } else {
                            //触发受击技能
                            triggerOnAttackedSkills(guardian, EffectType.TRUE_DAMAGE);
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
                        Map<String, TargetBattleData> deadUnits = new HashMap<>();
                        List<Guardian> deadGuardians = new ArrayList<>();
                        Guardian guardian = enemieList.get(0);
                        int fireDamage = 125 * skillLevel[0];
                        guardian.setCurrentHp(guardian.getCurrentHp() - fireDamage);
                        if (guardian.getCurrentHp() <= 0) {
                            guardian.setDead(true);
                            guardian.setOnField(false);
                            TargetBattleData data = new TargetBattleData(guardian.getMaxHp(), guardian.getCurrentHp(), fireDamage, guardian.isOnField());
                            deadUnits.put(guardian.getId(), data);
                            deadGuardians.add(guardian);
                        }
                        addLog("穿云剑",
                                attacker.getId(),
                                attacker.getMaxHp(),
                                attacker.getCurrentHp(),
                                0,
                                attacker.isOnField(),
                                guardian.getId(),
                                guardian.getMaxHp(),
                                guardian.getCurrentHp(),
                                fireDamage,
                                guardian.isOnField(),
                                EffectType.TRUE_DAMAGE,
                                DamageType.PHYSICAL,
                                "-" + fireDamage);
                        // 死亡日志
                        if (!deadUnits.isEmpty()) {
                            addMultiTargetLog("UNIT_DEATH",
                                    null,
                                    0,
                                    0,
                                    false,
                                    deadUnits,
                                    null,
                                    null,
                                    "死亡");
                            //触发死亡技能
                            for (Guardian g : deadGuardians) {
                                triggerOnDeathSkills(g);
                            }

                        } else {
                            //触发受击技能
                            triggerOnAttackedSkills(guardian, EffectType.TRUE_DAMAGE);
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
                        Map<String, TargetBattleData> deadUnits = new HashMap<>();
                        List<Guardian> deadGuardians = new ArrayList<>();
                        Guardian guardian = enemieList.get(0);
                        int fireDamage = 150 * skillLevel[1];
                        guardian.setCurrentHp(guardian.getCurrentHp() - fireDamage);
                        if (guardian.getCurrentHp() <= 0) {
                            guardian.setDead(true);
                            guardian.setOnField(false);
                            TargetBattleData data = new TargetBattleData(guardian.getMaxHp(), guardian.getCurrentHp(), fireDamage, guardian.isOnField());
                            deadUnits.put(guardian.getId(), data);
                            deadGuardians.add(guardian);
                        }
                        addLog("圣灵斩",
                                attacker.getId(),
                                attacker.getMaxHp(),
                                attacker.getCurrentHp(),
                                0,
                                attacker.isOnField(),
                                guardian.getId(),
                                guardian.getMaxHp(),
                                guardian.getCurrentHp(),
                                fireDamage,
                                guardian.isOnField(),
                                EffectType.DAMAGE,
                                DamageType.TRUE,
                                "-" + fireDamage);
                        // 死亡日志
                        if (!deadUnits.isEmpty()) {
                            addMultiTargetLog("UNIT_DEATH",
                                    null,
                                    0,
                                    0,
                                    false,
                                    deadUnits,
                                    null,
                                    null,
                                    "死亡");
                            //触发死亡技能
                            for (Guardian g : deadGuardians) {
                                triggerOnDeathSkills(g);
                            }

                        } else {
                            //触发受击技能
                            triggerOnAttackedSkills(guardian, EffectType.TRUE_DAMAGE);
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
                        Map<String, TargetBattleData> deadUnits = new HashMap<>();
                        List<Guardian> deadGuardians = new ArrayList<>();
                        Guardian guardian = enemieList.get(0);
                        int fireDamage = 100 * skillLevel[0];
                        guardian.setCurrentHp(guardian.getCurrentHp() - fireDamage);
                        if (guardian.getCurrentHp() <= 0) {
                            guardian.setDead(true);
                            guardian.setOnField(false);
                            TargetBattleData data = new TargetBattleData(guardian.getMaxHp(), guardian.getCurrentHp(), fireDamage, guardian.isOnField());

                            deadUnits.put(guardian.getId(), data);
                            deadGuardians.add(guardian);
                        }

                        addLog("穿云长枪",
                                attacker.getId(),
                                attacker.getMaxHp(),
                                attacker.getCurrentHp(),
                                0,
                                attacker.isOnField(),
                                guardian.getId(),
                                guardian.getMaxHp(),
                                guardian.getCurrentHp(),
                                fireDamage,
                                guardian.isOnField(),
                                EffectType.DAMAGE,
                                DamageType.TRUE,
                                "-" + fireDamage);
                        // 死亡日志
                        if (!deadUnits.isEmpty()) {
                            addMultiTargetLog("UNIT_DEATH",
                                    null,
                                    0,
                                    0,
                                    false,
                                    deadUnits,
                                    null,
                                    null,
                                    "死亡");
                            //触发死亡技能
                            for (Guardian g : deadGuardians) {
                                triggerOnDeathSkills(g);
                            }

                        } else {
                            //触发受击技能
                            triggerOnAttackedSkills(guardian, EffectType.TRUE_DAMAGE);
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
                        Map<String, TargetBattleData> deadUnits = new HashMap<>();
                        List<Guardian> deadGuardians = new ArrayList<>();
                        int lavaDamage = 62 * skillLevel[0];

                        Map<String, TargetBattleData> targetStatus = new HashMap<>();

                        offFieldEnemies.forEach(g -> {
                            // 1. 计算所有中毒效果的总伤害（累加 POISON 类型的 value）
                            int totalPoisonDamage = lavaDamage;
                            // 2. 计算毒抗相关（直接基于你现有 EffectInstance 计算，不新增 Guardian 方法）
                            // 火伤增益：所有 POISON_RESIST 类型效果的 value 总和
                            int resistUp = calculateTotalVaule(attacker, EffectType.FIRE_BOOST);
                            // 火伤增益百分比：所有 POISON_RESIST 类型效果的 value 乘积
                            double resistUpPret = calculateTotalUpPretVaule(attacker, EffectType.FIRE_BOOST_PRET);
                            // 火伤降低：所有 POISON_RESIST_DOWN 类型效果的 value 总和
                            int resistDown = calculateTotalVaule(attacker, EffectType.FIRE_DOWN);
                            // 火伤降低百分比：所有 POISON_RESIST 类型效果的 value 乘积
                            double resistDownPret = calculateTotalDownPretVaule(attacker, EffectType.FIRE_DOWN_PRET);


                            // 2. 计算毒抗相关（直接基于你现有 EffectInstance 计算，不新增 Guardian 方法）
                            // 火抗增益：所有 POISON_RESIST 类型效果的 value 总和
                            int targetUp = calculateTotalVaule(g, EffectType.FIRE_RESIST_BOOST);
                            // 火抗增益百分比：所有 POISON_RESIST 类型效果的 value 乘积
                            double targetUpPret = calculateTotalDownPretVaule(g, EffectType.FIRE_RESIST_BOOST_PRET);
                            // 火抗降低：所有 POISON_RESIST_DOWN 类型效果的 value 总和
                            int targetDown = calculateTotalVaule(g, EffectType.FIRE_RESIST_DOWN);
                            // 火抗降低百分比：所有 POISON_RESIST 类型效果的 value 乘积
                            double targetDownPret = calculateTotalUpPretVaule(g, EffectType.FIRE_RESIST_DOWN_PRET);
                            // 最终（仅基于 buff 计算，无新增方法）

                            int finalDamage = (int) (totalPoisonDamage * resistUpPret * resistDownPret * targetUpPret * targetDownPret
                                    + (resistUp - resistDown + attacker.getHyAtk() - g.getHyDef() - targetUp + targetDown));

                            if (finalDamage < 0) {
                                finalDamage = 0;
                            }

                            // 4. 扣除伤害
                            g.setCurrentHp(g.getCurrentHp() - finalDamage);
                            TargetBattleData data = new TargetBattleData(g.getMaxHp(), g.getCurrentHp(), finalDamage, g.isOnField());
                            targetStatus.put(g.getId(), data);
                            if (g.isDead()) {

                                deadUnits.put(g.getId(), data);
                                deadGuardians.add(g);
                            }
                        });

                        // 单条日志记录多目标
                        addMultiTargetLog("熔岩爆发",
                                attacker.getId(),
                                attacker.getMaxHp(),
                                attacker.getCurrentHp(),
                                attacker.isOnField(),
                                targetStatus,
                                EffectType.FIRE_DAMAGE,
                                DamageType.FIRE,
                                "对全体造成火焰伤害");
                        // 死亡日志
                        if (!deadUnits.isEmpty()) {
                            addMultiTargetLog("UNIT_DEATH",
                                    null,
                                    0,
                                    0,
                                    false,
                                    deadUnits,
                                    null,
                                    null,
                                    "死亡");
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
            case "红孩儿":
                if (skillLevel[1] > 0) {
                    // 鞭挞Lv1每回合增加自身40点生命上限；三昧真火Lv1每回合有50%几率对敌我全体造成16点火焰伤害。
                    List<Guardian> offFieldEnemies = new ArrayList<>();
                    if (ProbabilityBooleanUtils.randomByProbability(0.5)) {
                        offFieldEnemies = campA.stream().filter(g -> !g.isDead()).collect(Collectors.toList());
                    } else {
                        offFieldEnemies = campB.stream().filter(g -> !g.isDead()).collect(Collectors.toList());
                    }

                    if (!offFieldEnemies.isEmpty()) {
                        Map<String, TargetBattleData> deadUnits = new HashMap<>();
                        List<Guardian> deadGuardians = new ArrayList<>();
                        int lavaDamage = 62 * skillLevel[0];

                        Map<String, TargetBattleData> targetStatus = new HashMap<>();

                        offFieldEnemies.forEach(g -> {
                            // 1. 计算所有中毒效果的总伤害（累加 POISON 类型的 value）
                            int totalPoisonDamage = lavaDamage;
                            // 2. 计算毒抗相关（直接基于你现有 EffectInstance 计算，不新增 Guardian 方法）
                            // 火伤增益：所有 POISON_RESIST 类型效果的 value 总和
                            int resistUp = calculateTotalVaule(attacker, EffectType.FIRE_BOOST);
                            // 火伤增益百分比：所有 POISON_RESIST 类型效果的 value 乘积
                            double resistUpPret = calculateTotalUpPretVaule(attacker, EffectType.FIRE_BOOST_PRET);
                            // 火伤降低：所有 POISON_RESIST_DOWN 类型效果的 value 总和
                            int resistDown = calculateTotalVaule(attacker, EffectType.FIRE_DOWN);
                            // 火伤降低百分比：所有 POISON_RESIST 类型效果的 value 乘积
                            double resistDownPret = calculateTotalDownPretVaule(attacker, EffectType.FIRE_DOWN_PRET);


                            // 2. 计算毒抗相关（直接基于你现有 EffectInstance 计算，不新增 Guardian 方法）
                            // 火抗增益：所有 POISON_RESIST 类型效果的 value 总和
                            int targetUp = calculateTotalVaule(g, EffectType.FIRE_RESIST_BOOST);
                            // 火抗增益百分比：所有 POISON_RESIST 类型效果的 value 乘积
                            double targetUpPret = calculateTotalDownPretVaule(g, EffectType.FIRE_RESIST_BOOST_PRET);
                            // 火抗降低：所有 POISON_RESIST_DOWN 类型效果的 value 总和
                            int targetDown = calculateTotalVaule(g, EffectType.FIRE_RESIST_DOWN);
                            // 火抗降低百分比：所有 POISON_RESIST 类型效果的 value 乘积
                            double targetDownPret = calculateTotalUpPretVaule(g, EffectType.FIRE_RESIST_DOWN_PRET);
                            // 最终（仅基于 buff 计算，无新增方法）

                            int finalDamage = (int) (totalPoisonDamage * resistUpPret * resistDownPret * targetUpPret * targetDownPret
                                    + (resistUp - resistDown + attacker.getHyAtk() - g.getHyDef() - targetUp + targetDown));

                            if (finalDamage < 0) {
                                finalDamage = 0;
                            }

                            // 4. 扣除伤害
                            g.setCurrentHp(g.getCurrentHp() - finalDamage);
                            TargetBattleData data = new TargetBattleData(g.getMaxHp(), g.getCurrentHp(), finalDamage, g.isOnField());
                            targetStatus.put(g.getId(), data);
                            if (g.isDead()) {

                                deadUnits.put(g.getId(), data);
                                deadGuardians.add(g);
                            }
                        });

                        // 单条日志记录多目标
                        addMultiTargetLog("三昧真火",
                                attacker.getId(),
                                attacker.getMaxHp(),
                                attacker.getCurrentHp(),
                                attacker.isOnField(),
                                targetStatus,
                                EffectType.FIRE_DAMAGE,
                                DamageType.FIRE,
                                "对全体造成火焰伤害");
                        // 死亡日志
                        if (!deadUnits.isEmpty()) {
                            addMultiTargetLog("UNIT_DEATH",
                                    null,
                                    0,
                                    0,
                                    false,
                                    deadUnits,
                                    null,
                                    null,
                                    "死亡");
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
            case "银角大王":
                if (1 == 1) {
                    // 紫金葫芦Lv1攻击后对全体敌方造成22点火焰伤害;金角大王协同Lv1与金角大王在同一队伍时，增加自身200点生命上限，50点攻击，50点速度。
                    List<Guardian> offFieldEnemies = defender.getCamp() == Camp.A ?
                            campB.stream().filter(g -> !g.isDead()).collect(Collectors.toList()) :
                            campA.stream().filter(g -> !g.isDead()).collect(Collectors.toList());

                    if (!offFieldEnemies.isEmpty()) {
                        Map<String, TargetBattleData> deadUnits = new HashMap<>();
                        List<Guardian> deadGuardians = new ArrayList<>();
                        int lavaDamage = 22 * skillLevel[0];

                        Map<String, TargetBattleData> targetStatus = new HashMap<>();

                        offFieldEnemies.forEach(g -> {
                            // 1. 计算所有中毒效果的总伤害（累加 POISON 类型的 value）
                            int totalPoisonDamage = lavaDamage;
                            // 2. 计算毒抗相关（直接基于你现有 EffectInstance 计算，不新增 Guardian 方法）
                            // 火伤增益：所有 POISON_RESIST 类型效果的 value 总和
                            int resistUp = calculateTotalVaule(attacker, EffectType.FIRE_BOOST);
                            // 火伤增益百分比：所有 POISON_RESIST 类型效果的 value 乘积
                            double resistUpPret = calculateTotalUpPretVaule(attacker, EffectType.FIRE_BOOST_PRET);
                            // 火伤降低：所有 POISON_RESIST_DOWN 类型效果的 value 总和
                            int resistDown = calculateTotalVaule(attacker, EffectType.FIRE_DOWN);
                            // 火伤降低百分比：所有 POISON_RESIST 类型效果的 value 乘积
                            double resistDownPret = calculateTotalDownPretVaule(attacker, EffectType.FIRE_DOWN_PRET);


                            // 2. 计算毒抗相关（直接基于你现有 EffectInstance 计算，不新增 Guardian 方法）
                            // 火抗增益：所有 POISON_RESIST 类型效果的 value 总和
                            int targetUp = calculateTotalVaule(g, EffectType.FIRE_RESIST_BOOST);
                            // 火抗增益百分比：所有 POISON_RESIST 类型效果的 value 乘积
                            double targetUpPret = calculateTotalDownPretVaule(g, EffectType.FIRE_RESIST_BOOST_PRET);
                            // 火抗降低：所有 POISON_RESIST_DOWN 类型效果的 value 总和
                            int targetDown = calculateTotalVaule(g, EffectType.FIRE_RESIST_DOWN);
                            // 火抗降低百分比：所有 POISON_RESIST 类型效果的 value 乘积
                            double targetDownPret = calculateTotalUpPretVaule(g, EffectType.FIRE_RESIST_DOWN_PRET);
                            // 最终（仅基于 buff 计算，无新增方法）

                            int finalDamage = (int) (totalPoisonDamage * resistUpPret * resistDownPret * targetUpPret * targetDownPret
                                    + (resistUp - resistDown + attacker.getHyAtk() - g.getHyDef() - targetUp + targetDown));

                            if (finalDamage < 0) {
                                finalDamage = 0;
                            }

                            // 4. 扣除伤害
                            g.setCurrentHp(g.getCurrentHp() - finalDamage);
                            TargetBattleData data = new TargetBattleData(g.getMaxHp(), g.getCurrentHp(), finalDamage, g.isOnField());
                            targetStatus.put(g.getId(), data);
                            if (g.isDead()) {

                                deadUnits.put(g.getId(), data);
                                deadGuardians.add(g);
                            }
                        });


                        // 单条日志记录多目标
                        addMultiTargetLog("紫金葫芦",
                                attacker.getId(),
                                attacker.getMaxHp(),
                                attacker.getCurrentHp(),
                                attacker.isOnField(),
                                targetStatus,
                                EffectType.FIRE_DAMAGE,
                                DamageType.FIRE,
                                "对全体造成火焰伤害");
                        // 死亡日志
                        if (!deadUnits.isEmpty()) {
                            addMultiTargetLog("UNIT_DEATH",
                                    null,
                                    0,
                                    0,
                                    false,
                                    deadUnits,
                                    null,
                                    null,
                                    "死亡");
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
                        Map<String, TargetBattleData> deadUnits = new HashMap<>();
                        List<Guardian> deadGuardians = new ArrayList<>();
                        int reduce = 0;
                        reduce = 62 * skillLevel[0];

                        Map<String, TargetBattleData> targetStatus = new HashMap<>();

                        int lavaDamage = reduce;
                        aliveUnits.forEach(g -> {
                            // 1. 计算所有中毒效果的总伤害（累加 POISON 类型的 value）
                            int totalPoisonDamage = lavaDamage;
                            // 2. 计算毒抗相关（直接基于你现有 EffectInstance 计算，不新增 Guardian 方法）
                            // 火伤增益：所有 POISON_RESIST 类型效果的 value 总和
                            int resistUp = calculateTotalVaule(attacker, EffectType.FIRE_BOOST);
                            // 火伤增益百分比：所有 POISON_RESIST 类型效果的 value 乘积
                            double resistUpPret = calculateTotalUpPretVaule(attacker, EffectType.FIRE_BOOST_PRET);
                            // 火伤降低：所有 POISON_RESIST_DOWN 类型效果的 value 总和
                            int resistDown = calculateTotalVaule(attacker, EffectType.FIRE_DOWN);
                            // 火伤降低百分比：所有 POISON_RESIST 类型效果的 value 乘积
                            double resistDownPret = calculateTotalDownPretVaule(attacker, EffectType.FIRE_DOWN_PRET);


                            // 2. 计算毒抗相关（直接基于你现有 EffectInstance 计算，不新增 Guardian 方法）
                            // 火抗增益：所有 POISON_RESIST 类型效果的 value 总和
                            int targetUp = calculateTotalVaule(g, EffectType.FIRE_RESIST_BOOST);
                            // 火抗增益百分比：所有 POISON_RESIST 类型效果的 value 乘积
                            double targetUpPret = calculateTotalDownPretVaule(g, EffectType.FIRE_RESIST_BOOST_PRET);
                            // 火抗降低：所有 POISON_RESIST_DOWN 类型效果的 value 总和
                            int targetDown = calculateTotalVaule(g, EffectType.FIRE_RESIST_DOWN);
                            // 火抗降低百分比：所有 POISON_RESIST 类型效果的 value 乘积
                            double targetDownPret = calculateTotalUpPretVaule(g, EffectType.FIRE_RESIST_DOWN_PRET);
                            // 最终（仅基于 buff 计算，无新增方法）

                            int finalDamage = (int) (totalPoisonDamage * resistUpPret * resistDownPret * targetUpPret * targetDownPret
                                    + (resistUp - resistDown + attacker.getHyAtk() - g.getHyDef() - targetUp + targetDown));

                            if (finalDamage < 0) {
                                finalDamage = 0;
                            }

                            // 4. 扣除伤害
                            g.setCurrentHp(g.getCurrentHp() - finalDamage);
                            TargetBattleData data = new TargetBattleData(g.getMaxHp(), g.getCurrentHp(), finalDamage, g.isOnField());
                            targetStatus.put(g.getId(), data);
                            if (g.isDead()) {

                                deadUnits.put(g.getId(), data);
                                deadGuardians.add(g);
                            }
                        });

                        addMultiTargetLog("三昧真火",
                                attacker.getId(),
                                attacker.getMaxHp(),
                                attacker.getCurrentHp(),
                                attacker.isOnField(),
                                targetStatus,
                                EffectType.FIRE_DAMAGE,
                                DamageType.FIRE,
                                "对全体造成火焰伤害");
                        // 死亡日志
                        if (!deadUnits.isEmpty()) {
                            addMultiTargetLog("UNIT_DEATH",
                                    null,
                                    0,
                                    0,
                                    false,
                                    deadUnits,
                                    null,
                                    null,
                                    "死亡");
                            //触发死亡技能
                            for (Guardian g : deadGuardians) {
                                triggerOnDeathSkills(g);
                            }
                        }
                        //触发受击技能
                        aliveUnits.forEach(g -> {
                            //触发受到任意伤害技能
                            triggerOnAttackedSkills(g, EffectType.FIRE_DAMAGE);
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
                        Map<String, TargetBattleData> deadUnits = new HashMap<>();
                        List<Guardian> deadGuardians = new ArrayList<>();
                        int lavaDamage = 12 * skillLevel[0];

                        Map<String, TargetBattleData> targetStatus = new HashMap<>();

                        offFieldEnemies.forEach(g -> {
                            // 1. 计算所有中毒效果的总伤害（累加 POISON 类型的 value）
                            int totalPoisonDamage = lavaDamage;
                            // 2. 计算毒抗相关（直接基于你现有 EffectInstance 计算，不新增 Guardian 方法）
                            // 火伤增益：所有 POISON_RESIST 类型效果的 value 总和
                            int resistUp = calculateTotalVaule(attacker, EffectType.FIRE_BOOST);
                            // 火伤增益百分比：所有 POISON_RESIST 类型效果的 value 乘积
                            double resistUpPret = calculateTotalUpPretVaule(attacker, EffectType.FIRE_BOOST_PRET);
                            // 火伤降低：所有 POISON_RESIST_DOWN 类型效果的 value 总和
                            int resistDown = calculateTotalVaule(attacker, EffectType.FIRE_DOWN);
                            // 火伤降低百分比：所有 POISON_RESIST 类型效果的 value 乘积
                            double resistDownPret = calculateTotalDownPretVaule(attacker, EffectType.FIRE_DOWN_PRET);


                            // 2. 计算毒抗相关（直接基于你现有 EffectInstance 计算，不新增 Guardian 方法）
                            // 火抗增益：所有 POISON_RESIST 类型效果的 value 总和
                            int targetUp = calculateTotalVaule(g, EffectType.FIRE_RESIST_BOOST);
                            // 火抗增益百分比：所有 POISON_RESIST 类型效果的 value 乘积
                            double targetUpPret = calculateTotalDownPretVaule(g, EffectType.FIRE_RESIST_BOOST_PRET);
                            // 火抗降低：所有 POISON_RESIST_DOWN 类型效果的 value 总和
                            int targetDown = calculateTotalVaule(g, EffectType.FIRE_RESIST_DOWN);
                            // 火抗降低百分比：所有 POISON_RESIST 类型效果的 value 乘积
                            double targetDownPret = calculateTotalUpPretVaule(g, EffectType.FIRE_RESIST_DOWN_PRET);
                            // 最终（仅基于 buff 计算，无新增方法）

                            int finalDamage = (int) (totalPoisonDamage * resistUpPret * resistDownPret * targetUpPret * targetDownPret
                                    + (resistUp - resistDown + attacker.getHyAtk() - g.getHyDef() - targetUp + targetDown));

                            if (finalDamage < 0) {
                                finalDamage = 0;
                            }

                            // 4. 扣除伤害
                            g.setCurrentHp(g.getCurrentHp() - finalDamage);
                            TargetBattleData data = new TargetBattleData(g.getMaxHp(), g.getCurrentHp(), finalDamage, g.isOnField());
                            targetStatus.put(g.getId(), data);
                            if (g.isDead()) {


                                deadUnits.put(g.getId(), data);
                                deadGuardians.add(g);
                            }
                        });

                        // 单条日志记录多目标
                        addMultiTargetLog("妖火",
                                attacker.getId(),
                                attacker.getMaxHp(),
                                attacker.getCurrentHp(),
                                attacker.isOnField(),
                                targetStatus,
                                EffectType.FIRE_DAMAGE,
                                DamageType.FIRE,
                                "对全体造成火焰伤害");
                        // 死亡日志
                        if (!deadUnits.isEmpty()) {
                            addMultiTargetLog("UNIT_DEATH",
                                    null,
                                    0,
                                    0,
                                    false,
                                    deadUnits,
                                    null,
                                    null,
                                    "死亡");
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
            case "赛太岁":
                if (1 == 1) {
                    // 焚烧lv1攻击后对全体敌方造成12点火焰伤害
                    List<Guardian> offFieldEnemies = defender.getCamp() == Camp.A ?
                            campA.stream().filter(g -> !g.isDead()).collect(Collectors.toList()) :
                            campB.stream().filter(g -> !g.isDead()).collect(Collectors.toList());

                    if (!offFieldEnemies.isEmpty()) {
                        Map<String, TargetBattleData> deadUnits = new HashMap<>();
                        List<Guardian> deadGuardians = new ArrayList<>();
                        int lavaDamage = 12 * skillLevel[0];

                        Map<String, TargetBattleData> targetStatus = new HashMap<>();

                        offFieldEnemies.forEach(g -> {
                            // 1. 计算所有中毒效果的总伤害（累加 POISON 类型的 value）
                            int totalPoisonDamage = lavaDamage;
                            // 2. 计算毒抗相关（直接基于你现有 EffectInstance 计算，不新增 Guardian 方法）
                            // 火伤增益：所有 POISON_RESIST 类型效果的 value 总和
                            int resistUp = calculateTotalVaule(attacker, EffectType.FIRE_BOOST);
                            // 火伤增益百分比：所有 POISON_RESIST 类型效果的 value 乘积
                            double resistUpPret = calculateTotalUpPretVaule(attacker, EffectType.FIRE_BOOST_PRET);
                            // 火伤降低：所有 POISON_RESIST_DOWN 类型效果的 value 总和
                            int resistDown = calculateTotalVaule(attacker, EffectType.FIRE_DOWN);
                            // 火伤降低百分比：所有 POISON_RESIST 类型效果的 value 乘积
                            double resistDownPret = calculateTotalDownPretVaule(attacker, EffectType.FIRE_DOWN_PRET);


                            // 2. 计算毒抗相关（直接基于你现有 EffectInstance 计算，不新增 Guardian 方法）
                            // 火抗增益：所有 POISON_RESIST 类型效果的 value 总和
                            int targetUp = calculateTotalVaule(g, EffectType.FIRE_RESIST_BOOST);
                            // 火抗增益百分比：所有 POISON_RESIST 类型效果的 value 乘积
                            double targetUpPret = calculateTotalDownPretVaule(g, EffectType.FIRE_RESIST_BOOST_PRET);
                            // 火抗降低：所有 POISON_RESIST_DOWN 类型效果的 value 总和
                            int targetDown = calculateTotalVaule(g, EffectType.FIRE_RESIST_DOWN);
                            // 火抗降低百分比：所有 POISON_RESIST 类型效果的 value 乘积
                            double targetDownPret = calculateTotalUpPretVaule(g, EffectType.FIRE_RESIST_DOWN_PRET);
                            // 最终（仅基于 buff 计算，无新增方法）

                            int finalDamage = (int) (totalPoisonDamage * resistUpPret * resistDownPret * targetUpPret * targetDownPret
                                    + (resistUp - resistDown + attacker.getHyAtk() - g.getHyDef() - targetUp + targetDown));

                            if (finalDamage < 0) {
                                finalDamage = 0;
                            }

                            // 4. 扣除伤害
                            g.setCurrentHp(g.getCurrentHp() - finalDamage);
                            TargetBattleData data = new TargetBattleData(g.getMaxHp(), g.getCurrentHp(), finalDamage, g.isOnField());

                            targetStatus.put(g.getId(), data);
                            if (g.isDead()) {


                                deadUnits.put(g.getId(), data);
                                deadGuardians.add(g);
                            }
                        });

                        // 单条日志记录多目标
                        addMultiTargetLog("焚烧",
                                attacker.getId(),
                                attacker.getMaxHp(),
                                attacker.getCurrentHp(),
                                attacker.isOnField(),
                                targetStatus,
                                EffectType.FIRE_DAMAGE,
                                DamageType.FIRE,
                                "对全体造成火焰伤害");
                        // 死亡日志
                        if (!deadUnits.isEmpty()) {
                            addMultiTargetLog("UNIT_DEATH",
                                    null,
                                    0,
                                    0,
                                    false,
                                    deadUnits,
                                    null,
                                    null,
                                    "死亡");
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
            case "琵琶精":
                if (2 == 2) {
                    //妖火lv1攻击后对全体敌方造成12点 火焰伤害
                    List<Guardian> offFieldEnemies = defender.getCamp() == Camp.A ?
                            campA.stream().filter(g -> !g.isDead()).collect(Collectors.toList()) :
                            campB.stream().filter(g -> !g.isDead()).collect(Collectors.toList());

                    if (!offFieldEnemies.isEmpty()) {
                        Map<String, TargetBattleData> deadUnits = new HashMap<>();
                        List<Guardian> deadGuardians = new ArrayList<>();
                        int lavaDamage = 12 * skillLevel[0];

                        Map<String, TargetBattleData> targetStatus = new HashMap<>();

                        offFieldEnemies.forEach(g -> {

                            // 1. 计算所有中毒效果的总伤害（累加 POISON 类型的 value）
                            int totalPoisonDamage = lavaDamage;
                            // 2. 计算毒抗相关（直接基于你现有 EffectInstance 计算，不新增 Guardian 方法）
                            // 火伤增益：所有 POISON_RESIST 类型效果的 value 总和
                            int resistUp = calculateTotalVaule(attacker, EffectType.FIRE_BOOST);
                            // 火伤增益百分比：所有 POISON_RESIST 类型效果的 value 乘积
                            double resistUpPret = calculateTotalUpPretVaule(attacker, EffectType.FIRE_BOOST_PRET);
                            // 火伤降低：所有 POISON_RESIST_DOWN 类型效果的 value 总和
                            int resistDown = calculateTotalVaule(attacker, EffectType.FIRE_DOWN);
                            // 火伤降低百分比：所有 POISON_RESIST 类型效果的 value 乘积
                            double resistDownPret = calculateTotalDownPretVaule(attacker, EffectType.FIRE_DOWN_PRET);


                            // 2. 计算毒抗相关（直接基于你现有 EffectInstance 计算，不新增 Guardian 方法）
                            // 火抗增益：所有 POISON_RESIST 类型效果的 value 总和
                            int targetUp = calculateTotalVaule(g, EffectType.FIRE_RESIST_BOOST);
                            // 火抗增益百分比：所有 POISON_RESIST 类型效果的 value 乘积
                            double targetUpPret = calculateTotalDownPretVaule(g, EffectType.FIRE_RESIST_BOOST_PRET);
                            // 火抗降低：所有 POISON_RESIST_DOWN 类型效果的 value 总和
                            int targetDown = calculateTotalVaule(g, EffectType.FIRE_RESIST_DOWN);
                            // 火抗降低百分比：所有 POISON_RESIST 类型效果的 value 乘积
                            double targetDownPret = calculateTotalUpPretVaule(g, EffectType.FIRE_RESIST_DOWN_PRET);
                            // 最终（仅基于 buff 计算，无新增方法）

                            int finalDamage = (int) (totalPoisonDamage * resistUpPret * resistDownPret * targetUpPret * targetDownPret
                                    + (resistUp - resistDown + attacker.getHyAtk() - g.getHyDef() - targetUp + targetDown));

                            if (finalDamage < 0) {
                                finalDamage = 0;
                            }

                            // 4. 扣除伤害
                            g.setCurrentHp(g.getCurrentHp() - finalDamage);
                            TargetBattleData data = new TargetBattleData(g.getMaxHp(), g.getCurrentHp(), finalDamage, g.isOnField());

                            targetStatus.put(g.getId(), data);
                            if (g.isDead()) {


                                deadUnits.put(g.getId(), data);
                                deadGuardians.add(g);
                            }
                        });

                        // 单条日志  记录多目标
                        addMultiTargetLog("妖火",
                                attacker.getId(),
                                attacker.getMaxHp(),
                                attacker.getCurrentHp(),
                                attacker.isOnField(),
                                targetStatus,
                                EffectType.FIRE_DAMAGE,
                                DamageType.FIRE,
                                "对全体造成火焰伤害");
                        // 死亡日志
                        if (!deadUnits.isEmpty()) {
                            addMultiTargetLog("UNIT_DEATH",
                                    null,
                                    0,
                                    0,
                                    false,
                                    deadUnits,
                                    null,
                                    null,
                                    "死亡");
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
        }
    }


    // 触发死亡相关技能
    private void triggerOnDeathSkills(Guardian v) {
        if (!v.isSilence() && v.getName().equals("燃灯道人")) {
            List<Guardian> offFieldEnemies = v.getCamp() == Camp.A ?
                    campB.stream().filter(g -> !g.isDead()).collect(Collectors.toList()) :
                    campA.stream().filter(g -> !g.isDead()).collect(Collectors.toList());
            if (Xtool.isNotNull(offFieldEnemies)) {
                Guardian minHpPerson = offFieldEnemies.get(0); // 先默认第一个为最大
                for (Guardian p : offFieldEnemies) {
                    // 如果当前对象年龄大于已记录的最大年龄，更新
                    if (p.getCurrentHp() < minHpPerson.getCurrentHp()) {
                        minHpPerson = p;
                    }
                }
                List<Guardian> deadGuardians = new ArrayList<>();
                Map<String, TargetBattleData> deadUnits = new HashMap<>();
                int[] skillLevel = CardSkillLevelUtil.calculateSkillLevels(v.getLevel(), v.getStar().doubleValue());
                if (skillLevel[1] > 0) {
                    // 1. 计算所有中毒效果的总伤害（累加 POISON 类型的 value）
                    int totalPoisonDamage = 325 * skillLevel[0];
                    // 2. 计算毒抗相关（直接基于你现有 EffectInstance 计算，不新增 Guardian 方法）
                    // 飞弹增益：所有 POISON_RESIST 类型效果的 value 总和
                    int resistUp = calculateTotalVaule(v, EffectType.MISSILE_BOOST);
                    // 飞弹增益百分比：所有 POISON_RESIST 类型效果的 value 乘积
                    double resistUpPret = calculateTotalUpPretVaule(v, EffectType.MISSILE_BOOST_PRET);
                    // 飞弹降低：所有 POISON_RESIST_DOWN 类型效果的 value 总和
                    int resistDown = calculateTotalVaule(v, EffectType.MISSILE_DOWN);
                    // 飞弹降低百分比：所有 POISON_RESIST 类型效果的 value 乘积
                    double resistDownPret = calculateTotalDownPretVaule(v, EffectType.MISSILE_DOWN_PRET);


                    // 2. 计算毒抗相关（直接基于你现有 EffectInstance 计算，不新增 Guardian 方法）
                    // 弹抗增益：所有 POISON_RESIST 类型效果的 value 总和
                    int targetUp = calculateTotalVaule(minHpPerson, EffectType.MISSILE_RESIST_BOOST);
                    // 弹抗增益百分比：所有 POISON_RESIST 类型效果的 value 乘积
                    double targetUpPret = calculateTotalDownPretVaule(minHpPerson, EffectType.MISSILE_RESIST_BOOST_PRET);
                    // 弹抗降低：所有 POISON_RESIST_DOWN 类型效果的 value 总和
                    int targetDown = calculateTotalVaule(minHpPerson, EffectType.MISSILE_RESIST_DOWN);
                    // 弹抗降低百分比：所有 POISON_RESIST 类型效果的 value fieldA
                    double targetDownPret = calculateTotalUpPretVaule(minHpPerson, EffectType.MISSILE_RESIST_DOWN_PRET);
                    // 最终（仅基于 buff 计算，无新增方法）

                    int burnDamage = (int) (totalPoisonDamage * resistUpPret * resistDownPret * targetUpPret * targetDownPret
                            + (resistUp - resistDown + v.getFdAtk() - minHpPerson.getFdDef() - targetUp + targetDown));

                    if (burnDamage < 0) {
                        burnDamage = 0;
                    }

                    // 4. 扣除伤害
                    minHpPerson.setCurrentHp(minHpPerson.getCurrentHp() - burnDamage);
                    if (minHpPerson.isDead()) {
                        TargetBattleData data = new TargetBattleData(minHpPerson.getMaxHp(), minHpPerson.getCurrentHp(), burnDamage, minHpPerson.isOnField());
                        deadUnits.put(minHpPerson.getId(), data);
                        deadGuardians.add(minHpPerson);
                    }
                    //；信念报偿Lv1死亡时，对敌方血量最小者造成325点飞弹伤害；
                    addLog("信念报偿",
                            v.getId(),
                            v.getMaxHp(),
                            v.getCurrentHp(),
                            0,
                            v.isOnField(),
                            minHpPerson.getId(),
                            minHpPerson.getMaxHp(),
                            minHpPerson.getCurrentHp(),
                            burnDamage,
                            minHpPerson.isOnField(),
                            EffectType.MISSILE_DAMAGE,
                            DamageType.MISSILE,
                            "-" + burnDamage);
                    // 死亡日志
                    if (!deadUnits.isEmpty()) {
                        addMultiTargetLog("UNIT_DEATH",
                                null,
                                0,
                                0,
                                false,
                                deadUnits,
                                null,
                                null,
                                "死亡");
                        //触发死亡技能
                        for (Guardian g : deadGuardians) {
                            triggerOnDeathSkills(g);
                        }
                    } else {
                        triggerOnAttackedSkills(minHpPerson, EffectType.MISSILE_DAMAGE);
                    }
                }


            }

        }

        if (!v.isSilence() && v.getName().equals("鹏魔王")) {
            List<Guardian> offFieldEnemies = v.getCamp() == Camp.A ?
                    campB.stream().filter(g -> !g.isDead()).collect(Collectors.toList()) :
                    campA.stream().filter(g -> !g.isDead()).collect(Collectors.toList());
            if (!offFieldEnemies.isEmpty()) {

                Map<String, TargetBattleData> targetStatus = new HashMap<>();
                int[] skillLevel = CardSkillLevelUtil.calculateSkillLevels(v.getLevel(), v.getStar().doubleValue());
                for (Guardian g : offFieldEnemies) {
                    int totalPoisonDamage = 10 * skillLevel[0];
                    // 1. 计算所有中毒效果的总伤害（累加 POISON 类型的 value）
                    // 2. 计算毒抗相关（直接基于你现有 EffectInstance 计算，不新增 Guardian 方法）
                    // 中毒增益：所有 POISON_RESIST 类型效果的 value 总和
                    int resistUp = calculateTotalVaule(v, EffectType.POISON_BOOST);
                    // 中毒增益百分比：所有 POISON_RESIST 类型效果的 value 乘积
                    double resistUpPret = calculateTotalUpPretVaule(v, EffectType.POISON_BOOST_PRET);
                    // 中毒降低：所有 POISON_RESIST_DOWN 类型效果的 value 总和
                    int resistDown = calculateTotalVaule(v, EffectType.POISON_DOWN);
                    // 中毒降低百分比：所有 POISON_RESIST 类型效果的 value 乘积
                    double resistDownPret = calculateTotalDownPretVaule(v, EffectType.POISON_DOWN_PRET);

                    int poisonValue = (int) (totalPoisonDamage * resistUpPret * resistDownPret + (resistUp + v.getDsAtk() - resistDown));
                    g.addEffect(EffectType.POISON, poisonValue + v.getDsAtk(), 99, v.getId());
                    TargetBattleData data = new TargetBattleData(g.getMaxHp(), g.getCurrentHp(), poisonValue, g.isOnField());

                    targetStatus.put(g.getId(), data);
                }
                // 单条日志记录多目标
                addMultiTargetLog("死雨风暴",
                        v.getId(),
                        v.getMaxHp(),
                        v.getCurrentHp(),
                        v.isOnField(),
                        targetStatus,
                        EffectType.POISON,
                        DamageType.POISON,
                        "敌方所有生物中毒");

                Guardian enemie = v.getCamp() == Camp.A ? fieldB : fieldA;
                if (!enemie.isDead() && skillLevel[1] > 0) {
                    int totalPoisonDamage = 70 * skillLevel[0];
                    // 1. 计算所有中毒效果的总伤害（累加 POISON 类型的 value）
                    // 2. 计算毒抗相关（直接基于你现有 EffectInstance 计算，不新增 Guardian 方法）
                    // 中毒增益：所有 POISON_RESIST 类型效果的 value 总和
                    int resistUp = calculateTotalVaule(v, EffectType.POISON_BOOST);
                    // 中毒增益百分比：所有 POISON_RESIST 类型效果的 value 乘积
                    double resistUpPret = calculateTotalUpPretVaule(v, EffectType.POISON_BOOST_PRET);
                    // 中毒降低：所有 POISON_RESIST_DOWN 类型效果的 value 总和
                    int resistDown = calculateTotalVaule(v, EffectType.POISON_DOWN);
                    // 中毒降低百分比：所有 POISON_RESIST 类型效果的 value 乘积
                    double resistDownPret = calculateTotalDownPretVaule(v, EffectType.POISON_DOWN_PRET);

                    int poisonValue = (int) (totalPoisonDamage * resistUpPret * resistDownPret + (resistUp + v.getDsAtk() - resistDown));
                    enemie.addEffect(EffectType.POISON, poisonValue, 99, v.getId());
                    addLog("死亡徘徊",
                            v.getId(),
                            v.getMaxHp(),
                            v.getCurrentHp(),
                            poisonValue,
                            v.isOnField(),
                            enemie.getId(),
                            enemie.getMaxHp(),
                            enemie.getCurrentHp(),
                            poisonValue,
                            enemie.isOnField(),
                            EffectType.POISON,
                            DamageType.POISON,
                            "中毒+" + poisonValue);
                }
            }
        }

        if (!v.isSilence() && v.getName().equals("辟尘大王")) {
            List<Guardian> offFieldEnemies = v.getCamp() == Camp.A ?
                    campB.stream().filter(g -> !g.isDead()).collect(Collectors.toList()) :
                    campA.stream().filter(g -> !g.isDead()).collect(Collectors.toList());
            if (!offFieldEnemies.isEmpty()) {

                Map<String, TargetBattleData> targetStatus = new HashMap<>();
                int[] skillLevel = CardSkillLevelUtil.calculateSkillLevels(v.getLevel(), v.getStar().doubleValue());
                for (Guardian g : offFieldEnemies) {
                    int totalPoisonDamage = 30 * skillLevel[0];
                    // 1. 计算所有中毒效果的总伤害（累加 POISON 类型的 value）
                    // 2. 计算毒抗相关（直接基于你现有 EffectInstance 计算，不新增 Guardian 方法）
                    // 中毒增益：所有 POISON_RESIST 类型效果的 value 总和
                    int resistUp = calculateTotalVaule(v, EffectType.POISON_BOOST);
                    // 中毒增益百分比：所有 POISON_RESIST 类型效果的 value 乘积
                    double resistUpPret = calculateTotalUpPretVaule(v, EffectType.POISON_BOOST_PRET);
                    // 中毒降低：所有 POISON_RESIST_DOWN 类型效果的 value 总和
                    int resistDown = calculateTotalVaule(v, EffectType.POISON_DOWN);
                    // 中毒降低百分比：所有 POISON_RESIST 类型效果的 value 乘积
                    double resistDownPret = calculateTotalDownPretVaule(v, EffectType.POISON_DOWN_PRET);

                    int poisonValue = (int) (totalPoisonDamage * resistUpPret * resistDownPret + (resistUp + v.getDsAtk() - resistDown));

                    g.addEffect(EffectType.POISON, poisonValue, 99, v.getId());
                    TargetBattleData data = new TargetBattleData(g.getMaxHp(), g.getCurrentHp(), poisonValue, g.isOnField());

                    targetStatus.put(g.getId(), data);
                }
                // 单条日志记录多目标


                addMultiTargetLog("尸毒",
                        v.getId(),
                        v.getMaxHp(),
                        v.getCurrentHp(),
                        v.isOnField(),
                        targetStatus,
                        EffectType.POISON,
                        DamageType.POISON,
                        "敌方所有生物中毒");

            }
        }

        if (!v.isSilence() && v.getName().equals("混世魔王")) {
            List<Guardian> offFieldEnemies = v.getCamp() == Camp.A ?
                    campB.stream().filter(g -> !g.isDead()).collect(Collectors.toList()) :
                    campA.stream().filter(g -> !g.isDead()).collect(Collectors.toList());
            if (!offFieldEnemies.isEmpty()) {

                Map<String, TargetBattleData> targetStatus = new HashMap<>();
                int[] skillLevel = CardSkillLevelUtil.calculateSkillLevels(v.getLevel(), v.getStar().doubleValue());
                for (Guardian g : offFieldEnemies) {
                    int totalPoisonDamage = 30 * skillLevel[0];
                    // 1. 计算所有中毒效果的总伤害（累加 POISON 类型的 value）
                    // 2. 计算毒抗相关（直接基于你现有 EffectInstance 计算，不新增 Guardian 方法）
                    // 中毒增益：所有 POISON_RESIST 类型效果的 value 总和
                    int resistUp = calculateTotalVaule(v, EffectType.POISON_BOOST);
                    // 中毒增益百分比：所有 POISON_RESIST 类型效果的 value 乘积
                    double resistUpPret = calculateTotalUpPretVaule(v, EffectType.POISON_BOOST_PRET);
                    // 中毒降低：所有 POISON_RESIST_DOWN 类型效果的 value 总和
                    int resistDown = calculateTotalVaule(v, EffectType.POISON_DOWN);
                    // 中毒降低百分比：所有 POISON_RESIST 类型效果的 value 乘积
                    double resistDownPret = calculateTotalDownPretVaule(v, EffectType.POISON_DOWN_PRET);

                    int poisonValue = (int) (totalPoisonDamage * resistUpPret * resistDownPret + (resistUp + v.getDsAtk() - resistDown));

                    g.addEffect(EffectType.POISON, poisonValue, 99, v.getId());
                    TargetBattleData data = new TargetBattleData(g.getMaxHp(), g.getCurrentHp(), poisonValue, g.isOnField());

                    targetStatus.put(g.getId(), data);
                }
                // 单条日志记录多目标
                addMultiTargetLog("尸毒",
                        v.getId(),
                        v.getMaxHp(),
                        v.getCurrentHp(),
                        v.isOnField(),
                        targetStatus,
                        EffectType.POISON,
                        DamageType.POISON,
                        "敌方所有生物中毒");

            }
        }

        if (!v.isSilence() && v.getName().equals("陆压道君")) {
            List<Guardian> immortalAllies = v.getCamp() == Camp.A ?
                    campA.stream().filter(g -> !g.isDead() && g.getRace() == Race.IMMORTAL).collect(Collectors.toList()) :
                    campB.stream().filter(g -> !g.isDead() && g.getRace() == Race.IMMORTAL).collect(Collectors.toList());
            if (Xtool.isNotNull(immortalAllies)) {
                int[] skillLevel = CardSkillLevelUtil.calculateSkillLevels(v.getLevel(), v.getStar().doubleValue());
                if (!immortalAllies.isEmpty() && skillLevel[1] > 0) {
                    int heal = 70 * skillLevel[1];

                    Map<String, TargetBattleData> targetStatus = new HashMap<>();

                    immortalAllies.forEach(g -> {

                        g.setCurrentHp(g.getCurrentHp() + heal);
                        TargetBattleData data = new TargetBattleData(g.getMaxHp(), g.getCurrentHp(), heal, g.isOnField());
                        targetStatus.put(g.getId(), data);
                    });

                    addMultiTargetLog("舍身取义",
                            v.getId(),
                            v.getMaxHp(),
                            v.getCurrentHp(),
                            v.isOnField(),
                            targetStatus,
                            EffectType.HEAL,
                            null,
                            "治疗我方仙界单位");
                    immortalAllies.forEach(g -> {
                        triggerOnHelSkills(g);

                    });
                }
            }

        }

//       玄甲神，临别赠言lv1死亡后增加后方单位108点生命上限；巨灵神协同lv1与巨灵神在同
        if (!v.isSilence() && v.getName().equals("玄甲神")) {
            List<Guardian> immortalAllies = v.getCamp() == Camp.A ?
                    campA.stream().filter(g -> !g.isDead() && g.getPosition() == v.getPosition() + 1).collect(Collectors.toList()) :
                    campB.stream().filter(g -> !g.isDead() && g.getPosition() == v.getPosition() + 1).collect(Collectors.toList());
            if (Xtool.isNotNull(immortalAllies)) {
                int[] skillLevel = CardSkillLevelUtil.calculateSkillLevels(v.getLevel(), v.getStar().doubleValue());
                int heal = 108 * skillLevel[0];

                Guardian g = immortalAllies.get(0);
                addLog("临别赠言",
                        v.getId(),
                        v.getMaxHp(),
                        v.getCurrentHp(),
                        0,
                        v.isOnField(),
                        g.getId(),
                        g.getMaxHp(),
                        g.getCurrentHp(),
                        heal,
                        g.isOnField(),
                        EffectType.HP_UP,
                        null,
                        "生命上限+" + heal);
            }

        }


        //句芒，残酷收割Lv1每当有生物死亡时，回复自身6%最大生命；
        if (campA.stream().anyMatch(g -> g.getName().equals("句芒") && !g.isDead())) {
            Guardian changsheng = campA.stream()
                    .filter(g -> g.getName().equals("句芒") && !g.isDead())
                    .findFirst().get();
            if (!changsheng.isSilence()) {
                int[] skillLevel = CardSkillLevelUtil.calculateSkillLevels(changsheng.getLevel(), changsheng.getStar().doubleValue());
                if (skillLevel[1] > 0) {
                    int hel = (int) (0.06 * skillLevel[1] * changsheng.getMaxHp());
                    changsheng.setCurrentHp(changsheng.getCurrentHp() + hel);
                    addLog("残酷收割",
                            changsheng.getId(),
                            changsheng.getMaxHp(),
                            changsheng.getCurrentHp(),
                            hel,
                            changsheng.isOnField(),
                            changsheng.getId(),
                            changsheng.getMaxHp(),
                            changsheng.getCurrentHp(),
                            hel,
                            changsheng.isOnField(),
                            EffectType.HP_UP,
                            DamageType.MAGIC,
                            "+" + hel);
                }
            }

        }

        if (campB.stream().anyMatch(g -> g.getName().equals("句芒") && !g.isDead())) {
            Guardian changsheng = campB.stream()
                    .filter(g -> g.getName().equals("句芒") && !g.isDead())
                    .findFirst().get();
            if (!changsheng.isSilence()) {
                int[] skillLevel = CardSkillLevelUtil.calculateSkillLevels(changsheng.getLevel(), changsheng.getStar().doubleValue());
                if (skillLevel[1] > 0) {
                    int hel = (int) (0.06 * skillLevel[1] * changsheng.getMaxHp());
                    changsheng.setCurrentHp(changsheng.getCurrentHp() + hel);
                    addLog("残酷收割",
                            changsheng.getId(),
                            changsheng.getMaxHp(),
                            changsheng.getCurrentHp(),
                            hel,
                            changsheng.isOnField(),
                            changsheng.getId(),
                            changsheng.getMaxHp(),
                            changsheng.getCurrentHp(),
                            hel,
                            changsheng.isOnField(),
                            EffectType.HP_UP,
                            DamageType.MAGIC,
                            "+" + hel);
                }
            }
        }
//        场上触发，每当有单位死亡时，对场上敌方身后单位造成237点飞弹伤害[装备飞弹提成100%]
        if (fieldA.getName().equals("王天君")&&!fieldA.isDead()&&fieldA.isOnField()&&!fieldA.isSilence()) {
            List<Guardian> offFieldEnemies = fieldA.getCamp() == Camp.A ?
                    campB.stream().filter(g -> !g.isDead())  // 筛选未死亡的对象
                            .sorted(Comparator.comparing(Guardian::getPosition))  // 升序 = 最小值在前
                            .collect(Collectors.toList()) :
                    campA.stream().filter(g -> !g.isDead())  // 筛选未死亡的对象
                    .sorted(Comparator.comparing(Guardian::getPosition))  // 升序 = 最小值在前
                    .collect(Collectors.toList());
            if (Xtool.isNotNull(offFieldEnemies)) {
                Guardian minHpPerson = offFieldEnemies.get(0); // 先默认第一个为最大
                List<Guardian> deadGuardians = new ArrayList<>();
                Map<String, TargetBattleData> deadUnits = new HashMap<>();
                int[] skillLevel = CardSkillLevelUtil.calculateSkillLevels(v.getLevel(), v.getStar().doubleValue());
                if (skillLevel[1] > 0) {
                    // 1. 计算所有中毒效果的总伤害（累加 POISON 类型的 value）
                    int totalPoisonDamage = 237 * skillLevel[1];
                    // 2. 计算毒抗相关（直接基于你现有 EffectInstance 计算，不新增 Guardian 方法）
                    // 飞弹增益：所有 POISON_RESIST 类型效果的 value 总和
                    int resistUp = calculateTotalVaule(fieldA, EffectType.MISSILE_BOOST);
                    // 飞弹增益百分比：所有 POISON_RESIST 类型效果的 value 乘积
                    double resistUpPret = calculateTotalUpPretVaule(fieldA, EffectType.MISSILE_BOOST_PRET);
                    // 飞弹降低：所有 POISON_RESIST_DOWN 类型效果的 value 总和
                    int resistDown = calculateTotalVaule(fieldA, EffectType.MISSILE_DOWN);
                    // 飞弹降低百分比：所有 POISON_RESIST 类型效果的 value 乘积
                    double resistDownPret = calculateTotalDownPretVaule(fieldA, EffectType.MISSILE_DOWN_PRET);


                    // 2. 计算毒抗相关（直接基于你现有 EffectInstance 计算，不新增 Guardian 方法）
                    // 弹抗增益：所有 POISON_RESIST 类型效果的 value 总和
                    int targetUp = calculateTotalVaule(minHpPerson, EffectType.MISSILE_RESIST_BOOST);
                    // 弹抗增益百分比：所有 POISON_RESIST 类型效果的 value 乘积
                    double targetUpPret = calculateTotalDownPretVaule(minHpPerson, EffectType.MISSILE_RESIST_BOOST_PRET);
                    // 弹抗降低：所有 POISON_RESIST_DOWN 类型效果的 value 总和
                    int targetDown = calculateTotalVaule(minHpPerson, EffectType.MISSILE_RESIST_DOWN);
                    // 弹抗降低百分比：所有 POISON_RESIST 类型效果的 value fieldA
                    double targetDownPret = calculateTotalUpPretVaule(minHpPerson, EffectType.MISSILE_RESIST_DOWN_PRET);
                    // 最终（仅基于 buff 计算，无新增方法）

                    int burnDamage = (int) (totalPoisonDamage * resistUpPret * resistDownPret * targetUpPret * targetDownPret
                            + (resistUp - resistDown + v.getFdAtk() - minHpPerson.getFdDef() - targetUp + targetDown));

                    if (burnDamage < 0) {
                        burnDamage = 0;
                    }

                    // 4. 扣除伤害
                    minHpPerson.setCurrentHp(minHpPerson.getCurrentHp() - burnDamage);
                    if (minHpPerson.isDead()) {
                        TargetBattleData data = new TargetBattleData(minHpPerson.getMaxHp(), minHpPerson.getCurrentHp(), burnDamage, minHpPerson.isOnField());
                        deadUnits.put(minHpPerson.getId(), data);
                        deadGuardians.add(minHpPerson);
                    }
                    //；信念报偿Lv1死亡时，对敌方血量最小者造成325点飞弹伤害；
                    addLog("法宝反噬",
                            fieldA.getId(),
                            fieldA.getMaxHp(),
                            fieldA.getCurrentHp(),
                            0,
                            fieldA.isOnField(),
                            minHpPerson.getId(),
                            minHpPerson.getMaxHp(),
                            minHpPerson.getCurrentHp(),
                            burnDamage,
                            minHpPerson.isOnField(),
                            EffectType.MISSILE_DAMAGE,
                            DamageType.MISSILE,
                            "-" + burnDamage);
                    // 死亡日志
                    if (!deadUnits.isEmpty()) {
                        addMultiTargetLog("UNIT_DEATH",
                                null,
                                0,
                                0,
                                false,
                                deadUnits,
                                null,
                                null,
                                "死亡");
                        //触发死亡技能
                        for (Guardian g : deadGuardians) {
                            triggerOnDeathSkills(g);
                        }
                    } else {
                        triggerOnAttackedSkills(minHpPerson, EffectType.MISSILE_DAMAGE);
                    }
                }


            }

        }

        //场上触发，每当有单位死亡时，对场上敌方身后单位造成237点飞弹伤害[装备飞弹提成100%]
        if (fieldB.getName().equals("王天君")&&!fieldB.isDead()&&fieldB.isOnField()&&!fieldB.isSilence()) {
            List<Guardian> offFieldEnemies = fieldB.getCamp() == Camp.B ?
                    campA.stream().filter(g -> !g.isDead())  // 筛选未死亡的对象
                            .sorted(Comparator.comparing(Guardian::getPosition))  // 升序 = 最小值在前
                            .collect(Collectors.toList()) :
                    campB.stream().filter(g -> !g.isDead())  // 筛选未死亡的对象
                            .sorted(Comparator.comparing(Guardian::getPosition))  // 升序 = 最小值在前
                            .collect(Collectors.toList());
            if (Xtool.isNotNull(offFieldEnemies)) {
                Guardian minHpPerson = offFieldEnemies.get(0); // 先默认第一个为最大
                List<Guardian> deadGuardians = new ArrayList<>();
                Map<String, TargetBattleData> deadUnits = new HashMap<>();
                int[] skillLevel = CardSkillLevelUtil.calculateSkillLevels(v.getLevel(), v.getStar().doubleValue());
                if (skillLevel[1] > 0) {
                    // 1. 计算所有中毒效果的总伤害（累加 POISON 类型的 value）
                    int totalPoisonDamage = 237 * skillLevel[1];
                    // 2. 计算毒抗相关（直接基于你现有 EffectInstance 计算，不新增 Guardian 方法）
                    // 飞弹增益：所有 POISON_RESIST 类型效果的 value 总和
                    int resistUp = calculateTotalVaule(fieldB, EffectType.MISSILE_BOOST);
                    // 飞弹增益百分比：所有 POISON_RESIST 类型效果的 value 乘积
                    double resistUpPret = calculateTotalUpPretVaule(fieldB, EffectType.MISSILE_BOOST_PRET);
                    // 飞弹降低：所有 POISON_RESIST_DOWN 类型效果的 value 总和
                    int resistDown = calculateTotalVaule(fieldB, EffectType.MISSILE_DOWN);
                    // 飞弹降低百分比：所有 POISON_RESIST 类型效果的 value 乘积
                    double resistDownPret = calculateTotalDownPretVaule(fieldB, EffectType.MISSILE_DOWN_PRET);


                    // 2. 计算毒抗相关（直接基于你现有 EffectInstance 计算，不新增 Guardian 方法）
                    // 弹抗增益：所有 POISON_RESIST 类型效果的 value 总和
                    int targetUp = calculateTotalVaule(minHpPerson, EffectType.MISSILE_RESIST_BOOST);
                    // 弹抗增益百分比：所有 POISON_RESIST 类型效果的 value 乘积
                    double targetUpPret = calculateTotalDownPretVaule(minHpPerson, EffectType.MISSILE_RESIST_BOOST_PRET);
                    // 弹抗降低：所有 POISON_RESIST_DOWN 类型效果的 value 总和
                    int targetDown = calculateTotalVaule(minHpPerson, EffectType.MISSILE_RESIST_DOWN);
                    // 弹抗降低百分比：所有 POISON_RESIST 类型效果的 value fieldA
                    double targetDownPret = calculateTotalUpPretVaule(minHpPerson, EffectType.MISSILE_RESIST_DOWN_PRET);
                    // 最终（仅基于 buff 计算，无新增方法）

                    int burnDamage = (int) (totalPoisonDamage * resistUpPret * resistDownPret * targetUpPret * targetDownPret
                            + (resistUp - resistDown + v.getFdAtk() - minHpPerson.getFdDef() - targetUp + targetDown));

                    if (burnDamage < 0) {
                        burnDamage = 0;
                    }

                    // 4. 扣除伤害
                    minHpPerson.setCurrentHp(minHpPerson.getCurrentHp() - burnDamage);
                    if (minHpPerson.isDead()) {
                        TargetBattleData data = new TargetBattleData(minHpPerson.getMaxHp(), minHpPerson.getCurrentHp(), burnDamage, minHpPerson.isOnField());
                        deadUnits.put(minHpPerson.getId(), data);
                        deadGuardians.add(minHpPerson);
                    }
                    //；信念报偿Lv1死亡时，对敌方血量最小者造成325点飞弹伤害；
                    addLog("法宝反噬",
                            fieldB.getId(),
                            fieldB.getMaxHp(),
                            fieldB.getCurrentHp(),
                            0,
                            fieldB.isOnField(),
                            minHpPerson.getId(),
                            minHpPerson.getMaxHp(),
                            minHpPerson.getCurrentHp(),
                            burnDamage,
                            minHpPerson.isOnField(),
                            EffectType.MISSILE_DAMAGE,
                            DamageType.MISSILE,
                            "-" + burnDamage);
                    // 死亡日志
                    if (!deadUnits.isEmpty()) {
                        addMultiTargetLog("UNIT_DEATH",
                                null,
                                0,
                                0,
                                false,
                                deadUnits,
                                null,
                                null,
                                "死亡");
                        //触发死亡技能
                        for (Guardian g : deadGuardians) {
                            triggerOnDeathSkills(g);
                        }
                    } else {
                        triggerOnAttackedSkills(minHpPerson, EffectType.MISSILE_DAMAGE);
                    }
                }


            }

        }

        // 牛魔王鲜血盛宴
        if (campA.stream().anyMatch(g -> g.getName().equals("牛魔王") && !g.isDead() && g.getBuffStacks() < 3)) {
            Guardian changsheng = campA.stream()
                    .filter(g -> g.getName().equals("牛魔王") && !g.isDead())
                    .findFirst().get();
            if (!changsheng.isSilence()) {
                int[] skillLevel = CardSkillLevelUtil.calculateSkillLevels(changsheng.getLevel(), changsheng.getStar().doubleValue());
                if (skillLevel[1] > 0) {
                    int hel = 117 * skillLevel[1];
                    changsheng.setBuffStacks(changsheng.getBuffStacks() + 1);
                    if (duoBaoGuanHuan()) {
                        changsheng.setCurrentHp(changsheng.getCurrentHp() + hel);
                    } else {
                        changsheng.setMaxHp(changsheng.getMaxHp() + hel);
                        changsheng.setCurrentHp(changsheng.getCurrentHp() + hel);
                    }

                    addLog("鲜血盛宴",
                            changsheng.getId(),
                            changsheng.getMaxHp(),
                            changsheng.getCurrentHp(),
                            hel,
                            changsheng.isOnField(),
                            changsheng.getId(),
                            changsheng.getMaxHp(),
                            changsheng.getCurrentHp(),
                            hel,
                            changsheng.isOnField(),
                            EffectType.HP_UP,
                            DamageType.BUFF,
                            "生命上限提升+" + hel);
                }

            }

        }

        if (campB.stream().anyMatch(g -> g.getName().equals("牛魔王") && !g.isDead() && g.getBuffStacks() < 3)) {
            Guardian changsheng = campB.stream()
                    .filter(g -> g.getName().equals("牛魔王") && !g.isDead())
                    .findFirst().get();
            if (!changsheng.isSilence()) {
                int[] skillLevel = CardSkillLevelUtil.calculateSkillLevels(changsheng.getLevel(), changsheng.getStar().doubleValue());
                if (skillLevel[1] > 0) {
                    int hel = 117 * skillLevel[1];
                    changsheng.setBuffStacks(changsheng.getBuffStacks() + 1);
                    if (duoBaoGuanHuan()) {
                        changsheng.setCurrentHp(changsheng.getCurrentHp() + hel);
                    } else {
                        changsheng.setMaxHp(changsheng.getMaxHp() + hel);
                        changsheng.setCurrentHp(changsheng.getCurrentHp() + hel);
                    }

                    addLog("鲜血盛宴",
                            changsheng.getId(),
                            changsheng.getMaxHp(),
                            changsheng.getCurrentHp(),
                            hel,
                            changsheng.isOnField(),
                            changsheng.getId(),
                            changsheng.getMaxHp(),
                            changsheng.getCurrentHp(),
                            hel,
                            changsheng.isOnField(),
                            EffectType.HP_UP,
                            DamageType.BUFF,
                            "生命上限提升+" + hel);
                }

            }

        }

        if (campA.stream().anyMatch(g -> g.getName().equals("鲤鱼精") && !g.isDead() && g.getBuffStacks() < 3)) {
            Guardian changsheng = campA.stream()
                    .filter(g -> g.getName().equals("鲤鱼精") && !g.isDead())
                    .findFirst().get();
            if (!changsheng.isSilence()) {
                int[] skillLevel = CardSkillLevelUtil.calculateSkillLevels(changsheng.getLevel(), changsheng.getStar().doubleValue());
                if (skillLevel[1] > 0) {
                    int hel = 117 * skillLevel[1];
                    changsheng.setBuffStacks(changsheng.getBuffStacks() + 1);
                    if (duoBaoGuanHuan()) {
                        changsheng.setCurrentHp(changsheng.getCurrentHp() + hel);
                    } else {
                        changsheng.setMaxHp(changsheng.getMaxHp() + hel);
                        changsheng.setCurrentHp(changsheng.getCurrentHp() + hel);
                    }

                    addLog("如鱼得水",
                            changsheng.getId(),
                            changsheng.getMaxHp(),
                            changsheng.getCurrentHp(),
                            hel,
                            changsheng.isOnField(),
                            changsheng.getId(),
                            changsheng.getMaxHp(),
                            changsheng.getCurrentHp(),
                            hel,
                            changsheng.isOnField(),
                            EffectType.HP_UP,
                            DamageType.BUFF,
                            "生命上限提升+" + hel);
                }

            }

        }

        if (campB.stream().anyMatch(g -> g.getName().equals("鲤鱼精") && !g.isDead() && g.getBuffStacks() < 3)) {
            Guardian changsheng = campB.stream()
                    .filter(g -> g.getName().equals("鲤鱼精") && !g.isDead())
                    .findFirst().get();
            if (!changsheng.isSilence()) {
                int[] skillLevel = CardSkillLevelUtil.calculateSkillLevels(changsheng.getLevel(), changsheng.getStar().doubleValue());
                if (skillLevel[1] > 0) {
                    int hel = 117 * skillLevel[1];
                    changsheng.setBuffStacks(changsheng.getBuffStacks() + 1);
                    if (duoBaoGuanHuan()) {
                        changsheng.setCurrentHp(changsheng.getCurrentHp() + hel);
                    } else {
                        changsheng.setMaxHp(changsheng.getMaxHp() + hel);
                        changsheng.setCurrentHp(changsheng.getCurrentHp() + hel);
                    }
                    addLog("如鱼得水",
                            changsheng.getId(),
                            changsheng.getMaxHp(),
                            changsheng.getCurrentHp(),
                            hel,
                            changsheng.isOnField(),
                            changsheng.getId(),
                            changsheng.getMaxHp(),
                            changsheng.getCurrentHp(),
                            hel,
                            changsheng.isOnField(),
                            EffectType.HP_UP,
                            DamageType.BUFF,
                            "生命上限提升+" + hel);
                }

            }

        }


        // 紫薇大帝，背水一战Lv1我方单位死亡时，增加自身攻击50，最多叠加4次；
        if (v.getCamp() == Camp.A && campA.stream().anyMatch(g -> g.getName().equals("紫薇大帝") && !g.isDead() && g.getBuffStacks() < 4)) {
            Guardian changsheng = campA.stream()
                    .filter(g -> g.getName().equals("紫薇大帝") && !g.isDead())
                    .findFirst().get();
            if (!changsheng.isSilence()) {
                int[] skillLevel = CardSkillLevelUtil.calculateSkillLevels(changsheng.getLevel(), changsheng.getStar().doubleValue());
                if (skillLevel[1] > 0) {
                    changsheng.setBuffStacks(changsheng.getBuffStacks() + 1);
                    int value = 50 * skillLevel[1];
                    changsheng.setAttack(changsheng.getAttack() + value);

                    addLog("背水一战",
                            changsheng.getId(),
                            changsheng.getMaxHp(),
                            changsheng.getCurrentHp(),
                            value,
                            changsheng.isOnField(),
                            changsheng.getId(),
                            changsheng.getMaxHp(),
                            changsheng.getCurrentHp(),
                            value,
                            changsheng.isOnField(),
                            EffectType.ATTACK_UP,
                            DamageType.BUFF,
                            "攻击提升+" + value);
                }

            }

        }

        if (v.getCamp() == Camp.B && campB.stream().anyMatch(g -> g.getName().equals("紫薇大帝") && !g.isDead() && g.getBuffStacks() < 4)) {
            Guardian changsheng = campB.stream()
                    .filter(g -> g.getName().equals("紫薇大帝") && !g.isDead())
                    .findFirst().get();
            if (!changsheng.isSilence()) {
                int[] skillLevel = CardSkillLevelUtil.calculateSkillLevels(changsheng.getLevel(), changsheng.getStar().doubleValue());
                if (skillLevel[1] > 0) {
                    changsheng.setBuffStacks(changsheng.getBuffStacks() + 1);
                    int value = 50 * skillLevel[1];
                    changsheng.setAttack(changsheng.getAttack() + value);


                    addLog("背水一战",
                            changsheng.getId(),
                            changsheng.getMaxHp(),
                            changsheng.getCurrentHp(),
                            value,
                            changsheng.isOnField(),
                            changsheng.getId(),
                            changsheng.getMaxHp(),
                            changsheng.getCurrentHp(),
                            value,
                            changsheng.isOnField(),
                            EffectType.ATTACK_UP,
                            DamageType.BUFF,
                            "攻击提升+" + value);
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


                    fieldA.setBuffLuoShens(fieldA.getBuffLuoShens() + 1);
                    fieldA.addEffect(EffectType.ATTACK_UP, attack, 99, v.getId());

                    addLog("洛水歌声",
                            luoshen.getId(),
                            luoshen.getMaxHp(),
                            luoshen.getCurrentHp(),
                            attack,
                            luoshen.isOnField(),
                            fieldA.getId(),
                            fieldA.getMaxHp(),
                            fieldA.getCurrentHp(),
                            attack,
                            fieldA.isOnField(),
                            EffectType.ATTACK_UP,
                            DamageType.BUFF,
                            "攻击力提升+" + attack);
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


                    fieldB.setBuffLuoShens(fieldB.getBuffLuoShens() + 1);
                    fieldB.addEffect(EffectType.ATTACK_UP, attack, 99, v.getId());


                    addLog("洛水歌声",
                            luoshen.getId(),
                            luoshen.getMaxHp(),
                            luoshen.getCurrentHp(),
                            attack,
                            luoshen.isOnField(),
                            fieldB.getId(),
                            fieldB.getMaxHp(),
                            fieldB.getCurrentHp(),
                            attack,
                            fieldB.isOnField(),
                            EffectType.ATTACK_UP,
                            DamageType.BUFF,
                            "攻击力提升+" + attack);
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
                fieldA.setCurrentHp(fieldA.getCurrentHp() + heal);

                addLog("百花酿",
                        luoshen.getId(),
                        luoshen.getMaxHp(),
                        luoshen.getCurrentHp(),
                        heal,
                        luoshen.isOnField(),
                        fieldA.getId(),
                        fieldA.getMaxHp(),
                        fieldA.getCurrentHp(),
                        heal,
                        fieldA.isOnField(),
                        EffectType.HEAL,
                        DamageType.MAGIC,
                        "+" + heal);
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
                fieldB.setCurrentHp(fieldB.getCurrentHp() + heal);

                addLog("百花酿",
                        luoshen.getId(),
                        luoshen.getMaxHp(),
                        luoshen.getCurrentHp(),
                        heal,
                        luoshen.isOnField(),
                        fieldB.getId(),
                        fieldB.getMaxHp(),
                        fieldB.getCurrentHp(),
                        heal,
                        fieldB.isOnField(),
                        EffectType.HEAL,
                        DamageType.MAGIC,
                        "+" + heal);
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

                Map<String, TargetBattleData> targetStatus = new HashMap<>();

                immortalAllies.forEach(g -> {

                    g.setCurrentHp(g.getCurrentHp() + heal);
                    TargetBattleData data = new TargetBattleData(g.getMaxHp(), g.getCurrentHp(), heal, g.isOnField());
                    targetStatus.put(g.getId(), data);
                });

                addMultiTargetLog("生生不息",
                        changsheng.getId(),
                        changsheng.getMaxHp(),
                        changsheng.getCurrentHp(),
                        changsheng.isOnField(),
                        targetStatus,
                        EffectType.HEAL,
                        DamageType.MAGIC,
                        "治疗我方仙界单位");
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

                Map<String, TargetBattleData> targetStatus = new HashMap<>();

                immortalAllies.forEach(g -> {

                    g.setCurrentHp(g.getCurrentHp() + heal);
                    TargetBattleData data = new TargetBattleData(g.getMaxHp(), g.getCurrentHp(), heal, g.isOnField());
                    targetStatus.put(g.getId(), data);
                });

                addMultiTargetLog("生生不息",
                        changsheng.getId(),
                        changsheng.getMaxHp(),
                        changsheng.getCurrentHp(),
                        changsheng.isOnField(),
                        targetStatus,
                        EffectType.HEAL,
                        DamageType.MAGIC,
                        "治疗我方仙界单位");
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
        List<Guardian> campAHasAlive = campA.stream().filter(g -> g.getName().equals("青霞仙子") && !g.isDead()).collect(Collectors.toList());
        if (Xtool.isNotNull(campAHasAlive) && currentRound == 1) {
            Guardian defender = campAHasAlive.get(0);
            int[] skillLevel = CardSkillLevelUtil.calculateSkillLevels(defender.getLevel(), defender.getStar().doubleValue());
            int value = 60 * skillLevel[0];
            defender.setAttack(defender.getAttack() + value);
            addLog("克敌机先",
                    defender.getId(),
                    defender.getMaxHp(),
                    defender.getCurrentHp(),
                    value,
                    defender.isOnField(),
                    defender.getId(),
                    defender.getMaxHp(),
                    defender.getCurrentHp(),
                    value,
                    defender.isOnField(),
                    EffectType.ATTACK_UP,
                    DamageType.MAGIC,
                    "攻击提升+" + value);
        }

        List<Guardian> campBHasAlive = campB.stream().filter(g -> g.getName().equals("青霞仙子") && !g.isDead()).collect(Collectors.toList());
        if (Xtool.isNotNull(campBHasAlive) && currentRound == 1) {
            Guardian defender = campBHasAlive.get(0);
            int[] skillLevel = CardSkillLevelUtil.calculateSkillLevels(defender.getLevel(), defender.getStar().doubleValue());
            int value = 60 * skillLevel[0];
            defender.setAttack(defender.getAttack() + value);
            addLog("克敌机先",
                    defender.getId(),
                    defender.getMaxHp(),
                    defender.getCurrentHp(),
                    value,
                    defender.isOnField(),
                    defender.getId(),
                    defender.getMaxHp(),
                    defender.getCurrentHp(),
                    value,
                    defender.isOnField(),
                    EffectType.ATTACK_UP,
                    DamageType.MAGIC,
                    "攻击提升+" + value);
        }

        // 厚土娘娘后土聚能
        if (!fieldA.isSilence() && !fieldA.isDead() && fieldA != null && fieldA.getName().equals("厚土娘娘") && fieldA.getBuffStacks() < 99) {
            int[] skillLevel = CardSkillLevelUtil.calculateSkillLevels(fieldA.getLevel(), fieldA.getStar().doubleValue());
            if (skillLevel[1] > 0) {
                fieldA.setBuffStacks(fieldA.getBuffStacks() + 1);
                int hel = 197 * skillLevel[1];
                if (duoBaoGuanHuan()) {
                    fieldA.setCurrentHp(fieldA.getCurrentHp() + hel);
                } else {
                    fieldA.setMaxHp(fieldA.getMaxHp() + hel);
                    fieldA.setCurrentHp(fieldA.getCurrentHp() + hel);
                }
                int value = 67 * skillLevel[1];
                fieldA.addEffect(EffectType.ATTACK_UP, value, 99, fieldA.getId());


                addLog("后土聚能",
                        fieldA.getId(),
                        fieldA.getMaxHp(),
                        fieldA.getCurrentHp(),
                        hel,
                        fieldA.isOnField(),
                        fieldA.getId(),
                        fieldA.getMaxHp(),
                        fieldA.getCurrentHp(),
                        hel,
                        fieldA.isOnField(),
                        EffectType.HP_UP,
                        DamageType.BUFF,
                        "生命上限+" + hel + "，攻击+" + value);
            }

        }

        if (!fieldB.isSilence() && !fieldB.isDead() && fieldB != null && fieldB.getName().equals("厚土娘娘") && fieldB.getBuffStacks() < 99) {
            int[] skillLevel = CardSkillLevelUtil.calculateSkillLevels(fieldB.getLevel(), fieldB.getStar().doubleValue());
            if (skillLevel[1] > 0) {
                fieldB.setBuffStacks(fieldB.getBuffStacks() + 1);
                int hel = 197 * skillLevel[1];
                if (duoBaoGuanHuan()) {
                    fieldB.setCurrentHp(fieldB.getCurrentHp() + hel);
                } else {
                    fieldB.setMaxHp(fieldB.getMaxHp() + hel);
                    fieldB.setCurrentHp(fieldB.getCurrentHp() + hel);
                }
                int value = 67 * skillLevel[1];
                fieldB.addEffect(EffectType.ATTACK_UP, value, 99, fieldB.getId());

                addLog("后土聚能",
                        fieldB.getId(),
                        fieldB.getMaxHp(),
                        fieldB.getCurrentHp(),
                        hel,
                        fieldB.isOnField(),
                        fieldB.getId(),
                        fieldB.getMaxHp(),
                        fieldB.getCurrentHp(),
                        value,
                        fieldB.isOnField(),
                        EffectType.HP_UP,
                        DamageType.BUFF,
                        "生命上限+" + hel + "，攻击+" + value);
            }
        }

        // 阎王生死簿（多目标整合）
        if (campA.stream().anyMatch(g -> g.getName().equals("阎王") && !g.isDead() && !g.isSilence())) {
            Guardian yanwang = campA.stream()
                    .filter(g -> g.getName().equals("阎王") && !g.isDead())
                    .findFirst().get();
            if (!yanwang.isSilence()) {
                int[] skillLevel = CardSkillLevelUtil.calculateSkillLevels(yanwang.getLevel(), yanwang.getStar().doubleValue());
                List<Guardian> allUnits = new ArrayList<>();
                allUnits.addAll(campA);
                allUnits.addAll(campB);

                List<Guardian> aliveUnits = allUnits.stream()
                        .filter(g -> !g.isDead())
                        .collect(Collectors.toList());

                if (!aliveUnits.isEmpty()) {
                    Map<String, TargetBattleData> deadUnits = new HashMap<>();
                    List<Guardian> deadGuardians = new ArrayList<>();
                    int reduce = 100 * skillLevel[0];
                    if (duoBaoGuanHuan()) {
                        reduce=0;
                    }

                    Map<String, TargetBattleData> targetStatus = new HashMap<>();

                    int finalReduce = reduce;
                    aliveUnits.forEach(g -> {
                        int a = 0;
                        if (!g.isMaxHpNoDown()) {
                            a = finalReduce;
                        }
                        g.setMaxHp(g.getMaxHp() - a);
                        TargetBattleData data = new TargetBattleData(g.getMaxHp(), g.getCurrentHp(), a, g.isOnField());
                        targetStatus.put(g.getId(), data);
                        if (g.isDead()) {

                            deadUnits.put(g.getId(), data);
                            deadGuardians.add(g);
                        }
                    });


                    addMultiTargetLog("生死簿",
                            yanwang.getId(),
                            yanwang.getMaxHp(),
                            yanwang.getCurrentHp(),
                            yanwang.isOnField(),
                            targetStatus,
                            EffectType.MAX_HP_DOWN,
                            DamageType.MAGIC,
                            "降低全体单位生命上限");
                    // 死亡日志
                    if (!deadUnits.isEmpty()) {
                        addMultiTargetLog("UNIT_DEATH",
                                null,
                                0,
                                0,
                                false,
                                deadUnits,
                                null,
                                null,
                                "死亡");
                        //触发死亡技能
                        for (Guardian g : deadGuardians) {
                            triggerOnDeathSkills(g);
                        }
                    }
                    //触发受击技能
                    aliveUnits.forEach(g -> {
                        //触发受到任意伤害技能
                        triggerOnAttackedSkills(g, EffectType.MAX_HP_DOWN);
                    });

                }
            }


        }

        if (campB.stream().anyMatch(g -> g.getName().equals("阎王") && !g.isDead() && !g.isSilence())) {
            Guardian yanwang = campB.stream()
                    .filter(g -> g.getName().equals("阎王") && !g.isDead())
                    .findFirst().get();
            if (!yanwang.isSilence()) {
                int[] skillLevel = CardSkillLevelUtil.calculateSkillLevels(yanwang.getLevel(), yanwang.getStar().doubleValue());
                List<Guardian> allUnits = new ArrayList<>();
                allUnits.addAll(campA);
                allUnits.addAll(campB);

                List<Guardian> aliveUnits = allUnits.stream()
                        .filter(g -> !g.isDead())
                        .collect(Collectors.toList());

                if (!aliveUnits.isEmpty()) {
                    Map<String, TargetBattleData> deadUnits = new HashMap<>();
                    List<Guardian> deadGuardians = new ArrayList<>();
                    int reduce = 100 * skillLevel[0];
                    if (duoBaoGuanHuan()) {
                        reduce=0;
                    }

                    Map<String, TargetBattleData> targetStatus = new HashMap<>();

                    int finalReduce = reduce;
                    aliveUnits.forEach(g -> {
                        int a = 0;
                        if (!g.isMaxHpNoDown()) {
                            a = finalReduce;
                        }
                        g.setMaxHp(g.getMaxHp() - a);
                        TargetBattleData data = new TargetBattleData(g.getMaxHp(), g.getCurrentHp(), a, g.isOnField());
                        targetStatus.put(g.getId(), data);
                        if (g.isDead()) {

                            deadUnits.put(g.getId(), data);
                            deadGuardians.add(g);
                        }
                    });

                    addMultiTargetLog("生死簿",
                            yanwang.getId(),
                            yanwang.getMaxHp(),
                            yanwang.getCurrentHp(),
                            yanwang.isOnField(),
                            targetStatus,
                            EffectType.MAX_HP_DOWN,
                            DamageType.MAGIC,
                            "降低全体单位生命上限");
                    // 死亡日志
                    if (!deadUnits.isEmpty()) {
                        addMultiTargetLog("UNIT_DEATH",
                                null,
                                0,
                                0,
                                false,
                                deadUnits,
                                null,
                                null,
                                "死亡");
                        //触发死亡技能
                        for (Guardian g : deadGuardians) {
                            triggerOnDeathSkills(g);
                        }
                    }
                    //触发受击技能
                    aliveUnits.forEach(g -> {
                        //触发受到任意伤害技能
                        triggerOnAttackedSkills(g, EffectType.MAX_HP_DOWN);
                    });

                }
            }


        }
        if (campA.stream().anyMatch(g -> g.getName().equals("萌年兽") && !g.isDead() && !g.isSilence())) {
            Guardian nianshou = campA.stream()
                    .filter(g -> g.getName().equals("萌年兽") && !g.isDead())
                    .findFirst().get();
            if (!nianshou.isSilence() && nianshou.getBuffNianShous() < 5) {
                int[] skillLevel = CardSkillLevelUtil.calculateSkillLevels(nianshou.getLevel(), nianshou.getStar().doubleValue());
                if (skillLevel[1] > 0) {
                    nianshou.setBuffStacks(nianshou.getBuffNianShous() + 1);
                    int hel = 76 * skillLevel[1];
                    if (duoBaoGuanHuan()) {
                        nianshou.setCurrentHp(nianshou.getCurrentHp() + hel);
                    } else {
                        nianshou.setMaxHp(nianshou.getMaxHp() + hel);
                        nianshou.setCurrentHp(nianshou.getCurrentHp() + hel);
                    }
                    nianshou.setMaxHp(nianshou.getMaxHp() + hel);
                    nianshou.setCurrentHp(nianshou.getCurrentHp() + hel);
                    int value = 15 * skillLevel[1];
                    nianshou.addEffect(EffectType.FIRE_BOOST, value, 999, nianshou.getId());
                    addLog("幸运年糕",
                            nianshou.getId(),
                            nianshou.getMaxHp(),
                            nianshou.getCurrentHp(),
                            hel,
                            nianshou.isOnField(),
                            nianshou.getId(),
                            nianshou.getMaxHp(),
                            nianshou.getCurrentHp(),
                            hel,
                            nianshou.isOnField(),
                            EffectType.HP_UP,
                            DamageType.BUFF,
                            "生命上限+" + hel + "，火焰伤害+" + value);
                }

            }


        }


        if (campA.stream().anyMatch(g -> g.getName().equals("红孩儿") && !g.isDead() && !g.isSilence())) {
            Guardian nianshou = campA.stream()
                    .filter(g -> g.getName().equals("红孩儿") && !g.isDead())
                    .findFirst().get();
            if (!nianshou.isSilence()) {
                int[] skillLevel = CardSkillLevelUtil.calculateSkillLevels(nianshou.getLevel(), nianshou.getStar().doubleValue());
                int hel = 76 * skillLevel[0];
                if (duoBaoGuanHuan()) {
                    nianshou.setCurrentHp(nianshou.getCurrentHp() + hel);
                } else {
                    nianshou.setMaxHp(nianshou.getMaxHp() + hel);
                    nianshou.setCurrentHp(nianshou.getCurrentHp() + hel);
                }
                nianshou.setMaxHp(nianshou.getMaxHp() + hel);
                nianshou.setCurrentHp(nianshou.getCurrentHp() + hel);

                addLog("鞭挞",
                        nianshou.getId(),
                        nianshou.getMaxHp(),
                        nianshou.getCurrentHp(),
                        hel,
                        nianshou.isOnField(),
                        nianshou.getId(),
                        nianshou.getMaxHp(),
                        nianshou.getCurrentHp(),
                        hel,
                        nianshou.isOnField(),
                        EffectType.HP_UP,
                        DamageType.BUFF,
                        "生命上限+" + hel);

            }


        }


        if (campA.stream().anyMatch(g -> g.getName().equals("西岳大帝") && !g.isDead() && !g.isOnField() && !g.isSilence())) {
            Guardian nianshou = campA.stream()
                    .filter(g -> g.getName().equals("西岳大帝") && !g.isDead())
                    .findFirst().get();
            if (!nianshou.isSilence() && nianshou.getBuffStacks() < 5) {
                int[] skillLevel = CardSkillLevelUtil.calculateSkillLevels(nianshou.getLevel(), nianshou.getStar().doubleValue());
                if (skillLevel[1] > 0) {
                    nianshou.setBuffStacks(nianshou.getBuffStacks() + 1);
                    int value = 15 * skillLevel[1];
                    nianshou.addEffect(EffectType.MISSILE_BOOST, value, 999, nianshou.getId());
                    addLog("策兵奇袭",
                            nianshou.getId(),
                            nianshou.getMaxHp(),
                            nianshou.getCurrentHp(),
                            value,
                            nianshou.isOnField(),
                            nianshou.getId(),
                            nianshou.getMaxHp(),
                            nianshou.getCurrentHp(),
                            value,
                            nianshou.isOnField(),
                            EffectType.MISSILE_BOOST,
                            DamageType.BUFF,
                            "飞弹伤害+" + value);
                }

            }


        }

        if (campB.stream().anyMatch(g -> g.getName().equals("西岳大帝") && !g.isDead() && !g.isOnField() && !g.isSilence())) {
            Guardian nianshou = campB.stream()
                    .filter(g -> g.getName().equals("西岳大帝") && !g.isDead())
                    .findFirst().get();
            if (!nianshou.isSilence() && nianshou.getBuffStacks() < 5) {
                int[] skillLevel = CardSkillLevelUtil.calculateSkillLevels(nianshou.getLevel(), nianshou.getStar().doubleValue());
                if (skillLevel[1] > 0) {
                    nianshou.setBuffStacks(nianshou.getBuffStacks() + 1);
                    int value = 15 * skillLevel[1];
                    nianshou.addEffect(EffectType.MISSILE_BOOST, value, 999, nianshou.getId());
                    addLog("策兵奇袭",
                            nianshou.getId(),
                            nianshou.getMaxHp(),
                            nianshou.getCurrentHp(),
                            value,
                            nianshou.isOnField(),
                            nianshou.getId(),
                            nianshou.getMaxHp(),
                            nianshou.getCurrentHp(),
                            value,
                            nianshou.isOnField(),
                            EffectType.MISSILE_BOOST,
                            DamageType.BUFF,
                            "飞弹伤害+" + value);
                }

            }


        }
        //
        if (campB.stream().anyMatch(g -> g.getName().equals("萌年兽") && !g.isDead() && !g.isSilence())) {

            Guardian nianshou = campB.stream()
                    .filter(g -> g.getName().equals("萌年兽") && !g.isDead())
                    .findFirst().get();
            if (!nianshou.isSilence() && nianshou.getBuffNianShous() < 5) {
                int[] skillLevel = CardSkillLevelUtil.calculateSkillLevels(nianshou.getLevel(), nianshou.getStar().doubleValue());
                if (skillLevel[1] > 0) {
                    nianshou.setBuffStacks(nianshou.getBuffNianShous() + 1);
                    int hel = 76 * skillLevel[1];
                    if (duoBaoGuanHuan()) {
                        nianshou.setCurrentHp(nianshou.getCurrentHp() + hel);
                    } else {
                        nianshou.setMaxHp(nianshou.getMaxHp() + hel);
                        nianshou.setCurrentHp(nianshou.getCurrentHp() + hel);
                    }
                    nianshou.setMaxHp(nianshou.getMaxHp() + hel);
                    nianshou.setCurrentHp(nianshou.getCurrentHp() + hel);
                    int value = 15 * skillLevel[1];
                    nianshou.addEffect(EffectType.FIRE_BOOST, value, 999, nianshou.getId());
                    addLog("幸运年糕",
                            nianshou.getId(),
                            nianshou.getMaxHp(),
                            nianshou.getCurrentHp(),
                            hel,
                            nianshou.isOnField(),
                            nianshou.getId(),
                            nianshou.getMaxHp(),
                            nianshou.getCurrentHp(),
                            hel,
                            nianshou.isOnField(),
                            EffectType.HP_UP,
                            DamageType.BUFF,
                            nianshou.getName() + "生命上限+" + hel + "，火焰伤害+" + value);
                }

            }

        }


        if (campB.stream().anyMatch(g -> g.getName().equals("红孩儿") && !g.isDead() && !g.isSilence())) {
            Guardian nianshou = campB.stream()
                    .filter(g -> g.getName().equals("红孩儿") && !g.isDead())
                    .findFirst().get();
            if (!nianshou.isSilence()) {
                int[] skillLevel = CardSkillLevelUtil.calculateSkillLevels(nianshou.getLevel(), nianshou.getStar().doubleValue());
                int hel = 76 * skillLevel[0];
                if (duoBaoGuanHuan()) {
                    nianshou.setCurrentHp(nianshou.getCurrentHp() + hel);
                } else {
                    nianshou.setMaxHp(nianshou.getMaxHp() + hel);
                    nianshou.setCurrentHp(nianshou.getCurrentHp() + hel);
                }


                addLog("鞭挞",
                        nianshou.getId(),
                        nianshou.getMaxHp(),
                        nianshou.getCurrentHp(),
                        hel,
                        nianshou.isOnField(),
                        nianshou.getId(),
                        nianshou.getMaxHp(),
                        nianshou.getCurrentHp(),
                        hel,
                        nianshou.isOnField(),
                        EffectType.HP_UP,
                        null,
                        "生命上限+" + hel);

            }


        }


        if (campA.stream().anyMatch(g -> g.getName().equals("玉兔精") && !g.isDead() && !g.isSilence())) {

            Guardian nianshou = campA.stream()
                    .filter(g -> g.getName().equals("玉兔精") && !g.isDead())
                    .findFirst().get();
            if (!nianshou.isSilence() && nianshou.getBuffNianShous() < 5) {
                int[] skillLevel = CardSkillLevelUtil.calculateSkillLevels(nianshou.getLevel(), nianshou.getStar().doubleValue());
                nianshou.setBuffStacks(nianshou.getBuffNianShous() + 1);
                int totalPoisonDamage = 20 * skillLevel[1];
                // 1. 计算所有中毒效果的总伤害（累加 POISON 类型的 value）
                // 2. 计算毒抗相关（直接基于你现有 EffectInstance 计算，不新增 Guardian 方法）
                // 中毒增益：所有 POISON_RESIST 类型效果的 value 总和
                double resistUpPret = calculateTotalUpPretVaule(nianshou, EffectType.SPEED_UP_PRET);
                int speed = (int) (totalPoisonDamage * resistUpPret);
                nianshou.setSpeed(nianshou.getSpeed() + speed);
                addLog("幸运之脚",
                        nianshou.getId(),
                        nianshou.getMaxHp(),
                        nianshou.getCurrentHp(),
                        speed,
                        nianshou.isOnField(),
                        nianshou.getId(),
                        nianshou.getMaxHp(),
                        nianshou.getCurrentHp(),
                        speed,
                        nianshou.isOnField(),
                        EffectType.SPEED_UP,
                        DamageType.BUFF,
                        "速度上限+" + speed);

            }

        }
        if (campB.stream().anyMatch(g -> g.getName().equals("玉兔精") && !g.isDead() && !g.isSilence())) {

            Guardian nianshou = campB.stream()
                    .filter(g -> g.getName().equals("玉兔精") && !g.isDead())
                    .findFirst().get();
            if (!nianshou.isSilence() && nianshou.getBuffNianShous() < 5) {
                int[] skillLevel = CardSkillLevelUtil.calculateSkillLevels(nianshou.getLevel(), nianshou.getStar().doubleValue());
                nianshou.setBuffStacks(nianshou.getBuffNianShous() + 1);
                int totalPoisonDamage = 20 * skillLevel[1];
                // 1. 计算所有中毒效果的总伤害（累加 POISON 类型的 value）
                // 2. 计算毒抗相关（直接基于你现有 EffectInstance 计算，不新增 Guardian 方法）
                // 中毒增益：所有 POISON_RESIST 类型效果的 value 总和
                double resistUpPret = calculateTotalUpPretVaule(nianshou, EffectType.SPEED_UP_PRET);
                int speed = (int) (totalPoisonDamage * resistUpPret);
                nianshou.setSpeed(nianshou.getSpeed() + speed);
                addLog("幸运之脚",
                        nianshou.getId(),
                        nianshou.getMaxHp(),
                        nianshou.getCurrentHp(),
                        speed,
                        nianshou.isOnField(),
                        nianshou.getId(),
                        nianshou.getMaxHp(),
                        nianshou.getCurrentHp(),
                        speed,
                        nianshou.isOnField(),
                        EffectType.SPEED_UP,
                        DamageType.BUFF,
                        "速度上限+" + speed);
            }

        }
        // A玄冥
//            玄冥，毒入骨髓Lv1场下，每回合令随机敌方中毒每回损失16点生命；
        if (campA.stream().anyMatch(g -> g.getName().equals("玄冥") && !g.isDead() && !g.isOnField() && !g.isSilence())) {
            Guardian daji = campA.stream()
                    .filter(g -> g.getName().equals("玄冥") && !g.isDead() && !g.isOnField())
                    .findFirst().get();
            int[] skillLevel = CardSkillLevelUtil.calculateSkillLevels(daji.getLevel(), daji.getStar().doubleValue());

            // 毒入骨髓：随机敌方中毒
            List<Guardian> enemies = campB.stream()
                    .filter(g -> !g.isDead())
                    .collect(Collectors.toList());

            if (!enemies.isEmpty()) {
                Guardian randomEnemy = enemies.get(random.nextInt(enemies.size()));
                int totalPoisonDamage = 16 * skillLevel[0];
                // 1. 计算所有中毒效果的总伤害（累加 POISON 类型的 value）
                // 2. 计算毒抗相关（直接基于你现有 EffectInstance 计算，不新增 Guardian 方法）
                // 中毒增益：所有 POISON_RESIST 类型效果的 value 总和
                int resistUp = calculateTotalVaule(daji, EffectType.POISON_BOOST);
                // 中毒增益百分比：所有 POISON_RESIST 类型效果的 value 乘积
                double resistUpPret = calculateTotalUpPretVaule(daji, EffectType.POISON_BOOST_PRET);
                // 中毒降低：所有 POISON_RESIST_DOWN 类型效果的 value 总和
                int resistDown = calculateTotalVaule(daji, EffectType.POISON_DOWN);
                // 中毒降低百分比：所有 POISON_RESIST 类型效果的 value 乘积
                double resistDownPret = calculateTotalDownPretVaule(daji, EffectType.POISON_DOWN_PRET);

                int poisonValue = (int) (totalPoisonDamage * resistUpPret * resistDownPret + (resistUp + daji.getDsAtk() - resistDown));
                randomEnemy.addEffect(EffectType.POISON, poisonValue, 99, daji.getId());

                addLog("毒入骨髓",
                        daji.getId(),
                        daji.getMaxHp(),
                        daji.getCurrentHp(),
                        0,
                        daji.isOnField(),
                        randomEnemy.getId(),
                        randomEnemy.getMaxHp(),
                        randomEnemy.getCurrentHp(),
                        0,
                        randomEnemy.isOnField(),
                        EffectType.POISON,
                        DamageType.POISON,
                        "中毒");
            }
        }

        // B队玄冥
        if (campB.stream().anyMatch(g -> g.getName().equals("玄冥") && !g.isDead() && !g.isOnField() && !g.isSilence())) {
            Guardian daji = campB.stream()
                    .filter(g -> g.getName().equals("玄冥") && !g.isDead() && !g.isOnField())
                    .findFirst().get();
            int[] skillLevel = CardSkillLevelUtil.calculateSkillLevels(daji.getLevel(), daji.getStar().doubleValue());


            // 谄媚噬魂：随机敌方中毒
            List<Guardian> enemies = campA.stream()
                    .filter(g -> !g.isDead())
                    .collect(Collectors.toList());

            if (!enemies.isEmpty()) {
                Guardian randomEnemy = enemies.get(random.nextInt(enemies.size()));
                int totalPoisonDamage = 16 * skillLevel[0];
                // 1. 计算所有中毒效果的总伤害（累加 POISON 类型的 value）
                // 2. 计算毒抗相关（直接基于你现有 EffectInstance 计算，不新增 Guardian 方法）
                // 中毒增益：所有 POISON_RESIST 类型效果的 value 总和
                int resistUp = calculateTotalVaule(daji, EffectType.FIRE_BOOST);
                // 中毒增益百分比：所有 POISON_RESIST 类型效果的 value 乘积
                double resistUpPret = calculateTotalUpPretVaule(daji, EffectType.FIRE_BOOST_PRET);
                // 中毒降低：所有 POISON_RESIST_DOWN 类型效果的 value 总和
                int resistDown = calculateTotalVaule(daji, EffectType.FIRE_DOWN);
                // 中毒降低百分比：所有 POISON_RESIST 类型效果的 value 乘积
                double resistDownPret = calculateTotalDownPretVaule(daji, EffectType.FIRE_DOWN_PRET);

                int poisonValue = (int) (totalPoisonDamage * resistUpPret * resistDownPret + (resistUp + daji.getDsAtk() - resistDown));
                randomEnemy.addEffect(EffectType.POISON, poisonValue, 99, daji.getId());

                addLog("毒入骨髓",
                        daji.getId(),
                        daji.getMaxHp(),
                        daji.getCurrentHp(),
                        0,
                        daji.isOnField(),
                        randomEnemy.getId(),
                        randomEnemy.getMaxHp(),
                        randomEnemy.getCurrentHp(),
                        0,
                        randomEnemy.isOnField(),
                        EffectType.POISON,
                        DamageType.POISON,
                        "中毒+" + poisonValue);
            }
        }

        // A玄冥
//            任意位置，若场上敌方有疾病则每回合令其中毒，受到40点毒素伤害
        if (campA.stream().anyMatch(g -> g.getName().equals("金钩大王") && !g.isDead() && !g.isOnField() && !g.isSilence())) {
            if (!fieldB.isDead() && fieldB.getEffects() != null) {
                Guardian daji = campA.stream()
                        .filter(g -> g.getName().equals("金钩大王") && !g.isDead() && !g.isOnField())
                        .findFirst().get();
                int[] skillLevel = CardSkillLevelUtil.calculateSkillLevels(daji.getLevel(), daji.getStar().doubleValue());
                if (skillLevel[1] > 0) {
                    int totalPoisonDamage = 40 * skillLevel[0];
                    // 1. 计算所有中毒效果的总伤害（累加 POISON 类型的 value）
                    // 2. 计算毒抗相关（直接基于你现有 EffectInstance 计算，不新增 Guardian 方法）
                    // 中毒增益：所有 POISON_RESIST 类型效果的 value 总和
                    int resistUp = calculateTotalVaule(daji, EffectType.POISON_BOOST);
                    // 中毒增益百分比：所有 POISON_RESIST 类型效果的 value 乘积
                    double resistUpPret = calculateTotalUpPretVaule(daji, EffectType.POISON_BOOST_PRET);
                    // 中毒降低：所有 POISON_RESIST_DOWN 类型效果的 value 总和
                    int resistDown = calculateTotalVaule(daji, EffectType.POISON_DOWN);
                    // 中毒降低百分比：所有 POISON_RESIST 类型效果的 value 乘积
                    double resistDownPret = calculateTotalDownPretVaule(daji, EffectType.POISON_DOWN_PRET);

                    int poisonValue = (int) (totalPoisonDamage * resistUpPret * resistDownPret + (resistUp + daji.getDsAtk() - resistDown));
                    fieldB.addEffect(EffectType.POISON, poisonValue, 99, daji.getId());
                    addLog("疾病传染",
                            daji.getId(),
                            daji.getMaxHp(),
                            daji.getCurrentHp(),
                            poisonValue,
                            daji.isOnField(),
                            fieldB.getId(),
                            fieldB.getMaxHp(),
                            fieldB.getCurrentHp(),
                            poisonValue,
                            fieldB.isOnField(),
                            EffectType.POISON,
                            DamageType.POISON,
                            "中毒+" + poisonValue);
                }

            }

        }

        // B队玄冥
        if (campB.stream().anyMatch(g -> g.getName().equals("金钩大王") && !g.isDead() && !g.isOnField() && !g.isSilence())) {
            if (!fieldA.isDead() && fieldA.getEffects() != null) {
                Guardian daji = campB.stream()
                        .filter(g -> g.getName().equals("金钩大王") && !g.isDead() && !g.isOnField())
                        .findFirst().get();
                int[] skillLevel = CardSkillLevelUtil.calculateSkillLevels(daji.getLevel(), daji.getStar().doubleValue());
                if (skillLevel[1] > 0) {
                    int totalPoisonDamage = 40 * skillLevel[0];
                    // 1. 计算所有中毒效果的总伤害（累加 POISON 类型的 value）
                    // 2. 计算毒抗相关（直接基于你现有 EffectInstance 计算，不新增 Guardian 方法）
                    // 中毒增益：所有 POISON_RESIST 类型效果的 value 总和
                    int resistUp = calculateTotalVaule(daji, EffectType.POISON_BOOST);
                    // 中毒增益百分比：所有 POISON_RESIST 类型效果的 value 乘积
                    double resistUpPret = calculateTotalUpPretVaule(daji, EffectType.POISON_BOOST_PRET);
                    // 中毒降低：所有 POISON_RESIST_DOWN 类型效果的 value 总和
                    int resistDown = calculateTotalVaule(daji, EffectType.POISON_DOWN);
                    // 中毒降低百分比：所有 POISON_RESIST 类型效果的 value 乘积
                    double resistDownPret = calculateTotalDownPretVaule(daji, EffectType.POISON_DOWN_PRET);

                    int poisonValue = (int) (totalPoisonDamage * resistUpPret * resistDownPret + (resistUp + daji.getDsAtk() - resistDown));
                    fieldA.addEffect(EffectType.POISON, poisonValue, 99, daji.getId());
                    addLog("疾病传染",
                            daji.getId(),
                            daji.getMaxHp(),
                            daji.getCurrentHp(),
                            poisonValue,
                            daji.isOnField(),
                            fieldA.getId(),
                            fieldA.getMaxHp(),
                            fieldA.getCurrentHp(),
                            poisonValue,
                            fieldA.isOnField(),
                            EffectType.POISON,
                            DamageType.POISON,
                            "中毒+" + poisonValue);
                }

            }

        }


//        南华真人，魂力飞弹Lv1场下，每当新单位入场时，对场上敌人造成178点飞弹伤害；报复神箭Lv1场下，每回合对场上敌方造成106；镇元子协同Lv1与镇元子在同一队伍时，增加自身453点生命上限，158点攻击，158点速度。
        if (campA.stream().anyMatch(g -> g.getName().equals("南华真人") && !g.isDead() && !g.isOnField() && !g.isSilence())) {
            if (!fieldB.isDead() && fieldB.getEffects() != null) {
                Guardian guardian = campA.stream()
                        .filter(g -> g.getName().equals("南华真人") && !g.isDead() && !g.isOnField())
                        .findFirst().get();
                int[] skillLevel = CardSkillLevelUtil.calculateSkillLevels(guardian.getLevel(), guardian.getStar().doubleValue());
                if (fieldB != null && !fieldB.isDead()) {
                    // 1. 计算所有中毒效果的总伤害（累加 POISON 类型的 value）
                    int totalPoisonDamage = 106 * skillLevel[0];
                    // 2. 计算毒抗相关（直接基于你现有 EffectInstance 计算，不新增 Guardian 方法）
                    // 飞弹增益：所有 POISON_RESIST 类型效果的 value 总和
                    int resistUp = calculateTotalVaule(guardian, EffectType.MISSILE_BOOST);
                    // 飞弹增益百分比：所有 POISON_RESIST 类型效果的 value 乘积
                    double resistUpPret = calculateTotalUpPretVaule(guardian, EffectType.MISSILE_BOOST_PRET);
                    // 飞弹降低：所有 POISON_RESIST_DOWN 类型效果的 value 总和
                    int resistDown = calculateTotalVaule(guardian, EffectType.MISSILE_DOWN);
                    // 飞弹降低百分比：所有 POISON_RESIST 类型效果的 value 乘积
                    double resistDownPret = calculateTotalDownPretVaule(guardian, EffectType.MISSILE_DOWN_PRET);


                    // 2. 计算毒抗相关（直接基于你现有 EffectInstance 计算，不新增 Guardian 方法）
                    // 弹抗增益：所有 POISON_RESIST 类型效果的 value 总和
                    int targetUp = calculateTotalVaule(fieldB, EffectType.MISSILE_RESIST_BOOST);
                    // 弹抗增益百分比：所有 POISON_RESIST 类型效果的 value 乘积
                    double targetUpPret = calculateTotalDownPretVaule(fieldB, EffectType.MISSILE_RESIST_BOOST_PRET);
                    // 弹抗降低：所有 POISON_RESIST_DOWN 类型效果的 value 总和
                    int targetDown = calculateTotalVaule(fieldB, EffectType.MISSILE_RESIST_DOWN);
                    // 弹抗降低百分比：所有 POISON_RESIST 类型效果的 value fieldA
                    double targetDownPret = calculateTotalUpPretVaule(fieldB, EffectType.MISSILE_RESIST_DOWN_PRET);
                    // 最终（仅基于 buff 计算，无新增方法）

                    int burnDamage = (int) (totalPoisonDamage * resistUpPret * resistDownPret * targetUpPret * targetDownPret
                            + (resistUp - resistDown + guardian.getFdAtk() - fieldB.getFdDef() - targetUp + targetDown));

                    if (burnDamage < 0) {
                        burnDamage = 0;
                    }

                    // 4. 扣除伤害
                    fieldB.setCurrentHp(fieldB.getCurrentHp() - burnDamage);
                    Map<String, TargetBattleData> deadUnits = new HashMap<>();

                    if (fieldB.getCurrentHp() <= 0) {
                        fieldB.setDead(true);
                        fieldB.setOnField(false);
                        TargetBattleData data = new TargetBattleData(fieldB.getMaxHp(), fieldB.getCurrentHp(), burnDamage, fieldB.isOnField());
                        deadUnits.put(fieldB.getId(), data);
                    }
                    addLog("报复神箭",
                            guardian.getId(),
                            guardian.getMaxHp(),
                            guardian.getCurrentHp(),
                            0,
                            guardian.isOnField(),
                            fieldB.getId(),
                            fieldB.getMaxHp(),
                            fieldB.getCurrentHp(),
                            burnDamage,
                            fieldB.isOnField(),
                            EffectType.MISSILE_DAMAGE,
                            DamageType.MISSILE,
                            "-" + burnDamage);
                    // 死亡日志
                    if (!deadUnits.isEmpty()) {
                        addMultiTargetLog("UNIT_DEATH",
                                null,
                                0,
                                0,
                                false,
                                deadUnits,
                                null,
                                null,
                                "死亡");
                        //触发死亡技能
                        triggerOnDeathSkills(fieldB);

                    } else {
                        //触发受击技能
                        triggerOnAttackedSkills(fieldB, EffectType.MISSILE_DAMAGE);
                    }
                }
            }

        }


        //        南华真人，魂力飞弹Lv1场下，每当新单位入场时，对场上敌人造成178点飞弹伤害；报复神箭Lv1场下，每回合对场上敌方造成106；镇元子协同Lv1与镇元子在同一队伍时，增加自身453点生命上限，158点攻击，158点速度。
        if (campB.stream().anyMatch(g -> g.getName().equals("南华真人") && !g.isDead() && !g.isOnField() && !g.isSilence())) {
            if (!fieldA.isDead() && fieldA.getEffects() != null) {
                Guardian guardian = campB.stream()
                        .filter(g -> g.getName().equals("南华真人") && !g.isDead() && !g.isOnField())
                        .findFirst().get();
                int[] skillLevel = CardSkillLevelUtil.calculateSkillLevels(guardian.getLevel(), guardian.getStar().doubleValue());
                if (fieldA != null && !fieldA.isDead()) {
                    // 1. 计算所有中毒效果的总伤害（累加 POISON 类型的 value）
                    int totalPoisonDamage = 106 * skillLevel[0];
                    // 2. 计算毒抗相关（直接基于你现有 EffectInstance 计算，不新增 Guardian 方法）
                    // 飞弹增益：所有 POISON_RESIST 类型效果的 value 总和
                    int resistUp = calculateTotalVaule(guardian, EffectType.MISSILE_BOOST);
                    // 飞弹增益百分比：所有 POISON_RESIST 类型效果的 value 乘积
                    double resistUpPret = calculateTotalUpPretVaule(guardian, EffectType.MISSILE_BOOST_PRET);
                    // 飞弹降低：所有 POISON_RESIST_DOWN 类型效果的 value 总和
                    int resistDown = calculateTotalVaule(guardian, EffectType.MISSILE_DOWN);
                    // 飞弹降低百分比：所有 POISON_RESIST 类型效果的 value 乘积
                    double resistDownPret = calculateTotalDownPretVaule(guardian, EffectType.MISSILE_DOWN_PRET);


                    // 2. 计算毒抗相关（直接基于你现有 EffectInstance 计算，不新增 Guardian 方法）
                    // 弹抗增益：所有 POISON_RESIST 类型效果的 value 总和
                    int targetUp = calculateTotalVaule(fieldA, EffectType.MISSILE_RESIST_BOOST);
                    // 弹抗增益百分比：所有 POISON_RESIST 类型效果的 value 乘积
                    double targetUpPret = calculateTotalDownPretVaule(fieldA, EffectType.MISSILE_RESIST_BOOST_PRET);
                    // 弹抗降低：所有 POISON_RESIST_DOWN 类型效果的 value 总和
                    int targetDown = calculateTotalVaule(fieldA, EffectType.MISSILE_RESIST_DOWN);
                    // 弹抗降低百分比：所有 POISON_RESIST 类型效果的 value fieldA
                    double targetDownPret = calculateTotalUpPretVaule(fieldA, EffectType.MISSILE_RESIST_DOWN_PRET);
                    // 最终（仅基于 buff 计算，无新增方法）

                    int burnDamage = (int) (totalPoisonDamage * resistUpPret * resistDownPret * targetUpPret * targetDownPret
                            + (resistUp - resistDown + guardian.getFdAtk() - fieldA.getFdDef() - targetUp + targetDown));

                    if (burnDamage < 0) {
                        burnDamage = 0;
                    }

                    // 4. 扣除伤害
                    fieldA.setCurrentHp(fieldA.getCurrentHp() - burnDamage);
                    Map<String, TargetBattleData> deadUnits = new HashMap<>();

                    if (fieldA.getCurrentHp() <= 0) {
                        fieldA.setDead(true);
                        fieldA.setOnField(false);
                        TargetBattleData data = new TargetBattleData(fieldA.getMaxHp(), fieldA.getCurrentHp(), burnDamage, fieldA.isOnField());
                        deadUnits.put(fieldA.getId(), data);
                    }
                    addLog("报复神箭",
                            guardian.getId(),
                            guardian.getMaxHp(),
                            guardian.getCurrentHp(),
                            0,
                            guardian.isOnField(),
                            fieldA.getId(),
                            fieldA.getMaxHp(),
                            fieldA.getCurrentHp(),
                            burnDamage,
                            fieldA.isOnField(),
                            EffectType.MISSILE_DAMAGE,
                            DamageType.MISSILE,
                            "-" + burnDamage);
                    // 死亡日志
                    if (!deadUnits.isEmpty()) {
                        addMultiTargetLog("UNIT_DEATH",
                                null,
                                0,
                                0,
                                false,
                                deadUnits,
                                null,
                                null,
                                "死亡");
                        //触发死亡技能
                        triggerOnDeathSkills(fieldA);

                    } else {
                        //触发受击技能
                        triggerOnAttackedSkills(fieldA, EffectType.MISSILE_DAMAGE);
                    }
                }
            }

        }

//        南岳大帝，通灵神箭Lv1每回合对同位置敌方造成42点飞弹伤害；草船借箭Lv1受到火焰伤害时，增加自身飞弹伤害13点，最多叠加99层；大帝协同Lv8与西岳大帝在同一队伍时，增加自身604点生命上限，158点攻击，211点速度。
        if (campA.stream().anyMatch(g -> g.getName().equals("南岳大帝") && !g.isDead() && !g.isOnField() && !g.isSilence())) {
            Guardian guardian = campA.stream()
                    .filter(g -> g.getName().equals("南岳大帝") && !g.isDead() && !g.isOnField())
                    .findFirst().get();
            List<Guardian> aliveUnits = campB.stream()
                    .filter(g -> !g.isDead() && guardian.getPosition() == g.getPosition())
                    .collect(Collectors.toList());
            if (Xtool.isNotNull(aliveUnits)) {
                Guardian defender = aliveUnits.get(0);
                int[] skillLevel = CardSkillLevelUtil.calculateSkillLevels(guardian.getLevel(), guardian.getStar().doubleValue());
                if (defender != null && !defender.isDead()) {
                    // 1. 计算所有中毒效果的总伤害（累加 POISON 类型的 value）
                    int totalPoisonDamage = 42 * skillLevel[0];
                    // 2. 计算毒抗相关（直接基于你现有 EffectInstance 计算，不新增 Guardian 方法）
                    // 飞弹增益：所有 POISON_RESIST 类型效果的 value 总和
                    int resistUp = calculateTotalVaule(guardian, EffectType.MISSILE_BOOST);
                    // 飞弹增益百分比：所有 POISON_RESIST 类型效果的 value 乘积
                    double resistUpPret = calculateTotalUpPretVaule(guardian, EffectType.MISSILE_BOOST_PRET);
                    // 飞弹降低：所有 POISON_RESIST_DOWN 类型效果的 value 总和
                    int resistDown = calculateTotalVaule(guardian, EffectType.MISSILE_DOWN);
                    // 飞弹降低百分比：所有 POISON_RESIST 类型效果的 value 乘积
                    double resistDownPret = calculateTotalDownPretVaule(guardian, EffectType.MISSILE_DOWN_PRET);


                    // 2. 计算毒抗相关（直接基于你现有 EffectInstance 计算，不新增 Guardian 方法）
                    // 弹抗增益：所有 POISON_RESIST 类型效果的 value 总和
                    int targetUp = calculateTotalVaule(defender, EffectType.MISSILE_RESIST_BOOST);
                    // 弹抗增益百分比：所有 POISON_RESIST 类型效果的 value 乘积
                    double targetUpPret = calculateTotalDownPretVaule(defender, EffectType.MISSILE_RESIST_BOOST_PRET);
                    // 弹抗降低：所有 POISON_RESIST_DOWN 类型效果的 value 总和
                    int targetDown = calculateTotalVaule(defender, EffectType.MISSILE_RESIST_DOWN);
                    // 弹抗降低百分比：所有 POISON_RESIST 类型效果的 value fieldA
                    double targetDownPret = calculateTotalUpPretVaule(defender, EffectType.MISSILE_RESIST_DOWN_PRET);
                    // 最终（仅基于 buff 计算，无新增方法）

                    int burnDamage = (int) (totalPoisonDamage * resistUpPret * resistDownPret * targetUpPret * targetDownPret
                            + (resistUp - resistDown + guardian.getFdAtk() - defender.getFdDef() - targetUp + targetDown));

                    if (burnDamage < 0) {
                        burnDamage = 0;
                    }

                    // 4. 扣除伤害
                    defender.setCurrentHp(defender.getCurrentHp() - burnDamage);
                    Map<String, TargetBattleData> deadUnits = new HashMap<>();

                    if (defender.getCurrentHp() <= 0) {
                        defender.setDead(true);
                        defender.setOnField(false);
                        TargetBattleData data = new TargetBattleData(defender.getMaxHp(), defender.getCurrentHp(), burnDamage, defender.isOnField());
                        deadUnits.put(defender.getId(), data);
                    }
                    addLog("通灵神箭",
                            guardian.getId(),
                            guardian.getMaxHp(),
                            guardian.getCurrentHp(),
                            0,
                            guardian.isOnField(),
                            defender.getId(),
                            defender.getMaxHp(),
                            defender.getCurrentHp(),
                            burnDamage,
                            defender.isOnField(),
                            EffectType.MISSILE_DAMAGE,
                            DamageType.MISSILE,
                            "-" + burnDamage);
                    // 死亡日志
                    if (!deadUnits.isEmpty()) {
                        addMultiTargetLog("UNIT_DEATH",
                                null,
                                0,
                                0,
                                false,
                                deadUnits,
                                null,
                                null,
                                "死亡");
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
        if (campB.stream().anyMatch(g -> g.getName().equals("南岳大帝") && !g.isDead() && !g.isOnField() && !g.isSilence())) {
            Guardian guardian = campB.stream()
                    .filter(g -> g.getName().equals("南岳大帝") && !g.isDead() && !g.isOnField())
                    .findFirst().get();
            List<Guardian> aliveUnits = campA.stream()
                    .filter(g -> !g.isDead() && guardian.getPosition() == g.getPosition())
                    .collect(Collectors.toList());
            if (Xtool.isNotNull(aliveUnits)) {
                Guardian defender = aliveUnits.get(0);
                int[] skillLevel = CardSkillLevelUtil.calculateSkillLevels(guardian.getLevel(), guardian.getStar().doubleValue());
                if (defender != null && !defender.isDead()) {
                    // 1. 计算所有中毒效果的总伤害（累加 POISON 类型的 value）
                    int totalPoisonDamage = 42 * skillLevel[0];
                    // 2. 计算毒抗相关（直接基于你现有 EffectInstance 计算，不新增 Guardian 方法）
                    // 飞弹增益：所有 POISON_RESIST 类型效果的 value 总和
                    int resistUp = calculateTotalVaule(guardian, EffectType.MISSILE_BOOST);
                    // 飞弹增益百分比：所有 POISON_RESIST 类型效果的 value 乘积
                    double resistUpPret = calculateTotalUpPretVaule(guardian, EffectType.MISSILE_BOOST_PRET);
                    // 飞弹降低：所有 POISON_RESIST_DOWN 类型效果的 value 总和
                    int resistDown = calculateTotalVaule(guardian, EffectType.MISSILE_DOWN);
                    // 飞弹降低百分比：所有 POISON_RESIST 类型效果的 value 乘积
                    double resistDownPret = calculateTotalDownPretVaule(guardian, EffectType.MISSILE_DOWN_PRET);


                    // 2. 计算毒抗相关（直接基于你现有 EffectInstance 计算，不新增 Guardian 方法）
                    // 弹抗增益：所有 POISON_RESIST 类型效果的 value 总和
                    int targetUp = calculateTotalVaule(defender, EffectType.MISSILE_RESIST_BOOST);
                    // 弹抗增益百分比：所有 POISON_RESIST 类型效果的 value 乘积
                    double targetUpPret = calculateTotalDownPretVaule(defender, EffectType.MISSILE_RESIST_BOOST_PRET);
                    // 弹抗降低：所有 POISON_RESIST_DOWN 类型效果的 value 总和
                    int targetDown = calculateTotalVaule(defender, EffectType.MISSILE_RESIST_DOWN);
                    // 弹抗降低百分比：所有 POISON_RESIST 类型效果的 value fieldA
                    double targetDownPret = calculateTotalUpPretVaule(defender, EffectType.MISSILE_RESIST_DOWN_PRET);
                    // 最终（仅基于 buff 计算，无新增方法）

                    int burnDamage = (int) (totalPoisonDamage * resistUpPret * resistDownPret * targetUpPret * targetDownPret
                            + (resistUp - resistDown + guardian.getFdAtk() - defender.getFdDef() - targetUp + targetDown));

                    if (burnDamage < 0) {
                        burnDamage = 0;
                    }

                    // 4. 扣除伤害
                    defender.setCurrentHp(defender.getCurrentHp() - burnDamage);
                    Map<String, TargetBattleData> deadUnits = new HashMap<>();

                    if (defender.getCurrentHp() <= 0) {
                        defender.setDead(true);
                        defender.setOnField(false);
                        TargetBattleData data = new TargetBattleData(defender.getMaxHp(), defender.getCurrentHp(), burnDamage, defender.isOnField());
                        deadUnits.put(defender.getId(), data);
                    }
                    addLog("通灵神箭",
                            guardian.getId(),
                            guardian.getMaxHp(),
                            guardian.getCurrentHp(),
                            0,
                            guardian.isOnField(),
                            defender.getId(),
                            defender.getMaxHp(),
                            defender.getCurrentHp(),
                            burnDamage,
                            defender.isOnField(),
                            EffectType.MISSILE_DAMAGE,
                            DamageType.MISSILE,
                            "-" + burnDamage);
                    // 死亡日志
                    if (!deadUnits.isEmpty()) {
                        addMultiTargetLog("UNIT_DEATH",
                                null,
                                0,
                                0,
                                false,
                                deadUnits,
                                null,
                                null,
                                "死亡");
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
        if (campA.stream().anyMatch(g -> g.getName().equals("太岁灵君") && !g.isDead() && !g.isOnField() && !g.isSilence())) {
            Guardian guardian = campA.stream()
                    .filter(g -> g.getName().equals("太岁灵君") && !g.isDead() && !g.isOnField())
                    .findFirst().get();
            List<Guardian> aliveUnits = campB.stream()
                    .filter(g -> !g.isDead() && guardian.getPosition() == g.getPosition())
                    .collect(Collectors.toList());
            if (Xtool.isNotNull(aliveUnits)) {
                Guardian defender = aliveUnits.get(0);
                int[] skillLevel = CardSkillLevelUtil.calculateSkillLevels(guardian.getLevel(), guardian.getStar().doubleValue());
                if (defender != null && !defender.isDead()) {
                    // 1. 计算所有中毒效果的总伤害（累加 POISON 类型的 value）
                    int totalPoisonDamage = 35 * skillLevel[0];
                    // 2. 计算毒抗相关（直接基于你现有 EffectInstance 计算，不新增 Guardian 方法）
                    // 飞弹增益：所有 POISON_RESIST 类型效果的 value 总和
                    int resistUp = calculateTotalVaule(guardian, EffectType.MISSILE_BOOST);
                    // 飞弹增益百分比：所有 POISON_RESIST 类型效果的 value 乘积
                    double resistUpPret = calculateTotalUpPretVaule(guardian, EffectType.MISSILE_BOOST_PRET);
                    // 飞弹降低：所有 POISON_RESIST_DOWN 类型效果的 value 总和
                    int resistDown = calculateTotalVaule(guardian, EffectType.MISSILE_DOWN);
                    // 飞弹降低百分比：所有 POISON_RESIST 类型效果的 value 乘积
                    double resistDownPret = calculateTotalDownPretVaule(guardian, EffectType.MISSILE_DOWN_PRET);


                    // 2. 计算毒抗相关（直接基于你现有 EffectInstance 计算，不新增 Guardian 方法）
                    // 弹抗增益：所有 POISON_RESIST 类型效果的 value 总和
                    int targetUp = calculateTotalVaule(defender, EffectType.MISSILE_RESIST_BOOST);
                    // 弹抗增益百分比：所有 POISON_RESIST 类型效果的 value 乘积
                    double targetUpPret = calculateTotalDownPretVaule(defender, EffectType.MISSILE_RESIST_BOOST_PRET);
                    // 弹抗降低：所有 POISON_RESIST_DOWN 类型效果的 value 总和
                    int targetDown = calculateTotalVaule(defender, EffectType.MISSILE_RESIST_DOWN);
                    // 弹抗降低百分比：所有 POISON_RESIST 类型效果的 value fieldA
                    double targetDownPret = calculateTotalUpPretVaule(defender, EffectType.MISSILE_RESIST_DOWN_PRET);
                    // 最终（仅基于 buff 计算，无新增方法）

                    int burnDamage = (int) (totalPoisonDamage * resistUpPret * resistDownPret * targetUpPret * targetDownPret
                            + (resistUp - resistDown + guardian.getFdAtk() - defender.getFdDef() - targetUp + targetDown));

                    if (burnDamage < 0) {
                        burnDamage = 0;
                    }

                    // 4. 扣除伤害
                    defender.setCurrentHp(defender.getCurrentHp() - burnDamage);
                    Map<String, TargetBattleData> deadUnits = new HashMap<>();

                    if (defender.getCurrentHp() <= 0) {
                        defender.setDead(true);
                        defender.setOnField(false);
                        TargetBattleData data = new TargetBattleData(defender.getMaxHp(), defender.getCurrentHp(), burnDamage, defender.isOnField());
                        deadUnits.put(defender.getId(), data);
                    }
                    addLog("苦痛箭",
                            guardian.getId(),
                            guardian.getMaxHp(),
                            guardian.getCurrentHp(),
                            0,
                            guardian.isOnField(),
                            defender.getId(),
                            defender.getMaxHp(),
                            defender.getCurrentHp(),
                            burnDamage,
                            defender.isOnField(),
                            EffectType.MISSILE_DAMAGE,
                            DamageType.TRUE,
                            "-" + burnDamage);
                    // 死亡日志
                    if (!deadUnits.isEmpty()) {
                        addMultiTargetLog("UNIT_DEATH",
                                null,
                                0,
                                0,
                                false,
                                deadUnits,
                                null,
                                null,
                                "死亡");
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
        if (campB.stream().anyMatch(g -> g.getName().equals("太岁灵君") && !g.isDead() && !g.isOnField() && !g.isSilence())) {
            Guardian guardian = campB.stream()
                    .filter(g -> g.getName().equals("太岁灵君") && !g.isDead() && !g.isOnField())
                    .findFirst().get();
            List<Guardian> aliveUnits = campA.stream()
                    .filter(g -> !g.isDead() && guardian.getPosition() == g.getPosition())
                    .collect(Collectors.toList());
            if (Xtool.isNotNull(aliveUnits)) {
                Guardian defender = aliveUnits.get(0);
                int[] skillLevel = CardSkillLevelUtil.calculateSkillLevels(guardian.getLevel(), guardian.getStar().doubleValue());
                if (defender != null && !defender.isDead()) {
                    // 1. 计算所有中毒效果的总伤害（累加 POISON 类型的 value）
                    int totalPoisonDamage = 35 * skillLevel[0];
                    // 2. 计算毒抗相关（直接基于你现有 EffectInstance 计算，不新增 Guardian 方法）
                    // 飞弹增益：所有 POISON_RESIST 类型效果的 value 总和
                    int resistUp = calculateTotalVaule(guardian, EffectType.MISSILE_BOOST);
                    // 飞弹增益百分比：所有 POISON_RESIST 类型效果的 value 乘积
                    double resistUpPret = calculateTotalUpPretVaule(guardian, EffectType.MISSILE_BOOST_PRET);
                    // 飞弹降低：所有 POISON_RESIST_DOWN 类型效果的 value 总和
                    int resistDown = calculateTotalVaule(guardian, EffectType.MISSILE_DOWN);
                    // 飞弹降低百分比：所有 POISON_RESIST 类型效果的 value 乘积
                    double resistDownPret = calculateTotalDownPretVaule(guardian, EffectType.MISSILE_DOWN_PRET);


                    // 2. 计算毒抗相关（直接基于你现有 EffectInstance 计算，不新增 Guardian 方法）
                    // 弹抗增益：所有 POISON_RESIST 类型效果的 value 总和
                    int targetUp = calculateTotalVaule(defender, EffectType.MISSILE_RESIST_BOOST);
                    // 弹抗增益百分比：所有 POISON_RESIST 类型效果的 value 乘积
                    double targetUpPret = calculateTotalDownPretVaule(defender, EffectType.MISSILE_RESIST_BOOST_PRET);
                    // 弹抗降低：所有 POISON_RESIST_DOWN 类型效果的 value 总和
                    int targetDown = calculateTotalVaule(defender, EffectType.MISSILE_RESIST_DOWN);
                    // 弹抗降低百分比：所有 POISON_RESIST 类型效果的 value fieldA
                    double targetDownPret = calculateTotalUpPretVaule(defender, EffectType.MISSILE_RESIST_DOWN_PRET);
                    // 最终（仅基于 buff 计算，无新增方法）

                    int burnDamage = (int) (totalPoisonDamage * resistUpPret * resistDownPret * targetUpPret * targetDownPret
                            + (resistUp - resistDown + guardian.getFdAtk() - defender.getFdDef() - targetUp + targetDown));

                    if (burnDamage < 0) {
                        burnDamage = 0;
                    }

                    // 4. 扣除伤害
                    defender.setCurrentHp(defender.getCurrentHp() - burnDamage);
                    Map<String, TargetBattleData> deadUnits = new HashMap<>();

                    if (defender.getCurrentHp() <= 0) {
                        defender.setDead(true);
                        defender.setOnField(false);
                        TargetBattleData data = new TargetBattleData(defender.getMaxHp(), defender.getCurrentHp(), burnDamage, defender.isOnField());
                        deadUnits.put(defender.getId(), data);
                    }
                    addLog("苦痛箭",
                            guardian.getId(),
                            guardian.getMaxHp(),
                            guardian.getCurrentHp(),
                            0,
                            guardian.isOnField(),
                            defender.getId(),
                            defender.getMaxHp(),
                            defender.getCurrentHp(),
                            burnDamage,
                            defender.isOnField(),
                            EffectType.MISSILE_DAMAGE,
                            DamageType.TRUE,
                            "-" + burnDamage);
                    // 死亡日志
                    if (!deadUnits.isEmpty()) {
                        addMultiTargetLog("UNIT_DEATH",
                                null,
                                0,
                                0,
                                false,
                                deadUnits,
                                null,
                                null,
                                "死亡");
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
        if (campA.stream().anyMatch(g -> g.getName().equals("黄梅老佛") && !g.isDead() && !g.isOnField() && !g.isSilence())) {
            Guardian guardian = campA.stream()
                    .filter(g -> g.getName().equals("黄梅老佛") && !g.isDead() && !g.isOnField())
                    .findFirst().get();
            List<Guardian> aliveUnits = campB.stream()
                    .filter(g -> !g.isDead() && 4 == g.getPosition())
                    .collect(Collectors.toList());
            if (Xtool.isNotNull(aliveUnits)) {
                Guardian defender = aliveUnits.get(0);
                int[] skillLevel = CardSkillLevelUtil.calculateSkillLevels(guardian.getLevel(), guardian.getStar().doubleValue());
                if (defender != null && !defender.isDead()) {
                    //TODO 真实伤害无法防御
                    // 1. 计算所有中毒效果的总伤害（累加 POISON 类型的 value）
                    int totalPoisonDamage = 410 * skillLevel[0];

                    // 4. 扣除伤害
                    defender.setCurrentHp(defender.getCurrentHp() - totalPoisonDamage);
                    Map<String, TargetBattleData> deadUnits = new HashMap<>();

                    if (defender.getCurrentHp() <= 0) {
                        defender.setDead(true);
                        defender.setOnField(false);
                        TargetBattleData data = new TargetBattleData(defender.getMaxHp(), defender.getCurrentHp(), totalPoisonDamage, defender.isOnField());
                        deadUnits.put(defender.getId(), data);
                    }
                    addLog("群鸦箭",
                            guardian.getId(),
                            guardian.getMaxHp(),
                            guardian.getCurrentHp(),
                            0,
                            guardian.isOnField(),
                            defender.getId(),
                            defender.getMaxHp(),
                            defender.getCurrentHp(),
                            totalPoisonDamage,
                            defender.isOnField(),
                            EffectType.MISSILE_DAMAGE,
                            DamageType.TRUE,
                            "-" + totalPoisonDamage);
                    // 死亡日志
                    if (!deadUnits.isEmpty()) {
                        addMultiTargetLog("UNIT_DEATH",
                                null,
                                0,
                                0,
                                false,
                                deadUnits,
                                null,
                                null,
                                "死亡");
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
        if (campB.stream().anyMatch(g -> g.getName().equals("黄梅老佛") && !g.isDead() && !g.isOnField() && !g.isSilence())) {
            Guardian guardian = campB.stream()
                    .filter(g -> g.getName().equals("黄梅老佛") && !g.isDead() && !g.isOnField())
                    .findFirst().get();
            List<Guardian> aliveUnits = campA.stream()
                    .filter(g -> !g.isDead() && 4 == g.getPosition())
                    .collect(Collectors.toList());
            if (Xtool.isNotNull(aliveUnits)) {
                Guardian defender = aliveUnits.get(0);
                int[] skillLevel = CardSkillLevelUtil.calculateSkillLevels(guardian.getLevel(), guardian.getStar().doubleValue());
                if (defender != null && !defender.isDead()) {
                    //TODO 真实伤害无法防御
                    // 1. 计算所有中毒效果的总伤害（累加 POISON 类型的 value）
                    int totalPoisonDamage = 410 * skillLevel[0];


                    // 4. 扣除伤害
                    defender.setCurrentHp(defender.getCurrentHp() - totalPoisonDamage);
                    Map<String, TargetBattleData> deadUnits = new HashMap<>();

                    if (defender.getCurrentHp() <= 0) {
                        defender.setDead(true);
                        defender.setOnField(false);
                        TargetBattleData data = new TargetBattleData(defender.getMaxHp(), defender.getCurrentHp(), totalPoisonDamage, defender.isOnField());
                        deadUnits.put(defender.getId(), data);

                    }
                    addLog("群鸦箭",
                            guardian.getId(),
                            guardian.getMaxHp(),
                            guardian.getCurrentHp(),
                            0,
                            guardian.isOnField(),
                            defender.getId(),
                            defender.getMaxHp(),
                            defender.getCurrentHp(),
                            totalPoisonDamage,
                            defender.isOnField(),
                            EffectType.MISSILE_DAMAGE,
                            DamageType.TRUE,
                            "-" + totalPoisonDamage);
                    // 死亡日志
                    if (!deadUnits.isEmpty()) {
                        addMultiTargetLog("UNIT_DEATH",
                                null,
                                0,
                                0,
                                false,
                                deadUnits,
                                null,
                                null,
                                "死亡");
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
        if (campA.stream().anyMatch(g -> g.getName().equals("六耳猕猴") && !g.isDead() && !g.isOnField() && !g.isSilence())) {
            Guardian guardian = campA.stream()
                    .filter(g -> g.getName().equals("六耳猕猴") && !g.isDead() && !g.isOnField())
                    .findFirst().get();
            List<Guardian> aliveUnits = campB.stream()
                    .filter(g -> !g.isDead() && 3 == g.getPosition())
                    .collect(Collectors.toList());
            if (Xtool.isNotNull(aliveUnits)) {
                Guardian defender = aliveUnits.get(0);
                int[] skillLevel = CardSkillLevelUtil.calculateSkillLevels(guardian.getLevel(), guardian.getStar().doubleValue());
                if (defender != null && !defender.isDead()) {
                    //TODO 真实伤害无增益也无法防御
                    // 1. 计算所有中毒效果的总伤害（累加 POISON 类型的 value）
                    int totalPoisonDamage = 41 * skillLevel[0];


                    // 4. 扣除伤害
                    defender.setCurrentHp(defender.getCurrentHp() - totalPoisonDamage);
                    Map<String, TargetBattleData> deadUnits = new HashMap<>();

                    if (defender.getCurrentHp() <= 0) {
                        defender.setDead(true);
                        defender.setOnField(false);
                        TargetBattleData data = new TargetBattleData(defender.getMaxHp(), defender.getCurrentHp(), totalPoisonDamage, defender.isOnField());
                        deadUnits.put(defender.getId(), data);

                    }
                    addLog("幻影箭",
                            guardian.getId(),
                            guardian.getMaxHp(),
                            guardian.getCurrentHp(),
                            0,
                            guardian.isOnField(),
                            defender.getId(),
                            defender.getMaxHp(),
                            defender.getCurrentHp(),
                            totalPoisonDamage,
                            defender.isOnField(),
                            EffectType.MISSILE_DAMAGE,
                            DamageType.TRUE,
                            "-" + totalPoisonDamage);
                    // 死亡日志
                    if (!deadUnits.isEmpty()) {
                        addMultiTargetLog("UNIT_DEATH",
                                null,
                                0,
                                0,
                                false,
                                deadUnits,
                                null,
                                null,
                                "死亡");
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
        if (campB.stream().anyMatch(g -> g.getName().equals("六耳猕猴") && !g.isDead() && !g.isOnField() && !g.isSilence())) {
            Guardian guardian = campB.stream()
                    .filter(g -> g.getName().equals("六耳猕猴") && !g.isDead() && !g.isOnField())
                    .findFirst().get();
            List<Guardian> aliveUnits = campA.stream()
                    .filter(g -> !g.isDead() && 3 == g.getPosition())
                    .collect(Collectors.toList());
            if (Xtool.isNotNull(aliveUnits)) {
                Guardian defender = aliveUnits.get(0);
                int[] skillLevel = CardSkillLevelUtil.calculateSkillLevels(guardian.getLevel(), guardian.getStar().doubleValue());
                if (defender != null && !defender.isDead()) {
                    //TODO 真实伤害无增益也无法防御
                    // 1. 计算所有中毒效果的总伤害（累加 POISON 类型的 value）
                    int totalPoisonDamage = 41 * skillLevel[0];

                    // 4. 扣除伤害
                    defender.setCurrentHp(defender.getCurrentHp() - totalPoisonDamage);
                    Map<String, TargetBattleData> deadUnits = new HashMap<>();

                    if (defender.getCurrentHp() <= 0) {
                        defender.setDead(true);
                        defender.setOnField(false);
                        TargetBattleData data = new TargetBattleData(defender.getMaxHp(), defender.getCurrentHp(), totalPoisonDamage, defender.isOnField());
                        deadUnits.put(defender.getId(), data);

                    }
                    addLog("幻影箭",
                            guardian.getId(),
                            guardian.getMaxHp(),
                            guardian.getCurrentHp(),
                            0,
                            guardian.isOnField(),
                            defender.getId(),
                            defender.getMaxHp(),
                            defender.getCurrentHp(),
                            totalPoisonDamage,
                            defender.isOnField(),
                            EffectType.MISSILE_DAMAGE,
                            DamageType.MISSILE,
                            "-" + totalPoisonDamage);
                    // 死亡日志
                    if (!deadUnits.isEmpty()) {
                        addMultiTargetLog("UNIT_DEATH",
                                null,
                                0,
                                0,
                                false,
                                deadUnits,
                                null,
                                null,
                                "死亡");
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
        if (campA.stream().anyMatch(g -> g.getName().equals("九天玄女") && !g.isDead() && !g.isSilence())) {
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
                Guardian defender = aliveUnits.get(0);
                int[] skillLevel = CardSkillLevelUtil.calculateSkillLevels(guardian.getLevel(), guardian.getStar().doubleValue());
                if (defender != null && !defender.isDead()) {
                    // 1. 计算所有中毒效果的总伤害（累加 POISON 类型的 value）
                    int totalPoisonDamage = 42 * skillLevel[0];
                    // 2. 计算毒抗相关（直接基于你现有 EffectInstance 计算，不新增 Guardian 方法）
                    // 飞弹增益：所有 POISON_RESIST 类型效果的 value 总和
                    int resistUp = calculateTotalVaule(guardian, EffectType.MISSILE_BOOST);
                    // 飞弹增益百分比：所有 POISON_RESIST 类型效果的 value 乘积
                    double resistUpPret = calculateTotalUpPretVaule(guardian, EffectType.MISSILE_BOOST_PRET);
                    // 飞弹降低：所有 POISON_RESIST_DOWN 类型效果的 value 总和
                    int resistDown = calculateTotalVaule(guardian, EffectType.MISSILE_DOWN);
                    // 飞弹降低百分比：所有 POISON_RESIST 类型效果的 value 乘积
                    double resistDownPret = calculateTotalDownPretVaule(guardian, EffectType.MISSILE_DOWN_PRET);


                    // 2. 计算毒抗相关（直接基于你现有 EffectInstance 计算，不新增 Guardian 方法）
                    // 弹抗增益：所有 POISON_RESIST 类型效果的 value 总和
                    int targetUp = calculateTotalVaule(defender, EffectType.MISSILE_RESIST_BOOST);
                    // 弹抗增益百分比：所有 POISON_RESIST 类型效果的 value 乘积
                    double targetUpPret = calculateTotalDownPretVaule(defender, EffectType.MISSILE_RESIST_BOOST_PRET);
                    // 弹抗降低：所有 POISON_RESIST_DOWN 类型效果的 value 总和
                    int targetDown = calculateTotalVaule(defender, EffectType.MISSILE_RESIST_DOWN);
                    // 弹抗降低百分比：所有 POISON_RESIST 类型效果的 value fieldA
                    double targetDownPret = calculateTotalUpPretVaule(defender, EffectType.MISSILE_RESIST_DOWN_PRET);
                    // 最终（仅基于 buff 计算，无新增方法）

                    int burnDamage = (int) (totalPoisonDamage * resistUpPret * resistDownPret * targetUpPret * targetDownPret
                            + (resistUp - resistDown + guardian.getFdAtk() - defender.getFdDef() - targetUp + targetDown));

                    if (burnDamage < 0) {
                        burnDamage = 0;
                    }

                    // 4. 扣除伤害
                    defender.setCurrentHp(defender.getCurrentHp() - burnDamage);
                    Map<String, TargetBattleData> deadUnits = new HashMap<>();

                    if (defender.getCurrentHp() <= 0) {
                        defender.setDead(true);
                        defender.setOnField(false);
                        TargetBattleData data = new TargetBattleData(defender.getMaxHp(), defender.getCurrentHp(), burnDamage, defender.isOnField());
                        deadUnits.put(defender.getId(), data);

                    }
                    addLog("觅心神箭",
                            guardian.getId(),
                            guardian.getMaxHp(),
                            guardian.getCurrentHp(),
                            0,
                            guardian.isOnField(),
                            defender.getId(),
                            defender.getMaxHp(),
                            defender.getCurrentHp(),
                            burnDamage,
                            defender.isOnField(),
                            EffectType.MISSILE_DAMAGE,
                            DamageType.MISSILE,
                            "-" + burnDamage);
                    // 死亡日志
                    if (!deadUnits.isEmpty()) {
                        addMultiTargetLog("UNIT_DEATH",
                                null,
                                0,
                                0,
                                false,
                                deadUnits,
                                null,
                                null,
                                "死亡");
                        //触发死亡技能
                        triggerOnDeathSkills(defender);

                    } else {
                        //触发受击技能
                        triggerOnAttackedSkills(defender, EffectType.MISSILE_DAMAGE);
                    }
                }
            }

        }


//        觅心神箭Lv1每回合对生命值最低的敌方造成42点飞弹伤害；穿云剑Lv1普通攻击后，对场上敌方身后一个单位造成52点真实伤害；仙将神临Lv1与杨戬在同一队伍时，增加自身781点生命上限，130点攻击，139点速度
        if (campB.stream().anyMatch(g -> g.getName().equals("九天玄女") && !g.isDead() && !g.isSilence())) {
            Guardian guardian = campB.stream()
                    .filter(g -> g.getName().equals("九天玄女") && !g.isDead())
                    .findFirst().get();
            List<Guardian> aliveUnits = campA.stream()
                    .filter(g -> !g.isDead())
                    // 按血量升序排序
                    .sorted(Comparator.comparingInt(Guardian::getCurrentHp))
                    .collect(Collectors.toList());
            if (Xtool.isNotNull(aliveUnits)) {
                Guardian defender = aliveUnits.get(0);
                int[] skillLevel = CardSkillLevelUtil.calculateSkillLevels(guardian.getLevel(), guardian.getStar().doubleValue());
                if (defender != null && !defender.isDead()) {
                    // 1. 计算所有中毒效果的总伤害（累加 POISON 类型的 value）
                    int totalPoisonDamage = 42 * skillLevel[0];
                    // 2. 计算毒抗相关（直接基于你现有 EffectInstance 计算，不新增 Guardian 方法）
                    // 飞弹增益：所有 POISON_RESIST 类型效果的 value 总和
                    int resistUp = calculateTotalVaule(guardian, EffectType.MISSILE_BOOST);
                    // 飞弹增益百分比：所有 POISON_RESIST 类型效果的 value 乘积
                    double resistUpPret = calculateTotalUpPretVaule(guardian, EffectType.MISSILE_BOOST_PRET);
                    // 飞弹降低：所有 POISON_RESIST_DOWN 类型效果的 value 总和
                    int resistDown = calculateTotalVaule(guardian, EffectType.MISSILE_DOWN);
                    // 飞弹降低百分比：所有 POISON_RESIST 类型效果的 value 乘积
                    double resistDownPret = calculateTotalDownPretVaule(guardian, EffectType.MISSILE_DOWN_PRET);


                    // 2. 计算毒抗相关（直接基于你现有 EffectInstance 计算，不新增 Guardian 方法）
                    // 弹抗增益：所有 POISON_RESIST 类型效果的 value 总和
                    int targetUp = calculateTotalVaule(defender, EffectType.MISSILE_RESIST_BOOST);
                    // 弹抗增益百分比：所有 POISON_RESIST 类型效果的 value 乘积
                    double targetUpPret = calculateTotalDownPretVaule(defender, EffectType.MISSILE_RESIST_BOOST_PRET);
                    // 弹抗降低：所有 POISON_RESIST_DOWN 类型效果的 value 总和
                    int targetDown = calculateTotalVaule(defender, EffectType.MISSILE_RESIST_DOWN);
                    // 弹抗降低百分比：所有 POISON_RESIST 类型效果的 value fieldA
                    double targetDownPret = calculateTotalUpPretVaule(defender, EffectType.MISSILE_RESIST_DOWN_PRET);
                    // 最终（仅基于 buff 计算，无新增方法）

                    int burnDamage = (int) (totalPoisonDamage * resistUpPret * resistDownPret * targetUpPret * targetDownPret
                            + (resistUp - resistDown + guardian.getFdAtk() - defender.getFdDef() - targetUp + targetDown));

                    if (burnDamage < 0) {
                        burnDamage = 0;
                    }

                    // 4. 扣除伤害
                    defender.setCurrentHp(defender.getCurrentHp() - burnDamage);
                    Map<String, TargetBattleData> deadUnits = new HashMap<>();

                    if (defender.getCurrentHp() <= 0) {
                        defender.setDead(true);
                        defender.setOnField(false);
                        TargetBattleData data = new TargetBattleData(defender.getMaxHp(), defender.getCurrentHp(), burnDamage, defender.isOnField());
                        deadUnits.put(defender.getId(), data);

                    }
                    addLog("觅心神箭",
                            guardian.getId(),
                            guardian.getMaxHp(),
                            guardian.getCurrentHp(),
                            0,
                            guardian.isOnField(),
                            defender.getId(),
                            defender.getMaxHp(),
                            defender.getCurrentHp(),
                            burnDamage,
                            defender.isOnField(),
                            EffectType.MISSILE_DAMAGE,
                            DamageType.MISSILE,
                            "-" + burnDamage);
                    // 死亡日志
                    if (!deadUnits.isEmpty()) {
                        addMultiTargetLog("UNIT_DEATH",
                                null,
                                0,
                                0,
                                false,
                                deadUnits,
                                null,
                                null,
                                "死亡");
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
        if (campA.stream().anyMatch(g -> g.getName().equals("白天君") && !g.isDead() && g.isOnField())) {
            Guardian guardian = campA.stream()
                    .filter(g -> g.getName().equals("白天君") && !g.isDead())
                    .findFirst().get();
            int xuli = guardian.getBuffStacks();
            guardian.setBuffStacks(guardian.getBuffStacks() + 1);
            // 查找血量最低的存活守卫
            if (xuli > 2) {
                guardian.setBuffStacks(0);
                int[] skillLevel = CardSkillLevelUtil.calculateSkillLevels(guardian.getLevel(), guardian.getStar().doubleValue());
                //烈焰阵Lv1登场时，令敌方全体收到火焰伤害420点；
                Map<String, TargetBattleData> deadUnits = new HashMap<>();
                List<Guardian> deadGuardians = new ArrayList<>();

                Map<String, TargetBattleData> targetStatus = new HashMap<>();
                List<Guardian> enemies = campB.stream().filter(g -> !g.isDead()).collect(Collectors.toList());
                if (Xtool.isNotNull(enemies)) {
                    enemies.forEach(g -> {
                        // 1. 计算所有中毒效果的总伤害（累加 POISON 类型的 value）
                        int totalPoisonDamage = 355 * skillLevel[0];
                        // 2. 计算毒抗相关（直接基于你现有 EffectInstance 计算，不新增 Guardian 方法）
                        // 火伤增益：所有 POISON_RESIST 类型效果的 value 总和
                        int resistUp = calculateTotalVaule(guardian, EffectType.FIRE_BOOST);
                        // 火伤增益百分比：所有 POISON_RESIST 类型效果的 value 乘积
                        double resistUpPret = calculateTotalUpPretVaule(guardian, EffectType.FIRE_BOOST_PRET);
                        // 火伤降低：所有 POISON_RESIST_DOWN 类型效果的 value 总和
                        int resistDown = calculateTotalVaule(guardian, EffectType.FIRE_DOWN);
                        // 火伤降低百分比：所有 POISON_RESIST 类型效果的 value 乘积
                        double resistDownPret = calculateTotalDownPretVaule(guardian, EffectType.FIRE_DOWN_PRET);


                        // 2. 计算毒抗相关（直接基于你现有 EffectInstance 计算，不新增 Guardian 方法）
                        // 火抗增益：所有 POISON_RESIST 类型效果的 value 总和
                        int targetUp = calculateTotalVaule(g, EffectType.FIRE_RESIST_BOOST);
                        // 火抗增益百分比：所有 POISON_RESIST 类型效果的 value 乘积
                        double targetUpPret = calculateTotalDownPretVaule(g, EffectType.FIRE_RESIST_BOOST_PRET);
                        // 火抗降低：所有 POISON_RESIST_DOWN 类型效果的 value 总和
                        int targetDown = calculateTotalVaule(g, EffectType.FIRE_RESIST_DOWN);
                        // 火抗降低百分比：所有 POISON_RESIST 类型效果的 value 乘积
                        double targetDownPret = calculateTotalUpPretVaule(g, EffectType.FIRE_RESIST_DOWN_PRET);
                        // 最终（仅基于 buff 计算，无新增方法）

                        int burnDamage = (int) (totalPoisonDamage * resistUpPret * resistDownPret * targetUpPret * targetDownPret
                                + (resistUp - resistDown + guardian.getHyAtk() - g.getHyDef() - targetUp + targetDown));

                        if (burnDamage < 0) {
                            burnDamage = 0;
                        }

                        // 4. 扣除伤害
                        g.setCurrentHp(g.getCurrentHp() - burnDamage);
                        TargetBattleData data = new TargetBattleData(g.getMaxHp(), g.getCurrentHp(), burnDamage, g.isOnField());
                        targetStatus.put(g.getId(), data);
                        if (g.isDead()) {
                            deadUnits.put(g.getId(), data);
                            deadGuardians.add(g);
                        }
                    });

                    // 单条日志记录多目标
                    addMultiTargetLog("三火齐飞",
                            guardian.getId(),
                            guardian.getMaxHp(),
                            guardian.getCurrentHp(),
                            guardian.isOnField(),
                            targetStatus,
                            EffectType.FIRE_DAMAGE,
                            DamageType.FIRE,
                            "敌方全体收到火焰伤害");
                    // 死亡日志
                    if (!deadUnits.isEmpty()) {
                        addMultiTargetLog("UNIT_DEATH",
                                null,
                                0,
                                0,
                                false,
                                deadUnits,
                                null,
                                null,
                                "死亡");
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
            } else {
                String[] str = {"白天君蓄力·一", "白天君蓄力·二", "白天君蓄力·三"};
                addLog(str[xuli],
                        guardian.getId(),
                        guardian.getMaxHp(),
                        guardian.getCurrentHp(),
                        0,
                        guardian.isOnField(),
                        guardian.getId(),
                        guardian.getMaxHp(),
                        guardian.getCurrentHp(),
                        0,
                        guardian.isOnField(),
                        EffectType.CHARGE_UP,
                        DamageType.BUFF,
                        str[xuli]);
            }

        }

        if (campB.stream().anyMatch(g -> g.getName().equals("白天君") && !g.isDead() && g.isOnField())) {
            Guardian guardian = campB.stream()
                    .filter(g -> g.getName().equals("白天君") && !g.isDead())
                    .findFirst().get();
            int xuli = guardian.getBuffStacks();
            guardian.setBuffStacks(guardian.getBuffStacks() + 1);
            // 查找血量最低的存活守卫
            if (xuli > 2) {
                guardian.setBuffStacks(0);
                int[] skillLevel = CardSkillLevelUtil.calculateSkillLevels(guardian.getLevel(), guardian.getStar().doubleValue());
                //烈焰阵Lv1登场时，令敌方全体收到火焰伤害420点；
                Map<String, TargetBattleData> deadUnits = new HashMap<>();
                List<Guardian> deadGuardians = new ArrayList<>();

                Map<String, TargetBattleData> targetStatus = new HashMap<>();
                List<Guardian> enemies = campA.stream().filter(g -> !g.isDead()).collect(Collectors.toList());
                if (Xtool.isNotNull(enemies)) {
                    enemies.forEach(g -> {
                        // 1. 计算所有中毒效果的总伤害（累加 POISON 类型的 value）
                        int totalPoisonDamage = 355 * skillLevel[0];
                        // 2. 计算毒抗相关（直接基于你现有 EffectInstance 计算，不新增 Guardian 方法）
                        // 火伤增益：所有 POISON_RESIST 类型效果的 value 总和
                        int resistUp = calculateTotalVaule(guardian, EffectType.FIRE_BOOST);
                        // 火伤增益百分比：所有 POISON_RESIST 类型效果的 value 乘积
                        double resistUpPret = calculateTotalUpPretVaule(guardian, EffectType.FIRE_BOOST_PRET);
                        // 火伤降低：所有 POISON_RESIST_DOWN 类型效果的 value 总和
                        int resistDown = calculateTotalVaule(guardian, EffectType.FIRE_DOWN);
                        // 火伤降低百分比：所有 POISON_RESIST 类型效果的 value 乘积
                        double resistDownPret = calculateTotalDownPretVaule(guardian, EffectType.FIRE_DOWN_PRET);


                        // 2. 计算毒抗相关（直接基于你现有 EffectInstance 计算，不新增 Guardian 方法）
                        // 火抗增益：所有 POISON_RESIST 类型效果的 value 总和
                        int targetUp = calculateTotalVaule(g, EffectType.FIRE_RESIST_BOOST);
                        // 火抗增益百分比：所有 POISON_RESIST 类型效果的 value 乘积
                        double targetUpPret = calculateTotalDownPretVaule(g, EffectType.FIRE_RESIST_BOOST_PRET);
                        // 火抗降低：所有 POISON_RESIST_DOWN 类型效果的 value 总和
                        int targetDown = calculateTotalVaule(g, EffectType.FIRE_RESIST_DOWN);
                        // 火抗降低百分比：所有 POISON_RESIST 类型效果的 value 乘积
                        double targetDownPret = calculateTotalUpPretVaule(g, EffectType.FIRE_RESIST_DOWN_PRET);
                        // 最终（仅基于 buff 计算，无新增方法）

                        int burnDamage = (int) (totalPoisonDamage * resistUpPret * resistDownPret * targetUpPret * targetDownPret
                                + (resistUp - resistDown + guardian.getHyAtk() - g.getHyDef() - targetUp + targetDown));

                        if (burnDamage < 0) {
                            burnDamage = 0;
                        }

                        // 4. 扣除伤害
                        g.setCurrentHp(g.getCurrentHp() - burnDamage);
                        TargetBattleData data = new TargetBattleData(g.getMaxHp(), g.getCurrentHp(), burnDamage, g.isOnField());
                        targetStatus.put(g.getId(), data);
                        if (g.isDead()) {
                            deadUnits.put(g.getId(), data);
                            deadGuardians.add(g);
                        }
                    });

                    // 单条日志记录多目标
                    addMultiTargetLog("三火齐飞",
                            guardian.getId(),
                            guardian.getMaxHp(),
                            guardian.getCurrentHp(),
                            guardian.isOnField(),
                            targetStatus,
                            EffectType.FIRE_DAMAGE,
                            DamageType.FIRE,
                            "敌方全体收到火焰伤害");
                    // 死亡日志
                    if (!deadUnits.isEmpty()) {
                        addMultiTargetLog("UNIT_DEATH",
                                null,
                                0,
                                0,
                                false,
                                deadUnits,
                                null,
                                null,
                                "死亡");
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
            } else {
                String[] str = {"白天君蓄力·一", "白天君蓄力·二", "白天君蓄力·三"};
                addLog(str[xuli],
                        guardian.getId(),
                        guardian.getMaxHp(),
                        guardian.getCurrentHp(),
                        0,
                        guardian.isOnField(),
                        guardian.getId(),
                        guardian.getMaxHp(),
                        guardian.getCurrentHp(),
                        0,
                        guardian.isOnField(),
                        EffectType.CHARGE_UP,
                        DamageType.BUFF,
                        str[xuli]);
            }

        }
        // 妲己场下技能
        // A队妲己
        if (campA.stream().anyMatch(g -> g.getName().equals("妲己") && !g.isDead() && !g.isOnField() && !g.isSilence())) {
            Guardian daji = campA.stream()
                    .filter(g -> g.getName().equals("妲己") && !g.isDead() && !g.isOnField())
                    .findFirst().get();
            int[] skillLevel = CardSkillLevelUtil.calculateSkillLevels(daji.getLevel(), daji.getStar().doubleValue());


            // 妖狐蔽天：3%几率眩晕当前敌人
            if (ProbabilityBooleanUtils.randomByProbability(0.35) && fieldB != null && !fieldB.isDead()) {
                fieldB.addEffect(EffectType.STUN, 0, 2, fieldB.getId());
                addLog("妖狐蔽天",
                        daji.getId(),
                        daji.getMaxHp(),
                        daji.getCurrentHp(),
                        0,
                        daji.isOnField(),
                        fieldB.getId(),
                        fieldB.getMaxHp(),
                        fieldB.getCurrentHp(),
                        0,
                        fieldB.isOnField(),
                        EffectType.STUN,
                        DamageType.BUFF,
                        "眩晕2回合");
            }

            // 谄媚噬魂：随机敌方中毒
            List<Guardian> enemies = campB.stream()
                    .filter(g -> !g.isDead())
                    .collect(Collectors.toList());

            if (!enemies.isEmpty() && skillLevel[1] > 0) {
                Guardian randomEnemy = enemies.get(random.nextInt(enemies.size()));
                int totalPoisonDamage = 7 * skillLevel[0];
                // 1. 计算所有中毒效果的总伤害（累加 POISON 类型的 value）
                // 2. 计算毒抗相关（直接基于你现有 EffectInstance 计算，不新增 Guardian 方法）
                // 中毒增益：所有 POISON_RESIST 类型效果的 value 总和
                int resistUp = calculateTotalVaule(daji, EffectType.POISON_BOOST);
                // 中毒增益百分比：所有 POISON_RESIST 类型效果的 value 乘积
                double resistUpPret = calculateTotalUpPretVaule(daji, EffectType.POISON_BOOST_PRET);
                // 中毒降低：所有 POISON_RESIST_DOWN 类型效果的 value 总和
                int resistDown = calculateTotalVaule(daji, EffectType.POISON_DOWN);
                // 中毒降低百分比：所有 POISON_RESIST 类型效果的 value 乘积
                double resistDownPret = calculateTotalDownPretVaule(daji, EffectType.POISON_DOWN_PRET);

                int poisonValue = (int) (totalPoisonDamage * resistUpPret * resistDownPret + (resistUp + daji.getDsAtk() - resistDown));
                randomEnemy.addEffect(EffectType.POISON, poisonValue, 99, daji.getId());
                addLog("谄媚噬魂",
                        daji.getId(),
                        daji.getMaxHp(),
                        daji.getCurrentHp(),
                        poisonValue,
                        daji.isOnField(),
                        randomEnemy.getId(),
                        randomEnemy.getMaxHp(),
                        randomEnemy.getCurrentHp(),
                        poisonValue,
                        randomEnemy.isOnField(),
                        EffectType.POISON,
                        DamageType.POISON,
                        "眩晕2回合");
            }
        }

        // B队妲己
        if (campB.stream().anyMatch(g -> g.getName().equals("妲己") && !g.isDead() && !g.isOnField() && !g.isSilence())) {
            Guardian daji = campB.stream()
                    .filter(g -> g.getName().equals("妲己") && !g.isDead() && !g.isOnField())
                    .findFirst().get();
            int[] skillLevel = CardSkillLevelUtil.calculateSkillLevels(daji.getLevel(), daji.getStar().doubleValue());


            // 妖狐蔽天：3%几率眩晕当前敌人
            if (ProbabilityBooleanUtils.randomByProbability(0.35) && fieldA != null && !fieldA.isDead()) {
                fieldA.addEffect(EffectType.STUN, 0, 2, daji.getId());
                addLog("妖狐蔽天",
                        daji.getId(),
                        daji.getMaxHp(),
                        daji.getCurrentHp(),
                        0,
                        daji.isOnField(),
                        fieldA.getId(),
                        fieldA.getMaxHp(),
                        fieldA.getCurrentHp(),
                        0,
                        fieldA.isOnField(),
                        EffectType.STUN,
                        DamageType.BUFF,
                        "眩晕2回合");
            }

            // 谄媚噬魂：随机敌方中毒
            List<Guardian> enemies = campA.stream()
                    .filter(g -> !g.isDead())
                    .collect(Collectors.toList());

            if (!enemies.isEmpty() && skillLevel[1] > 0) {
                Guardian randomEnemy = enemies.get(random.nextInt(enemies.size()));
                int totalPoisonDamage = 7 * skillLevel[0];
                // 1. 计算所有中毒效果的总伤害（累加 POISON 类型的 value）
                // 2. 计算毒抗相关（直接基于你现有 EffectInstance 计算，不新增 Guardian 方法）
                // 中毒增益：所有 POISON_RESIST 类型效果的 value 总和
                int resistUp = calculateTotalVaule(daji, EffectType.POISON_BOOST);
                // 中毒增益百分比：所有 POISON_RESIST 类型效果的 value 乘积
                double resistUpPret = calculateTotalUpPretVaule(daji, EffectType.POISON_BOOST_PRET);
                // 中毒降低：所有 POISON_RESIST_DOWN 类型效果的 value 总和
                int resistDown = calculateTotalVaule(daji, EffectType.POISON_DOWN);
                // 中毒降低百分比：所有 POISON_RESIST 类型效果的 value 乘积
                double resistDownPret = calculateTotalDownPretVaule(daji, EffectType.POISON_DOWN_PRET);

                int poisonValue = (int) (totalPoisonDamage * resistUpPret * resistDownPret + (resistUp + daji.getDsAtk() - resistDown));
                randomEnemy.addEffect(EffectType.POISON, poisonValue, 99, daji.getId());
                addLog("谄媚噬魂",
                        daji.getId(),
                        daji.getMaxHp(),
                        daji.getCurrentHp(),
                        poisonValue,
                        daji.isOnField(),
                        randomEnemy.getId(),
                        randomEnemy.getMaxHp(),
                        randomEnemy.getCurrentHp(),
                        poisonValue,
                        randomEnemy.isOnField(),
                        EffectType.POISON,
                        DamageType.POISON,
                        "中毒+" + poisonValue);
            }
        }


        // A队妲己
        if (campA.stream().anyMatch(g -> g.getName().equals("白晶晶") && !g.isDead() && !g.isOnField() && !g.isSilence())) {
            Guardian daji = campA.stream()
                    .filter(g -> g.getName().equals("白晶晶") && !g.isDead() && !g.isOnField())
                    .findFirst().get();
            int[] skillLevel = CardSkillLevelUtil.calculateSkillLevels(daji.getLevel(), daji.getStar().doubleValue());


            // 谄媚噬魂：随机敌方中毒
            List<Guardian> enemies = campB.stream()
                    .filter(g -> !g.isDead())
                    .collect(Collectors.toList());

            if (!enemies.isEmpty()) {
                Guardian randomEnemy = enemies.get(random.nextInt(enemies.size()));
                int totalPoisonDamage = 7 * skillLevel[0];
                // 1. 计算所有中毒效果的总伤害（累加 POISON 类型的 value）
                // 2. 计算毒抗相关（直接基于你现有 EffectInstance 计算，不新增 Guardian 方法）
                // 中毒增益：所有 POISON_RESIST 类型效果的 value 总和
                int resistUp = calculateTotalVaule(daji, EffectType.POISON_BOOST);
                // 中毒增益百分比：所有 POISON_RESIST 类型效果的 value 乘积
                double resistUpPret = calculateTotalUpPretVaule(daji, EffectType.POISON_BOOST_PRET);
                // 中毒降低：所有 POISON_RESIST_DOWN 类型效果的 value 总和
                int resistDown = calculateTotalVaule(daji, EffectType.POISON_DOWN);
                // 中毒降低百分比：所有 POISON_RESIST 类型效果的 value 乘积
                double resistDownPret = calculateTotalDownPretVaule(daji, EffectType.POISON_DOWN_PRET);

                int poisonValue = (int) (totalPoisonDamage * resistUpPret * resistDownPret + (resistUp + daji.getDsAtk() - resistDown));
                randomEnemy.addEffect(EffectType.POISON, poisonValue, 99, daji.getId());
                addLog("谄媚噬魂",
                        daji.getId(),
                        daji.getMaxHp(),
                        daji.getCurrentHp(),
                        poisonValue,
                        daji.isOnField(),
                        randomEnemy.getId(),
                        randomEnemy.getMaxHp(),
                        randomEnemy.getCurrentHp(),
                        poisonValue,
                        randomEnemy.isOnField(),
                        EffectType.POISON,
                        DamageType.POISON,
                        "中毒+" + poisonValue);
            }
        }

        // B队妲己
        if (campB.stream().anyMatch(g -> g.getName().equals("白晶晶") && !g.isDead() && !g.isOnField() && !g.isSilence())) {
            Guardian daji = campB.stream()
                    .filter(g -> g.getName().equals("白晶晶") && !g.isDead() && !g.isOnField())
                    .findFirst().get();
            int[] skillLevel = CardSkillLevelUtil.calculateSkillLevels(daji.getLevel(), daji.getStar().doubleValue());


            // 谄媚噬魂：随机敌方中毒
            List<Guardian> enemies = campA.stream()
                    .filter(g -> !g.isDead())
                    .collect(Collectors.toList());

            if (!enemies.isEmpty()) {
                Guardian randomEnemy = enemies.get(random.nextInt(enemies.size()));
                int totalPoisonDamage = 7 * skillLevel[0];
                // 1. 计算所有中毒效果的总伤害（累加 POISON 类型的 value）
                // 2. 计算毒抗相关（直接基于你现有 EffectInstance 计算，不新增 Guardian 方法）
                // 中毒增益：所有 POISON_RESIST 类型效果的 value 总和
                int resistUp = calculateTotalVaule(daji, EffectType.POISON_BOOST);
                // 中毒增益百分比：所有 POISON_RESIST 类型效果的 value 乘积
                double resistUpPret = calculateTotalUpPretVaule(daji, EffectType.POISON_BOOST_PRET);
                // 中毒降低：所有 POISON_RESIST_DOWN 类型效果的 value 总和
                int resistDown = calculateTotalVaule(daji, EffectType.POISON_DOWN);
                // 中毒降低百分比：所有 POISON_RESIST 类型效果的 value 乘积
                double resistDownPret = calculateTotalDownPretVaule(daji, EffectType.POISON_DOWN_PRET);

                int poisonValue = (int) (totalPoisonDamage * resistUpPret * resistDownPret + (resistUp + daji.getDsAtk() - resistDown));
                randomEnemy.addEffect(EffectType.POISON, poisonValue, 99, daji.getId());
                addLog("谄媚噬魂",
                        daji.getId(),
                        daji.getMaxHp(),
                        daji.getCurrentHp(),
                        poisonValue,
                        daji.isOnField(),
                        randomEnemy.getId(),
                        randomEnemy.getMaxHp(),
                        randomEnemy.getCurrentHp(),
                        poisonValue,
                        randomEnemy.isOnField(),
                        EffectType.POISON,
                        DamageType.POISON,
                        "中毒+" + poisonValue);
            }
        }
    }

    // 批量处理中毒效果（完全按你真实结构：List<EffectInstance>）
// 批量处理中毒效果（无新增 Guardian 方法 · 完全适配你的 EffectInstance）
    private void processPoisonEffects() {
        List<Guardian> poisonedUnits = new ArrayList<>();

        // 收集所有带中毒效果且未死亡的单位
        campA.forEach(g -> {
            if (hasPoisonEffect(g) && !g.isDead()) {
                poisonedUnits.add(g);
            }
        });
        campB.forEach(g -> {
            if (hasPoisonEffect(g) && !g.isDead()) {
                poisonedUnits.add(g);
            }
        });

        if (poisonedUnits.isEmpty()) {
            return;
        }


        Map<String, TargetBattleData> targetStatus = new HashMap<>();
        Map<String, TargetBattleData> deadUnits = new HashMap<>();
        List<Guardian> deadGuardians = new ArrayList<>();
        poisonedUnits.forEach(g -> {


            // 1. 计算所有中毒效果的总伤害（累加 POISON 类型的 value）
            int totalPoisonDamage = calculateTotalVaule(g, EffectType.POISON);

            // 2. 计算毒抗相关（直接基于你现有 EffectInstance 计算，不新增 Guardian 方法）
            // 毒抗增益：所有 POISON_RESIST 类型效果的 value 总和
            int resistUp = calculateTotalVaule(g, EffectType.POISON_RESIST_BOOST);
            // 毒抗增益百分比：所有 POISON_RESIST 类型效果的 value 乘积
            double resistUpPret = calculateTotalDownPretVaule(g, EffectType.POISON_RESIST_BOOST_PRET);
            // 毒抗降低：所有 POISON_RESIST_DOWN 类型效果的 value 总和
            int resistDown = calculateTotalVaule(g, EffectType.POISON_RESIST_DOWN);
            // 毒抗降低百分比：所有 POISON_RESIST 类型效果的 value 乘积
            double resistDownPret = calculateTotalUpPretVaule(g, EffectType.POISON_RESIST_DOWN_PRET);
            // 最终有效毒抗（仅基于 buff 计算，无新增方法）

            int finalDamage = (int) (totalPoisonDamage * resistUpPret * resistDownPret - (resistUp + g.getDsDef() - resistDown));

            if (finalDamage < 0) {
                finalDamage = 0;
            }

            // 4. 扣除中毒伤害
            g.setCurrentHp(g.getCurrentHp() - finalDamage);
            TargetBattleData data = new TargetBattleData(g.getMaxHp(), g.getCurrentHp(), finalDamage, g.isOnField());
            targetStatus.put(g.getId(), data);

            // 5. 处理中毒效果回合数（调用自带的 tickRound()，移除回合≤0的效果）


            // 6. 死亡判定
            if (g.getCurrentHp() <= 0) {
                g.setDead(true);
                g.setOnField(false);
                deadUnits.put(g.getId(), data);
                deadGuardians.add(g);
            }
        });

        // 中毒伤害日志
        addMultiTargetLog("POISON",
                null,
                0,
                0,
                false,
                targetStatus,
                null,
                null,
                "中毒效果触发");

        // 阵亡日志
        if (!deadUnits.isEmpty()) {
            addMultiTargetLog("UNIT_DEATH",
                    null,
                    0,
                    0,
                    false,
                    deadUnits,
                    null,
                    null,
                    "死亡");
            //触发死亡技能
            for (Guardian g : deadGuardians) {
                triggerOnDeathSkills(g);
            }
        }
    }

// ===================== 仅保留必要的工具方法 =====================

    /**
     * 判断单位是否有中毒效果（仅用现有方法）
     */
    private boolean hasPoisonEffect(Guardian guardian) {
        return guardian.getEffects().stream()
                .anyMatch(e -> EffectType.POISON.equals(e.getType()));
    }

    /**
     * 计算总中毒伤害（仅用现有方法）
     */
    private int calculateTotalVaule(Guardian guardian, EffectType type) {
        return guardian.getEffects().stream()
                .filter(e -> type.equals(e.getType()))
                .mapToInt(EffectInstance::getValue)
                .sum();
    }

    private double calculateTotalUpPretVaule(Guardian guardian, EffectType type) {
        return guardian.getEffects().stream()
                .filter(e -> type.equals(e.getType()))
                .mapToDouble(e -> 1 + (double) e.getValue() / 100)
                .reduce((a, b) -> a * b) // 无初始值，返回OptionalDouble
                .orElse(1.0); // 无匹配元素时返回1.0（空乘积默认值）
    }

    private double calculateTotalDownPretVaule(Guardian guardian, EffectType type) {
        return guardian.getEffects().stream()
                .filter(e -> type.equals(e.getType()))
                .mapToDouble(e -> 1 - (double) e.getValue() / 100)
                .reduce((a, b) -> a * b) // 无初始值，返回OptionalDouble
                .orElse(1.0); // 无匹配元素时返回1.0（空乘积默认值）
    }

    // 处理登场场下技能
    private void processOnFieldSkills0(Guardian defender) {
        for (int i = 1; i < 6; i++) {
            int position = i;
            if (defender.getCamp() == Camp.B && campA.stream().anyMatch(g -> g.getName().equals("阎王") && g.getPosition() == position && !g.isDead() && !g.isOnField() && !g.isSilence())) {
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
                        int totalPoisonDamage = 73 * skillLevel[0];
                        // 1. 计算所有中毒效果的总伤害（累加 POISON 类型的 value）
                        // 2. 计算毒抗相关（直接基于你现有 EffectInstance 计算，不新增 Guardian 方法）
                        // 中毒增益：所有 POISON_RESIST 类型效果的 value 总和
                        int resistUp = calculateTotalVaule(guardian, EffectType.POISON_BOOST);
                        // 中毒增益百分比：所有 POISON_RESIST 类型效果的 value 乘积
                        double resistUpPret = calculateTotalUpPretVaule(guardian, EffectType.POISON_BOOST_PRET);
                        // 中毒降低：所有 POISON_RESIST_DOWN 类型效果的 value 总和
                        int resistDown = calculateTotalVaule(guardian, EffectType.POISON_DOWN);
                        // 中毒降低百分比：所有 POISON_RESIST 类型效果的 value 乘积
                        double resistDownPret = calculateTotalDownPretVaule(guardian, EffectType.POISON_DOWN_PRET);

                        int poisonValue = (int) (totalPoisonDamage * resistUpPret * resistDownPret + (resistUp + guardian.getDsAtk() - resistDown));
                        randomEnemy.addEffect(EffectType.POISON, poisonValue, 99, guardian.getId());
                        addLog("幽冥审判",
                                guardian.getId(),
                                guardian.getMaxHp(),
                                guardian.getCurrentHp(),
                                poisonValue,
                                guardian.isOnField(),
                                randomEnemy.getId(),
                                randomEnemy.getMaxHp(),
                                randomEnemy.getCurrentHp(),
                                poisonValue,
                                randomEnemy.isOnField(),
                                EffectType.POISON,
                                DamageType.POISON,
                                "中毒+" + poisonValue);
                    }
                }

            }

            if (defender.getCamp() == Camp.B && campA.stream().anyMatch(g -> g.getName().equals("金角大王") && g.getPosition() == position && !g.isDead() && !g.isOnField() && !g.isSilence())) {
                Guardian guardian = campA.stream()
                        .filter(g -> g.getName().equals("金角大王") && g.getPosition() == position && !g.isDead() && !g.isOnField())
                        .findFirst().get();
                int[] skillLevel = CardSkillLevelUtil.calculateSkillLevels(guardian.getLevel(), guardian.getStar().doubleValue());
                //幽冥审判Lv1每当有敌方登场，令随机敌方中毒73；
                List<Guardian> enemies = guardian.getCamp() == Camp.A ?
                        campB.stream().filter(g -> !g.isDead()).collect(Collectors.toList()) :
                        campA.stream().filter(g -> !g.isDead()).collect(Collectors.toList());
                if (!enemies.isEmpty()) {
                    Guardian randomEnemy = enemies.get(random.nextInt(enemies.size()));
                    int totalPoisonDamage = 7 * skillLevel[0];
                    // 1. 计算所有中毒效果的总伤害（累加 POISON 类型的 value）
                    // 2. 计算毒抗相关（直接基于你现有 EffectInstance 计算，不新增 Guardian 方法）
                    // 中毒增益：所有 POISON_RESIST 类型效果的 value 总和
                    int resistUp = calculateTotalVaule(guardian, EffectType.POISON_BOOST);
                    // 中毒增益百分比：所有 POISON_RESIST 类型效果的 value 乘积
                    double resistUpPret = calculateTotalUpPretVaule(guardian, EffectType.POISON_BOOST_PRET);
                    // 中毒降低：所有 POISON_RESIST_DOWN 类型效果的 value 总和
                    int resistDown = calculateTotalVaule(guardian, EffectType.POISON_DOWN);
                    // 中毒降低百分比：所有 POISON_RESIST 类型效果的 value 乘积
                    double resistDownPret = calculateTotalDownPretVaule(guardian, EffectType.POISON_DOWN_PRET);

                    int poisonValue = (int) (totalPoisonDamage * resistUpPret * resistDownPret + (resistUp + guardian.getDsAtk() - resistDown));
                    randomEnemy.addEffect(EffectType.POISON, poisonValue, 99, guardian.getId());
                    addLog("玉净瓶",
                            guardian.getId(),
                            guardian.getMaxHp(),
                            guardian.getCurrentHp(),
                            poisonValue,
                            guardian.isOnField(),
                            randomEnemy.getId(),
                            randomEnemy.getMaxHp(),
                            randomEnemy.getCurrentHp(),
                            poisonValue,
                            randomEnemy.isOnField(),
                            EffectType.POISON,
                            DamageType.POISON,
                            "中毒+" + poisonValue);
                }
            }

            if (defender.getCamp() == Camp.A && campB.stream().anyMatch(g -> g.getName().equals("阎王") && g.getPosition() == position && !g.isDead() && !g.isOnField())) {
                Guardian guardian = campB.stream()
                        .filter(g -> g.getName().equals("阎王") && g.getPosition() == position && !g.isDead() && !g.isOnField() && !g.isSilence())
                        .findFirst().get();
                int[] skillLevel = CardSkillLevelUtil.calculateSkillLevels(guardian.getLevel(), guardian.getStar().doubleValue());
                if (skillLevel[1] > 0) {
                    //幽冥审判Lv1每当有敌方登场，令随机敌方中毒73；
                    List<Guardian> enemies = guardian.getCamp() == Camp.B ?
                            campA.stream().filter(g -> !g.isDead()).collect(Collectors.toList()) :
                            campB.stream().filter(g -> !g.isDead()).collect(Collectors.toList());
                    if (!enemies.isEmpty()) {
                        Guardian randomEnemy = enemies.get(random.nextInt(enemies.size()));
                        int totalPoisonDamage = 73 * skillLevel[0];
                        // 1. 计算所有中毒效果的总伤害（累加 POISON 类型的 value）
                        // 2. 计算毒抗相关（直接基于你现有 EffectInstance 计算，不新增 Guardian 方法）
                        // 中毒增益：所有 POISON_RESIST 类型效果的 value 总和
                        int resistUp = calculateTotalVaule(guardian, EffectType.POISON_BOOST);
                        // 中毒增益百分比：所有 POISON_RESIST 类型效果的 value 乘积
                        double resistUpPret = calculateTotalUpPretVaule(guardian, EffectType.POISON_BOOST_PRET);
                        // 中毒降低：所有 POISON_RESIST_DOWN 类型效果的 value 总和
                        int resistDown = calculateTotalVaule(guardian, EffectType.POISON_DOWN);
                        // 中毒降低百分比：所有 POISON_RESIST 类型效果的 value 乘积
                        double resistDownPret = calculateTotalDownPretVaule(guardian, EffectType.POISON_DOWN_PRET);

                        int poisonValue = (int) (totalPoisonDamage * resistUpPret * resistDownPret + (resistUp + guardian.getDsAtk() - resistDown));
                        randomEnemy.addEffect(EffectType.POISON, poisonValue, 99, guardian.getId());
                        addLog("幽冥审判",
                                guardian.getId(),
                                guardian.getMaxHp(),
                                guardian.getCurrentHp(),
                                poisonValue,
                                guardian.isOnField(),
                                randomEnemy.getId(),
                                randomEnemy.getMaxHp(),
                                randomEnemy.getCurrentHp(),
                                poisonValue,
                                randomEnemy.isOnField(),
                                EffectType.POISON,
                                DamageType.POISON,
                                "中毒+" + poisonValue);
                    }
                }

            }

            if (defender.getCamp() == Camp.A && campB.stream().anyMatch(g -> g.getName().equals("金角大王") && g.getPosition() == position && !g.isDead() && !g.isOnField() && !g.isSilence())) {
                Guardian guardian = campB.stream()
                        .filter(g -> g.getName().equals("金角大王") && g.getPosition() == position && !g.isDead() && !g.isOnField())
                        .findFirst().get();
                int[] skillLevel = CardSkillLevelUtil.calculateSkillLevels(guardian.getLevel(), guardian.getStar().doubleValue());
                //幽冥审判Lv1每当有敌方登场，令随机敌方中毒73；
                List<Guardian> enemies = guardian.getCamp() == Camp.B ?
                        campA.stream().filter(g -> !g.isDead()).collect(Collectors.toList()) :
                        campB.stream().filter(g -> !g.isDead()).collect(Collectors.toList());
                if (!enemies.isEmpty()) {
                    Guardian randomEnemy = enemies.get(random.nextInt(enemies.size()));
                    int totalPoisonDamage = 7 * skillLevel[0] + guardian.getDsAtk();
                    // 1. 计算所有中毒效果的总伤害（累加 POISON 类型的 value）
                    // 2. 计算毒抗相关（直接基于你现有 EffectInstance 计算，不新增 Guardian 方法）
                    // 中毒增益：所有 POISON_RESIST 类型效果的 value 总和
                    int resistUp = calculateTotalVaule(guardian, EffectType.FIRE_BOOST);
                    // 中毒增益百分比：所有 POISON_RESIST 类型效果的 value 乘积
                    double resistUpPret = calculateTotalUpPretVaule(guardian, EffectType.FIRE_BOOST_PRET);
                    // 中毒降低：所有 POISON_RESIST_DOWN 类型效果的 value 总和
                    int resistDown = calculateTotalVaule(guardian, EffectType.FIRE_DOWN);
                    // 中毒降低百分比：所有 POISON_RESIST 类型效果的 value 乘积
                    double resistDownPret = calculateTotalDownPretVaule(guardian, EffectType.FIRE_DOWN_PRET);

                    int poisonValue = (int) (totalPoisonDamage * resistUpPret * resistDownPret + (resistUp + guardian.getDsAtk() - resistDown));
                    randomEnemy.addEffect(EffectType.POISON, poisonValue, 99, guardian.getId());
                    addLog("玉净瓶",
                            guardian.getId(),
                            guardian.getMaxHp(),
                            guardian.getCurrentHp(),
                            poisonValue,
                            guardian.isOnField(),
                            randomEnemy.getId(),
                            randomEnemy.getMaxHp(),
                            randomEnemy.getCurrentHp(),
                            poisonValue,
                            randomEnemy.isOnField(),
                            EffectType.POISON,
                            DamageType.POISON,
                            "中毒+" + poisonValue);
                }

            }
//            疫病侵染Lv1场下，我方单位登场时为场上敌人添加疾病效果，疾病令受到治疗的效果降低50%，最多叠加1层
            if (defender.getCamp() == Camp.A && campA.stream().anyMatch(g -> g.getName().equals("玄冥") && g.getPosition() == position && !g.isDead() && !g.isOnField() && !g.isSilence())) {
                Guardian guardian = campA.stream()
                        .filter(g -> g.getName().equals("玄冥") && g.getPosition() == position && !g.isDead() && !g.isOnField())
                        .findFirst().get();
                int[] skillLevel = CardSkillLevelUtil.calculateSkillLevels(guardian.getLevel(), guardian.getStar().doubleValue());
                if (fieldB != null && skillLevel[1] > 0 && fieldB.getBuffXuanMins() <= 0) {
                    int healDowNew = skillLevel[1] * 8;
                    fieldB.setBuffXuanMins(1);
                    fieldB.addEffect(EffectType.HEAL_DOWNT_PRET, healDowNew, 99, guardian.getId());

                    addLog("疫病侵染",
                            guardian.getId(),
                            guardian.getMaxHp(),
                            guardian.getCurrentHp(),
                            0,
                            guardian.isOnField(),
                            fieldB.getId(),
                            fieldB.getMaxHp(),
                            fieldB.getCurrentHp(),
                            0,
                            fieldB.isOnField(),
                            EffectType.HEAL_DOWN,
                            DamageType.MAGIC,
                            "治疗降低" + healDowNew + "%");
                }
            }

            if (defender.getCamp() == Camp.B && campB.stream().anyMatch(g -> g.getName().equals("玄冥") && g.getPosition() == position && !g.isDead() && !g.isOnField() && !g.isSilence())) {
                Guardian guardian = campB.stream()
                        .filter(g -> g.getName().equals("玄冥") && g.getPosition() == position && !g.isDead() && !g.isOnField())
                        .findFirst().get();
                int[] skillLevel = CardSkillLevelUtil.calculateSkillLevels(guardian.getLevel(), guardian.getStar().doubleValue());

                if (fieldA != null && skillLevel[1] > 0 && fieldA.getBuffXuanMins() <= 0) {
                    int healDowNew = skillLevel[1] * 8;
                    fieldA.setBuffXuanMins(1);
                    fieldA.addEffect(EffectType.HEAL_DOWNT_PRET, healDowNew, 99, guardian.getId());


                    addLog("疫病侵染",
                            guardian.getId(),
                            guardian.getMaxHp(),
                            guardian.getCurrentHp(),
                            0,
                            guardian.isOnField(),
                            fieldA.getId(),
                            fieldA.getMaxHp(),
                            fieldA.getCurrentHp(),
                            0,
                            fieldA.isOnField(),
                            EffectType.HEAL_DOWN,
                            DamageType.MAGIC,
                            "治疗降低" + healDowNew + "%");
                }
            }

//            镇元子，魂力飞弹Lv1场下，每当新单位入场时，对场上敌人造成178点飞弹伤害；禁心咒Lv1场下，每当有单位登场，有17%几率令场上英雄沉默2回合；多宝道人同Lv1与多宝道人在同一队伍时，增加自身453点生命上限，158点攻击，158点速度。
            if (campA.stream().anyMatch(g -> g.getName().equals("镇元子") && g.getPosition() == position && !g.isDead() && !g.isOnField() && !g.isSilence())) {
                Guardian guardian = campA.stream()
                        .filter(g -> g.getName().equals("镇元子") && g.getPosition() == position && !g.isDead() && !g.isOnField())
                        .findFirst().get();
                int[] skillLevel = CardSkillLevelUtil.calculateSkillLevels(guardian.getLevel(), guardian.getStar().doubleValue());
                if (fieldB != null && !fieldB.isDead()) {
                    // 1. 计算所有中毒效果的总伤害（累加 POISON 类型的 value）
                    int totalPoisonDamage = 178 * skillLevel[0];
                    // 2. 计算毒抗相关（直接基于你现有 EffectInstance 计算，不新增 Guardian 方法）
                    // 飞弹增益：所有 POISON_RESIST 类型效果的 value 总和
                    int resistUp = calculateTotalVaule(guardian, EffectType.MISSILE_BOOST);
                    // 飞弹增益百分比：所有 POISON_RESIST 类型效果的 value 乘积
                    double resistUpPret = calculateTotalUpPretVaule(guardian, EffectType.MISSILE_BOOST_PRET);
                    // 飞弹降低：所有 POISON_RESIST_DOWN 类型效果的 value 总和
                    int resistDown = calculateTotalVaule(guardian, EffectType.MISSILE_DOWN);
                    // 飞弹降低百分比：所有 POISON_RESIST 类型效果的 value 乘积
                    double resistDownPret = calculateTotalDownPretVaule(guardian, EffectType.MISSILE_DOWN_PRET);


                    // 2. 计算毒抗相关（直接基于你现有 EffectInstance 计算，不新增 Guardian 方法）
                    // 弹抗增益：所有 POISON_RESIST 类型效果的 value 总和
                    int targetUp = calculateTotalVaule(fieldB, EffectType.MISSILE_RESIST_BOOST);
                    // 弹抗增益百分比：所有 POISON_RESIST 类型效果的 value 乘积
                    double targetUpPret = calculateTotalDownPretVaule(fieldB, EffectType.MISSILE_RESIST_BOOST_PRET);
                    // 弹抗降低：所有 POISON_RESIST_DOWN 类型效果的 value 总和
                    int targetDown = calculateTotalVaule(fieldB, EffectType.MISSILE_RESIST_DOWN);
                    // 弹抗降低百分比：所有 POISON_RESIST 类型效果的 value fieldA
                    double targetDownPret = calculateTotalUpPretVaule(fieldB, EffectType.MISSILE_RESIST_DOWN_PRET);
                    // 最终（仅基于 buff 计算，无新增方法）

                    int burnDamage = (int) (totalPoisonDamage * resistUpPret * resistDownPret * targetUpPret * targetDownPret
                            + (resistUp - resistDown + guardian.getFdAtk() - fieldB.getFdDef() - targetUp + targetDown));

                    if (burnDamage < 0) {
                        burnDamage = 0;
                    }

                    // 4. 扣除伤害
                    fieldB.setCurrentHp(fieldB.getCurrentHp() - burnDamage);
                    Map<String, TargetBattleData> deadUnits = new HashMap<>();

                    if (fieldB.getCurrentHp() <= 0) {
                        fieldB.setDead(true);
                        fieldB.setOnField(false);
                        TargetBattleData data = new TargetBattleData(fieldB.getMaxHp(), fieldB.getCurrentHp(), burnDamage, fieldB.isOnField());
                        deadUnits.put(fieldB.getId(), data);
                    }
                    addLog("魂力飞弹",
                            guardian.getId(),
                            guardian.getMaxHp(),
                            guardian.getCurrentHp(),
                            0,
                            guardian.isOnField(),
                            fieldB.getId(),
                            fieldB.getMaxHp(),
                            fieldB.getCurrentHp(),
                            burnDamage,
                            fieldB.isOnField(),
                            EffectType.MISSILE_DAMAGE,
                            DamageType.MISSILE,
                            "-" + burnDamage);
                    // 死亡日志
                    if (!deadUnits.isEmpty()) {
                        addMultiTargetLog("UNIT_DEATH",
                                null,
                                0,
                                0,
                                false,
                                deadUnits,
                                null,
                                null,
                                "死亡");
                        //触发死亡技能
                        triggerOnDeathSkills(fieldB);

                    } else {
                        //触发受击技能
                        triggerOnAttackedSkills(fieldB, EffectType.MISSILE_DAMAGE);
                    }
                }
            }
//            禁心咒Lv1场下，每当有单位登场，有17%几率令场上英雄沉默2回合；
            if (campA.stream().anyMatch(g -> g.getName().equals("镇元子") && g.getPosition() == position && !g.isDead() && !g.isOnField() && !g.isSilence())) {
                Guardian guardian = campA.stream()
                        .filter(g -> g.getName().equals("镇元子") && g.getPosition() == position && !g.isDead() && !g.isOnField())
                        .findFirst().get();
                int[] skillLevel = CardSkillLevelUtil.calculateSkillLevels(guardian.getLevel(), guardian.getStar().doubleValue());
                //  禁心咒Lv1场下
                if (fieldB != null && !fieldB.isDead() && skillLevel[1] > 0 && ProbabilityBooleanUtils.randomByProbability(0.5) && fieldB != null) {
                    fieldB.addEffect(EffectType.SILENCE, 0, 2, guardian.getId());
                    addLog("禁心咒",
                            guardian.getId(),
                            guardian.getMaxHp(),
                            guardian.getCurrentHp(),
                            0,
                            guardian.isOnField(),
                            fieldB.getId(),
                            fieldB.getMaxHp(),
                            fieldB.getCurrentHp(),
                            0,
                            fieldB.isOnField(),
                            EffectType.SILENCE,
                            DamageType.MAGIC,
                            "沉默2回合");
                }
            }

//            镇元子，魂力飞弹Lv1场下，每当新单位入场时，对场上敌人造成178点飞弹伤害；禁心咒Lv1场下，每当有单位登场，有17%几率令场上英雄沉默2回合；多宝道人同Lv1与多宝道人在同一队伍时，增加自身453点生命上限，158点攻击，158点速度。
            if (campB.stream().anyMatch(g -> g.getName().equals("镇元子") && g.getPosition() == position && !g.isDead() && !g.isOnField() && !g.isSilence())) {
                Guardian guardian = campB.stream()
                        .filter(g -> g.getName().equals("镇元子") && g.getPosition() == position && !g.isDead() && !g.isOnField())
                        .findFirst().get();
                int[] skillLevel = CardSkillLevelUtil.calculateSkillLevels(guardian.getLevel(), guardian.getStar().doubleValue());
                if (fieldA != null && !fieldA.isDead()) {
                    // 1. 计算所有中毒效果的总伤害（累加 POISON 类型的 value）
                    int totalPoisonDamage = 178 * skillLevel[0];
                    // 2. 计算毒抗相关（直接基于你现有 EffectInstance 计算，不新增 Guardian 方法）
                    // 飞弹增益：所有 POISON_RESIST 类型效果的 value 总和
                    int resistUp = calculateTotalVaule(guardian, EffectType.MISSILE_BOOST);
                    // 飞弹增益百分比：所有 POISON_RESIST 类型效果的 value 乘积
                    double resistUpPret = calculateTotalUpPretVaule(guardian, EffectType.MISSILE_BOOST_PRET);
                    // 飞弹降低：所有 POISON_RESIST_DOWN 类型效果的 value 总和
                    int resistDown = calculateTotalVaule(guardian, EffectType.MISSILE_DOWN);
                    // 飞弹降低百分比：所有 POISON_RESIST 类型效果的 value 乘积
                    double resistDownPret = calculateTotalDownPretVaule(guardian, EffectType.MISSILE_DOWN_PRET);


                    // 2. 计算毒抗相关（直接基于你现有 EffectInstance 计算，不新增 Guardian 方法）
                    // 弹抗增益：所有 POISON_RESIST 类型效果的 value 总和
                    int targetUp = calculateTotalVaule(fieldA, EffectType.MISSILE_RESIST_BOOST);
                    // 弹抗增益百分比：所有 POISON_RESIST 类型效果的 value 乘积
                    double targetUpPret = calculateTotalDownPretVaule(fieldA, EffectType.MISSILE_RESIST_BOOST_PRET);
                    // 弹抗降低：所有 POISON_RESIST_DOWN 类型效果的 value 总和
                    int targetDown = calculateTotalVaule(fieldA, EffectType.MISSILE_RESIST_DOWN);
                    // 弹抗降低百分比：所有 POISON_RESIST 类型效果的 value fieldA
                    double targetDownPret = calculateTotalUpPretVaule(fieldA, EffectType.MISSILE_RESIST_DOWN_PRET);
                    // 最终（仅基于 buff 计算，无新增方法）

                    int burnDamage = (int) (totalPoisonDamage * resistUpPret * resistDownPret * targetUpPret * targetDownPret
                            + (resistUp - resistDown + guardian.getFdAtk() - fieldA.getFdDef() - targetUp + targetDown));

                    if (burnDamage < 0) {
                        burnDamage = 0;
                    }

                    // 4. 扣除伤害
                    fieldA.setCurrentHp(fieldA.getCurrentHp() - burnDamage);
                    Map<String, TargetBattleData> deadUnits = new HashMap<>();

                    if (fieldA.getCurrentHp() <= 0) {
                        fieldA.setDead(true);
                        fieldA.setOnField(false);
                        TargetBattleData data = new TargetBattleData(fieldA.getMaxHp(), fieldA.getCurrentHp(), burnDamage, fieldA.isOnField());
                        deadUnits.put(fieldA.getId(), data);
                    }
                    addLog("魂力飞弹",
                            guardian.getId(),
                            guardian.getMaxHp(),
                            guardian.getCurrentHp(),
                            0,
                            guardian.isOnField(),
                            fieldA.getId(),
                            fieldA.getMaxHp(),
                            fieldA.getCurrentHp(),
                            burnDamage,
                            fieldA.isOnField(),
                            EffectType.MISSILE_DAMAGE,
                            DamageType.MISSILE,
                            "-" + burnDamage);
                    // 死亡日志
                    if (!deadUnits.isEmpty()) {
                        addMultiTargetLog("UNIT_DEATH",
                                null,
                                0,
                                0,
                                false,
                                deadUnits,
                                null,
                                null,
                                "死亡");
                        //触发死亡技能
                        triggerOnDeathSkills(fieldA);

                    } else {
                        //触发受击技能
                        triggerOnAttackedSkills(fieldA, EffectType.MISSILE_DAMAGE);
                    }
                }
            }
//            禁心咒Lv1场下，每当有单位登场，有17%几率令场上英雄沉默2回合；
            if (campB.stream().anyMatch(g -> g.getName().equals("镇元子") && g.getPosition() == position && !g.isDead() && !g.isOnField() && !g.isSilence())) {
                Guardian guardian = campB.stream()
                        .filter(g -> g.getName().equals("镇元子") && g.getPosition() == position && !g.isDead() && !g.isOnField())
                        .findFirst().get();
                int[] skillLevel = CardSkillLevelUtil.calculateSkillLevels(guardian.getLevel(), guardian.getStar().doubleValue());
                //  禁心咒Lv1场下
                if (fieldA != null && !fieldA.isDead() && skillLevel[1] > 0 && ProbabilityBooleanUtils.randomByProbability(0.5) && fieldA != null) {
                    fieldA.addEffect(EffectType.SILENCE, 0, 2, guardian.getId());
                    addLog("禁心咒",
                            guardian.getId(),
                            guardian.getMaxHp(),
                            guardian.getCurrentHp(),
                            0,
                            guardian.isOnField(),
                            fieldA.getId(),
                            fieldA.getMaxHp(),
                            fieldA.getCurrentHp(),
                            0,
                            fieldA.isOnField(),
                            EffectType.SILENCE,
                            DamageType.MAGIC,
                            "沉默2回合");
                }
            }

//            南华真人，魂力飞弹Lv1场下，每当新单位入场时，对场上敌人造成178点飞弹伤害；报复神箭Lv1场下，每回合对场上敌方造成106；
            if (campA.stream().anyMatch(g -> g.getName().equals("南华真人") && g.getPosition() == position && !g.isDead() && !g.isOnField() && !g.isSilence())) {
                Guardian guardian = campA.stream()
                        .filter(g -> g.getName().equals("南华真人") && g.getPosition() == position && !g.isDead() && !g.isOnField())
                        .findFirst().get();
                int[] skillLevel = CardSkillLevelUtil.calculateSkillLevels(guardian.getLevel(), guardian.getStar().doubleValue());
                if (fieldB != null && !fieldB.isDead()) {
                    // 1. 计算所有中毒效果的总伤害（累加 POISON 类型的 value）
                    int totalPoisonDamage = 178 * skillLevel[0];
                    // 2. 计算毒抗相关（直接基于你现有 EffectInstance 计算，不新增 Guardian 方法）
                    // 飞弹增益：所有 POISON_RESIST 类型效果的 value 总和
                    int resistUp = calculateTotalVaule(guardian, EffectType.MISSILE_BOOST);
                    // 飞弹增益百分比：所有 POISON_RESIST 类型效果的 value 乘积
                    double resistUpPret = calculateTotalUpPretVaule(guardian, EffectType.MISSILE_BOOST_PRET);
                    // 飞弹降低：所有 POISON_RESIST_DOWN 类型效果的 value 总和
                    int resistDown = calculateTotalVaule(guardian, EffectType.MISSILE_DOWN);
                    // 飞弹降低百分比：所有 POISON_RESIST 类型效果的 value 乘积
                    double resistDownPret = calculateTotalDownPretVaule(guardian, EffectType.MISSILE_DOWN_PRET);


                    // 2. 计算毒抗相关（直接基于你现有 EffectInstance 计算，不新增 Guardian 方法）
                    // 弹抗增益：所有 POISON_RESIST 类型效果的 value 总和
                    int targetUp = calculateTotalVaule(fieldB, EffectType.MISSILE_RESIST_BOOST);
                    // 弹抗增益百分比：所有 POISON_RESIST 类型效果的 value 乘积
                    double targetUpPret = calculateTotalDownPretVaule(fieldB, EffectType.MISSILE_RESIST_BOOST_PRET);
                    // 弹抗降低：所有 POISON_RESIST_DOWN 类型效果的 value 总和
                    int targetDown = calculateTotalVaule(fieldB, EffectType.MISSILE_RESIST_DOWN);
                    // 弹抗降低百分比：所有 POISON_RESIST 类型效果的 value fieldA
                    double targetDownPret = calculateTotalUpPretVaule(fieldB, EffectType.MISSILE_RESIST_DOWN_PRET);
                    // 最终（仅基于 buff 计算，无新增方法）

                    int burnDamage = (int) (totalPoisonDamage * resistUpPret * resistDownPret * targetUpPret * targetDownPret
                            + (resistUp - resistDown + guardian.getFdAtk() - fieldB.getFdDef() - targetUp + targetDown));

                    if (burnDamage < 0) {
                        burnDamage = 0;
                    }

                    // 4. 扣除伤害
                    fieldB.setCurrentHp(fieldB.getCurrentHp() - burnDamage);
                    Map<String, TargetBattleData> deadUnits = new HashMap<>();

                    if (fieldB.getCurrentHp() <= 0) {
                        fieldB.setDead(true);
                        fieldB.setOnField(false);
                        TargetBattleData data = new TargetBattleData(fieldB.getMaxHp(), fieldB.getCurrentHp(), burnDamage, fieldB.isOnField());
                        deadUnits.put(fieldB.getId(), data);
                    }
                    addLog("魂力飞弹",
                            guardian.getId(),
                            guardian.getMaxHp(),
                            guardian.getCurrentHp(),
                            0,
                            guardian.isOnField(),
                            fieldB.getId(),
                            fieldB.getMaxHp(),
                            fieldB.getCurrentHp(),
                            burnDamage,
                            fieldB.isOnField(),
                            EffectType.MISSILE_DAMAGE,
                            DamageType.MISSILE,
                            "-" + burnDamage);
                    // 死亡日志
                    if (!deadUnits.isEmpty()) {
                        addMultiTargetLog("UNIT_DEATH",
                                null,
                                0,
                                0,
                                false,
                                deadUnits,
                                null,
                                null,
                                "死亡");
                        //触发死亡技能
                        triggerOnDeathSkills(fieldB);

                    } else {
                        //触发受击技能
                        triggerOnAttackedSkills(fieldB, EffectType.MISSILE_DAMAGE);
                    }
                }
            }
//            镇元子，魂力飞弹Lv1场下，每当新单位入场时，对场上敌人造成178点飞弹伤害；禁心咒Lv1场下，每当有单位登场，有17%几率令场上英雄沉默2回合；多宝道人同Lv1与多宝道人在同一队伍时，增加自身453点生命上限，158点攻击，158点速度。
            if (campB.stream().anyMatch(g -> g.getName().equals("南华真人") && g.getPosition() == position && !g.isDead() && !g.isOnField() && !g.isSilence())) {
                Guardian guardian = campB.stream()
                        .filter(g -> g.getName().equals("南华真人") && g.getPosition() == position && !g.isDead() && !g.isOnField())
                        .findFirst().get();
                int[] skillLevel = CardSkillLevelUtil.calculateSkillLevels(guardian.getLevel(), guardian.getStar().doubleValue());
                if (fieldA != null && !fieldA.isDead()) {
                    // 1. 计算所有中毒效果的总伤害（累加 POISON 类型的 value）
                    int totalPoisonDamage = 178 * skillLevel[0];
                    // 2. 计算毒抗相关（直接基于你现有 EffectInstance 计算，不新增 Guardian 方法）
                    // 飞弹增益：所有 POISON_RESIST 类型效果的 value 总和
                    int resistUp = calculateTotalVaule(guardian, EffectType.MISSILE_BOOST);
                    // 飞弹增益百分比：所有 POISON_RESIST 类型效果的 value 乘积
                    double resistUpPret = calculateTotalUpPretVaule(guardian, EffectType.MISSILE_BOOST_PRET);
                    // 飞弹降低：所有 POISON_RESIST_DOWN 类型效果的 value 总和
                    int resistDown = calculateTotalVaule(guardian, EffectType.MISSILE_DOWN);
                    // 飞弹降低百分比：所有 POISON_RESIST 类型效果的 value 乘积
                    double resistDownPret = calculateTotalDownPretVaule(guardian, EffectType.MISSILE_DOWN_PRET);


                    // 2. 计算毒抗相关（直接基于你现有 EffectInstance 计算，不新增 Guardian 方法）
                    // 弹抗增益：所有 POISON_RESIST 类型效果的 value 总和
                    int targetUp = calculateTotalVaule(fieldA, EffectType.MISSILE_RESIST_BOOST);
                    // 弹抗增益百分比：所有 POISON_RESIST 类型效果的 value 乘积
                    double targetUpPret = calculateTotalDownPretVaule(fieldA, EffectType.MISSILE_RESIST_BOOST_PRET);
                    // 弹抗降低：所有 POISON_RESIST_DOWN 类型效果的 value 总和
                    int targetDown = calculateTotalVaule(fieldA, EffectType.MISSILE_RESIST_DOWN);
                    // 弹抗降低百分比：所有 POISON_RESIST 类型效果的 value fieldA
                    double targetDownPret = calculateTotalUpPretVaule(fieldA, EffectType.MISSILE_RESIST_DOWN_PRET);
                    // 最终（仅基于 buff 计算，无新增方法）

                    int burnDamage = (int) (totalPoisonDamage * resistUpPret * resistDownPret * targetUpPret * targetDownPret
                            + (resistUp - resistDown + guardian.getFdAtk() - fieldA.getFdDef() - targetUp + targetDown));

                    if (burnDamage < 0) {
                        burnDamage = 0;
                    }

                    // 4. 扣除伤害
                    fieldA.setCurrentHp(fieldA.getCurrentHp() - burnDamage);
                    Map<String, TargetBattleData> deadUnits = new HashMap<>();

                    if (fieldA.getCurrentHp() <= 0) {
                        fieldA.setDead(true);
                        fieldA.setOnField(false);
                        TargetBattleData data = new TargetBattleData(fieldA.getMaxHp(), fieldA.getCurrentHp(), burnDamage, fieldA.isOnField());
                        deadUnits.put(fieldA.getId(), data);
                    }
                    addLog("魂力飞弹",
                            guardian.getId(),
                            guardian.getMaxHp(),
                            guardian.getCurrentHp(),
                            0,
                            guardian.isOnField(),
                            fieldA.getId(),
                            fieldA.getMaxHp(),
                            fieldA.getCurrentHp(),
                            burnDamage,
                            fieldA.isOnField(),
                            EffectType.MISSILE_DAMAGE,
                            DamageType.MISSILE,
                            "-" + burnDamage);
                    // 死亡日志
                    if (!deadUnits.isEmpty()) {
                        addMultiTargetLog("UNIT_DEATH",
                                null,
                                0,
                                0,
                                false,
                                deadUnits,
                                null,
                                null,
                                "死亡");
                        //触发死亡技能
                        triggerOnDeathSkills(fieldA);

                    } else {
                        //触发受击技能
                        triggerOnAttackedSkills(fieldA, EffectType.MISSILE_DAMAGE);
                    }
                }
            }


            //            南华真人，魂力飞弹Lv1场下，每当新单位入场时，对场上敌人造成178点飞弹伤害；报复神箭Lv1场下，每回合对场上敌方造成106；
            if (campA.stream().anyMatch(g -> g.getName().equals("北岳大帝") && g.getPosition() == position && !g.isDead() && !g.isOnField() && !g.isSilence())) {
                Guardian guardian = campA.stream()
                        .filter(g -> g.getName().equals("北岳大帝") && g.getPosition() == position && !g.isDead() && !g.isOnField())
                        .findFirst().get();
                int[] skillLevel = CardSkillLevelUtil.calculateSkillLevels(guardian.getLevel(), guardian.getStar().doubleValue());
                if (fieldB != null && !fieldB.isDead()) {
                    // 1. 计算所有中毒效果的总伤害（累加 POISON 类型的 value）
                    int totalPoisonDamage = 169 * skillLevel[0];
                    // 2. 计算毒抗相关（直接基于你现有 EffectInstance 计算，不新增 Guardian 方法）
                    // 飞弹增益：所有 POISON_RESIST 类型效果的 value 总和
                    int resistUp = calculateTotalVaule(guardian, EffectType.MISSILE_BOOST);
                    // 飞弹增益百分比：所有 POISON_RESIST 类型效果的 value 乘积
                    double resistUpPret = calculateTotalUpPretVaule(guardian, EffectType.MISSILE_BOOST_PRET);
                    // 飞弹降低：所有 POISON_RESIST_DOWN 类型效果的 value 总和
                    int resistDown = calculateTotalVaule(guardian, EffectType.MISSILE_DOWN);
                    // 飞弹降低百分比：所有 POISON_RESIST 类型效果的 value 乘积
                    double resistDownPret = calculateTotalDownPretVaule(guardian, EffectType.MISSILE_DOWN_PRET);


                    // 2. 计算毒抗相关（直接基于你现有 EffectInstance 计算，不新增 Guardian 方法）
                    // 弹抗增益：所有 POISON_RESIST 类型效果的 value 总和
                    int targetUp = calculateTotalVaule(fieldB, EffectType.MISSILE_RESIST_BOOST);
                    // 弹抗增益百分比：所有 POISON_RESIST 类型效果的 value 乘积
                    double targetUpPret = calculateTotalDownPretVaule(fieldB, EffectType.MISSILE_RESIST_BOOST_PRET);
                    // 弹抗降低：所有 POISON_RESIST_DOWN 类型效果的 value 总和
                    int targetDown = calculateTotalVaule(fieldB, EffectType.MISSILE_RESIST_DOWN);
                    // 弹抗降低百分比：所有 POISON_RESIST 类型效果的 value fieldA
                    double targetDownPret = calculateTotalUpPretVaule(fieldB, EffectType.MISSILE_RESIST_DOWN_PRET);
                    // 最终（仅基于 buff 计算，无新增方法）

                    int burnDamage = (int) (totalPoisonDamage * resistUpPret * resistDownPret * targetUpPret * targetDownPret
                            + (resistUp - resistDown + guardian.getFdAtk() - fieldB.getFdDef() - targetUp + targetDown));

                    if (burnDamage < 0) {
                        burnDamage = 0;
                    }

                    // 4. 扣除伤害
                    fieldB.setCurrentHp(fieldB.getCurrentHp() - burnDamage);
                    Map<String, TargetBattleData> deadUnits = new HashMap<>();

                    if (fieldB.getCurrentHp() <= 0) {
                        fieldB.setDead(true);
                        fieldB.setOnField(false);
                        TargetBattleData data = new TargetBattleData(fieldB.getMaxHp(), fieldB.getCurrentHp(), burnDamage, fieldB.isOnField());
                        deadUnits.put(fieldB.getId(), data);
                    }
                    addLog("魂力飞弹",
                            guardian.getId(),
                            guardian.getMaxHp(),
                            guardian.getCurrentHp(),
                            0,
                            guardian.isOnField(),
                            fieldB.getId(),
                            fieldB.getMaxHp(),
                            fieldB.getCurrentHp(),
                            burnDamage,
                            fieldB.isOnField(),
                            EffectType.MISSILE_DAMAGE,
                            DamageType.MISSILE,
                            "-" + burnDamage);
                    // 死亡日志
                    if (!deadUnits.isEmpty()) {
                        addMultiTargetLog("UNIT_DEATH",
                                null,
                                0,
                                0,
                                false,
                                deadUnits,
                                null,
                                null,
                                "死亡");
                        //触发死亡技能
                        triggerOnDeathSkills(fieldB);

                    } else {
                        //触发受击技能
                        triggerOnAttackedSkills(fieldB, EffectType.MISSILE_DAMAGE);
                    }
                }
            }
//            镇元子，魂力飞弹Lv1场下，每当新单位入场时，对场上敌人造成178点飞弹伤害；禁心咒Lv1场下，每当有单位登场，有17%几率令场上英雄沉默2回合；多宝道人同Lv1与多宝道人在同一队伍时，增加自身453点生命上限，158点攻击，158点速度。
            if (campB.stream().anyMatch(g -> g.getName().equals("北岳大帝") && g.getPosition() == position && !g.isDead() && !g.isOnField() && !g.isSilence())) {
                Guardian guardian = campB.stream()
                        .filter(g -> g.getName().equals("北岳大帝") && g.getPosition() == position && !g.isDead() && !g.isOnField())
                        .findFirst().get();
                int[] skillLevel = CardSkillLevelUtil.calculateSkillLevels(guardian.getLevel(), guardian.getStar().doubleValue());
                if (fieldA != null && !fieldA.isDead()) {
                    // 1. 计算所有中毒效果的总伤害（累加 POISON 类型的 value）
                    int totalPoisonDamage = 169 * skillLevel[0];
                    // 2. 计算毒抗相关（直接基于你现有 EffectInstance 计算，不新增 Guardian 方法）
                    // 飞弹增益：所有 POISON_RESIST 类型效果的 value 总和
                    int resistUp = calculateTotalVaule(guardian, EffectType.MISSILE_BOOST);
                    // 飞弹增益百分比：所有 POISON_RESIST 类型效果的 value 乘积
                    double resistUpPret = calculateTotalUpPretVaule(guardian, EffectType.MISSILE_BOOST_PRET);
                    // 飞弹降低：所有 POISON_RESIST_DOWN 类型效果的 value 总和
                    int resistDown = calculateTotalVaule(guardian, EffectType.MISSILE_DOWN);
                    // 飞弹降低百分比：所有 POISON_RESIST 类型效果的 value 乘积
                    double resistDownPret = calculateTotalDownPretVaule(guardian, EffectType.MISSILE_DOWN_PRET);


                    // 2. 计算毒抗相关（直接基于你现有 EffectInstance 计算，不新增 Guardian 方法）
                    // 弹抗增益：所有 POISON_RESIST 类型效果的 value 总和
                    int targetUp = calculateTotalVaule(fieldA, EffectType.MISSILE_RESIST_BOOST);
                    // 弹抗增益百分比：所有 POISON_RESIST 类型效果的 value 乘积
                    double targetUpPret = calculateTotalDownPretVaule(fieldA, EffectType.MISSILE_RESIST_BOOST_PRET);
                    // 弹抗降低：所有 POISON_RESIST_DOWN 类型效果的 value 总和
                    int targetDown = calculateTotalVaule(fieldA, EffectType.MISSILE_RESIST_DOWN);
                    // 弹抗降低百分比：所有 POISON_RESIST 类型效果的 value fieldA
                    double targetDownPret = calculateTotalUpPretVaule(fieldA, EffectType.MISSILE_RESIST_DOWN_PRET);
                    // 最终（仅基于 buff 计算，无新增方法）

                    int burnDamage = (int) (totalPoisonDamage * resistUpPret * resistDownPret * targetUpPret * targetDownPret
                            + (resistUp - resistDown + guardian.getFdAtk() - fieldA.getFdDef() - targetUp + targetDown));

                    if (burnDamage < 0) {
                        burnDamage = 0;
                    }

                    // 4. 扣除伤害
                    fieldA.setCurrentHp(fieldA.getCurrentHp() - burnDamage);
                    Map<String, TargetBattleData> deadUnits = new HashMap<>();

                    if (fieldA.getCurrentHp() <= 0) {
                        fieldA.setDead(true);
                        fieldA.setOnField(false);
                        TargetBattleData data = new TargetBattleData(fieldA.getMaxHp(), fieldA.getCurrentHp(), burnDamage, fieldA.isOnField());
                        deadUnits.put(fieldA.getId(), data);
                    }
                    addLog("魂力飞弹",
                            guardian.getId(),
                            guardian.getMaxHp(),
                            guardian.getCurrentHp(),
                            0,
                            guardian.isOnField(),
                            fieldA.getId(),
                            fieldA.getMaxHp(),
                            fieldA.getCurrentHp(),
                            burnDamage,
                            fieldA.isOnField(),
                            EffectType.MISSILE_DAMAGE,
                            DamageType.MISSILE,
                            "-" + burnDamage);
                    // 死亡日志
                    if (!deadUnits.isEmpty()) {
                        addMultiTargetLog("UNIT_DEATH",
                                null,
                                0,
                                0,
                                false,
                                deadUnits,
                                null,
                                null,
                                "死亡");
                        //触发死亡技能
                        triggerOnDeathSkills(fieldA);

                    } else {
                        //触发受击技能
                        triggerOnAttackedSkills(fieldA, EffectType.MISSILE_DAMAGE);
                    }
                }
            }

            //            南华真人，魂力飞弹Lv1场下，每当新单位入场时，对场上敌人造成178点飞弹伤害；报复神箭Lv1场下，每回合对场上敌方造成106；
            if (campA.stream().anyMatch(g -> g.getName().equals("西岳大帝") && g.getPosition() == position && !g.isDead() && !g.isOnField() && !g.isSilence())) {
                Guardian guardian = campA.stream()
                        .filter(g -> g.getName().equals("西岳大帝") && g.getPosition() == position && !g.isDead() && !g.isOnField())
                        .findFirst().get();
                int[] skillLevel = CardSkillLevelUtil.calculateSkillLevels(guardian.getLevel(), guardian.getStar().doubleValue());
                if (fieldB != null && !fieldB.isDead()) {
                    // 1. 计算所有中毒效果的总伤害（累加 POISON 类型的 value）
                    int totalPoisonDamage = 84 * skillLevel[0];
                    // 2. 计算毒抗相关（直接基于你现有 EffectInstance 计算，不新增 Guardian 方法）
                    // 飞弹增益：所有 POISON_RESIST 类型效果的 value 总和
                    int resistUp = calculateTotalVaule(guardian, EffectType.MISSILE_BOOST);
                    // 飞弹增益百分比：所有 POISON_RESIST 类型效果的 value 乘积
                    double resistUpPret = calculateTotalUpPretVaule(guardian, EffectType.MISSILE_BOOST_PRET);
                    // 飞弹降低：所有 POISON_RESIST_DOWN 类型效果的 value 总和
                    int resistDown = calculateTotalVaule(guardian, EffectType.MISSILE_DOWN);
                    // 飞弹降低百分比：所有 POISON_RESIST 类型效果的 value 乘积
                    double resistDownPret = calculateTotalDownPretVaule(guardian, EffectType.MISSILE_DOWN_PRET);


                    // 2. 计算毒抗相关（直接基于你现有 EffectInstance 计算，不新增 Guardian 方法）
                    // 弹抗增益：所有 POISON_RESIST 类型效果的 value 总和
                    int targetUp = calculateTotalVaule(fieldB, EffectType.MISSILE_RESIST_BOOST);
                    // 弹抗增益百分比：所有 POISON_RESIST 类型效果的 value 乘积
                    double targetUpPret = calculateTotalDownPretVaule(fieldB, EffectType.MISSILE_RESIST_BOOST_PRET);
                    // 弹抗降低：所有 POISON_RESIST_DOWN 类型效果的 value 总和
                    int targetDown = calculateTotalVaule(fieldB, EffectType.MISSILE_RESIST_DOWN);
                    // 弹抗降低百分比：所有 POISON_RESIST 类型效果的 value fieldA
                    double targetDownPret = calculateTotalUpPretVaule(fieldB, EffectType.MISSILE_RESIST_DOWN_PRET);
                    // 最终（仅基于 buff 计算，无新增方法）

                    int burnDamage = (int) (totalPoisonDamage * resistUpPret * resistDownPret * targetUpPret * targetDownPret
                            + (resistUp - resistDown + guardian.getFdAtk() - fieldB.getFdDef() - targetUp + targetDown));

                    if (burnDamage < 0) {
                        burnDamage = 0;
                    }

                    // 4. 扣除伤害
                    fieldB.setCurrentHp(fieldB.getCurrentHp() - burnDamage);
                    Map<String, TargetBattleData> deadUnits = new HashMap<>();

                    if (fieldB.getCurrentHp() <= 0) {
                        fieldB.setDead(true);
                        fieldB.setOnField(false);
                        TargetBattleData data = new TargetBattleData(fieldB.getMaxHp(), fieldB.getCurrentHp(), burnDamage, fieldB.isOnField());
                        deadUnits.put(fieldB.getId(), data);
                    }
                    addLog("魂力飞弹",
                            guardian.getId(),
                            guardian.getMaxHp(),
                            guardian.getCurrentHp(),
                            0,
                            guardian.isOnField(),
                            fieldB.getId(),
                            fieldB.getMaxHp(),
                            fieldB.getCurrentHp(),
                            burnDamage,
                            fieldB.isOnField(),
                            EffectType.MISSILE_DAMAGE,
                            DamageType.MISSILE,
                            "-" + burnDamage);
                    // 死亡日志
                    if (!deadUnits.isEmpty()) {
                        addMultiTargetLog("UNIT_DEATH",
                                null,
                                0,
                                0,
                                false,
                                deadUnits,
                                null,
                                null,
                                "死亡");
                        //触发死亡技能
                        triggerOnDeathSkills(fieldB);

                    } else {
                        //触发受击技能
                        triggerOnAttackedSkills(fieldB, EffectType.MISSILE_DAMAGE);
                    }
                }
            }
//            镇元子，魂力飞弹Lv1场下，每当新单位入场时，对场上敌人造成178点飞弹伤害；禁心咒Lv1场下，每当有单位登场，有17%几率令场上英雄沉默2回合；多宝道人同Lv1与多宝道人在同一队伍时，增加自身453点生命上限，158点攻击，158点速度。
            if (campB.stream().anyMatch(g -> g.getName().equals("西岳大帝") && g.getPosition() == position && !g.isDead() && !g.isOnField() && !g.isSilence())) {
                Guardian guardian = campB.stream()
                        .filter(g -> g.getName().equals("西岳大帝") && g.getPosition() == position && !g.isDead() && !g.isOnField())
                        .findFirst().get();
                int[] skillLevel = CardSkillLevelUtil.calculateSkillLevels(guardian.getLevel(), guardian.getStar().doubleValue());
                if (fieldA != null && !fieldA.isDead()) {
                    // 1. 计算所有中毒效果的总伤害（累加 POISON 类型的 value）
                    int totalPoisonDamage = 84 * skillLevel[0];
                    // 2. 计算毒抗相关（直接基于你现有 EffectInstance 计算，不新增 Guardian 方法）
                    // 飞弹增益：所有 POISON_RESIST 类型效果的 value 总和
                    int resistUp = calculateTotalVaule(guardian, EffectType.MISSILE_BOOST);
                    // 飞弹增益百分比：所有 POISON_RESIST 类型效果的 value 乘积
                    double resistUpPret = calculateTotalUpPretVaule(guardian, EffectType.MISSILE_BOOST_PRET);
                    // 飞弹降低：所有 POISON_RESIST_DOWN 类型效果的 value 总和
                    int resistDown = calculateTotalVaule(guardian, EffectType.MISSILE_DOWN);
                    // 飞弹降低百分比：所有 POISON_RESIST 类型效果的 value 乘积
                    double resistDownPret = calculateTotalDownPretVaule(guardian, EffectType.MISSILE_DOWN_PRET);


                    // 2. 计算毒抗相关（直接基于你现有 EffectInstance 计算，不新增 Guardian 方法）
                    // 弹抗增益：所有 POISON_RESIST 类型效果的 value 总和
                    int targetUp = calculateTotalVaule(fieldA, EffectType.MISSILE_RESIST_BOOST);
                    // 弹抗增益百分比：所有 POISON_RESIST 类型效果的 value 乘积
                    double targetUpPret = calculateTotalDownPretVaule(fieldA, EffectType.MISSILE_RESIST_BOOST_PRET);
                    // 弹抗降低：所有 POISON_RESIST_DOWN 类型效果的 value 总和
                    int targetDown = calculateTotalVaule(fieldA, EffectType.MISSILE_RESIST_DOWN);
                    // 弹抗降低百分比：所有 POISON_RESIST 类型效果的 value fieldA
                    double targetDownPret = calculateTotalUpPretVaule(fieldA, EffectType.MISSILE_RESIST_DOWN_PRET);
                    // 最终（仅基于 buff 计算，无新增方法）

                    int burnDamage = (int) (totalPoisonDamage * resistUpPret * resistDownPret * targetUpPret * targetDownPret
                            + (resistUp - resistDown + guardian.getFdAtk() - fieldA.getFdDef() - targetUp + targetDown));

                    if (burnDamage < 0) {
                        burnDamage = 0;
                    }

                    // 4. 扣除伤害
                    fieldA.setCurrentHp(fieldA.getCurrentHp() - burnDamage);
                    Map<String, TargetBattleData> deadUnits = new HashMap<>();

                    if (fieldA.getCurrentHp() <= 0) {
                        fieldA.setDead(true);
                        fieldA.setOnField(false);
                        TargetBattleData data = new TargetBattleData(fieldA.getMaxHp(), fieldA.getCurrentHp(), burnDamage, fieldA.isOnField());
                        deadUnits.put(fieldA.getId(), data);
                    }
                    addLog("魂力飞弹",
                            guardian.getId(),
                            guardian.getMaxHp(),
                            guardian.getCurrentHp(),
                            0,
                            guardian.isOnField(),
                            fieldA.getId(),
                            fieldA.getMaxHp(),
                            fieldA.getCurrentHp(),
                            burnDamage,
                            fieldA.isOnField(),
                            EffectType.MISSILE_DAMAGE,
                            DamageType.MISSILE,
                            "-" + burnDamage);
                    // 死亡日志
                    if (!deadUnits.isEmpty()) {
                        addMultiTargetLog("UNIT_DEATH",
                                null,
                                0,
                                0,
                                false,
                                deadUnits,
                                null,
                                null,
                                "死亡");
                        //触发死亡技能
                        triggerOnDeathSkills(fieldA);

                    } else {
                        //触发受击技能
                        triggerOnAttackedSkills(fieldA, EffectType.MISSILE_DAMAGE);
                    }
                }
            }

            //            南华真人，魂力飞弹Lv1场下，每当新单位入场时，对场上敌人造成178点飞弹伤害；报复神箭Lv1场下，每回合对场上敌方造成106；
            if (campA.stream().anyMatch(g -> g.getName().equals("西海龙王") && g.getPosition() == position && !g.isDead() && !g.isOnField() && !g.isSilence())) {
                Guardian guardian = campA.stream()
                        .filter(g -> g.getName().equals("西海龙王") && g.getPosition() == position && !g.isDead() && !g.isOnField())
                        .findFirst().get();
                int[] skillLevel = CardSkillLevelUtil.calculateSkillLevels(guardian.getLevel(), guardian.getStar().doubleValue());
                if (fieldB != null && !fieldB.isDead()) {
                    // 1. 计算所有中毒效果的总伤害（累加 POISON 类型的 value）
                    int totalPoisonDamage = 50 * skillLevel[0];
                    // 2. 计算毒抗相关（直接基于你现有 EffectInstance 计算，不新增 Guardian 方法）
                    // 飞弹增益：所有 POISON_RESIST 类型效果的 value 总和
                    int resistUp = calculateTotalVaule(guardian, EffectType.MISSILE_BOOST);
                    // 飞弹增益百分比：所有 POISON_RESIST 类型效果的 value 乘积
                    double resistUpPret = calculateTotalUpPretVaule(guardian, EffectType.MISSILE_BOOST_PRET);
                    // 飞弹降低：所有 POISON_RESIST_DOWN 类型效果的 value 总和
                    int resistDown = calculateTotalVaule(guardian, EffectType.MISSILE_DOWN);
                    // 飞弹降低百分比：所有 POISON_RESIST 类型效果的 value 乘积
                    double resistDownPret = calculateTotalDownPretVaule(guardian, EffectType.MISSILE_DOWN_PRET);


                    // 2. 计算毒抗相关（直接基于你现有 EffectInstance 计算，不新增 Guardian 方法）
                    // 弹抗增益：所有 POISON_RESIST 类型效果的 value 总和
                    int targetUp = calculateTotalVaule(fieldB, EffectType.MISSILE_RESIST_BOOST);
                    // 弹抗增益百分比：所有 POISON_RESIST 类型效果的 value 乘积
                    double targetUpPret = calculateTotalDownPretVaule(fieldB, EffectType.MISSILE_RESIST_BOOST_PRET);
                    // 弹抗降低：所有 POISON_RESIST_DOWN 类型效果的 value 总和
                    int targetDown = calculateTotalVaule(fieldB, EffectType.MISSILE_RESIST_DOWN);
                    // 弹抗降低百分比：所有 POISON_RESIST 类型效果的 value fieldA
                    double targetDownPret = calculateTotalUpPretVaule(fieldB, EffectType.MISSILE_RESIST_DOWN_PRET);
                    // 最终（仅基于 buff 计算，无新增方法）

                    int burnDamage = (int) (totalPoisonDamage * resistUpPret * resistDownPret * targetUpPret * targetDownPret
                            + (resistUp - resistDown + guardian.getFdAtk() - fieldB.getFdDef() - targetUp + targetDown));

                    if (burnDamage < 0) {
                        burnDamage = 0;
                    }

                    // 4. 扣除伤害
                    fieldB.setCurrentHp(fieldB.getCurrentHp() - burnDamage);
                    Map<String, TargetBattleData> deadUnits = new HashMap<>();

                    if (fieldB.getCurrentHp() <= 0) {
                        fieldB.setDead(true);
                        fieldB.setOnField(false);
                        TargetBattleData data = new TargetBattleData(fieldB.getMaxHp(), fieldB.getCurrentHp(), burnDamage, fieldB.isOnField());
                        deadUnits.put(fieldB.getId(), data);
                    }
                    addLog("魂力飞弹",
                            guardian.getId(),
                            guardian.getMaxHp(),
                            guardian.getCurrentHp(),
                            0,
                            guardian.isOnField(),
                            fieldB.getId(),
                            fieldB.getMaxHp(),
                            fieldB.getCurrentHp(),
                            burnDamage,
                            fieldB.isOnField(),
                            EffectType.MISSILE_DAMAGE,
                            DamageType.MISSILE,
                            "-" + burnDamage);
                    // 死亡日志
                    if (!deadUnits.isEmpty()) {
                        addMultiTargetLog("UNIT_DEATH",
                                null,
                                0,
                                0,
                                false,
                                deadUnits,
                                null,
                                null,
                                "死亡");
                        //触发死亡技能
                        triggerOnDeathSkills(fieldB);

                    } else {
                        //触发受击技能
                        triggerOnAttackedSkills(fieldB, EffectType.MISSILE_DAMAGE);
                    }
                }
            }
//            镇元子，魂力飞弹Lv1场下，每当新单位入场时，对场上敌人造成178点飞弹伤害；禁心咒Lv1场下，每当有单位登场，有17%几率令场上英雄沉默2回合；多宝道人同Lv1与多宝道人在同一队伍时，增加自身453点生命上限，158点攻击，158点速度。
            if (campB.stream().anyMatch(g -> g.getName().equals("西海龙王") && g.getPosition() == position && !g.isDead() && !g.isOnField() && !g.isSilence())) {
                Guardian guardian = campB.stream()
                        .filter(g -> g.getName().equals("西海龙王") && g.getPosition() == position && !g.isDead() && !g.isOnField())
                        .findFirst().get();
                int[] skillLevel = CardSkillLevelUtil.calculateSkillLevels(guardian.getLevel(), guardian.getStar().doubleValue());
                if (fieldA != null && !fieldA.isDead()) {
                    // 1. 计算所有中毒效果的总伤害（累加 POISON 类型的 value）
                    int totalPoisonDamage = 50 * skillLevel[0];
                    // 2. 计算毒抗相关（直接基于你现有 EffectInstance 计算，不新增 Guardian 方法）
                    // 飞弹增益：所有 POISON_RESIST 类型效果的 value 总和
                    int resistUp = calculateTotalVaule(guardian, EffectType.MISSILE_BOOST);
                    // 飞弹增益百分比：所有 POISON_RESIST 类型效果的 value 乘积
                    double resistUpPret = calculateTotalUpPretVaule(guardian, EffectType.MISSILE_BOOST_PRET);
                    // 飞弹降低：所有 POISON_RESIST_DOWN 类型效果的 value 总和
                    int resistDown = calculateTotalVaule(guardian, EffectType.MISSILE_DOWN);
                    // 飞弹降低百分比：所有 POISON_RESIST 类型效果的 value 乘积
                    double resistDownPret = calculateTotalDownPretVaule(guardian, EffectType.MISSILE_DOWN_PRET);


                    // 2. 计算毒抗相关（直接基于你现有 EffectInstance 计算，不新增 Guardian 方法）
                    // 弹抗增益：所有 POISON_RESIST 类型效果的 value 总和
                    int targetUp = calculateTotalVaule(fieldA, EffectType.MISSILE_RESIST_BOOST);
                    // 弹抗增益百分比：所有 POISON_RESIST 类型效果的 value 乘积
                    double targetUpPret = calculateTotalDownPretVaule(fieldA, EffectType.MISSILE_RESIST_BOOST_PRET);
                    // 弹抗降低：所有 POISON_RESIST_DOWN 类型效果的 value 总和
                    int targetDown = calculateTotalVaule(fieldA, EffectType.MISSILE_RESIST_DOWN);
                    // 弹抗降低百分比：所有 POISON_RESIST 类型效果的 value fieldA
                    double targetDownPret = calculateTotalUpPretVaule(fieldA, EffectType.MISSILE_RESIST_DOWN_PRET);
                    // 最终（仅基于 buff 计算，无新增方法）

                    int burnDamage = (int) (totalPoisonDamage * resistUpPret * resistDownPret * targetUpPret * targetDownPret
                            + (resistUp - resistDown + guardian.getFdAtk() - fieldA.getFdDef() - targetUp + targetDown));

                    if (burnDamage < 0) {
                        burnDamage = 0;
                    }

                    // 4. 扣除伤害
                    fieldA.setCurrentHp(fieldA.getCurrentHp() - burnDamage);
                    Map<String, TargetBattleData> deadUnits = new HashMap<>();

                    if (fieldA.getCurrentHp() <= 0) {
                        fieldA.setDead(true);
                        fieldA.setOnField(false);
                        TargetBattleData data = new TargetBattleData(fieldA.getMaxHp(), fieldA.getCurrentHp(), burnDamage, fieldA.isOnField());
                        deadUnits.put(fieldA.getId(), data);
                    }
                    addLog("魂力飞弹",
                            guardian.getId(),
                            guardian.getMaxHp(),
                            guardian.getCurrentHp(),
                            0,
                            guardian.isOnField(),
                            fieldA.getId(),
                            fieldA.getMaxHp(),
                            fieldA.getCurrentHp(),
                            burnDamage,
                            fieldA.isOnField(),
                            EffectType.MISSILE_DAMAGE,
                            DamageType.MISSILE,
                            "-" + burnDamage);
                    // 死亡日志
                    if (!deadUnits.isEmpty()) {
                        addMultiTargetLog("UNIT_DEATH",
                                null,
                                0,
                                0,
                                false,
                                deadUnits,
                                null,
                                null,
                                "死亡");
                        //触发死亡技能
                        triggerOnDeathSkills(fieldA);

                    } else {
                        //触发受击技能
                        triggerOnAttackedSkills(fieldA, EffectType.MISSILE_DAMAGE);
                    }
                }
            }

            //            南华真人，魂力飞弹Lv1场下，每当新单位入场时，对场上敌人造成178点飞弹伤害；报复神箭Lv1场下，每回合对场上敌方造成106；
            if (campA.stream().anyMatch(g -> g.getName().equals("东海龙王") && g.getPosition() == position && !g.isDead() && !g.isOnField() && !g.isSilence())) {
                Guardian guardian = campA.stream()
                        .filter(g -> g.getName().equals("东海龙王") && g.getPosition() == position && !g.isDead() && !g.isOnField())
                        .findFirst().get();
                int[] skillLevel = CardSkillLevelUtil.calculateSkillLevels(guardian.getLevel(), guardian.getStar().doubleValue());
                if (fieldB != null && !fieldB.isDead()) {
                    // 1. 计算所有中毒效果的总伤害（累加 POISON 类型的 value）
                    int totalPoisonDamage = 50 * skillLevel[0];
                    // 2. 计算毒抗相关（直接基于你现有 EffectInstance 计算，不新增 Guardian 方法）
                    // 飞弹增益：所有 POISON_RESIST 类型效果的 value 总和
                    int resistUp = calculateTotalVaule(guardian, EffectType.MISSILE_BOOST);
                    // 飞弹增益百分比：所有 POISON_RESIST 类型效果的 value 乘积
                    double resistUpPret = calculateTotalUpPretVaule(guardian, EffectType.MISSILE_BOOST_PRET);
                    // 飞弹降低：所有 POISON_RESIST_DOWN 类型效果的 value 总和
                    int resistDown = calculateTotalVaule(guardian, EffectType.MISSILE_DOWN);
                    // 飞弹降低百分比：所有 POISON_RESIST 类型效果的 value 乘积
                    double resistDownPret = calculateTotalDownPretVaule(guardian, EffectType.MISSILE_DOWN_PRET);


                    // 2. 计算毒抗相关（直接基于你现有 EffectInstance 计算，不新增 Guardian 方法）
                    // 弹抗增益：所有 POISON_RESIST 类型效果的 value 总和
                    int targetUp = calculateTotalVaule(fieldB, EffectType.MISSILE_RESIST_BOOST);
                    // 弹抗增益百分比：所有 POISON_RESIST 类型效果的 value 乘积
                    double targetUpPret = calculateTotalDownPretVaule(fieldB, EffectType.MISSILE_RESIST_BOOST_PRET);
                    // 弹抗降低：所有 POISON_RESIST_DOWN 类型效果的 value 总和
                    int targetDown = calculateTotalVaule(fieldB, EffectType.MISSILE_RESIST_DOWN);
                    // 弹抗降低百分比：所有 POISON_RESIST 类型效果的 value fieldA
                    double targetDownPret = calculateTotalUpPretVaule(fieldB, EffectType.MISSILE_RESIST_DOWN_PRET);
                    // 最终（仅基于 buff 计算，无新增方法）

                    int burnDamage = (int) (totalPoisonDamage * resistUpPret * resistDownPret * targetUpPret * targetDownPret
                            + (resistUp - resistDown + guardian.getFdAtk() - fieldB.getFdDef() - targetUp + targetDown));

                    if (burnDamage < 0) {
                        burnDamage = 0;
                    }

                    // 4. 扣除伤害
                    fieldB.setCurrentHp(fieldB.getCurrentHp() - burnDamage);
                    Map<String, TargetBattleData> deadUnits = new HashMap<>();

                    if (fieldB.getCurrentHp() <= 0) {
                        fieldB.setDead(true);
                        fieldB.setOnField(false);
                        TargetBattleData data = new TargetBattleData(fieldB.getMaxHp(), fieldB.getCurrentHp(), burnDamage, fieldB.isOnField());
                        deadUnits.put(fieldB.getId(), data);
                    }
                    addLog("魂力飞弹",
                            guardian.getId(),
                            guardian.getMaxHp(),
                            guardian.getCurrentHp(),
                            0,
                            guardian.isOnField(),
                            fieldB.getId(),
                            fieldB.getMaxHp(),
                            fieldB.getCurrentHp(),
                            burnDamage,
                            fieldB.isOnField(),
                            EffectType.MISSILE_DAMAGE,
                            DamageType.MISSILE,
                            "-" + burnDamage);
                    // 死亡日志
                    if (!deadUnits.isEmpty()) {
                        addMultiTargetLog("UNIT_DEATH",
                                null,
                                0,
                                0,
                                false,
                                deadUnits,
                                null,
                                null,
                                "死亡");
                        //触发死亡技能
                        triggerOnDeathSkills(fieldB);

                    } else {
                        //触发受击技能
                        triggerOnAttackedSkills(fieldB, EffectType.MISSILE_DAMAGE);
                    }
                }
            }
//            镇元子，魂力飞弹Lv1场下，每当新单位入场时，对场上敌人造成178点飞弹伤害；禁心咒Lv1场下，每当有单位登场，有17%几率令场上英雄沉默2回合；多宝道人同Lv1与多宝道人在同一队伍时，增加自身453点生命上限，158点攻击，158点速度。
            if (campB.stream().anyMatch(g -> g.getName().equals("东海龙王") && g.getPosition() == position && !g.isDead() && !g.isOnField() && !g.isSilence())) {
                Guardian guardian = campB.stream()
                        .filter(g -> g.getName().equals("东海龙王") && g.getPosition() == position && !g.isDead() && !g.isOnField())
                        .findFirst().get();
                int[] skillLevel = CardSkillLevelUtil.calculateSkillLevels(guardian.getLevel(), guardian.getStar().doubleValue());
                if (fieldA != null && !fieldA.isDead()) {
                    // 1. 计算所有中毒效果的总伤害（累加 POISON 类型的 value）
                    int totalPoisonDamage = 50 * skillLevel[0];
                    // 2. 计算毒抗相关（直接基于你现有 EffectInstance 计算，不新增 Guardian 方法）
                    // 飞弹增益：所有 POISON_RESIST 类型效果的 value 总和
                    int resistUp = calculateTotalVaule(guardian, EffectType.MISSILE_BOOST);
                    // 飞弹增益百分比：所有 POISON_RESIST 类型效果的 value 乘积
                    double resistUpPret = calculateTotalUpPretVaule(guardian, EffectType.MISSILE_BOOST_PRET);
                    // 飞弹降低：所有 POISON_RESIST_DOWN 类型效果的 value 总和
                    int resistDown = calculateTotalVaule(guardian, EffectType.MISSILE_DOWN);
                    // 飞弹降低百分比：所有 POISON_RESIST 类型效果的 value 乘积
                    double resistDownPret = calculateTotalDownPretVaule(guardian, EffectType.MISSILE_DOWN_PRET);


                    // 2. 计算毒抗相关（直接基于你现有 EffectInstance 计算，不新增 Guardian 方法）
                    // 弹抗增益：所有 POISON_RESIST 类型效果的 value 总和
                    int targetUp = calculateTotalVaule(fieldA, EffectType.MISSILE_RESIST_BOOST);
                    // 弹抗增益百分比：所有 POISON_RESIST 类型效果的 value 乘积
                    double targetUpPret = calculateTotalDownPretVaule(fieldA, EffectType.MISSILE_RESIST_BOOST_PRET);
                    // 弹抗降低：所有 POISON_RESIST_DOWN 类型效果的 value 总和
                    int targetDown = calculateTotalVaule(fieldA, EffectType.MISSILE_RESIST_DOWN);
                    // 弹抗降低百分比：所有 POISON_RESIST 类型效果的 value fieldA
                    double targetDownPret = calculateTotalUpPretVaule(fieldA, EffectType.MISSILE_RESIST_DOWN_PRET);
                    // 最终（仅基于 buff 计算，无新增方法）

                    int burnDamage = (int) (totalPoisonDamage * resistUpPret * resistDownPret * targetUpPret * targetDownPret
                            + (resistUp - resistDown + guardian.getFdAtk() - fieldA.getFdDef() - targetUp + targetDown));

                    if (burnDamage < 0) {
                        burnDamage = 0;
                    }

                    // 4. 扣除伤害
                    fieldA.setCurrentHp(fieldA.getCurrentHp() - burnDamage);
                    Map<String, TargetBattleData> deadUnits = new HashMap<>();

                    if (fieldA.getCurrentHp() <= 0) {
                        fieldA.setDead(true);
                        fieldA.setOnField(false);
                        TargetBattleData data = new TargetBattleData(fieldA.getMaxHp(), fieldA.getCurrentHp(), burnDamage, fieldA.isOnField());
                        deadUnits.put(fieldA.getId(), data);
                    }
                    addLog("魂力飞弹",
                            guardian.getId(),
                            guardian.getMaxHp(),
                            guardian.getCurrentHp(),
                            0,
                            guardian.isOnField(),
                            fieldA.getId(),
                            fieldA.getMaxHp(),
                            fieldA.getCurrentHp(),
                            burnDamage,
                            fieldA.isOnField(),
                            EffectType.MISSILE_DAMAGE,
                            DamageType.MISSILE,
                            "-" + burnDamage);
                    // 死亡日志
                    if (!deadUnits.isEmpty()) {
                        addMultiTargetLog("UNIT_DEATH",
                                null,
                                0,
                                0,
                                false,
                                deadUnits,
                                null,
                                null,
                                "死亡");
                        //触发死亡技能
                        triggerOnDeathSkills(fieldA);

                    } else {
                        //触发受击技能
                        triggerOnAttackedSkills(fieldA, EffectType.MISSILE_DAMAGE);
                    }
                }
            }

            //            南华真人，魂力飞弹Lv1场下，每当新单位入场时，对场上敌人造成178点飞弹伤害；报复神箭Lv1场下，每回合对场上敌方造成106；
            if (campA.stream().anyMatch(g -> g.getName().equals("南海龙王") && g.getPosition() == position && !g.isDead() && !g.isOnField() && !g.isSilence())) {
                Guardian guardian = campA.stream()
                        .filter(g -> g.getName().equals("南海龙王") && g.getPosition() == position && !g.isDead() && !g.isOnField())
                        .findFirst().get();
                int[] skillLevel = CardSkillLevelUtil.calculateSkillLevels(guardian.getLevel(), guardian.getStar().doubleValue());
                if (fieldB != null && !fieldB.isDead()) {
                    // 1. 计算所有中毒效果的总伤害（累加 POISON 类型的 value）
                    int totalPoisonDamage = 50 * skillLevel[0];
                    // 2. 计算毒抗相关（直接基于你现有 EffectInstance 计算，不新增 Guardian 方法）
                    // 飞弹增益：所有 POISON_RESIST 类型效果的 value 总和
                    int resistUp = calculateTotalVaule(guardian, EffectType.MISSILE_BOOST);
                    // 飞弹增益百分比：所有 POISON_RESIST 类型效果的 value 乘积
                    double resistUpPret = calculateTotalUpPretVaule(guardian, EffectType.MISSILE_BOOST_PRET);
                    // 飞弹降低：所有 POISON_RESIST_DOWN 类型效果的 value 总和
                    int resistDown = calculateTotalVaule(guardian, EffectType.MISSILE_DOWN);
                    // 飞弹降低百分比：所有 POISON_RESIST 类型效果的 value 乘积
                    double resistDownPret = calculateTotalDownPretVaule(guardian, EffectType.MISSILE_DOWN_PRET);


                    // 2. 计算毒抗相关（直接基于你现有 EffectInstance 计算，不新增 Guardian 方法）
                    // 弹抗增益：所有 POISON_RESIST 类型效果的 value 总和
                    int targetUp = calculateTotalVaule(fieldB, EffectType.MISSILE_RESIST_BOOST);
                    // 弹抗增益百分比：所有 POISON_RESIST 类型效果的 value 乘积
                    double targetUpPret = calculateTotalDownPretVaule(fieldB, EffectType.MISSILE_RESIST_BOOST_PRET);
                    // 弹抗降低：所有 POISON_RESIST_DOWN 类型效果的 value 总和
                    int targetDown = calculateTotalVaule(fieldB, EffectType.MISSILE_RESIST_DOWN);
                    // 弹抗降低百分比：所有 POISON_RESIST 类型效果的 value 乘积
                    double targetDownPret = calculateTotalUpPretVaule(fieldB, EffectType.MISSILE_RESIST_DOWN_PRET);
                    // 最终（仅基于 buff 计算，无新增方法）

                    int burnDamage = (int) (totalPoisonDamage * resistUpPret * resistDownPret * targetUpPret * targetDownPret
                            + (resistUp - resistDown + guardian.getFdAtk() - fieldB.getFdDef() - targetUp + targetDown));

                    if (burnDamage < 0) {
                        burnDamage = 0;
                    }

                    // 4. 扣除伤害
                    fieldB.setCurrentHp(fieldB.getCurrentHp() - burnDamage);
                    Map<String, TargetBattleData> deadUnits = new HashMap<>();

                    if (fieldB.getCurrentHp() <= 0) {
                        fieldB.setDead(true);
                        fieldB.setOnField(false);
                        TargetBattleData data = new TargetBattleData(fieldB.getMaxHp(), fieldB.getCurrentHp(), burnDamage, fieldB.isOnField());
                        deadUnits.put(fieldB.getId(), data);
                    }
                    addLog("魂力飞弹",
                            guardian.getId(),
                            guardian.getMaxHp(),
                            guardian.getCurrentHp(),
                            0,
                            guardian.isOnField(),
                            fieldB.getId(),
                            fieldB.getMaxHp(),
                            fieldB.getCurrentHp(),
                            burnDamage,
                            fieldB.isOnField(),
                            EffectType.MISSILE_DAMAGE,
                            DamageType.MISSILE,
                            "-" + burnDamage);
                    // 死亡日志
                    if (!deadUnits.isEmpty()) {
                        addMultiTargetLog("UNIT_DEATH",
                                null,
                                0,
                                0,
                                false,
                                deadUnits,
                                null,
                                null,
                                "死亡");
                        //触发死亡技能
                        triggerOnDeathSkills(fieldB);

                    } else {
                        //触发受击技能
                        triggerOnAttackedSkills(fieldB, EffectType.MISSILE_DAMAGE);
                    }
                }
            }
//            镇元子，魂力飞弹Lv1场下，每当新单位入场时，对场上敌人造成178点飞弹伤害；禁心咒Lv1场下，每当有单位登场，有17%几率令场上英雄沉默2回合；多宝道人同Lv1与多宝道人在同一队伍时，增加自身453点生命上限，158点攻击，158点速度。
            if (campB.stream().anyMatch(g -> g.getName().equals("南海龙王") && g.getPosition() == position && !g.isDead() && !g.isOnField() && !g.isSilence())) {
                Guardian guardian = campB.stream()
                        .filter(g -> g.getName().equals("南海龙王") && g.getPosition() == position && !g.isDead() && !g.isOnField())
                        .findFirst().get();
                int[] skillLevel = CardSkillLevelUtil.calculateSkillLevels(guardian.getLevel(), guardian.getStar().doubleValue());
                if (fieldA != null && !fieldA.isDead()) {
                    // 1. 计算所有中毒效果的总伤害（累加 POISON 类型的 value）
                    int totalPoisonDamage = 50 * skillLevel[0];
                    // 2. 计算毒抗相关（直接基于你现有 EffectInstance 计算，不新增 Guardian 方法）
                    // 飞弹增益：所有 POISON_RESIST 类型效果的 value 总和
                    int resistUp = calculateTotalVaule(guardian, EffectType.MISSILE_BOOST);
                    // 飞弹增益百分比：所有 POISON_RESIST 类型效果的 value 乘积
                    double resistUpPret = calculateTotalUpPretVaule(guardian, EffectType.MISSILE_BOOST_PRET);
                    // 飞弹降低：所有 POISON_RESIST_DOWN 类型效果的 value 总和
                    int resistDown = calculateTotalVaule(guardian, EffectType.MISSILE_DOWN);
                    // 飞弹降低百分比：所有 POISON_RESIST 类型效果的 value 乘积
                    double resistDownPret = calculateTotalDownPretVaule(guardian, EffectType.MISSILE_DOWN_PRET);


                    // 2. 计算毒抗相关（直接基于你现有 EffectInstance 计算，不新增 Guardian 方法）
                    // 弹抗增益：所有 POISON_RESIST 类型效果的 value 总和
                    int targetUp = calculateTotalVaule(fieldA, EffectType.MISSILE_RESIST_BOOST);
                    // 弹抗增益百分比：所有 POISON_RESIST 类型效果的 value 乘积
                    double targetUpPret = calculateTotalDownPretVaule(fieldA, EffectType.MISSILE_RESIST_BOOST_PRET);
                    // 弹抗降低：所有 POISON_RESIST_DOWN 类型效果的 value 总和
                    int targetDown = calculateTotalVaule(fieldA, EffectType.MISSILE_RESIST_DOWN);
                    // 弹抗降低百分比：所有 POISON_RESIST 类型效果的 value 乘积
                    double targetDownPret = calculateTotalUpPretVaule(fieldA, EffectType.MISSILE_RESIST_DOWN_PRET);
                    // 最终（仅基于 buff 计算，无新增方法）

                    int burnDamage = (int) (totalPoisonDamage * resistUpPret * resistDownPret * targetUpPret * targetDownPret
                            + (resistUp - resistDown + guardian.getFdAtk() - fieldA.getFdDef() - targetUp + targetDown));

                    if (burnDamage < 0) {
                        burnDamage = 0;
                    }

                    // 4. 扣除伤害
                    fieldA.setCurrentHp(fieldA.getCurrentHp() - burnDamage);
                    Map<String, TargetBattleData> deadUnits = new HashMap<>();

                    if (fieldA.getCurrentHp() <= 0) {
                        fieldA.setDead(true);
                        fieldA.setOnField(false);
                        TargetBattleData data = new TargetBattleData(fieldA.getMaxHp(), fieldA.getCurrentHp(), burnDamage, fieldA.isOnField());
                        deadUnits.put(fieldA.getId(), data);
                    }
                    addLog("魂力飞弹",
                            guardian.getId(),
                            guardian.getMaxHp(),
                            guardian.getCurrentHp(),
                            0,
                            guardian.isOnField(),
                            fieldA.getId(),
                            fieldA.getMaxHp(),
                            fieldA.getCurrentHp(),
                            burnDamage,
                            fieldA.isOnField(),
                            EffectType.MISSILE_DAMAGE,
                            DamageType.MISSILE,
                            "-" + burnDamage);
                    // 死亡日志
                    if (!deadUnits.isEmpty()) {
                        addMultiTargetLog("UNIT_DEATH",
                                null,
                                0,
                                0,
                                false,
                                deadUnits,
                                null,
                                null,
                                "死亡");
                        //触发死亡技能
                        triggerOnDeathSkills(fieldA);

                    } else {
                        //触发受击技能
                        triggerOnAttackedSkills(fieldA, EffectType.MISSILE_DAMAGE);
                    }
                }
            }

            //            南华真人，魂力飞弹Lv1场下，每当新单位入场时，对场上敌人造成178点飞弹伤害；报复神箭Lv1场下，每回合对场上敌方造成106；
            if (campA.stream().anyMatch(g -> g.getName().equals("北海龙王") && g.getPosition() == position && !g.isDead() && !g.isOnField() && !g.isSilence())) {
                Guardian guardian = campA.stream()
                        .filter(g -> g.getName().equals("北海龙王") && g.getPosition() == position && !g.isDead() && !g.isOnField())
                        .findFirst().get();
                int[] skillLevel = CardSkillLevelUtil.calculateSkillLevels(guardian.getLevel(), guardian.getStar().doubleValue());
                if (fieldB != null && !fieldB.isDead()) {
// 1. 计算所有中毒效果的总伤害（累加 POISON 类型的 value）
                    int totalPoisonDamage = 50 * skillLevel[0];
                    // 2. 计算毒抗相关（直接基于你现有 EffectInstance 计算，不新增 Guardian 方法）
                    // 飞弹增益：所有 POISON_RESIST 类型效果的 value 总和
                    int resistUp = calculateTotalVaule(guardian, EffectType.MISSILE_BOOST);
                    // 飞弹增益百分比：所有 POISON_RESIST 类型效果的 value 乘积
                    double resistUpPret = calculateTotalUpPretVaule(guardian, EffectType.MISSILE_BOOST_PRET);
                    // 飞弹降低：所有 POISON_RESIST_DOWN 类型效果的 value 总和
                    int resistDown = calculateTotalVaule(guardian, EffectType.MISSILE_DOWN);
                    // 飞弹降低百分比：所有 POISON_RESIST 类型效果的 value 乘积
                    double resistDownPret = calculateTotalDownPretVaule(guardian, EffectType.MISSILE_DOWN_PRET);


                    // 2. 计算毒抗相关（直接基于你现有 EffectInstance 计算，不新增 Guardian 方法）
                    // 弹抗增益：所有 POISON_RESIST 类型效果的 value 总和
                    int targetUp = calculateTotalVaule(fieldB, EffectType.MISSILE_RESIST_BOOST);
                    // 弹抗增益百分比：所有 POISON_RESIST 类型效果的 value 乘积
                    double targetUpPret = calculateTotalDownPretVaule(fieldB, EffectType.MISSILE_RESIST_BOOST_PRET);
                    // 弹抗降低：所有 POISON_RESIST_DOWN 类型效果的 value 总和
                    int targetDown = calculateTotalVaule(fieldB, EffectType.MISSILE_RESIST_DOWN);
                    // 弹抗降低百分比：所有 POISON_RESIST 类型效果的 value 乘积
                    double targetDownPret = calculateTotalUpPretVaule(fieldB, EffectType.MISSILE_RESIST_DOWN_PRET);
                    // 最终（仅基于 buff 计算，无新增方法）

                    int burnDamage = (int) (totalPoisonDamage * resistUpPret * resistDownPret * targetUpPret * targetDownPret
                            + (resistUp - resistDown + guardian.getFdAtk() - fieldB.getFdDef() - targetUp + targetDown));

                    if (burnDamage < 0) {
                        burnDamage = 0;
                    }

                    // 4. 扣除伤害
                    fieldB.setCurrentHp(fieldB.getCurrentHp() - burnDamage);
                    Map<String, TargetBattleData> deadUnits = new HashMap<>();

                    if (fieldB.getCurrentHp() <= 0) {
                        fieldB.setDead(true);
                        fieldB.setOnField(false);
                        TargetBattleData data = new TargetBattleData(fieldB.getMaxHp(), fieldB.getCurrentHp(), burnDamage, fieldB.isOnField());
                        deadUnits.put(fieldB.getId(), data);
                    }
                    addLog("魂力飞弹",
                            guardian.getId(),
                            guardian.getMaxHp(),
                            guardian.getCurrentHp(),
                            0,
                            guardian.isOnField(),
                            fieldB.getId(),
                            fieldB.getMaxHp(),
                            fieldB.getCurrentHp(),
                            burnDamage,
                            fieldB.isOnField(),
                            EffectType.MISSILE_DAMAGE,
                            DamageType.MISSILE,
                            "-" + burnDamage);
                    // 死亡日志
                    if (!deadUnits.isEmpty()) {
                        addMultiTargetLog("UNIT_DEATH",
                                null,
                                0,
                                0,
                                false,
                                deadUnits,
                                null,
                                null,
                                "死亡");
                        //触发死亡技能
                        triggerOnDeathSkills(fieldB);

                    } else {
                        //触发受击技能
                        triggerOnAttackedSkills(fieldB, EffectType.MISSILE_DAMAGE);
                    }
                }
            }
//            镇元子，魂力飞弹Lv1场下，每当新单位入场时，对场上敌人造成178点飞弹伤害；禁心咒Lv1场下，每当有单位登场，有17%几率令场上英雄沉默2回合；多宝道人同Lv1与多宝道人在同一队伍时，增加自身453点生命上限，158点攻击，158点速度。
            if (campB.stream().anyMatch(g -> g.getName().equals("北海龙王") && g.getPosition() == position && !g.isDead() && !g.isOnField() && !g.isSilence())) {
                Guardian guardian = campB.stream()
                        .filter(g -> g.getName().equals("北海龙王") && g.getPosition() == position && !g.isDead() && !g.isOnField())
                        .findFirst().get();
                int[] skillLevel = CardSkillLevelUtil.calculateSkillLevels(guardian.getLevel(), guardian.getStar().doubleValue());
                if (fieldA != null && !fieldA.isDead()) {
                    // 1. 计算所有中毒效果的总伤害（累加 POISON 类型的 value）
                    int totalPoisonDamage = 50 * skillLevel[0];
                    // 2. 计算毒抗相关（直接基于你现有 EffectInstance 计算，不新增 Guardian 方法）
                    // 飞弹增益：所有 POISON_RESIST 类型效果的 value 总和
                    int resistUp = calculateTotalVaule(guardian, EffectType.MISSILE_BOOST);
                    // 飞弹增益百分比：所有 POISON_RESIST 类型效果的 value 乘积
                    double resistUpPret = calculateTotalUpPretVaule(guardian, EffectType.MISSILE_BOOST_PRET);
                    // 飞弹降低：所有 POISON_RESIST_DOWN 类型效果的 value 总和
                    int resistDown = calculateTotalVaule(guardian, EffectType.MISSILE_DOWN);
                    // 飞弹降低百分比：所有 POISON_RESIST 类型效果的 value 乘积
                    double resistDownPret = calculateTotalDownPretVaule(guardian, EffectType.MISSILE_DOWN_PRET);


                    // 2. 计算毒抗相关（直接基于你现有 EffectInstance 计算，不新增 Guardian 方法）
                    // 弹抗增益：所有 POISON_RESIST 类型效果的 value 总和
                    int targetUp = calculateTotalVaule(fieldA, EffectType.MISSILE_RESIST_BOOST);
                    // 弹抗增益百分比：所有 POISON_RESIST 类型效果的 value 乘积
                    double targetUpPret = calculateTotalDownPretVaule(fieldA, EffectType.MISSILE_RESIST_BOOST_PRET);
                    // 弹抗降低：所有 POISON_RESIST_DOWN 类型效果的 value 总和
                    int targetDown = calculateTotalVaule(fieldA, EffectType.MISSILE_RESIST_DOWN);
                    // 弹抗降低百分比：所有 POISON_RESIST 类型效果的 value 乘积
                    double targetDownPret = calculateTotalUpPretVaule(fieldA, EffectType.MISSILE_RESIST_DOWN_PRET);
                    // 最终（仅基于 buff 计算，无新增方法）

                    int burnDamage = (int) (totalPoisonDamage * resistUpPret * resistDownPret * targetUpPret * targetDownPret
                            + (resistUp - resistDown + guardian.getFdAtk() - fieldA.getFdDef() - targetUp + targetDown));

                    if (burnDamage < 0) {
                        burnDamage = 0;
                    }

                    // 4. 扣除伤害
                    fieldA.setCurrentHp(fieldA.getCurrentHp() - burnDamage);
                    Map<String, TargetBattleData> deadUnits = new HashMap<>();

                    if (fieldA.getCurrentHp() <= 0) {
                        fieldA.setDead(true);
                        fieldA.setOnField(false);
                        TargetBattleData data = new TargetBattleData(fieldA.getMaxHp(), fieldA.getCurrentHp(), burnDamage, fieldA.isOnField());
                        deadUnits.put(fieldA.getId(), data);
                    }
                    addLog("魂力飞弹",
                            guardian.getId(),
                            guardian.getMaxHp(),
                            guardian.getCurrentHp(),
                            0,
                            guardian.isOnField(),
                            fieldA.getId(),
                            fieldA.getMaxHp(),
                            fieldA.getCurrentHp(),
                            burnDamage,
                            fieldA.isOnField(),
                            EffectType.MISSILE_DAMAGE,
                            DamageType.MISSILE,
                            "-" + burnDamage);
                    // 死亡日志
                    if (!deadUnits.isEmpty()) {
                        addMultiTargetLog("UNIT_DEATH",
                                null,
                                0,
                                0,
                                false,
                                deadUnits,
                                null,
                                null,
                                "死亡");
                        //触发死亡技能
                        triggerOnDeathSkills(fieldA);

                    } else {
                        //触发受击技能
                        triggerOnAttackedSkills(fieldA, EffectType.MISSILE_DAMAGE);
                    }
                }
            }

            //            南华真人，魂力飞弹Lv1场下，每当新单位入场时，对场上敌人造成178点飞弹伤害；报复神箭Lv1场下，每回合对场上敌方造成106；
            if (campA.stream().anyMatch(g -> g.getName().equals("白鹤童子") && g.getPosition() == position && !g.isDead() && !g.isOnField() && !g.isSilence())) {
                Guardian guardian = campA.stream()
                        .filter(g -> g.getName().equals("白鹤童子") && g.getPosition() == position && !g.isDead() && !g.isOnField())
                        .findFirst().get();
                int[] skillLevel = CardSkillLevelUtil.calculateSkillLevels(guardian.getLevel(), guardian.getStar().doubleValue());
                if (fieldB != null && !fieldB.isDead()) {
                    // 1. 计算所有中毒效果的总伤害（累加 POISON 类型的 value）
                    int totalPoisonDamage = 30 * skillLevel[0];
                    // 2. 计算毒抗相关（直接基于你现有 EffectInstance 计算，不新增 Guardian 方法）
                    // 飞弹增益：所有 POISON_RESIST 类型效果的 value 总和
                    int resistUp = calculateTotalVaule(guardian, EffectType.MISSILE_BOOST);
                    // 飞弹增益百分比：所有 POISON_RESIST 类型效果的 value 乘积
                    double resistUpPret = calculateTotalUpPretVaule(guardian, EffectType.MISSILE_BOOST_PRET);
                    // 飞弹降低：所有 POISON_RESIST_DOWN 类型效果的 value 总和
                    int resistDown = calculateTotalVaule(guardian, EffectType.MISSILE_DOWN);
                    // 飞弹降低百分比：所有 POISON_RESIST 类型效果的 value 乘积
                    double resistDownPret = calculateTotalDownPretVaule(guardian, EffectType.MISSILE_DOWN_PRET);


                    // 2. 计算毒抗相关（直接基于你现有 EffectInstance 计算，不新增 Guardian 方法）
                    // 弹抗增益：所有 POISON_RESIST 类型效果的 value 总和
                    int targetUp = calculateTotalVaule(fieldB, EffectType.MISSILE_RESIST_BOOST);
                    // 弹抗增益百分比：所有 POISON_RESIST 类型效果的 value 乘积
                    double targetUpPret = calculateTotalDownPretVaule(fieldB, EffectType.MISSILE_RESIST_BOOST_PRET);
                    // 弹抗降低：所有 POISON_RESIST_DOWN 类型效果的 value 总和
                    int targetDown = calculateTotalVaule(fieldB, EffectType.MISSILE_RESIST_DOWN);
                    // 弹抗降低百分比：所有 POISON_RESIST 类型效果的 value 乘积
                    double targetDownPret = calculateTotalUpPretVaule(fieldB, EffectType.MISSILE_RESIST_DOWN_PRET);
                    // 最终（仅基于 buff 计算，无新增方法）

                    int burnDamage = (int) (totalPoisonDamage * resistUpPret * resistDownPret * targetUpPret * targetDownPret
                            + (resistUp - resistDown + guardian.getFdAtk() - fieldB.getFdDef() - targetUp + targetDown));

                    if (burnDamage < 0) {
                        burnDamage = 0;
                    }

                    // 4. 扣除伤害
                    fieldB.setCurrentHp(fieldB.getCurrentHp() - burnDamage);
                    Map<String, TargetBattleData> deadUnits = new HashMap<>();

                    if (fieldB.getCurrentHp() <= 0) {
                        fieldB.setDead(true);
                        fieldB.setOnField(false);
                        TargetBattleData data = new TargetBattleData(fieldB.getMaxHp(), fieldB.getCurrentHp(), burnDamage, fieldB.isOnField());
                        deadUnits.put(fieldB.getId(), data);
                    }
                    addLog("魂力飞弹",
                            guardian.getId(),
                            guardian.getMaxHp(),
                            guardian.getCurrentHp(),
                            0,
                            guardian.isOnField(),
                            fieldB.getId(),
                            fieldB.getMaxHp(),
                            fieldB.getCurrentHp(),
                            burnDamage,
                            fieldB.isOnField(),
                            EffectType.MISSILE_DAMAGE,
                            DamageType.MISSILE,
                            "-" + burnDamage);
                    // 死亡日志
                    if (!deadUnits.isEmpty()) {
                        addMultiTargetLog("UNIT_DEATH",
                                null,
                                0,
                                0,
                                false,
                                deadUnits,
                                null,
                                null,
                                "死亡");
                        //触发死亡技能
                        triggerOnDeathSkills(fieldB);

                    } else {
                        //触发受击技能
                        triggerOnAttackedSkills(fieldB, EffectType.MISSILE_DAMAGE);
                    }
                }
            }
//            镇元子，魂力飞弹Lv1场下，每当新单位入场时，对场上敌人造成178点飞弹伤害；禁心咒Lv1场下，每当有单位登场，有17%几率令场上英雄沉默2回合；多宝道人同Lv1与多宝道人在同一队伍时，增加自身453点生命上限，158点攻击，158点速度。
            if (campB.stream().anyMatch(g -> g.getName().equals("白鹤童子") && g.getPosition() == position && !g.isDead() && !g.isOnField() && !g.isSilence())) {
                Guardian guardian = campB.stream()
                        .filter(g -> g.getName().equals("白鹤童子") && g.getPosition() == position && !g.isDead() && !g.isOnField())
                        .findFirst().get();
                int[] skillLevel = CardSkillLevelUtil.calculateSkillLevels(guardian.getLevel(), guardian.getStar().doubleValue());
                if (fieldA != null && !fieldA.isDead()) {
                    // 1. 计算所有中毒效果的总伤害（累加 POISON 类型的 value）
                    int totalPoisonDamage = 30 * skillLevel[0];
                    // 2. 计算毒抗相关（直接基于你现有 EffectInstance 计算，不新增 Guardian 方法）
                    // 飞弹增益：所有 POISON_RESIST 类型效果的 value 总和
                    int resistUp = calculateTotalVaule(guardian, EffectType.MISSILE_BOOST);
                    // 飞弹增益百分比：所有 POISON_RESIST 类型效果的 value 乘积
                    double resistUpPret = calculateTotalUpPretVaule(guardian, EffectType.MISSILE_BOOST_PRET);
                    // 飞弹降低：所有 POISON_RESIST_DOWN 类型效果的 value 总和
                    int resistDown = calculateTotalVaule(guardian, EffectType.MISSILE_DOWN);
                    // 飞弹降低百分比：所有 POISON_RESIST 类型效果的 value 乘积
                    double resistDownPret = calculateTotalDownPretVaule(guardian, EffectType.MISSILE_DOWN_PRET);


                    // 2. 计算毒抗相关（直接基于你现有 EffectInstance 计算，不新增 Guardian 方法）
                    // 弹抗增益：所有 POISON_RESIST 类型效果的 value 总和
                    int targetUp = calculateTotalVaule(fieldA, EffectType.MISSILE_RESIST_BOOST);
                    // 弹抗增益百分比：所有 POISON_RESIST 类型效果的 value 乘积
                    double targetUpPret = calculateTotalDownPretVaule(fieldA, EffectType.MISSILE_RESIST_BOOST_PRET);
                    // 弹抗降低：所有 POISON_RESIST_DOWN 类型效果的 value 总和
                    int targetDown = calculateTotalVaule(fieldA, EffectType.MISSILE_RESIST_DOWN);
                    // 弹抗降低百分比：所有 POISON_RESIST 类型效果的 value 乘积
                    double targetDownPret = calculateTotalUpPretVaule(fieldA, EffectType.MISSILE_RESIST_DOWN_PRET);
                    // 最终（仅基于 buff 计算，无新增方法）

                    int burnDamage = (int) (totalPoisonDamage * resistUpPret * resistDownPret * targetUpPret * targetDownPret
                            + (resistUp - resistDown + guardian.getFdAtk() - fieldA.getFdDef() - targetUp + targetDown));

                    if (burnDamage < 0) {
                        burnDamage = 0;
                    }

                    // 4. 扣除伤害
                    fieldA.setCurrentHp(fieldA.getCurrentHp() - burnDamage);
                    Map<String, TargetBattleData> deadUnits = new HashMap<>();

                    if (fieldA.getCurrentHp() <= 0) {
                        fieldA.setDead(true);
                        fieldA.setOnField(false);
                        TargetBattleData data = new TargetBattleData(fieldA.getMaxHp(), fieldA.getCurrentHp(), burnDamage, fieldA.isOnField());
                        deadUnits.put(fieldA.getId(), data);
                    }
                    addLog("魂力飞弹",
                            guardian.getId(),
                            guardian.getMaxHp(),
                            guardian.getCurrentHp(),
                            0,
                            guardian.isOnField(),
                            fieldA.getId(),
                            fieldA.getMaxHp(),
                            fieldA.getCurrentHp(),
                            burnDamage,
                            fieldA.isOnField(),
                            EffectType.MISSILE_DAMAGE,
                            DamageType.MISSILE,
                            "-" + burnDamage);
                    // 死亡日志
                    if (!deadUnits.isEmpty()) {
                        addMultiTargetLog("UNIT_DEATH",
                                null,
                                0,
                                0,
                                false,
                                deadUnits,
                                null,
                                null,
                                "死亡");
                        //触发死亡技能
                        triggerOnDeathSkills(fieldA);

                    } else {
                        //触发受击技能
                        triggerOnAttackedSkills(fieldA, EffectType.MISSILE_DAMAGE);
                    }
                }
            }


            //            南华真人，魂力飞弹Lv1场下，每当新单位入场时，对场上敌人造成178点飞弹伤害；报复神箭Lv1场下，每回合对场上敌方造成106；
            if (campA.stream().anyMatch(g -> g.getName().equals("金霞童子") && g.getPosition() == position && !g.isDead() && !g.isOnField() && !g.isSilence())) {
                Guardian guardian = campA.stream()
                        .filter(g -> g.getName().equals("金霞童子") && g.getPosition() == position && !g.isDead() && !g.isOnField())
                        .findFirst().get();
                int[] skillLevel = CardSkillLevelUtil.calculateSkillLevels(guardian.getLevel(), guardian.getStar().doubleValue());
                if (fieldB != null && !fieldB.isDead()) {
                    // 1. 计算所有中毒效果的总伤害（累加 POISON 类型的 value）
                    int totalPoisonDamage = 35 * skillLevel[0];
                    // 2. 计算毒抗相关（直接基于你现有 EffectInstance 计算，不新增 Guardian 方法）
                    // 飞弹增益：所有 POISON_RESIST 类型效果的 value 总和
                    int resistUp = calculateTotalVaule(guardian, EffectType.MISSILE_BOOST);
                    // 飞弹增益百分比：所有 POISON_RESIST 类型效果的 value 乘积
                    double resistUpPret = calculateTotalUpPretVaule(guardian, EffectType.MISSILE_BOOST_PRET);
                    // 飞弹降低：所有 POISON_RESIST_DOWN 类型效果的 value 总和
                    int resistDown = calculateTotalVaule(guardian, EffectType.MISSILE_DOWN);
                    // 飞弹降低百分比：所有 POISON_RESIST 类型效果的 value 乘积
                    double resistDownPret = calculateTotalDownPretVaule(guardian, EffectType.MISSILE_DOWN_PRET);


                    // 2. 计算毒抗相关（直接基于你现有 EffectInstance 计算，不新增 Guardian 方法）
                    // 弹抗增益：所有 POISON_RESIST 类型效果的 value 总和
                    int targetUp = calculateTotalVaule(fieldB, EffectType.MISSILE_RESIST_BOOST);
                    // 弹抗增益百分比：所有 POISON_RESIST 类型效果的 value 乘积
                    double targetUpPret = calculateTotalDownPretVaule(fieldB, EffectType.MISSILE_RESIST_BOOST_PRET);
                    // 弹抗降低：所有 POISON_RESIST_DOWN 类型效果的 value 总和
                    int targetDown = calculateTotalVaule(fieldB, EffectType.MISSILE_RESIST_DOWN);
                    // 弹抗降低百分比：所有 POISON_RESIST 类型效果的 value 乘积
                    double targetDownPret = calculateTotalUpPretVaule(fieldB, EffectType.MISSILE_RESIST_DOWN_PRET);
                    // 最终（仅基于 buff 计算，无新增方法）

                    int burnDamage = (int) (totalPoisonDamage * resistUpPret * resistDownPret * targetUpPret * targetDownPret
                            + (resistUp - resistDown + guardian.getFdAtk() - fieldB.getFdDef() - targetUp + targetDown));

                    if (burnDamage < 0) {
                        burnDamage = 0;
                    }

                    // 4. 扣除伤害
                    fieldB.setCurrentHp(fieldB.getCurrentHp() - burnDamage);
                    Map<String, TargetBattleData> deadUnits = new HashMap<>();

                    if (fieldB.getCurrentHp() <= 0) {
                        fieldB.setDead(true);
                        fieldB.setOnField(false);
                        TargetBattleData data = new TargetBattleData(fieldB.getMaxHp(), fieldB.getCurrentHp(), burnDamage, fieldB.isOnField());
                        deadUnits.put(fieldB.getId(), data);
                    }
                    addLog("魂力飞弹",
                            guardian.getId(),
                            guardian.getMaxHp(),
                            guardian.getCurrentHp(),
                            0,
                            guardian.isOnField(),
                            fieldB.getId(),
                            fieldB.getMaxHp(),
                            fieldB.getCurrentHp(),
                            burnDamage,
                            fieldB.isOnField(),
                            EffectType.MISSILE_DAMAGE,
                            DamageType.MISSILE,
                            "-" + burnDamage);
                    // 死亡日志
                    if (!deadUnits.isEmpty()) {
                        addMultiTargetLog("UNIT_DEATH",
                                null,
                                0,
                                0,
                                false,
                                deadUnits,
                                null,
                                null,
                                "死亡");
                        //触发死亡技能
                        triggerOnDeathSkills(fieldB);

                    } else {
                        //触发受击技能
                        triggerOnAttackedSkills(fieldB, EffectType.MISSILE_DAMAGE);
                    }
                }
            }
//            镇元子，魂力飞弹Lv1场下，每当新单位入场时，对场上敌人造成178点飞弹伤害；禁心咒Lv1场下，每当有单位登场，有17%几率令场上英雄沉默2回合；多宝道人同Lv1与多宝道人在同一队伍时，增加自身453点生命上限，158点攻击，158点速度。
            if (campB.stream().anyMatch(g -> g.getName().equals("金霞童子") && g.getPosition() == position && !g.isDead() && !g.isOnField() && !g.isSilence())) {
                Guardian guardian = campB.stream()
                        .filter(g -> g.getName().equals("金霞童子") && g.getPosition() == position && !g.isDead() && !g.isOnField())
                        .findFirst().get();
                int[] skillLevel = CardSkillLevelUtil.calculateSkillLevels(guardian.getLevel(), guardian.getStar().doubleValue());
                if (fieldA != null && !fieldA.isDead()) {
                    // 1. 计算所有中毒效果的总伤害（累加 POISON 类型的 value）
                    int totalPoisonDamage = 178 * skillLevel[0];
                    // 2. 计算毒抗相关（直接基于你现有 EffectInstance 计算，不新增 Guardian 方法）
                    // 飞弹增益：所有 POISON_RESIST 类型效果的 value 总和
                    int resistUp = calculateTotalVaule(guardian, EffectType.MISSILE_BOOST);
                    // 飞弹增益百分比：所有 POISON_RESIST 类型效果的 value 乘积
                    double resistUpPret = calculateTotalUpPretVaule(guardian, EffectType.MISSILE_BOOST_PRET);
                    // 飞弹降低：所有 POISON_RESIST_DOWN 类型效果的 value 总和
                    int resistDown = calculateTotalVaule(guardian, EffectType.MISSILE_DOWN);
                    // 飞弹降低百分比：所有 POISON_RESIST 类型效果的 value 乘积
                    double resistDownPret = calculateTotalDownPretVaule(guardian, EffectType.MISSILE_DOWN_PRET);


                    // 2. 计算毒抗相关（直接基于你现有 EffectInstance 计算，不新增 Guardian 方法）
                    // 弹抗增益：所有 POISON_RESIST 类型效果的 value 总和
                    int targetUp = calculateTotalVaule(fieldA, EffectType.MISSILE_RESIST_BOOST);
                    // 弹抗增益百分比：所有 POISON_RESIST 类型效果的 value 乘积
                    double targetUpPret = calculateTotalDownPretVaule(fieldA, EffectType.MISSILE_RESIST_BOOST_PRET);
                    // 弹抗降低：所有 POISON_RESIST_DOWN 类型效果的 value 总和
                    int targetDown = calculateTotalVaule(fieldA, EffectType.MISSILE_RESIST_DOWN);
                    // 弹抗降低百分比：所有 POISON_RESIST 类型效果的 value 乘积
                    double targetDownPret = calculateTotalUpPretVaule(fieldA, EffectType.MISSILE_RESIST_DOWN_PRET);
                    // 最终（仅基于 buff 计算，无新增方法）

                    int burnDamage = (int) (totalPoisonDamage * resistUpPret * resistDownPret * targetUpPret * targetDownPret
                            + (resistUp - resistDown + guardian.getFdAtk() - fieldA.getFdDef() - targetUp + targetDown));

                    if (burnDamage < 0) {
                        burnDamage = 0;
                    }

                    // 4. 扣除伤害
                    fieldA.setCurrentHp(fieldA.getCurrentHp() - burnDamage);
                    Map<String, TargetBattleData> deadUnits = new HashMap<>();

                    if (fieldA.getCurrentHp() <= 0) {
                        fieldA.setDead(true);
                        fieldA.setOnField(false);
                        TargetBattleData data = new TargetBattleData(fieldA.getMaxHp(), fieldA.getCurrentHp(), burnDamage, fieldA.isOnField());
                        deadUnits.put(fieldA.getId(), data);
                    }
                    addLog("魂力飞弹",
                            guardian.getId(),
                            guardian.getMaxHp(),
                            guardian.getCurrentHp(),
                            0,
                            guardian.isOnField(),
                            fieldA.getId(),
                            fieldA.getMaxHp(),
                            fieldA.getCurrentHp(),
                            burnDamage,
                            fieldA.isOnField(),
                            EffectType.MISSILE_DAMAGE,
                            DamageType.MISSILE,
                            "-" + burnDamage);
                    // 死亡日志
                    if (!deadUnits.isEmpty()) {
                        addMultiTargetLog("UNIT_DEATH",
                                null,
                                0,
                                0,
                                false,
                                deadUnits,
                                null,
                                null,
                                "死亡");
                        //触发死亡技能
                        triggerOnDeathSkills(fieldA);

                    } else {
                        //触发受击技能
                        triggerOnAttackedSkills(fieldA, EffectType.MISSILE_DAMAGE);
                    }
                }
            }


//            瑶姬，倾盆大雨Lv1我方单位登场时，有50%几率令敌方场上单位眩晕2回合；
            if (defender.getCamp() == Camp.A && campA.stream().anyMatch(g -> g.getName().equals("瑶姬") && g.getPosition() == position && !g.isDead() && !g.isOnField() && !g.isSilence())) {
                Guardian guardian = campA.stream()
                        .filter(g -> g.getName().equals("瑶姬") && g.getPosition() == position && !g.isDead() && !g.isOnField())
                        .findFirst().get();
                if (ProbabilityBooleanUtils.randomByProbability(0.5) && fieldB != null && !fieldB.isDead()) {
                    fieldB.addEffect(EffectType.STUN, 0, 2, guardian.getId());
                    addLog("倾盆大雨",
                            guardian.getId(),
                            guardian.getMaxHp(),
                            guardian.getCurrentHp(),
                            0,
                            guardian.isOnField(),
                            fieldB.getId(),
                            fieldB.getMaxHp(),
                            fieldB.getCurrentHp(),
                            0,
                            fieldB.isOnField(),
                            EffectType.STUN,
                            null,
                            "眩晕2回合");
                }
            }
//            瑶姬，倾盆大雨Lv1我方单位登场时，有50%几率令敌方场上单位眩晕2回合；
            if (defender.getCamp() == Camp.B && campB.stream().anyMatch(g -> g.getName().equals("瑶姬") && g.getPosition() == position && !g.isDead() && !g.isOnField() && !g.isSilence())) {
                Guardian guardian = campB.stream()
                        .filter(g -> g.getName().equals("瑶姬") && g.getPosition() == position && !g.isDead() && !g.isOnField())
                        .findFirst().get();
                if (ProbabilityBooleanUtils.randomByProbability(0.5) && fieldA != null && !fieldA.isDead()) {
                    fieldA.addEffect(EffectType.STUN, 0, 2, guardian.getId());
                    addLog("倾盆大雨",
                            guardian.getId(),
                            guardian.getMaxHp(),
                            guardian.getCurrentHp(),
                            0,
                            guardian.isOnField(),
                            fieldA.getId(),
                            fieldA.getMaxHp(),
                            fieldA.getCurrentHp(),
                            0,
                            fieldA.isOnField(),
                            EffectType.STUN,
                            null,
                            "眩晕2回合");
                }
            }
        }
    }


    private void processOnFieldSkills2() {
        //续命技能
        String[] xuminHero = {"小龙女", "洛神", "瑶姬", "中岳大帝", "陆压道君", "多宝道人", "河伯", "赤精子", "广成子", "宫女", "玉兔", "田螺仙子"};
        List<String> xuminHeroList = Arrays.asList(xuminHero);
        for (int i = 1; i < 6; i++) {
            int position = i;
            if (campA.stream().anyMatch(g -> xuminHeroList.contains(g.getName()) && g.getPosition() == position && !g.isDead() && !g.isOnField() && !g.isSilence())) {
                Guardian guardian = campA.stream()
                        .filter(g -> xuminHeroList.contains(g.getName()) && g.getPosition() == position && !g.isDead() && !g.isOnField())
                        .findFirst().get();
                int[] skillLevel = CardSkillLevelUtil.calculateSkillLevels(guardian.getLevel(), guardian.getStar().doubleValue());
                //疾病效果或加成效果
//                哪吒属性还是=飞弹伤害*（1-0.8哪吒）*（1-0.1蛇）*（1+0.1中岳光环）-（装备抗性-装备增伤）
                int healDow = calculateTotalVaule(fieldA, EffectType.HEAL_DOWN);
                double healDowPret = calculateTotalDownPretVaule(fieldA, EffectType.HEAL_DOWNT_PRET);
                int healBoost = calculateTotalVaule(fieldA, EffectType.HEAL_BOOST);
                double healBoostPret = calculateTotalUpPretVaule(fieldA, EffectType.HEAL_BOOST_PRET);

                if (!fieldA.isDead() && fieldA.getRace() == Race.IMMORTAL) {
                    int hel = 20;
                    if ("小龙女".equals(guardian.getName())) {
                        hel = 39 * skillLevel[0];
                    } else if ("洛神".equals(guardian.getName())) {
                        hel = 104 * skillLevel[0];
                    } else if ("银甲神".equals(guardian.getName())) {
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
                    } else if ("田螺仙子".equals(guardian.getName())) {
                        hel = 39 * skillLevel[0];
                    }
                    guardian.setCurrentHp(guardian.getCurrentHp() - hel - guardian.getZlDef());
                    int hel1 = hel + guardian.getZlDef();
//                    最终受到伤害
//
//                    = 原始伤害
//                    × 火抗减免
//                    × 毒抗减免
//                    × 飞弹减免
//                    × 光环减伤1
//                    × 光环减伤2
//                    × 技能减伤buff1
//                    × 技能减伤buff2
//                    − 固定减伤（比如飞弹减伤500）
                    hel = (int) (hel * healDowPret * healBoostPret + guardian.getZlDef() - healDow + healBoost);
                    if (hel < 0) {
                        hel = 0;
                    }
                    fieldA.setCurrentHp(fieldA.getCurrentHp() + hel);
                    Map<String, TargetBattleData> deadUnits = new HashMap<>();

                    if (guardian.getCurrentHp() <= 0) {
                        guardian.setDead(true);
                        guardian.setOnField(false);
                        TargetBattleData data = new TargetBattleData(guardian.getMaxHp(), guardian.getCurrentHp(), hel, guardian.isOnField());
                        deadUnits.put(guardian.getId(), data);
                    }
                    addLog("续命",
                            guardian.getId(),
                            guardian.getMaxHp(),
                            guardian.getCurrentHp(),
                            hel1,
                            guardian.isOnField(),
                            fieldA.getId(),
                            fieldA.getMaxHp(),
                            fieldA.getCurrentHp(),
                            hel,
                            fieldA.isOnField(),
                            EffectType.XU_HEAL,
                            null,
                            "+" + hel);
                    // 死亡日志
                    if (!deadUnits.isEmpty()) {
                        addMultiTargetLog("UNIT_DEATH",
                                null,
                                0,
                                0,
                                false,
                                deadUnits,
                                null,
                                null,
                                "死亡");
                        //触发死亡技能
                        triggerOnDeathSkills(guardian);

                    }
                    triggerOnHelSkills(fieldA);
                }

            }

            if (campB.stream().anyMatch(g -> xuminHeroList.contains(g.getName()) && g.getPosition() == position && !g.isDead() && !g.isOnField() && !g.isSilence())) {
                Guardian guardian = campB.stream()
                        .filter(g -> xuminHeroList.contains(g.getName()) && g.getPosition() == position && !g.isDead() && !g.isOnField())
                        .findFirst().get();
                int[] skillLevel = CardSkillLevelUtil.calculateSkillLevels(guardian.getLevel(), guardian.getStar().doubleValue());
                //疾病效果或加成效果
//                哪吒属性还是=飞弹伤害*（1-0.8哪吒）*（1-0.1蛇）*（1+0.1中岳光环）-（装备抗性-装备增伤）
                int healDow = calculateTotalVaule(fieldB, EffectType.HEAL_DOWN);
                double healDowPret = calculateTotalDownPretVaule(fieldB, EffectType.HEAL_DOWNT_PRET);
                int healBoost = calculateTotalVaule(fieldB, EffectType.HEAL_BOOST);
                double healBoostPret = calculateTotalUpPretVaule(fieldB, EffectType.HEAL_BOOST_PRET);

                if (!fieldB.isDead() && fieldB.getRace() == Race.IMMORTAL) {
                    int hel = 20;
                    if ("小龙女".equals(guardian.getName())) {
                        hel = 39 * skillLevel[0];
                    } else if ("洛神".equals(guardian.getName())) {
                        hel = 104 * skillLevel[0];
                    } else if ("银甲神".equals(guardian.getName())) {
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
                    } else if ("田螺仙子".equals(guardian.getName())) {
                        hel = 39 * skillLevel[0];
                    }
                    guardian.setCurrentHp(guardian.getCurrentHp() - hel - guardian.getZlDef());
                    int hel1 = hel + guardian.getZlDef();
//                    最终受到伤害
//
//                    = 原始伤害
//                    × 火抗减免
//                    × 毒抗减免
//                    × 飞弹减免
//                    × 光环减伤1
//                    × 光环减伤2
//                    × 技能减伤buff1
//                    × 技能减伤buff2
//                    − 固定减伤（比如飞弹减伤500）
                    hel = (int) (hel * healDowPret * healBoostPret + guardian.getZlDef() - healDow + healBoost);
                    if (hel < 0) {
                        hel = 0;
                    }
                    fieldB.setCurrentHp(fieldB.getCurrentHp() + hel);
                    Map<String, TargetBattleData> deadUnits = new HashMap<>();

                    if (guardian.getCurrentHp() <= 0) {
                        guardian.setDead(true);
                        guardian.setOnField(false);
                        TargetBattleData data = new TargetBattleData(guardian.getMaxHp(), guardian.getCurrentHp(), hel, guardian.isOnField());
                        deadUnits.put(guardian.getId(), data);
                    }
                    addLog("续命",
                            guardian.getId(),
                            guardian.getMaxHp(),
                            guardian.getCurrentHp(),
                            hel1,
                            guardian.isOnField(),
                            fieldB.getId(),
                            fieldB.getMaxHp(),
                            fieldB.getCurrentHp(),
                            hel,
                            fieldB.isOnField(),
                            EffectType.XU_HEAL,
                            null,
                            "+" + hel);
                    // 死亡日志
                    if (!deadUnits.isEmpty()) {
                        addMultiTargetLog("UNIT_DEATH",
                                null,
                                0,
                                0,
                                false,
                                deadUnits,
                                null,
                                null,
                                "死亡");
                        //触发死亡技能
                        triggerOnDeathSkills(guardian);

                    }
                    triggerOnHelSkills(fieldB);
                }

            }

        }


    }


    private boolean duoBaoGuanHuan() {
        //血量上限不改变光环
        boolean campAHasAlive = campA.stream().anyMatch(g -> g.getName().equals("多宝道人") && !g.isDead());
        boolean campBHasAlive = campB.stream().anyMatch(g -> g.getName().equals("多宝道人") && !g.isDead());
        return campAHasAlive || campBHasAlive;
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
                null, 0, 0,
                0, false, null, 0, 0, 0,
                false, null, null, result);
    }

    // 添加单目标日志（包含位置）
    private void addLog(String eventType,
                        String sourceUnitId,
                        int sourceHpBefore, int sourceHpAfter,
                        int sourceSelfValue,
                        boolean sourceFieldStatus,
                        String targetUnitId,
                        int targetHpBefore, int targetHpAfter,
                        int singleTargetValue,
                        boolean targetFieldStatus,
                        EffectType effectType,
                        DamageType damageType,
                        String extraDesc
    ) {
        battleLogs.add(new BattleLog(
                battleId,
                currentRound,
                eventType,
                0,
                sourceUnitId,
                sourceHpBefore,
                sourceHpAfter,
                sourceSelfValue,
                sourceFieldStatus,
                targetUnitId,
                targetHpBefore,
                targetHpAfter,
                singleTargetValue,
                targetFieldStatus,
                null,
                effectType,
                damageType,
                extraDesc,
                0
        ));
    }

    // 添加单目标日志（包含位置）
    private void addLogEnter(int flyup, String eventType,
                             String sourceUnitId,
                             int sourceHpBefore, int sourceHpAfter,
                             boolean sourceFieldStatus,
                             Map<String, TargetBattleData> multiTargetDataMap,
                             String extraDesc
    ) {
        battleLogs.add(new BattleLog(
                battleId,
                currentRound,
                eventType,
                flyup,
                sourceUnitId,
                sourceHpBefore,
                sourceHpAfter,
                0,
                sourceFieldStatus,
                null,
                0,
                0,
                0,
                false,
                multiTargetDataMap,
                null,
                null,
                extraDesc,
                1
        ));
    }

    // 添加多目标日志（包含位置）
    private void addMultiTargetLog(String eventType,
                                   String sourceUnitId,
                                   int sourceHpBefore, int sourceHpAfter,
                                   boolean sourceFieldStatus,
                                   Map<String, TargetBattleData> multiTargetDataMap,
                                   EffectType effectType,
                                   DamageType damageType,
                                   String extraDesc
    ) {
        battleLogs.add(new BattleLog(
                battleId,
                currentRound,
                eventType,
                0,
                sourceUnitId,
                sourceHpBefore,
                sourceHpAfter,
                0,
                sourceFieldStatus,
                null,
                0,
                0,
                0,
                false,
                multiTargetDataMap,
                effectType,
                damageType,
                extraDesc,
                1
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