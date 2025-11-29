package com.sy.model.game;

import java.util.*;

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
    private List<String> synergyTargets; // 协同目标列表
    private Map<String, Integer> skillTriggers;
    private Random random;
    // 新增技能叠加属性
    private int luoshenAttackStacks; // 洛神攻击叠加层数
    private int niumoWangHpStacks; // 牛魔王生命上限叠加层数
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
        this.synergyTargets = new ArrayList<>(); // 初始化协同目标列表
        this.skillTriggers = new HashMap<>();
        this.random = new Random();
        this.luoshenAttackStacks = 0;
        this.niumoWangHpStacks = 0;
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
            case "洛神":
                this.baseMaxHp = (int) (1500 * levelScale);
                this.baseAttack = (int) (250 * levelScale);
                this.baseSpeed = (int) (240 * levelScale);
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
            case "阎罗王":
                this.baseMaxHp = (int) (1900 * levelScale);
                this.baseAttack = (int) (250 * levelScale);
                this.baseSpeed = (int) (180 * levelScale);
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
                synergyTargets.add("圣婴大王"); // 修改为圣婴大王
                break;
            case "铁扇公主":
                synergyTargets.add("牛魔王");
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
                skillTriggers.put("鲜血盛宴", 0); // 牛魔王生命叠加
                break;
            case "洛神":
                skillTriggers.put("洛水歌声", 0); // 洛神攻击叠加
                break;
            default:
                break;
        }
    }
    // 洛神：续命技能（每回合转移生命给场上仙界队友）
    public void triggerLifeTransfer(List<Guardian> ownTeam, List<BattleLog> battleLogs, String battleId, int round) {
        if (!isAlive() || !isOnField || ownTeam == null) return;

        Guardian fieldAlly = ownTeam.stream()
                .filter(Objects::nonNull)
                .filter(Guardian::isOnField)
                .filter(Guardian::isAlive)
                .filter(g -> g.getRace() == Race.IMMORTAL_RACE) // 只治疗仙界
                .findFirst()
                .orElse(null);

        if (fieldAlly != null && !fieldAlly.getId().equals(this.id)) {
            int transferAmount = 40 * level;
            int actualTransfer = Math.min(transferAmount, this.currentHp - 1); // 保留1点生命

            if (actualTransfer > 0) {
                this.currentHp -= actualTransfer;
                int healAmount = fieldAlly.heal(actualTransfer);

                battleLogs.add(new BattleLog(battleId, round, "续命（洛神）", this.name, fieldAlly.getName(),
                        healAmount, EffectType.HEAL));
            }
        }
    }

    // 牛魔王：熔岩爆发（攻击后对场下敌方造成火焰伤害）
    public void triggerLavaBurst(List<Guardian> enemyTeam, List<BattleLog> battleLogs, String battleId, int round) {
        if (!isAlive() || !isOnField || enemyTeam == null) return;

        List<String> damagedTargets = new ArrayList<>();
        int damageAmount = 62 * level;

        enemyTeam.stream()
                .filter(Objects::nonNull)
                .filter(g -> !g.isOnField() && g.isAlive()) // 只攻击场下单位
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

    public void checkSynergy(List<Guardian> team, List<BattleLog> battleLogs, String battleId, int round) {
        if (team == null || battleLogs == null || battleId == null) {
            return;
        }

        for (String targetName : synergyTargets) {
            boolean hasSynergy = team.stream()
                    .filter(Objects::nonNull)
                    .anyMatch(g -> g.getName().equals(targetName) && g.isAlive());

            if (hasSynergy) {
                int addHp = 453, addAtk = 158, addSpd = 158;
                this.currentMaxHp += addHp;
                this.currentAttack += addAtk;
                this.currentSpeed += addSpd;

                battleLogs.add(new BattleLog(battleId, round, "众妖皆狂（协同）", this.name, targetName,
                        addHp, EffectType.HP_UP));
                battleLogs.add(new BattleLog(battleId, round, "众妖皆狂（协同）", this.name, targetName,
                        addAtk, EffectType.ATTACK_UP));
                battleLogs.add(new BattleLog(battleId, round, "众妖皆狂（协同）", this.name, targetName,
                        addSpd, EffectType.SPEED_UP));
                break;
            }
        }
    }

    // 添加缺失的getSynergyTargets方法
    public List<String> getSynergyTargets() {
        return new ArrayList<>(synergyTargets); // 返回副本防止外部修改
    }

    // 其他方法保持不变...

    public void handleEvent(BattleEvent event, List<Guardian> ownTeam, List<Guardian> enemyTeam,
                            List<BattleLog> battleLogs, String battleId, int round) {
        if (event == null || ownTeam == null || battleLogs == null || battleId == null) {
            return;
        }

        if (!isAlive() || isOnField) return;

        switch (name) {
            case "白骨精":
                if (event.getType() == EventType.UNIT_DEATH) {
                    int addAtk = 120 * level;
                    addAttack(addAtk);
                    battleLogs.add(new BattleLog(battleId, round, "白骨噬魂（场下）", name, "自身",
                            addAtk, EffectType.ATTACK_UP));
                }
                break;
            case "阎罗王":
                if (enemyTeam != null && event.getType() == EventType.UNIT_DAMAGED &&
                        enemyTeam.stream().anyMatch(g -> g.getId().equals(event.getTargetId()))) {

                    Optional<Guardian> targetOpt = enemyTeam.stream()
                            .filter(g -> g.getId().equals(event.getTargetId()))
                            .findFirst();

                    if (targetOpt.isPresent()) {
                        Guardian target = targetOpt.get();
                        target.addBuff(new Buff(BuffType.DISEASED, 2, 30 * level));
                        battleLogs.add(new BattleLog(battleId, round, "阎罗审判（场下）", name, target.getName(),
                                30 * level, EffectType.DEBUFF));
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
                                            debuffCount, EffectType.BUFF));
                                }
                            });
                }
                break;
        }
    }
    // 处理死亡事件
    public void handleDeathEvent(BattleEvent event, List<Guardian> ownTeam, List<Guardian> enemyTeam,
                                 boolean isEnemyDeath, List<BattleLog> battleLogs, String battleId, int round) {
        if (!isAlive()) return;

        // 洛神：洛水歌声（敌方死亡时提升我方场上攻击）
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
                        this.name, fieldAlly.getName(), attackBoost, EffectType.ATTACK_UP));
            }
        }

        // 牛魔王：鲜血盛宴（任何生物死亡时增加生命上限）
        if (name.equals("牛魔王") && niumoWangHpStacks < 3) {
            int hpBoost = 117 * level;
            this.currentMaxHp += hpBoost;
            niumoWangHpStacks++;

            battleLogs.add(new BattleLog(battleId, round, "鲜血盛宴（叠加" + niumoWangHpStacks + "层）",
                    this.name, "自身", hpBoost, EffectType.HP_UP));
        }

        // 白骨精：白骨噬魂
        if (name.equals("白骨精") && isEnemyDeath) {
            int addAtk = 120 * level;
            addAttack(addAtk);
            battleLogs.add(new BattleLog(battleId, round, "白骨噬魂", name, "自身",
                    addAtk, EffectType.ATTACK_UP));
        }
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

    public void cleanExpiredBuffs() {
        buffs.removeIf(buff -> buff.getDuration() == 0 && buff.getType() != BuffType.POISONED);
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
                .filter(b -> b.getType() == BuffType.ATTACK_UP || b.getType() == BuffType.BLOODLUST)
                .mapToInt(Buff::getValue)
                .sum();
    }

    public int getCurrentSpeed() {
        return currentSpeed + buffs.stream()
                .filter(Objects::nonNull)
                .filter(b -> b.getType() == BuffType.SPEED_UP || b.getType() == BuffType.BLOODLUST)
                .mapToInt(Buff::getValue)
                .sum();
    }

    // getter/setter方法...
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
    public void setOnField(boolean onField) { isOnField = onField; }
    public boolean isAlive() { return isAlive; }
    public void setAlive(boolean alive) { isAlive = alive; }
    public List<Buff> getBuffs() { return new ArrayList<>(buffs); }
    public Map<String, Integer> getSkillTriggers() { return new HashMap<>(skillTriggers); }
    public Random getRandom() { return random; }
}