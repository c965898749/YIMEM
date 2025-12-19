package com.sy.service.impl;

import cn.hutool.core.util.IdUtil;
import com.baomidou.mybatisplus.mapper.Wrapper;
import com.sy.mapper.game.*;
import com.sy.mapper.UserMapper;
import com.sy.model.User;
import com.sy.model.game.*;
import com.sy.model.game.Character;
import com.sy.model.resp.BaseResp;
import com.sy.service.GameServiceService;
import com.sy.service.UserServic;
import com.sy.tool.*;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.ValueOperations;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.DigestUtils;

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

@Slf4j
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
    @Autowired
    private FriendRelationMapper friendRelationMapper;
    @Autowired
    private PveRewardMapper pveRewardMapper;
    @Autowired
    private PveBossDetailMapper pveBossDetailMapper;
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
        Map map = new HashMap();
        map.put("detail_code", token.getId());
        List<PveBossDetail> pveBossDetailList = pveBossDetailMapper.selectByMap(map);
        List<PveBossDetail> uniqueUserList = pveBossDetailList.stream()
                // 以name为key，User为value，LinkedHashMap保留插入顺序
                .collect(Collectors.toMap(
                        PveBossDetail::getBossId,    // key：名字（去重依据）
                        x -> x,     // value：用户对象
                        (oldUser, newUser) -> oldUser, // 重复时保留旧值（首次出现）
                        LinkedHashMap::new             // 保证顺序
                ))
                .values() // 提取去重后的User集合
                .stream()
                .collect(Collectors.toList());
        pveDetail.setPveBossDetails(uniqueUserList);
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
        String userId = token.getUserId();
//        if (Xtool.isNull(userId)) {
//            baseResp.setSuccess(0);
//            baseResp.setErrorMsg("登录过期");
//            return baseResp;
//        }

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
        String name = luminaryMap[dayOfWeek.getValue() - 1] + activityDetail.getDifficultyLevel();
        Battle battle = this.battle(leftCharacter, Integer.parseInt(userId), user.getNickname(), rightCharacter, 0, name, user.getGameImg(), "0");
        if (battle.getIsWin() == 0) {
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
                        Card card1 = cardMapper.selectByid(content.getItemId());
                        if (card1 == null) {
                            baseResp.setErrorMsg("服务器异常联想管理员");
                            baseResp.setSuccess(0);
                            return baseResp;
                        }
                        Characters characters = new Characters();
                        characters.setStackCount(content.getRewardAmount() - 1);
                        characters.setId(content.getItemId() + "");
                        characters.setLv(1);
                        characters.setUserId(Integer.parseInt(userId));
                        characters.setStar(new BigDecimal(1));
                        characters.setMaxLv(CardMaxLevelUtils.getMaxLevel(card1.getName(), card1.getStar().doubleValue()));
                        charactersMapper.insert(characters);
                    }
                }
            }
            map.put("rewards", rewardList);
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
    public LevelUpResult calculateLevelUp(int mainCardLevel, int mainCardExp, List<MaterialCard> materialCards, List<Integer> expTable, List<Integer> silverTable, Integer maxLevel, String id) {

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


        return new LevelUpResult(currentLevel, currentLevelExp, totalSilver, id);
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
        BaseResp baseResp = new BaseResp();
        if (Xtool.isNull(token.getMyMap())) {
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
        // 1. 获取前端传递的二维数组
        List<List<Object>> strArray = token.getMyMap();
        if (strArray == null || strArray.isEmpty()) {
            baseResp.setSuccess(0);
            baseResp.setErrorMsg("服务器异常1");
            return baseResp;
        }

        // 2. 将二维数组转为Map<String, Integer>（核心步骤）
        Map<String, Integer> myMap = new HashMap<>();
        for (List<Object> entry : strArray) {
            // 校验数组元素格式（避免前端传参异常导致报错）
            if (entry.size() != 2) {
                baseResp.setSuccess(0);
                baseResp.setErrorMsg("服务器异常2");
                return baseResp;
            }
            // 强转：第一个元素是String（键），第二个是Integer（值）
            String key = (String) entry.get(0);
            Integer value = (Integer) entry.get(1);
            myMap.put(key, value);
        }
//        List<Characters> charactersList = new ArrayList<>();
        // 3. 业务逻辑处理（示例：遍历Map）
        for (Map.Entry<String, Integer> entry : myMap.entrySet()) {
//            System.out.println("键：" + entry.getKey() + "，值：" + entry.getValue());
            Characters characterCong = charactersMapper.listById(token.getUserId(), entry.getKey());
            for (int i = 0; i < entry.getValue(); i++) {
//                charactersList.add(characterCong);
                //如果是魂力宝珠
                if ("105".equals(characterCong.getId())){
                    materials.add(new MaterialCard(1, 5000));
                }else {
                    //第一张吞掉本经验，后续则5经验
                    if (i == 0) {
                        materials.add(new MaterialCard(characterCong.getLv(), characterCong.getExp()));
                    } else {
                        materials.add(new MaterialCard(1, 5));
                    }
                }

            }

        }
        int maxLevel = character.getMaxLv(); // 最高等级5级
        // 主卡：当前2级，已有30经验
        int mainLevel = character.getLv();
        int mainExp = character.getExp();

        LevelUpResult result = this.calculateLevelUp(mainLevel, mainExp, materials, expTable, silverTable, maxLevel, token.getId());

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
        BaseResp baseResp = new BaseResp();
        if (Xtool.isNull(token.getMyMap())) {
            baseResp.setSuccess(0);
            baseResp.setErrorMsg("从卡不存在");
            return baseResp;
        }
        // 1. 获取前端传递的二维数组
        List<List<Object>> strArray = token.getMyMap();
        if (strArray == null || strArray.isEmpty()) {
            baseResp.setSuccess(0);
            baseResp.setErrorMsg("服务器异常1");
            return baseResp;
        }

        // 2. 将二维数组转为Map<String, Integer>（核心步骤）
        Map<String, Integer> myMap = new HashMap<>();
        for (List<Object> entry : strArray) {
            // 校验数组元素格式（避免前端传参异常导致报错）
            if (entry.size() != 2) {
                baseResp.setSuccess(0);
                baseResp.setErrorMsg("服务器异常2");
                return baseResp;
            }
            // 强转：第一个元素是String（键），第二个是Integer（值）
            String key = (String) entry.get(0);
            Integer value = (Integer) entry.get(1);
            myMap.put(key, value);
        }
        for (Map.Entry<String, Integer> entry : myMap.entrySet()) {
            Characters characters = charactersMapper.listById(token.getUserId(), entry.getKey());
            if (characters.getStackCount()-entry.getValue()>=0) {
                characters.setStackCount(characters.getStackCount() -entry.getValue());
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
            Map map = new HashMap();
            map.put("friend_id", token.getUserId());
            map.put("status", 0);
            List<FriendRelation> friendRelations = friendRelationMapper.selectByMap(map);
            List<UserInfo> userList = new ArrayList<>();
            for (FriendRelation friendRelation : friendRelations) {
                User user = userMapper.selectUserByUserId(friendRelation.getUserId());
                UserInfo userInfo = new UserInfo();
                BeanUtils.copyProperties(user, userInfo);
                userInfo.setId(friendRelation.getId() + "");
                userList.add(userInfo);
            }
            BaseResp baseResp = new BaseResp();
            baseResp.setData(userList);
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
                    Card card1 = cardMapper.selectByid(Integer.parseInt(content.getItemId() + ""));
                    if (card1 == null) {
                        baseResp.setErrorMsg("服务器异常联想管理员");
                        baseResp.setSuccess(0);
                        return baseResp;
                    }
                    Characters characters = new Characters();
                    characters.setStackCount(content.getItemQuantity() - 1);
                    characters.setId(content.getItemId() + "");
                    characters.setLv(1);
                    characters.setUserId(Integer.parseInt(userId));
                    characters.setStar(new BigDecimal(1));
                    characters.setMaxLv(CardMaxLevelUtils.getMaxLevel(card1.getName(), card1.getStar().doubleValue()));
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
            Card card1 = cardMapper.selectByid(Integer.parseInt(token.getId()));
            if (card1 == null) {
                baseResp.setErrorMsg("服务器异常联想管理员");
                baseResp.setSuccess(0);
                return baseResp;
            }
            Characters characters = new Characters();
            characters.setStackCount(0);
            characters.setId(token.getId());
            characters.setLv(1);
            characters.setUserId(Integer.parseInt(userId));
            characters.setStar(new BigDecimal(1));
            characters.setMaxLv(CardMaxLevelUtils.getMaxLevel(card1.getName(), card1.getStar().doubleValue()));
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
    public BaseResp hechenCard(TokenDto token, HttpServletRequest request) throws Exception {
        BaseResp baseResp = new BaseResp();
        if (token == null || Xtool.isNull(token.getToken())) {
            baseResp.setSuccess(0);
            baseResp.setErrorMsg("登录过期");
            return baseResp;
        }
        String userId = token.getUserId();
        User user = userMapper.selectUserByUserId(Integer.parseInt(token.getUserId()));
        if (Xtool.isNull(userId)) {
            baseResp.setSuccess(0);
            baseResp.setErrorMsg("登录过期");
            return baseResp;
        }
        StarSynthesisMain starSynthesisMain=starSynthesisMainMapper.selectById(token.getId());
        if (user.getGold().subtract(starSynthesisMain.getExtraCost()).compareTo(BigDecimal.ZERO)<=0){
            baseResp.setSuccess(0);
            baseResp.setErrorMsg("合成金币不足");
            return baseResp;
        }
        user.setGold(user.getGold().subtract(starSynthesisMain.getExtraCost()));
        List<Characters> charactersList=new ArrayList<>();
        if (starSynthesisMain!=null){
            List<StarSynthesisMaterials> materials = starSynthesisMaterialsMapper.selectall(starSynthesisMain.getId());
            for (StarSynthesisMaterials material : materials) {
                Characters characters = charactersMapper.listById(token.getUserId(), material.getId());
                if (characters==null){
                    baseResp.setSuccess(0);
                    baseResp.setErrorMsg("合成素材不足");
                    return baseResp;
                }
                Integer count=  materials.stream().filter(x->x.getId().equals(material.getId())).collect(Collectors.toList()).size();
                if (characters.getStackCount()+1<count){
                    baseResp.setSuccess(0);
                    baseResp.setErrorMsg("合成素材不足");
                    return baseResp;
                }
                if (characters.getLv()<characters.getMaxLv()){
                    baseResp.setSuccess(0);
                    baseResp.setErrorMsg("合成素材未满级");
                    return baseResp;
                }
                charactersList.add(characters);
            }
        }

       for (Characters characters : charactersList) {
           Characters characters1 = charactersMapper.listById(token.getUserId(), characters.getId());
            if (characters1.getStackCount()-1>=0) {
                characters1.setStackCount(characters1.getStackCount() -1);
                characters1.setLv(1);
                characters1.setExp(5);
            } else {
                characters1.setIsDelete("1");
            }
            charactersMapper.updateByPrimaryKey(characters1);
        }
        userMapper.updateuser(user);
        Characters characters1 = charactersMapper.listById(userId, token.getId());
        Card card1 = cardMapper.selectByid(Integer.parseInt(token.getId()));
        if (characters1 != null) {
            characters1.setStackCount(characters1.getStackCount() + 1);
            charactersMapper.updateByPrimaryKey(characters1);
        } else {

            if (card1 == null) {
                baseResp.setErrorMsg("服务器异常联想管理员");
                baseResp.setSuccess(0);
                return baseResp;
            }
            Characters characters = new Characters();
            characters.setStackCount(0);
            characters.setId(token.getId());
            characters.setLv(1);
            characters.setUserId(Integer.parseInt(userId));
            characters.setStar(card1.getStar());
            characters.setMaxLv(CardMaxLevelUtils.getMaxLevel(card1.getName(), card1.getStar().doubleValue()));
            charactersMapper.insert(characters);
        }
        CardDto dto = new CardDto();
        dto.setHero(card1);
        ValueOperations opsForValue = redisTemplate.opsForValue();
        if (card1.getStar().compareTo(new BigDecimal(3)) > 0) {
            Date date = new Date();
            opsForValue.set("notice_" + date.getTime() + "", "恭喜 " + user.getNickname() + " 图谱合成获得" + card1.getStar().stripTrailingZeros() + "星" + card1.getName(), 3600 * 12, TimeUnit.SECONDS);
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
        baseResp.setErrorMsg("合成成功");
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
            Card card1 = cardMapper.selectByid(Integer.parseInt(drawnCard.getId()));
            if (card1 == null) {
                baseResp.setErrorMsg("服务器异常联想管理员");
                baseResp.setSuccess(0);
                return baseResp;
            }
            Characters characters = new Characters();
            characters.setStackCount(0);
            characters.setId(drawnCard.getId());
            characters.setLv(1);
            characters.setUserId(Integer.parseInt(userId));
            characters.setStar(drawnCard.getStar());
            characters.setMaxLv(CardMaxLevelUtils.getMaxLevel(drawnCard.getName(), drawnCard.getStar().doubleValue()));
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
            Card card1 = cardMapper.selectByid(Integer.parseInt(drawnCard.getId()));
            if (card1 == null) {
                baseResp.setErrorMsg("服务器异常联想管理员");
                baseResp.setSuccess(0);
                return baseResp;
            }
            Characters characters = new Characters();
            characters.setStackCount(0);
            characters.setId(drawnCard.getId());
            characters.setLv(1);
            characters.setUserId(Integer.parseInt(userId));
            characters.setStar(drawnCard.getStar());
            characters.setMaxLv(CardMaxLevelUtils.getMaxLevel(drawnCard.getName(), drawnCard.getStar().doubleValue()));
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
    public BaseResp characteSell(TokenDto token, HttpServletRequest request) throws Exception {
        BaseResp baseResp = new BaseResp();
        String userId = token.getUserId();
        User user = userMapper.selectUserByUserId(Integer.parseInt(userId));
        Characters characters1 = charactersMapper.listById(userId, token.getId());
        if (characters1==null){
            baseResp.setErrorMsg("卡牌已售罄请勿重复出售");
            baseResp.setSuccess(0);
            return baseResp;
        }
        if (characters1.getStackCount()-1>=0) {
            characters1.setStackCount(characters1.getStackCount() -1);
        } else {
            characters1.setIsDelete("1");
        }
        charactersMapper.updateByPrimaryKey(characters1);
        BigDecimal gold=new BigDecimal(1000);
        if ("104".equals(token.getId())){
            gold=new BigDecimal(999999);
        }else if ("1082".equals(token.getId())){
            gold=new BigDecimal(99999);
        }else if ("1091".equals(token.getId())){
            gold=new BigDecimal(99999);
        }
        user.setGold(user.getGold().add(gold));
        userMapper.updateuser(user);
        List<Characters> nowCharactersList = charactersMapper.selectByUserId(Integer.parseInt(userId));
        CardDto dto = new CardDto();
        dto.setCharacters(nowCharactersList);
        //卡池数量
        UserInfo info = new UserInfo();
        BeanUtils.copyProperties(user, info);
        baseResp.setSuccess(1);
        Map map = new HashMap();
        map.put("user", info);
        map.put("dto", dto);
        map.put("gold", gold);
        baseResp.setData(map);
        baseResp.setErrorMsg("出售成功");
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
            Card card1 = cardMapper.selectByid(Integer.parseInt(drawnCard.getId()));
            if (card1 == null) {
                baseResp.setErrorMsg("服务器异常联想管理员");
                baseResp.setSuccess(0);
                return baseResp;
            }
            Characters characters = new Characters();
            characters.setStackCount(0);
            characters.setId(drawnCard.getId());
            characters.setLv(1);
            characters.setUserId(Integer.parseInt(userId));
            characters.setStar(drawnCard.getStar());
            characters.setMaxLv(CardMaxLevelUtils.getMaxLevel(drawnCard.getName(), drawnCard.getStar().doubleValue()));
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
            user.setRate(user.getRate() + 1);
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
            //如果是20倍则获取的女娲石
            if (user.getRate() > 10 && i == 1) {
                Card card = cardMapper.selectByid(100);
                drawnCards.add(card);
                user.setRate(0);
                userMapper.updateuser(user);
            } else {
                drawnCards.add(drawnCard);
            }
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
                Card card1 = cardMapper.selectByid(Integer.parseInt(drawnCard.getId()));
                if (card1 == null) {
                    baseResp.setErrorMsg("服务器异常联想管理员");
                    baseResp.setSuccess(0);
                    return baseResp;
                }
                Characters characters = new Characters();
                characters.setStackCount(0);
                characters.setId(drawnCard.getId());
                characters.setLv(1);
                characters.setUserId(Integer.parseInt(userId));
                characters.setStar(drawnCard.getStar());
                characters.setCreateTime(new Date());
                characters.setMaxLv(CardMaxLevelUtils.getMaxLevel(drawnCard.getName(), drawnCard.getStar().doubleValue()));
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
    public BaseResp start3(TokenDto token, HttpServletRequest request) throws Exception {
        //先获取当前用户战队
        BaseResp baseResp = new BaseResp();
        if (token == null || Xtool.isNull(token.getToken())) {
            baseResp.setSuccess(0);
            baseResp.setErrorMsg("登录过期");
            return baseResp;
        }
//        String userId = (String) redisTemplate.opsForValue().get(token.getToken());
//        if (Xtool.isNull(userId)) {
//            baseResp.setSuccess(0);
//            baseResp.setErrorMsg("登录过期");
//            return baseResp;
//        }
        User user = userMapper.selectUserByUserId(Integer.parseInt(token.getUserId()));
        //自己的战队
        List<Characters> leftCharacter = charactersMapper.goIntoListById(user.getUserId() + "");
        if (Xtool.isNull(leftCharacter)) {
            baseResp.setSuccess(0);
            baseResp.setErrorMsg("你没有配置战队无法战斗");
            return baseResp;
        }
        Collections.sort(leftCharacter, Comparator.comparing(Characters::getGoIntoNum));
        //对手战队
        User user1 = userMapper.selectUserByUserId(Integer.parseInt(token.getId()));
        List<Characters> rightCharacter = charactersMapper.goIntoListById(user1.getUserId() + "");
        if (Xtool.isNull(rightCharacter)) {
            baseResp.setSuccess(0);
            baseResp.setErrorMsg("对方没有配置战队无法战斗");
            return baseResp;
        }

        baseResp.setSuccess(1);
        Battle battle = this.battle(leftCharacter, user.getUserId(), user.getNickname(), rightCharacter, user1.getUserId(), user1.getNickname(), user.getGameImg(), "3");
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
        Map map1 = new HashMap();
        map1.put("detail_code", token.getStr());
        List<PveBossDetail> pveBossDetails = pveBossDetailMapper.selectByMap(map1);
        Integer i = 0;
        for (PveBossDetail pveBossDetail : pveBossDetails) {
            Card card = cardMapper.selectByid(pveBossDetail.getBossId());
            Characters characters = new Characters();
            BeanUtils.copyProperties(card, characters);
            characters.setGoIntoNum(pveBossDetail.getGoIntoNum());
            characters.setLv(pveBossDetail.getDifficultyLevel());
            characters.setUuid(i);
            long originalCount = pveBossDetails.stream()
                    .filter(Objects::nonNull) // 过滤null对象
                    .map(PveBossDetail::getBossId)
                    .filter(Objects::nonNull) // 过滤null名称
                    .count();
            if (originalCount > 1) {
                characters.setName(characters.getName() + i);
            } else {
                characters.setName(characters.getName());
            }
            rightCharacter.add(characters);
            i++;
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
            List<PveReward> pveRewardsAll = pveRewardMapper.selectByMap(map1);
            List<PveReward> pveRewards = new ArrayList<>();
            for (PveReward pveReward : pveRewardsAll) {
                if (!ProbabilityUtils.hitProbability(pveReward.getPrent())) {
                    continue;
                }
                pveRewards.add(pveReward);
            }
            for (PveReward content : pveRewards) {
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
                        Card card = cardMapper.selectByid(content.getItemId());
                        if (card == null) {
                            baseResp.setErrorMsg("服务器异常联想管理员");
                            baseResp.setSuccess(0);
                            return baseResp;
                        }
                        Characters characters = new Characters();
                        characters.setStackCount(content.getRewardAmount() - 1);
                        characters.setId(content.getItemId() + "");
                        characters.setLv(1);
                        characters.setUserId(Integer.parseInt(userId));
                        characters.setStar(new BigDecimal(1));
                        characters.setMaxLv(CardMaxLevelUtils.getMaxLevel(card.getName(), card.getStar().doubleValue()));
                        charactersMapper.insert(characters);
                    }
                }
            }
            map.put("rewards", pveRewards);
        } else {
            battle.setChapter(token.getStr());
        }
        user.setTiliCount(user.getTiliCount() - 2);
        user.setTiliCountTime(new Date());
        PveDetail pveDetail2 = pveDetailMapper.selectById(battle.getChapter());
        Map map2 = new HashMap();
        map2.put("detail_code", battle.getChapter());
        List<PveBossDetail> pveBossDetailList = pveBossDetailMapper.selectByMap(map2);
        List<PveBossDetail> uniqueUserList = pveBossDetailList.stream()
                // 以name为key，User为value，LinkedHashMap保留插入顺序
                .collect(Collectors.toMap(
                        PveBossDetail::getBossId,    // key：名字（去重依据）
                        x -> x,     // value：用户对象
                        (oldUser, newUser) -> oldUser, // 重复时保留旧值（首次出现）
                        LinkedHashMap::new             // 保证顺序
                ))
                .values() // 提取去重后的User集合
                .stream()
                .collect(Collectors.toList());
        pveDetail2.setPveBossDetails(uniqueUserList);
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
        List<User> users = userMapper.SelectRandUser();
        for (User user1 : users) {
            List<FriendRelation> friendRelations = friendRelationMapper.findByUserid(userId, user1.getUserId());
            if (Xtool.isNotNull(friendRelations)) {
                user1.setFriendStatus(friendRelations.get(0).getStatus());
            }
        }
        baseResp.setSuccess(1);
        Map map = new HashMap();
        map.put("user", user);
        map.put("parking", users);
        baseResp.setData(map);
        return baseResp;
    }

    @Override
    public BaseResp friendAllList(TokenDto token, HttpServletRequest request) throws Exception {
        //先获取当前用户战队
        BaseResp baseResp = new BaseResp();
        if (token == null || Xtool.isNull(token.getToken())) {
            baseResp.setSuccess(0);
            baseResp.setErrorMsg("登录过期");
            return baseResp;
        }
        List<User> users = friendRelationMapper.findByid(token.getUserId(), 1);
        baseResp.setSuccess(1);
        Map map = new HashMap();
        map.put("friends", users);
        baseResp.setData(map);
        return baseResp;
    }

    @Override
    public BaseResp invitationSend(TokenDto token, HttpServletRequest request) throws Exception {
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
        if (userMapper.selectUserByUserId(Integer.parseInt(token.getUserId())) == null) {
            baseResp.setSuccess(0);
            baseResp.setErrorMsg("未找到该玩家");
            return baseResp;
        }
        //判断好友是否上限
        if (friendRelationMapper.findCount(userId) >= 40) {
            baseResp.setSuccess(0);
            baseResp.setErrorMsg("你的好友已上限");
            return baseResp;
        }
        if (friendRelationMapper.findCount(token.getUserId()) >= 40) {
            baseResp.setSuccess(0);
            baseResp.setErrorMsg("对方好友已上限");
            return baseResp;
        }
        Wrapper<FriendRelation> wrapper = new Wrapper<FriendRelation>() {
            @Override
            public String getSqlSegment() {
                return "user_id like 1";
            }
        };

        List<FriendRelation> friendRelationList = friendRelationMapper.selectList(new Wrapper<FriendRelation>() {
            @Override
            public String getSqlSegment() {
                return "where user_id =" + userId + " and friend_id =" + token.getUserId();
            }
        });
        if (Xtool.isNotNull(friendRelationList)) {
            baseResp.setSuccess(0);
            baseResp.setErrorMsg("请勿重复结伴");
            return baseResp;
        } else {
            FriendRelation friendRelation = new FriendRelation();
            friendRelation.setCreateTime(new Date());
            friendRelation.setFriendId(Integer.parseInt(token.getUserId() + ""));
            friendRelation.setUserId(Integer.parseInt(userId + ""));
            friendRelation.setStatus(0);
            friendRelation.setCreateTime(new Date());
            friendRelationMapper.insert(friendRelation);
        }
        baseResp.setSuccess(1);
        baseResp.setErrorMsg("结伴中");
        return baseResp;
    }

    @Override
    public BaseResp invitationHandle(TokenDto token, HttpServletRequest request) throws Exception {
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
        // 1. 查找申请记录（applyUserId发起的，userId是被申请人）
        FriendRelation relationOpt = friendRelationMapper.selectById(token.getId());
        if (relationOpt == null || relationOpt.getStatus() != 0 || !userId.equals(relationOpt.getFriendId() + "")) {
            baseResp.setSuccess(0);
            baseResp.setErrorMsg("无待处理的好友申请");
            return baseResp;
        }
        if ("1".equals(token.getStr())) {
            // 2. 同意：更新状态为1（已好友），并创建反向关系
            relationOpt.setStatus(1);
            friendRelationMapper.updateById(relationOpt);

            FriendRelation reverseRelation = new FriendRelation();
            reverseRelation.setUserId(Integer.parseInt(userId));
            reverseRelation.setFriendId(relationOpt.getUserId());
            reverseRelation.setStatus(1);
            friendRelationMapper.insert(reverseRelation);
        } else {
            // 3. 拒绝：删除申请记录
            friendRelationMapper.deleteById(token.getId());
        }
        baseResp.setSuccess(1);
        baseResp.setErrorMsg("操作成功");
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
        if (Xtool.isNotNull(characters.getPassiveIntroduceThree())){
            List<Characters> xieTong=charactersList.stream().filter(x->characters.getPassiveIntroduceThree().equals(x.getId())).collect(Collectors.toList());
            if (Xtool.isNotNull(xieTong)){
                int skillLevel = SkillLevelCalculator.getSkillLevel(lv.intValue());
//                453点生命上限，158点攻击，158点速度。
                if (Xtool.isNotNull(characters.getCollHp())){
                    character.setHp(character.getHp()+skillLevel*characters.getCollHp());
                    character.setMaxHp(maxHp.intValue()+skillLevel*characters.getCollHp());
                }
                if (Xtool.isNotNull(characters.getCollAttack())){
                    character.setAttack(attack.intValue()+skillLevel*characters.getCollAttack());
                }
                if (Xtool.isNotNull(characters.getCollSpeed())){
                    character.setSpeed(speed.intValue()+skillLevel*characters.getCollSpeed());
                }
            }
        }

        if ("不动如山1".equals(character.getPassiveIntroduceThree())&&characters.getGoIntoNum()==1){
            int skillLevel = SkillLevelCalculator.getSkillLevel(lv.intValue());
//                453点生命上限，158点攻击，158点速度。
            if (Xtool.isNotNull(characters.getCollHp())){
                character.setHp(character.getHp()+skillLevel*characters.getCollHp());
                character.setMaxHp(maxHp.intValue()+skillLevel*characters.getCollHp());
            }
            if (Xtool.isNotNull(characters.getCollAttack())){
                character.setAttack(attack.intValue()+skillLevel*characters.getCollAttack());
            }
            if (Xtool.isNotNull(characters.getCollSpeed())){
                character.setSpeed(speed.intValue()+skillLevel*characters.getCollSpeed());
            }
        }

        if ("不动如山2".equals(character.getPassiveIntroduceThree())&&characters.getGoIntoNum()==2){
            int skillLevel = SkillLevelCalculator.getSkillLevel(lv.intValue());
//                453点生命上限，158点攻击，158点速度。
            if (Xtool.isNotNull(characters.getCollHp())){
                character.setHp(character.getHp()+skillLevel*characters.getCollHp());
                character.setMaxHp(maxHp.intValue()+skillLevel*characters.getCollHp());
            }
            if (Xtool.isNotNull(characters.getCollAttack())){
                character.setAttack(attack.intValue()+skillLevel*characters.getCollAttack());
            }
            if (Xtool.isNotNull(characters.getCollSpeed())){
                character.setSpeed(speed.intValue()+skillLevel*characters.getCollSpeed());
            }
        }

        if ("不动如山3".equals(character.getPassiveIntroduceThree())&&characters.getGoIntoNum()==3){
            int skillLevel = SkillLevelCalculator.getSkillLevel(lv.intValue());
//                453点生命上限，158点攻击，158点速度。
            if (Xtool.isNotNull(characters.getCollHp())){
                character.setHp(character.getHp()+skillLevel*characters.getCollHp());
                character.setMaxHp(maxHp.intValue()+skillLevel*characters.getCollHp());
            }
            if (Xtool.isNotNull(characters.getCollAttack())){
                character.setAttack(attack.intValue()+skillLevel*characters.getCollAttack());
            }
            if (Xtool.isNotNull(characters.getCollSpeed())){
                character.setSpeed(speed.intValue()+skillLevel*characters.getCollSpeed());
            }
        }

        if ("不动如山4".equals(character.getPassiveIntroduceThree())&&characters.getGoIntoNum()==4){
            int skillLevel = SkillLevelCalculator.getSkillLevel(lv.intValue());
//                453点生命上限，158点攻击，158点速度。
            if (Xtool.isNotNull(characters.getCollHp())){
                character.setHp(character.getHp()+skillLevel*characters.getCollHp());
                character.setMaxHp(maxHp.intValue()+skillLevel*characters.getCollHp());
            }
            if (Xtool.isNotNull(characters.getCollAttack())){
                character.setAttack(attack.intValue()+skillLevel*characters.getCollAttack());
            }
            if (Xtool.isNotNull(characters.getCollSpeed())){
                character.setSpeed(speed.intValue()+skillLevel*characters.getCollSpeed());
            }
        }

        if ("不动如山5".equals(character.getPassiveIntroduceThree())&&characters.getGoIntoNum()==5){
            int skillLevel = SkillLevelCalculator.getSkillLevel(lv.intValue());
//                453点生命上限，158点攻击，158点速度。
            if (Xtool.isNotNull(characters.getCollHp())){
                character.setHp(character.getHp()+skillLevel*characters.getCollHp());
                character.setMaxHp(maxHp.intValue()+skillLevel*characters.getCollHp());
            }
            if (Xtool.isNotNull(characters.getCollAttack())){
                character.setAttack(attack.intValue()+skillLevel*characters.getCollAttack());
            }
            if (Xtool.isNotNull(characters.getCollSpeed())){
                character.setSpeed(speed.intValue()+skillLevel*characters.getCollSpeed());
            }
        }


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
                Card card = cardMapper.selectByid(1030);
                if (card == null) {
                    baseResp.setErrorMsg("服务器异常联想管理员");
                    baseResp.setSuccess(0);
                    return baseResp;
                }
                Characters characters = new Characters();
                characters.setStackCount(0);
                characters.setGoIntoNum(0);
                characters.setId("1030");
                characters.setUserId(Integer.parseInt(userId));
                characters.setLv(1);
                characters.setCreateTime(new Date());
                characters.setMaxLv(CardMaxLevelUtils.getMaxLevel(card.getName(), card.getStar().doubleValue()));
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
                Card card = cardMapper.selectByid(1040);
                if (card == null) {
                    baseResp.setErrorMsg("服务器异常联想管理员");
                    baseResp.setSuccess(0);
                    return baseResp;
                }
                Characters characters = new Characters();
                characters.setStackCount(0);
                characters.setGoIntoNum(0);
                characters.setId("1040");
                characters.setUserId(Integer.parseInt(userId));
                characters.setLv(1);
                characters.setCreateTime(new Date());
                characters.setMaxLv(CardMaxLevelUtils.getMaxLevel(card.getName(), card.getStar().doubleValue()));
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

    public Battle battle(List<Characters> leftCharacters, Integer userId, String name0, List<Characters> rightCharacters, Integer toUserId, String name1, String img, String type) throws Exception {
        Map map = new HashMap();
        Integer isWin = 1;
        // 创建战斗缓存
        Map<String, BattleManager> battleCache = new HashMap<>();
        // 所有存活的角色
        List<Guardian> campA = new ArrayList<>();
        List<Character> copyCampA = new ArrayList<>();
        // 创建B队护法
        List<Guardian> campB = new ArrayList<>();
        List<Character> copyCampB = new ArrayList<>();
        leftCharacters.sort(Comparator.comparing(Characters::getGoIntoNum,
                Comparator.nullsFirst(Integer::compareTo)));
        for (Characters characters : leftCharacters) {
            // 设置角色
            Character character = reasonableData(characters, leftCharacters);
            campA.add(new Guardian(character.getName(), Camp.A, character.getGoIntoNum(), Profession.fromName(characters.getProfession()),
                    Race.fromName(characters.getCamp()), character.getMaxHp(), character.getAttack(), character.getSpeed(),character.getLv()));
            copyCampA.add(character);
        }
        rightCharacters.sort(Comparator.comparing(Characters::getGoIntoNum,
                Comparator.nullsFirst(Integer::compareTo)));
        for (Characters characters : rightCharacters) {
            // 设置角色
            Character character = reasonableData(characters, rightCharacters);
            campB.add(new Guardian(character.getName(), Camp.B, character.getGoIntoNum(), Profession.fromName(characters.getProfession()),
                    Race.fromName(characters.getCamp()), character.getMaxHp(), character.getAttack(), character.getSpeed(),character.getLv()));
            copyCampB.add(character);
        }
        BattleSnowflakeIdGenerator generator = BattleSnowflakeIdGenerator.getInstance();
        // 开始战斗
        String battleId = generator.generateBattleId();
        BattleManager battle = new BattleManager(battleId, campA, campB);
        battleCache.put(battleId, battle);
        battle.startBattle();
        List<BattleLog> logs = battle.getBattleLogs().stream().filter(x -> "BATTLE_END".equals(x.getEventType())).collect(Collectors.toList());
        BattleLog log = logs.get(0);
        // 精确匹配
        if (isTeamAVictoryAdvanced(log.getExtraDesc())) {
            isWin = 0;
        }
        // 打印优化后的日志
//        printFinalBattleLogs(battle.getBattleLogs());
        map.put("battleLogs", battle.getBattleLogs());
        map.put("campA", copyCampA);
        map.put("campB", copyCampB);
        map.put("name0", name0);
        map.put("name1", name1);
        map.put("isWin", isWin);
        GameFight fight = new GameFight();
        fight.setId(battleId);
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
        Battle bt = new Battle();
        bt.setIsWin(isWin);
        bt.setId(fight.getId());
        return bt;
    }

    public static boolean isTeamAVictoryAdvanced(String content) {
        if (content == null || content.isEmpty()) {
            return false;
        }
        // 正则表达式：匹配"A队胜利"，允许前后有任意空白字符（空格、制表符等）
        // 匹配规则可根据实际需求调整
        return content.matches(".*\\s*A队胜利\\s*.*");
    }

    @Override
    public BaseResp playBattle2(TokenDto token, HttpServletRequest request) throws Exception {
        // 创建战斗缓存
//        Map<String, BattleManager> battleCache = new HashMap<>();
//
//        // 创建A队护法
//        List<Guardian> campA = new ArrayList<>();
//        List<Character> copyCampA = new ArrayList<>();
//        campA.add(new Guardian("牛魔王", Camp.A, 1, Profession.WARRIOR, Race.DEMON, 2000, 300, 100));
//        campA.add(new Guardian("厚土娘娘", Camp.A, 2, Profession.IMMORTAL, Race.IMMORTAL, 2500, 200, 80));
//        campA.add(new Guardian("镇元子", Camp.A, 3, Profession.GOD, Race.DEMON, 1800, 250, 90));
//        campA.add(new Guardian("妲己", Camp.A, 4, Profession.IMMORTAL, Race.DEMON, 1500, 180, 120));
//        campA.add(new Guardian("长生大帝", Camp.A, 5, Profession.GOD, Race.IMMORTAL, 2200, 150, 80));
//
//        copyCampA.add(new Character("1027", "牛魔王", Camp.A, 1, Profession.WARRIOR, Race.DEMON, 2000, 300, 100));
//        copyCampA.add(new Character("1012", "厚土娘娘", Camp.A, 2, Profession.IMMORTAL, Race.IMMORTAL, 2500, 200, 80));
//        copyCampA.add(new Character("1016", "镇元子", Camp.A, 3, Profession.GOD, Race.IMMORTAL, 1800, 250, 90));
//        copyCampA.add(new Character("1005", "妲己", Camp.A, 4, Profession.IMMORTAL, Race.DEMON, 1500, 180, 120));
//        copyCampA.add(new Character("1020", "长生大帝", Camp.A, 5, Profession.GOD, Race.IMMORTAL, 2200, 150, 80));
//        // 创建B队护法
//        List<Guardian> campB = new ArrayList<>();
//        List<Character> copyCampB = new ArrayList<>();
//        campB.add(new Guardian("阎王", Camp.B, 1, Profession.GOD, Race.DEMON, 1500, 220, 85));
//        campB.add(new Guardian("聂小倩", Camp.B, 2, Profession.IMMORTAL, Race.DEMON, 1200, 180, 110));
//        campB.add(new Guardian("托塔天王", Camp.B, 3, Profession.GOD, Race.IMMORTAL, 2200, 280, 95));
//        campB.add(new Guardian("齐天大圣", Camp.B, 4, Profession.WARRIOR, Race.DEMON, 1800, 320, 130));
//        campB.add(new Guardian("铁扇公主", Camp.B, 5, Profession.IMMORTAL, Race.DEMON, 1600, 200, 90));
//
//        copyCampB.add(new Character("1035", "阎王", Camp.B, 1, Profession.GOD, Race.DEMON, 1500, 220, 85));
//        copyCampB.add(new Character("1007", "聂小倩", Camp.B, 2, Profession.IMMORTAL, Race.DEMON, 1200, 180, 110));
//        copyCampB.add(new Character("1002", "托塔天王", Camp.B, 3, Profession.GOD, Race.IMMORTAL, 2200, 280, 95));
//        copyCampB.add(new Character("1010", "齐天大圣", Camp.B, 4, Profession.WARRIOR, Race.DEMON, 1800, 320, 130));
//        copyCampB.add(new Character("1030", "洛神", Camp.B, 5, Profession.IMMORTAL, Race.IMMORTAL, 1600, 200, 90));
//
//        // 开始战斗
//        String battleId = "BATTLE_20251130_001";
//        BattleManager battle = new BattleManager(battleId, campA, campB);
//        battleCache.put(battleId, battle);
//        battle.startBattle();
//
//        // 打印优化后的日志
//        printFinalBattleLogs(battle.getBattleLogs());
        BaseResp baseResp = new BaseResp();
//        baseResp.setSuccess(1);
//        Map map = new HashMap();
//        map.put("campA", copyCampA);
//        map.put("campB", copyCampB);
//        map.put("battleLogs", battle.getBattleLogs());
//        baseResp.setData(map);
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
