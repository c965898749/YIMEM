package com.sy.model.game;

import lombok.Data;

@Data
public class LevelUpResult {
    private final int finalLevel;       // 最终等级
    private final int remainingExp;     // 当前等级的剩余经验
    private final int totalSilverSpent; // 升级消耗的总银两
    private final String id; // 升级消耗的总银两
//    private final String str; // 升级消耗的总银两

    public LevelUpResult(int finalLevel, int remainingExp, int totalSilverSpent, String id) {
        this.finalLevel = finalLevel;
        this.remainingExp = remainingExp;
        this.totalSilverSpent = totalSilverSpent;
        this.id = id;
//        this.str = str;
    }

}
