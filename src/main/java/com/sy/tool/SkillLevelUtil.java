package com.sy.tool;

/**
 * 人物技能等级计算工具类
 * 规则：1级时技能1为1级，其他为0；后续每5级均匀提升，50级时全部满级（10级）
 */
public class SkillLevelUtil {
    public static final int MAX_CHARACTER_LEVEL = 50;  // 人物最大等级
    public static final int MAX_SKILL_LEVEL = 10;      // 技能最大等级
    private static final int SKILL_COUNT = 3;           // 技能总数

    // 私有构造，禁止实例化
    private SkillLevelUtil() {
        throw new UnsupportedOperationException("工具类不允许实例化");
    }

    /**
     * 获取第1个技能的等级
     */
    public static int getSkill1Level(int characterLevel) {
        return calculateSkillLevels(characterLevel)[0];
    }

    /**
     * 获取第2个技能的等级
     */
    public static int getSkill2Level(int characterLevel) {
        return calculateSkillLevels(characterLevel)[1];
    }

    /**
     * 获取第3个技能的等级
     */
    public static int getSkill3Level(int characterLevel) {
        return calculateSkillLevels(characterLevel)[2];
    }

    /**
     * 核心计算方法
     */
    private static int[] calculateSkillLevels(int characterLevel) {
        validateLevel(characterLevel);
        int[] skills = new int[SKILL_COUNT];

        // 1级特殊处理：技能1为1，其他为0
        if (characterLevel == 1) {
            skills[0] = 1;
            skills[1] = 0;
            skills[2] = 0;
            return skills;
        }

        // 总需提升量：技能1需从1→10（9级），技能2和3需从0→10（各10级），总计29级
        // 从2级到50级共49级，每级贡献 29/49 点总成长
        int growthSteps = characterLevel - 1;  // 从2级开始的成长步数（1级为0步）
        double totalGrowth = (double) 29 / 49 * growthSteps;

        // 基础分配（技能1初始有1级，需额外分配的基础值）
        int baseAdd = (int) (totalGrowth / SKILL_COUNT);
        int remainder = (int) (totalGrowth % SKILL_COUNT);

        // 技能1初始1级 + 基础分配
        skills[0] = 1 + baseAdd;
        // 技能2和3从0开始 + 基础分配
        skills[1] = baseAdd;
        skills[2] = baseAdd;

        // 分配余数（轮询分配，优先技能1，再技能2，最后技能3）
        for (int i = 0; i < remainder; i++) {
            skills[i]++;
        }

        // 确保不超过满级
        for (int i = 0; i < SKILL_COUNT; i++) {
            if (skills[i] > MAX_SKILL_LEVEL) {
                skills[i] = MAX_SKILL_LEVEL;
            }
        }

        return skills;
    }

    // 等级校验
    private static void validateLevel(int level) {
        if (level < 1 || level > MAX_CHARACTER_LEVEL) {
            throw new IllegalArgumentException(
                    "人物等级必须在1-" + MAX_CHARACTER_LEVEL + "之间，当前等级：" + level
            );
        }
    }


}