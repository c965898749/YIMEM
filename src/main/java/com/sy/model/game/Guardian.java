package com.sy.model.game;

import java.math.BigDecimal;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
public class Guardian {
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
    private int buffLuoShens;
    private int buffNianShous;
    private int buffRandengs;
    private int buffLiuers;
    private int wlAtk;
    private int hyAtk;
    private int dsAtk;
    private int fdAtk;
    private int wlDef;
    private int hyDef;
    private int dsDef;
    private int fdDef;
    private int zlDef;
    private Map<EffectType, Integer> effects;

    // 构造函数
    public Guardian(String name, Camp camp, int position, Profession profession, Race race,
                    int maxHp, int attack, int speed, int level, BigDecimal star,
                   int wlAtk,
                   int hyAtk,
                   int dsAtk,
                   int fdAtk,
                   int wlDef,
                   int hyDef,
                   int dsDef,
                   int fdDef,
                   int zlDef) {
        this.name = name;
        this.camp = camp;
        this.position = position;
        this.profession = profession;
        this.race = race;
        this.level = level;
        this.maxHp = maxHp;
        this.currentHp = maxHp;  // 初始当前血量等于血量上限
        this.attack = attack;
        this.speed = speed;
        this.isDead = false;
        this.isOnField = false;
        this.buffStacks = 0;
        this.buffLuoShens = 0;
        this.buffNianShous = 0;
        this.star = star;
        this.effects = new HashMap<>();
        this.wlAtk=wlAtk;
        this.hyAtk=hyAtk;
        this.dsAtk=dsAtk;
        this.fdAtk=fdAtk;
        this.wlDef=wlDef;
        this.hyDef=hyDef;
        this.dsDef=dsDef;
        this.fdDef=fdDef;
        this.zlDef=zlDef;
    }

    // 核心方法1：判断是否包含某个具体的EffectType枚举
    public boolean hasEffect(EffectType targetEffect) {
        // 空指针防护：如果effects为null，直接返回false
        if (effects == null) {
            return false;
        }
        // Map的containsKey方法判断是否包含指定枚举key
        return effects.containsKey(targetEffect);
    }

    // 扩展方法2：判断是否包含多种效果中的任意一种
    public boolean hasAnyEffect(EffectType... targetEffects) {
        if (effects == null || targetEffects == null || targetEffects.length == 0) {
            return false;
        }
        for (EffectType effect : targetEffects) {
            if (effects.containsKey(effect)) {
                return true;
            }
        }
        return false;
    }

    // 扩展方法3：获取指定效果的层数/持续回合（如果有）
    public int getEffectStacks(EffectType targetEffect) {
        if (effects == null) {
            return 0;
        }
        // getOrDefault：有该效果返回对应值，无则返回0
        return effects.getOrDefault(targetEffect, 0);
    }

    // 设置血量上限（联动调整当前血量）
    public void setMaxHp(int maxHp) {
        this.maxHp = maxHp;
        this.maxHp = Math.max(this.maxHp, 0);      // 不低于0
        // 如果当前血量超过新的上限，调整为上限值
        if (this.currentHp > this.maxHp) {
            this.currentHp = this.maxHp;
        }
        // 如果血量为0，标记为死亡
        if (this.maxHp <= 0) {
            this.isDead = true;
            this.currentHp = 0;
            this.maxHp = 0;
        }
    }

    // 设置当前血量（确保不超过上限）
    public void setCurrentHp(int currentHp) {
        this.currentHp = Math.min(currentHp, this.maxHp);  // 不超过上限
        this.currentHp = Math.max(this.currentHp, 0);      // 不低于0

        // 如果血量为0，标记为死亡
        if (this.currentHp <= 0) {
            this.isDead = true;
            this.currentHp = 0;
        }
    }

    // 其他getter和setter方法
    public int getWlAtk() { return wlAtk; }
    public int getHyAtk() { return hyAtk; }
    public int getDsAtk() { return dsAtk; }
    public int getFdAtk() { return fdAtk; }
    public int getWlDef() { return wlDef; }
    public int getHyDef() { return hyDef; }
    public int getDsDef() { return dsDef; }
    public int getFdDef() { return fdDef; }
    public int getZlDef() { return zlDef; }

    public void setWlAtk(int wlAtk) { this.wlAtk=wlAtk; }
    public void setHyAtk(int hyAtk) { this.hyAtk=hyAtk; }
    public void setDsAtk(int dsAtk) { this.dsAtk=dsAtk; }
    public void setFdAtk(int fdAtk) { this.fdAtk=fdAtk; }
    public void setWlDef(int wlDef) { this.wlDef=wlDef; }
    public void setHyDef(int hyDef) { this.hyDef=hyDef; }
    public void setDsDef(int dsDef) { this.dsDef=dsDef; }
    public void setFdDef(int fdDef) { this.fdDef=fdDef; }
    public void setZlDef(int zlDef) { this.zlDef=zlDef; }

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
    public int getBuffNianShous() { return buffNianShous; }
    public int getBuffRandengs() { return buffRandengs; }
    public int getBuffLiuers() { return buffLiuers; }
    public void setStar(BigDecimal star) { this.star = star; }
    public void setBuffStacks(int buffStacks) { this.buffStacks = buffStacks; }
    public void setBuffLuoShens(int buffLuoShens) { this.buffLuoShens = buffLuoShens; }
    public void setBuffRandengs(int buffRandengs) { this.buffRandengs = buffRandengs; }
    public void setBuffLiuers(int buffLiuers) { this.buffLiuers = buffLiuers; }
    public void setBuffNianShous(int buffNianShous) { this.buffNianShous = buffNianShous; }
    public Map<EffectType, Integer> getEffects() { return effects; }
}

