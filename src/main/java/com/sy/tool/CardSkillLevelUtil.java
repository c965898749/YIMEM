package com.sy.tool;

import java.util.Arrays;

import java.util.Arrays;

/**
 * 卡牌技能等级计算工具类
 * 核心规则：
 * 1. 星级决定技能基础满级：1星=5级、1.5星=10级、2星=15级……（步长5）
 * 2. 初始技能：[1,0,0,0]，基础满级技能：[6,5,5,5]
 * 3. 满级后每增加5级，所有技能+1
 * 4. 所有技能等级封顶为10级
 */
public class CardSkillLevelUtil {

    // 初始技能等级（1级卡牌的技能等级）
    private static final int[] INITIAL_SKILL_LEVELS = {1, 0, 0, 0};
    // 基础满级技能等级（卡牌达到星级对应的基础满级时的技能等级）
    private static final int[] BASE_MAX_SKILL_LEVELS = {6, 5, 5, 5};
    // 初始星级（1星）对应的基础满级等级
    private static final int BASE_STAR_MAX_LEVEL = 5;
    // 星级增量步长（0.5星对应等级增量）
    private static final double STAR_STEP = 0.5;
    // 星级对应的等级增量（每0.5星，基础满级+5级）
    private static final int LEVEL_INCREMENT_PER_STAR_STEP = 5;
    // 满级后技能升级步长（每增加5级，所有技能+1）
    private static final int SKILL_UP_STEP = 5;
    // 技能等级封顶值
    private static final int SKILL_MAX_CAP = 10;

    /**
     * 根据星级计算技能的基础满级等级（如1星=5，1.5星=10，4星=35）
     * @param star 卡牌星级（支持0.5星步长，如1、1.5、2、2.5...）
     * @return 该星级对应的技能基础满级等级
     */
    public static int getSkillBaseMaxLevelByStar(double star) {
        if (star <= 0) {
            throw new IllegalArgumentException("星级必须大于0，当前值：" + star);
        }
        // 计算星级增量：(星级-1) / 0.5 → 得到0.5星的倍数
        double starStepCount = (star - 1) / STAR_STEP;
        // 基础满级等级 = 1星基础值 + 增量数 * 每步增量
        return BASE_STAR_MAX_LEVEL + (int) (starStepCount * LEVEL_INCREMENT_PER_STAR_STEP);
    }

    /**
     * 核心方法：根据卡牌等级和星级，计算4个技能的等级（封顶10级）
     * @param cardLevel 卡牌当前等级（≥1）
     * @param star 卡牌星级（≥1，支持0.5步长）
     * @return 4个技能的等级数组 [技能1, 技能2, 技能3, 技能4]
     */
    public static int[] calculateSkillLevels(int cardLevel, double star) {
        // 1. 参数校验
        if (cardLevel < 1) {
            throw new IllegalArgumentException("卡牌等级必须≥1，当前值：" + cardLevel);
        }
        if (star <= 0) {
            throw new IllegalArgumentException("星级必须大于0，当前值：" + star);
        }

        // 2. 获取该星级对应的技能基础满级等级
        int skillBaseMaxLevel = getSkillBaseMaxLevelByStar(star);

        // 3. 初始化技能等级数组（避免修改原常量数组）
        int[] skillLevels = Arrays.copyOf(INITIAL_SKILL_LEVELS, INITIAL_SKILL_LEVELS.length);

        // 4. 情况1：卡牌等级≤1 → 直接返回初始技能等级
        if (cardLevel == 1) {
            return skillLevels;
        }

        // 5. 情况2：卡牌等级≤基础满级 → 按比例计算技能等级
        if (cardLevel <= skillBaseMaxLevel) {
            // 计算等级进度（相对于1级到基础满级的区间）
            int levelRange = skillBaseMaxLevel - 1; // 1级到基础满级的等级差
            int currentProgress = cardLevel - 1;    // 当前等级相对于1级的增量

            for (int i = 0; i < skillLevels.length; i++) {
                // 技能等级 = 初始值 + (基础满级技能值 - 初始值) * 进度比例
                int skillMaxDelta = BASE_MAX_SKILL_LEVELS[i] - INITIAL_SKILL_LEVELS[i];
                // 向上取整（确保进度达标时能拿到满级值，如5级时技能1刚好到6）
                int skillLevel = INITIAL_SKILL_LEVELS[i] + (int) Math.ceil(
                        (double) currentProgress * skillMaxDelta / levelRange
                );
                // 确保不超过基础满级技能值，且不超过封顶值
                skillLevels[i] = Math.min(skillLevel, Math.min(BASE_MAX_SKILL_LEVELS[i], SKILL_MAX_CAP));
            }
            return skillLevels;
        }

        // 6. 情况3：卡牌等级超过基础满级 → 基础满级值 + 额外增量（不超过封顶）
        // 先把技能等级设为基础满级值
        System.arraycopy(BASE_MAX_SKILL_LEVELS, 0, skillLevels, 0, skillLevels.length);
        // 计算超过基础满级的等级数
        int exceedLevels = cardLevel - skillBaseMaxLevel;
        // 计算额外技能增量（每5级+1）
        int extraSkillIncrement = exceedLevels / SKILL_UP_STEP;

        // 给所有技能增加额外增量，并校验封顶
        for (int i = 0; i < skillLevels.length; i++) {
            int tempLevel = skillLevels[i] + extraSkillIncrement;
            // 最终技能等级 = 最小值（计算值，封顶值）
            skillLevels[i] = Math.min(tempLevel, SKILL_MAX_CAP);
        }

        return skillLevels;
    }

    // 测试示例：覆盖核心场景（重点验证封顶效果）
    public static void main(String[] args) {
        // 测试1：1级卡牌，任意星级 → [1,0,0,0]
        int[] skill1 = calculateSkillLevels(1, 4);
        System.out.println("1级4星卡牌技能等级：" + Arrays.toString(skill1));

        // 测试2：4星卡牌35级（基础满级）→ [6,5,5,5]
        int[] skill2 = calculateSkillLevels(35, 4);
        System.out.println("35级4星卡牌技能等级：" + Arrays.toString(skill2));

        // 测试3：4星卡牌40级（超过满级5级）→ [7,6,6,6]
        int[] skill3 = calculateSkillLevels(40, 4);
        System.out.println("40级4星卡牌技能等级：" + Arrays.toString(skill3));

        // 测试4：4星卡牌70级（超过满级35级，增量7级）→ 6+7=13→封顶10；5+7=12→封顶10 → [10,10,10,10]
        int[] skill4 = calculateSkillLevels(70, 4);
        System.out.println("70级4星卡牌技能等级（封顶验证）：" + Arrays.toString(skill4));

        // 测试5：2星卡牌25级（超过满级15级，增量2级）→ 6+2=8；5+2=7 → [8,7,7,7]
        int[] skill5 = calculateSkillLevels(25, 2);
        System.out.println("25级2星卡牌技能等级：" + Arrays.toString(skill5));

        // 测试6：5星卡牌100级（极端场景）→ 所有技能封顶10
        int[] skill6 = calculateSkillLevels(100, 5);
        System.out.println("100级5星卡牌技能等级（封顶验证）：" + Arrays.toString(skill6));
    }
}