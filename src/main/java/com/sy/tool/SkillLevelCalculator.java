package com.sy.tool;

/**
 * 人物技能等级计算工具类
 * 核心功能：根据人物等级（1-50级）计算对应的技能等级（1-10级）
 * @author 工具类生成
 * @version 1.0
 */
public final class SkillLevelCalculator {

    // 私有化构造方法，禁止外部实例化工具类
    private SkillLevelCalculator() {
        throw new AssertionError("禁止实例化工具类 SkillLevelCalculator");
    }

    // 核心配置常量（私有化，仅内部使用）
    private static final int MAX_CHARACTER_LEVEL = 50;  // 人物满级
    private static final int MAX_SKILL_LEVEL = 10;      // 技能满级
    private static final int MIN_CHARACTER_LEVEL = 1;   // 人物最低等级
    private static final int MIN_SKILL_LEVEL = 1;       // 技能最低等级

    /**
     * 根据人物等级计算对应的技能等级
     * 映射规则：人物1级→技能1级，人物50级→技能10级，中间等级线性映射并向下取整
     *
     * @param characterLevel 人物当前等级，必须在1-50之间
     * @return 对应的技能等级（1-10之间的整数）
     * @throws IllegalArgumentException 当人物等级超出1-50范围时抛出异常
     */
    public static int getSkillLevel(int characterLevel) {
        // 1. 校验输入参数合法性
        validateCharacterLevel(characterLevel);

        // 2. 线性映射计算技能等级
        double levelRatio = (double) (characterLevel - MIN_CHARACTER_LEVEL) / (MAX_CHARACTER_LEVEL - MIN_CHARACTER_LEVEL);
        int skillLevel = (int) (levelRatio * (MAX_SKILL_LEVEL - MIN_SKILL_LEVEL)) + MIN_SKILL_LEVEL;

        // 3. 兜底保证技能等级在合法范围（防止浮点计算误差）
        return Math.min(Math.max(skillLevel, MIN_SKILL_LEVEL), MAX_SKILL_LEVEL);
    }

    /**
     * 校验人物等级是否合法
     * 私有辅助方法，仅工具类内部使用
     *
     * @param characterLevel 待校验的人物等级
     */
    private static void validateCharacterLevel(int characterLevel) {
        if (characterLevel < MIN_CHARACTER_LEVEL) {
            characterLevel=1;
//            throw new IllegalArgumentException(
//                    String.format("人物等级非法！要求范围[%d-%d]，当前输入：%d",
//                            MIN_CHARACTER_LEVEL, MAX_CHARACTER_LEVEL, characterLevel)
//            );
        }
        if (characterLevel > MAX_CHARACTER_LEVEL) {
            characterLevel=50;
//            throw new IllegalArgumentException(
//                    String.format("人物等级非法！要求范围[%d-%d]，当前输入：%d",
//                            MIN_CHARACTER_LEVEL, MAX_CHARACTER_LEVEL, characterLevel)
//            );
        }
    }

    // -------------------- 可选扩展方法（方便外部获取配置） --------------------
    /**
     * 获取人物最大等级
     * @return 人物满级数值（默认50）
     */
    public static int getMaxCharacterLevel() {
        return MAX_CHARACTER_LEVEL;
    }

    /**
     * 获取技能最大等级
     * @return 技能满级数值（默认10）
     */
    public static int getMaxSkillLevel() {
        return MAX_SKILL_LEVEL;
    }

    // 测试方法（可选，用于验证工具类功能）
    public static void main(String[] args) {
        // 测试核心方法
        System.out.println("人物1级 → 技能等级：" + SkillLevelCalculator.getSkillLevel(1));    // 1
        System.out.println("人物25级 → 技能等级：" + SkillLevelCalculator.getSkillLevel(25));  // 5
        System.out.println("人物50级 → 技能等级：" + SkillLevelCalculator.getSkillLevel(50));  // 10

        // 测试非法参数（会抛出异常）
        // System.out.println(SkillLevelCalculator.getSkillLevel(0));
        // System.out.println(SkillLevelCalculator.getSkillLevel(51));

        // 测试扩展方法
        System.out.println("人物满级：" + SkillLevelCalculator.getMaxCharacterLevel()); // 50
        System.out.println("技能满级：" + SkillLevelCalculator.getMaxSkillLevel());     // 10
    }
}
