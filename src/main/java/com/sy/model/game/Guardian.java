package com.sy.model.game;

import java.util.HashMap;
import java.util.Map;
public class Guardian {
    private String name;
    private Camp camp;
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
    private Map<EffectType, Integer> effects;

    // 构造函数
    public Guardian(String name, Camp camp, int position, Profession profession, Race race,
                    int maxHp, int attack, int speed) {
        this.name = name;
        this.camp = camp;
        this.position = position;
        this.profession = profession;
        this.race = race;
        this.level = 1;
        this.maxHp = maxHp;
        this.currentHp = maxHp;  // 初始当前血量等于血量上限
        this.attack = attack;
        this.speed = speed;
        this.isDead = false;
        this.isOnField = false;
        this.buffStacks = 0;
        this.buffLuoShens = 0;
        this.effects = new HashMap<>();
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
    public String getName() { return name; }
    public Camp getCamp() { return camp; }
    public int getPosition() { return position; }
    public Profession getProfession() { return profession; }
    public Race getRace() { return race; }
    public int getLevel() { return level; }
    public void setLevel(int level) { this.level = level; }
    public int getMaxHp() { return maxHp; }
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
    public void setBuffStacks(int buffStacks) { this.buffStacks = buffStacks; }
    public void setBuffLuoShens(int buffLuoShens) { this.buffLuoShens = buffLuoShens; }
    public Map<EffectType, Integer> getEffects() { return effects; }
}

