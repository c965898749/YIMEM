package com.sy.model.game;

public enum Profession {
    WARRIOR("武圣"), IMMORTAL("仙灵"), GOD("神将");
    private String name;
    Profession(String name) { this.name = name; }
    public static Profession fromName(String name) {
        if (name == null) {
            throw new IllegalArgumentException("Profession名称不能为空");
        }
        for (Profession profession : Profession.values()) {
            if (profession.name.equals(name)) {
                return profession;
            }
        }
        throw new IllegalArgumentException("无效的Profession名称: " + name);
    }
}
