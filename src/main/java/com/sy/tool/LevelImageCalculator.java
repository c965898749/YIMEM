package com.sy.tool;

/**
 * 关卡图片计算工具类（返回数组版）
 * 规则：每张图包含7个关卡，第1张：1-7，第2张：8-14，依此类推（100以内）
 * 返回结果：二维数组，[0] = 当前图所有数字，[1] = 下一张图所有数字（无则为空数组）
 */

import com.sy.model.game.ImageLevelResult;

/**
 * 关卡图片计算工具类（含位置+数组版）
 * 规则：每张图包含7个关卡，第1张：1-7，第2张：8-14，依此类推（100以内）
 */
public class LevelImageCalculator {

    // 每张图片的关卡数
    private static final int LEVELS_PER_IMAGE = 7;
    // 数字上限
    private static final int MAX_NUMBER = 100;

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

        // 计算所属图片序号（第几张图）
        int imageIndex = (targetNumber - 1) / LEVELS_PER_IMAGE + 1;
        // 计算当前数字在图中的位置（第几位，1-7）
        int positionInImage = (targetNumber - 1) % LEVELS_PER_IMAGE + 1;

        // 获取当前图片的数字数组
        int[] currentImageArray = getImageAllNumbersAsArray(imageIndex);
        // 获取下一张图片的数字数组（无则返回空数组）
        int[] nextImageArray = (imageIndex + 1) * LEVELS_PER_IMAGE <= MAX_NUMBER
                ? getImageAllNumbersAsArray(imageIndex + 1)
                : new int[0];

        // 返回封装好的结果对象
        return new ImageLevelResult(positionInImage, currentImageArray, nextImageArray);
    }

    /**
     * 辅助方法：根据图片序号，获取该图片包含的所有数字（返回数组）
     * @param imageIndex 图片序号（第几张图）
     * @return 该图片的数字数组（无则返回空数组）
     */
    private static int[] getImageAllNumbersAsArray(int imageIndex) {
        int start = (imageIndex - 1) * LEVELS_PER_IMAGE + 1;
        int end = Math.min(imageIndex * LEVELS_PER_IMAGE, MAX_NUMBER);

        if (start > MAX_NUMBER) {
            return new int[0];
        }

        int[] numbers = new int[end - start + 1];
        for (int i = 0; i < numbers.length; i++) {
            numbers[i] = start + i;
        }
        return numbers;
    }



    // 测试示例
    public static void main(String[] args) {
        // 测试数字10
        ImageLevelResult result10 = calculate(1);
        System.out.println("数字10的计算结果：");
        System.out.println("在图中的位置：第" + result10.getPositionInImage() + "位");
        System.out.println("当前图片数字数组：" + java.util.Arrays.toString(result10.getCurrentImageNumbers()));
        System.out.println("下一张图片数字数组：" + java.util.Arrays.toString(result10.getNextImageNumbers()));
        System.out.println("-------------------");
//
//        // 测试数字98
//        ImageLevelResult result98 = calculate(98);
//        System.out.println("数字98的计算结果：");
//        System.out.println("在图中的位置：第" + result98.getPositionInImage() + "位");
//        System.out.println("当前图片数字数组：" + java.util.Arrays.toString(result98.getCurrentImageNumbers()));
//        System.out.println("下一张图片数字数组：" + java.util.Arrays.toString(result98.getNextImageNumbers()));
//        System.out.println("-------------------");
//
//        // 测试数字100
//        ImageLevelResult result100 = calculate(100);
//        System.out.println("数字100的计算结果：");
//        System.out.println("在图中的位置：第" + result100.getPositionInImage() + "位");
//        System.out.println("当前图片数字数组：" + java.util.Arrays.toString(result100.getCurrentImageNumbers()));
//        System.out.println("下一张图片数字数组：" + java.util.Arrays.toString(result100.getNextImageNumbers()));
    }
}
