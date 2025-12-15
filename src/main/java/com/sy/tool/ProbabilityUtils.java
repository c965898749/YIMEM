package com.sy.tool;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.security.SecureRandom;
import java.util.*;
import java.util.concurrent.ThreadLocalRandom;

/**
 * 概率工具类，提供各类概率相关操作
 */
public class ProbabilityUtils {
    // 安全随机数生成器（高安全性场景）
    private static final SecureRandom SECURE_RANDOM = new SecureRandom();
    // 线程本地随机数（高性能场景）
    private static final ThreadLocalRandom THREAD_RANDOM = ThreadLocalRandom.current();

    /**
     * 私有化构造函数，禁止实例化
     */
    private ProbabilityUtils() {
        throw new UnsupportedOperationException("工具类禁止实例化");
    }

    // ===================== 基础概率判断 =====================

    /**
     * 判断是否命中指定概率（百分比，0-100）
     * 例如：probability=30 表示 30% 的概率命中
     *
     * @param probability 概率百分比（0-100）
     * @return true=命中，false=未命中
     */
    public static boolean hitProbability(double probability) {
        if (probability <= 0) {
            return false;
        }
        if (probability >= 100) {
            return true;
        }
        return THREAD_RANDOM.nextDouble() * 100 < probability;
    }

    /**
     * 判断是否命中指定概率（小数形式，0-1）
     * 例如：probability=0.3 表示 30% 的概率命中
     *
     * @param probability 概率（0-1）
     * @return true=命中，false=未命中
     */
    public static boolean hitProbabilityDecimal(double probability) {
        if (probability <= 0D) {
            return false;
        }
        if (probability >= 1D) {
            return true;
        }
        return THREAD_RANDOM.nextDouble() < probability;
    }

    // ===================== 权重随机选择 =====================

    /**
     * 根据权重随机选择一个元素（整数权重）
     *
     * @param elements 元素-权重映射
     * @param <T>      元素类型
     * @return 选中的元素，若映射为空返回null
     */
    public static <T> T randomByWeight(Map<T, Integer> elements) {
        if (elements == null || elements.isEmpty()) {
            return null;
        }

        // 计算总权重
        int totalWeight = elements.values().stream().mapToInt(Integer::intValue).sum();
        if (totalWeight <= 0) {
            throw new IllegalArgumentException("总权重必须大于0");
        }

        // 生成随机数
        int random = THREAD_RANDOM.nextInt(totalWeight);
        int current = 0;

        // 遍历权重区间
        for (Map.Entry<T, Integer> entry : elements.entrySet()) {
            current += entry.getValue();
            if (random < current) {
                return entry.getKey();
            }
        }

        // 理论上不会走到这里，兜底返回第一个元素
        return elements.keySet().iterator().next();
    }

    /**
     * 根据权重随机选择一个元素（小数权重）
     *
     * @param elements 元素-权重映射
     * @param <T>      元素类型
     * @return 选中的元素，若映射为空返回null
     */
    public static <T> T randomByWeightDecimal(Map<T, Double> elements) {
        if (elements == null || elements.isEmpty()) {
            return null;
        }

        // 计算总权重
        double totalWeight = elements.values().stream().mapToDouble(Double::doubleValue).sum();
        if (totalWeight <= 0D) {
            throw new IllegalArgumentException("总权重必须大于0");
        }

        // 生成随机数
        double random = THREAD_RANDOM.nextDouble() * totalWeight;
        double current = 0D;

        // 遍历权重区间
        for (Map.Entry<T, Double> entry : elements.entrySet()) {
            current += entry.getValue();
            if (random < current) {
                return entry.getKey();
            }
        }

        // 兜底返回第一个元素
        return elements.keySet().iterator().next();
    }

    // ===================== 随机数生成 =====================

    /**
     * 生成指定范围的随机整数（包含min和max）
     *
     * @param min 最小值
     * @param max 最大值
     * @return 随机整数
     */
    public static int randomInt(int min, int max) {
        if (min > max) {
            throw new IllegalArgumentException("最小值不能大于最大值");
        }
        return THREAD_RANDOM.nextInt(max - min + 1) + min;
    }

    /**
     * 生成指定范围的随机小数（包含min，不包含max）
     *
     * @param min 最小值
     * @param max 最大值
     * @return 随机小数
     */
    public static double randomDouble(double min, double max) {
        if (min > max) {
            throw new IllegalArgumentException("最小值不能大于最大值");
        }
        return THREAD_RANDOM.nextDouble() * (max - min) + min;
    }

    /**
     * 生成正态分布随机数（高斯分布）
     *
     * @param mean  均值
     * @param sigma 标准差
     * @return 正态分布随机数
     */
    public static double randomGaussian(double mean, double sigma) {
        return THREAD_RANDOM.nextGaussian() * sigma + mean;
    }

    // ===================== 高级概率操作 =====================

    /**
     * 按概率比例随机选择多个不重复元素（不放回）
     *
     * @param elements 元素-权重映射
     * @param count    选择数量
     * @param <T>      元素类型
     * @return 选中的元素列表
     */
    public static <T> List<T> randomMultipleByWeight(Map<T, Integer> elements, int count) {
        if (elements == null || elements.isEmpty()) {
            return Collections.emptyList();
        }
        if (count <= 0) {
            return Collections.emptyList();
        }
        if (count > elements.size()) {
            throw new IllegalArgumentException("选择数量不能超过元素总数");
        }

        List<T> result = new ArrayList<>();
        Map<T, Integer> tempMap = new HashMap<>(elements);

        for (int i = 0; i < count; i++) {
            T selected = randomByWeight(tempMap);
            result.add(selected);
            tempMap.remove(selected); // 移除已选中元素，不放回
        }

        return result;
    }

    /**
     * 计算概率百分比（保留指定小数位数）
     *
     * @param numerator   分子（命中次数）
     * @param denominator 分母（总次数）
     * @param scale       小数位数
     * @return 概率百分比
     */
    public static BigDecimal calculateProbabilityPercent(long numerator, long denominator, int scale) {
        if (denominator == 0) {
            return BigDecimal.ZERO;
        }
        return new BigDecimal(numerator)
                .multiply(new BigDecimal(100))
                .divide(new BigDecimal(denominator), scale, RoundingMode.HALF_UP);
    }

    /**
     * 安全随机数生成（适用于密码/令牌等场景）
     *
     * @param length 随机字节数组长度
     * @return 随机字节数组
     */
    public static byte[] generateSecureRandomBytes(int length) {
        if (length <= 0) {
            throw new IllegalArgumentException("长度必须大于0");
        }
        byte[] bytes = new byte[length];
        SECURE_RANDOM.nextBytes(bytes);
        return bytes;
    }
}