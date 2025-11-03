package com.sy.model.game;

public class QqCardExp {
    private Integer id;

    private Integer level;

    private String upgradeType;

    private Integer upgradeExp;

    private Integer gold;

    private Integer skillExp;

    public QqCardExp(Integer id, Integer level, String upgradeType, Integer upgradeExp, Integer gold, Integer skillExp) {
        this.id = id;
        this.level = level;
        this.upgradeType = upgradeType;
        this.upgradeExp = upgradeExp;
        this.gold = gold;
        this.skillExp = skillExp;
    }

    public QqCardExp() {
        super();
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public Integer getLevel() {
        return level;
    }

    public void setLevel(Integer level) {
        this.level = level;
    }

    public String getUpgradeType() {
        return upgradeType;
    }

    public void setUpgradeType(String upgradeType) {
        this.upgradeType = upgradeType == null ? null : upgradeType.trim();
    }

    public Integer getUpgradeExp() {
        return upgradeExp;
    }

    public void setUpgradeExp(Integer upgradeExp) {
        this.upgradeExp = upgradeExp;
    }

    public Integer getGold() {
        return gold;
    }

    public void setGold(Integer gold) {
        this.gold = gold;
    }

    public Integer getSkillExp() {
        return skillExp;
    }

    public void setSkillExp(Integer skillExp) {
        this.skillExp = skillExp;
    }
}