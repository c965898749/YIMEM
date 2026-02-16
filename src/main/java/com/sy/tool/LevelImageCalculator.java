package com.sy.tool;

import com.sy.model.game.ImageLevelResult;
import java.util.Arrays;

/**
 * 关卡图片计算工具类（关联版）
 * 规则：
 * 1. 每张图包含最多7个关卡（最后一张可能不足），数字范围1-100
 * 2. 后一张图的第一个数字 = 前一张图的最后一个数字
 * 3. 若currentImageNumbers长度不足7个，nextImageNumbers返回空数组
 * 4. currentImageNumbers 是 上一个nextImageNumbers 的第一个数字对应的图片数组
 */
public class LevelImageCalculator {

    // 每张图片的最大关卡数
    private static final int LEVELS_PER_IMAGE = 7;
    // 数字上限
    private static final int MAX_NUMBER = 101;

    /**
     * 根据输入数字计算所属图片信息
     * @param targetNumber 1-100之间的数字
     * @return 包含位置、当前图数组、下一张图数组的结果对象
     * @throws IllegalArgumentException 输入数字超出范围时抛出异常
     */
    public static ImageLevelResult calculate(int targetNumber) {
        // 校验输入合法性
        if (targetNumber < 1 || targetNumber > MAX_NUMBER) {
            throw new IllegalArgumentException("输入数字必须在1到100之间");
        }

        // 步骤1：找到目标数字所属图片的起始值
        int currentStart = findImageStartByNumber(targetNumber);

        // 步骤2：获取当前图片的数字数组
        int[] currentImageArray = getImageArrayByStart(currentStart);

        // 步骤3：计算当前数字在图中的位置
        int positionInImage = findPositionInArray(currentImageArray, targetNumber);

        // 步骤4：获取下一张图片数组（当前数组不足7个则返回空）
        int[] nextImageArray = currentImageArray.length < LEVELS_PER_IMAGE
                ? new int[0]
                : getImageArrayByStart(currentImageArray[currentImageArray.length - 1]);

        // 返回封装好的结果对象
        return new ImageLevelResult(positionInImage, currentImageArray, nextImageArray);
    }

    /**
     * 根据目标数字，找到其所属图片的起始值
     * 核心逻辑：从1开始，依次推导每张图的起始/结束值，直到找到包含目标数字的图片
     * @param targetNumber 目标数字
     * @return 所属图片的起始值
     */
    private static int findImageStartByNumber(int targetNumber) {
        if (targetNumber == 1) {
            return 1;
        }

        int currentStart = 1;
        int[] currentArray = getImageArrayByStart(currentStart);

        // 循环推导，直到找到包含目标数字的图片
        while (!containsNumber(currentArray, targetNumber)) {
            // 下一张图的起始值 = 当前图的最后一个值
            currentStart = currentArray[currentArray.length - 1];
            currentArray = getImageArrayByStart(currentStart);
        }

        return currentStart;
    }

    /**
     * 根据起始数字，生成图片的数字数组
     * @param start 起始数字
     * @return 图片数字数组（最多7个，不超过100）
     */
    private static int[] getImageArrayByStart(int start) {
        if (start > MAX_NUMBER) {
            return new int[0];
        }

        // 结束数字 = 起始数字 + 6（最多7个），且不超过100
        int end = Math.min(start + LEVELS_PER_IMAGE - 1, MAX_NUMBER);
        int[] array = new int[end - start + 1];

        for (int i = 0; i < array.length; i++) {
            array[i] = start + i;
        }

        return array;
    }

    /**
     * 查找数字在数组中的位置（从1开始计数）
     * @param array 目标数组
     * @param number 要查找的数字
     * @return 位置（1-based）
     */
    private static int findPositionInArray(int[] array, int number) {
        for (int i = 0; i < array.length; i++) {
            if (array[i] == number) {
                return i + 1;
            }
        }
        return -1; // 理论上不会走到这里，因为输入已校验
    }

    /**
     * 判断数组是否包含指定数字
     * @param array 数组
     * @param number 数字
     * @return 是否包含
     */
    private static boolean containsNumber(int[] array, int number) {
        for (int num : array) {
            if (num == number) {
                return true;
            }
        }
        return false;
    }

    // 测试示例
    public static void main(String[] args) {
        // 测试数字1（当前数组[1,2,3,4,5,6,7]，下一张[7,8,9,10,11,12,13]）
        ImageLevelResult result1 = calculate(1);
        System.out.println("数字1的计算结果：");
        System.out.println("在图中的位置：第" + result1.getPositionInImage() + "位");
        System.out.println("当前图片数字数组：" + Arrays.toString(result1.getCurrentImageNumbers()));
        System.out.println("下一张图片数字数组：" + Arrays.toString(result1.getNextImageNumbers()));
        System.out.println("-------------------");

        // 测试数字7（当前数组[1,2,3,4,5,6,7]，下一张[7,8,9,10,11,12,13]）
        ImageLevelResult result7 = calculate(7);
        System.out.println("数字7的计算结果：");
        System.out.println("在图中的位置：第" + result7.getPositionInImage() + "位");
        System.out.println("当前图片数字数组：" + Arrays.toString(result7.getCurrentImageNumbers()));
        System.out.println("下一张图片数字数组：" + Arrays.toString(result7.getNextImageNumbers()));
        System.out.println("-------------------");

        // 测试数字8（当前数组[7,8,9,10,11,12,13]，下一张[13,14,15,16,17,18,19]）
        ImageLevelResult result8 = calculate(8);
        System.out.println("数字8的计算结果：");
        System.out.println("在图中的位置：第" + result8.getPositionInImage() + "位");
        System.out.println("当前图片数字数组：" + Arrays.toString(result8.getCurrentImageNumbers()));
        System.out.println("下一张图片数字数组：" + Arrays.toString(result8.getNextImageNumbers()));
        System.out.println("-------------------");

        // 测试数字98（当前数组[92,93,94,95,96,97,98]，下一张[98,99,100]）
        ImageLevelResult result98 = calculate(98);
        System.out.println("数字98的计算结果：");
        System.out.println("在图中的位置：第" + result98.getPositionInImage() + "位");
        System.out.println("当前图片数字数组：" + Arrays.toString(result98.getCurrentImageNumbers()));
        System.out.println("下一张图片数字数组：" + Arrays.toString(result98.getNextImageNumbers()));
        System.out.println("-------------------");

        // 测试数字100（当前数组[98,99,100]，长度3<7，下一张为空）
        ImageLevelResult result100 = calculate(100);
        System.out.println("数字100的计算结果：");
        System.out.println("在图中的位置：第" + result100.getPositionInImage() + "位");
        System.out.println("当前图片数字数组：" + Arrays.toString(result100.getCurrentImageNumbers()));
        System.out.println("下一张图片数字数组：" + Arrays.toString(result100.getNextImageNumbers()));
    }
}