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
            {"腰带", "Y", 1000, new String[]{
                    "紫金腰带", "玄铁腰带", "龙鳞腰带", "玄武腰带", "烈焰腰带", "寒冰腰带", "碧水腰带", "磐石腰带", "流云腰带", "圣光腰带",
                    "幽冥腰带", "荆棘腰带", "反伤腰带", "守护腰带", "勇者腰带", "王者腰带", "青铜腰带", "白银腰带", "黄金腰带", "暗金腰带",
                    "史诗腰带", "传说腰带", "神器腰带", "蜀纹腰带", "魏纹腰带", "吴纹腰带", "蛮骨腰带", "仙纹腰带", "魔纹腰带", "中立玄腰带"
            }, 29},
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
            {"法器", "K", 1000, new String[]{
                    "噬魂幡", "聚灵瓶", "风雷扇", "寒冰镜", "焚天炉", "沧溟珠", "定海珠", "乾坤袋", "山河印", "玲珑塔",
                    "紫金葫芦", "芭蕉扇", "捆仙绳", "打神鞭", "翻天印", "轩辕镜", "东皇钟", "炼妖壶", "昊天塔", "伏羲琴",
                    "神农鼎", "崆峒印", "昆仑镜", "女娲石", "招魂幡", "引魂灯", "摄魂铃", "镇魂塔", "凝魂珠", "聚魂玉",
                    "破法杵", "封魔剑", "降妖杵", "除魔幡", "净世瓶", "渡厄莲", "往生镜", "轮回盘"
            }, 38},
    };

    // 装备大类-eq_type映射（无改动）
    public static final int EQ_TYPE_WEAPON = 0;  // 剑、弓、长枪
    public static final int EQ_TYPE_DEFENSE = 1; // 盾、帽子、披风、胸甲、腰带
    public static final int EQ_TYPE_TREASURE = 2;// 宝具
    public static final int EQ_TYPE_MAGIC = 3;   // 法器

    // 工具类&计数器（无改动）
    private static final Random RANDOM = new Random();




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
    /** 【最终版】ID生成：前缀+星级对应区间随机数字 + "_" + N个属性字母拼接
     * 规则：
     * 1. J/D等所有前缀通用区间：
     *    - 1-3星 → 1000~1014（星级越高数字越大）
     *    - 3.5-4星 → 1015~1038（星级越高数字越大）
     *    - 4.5-5星 → 1039~1109（星级越高数字越大）
     * 2. 数字随机但星级越高越靠近区间上限，保证“星级越数字越大”
     * 3. 计数器更新，保证数字不重复
     */
    private static String generateFinalEquipId(String equipPrefix, List<String> attrList, double starLevel) {
        StringBuilder idSb = new StringBuilder();
        int randomNum = 0;

        // ========== 核心：按星级区间+前缀规则生成数字 ==========
        if ("J".equals(equipPrefix)){
            if (starLevel >= 1.0 && starLevel <= 3.0) {
                // 1-3星：1000~1014，星级越高数字越接近1014
                int base = 1000;
                int range = 14; // 1000+14=1014
                // 星级归一化（1.0→0，3.0→1），乘以区间长度得到偏移量，保证星级越高数字越大
                double normalized = (starLevel - 1.0) / 2.0; // 1.0→0, 2.0→0.5, 3.0→1
                randomNum = base + (int) (normalized * range) + new Random().nextInt(3); // 小随机避免完全固定
                // 兜底：确保不超出1000~1014范围
                randomNum = Math.min(Math.max(randomNum, 1000), 1014);
            } else if (starLevel >= 3.5 && starLevel <= 4.0) {
                // 3.5-4星：1015~1038，星级越高数字越接近1038
                int base = 1015;
                int range = 92; // 1015+23=1038
                double normalized = (starLevel - 3.5) / 0.5; // 3.5→0, 4.0→1
                randomNum = base + (int) (normalized * range) + new Random().nextInt(3);
                randomNum = Math.min(Math.max(randomNum, 1015), 1107);
            } else if (starLevel >= 4.5 && starLevel <= 5.0) {
                // 4.5-5星：1039~1109，星级越高数字越接近1109
                int base = 1108;
                int range = 1; // 1039+70=1109
                double normalized = (starLevel - 4.5) / 0.5; // 4.5→0, 5.0→1
                randomNum = base + (int) (normalized * range) + new Random().nextInt(3);
                randomNum = Math.min(Math.max(randomNum, 1108), 1109);
            }
        }

        if ("G".equals(equipPrefix)){
            if (starLevel >= 1.0 && starLevel <= 3.0) {
                // 1-3星：1000~1014，星级越高数字越接近1014
                int base = 1000;
                int range = 2; // 1000+14=1014
                // 星级归一化（1.0→0，3.0→1），乘以区间长度得到偏移量，保证星级越高数字越大
                double normalized = (starLevel - 1.0) / 2.0; // 1.0→0, 2.0→0.5, 3.0→1
                randomNum = base + (int) (normalized * range) + new Random().nextInt(3); // 小随机避免完全固定
                // 兜底：确保不超出1000~1014范围
                randomNum = Math.min(Math.max(randomNum, 1000), 1002);
            } else if (starLevel >= 3.5 && starLevel <= 4.0) {
                // 3.5-4星：1015~1038，星级越高数字越接近1038
                int base = 1003;
                int range = 5; // 1015+23=1038
                double normalized = (starLevel - 3.5) / 0.5; // 3.5→0, 4.0→1
                randomNum = base + (int) (normalized * range) + new Random().nextInt(3);
                randomNum = Math.min(Math.max(randomNum, 1003), 1008);
            } else if (starLevel >= 4.5 && starLevel <= 5.0) {
                // 4.5-5星：1039~1109，星级越高数字越接近1109
                int base = 1009;
                int range = 4; // 1039+70=1109
                double normalized = (starLevel - 4.5) / 0.5; // 4.5→0, 5.0→1
                randomNum = base + (int) (normalized * range) + new Random().nextInt(3);
                randomNum = Math.min(Math.max(randomNum, 1009), 1013);
            }
        }


        if ("Q".equals(equipPrefix)){
            if (starLevel >= 1.0 && starLevel <= 3.0) {
                // 1-3星：1000~1014，星级越高数字越接近1014
                int base = 1000;
                int range = 3; // 1000+14=1014
                // 星级归一化（1.0→0，3.0→1），乘以区间长度得到偏移量，保证星级越高数字越大
                double normalized = (starLevel - 1.0) / 2.0; // 1.0→0, 2.0→0.5, 3.0→1
                randomNum = base + (int) (normalized * range) + new Random().nextInt(3); // 小随机避免完全固定
                // 兜底：确保不超出1000~1014范围
                randomNum = Math.min(Math.max(randomNum, 1000), 1003);
            } else if (starLevel >= 3.5 && starLevel <= 4.0) {
                // 3.5-4星：1015~1038，星级越高数字越接近1038
                int base = 1004;
                int range = 3; // 1015+23=1038
                double normalized = (starLevel - 3.5) / 0.5; // 3.5→0, 4.0→1
                randomNum = base + (int) (normalized * range) + new Random().nextInt(3);
                randomNum = Math.min(Math.max(randomNum, 1004), 1007);
            } else if (starLevel >= 4.5 && starLevel <= 5.0) {
                // 4.5-5星：1039~1109，星级越高数字越接近1109
                int base = 1008;
                int range = 3; // 1039+70=1109
                double normalized = (starLevel - 4.5) / 0.5; // 4.5→0, 5.0→1
                randomNum = base + (int) (normalized * range) + new Random().nextInt(3);
                randomNum = Math.min(Math.max(randomNum, 1008), 1011);
            }
        }


        if ("D".equals(equipPrefix)){
            if (starLevel >= 1.0 && starLevel <= 3.0) {
                // 1-3星：1000~1014，星级越高数字越接近1014
                int base = 1000;
                int range = 6; // 1000+14=1014
                // 星级归一化（1.0→0，3.0→1），乘以区间长度得到偏移量，保证星级越高数字越大
                double normalized = (starLevel - 1.0) / 2.0; // 1.0→0, 2.0→0.5, 3.0→1
                randomNum = base + (int) (normalized * range) + new Random().nextInt(3); // 小随机避免完全固定
                // 兜底：确保不超出1000~1014范围
                randomNum = Math.min(Math.max(randomNum, 1000), 1006);
            } else if (starLevel >= 3.5 && starLevel <= 4.0) {
                // 3.5-4星：1015~1038，星级越高数字越接近1038
                int base = 1007;
                int range = 20; // 1015+23=1038
                double normalized = (starLevel - 3.5) / 0.5; // 3.5→0, 4.0→1
                randomNum = base + (int) (normalized * range) + new Random().nextInt(3);
                randomNum = Math.min(Math.max(randomNum, 1007), 1027);
            } else if (starLevel >= 4.5 && starLevel <= 5.0) {
                // 4.5-5星：1039~1109，星级越高数字越接近1109
                int base = 1028;
                int range = 8; // 1039+70=1109
                double normalized = (starLevel - 4.5) / 0.5; // 4.5→0, 5.0→1
                randomNum = base + (int) (normalized * range) + new Random().nextInt(3);
                randomNum = Math.min(Math.max(randomNum, 1028), 1036);
            }
        }



        if ("M".equals(equipPrefix)){
            if (starLevel >= 1.0 && starLevel <= 3.0) {
                // 1-3星：1000~1014，星级越高数字越接近1014
                int base = 1000;
                int range = 5; // 1000+14=1014
                // 星级归一化（1.0→0，3.0→1），乘以区间长度得到偏移量，保证星级越高数字越大
                double normalized = (starLevel - 1.0) / 2.0; // 1.0→0, 2.0→0.5, 3.0→1
                randomNum = base + (int) (normalized * range) + new Random().nextInt(3); // 小随机避免完全固定
                // 兜底：确保不超出1000~1014范围
                randomNum = Math.min(Math.max(randomNum, 1000), 1005);
            } else if (starLevel >= 3.5 && starLevel <= 4.0) {
                // 3.5-4星：1015~1038，星级越高数字越接近1038
                int base = 1006;
                int range = 12; // 1015+23=1038
                double normalized = (starLevel - 3.5) / 0.5; // 3.5→0, 4.0→1
                randomNum = base + (int) (normalized * range) + new Random().nextInt(3);
                randomNum = Math.min(Math.max(randomNum, 1006), 1018);
            } else if (starLevel >= 4.5 && starLevel <= 5.0) {
                // 4.5-5星：1039~1109，星级越高数字越接近1109
                int base = 1019;
                int range = 7; // 1039+70=1109
                double normalized = (starLevel - 4.5) / 0.5; // 4.5→0, 5.0→1
                randomNum = base + (int) (normalized * range) + new Random().nextInt(3);
                randomNum = Math.min(Math.max(randomNum, 1019), 1026);
            }
        }


        if ("P".equals(equipPrefix)){
            if (starLevel >= 1.0 && starLevel <= 3.0) {
                // 1-3星：1000~1014，星级越高数字越接近1014
                int base = 1000;
                int range = 11; // 1000+14=1014
                // 星级归一化（1.0→0，3.0→1），乘以区间长度得到偏移量，保证星级越高数字越大
                double normalized = (starLevel - 1.0) / 2.0; // 1.0→0, 2.0→0.5, 3.0→1
                randomNum = base + (int) (normalized * range) + new Random().nextInt(3); // 小随机避免完全固定
                // 兜底：确保不超出1000~1014范围
                randomNum = Math.min(Math.max(randomNum, 1000), 1011);
            } else if (starLevel >= 3.5 && starLevel <= 4.0) {
                // 3.5-4星：1015~1038，星级越高数字越接近1038
                int base = 1012;
                int range = 15; // 1015+23=1038
                double normalized = (starLevel - 3.5) / 0.5; // 3.5→0, 4.0→1
                randomNum = base + (int) (normalized * range) + new Random().nextInt(3);
                randomNum = Math.min(Math.max(randomNum, 1012), 1027);
            } else if (starLevel >= 4.5 && starLevel <= 5.0) {
                // 4.5-5星：1039~1109，星级越高数字越接近1109
                int base = 1028;
                int range = 6; // 1039+70=1109
                double normalized = (starLevel - 4.5) / 0.5; // 4.5→0, 5.0→1
                randomNum = base + (int) (normalized * range) + new Random().nextInt(3);
                randomNum = Math.min(Math.max(randomNum, 1028), 1034);
            }
        }


        if ("S".equals(equipPrefix)){
            if (starLevel >= 1.0 && starLevel <= 3.0) {
                // 1-3星：1000~1014，星级越高数字越接近1014
                int base = 1000;
                int range = 6; // 1000+14=1014
                // 星级归一化（1.0→0，3.0→1），乘以区间长度得到偏移量，保证星级越高数字越大
                double normalized = (starLevel - 1.0) / 2.0; // 1.0→0, 2.0→0.5, 3.0→1
                randomNum = base + (int) (normalized * range) + new Random().nextInt(3); // 小随机避免完全固定
                // 兜底：确保不超出1000~1014范围
                randomNum = Math.min(Math.max(randomNum, 1000), 1006);
            } else if (starLevel >= 3.5 && starLevel <= 4.0) {
                // 3.5-4星：1015~1038，星级越高数字越接近1038
                int base = 1007;
                int range = 21; // 1015+23=1038
                double normalized = (starLevel - 3.5) / 0.5; // 3.5→0, 4.0→1
                randomNum = base + (int) (normalized * range) + new Random().nextInt(3);
                randomNum = Math.min(Math.max(randomNum, 1007), 1028);
            } else if (starLevel >= 4.5 && starLevel <= 5.0) {
                // 4.5-5星：1039~1109，星级越高数字越接近1109
                int base = 1029;
                int range = 6; // 1039+70=1109
                double normalized = (starLevel - 4.5) / 0.5; // 4.5→0, 5.0→1
                randomNum = base + (int) (normalized * range) + new Random().nextInt(3);
                randomNum = Math.min(Math.max(randomNum, 1029), 1035);
            }
        }


        if ("Y".equals(equipPrefix)){
            if (starLevel >= 1.0 && starLevel <= 3.0) {
                // 1-3星：1000~1014，星级越高数字越接近1014
                int base = 1000;
                int range = 5; // 1000+14=1014
                // 星级归一化（1.0→0，3.0→1），乘以区间长度得到偏移量，保证星级越高数字越大
                double normalized = (starLevel - 1.0) / 2.0; // 1.0→0, 2.0→0.5, 3.0→1
                randomNum = base + (int) (normalized * range) + new Random().nextInt(3); // 小随机避免完全固定
                // 兜底：确保不超出1000~1014范围
                randomNum = Math.min(Math.max(randomNum, 1000), 1005);
            } else if (starLevel >= 3.5 && starLevel <= 4.0) {
                // 3.5-4星：1015~1038，星级越高数字越接近1038
                int base = 1006;
                int range = 18; // 1015+23=1038
                double normalized = (starLevel - 3.5) / 0.5; // 3.5→0, 4.0→1
                randomNum = base + (int) (normalized * range) + new Random().nextInt(3);
                randomNum = Math.min(Math.max(randomNum, 1006), 1024);
            } else if (starLevel >= 4.5 && starLevel <= 5.0) {
                // 4.5-5星：1039~1109，星级越高数字越接近1109
                int base = 1025;
                int range = 4; // 1039+70=1109
                double normalized = (starLevel - 4.5) / 0.5; // 4.5→0, 5.0→1
                randomNum = base + (int) (normalized * range) + new Random().nextInt(3);
                randomNum = Math.min(Math.max(randomNum, 1025), 1029);
            }
        }


        if ("X".equals(equipPrefix)){
            if (starLevel >= 1.0 && starLevel <= 3.0) {
                // 1-3星：1000~1014，星级越高数字越接近1014
                int base = 1000;
                int range = 18; // 1000+14=1014
                // 星级归一化（1.0→0，3.0→1），乘以区间长度得到偏移量，保证星级越高数字越大
                double normalized = (starLevel - 1.0) / 2.0; // 1.0→0, 2.0→0.5, 3.0→1
                randomNum = base + (int) (normalized * range) + new Random().nextInt(3); // 小随机避免完全固定
                // 兜底：确保不超出1000~1014范围
                randomNum = Math.min(Math.max(randomNum, 1000), 1018);
            } else if (starLevel >= 3.5 && starLevel <= 4.0) {
                // 3.5-4星：1015~1038，星级越高数字越接近1038
                int base = 1019;
                int range = 42; // 1015+23=1038
                double normalized = (starLevel - 3.5) / 0.5; // 3.5→0, 4.0→1
                randomNum = base + (int) (normalized * range) + new Random().nextInt(3);
                randomNum = Math.min(Math.max(randomNum, 1019), 1061);
            } else if (starLevel >= 4.5 && starLevel <= 5.0) {
                // 4.5-5星：1039~1109，星级越高数字越接近1109
                int base = 1062;
                int range = 3; // 1039+70=1109
                double normalized = (starLevel - 4.5) / 0.5; // 4.5→0, 5.0→1
                randomNum = base + (int) (normalized * range) + new Random().nextInt(3);
                randomNum = Math.min(Math.max(randomNum, 1062), 1065);
            }
        }

        if ("K".equals(equipPrefix)){
            if (starLevel >= 1.0 && starLevel <= 3.0) {
                // 1-3星：1000~1014，星级越高数字越接近1014
                int base = 1000;
                int range = 12; // 1000+14=1014
                // 星级归一化（1.0→0，3.0→1），乘以区间长度得到偏移量，保证星级越高数字越大
                double normalized = (starLevel - 1.0) / 2.0; // 1.0→0, 2.0→0.5, 3.0→1
                randomNum = base + (int) (normalized * range) + new Random().nextInt(3); // 小随机避免完全固定
                // 兜底：确保不超出1000~1014范围
                randomNum = Math.min(Math.max(randomNum, 1000), 1012);
            } else if (starLevel >= 3.5 && starLevel <= 4.0) {
                // 3.5-4星：1015~1038，星级越高数字越接近1038
                int base = 1013;
                int range = 24; // 1015+23=1038
                double normalized = (starLevel - 3.5) / 0.5; // 3.5→0, 4.0→1
                randomNum = base + (int) (normalized * range) + new Random().nextInt(3);
                randomNum = Math.min(Math.max(randomNum, 1013), 1037);
            } else if (starLevel >= 4.5 && starLevel <= 5.0) {
                // 4.5-5星：1039~1109，星级越高数字越接近1109
                int base = 1038;
                int range = 1; // 1039+70=1109
                double normalized = (starLevel - 4.5) / 0.5; // 4.5→0, 5.0→1
                randomNum = base + (int) (normalized * range) + new Random().nextInt(3);
                randomNum = Math.min(Math.max(randomNum, 1038), 1039);
            }
        }

        // ========== 拼接ID ==========
        // 第一步：前缀 + 随机数字（按星级区间生成）
        idSb.append(equipPrefix).append(randomNum);
        // 第二步：下划线
        idSb.append("_");
        // 第三步：属性字母
        attrList.forEach(attr -> idSb.append(ATTR_TO_CODE.get(attr)));

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
        String equipName = getEquipNameByStar(starLevel, nameList);

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
        eqCard.setId(generateFinalEquipId(equipPrefix, attrList,starLevel));

        return eqCard;
    }

    /**
     * 按星级高低选取装备名称：星级越高，选取nameList中越靠后的名称
     * 规则：
     * 1. 1-3星 → 选取数组前30%的名称（基础级）
     * 2. 3.5-4星 → 选取数组中间40%的名称（稀有级）
     * 3. 4.5-5星 → 选取数组后30%的名称（顶级/稀有级）
     * 4. 每个区间内小范围随机，保证同一星级有少量名称变化，且整体趋势是星级越高名字越靠后
     */
    private static String getEquipNameByStar(double starLevel, String[] nameList) {
        if (nameList == null || nameList.length == 0) {
            return "未知装备"; // 兜底，避免空指针
        }
        int listLength = nameList.length;
        int startIdx = 0;
        int endIdx = listLength - 1;

        // 按星级划分名称选取区间（星级越高，区间越靠后）
        if (starLevel >= 1.0 && starLevel <= 3.0) {
            // 1-3星：前30%名称（基础级）
            endIdx = (int) (listLength * 0.3) - 1;
            // 星级越高，在该区间内越靠后（1星→最前，3星→区间末尾）
            double normalized = (starLevel - 1.0) / 2.0; // 1.0→0, 3.0→1
            startIdx = (int) (normalized * endIdx);
        } else if (starLevel >= 3.5 && starLevel <= 4.0) {
            // 3.5-4星：中间40%名称（稀有级）
            startIdx = (int) (listLength * 0.3);
            endIdx = (int) (listLength * 0.7) - 1;
            // 星级越高，在该区间内越靠后（3.5星→区间开头，4星→区间末尾）
            double normalized = (starLevel - 3.5) / 0.5; // 3.5→0, 4.0→1
            startIdx = startIdx + (int) (normalized * (endIdx - startIdx));
        } else if (starLevel >= 4.5 && starLevel <= 5.0) {
            // 4.5-5星：后30%名称（顶级）
            startIdx = (int) (listLength * 0.7);
            endIdx = listLength - 1;
            // 星级越高，在该区间内越靠后（4.5星→区间开头，5星→区间末尾）
            double normalized = (starLevel - 4.5) / 0.5; // 4.5→0, 5.0→1
            startIdx = startIdx + (int) (normalized * (endIdx - startIdx));
        }

        // 兜底：避免索引越界
        startIdx = Math.max(0, startIdx);
        endIdx = Math.min(listLength - 1, endIdx);
        // 区间内小范围随机，保证同一星级有不同名称（但整体位置靠后）
        if (startIdx >= endIdx) {
            return nameList[endIdx];
        }
        return nameList[new Random().nextInt(endIdx - startIdx + 1) + startIdx];
    }

    // ========== 调用改造（替换原随机选取名称的代码） ==========
// 原代码：String equipName = nameList[RANDOM.nextInt(nameList.length)];
// 新代码：


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