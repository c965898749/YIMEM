package com.sy.tool;

import java.util.HashMap;
import java.util.Map;

/**
 * 卡牌最大等级工具类（极简版）
 * 核心规则：
 * 1. 特殊卡牌：指定名称直接返回固定等级（优先级最高）；
 * 2. 普通卡牌：
 *    - 1/1.5/2/2.5/3/3.5星：等级 = 星级 × 5；
 *    - 4星及以上（含4/4.5/5...）：等级固定25级；
 * 3. 星级异常值（≤0）：默认按1星计算。
 */
public class CardMaxLevelUtils {

    // 特殊卡牌名称-最大等级映射（可直接添加/删除）
    private static final Map<String, Integer> SPECIAL_CARD_MAP = new HashMap<>();

    // 初始化特殊卡牌规则
    static {
        SPECIAL_CARD_MAP.put("女娲石", 1);
        SPECIAL_CARD_MAP.put("幼年年兽", 1);
        SPECIAL_CARD_MAP.put("成年年兽", 1);
        SPECIAL_CARD_MAP.put("孤魂野鬼", 1);
        SPECIAL_CARD_MAP.put("幼年天将", 1);
        SPECIAL_CARD_MAP.put("白无常", 1);
        SPECIAL_CARD_MAP.put("大夜明珠·紫", 1);
        SPECIAL_CARD_MAP.put("大夜明珠·蓝", 1);
        SPECIAL_CARD_MAP.put("大夜明珠·黄", 1);
        SPECIAL_CARD_MAP.put("大夜明珠·红", 1);
        SPECIAL_CARD_MAP.put("大夜明珠·青", 1);
        SPECIAL_CARD_MAP.put("小夜明珠·紫", 1);
        SPECIAL_CARD_MAP.put("小夜明珠·蓝", 1);
        SPECIAL_CARD_MAP.put("小夜明珠·黄", 1);
        SPECIAL_CARD_MAP.put("小夜明珠·红", 1);
        SPECIAL_CARD_MAP.put("小夜明珠·青", 1);
        SPECIAL_CARD_MAP.put("中夜明珠·紫", 1);
        SPECIAL_CARD_MAP.put("中夜明珠·蓝", 1);
        SPECIAL_CARD_MAP.put("中夜明珠·黄", 1);
        SPECIAL_CARD_MAP.put("中夜明珠·红", 1);
        SPECIAL_CARD_MAP.put("中夜明珠·青", 1);
        SPECIAL_CARD_MAP.put("魂力宝珠", 1);
        SPECIAL_CARD_MAP.put("崆峒印", 1);
        SPECIAL_CARD_MAP.put("崆峒印碎片", 1);
    }

    /**
     * 获取卡牌最大等级
     * @param cardName 卡牌名称（可为空，空则按普通卡牌处理）
     * @param starLevel 卡牌星级（支持1/1.5/2/2.5...等小数星级）
     * @return 卡牌最大等级
     */
    public static int getMaxLevel(String cardName, double starLevel) {
        // 1. 优先判断特殊卡牌（名称匹配则直接返回）
        if (cardName != null && SPECIAL_CARD_MAP.containsKey(cardName)) {
            return SPECIAL_CARD_MAP.get(cardName);
        }

//        // 2. 普通卡牌：星级校验（≤0则按1星算）
//        double validStar = Math.max(starLevel, 1.0);

        // 3. 按星级计算等级
        if (starLevel == 5.0) { // 4星及以上固定25级
            return 45;
        } else if (starLevel == 4.5) { // 4星及以上固定25级
            return 40;
        } else if(starLevel == 4.0){  // 1~3.5星：星级 × 5（保留整数）
            return 35;
        }  else if(starLevel == 3.5){  // 1~3.5星：星级 × 5（保留整数）
            return 30;
        } else if(starLevel == 3.0){  // 1~3.5星：星级 × 5（保留整数）
            return 25;
        } else if(starLevel == 2.5){  // 1~3.5星：星级 × 5（保留整数）
            return 20;
        } else if(starLevel == 2.0){  // 1~3.5星：星级 × 5（保留整数）
            return 15;
        } else if(starLevel == 1.5){  // 1~3.5星：星级 × 5（保留整数）
            return 10;
        } else {  // 1~3.5星：星级 × 5（保留整数）
            return 5;
        }
    }

//    // 测试示例
//    public static void main(String[] args) {
//        // 特殊卡牌（无视星级）
//        System.out.println(getMaxLevel("传说之刃", 3.5)); // 输出50
//        // 1星普通卡牌
//        System.out.println(getMaxLevel("普通剑", 1.0));   // 输出5
//        // 1.5星普通卡牌
//        System.out.println(getMaxLevel("普通弓", 1.5));   // 输出7（1.5×5=7.5→取整7）
//        // 3.5星普通卡牌
//        System.out.println(getMaxLevel("普通斧", 3.5));   // 输出17（3.5×5=17.5→取整17）
//        // 4星普通卡牌（固定25）
//        System.out.println(getMaxLevel("普通锤", 4.0));   // 输出25
//        // 5星普通卡牌（固定25）
//        System.out.println(getMaxLevel("普通盾", 5.5));   // 输出25
//        // 星级异常（0星→按1星算）
//        System.out.println(getMaxLevel("普通枪", 0.0));   // 输出5
//    }
}