package com.sy.vo;

public enum ItemQuality {
    COMMON("1", 40),     // 权重40（最高）
    ELITE("2", 25),      // 新增中间品质，权重25
    UNCOMMON("3", 20),   // 权重20
    RARE("4", 10),       // 权重10
    LEGENDARY("5", 5);   // 权重5（最低）

    private final String qualityName;  // 对应商城表的quality字段
    private final int weight;          // 权重值

    ItemQuality(String qualityName, int weight) {
        this.qualityName = qualityName;
        this.weight = weight;
    }

    public String getQualityName() {
        return qualityName;
    }

    public int getWeight() {
        return weight;
    }

    // 根据品质名称获取枚举（用于匹配商城表数据）
    public static ItemQuality fromQualityName(String name) {
        for (ItemQuality q : values()) {
            if (q.qualityName.equals(name)) return q;
        }
        return null;
    }
}
