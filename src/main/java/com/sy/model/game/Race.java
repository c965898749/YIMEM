package com.sy.model.game;

public enum Race {
    IMMORTAL_RACE("仙族"), DEMON_RACE("妖族");
    private final String name;
    Race(String name) { this.name = name; }
    public String getName() { return name; }
}
