package com.sy.tool;

/**
 * 材料合成工具类（极简版）
 * 核心功能：计算可合成数量 + 剩余（返还）材料数
 */
public class MaterialSynthesisUtil {
    // 默认合成1个需要7个材料
    private static final int DEFAULT_NEEDED = 7;

    /**
     * 核心计算方法（返回数组：[可合成数量, 剩余材料数]）
     * @param total 总材料数
     * @param neededPer 合成1个需要的材料数
     * @return int[2]：索引0=可合成数量，索引1=剩余（返还）数
     */
    public static int[] calculate(int total, int neededPer) {
        if (neededPer <= 0 || total < 0) {
            throw new IllegalArgumentException("参数不合法：材料数需为非负数，合成单量需大于0");
        }
        int synthesisCount = total / neededPer; // 可合成数量
        int remaining = total - synthesisCount * neededPer; // 剩余（返还）数
        return new int[]{synthesisCount, remaining};
    }

    /**
     * 重载方法：使用默认7个材料合成1个
     * @param total 总材料数
     * @return int[2]：[可合成数量, 剩余数]
     */
    public static int[] calculate(int total) {
        return calculate(total, DEFAULT_NEEDED);
    }

    // 测试示例
    public static void main(String[] args) {
        // 测试：10个材料，默认7个合成1个
        int[] result = calculate(10);
        System.out.println("可合成数量：" + result[0]); // 输出1
        System.out.println("剩余（返还）数：" + result[1]); // 输出3

        // 自定义：15个材料，每个需要5个
        int[] customResult = calculate(15, 5);
        System.out.println("可合成数量：" + customResult[0]); // 输出3
        System.out.println("剩余（返还）数：" + customResult[1]); // 输出0
    }
}
