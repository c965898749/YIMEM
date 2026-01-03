package com.sy.tool;

import java.util.Calendar;
import java.util.Date;

/**
 * 擂台赛周数工具类（精准获取当前这周、推导上周）
 */
public class ArenaWeekUtils {

    /**
     * 核心方法：获取任意日期对应的「当前这周」唯一周数（年份+周数，格式统一，无跨年重复）
     * @param date 目标日期（传入new Date()即可获取当前系统时间对应的这周）
     * @return 唯一周编号（如2025年第50周=202550，2026年第1周=202601，2026年第10周=202610）
     */
    public static int getCurrentUniqueWeekNum(Date date) {
        Calendar calendar = Calendar.getInstance();
        // 关键配置：适配擂台赛周期（周一作为一周的第一天，与“周一报名、周日结束”规则一致）
        calendar.setFirstDayOfWeek(Calendar.MONDAY);
        // 忽略时间部分，仅关注日期，避免同一天不同时间导致周数判断异常
        calendar.setTime(date);
        calendar.set(Calendar.HOUR_OF_DAY, 0);
        calendar.set(Calendar.MINUTE, 0);
        calendar.set(Calendar.SECOND, 0);
        calendar.set(Calendar.MILLISECOND, 0);

        int year = calendar.get(Calendar.YEAR); // 提取年份（如2025、2026）
        int weekOfYear = calendar.get(Calendar.WEEK_OF_YEAR); // 提取当前日期在当年的周数（1~52/53）

        // 周数补0：确保1~9周格式为01~09，保证唯一周编号格式统一（如202601而非20261）
        String weekStr = String.format("%02d", weekOfYear);
        // 拼接为年份+周数的整数（如2025 + "50" = 202550，2026 + "01" = 202601）
        return Integer.parseInt(year + weekStr);
    }

    /**
     * 辅助方法：通过「当前这周」唯一周数，推导「上周」唯一周数（处理跨年场景）
     * @param currentUniqueWeek 当前这周的唯一周编号（如202601、202550）
     * @return 上周的唯一周编号（如202601→202552，202550→202549）
     */
    public static int getLastUniqueWeekNum(int currentUniqueWeek) {
        // 提取年份（如202601 → 2026，202550 → 2025）
        int year = currentUniqueWeek / 100;
        // 提取周数（如202601 → 01，202550 → 50）
        int week = currentUniqueWeek % 100;

        if (week > 1) {
            // 非年初场景：当前周数>1，直接周数-1，年份不变
            // 注意：补0处理，确保周数始终是2位（如202602 → 202601，而非20261）
            String lastWeekStr = String.format("%02d", week - 1);
            return Integer.parseInt(year + lastWeekStr);
        } else {
            // 年初场景：当前周数=01，上周为上一年的最后一周
            int lastYear = year - 1;
            // 获取上一年最后一天对应的周数（即上一年的最后一周）
            Calendar calendar = Calendar.getInstance();
            calendar.setFirstDayOfWeek(Calendar.MONDAY);
            // 设置为上一年的12月31日
            calendar.set(Calendar.YEAR, lastYear);
            calendar.set(Calendar.MONTH, Calendar.DECEMBER);
            calendar.set(Calendar.DAY_OF_MONTH, 31);
            calendar.set(Calendar.HOUR_OF_DAY, 0);
            calendar.set(Calendar.MINUTE, 0);
            calendar.set(Calendar.SECOND, 0);
            calendar.set(Calendar.MILLISECOND, 0);
            // 获取上一年最后一周的唯一周数，再提取其周数部分
            int lastYearLastWeekNum = getCurrentUniqueWeekNum(calendar.getTime());
            int lastYearLastWeek = lastYearLastWeekNum % 100;
            // 拼接上一年+最后一周的周数
            String lastWeekStr = String.format("%02d", lastYearLastWeek);
            return Integer.parseInt(lastYear + lastWeekStr);
        }
    }
}