package com.sy.tool;

import java.util.Scanner;

/**
 * 数值更新工具类
 * 规则：输入一个值，按"起始20，每次加40，到1000则不变"更新
 */
public class ValueUpdateUtil {
    // 初始值、步长、最大值常量
    private static final int INIT_VALUE = 20;
    private static final int STEP = 40;
    private static final int MAX_VALUE = 1000;

    /**
     * 根据输入值计算下一个值（核心方法）
     * @param inputValue 输入的当前值
     * @return 按照规则更新后的值
     */
    public static int calculateNextValue(int inputValue) {
        // 1. 如果输入值小于初始值20，默认从20开始
        if (inputValue < INIT_VALUE) {
            return INIT_VALUE;
        }
        // 2. 如果输入值已经达到/超过1000，返回1000
        else if (inputValue >= MAX_VALUE) {
            return MAX_VALUE;
        }
        // 3. 否则，输入值 + 40（如果超过1000则取1000）
        else {
            int nextValue = inputValue + STEP;
            return Math.min(nextValue, MAX_VALUE);
        }
    }

    // 交互式测试：手动输入值，实时查看更新结果
    public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in);
        System.out.println("===== 数值更新工具 =====");
        System.out.println("规则：起始20，每次加40，到1000则不变");
        System.out.println("输入 'q' 退出程序\n");

        while (true) {
            System.out.print("请输入当前值：");
            String input = scanner.nextLine();

            // 输入q退出
            if ("q".equalsIgnoreCase(input)) {
                System.out.println("程序退出！");
                break;
            }

            // 处理输入的数值
            try {
                int inputValue = Integer.parseInt(input);
                int nextValue = calculateNextValue(inputValue);
                System.out.println("更新后的值：" + nextValue + "\n");
            } catch (NumberFormatException e) {
                System.out.println("输入错误！请输入有效的数字或 'q' 退出\n");
            }
        }
        scanner.close();
    }
}