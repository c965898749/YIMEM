package com.sy.tool;

import java.security.SecureRandom;
import java.util.Random;

/**
 * 邀请码生成工具类
 * 特点：去重易混淆字符，结合时间戳+随机数，高唯一性，可配置长度
 */
public class InviteCodeGenerator {

    // 单例实例
    private static volatile InviteCodeGenerator instance;

    // 邀请码字符集（去掉0/O、1/I/l，避免混淆）
    private static final char[] CODE_CHARSET = {
            '2', '3', '4', '5', '6', '7', '8', '9',
            'A', 'B', 'C', 'D', 'E', 'F', 'G', 'H', 'J', 'K', 'L', 'M', 'N', 'P', 'Q', 'R', 'S', 'T', 'U', 'V', 'W', 'X', 'Y', 'Z',
            'a', 'b', 'c', 'd', 'e', 'f', 'g', 'h', 'j', 'k', 'm', 'n', 'p', 'q', 'r', 's', 't', 'u', 'v', 'w', 'x', 'y', 'z'
    };

    // 字符集长度
    private static final int CHARSET_LENGTH = CODE_CHARSET.length;

    // 安全随机数生成器
    private final Random random;

    // 私有构造方法
    private InviteCodeGenerator() {
        // 使用SecureRandom提高随机性，比普通Random更安全
        this.random = new SecureRandom();
    }

    // 获取单例实例
    public static InviteCodeGenerator getInstance() {
        if (instance == null) {
            synchronized (InviteCodeGenerator.class) {
                if (instance == null) {
                    instance = new InviteCodeGenerator();
                }
            }
        }
        return instance;
    }

    /**
     * 生成指定长度的邀请码
     * @param length 邀请码长度（建议6-12位）
     * @return 随机邀请码
     */
    public String generateInviteCode(int length) {
        // 参数校验
        if (length <= 0 || length > 32) {
            throw new IllegalArgumentException("邀请码长度必须在1-32之间");
        }

        StringBuilder sb = new StringBuilder(length);

        // 加入时间戳因子，降低重复概率
        long timestamp = System.currentTimeMillis();
        String timeStr = String.valueOf(timestamp);
        int timeLen = Math.min(timeStr.length(), length / 2);

        // 先填充部分时间戳转换的字符
        for (int i = 0; i < timeLen; i++) {
            int num = timeStr.charAt(i) - '0';
            sb.append(CODE_CHARSET[num % CHARSET_LENGTH]);
        }

        // 剩余部分填充随机字符
        for (int i = sb.length(); i < length; i++) {
            int randomIndex = random.nextInt(CHARSET_LENGTH);
            sb.append(CODE_CHARSET[randomIndex]);
        }

        // 打乱字符顺序，避免时间戳规律暴露
        return shuffleString(sb.toString());
    }

    /**
     * 生成默认长度（8位）的邀请码
     * @return 8位随机邀请码
     */
    public String generateInviteCode() {
        return generateInviteCode(8);
    }

    /**
     * 打乱字符串字符顺序
     * @param str 原字符串
     * @return 打乱后的字符串
     */
    private String shuffleString(String str) {
        char[] chars = str.toCharArray();
        for (int i = chars.length - 1; i > 0; i--) {
            int j = random.nextInt(i + 1);
            // 交换字符
            char temp = chars[i];
            chars[i] = chars[j];
            chars[j] = temp;
        }
        return new String(chars);
    }

    // 测试示例
    public static void main(String[] args) {
        InviteCodeGenerator generator = InviteCodeGenerator.getInstance();

        // 生成8位邀请码
        String code1 = generator.generateInviteCode();
        System.out.println("8位邀请码：" + code1);

        // 生成6位邀请码
        String code2 = generator.generateInviteCode(6);
        System.out.println("6位邀请码：" + code2);

        // 生成12位邀请码
        String code3 = generator.generateInviteCode(12);
        System.out.println("12位邀请码：" + code3);

        // 批量生成测试
        System.out.println("\n批量生成测试：");
        for (int i = 0; i < 5; i++) {
            System.out.println(generator.generateInviteCode());
        }
    }
}