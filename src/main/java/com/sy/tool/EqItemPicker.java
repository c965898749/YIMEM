package com.sy.tool;

import com.sy.model.game.EqCard;
import com.sy.vo.EqItemQuality;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class EqItemPicker {
    // 动态物品池（支持新增物品）
    private List<EqCard> dynamicItemPool = new ArrayList<>();
    private Random random = new Random();

    // 新增物品到动态池
    public void addItem(EqCard item) {
        if (item != null) {
            dynamicItemPool.add(item);
        }
    }

    /**
     * 随机获取指定数量的物品（支持种类不足时重复获取）
     * @param count 目标数量（如16）
     * @return 随机物品列表
     */
    public List<EqCard> pickRandomItems(int count) {
        List<EqCard> result = new ArrayList<>();
        if (count <= 0) {
            System.out.println("获取数量必须大于0");
            return result;
        }
        if (dynamicItemPool.isEmpty()) {
            System.out.println("物品池为空，无法获取物品");
            return result;
        }

        // 1. 构建品质权重池（动态适配当前物品池的品质分布）
        List<EqItemQuality> qualityPool = new ArrayList<>();
        for (EqItemQuality quality : EqItemQuality.values()) {
            // 只添加有对应品质物品的权重（避免无物品的品质被选中）
            boolean hasQualityItem = dynamicItemPool.stream()
                    .anyMatch(item -> item.getStar().compareTo(new BigDecimal(quality.getQualityName()))==0);
            if (hasQualityItem) {
                for (int i = 0; i < quality.getWeight(); i++) {
                    qualityPool.add(quality);
                }
            }
        }

        // 2. 若所有品质都无可用物品（如库存全为0）
        if (qualityPool.isEmpty()) {
            System.out.println("所有物品库存不足");
            return result;
        }

        // 3. 循环获取指定数量的物品（允许重复）
        for (int i = 0; i < count; i++) {
            // 3.1 按权重随机选择品质
            EqItemQuality targetQuality = qualityPool.get(
                    random.nextInt(qualityPool.size())
            );

            // 3.2 筛选该品质下有库存的物品
            List<EqCard> candidates = new ArrayList<>();
            for (EqCard item : dynamicItemPool) {
                if (item.getStar().compareTo(new BigDecimal(targetQuality.getQualityName()))==0) {
                    candidates.add(item);
                }
            }

            // 3.3 从候选中随机选一个（必然有值，因qualityPool已过滤）
            EqCard selected = candidates.get(
                    random.nextInt(candidates.size())
            );
            result.add(selected);
        }

        return result;
    }

}
