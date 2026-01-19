package com.sy.tool;

import com.sy.model.game.EqCard;

import java.math.BigDecimal;
import java.util.*;

/**
 * 装备生成工具类 - 精准适配eq_card表 + 固定星级/阵营/职业/ID规则
 * 星级：1/1.5/2.5/3/3.5/4 | 阵营：dark/sacred | 职业：武圣/神将/仙灵 | ID：大写字母+数字（如J1000）
 */
public class EquipmentGenerateUtil {
    // ========== 一、常量配置区 - 核心规则定义 ==========
    // 基础属性（锋利/坚韧等9大属性，关联数据库攻防字段）
    public static final List<String> BASE_ATTRS = new ArrayList<>(Arrays.asList(
            "锋利", "坚韧", "火焰", "火抗", "毒素", "毒抗", "飞弹", "弹抗", "治愈"
    ));

    // 【核心修改1】固定星级列表（仅支持这6个星级）
    public static final Set<Double> ALLOW_STARS = new HashSet<>(Arrays.asList(1.0, 1.5,2.0, 2.5, 3.0, 3.5, 4.0));
    // 【核心修改2】阵营仅dark/sacred
    public static final String[] CAMP_LIST = {"dark", "sacred"};
    // 【核心修改3】职业仅武圣/神将/仙灵
    public static final String[] PROFESSION_LIST = {"武圣", "神将", "仙灵"};

    // 装备小类枚举（对应eq_type2）+ ID前缀 + 起始编号 + 名称列表
    // 格式：{类型名, ID前缀, 起始编号, 名称数组, 数量}
    public static final Object[][] EQUIP_CONFIG = {
            {"剑", "J", 1000, new String[]{
                    "青釭剑", "倚天剑", "湛卢剑", "赤霄剑", "太阿剑", "龙泉剑", "鱼肠剑", "纯钧剑", "承影剑", "巨阙剑",
                    // 此处省略剩余1099个剑名，直接追加即可，总数1109个
            }, 1109},
            {"弓", "G", 1000, new String[]{
                    "震天弓", "落日弓", "穿云弓", "裂风弓", "追月弓", "流星弓", "寒铁弓", "玄木弓", "紫金弓", "龙骨弓",
                    "凤鸣弓", "虎啸弓", "狼牙利弓", "朱雀弓", "玄武弓", "青龙弓", "白虎弓", "幽冥弓", "圣光弓", "破甲弓"
            }, 20},
            {"长枪", "Q", 1000, new String[]{
                    "龙胆亮银枪", "虎头湛金枪", "涯角枪", "沥泉枪", "梅花枪", "九曲枪", "裂穹枪", "破阵枪", "寒芒枪", "烈焰枪", "盘龙枪"
            }, 11},
            {"盾", "D", 1000, new String[]{
                    "比蒙盾", "玄铁盾", "龙鳞盾", "玄武盾", "紫金盾", "寒晶盾", "烈焰盾", "碧水盾", "磐石盾", "流云盾",
                    "圣光盾", "幽冥盾", "荆棘盾", "反伤盾", "守护盾", "勇者盾", "王者盾", "青铜盾", "白银盾", "黄金盾",
                    "暗金盾", "史诗盾", "传说盾", "神器盾", "蜀纹盾", "魏纹盾", "吴纹盾", "蛮骨盾", "仙纹盾", "魔纹盾",
                    "妖影盾", "中立玄盾", "破空盾", "裂地盾", "御风盾", "镇岳盾"
            }, 36},
            {"法器", "K", 1000, new String[]{
                    "噬魂幡", "聚灵瓶", "风雷扇", "寒冰镜", "焚天炉", "沧溟珠", "定海珠", "乾坤袋", "山河印", "玲珑塔",
                    "紫金葫芦", "芭蕉扇", "捆仙绳", "打神鞭", "翻天印", "轩辕镜", "东皇钟", "炼妖壶", "昊天塔", "伏羲琴",
                    "神农鼎", "崆峒印", "昆仑镜", "女娲石", "招魂幡", "引魂灯", "摄魂铃", "镇魂塔", "凝魂珠", "聚魂玉",
                    "破法杵", "封魔剑", "降妖杵", "除魔幡", "净世瓶", "渡厄莲", "往生镜", "轮回盘"
            }, 38},
            {"帽子", "M", 1000, new String[]{
                    "紫金盔", "凤翅盔", "虎头盔", "狼盔", "豹盔", "玄铁盔", "龙鳞盔", "玄武盔", "朱雀盔", "青龙盔",
                    "白虎盔", "圣光盔", "幽冥盔", "勇者盔", "王者盔", "青铜盔", "白银盔", "黄金盔", "暗金盔", "史诗盔",
                    "传说盔", "神器盔", "蜀纹盔", "魏纹盔", "吴纹盔", "蛮骨盔", "仙纹盔", "魔纹盔", "妖影盔", "中立玄盔",
                    "御风盔", "镇岳盔"
            }, 32},
            {"披风", "P", 1000, new String[]{
                    "流云披风", "烈焰披风", "寒冰披风", "玄铁披风", "龙鳞披风", "玄武披风", "紫金披风", "圣光披风", "幽冥披风", "荆棘披风",
                    "反伤披风", "守护披风", "勇者披风", "王者披风", "青铜披风", "白银披风", "黄金披风", "暗金披风", "史诗披风", "传说披风",
                    "神器披风", "蜀纹披风", "魏纹披风", "吴纹披风", "蛮骨披风", "仙纹披风", "魔纹披风", "妖影披风", "中立玄披风",
                    "御风披风", "镇岳披风", "破空披风", "裂地披风", "隐身披风"
            }, 34},
            {"胸甲", "S", 1000, new String[]{
                    "玄铁甲", "龙鳞甲", "玄武甲", "紫金甲", "寒晶甲", "烈焰甲", "碧水甲", "磐石甲", "流云甲", "圣光甲",
                    "幽冥甲", "荆棘甲", "反伤甲", "守护甲", "勇者甲", "王者甲", "青铜甲", "白银甲", "黄金甲", "暗金甲",
                    "史诗甲", "传说甲", "神器甲", "蜀纹甲", "魏纹甲", "吴纹甲", "蛮骨甲", "仙纹甲", "魔纹甲", "妖影甲",
                    "中立玄甲", "御风甲", "镇岳甲", "破空甲", "裂地甲"
            }, 35},
            {"宝具", "X", 1000, new String[]{
                    "乾坤戒", "山河印", "定海珠", "玲珑塔", "紫金葫芦", "芭蕉扇", "捆仙绳", "打神鞭", "翻天印", "轩辕镜",
                    "东皇钟", "炼妖壶", "昊天塔", "伏羲琴", "神农鼎", "崆峒印", "昆仑镜", "女娲石", "招魂幡", "引魂灯",
                    // 此处省略剩余45个宝具名，总数65个
            }, 65},
            {"腰带", "Y", 1000, new String[]{
                    "紫金腰带", "玄铁腰带", "龙鳞腰带", "玄武腰带", "烈焰腰带", "寒冰腰带", "碧水腰带", "磐石腰带", "流云腰带", "圣光腰带",
                    "幽冥腰带", "荆棘腰带", "反伤腰带", "守护腰带", "勇者腰带", "王者腰带", "青铜腰带", "白银腰带", "黄金腰带", "暗金腰带",
                    "史诗腰带", "传说腰带", "神器腰带", "蜀纹腰带", "魏纹腰带", "吴纹腰带", "蛮骨腰带", "仙纹腰带", "魔纹腰带", "中立玄腰带"
            }, 29}
    };

    // 装备大类对应eq_type
    public static final int EQ_TYPE_WEAPON = 0;  // 剑、弓、长枪
    public static final int EQ_TYPE_DEFENSE = 1; // 盾、帽子、披风、胸甲、腰带
    public static final int EQ_TYPE_TREASURE = 2;// 宝具
    public static final int EQ_TYPE_MAGIC = 3;   // 法器

    // 随机数工具
    private static final Random RANDOM = new Random();
    // 装备ID生成计数器（保证同类型ID递增）
    private static final Map<String, Integer> ID_COUNTER = new HashMap<>();
    static {
        // 初始化ID计数器：每个装备类型的起始编号
        for (Object[] config : EQUIP_CONFIG) {
            String prefix = (String) config[1];
            int startNum = (int) config[2];
            ID_COUNTER.put(prefix, startNum);
        }
    }



    // ========== 三、辅助方法 - 生成介绍/ID/属性映射 ==========
    /** 生成装备基础介绍 */
    private static String generateIntroduce(String equipName, String typeName) {
        return equipName + "，乃上古" + typeName + "，锻造精良，蕴含强大力量，适配" + PROFESSION_LIST[RANDOM.nextInt(PROFESSION_LIST.length)] + "使用。";
    }

    /** 生成被动介绍 */
    private static String generatePassive() {
        String[] passives = {
                "攻击时有10%概率触发暴击，造成1.5倍伤害",
                "受到攻击时反弹20%伤害",
                "每回合恢复5%生命值",
                "元素抗性提升15%",
                "物理攻击无视敌方10%防御"
        };
        return passives[RANDOM.nextInt(passives.length)];
    }

    /** 【核心修改4】生成装备ID：大写前缀+递增数字（如J1000→J1001→J1002） */
    private static String generateEquipId(String prefix) {
        int current = ID_COUNTER.get(prefix);
        String id = prefix + current;
        ID_COUNTER.put(prefix, current + 1); // 自增，保证唯一
        return id;
    }

    /** 属性→数据库攻防字段映射 */
    private static void mappingAttrToField(String attr, int value, EqCard eqCard) {
        switch (attr) {
            case "锋利": eqCard.setWlAtk(value); break;
            case "坚韧": eqCard.setWlAtk(value/2); eqCard.setWlDef(value/2); break;
            case "火焰": eqCard.setHyAtk(value); break;
            case "火抗": eqCard.setHyDef(value); break;
            case "毒素": eqCard.setDsDef(value); break;
            case "毒抗": eqCard.setDsDef(value); break;
            case "飞弹": eqCard.setFdDef(value); break;
            case "弹抗": eqCard.setFdDef(value); break;
            case "治愈": eqCard.setZlDef(value); break;
        }
    }

    /** 按固定星级生成属性列表 */
    private static List<String> generateAttrByStar(double star, List<Integer> valueList) {
        List<String> attrList = new ArrayList<>();
        int baseVal;
        if (star >=1.0 && star <=3.0) {
            // 1/1.5/2.5/3星 → 单属性
            baseVal = (int)(star * 4) + RANDOM.nextInt(3); // 数值随星级递增
            String attr = BASE_ATTRS.get(RANDOM.nextInt(BASE_ATTRS.size()));
            attrList.add(attr);
            valueList.add(baseVal);
        } else if (star >=3.5 && star <=4.0) {
            // 3.5/4星 → 双属性+同数值
            baseVal = (int)(star * 5) + RANDOM.nextInt(3);
            int idx1 = RANDOM.nextInt(BASE_ATTRS.size());
            int idx2;
            do { idx2 = RANDOM.nextInt(BASE_ATTRS.size()); } while (idx1 == idx2);
            attrList.add(BASE_ATTRS.get(idx1));
            attrList.add(BASE_ATTRS.get(idx2));
            valueList.add(baseVal);
            valueList.add(baseVal);
        }
        return attrList;
    }

    // ========== 四、核心方法 - 生成装备对象 ==========
    /**
     * 生成指定固定星级的装备对象（可直接入库）
     * @param starLevel 必须是1/1.5/2.5/3/3.5/4
     * @return EqCard 实体
     */
    public static EqCard generateEqCard(double starLevel) {
        // 校验星级合法性
        if (!ALLOW_STARS.contains(starLevel)) {
            throw new IllegalArgumentException("非法星级！仅支持：1、1.5、2.5、3、3.5、4");
        }
        EqCard eqCard = new EqCard();
        eqCard.setStar(new BigDecimal(starLevel));

        // 随机选择装备类型
        int typeIdx = RANDOM.nextInt(EQUIP_CONFIG.length);
        Object[] config = EQUIP_CONFIG[typeIdx];
        String typeName = (String) config[0];
        String idPrefix = (String) config[1];
        String[] nameList = (String[]) config[3];
        int typeCount = (int) config[4];

        // 生成装备名称和ID
        String equipName = nameList[RANDOM.nextInt(typeCount)];
        eqCard.setId(generateEquipId(idPrefix));

        // 设置基础字段
        eqCard.setWeight(0.1 + RANDOM.nextDouble() * 9.9);
        eqCard.setCamp(CAMP_LIST[RANDOM.nextInt(CAMP_LIST.length)]);
        eqCard.setProfession(PROFESSION_LIST[RANDOM.nextInt(PROFESSION_LIST.length)]);
        eqCard.setIntroduce(generateIntroduce(equipName, typeName));
        eqCard.setPassiveIntroduceOne(generatePassive());
        eqCard.setPassiveIntroduceTwo(RANDOM.nextBoolean() ? generatePassive() : null);
        eqCard.setPassiveIntroduceThree(RANDOM.nextBoolean() ? generatePassive() : null);

        // 设置eq_type和eq_type2
        eqCard.setEqType2(typeIdx + 1);
        if (typeIdx <=2) {
            eqCard.setEqType(EQ_TYPE_WEAPON); // 剑/弓/长枪=武器
        } else if (typeIdx >=3 && typeIdx <=7) {
            eqCard.setEqType(EQ_TYPE_DEFENSE); // 盾/帽子/披风/胸甲/腰带=防具
        } else if (typeIdx ==8) {
            eqCard.setEqType(EQ_TYPE_TREASURE); // 宝具
        } else {
            eqCard.setEqType(EQ_TYPE_MAGIC); // 法器
        }

        // 生成属性并映射到数据库字段
        List<Integer> valueList = new ArrayList<>();
        List<String> attrList = generateAttrByStar(starLevel, valueList);
        for (int i=0; i<attrList.size(); i++) {
            mappingAttrToField(attrList.get(i), valueList.get(i), eqCard);
        }

        // 生成装备显示名称：【属性1.属性2.装备名】
        StringBuilder nameSb = new StringBuilder("[");
        attrList.forEach(attr -> nameSb.append(attr).append("."));
        nameSb.append(equipName).append("]");
        eqCard.setName(nameSb.toString());

        return eqCard;
    }

    // ========== 五、批量生成方法 ==========
    public static List<EqCard> batchGenerate(int count, double... stars) {
        List<EqCard> list = new ArrayList<>();
        for (int i=0; i<count; i++) {
            double star = stars.length >0 ? stars[RANDOM.nextInt(stars.length)] : 1.0;
            list.add(generateEqCard(star));
        }
        return list;
    }

    // ========== 六、测试主方法 ==========
    public static void main(String[] args) {
        // 测试生成指定星级装备
//        System.out.println("======= 生成1星剑（ID从J1000开始）=======");
//        EqCard sword1 = generateEqCard(1.0);
//        System.out.println(sword1);
//
//        System.out.println("\n======= 生成4星盾 ========");
//        EqCard shield4 = generateEqCard(4.0);
//        System.out.println(shield4);

        System.out.println("\n======= 批量生成5个装备（指定星级3.5）=======");
        List<EqCard> list = batchGenerate(1, 3.5);
        list.forEach(System.out::println);
    }
}
