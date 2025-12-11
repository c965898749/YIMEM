package com.sy.model.game;

public enum Race {
    IMMORTAL("sacred"), DEMON("dark");
    private String name;
    Race(String name) { this.name = name; }
    // 核心：数据库值（sacred/dark）→ 枚举实例
    public static Race fromName(String name) {
        // 空值校验
        if (name == null) {
            throw new IllegalArgumentException("Race名称不能为空");
        }
        // 遍历匹配枚举的name属性
        for (Race race : Race.values()) {
            if (race.name.equals(name)) {
                return race;
            }
        }
        // 匹配失败抛异常，明确错误
        throw new IllegalArgumentException("无效的Race值: " + name);
    }

}