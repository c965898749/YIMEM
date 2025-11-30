package com.sy.model.game;

import java.util.*;
import java.util.stream.Collectors;



/**
 * 护法（卡牌）实体类
 */
public class Guardian {
    private String id;
    private String name;
    private Profession profession;
    private Race race;
    private int level;
    private int position;
    private int baseMaxHp;
    private int currentMaxHp;
    private int currentHp;
    private int baseAttack;
    private int currentAttack;
    private int baseSpeed;
    private int currentSpeed;
    private boolean isOnField;
    private boolean isAlive;
    private List<Buff> buffs;
    private List<String> synergyTargets;
    private Map<String, Integer> skillTriggers;
    private Random random;

    // 技能叠加属性
    private int luoshenAttackStacks;

    // 牛魔王叠加属性
    private int niumoWangHpStacks;  // 鲜血盛宴生命上限叠加层数
    private static final int NIUMOWANG_MAX_STACKS = 3; // 牛魔王最大叠加层数

    // 厚土娘娘叠加属性
    private int houtuHpStacks;      // 后土聚能生命上限叠加层数
    private int houtuAttackStacks;  // 后土聚能攻击叠加层数
    private static final int HOUTU_MAX_STACKS = 99; // 最大叠加层数

    // 状态标记
    private boolean isHpProtected;

    public Guardian(String name, Profession profession, Race race, int level, int position) {
        this.id = UUID.randomUUID().toString().substring(0, 8);
        this.name = name;
        this.profession = profession;
        this.race = race;
        this.level = level;
        this.position = position;
        this.isOnField = false;
        this.isAlive = true;
        this.buffs = new ArrayList<>();
        this.synergyTargets = new ArrayList<>();
        this.skillTriggers = new HashMap<>();
        this.random = new Random();
        this.luoshenAttackStacks = 0;
        this.niumoWangHpStacks = 0;
        this.houtuHpStacks = 0;
        this.houtuAttackStacks = 0;
        this.isHpProtected = false;
        initBaseAttributes();
        initSynergyTargets();
        initSkillTriggers();
    }

    private void initBaseAttributes() {
        double levelScale = 1 + (level - 1) * 0.2;
        switch (name) {
            case "牛魔王":
                this.baseMaxHp = (int) (3000 * levelScale);
                this.baseAttack = (int) (384 * levelScale);
                this.baseSpeed = (int) (190 * levelScale);
                break;
            case "洛神":
                this.baseMaxHp = (int) (1500 * levelScale);
                this.baseAttack = (int) (250 * levelScale);
                this.baseSpeed = (int) (240 * levelScale);
                break;
            case "阎罗王":
                this.baseMaxHp = (int) (1900 * levelScale);
                this.baseAttack = (int) (250 * levelScale);
                this.baseSpeed = (int) (180 * levelScale);
                break;
            case "圣灵天将":
                this.baseMaxHp = (int) (2000 * levelScale);
                this.baseAttack = (int) (300 * levelScale);
                this.baseSpeed = (int) (220 * levelScale);
                break;
            case "瑶池仙女":
                this.baseMaxHp = (int) (1600 * levelScale);
                this.baseAttack = (int) (240 * levelScale);
                this.baseSpeed = (int) (200 * levelScale);
                break;
            case "厚土娘娘":
                this.baseMaxHp = (int) (2200 * levelScale);
                this.baseAttack = (int) (260 * levelScale);
                this.baseSpeed = (int) (170 * levelScale);
                break;
            case "妲己":
                this.baseMaxHp = (int) (1300 * levelScale);
                this.baseAttack = (int) (290 * levelScale);
                this.baseSpeed = (int) (230 * levelScale);
                break;
            case "玄冥":
                this.baseMaxHp = (int) (1400 * levelScale);
                this.baseAttack = (int) (270 * levelScale);
                this.baseSpeed = (int) (210 * levelScale);
                break;
            case "齐天大圣":
                this.baseMaxHp = (int) (2500 * levelScale);
                this.baseAttack = (int) (700 * levelScale);
                this.baseSpeed = (int) (300 * levelScale);
                break;
            case "托塔天王":
                this.baseMaxHp = (int) (1600 * levelScale);
                this.baseAttack = (int) (280 * levelScale);
                this.baseSpeed = (int) (200 * levelScale);
                break;
            case "紫薇大帝":
                this.baseMaxHp = (int) (1500 * levelScale);
                this.baseAttack = (int) (270 * levelScale);
                this.baseSpeed = (int) (200 * levelScale);
                break;
            case "长生大帝":
                this.baseMaxHp = (int) (1700 * levelScale);
                this.baseAttack = (int) (240 * levelScale);
                this.baseSpeed = (int) (180 * levelScale);
                break;
            case "聂小倩":
                this.baseMaxHp = (int) (1200 * levelScale);
                this.baseAttack = (int) (300 * levelScale);
                this.baseSpeed = (int) (220 * levelScale);
                break;
            case "白骨精":
                this.baseMaxHp = (int) (1100 * levelScale);
                this.baseAttack = (int) (310 * levelScale);
                this.baseSpeed = (int) (230 * levelScale);
                break;
            case "孟婆":
                this.baseMaxHp = (int) (1300 * levelScale);
                this.baseAttack = (int) (220 * levelScale);
                this.baseSpeed = (int) (200 * levelScale);
                break;
            case "铁扇公主":
                this.baseMaxHp = (int) (1800 * levelScale);
                this.baseAttack = (int) (320 * levelScale);
                this.baseSpeed = (int) (210 * levelScale);
                break;
            case "燃灯道人":
                this.baseMaxHp = (int) (1600 * levelScale);
                this.baseAttack = (int) (290 * levelScale);
                this.baseSpeed = (int) (190 * levelScale);
                break;
            case "圣婴大王":
                this.baseMaxHp = (int) (1700 * levelScale);
                this.baseAttack = (int) (330 * levelScale);
                this.baseSpeed = (int) (210 * levelScale);
                break;
            default:
                this.baseMaxHp = (int) (1000 * levelScale);
                this.baseAttack = (int) (200 * levelScale);
                this.baseSpeed = (int) (200 * levelScale);
        }
        this.currentMaxHp = baseMaxHp;
        this.currentAttack = baseAttack;
        this.currentSpeed = baseSpeed;
        this.currentHp = currentMaxHp;
    }

    private void initSynergyTargets() {
        switch (name) {
            case "妲己":
                synergyTargets.add("白素贞");
                break;
            case "厚土娘娘":
                synergyTargets.add("燃灯道人");
                break;
            case "玄冥":
                synergyTargets.add("将臣");
                break;
            case "齐天大圣":
                synergyTargets.add("句芒");
                break;
            case "紫薇大帝":
                synergyTargets.add("长生大帝");
                break;
            case "白骨精":
                synergyTargets.add("阎罗王");
                break;
            case "牛魔王":
                synergyTargets.add("圣婴大王");
                break;
            case "洛神":
                synergyTargets.add("伏羲");
                break;
            case "圣灵天将":
                synergyTargets.add("瑶池仙女");
                break;
            default:
                break;
        }
    }

    private void initSkillTriggers() {
        switch (name) {
            case "白天君":
                skillTriggers.put("三火齐飞", 0);
                break;
            case "厚土娘娘":
                skillTriggers.put("后土聚能", 0);
                break;
            case "齐天大圣":
                skillTriggers.put("大圣降临", 0);
                break;
            case "白骨精":
                skillTriggers.put("白骨噬魂", 0);
                break;
            case "牛魔王":
                skillTriggers.put("鲜血盛宴", 0);
                break;
            case "洛神":
                skillTriggers.put("洛水歌声", 0);
                break;
            case "阎罗王":
                skillTriggers.put("生死簿层数", 0);
                break;
            case "圣灵天将":
                skillTriggers.put("圣灵法阵持续", 0);
                break;
            default:
                break;
        }
    }

    public void checkSynergy(List<Guardian> team, List<BattleLog> battleLogs, String battleId, int round) {
        if (team == null || battleLogs == null || battleId == null) {
            return;
        }

        for (String targetName : synergyTargets) {
            boolean hasSynergy = team.stream()
                    .filter(Objects::nonNull)
                    .anyMatch(g -> g.getName().equals(targetName) && g.isAlive());

            if (hasSynergy) {
                // 牛魔王与众妖皆狂协同效果
                if (name.equals("牛魔王") && targetName.equals("圣婴大王")) {
                    int addHp = 352;    // Lv1固定352点生命上限
                    int addAtk = 176;   // Lv1固定176点攻击
                    int addSpd = 176;   // Lv1固定176点速度

                    this.currentMaxHp += addHp;
                    this.currentAttack += addAtk;
                    this.currentSpeed += addSpd;

                    battleLogs.add(new BattleLog(battleId, round, "众妖皆狂（协同）", this.name, targetName,
                            addHp, EffectType.HP_UP, null));
                    battleLogs.add(new BattleLog(battleId, round, "众妖皆狂（协同）", this.name, targetName,
                            addAtk, EffectType.ATTACK_UP, null));
                    battleLogs.add(new BattleLog(battleId, round, "众妖皆狂（协同）", this.name, targetName,
                            addSpd, EffectType.SPEED_UP, null));
                }
                // 厚土娘娘与燃灯道人协同效果
                else if (name.equals("厚土娘娘") && targetName.equals("燃灯道人")) {
                    int addHp = 453 * level;
                    int addAtk = 158 * level;
                    int addSpd = 158 * level;

                    this.currentMaxHp += addHp;
                    this.currentAttack += addAtk;
                    this.currentSpeed += addSpd;

                    battleLogs.add(new BattleLog(battleId, round, "道法自然（协同）", this.name, targetName,
                            addHp, EffectType.HP_UP, null));
                    battleLogs.add(new BattleLog(battleId, round, "道法自然（协同）", this.name, targetName,
                            addAtk, EffectType.ATTACK_UP, null));
                    battleLogs.add(new BattleLog(battleId, round, "道法自然（协同）", this.name, targetName,
                            addSpd, EffectType.SPEED_UP, null));
                }
                // 圣灵天将与瑶池仙女协同效果
                else if (name.equals("圣灵天将") && targetName.equals("瑶池仙女")) {
                    int addHp = 423 * level;
                    int addAtk = 141 * level;
                    int addSpd = 151 * level;

                    this.currentMaxHp += addHp;
                    this.currentAttack += addAtk;
                    this.currentSpeed += addSpd;

                    battleLogs.add(new BattleLog(battleId, round, "仙凡共鸣（协同）", this.name, targetName,
                            addHp, EffectType.HP_UP, null));
                    battleLogs.add(new BattleLog(battleId, round, "仙凡共鸣（协同）", this.name, targetName,
                            addAtk, EffectType.ATTACK_UP, null));
                    battleLogs.add(new BattleLog(battleId, round, "仙凡共鸣（协同）", this.name, targetName,
                            addSpd, EffectType.SPEED_UP, null));
                }
                // 其他协同效果
                else {
                    int addHp = 453 * level;
                    int addAtk = 158 * level;
                    int addSpd = 158 * level;

                    this.currentMaxHp += addHp;
                    this.currentAttack += addAtk;
                    this.currentSpeed += addSpd;

                    battleLogs.add(new BattleLog(battleId, round, "协同效果激活", this.name, targetName,
                            addHp, EffectType.HP_UP, null));
                }
                break;
            }
        }

        if (name.equals("阎罗王") && position == 2) {
            int addHp = 729 * level;
            int addSpd = 130 * level;

            this.currentMaxHp += addHp;
            this.currentSpeed += addSpd;

            battleLogs.add(new BattleLog(battleId, round, "不动如山（位置加成）", this.name, "自身",
                    addHp, EffectType.HP_UP, null));
            battleLogs.add(new BattleLog(battleId, round, "不动如山（位置加成）", this.name, "自身",
                    addSpd, EffectType.SPEED_UP, null));
        }
    }

    /**
     * 触发牛魔王熔岩爆发技能（攻击后对场下敌方造成62点火焰伤害）
     */
    public void triggerLavaBurst(List<Guardian> enemyTeam, List<BattleLog> battleLogs, String battleId, int round) {
        if (!isAlive() || !isOnField || enemyTeam == null) return;

        List<String> damagedTargets = new ArrayList<>();
        int damageAmount = 62; // Lv1固定62点伤害

        enemyTeam.stream()
                .filter(Objects::nonNull)
                .filter(g -> !g.isOnField() && g.isAlive())
                .forEach(g -> {
                    int finalDamage = g.takeDamage(damageAmount, DamageType.FIRE);
                    if (finalDamage > 0) {
                        damagedTargets.add(g.getName());
                    }
                });

        if (!damagedTargets.isEmpty()) {
            battleLogs.add(new BattleLog(battleId, round, "熔岩爆发（牛魔王）", this.name,
                    damagedTargets, damageAmount, EffectType.DAMAGE, DamageType.FIRE));
        }
    }

    /**
     * 触发牛魔王鲜血盛宴技能（生物死亡时增加生命上限）
     */
    public void triggerBloodFeast(List<BattleLog> battleLogs, String battleId, int round) {
        if (!isAlive() || !isOnField || niumoWangHpStacks >= NIUMOWANG_MAX_STACKS) return;

        int hpIncrease = 117; // Lv1固定117点生命上限
        this.currentMaxHp += hpIncrease;
        niumoWangHpStacks++;

        battleLogs.add(new BattleLog(battleId, round, "鲜血盛宴（牛魔王）", this.name, "自身",
                hpIncrease, EffectType.HP_UP, null,
                "生命上限+" + hpIncrease + "（当前层数：" + niumoWangHpStacks + "/" + NIUMOWANG_MAX_STACKS + "）"));
    }

    /**
     * 触发厚土娘娘大地净化技能（敌方单位登场时驱散自身减益）
     */
    public void triggerEarthPurification(List<BattleLog> battleLogs, String battleId, int round) {
        if (!isAlive() || !isOnField() || !name.equals("厚土娘娘")) return;

        int debuffCount = (int) buffs.stream()
                .filter(Objects::nonNull)
                .filter(b -> b.getType().isDebuff())
                .count();

        if (debuffCount > 0) {
            buffs.removeIf(buff -> buff != null && buff.getType().isDebuff());
            battleLogs.add(new BattleLog(battleId, round, "大地净化（厚土娘娘）", this.name, "自身",
                    debuffCount, EffectType.BUFF, null,
                    "驱散" + debuffCount + "个减益效果"));
        }
    }

    /**
     * 触发后土聚能技能（每回合叠加属性）
     */
    public void triggerHoutuAccumulation(List<BattleLog> battleLogs, String battleId, int round) {
        if (!isAlive() || !isOnField() || !name.equals("厚土娘娘")) return;

        if (houtuHpStacks < HOUTU_MAX_STACKS) {
            int hpIncrease = 197 * level;
            int attackIncrease = 67 * level;

            currentMaxHp += hpIncrease;
            currentAttack += attackIncrease;

            houtuHpStacks++;
            houtuAttackStacks++;

            battleLogs.add(new BattleLog(battleId, round, "后土聚能（厚土娘娘）", this.name, "自身",
                    hpIncrease, EffectType.HP_UP, null,
                    "生命上限+" + hpIncrease + "，攻击+" + attackIncrease +
                            "（当前层数：" + houtuHpStacks + "/" + HOUTU_MAX_STACKS + "）"));
        }
    }

    public void triggerLifeTransfer(List<Guardian> ownTeam, List<BattleLog> battleLogs, String battleId, int round) {
        if (!isAlive() || !isOnField || ownTeam == null) return;

        Guardian fieldAlly = ownTeam.stream()
                .filter(Objects::nonNull)
                .filter(Guardian::isOnField)
                .filter(Guardian::isAlive)
                .filter(g -> g.getRace() == Race.IMMORTAL_RACE)
                .findFirst()
                .orElse(null);

        if (fieldAlly != null && !fieldAlly.getId().equals(this.id)) {
            int transferAmount = 40 * level;
            int actualTransfer = Math.min(transferAmount, this.currentHp - 1);

            if (actualTransfer > 0) {
                this.currentHp -= actualTransfer;
                int healAmount = fieldAlly.heal(actualTransfer);

                battleLogs.add(new BattleLog(battleId, round, "续命（洛神）", this.name, fieldAlly.getName(),
                        healAmount, EffectType.HEAL, null));
            }
        }
    }

    public void triggerLifeBook(List<Guardian> allTeamA, List<Guardian> allTeamB,
                                List<BattleLog> battleLogs, String battleId, int round) {
        if (!isAlive() || !isOnField) return;

        int reduceAmount = 100 * level;
        List<String> affectedTargets = new ArrayList<>();

        allTeamA.stream()
                .filter(Objects::nonNull)
                .filter(Guardian::isAlive)
                .forEach(g -> {
                    if (!g.isHpProtected()) {
                        g.reduceMaxHp(reduceAmount);
                        affectedTargets.add(g.getName());
                    }
                });

        allTeamB.stream()
                .filter(Objects::nonNull)
                .filter(Guardian::isAlive)
                .forEach(g -> {
                    if (!g.isHpProtected()) {
                        g.reduceMaxHp(reduceAmount);
                        affectedTargets.add(g.getName());
                    }
                });

        if (!affectedTargets.isEmpty()) {
            battleLogs.add(new BattleLog(battleId, round, "生死簿（阎罗王）", this.name,
                    affectedTargets, reduceAmount, EffectType.HP_DOWN, null));

            int stacks = skillTriggers.getOrDefault("生死簿层数", 0) + 1;
            skillTriggers.put("生死簿层数", stacks);
        }
    }

    public void triggerNetherJudgment(List<Guardian> enemyTeam, List<BattleLog> battleLogs,
                                      String battleId, int round) {
        if (!isAlive() || (isOnField && position == 2)) return;

        List<Guardian> aliveEnemies = enemyTeam.stream()
                .filter(Objects::nonNull)
                .filter(Guardian::isAlive)
                .collect(Collectors.toList());

        if (!aliveEnemies.isEmpty()) {
            Guardian target = aliveEnemies.get(random.nextInt(aliveEnemies.size()));
            int poisonDamage = 73 * level;

            Buff poisonBuff = new Buff(Buff.BuffType.POISONED, 3, poisonDamage);
            target.addBuff(poisonBuff);

            String remainingTurns = poisonBuff.isPermanent() ? "永久" : String.valueOf(poisonBuff.getRemainingTurns());
            battleLogs.add(new BattleLog(battleId, round, "幽冥审判（阎罗王）", this.name, target.getName(),
                    poisonDamage, EffectType.DEBUFF, DamageType.POISON,
                    "中毒+"+poisonDamage+"，剩余回合：" + remainingTurns));
        }
    }

    public void triggerHolyArray(List<Guardian> allTeamA, List<Guardian> allTeamB,
                                 List<BattleLog> battleLogs, String battleId, int round) {
        if (!isAlive() || !isOnField || position != 1) return;

        int duration = 2;
        skillTriggers.put("圣灵法阵持续", duration);

        allTeamA.stream()
                .filter(Objects::nonNull)
                .filter(Guardian::isAlive)
                .forEach(g -> g.setHpProtected(true));

        allTeamB.stream()
                .filter(Objects::nonNull)
                .filter(Guardian::isAlive)
                .forEach(g -> g.setHpProtected(true));

        battleLogs.add(new BattleLog(battleId, round, "圣灵法阵（圣灵天将）", this.name,
                Arrays.asList("全体单位"), 0, EffectType.PROTECT, null,
                "持续" + duration + "回合"));
    }

    public int triggerHolySlash(Guardian defender, List<BattleLog> battleLogs, String battleId, int round) {
        if (!isAlive() || !isOnField || defender == null) return 0;

        if (defender.getProfession() == Profession.WARRIOR) {
            int trueDamage = 150 * level;
            int finalDamage = defender.takeDamage(trueDamage, DamageType.TRUE_DAMAGE);

            battleLogs.add(new BattleLog(battleId, round, "圣灵斩（圣灵天将）", this.name, defender.getName(),
                    finalDamage, EffectType.DAMAGE, DamageType.TRUE_DAMAGE));

            return finalDamage;
        }

        return 0;
    }

    public void updateHolyArrayStatus() {
        if (name.equals("圣灵天将")) {
            int duration = skillTriggers.getOrDefault("圣灵法阵持续", 0);
            if (duration > 0) {
                duration--;
                skillTriggers.put("圣灵法阵持续", duration);

                if (duration == 0) {
                    isHpProtected = false;
                }
            }
        }
    }

    public void handleDeathEvent(BattleEvent event, List<Guardian> ownTeam, List<Guardian> enemyTeam,
                                 boolean isEnemyDeath, List<BattleLog> battleLogs, String battleId, int round) {
        if (!isAlive()) return;

        // 牛魔王鲜血盛宴触发
        if (name.equals("牛魔王")) {
            triggerBloodFeast(battleLogs, battleId, round);
        }

        if (name.equals("洛神") && isEnemyDeath && luoshenAttackStacks < 3) {
            Guardian fieldAlly = ownTeam.stream()
                    .filter(Objects::nonNull)
                    .filter(Guardian::isOnField)
                    .filter(Guardian::isAlive)
                    .findFirst()
                    .orElse(null);

            if (fieldAlly != null) {
                int attackBoost = 104 * level;
                fieldAlly.addAttack(attackBoost);
                luoshenAttackStacks++;

                battleLogs.add(new BattleLog(battleId, round, "洛水歌声（叠加" + luoshenAttackStacks + "层）",
                        this.name, fieldAlly.getName(), attackBoost, EffectType.ATTACK_UP, null));
            }
        }

        if (name.equals("白骨精") && isEnemyDeath) {
            int addAtk = 120 * level;
            addAttack(addAtk);
            battleLogs.add(new BattleLog(battleId, round, "白骨噬魂", name, "自身",
                    addAtk, EffectType.ATTACK_UP, null));
        }
    }

    public void handleEvent(BattleEvent event, List<Guardian> ownTeam, List<Guardian> enemyTeam,
                            List<BattleLog> battleLogs, String battleId, int round) {
        if (event == null || ownTeam == null || battleLogs == null || battleId == null) {
            return;
        }

        if (!isAlive() || isOnField) return;

        if (name.equals("阎罗王") && event.getType() == EventType.UNIT_ENTER) {
            triggerNetherJudgment(enemyTeam, battleLogs, battleId, round);
        }

        switch (name) {
            case "阎罗王":
                if (enemyTeam != null && event.getType() == EventType.UNIT_DAMAGED &&
                        enemyTeam.stream().anyMatch(g -> g.getId().equals(event.getTargetId()))) {

                    Optional<Guardian> targetOpt = enemyTeam.stream()
                            .filter(g -> g.getId().equals(event.getTargetId()))
                            .findFirst();

                    if (targetOpt.isPresent()) {
                        Guardian target = targetOpt.get();
                        Buff diseaseBuff = new Buff(Buff.BuffType.DISEASED, 2, 30 * level);
                        target.addBuff(diseaseBuff);

                        String remainingTurns = diseaseBuff.isPermanent() ? "永久" : String.valueOf(diseaseBuff.getRemainingTurns());
                        battleLogs.add(new BattleLog(battleId, round, "阎罗审判（场下）", name, target.getName(),
                                diseaseBuff.getValue(), EffectType.DEBUFF, null,
                                "疾病+"+diseaseBuff.getValue()+"，剩余回合：" + remainingTurns));
                    }
                }
                break;
            case "孟婆":
                if (event.getType() == EventType.TURN_END) {
                    ownTeam.stream()
                            .filter(Objects::nonNull)
                            .filter(Guardian::isAlive)
                            .forEach(g -> {
                                int debuffCount = (int) g.getBuffs().stream()
                                        .filter(Objects::nonNull)
                                        .filter(b -> b.getType().isDebuff())
                                        .count();

                                if (debuffCount > 0) {
                                    g.getBuffs().removeIf(b -> b != null && b.getType().isDebuff());
                                    battleLogs.add(new BattleLog(battleId, round, "忘忧汤（场下）", name, g.getName(),
                                            debuffCount, EffectType.BUFF, null));
                                }
                            });
                }
                break;
        }
    }

    public void reduceMaxHp(int amount) {
        if (amount > 0 && !isHpProtected) {
            this.currentMaxHp = Math.max(1, this.currentMaxHp - amount);
            if (this.currentHp > this.currentMaxHp) {
                this.currentHp = this.currentMaxHp;
            }
        }
    }

    public List<String> getSynergyTargets() {
        return new ArrayList<>(synergyTargets);
    }

    public void addBuff(Buff buff) {
        if (buff == null) return;

        for (Buff existing : buffs) {
            if (existing.getType() == buff.getType()) {
                existing.stack(buff.getValue());
                return;
            }
        }
        buffs.add(buff);
    }

    public List<String> cleanExpiredBuffs() {
        List<String> expiredBuffs = new ArrayList<>();
        Iterator<Buff> iterator = buffs.iterator();

        while (iterator.hasNext()) {
            Buff buff = iterator.next();
            if (buff.updateTurn() && buff.getDuration() != -1) {
                expiredBuffs.add(buff.getType().getDesc());
                iterator.remove();
            }
        }

        updateHolyArrayStatus();
        return expiredBuffs;
    }

    public int takeDamage(int damage, DamageType type) {
        if (damage <= 0 || !isAlive()) {
            return 0;
        }

        int finalDamage = damage;
        if (name.equals("白素贞") && type == DamageType.FIRE) {
            finalDamage = (int) (finalDamage * 0.9);
        }

        int oldHp = this.currentHp;
        this.currentHp = Math.max(0, this.currentHp - finalDamage);

        if (this.currentHp == 0 && oldHp > 0) {
            this.isAlive = false;
            this.isOnField = false;
        }

        return finalDamage;
    }

    public int heal(int amount) {
        if (amount <= 0 || !isAlive()) {
            return 0;
        }

        int oldHp = this.currentHp;
        this.currentHp = Math.min(this.currentMaxHp, this.currentHp + amount);
        return this.currentHp - oldHp;
    }

    public void addAttack(int amount) {
        if (amount > 0) {
            this.currentAttack += amount;
        }
    }

    public void addMaxHp(int amount) {
        if (amount > 0) {
            this.currentMaxHp += amount;
        }
    }

    public void addSpeed(int amount) {
        if (amount > 0) {
            this.currentSpeed += amount;
        }
    }

    public int getCurrentAttack() {
        return currentAttack + buffs.stream()
                .filter(Objects::nonNull)
                .filter(b -> b.getType() == Buff.BuffType.ATTACK_UP || b.getType() == Buff.BuffType.BLOODLUST)
                .mapToInt(Buff::getValue)
                .sum();
    }

    public int getCurrentSpeed() {
        return currentSpeed + buffs.stream()
                .filter(Objects::nonNull)
                .filter(b -> b.getType() == Buff.BuffType.SPEED_UP || b.getType() == Buff.BuffType.BLOODLUST)
                .mapToInt(Buff::getValue)
                .sum();
    }

    // Getter/Setter方法
    public String getId() { return id; }
    public String getName() { return name; }
    public Profession getProfession() { return profession; }
    public Race getRace() { return race; }
    public int getLevel() { return level; }
    public int getPosition() { return position; }
    public int getCurrentMaxHp() { return currentMaxHp; }
    public int getCurrentHp() { return currentHp; }
    public void setCurrentHp(int currentHp) { this.currentHp = currentHp; }
    public void setCurrentMaxHp(int currentMaxHp) { this.currentMaxHp = currentMaxHp; }
    public boolean isOnField() { return isOnField; }
    public void setOnField(boolean onField) { this.isOnField = onField; }
    public boolean isAlive() { return isAlive; }
    public void setAlive(boolean alive) { isAlive = alive; }
    public List<Buff> getBuffs() { return new ArrayList<>(buffs); }
    public Map<String, Integer> getSkillTriggers() { return new HashMap<>(skillTriggers); }
    public Random getRandom() { return random; }
    public boolean isHpProtected() { return isHpProtected; }
    public void setHpProtected(boolean isHpProtected) { this.isHpProtected = isHpProtected; }

    // 牛魔王叠加属性Getter/Setter
    public int getNiumoWangHpStacks() { return niumoWangHpStacks; }
    public void resetNiumoWangStacks() { this.niumoWangHpStacks = 0; }

    // 厚土娘娘叠加属性Getter/Setter
    public int getHoutuHpStacks() { return houtuHpStacks; }
    public int getHoutuAttackStacks() { return houtuAttackStacks; }
    public void resetHoutuStacks() {
        this.houtuHpStacks = 0;
        this.houtuAttackStacks = 0;
    }
}