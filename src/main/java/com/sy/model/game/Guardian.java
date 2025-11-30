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
    private Map<EffectType, Integer> effects = new HashMap<>();
    private int buffStacks = 0;
    private int chargeTurns = 0;

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
        this.currentHp = maxHp;
        this.attack = attack;
        this.speed = speed;
        this.isDead = false;
        this.isOnField = false;
    }

    // Getter和Setter方法
    public String getName() { return name; }
    public void setName(String name) { this.name = name; }

    public Camp getCamp() { return camp; }
    public void setCamp(Camp camp) { this.camp = camp; }

    public int getPosition() { return position; }
    public void setPosition(int position) { this.position = position; }

    public Profession getProfession() { return profession; }
    public void setProfession(Profession profession) { this.profession = profession; }

    public Race getRace() { return race; }
    public void setRace(Race race) { this.race = race; }

    public int getLevel() { return level; }
    public void setLevel(int level) { this.level = level; }

    public int getMaxHp() { return maxHp; }
    public void setMaxHp(int maxHp) { this.maxHp = maxHp; }

    public int getCurrentHp() { return currentHp; }
    public void setCurrentHp(int currentHp) {
        this.currentHp = Math.max(0, Math.min(currentHp, this.maxHp));
        if (this.currentHp <= 0) {
            this.isDead = true;
        }
    }

    public int getAttack() { return attack; }
    public void setAttack(int attack) { this.attack = attack; }

    public int getSpeed() { return speed; }
    public void setSpeed(int speed) { this.speed = speed; }

    public boolean isDead() { return isDead; }
    public void setDead(boolean dead) { isDead = dead; }

    public boolean isOnField() { return isOnField; }
    public void setOnField(boolean onField) { isOnField = onField; }

    public Map<EffectType, Integer> getEffects() { return effects; }
    public void setEffects(Map<EffectType, Integer> effects) { this.effects = effects; }

    public int getBuffStacks() { return buffStacks; }
    public void setBuffStacks(int buffStacks) { this.buffStacks = buffStacks; }

    public int getChargeTurns() { return chargeTurns; }
    public void setChargeTurns(int chargeTurns) { this.chargeTurns = chargeTurns; }
}
