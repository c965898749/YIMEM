package com.sy.model.game;

public enum Profession {
    WARRIOR("武圣"), IMMORTAL("仙灵"), GOD("神将");
    private final String name;
    Profession(String name) { this.name = name; }
    public String getName() { return name; }
}
