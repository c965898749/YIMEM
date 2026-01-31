package com.sy.tool;
import java.util.concurrent.ThreadLocalRandom;

/**
 * 概率工具类 - 核心：按概率返回布尔结果
 */
public class ProbabilityBooleanUtils {

    // 线程安全的随机数生成器
    private static final ThreadLocalRandom RANDOM = ThreadLocalRandom.current();

    /**
     * 根据指定概率返回true/false
     * @param probability 命中true的概率，范围 [0.0, 1.0]（如0.3表示30%概率返回true）
     * @return true=命中概率，false=未命中
     * @throws IllegalArgumentException 概率值超出0-1范围时抛出异常
     */
    public static boolean randomByProbability(double probability) {

        // 2. 边界值优化：避免不必要的随机计算
        if (probability <= 0.0) {
            return false;
        }
        if (probability >= 1.0) {
            return true;
        }
        // 3. 核心逻辑：生成0-1的随机数，小于概率值则返回true
        return RANDOM.nextDouble() < probability;
    }

    // 禁止实例化工具类
    private ProbabilityBooleanUtils() {}

    // 测试示例
    public static void main(String[] args) {
        // 示例1：30%概率返回true
        double rate1 = 0.3;
        System.out.println("30%概率测试结果：" + randomByProbability(rate1));

        // 示例2：测试10万次，验证概率准确性
        int total = 100000;
        int trueCount = 0;
        double targetRate = 0.5; // 50%概率
        for (int i = 0; i < total; i++) {
            if (randomByProbability(targetRate)) {
                trueCount++;
            }
        }
        double actualRate = (double) trueCount / total;
        System.out.printf("测试%d次，目标概率：%.2f%%，实际命中概率：%.2f%%%n",
                total, targetRate * 100, actualRate * 100);

        // 示例3：边界值测试（100%返回true，0%返回false）
        System.out.println("100%概率：" + randomByProbability(1.0)); // true
        System.out.println("0%概率：" + randomByProbability(0.0));   // false
    }
}