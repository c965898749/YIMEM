package com.sy.tool;

import java.util.Random;

import java.util.UUID;

import java.util.UUID;

/**
 * 优化版：无索引越界风险的UUID转换唯一随机码工具类
 */
public class RandomCodeGenerator {
    // 确保参数为正数（避免索引计算为负数）
    private static final int GROUP_LENGTH = 4;
    private static final int GROUP_COUNT = 3;
    private static final String SEPARATOR = "-";
    private static final String CHARACTER_POOL = "0123456789ABCDEFGHIJKLMNOPQRSTUVWXYZ";
    private static final int POOL_LENGTH = CHARACTER_POOL.length();

    // 静态代码块：校验核心参数合法性，提前暴露配置错误
    static {
        if (GROUP_LENGTH <= 0 || GROUP_COUNT <= 0) {
            throw new IllegalArgumentException("GROUP_LENGTH和GROUP_COUNT必须为正整数");
        }
        if (POOL_LENGTH <= 0) {
            throw new IllegalArgumentException("CHARACTER_POOL不能为空，必须包含至少一个字符");
        }
    }

    private RandomCodeGenerator() {}

    public static String generateUniqueCode() {
        // 1. 获取UUID并处理
        String uuid = UUID.randomUUID().toString().replace("-", "").toUpperCase();
        long num = 0;
        int uuidLength = Math.min(16, uuid.length());
        for (int i = 0; i < uuidLength; i++) {
            char c = uuid.charAt(i);
            int val = Character.isDigit(c) ? (c - '0') : (c - 'A' + 10);
            num = num * 16 + val;
        }

        // 2. 生成原始编码（确保长度足够）
        StringBuilder codeBuilder = new StringBuilder();
        int totalLength = GROUP_LENGTH * GROUP_COUNT;
        // 循环生成，确保原始编码长度达标
        while (codeBuilder.length() < totalLength) {
            long index = num % POOL_LENGTH;
            codeBuilder.append(CHARACTER_POOL.charAt((int) Math.abs(index))); // 确保索引非负
            num = num / POOL_LENGTH;
        }
        String rawCode = codeBuilder.reverse().toString();
        // 截断或补位，确保rawCode长度严格等于totalLength
        if (rawCode.length() > totalLength) {
            rawCode = rawCode.substring(0, totalLength);
        } else if (rawCode.length() < totalLength) {
            StringBuilder supplement = new StringBuilder(rawCode);
            while (supplement.length() < totalLength) {
                supplement.append(CHARACTER_POOL.charAt(0));
            }
            rawCode = supplement.toString();
        }

        // 3. 安全分割格式（添加索引校验）
        StringBuilder finalCode = new StringBuilder();
        for (int i = 0; i < GROUP_COUNT; i++) {
            int start = i * GROUP_LENGTH;
            int end = start + GROUP_LENGTH;
            // 核心安全校验：确保start和end在合法范围内
            if (start < 0 || end > rawCode.length()) {
                start = Math.max(0, start);
                end = Math.min(rawCode.length(), end);
            }
            if (start >= end) {
                break; // 避免无效截取
            }
            finalCode.append(rawCode.substring(start, end));
            if (i != GROUP_COUNT - 1) {
                finalCode.append(SEPARATOR);
            }
        }

        return finalCode.toString();
    }

    public static void main(String[] args) {
        for (int i = 0; i < 20; i++) {
            String uniqueCode = generateUniqueCode();
            System.out.println("生成的唯一随机码[" + (i + 1) + "]：" + uniqueCode);
        }
    }
}