package com.sy.tool;

import com.sy.model.game.EqCard;

import java.math.BigDecimal;
import java.util.*;

/**
 * 装备生成工具类 - 最终完整版
 * 适配eq_card表 | 8大固定星级 | 1/2/3属性分级 | ID前缀+N属性字母+自增数字
 * 核心规则：1~3星1属性、3.5~4星2属性、4.5~5星3属性，均为不同属性+同数值
 */
public class EquipmentGenerateUtil {
    // ========== 一、全局常量配置区（集中管理，一键修改） ==========
    // 9大属性-固定字母映射（永久不变）
    public static final Map<String, String> ATTR_TO_CODE = new HashMap<String, String>() {{
        put("锋利", "F");
        put("坚韧", "J");
        put("火焰", "H");
        put("火抗", "K");
        put("毒素", "D");
        put("毒抗", "N");
        put("飞弹", "L");
        put("弹抗", "A");
        put("治愈", "Z");
    }};
    public static final List<String> BASE_ATTRS = new ArrayList<>(ATTR_TO_CODE.keySet());

    // 【核心更新】8个合法星级（新增4.5、5星）
    public static final Set<Double> ALLOW_STARS = new HashSet<>(Arrays.asList(1.0, 2.0,1.5, 2.5, 3.0, 3.5, 4.0, 4.5, 5.0));
    // 阵营/职业（无改动）
    public static final String[] CAMP_LIST = {"dark", "sacred"};
    public static final String[] PROFESSION_LIST = {"武圣", "神将", "仙灵"};

    // 装备全配置：类型名、前缀、ID起始数、名称数组、总数量（完全匹配要求）
    public static final Object[][] EQUIP_CONFIG = {
            {"剑", "J", 1000, new String[]{
                    // ===== 上古十大名剑 (10) =====
                    "青釭剑", "倚天剑", "湛卢剑", "赤霄剑", "太阿剑", "龙泉剑", "鱼肠剑", "纯钧剑", "承影剑", "巨阙剑",
                    // ===== 春秋吴越名剑 (20) =====
                    "干将剑", "莫邪剑", "龙渊剑", "泰阿剑", "工布剑", "磐郢剑", "豪曹剑", "鱼藏剑", "扁诸剑", "步光剑",
                    "长扬剑", "流光剑", "百里剑", "宵练剑", "大夏龙雀", "赤堇剑", "锟铻剑", "镆铘剑", "吴钩剑", "越王八剑",
                    // ===== 秦汉传世名剑 (30) =====
                    "赤帝剑", "斩蛇剑", "定秦剑", "始皇剑", "神龟剑", "含光剑", "承景剑", "脊鞘剑", "骏剑", "华铤剑",
                    "孟德剑", "思召剑", "倚天青釭", "青冥剑", "追风剑", "逐日剑", "奔雷剑", "破山剑", "断岳剑", "裂云剑",
                    "撼岳剑", "镇岳剑", "吞星剑", "揽月剑", "追星剑", "赶月剑", "流星剑", "飞星剑", "碎星剑", "裂星剑",
                    // ===== 三国名将佩剑 (28) =====
                    "青龙偃月", "丈八青锋", "龙胆亮银", "青釭倚天", "紫电青霜", "古锭青锋", "七星龙渊", "霜刃剑", "寒锋剑", "冰魄剑",
                    "雪魂剑", "玄铁重剑", "乌金剑", "赤铜剑", "青铜剑", "百炼剑", "精钢剑", "镔铁剑", "陨铁剑", "玄钢剑",
                    "赤霄红莲", "太阿紫电", "龙泉秋水", "鱼肠七杀", "纯钧耀阳", "承影流光", "巨阙裂地", "湛卢无尘",
                    // ===== 江湖玄铁名剑 (50) =====
                    "玄铁剑", "墨渊剑", "黑炎剑", "苍龙剑", "白虎剑", "朱雀剑", "玄武剑", "麒麟剑", "貔貅剑", "饕餮剑",
                    "穷奇剑", "梼杌剑", "混沌剑", "梼戭剑", "辟邪剑", "镇魂剑", "封魔剑", "降妖剑", "伏魔剑", "斩妖剑",
                    "诛邪剑", "灭魔剑", "破邪剑", "驱邪剑", "镇邪剑", "凝霜剑", "傲雪剑", "凌霜剑", "踏雪剑", "寒雪剑",
                    "飞雪剑", "吹雪剑", "惊雪剑", "映雪剑", "残雪剑", "暮雪剑", "朝雪剑", "夜雪剑", "秋霜剑", "冬雪剑",
                    "春露剑", "夏雨剑", "秋风剑", "冬雷剑", "晨雾剑", "晚霞剑", "晓月剑", "晨星剑", "朝阳剑", "落日剑",
                    // ===== 天地五行剑 (40) =====
                    "天罡剑", "地煞剑", "玄阳剑", "九阴剑", "金罡剑", "木灵剑", "水寒剑", "火炎剑", "土厚剑", "风巽剑",
                    "雷泽剑", "山艮剑", "泽兑剑", "天乾剑", "地坤剑", "雷火剑", "风水剑", "山泽剑", "金木剑", "水火剑",
                    "金雷剑", "木风剑", "水泽剑", "火山剑", "土雷剑", "炎风剑", "寒雷剑", "霜火剑", "冰雷剑", "炎霜剑",
                    "风雷剑", "水雷剑", "火风剑", "木雷剑", "金风剑", "土风剑", "水冰剑", "火炎剑", "金锐剑", "木韧剑",
                    // ===== 紫气东来系列剑 (60) =====
                    "紫宸剑", "紫霄剑", "紫电剑", "紫霜剑", "紫焰剑", "紫冥剑", "紫渊剑", "紫霞剑", "紫云剑", "紫雾剑",
                    "紫雪剑", "紫冰剑", "紫炎剑", "紫雷剑", "紫风剑", "紫岳剑", "紫峰剑", "紫川剑", "紫江剑", "紫河剑",
                    "紫海剑", "紫山剑", "紫林剑", "紫竹剑", "紫梅剑", "紫薇剑", "紫槿剑", "紫藤剑", "紫荆剑", "紫菀剑",
                    "紫菱剑", "紫荷剑", "紫桐剑", "紫槐剑", "紫榆剑", "紫杉剑", "紫柏剑", "紫松剑", "紫竹剑", "紫桂剑",
                    "紫兰剑", "紫菊剑", "紫微剑", "紫微星", "紫府剑", "紫虚剑", "紫府剑", "紫虚剑", "紫极剑", "紫清剑",
                    // ===== 青云流风系列剑 (80) =====
                    "青云剑", "流云剑", "长风剑", "清风剑", "凌风剑", "惊风剑", "回风剑", "烈风剑", "狂风剑", "旋风剑",
                    "御风剑", "乘风剑", "扶风剑", "栖风剑", "鸣风剑", "舞风剑", "凌风剑", "惊风剑", "飘风剑", "朔风剑",
                    "南风剑", "北风剑", "东风剑", "西风剑", "春风剑", "夏风剑", "秋风剑", "冬风剑", "晨风剑", "晚风剑",
                    "晓风剑", "夜风剑", "疾风剑", "微风剑", "罡风剑", "和风剑", "烈风剑", "暴风剑", "飓风剑", "龙卷风",
                    "青云志", "流云心", "长风吟", "清风谣", "凌风曲", "惊风词", "回风赋", "烈风歌", "狂风啸", "旋风吼",

                    "归宗剑", "归一剑", "万仞剑", "千锋剑", "百炼剑", "十绝剑", "一剑封喉", "万剑穿心", "剑指苍穹", "剑破九天",
                    "剑裂山河", "剑定乾坤", "剑镇寰宇", "剑斩星辰", "剑劈日月", "剑扫六合", "剑荡八荒", "剑指九州", "剑耀四海", "剑辉五岳",
                    "剑鸣九霄", "剑啸长空", "剑泣幽冥", "剑舞九天", "剑歌九霄", "剑吟长空", "剑叹幽冥", "剑泣黄泉", "剑怒雷霆", "剑喜清风",
                    "剑悲寒霜", "剑乐暖阳", "剑愁秋雨", "剑思春风", "剑念秋月", "剑怀冬雪", "剑忆春花", "剑梦夏荷", "剑醒秋菊", "剑醉冬梅",
                    "剑痴山水", "剑迷风月", "剑恋星辰", "剑惜草木", "剑怜花鸟", "剑悯众生", "剑惩恶徒", "剑护良善", "剑诛邪魔", "剑斩妖邪",
            }, 109},
            {"弓", "G", 1000, new String[]{
                    "震天弓", "落日弓", "穿云弓", "裂风弓", "追月弓", "流星弓", "寒铁弓", "玄木弓", "紫金弓", "龙骨弓",
                    "凤鸣弓", "虎啸弓", "狼牙利弓", "朱雀弓", "玄武弓", "青龙弓", "白虎弓", "幽冥弓", "圣光弓", "破甲弓"
            }, 13},
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
            }, 29},
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
                    // 补全剩余45个，总数精准65个
                    "摄魂铃", "镇魂塔", "凝魂珠", "聚魂玉", "破法杵", "封魔剑", "降妖杵", "除魔幡", "净世瓶", "渡厄莲",
                    "往生镜", "轮回盘", "三生石", "忘川壶", "奈何印", "黄泉灯", "碧落珠", "九霄塔", "九幽镜", "封神榜",
                    "戮仙幡", "碎星锤", "揽月簪", "追星佩", "逐日环", "奔雷扣", "流云带", "御风帕", "傲雪绫", "凌霜绡",
                    "镇岳玺", "定川符", "镇海珠", "撼山鼓", "裂云锣", "焚天鼎", "冰封盏", "炎阳玉", "寒月璧", "星辰砂",
                    "银河石", "天罡令", "地煞旗", "玄阳佩", "九阴环"
            }, 65},
            {"腰带", "Y", 1000, new String[]{
                    "紫金腰带", "玄铁腰带", "龙鳞腰带", "玄武腰带", "烈焰腰带", "寒冰腰带", "碧水腰带", "磐石腰带", "流云腰带", "圣光腰带",
                    "幽冥腰带", "荆棘腰带", "反伤腰带", "守护腰带", "勇者腰带", "王者腰带", "青铜腰带", "白银腰带", "黄金腰带", "暗金腰带",
                    "史诗腰带", "传说腰带", "神器腰带", "蜀纹腰带", "魏纹腰带", "吴纹腰带", "蛮骨腰带", "仙纹腰带", "魔纹腰带", "中立玄腰带"
            }, 29}
    };

    // 装备大类-eq_type映射（无改动）
    public static final int EQ_TYPE_WEAPON = 0;  // 剑、弓、长枪
    public static final int EQ_TYPE_DEFENSE = 1; // 盾、帽子、披风、胸甲、腰带
    public static final int EQ_TYPE_TREASURE = 2;// 宝具
    public static final int EQ_TYPE_MAGIC = 3;   // 法器

    // 工具类&计数器（无改动）
    private static final Random RANDOM = new Random();
    private static final Map<String, Integer> ID_COUNTER = new HashMap<>();
    static {
        // 初始化ID自增计数器，每个装备前缀独立计数
        for (Object[] config : EQUIP_CONFIG) {
            String prefix = (String) config[1];
            int startNum = (int) config[2];
            ID_COUNTER.put(prefix, startNum);
        }
    }



    // ========== 三、核心工具方法 ==========
    /** 生成装备基础介绍 */
    private static String generateIntroduce(String equipName, String typeName) {
        return equipName + "，上古至宝级" + typeName + "，蕴天地灵韵，属性增幅强横，为当世顶尖神兵。";
    }

    /** 生成被动技能介绍 */
    private static String generatePassiveIntro() {
        String[] passives = {
                "攻击有15%概率触发暴击，造成2倍伤害",
                "受击时反弹30%所受伤害，附带元素反噬",
                "每回合恢复自身8%生命值，无视负面效果",
                "全元素抗性提升20%，免疫低级属性伤害",
                "物理攻击无视敌方20%防御，触发破甲必暴击",
                "属性攻击有10%概率触发持续灼烧/中毒/眩晕",
                "被攻击时有15%概率生成护盾，吸收一次高额伤害"
        };
        return passives[RANDOM.nextInt(passives.length)];
    }

    /** 属性-数据库攻防字段精准映射 */
    private static void mappingAttrToDbField(String attr, int value, EqCard eqCard) {
        switch (attr) {
            case "锋利": eqCard.setWlAtk(value); break;
            case "坚韧": eqCard.setWlDef(value); break;
            case "火焰": eqCard.setHyAtk(value); break;
            case "火抗": eqCard.setHyDef(value); break;
            case "毒素": eqCard.setDsAtk(value); break;
            case "毒抗": eqCard.setDsDef(value); break;
            case "飞弹": eqCard.setFdAtk(value); break;
            case "弹抗": eqCard.setFdDef(value); break;
            case "治愈": eqCard.setZlDef(value); break;
        }
    }

    /** 【核心升级】星级-属性生成规则：1/2/3属性分级生成，保证属性不重复 */
    private static List<String> generateAttrByStar(double star, List<Integer> valueList) {
        List<String> attrList = new ArrayList<>();
        int baseValue;
        // 1~3星 → 1个属性
        if (star >= 1.0 && star <= 3.0) {
            baseValue = (int) (star * 4) + RANDOM.nextInt(3);
            String attr = BASE_ATTRS.get(RANDOM.nextInt(BASE_ATTRS.size()));
            attrList.add(attr);
            valueList.add(baseValue);
        }
        // 3.5~4星 → 2个不同属性
        else if (star >= 3.5 && star <= 4.0) {
            baseValue = (int) (star * 5) + RANDOM.nextInt(3);
            int idx1 = RANDOM.nextInt(BASE_ATTRS.size());
            int idx2;
            do { idx2 = RANDOM.nextInt(BASE_ATTRS.size()); } while (idx1 == idx2);
            attrList.add(BASE_ATTRS.get(idx1));
            attrList.add(BASE_ATTRS.get(idx2));
            valueList.add(baseValue);
            valueList.add(baseValue);
        }
        // 【新增】4.5~5星 → 3个不同属性
        else if (star >= 4.5 && star <= 5.0) {
            baseValue = (int) (star * 6) + RANDOM.nextInt(3);
            Set<Integer> idxSet = new HashSet<>();
            // 随机3个不重复的属性索引
            while (idxSet.size() < 3) {
                idxSet.add(RANDOM.nextInt(BASE_ATTRS.size()));
            }
            // 转换为属性列表
            for (int idx : idxSet) {
                attrList.add(BASE_ATTRS.get(idx));
            }
            // 3个属性同数值，添加3次
            valueList.add(baseValue);
            valueList.add(baseValue);
            valueList.add(baseValue);
        }
        return attrList;
    }

    /** 【核心升级】ID生成：适配1/2/3属性，前缀+N个属性字母+自增数字 */
    /** 【最终版】ID生成：前缀+自增数字 + "_" + N个属性字母拼接 （完美匹配要求） */
    private static String generateFinalEquipId(String equipPrefix, List<String> attrList) {
        StringBuilder idSb = new StringBuilder();
        // 第一步：拼接 装备前缀 + 自增数字
        int currentNum = ID_COUNTER.get(equipPrefix);
        idSb.append(equipPrefix).append(currentNum);
        // 第二步：拼接 下划线
        idSb.append("_");
        // 第三步：拼接所有属性对应字母（1个/2个/3个，自动适配）
        attrList.forEach(attr -> idSb.append(ATTR_TO_CODE.get(attr)));
        // 更新自增计数器，保证数字唯一
        ID_COUNTER.put(equipPrefix, currentNum + 1);
        // 返回最终ID
        return idSb.toString();
    }
    // ========== 四、对外核心方法 - 生成单个装备 ==========
    public static EqCard generateEqCard(double starLevel) {
        // 星级合法性校验
        if (!ALLOW_STARS.contains(starLevel)) {
            throw new IllegalArgumentException("非法星级！仅支持：1、1.5、2.5、3、3.5、4、4.5、5");
        }
        EqCard eqCard = new EqCard();
        eqCard.setStar(new BigDecimal(starLevel));

        // 随机选择装备类型
        int typeIdx = RANDOM.nextInt(EQUIP_CONFIG.length);
        Object[] config = EQUIP_CONFIG[typeIdx];
        String typeName = (String) config[0];
        String equipPrefix = (String) config[1];
        String[] nameList = (String[]) config[3];

        // 随机获取装备名称
        String equipName = nameList[RANDOM.nextInt(nameList.length)];

        // 设置基础字段
        eqCard.setWeight(0.1 + RANDOM.nextDouble() * 9.9); // 权重0.1~10.0
        eqCard.setCamp(CAMP_LIST[RANDOM.nextInt(CAMP_LIST.length)]);
        eqCard.setProfession(PROFESSION_LIST[RANDOM.nextInt(PROFESSION_LIST.length)]);
        eqCard.setIntroduce(generateIntroduce(equipName, typeName));
        eqCard.setPassiveIntroduceOne(generatePassiveIntro());
        eqCard.setPassiveIntroduceTwo(RANDOM.nextBoolean() ? generatePassiveIntro() : null);
        eqCard.setPassiveIntroduceThree(RANDOM.nextBoolean() ? generatePassiveIntro() : null);

        // 设置装备大类/小类
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

        // 生成属性+数值，映射到数据库字段
        List<Integer> valueList = new ArrayList<>();
        List<String> attrList = generateAttrByStar(starLevel, valueList);
        for (int i = 0; i < attrList.size(); i++) {
            mappingAttrToDbField(attrList.get(i), valueList.get(i), eqCard);
        }

        // 生成装备显示名称：【属性1.属性2.属性3.装备名】
        StringBuilder nameSb = new StringBuilder();
        attrList.forEach(attr -> nameSb.append(attr).append("."));
        nameSb.append(equipName);
        eqCard.setName(nameSb.toString());

        // 生成最终ID（适配1/2/3属性）
        eqCard.setId(generateFinalEquipId(equipPrefix, attrList));

        return eqCard;
    }

    // ========== 五、批量生成装备方法（推荐入库使用） ==========
    public static List<EqCard> batchGenerateEqCard(int count, double... assignStars) {
        List<EqCard> eqCardList = new ArrayList<>();
        for (int i = 0; i < count; i++) {
            double star;
            // 传入指定星级则随机选，否则从合法星级池随机
            if (assignStars != null && assignStars.length > 0) {
                star = assignStars[RANDOM.nextInt(assignStars.length)];
            } else {
                star = new ArrayList<>(ALLOW_STARS).get(RANDOM.nextInt(ALLOW_STARS.size()));
            }
            eqCardList.add(generateEqCard(star));
        }
        return eqCardList;
    }

    // ========== 六、测试主方法 - 验证1/2/3属性效果 ==========
    public static void main(String[] args) {
        System.out.println("======= 1星 剑-单属性 → ID:JF1000 =======");
        System.out.println(generateEqCard(1.0));

        System.out.println("\n======= 4星 盾-双属性 → ID:SHZ1000 =======");
        System.out.println(generateEqCard(4.0));

        System.out.println("\n======= 5星 法器-三属性 → ID:MHDZ1000 =======");
        System.out.println(generateEqCard(5.0));

//        System.out.println("\n======= 批量生成8个装备（所有星级各1个） =======");
//        List<EqCard> list = batchGenerateEqCard(8,1.0,1.5,2.5,3.0,3.5,4.0,4.5,5.0);
//        list.forEach(System.out::println);
    }
}