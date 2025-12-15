package com.sy.model.game;

import lombok.Data;

import java.util.List;
import java.util.Map;

@Data
public class TokenDto {
    private String token;
    private String userId;
    private String id;
    private String str;
    private int finalLevel;       // 最终等级
    private int remainingExp;     // 当前等级的剩余经验
    private int totalSilverSpent; // 升级消耗的总银两
    private String difficultyLevel;
    private List<List<Object>> myMap; // 接收前端的str（JSON对象 → Java Map）
}
