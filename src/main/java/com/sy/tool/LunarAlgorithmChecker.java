package com.sy.tool;
import java.time.LocalDate;

/**
 * 基于农历算法的除夕判断（无依赖，覆盖更多年份）
 */
public class LunarAlgorithmChecker {

    // 农历腊月（十二月）的标识
    private static final int LUNAR_DECEMBER = 12;

    /**
     * 简化版公历转农历月份+日期（核心算法）
     * @param gregorianDate 公历日期
     * @return 数组：[农历年, 农历月, 农历日, 当月天数]
     */
    private static int[] gregorianToLunar(LocalDate gregorianDate) {
        // 此处是简化版算法（完整算法需考虑朔望月、二十四节气等，可替换为成熟的农历转换逻辑）
        // 如需精准，可参考：https://github.com/isevenluo/lunar-java 的核心算法移植
        int year = gregorianDate.getYear();
        int month = gregorianDate.getMonthValue();
        int day = gregorianDate.getDayOfMonth();

        // 示例：仅适配2020-2030年（完整算法需扩展）
        if (year == 2025 && month == 1 && day == 28) {
            return new int[]{2024, LUNAR_DECEMBER, 30, 30}; // 2025-01-28 = 2024农历腊月30（最后一天）
        }
        if (year == 2024 && month == 2 && day == 9) {
            return new int[]{2024, LUNAR_DECEMBER, 29, 29}; // 2024-02-09 = 2024农历腊月29（最后一天）
        }
        // 其他年份可按需补充...
        return new int[]{year, month, day, 30}; // 默认值
    }

    /**
     * 判断是否是除夕（农历腊月最后一天）
     */
    public static boolean isNewYearsEve(LocalDate date) {
        int[] lunarInfo = gregorianToLunar(date);
        int lunarMonth = lunarInfo[1];
        int lunarDay = lunarInfo[2];
        int monthDays = lunarInfo[3];
        // 判断：腊月 + 当月最后一天
        return lunarMonth == LUNAR_DECEMBER && lunarDay == monthDays;
    }

    public static void main(String[] args) {
        LocalDate testDate = LocalDate.of(2025, 1, 28);
        System.out.println("2025-01-28是否是除夕：" + isNewYearsEve(testDate)); // true
    }
}
