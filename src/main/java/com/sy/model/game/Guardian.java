package com.sy.model.game;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

public class Guardian {
    // ========== 新增：唯一ID（标识释放者） ==========
    private String id;  // 可以用Long、String，根据你业务选

    private String name;
    private Camp camp;
    private BigDecimal star;
    private int position;
    private Profession profession;
    private Race race;
    private int level;
    private int maxHp;
    private int currentHp;
    private int attack;
    private int speed;
    private boolean isDead;
    private boolean isOnField;
    private int buffStacks;
    //只增加别人给你buff，自己的buff全部使用buffStacks
    //TODO 特殊bufff
    private int buffLuoShens;
    private int buffRandengs;
    private int buffYuRongWans;
    private int buffTianLuos;
    private int buffDongyues;

    public int getBuffXuanMins() {
        return buffXuanMins;
    }

    public void setBuffXuanMins(int buffXuanMins) {
        this.buffXuanMins = buffXuanMins;
    }

    private int buffXuanMins;
    private int wlAtk;
    private int hyAtk;
    private int dsAtk;
    private int fdAtk;
    private int wlDef;
    private int hyDef;
    private int dsDef;
    private int fdDef;
    private int zlDef;
    private int sex;
    private int flyup;

    public int getFlyup() {
        return flyup;
    }

    public void setFlyup(int flyup) {
        this.flyup = flyup;
    }

    // 换成 List 存储效果实例，解决多来源混乱问题
    private List<EffectInstance> effects = new ArrayList<>();

    // ========== 构造函数：新增 id 参数 ==========
    public Guardian(String id,  // 新增ID
                    String name, Camp camp, int position, Profession profession, Race race,
                    int maxHp, int attack, int speed, int level, BigDecimal star,
                    int wlAtk, int hyAtk, int dsAtk, int fdAtk,
                    int wlDef, int hyDef, int dsDef, int fdDef, int zlDef,int flyup,int sex) {
        this.id = id;  // 初始化ID
        this.name = name;
        this.camp = camp;
        this.position = position;
        this.profession = profession;
        this.race = race;
        this.level = level;
        this.maxHp = maxHp;
        this.currentHp = maxHp;
        this.attack = attack;
        this.speed = speed;
        this.isDead = false;
        this.isOnField = false;
        this.buffStacks = 0;
        this.buffLuoShens = 0;
        this.buffRandengs = 0;
        this.star = star;
        this.wlAtk = wlAtk;
        this.hyAtk = hyAtk;
        this.dsAtk = dsAtk;
        this.fdAtk = fdAtk;
        this.wlDef = wlDef;
        this.hyDef = hyDef;
        this.dsDef = dsDef;
        this.fdDef = fdDef;
        this.zlDef = zlDef;
        this.sex = sex;
        this.flyup = flyup;
    }
    /**
     * 是否处于眩晕/昏睡/冰冻等控制中
     */
    public boolean isStunned() {
        return hasEffect(EffectType.STUN);
    }
    public boolean isMaxHpNoDown() {
        return hasEffect(EffectType.MAX_HP_NO_DOWN);
    }
    public boolean isHealDown() {
        return hasEffect(EffectType.HEAL_DOWN);
    }
    public boolean isSilence() {
        return hasEffect(EffectType.SILENCE);
    }
    public boolean isPoison() {
        return hasEffect(EffectType.POISON);
    }
    public boolean isFireBoost() {
        return hasEffect(EffectType.FIRE_BOOST);
    }
    /**
     * 添加控制类效果：眩晕、冰冻、沉默等
     * 只会保留【回合最长】的那一个，不会叠加多个
     */
    public void addControlEffect(EffectType type, int totalRound, String casterId) {
        // 先看身上有没有这个控制
        EffectInstance existing = null;
        for (EffectInstance e : effects) {
            if (e.getType() == type) {
                existing = e;
                break;
            }
        }

        // 如果没有 → 直接加
        if (existing == null) {
            effects.add(new EffectInstance(type, 1, totalRound, casterId));
            return;
        }

        // 如果有 → 比较回合：新回合更长才替换
        if (totalRound > existing.getRemainRound()) {
            effects.remove(existing);
            effects.add(new EffectInstance(type, 1, totalRound, casterId));
        }
    }
    // ========== 新增：ID的GETTER/SETTER ==========
    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    // ====================== 原有方法全部保留 ======================
    public boolean hasEffect(EffectType targetEffect) {
        for (EffectInstance e : effects) {
            if (e.getType() == targetEffect) {
                return true;
            }
        }
        return false;
    }

    public boolean hasAnyEffect(EffectType... targetEffects) {
        if (effects == null || targetEffects == null) return false;
        for (EffectType type : targetEffects) {
            if (hasEffect(type)) return true;
        }
        return false;
    }

    public int getEffectStacks(EffectType targetEffect) {
        int count = 0;
        for (EffectInstance e : effects) {
            if (e.getType() == targetEffect) {
                count++;
            }
        }
        return count;
    }

    public int getEffectTotalValue(EffectType type) {
        int sum = 0;
        for (EffectInstance e : effects) {
            if (e.getType() == type) {
                sum += e.getValue();
            }
        }
        return sum;
    }

    public void addEffect(EffectType type, int value, int round, String casterId) {
        effects.add(new EffectInstance(type, value, round, casterId));
    }

    public void tickAllEffects() {
        Iterator<EffectInstance> it = effects.iterator();
        while (it.hasNext()) {
            EffectInstance e = it.next();
            e.tickRound();
            if (e.getRemainRound() <= 0) {
                it.remove();
            }
        }
    }
    
    public void  remove(EffectType effectType){
        Iterator<EffectInstance> it = effects.iterator();
        while (it.hasNext()) {
            EffectInstance e = it.next();
            if (e.getType()==effectType) {
                it.remove();
            }
        }
    }

    public List<EffectInstance> getEffects() {
        return effects;
    }

    public void setMaxHp(int maxHp) {
        this.maxHp = Math.max(maxHp, 0);
        if (currentHp > this.maxHp) currentHp = this.maxHp;
        if (this.maxHp <= 0) {
            isDead = true;
            currentHp = 0;
        }
    }

    public void setCurrentHp(int currentHp) {
        this.currentHp = Math.min(Math.max(currentHp, 0), maxHp);
        if (this.currentHp <= 0) {
            isDead = true;
            this.currentHp = 0;
        }
    }
    /**
     * 回合结束时调用：
     * 所有buff剩余回合 -1
     * 回合 <=0 的buff自动移除
     */
    public void onRoundEndBuffTick() {
        Iterator<EffectInstance> it = effects.iterator();
        while (it.hasNext()) {
            EffectInstance buff = it.next();

            // 回合 -1
            buff.tickRound();

            // 如果回合没了，直接删掉这个buff
            if (buff.getRemainRound() <= 0) {
                it.remove();
            }
        }
    }
    public int getWlAtk() { return wlAtk; }
    public int getHyAtk() { return hyAtk; }
    public int getDsAtk() { return dsAtk; }
    public int getFdAtk() { return fdAtk; }
    public int getWlDef() { return wlDef; }
    public int getHyDef() { return hyDef; }
    public int getDsDef() { return dsDef; }
    public int getFdDef() { return fdDef; }
    public int getZlDef() { return zlDef; }
    public int getSex() { return sex; }

    public void setWlAtk(int wlAtk) { this.wlAtk = wlAtk; }
    public void setHyAtk(int hyAtk) { this.hyAtk = hyAtk; }
    public void setDsAtk(int dsAtk) { this.dsAtk = dsAtk; }
    public void setFdAtk(int fdAtk) { this.fdAtk = fdAtk; }
    public void setWlDef(int wlDef) { this.wlDef = wlDef; }
    public void setHyDef(int hyDef) { this.hyDef = hyDef; }
    public void setDsDef(int dsDef) { this.dsDef = dsDef; }
    public void setFdDef(int fdDef) { this.fdDef = fdDef; }
    public void setZlDef(int zlDef) { this.zlDef = zlDef; }
    public void setSex(int sex) { this.sex = sex; }

    public String getName() { return name; }
    public Camp getCamp() { return camp; }
    public int getPosition() { return position; }
    public Profession getProfession() { return profession; }
    public Race getRace() { return race; }
    public int getLevel() { return level; }
    public void setLevel(int level) { this.level = level; }
    public int getMaxHp() { return maxHp; }
    public BigDecimal getStar() { return star; }
    public int getCurrentHp() { return currentHp; }
    public int getAttack() { return attack; }
    public void setAttack(int attack) { this.attack = attack; }
    public int getSpeed() { return speed; }
    public void setSpeed(int speed) { this.speed = speed; }
    public boolean isDead() { return isDead; }
    public void setDead(boolean dead) { isDead = dead; }
    public boolean isOnField() { return isOnField; }
    public void setOnField(boolean onField) { isOnField = onField; }
    public int getBuffStacks() { return buffStacks; }
    public int getBuffLuoShens() { return buffLuoShens; }
    public int getBuffRandengs() { return buffRandengs; }
    public int getBuffYuRongWans() { return buffYuRongWans; }
    public int getBuffTianLuos() { return buffTianLuos; }
    public int getBuffDongyues() { return buffDongyues; }
    public void setStar(BigDecimal star) { this.star = star; }
    public void setBuffStacks(int buffStacks) { this.buffStacks = buffStacks; }
    public void setBuffLuoShens(int buffLuoShens) { this.buffLuoShens = buffLuoShens; }
    public void setBuffRandengs(int buffRandengs) { this.buffRandengs = buffRandengs; }
    public void setBuffYuRongWans(int buffYuRongWans) { this.buffYuRongWans = buffYuRongWans; }
    public void setBuffTianLuos(int buffTianLuos) { this.buffTianLuos = buffTianLuos; }
    public void setBuffDongyues(int buffDongyues) { this.buffDongyues = buffDongyues; }
}