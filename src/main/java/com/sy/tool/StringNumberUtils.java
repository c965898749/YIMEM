package com.sy.tool;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * 字符串数字处理工具类
 * 提供字符串中数字的倍数计算、占位符替换等功能
 *
 * @author 工具类生成
 * @version 1.0
 */
public final class StringNumberUtils {

    // 匹配整数的正则表达式（核心正则）
    private static final Pattern INTEGER_PATTERN = Pattern.compile("\\d+");
    // 匹配小数的正则表达式（支持整数/小数）
    private static final Pattern DECIMAL_PATTERN = Pattern.compile("\\d+\\.?\\d*");

    /**
     * 私有构造器，防止工具类实例化
     */
    private StringNumberUtils() {
        throw new AssertionError("工具类禁止实例化");
    }

    // ==================== 数字倍数处理 ====================

    /**
     * 将字符串中所有数字（整数）乘以指定倍数，结果取整
     *
     * @param original 原始字符串，不可为 null
     * @param multiple 倍数（正数，0 < multiple）
     * @return 替换后的字符串
     * @throws IllegalArgumentException 当 original 为 null 或 multiple ≤ 0 时抛出
     */
    public static String multiplyAllIntNumbers(String original, double multiple) {
        return multiplyAllNumbers(original, multiple, true);
    }

    /**
     * 将字符串中所有数字（支持小数）乘以指定倍数，结果保留小数
     *
     * @param original 原始字符串，不可为 null
     * @param multiple 倍数（正数，0 < multiple）
     * @return 替换后的字符串
     * @throws IllegalArgumentException 当 original 为 null 或 multiple ≤ 0 时抛出
     */
    public static String multiplyAllDecimalNumbers(String original, double multiple) {
        return multiplyAllNumbers(original, multiple, false);
    }

    /**
     * 为字符串中指定位置的数字设置不同倍数（整数，结果取整）
     *
     * @param original  原始字符串，不可为 null
     * @param multiples 倍数数组（第1个元素对应第1个数字，以此类推），不可为 null
     * @return 替换后的字符串
     * @throws IllegalArgumentException 当 original/multiples 为 null 或倍数 ≤ 0 时抛出
     */
    public static String multiplySpecifiedPositionIntNumbers(String original, double[] multiples) {
        return multiplySpecifiedPositionNumbers(original, multiples, true);
    }

    // ==================== 私有核心方法（封装通用逻辑） ====================

    /**
     * 核心方法：处理所有数字的倍数计算
     *
     * @param original  原始字符串
     * @param multiple  倍数
     * @param isInteger 是否只处理整数（true=整数取整，false=小数保留）
     * @return 替换后的字符串
     */
    private static String multiplyAllNumbers(String original, double multiple, boolean isInteger) {
        // 参数校验
        validateParams(original, multiple);
        Pattern pattern = isInteger ? INTEGER_PATTERN : DECIMAL_PATTERN;
        Matcher matcher = pattern.matcher(original);
        StringBuffer sb = new StringBuffer();

        while (matcher.find()) {
            double oldNum = Double.parseDouble(matcher.group());
            double newNum = oldNum * multiple;
            // 根据是否整数，决定输出格式
            String replaceStr = isInteger ? String.valueOf(Math.round(newNum)) : String.valueOf(newNum);
            matcher.appendReplacement(sb, replaceStr);
        }
        matcher.appendTail(sb);
        return sb.toString();
    }

    /**
     * 核心方法：处理指定位置数字的倍数计算
     *
     * @param original  原始字符串
     * @param multiples 倍数数组
     * @param isInteger 是否只处理整数
     * @return 替换后的字符串
     */
    private static String multiplySpecifiedPositionNumbers(String original, double[] multiples, boolean isInteger) {
        // 参数校验
        if (original == null) {
            throw new IllegalArgumentException("原始字符串不可为null");
        }
        if (multiples == null || multiples.length == 0) {
            throw new IllegalArgumentException("倍数数组不可为null或空");
        }
        for (double m : multiples) {
            if (m <= 0) {
                throw new IllegalArgumentException("倍数必须大于0，当前存在非法倍数：" + m);
            }
        }

        Pattern pattern = isInteger ? INTEGER_PATTERN : DECIMAL_PATTERN;
        Matcher matcher = pattern.matcher(original);
        StringBuffer sb = new StringBuffer();
        int numCount = 0; // 记录匹配到的数字位置

        while (matcher.find()) {
            numCount++;
            // 超出数组长度的数字，默认乘以1（不修改）
            double multiple = numCount <= multiples.length ? multiples[numCount - 1] : 1.0;
            double oldNum = Double.parseDouble(matcher.group());
            double newNum = oldNum * multiple;
            String replaceStr = isInteger ? String.valueOf(Math.round(newNum)) : String.valueOf(newNum);
            matcher.appendReplacement(sb, replaceStr);
        }
        matcher.appendTail(sb);
        return sb.toString();
    }

    // ==================== 通用工具方法 ====================

    /**
     * 基础参数校验
     *
     * @param original 原始字符串
     * @param multiple 倍数
     */
    private static void validateParams(String original, double multiple) {
        if (original == null) {
            throw new IllegalArgumentException("原始字符串不可为null");
        }
        if (multiple <= 0) {
            throw new IllegalArgumentException("倍数必须大于0，当前值：" + multiple);
        }
    }

    // ==================== 占位符替换（补充之前的需求） ====================

    /**
     * 替换字符串中的命名占位符 {xxx}
     *
     * @param template 带占位符的模板，不可为 null
     * @param params   占位符参数映射，不可为 null
     * @return 替换后的字符串
     * @throws IllegalArgumentException 当 template/params 为 null 时抛出
     */
    public static String replaceNamedPlaceholders(String template, java.util.Map<String, Object> params) {
        if (template == null || params == null) {
            throw new IllegalArgumentException("模板和参数映射不可为null");
        }
        Pattern pattern = Pattern.compile("\\{(\\w+)\\}");
        Matcher matcher = pattern.matcher(template);
        StringBuffer sb = new StringBuffer();

        while (matcher.find()) {
            String placeholderName = matcher.group(1);
            Object value = params.getOrDefault(placeholderName, matcher.group());
            matcher.appendReplacement(sb, value.toString());
        }
        matcher.appendTail(sb);
        return sb.toString();
    }
}