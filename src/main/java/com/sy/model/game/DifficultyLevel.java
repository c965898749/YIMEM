package com.sy.model.game;

public enum DifficultyLevel {
    LOW("初级"), MIDDLE("精英"), HIGH("神话");
    private String name;
    DifficultyLevel(String name) { this.name = name; }
    public String getKey() {
        return name;
    }
    // 4. 提供getter方法，用于获取中文名称
    public String getName() {
        return this.name;
    }
    // 【可选】扩展：根据字符串（如"LOW"）获取枚举，再拿名称（适配传字符串的场景）
    public static DifficultyLevel getByCode(String code) {
        for (DifficultyLevel level : DifficultyLevel.values()) {
            if (level.name().equals(code)) { // name() 是枚举自带方法，获取常量名（如LOW）
                return level;
            }
        }
        throw new IllegalArgumentException("无效的等级编码：" + code);
    }
}
