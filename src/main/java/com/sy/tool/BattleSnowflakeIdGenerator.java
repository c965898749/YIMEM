package com.sy.tool;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.concurrent.atomic.AtomicLong;

/**
 * 改造版雪花算法工具类：生成 BATTLE_yyyyMMdd_雪花ID 格式的业务ID
 * 解决分布式环境下ID唯一性，且支持单日无限序号（突破999上限）
 */
public class BattleSnowflakeIdGenerator {
    // ====================== 雪花算法核心参数 ======================
    /** 开始时间戳 (2025-01-01 00:00:00)，单位：毫秒 */
    private static final long START_TIMESTAMP = 1735689600000L;
    /** 机器ID位数（最多支持 2^5=32 台机器） */
    private static final long WORKER_ID_BITS = 5L;
    /** 数据中心ID位数（最多支持 2^5=32 个数据中心） */
    private static final long DATACENTER_ID_BITS = 5L;
    /** 序列号位数（每毫秒最多生成 2^12=4096 个ID） */
    private static final long SEQUENCE_BITS = 12L;

    /** 机器ID最大值（31） */
    private static final long MAX_WORKER_ID = ~(-1L << WORKER_ID_BITS);
    /** 数据中心ID最大值（31） */
    private static final long MAX_DATACENTER_ID = ~(-1L << DATACENTER_ID_BITS);
    /** 序列号最大值（4095） */
    private static final long MAX_SEQUENCE = ~(-1L << SEQUENCE_BITS);

    /** 机器ID左移位数（12） */
    private static final long WORKER_ID_SHIFT = SEQUENCE_BITS;
    /** 数据中心ID左移位数（17） */
    private static final long DATACENTER_ID_SHIFT = SEQUENCE_BITS + WORKER_ID_BITS;
    /** 时间戳左移位数（22） */
    private static final long TIMESTAMP_SHIFT = SEQUENCE_BITS + WORKER_ID_BITS + DATACENTER_ID_BITS;

    // ====================== 业务自定义参数 ======================
    /** 业务前缀 */
    private static final String BUSINESS_PREFIX = "BATTLE";
    /** 日期格式化器（线程安全） */
    private static final DateTimeFormatter DATE_FORMATTER = DateTimeFormatter.ofPattern("yyyyMMdd");

    // ====================== 实例变量 ======================
    private final long workerId; // 机器ID
    private final long datacenterId; // 数据中心ID
    private long lastTimestamp = -1L; // 上一次生成ID的时间戳
    private final AtomicLong sequence = new AtomicLong(0L); // 序列号（原子类保证线程安全）

    /**
     * 构造方法：初始化机器ID和数据中心ID
     * @param workerId 机器ID（0-31）
     * @param datacenterId 数据中心ID（0-31）
     */
    public BattleSnowflakeIdGenerator(long workerId, long datacenterId) {
        // 校验机器ID和数据中心ID合法性
        if (workerId > MAX_WORKER_ID || workerId < 0) {
            throw new IllegalArgumentException(String.format("Worker ID 必须在 0 - %d 之间", MAX_WORKER_ID));
        }
        if (datacenterId > MAX_DATACENTER_ID || datacenterId < 0) {
            throw new IllegalArgumentException(String.format("Datacenter ID 必须在 0 - %d 之间", MAX_DATACENTER_ID));
        }
        this.workerId = workerId;
        this.datacenterId = datacenterId;
    }

    /**
     * 生成雪花算法核心数字ID（64位Long）
     * @return 雪花数字ID
     */
    private synchronized long generateSnowflakeNumber() {
        long currentTimestamp = System.currentTimeMillis();

        // 1. 校验时间戳：若当前时间 < 上一次生成ID的时间，说明时钟回拨，抛出异常
        if (currentTimestamp < lastTimestamp) {
            throw new RuntimeException(
                    String.format("时钟回拨异常！当前时间戳：%d，上一次时间戳：%d", currentTimestamp, lastTimestamp)
            );
        }

        // 2. 同一毫秒内，序列号自增
        if (currentTimestamp == lastTimestamp) {
            sequence.set((sequence.get() + 1) & MAX_SEQUENCE);
            // 序列号溢出（同一毫秒生成超过4096个ID），等待下一毫秒
            if (sequence.get() == 0) {
                currentTimestamp = waitNextMillis(lastTimestamp);
            }
        } else {
            // 3. 不同毫秒，序列号重置为0
            sequence.set(0L);
        }

        // 更新上一次时间戳
        lastTimestamp = currentTimestamp;

        // 4. 拼接雪花ID：时间戳部分 + 数据中心ID + 机器ID + 序列号
        return ((currentTimestamp - START_TIMESTAMP) << TIMESTAMP_SHIFT)
                | (datacenterId << DATACENTER_ID_SHIFT)
                | (workerId << WORKER_ID_SHIFT)
                | sequence.get();
    }

    /**
     * 等待下一毫秒，获取新的时间戳
     * @param lastTimestamp 上一次生成ID的时间戳
     * @return 新的时间戳
     */
    private long waitNextMillis(long lastTimestamp) {
        long timestamp = System.currentTimeMillis();
        while (timestamp <= lastTimestamp) {
            timestamp = System.currentTimeMillis();
        }
        return timestamp;
    }

    /**
     * 生成最终业务ID：BATTLE_yyyyMMdd_雪花数字ID
     * @return 格式化后的业务ID
     */
    public String generateBattleId() {
        // 1. 获取当前日期字符串
        String dateStr = LocalDate.now().format(DATE_FORMATTER);
        // 2. 生成雪花数字ID
        long snowflakeNumber = generateSnowflakeNumber();
        // 3. 拼接业务ID
        return String.format("%s_%s_%d", BUSINESS_PREFIX, dateStr, snowflakeNumber);
    }

    // ====================== 单例模式（可选） ======================
    // 推荐：通过配置中心注入机器ID和数据中心ID，此处为演示固定值
    private static class SingletonHolder {
        private static final BattleSnowflakeIdGenerator INSTANCE = new BattleSnowflakeIdGenerator(1, 1);
    }

    /**
     * 获取单例实例
     * @return 雪花算法生成器实例
     */
    public static BattleSnowflakeIdGenerator getInstance() {
        return SingletonHolder.INSTANCE;
    }

    // ====================== 测试方法 ======================
    public static void main(String[] args) {
        // 测试生成10个ID
        BattleSnowflakeIdGenerator generator = BattleSnowflakeIdGenerator.getInstance();
        for (int i = 0; i < 10; i++) {
            String battleId = generator.generateBattleId();
            System.out.println(battleId);
        }
    }
}
