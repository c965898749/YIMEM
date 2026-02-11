package com.sy.tool;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * 字符串数值提取、计算与替换工具类
 * @author 编程助手
 */
public class NumberExtractUtil {

    // 匹配数字的正则表达式
    private static final Pattern NUMBER_PATTERN = Pattern.compile("\\d+");

    /**
     * 从字符串中提取所有数字
     * @param input 输入字符串
     * @return 提取出的数字列表
     */
    public static List<Integer> extractNumbers(String input) {
        List<Integer> numbers = new ArrayList<>();

        // 空值校验
        if (input == null || input.trim().isEmpty()) {
            return numbers;
        }

        Matcher matcher = NUMBER_PATTERN.matcher(input);
        while (matcher.find()) {
            String numStr = matcher.group();
            try {
                int number = Integer.parseInt(numStr);
                numbers.add(number);
            } catch (NumberFormatException e) {
                System.err.println("数字转换异常: " + numStr);
            }
        }
        return numbers;
    }

    /**
     * 将字符串中的数字替换为「数字 × 等级」后的新数值，生成新字符串
     * @param input 原始字符串
     * @param level 等级
     * @return 替换后的新字符串
     */
    public static String replaceNumbersWithLevel(String input, int level) {
        // 空值校验
        if (input == null || input.trim().isEmpty()) {
            return input;
        }

        // 先提取所有数字
        List<Integer> originalNumbers = extractNumbers(input);
        if (originalNumbers.isEmpty()) {
            return input; // 没有数字则直接返回原字符串
        }

        // 构建替换后的字符串
        StringBuilder newStr = new StringBuilder(input);
        Matcher matcher = NUMBER_PATTERN.matcher(input);
        int replaceCount = 0; // 记录已替换的数字个数
        int offset = 0; // 记录替换后字符串长度变化的偏移量

        while (matcher.find()) {
            if (replaceCount >= originalNumbers.size()) {
                break;
            }

            // 计算新数值
            int originalNum = originalNumbers.get(replaceCount);
            int newNum = originalNum * level;

            // 原数字的起始和结束位置（需加上偏移量）
            int start = matcher.start() + offset;
            int end = matcher.end() + offset;

            // 替换原数字为新数字
            newStr.replace(start, end, String.valueOf(newNum));

            // 更新偏移量（新数字长度 - 原数字长度）
            offset += String.valueOf(newNum).length() - (end - start);
            replaceCount++;
        }

        return newStr.toString();
    }

    /**
     * 提取数字并与等级相乘，返回详细的计算结果（包含原始值和计算后的值）
     * @param input 输入字符串
     * @param level 等级
     * @return 详细计算结果
     */
    public static String getCalculationDetail(String input, int level) {
        List<Integer> originalNumbers = extractNumbers(input);
        List<Integer> newNumbers = new ArrayList<>();
        for (int num : originalNumbers) {
            newNumbers.add(num * level);
        }

        StringBuilder sb = new StringBuilder();
        sb.append("原始字符串: ").append(input).append("\n");
        sb.append("提取的数值: ").append(originalNumbers).append("\n");
        sb.append("等级: ").append(level).append("\n");
        sb.append("计算后数值: ").append(newNumbers).append("\n");
        sb.append("新字符串: ").append(replaceNumbersWithLevel(input, level)).append("\n");

        // 生成对应关系
        sb.append("\n详细对应:\n");
        for (int i = 0; i < originalNumbers.size(); i++) {
            sb.append(originalNumbers.get(i))
                    .append(" × ")
                    .append(level)
                    .append(" = ")
                    .append(newNumbers.get(i))
                    .append("\n");
        }

        return sb.toString();
    }

    // 测试示例
    public static void main(String[] args) {
        String originalStr = "与狮驼王在同一队伍时，增加自身705点生命上限，63点攻击，176点速度";
        int level = 5; // 示例等级

        // 生成新字符串
        String newStr = replaceNumbersWithLevel(originalStr, level);
        System.out.println("原始字符串: " + originalStr);
        System.out.println("等级" + level + "后的新字符串: " + newStr);

        // 输出详细信息
        System.out.println("\n" + getCalculationDetail(originalStr, level));
    }
}