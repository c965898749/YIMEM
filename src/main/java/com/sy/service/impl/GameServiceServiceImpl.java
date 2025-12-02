package com.sy.service.impl;

import cn.hutool.core.util.IdUtil;
import com.sy.mapper.game.*;
import com.sy.mapper.UserMapper;
import com.sy.model.User;
import com.sy.model.game.*;
import com.sy.model.game.Character;
import com.sy.model.resp.BaseResp;
import com.sy.service.GameServiceService;
import com.sy.service.UserServic;
import com.sy.tool.*;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.ValueOperations;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.DigestUtils;
import org.springframework.util.StringUtils;

import javax.servlet.http.HttpServletRequest;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.text.SimpleDateFormat;
import java.time.*;
import java.time.format.DateTimeFormatter;
import java.time.temporal.ChronoUnit;
import java.util.*;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

@Service
public class GameServiceServiceImpl implements GameServiceService {
    @Autowired
    private UserMapper userMapper;
    @Autowired
    private RedisTemplate redisTemplate;
    @Autowired
    private CharactersMapper charactersMapper;
    @Autowired
    private CardMapper cardMapper;
    @Autowired
    private GameFightMapper gameFightMapper;
    @Autowired
    private QqCardExpMapper qqCardExpMapper;
    @Autowired
    UserServic servic;
    @Autowired
    private PveDetailMapper pveDetailMapper;
    @Autowired
    private GameGiftMapper gameGiftMapper;
    @Autowired
    private GameGiftContentMapper gameGiftContentMapper;
    @Autowired
    private GameGiftRecordMapper gameGiftRecordMapper;
    @Autowired
    private GameGiftRuleMapper gameGiftRuleMapper;
    @Autowired
    private StarSynthesisMainMapper starSynthesisMainMapper;
    @Autowired
    private StarSynthesisMaterialsMapper starSynthesisMaterialsMapper;
    @Autowired
    private GameGiftExchangeCodeMapper gameGiftExchangeCodeMapper;
    @Autowired
    private GameItemShopMapper gameItemShopMapper;
    @Autowired
    private UserActivityRecordsMapper recordMapper;
    @Autowired
    private ActivityConfigMapper configMapper;
    @Autowired
    private ActivityRewardMapper rewardMapper;  // 新增奖励表Mapper
    @Autowired
    private ActivityDetailMapper activityDetailMapper;
    // 最大体力值
    private static final int MAX_STAMINA = 720;
    // 每10分钟恢复1点体力
    private static final long RECOVER_INTERVAL_MINUTES = 10;

    // 关卡结构定义：第一层5个，第二层6个，第三层10个
    private static final int LAYER1_MAX = 5;
    private static final int LAYER2_MAX = 6;
    private static final int LAYER3_MAX = 10;
    private static final int MAX_LEVEL = 50;
    // 有效难度等级白名单

    @Override
    public BaseResp loginGame(User user, HttpServletRequest request) throws Exception {
        BaseResp baseResp = new BaseResp();
        if (user == null) {
            baseResp.setSuccess(0);
            baseResp.setErrorMsg("请输入账号和密码");
            return baseResp;
        }
        if (Xtool.isNull(user.getUsername()) || Xtool.isNull(user.getUserpassword())) {
            baseResp.setSuccess(0);
            baseResp.setErrorMsg("请输入账号和密码");
            return baseResp;
        }
        String password = DigestUtils.md5DigestAsHex(user.getUserpassword().getBytes());
        User emp = userMapper.selectUserByusername(user.getUsername());
        if (emp == null) {
            baseResp.setSuccess(0);
            baseResp.setErrorMsg("用户名或密码错误");
            return baseResp;
        }
        //4、密码比对，如果不一致则返回登录失败结果
        if (!emp.getUserpassword().equals(password)) {
            baseResp.setSuccess(0);
            baseResp.setErrorMsg("用户名或密码错误");
            return baseResp;
        }
        //5、查看状态，如果为已禁用状态，则返回员工已禁用结果
        if (emp.getStatus() == 0) {
            baseResp.setSuccess(0);
            baseResp.setErrorMsg("账号已被禁用");
            return baseResp;
        }
        updateStaminaOnLogin(emp);
        //先判断今天是否签到
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
        if (emp.getSignTime() != null) {
            String today = sdf.format(emp.getSignTime()); // 获取今天的日期
            String dateTime = sdf.format(new Date()); // 获取当前日期和时间
            if (!today.equals(dateTime) && emp.getSignCount() == 7) { // 判断字符串日期是否相等
                emp.setSignCount(0);
            }
        }
        baseResp.setSuccess(1);
        UserInfo info = new UserInfo();
        BeanUtils.copyProperties(emp, info);
        //获取卡牌数据
        List<Characters> characterList = charactersMapper.selectByUserId(emp.getUserId());
        //卡池数量
        List<Card> cardList = cardMapper.selectAll();
        info.setUseCardCount(cardList.size() + "");
        info.setCharacterList(characterList);
        String token = IdUtil.simpleUUID();
        info.setToken(token);
        ValueOperations opsForValue = redisTemplate.opsForValue();
        opsForValue.set(token, emp.getUserId() + "", 2592000, TimeUnit.SECONDS);
        baseResp.setData(info);
        baseResp.setErrorMsg("登录成功");
        return baseResp;
    }

    public void updateStaminaOnLogin(User user) {
        Integer stamina = user.getTiliCount();
        if (user.getTiliCountTime() == null) {
            user.setTiliCount(720);
            user.setTiliCountTime(new Date());
        } else {
            LocalDateTime now = LocalDateTime.now();
            // 计算上次更新到现在的时间差（分钟）
            LocalDateTime lastStaminaUpdateTime = user.getTiliCountTime().toInstant().atZone(ZoneId.systemDefault()).toLocalDateTime();

            long minutesPassed = Duration.between(lastStaminaUpdateTime, now).toMinutes();

            // 计算可恢复的体力值（每10分钟1点）
            int recoverStamina = (int) (minutesPassed / RECOVER_INTERVAL_MINUTES);

            // 更新体力（不超过最大值）
            int newStamina = Math.min(stamina + recoverStamina, MAX_STAMINA);

            // 只有体力有变化时才更新时间（优化处理）
            if (newStamina != stamina) {
                stamina = newStamina;
            }
            user.setTiliCount(stamina);
            user.setTiliCountTime(new Date());
        }
        if (user.getHuoliCountTime() == null) {
            user.setHuoliCount(720);
            user.setHuoliCountTime(new Date());
        } else {
            LocalDateTime now = LocalDateTime.now();
            // 计算上次更新到现在的时间差（分钟）
            LocalDateTime lastStaminaUpdateTime = user.getHuoliCountTime().toInstant().atZone(ZoneId.systemDefault()).toLocalDateTime();

            long minutesPassed = Duration.between(lastStaminaUpdateTime, now).toMinutes();

            // 计算可恢复的体力值（每10分钟1点）
            int recoverStamina = (int) (minutesPassed / RECOVER_INTERVAL_MINUTES);

            // 更新体力（不超过最大值）
            int newStamina = Math.min(stamina + recoverStamina, MAX_STAMINA);

            // 只有体力有变化时才更新时间（优化处理）
            if (newStamina != stamina) {
                stamina = newStamina;
            }
            user.setHuoliCount(stamina);
            user.setHuoliCountTime(new Date());
        }
        userMapper.updateuser(user);
    }

    @Override
    public BaseResp registerGame(User user, HttpServletRequest request) throws Exception {
        BaseResp baseResp = new BaseResp();
        try {
            if (user == null) {
                baseResp.setSuccess(0);
                baseResp.setErrorMsg("请输入账号和密码");
                return baseResp;
            }
            if (Xtool.isNull(user.getUsername()) || Xtool.isNull(user.getUserpassword())) {
                baseResp.setSuccess(0);
                baseResp.setErrorMsg("请输入账号和密码");
                return baseResp;
            }
            baseResp = servic.addUser(user.getUsername(), user.getUserpassword());
            return baseResp;
        } catch (Exception e) {
            e.printStackTrace();
            baseResp.setSuccess(0);
            return baseResp;
        }
    }

    @Override
    public BaseResp updateGame(TokenDto token, HttpServletRequest request) throws Exception {
        BaseResp baseResp = new BaseResp();
        if (token == null || Xtool.isNull(token.getToken())) {
            baseResp.setSuccess(0);
            baseResp.setErrorMsg("登录过期");
            return baseResp;
        }
        String userId = (String) redisTemplate.opsForValue().get(token.getToken());
        if (Xtool.isNull(userId)) {
            baseResp.setSuccess(0);
            baseResp.setErrorMsg("登录过期");
            return baseResp;
        }
        User user = userMapper.selectUserByUserId(Integer.parseInt(userId));
        updateStaminaOnLogin(user);
        //先判断今天是否签到
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
        if (user.getSignTime() != null) {
            String today = sdf.format(user.getSignTime()); // 获取今天的日期
            String dateTime = sdf.format(new Date()); // 获取当前日期和时间
            if (!today.equals(dateTime) && user.getSignCount() == 7) { // 判断字符串日期是否相等
                user.setSignCount(0);
            }
        }
        baseResp.setSuccess(1);
        UserInfo info = new UserInfo();
        BeanUtils.copyProperties(user, info);
        //获取卡牌数据
        List<Characters> characterList = charactersMapper.selectByUserId(user.getUserId());
        info.setCharacterList(characterList);
        //卡池数量
        List<Card> cardList = cardMapper.selectAll();
        info.setUseCardCount(cardList.size() + "");
        info.setCharacterList(characterList);
        baseResp.setData(info);
        baseResp.setErrorMsg("更新成功");
        return baseResp;
    }

    @Override
    public BaseResp changeState(TokenDto token, HttpServletRequest request) throws Exception {
        BaseResp baseResp = new BaseResp();
        if (token == null || Xtool.isNull(token.getToken())) {
            baseResp.setSuccess(0);
            baseResp.setErrorMsg("登录过期");
            return baseResp;
        }
        String userId = (String) redisTemplate.opsForValue().get(token.getToken());
        if (Xtool.isNull(userId)) {
            baseResp.setSuccess(0);
            baseResp.setErrorMsg("登录过期");
            return baseResp;
        }
        Characters characters = charactersMapper.listById(userId, token.getId());
        if (characters == null) {
            baseResp.setSuccess(0);
            baseResp.setErrorMsg("卡牌不存在");
            return baseResp;
        }
        if (characters.getGoIntoNum() != 0) {
            characters.setGoIntoNum(0);
            this.charactersMapper.updateByPrimaryKey(characters);
        } else {
            List<Characters> characters1 = this.charactersMapper.goIntoListById(userId);
            if (Xtool.isNotNull(characters1) && characters1.size() >= 5) {
                baseResp.setSuccess(0);
                baseResp.setErrorMsg("阵容已满请下架其他英雄");
                return baseResp;
            } else {
                for (int i = 1; i <= 5; i++) {
                    int a = i;
                    if (Xtool.isNull(characters1.stream().filter(x -> x.getGoIntoNum() == a).collect(Collectors.toList()))) {
                        characters.setGoIntoNum(i);
                        this.charactersMapper.updateByPrimaryKey(characters);
                        break;
                    }
                }

            }
        }
        User user = userMapper.selectUserByUserId(Integer.parseInt(userId));
        baseResp.setSuccess(1);
        UserInfo info = new UserInfo();
        BeanUtils.copyProperties(user, info);
        //获取卡牌数据
        List<Characters> characterList = charactersMapper.selectByUserId(user.getUserId());
        info.setCharacterList(characterList);
        baseResp.setData(info);
        baseResp.setErrorMsg("更新成功");
        return baseResp;
    }

    @Override
    public BaseResp changeName(TokenDto token, HttpServletRequest request) throws Exception {
        BaseResp baseResp = new BaseResp();
        if (token == null || Xtool.isNull(token.getToken())) {
            baseResp.setSuccess(0);
            baseResp.setErrorMsg("登录过期");
            return baseResp;
        }
        String userId = (String) redisTemplate.opsForValue().get(token.getToken());
        if (Xtool.isNull(userId)) {
            baseResp.setSuccess(0);
            baseResp.setErrorMsg("登录过期");
            return baseResp;
        }
        if (Xtool.isNull(token.getStr())) {
            baseResp.setSuccess(0);
            baseResp.setErrorMsg("昵称不能为空");
            return baseResp;
        }
        if (token.getStr().length() > 10) {
            baseResp.setSuccess(0);
            baseResp.setErrorMsg("昵称长度不得超过10个字");
            return baseResp;
        }
        SensitiveWord sw = new SensitiveWord("CensorWords.txt");
        sw.InitializationWork();
        String nickName = sw.filterInfo(token.getStr());
        if (!token.getStr().equals(nickName)) {
            baseResp.setSuccess(0);
            baseResp.setErrorMsg("昵称出现敏感词汇");
            return baseResp;
        }

        if (userMapper.selectUserByNickName(nickName, Integer.parseInt(userId)) > 0) {
            baseResp.setSuccess(0);
            baseResp.setErrorMsg("该昵称已存在");
            return baseResp;
        }
        User user = userMapper.selectUserByUserId(Integer.parseInt(userId));
        BigDecimal diamond = user.getDiamond().subtract(new BigDecimal(500));
        if (diamond.compareTo(BigDecimal.ZERO) < 0) {
            baseResp.setSuccess(0);
            baseResp.setErrorMsg("钻石不足");
            return baseResp;
        }
        user.setDiamond(diamond);
        user.setNickname(nickName);
        userMapper.updateuser(user);
        baseResp.setSuccess(1);
        baseResp.setErrorMsg("修改成功");
        baseResp.setData(user);
        return baseResp;
    }

    @Override
    public BaseResp getActivityList(TokenDto token, HttpServletRequest request) throws Exception {
        List<ActivityConfig> activityConfigList = configMapper.selectAll();
        BaseResp baseResp = new BaseResp();
        baseResp.setSuccess(1);
        baseResp.setData(activityConfigList);
        baseResp.setErrorMsg("更新成功");
        return baseResp;
    }

    @Override
    public BaseResp getTodayRecords(TokenDto token, HttpServletRequest request) throws Exception {
        BaseResp baseResp = new BaseResp();
        if (token == null || Xtool.isNull(token.getToken())) {
            baseResp.setSuccess(0);
            baseResp.setErrorMsg("登录过期");
            return baseResp;
        }
        String userId = (String) redisTemplate.opsForValue().get(token.getToken());
        if (Xtool.isNull(userId)) {
            baseResp.setSuccess(0);
            baseResp.setErrorMsg("登录过期");
            return baseResp;
        }
        if (userId == null || token.getStr() == null) {
            baseResp.setSuccess(0);
            baseResp.setErrorMsg("登录过期");
            return baseResp;
        }
        List<UserActivityRecords> records = recordMapper.listTodayRecords(userId, token.getStr());
        baseResp.setSuccess(1);
        baseResp.setData(records);
        baseResp.setErrorMsg("更新成功");
        return baseResp;
    }

    @Override
    public BaseResp getUserActivityDetail(TokenDto token, HttpServletRequest request) throws Exception {
        BaseResp baseResp = new BaseResp();
        if (token == null || Xtool.isNull(token.getToken())) {
            baseResp.setSuccess(0);
            baseResp.setErrorMsg("登录过期");
            return baseResp;
        }
        String userId = (String) redisTemplate.opsForValue().get(token.getToken());
        if (Xtool.isNull(userId)) {
            baseResp.setSuccess(0);
            baseResp.setErrorMsg("登录过期");
            return baseResp;
        }
        if (Xtool.isNull(token.getStr())) {
            baseResp.setSuccess(0);
            baseResp.setErrorMsg("登录过期");
            return baseResp;
        }

        ActivityConfig config = configMapper.getByCode(token.getStr());
        // 获取当前日期
        LocalDate today = LocalDate.now();
        // 获取星期几（返回DayOfWeek枚举）
        DayOfWeek dayOfWeek = today.getDayOfWeek();
        List<ActivityDetail> details = new ArrayList<>();
        if (1 == config.getIsPermanent()) {
            details = activityDetailMapper.getByCodde2(token.getStr(), dayOfWeek.getValue());

        } else {
            details = activityDetailMapper.getByCodde(token.getStr());
        }
        for (ActivityDetail detail : details) {
            List<ActivityReward> rewardList = rewardMapper.getByCodde(detail.getDetailCode());
            List<UserActivityRecords> records = recordMapper.listTodayRecords(userId, detail.getDetailCode());
            detail.setRewardList(rewardList);
            detail.setRecords(records);
        }
        config.setDetails(details);
        if (config == null) {
            baseResp.setSuccess(0);
            baseResp.setErrorMsg("活动已结束");
            return baseResp;
        }
        baseResp.setSuccess(1);
        baseResp.setData(config);
        baseResp.setErrorMsg("更新成功");
        return baseResp;
    }

    @Override
    public BaseResp pveDetail(TokenDto token, HttpServletRequest request) throws Exception {
        BaseResp baseResp = new BaseResp();
        if (token == null || Xtool.isNull(token.getToken())) {
            baseResp.setSuccess(0);
            baseResp.setErrorMsg("登录过期");
            return baseResp;
        }
        String userId = (String) redisTemplate.opsForValue().get(token.getToken());
        if (Xtool.isNull(userId)) {
            baseResp.setSuccess(0);
            baseResp.setErrorMsg("登录过期");
            return baseResp;
        }
        if (Xtool.isNull(token.getId())) {
            baseResp.setSuccess(0);
            baseResp.setErrorMsg("请选择关卡");
            return baseResp;
        }
        PveDetail pveDetail = pveDetailMapper.selectById(token.getId());
        if (pveDetail == null) {
            baseResp.setSuccess(0);
            baseResp.setErrorMsg("关卡不存在");
            return baseResp;
        }
        baseResp.setSuccess(1);
//        UserInfo info = new UserInfo();
//        BeanUtils.copyProperties(user, info);
//        //获取卡牌数据
//        List<Characters> characterList = charactersMapper.selectByUserId(user.getUserId());
//        info.setCharacterList(characterList);
        baseResp.setData(pveDetail);
        baseResp.setErrorMsg("更新成功");
        return baseResp;
    }

    @Override
    @Transactional
    public BaseResp participate(TokenDto token, HttpServletRequest request) throws Exception {
        Integer levelUp = 0;
        Map map = new HashMap();
        BaseResp baseResp = new BaseResp();
        if (token == null || Xtool.isNull(token.getToken())) {
            baseResp.setSuccess(0);
            baseResp.setErrorMsg("登录过期");
            return baseResp;
        }
        String userId = (String) redisTemplate.opsForValue().get(token.getToken());
        if (Xtool.isNull(userId)) {
            baseResp.setSuccess(0);
            baseResp.setErrorMsg("登录过期");
            return baseResp;
        }

        User user = userMapper.selectUserByUserId(Integer.parseInt(userId));
        // 1. 基础参数非空校验
        if (Xtool.isNull(token.getStr())) {
            baseResp.setSuccess(0);
            baseResp.setErrorMsg("登录过期");
            return baseResp;
        }
        ActivityDetail activityDetail = activityDetailMapper.getByCodde3(token.getStr());
        String activityCode = activityDetail.getActivityCode();
        // 2. 校验活动配置
        ActivityConfig config = configMapper.getByCode(activityCode);
        if (config == null) {
            baseResp.setSuccess(0);
            baseResp.setErrorMsg("活动不存在");
            return baseResp;
        }
        // 校验活动状态（常驻活动忽略时间，非常驻校验时间范围）
        Date today = new Date();
        if (config.getStatus() != 1) {
            baseResp.setSuccess(0);
            baseResp.setErrorMsg("活动已结束");
            return baseResp;
        }
        if (config.getIsPermanent() == 0) {
            if (today.before(config.getStartDate()) || today.after(config.getEndDate())) {
                baseResp.setSuccess(0);
                baseResp.setErrorMsg("活动已结束");
                return baseResp;
            }
        }


        // 6. 校验当日参与次数
        int todayCount = recordMapper.countTodayValidRecords(userId, activityCode);
        if (todayCount >= activityDetail.getDailyMaxTimes()) {
            baseResp.setSuccess(0);
            baseResp.setErrorMsg("今日参与次数已达上限（" + config.getDailyMaxTimes() + "次）");
            return baseResp;
        }
        if (user.getLv().compareTo(new BigDecimal(100)) < 0) {
            BigDecimal exp = user.getExp().add(new BigDecimal(50));
            if (exp.compareTo(new BigDecimal(1000)) >= 0) {
                user.setLv(user.getLv().add(new BigDecimal(1)));
                user.setExp(exp.subtract(new BigDecimal(1000)));
                levelUp = user.getLv().intValue();
            } else {
                user.setExp(exp);
            }
        }
        //自己的战队
        List<Characters> leftCharacter = charactersMapper.goIntoListById(user.getUserId() + "");
        if (Xtool.isNull(leftCharacter)) {
            baseResp.setSuccess(0);
            baseResp.setErrorMsg("你没有配置战队无法战斗");
            return baseResp;
        }

        List<Characters> rightCharacter = new ArrayList<>();
        Card card = cardMapper.selectByid(activityDetail.getBossId());
        Characters character = new Characters();
        BeanUtils.copyProperties(card, character);
        character.setGoIntoNum(1);
        character.setLv(Integer.parseInt(activityDetail.getDifficultyLevel()));
        character.setUuid(1);
        rightCharacter.add(character);
        String[] luminaryMap = {
                 // 0: 星期日（太阳）
                "太阴星君",  // 1: 星期一（太阴/月亮）
                "荧惑星君",          // 2: 星期二
                "水星真君",          // 3: 星期三
                "木星真君",          // 4: 星期四
                "金星真君",          // 5: 星期五
                "土星真君",
                "太阳星君",// 6: 星期六
        };
        // 获取当前日期
        LocalDate today2 = LocalDate.now();
        // 获取星期几（返回DayOfWeek枚举）
        DayOfWeek dayOfWeek = today2.getDayOfWeek();
        String name=luminaryMap[dayOfWeek.getValue()]+activityDetail.getDifficultyLevel();
        Battle battle = this.battle(leftCharacter, Integer.parseInt(userId), user.getNickname(), rightCharacter, 0,name, user.getGameImg(), "0");
        if (battle.getIsWin()==0){
            // 7. 插入参与记录
            UserActivityRecords record = new UserActivityRecords();
            record.setUserId(userId);
            record.setDetailCode(activityDetail.getDetailCode());
            record.setStarLevel(token.getFinalLevel());
            record.setDifficultyLevel(token.getDifficultyLevel());
            record.setParticipationDate(new Date());
            record.setDifficultyLevel(activityDetail.getDifficultyLevel());
            record.setParticipationTime(new Date());
            record.setStatus(1);
            int rows = recordMapper.insert(record);

            // 8. 查询并发放奖励（模拟发放，实际需关联用户资产表）
            List<ActivityReward> rewardList = rewardMapper.getByCodde(token.getStr());
            for (ActivityReward content : rewardList) {
                if ("1".equals(content.getRewardType() + "")) {
                    //钻石
                    user.setDiamond(user.getDiamond().add(new BigDecimal(content.getRewardAmount())));
                } else if ("2".equals(content.getRewardType() + "")) {
                    user.setGold(user.getGold().add(new BigDecimal(content.getRewardAmount())));
                } else if ("3".equals(content.getRewardType() + "")) {
                    user.setSoul(user.getSoul().add(new BigDecimal(content.getRewardAmount())));
                } else if ("4".equals(content.getRewardType() + "")) {
                    Characters characters1 = charactersMapper.listById(userId, content.getItemId() + "");
                    if (characters1 != null) {
                        characters1.setStackCount(characters1.getStackCount() + content.getRewardAmount());
                        charactersMapper.updateByPrimaryKey(characters1);
                    } else {
                        Characters characters = new Characters();
                        characters.setStackCount(content.getRewardAmount() - 1);
                        characters.setId(content.getItemId() + "");
                        characters.setLv(1);
                        characters.setUserId(Integer.parseInt(userId));
                        characters.setStar(new BigDecimal(1));
                        charactersMapper.insert(characters);
                    }
                }
            }
        }
        user.setTiliCount(user.getTiliCount() - 2);
        user.setTiliCountTime(new Date());
        userMapper.updateuser(user);
        UserInfo userInfo = new UserInfo();
        BeanUtils.copyProperties(user, userInfo);
        userInfo.setLevelUp(levelUp);
        map.put("levelUp", levelUp);
        map.put("user", userInfo);
        map.put("battle", battle);
        baseResp.setData(map);
        baseResp.setSuccess(1);
        return baseResp;
    }

    private Integer getTodayQiyaoxingStar() {
        LocalDate today = LocalDate.now();
        // 获取本周第几天（1=周一，7=周日），对应星级1-7
        int dayOfWeek = today.getDayOfWeek().getValue();
        return dayOfWeek;
    }

    /**
     * 构造函数
     *
     * @param experienceTable 升级经验表（index为当前等级，value为升级到下一级所需经验）
     * @param silverTable     升级银两消耗表（index为当前等级，value为升级到下一级所需银两）
     * @param maxLevel        最高等级限制（不能超过此等级）
    //     */
//    public GameServiceServiceImpl(int[] experienceTable, int[] silverTable, int maxLevel) {
//        // 验证参数合法性
//        if (experienceTable == null || experienceTable.length == 0) {
//            throw new IllegalArgumentException("经验表不能为空");
//        }
//        if (silverTable == null || silverTable.length == 0) {
//            throw new IllegalArgumentException("银两消耗表不能为空");
//        }
//        if (experienceTable.length != silverTable.length) {
//            throw new IllegalArgumentException("经验表和银两消耗表长度必须一致");
//        }
//        if (maxLevel <= 0) {
//            throw new IllegalArgumentException("最高等级必须大于0");
//        }
//        if (experienceTable.length < maxLevel) {
//            throw new IllegalArgumentException("经验表长度不足，无法支持到最高等级" + maxLevel);
//        }
//        this.experienceTable = experienceTable;
//        this.silverTable = silverTable;
//        this.maxLevel = maxLevel;
//    }

    /**
     * 计算主卡使用材料卡后能升级到的等级、剩余经验及消耗的银两
     *
     * @param mainCardLevel 主卡当前等级（不能超过最高等级）
     * @param mainCardExp   主卡当前等级的经验值
     * @param materialCards 材料卡列表
     * @return 包含最终等级、剩余经验和总消耗银两的结果对象
     */
    public LevelUpResult calculateLevelUp(int mainCardLevel, int mainCardExp, List<MaterialCard> materialCards, List<Integer> expTable, List<Integer> silverTable, Integer maxLevel, String id, String str) {

        // 验证主卡参数
        if (mainCardLevel < 1) {
            throw new IllegalArgumentException("主卡等级不能小于1");
        }
        if (mainCardLevel > maxLevel) {
            throw new IllegalArgumentException("主卡等级不能超过最高等级" + maxLevel);
        }
        if (mainCardExp < 0) {
            throw new IllegalArgumentException("经验值不能为负数");
        }

        // 计算材料卡总经验
        int totalMaterialExp = 0;
        if (materialCards != null) {
            for (MaterialCard card : materialCards) {
                if (card.getExperience() < 0) {
                    throw new IllegalArgumentException("材料卡经验值不能为负数");
                }
                totalMaterialExp += card.getExperience();
            }
        }

        // 初始化计算参数
        int currentLevelExp = mainCardExp;  // 当前等级的经验
        int currentLevel = mainCardLevel;   // 当前等级
        int remainingExp = totalMaterialExp; // 剩余可分配经验
        int totalSilverSpent = 0;           // 总消耗银两（整级升级部分）
        double partialSilver = 0;           // 部分升级的银两消耗

        // 循环升级直到经验不足或达到最高等级
        while (remainingExp > 0 && currentLevel < maxLevel) {
            int requiredExp = expTable.get(currentLevel);
            int requiredSilver = silverTable.get(currentLevel);

            if (currentLevelExp + remainingExp < requiredExp) {
                // 经验不足一整级，计算部分升级的银两消耗
                int totalGainedExp = currentLevelExp + remainingExp;
                // 按经验比例计算银两消耗（保留两位小数）
                partialSilver = (double) totalGainedExp / requiredExp * requiredSilver;
                currentLevelExp = totalGainedExp;
                remainingExp = 0;
            } else {
                // 完成整级升级，消耗全额银两
                totalSilverSpent += requiredSilver;
                remainingExp -= (requiredExp - currentLevelExp);
                currentLevel++;
                currentLevelExp = 0; // 升级后经验清零
                partialSilver = 0;   // 重置部分银两（进入下一级）
            }
        }

        // 达到最高等级后，剩余经验全部保留（不消耗银两）
        if (currentLevel >= maxLevel) {
            currentLevelExp += remainingExp;
            remainingExp = 0;
            partialSilver = 0;
        }

        // 总银两消耗 = 整级消耗 + 部分消耗（四舍五入保留整数）
        int totalSilver = totalSilverSpent + (int) Math.round(partialSilver);


        return new LevelUpResult(currentLevel, currentLevelExp, totalSilver, id, str);
    }

    // 材料卡类
    public static class MaterialCard {
        private final int level;
        private final int experience;

        public MaterialCard(int level, int experience) {
            this.level = level;
            this.experience = experience;
        }

        public int getLevel() {
            return level;
        }

        public int getExperience() {
            return experience;
        }
    }


    @Override
    @Transactional
    public BaseResp cardLevelUp(TokenDto token, HttpServletRequest request) throws Exception {
        if (Xtool.isNull(token.getStr())) {
            BaseResp baseResp = new BaseResp();
            baseResp.setSuccess(1);
            baseResp.setErrorMsg("更新成功");
            return baseResp;
        }
        Characters character = charactersMapper.listById(token.getUserId(), token.getId());
        List<QqCardExp> qqCardExpList = qqCardExpMapper.findbyStar(character.getStar().stripTrailingZeros() + "");
        List<Integer> expTable = new ArrayList<>();
        List<Integer> silverTable = new ArrayList<>();
        List<MaterialCard> materials = new ArrayList<>();
        for (QqCardExp qqCardExp : qqCardExpList) {
            expTable.add(qqCardExp.getUpgradeExp());
            silverTable.add(qqCardExp.getGold());
        }
        List<String> ids = Arrays.asList(token.getStr().split(","));
        List<Characters> charactersList = new ArrayList<>();
        for (String id : ids) {
            Characters characterCong = charactersMapper.listById(token.getUserId(), id);
            charactersList.add(characterCong);
            materials.add(new MaterialCard(characterCong.getLv(), characterCong.getExp()));
        }
        int maxLevel = character.getMaxLv(); // 最高等级5级
        // 主卡：当前2级，已有30经验
        int mainLevel = character.getLv();
        int mainExp = character.getExp();

        LevelUpResult result = this.calculateLevelUp(mainLevel, mainExp, materials, expTable, silverTable, maxLevel, token.getId(), token.getStr());
        BaseResp baseResp = new BaseResp();
        baseResp.setSuccess(1);
        baseResp.setData(result);
        baseResp.setErrorMsg("更新成功");
        return baseResp;
    }

    @Override
    public BaseResp stopLevel(TokenDto token, HttpServletRequest request) throws Exception {
        BaseResp baseResp = new BaseResp();
        if (token == null || Xtool.isNull(token.getToken())) {
            baseResp.setSuccess(0);
            baseResp.setErrorMsg("登录过期");
            return baseResp;
        }
        String userId = (String) redisTemplate.opsForValue().get(token.getToken());
        if (Xtool.isNull(userId)) {
            baseResp.setSuccess(0);
            baseResp.setErrorMsg("登录过期");
            return baseResp;
        }

        User user = userMapper.selectUserByUserId(Integer.parseInt(userId));
        user.setStopLevel(token.getStr());
        userMapper.updateuser(user);
        baseResp.setSuccess(1);
        baseResp.setErrorMsg("更新成功");
        return baseResp;
    }

    @Override
    public BaseResp cardLevelUp2(TokenDto token, HttpServletRequest request) throws Exception {
        List<String> ids = Arrays.asList(token.getStr().split(","));
        for (String id : ids) {
            Characters characters = charactersMapper.listById(token.getUserId(), id);
            if (characters.getStackCount() > 0) {
                characters.setStackCount(characters.getStackCount() - 1);
                characters.setExp(5);
            } else {
                characters.setIsDelete("1");
            }
            charactersMapper.updateByPrimaryKey(characters);
        }
        Characters characters = charactersMapper.listById(token.getUserId(), token.getId());
        characters.setExp(token.getRemainingExp());
        characters.setLv(token.getFinalLevel());
        charactersMapper.updateByPrimaryKey(characters);
        User user = userMapper.selectUserByUserId(Integer.parseInt(token.getUserId()));
        user.setGold(user.getGold().subtract(new BigDecimal(token.getTotalSilverSpent())));
        userMapper.updateuser(user);
        List<Characters> characterList = charactersMapper.selectByUserId(user.getUserId());
        UserInfo info = new UserInfo();
        BaseResp baseResp = new BaseResp();
        BeanUtils.copyProperties(user, info);
        info.setCharacterList(characterList);
        baseResp.setSuccess(1);
        baseResp.setData(info);
        baseResp.setErrorMsg("更新成功");
        return baseResp;
    }

    @Override
    public BaseResp changerHeader(TokenDto token, HttpServletRequest request) throws Exception {
        BaseResp baseResp = new BaseResp();
        if (token == null || Xtool.isNull(token.getToken())) {
            baseResp.setSuccess(0);
            baseResp.setErrorMsg("登录过期");
            return baseResp;
        }
        String userId = (String) redisTemplate.opsForValue().get(token.getToken());
        if (Xtool.isNull(userId)) {
            baseResp.setSuccess(0);
            baseResp.setErrorMsg("登录过期");
            return baseResp;
        }
        if (Xtool.isNull(token.getStr())) {
            baseResp.setSuccess(0);
            baseResp.setErrorMsg("头像跟换异常");
            return baseResp;
        }

        User user = userMapper.selectUserByUserId(Integer.parseInt(userId));
        user.setGameImg(token.getStr());
        userMapper.updateuser(user);
        baseResp.setSuccess(1);
        UserInfo info = new UserInfo();
        BeanUtils.copyProperties(user, info);
        //获取卡牌数据
        List<Characters> characterList = charactersMapper.selectByUserId(user.getUserId());
        info.setCharacterList(characterList);
        baseResp.setData(info);
        baseResp.setErrorMsg("更新成功");
        return baseResp;
    }

    @Override
    public BaseResp itemUpdate(TokenDto token, HttpServletRequest request) throws Exception {
        BaseResp baseResp = new BaseResp();
        if (token == null || Xtool.isNull(token.getToken())) {
            baseResp.setSuccess(0);
            baseResp.setErrorMsg("登录过期");
            return baseResp;
        }
        String userId = (String) redisTemplate.opsForValue().get(token.getToken());
        if (Xtool.isNull(userId)) {
            baseResp.setSuccess(0);
            baseResp.setErrorMsg("登录过期");
            return baseResp;
        }

        if (Xtool.isNotNull(token.getStr())) {
            String str = token.getStr();
            List<String> strings = Arrays.asList(str.split(","));
            //先将用户所有卡下架然后再更新
            charactersMapper.updateGoNuM(userId);
            for (int i = 0; i < strings.size(); i++) {
                if (!"@".equals(strings.get(i))) {
                    charactersMapper.updateGoNuM2(i + 1, strings.get(i), userId);
                }
            }
        }
        User user = userMapper.selectUserByUserId(Integer.parseInt(userId));
        baseResp.setSuccess(1);
        UserInfo info = new UserInfo();
        BeanUtils.copyProperties(user, info);
        //获取卡牌数据
        List<Characters> characterList = charactersMapper.selectByUserId(user.getUserId());
        info.setCharacterList(characterList);
        baseResp.setData(info);
        baseResp.setErrorMsg("更新成功");
        return baseResp;
    }

    @Override
    public BaseResp messageList(TokenDto token, HttpServletRequest request) throws Exception {
        if ("1".equals(token.getStr())) {
            return warReport(token, request);
        } else if ("2".equals(token.getStr())) {
            BaseResp baseResp = new BaseResp();
            baseResp.setData(new ArrayList<>());
            return baseResp;
        } else if ("3".equals(token.getStr())) {
            return userGiftService(token, request);
        }
        return null;
    }

    @Override
    public BaseResp receive(TokenDto token, HttpServletRequest request) throws Exception {
        BaseResp baseResp = new BaseResp();
        if (token == null || Xtool.isNull(token.getToken())) {
            baseResp.setSuccess(0);
            baseResp.setErrorMsg("登录过期");
            return baseResp;
        }
        String userId = (String) redisTemplate.opsForValue().get(token.getToken());
        if (Xtool.isNull(userId)) {
            baseResp.setSuccess(0);
            baseResp.setErrorMsg("登录过期");
            return baseResp;
        }
        User user = userMapper.selectUserByUserId(Integer.parseInt(userId));
        int userLevel = Integer.parseInt(user.getLv() + "");
        boolean isNewUser = false;
        if (userLevel == 1) {
            isNewUser = true;
        }
        String giftCode = token.getStr();
//        String platform = request.getPlatform();
//        String ip = request.getIpAddress();

        // 1. 查询礼包基础信息
        GameGift gift = gameGiftMapper.selectByGiftCode(giftCode);
        if (gift == null || gift.getIsActive() != 1) {
            baseResp.setSuccess(0);
            baseResp.setErrorMsg(Constants.GIFT_NOT_EXIST_OR_DISABLED);
            return baseResp;
        }
        Long giftId = gift.getGiftId();

        // 2. 校验有效期
        Date now = new Date();
        if (now.before(gift.getStartTime()) || now.after(gift.getEndTime())) {
            baseResp.setSuccess(0);
            baseResp.setErrorMsg(Constants.GIFT_NOT_IN_VALID_TIME);
            return baseResp;
        }

        // 3. 校验剩余数量（非不限量时）
        if (gift.getRemainingQuantity() != -1 && gift.getRemainingQuantity() <= 0) {
            baseResp.setSuccess(0);
            baseResp.setErrorMsg(Constants.GIFT_OUT_OF_STOCK);
            return baseResp;
        }

        // 4. 校验领取规则（满足任一规则即可）
        List<GameGiftRule> rules = gameGiftRuleMapper.selectByGiftId(giftId);
        if (!checkRuleSatisfied(userLevel, isNewUser, rules)) {
            baseResp.setSuccess(0);
            baseResp.setErrorMsg(Constants.GIFT_RULE_NOT_SATISFIED);
            return baseResp;
        }

        // 5. 校验用户领取次数（是否超过单用户上限）
        int userReceiveCount = gameGiftRecordMapper.countByUserIdAndGiftId(userId, giftId);
        Integer maxGetCount = rules.stream()
                .map(GameGiftRule::getMaxGetCount)
                .min(Comparator.naturalOrder()) // 取最严格的限制
                .orElse(1);
        if (maxGetCount != -1 && userReceiveCount >= maxGetCount) {
            baseResp.setSuccess(0);
            baseResp.setErrorMsg(Constants.GIFT_RECEIVE_COUNT_EXCEEDED);
            return baseResp;
        }

        //判断 如果是兑换礼包查询是否有兑换记录
        if ("4".equals(gift.getGiftType())) {
            GameGiftExchangeCode record = new GameGiftExchangeCode();
            record.setGiftId(giftId);
            record.setUseUserId(Long.parseLong(userId));
            record.setExchangeCode(gift.getGiftCode());
            List<GameGiftExchangeCode> codeList = gameGiftExchangeCodeMapper.selectByUserCode(record);
            if (Xtool.isNull(codeList)) {
                baseResp.setSuccess(0);
                baseResp.setErrorMsg(Constants.GIFT_RECEIVE_COUNT_EXCEEDED);
                return baseResp;
            } else {
                GameGiftExchangeCode code = codeList.get(0);
                code.setIsUsed(1);
                code.setUseTime(new Date());
                gameGiftExchangeCodeMapper.updateByPrimaryKey(code);
            }
        }

        // 8. 记录领取记录
        GameGiftRecord record = new GameGiftRecord();
        record.setUserId(Long.parseLong(userId));
        record.setGiftId(giftId);
        record.setGiftCode(giftCode);
        record.setGetTime(now);
        record.setStatus(1); // 1：成功
        record.setPlatform("");
        record.setIpAddress("");
        gameGiftRecordMapper.insert(record);

        // 9. 发放奖励（调用道具/金币发放接口，此处简化）
        List<GameGiftContent> contents = gameGiftContentMapper.selectByGiftId(giftId);
        for (GameGiftContent content : contents) {
            if ("1".equals(content.getItemType() + "")) {
                //钻石
                user.setDiamond(user.getDiamond().add(new BigDecimal(content.getItemQuantity())));
            } else if ("2".equals(content.getItemType() + "")) {
                user.setGold(user.getGold().add(new BigDecimal(content.getItemQuantity())));
            } else if ("3".equals(content.getItemType() + "")) {
                user.setSoul(user.getSoul().add(new BigDecimal(content.getItemQuantity())));
            } else if ("4".equals(content.getItemType() + "")) {
                Characters characters1 = charactersMapper.listById(userId, content.getItemId() + "");
                if (characters1 != null) {
                    characters1.setStackCount(characters1.getStackCount() + content.getItemQuantity());
                    charactersMapper.updateByPrimaryKey(characters1);
                } else {
                    Characters characters = new Characters();
                    characters.setStackCount(content.getItemQuantity() - 1);
                    characters.setId(content.getItemId() + "");
                    characters.setLv(1);
                    characters.setUserId(Integer.parseInt(userId));
                    characters.setStar(new BigDecimal(1));
                    charactersMapper.insert(characters);
                }
            }
        }
        userMapper.updateuser(user);
        baseResp.setSuccess(1);
        UserInfo info = new UserInfo();
        BeanUtils.copyProperties(user, info);
        //获取卡牌数据
        List<Characters> characterList = charactersMapper.selectByUserId(user.getUserId());
        info.setCharacterList(characterList);
        baseResp.setData(info);
        baseResp.setSuccess(1);
        baseResp.setErrorMsg("领取成功");
        return baseResp;
//        } finally {
//            if (lock.isHeldByCurrentThread()) {
//                lock.unlock();
//            }
//        }
    }

    @Override
    public BaseResp getStore(TokenDto token, HttpServletRequest request) throws Exception {
        BaseResp baseResp = new BaseResp();
        List<GameItemShop> gameItemShopList = gameItemShopMapper.selectAll();
        DynamicItemPicker picker = new DynamicItemPicker();
        for (GameItemShop gameItemShop : gameItemShopList) {
            picker.addItem(gameItemShop);
        }
        // 尝试获取16个物品（种类不足，会重复获取）
        List<GameItemShop> picked = picker.pickRandomItems(16);
        baseResp.setSuccess(1);
        baseResp.setData(picked);
        baseResp.setErrorMsg("成功");
        return baseResp;
    }

    @Override
    public BaseResp buyStore(TokenDto token, HttpServletRequest request) throws Exception {
        BaseResp baseResp = new BaseResp();
        if (token == null || Xtool.isNull(token.getToken())) {
            baseResp.setSuccess(0);
            baseResp.setErrorMsg("登录过期");
            return baseResp;
        }
        String userId = (String) redisTemplate.opsForValue().get(token.getToken());
        if (Xtool.isNull(userId)) {
            baseResp.setSuccess(0);
            baseResp.setErrorMsg("登录过期");
            return baseResp;
        }
        User user = userMapper.selectUserByUserId(Integer.parseInt(userId));
        GameItemShop gameItemShop = gameItemShopMapper.selectByItemId(token.getId());
        if (gameItemShop == null) {
            baseResp.setSuccess(0);
            baseResp.setErrorMsg("商品不存在或已下架");
            return baseResp;
        }
        if (gameItemShop.getGoldEdgePrice() != 0) {
            BigDecimal gold = user.getGold().subtract(new BigDecimal(gameItemShop.getGoldEdgePrice()));
            if (gold.compareTo(BigDecimal.ZERO) < 0) {
                baseResp.setSuccess(0);
                baseResp.setErrorMsg("银两不足");
                return baseResp;
            }
            user.setGold(gold);
        } else {
            BigDecimal diamond = user.getDiamond().subtract(new BigDecimal(gameItemShop.getGemPrice()));
            if (diamond.compareTo(BigDecimal.ZERO) < 0) {
                baseResp.setSuccess(0);
                baseResp.setErrorMsg("钻石不足");
                return baseResp;
            }
            user.setDiamond(diamond);
        }
        Characters characters1 = charactersMapper.listById(userId, token.getId());
        if (characters1 != null) {
            characters1.setStackCount(characters1.getStackCount() + 1);
            charactersMapper.updateByPrimaryKey(characters1);
        } else {
            Characters characters = new Characters();
            characters.setStackCount(0);
            characters.setId(token.getId());
            characters.setLv(1);
            characters.setUserId(Integer.parseInt(userId));
            characters.setStar(new BigDecimal(1));
            charactersMapper.insert(characters);
        }
        userMapper.updateuser(user);
        baseResp.setSuccess(1);
        UserInfo info = new UserInfo();
        BeanUtils.copyProperties(user, info);
        //获取卡牌数据
        List<Characters> characterList = charactersMapper.selectByUserId(user.getUserId());
        info.setCharacterList(characterList);
        baseResp.setData(info);
        baseResp.setSuccess(1);
        baseResp.setErrorMsg("领取成功");
        return baseResp;
    }


    @Override
    public BaseResp giftExchangeCode(TokenDto token, HttpServletRequest request) throws Exception {
        BaseResp baseResp = new BaseResp();
        if (token == null || Xtool.isNull(token.getToken())) {
            baseResp.setSuccess(0);
            baseResp.setErrorMsg("登录过期");
            return baseResp;
        }
        String userId = (String) redisTemplate.opsForValue().get(token.getToken());
        if (Xtool.isNull(userId)) {
            baseResp.setSuccess(0);
            baseResp.setErrorMsg("登录过期");
            return baseResp;
        }
        User user = userMapper.selectUserByUserId(Integer.parseInt(userId));
        int userLevel = Integer.parseInt(user.getLv() + "");
        boolean isNewUser = false;
        if (userLevel == 1) {
            isNewUser = true;
        }
        String giftCode = token.getStr();
//        String platform = request.getPlatform();
//        String ip = request.getIpAddress();

        // 1. 查询礼包基础信息
        GameGift gift = gameGiftMapper.selectByGiftCode(giftCode);
        if (gift == null || gift.getIsActive() != 1) {
            baseResp.setSuccess(0);
            baseResp.setErrorMsg(Constants.GIFT_NOT_EXIST_OR_DISABLED);
            return baseResp;
        }
        Long giftId = gift.getGiftId();

        // 2. 校验有效期
        Date now = new Date();
        if (now.before(gift.getStartTime()) || now.after(gift.getEndTime())) {
            baseResp.setSuccess(0);
            baseResp.setErrorMsg(Constants.GIFT_NOT_IN_VALID_TIME);
            return baseResp;
        }

        // 3. 校验剩余数量（非不限量时）
        if (gift.getRemainingQuantity() != -1 && gift.getRemainingQuantity() <= 0) {
            baseResp.setSuccess(0);
            baseResp.setErrorMsg(Constants.GIFT_OUT_OF_STOCK);
            return baseResp;
        }

        // 4. 校验领取规则（满足任一规则即可）
        List<GameGiftRule> rules = gameGiftRuleMapper.selectByGiftId(giftId);
        if (!checkRuleSatisfied(userLevel, isNewUser, rules)) {
            baseResp.setSuccess(0);
            baseResp.setErrorMsg(Constants.GIFT_RULE_NOT_SATISFIED);
            return baseResp;
        }

        // 5. 校验用户领取次数（是否超过单用户上限）
        int userReceiveCount = gameGiftRecordMapper.countByUserIdAndGiftId(userId, giftId);
        Integer maxGetCount = rules.stream()
                .map(GameGiftRule::getMaxGetCount)
                .min(Comparator.naturalOrder()) // 取最严格的限制
                .orElse(1);
        if (maxGetCount != -1 && userReceiveCount >= maxGetCount) {
            baseResp.setSuccess(0);
            baseResp.setErrorMsg(Constants.GIFT_RECEIVE_COUNT_EXCEEDED);
            return baseResp;
        }
        if (!"4".equals(gift.getGiftType() + "")) {
            baseResp.setSuccess(0);
            baseResp.setErrorMsg(Constants.GIFT_CODE_DUPLICATE);
            return baseResp;
        }
        //判断 如果是兑换礼包查询是否有兑换记录
        GameGiftExchangeCode record = new GameGiftExchangeCode();
        record.setGiftId(giftId);
        record.setUseUserId(Long.parseLong(userId));
        record.setExchangeCode(gift.getGiftCode());
        List<GameGiftExchangeCode> codeList = gameGiftExchangeCodeMapper.selectByUserCode2(record);
        if (Xtool.isNotNull(codeList)) {
            baseResp.setSuccess(0);
            baseResp.setErrorMsg(Constants.GIFT_RECEIVE_COUNT_EXCEEDED);
            return baseResp;
        }
        record.setCreateTime(new Date());
        gameGiftExchangeCodeMapper.insertSelective(record);
        baseResp.setErrorMsg("兑换成功请注意查收");
        baseResp.setSuccess(1);
        return baseResp;
    }

    @Override
    public BaseResp checkHechen(TokenDto token, HttpServletRequest request) throws Exception {
        BaseResp baseResp = new BaseResp();
        List<String> ids = Arrays.asList(token.getStr().split(","));
        List<Characters> charactersList = new ArrayList<>();
        for (String id : ids) {
            Characters characters = charactersMapper.listById(token.getUserId(), id);
            charactersList.add(characters);
        }
        if (charactersList.size() < 5) {
            baseResp.setSuccess(0);
            baseResp.setErrorMsg("卡牌数量不足");
            return baseResp;
        }

        if (Xtool.isNotNull(charactersList.stream().filter(x -> x.getLv() < x.getMaxLv()).collect(Collectors.toList()))) {
            baseResp.setSuccess(0);
            baseResp.setErrorMsg("卡牌未满级");
            return baseResp;
        }

        baseResp.setData(calculateStar(charactersList.stream().map(Characters::getStar).collect(Collectors.toList())));
        baseResp.setSuccess(1);
        return baseResp;
    }

    @Override
    public BaseResp tuPuhenchenList(TokenDto token, HttpServletRequest request) throws Exception {
        BaseResp baseResp = new BaseResp();
        List<StarSynthesisMain> mainList = starSynthesisMainMapper.selectAll();
        for (StarSynthesisMain starSynthesisMain : mainList) {
            List<StarSynthesisMaterials> materials = starSynthesisMaterialsMapper.selectall(starSynthesisMain.getId());
            starSynthesisMain.setMaterials(materials);
        }
        baseResp.setData(mainList);
        baseResp.setSuccess(1);
        return baseResp;
    }

    @Override
    public BaseResp findHechenCard(TokenDto token, HttpServletRequest request) throws Exception {
        BaseResp baseResp = new BaseResp();
        if (token == null || Xtool.isNull(token.getToken())) {
            baseResp.setSuccess(0);
            baseResp.setErrorMsg("登录过期");
            return baseResp;
        }
        String userId = (String) redisTemplate.opsForValue().get(token.getToken());
        if (Xtool.isNull(userId)) {
            baseResp.setSuccess(0);
            baseResp.setErrorMsg("登录过期");
            return baseResp;
        }
        User user = userMapper.selectUserByUserId(Integer.parseInt(userId));
        List<String> ids = Arrays.asList(token.getStr().split(","));
        List<Characters> charactersList = new ArrayList<>();
        for (String id : ids) {
            Characters characters = charactersMapper.listById(token.getUserId(), id);
            charactersList.add(characters);
        }
        if (charactersList.size() < 5) {
            baseResp.setSuccess(0);
            baseResp.setErrorMsg("卡牌数量不足");
            return baseResp;
        }

        if (Xtool.isNotNull(charactersList.stream().filter(x -> x.getLv() < x.getMaxLv()).collect(Collectors.toList()))) {
            baseResp.setSuccess(0);
            baseResp.setErrorMsg("卡牌未满级");
            return baseResp;
        }

        BigDecimal star = calculateStar(charactersList.stream().map(Characters::getStar).collect(Collectors.toList()));
        if (star.compareTo(new BigDecimal(5)) >= 0) {
            baseResp.setSuccess(0);
            baseResp.setErrorMsg("5星卡还未开放敬请期待");
            return baseResp;
        }
        BigDecimal gold = getValue(star);
        if (gold.compareTo(user.getGold()) > 0) {
            baseResp.setSuccess(0);
            baseResp.setErrorMsg("合成银两不足");
            return baseResp;
        }
        for (Characters characters : charactersList) {
            if (characters.getStackCount() > 0) {
                characters.setStackCount(characters.getStackCount() - 1);
                characters.setExp(5);
                characters.setLv(1);
            } else {
                characters.setIsDelete("1");
            }
            charactersMapper.updateByPrimaryKey(characters);
        }
        List<Card> cardList = cardMapper.selectAll();
        cardList = cardList.stream().filter(x -> x.getStar().compareTo(star) == 0).collect(Collectors.toList());
        Random random = new Random();
        int randomIndex = random.nextInt(cardList.size()); // 生成0到集合大小-1的随机索引
        Card drawnCard = cardList.get(randomIndex);
        Characters characters1 = charactersMapper.listById(userId, drawnCard.getId());
        if (characters1 != null) {
            characters1.setStackCount(characters1.getStackCount() + 1);
            charactersMapper.updateByPrimaryKey(characters1);
        } else {
            Characters characters = new Characters();
            characters.setStackCount(0);
            characters.setId(drawnCard.getId());
            characters.setLv(1);
            characters.setUserId(Integer.parseInt(userId));
            characters.setStar(drawnCard.getStar());
            charactersMapper.insert(characters);
        }
        CardDto dto = new CardDto();
        dto.setHero(drawnCard);
        ValueOperations opsForValue = redisTemplate.opsForValue();
        if (drawnCard.getStar().compareTo(new BigDecimal(3)) > 0) {
            Date date = new Date();
            opsForValue.set("notice_" + date.getTime() + "", "恭喜 " + user.getNickname() + " 合成召唤获得" + drawnCard.getStar().stripTrailingZeros() + "星" + drawnCard.getName(), 3600 * 12, TimeUnit.SECONDS);
        }
        List<Characters> nowCharactersList = charactersMapper.selectByUserId(Integer.parseInt(userId));
        dto.setCharacters(nowCharactersList);
        //卡池数量
        UserInfo info = new UserInfo();
        BeanUtils.copyProperties(user, info);
        baseResp.setSuccess(1);
        Map map = new HashMap();
        map.put("user", info);
        map.put("dto", dto);
        baseResp.setData(map);
        baseResp.setErrorMsg("单抽成功");
        return baseResp;
    }

    // 常量定义（避免魔法值，提高可读性）
    private static final BigDecimal MIN_VALUE = new BigDecimal("1");
    private static final BigDecimal MAX_VALUE = new BigDecimal("5");
    private static final BigDecimal STEP = new BigDecimal("0.5");
    private static final BigDecimal BASE_VALUE = new BigDecimal("5000");

    public BigDecimal getValue(BigDecimal num) {

        // 计算倍数：(数值 - 1) / 0.5 + 1
        BigDecimal subtractOne = num.subtract(MIN_VALUE); // 数值 - 1
        BigDecimal divideStep = subtractOne.divide(STEP); // 除以0.5
        BigDecimal multiple = divideStep.add(BigDecimal.ONE); // 加1

        // 计算结果：倍数 * 5000
        return multiple.multiply(BASE_VALUE);
    }

    // 常量定义
    private static final BigDecimal INCREMENT = new BigDecimal("0.5");
    private static final BigDecimal MAX_STAR_LIMIT = new BigDecimal("5");
    private static final BigDecimal MIN_STAR_LIMIT = new BigDecimal("1");
    private static final int REQUIRED_SIZE = 5;

    /**
     * 计算星级结果
     * 规则：
     * - 全相同星级：结果 = 星级 + 0.5（最高不超过5.0）
     * - 星级不同：结果 = 最高星级 - 0.5（最低不低于1.0）
     *
     * @param starList 包含5个元素的星级集合（元素为BigDecimal类型，范围1.0-5.0，步长0.5）
     * @return 计算后的星级结果
     * @throws IllegalArgumentException 当集合为空、大小不是5、包含null元素或星级值无效时抛出
     */
    public static BigDecimal calculateStar(List<BigDecimal> starList) {
        // 验证输入集合有效性
        validateStarList(starList);

        // 获取第一个星级作为参考
        BigDecimal firstStar = starList.get(0);
        boolean allSame = true;
        BigDecimal maxStar = firstStar;

        // 遍历集合，判断是否所有星级相同并寻找最大值
        for (int i = 1; i < starList.size(); i++) {
            BigDecimal currentStar = starList.get(i);

            // 更新最大星级
            if (currentStar.compareTo(maxStar) > 0) {
                maxStar = currentStar;
            }

            // 检查是否与第一个星级不同
            if (currentStar.compareTo(firstStar) != 0) {
                allSame = false;
            }
        }

        // 根据判断结果计算最终星级
        BigDecimal result;
        if (allSame) {
            // 全相同时加0.5，不超过5.0
            result = firstStar.add(INCREMENT);
            if (result.compareTo(MAX_STAR_LIMIT) > 0) {
                result = MAX_STAR_LIMIT;
            }
        } else {
            // 不同时最高星减0.5，不低于1.0
            result = maxStar.subtract(INCREMENT);
            if (result.compareTo(MIN_STAR_LIMIT) < 0) {
                result = MIN_STAR_LIMIT;
            }
        }

        // 确保结果保留一位小数（处理精度问题）
        return result.setScale(1, RoundingMode.HALF_UP);
    }

    /**
     * 验证星级集合的有效性
     */
    private static void validateStarList(List<BigDecimal> starList) {
        if (starList == null) {
            throw new IllegalArgumentException("星级集合不能为null");
        }
        if (starList.size() != REQUIRED_SIZE) {
            throw new IllegalArgumentException("星级集合必须包含" + REQUIRED_SIZE + "个元素");
        }
        // 验证每个星级的有效性
        for (BigDecimal star : starList) {
            validateSingleStar(star);
        }
    }

    /**
     * 验证单个星级的有效性（范围1.0-5.0，步长0.5）
     */
    private static void validateSingleStar(BigDecimal star) {
        if (star == null) {
            throw new IllegalArgumentException("星级不能为null");
        }
        // 检查是否在1.0-5.0范围内
        if (star.compareTo(MIN_STAR_LIMIT) < 0 || star.compareTo(MAX_STAR_LIMIT) > 0) {
            throw new IllegalArgumentException("星级必须在1.0-5.0之间");
        }
        // 检查是否为0.5的整数倍（确保是合法星级值：1.0,1.5,2.0...5.0）
        BigDecimal remainder = star.multiply(new BigDecimal("2")).remainder(BigDecimal.ONE);
        if (remainder.compareTo(BigDecimal.ZERO) != 0) {
            throw new IllegalArgumentException("星级必须为0.5的整数倍（如1.0,1.5,2.0...）");
        }
    }

    public BaseResp userGiftService(TokenDto token, HttpServletRequest request) throws Exception {
//        Long userId = request.getUserId();
//        String platform = request.getPlatform();
//
//        // 1. 查询用户信息（等级、是否新用户）
//        User user = userService.getUserById(userId);
//        if (user == null) {
//            throw new GiftException(ErrorCode.USER_NOT_EXIST);
//        }
//        int userLevel = user.getLevel();
//        boolean isNewUser = user.isNewUser();
        BaseResp baseResp = new BaseResp();
        if (token == null || Xtool.isNull(token.getToken())) {
            baseResp.setSuccess(0);
            baseResp.setErrorMsg("登录过期");
            return baseResp;
        }
        String userId = (String) redisTemplate.opsForValue().get(token.getToken());
        if (Xtool.isNull(userId)) {
            baseResp.setSuccess(0);
            baseResp.setErrorMsg("登录过期");
            return baseResp;
        }
        User user = userMapper.selectUserByUserId(Integer.parseInt(userId));
        int userLevel = Integer.parseInt(user.getLv() + "");
        boolean isNewUser = false;
        if (userLevel == 1) {
            isNewUser = true;
        }
        // 2. 查询所有有效礼包（启用、在有效期内、剩余数量充足）
        LocalDateTime now = LocalDateTime.now();
        List<GameGift> validGifts = gameGiftMapper.selectValidGifts(now);
        if (Xtool.isNull(validGifts)) {
            baseResp.setSuccess(1);
            baseResp.setData(validGifts);
            return baseResp;
        }

        // 3. 筛选符合用户领取规则的礼包
        List<GiftListItemVO> result = new ArrayList<>();
        for (GameGift gift : validGifts) {
            Long giftId = gift.getGiftId();

            // 3.1 校验用户是否已达领取上限
            int userReceiveCount = gameGiftRecordMapper.countByUserIdAndGiftId(userId, giftId);
            List<GameGiftRule> rules = gameGiftRuleMapper.selectByGiftId(giftId);
            Integer maxGetCount = rules.stream()
                    .map(GameGiftRule::getMaxGetCount)
                    .min(Comparator.naturalOrder()) // 取最严格的限制
                    .orElse(1);
            if (maxGetCount != -1 && userReceiveCount >= maxGetCount) {
                continue; // 已达领取上限，跳过
            }

            // 3.2 校验是否满足任一领取规则
            boolean isRuleSatisfied = checkRuleSatisfied(userLevel, isNewUser, rules);
            if (!isRuleSatisfied) {
                continue;
            }

            //判断 如果是兑换礼包查询是否有兑换记录
            if ("4".equals(gift.getGiftType() + "")) {
                GameGiftExchangeCode record = new GameGiftExchangeCode();
                record.setGiftId(giftId);
                record.setUseUserId(Long.parseLong(userId));
                record.setExchangeCode(gift.getGiftCode());
                List<GameGiftExchangeCode> codeList = gameGiftExchangeCodeMapper.selectByUserCode(record);
                if (Xtool.isNull(codeList)) {
                    continue;
                }
            }
            // 3.3 封装礼包信息（含内容）
            GiftListItemVO vo = convertToVO(gift);
            result.add(vo);
        }
        baseResp.setSuccess(1);
        baseResp.setData(result);
        return baseResp;
    }


    /**
     * 校验用户是否满足礼包的任一领取规则
     */
    private boolean checkRuleSatisfied(int userLevel, boolean isNewUser, List<GameGiftRule> rules) {
        if (Xtool.isNull(rules)) {
            return true; // 无规则即满足
        }
        for (GameGiftRule rule : rules) {
            // 校验等级范围
            if (userLevel < rule.getMinLevel()) {
                continue;
            }
            if (rule.getMaxLevel() != -1 && userLevel > rule.getMaxLevel()) {
                continue;
            }

            // 校验新用户限制
            if (rule.getIsNewUser() == 1 && !isNewUser) {
                continue;
            }
            if (rule.getIsNewUser() == 0 && isNewUser) {
                continue;
            }

//            // 校验平台限制
//            if (StringUtils.hasText(rule.getPlatformLimit())) {
//                List<String> allowedPlatforms = Arrays.asList(rule.getPlatformLimit().split(","));
//                if (!allowedPlatforms.contains(platform)) {
//                    continue;
//                }
//            }

            // 满足当前规则
            return true;
        }
        return false;
    }

    /**
     * 转换Gift实体为VO（含礼包内容）
     */
    private GiftListItemVO convertToVO(GameGift gift) {
        GiftListItemVO vo = new GiftListItemVO();
        BeanUtils.copyProperties(gift, vo);

        // 补充礼包内容（查询物品名称）
        List<GameGiftContent> contents = gameGiftContentMapper.selectByGiftId(gift.getGiftId());
        List<GiftContentVO> contentVOs = contents.stream().map(content -> {
            GiftContentVO contentVO = new GiftContentVO();
            contentVO.setItemType(content.getItemType());
            contentVO.setItemQuantity(content.getItemQuantity());
            // 查询物品名称（从物品表获取，此处简化）
//            String itemName = itemService.getItemName(content.getItemType(), content.getItemId());
//            contentVO.setItemName(content.getItemName());
            return contentVO;
        }).collect(Collectors.toList());
        vo.setContents(contentVOs);

        return vo;
    }

    @Override
    @Transactional
    public BaseResp danChou(TokenDto token, HttpServletRequest request) throws Exception {
        BaseResp baseResp = new BaseResp();
        String userId = token.getUserId();
        User user = userMapper.selectUserByUserId(Integer.parseInt(userId));

        BigDecimal number = new BigDecimal("1000");
        if (user.getDiamond().compareTo(number) < 0) {
            baseResp.setSuccess(0);
            baseResp.setErrorMsg("当前钻石小于1000");
            return baseResp;
        } else {
            user.setDiamond(user.getDiamond().subtract(number));
            userMapper.updateuser(user);
        }
        List<Card> cardList = cardMapper.selectAll();
        cardList = cardList.stream().filter(x -> x.getWeight() > 0).collect(Collectors.toList());
        CardPool pool = new CardPool();
        for (Card card : cardList) {
            pool.addCard(card);
        }
        Card drawnCard = pool.draw();
        Characters characters1 = charactersMapper.listById(userId, drawnCard.getId());
        if (characters1 != null) {
            characters1.setStackCount(characters1.getStackCount() + 1);
            charactersMapper.updateByPrimaryKey(characters1);
        } else {
            Characters characters = new Characters();
            characters.setStackCount(0);
            characters.setId(drawnCard.getId());
            characters.setLv(1);
            characters.setUserId(Integer.parseInt(userId));
            characters.setStar(drawnCard.getStar());
            charactersMapper.insert(characters);
        }
        CardDto dto = new CardDto();
        dto.setHero(drawnCard);
        ValueOperations opsForValue = redisTemplate.opsForValue();
        if (drawnCard.getStar().compareTo(new BigDecimal(3)) > 0) {
            Date date = new Date();
            opsForValue.set("notice_" + date.getTime() + "", "恭喜 " + user.getNickname() + " 高级召唤获得" + drawnCard.getStar().stripTrailingZeros() + "星" + drawnCard.getName(), 3600 * 12, TimeUnit.SECONDS);
        }
        List<Characters> nowCharactersList = charactersMapper.selectByUserId(Integer.parseInt(userId));
        dto.setCharacters(nowCharactersList);
        //卡池数量
        UserInfo info = new UserInfo();
        BeanUtils.copyProperties(user, info);
        baseResp.setSuccess(1);
        Map map = new HashMap();
        map.put("user", info);
        map.put("dto", dto);
        baseResp.setData(map);
        baseResp.setErrorMsg("单抽成功");
        return baseResp;
    }


    @Override
    public BaseResp soulChou(TokenDto token, HttpServletRequest request) throws Exception {
        BaseResp baseResp = new BaseResp();
        String userId = token.getUserId();
        User user = userMapper.selectUserByUserId(Integer.parseInt(userId));

        BigDecimal number = new BigDecimal("30");
        if (user.getSoul().compareTo(number) < 0) {
            baseResp.setSuccess(0);
            baseResp.setErrorMsg("当前魂珠小于30");
            return baseResp;
        } else {
            user.setSoul(user.getSoul().subtract(number));
            userMapper.updateuser(user);
        }
        List<Card> cardList = cardMapper.selectAll();
        cardList = cardList.stream().filter(x -> x.getWeight() > 0 && x.getStar().compareTo(new BigDecimal(4)) < 0).collect(Collectors.toList());
        CardPool pool = new CardPool();
        for (Card card : cardList) {
            pool.addCard(card);
        }
        Card drawnCard = pool.draw();
        Characters characters1 = charactersMapper.listById(userId, drawnCard.getId());
        if (characters1 != null) {
            characters1.setStackCount(characters1.getStackCount() + 1);
            charactersMapper.updateByPrimaryKey(characters1);
        } else {
            Characters characters = new Characters();
            characters.setStackCount(0);
            characters.setId(drawnCard.getId());
            characters.setLv(1);
            characters.setUserId(Integer.parseInt(userId));
            characters.setStar(drawnCard.getStar());
            charactersMapper.insert(characters);
        }
        CardDto dto = new CardDto();
        dto.setHero(drawnCard);
        ValueOperations opsForValue = redisTemplate.opsForValue();
        if (drawnCard.getStar().compareTo(new BigDecimal(3)) > 0) {
            Date date = new Date();
            opsForValue.set("notice_" + date.getTime() + "", "恭喜 " + user.getNickname() + " 魂珠召唤获得" + drawnCard.getStar().stripTrailingZeros() + "星" + drawnCard.getName(), 3600 * 12, TimeUnit.SECONDS);
        }
        List<Characters> nowCharactersList = charactersMapper.selectByUserId(Integer.parseInt(userId));
        dto.setCharacters(nowCharactersList);
        //卡池数量
        UserInfo info = new UserInfo();
        BeanUtils.copyProperties(user, info);
        baseResp.setSuccess(1);
        Map map = new HashMap();
        map.put("user", info);
        map.put("dto", dto);
        baseResp.setData(map);
        baseResp.setErrorMsg("单抽成功");
        return baseResp;
    }

    @Override
    @Transactional
    public BaseResp shiChou(TokenDto token, HttpServletRequest request) throws Exception {
        BaseResp baseResp = new BaseResp();
        String userId = token.getUserId();
        User user = userMapper.selectUserByUserId(Integer.parseInt(userId));

        BigDecimal number = new BigDecimal("10000");
        if (user.getDiamond().compareTo(number) < 0) {
            baseResp.setSuccess(0);
            baseResp.setErrorMsg("当前钻石小于10000");
            return baseResp;
        } else {
            user.setDiamond(user.getDiamond().subtract(number));
            userMapper.updateuser(user);
        }
        List<Card> cardList = cardMapper.selectAll();
        cardList = cardList.stream().filter(x -> x.getWeight() > 0).collect(Collectors.toList());
        CardPool pool = new CardPool();
        for (Card card : cardList) {
            pool.addCard(card);
        }
        List<Card> drawnCards = new ArrayList<>();
        for (int i = 0; i < 10; i++) {
            Card drawnCard = pool.draw();
            drawnCards.add(drawnCard);
        }
        ValueOperations opsForValue = redisTemplate.opsForValue();
        for (Card drawnCard : drawnCards) {
            if (drawnCard.getStar().compareTo(new BigDecimal(3)) > 0) {
                Date date = new Date();
                opsForValue.set("notice_" + date.getTime(), "恭喜 " + user.getNickname() + " 高级召唤获得" + drawnCard.getStar().stripTrailingZeros() + "星" + drawnCard.getName(), 3600 * 12, TimeUnit.SECONDS);
            }
            Characters characters1 = charactersMapper.listById(userId, drawnCard.getId());
            if (characters1 != null) {
                characters1.setStackCount(characters1.getStackCount() + 1);
                characters1.setUpdateTime(new Date());
                charactersMapper.updateByPrimaryKey(characters1);
            } else {
                Characters characters = new Characters();
                characters.setStackCount(0);
                characters.setId(drawnCard.getId());
                characters.setLv(1);
                characters.setUserId(Integer.parseInt(userId));
                characters.setStar(drawnCard.getStar());
                characters.setCreateTime(new Date());
                charactersMapper.insert(characters);
            }
        }
        baseResp.setSuccess(1);
        CardDto dto = new CardDto();
        dto.setHeros(drawnCards);
        List<Characters> nowCharactersList = charactersMapper.selectByUserId(Integer.parseInt(userId));
        dto.setCharacters(nowCharactersList);
        baseResp.setSuccess(1);
        Map map = new HashMap();
        //卡池数量
        UserInfo info = new UserInfo();
        BeanUtils.copyProperties(user, info);
        map.put("user", info);
        map.put("dto", dto);
        baseResp.setData(map);
        baseResp.setErrorMsg("10抽成功");
        return baseResp;
    }

    //战斗过程
    @Override
    public BaseResp start(TokenDto token, HttpServletRequest request) throws Exception {
        //先获取当前用户战队
        BaseResp baseResp = new BaseResp();
        if (token == null || Xtool.isNull(token.getToken())) {
            baseResp.setSuccess(0);
            baseResp.setErrorMsg("登录过期");
            return baseResp;
        }
        String userId = (String) redisTemplate.opsForValue().get(token.getToken());
        if (Xtool.isNull(userId)) {
            baseResp.setSuccess(0);
            baseResp.setErrorMsg("登录过期");
            return baseResp;
        }
        User user = userMapper.selectUserByUserId(Integer.parseInt(userId));
        //自己的战队
        List<Characters> leftCharacter = charactersMapper.goIntoListById(user.getUserId() + "");
        if (Xtool.isNull(leftCharacter)) {
            baseResp.setSuccess(0);
            baseResp.setErrorMsg("你没有配置战队无法战斗");
            return baseResp;
        }
        Collections.sort(leftCharacter, Comparator.comparing(Characters::getGoIntoNum));
        //对手战队
        User user1 = userMapper.selectUserByUserId(Integer.parseInt(token.getUserId()));
        List<Characters> rightCharacter = charactersMapper.goIntoListById(token.getUserId() + "");
        if (Xtool.isNull(rightCharacter)) {
            baseResp.setSuccess(0);
            baseResp.setErrorMsg("对方没有配置战队无法战斗");
            return baseResp;
        }

        baseResp.setSuccess(1);
        Battle battle = this.battle(leftCharacter, Integer.parseInt(userId), user.getNickname(), rightCharacter, Integer.parseInt(token.getUserId()), user1.getNickname(), user.getGameImg(), "1");
        if (battle.getIsWin() == 0) {
            user.setWinCount(user.getWinCount() + 1);
        }
        user.setTiliCount(user.getHuoliCount() - 10);
        user.setTiliCountTime(new Date());
        userMapper.updateuser(user);
        baseResp.setData(battle);
        return baseResp;
    }

    @Override
    public BaseResp ranking(TokenDto token, HttpServletRequest request) throws Exception {
        BaseResp baseResp = new BaseResp();
        baseResp.setData(userMapper.getMyRankig(token.getUserId()));
        baseResp.setSuccess(1);
        return baseResp;
    }

    @Override
    public BaseResp ranking100(TokenDto token, HttpServletRequest request) throws Exception {
        BaseResp baseResp = new BaseResp();
        baseResp.setData(userMapper.getMyRankig100());
        baseResp.setSuccess(1);
        return baseResp;
    }

    @Override
    public BaseResp start2(TokenDto token, HttpServletRequest request) throws Exception {
        Integer levelUp = 0;
        Map map = new HashMap();
        //先获取当前用户战队
        BaseResp baseResp = new BaseResp();
        if (token == null || Xtool.isNull(token.getToken())) {
            baseResp.setSuccess(0);
            baseResp.setErrorMsg("登录过期");
            return baseResp;
        }
        String userId = (String) redisTemplate.opsForValue().get(token.getToken());
        if (Xtool.isNull(userId)) {
            baseResp.setSuccess(0);
            baseResp.setErrorMsg("登录过期");
            return baseResp;
        }
        User user = userMapper.selectUserByUserId(Integer.parseInt(userId));
        if (user.getLv().compareTo(new BigDecimal(100)) < 0) {
            BigDecimal exp = user.getExp().add(new BigDecimal(50));
            if (exp.compareTo(new BigDecimal(1000)) >= 0) {
                user.setLv(user.getLv().add(new BigDecimal(1)));
                user.setExp(exp.subtract(new BigDecimal(1000)));
                levelUp = user.getLv().intValue();
            } else {
                user.setExp(exp);
            }
        }
        //自己的战队
        List<Characters> leftCharacter = charactersMapper.goIntoListById(user.getUserId() + "");
        if (Xtool.isNull(leftCharacter)) {
            baseResp.setSuccess(0);
            baseResp.setErrorMsg("你没有配置战队无法战斗");
            return baseResp;
        }
        PveDetail pveDetail = pveDetailMapper.selectById(token.getStr());
        if (pveDetail == null) {
            baseResp.setSuccess(0);
            baseResp.setErrorMsg("关卡已探索完！");
            return baseResp;
        }
        baseResp.setSuccess(1);
        List<String> list = Arrays.asList(token.getStr().split("-"));
        Integer num1 = Integer.parseInt(list.get(0));
        Integer num2 = Integer.parseInt(list.get(1));
        Integer num3 = Integer.parseInt(list.get(2));
        List<Characters> rightCharacter = new ArrayList<>();
        // 创建Random实例
        Random random = new Random();

        // 生成1-5之间的随机整数（包含1和5）
        int randomNumber = random.nextInt(5) + 1;
        Card card = cardMapper.selectByid(randomNumber);
        for (int i = 0; i < 5; i++) {
            Characters characters = new Characters();
            BeanUtils.copyProperties(card, characters);
            int lv = getBossLevel(token.getStr());
            characters.setGoIntoNum(i + 1);
            characters.setLv(lv);
            characters.setUuid(i);
            rightCharacter.add(characters);
        }
        Battle battle = this.battle(leftCharacter, Integer.parseInt(userId), user.getNickname(), rightCharacter, 0, pveDetail.getGuanName(), user.getGameImg(), "0");
        if (battle.getIsWin() == 0) {
            if (num3 + 1 > 10) {
                if (num2 + 1 > 6) {
                    if (num1 + 1 > 5) {

                    } else {
                        num1 = num1 + 1;
                        num2 = 1;
                        num3 = 1;
                    }
                } else {
                    num2 = num2 + 1;
                    num3 = 1;
                }
            } else {
                num3 = num3 + 1;
            }
            battle.setChapter(num1 + "-" + num2 + "-" + num3);
            if (!isCandidateGreater(battle.getChapter(), user.getChapter())) {
                user.setChapter(battle.getChapter());
            }
            //TODO 临时固定奖励随机奖励
            // 定义4个物品
            List<String> items = Arrays.asList("10000", "天兵", "20", "3000");

            // 随机选择1~4个物品
            List<String> rewards = selectRandomItems(items);
            for (String s : rewards) {
                if ("10000".equals(s)) {
                    user.setGold(user.getGold().add(new BigDecimal(10000)));
                } else if ("天兵".equals(s)) {
                    Characters characters1 = charactersMapper.listById(userId, "1003");
                    if (characters1 != null) {
                        characters1.setStackCount(characters1.getStackCount() + 1);
                        charactersMapper.updateByPrimaryKey(characters1);
                    } else {
                        Characters characters = new Characters();
                        characters.setStackCount(0);
                        characters.setId("1003");
                        characters.setLv(1);
                        characters.setUserId(Integer.parseInt(userId));
                        characters.setStar(new BigDecimal(1));
                        charactersMapper.insert(characters);
                    }
                } else if ("20".equals(s)) {
                    user.setSoul(user.getSoul().add(new BigDecimal(20)));
                } else if ("3000".equals(s)) {
                    user.setGold(user.getGold().add(new BigDecimal(3000)));
                }
            }
            map.put("rewards", rewards);
        }
        user.setTiliCount(user.getTiliCount() - 2);
        user.setTiliCountTime(new Date());
        PveDetail pveDetail2 = pveDetailMapper.selectById(battle.getChapter());
        userMapper.updateuser(user);
        UserInfo userInfo = new UserInfo();
        BeanUtils.copyProperties(user, userInfo);
        userInfo.setLevelUp(levelUp);
        map.put("levelUp", levelUp);
        map.put("user", userInfo);
        map.put("battle", battle);
        map.put("pveDetail", pveDetail2);
        baseResp.setData(map);
        baseResp.setSuccess(1);
        return baseResp;
    }

    /**
     * 从列表中随机选择1~n个物品（n为列表长度）
     */
    public static <T> List<T> selectRandomItems(List<T> items) {
        if (items == null || items.isEmpty()) {
            return new ArrayList<>();
        }

        Random random = new Random();
        int total = items.size();

        // 随机生成选择的数量（1到总数量之间）
        int selectCount = random.nextInt(total) + 1; // 生成1~4的随机数

        // 复制原列表并打乱顺序
        List<T> shuffled = new ArrayList<>(items);
        Collections.shuffle(shuffled, random);

        // 取前selectCount个元素作为结果
        return shuffled.subList(0, selectCount);
    }

    private boolean isCandidateGreater(String current, String candidate) {
        String[] currentParts = current.split("-");
        String[] candidateParts = candidate.split("-");

        int maxLength = Math.max(currentParts.length, candidateParts.length);

        for (int i = 0; i < maxLength; i++) {
            int currentNum = (i < currentParts.length) ? Integer.parseInt(currentParts[i]) : 0;
            int candidateNum = (i < candidateParts.length) ? Integer.parseInt(candidateParts[i]) : 0;

            if (candidateNum > currentNum) {
                return true;
            } else if (candidateNum < currentNum) {
                return false;
            }
        }
        // 版本号完全相同
        return false;
    }


    public int getBossLevel(String level) {
        // 校验输入格式
        if (level == null || !level.matches("^\\d+-\\d+-\\d+$")) {
            return -1;
        }

        // 拆分关卡编号
        String[] parts = level.split("-");
        if (parts.length != 3) {
            return -1;
        }

        try {
            // 转换为数字（x, y, z）
            int x = Integer.parseInt(parts[0]);
            int y = Integer.parseInt(parts[1]);
            int z = Integer.parseInt(parts[2]);

            // 校验关卡范围有效性
            if (x < 1 || x > LAYER1_MAX ||
                    y < 1 || y > LAYER2_MAX ||
                    z < 1 || z > LAYER3_MAX) {
                return -1;
            }

            // 计算序号（从0开始）
            int index = (x - 1) * LAYER2_MAX * LAYER3_MAX
                    + (y - 1) * LAYER3_MAX
                    + (z - 1);

            // 计算等级（上限50级）
            return Math.min(index + 1, MAX_LEVEL);

        } catch (NumberFormatException e) {
            // 数字转换失败（非整数）
            return -1;
        }
    }

    @Override
    public BaseResp jingji(TokenDto token, HttpServletRequest request) throws Exception {
        //先获取当前用户战队
        BaseResp baseResp = new BaseResp();
        if (token == null || Xtool.isNull(token.getToken())) {
            baseResp.setSuccess(0);
            baseResp.setErrorMsg("登录过期");
            return baseResp;
        }
        String userId = (String) redisTemplate.opsForValue().get(token.getToken());
        if (Xtool.isNull(userId)) {
            baseResp.setSuccess(0);
            baseResp.setErrorMsg("登录过期");
            return baseResp;
        }
        User user = userMapper.selectUserByUserId(Integer.parseInt(userId));
        //随机获取有队伍的5个人
        List<User> users = userMapper.SelectAllUser();
        String userIds = users.stream().map(User::getUserId).map(String::valueOf).collect(Collectors.joining(","));
        List<Characters> charactersList = charactersMapper.goIntoListByIds(userIds);
        Set<Integer> userIds2 = charactersList.stream().map(Characters::getUserId).collect(Collectors.toSet());
        Set<Integer> userIds3 = getRandomElements(userIds2, 5);
        List<User> parking = users.stream().filter(x -> userIds3.contains(x.getUserId())).collect(Collectors.toList());
        baseResp.setSuccess(1);
        Map map = new HashMap();
        map.put("user", user);
        map.put("parking", parking);
        baseResp.setData(map);
        return baseResp;
    }

    // 计算技能等级的方法
    public static int[] calculateSkillLevels(int characterLevel) {
        // 计算总共有多少个"5级段"（每5级为一个单位）
        int fiveLevelSegments = characterLevel / 5;

        // 每3个5级段为一个完整循环（a、b、c各升1级）
        int fullCycles = fiveLevelSegments / 3;
        // 剩余的5级段（0-2，用于分配额外等级）
        int remainingSegments = fiveLevelSegments % 3;

        // 计算每个技能的等级
        int skillA = fullCycles + (remainingSegments >= 1 ? 1 : 0);
        int skillB = fullCycles + (remainingSegments >= 2 ? 1 : 0);
        int skillC = fullCycles;

        return new int[]{skillA, skillB, skillC};
    }

    @Override
    public BaseResp playBattle(TokenDto token, HttpServletRequest request) throws Exception {
        GameFight gameFight = gameFightMapper.selectByPrimaryKey(token.getId());
        BaseResp baseResp = new BaseResp();
        baseResp.setData(JsonUtils.fromJsonToObjList(gameFight.getFightter()));
        baseResp.setSuccess(1);
        return baseResp;
    }

    public BaseResp warReport(TokenDto token, HttpServletRequest request) throws Exception {
        List<GameFight> list = gameFightMapper.selectAll(token.getUserId());
        list.stream().map(x -> {
            x.setTimeStr(formatTime(x.getCreatetime()));
            return x;
        }).collect(Collectors.toList());
        BaseResp baseResp = new BaseResp();
        baseResp.setData(list);
        baseResp.setSuccess(1);
        return baseResp;
    }

    /**
     * 格式化 Date 类型的时间
     *
     * @param targetDate 目标时间（java.util.Date）
     * @return 格式化后的字符串（如"刚刚"、"1小时内"、"今日"等）
     */
    public String formatTime(Date targetDate) {
        // 1. 将 Date 转换为 LocalDateTime（关键：指定时区，避免默认时区问题）
        // 推荐使用 Asia/Shanghai 时区（北京时间），避免系统时区影响
        LocalDateTime targetTime = LocalDateTime.ofInstant(
                targetDate.toInstant(),
                ZoneId.of("Asia/Shanghai") // 固定时区，确保一致性
        );

        // 2. 后续逻辑与之前一致，复用时间差计算和判断
        LocalDateTime now = LocalDateTime.now(ZoneId.of("Asia/Shanghai"));
        long diffHours = Math.abs(ChronoUnit.HOURS.between(targetTime, now));

        if (diffHours <= 1) {
            return "刚刚";
        } else if (diffHours <= 2) {
            return "1小时内";
        } else if (diffHours <= 3) {
            return "2小时内";
        } else if (diffHours <= 4) {
            return "3小时内";
        }

        LocalDate targetLocalDate = targetTime.toLocalDate();
        LocalDate today = now.toLocalDate();
        LocalDate yesterday = today.minusDays(1);

        if (targetLocalDate.isEqual(today)) {
            return "今日";
        } else if (targetLocalDate.isEqual(yesterday)) {
            return "昨日";
        } else {
            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");
            return targetLocalDate.format(formatter);
        }
    }


    public Battle battle(List<Characters> leftCharacters, Integer userId, String name0, List<Characters> rightCharacters, Integer toUserId, String name1, String img, String type) throws Exception {
        Map map = new HashMap();
        Integer isWin = 0;
        //战斗过程
        List<Map> list = new ArrayList<>();
        // 所有存活的角色
        List<Character> allLiveCharacter = new ArrayList();
        List<Character> leftCharacter = new ArrayList<>();
        List<Character> rightCharacter = new ArrayList<>();
        // 当前回合数
        Integer currentRound = 1;

//        gameFight.setToUserId(593);
        for (Characters characters : leftCharacters) {
            // 设置角色
            Character character = reasonableData(characters, leftCharacters);
            character.setDirection("0");
            Character character333 = new Character();
            BeanUtils.copyProperties(character, character333);
            leftCharacter.add(character333);
            allLiveCharacter.add(character);
        }
        for (Characters characters : rightCharacters) {
            // 设置角色
            Character character = reasonableData(characters, rightCharacters);
            character.setDirection("1");
            Character character333 = new Character();
            BeanUtils.copyProperties(character, character333);
            rightCharacter.add(character333);
            allLiveCharacter.add(character);
        }
        //TODO 调用战斗开始回调
        //TODO 装备buff待开发
        outerLoop:
        while (currentRound <= 99) {
            Map mapProsse = new HashMap();
            List<Fightter> fightterList = new ArrayList<>();
            //TODO  排序：先按position升序，相同position按speed降序
            List<Character> allLiveCharacterNew = allLiveCharacter.stream()
                    .sorted(Comparator
                            .comparingInt(Character::getGoIntoNum)
                            .thenComparing(Comparator.comparingDouble(Character::getSpeed).reversed())
                    )
                    .collect(Collectors.toList());
            //设置战场护法//左0右1
            List<Character> leftCharters = allLiveCharacterNew.stream().filter(x -> "0".equals(x.getDirection()) && allLiveCharacter.contains(x)).map(p -> {
                p.setGoON("0");
                return p;
            }).collect(Collectors.toList());
            List<Character> rightCharters = allLiveCharacterNew.stream().filter(x -> "1".equals(x.getDirection()) && allLiveCharacter.contains(x)).map(p -> {
                p.setGoON("0");
                return p;
            }).collect(Collectors.toList());
            Character leftCharters1 = leftCharters.get(0);
            leftCharters1.setGoON("1");
            Character rightCharters1 = rightCharters.get(0);
            rightCharters1.setGoON("1");
            Character character111 = new Character();
            BeanUtils.copyProperties(leftCharters1, character111);
            mapProsse.put("leftCharter", character111);
            Character character222 = new Character();
            BeanUtils.copyProperties(rightCharters1, character222);
            mapProsse.put("rightCharter", character222);
            //TODO 调用回合任务
//            for (const task of this.allRoundQueue.get(this.currentRound) || []) await task()
            //TODO 回合开始函数
            //TODO 装备buff待开发


            //TODO 状态buff
//            for (Character character : allLiveCharacterNew) {
//                if (!allLiveCharacter.contains(character)) break;
//                if (Xtool.isNotNull(character.getBuff())) {
//                    for (int i = character.getBuff().size() - 1; i >= 0; i--) {
//                        Buff buff = character.getBuff().get(i);
//                        if ("中毒".equals(buff.getName())) {
//                            Fightter fightter = new Fightter();
//                            Integer hurt = buff.getRoundReduceBleed();
//                            fightter.setStr("中毒" + "-" + hurt);
//                            fightter.setDirection(character.getDirection());
//                            fightter.setGoON(character.getGoON());
//                            fightter.setAttack(character.getAttack());
//                            if (character.getHp() - hurt <= 0) {
//                                character.setHp(0);
//                                dead(character, allLiveCharacter);
//                            } else {
//                                character.setHp(character.getHp() - hurt);
//                            }
//                            if (buff.getRoundNum() - 1 <= 0) {
//                                //buff失效则移除
//                                character.getBuff().remove(i);
//                                fightter.setIsbuff(0);
//                            } else {
//                                buff.setRoundNum(buff.getRoundNum() - 1);
//                                fightter.setIsbuff(1);
//                            }
//                            fightter.setHp(character.getHp());
//                            fightter.setSpeed(character.getSpeed());
//                            fightter.setIsAction(0);
//                            fightter.setIsDead("1");
//                            fightterList.add(fightter);
//                        } else if ("眩晕".equals(buff.getName())) {
//                            Fightter fightter = new Fightter();
//                            fightter.setDirection(character.getDirection());
//                            fightter.setGoON(character.getGoON());
//                            fightter.setAttack(character.getAttack());
//                            if (buff.getRoundNum() - 1 <= 0) {
//                                //buff失效则移除
//                                character.getBuff().remove(i);
//                                fightter.setIsbuff(0);
//                                character.setIsAction("1");
//                            } else {
//                                buff.setRoundNum(buff.getRoundNum() - 1);
//                                fightter.setIsbuff(1);
//                                character.setIsAction("0");
//                            }
//                            fightter.setHp(character.getHp());
//                            fightter.setSpeed(character.getSpeed());
//                            fightter.setIsAction(0);
//                            fightterList.add(fightter);
//                        } else {
//
//
//                        }
//                    }
//                }
//
//            }


//            苦痛箭 Lv1
//            场下每回合对同位置敌人造成35点真实伤害
            isWin = castskill("苦痛箭", isWin, map, list, mapProsse, rightCharters1, leftCharters1,
                    allLiveCharacter, allLiveCharacterNew, fightterList);

            //TODO 角色行动1
            if (leftCharters1.getSpeed() >= rightCharters1.getSpeed()) {
                //TODO 回合开始

//            后土聚能 Lv1
//            场上，每回合提高自身生命上限197点、攻击67点，最多叠加99层
                for (Character character : allLiveCharacterNew) {
                    if (!allLiveCharacter.contains(character)) continue;
                    if ("1".equals(character.getGoON())) {
                        //仙塔庇护 Lv1
                        //在场,每回合恢复自身25点生命值
                        if ("仙塔庇护".equals(character.getPassiveIntroduceOne()) || "仙塔庇护".equals(character.getPassiveIntroduceTwo())) {
                            if ("0".equals(character.getDirection())) {
                                //大于0时才能治疗
                                if (leftCharters1.getHp() > 0 && "sacred".equals(leftCharters1.getCamp())) {
                                    Fightter fightter = new Fightter();
                                    fightter.setDirection(leftCharters1.getDirection());
                                    character.setHp(leftCharters1.getHp() + 25 * SkillLevelUtil.getSkill1Level(character.getLv()));
                                    fightter.setGoON(leftCharters1.getGoON());
                                    fightter.setAttack(leftCharters1.getAttack());
                                    fightter.setHp(leftCharters1.getHp());
                                    fightter.setMaxHp(leftCharters1.getMaxHp());
                                    fightter.setSpeed(leftCharters1.getSpeed());
                                    fightter.setIsAction(1);
                                    fightter.setGoIntoNum(leftCharters1.getGoIntoNum());
                                    fightter.setStr("+" + (25 * SkillLevelUtil.getSkill1Level(character.getLv())));
                                    fightter.setBuff("仙塔庇护");
                                    fightter.setIsSkill(1);
                                    //
                                    fightter.setDirectionFace(rightCharters1.getDirection());
                                    fightter.setGoONFace(rightCharters1.getGoON());
                                    fightter.setAttackFace(rightCharters1.getAttack());
                                    fightter.setHpFace(rightCharters1.getHp());
                                    fightter.setMaxHpFace(rightCharters1.getMaxHp());
                                    fightter.setSpeedFace(rightCharters1.getSpeed());
                                    fightter.setIsActionFace(0);
                                    fightter.setGoIntoNumFace(rightCharters1.getGoIntoNum());
                                    fightterList.add(fightter);
                                }
                            } else {
                                //大于0时才能治疗
                                if (rightCharters1.getHp() > 0 && "sacred".equals(rightCharters1.getCamp())) {
                                    Fightter fightter = new Fightter();
                                    fightter.setDirection(rightCharters1.getDirection());
                                    character.setHp(rightCharters1.getHp() + 25 * SkillLevelUtil.getSkill1Level(character.getLv()));
                                    fightter.setGoON(rightCharters1.getGoON());
                                    fightter.setAttack(rightCharters1.getAttack());
                                    fightter.setHp(rightCharters1.getHp());
                                    fightter.setMaxHp(rightCharters1.getMaxHp());
                                    fightter.setSpeed(rightCharters1.getSpeed());
                                    fightter.setIsAction(1);
                                    fightter.setGoIntoNum(rightCharters1.getGoIntoNum());
                                    fightter.setStr("+" + 25 * SkillLevelUtil.getSkill1Level(character.getLv()));
                                    fightter.setBuff("仙塔庇护");
                                    fightter.setIsSkill(1);
                                    //
                                    fightter.setDirectionFace(leftCharters1.getDirection());
                                    fightter.setGoONFace(leftCharters1.getGoON());
                                    fightter.setAttackFace(leftCharters1.getAttack());
                                    fightter.setHpFace(leftCharters1.getHp());
                                    fightter.setMaxHpFace(leftCharters1.getMaxHp());
                                    fightter.setSpeedFace(leftCharters1.getSpeed());
                                    fightter.setIsActionFace(0);
                                    fightter.setGoIntoNumFace(leftCharters1.getGoIntoNum());
                                    fightterList.add(fightter);
                                }
                            }
                        }
                    }
                    // 判断是否结束
                    if (allLiveCharacter.stream().filter(x -> "0".equals(x.getDirection())).count() <= 0) {
                        mapProsse.put("fightterList", fightterList);
                        list.add(mapProsse);
                        map.put("result", false);
                        isWin = 1;
                        break outerLoop; // 直接跳出外层循环
                    } else if (allLiveCharacter.stream().filter(x -> "1".equals(x.getDirection())).count() <= 0) {
                        mapProsse.put("fightterList", fightterList);
                        list.add(mapProsse);
                        map.put("result", true);
                        isWin = 0;
                        break outerLoop; // 直接跳出外层循环
                    }
                }


                //TODO 回合开始2
//            妖狐蔽天 Lv1
//            场下，每回合开始有3%几率使当前敌人眩晕，持续1回合

//                ‌倩女幽魂 Lv1
//            场下，每回合令随机一名敌方中毒，每回合损失7点生命


//            谄媚噬魂 Lv1
//            场下，每回合令随机一名敌方中毒，每回合损失7点生命

//            毒入骨髓 Lv1
//            场下，每回合令随机敌方中毒每回损失16点生命

//            报复神箭 Lv1
//            场下，每回合对场上敌方造成106


                //TODO 攻击前技能
//                定海神针 Lv1
//                普通攻击前对敌人造成当前生命值的6%的伤害


                //左边先打
                Fightter fightter = new Fightter();
                if (rightCharters1.getHp() - leftCharters1.getAttack() <= 0) {
                    rightCharters1.setHp(0);
                    //死亡
                    dead(rightCharters1, allLiveCharacter);
                    fightter.setIsDead("1");
                } else {
                    rightCharters1.setHp(rightCharters1.getHp() - leftCharters1.getAttack());
                }

                fightter.setDirection(leftCharters1.getDirection());
                fightter.setGoON(leftCharters1.getGoON());
                fightter.setAttack(leftCharters1.getAttack());
                fightter.setHp(leftCharters1.getHp());
                fightter.setMaxHp(leftCharters1.getMaxHp());
                fightter.setSpeed(leftCharters1.getSpeed());
                fightter.setGoIntoNum(leftCharters1.getGoIntoNum());
                fightter.setIsAction(1);
                //
                fightter.setDirectionFace(rightCharters1.getDirection());
                fightter.setGoONFace(rightCharters1.getGoON());
                fightter.setAttackFace(rightCharters1.getAttack());
                fightter.setHpFace(rightCharters1.getHp());
                fightter.setMaxHpFace(rightCharters1.getMaxHp());
                fightter.setSpeedFace(rightCharters1.getSpeed());
                fightter.setGoIntoNumFace(rightCharters1.getGoIntoNum());
                fightter.setIsActionFace(0);
                fightterList.add(fightter);

                //TODO 攻击后技能
//                芭蕉扇 Lv1
//                场上，每次普通攻击后对当前敌人造成36点火焰伤害

//                斩杀 Lv1
//                普通攻击有13%几率对目标造成220点火焰伤害，如果目标是武圣则有几率一击必杀，替代普通攻击

                if ("0".equals(fightter.getIsDead())) {

                    //TODO 攻击前技能2

                    Fightter fightter2 = new Fightter();
                    if (leftCharters1.getHp() - rightCharters1.getAttack() <= 0) {
                        leftCharters1.setHp(0);
                        //死亡
                        dead(leftCharters1, allLiveCharacter);
                        fightter2.setIsDead("1");
                    } else {
                        leftCharters1.setHp(leftCharters1.getHp() - rightCharters1.getAttack());
                    }
                    fightter2.setDirection(rightCharters1.getDirection());
                    fightter2.setGoON(rightCharters1.getGoON());
                    fightter2.setAttack(rightCharters1.getAttack());
                    fightter2.setHp(rightCharters1.getHp());
                    fightter2.setMaxHp(rightCharters1.getMaxHp());
                    fightter2.setSpeed(rightCharters1.getSpeed());
                    fightter2.setGoIntoNum(rightCharters1.getGoIntoNum());
                    fightter2.setIsAction(1);
                    //
                    fightter2.setDirectionFace(leftCharters1.getDirection());
                    fightter2.setGoONFace(leftCharters1.getGoON());
                    fightter2.setAttackFace(leftCharters1.getAttack());
                    fightter2.setHpFace(leftCharters1.getHp());
                    fightter2.setMaxHpFace(leftCharters1.getMaxHp());
                    fightter2.setSpeedFace(leftCharters1.getSpeed());
                    fightter2.setGoIntoNumFace(leftCharters1.getGoIntoNum());
                    fightter2.setIsActionFace(0);
                    fightterList.add(fightter2);

                    //TODO 攻击后技能2

                }

            } else {


                //TODO 回合开始
                //仙塔庇护 Lv1
                //在场,每回合恢复自身25点生命值

//            后土聚能 Lv1
//            场上，每回合提高自身生命上限197点、攻击67点，最多叠加99层

                //TODO 回合开始2
//            妖狐蔽天 Lv1
//            场下，每回合开始有3%几率使当前敌人眩晕，持续1回合

//                ‌倩女幽魂 Lv1
//            场下，每回合令随机一名敌方中毒，每回合损失7点生命


//            谄媚噬魂 Lv1
//            场下，每回合令随机一名敌方中毒，每回合损失7点生命

//            毒入骨髓 Lv1
//            场下，每回合令随机敌方中毒每回损失16点生命

//            报复神箭 Lv1
//            场下，每回合对场上敌方造成106


                //TODO 攻击前技能


                Fightter fightter2 = new Fightter();
                if (leftCharters1.getHp() - rightCharters1.getAttack() <= 0) {
                    leftCharters1.setHp(0);
                    //死亡
                    dead(leftCharters1, allLiveCharacter);
                    fightter2.setIsDead("1");
                } else {
                    leftCharters1.setHp(leftCharters1.getHp() - rightCharters1.getAttack());
                }
                fightter2.setDirection(rightCharters1.getDirection());
                fightter2.setGoON(rightCharters1.getGoON());
                fightter2.setAttack(rightCharters1.getAttack());
                fightter2.setHp(rightCharters1.getHp());
                fightter2.setMaxHp(rightCharters1.getMaxHp());
                fightter2.setSpeed(rightCharters1.getSpeed());
                fightter2.setGoIntoNum(rightCharters1.getGoIntoNum());
                fightter2.setIsAction(1);
                //
                fightter2.setDirectionFace(leftCharters1.getDirection());
                fightter2.setGoONFace(leftCharters1.getGoON());
                fightter2.setAttackFace(leftCharters1.getAttack());
                fightter2.setHpFace(leftCharters1.getHp());
                fightter2.setMaxHpFace(leftCharters1.getMaxHp());
                fightter2.setSpeedFace(leftCharters1.getSpeed());
                fightter2.setGoIntoNumFace(leftCharters1.getGoIntoNum());
                fightter2.setIsActionFace(0);
                fightterList.add(fightter2);


                //TODO 攻击后技能


                if ("0".equals(fightter2.getIsDead())) {
                    //TODO 攻击前技能2


                    //左边先打
                    Fightter fightter = new Fightter();
                    if (rightCharters1.getHp() - leftCharters1.getAttack() <= 0) {
                        rightCharters1.setHp(0);
                        //死亡
                        dead(rightCharters1, allLiveCharacter);
                        fightter.setIsDead("1");
                    } else {
                        rightCharters1.setHp(rightCharters1.getHp() - leftCharters1.getAttack());
                    }

                    fightter.setDirection(leftCharters1.getDirection());
                    fightter.setGoON(leftCharters1.getGoON());
                    fightter.setAttack(leftCharters1.getAttack());
                    fightter.setHp(leftCharters1.getHp());
                    fightter.setMaxHp(leftCharters1.getMaxHp());
                    fightter.setSpeed(leftCharters1.getSpeed());
                    fightter.setGoIntoNum(leftCharters1.getGoIntoNum());
                    fightter.setIsAction(1);
                    //
                    fightter.setDirectionFace(rightCharters1.getDirection());
                    fightter.setGoONFace(rightCharters1.getGoON());
                    fightter.setAttackFace(rightCharters1.getAttack());
                    fightter.setHpFace(rightCharters1.getHp());
                    fightter.setMaxHpFace(rightCharters1.getMaxHp());
                    fightter.setSpeedFace(rightCharters1.getSpeed());
                    fightter.setGoIntoNumFace(rightCharters1.getGoIntoNum());
                    fightter.setIsActionFace(0);
                    fightterList.add(fightter);


                    //TODO 攻击后技能2

                }


            }

            //TODO 角色行动2
            isWin = castskill("续命", isWin, map, list, mapProsse, rightCharters1, leftCharters1,
                    allLiveCharacter, allLiveCharacterNew, fightterList);

            //TODO 回合结束


            //TODO 回合结束2


            //TODO 调用回合结束回调
//            for (const character of allLiveCharacter) {
//                if (this.allLiveCharacter.indexOf(character) === -1) break
//                for (const buff of character.state.buff)
//                await buff.OnRoundEnd(buff, roundState, this)
//                for (const equipment of character.state.equipment)
//                await equipment.OnRoundEnd(equipment, roundState, this)
//                await character.state.OnRoundEnd(character.state, roundState, this)
//            }
            currentRound++;
            mapProsse.put("fightterList", fightterList);
            list.add(mapProsse);
            // 判断是否结束
            // 判断是否结束
            if (allLiveCharacter.stream().filter(x -> "0".equals(x.getDirection())).count() <= 0) {
                map.put("result", false);
                isWin = 1;
                break outerLoop; // 直接跳出外层循环
            } else if (allLiveCharacter.stream().filter(x -> "1".equals(x.getDirection())).count() <= 0) {
                map.put("result", true);
                isWin = 0;
                break outerLoop; // 直接跳出外层循环
            }

        }

        map.put("fightProcess", list);
        map.put("leftCharacter", leftCharacter);
        map.put("rightCharacter", rightCharacter);
        map.put("name0", name0);
        map.put("name1", name1);
        GameFight fight = new GameFight();
        String simpleUUID = IdUtil.simpleUUID();
        fight.setId(simpleUUID);
        fight.setToUserId(toUserId);
        fight.setUserId(userId);
        fight.setToUserName(name1);
        fight.setUserName(name0);
        fight.setIsWin(isWin);
        fight.setType(type);
        fight.setImg(img);
        //将map转json存储
        String json = JsonUtils.toJson(map);
        fight.setFightter(json);
        gameFightMapper.insert(fight);
        Battle battle = new Battle();
        battle.setIsWin(isWin);
        battle.setId(fight.getId());
        return battle;
    }

    public Integer castskill(String skillStr, Integer isWin, Map map, List<Map> list, Map mapProsse, Character rightCharters1, Character leftCharters1,
                             List<Character> allLiveCharacter, List<Character> allLiveCharacterNew, List<Fightter> fightterList) {
        List<String> skills = Arrays.asList(skillStr.split(","));
        outerLoop:
        for (Character character : allLiveCharacterNew) {
            //英雄1技能释放
            if (!allLiveCharacter.contains(character)) continue;
            if ("0".equals(character.getGoON())) {
                if (skills.contains("苦痛箭")) {
                    //英雄1技能释放
                    //TODO 场下每回合对同位置敌人造成35点真实伤害
                    if ("苦痛箭".equals(character.getPassiveIntroduceOne()) && SkillLevelUtil.getSkill1Level(character.getLv()) > 0) {
                        if ("0".equals(character.getDirection())) {
                            //TODO 筛出对位
                            List<Character> characters = allLiveCharacterNew.stream().filter(x -> "1".equals(x.getDirection() + "") && (character.getGoIntoNum() + "").equals(x.getGoIntoNum() + "")).collect(Collectors.toList());
                            if (Xtool.isNotNull(characters)) {
                                Character character1 = characters.get(0);
                                if (character1.getHp() > 0) {
                                    Fightter fightter = new Fightter();
                                    if (character1.getHp() - 35 * SkillLevelUtil.getSkill1Level(character.getLv()) <= 0) {
                                        character1.setHp(0);
                                        //死亡
                                        dead(character1, allLiveCharacter);
                                        fightter.setIsDead("1");
                                    } else {
                                        character1.setHp(character1.getHp() - 35 * SkillLevelUtil.getSkill1Level(character.getLv()));
                                    }
                                    fightter.setDirection(character.getDirection());
                                    fightter.setGoON(character.getGoON());
                                    fightter.setAttack(character.getAttack());
                                    fightter.setHp(character.getHp());
                                    fightter.setMaxHp(character.getMaxHp());
                                    fightter.setSpeed(character.getSpeed());
                                    fightter.setIsAction(1);
                                    fightter.setGoIntoNum(character.getGoIntoNum());
                                    fightter.setBuff("苦痛箭");
                                    //
                                    fightter.setDirectionFace(character1.getDirection());
                                    fightter.setGoONFace(character1.getGoON());
                                    fightter.setAttackFace(character1.getAttack());
                                    fightter.setHpFace(character1.getHp());
                                    fightter.setMaxHpFace(character1.getMaxHp());
                                    fightter.setSpeedFace(character1.getSpeed());
                                    fightter.setIsActionFace(0);
                                    fightter.setGoIntoNumFace(character1.getGoIntoNum());
                                    fightter.setStr("-" + 35 * SkillLevelUtil.getSkill1Level(character.getLv()));
                                    fightterList.add(fightter);
                                }
                            }

                        } else {
                            //TODO 筛出对位
                            List<Character> characters = allLiveCharacterNew.stream().filter(x -> "0".equals(x.getDirection()) && (character.getGoIntoNum() + "").equals(x.getGoIntoNum() + "")).collect(Collectors.toList());
                            if (Xtool.isNotNull(characters)) {
                                Character character1 = characters.get(0);
                                if (character1.getHp() > 0) {
                                    Fightter fightter = new Fightter();
                                    if (character1.getHp() - 35 * SkillLevelUtil.getSkill1Level(character.getLv()) <= 0) {
                                        character1.setHp(0);
                                        //死亡
                                        dead(character1, allLiveCharacter);
                                        fightter.setIsDead("1");
                                    } else {
                                        character1.setHp(character1.getHp() - 35 * SkillLevelUtil.getSkill1Level(character.getLv()));
                                    }
                                    fightter.setDirection(character.getDirection());
                                    fightter.setGoON(character.getGoON());
                                    fightter.setAttack(character.getAttack());
                                    fightter.setHp(character.getHp());
                                    fightter.setMaxHp(character.getMaxHp());
                                    fightter.setSpeed(character.getSpeed());
                                    fightter.setGoIntoNum(character.getGoIntoNum());
                                    fightter.setIsAction(1);
                                    fightter.setBuff("苦痛箭");
                                    //
                                    fightter.setDirectionFace(character1.getDirection());
                                    fightter.setGoONFace(character1.getGoON());
                                    fightter.setAttackFace(character1.getAttack());
                                    fightter.setHpFace(character1.getHp());
                                    fightter.setMaxHpFace(character1.getMaxHp());
                                    fightter.setSpeedFace(character1.getSpeed());
                                    fightter.setGoIntoNumFace(character1.getGoIntoNum());
                                    fightter.setIsActionFace(0);
                                    fightter.setStr("-" + 35 * SkillLevelUtil.getSkill1Level(character.getLv()));
                                    fightterList.add(fightter);
                                }
                            }
                        }
                    }
                    //英雄1技能释放
                    //TODO 场下每回合对同位置敌人造成35点真实伤害
                    if ("苦痛箭".equals(character.getPassiveIntroduceTwo()) && SkillLevelUtil.getSkill2Level(character.getLv()) > 0) {
                        if ("0".equals(character.getDirection())) {
                            //TODO 筛出对位
                            List<Character> characters = allLiveCharacterNew.stream().filter(x -> "1".equals(character.getDirection()) && (character.getGoIntoNum() + "").equals(x.getGoIntoNum() + "")).collect(Collectors.toList());
                            if (Xtool.isNotNull(characters)) {
                                Character character1 = characters.get(0);
                                if (character1.getHp() > 0) {
                                    Fightter fightter = new Fightter();
                                    if (character1.getHp() - 35 * SkillLevelUtil.getSkill2Level(character.getLv()) <= 0) {
                                        character1.setHp(0);
                                        //死亡
                                        dead(character1, allLiveCharacter);
                                        fightter.setIsDead("1");
                                    } else {
                                        character1.setHp(character1.getHp() - 35 * SkillLevelUtil.getSkill2Level(character.getLv()));
                                    }
                                    fightter.setDirection(character.getDirection());
                                    fightter.setGoON(character.getGoON());
                                    fightter.setAttack(character.getAttack());
                                    fightter.setHp(character.getHp());
                                    fightter.setMaxHp(character.getMaxHp());
                                    fightter.setSpeed(character.getSpeed());
                                    fightter.setIsAction(1);
                                    fightter.setGoIntoNum(character.getGoIntoNum());
                                    fightter.setBuff("苦痛箭");
                                    //
                                    fightter.setDirectionFace(character1.getDirection());
                                    fightter.setGoONFace(character1.getGoON());
                                    fightter.setAttackFace(character1.getAttack());
                                    fightter.setHpFace(character1.getHp());
                                    fightter.setMaxHpFace(character1.getMaxHp());
                                    fightter.setSpeedFace(character1.getSpeed());
                                    fightter.setIsActionFace(0);
                                    fightter.setGoIntoNumFace(character1.getGoIntoNum());
                                    fightter.setStr("-" + 35 * SkillLevelUtil.getSkill2Level(character.getLv()));
                                    fightterList.add(fightter);
                                }
                            }

                        } else {
                            //TODO 筛出对位
                            List<Character> characters = allLiveCharacterNew.stream().filter(x -> "0".equals(character.getDirection()) && (character.getGoIntoNum() + "").equals(x.getGoIntoNum() + "")).collect(Collectors.toList());
                            if (Xtool.isNotNull(characters)) {
                                Character character1 = characters.get(0);
                                if (character1.getHp() > 0) {
                                    Fightter fightter = new Fightter();
                                    if (character1.getHp() - 35 * SkillLevelUtil.getSkill2Level(character.getLv()) <= 0) {
                                        character1.setHp(0);
                                        //死亡
                                        dead(character1, allLiveCharacter);
                                        fightter.setIsDead("1");
                                    } else {
                                        character1.setHp(character1.getHp() - 35 * SkillLevelUtil.getSkill2Level(character.getLv()));
                                    }
                                    fightter.setDirection(character.getDirection());
                                    fightter.setGoON(character.getGoON());
                                    fightter.setAttack(character.getAttack());
                                    fightter.setHp(character.getHp());
                                    fightter.setMaxHp(character.getMaxHp());
                                    fightter.setSpeed(character.getSpeed());
                                    fightter.setGoIntoNum(character.getGoIntoNum());
                                    fightter.setIsAction(1);
                                    fightter.setBuff("苦痛箭");
                                    //
                                    fightter.setDirectionFace(character1.getDirection());
                                    fightter.setGoONFace(character1.getGoON());
                                    fightter.setAttackFace(character1.getAttack());
                                    fightter.setHpFace(character1.getHp());
                                    fightter.setMaxHpFace(character1.getMaxHp());
                                    fightter.setSpeedFace(character1.getSpeed());
                                    fightter.setGoIntoNumFace(character1.getGoIntoNum());
                                    fightter.setIsActionFace(0);
                                    fightter.setStr("-" + 35 * SkillLevelUtil.getSkill2Level(character.getLv()));
                                    fightterList.add(fightter);
                                }
                            }
                        }
                    }
                }
                if (skills.contains("续命")) {
                    if ("续命".equals(character.getPassiveIntroduceOne()) && SkillLevelUtil.getSkill1Level(character.getLv()) > 0) {
                        if ("0".equals(character.getDirection())) {
                            //大于0时才能治疗
                            if (leftCharters1.getHp() > 0 && "sacred".equals(leftCharters1.getCamp())) {
                                Fightter fightter = new Fightter();
                                if (character.getHp() - 40 * SkillLevelUtil.getSkill1Level(character.getLv()) <= 0) {
                                    character.setHp(0);
                                    //死亡
                                    dead(character, allLiveCharacter);
                                    fightter.setIsDead("1");
                                } else {
                                    character.setHp(character.getHp() - 40 * SkillLevelUtil.getSkill1Level(character.getLv()));
                                }
                                if (leftCharters1.getHp() + 40 * SkillLevelUtil.getSkill1Level(character.getLv()) > leftCharters1.getMaxHp()) {
                                    leftCharters1.setHp(leftCharters1.getMaxHp());
                                } else {
                                    leftCharters1.setHp(leftCharters1.getHp() + 40 * SkillLevelUtil.getSkill1Level(character.getLv()));
                                }
                                fightter.setDirection(character.getDirection());
                                fightter.setGoON(character.getGoON());
                                fightter.setAttack(character.getAttack());
                                fightter.setHp(character.getHp());
                                fightter.setMaxHp(character.getMaxHp());
                                fightter.setSpeed(character.getSpeed());
                                fightter.setIsAction(1);
                                fightter.setGoIntoNum(character.getGoIntoNum());
                                fightter.setStr("-" + 40 * SkillLevelUtil.getSkill1Level(character.getLv()));
                                fightter.setBuff("续命");
                                //
                                fightter.setDirectionFace(leftCharters1.getDirection());
                                fightter.setGoONFace(leftCharters1.getGoON());
                                fightter.setAttackFace(leftCharters1.getAttack());
                                fightter.setHpFace(leftCharters1.getHp());
                                fightter.setMaxHpFace(leftCharters1.getMaxHp());
                                fightter.setSpeedFace(leftCharters1.getSpeed());
                                fightter.setIsActionFace(0);
                                fightter.setGoIntoNumFace(leftCharters1.getGoIntoNum());
                                fightter.setStr("" + 40 * SkillLevelUtil.getSkill1Level(character.getLv()));
                                fightter.setBuff("续命");
                                fightterList.add(fightter);
                            }
                        } else {
                            //大于0时才能治疗
                            if (rightCharters1.getHp() > 0 && "sacred".equals(rightCharters1.getCamp())) {
                                Fightter fightter = new Fightter();
                                if (character.getHp() - 40 * SkillLevelUtil.getSkill1Level(character.getLv()) <= 0) {
                                    character.setHp(0);
                                    //死亡
                                    dead(character, allLiveCharacter);
                                    fightter.setIsDead("1");
                                } else {
                                    character.setHp(character.getHp() - 40 * SkillLevelUtil.getSkill1Level(character.getLv()));
                                }
                                if (rightCharters1.getHp() + 40 * SkillLevelUtil.getSkill1Level(character.getLv()) > rightCharters1.getMaxHp()) {
                                    rightCharters1.setHp(rightCharters1.getMaxHp());
                                } else {
                                    rightCharters1.setHp(rightCharters1.getHp() + 40 * SkillLevelUtil.getSkill1Level(character.getLv()));
                                }
                                fightter.setDirection(character.getDirection());
                                fightter.setGoON(character.getGoON());
                                fightter.setAttack(character.getAttack());
                                fightter.setHp(character.getHp());
                                fightter.setMaxHp(character.getMaxHp());
                                fightter.setSpeed(character.getSpeed());
                                fightter.setGoIntoNum(character.getGoIntoNum());
                                fightter.setIsAction(1);
                                fightter.setStr("-" + 40 * SkillLevelUtil.getSkill1Level(character.getLv()));
                                fightter.setBuff("续命");
                                //
                                fightter.setDirectionFace(rightCharters1.getDirection());
                                fightter.setGoONFace(rightCharters1.getGoON());
                                fightter.setAttackFace(rightCharters1.getAttack());
                                fightter.setHpFace(rightCharters1.getHp());
                                fightter.setMaxHpFace(rightCharters1.getMaxHp());
                                fightter.setSpeedFace(rightCharters1.getSpeed());
                                fightter.setGoIntoNumFace(rightCharters1.getGoIntoNum());
                                fightter.setIsActionFace(0);
                                fightter.setStr("" + 40 * SkillLevelUtil.getSkill1Level(character.getLv()));
                                fightter.setBuff("续命");
                                fightterList.add(fightter);
                            }
                        }
                    }
                    //英雄2技能释放
                    if ("续命".equals(character.getPassiveIntroduceTwo()) && SkillLevelUtil.getSkill2Level(character.getLv()) > 0) {
                        if ("0".equals(character.getDirection())) {
                            //大于0时才能治疗
                            if (leftCharters1.getHp() > 0 && "sacred".equals(leftCharters1.getCamp())) {
                                Fightter fightter = new Fightter();
                                if (character.getHp() - 40 * SkillLevelUtil.getSkill2Level(character.getLv()) <= 0) {
                                    character.setHp(0);
                                    //死亡
                                    dead(character, allLiveCharacter);
                                    fightter.setIsDead("1");
                                } else {
                                    character.setHp(character.getHp() - 40 * SkillLevelUtil.getSkill2Level(character.getLv()));
                                }
                                if (leftCharters1.getHp() + 40 * SkillLevelUtil.getSkill2Level(character.getLv()) > leftCharters1.getMaxHp()) {
                                    leftCharters1.setHp(leftCharters1.getMaxHp());
                                } else {
                                    leftCharters1.setHp(leftCharters1.getHp() + 40 * SkillLevelUtil.getSkill2Level(character.getLv()));
                                }
                                fightter.setDirection(character.getDirection());
                                fightter.setGoON(character.getGoON());
                                fightter.setAttack(character.getAttack());
                                fightter.setHp(character.getHp());
                                fightter.setMaxHp(character.getMaxHp());
                                fightter.setSpeed(character.getSpeed());
                                fightter.setIsAction(1);
                                fightter.setGoIntoNum(character.getGoIntoNum());
                                fightter.setStr("-" + 40 * SkillLevelUtil.getSkill2Level(character.getLv()));
                                fightter.setBuff("续命");
                                //
                                fightter.setDirectionFace(leftCharters1.getDirection());
                                fightter.setGoONFace(leftCharters1.getGoON());
                                fightter.setAttackFace(leftCharters1.getAttack());
                                fightter.setHpFace(leftCharters1.getHp());
                                fightter.setMaxHpFace(leftCharters1.getMaxHp());
                                fightter.setSpeedFace(leftCharters1.getSpeed());
                                fightter.setIsActionFace(0);
                                fightter.setGoIntoNumFace(leftCharters1.getGoIntoNum());
                                fightter.setStr("" + 40 * SkillLevelUtil.getSkill2Level(character.getLv()));
                                fightter.setBuff("续命");
                                fightterList.add(fightter);
                            }
                        } else {
                            //大于0时才能治疗
                            if (rightCharters1.getHp() > 0 && "sacred".equals(rightCharters1.getCamp())) {
                                Fightter fightter = new Fightter();
                                if (character.getHp() - 40 * SkillLevelUtil.getSkill2Level(character.getLv()) <= 0) {
                                    character.setHp(0);
                                    //死亡
                                    dead(character, allLiveCharacter);
                                    fightter.setIsDead("1");
                                } else {
                                    character.setHp(character.getHp() - 40 * SkillLevelUtil.getSkill2Level(character.getLv()));
                                }
                                if (rightCharters1.getHp() + 40 * SkillLevelUtil.getSkill2Level(character.getLv()) > rightCharters1.getMaxHp()) {
                                    rightCharters1.setHp(rightCharters1.getMaxHp());
                                } else {
                                    rightCharters1.setHp(rightCharters1.getHp() + 40 * SkillLevelUtil.getSkill2Level(character.getLv()));
                                }
                                fightter.setDirection(character.getDirection());
                                fightter.setGoON(character.getGoON());
                                fightter.setAttack(character.getAttack());
                                fightter.setHp(character.getHp());
                                fightter.setMaxHp(character.getMaxHp());
                                fightter.setSpeed(character.getSpeed());
                                fightter.setGoIntoNum(character.getGoIntoNum());
                                fightter.setIsAction(1);
                                fightter.setStr("-" + 40 * SkillLevelUtil.getSkill2Level(character.getLv()));
                                fightter.setBuff("续命");
                                //
                                fightter.setDirectionFace(rightCharters1.getDirection());
                                fightter.setGoONFace(rightCharters1.getGoON());
                                fightter.setAttackFace(rightCharters1.getAttack());
                                fightter.setHpFace(rightCharters1.getHp());
                                fightter.setMaxHpFace(rightCharters1.getMaxHp());
                                fightter.setSpeedFace(rightCharters1.getSpeed());
                                fightter.setGoIntoNumFace(rightCharters1.getGoIntoNum());
                                fightter.setIsActionFace(0);
                                fightter.setStr("" + 40 * SkillLevelUtil.getSkill2Level(character.getLv()));
                                fightter.setBuff("续命");
                                fightterList.add(fightter);
                            }
                        }
                    }
                }
            }
            // 判断是否结束
            if (allLiveCharacter.stream().filter(x -> "0".equals(x.getDirection())).count() <= 0) {
                mapProsse.put("fightterList", fightterList);
                list.add(mapProsse);
                map.put("result", false);
                isWin = 1;
                break outerLoop; // 直接跳出外层循环
            } else if (allLiveCharacter.stream().filter(x -> "1".equals(x.getDirection())).count() <= 0) {
                mapProsse.put("fightterList", fightterList);
                list.add(mapProsse);
                map.put("result", true);
                isWin = 0;
                break outerLoop; // 直接跳出外层循环
            }
        }
        return isWin;
    }

    public void goON() {

        //TODO 登场技能（瞬发）
        //镇妖塔 Lv1
        //每当新单位入场时，对场上敌方造成69点飞弹

//            致命衰竭 Lv1
//            场上，有单位登场时为目标添加衰弱状态，攻击减少10%，持续99回合

//            北极剑意 Lv1
//            登场时对场上敌方造成最大生命4%的真实伤害


//            大圣降临 Lv1
//            登场时回复自身生命值20%

//            大地净化 Lv1
//            场上，每当敌方单位登场，驱散自身减益效果

        //TODO 登场技能2（瞬发）
//            疫病侵染 Lv1
//            场下，我方单位登场时为场上敌人收到疾病效果，疾病令其受到治疗减少2%


//            魂力飞弹 Lv1
//            场下，每当新单位入场时，对场上敌人造成178点飞弹伤害

//            禁心咒 Lv1
//            场下，每当有单位登场，有17%几率令场上英雄沉默2回合

//            魂力飞弹 Lv1
//            场下，每当新单位入场时，对场上敌人造成178点飞弹伤害
    }

    /**
     * 死亡函数
     * 死亡时调用
     */
    public void dead(Character character, List<Character> allLiveCharacter) {
        //TODO 死亡时触发技能
//        ‌离魂 Lv1
//        死亡时，给场给全体护法增加13点攻击

//        背水一战 Lv1
//        我方单位死亡时，增加自身攻击50，最多叠加4次


//        生生不息 Lv1
//        场下，每当有生物死亡时治疗我方全体90点生命，只能治疗仙界生物
        allLiveCharacter.removeIf(x -> x.getDirection().equals(character.getDirection()) && x.getId().equals(character.getId()) && x.getUuid().equals(character.getUuid()));
    }

    /**
     * 任意伤害触发
     *
     * @return
     */
    public void anyhurt() {

        //TODO 角色
//        烛火燎原 Lv1
//        受到任意伤害时对全体敌方造成54点火焰伤害


    }

    public void anyhurt_up() {

        //TODO 角色
//        南极祝福 Lv1
//        场下，受到任意伤害时提升自身56点生命值上限。


    }

    public void hurt() {

        //TODO 角色

//        嗜血 Lv1
//        受到普通攻击时，为自身添加嗜血效果，提高攻击118点，速度20点

//        绝地反击 Lv1
//        被攻击时，对场上敌方造成相当于敌方攻击的10%的伤害

    }


    public Character reasonableData(Characters characters, List<Characters> charactersList) {
        //TODO 先初始化自身属性
        Character character = new Character();
        BeanUtils.copyProperties(characters, character);
        BigDecimal lv = new BigDecimal(characters.getLv());
        BigDecimal maxHp = lv.multiply(characters.getHpGrowth().multiply(((characters.getStar().subtract(new BigDecimal(1))).multiply(new BigDecimal("0.15")).add(new BigDecimal(1))).multiply((lv.divide(new BigDecimal(80)).add(new BigDecimal("0.8"))))));
        BigDecimal attack = lv.multiply(characters.getAttackGrowth().multiply(((characters.getStar().subtract(new BigDecimal(1))).multiply(new BigDecimal("0.15")).add(new BigDecimal(1))).multiply((lv.divide(new BigDecimal(80)).add(new BigDecimal("0.8"))))));
        BigDecimal speed = lv.multiply(characters.getSpeedGrowth().multiply(((characters.getStar().subtract(new BigDecimal(1))).multiply(new BigDecimal("0.15")).add(new BigDecimal(1))).multiply((lv.divide(new BigDecimal(80)).add(new BigDecimal("0.8"))))));
        character.setHp(maxHp.intValue());
        character.setMaxHp(maxHp.intValue());
        character.setAttack(attack.intValue());
        character.setSpeed(speed.intValue());
        //TODO 再叠加协同属性


        //TODO 装备属性


        return character;
    }

    @Override
    public BaseResp qiangdao(TokenDto token, HttpServletRequest request) throws Exception {
        BaseResp baseResp = new BaseResp();
        if (token == null || Xtool.isNull(token.getToken())) {
            baseResp.setSuccess(0);
            baseResp.setErrorMsg("登录过期");
            return baseResp;
        }
        String userId = (String) redisTemplate.opsForValue().get(token.getToken());
        if (Xtool.isNull(userId)) {
            baseResp.setSuccess(0);
            baseResp.setErrorMsg("登录过期");
            return baseResp;
        }
        User user = userMapper.selectUserByUserId(Integer.parseInt(userId));

        //先判断今天是否签到
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
        if (user.getSignTime() != null) {
            String today = sdf.format(user.getSignTime()); // 获取今天的日期
            String dateTime = sdf.format(new Date()); // 获取当前日期和时间
            if (today.equals(dateTime)) { // 判断字符串日期是否相等
                baseResp.setSuccess(0);
                baseResp.setErrorMsg("今日你已签到请勿重复操作！");
                return baseResp;
            }
        }

        //判断是第几次签到
        if (user.getSignCount() == 0 || user.getSignCount() == 7) {
            //获得3000银两
            user.setGold(user.getGold().add(new BigDecimal("3000")));
            user.setSignCount(1);
            user.setSignTime(new Date());
        } else if (user.getSignCount() == 1) {
            //获得洛神
            Characters characters1 = charactersMapper.listById(userId, "1030");
            if (characters1 != null) {
                characters1.setStackCount(characters1.getStackCount() + 1);
                charactersMapper.updateByPrimaryKey(characters1);
            } else {
                Characters characters = new Characters();
                characters.setStackCount(0);
                characters.setGoIntoNum(0);
                characters.setId("1030");
                characters.setUserId(Integer.parseInt(userId));
                characters.setLv(1);
                characters.setCreateTime(new Date());
                charactersMapper.insert(characters);
            }
            user.setSignCount(2);
            user.setSignTime(new Date());
        } else if (user.getSignCount() == 2) {
            user.setDiamond(user.getDiamond().add(new BigDecimal("500")));
            user.setSignCount(3);
            user.setSignTime(new Date());
        } else if (user.getSignCount() == 3) {
            //获得10000银两
            user.setGold(user.getGold().add(new BigDecimal("10000")));
            user.setSignCount(4);
            user.setSignTime(new Date());
        } else if (user.getSignCount() == 4) {
            user.setSignCount(5);
            user.setSignTime(new Date());
        } else if (user.getSignCount() == 5) {
            user.setDiamond(user.getDiamond().add(new BigDecimal("1000")));
            user.setSignCount(6);
            user.setSignTime(new Date());
        } else if (user.getSignCount() == 6) {
            //获得瑶池仙女
            Characters characters1 = charactersMapper.listById(userId, "1040");
            if (characters1 != null) {
                characters1.setStackCount(characters1.getStackCount() + 1);
                charactersMapper.updateByPrimaryKey(characters1);
            } else {
                Characters characters = new Characters();
                characters.setStackCount(0);
                characters.setGoIntoNum(0);
                characters.setId("1040");
                characters.setUserId(Integer.parseInt(userId));
                characters.setLv(1);
                characters.setCreateTime(new Date());
                charactersMapper.insert(characters);
            }
            user.setSignCount(7);
            user.setSignTime(new Date());
        }
        userMapper.updateuser(user);
        User user2 = userMapper.selectUserByUserId(Integer.parseInt(userId));
        baseResp.setSuccess(1);
        UserInfo info = new UserInfo();
        BeanUtils.copyProperties(user2, info);
        //获取卡牌数据
        List<Characters> characterList = charactersMapper.selectByUserId(user.getUserId());
        info.setCharacterList(characterList);
        baseResp.setData(info);
        baseResp.setErrorMsg("更新成功");
        return baseResp;
    }

    @Override
    public BaseResp notice(HttpServletRequest request) throws Exception {
        BaseResp baseResp = new BaseResp();
        Set<String> keys = redisTemplate.keys("notice_*");
        List<String> notices = new ArrayList<>();
        for (String key : keys) {
            String value = (String) redisTemplate.opsForValue().get(key);
            notices.add(value);
        }
        baseResp.setSuccess(1);
        baseResp.setData(notices);
        return baseResp;
    }

    @Override
    public BaseResp playBattle2(TokenDto token, HttpServletRequest request) throws Exception {
        // 创建战斗缓存
        Map<String, BattleManager> battleCache = new HashMap<>();

        // 创建A队护法
        List<Guardian> campA = new ArrayList<>();
        List<Character> copyCampA = new ArrayList<>();
        campA.add(new Guardian("牛魔王", Camp.A, 1, Profession.WARRIOR, Race.DEMON, 2000, 300, 100));
        campA.add(new Guardian("厚土娘娘", Camp.A, 2, Profession.IMMORTAL, Race.IMMORTAL, 2500, 200, 80));
        campA.add(new Guardian("镇元子", Camp.A, 3, Profession.GOD, Race.DEMON, 1800, 250, 90));
        campA.add(new Guardian("妲己", Camp.A, 4, Profession.IMMORTAL, Race.DEMON, 1500, 180, 120));
        campA.add(new Guardian("长生大帝", Camp.A, 5, Profession.GOD, Race.IMMORTAL, 2200, 150, 80));

        copyCampA.add(new Character("1027","牛魔王", Camp.A, 1, Profession.WARRIOR, Race.DEMON, 2000, 300, 100));
        copyCampA.add(new Character("1012","厚土娘娘", Camp.A, 2, Profession.IMMORTAL, Race.IMMORTAL, 2500, 200, 80));
        copyCampA.add(new Character("1016","镇元子", Camp.A, 3, Profession.GOD, Race.IMMORTAL, 1800, 250, 90));
        copyCampA.add(new Character("1005","妲己", Camp.A, 4, Profession.IMMORTAL, Race.DEMON, 1500, 180, 120));
        copyCampA.add(new Character("1020","长生大帝", Camp.A, 5, Profession.GOD, Race.IMMORTAL, 2200, 150, 80));
        // 创建B队护法
        List<Guardian> campB = new ArrayList<>();
        List<Character> copyCampB = new ArrayList<>();
        campB.add(new Guardian("阎王", Camp.B, 1, Profession.GOD, Race.DEMON, 1500, 220, 85));
        campB.add(new Guardian("聂小倩", Camp.B, 2, Profession.IMMORTAL, Race.DEMON, 1200, 180, 110));
        campB.add(new Guardian("托塔天王", Camp.B, 3, Profession.GOD, Race.IMMORTAL, 2200, 280, 95));
        campB.add(new Guardian("齐天大圣", Camp.B, 4, Profession.WARRIOR, Race.DEMON, 1800, 320, 130));
        campB.add(new Guardian("铁扇公主", Camp.B, 5, Profession.IMMORTAL, Race.DEMON, 1600, 200, 90));

        copyCampB.add(new Character("1035","阎王", Camp.B, 1, Profession.GOD, Race.DEMON, 1500, 220, 85));
        copyCampB.add(new Character("1007","聂小倩", Camp.B, 2, Profession.IMMORTAL, Race.DEMON, 1200, 180, 110));
        copyCampB.add(new Character("1002","托塔天王", Camp.B, 3, Profession.GOD, Race.IMMORTAL, 2200, 280, 95));
        copyCampB.add(new Character("1010","齐天大圣", Camp.B, 4, Profession.WARRIOR, Race.DEMON, 1800, 320, 130));
        copyCampB.add(new Character("1030","洛神", Camp.B, 5, Profession.IMMORTAL, Race.IMMORTAL, 1600, 200, 90));

        // 开始战斗
        String battleId = "BATTLE_20251130_001";
        BattleManager battle = new BattleManager(battleId, campA, campB);
        battleCache.put(battleId, battle);
        battle.startBattle();

        // 打印优化后的日志
        printFinalBattleLogs(battle.getBattleLogs());
        BaseResp baseResp = new BaseResp();
        baseResp.setSuccess(1);
        Map map=new HashMap();
        map.put("campA",copyCampA);
        map.put("campB",copyCampB);
        map.put("battleLogs",battle.getBattleLogs());
        baseResp.setData(map);
        return baseResp;
    }
    // 最终版日志打印格式（包含位置信息）
    private static void printFinalBattleLogs(List<BattleLog> logs) {
        for (BattleLog log : logs) {
            System.out.println("======================================================================");
            System.out.printf("[%s][回合%03d]%s\n", log.getBattleId(), log.getRound(), log.getEventType());

            // 来源单位信息（包含位置）
            if (log.getSourceUnit() != null) {
                System.out.printf("来源: %s[%s%d号位] HP:%d→%d ATK:%d→%d SPEED:%d→%d\n",
                        log.getSourceUnit(),
                        log.getSourceCamp() != null ? log.getSourceCamp() : "",
                        log.getSourcePosition(),
                        log.getSourceHpBefore(),
                        log.getSourceHpAfter(),
                        log.getSourceAttackBefore(),
                        log.getSourceAttackAfter(),
                        log.getSourceSpeedBefore(),
                        log.getSourceSpeedAfter());
            }

            // 目标单位信息（包含位置）
            if (log.getTargetUnit() != null) {
                System.out.printf("目标: %s[%s%d号位] HP:%d→%d ATK:%d→%d SPEED:%d→%d\n",
                        log.getTargetUnit(),
                        log.getTargetCamp() != null ? log.getTargetCamp() : "",
                        log.getTargetPosition(),
                        log.getTargetHpBefore(),
                        log.getTargetHpAfter(),
                        log.getTargetAttackBefore(),
                        log.getTargetAttackAfter(),
                        log.getTargetSpeedBefore(),
                        log.getTargetSpeedAfter());
            } else if (log.getTargetUnitList() != null && !log.getTargetUnitList().isEmpty()) {
                System.out.printf("目标列表: %s\n", log.getTargetUnitList());
            }

            // 在场单位状态（包含位置）
            if (log.getFieldUnitsStatus() != null && !log.getFieldUnitsStatus().isEmpty()) {
                System.out.printf("在场单位: %s\n", log.getFieldUnitsStatus());
            }

            // 其他信息
            if (log.getValue() > 0) {
                System.out.printf("数值: %d\n", log.getValue());
            }
            if (log.getEffectType() != null) {
                System.out.printf("效果: %s\n", log.getEffectType());
            }
            if (log.getDamageType() != null) {
                System.out.printf("伤害类型: %s\n", log.getDamageType());
            }
            if (log.getExtraDesc() != null) {
                System.out.printf("描述: %s\n", log.getExtraDesc());
            }
        }
    }
    public static Set<Integer> getRandomElements(Set<Integer> set, int count) {
        if (count > set.size()) {
            throw new IllegalArgumentException("请求数量超过集合大小");
        }
        List<Integer> list = new ArrayList<Integer>(set);
        Set<Integer> result = new HashSet<>();
        Random random = new Random();

        while (result.size() < count) {
            int index = random.nextInt(list.size());
            result.add(list.get(index));
        }
        return result;
    }
}
