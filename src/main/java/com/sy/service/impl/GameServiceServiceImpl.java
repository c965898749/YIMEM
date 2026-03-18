package com.sy.service.impl;

import cn.hutool.core.util.IdUtil;
import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.TypeReference;
import com.baomidou.mybatisplus.annotations.TableField;
import com.baomidou.mybatisplus.mapper.Wrapper;
import com.sy.mapper.game.*;
import com.sy.mapper.UserMapper;
import com.sy.model.PaymentRecord;
import com.sy.model.User;
import com.sy.model.game.*;
import com.sy.model.game.Character;
import com.sy.model.resp.BaseResp;
import com.sy.service.GameServiceService;
import com.sy.service.UserServic;
import com.sy.tool.*;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections.CollectionUtils;
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

import static com.sy.tool.Constants.*;


@Slf4j
@Service
public class GameServiceServiceImpl implements GameServiceService {
    @Autowired
    private UserMapper userMapper;
    @Autowired
    private AppVersionMapper appVersionMapper;
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
    @Autowired
    private FriendBlessingMapper friendBlessingMapper;
    @Autowired
    private ActivityBossMapper activityBossMapper;
    @Autowired
    private GameArenaSignupMapper gameArenaSignupMapper;
    @Autowired
    private GameArenaBattlecharactersMapper gameArenaBattlecharactersMapper;
    @Autowired
    private GameArenaBattleMapper gameArenaBattleMapper;
    @Autowired
    private GameArenaRankMapper gameArenaRankMapper;
    @Autowired
    private GameItemBaseMapper gameItemBaseMapper;
    @Autowired
    private GamePlayerBagExtMapper gamePlayerBagExtMapper;
    @Autowired
    private GamePlayerBagMapper gamePlayerBagMapper;
    @Autowired
    private GameItemPlayShopMapper gameItemPlayShopMapper;
    @Autowired
    private GameShopRecordMapper gameShopRecordMapper;
    @Autowired
    private EqCardMapper eqCardMapper;
    @Autowired
    private EqCharactersMapper eqCharactersMapper;
    @Autowired
    private EqCharactersRecordMapper eqCharactersRecordMapper;
    @Autowired
    private GameNoticeMapper gameNoticeMapper;
    @Autowired
    private PlayerBronzeTowerMapper playerBronzeTowerMapper;
    @Autowired
    private BronzeTowerMapper bronzeTowerMapper;
    @Autowired
    private BronzeBossDetailMapper bronzeBossDetailMapper;
    @Autowired
    private GameTimeRecordMapper gameTimeRecordMapper;
    @Autowired
    private MewYearItemShopMapper mewYearItemShopMapper;
    @Autowired
    private QqShenxianFlyupMapper qqShenxianFlyupMapper;
    @Autowired
    private QqShenxianPlayerFlyupMapper qqShenxianPlayerFlyupMapper;
    @Autowired
    private PillRobRecordMapper pillRobRecordMapper;
    @Autowired
    private RevengeRecordMapper revengeRecordMapper;
    @Autowired
    private GameEqRecordMapper gameEqRecordMapper;
    @Autowired
    private PveRewardRecordMapper pveRewardRecordMapper;
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
            baseResp.setErrorMsg("账号已被封禁");
            return baseResp;
        }
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
        List<EqCharacters> eqCharactersList = eqCharactersMapper.selectByUserId(emp.getUserId());
        info.setBronze(0);
        info.setDarkSteel(0);
        info.setPurpleGold(0);
        info.setCrystal(0);
        GamePlayerBag playerBag = gamePlayerBagMapper.goIntoListByIdAndItemId(emp.getUserId() + "", 13);
        if (playerBag != null) {
            info.setBronze(playerBag.getItemCount());
        }
        GamePlayerBag playerBag1 = gamePlayerBagMapper.goIntoListByIdAndItemId(emp.getUserId() + "", 14);
        if (playerBag1 != null) {
            info.setDarkSteel(playerBag1.getItemCount());
        }
        GamePlayerBag playerBag2 = gamePlayerBagMapper.goIntoListByIdAndItemId(emp.getUserId() + "", 15);
        if (playerBag2 != null) {
            info.setPurpleGold(playerBag2.getItemCount());
        }
        GamePlayerBag playerBag3 = gamePlayerBagMapper.goIntoListByIdAndItemId(emp.getUserId() + "", 16);
        if (playerBag3 != null) {
            info.setCrystal(playerBag3.getItemCount());
        }
        //卡池数量
        List<Card> cardList = cardMapper.selectAll();
        info.setUseCardCount(cardList.size() + "");
        info.setCharacterList(formateCharacter(characterList));
        info.setEqCharactersList(formateEqCharacter(eqCharactersList));
        String token = IdUtil.simpleUUID();
        info.setToken(token);
        ValueOperations opsForValue = redisTemplate.opsForValue();
        opsForValue.set(token, emp.getUserId() + "", 2592000, TimeUnit.SECONDS);
        emp.setToken(token);
        emp.setLoginTime(new Date());
        if (Xtool.isNull(emp.getMyCode())) {
            InviteCodeGenerator generator = InviteCodeGenerator.getInstance();
            emp.setMyCode(generator.generateInviteCode(12));
        }
        userMapper.updateuser(emp);
        baseResp.setData(info);
        baseResp.setErrorMsg("登录成功");
        return baseResp;
    }

    @Override
    public BaseResp isTrue(TokenDto token, HttpServletRequest request) throws Exception {
        BaseResp baseResp = new BaseResp();
        if (token == null || Xtool.isNull(token.getToken())) {
            baseResp.setSuccess(0);
            baseResp.setErrorMsg("登录过期");
            return baseResp;
        }

        if (token == null || Xtool.isNull(token.getUserId())) {
            baseResp.setSuccess(0);
            baseResp.setErrorMsg("登录过期");
            return baseResp;
        }
        User emp = userMapper.selectUserByUserId(Integer.parseInt(token.getUserId()));
        if (emp == null) {
            baseResp.setSuccess(0);
            baseResp.setErrorMsg("登录过期");
            return baseResp;
        }
        //5、查看状态，如果为已禁用状态，则返回员工已禁用结果
        if (emp.getStatus() == 0) {
            baseResp.setSuccess(0);
            baseResp.setErrorMsg("账号已被封禁");
            return baseResp;
        }
        //再判断收否过期
        if (!token.getToken().equals(emp.getToken())) {
            baseResp.setSuccess(0);
            baseResp.setErrorMsg("登录过期");
            return baseResp;
        }
        baseResp.setSuccess(1);
        return baseResp;
    }

    @Override
    public BaseResp gameVersion(TokenDto token, HttpServletRequest request) throws Exception {
        AppVersion list = appVersionMapper.selectListLast();
        BaseResp baseResp = new BaseResp();
        baseResp.setSuccess(1);
        baseResp.setData(list);
        return baseResp;
    }

    //格式化卡牌
    public List<Character> formateCharacter(List<Characters> characterList) {
        List<Character> characterArrayList = new ArrayList<>();
        List<Characters> charactersList1 = characterList.stream().filter(x -> x.getGoIntoNum() != 0).collect(Collectors.toList());
        List<Characters> charactersList2 = characterList.stream().filter(x -> x.getGoIntoNum() == 0).collect(Collectors.toList());
        for (Characters characters : charactersList1) {
            Character character = reasonableData(characters, charactersList1);
            characterArrayList.add(character);
        }
        characterArrayList.addAll(reasonableData2(charactersList2));
        for (Characters characters : charactersList2) {

        }
        return characterArrayList;
    }

    //格式化装备
    public List<EqCharacters> formateEqCharacter(List<EqCharacters> characterList) {
        for (EqCharacters eqCharacters : characterList) {
            eqCharacters.setWlAtk(eqCharacters.getWlAtk() * eqCharacters.getLv());
            eqCharacters.setHyAtk(eqCharacters.getHyAtk() * eqCharacters.getLv());
            eqCharacters.setFdAtk(eqCharacters.getFdAtk() * eqCharacters.getLv());
            eqCharacters.setDsAtk(eqCharacters.getDsAtk() * eqCharacters.getLv());
            eqCharacters.setWlDef(eqCharacters.getWlDef() * eqCharacters.getLv());
            eqCharacters.setHyDef(eqCharacters.getHyDef() * eqCharacters.getLv());
            eqCharacters.setDsDef(eqCharacters.getDsDef() * eqCharacters.getLv());
            eqCharacters.setFdDef(eqCharacters.getFdDef() * eqCharacters.getLv());
            eqCharacters.setZlDef(eqCharacters.getZlDef() * eqCharacters.getLv());
        }
        return characterList;
    }

    @Override
    @Transactional
    @NoRepeatSubmit(limitSeconds = 1)
    public BaseResp registerGame(User user2, HttpServletRequest request) throws Exception {
        BaseResp baseResp = new BaseResp();
        try {
            if (user2 == null) {
                baseResp.setSuccess(0);
                baseResp.setErrorMsg("请输入账号和密码");
                return baseResp;
            }
            if (Xtool.isNull(user2.getUsername()) || Xtool.isNull(user2.getUserpassword())) {
                baseResp.setSuccess(0);
                baseResp.setErrorMsg("请输入账号和密码");
                return baseResp;
            }
            if (Xtool.isNull(user2.getYaoCode()) || Xtool.isNull(user2.getYaoCode())) {
                baseResp.setSuccess(0);
                baseResp.setErrorMsg("请输入邀请码");
                return baseResp;
            }
            List<User> users = userMapper.selectUserByYaoCode(user2.getYaoCode());
            if (Xtool.isNull(users)) {
                baseResp.setSuccess(0);
                baseResp.setErrorMsg("邀请码不正确");
                return baseResp;
            }
            User user = new User();
            user.setUsername(user2.getUsername());
            String password = DigestUtils.md5DigestAsHex(user2.getUserpassword().getBytes());
            user.setUserpassword(password);
            List<User> userList = userMapper.SelectAllUser();
            List<String> usernamelist = new ArrayList<>();
            for (User user1 : userList) {
                usernamelist.add(user1.getUsername());
            }
            if (usernamelist.contains(user2.getUsername())) {
                baseResp.setSuccess(0);
                baseResp.setErrorMsg("您输入的账号已存在，请重新输入");
                return baseResp;
            } else {
                //设置昵称
                String nickname = null;
                Integer flag = 1;
                Integer len = 4;
                userList.clear();
                for (User user1 : userList) {
                    usernamelist.add(user1.getNickname());
                }
                while (flag != 0) {
                    nickname = RandomName.randomName(false, len);
                    if (!usernamelist.contains(nickname)) {
                        flag = 0;
                    } else {
                        flag++;
                        if (flag > 1100000000 && flag < 2100000000) {
                            len++;
                            flag = 1;
                        }
                    }
                }
                user.setNickname(nickname);
                int max = 6, min = 1;
                int ran2 = (int) (Math.random() * (max - min) + min);
                String url = "/imgs/headimg/" + ran2 + ".jpg";
                user.setHeadImg(url);
                user.setDownloadmoney((double) 0);
                user.setRanking(9999);
                user.setGameRanking(9999);
                user.setLevel(2);
                user.setCollectCount(0);
                user.setBlogCount(0);
                user.setAttentionCount(0);
                user.setFansCount(0);
                user.setResourceCount(0);
                user.setForumCount(0);
                user.setAskCount(0);
                user.setCommentCount(0);
                user.setLikeCount(0);
                user.setVisitorCount(0);
                user.setDownCount(0);
                user.setUnreadreplaycount(0);
                user.setReadquerylikecount(0);
                user.setUnreadfanscount(0);
                user.setIsEmil("0");
                user.setStatus(1);
                InviteCodeGenerator generator = InviteCodeGenerator.getInstance();
                user.setMyCode(generator.generateInviteCode(12));
                user.setYaoCode(user2.getYaoCode());
                int result = userMapper.insertUser(user);
                if (result <= 0) {
                    baseResp.setSuccess(0);
                    baseResp.setErrorMsg("注册失败！");
                    return baseResp;
                }
            }
            Card card = cardMapper.selectByid(1002);
            if (card == null) {
                baseResp.setErrorMsg("服务器异常联想管理员");
                baseResp.setSuccess(0);
                return baseResp;
            }
            User emp = userMapper.selectUserByusername(user.getUsername());
            Characters characters = new Characters();
            BeanUtils.copyProperties(card, characters);
            characters.setId("1002");
            characters.setLv(1);
            characters.setGoIntoNum(1);
            characters.setStackCount(0);
            characters.setUserId(Integer.parseInt(emp.getUserId() + ""));
            characters.setStar(new BigDecimal(1));
            characters.setMaxLv(CardMaxLevelUtils.getMaxLevel(card.getName(), card.getStar().doubleValue()));
            charactersMapper.insert2(characters);
            baseResp.setSuccess(1);
            baseResp.setErrorMsg("注册成功！");
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
        //5、查看状态，如果为已禁用状态，则返回员工已禁用结果
        if (user.getStatus() == 0) {
            baseResp.setSuccess(0);
            baseResp.setErrorMsg("账号已被封禁");
            return baseResp;
        }

        //再判断收否过期
        if (!token.getToken().equals(user.getToken())) {
            baseResp.setSuccess(0);
            baseResp.setErrorMsg("登录过期");
            return baseResp;
        }
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
//        private Integer bronze;
//        private Integer darkSteel;
//        private Integer purpleGold;
//        private Integer crystal;
        List<Characters> characterList = charactersMapper.selectByUserId(user.getUserId());
        List<EqCharacters> characterEqList = eqCharactersMapper.selectByUserId(user.getUserId());
        info.setBronze(0);
        info.setDarkSteel(0);
        info.setPurpleGold(0);
        info.setCrystal(0);
        GamePlayerBag playerBag = gamePlayerBagMapper.goIntoListByIdAndItemId(userId, 13);
        if (playerBag != null) {
            info.setBronze(playerBag.getItemCount());
        }
        GamePlayerBag playerBag1 = gamePlayerBagMapper.goIntoListByIdAndItemId(userId, 14);
        if (playerBag1 != null) {
            info.setDarkSteel(playerBag1.getItemCount());
        }
        GamePlayerBag playerBag2 = gamePlayerBagMapper.goIntoListByIdAndItemId(userId, 15);
        if (playerBag2 != null) {
            info.setPurpleGold(playerBag2.getItemCount());
        }
        GamePlayerBag playerBag3 = gamePlayerBagMapper.goIntoListByIdAndItemId(userId, 16);
        if (playerBag3 != null) {
            info.setCrystal(playerBag3.getItemCount());
        }
        info.setEqCharactersList(formateEqCharacter(characterEqList));
        //卡池数量
        List<Card> cardList = cardMapper.selectAll();
        info.setUseCardCount(cardList.size() + "");
        info.setCharacterList(formateCharacter(characterList));
        baseResp.setData(info);
        baseResp.setErrorMsg("更新成功");
        return baseResp;
    }

    @Override
    public BaseResp updateTli(TokenDto token, HttpServletRequest request) throws Exception {
        BaseResp baseResp = new BaseResp();
        if (token == null || Xtool.isNull(token.getToken())) {
            baseResp.setSuccess(0);
            baseResp.setErrorMsg("登录过期");
            return baseResp;
        }
        String userId = token.getUserId();
        if (Xtool.isNull(userId)) {
            baseResp.setSuccess(0);
            baseResp.setErrorMsg("登录过期");
            return baseResp;
        }
        if (Xtool.isNull(token.getTiLi())) {
            baseResp.setSuccess(0);
            baseResp.setErrorMsg("游戏异常，请注销重新登录");
            return baseResp;
        }
        User user = userMapper.selectUserByUserId(Integer.parseInt(userId));
        user.setTiliCount(token.getTiLi());
        user.setTiliCountTime(new Date(Long.parseLong(token.getStr())));
        userMapper.updateuserTili(user);
        baseResp.setSuccess(1);
        baseResp.setErrorMsg("同步成攻");
        return baseResp;
    }

    @Override
    public BaseResp updateTli3(TokenDto token, HttpServletRequest request) throws Exception {
        BaseResp baseResp = new BaseResp();
        if (token == null || Xtool.isNull(token.getToken())) {
            baseResp.setSuccess(0);
            baseResp.setErrorMsg("登录过期");
            return baseResp;
        }
        String userId = token.getUserId();
        if (Xtool.isNull(userId)) {
            baseResp.setSuccess(0);
            baseResp.setErrorMsg("登录过期");
            return baseResp;
        }
        if (Xtool.isNull(token.getHuoLi())) {
            baseResp.setSuccess(0);
            baseResp.setErrorMsg("游戏异常，请注销重新登录");
            return baseResp;
        }
        User user = userMapper.selectUserByUserId(Integer.parseInt(userId));
        user.setHuoliCount(token.getHuoLi());
        user.setHuoliCountTime(new Date(Long.parseLong(token.getStr())));
        userMapper.updateuserHuoli(user);
        baseResp.setSuccess(1);
        baseResp.setErrorMsg("同步成攻");
        return baseResp;
    }

    @Override
    public BaseResp updateTli2(TokenDto token, HttpServletRequest request) throws Exception {
        BaseResp baseResp = new BaseResp();
        if (token == null || Xtool.isNull(token.getToken())) {
            baseResp.setSuccess(0);
            baseResp.setErrorMsg("登录过期");
            return baseResp;
        }
        String userId = token.getUserId();
        if (Xtool.isNull(userId)) {
            baseResp.setSuccess(0);
            baseResp.setErrorMsg("登录过期");
            return baseResp;
        }
        User user = userMapper.selectUserByUserId(Integer.parseInt(userId));
        UserInfo userInfo = new UserInfo();
        BeanUtils.copyProperties(user, userInfo);
        baseResp.setSuccess(1);
        baseResp.setErrorMsg("同步成攻");
        baseResp.setData(userInfo);
        return baseResp;
    }

    @Override
    @Transactional
    public BaseResp changeState(TokenDto token, HttpServletRequest request) throws Exception {
        BaseResp baseResp = new BaseResp();
        if (token == null || Xtool.isNull(token.getToken())) {
            baseResp.setSuccess(0);
            baseResp.setErrorMsg("登录过期");
            return baseResp;
        }
        String userId = token.getUserId();
//        String userId = (String) redisTemplate.opsForValue().get(token.getToken());

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
        info.setCharacterList(formateCharacter(characterList));
        baseResp.setData(info);
        baseResp.setErrorMsg("更新成功");
        return baseResp;
    }

    @Override
    public BaseResp changeEqState(TokenDto token, HttpServletRequest request) throws Exception {
        BaseResp baseResp = new BaseResp();
        if (token == null || Xtool.isNull(token.getToken())) {
            baseResp.setSuccess(0);
            baseResp.setErrorMsg("登录过期");
            return baseResp;
        }
        String userId = token.getUserId();
//        String userId = (String) redisTemplate.opsForValue().get(token.getToken());

        if (Xtool.isNull(userId)) {
            baseResp.setSuccess(0);
            baseResp.setErrorMsg("登录过期");
            return baseResp;
        }
        User user = userMapper.selectUserByUserId(Integer.parseInt(userId));
        eqCharactersMapper.changeEqState(userId, token.getId());
        UserInfo info = new UserInfo();
        BeanUtils.copyProperties(user, info);
        List<EqCharacters> eqCharactersList = eqCharactersMapper.selectByUserId(Integer.parseInt(token.getUserId()));
        info.setEqCharactersList(formateEqCharacter(eqCharactersList));
        baseResp.setData(info);
        baseResp.setSuccess(1);
        baseResp.setErrorMsg("更新成功");
        return baseResp;
    }

    @Override
    @Transactional
    public BaseResp changeEqState2(TokenDto token, HttpServletRequest request) throws Exception {
        BaseResp baseResp = new BaseResp();
        if (token == null || Xtool.isNull(token.getToken())) {
            baseResp.setSuccess(0);
            baseResp.setErrorMsg("登录过期");
            return baseResp;
        }
        String userId = token.getUserId();
//        String userId = (String) redisTemplate.opsForValue().get(token.getToken());

        if (Xtool.isNull(userId)) {
            baseResp.setSuccess(0);
            baseResp.setErrorMsg("登录过期");
            return baseResp;
        }
        User user = userMapper.selectUserByUserId(Integer.parseInt(userId));
        //判断是否是该职业装备
        Characters characters = charactersMapper.listById(user.getUserId() + "", token.getStr());
        if (characters == null) {
            baseResp.setSuccess(0);
            baseResp.setErrorMsg("英雄不存在");
            return baseResp;
        }
        EqCharacters eqCharacters = eqCharactersMapper.listById(user.getUserId() + "", token.getId());
        if (eqCharacters == null) {
            baseResp.setSuccess(0);
            baseResp.setErrorMsg("装备不存在");
            return baseResp;
        }
        if (!characters.getCamp().equals(eqCharacters.getCamp()) || !characters.getProfession().equals(eqCharacters.getProfession())) {
            baseResp.setSuccess(0);
            baseResp.setErrorMsg("装备和护法的种族职业不一致");
            return baseResp;
        }
        eqCharactersMapper.changeEqState2(userId, token.getId(), token.getStr());
        UserInfo info = new UserInfo();
        BeanUtils.copyProperties(user, info);
        List<EqCharacters> eqCharactersList = eqCharactersMapper.selectByUserId(Integer.parseInt(token.getUserId()));
        info.setEqCharactersList(formateEqCharacter(eqCharactersList));
//        List<Characters> characterList = charactersMapper.selectByUserId(user.getUserId());
//        info.setCharacterList(formateCharacter(characterList));
        baseResp.setData(info);
        baseResp.setSuccess(1);
        baseResp.setErrorMsg("更新成功");
        return baseResp;
    }

    @Override
    @Transactional
    @NoRepeatSubmit(limitSeconds = 1)
    public BaseResp changeName(TokenDto token, HttpServletRequest request) throws Exception {
        BaseResp baseResp = new BaseResp();
        if (token == null || Xtool.isNull(token.getToken())) {
            baseResp.setSuccess(0);
            baseResp.setErrorMsg("登录过期");
            return baseResp;
        }
        String userId = token.getUserId();
//        String userId = (String) redisTemplate.opsForValue().get(token.getToken());
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
        List<ActivityConfig> activityConfigList2 = new ArrayList<>();
        //再判断今天有没有活动
        // 获取当前日期
        LocalDate today = LocalDate.now();
        // 获取星期几（返回DayOfWeek枚举）
        DayOfWeek dayOfWeek = today.getDayOfWeek();
        for (ActivityConfig activityConfig : activityConfigList) {
            if (activityConfig.getIsNotice() == 0 && activityConfig.getIsPermanent() == 1) {
                List<ActivityDetail> details = activityDetailMapper.getByCodde2(activityConfig.getActivityCode(), dayOfWeek.getValue());
                if (Xtool.isNull(details)) {
                    continue;
                }
            }
            activityConfigList2.add(activityConfig);
        }
        BaseResp baseResp = new BaseResp();
        baseResp.setSuccess(1);
        baseResp.setData(activityConfigList2);
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
//        String userId = (String) redisTemplate.opsForValue().get(token.getToken());
        String userId = token.getUserId();
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
//        String userId = (String) redisTemplate.opsForValue().get(token.getToken());
        String userId = token.getUserId();
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
        User user=userMapper.selectUserByUserId(Integer.parseInt(userId));
        List<String> list = Arrays.asList(token.getId().split("-"));
        Integer num1 = Integer.parseInt(list.get(0));
        Integer num2 = Integer.parseInt(list.get(1));
        List<String> list2 = Arrays.asList(user.getChapter().split("-"));
        Integer num11 = Integer.parseInt(list2.get(0));
        Integer num22 = Integer.parseInt(list2.get(1));
        PveDetail pveDetail =new PveDetail();
        if (num1==num11&&num2==num22){
            pveDetail = pveDetailMapper.selectById(user.getChapter());
        }else {
            pveDetail = pveDetailMapper.selectById(token.getId());
        }
        pveDetail.setBaoCount(user.getBaoCount());
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
        Map itemMap = new HashMap();
        itemMap.put("item_id", "28");
        itemMap.put("user_id", userId);
        itemMap.put("is_delete", "0");
        List<GamePlayerBag> playerBagList = gamePlayerBagMapper.selectByMap(itemMap);
        if (Xtool.isNotNull(playerBagList)) {
            pveDetail.setNum(playerBagList.get(0).getItemCount());
        } else {
            pveDetail.setNum(0);
        }
        baseResp.setSuccess(1);
        baseResp.setData(pveDetail);
        baseResp.setErrorMsg("更新成功");
        return baseResp;
    }

    @Override
    @Transactional
    @NoRepeatSubmit(limitSeconds = 3)
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
        if (user.getTiliCount() - 2 < 0) {
            baseResp.setSuccess(0);
            baseResp.setErrorMsg("体力不足");
            return baseResp;
        }
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
        for (Characters characters : leftCharacter) {
            List<EqCharacters> eqCharacters = eqCharactersMapper.listByGoOn(user.getUserId() + "", characters.getId());
            if (Xtool.isNotNull(eqCharacters)) {
                characters.setEqCharactersList(formateEqCharacter(eqCharacters));
            }
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
                } else if ("5".equals(content.getRewardType() + "") || "6".equals(content.getRewardType() + "")) {
                    Map itemMap = new HashMap();
                    itemMap.put("item_id", content.getItemId());
                    itemMap.put("user_id", userId);
                    itemMap.put("is_delete", "0");
                    List<GamePlayerBag> playerBagList = gamePlayerBagMapper.selectByMap(itemMap);
                    if (Xtool.isNotNull(playerBagList)) {
                        GamePlayerBag playerBag = playerBagList.get(0);
                        playerBag.setItemCount(playerBag.getItemCount() + content.getRewardAmount());
                        gamePlayerBagMapper.updateById(playerBag);
                    } else {
                        GamePlayerBag playerBag = new GamePlayerBag();
                        playerBag.setUserId(Integer.parseInt(userId));
                        playerBag.setItemCount(content.getRewardAmount());
                        playerBag.setGridIndex(1);
                        playerBag.setItemId(content.getItemId());
                        gamePlayerBagMapper.insert(playerBag);
                    }
                }
            }
            map.put("rewards", rewardList);
        }
        user.setTiliCount(user.getTiliCount() - 2);
        userMapper.updateuser(user);
        UserInfo userInfo = new UserInfo();
        BeanUtils.copyProperties(user, userInfo);
        List<Characters> characterList = charactersMapper.selectByUserId(user.getUserId());
        userInfo.setCharacterList(formateCharacter(characterList));
        userInfo.setLevelUp(levelUp);
        map.put("levelUp", levelUp);
        map.put("user", userInfo);
        map.put("battle", battle);
        baseResp.setData(map);
        baseResp.setSuccess(1);
        return baseResp;
    }

    @Override
    @Transactional
    @NoRepeatSubmit(limitSeconds = 3)
    public BaseResp participate2(TokenDto token, HttpServletRequest request) throws Exception {
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
        if (user.getTiliCount() - 2 < 0) {
            baseResp.setSuccess(0);
            baseResp.setErrorMsg("体力不足");
            return baseResp;
        }
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
        for (Characters characters : leftCharacter) {
            List<EqCharacters> eqCharacters = eqCharactersMapper.listByGoOn(user.getUserId() + "", characters.getId());
            if (Xtool.isNotNull(eqCharacters)) {
                characters.setEqCharactersList(formateEqCharacter(eqCharacters));
            }
        }
        List<Characters> rightCharacter = new ArrayList<>();
        Map map1 = new HashMap();
        map1.put("detail_code", activityDetail.getDetailCode());
        List<ActivityBoss> bosses = activityBossMapper.selectByMap(map1);
        for (ActivityBoss boss : bosses) {
            Card card = cardMapper.selectByid(boss.getBossId());
            Characters character = new Characters();
            BeanUtils.copyProperties(card, character);
            character.setGoIntoNum(boss.getGoIntoNum());
            character.setLv(Integer.parseInt(boss.getDifficultyLevel()));
            rightCharacter.add(character);
        }
        DifficultyLevel level3 = DifficultyLevel.getByCode(activityDetail.getDifficultyLevel());
        Battle battle = this.battle(leftCharacter, Integer.parseInt(userId), user.getNickname(), rightCharacter, 0, activityDetail.getBossName() + "(" + level3.getName() + ")", user.getGameImg(), "0");
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
                } else if ("5".equals(content.getRewardType() + "") || "6".equals(content.getRewardType() + "")) {
                    Map itemMap = new HashMap();
                    itemMap.put("item_id", content.getItemId());
                    itemMap.put("user_id", userId);
                    itemMap.put("is_delete", "0");
                    List<GamePlayerBag> playerBagList = gamePlayerBagMapper.selectByMap(itemMap);
                    if (Xtool.isNotNull(playerBagList)) {
                        GamePlayerBag playerBag = playerBagList.get(0);
                        playerBag.setItemCount(playerBag.getItemCount() + content.getRewardAmount());
                        gamePlayerBagMapper.updateById(playerBag);
                    } else {
                        GamePlayerBag playerBag = new GamePlayerBag();
                        playerBag.setUserId(Integer.parseInt(userId));
                        playerBag.setItemCount(content.getRewardAmount());
                        playerBag.setGridIndex(1);
                        playerBag.setItemId(content.getItemId());
                        gamePlayerBagMapper.insert(playerBag);
                    }
                }
            }
            map.put("rewards", rewardList);
        }
        user.setTiliCount(user.getTiliCount() - 2);
        userMapper.updateuser(user);
        UserInfo userInfo = new UserInfo();
        BeanUtils.copyProperties(user, userInfo);
        userInfo.setLevelUp(levelUp);
        List<Characters> characterList = charactersMapper.selectByUserId(user.getUserId());
        userInfo.setCharacterList(formateCharacter(characterList));
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
                if ("105".equals(characterCong.getId())) {
                    materials.add(new MaterialCard(1, 5000));
                } else {
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
    @Transactional(rollbackFor = Exception.class) // 明确指定所有异常都回滚
    @NoRepeatSubmit(limitSeconds = 5)
    public BaseResp xina(TokenDto token, HttpServletRequest request) throws Exception {
        BaseResp baseResp = new BaseResp();
        // 1. 基础参数校验
        if (token == null || Xtool.isNull(token.getToken()) || Xtool.isNull(token.getUserId()) || Xtool.isNull(token.getId())) {
            baseResp.setSuccess(0);
            baseResp.setErrorMsg("登录过期");
            return baseResp;
        }

        String userId = token.getUserId();
        User user = userMapper.selectUserByUserId(Integer.parseInt(userId));
        if (user == null) { // 增加用户存在性校验
            baseResp.setSuccess(0);
            baseResp.setErrorMsg("用户不存在");
            return baseResp;
        }
        // 5.2 金币校验
        if (user.getGold().compareTo(new BigDecimal(100000)) < 0) {
            baseResp.setSuccess(0);
            baseResp.setErrorMsg("飞升金币不足");
            return baseResp;
        }
        user.setGold(user.getGold().subtract(new BigDecimal(100000)));
        // 2. 查询角色信息（建议用selectByIdForUpdate加行锁，防止并发修改）
        Characters character = charactersMapper.listById(token.getUserId(), token.getId());
        if (character == null) {
            baseResp.setSuccess(1);
            baseResp.setErrorMsg("目标卡不存在");
            return baseResp;
        }

        Characters character2 = charactersMapper.listById(token.getUserId(), token.getStr());
        if (character2 == null) {
            baseResp.setSuccess(1);
            baseResp.setErrorMsg("吸纳卡不存在");
            return baseResp;
        }
        Map map2=new HashMap();
        map2.put("user_id",userId);
        map2.put("item_id",31);
        map2.put("is_delete",0);
        List<GamePlayerBag> playerBags=gamePlayerBagMapper.selectByMap(map2);
        if (Xtool.isNull(playerBags)){
            baseResp.setSuccess(0);
            baseResp.setErrorMsg("吸纳券不足");
            return baseResp;
        }
        GamePlayerBag gamePlayerBag=playerBags.get(0);
        if (gamePlayerBag.getItemCount()-1<0) {
            baseResp.setSuccess(0);
            baseResp.setErrorMsg("吸纳券不足");
            return baseResp;
        }
        // 扣减物品数量
        if (gamePlayerBag.getItemCount() - 1 > 0) {
            gamePlayerBag.setItemCount(gamePlayerBag.getItemCount() - 1);
        } else {
            gamePlayerBag.setIsDelete("1");
        }
        gamePlayerBagMapper.updateById(gamePlayerBag);
        if (character2.getLv()>character.getMaxLv()){
            character.setLv(character.getMaxLv());
        }else {
            character.setLv(character2.getLv());
        }
        charactersMapper.updateByPrimaryKeySelective(character); // 改为选择性更新，只更新有值的字段
        character2.setLv(1);
        character2.setExp(5);
        charactersMapper.updateByPrimaryKeySelective(character2); // 改为选择性更新，只更新有值的字段
        userMapper.updateuser(user);
        List<Characters> characterList = charactersMapper.selectByUserId(user.getUserId());
        UserInfo info = new UserInfo();
        BeanUtils.copyProperties(user, info);
        info.setCharacterList(formateCharacter(characterList));
        baseResp.setSuccess(1);
        baseResp.setData(info);
        baseResp.setErrorMsg("更新成功");
        return baseResp;
    }

    @Override
    public BaseResp cardFlyUp(TokenDto token, HttpServletRequest request) throws Exception {
        BaseResp baseResp = new BaseResp();
        if (token == null || Xtool.isNull(token.getToken())) {
            baseResp.setSuccess(1);
            baseResp.setErrorMsg("更新成功");
            return baseResp;
        }
//        String userId = (String) redisTemplate.opsForValue().get(token.getToken());
        String userId = token.getUserId();
        if (Xtool.isNull(userId)) {
            baseResp.setSuccess(1);
            baseResp.setErrorMsg("更新成功");
            return baseResp;
        }
        String Id = token.getId();
        if (Xtool.isNull(Id)) {
            baseResp.setSuccess(1);
            baseResp.setErrorMsg("更新成功");
            return baseResp;
        }
        Characters character = charactersMapper.listById(token.getUserId(), token.getId());
        if (character == null) {
            baseResp.setSuccess(1);
            baseResp.setErrorMsg("更新成功");
            return baseResp;
        }
        if (character.getFlyup() == 10) {
            baseResp.setSuccess(1);
            baseResp.setErrorMsg("更新成功");
            return baseResp;
        }
        List<QqShenxianFlyup> qqShenxianFlyupList = qqShenxianFlyupMapper.selectByMap(new HashMap<>());
        Map data = new HashMap();
        List<QqShenxianFlyup> qqShenxianFlyups = qqShenxianFlyupList.stream().filter(x -> x.getFlyupTimes() == character.getFlyup() + 1).collect(Collectors.toList());
        if (character.getProfession().equals("武圣")) {
            Map map = new HashMap();
            map.put("user_id", token.getUserId());
            map.put("item_id", 21);
            map.put("is_delete", 0);
            List<GamePlayerBag> playerBags = gamePlayerBagMapper.selectByMap(map);
            if (Xtool.isNotNull(playerBags)) {
                data.put("dangyaoTotal", playerBags.get(0).getItemCount());
            } else {
                data.put("dangyaoTotal", 0);
            }
        } else if (character.getProfession().equals("神将")) {
            Map map = new HashMap();
            map.put("user_id", token.getUserId());
            map.put("item_id", 23);
            map.put("is_delete", 0);
            List<GamePlayerBag> playerBags = gamePlayerBagMapper.selectByMap(map);
            if (Xtool.isNotNull(playerBags)) {
                data.put("dangyaoTotal", playerBags.get(0).getItemCount());
            } else {
                data.put("dangyaoTotal", 0);
            }
        } else {
            Map map = new HashMap();
            map.put("user_id", token.getUserId());
            map.put("item_id", 21);
            map.put("is_delete", 0);
            List<GamePlayerBag> playerBags = gamePlayerBagMapper.selectByMap(map);
            if (Xtool.isNotNull(playerBags)) {
                data.put("dangyaoTotal", playerBags.get(0).getItemCount());
            } else {
                data.put("dangyaoTotal", 0);
            }
        }
        QqShenxianFlyup flyup = qqShenxianFlyups.get(0);
        data.put("dangyaoTotal2", flyup.getCurrentConsume());
        data.put("cardTotal", character.getStackCount());
        data.put("cardTotal2", character.getFlyup() + 1);
        data.put("gold", flyup.getGold());
        baseResp.setSuccess(1);
        baseResp.setData(data);
        baseResp.setErrorMsg("更新成功");
        return baseResp;
    }

    @Override
    @Transactional(rollbackFor = Exception.class) // 明确指定所有异常都回滚
    @NoRepeatSubmit(limitSeconds = 5)
    public BaseResp cardFlyUp2(TokenDto token, HttpServletRequest request) throws Exception {
        BaseResp baseResp = new BaseResp();
        // 1. 基础参数校验
        if (token == null || Xtool.isNull(token.getToken()) || Xtool.isNull(token.getUserId()) || Xtool.isNull(token.getId())) {
            baseResp.setSuccess(0);
            baseResp.setErrorMsg("登录过期");
            return baseResp;
        }

        String userId = token.getUserId();
        User user = userMapper.selectUserByUserId(Integer.parseInt(userId));
        if (user == null) { // 增加用户存在性校验
            baseResp.setSuccess(0);
            baseResp.setErrorMsg("用户不存在");
            return baseResp;
        }

        // 2. 查询角色信息（建议用selectByIdForUpdate加行锁，防止并发修改）
        Characters character = charactersMapper.listById(token.getUserId(), token.getId());
        if (character == null) {
            baseResp.setSuccess(1);
            baseResp.setErrorMsg("飞升主卡不存在");
            return baseResp;
        }

        // 3. 飞升前置条件校验
        if (character.getLv() < character.getMaxLv()) {
            baseResp.setSuccess(0);
            baseResp.setErrorMsg("飞升需主卡达到满级");
            return baseResp;
        }
        if (character.getFlyup() >= 10) { // 改为>=，防止越界
            baseResp.setSuccess(0);
            baseResp.setErrorMsg("最多飞升10次");
            return baseResp;
        }

        // 4. 获取飞升配置（增加空值校验）
        List<QqShenxianFlyup> qqShenxianFlyupList = qqShenxianFlyupMapper.selectByMap(new HashMap<>());
        int targetFlyupTimes = character.getFlyup() + 1;
        List<QqShenxianFlyup> qqShenxianFlyups = qqShenxianFlyupList.stream()
                .filter(x -> x.getFlyupTimes() == targetFlyupTimes)
                .collect(Collectors.toList());
        if (CollectionUtils.isEmpty(qqShenxianFlyups)) { // 使用Spring的CollectionUtils做空判断
            baseResp.setSuccess(0);
            baseResp.setErrorMsg("飞升配置不存在");
            return baseResp;
        }
        QqShenxianFlyup flyup = qqShenxianFlyups.get(0);

        // 5. 校验飞升材料
        // 5.1 从卡数量校验
        if (character.getStackCount() < flyup.getFlyupTimes()) {
            baseResp.setSuccess(0);
            baseResp.setErrorMsg("飞升从卡不足");
            return baseResp;
        }
        // 5.2 金币校验
        if (user.getGold().compareTo(new BigDecimal(flyup.getGold())) < 0) {
            baseResp.setSuccess(0);
            baseResp.setErrorMsg("飞升金币不足");
            return baseResp;
        }

        // 5.3 飞升丹校验（抽取成方法，避免重复代码）
        int itemId;
        String professionName;
        switch (character.getProfession()) {
            case "武圣":
                itemId = 21;
                professionName = "武圣";
                break;
            case "神将":
                itemId = 23;
                professionName = "神将";
                break;
            default:
                itemId = 22; // 修复仙灵item_id错误
                professionName = "仙灵";
                break;
        }
        if (!checkAndDeductFlyupDan(token.getUserId(), itemId, flyup.getCurrentConsume(), professionName)) {
            baseResp.setSuccess(0);
            baseResp.setErrorMsg(professionName + "飞升丹不足");
            return baseResp;
        }

        // 6. 扣减资源
        character.setStackCount(character.getStackCount() - flyup.getFlyupTimes());
        user.setGold(user.getGold().subtract(new BigDecimal(flyup.getGold())));
        character.setFlyup(targetFlyupTimes);
        character.setMaxLv(character.getMaxLv() + 5); // 核心：更新maxLv

        // 7. 保存数据（先更新角色，再更新用户）
        charactersMapper.updateByPrimaryKeySelective(character); // 改为选择性更新，只更新有值的字段
        userMapper.updateuser(user);

        // 8. 插入系统公告
        GameNotice gameNotice = new GameNotice();
        gameNotice.setDescription("恭喜 " + user.getNickname() + " 玩家 " + character.getName() + " 成功飞升 + " + character.getFlyup() + "，战力飙升，傲视全服！");
        gameNoticeMapper.insert(gameNotice);

        // 9. 组装返回数据
        Map<String, Object> data = new HashMap<>();
        List<Characters> characterList = charactersMapper.selectByUserId(user.getUserId());
        UserInfo info = new UserInfo();
        BeanUtils.copyProperties(user, info);
        info.setCharacterList(formateCharacter(characterList));
        data.put("userInfo", info);
        data.put("cardTotal", character.getStackCount());
        data.put("cardTotal2", character.getFlyup());
        data.put("flyup", character.getFlyup());

        // 10. 组装下一次飞升所需信息
        if (character.getFlyup() < 11) {
            List<QqShenxianFlyup> qqShenxianFlyups2 = qqShenxianFlyupList.stream()
                    .filter(x -> x.getFlyupTimes() == character.getFlyup())
                    .collect(Collectors.toList());
            if (!CollectionUtils.isEmpty(qqShenxianFlyups2)) {
                QqShenxianFlyup flyup2 = qqShenxianFlyups2.get(0);
                data.put("dangyaoTotal2", flyup2.getCurrentConsume());
                data.put("gold", flyup2.getGold());
            }
        }

        // 11. 查询当前飞升丹数量
        int currentDanCount = getFlyupDanCount(token.getUserId(), itemId);
        data.put("dangyaoTotal", currentDanCount);

        // 12. 返回结果
        baseResp.setSuccess(1);
        baseResp.setData(data);
        baseResp.setErrorMsg("更新成功");
        return baseResp;
    }

    /**
     * 校验并扣减飞升丹
     */
    private boolean checkAndDeductFlyupDan(String userId, int itemId, int consumeCount, String professionName) {
        Map<String, Object> map = new HashMap<>();
        map.put("user_id", userId);
        map.put("item_id", itemId);
        map.put("is_delete", 0);
        List<GamePlayerBag> playerBags = gamePlayerBagMapper.selectByMap(map);

        if (CollectionUtils.isEmpty(playerBags)) {
            return false;
        }

        GamePlayerBag playerBag = playerBags.get(0);
        if (playerBag.getItemCount() < consumeCount) {
            return false;
        }

        // 扣减飞升丹
        int remainCount = playerBag.getItemCount() - consumeCount;
        if (remainCount > 0) {
            playerBag.setItemCount(remainCount);
        } else {
            playerBag.setIsDelete("1");
            playerBag.setItemCount(0);
        }
        gamePlayerBagMapper.updateById(playerBag);
        return true;
    }

    /**
     * 获取当前飞升丹数量
     */
    private int getFlyupDanCount(String userId, int itemId) {
        Map<String, Object> map = new HashMap<>();
        map.put("user_id", userId);
        map.put("item_id", itemId);
        map.put("is_delete", 0);
        List<GamePlayerBag> playerBags = gamePlayerBagMapper.selectByMap(map);
        if (CollectionUtils.isEmpty(playerBags)) {
            return 0;
        }
        return playerBags.get(0).getItemCount();
    }

    @Override
    public BaseResp eqCardLevelUp(TokenDto token, HttpServletRequest request) throws Exception {
        BaseResp baseResp = new BaseResp();
        if (Xtool.isNull(token.getMyMap())) {
            baseResp.setSuccess(1);
            baseResp.setErrorMsg("更新成功");
            return baseResp;
        }
        EqCharacters character = eqCharactersMapper.listById(token.getUserId(), token.getId());
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
            EqCharacters characterCong = eqCharactersMapper.listById(token.getUserId(), entry.getKey());
            for (int i = 0; i < entry.getValue(); i++) {
//                charactersList.add(characterCong);
                //如果是魂力宝珠
                if ("17000107".equals(characterCong.getId())) {
                    materials.add(new MaterialCard(1, 100));
                } else if ("17000108".equals(characterCong.getId())) {
                    materials.add(new MaterialCard(1, 500));
                } else if ("17000109".equals(characterCong.getId())) {
                    materials.add(new MaterialCard(1, 1000));
                } else{
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
//        String userId = (String) redisTemplate.opsForValue().get(token.getToken());
        String userId = token.getUserId();
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
    @Transactional
    @NoRepeatSubmit(limitSeconds = 5)
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

        // 1. 获取前端传递的二维数组
        if (token.getFinalLevel() <= 0) {
            baseResp.setSuccess(0);
            baseResp.setErrorMsg("服务器异常3");
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
        List<MaterialCard> materials = new ArrayList<>();
        for (Map.Entry<String, Integer> entry : myMap.entrySet()) {
//            System.out.println("键：" + entry.getKey() + "，值：" + entry.getValue());
            Characters characterCong = charactersMapper.listById(token.getUserId(), entry.getKey());
            for (int i = 0; i < entry.getValue(); i++) {
//                charactersList.add(characterCong);
                //如果是魂力宝珠
                if ("105".equals(characterCong.getId())) {
                    materials.add(new MaterialCard(1, 5000));
                } else {
                    //第一张吞掉本经验，后续则5经验
                    if (i == 0) {
                        materials.add(new MaterialCard(characterCong.getLv(), characterCong.getExp()));
                    } else {
                        materials.add(new MaterialCard(1, 5));
                    }
                }

            }

        }
        Characters character = charactersMapper.listById(token.getUserId(), token.getId());
        List<QqCardExp> qqCardExpList = qqCardExpMapper.findbyStar(character.getStar().stripTrailingZeros() + "");
        List<Integer> expTable = new ArrayList<>();
        List<Integer> silverTable = new ArrayList<>();
        for (QqCardExp qqCardExp : qqCardExpList) {
            expTable.add(qqCardExp.getUpgradeExp());
            silverTable.add(qqCardExp.getGold());
        }
        int maxLevel = character.getMaxLv(); // 最高等级5级
        // 主卡：当前2级，已有30经验
        int mainLevel = character.getLv();
        int mainExp = character.getExp();

        LevelUpResult result = this.calculateLevelUp(mainLevel, mainExp, materials, expTable, silverTable, maxLevel, token.getId());


        for (Map.Entry<String, Integer> entry : myMap.entrySet()) {
            Characters characters = charactersMapper.listById(token.getUserId(), entry.getKey());
            if (characters.getStackCount() - entry.getValue() >= 0) {
                characters.setStackCount(characters.getStackCount() - entry.getValue());
                characters.setLv(1);
                characters.setExp(5);
            } else {
                characters.setIsDelete("1");
            }
            charactersMapper.updateByPrimaryKey(characters);
        }
        Characters characters = charactersMapper.listById(token.getUserId(), token.getId());
        characters.setExp(result.getRemainingExp());
        characters.setLv(result.getFinalLevel());
        charactersMapper.updateByPrimaryKey(characters);
        User user = userMapper.selectUserByUserId(Integer.parseInt(token.getUserId()));
        user.setGold(user.getGold().subtract(new BigDecimal(result.getTotalSilverSpent())));
        userMapper.updateuser(user);
        List<Characters> characterList = charactersMapper.selectByUserId(user.getUserId());
        UserInfo info = new UserInfo();
        BeanUtils.copyProperties(user, info);
        info.setCharacterList(formateCharacter(characterList));
        baseResp.setSuccess(1);
        baseResp.setData(info);
        baseResp.setErrorMsg("更新成功");
        return baseResp;
    }

    @Override
    @Transactional
    @NoRepeatSubmit(limitSeconds = 5)
    public BaseResp eqCardLevelUp2(TokenDto token, HttpServletRequest request) throws Exception {
        BaseResp baseResp = new BaseResp();
        if (Xtool.isNull(token.getMyMap())) {
            baseResp.setSuccess(0);
            baseResp.setErrorMsg("从卡不存在");
            return baseResp;
        }


        // 1. 获取前端传递的二维数组
        if (token.getFinalLevel() <= 0) {
            baseResp.setSuccess(0);
            baseResp.setErrorMsg("服务器异常3");
            return baseResp;
        }
        EqCharacters character = eqCharactersMapper.listById(token.getUserId(), token.getId());
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
            EqCharacters characterCong = eqCharactersMapper.listById(token.getUserId(), entry.getKey());
            for (int i = 0; i < entry.getValue(); i++) {
//                charactersList.add(characterCong);
                //如果是魂力宝珠
                if ("17000107".equals(characterCong.getId())) {
                    materials.add(new MaterialCard(1, 100));
                } else if ("17000108".equals(characterCong.getId())) {
                    materials.add(new MaterialCard(1, 500));
                } else if ("17000109".equals(characterCong.getId())) {
                    materials.add(new MaterialCard(1, 1000));
                } else{
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


        for (Map.Entry<String, Integer> entry : myMap.entrySet()) {
            EqCharacters characters = eqCharactersMapper.listById(token.getUserId(), entry.getKey());
            characters.setIsDelete("1");
            eqCharactersMapper.updateByPrimaryKey(characters);
        }
        EqCharacters characters = eqCharactersMapper.listById(token.getUserId(), token.getId());
        characters.setExp(result.getRemainingExp());
        characters.setLv(result.getFinalLevel());
        eqCharactersMapper.updateByPrimaryKey(characters);
        User user = userMapper.selectUserByUserId(Integer.parseInt(token.getUserId()));
        user.setGold(user.getGold().subtract(new BigDecimal(result.getTotalSilverSpent())));
        userMapper.updateuser(user);
        List<EqCharacters> characterList = eqCharactersMapper.selectByUserId(user.getUserId());
        UserInfo info = new UserInfo();
        BeanUtils.copyProperties(user, info);
        info.setEqCharactersList(formateEqCharacter(characterList));
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
//        String userId = (String) redisTemplate.opsForValue().get(token.getToken());
        String userId = token.getUserId();
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
        info.setCharacterList(formateCharacter(characterList));
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
//        String userId = (String) redisTemplate.opsForValue().get(token.getToken());
        String userId = token.getUserId();
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
        info.setCharacterList(formateCharacter(characterList));
        baseResp.setData(info);
        baseResp.setErrorMsg("更新成功");
        return baseResp;
    }

    @Override
    public BaseResp arenaItemUpdate(TokenDto token, HttpServletRequest request) throws Exception {
        BaseResp baseResp = new BaseResp();
        if (token == null || Xtool.isNull(token.getToken())) {
            baseResp.setSuccess(0);
            baseResp.setErrorMsg("登录过期");
            return baseResp;
        }
//        String userId = (String) redisTemplate.opsForValue().get(token.getToken());
        String userId = token.getUserId();
        if (Xtool.isNull(userId)) {
            baseResp.setSuccess(0);
            baseResp.setErrorMsg("登录过期");
            return baseResp;
        }

        if (Xtool.isNotNull(token.getStr())) {
            String str = token.getStr();
            List<String> strings = Arrays.asList(str.split(","));
            //先将用户所有卡下架然后再更新
//            charactersMapper.updateGoNuM(userId);
            for (int i = 0; i < strings.size(); i++) {
                if (!"@".equals(strings.get(i))) {
                    gameArenaBattlecharactersMapper.updateGoNuM2(i + 1, token.getFinalLevel(), ArenaWeekUtils.getCurrentUniqueWeekNum(new Date()), strings.get(i), userId);
                }
            }
        }
        User user = userMapper.selectUserByUserId(Integer.parseInt(userId));
        baseResp.setSuccess(1);
        UserInfo info = new UserInfo();
        BeanUtils.copyProperties(user, info);
        //获取卡牌数据
        List<Characters> characterList = charactersMapper.selectByUserId(user.getUserId());
        info.setCharacterList(formateCharacter(characterList));
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
    public BaseResp duoMessageList(TokenDto token, HttpServletRequest request) throws Exception {
        List<PillRobRecord> list = pillRobRecordMapper.seletByUserId(token.getUserId());
        Collections.sort(list, Comparator.comparing(PillRobRecord::getCreateTime).reversed());
        list.stream().map(x -> {
            x.setTimeStr(formatTime(x.getCreateTime()));
            return x;
        }).collect(Collectors.toList());
        BaseResp baseResp = new BaseResp();
        baseResp.setData(list);
        baseResp.setSuccess(1);
        return baseResp;
    }

    @Override
    public BaseResp arenaMessageList(TokenDto token, HttpServletRequest request) throws Exception {
        Map map = new HashMap();
        map.put("arena_level", token.getFinalLevel());
        map.put("week_num", ArenaWeekUtils.getCurrentUniqueWeekNum(new Date()));
        List<GameArenaBattle> list = gameArenaBattleMapper.selectByMap(map);
        Collections.sort(list, Comparator.comparing(GameArenaBattle::getCreatetime).reversed());
        list.stream().map(x -> {
            x.setTimeStr(formatTime(x.getCreatetime()));
            return x;
        }).collect(Collectors.toList());
        BaseResp baseResp = new BaseResp();
        baseResp.setData(list);
        baseResp.setSuccess(1);
        return baseResp;
    }

    @Override
    @Transactional
    @NoRepeatSubmit(limitSeconds = 1)
    public BaseResp receive(TokenDto token, HttpServletRequest request) throws Exception {
        BaseResp baseResp = new BaseResp();
        if (token == null || Xtool.isNull(token.getToken())) {
            baseResp.setSuccess(0);
            baseResp.setErrorMsg("登录过期");
            return baseResp;
        }
        String userId = token.getUserId();
//        String userId = (String) redisTemplate.opsForValue().get(token.getToken());
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
        if (gift.getRemainingQuantity() != -1 && gift.getRemainingQuantity() <= 0 && gift.getGiftType() != 4) {
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
        if ("4".equals(gift.getGiftType()) || "5".equals(gift.getGiftType())) {
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
            } else if ("5".equals(content.getItemType() + "") || "6".equals(content.getItemType() + "")) {
                Map itemMap = new HashMap();
                itemMap.put("item_id", content.getItemId());
                itemMap.put("user_id", userId);
                itemMap.put("is_delete", "0");
                List<GamePlayerBag> playerBagList = gamePlayerBagMapper.selectByMap(itemMap);
                if (Xtool.isNotNull(playerBagList)) {
                    GamePlayerBag playerBag = playerBagList.get(0);
                    playerBag.setItemCount(playerBag.getItemCount() + content.getItemQuantity());
                    gamePlayerBagMapper.updateById(playerBag);
                } else {
                    GamePlayerBag playerBag = new GamePlayerBag();
                    playerBag.setUserId(Integer.parseInt(userId));
                    playerBag.setItemCount(content.getItemQuantity());
                    playerBag.setGridIndex(1);
                    playerBag.setItemId(Integer.parseInt(content.getItemId() + ""));
                    gamePlayerBagMapper.insert(playerBag);
                }
            }
        }
        userMapper.updateuser(user);
        baseResp.setSuccess(1);
        UserInfo info = new UserInfo();
        BeanUtils.copyProperties(user, info);
        info.setBronze(0);
        info.setDarkSteel(0);
        info.setPurpleGold(0);
        info.setCrystal(0);
        GamePlayerBag playerBag = gamePlayerBagMapper.goIntoListByIdAndItemId(userId, 13);
        if (playerBag != null) {
            info.setBronze(playerBag.getItemCount());
        }
        GamePlayerBag playerBag1 = gamePlayerBagMapper.goIntoListByIdAndItemId(userId, 14);
        if (playerBag1 != null) {
            info.setDarkSteel(playerBag1.getItemCount());
        }
        GamePlayerBag playerBag2 = gamePlayerBagMapper.goIntoListByIdAndItemId(userId, 15);
        if (playerBag2 != null) {
            info.setPurpleGold(playerBag2.getItemCount());
        }
        GamePlayerBag playerBag3 = gamePlayerBagMapper.goIntoListByIdAndItemId(userId, 16);
        if (playerBag3 != null) {
            info.setCrystal(playerBag3.getItemCount());
        }
        //获取卡牌数据
        List<Characters> characterList = charactersMapper.selectByUserId(user.getUserId());
        info.setCharacterList(formateCharacter(characterList));
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
    @Transactional
    @NoRepeatSubmit(limitSeconds = 5)
    public BaseResp getStore(TokenDto token, HttpServletRequest request) throws Exception {
        BaseResp baseResp = new BaseResp();
        if (token == null || Xtool.isNull(token.getToken())) {
            baseResp.setSuccess(0);
            baseResp.setErrorMsg("登录过期");
            return baseResp;
        }
        String userId = token.getUserId();
//        String userId = (String) redisTemplate.opsForValue().get(token.getToken());
        if (Xtool.isNull(userId)) {
            baseResp.setSuccess(0);
            baseResp.setErrorMsg("登录过期");
            return baseResp;
        }
        Date date2 = new Date(System.currentTimeMillis() - 1200 * 1000); // 1小时前的时间
        User user = userMapper.selectUserByUserId(Integer.parseInt(userId));
        Map map = new HashMap();
        map.put("chongzhi", user.getChongzhi());
        if (user.getShopUpdate() == null || (user.getShopUpdate().compareTo(date2) < 0 && "1".equals(token.getStr()))) {
            Date date = new Date();
            user.setShopUpdate(date);
            map.put("shopUpdate", date);
            List<GameItemShop> gameItemShopList = gameItemShopMapper.selectAll();
            DynamicItemPicker picker = new DynamicItemPicker();
            for (GameItemShop gameItemShop : gameItemShopList) {
                picker.addItem(gameItemShop);
            }
            // 尝试获取16个物品（种类不足，会重复获取）
            List<GameItemShop> picked = picker.pickRandomItems(16);
            List<GameItemShop> picked2 = new ArrayList<>();
            Integer id = 0;
            for (GameItemShop shop : picked) {
                GameItemShop itemShop = new GameItemShop();
                BeanUtils.copyProperties(shop, itemShop);
                itemShop.setId(id);
                itemShop.setIsBuy(0);
                picked2.add(itemShop);
                id++;
            }
            userMapper.updateuser(user);
            map.put("picked", picked2);
            String json = JsonUtils.toJson(picked2);
            //先删再新增
            gameTimeRecordMapper.deleteMe(Integer.parseInt(userId));
            GameTimeRecord record = new GameTimeRecord();
            record.setUserId(Integer.parseInt(userId));
            record.setPicked(json);
            gameTimeRecordMapper.insert(record);
        } else {
            Map hashMap = new HashMap();
            hashMap.put("user_id", userId);
            List<GameTimeRecord> gameTimeRecord = gameTimeRecordMapper.selectByMap(hashMap);
            if (Xtool.isNotNull(gameTimeRecord)) {
                map.put("picked", JsonUtils.fromJsonToObjList(gameTimeRecord.get(0).getPicked()));
            } else {
                List<GameItemShop> gameItemShopList = gameItemShopMapper.selectAll();
                DynamicItemPicker picker = new DynamicItemPicker();
                for (GameItemShop gameItemShop : gameItemShopList) {
                    picker.addItem(gameItemShop);
                }
                // 尝试获取16个物品（种类不足，会重复获取）
                List<GameItemShop> picked = picker.pickRandomItems(16);
                List<GameItemShop> picked2 = new ArrayList<>();
                Integer id = 0;
                for (GameItemShop shop : picked) {
                    GameItemShop itemShop = new GameItemShop();
                    BeanUtils.copyProperties(shop, itemShop);
                    itemShop.setId(id);
                    itemShop.setIsBuy(0);
                    picked2.add(itemShop);
                    id++;
                }
                map.put("picked", picked2);
                String json = JsonUtils.toJson(picked2);
                //先删再新增
                GameTimeRecord record = new GameTimeRecord();
                record.setUserId(Integer.parseInt(userId));
                record.setPicked(json);
                gameTimeRecordMapper.insert(record);
            }
            map.put("shopUpdate", user.getShopUpdate());
        }
        baseResp.setSuccess(1);
        baseResp.setData(map);
        baseResp.setErrorMsg("成功");
        return baseResp;
    }

    @Override
    @Transactional
    @NoRepeatSubmit(limitSeconds = 5)
    public BaseResp getEqChares(TokenDto token, HttpServletRequest request) throws Exception {
        BaseResp baseResp = new BaseResp();
        if (token == null || Xtool.isNull(token.getToken())) {
            baseResp.setSuccess(0);
            baseResp.setErrorMsg("登录过期");
            return baseResp;
        }
        String userId = token.getUserId();
//        String userId = (String) redisTemplate.opsForValue().get(token.getToken());
        if (Xtool.isNull(userId)) {
            baseResp.setSuccess(0);
            baseResp.setErrorMsg("登录过期");
            return baseResp;
        }
        User user = userMapper.selectUserByUserId(Integer.parseInt(userId));
        Map map = new HashMap();
        map.put("eqCount", user.getEqCount());
        map.put("diamond", user.getDiamond());
        Map hashMap = new HashMap();
        hashMap.put("user_id", userId);
        List<GameEqRecord> gameEqRecords = gameEqRecordMapper.selectByMap(hashMap);
        if (Xtool.isNotNull(gameEqRecords)) {
            map.put("picked", JsonUtils.fromJsonToObjList(gameEqRecords.get(0).getPicked()));
        } else {
            List<EqCard> eqCards = eqCardMapper.selectAll();
            EqItemPicker picker = new EqItemPicker();
            for (EqCard eqCard : eqCards) {
                picker.addItem(eqCard);
            }
            // 尝试获取16个物品（种类不足，会重复获取）
            List<EqCard> picked = picker.pickRandomItems(16);
            List<EqCard> picked2 = new ArrayList<>();
            Integer id = 0;
            for (EqCard shop : picked) {
                EqCard itemShop = new EqCard();
                BeanUtils.copyProperties(shop, itemShop);
                itemShop.setUuid(id);
                itemShop.setIsBuy(0);
                itemShop.setGoldEdgePrice(0);
                if (shop.getStar().compareTo(new BigDecimal(1.5)) < 0) {
                    itemShop.setGoldEdgePrice(100000);
                } else if (shop.getStar().compareTo(new BigDecimal(2)) < 0) {
                    itemShop.setGoldEdgePrice(200000);
                } else if (shop.getStar().compareTo(new BigDecimal(2.5)) < 0) {
                    itemShop.setGoldEdgePrice(300000);
                } else if (shop.getStar().compareTo(new BigDecimal(3)) < 0) {
                    itemShop.setGoldEdgePrice(400000);
                } else if (shop.getStar().compareTo(new BigDecimal(3.5)) < 0) {
                    itemShop.setGemPrice(100);
                } else if (shop.getStar().compareTo(new BigDecimal(4)) < 0) {
                    itemShop.setGemPrice(500);
                } else if (shop.getStar().compareTo(new BigDecimal(4.5)) < 0) {
                    itemShop.setGemPrice(1000);
                }
                picked2.add(itemShop);
                id++;
            }
            map.put("picked", picked2);
            String json = JsonUtils.toJson(picked2);
            //先删再新增
            GameEqRecord record = new GameEqRecord();
            record.setUserId(Integer.parseInt(userId));
            record.setPicked(json);
            gameEqRecordMapper.insert(record);
        }
        map.put("shopUpdate", user.getShopUpdate());
        baseResp.setSuccess(1);
        baseResp.setData(map);
        baseResp.setErrorMsg("成功");
        return baseResp;
    }

    @Override
    public BaseResp chongzhi(TokenDto token, HttpServletRequest request) throws Exception {
        BaseResp baseResp = new BaseResp();
        if (token == null || Xtool.isNull(token.getToken())) {
            baseResp.setSuccess(0);
            baseResp.setErrorMsg("登录过期");
            return baseResp;
        }
        String userId = token.getUserId();
//        String userId = (String) redisTemplate.opsForValue().get(token.getToken());
        if (Xtool.isNull(userId)) {
            baseResp.setSuccess(0);
            baseResp.setErrorMsg("登录过期");
            return baseResp;
        }
        User user = userMapper.selectUserByUserId(Integer.parseInt(userId));
        BigDecimal diamond = user.getDiamond().subtract(new BigDecimal(user.getChongzhi()));
        if (diamond.compareTo(BigDecimal.ZERO) < 0) {
            baseResp.setSuccess(0);
            baseResp.setErrorMsg("钻石不足");
            return baseResp;
        }
        user.setDiamond(diamond);
        user.setChongzhi(ValueUpdateUtil.calculateNextValue(user.getChongzhi()));
        Map map = new HashMap();
//        Date date = new Date();
//        user.setShopUpdate(date);
//        map.put("shopUpdate", date);
        List<GameItemShop> gameItemShopList = gameItemShopMapper.selectAll();
        DynamicItemPicker picker = new DynamicItemPicker();
        for (GameItemShop gameItemShop : gameItemShopList) {
            picker.addItem(gameItemShop);
        }
        // 尝试获取16个物品（种类不足，会重复获取）
        List<GameItemShop> picked = picker.pickRandomItems(16);
        List<GameItemShop> picked2 = new ArrayList<>();
        Integer id = 0;
        for (GameItemShop shop : picked) {
            GameItemShop itemShop = new GameItemShop();
            BeanUtils.copyProperties(shop, itemShop);
            itemShop.setId(id);
            itemShop.setIsBuy(0);
            picked2.add(itemShop);
            id++;
        }
        userMapper.updateuser(user);
        baseResp.setSuccess(1);
        UserInfo info = new UserInfo();
        BeanUtils.copyProperties(user, info);
        map.put("picked", picked2);
        map.put("userInfo", info);
        map.put("chongzhi", user.getChongzhi());
        String json = JsonUtils.toJson(picked2);
        //先删再新增
        gameTimeRecordMapper.deleteMe(Integer.parseInt(userId));
        GameTimeRecord record = new GameTimeRecord();
        record.setUserId(Integer.parseInt(userId));
        record.setPicked(json);
        gameTimeRecordMapper.insert(record);
        baseResp.setSuccess(1);
        baseResp.setData(map);
        baseResp.setErrorMsg("成功");
        return baseResp;
    }

    @Override
    public BaseResp chongzhi2(TokenDto token, HttpServletRequest request) throws Exception {
        BaseResp baseResp = new BaseResp();
        if (token == null || Xtool.isNull(token.getToken())) {
            baseResp.setSuccess(0);
            baseResp.setErrorMsg("登录过期");
            return baseResp;
        }
        String userId = token.getUserId();
//        String userId = (String) redisTemplate.opsForValue().get(token.getToken());
        if (Xtool.isNull(userId)) {
            baseResp.setSuccess(0);
            baseResp.setErrorMsg("登录过期");
            return baseResp;
        }
        User user = userMapper.selectUserByUserId(Integer.parseInt(userId));
        BigDecimal diamond = user.getDiamond().subtract(new BigDecimal(1000));
        if (diamond.compareTo(BigDecimal.ZERO) < 0) {
            baseResp.setSuccess(0);
            baseResp.setErrorMsg("钻石不足");
            return baseResp;
        }
        user.setDiamond(diamond);
        Map map = new HashMap();
        map.put("diamond", diamond);
        List<EqCard> eqCards2 = eqCardMapper.selectAll();
        EqItemPicker picker = new EqItemPicker();
        for (EqCard eqCard : eqCards2) {
            picker.addItem(eqCard);
        }
        // 尝试获取16个物品（种类不足，会重复获取）
        List<EqCard> picked = picker.pickRandomItems(16);
        List<EqCard> picked2 = new ArrayList<>();
        Integer id = 0;
        for (EqCard shop : picked) {
            EqCard itemShop = new EqCard();
            BeanUtils.copyProperties(shop, itemShop);
            itemShop.setUuid(id);
            itemShop.setIsBuy(0);
            itemShop.setGoldEdgePrice(0);
            if (shop.getStar().compareTo(new BigDecimal(1.5)) < 0) {
                itemShop.setGoldEdgePrice(100000);
            } else if (shop.getStar().compareTo(new BigDecimal(2)) < 0) {
                itemShop.setGoldEdgePrice(200000);
            } else if (shop.getStar().compareTo(new BigDecimal(2.5)) < 0) {
                itemShop.setGoldEdgePrice(300000);
            } else if (shop.getStar().compareTo(new BigDecimal(3)) < 0) {
                itemShop.setGoldEdgePrice(400000);
            } else if (shop.getStar().compareTo(new BigDecimal(3.5)) < 0) {
                itemShop.setGemPrice(100);
            } else if (shop.getStar().compareTo(new BigDecimal(4)) < 0) {
                itemShop.setGemPrice(500);
            } else if (shop.getStar().compareTo(new BigDecimal(4.5)) < 0) {
                itemShop.setGemPrice(1000);
            }
            picked2.add(itemShop);
            id++;
        }
        user.setEqCount(user.getEqCount() + 1);
        if (user.getEqCount() > 10) {
            user.setEqCount(0);
            EqCard drawnCard = EquipmentGenerateUtil.generateEqCard(4);
            Map map2 = new HashMap();
            map2.put("name", drawnCard.getName());
            map2.put("star", drawnCard.getStar());
            map2.put("camp", drawnCard.getCamp());
            map2.put("profession", drawnCard.getProfession());
            map2.put("eq_type", drawnCard.getEqType());
            map2.put("eq_type2", drawnCard.getEqType2());
            map2.put("wl_atk", drawnCard.getWlAtk());
            map2.put("hy_atk", drawnCard.getHyAtk());
            map2.put("ds_atk", drawnCard.getDsAtk());
            map2.put("fd_atk", drawnCard.getFdAtk());
            map2.put("wl_def", drawnCard.getWlDef());
            map2.put("hy_def", drawnCard.getHyAtk());
            map2.put("ds_def", drawnCard.getDsAtk());
            map2.put("fd_def", drawnCard.getFdDef());
            map2.put("zl_def", drawnCard.getZlDef());
            List<EqCard> eqCards = eqCardMapper.selectByMap(map2);
            if (Xtool.isNotNull(eqCards)) {
                drawnCard.setId(eqCards.get(0).getId());
            } else {
                drawnCard.setId(drawnCard.getId());
                eqCardMapper.insert(drawnCard);
                drawnCard.setId(drawnCard.getId() + drawnCard.getUuid());
                eqCardMapper.updateById(drawnCard);
            }
            EqCard itemShop = new EqCard();
            BeanUtils.copyProperties(drawnCard, itemShop);
            itemShop.setUuid(0);
            itemShop.setIsBuy(0);
            itemShop.setGoldEdgePrice(0);
            itemShop.setGemPrice(1000);
            picked2.set(0, itemShop);
        }
        userMapper.updateuser(user);
        map.put("eqCount", user.getEqCount());
        map.put("picked", picked2);
        String json = JsonUtils.toJson(picked2);
        //先删再新增
        gameEqRecordMapper.deleteMe(Integer.parseInt(userId));
        GameEqRecord record = new GameEqRecord();
        record.setUserId(Integer.parseInt(userId));
        record.setPicked(json);
        gameEqRecordMapper.insert(record);
        map.put("shopUpdate", user.getShopUpdate());
        baseResp.setSuccess(1);
        baseResp.setData(map);
        baseResp.setErrorMsg("成功");
        return baseResp;
    }

    @Override
    public BaseResp getStore2(TokenDto token, HttpServletRequest request) throws Exception {
        BaseResp baseResp = new BaseResp();
        baseResp.setSuccess(1);
        List<GameItemPlayShop> gameItemShopList = gameItemPlayShopMapper.selectAll();
        baseResp.setData(gameItemShopList);
        baseResp.setErrorMsg("成功");
        return baseResp;
    }

    @Override
    @Transactional
//    @NoRepeatSubmit(limitSeconds = 1)
    public BaseResp buyStore(TokenDto token, HttpServletRequest request) throws Exception {
        BaseResp baseResp = new BaseResp();
        if (token == null || Xtool.isNull(token.getToken())) {
            baseResp.setSuccess(0);
            baseResp.setErrorMsg("登录过期");
            return baseResp;
        }
        String userId = token.getUserId();
//        String userId = (String) redisTemplate.opsForValue().get(token.getToken());
        if (Xtool.isNull(userId)) {
            baseResp.setSuccess(0);
            baseResp.setErrorMsg("登录过期");
            return baseResp;
        }
        String lockKey = "USE_BUY_STORE_" + userId;
        try {
            Object countObj = redisTemplate.opsForValue().get(lockKey);
            Long currentCount = null;

// 安全转换：处理 null/字符串/数字等情况
            if (countObj != null) {
                if (countObj instanceof Long) {
                    currentCount = (Long) countObj;
                } else if (countObj instanceof String) {
                    try {
                        currentCount = Long.parseLong((String) countObj);
                    } catch (NumberFormatException e) {
                        // 解析失败，视为无效计数，重置为0
                        currentCount = 0L;
                    }
                }
            }

            if (currentCount == null) {
                // 首次请求，初始化计数并设置过期时间
                redisTemplate.opsForValue().set(lockKey, "1", 600, TimeUnit.MILLISECONDS);
            } else {
                // 超过阈值，抛出异常
                baseResp.setErrorMsg("操作过于频繁");
                baseResp.setSuccess(0);
                return baseResp;
            }


            User user = userMapper.selectUserByUserId(Integer.parseInt(userId));
            Map hashMap = new HashMap();
            hashMap.put("user_id", userId);
            List<GameTimeRecord> gameTimeRecord = gameTimeRecordMapper.selectByMap(hashMap);
            if (Xtool.isNull(gameTimeRecord)) {
                baseResp.setSuccess(0);
                baseResp.setErrorMsg("操作过快请刷新重试");
                return baseResp;
            }
            List<GameItemShop> picked2 = JSON.parseObject(gameTimeRecord.get(0).getPicked(), new TypeReference<List<GameItemShop>>() {
            });
            List<GameItemShop> gameItemShops = picked2.stream().filter(x -> (x.getId() + "").equals(token.getId())).collect(Collectors.toList());
            if (Xtool.isNull(gameItemShops)) {
                baseResp.setSuccess(0);
                baseResp.setErrorMsg("商品不存在或已下架");
                return baseResp;
            }
            GameItemShop gameItemShop = gameItemShops.get(0);
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
            Characters characters1 = charactersMapper.listById(userId, gameItemShop.getItemId() + "");
            if (characters1 != null) {
                characters1.setStackCount(characters1.getStackCount() + 1);
                charactersMapper.updateByPrimaryKey(characters1);
            } else {
                Card card1 = cardMapper.selectByid(gameItemShop.getItemId());
                if (card1 == null) {
                    baseResp.setErrorMsg("服务器异常联想管理员");
                    baseResp.setSuccess(0);
                    return baseResp;
                }
                Characters characters = new Characters();
                characters.setStackCount(0);
                characters.setId(gameItemShop.getItemId() + "");
                characters.setLv(1);
                characters.setUserId(Integer.parseInt(userId));
                characters.setStar(new BigDecimal(1));
                characters.setMaxLv(CardMaxLevelUtils.getMaxLevel(card1.getName(), card1.getStar().doubleValue()));
                charactersMapper.insert(characters);
            }
            gameItemShop.setIsBuy(1);
            //先删再新增
            Map map = new HashMap();
            map.put("picked", picked2);
            String json = JsonUtils.toJson(picked2);

            gameTimeRecordMapper.deleteMe(Integer.parseInt(userId));
            GameTimeRecord record = new GameTimeRecord();
            record.setUserId(Integer.parseInt(userId));
            record.setPicked(json);
            gameTimeRecordMapper.insert(record);
            userMapper.updateuser(user);
            baseResp.setSuccess(1);
            UserInfo info = new UserInfo();
            BeanUtils.copyProperties(user, info);
            //获取卡牌数据
            List<Characters> characterList = charactersMapper.selectByUserId(user.getUserId());
            info.setCharacterList(formateCharacter(characterList));
            baseResp.setData(info);
            baseResp.setSuccess(1);
            baseResp.setErrorMsg("领取成功");

        } finally {
            // 释放锁（只有加锁成功的线程才释放）
            redisTemplate.delete(lockKey);
        }
        return baseResp;
    }

    @Override
    @Transactional
    public BaseResp buyStore3(TokenDto token, HttpServletRequest request) throws Exception {
        BaseResp baseResp = new BaseResp();
        if (token == null || Xtool.isNull(token.getToken())) {
            baseResp.setSuccess(0);
            baseResp.setErrorMsg("登录过期");
            return baseResp;
        }
        String userId = token.getUserId();
//        String userId = (String) redisTemplate.opsForValue().get(token.getToken());
        if (Xtool.isNull(userId)) {
            baseResp.setSuccess(0);
            baseResp.setErrorMsg("登录过期");
            return baseResp;
        }
        String lockKey = "USE_BUY_EQ_" + userId;
        try {
            Object countObj = redisTemplate.opsForValue().get(lockKey);
            Long currentCount = null;

// 安全转换：处理 null/字符串/数字等情况
            if (countObj != null) {
                if (countObj instanceof Long) {
                    currentCount = (Long) countObj;
                } else if (countObj instanceof String) {
                    try {
                        currentCount = Long.parseLong((String) countObj);
                    } catch (NumberFormatException e) {
                        // 解析失败，视为无效计数，重置为0
                        currentCount = 0L;
                    }
                }
            }

            if (currentCount == null) {
                // 首次请求，初始化计数并设置过期时间
                redisTemplate.opsForValue().set(lockKey, "1", 600, TimeUnit.MILLISECONDS);
            } else {
                // 超过阈值，抛出异常
                baseResp.setErrorMsg("操作过于频繁");
                baseResp.setSuccess(0);
                return baseResp;
            }


            User user = userMapper.selectUserByUserId(Integer.parseInt(userId));
            Map hashMap = new HashMap();
            hashMap.put("user_id", userId);
            List<GameEqRecord> gameEqRecords = gameEqRecordMapper.selectByMap(hashMap);
            if (Xtool.isNull(gameEqRecords)) {
                baseResp.setSuccess(0);
                baseResp.setErrorMsg("操作过快请刷新重试");
                return baseResp;
            }
            List<EqCard> picked2 = JSON.parseObject(gameEqRecords.get(0).getPicked(), new TypeReference<List<EqCard>>() {
            });
            List<EqCard> eqCards = picked2.stream().filter(x -> (x.getUuid() + "").equals(token.getId())).collect(Collectors.toList());
            if (Xtool.isNull(eqCards)) {
                baseResp.setSuccess(0);
                baseResp.setErrorMsg("装备不存在或已下架");
                return baseResp;
            }
            EqCard eqCard = eqCards.get(0);
            if (eqCard == null) {
                baseResp.setSuccess(0);
                baseResp.setErrorMsg("装备不存在或已下架");
                return baseResp;
            }
            if (eqCard.getGoldEdgePrice() != 0) {
                BigDecimal gold = user.getGold().subtract(new BigDecimal(eqCard.getGoldEdgePrice()));
                if (gold.compareTo(BigDecimal.ZERO) < 0) {
                    baseResp.setSuccess(0);
                    baseResp.setErrorMsg("银两不足");
                    return baseResp;
                }
                user.setGold(gold);
            } else {
                BigDecimal diamond = user.getDiamond().subtract(new BigDecimal(eqCard.getGemPrice()));
                if (diamond.compareTo(BigDecimal.ZERO) < 0) {
                    baseResp.setSuccess(0);
                    baseResp.setErrorMsg("钻石不足");
                    return baseResp;
                }
                user.setDiamond(diamond);
            }
            EqCard card1 = eqCardMapper.selectByid(eqCard.getId());
            if (card1 == null) {
                baseResp.setErrorMsg("服务器异常联想管理员");
                baseResp.setSuccess(0);
                return baseResp;
            }
            EqCharacters characters = new EqCharacters();
            characters.setStackCount(0);
            characters.setId(eqCard.getId() + "");
            characters.setLv(1);
            characters.setUserId(Integer.parseInt(userId));
            characters.setStar(new BigDecimal(1));
            characters.setMaxLv(CardMaxLevelUtils.getMaxLevel(card1.getName(), card1.getStar().doubleValue()));
            eqCharactersMapper.insert(characters);

            if (eqCard.getStar().compareTo(new BigDecimal(3)) > 0) {
                GameNotice gameNotice = new GameNotice();
                gameNotice.setDescription("恭喜 " + user.getNickname() + " 神秘商店购买" + eqCard.getStar().stripTrailingZeros() + "星" + eqCard.getName());
                gameNoticeMapper.insert(gameNotice);
                EqCharactersRecord eqCharactersRecord = new EqCharactersRecord();
                eqCharactersRecord.setEqImg(eqCard.getImg());
                eqCharactersRecord.setEqName(eqCard.getName());
                eqCharactersRecord.setGetTime(new Date());
                eqCharactersRecord.setId(eqCard.getId());
                eqCharactersRecord.setStatus(0);
                eqCharactersRecord.setUserId(Integer.parseInt(userId));
                eqCharactersRecord.setUserName(user.getNickname());
                eqCharactersRecord.setStar(eqCard.getStar());
                eqCharactersRecord.setImg(user.getGameImg());
                eqCharactersRecordMapper.insert(eqCharactersRecord);
            }


            eqCard.setIsBuy(1);
            //先删再新增
            Map map = new HashMap();
            map.put("picked", picked2);
            String json = JsonUtils.toJson(picked2);

            gameEqRecordMapper.deleteMe(Integer.parseInt(userId));
            GameEqRecord record = new GameEqRecord();
            record.setUserId(Integer.parseInt(userId));
            record.setPicked(json);
            gameEqRecordMapper.insert(record);
            userMapper.updateuser(user);
            baseResp.setSuccess(1);
            UserInfo info = new UserInfo();
            BeanUtils.copyProperties(user, info);
            //获取卡牌数据
            List<EqCharacters> characterList = eqCharactersMapper.selectByUserId(user.getUserId());
            info.setEqCharactersList(characterList);
            baseResp.setData(info);
            baseResp.setSuccess(1);
            baseResp.setErrorMsg("领取成功");

        } finally {
            // 释放锁（只有加锁成功的线程才释放）
            redisTemplate.delete(lockKey);
        }
        return baseResp;
    }

    @Override
    @Transactional
//    @NoRepeatSubmit(limitSeconds = 1)
    public BaseResp buyStore2(TokenDto token, HttpServletRequest request) throws Exception {
        BaseResp baseResp = new BaseResp();
        if (token == null || Xtool.isNull(token.getToken())) {
            baseResp.setSuccess(0);
            baseResp.setErrorMsg("登录过期");
            return baseResp;
        }
//        String userId = (String) redisTemplate.opsForValue().get(token.getToken());
        String userId = token.getUserId();
        if (Xtool.isNull(userId)) {
            baseResp.setSuccess(0);
            baseResp.setErrorMsg("登录过期");
            return baseResp;
        }
        User user = userMapper.selectUserByUserId(Integer.parseInt(userId));
        GameItemPlayShop gameItemShop = gameItemPlayShopMapper.selectById(token.getId());
        if (gameItemShop == null) {
            baseResp.setSuccess(0);
            baseResp.setErrorMsg("商品不存在或已下架");
            return baseResp;
        }
        if (gameItemShop.getGoldEdgePrice() != 0) {
            BigDecimal gold = user.getGold().subtract(new BigDecimal(gameItemShop.getGoldEdgePrice()).multiply(new BigDecimal(token.getStr())));
            if (gold.compareTo(BigDecimal.ZERO) < 0) {
                baseResp.setSuccess(0);
                baseResp.setErrorMsg("银两不足");
                return baseResp;
            }
            user.setGold(gold);
        } else {
            BigDecimal diamond = user.getDiamond().subtract(new BigDecimal(gameItemShop.getGemPrice()).multiply(new BigDecimal(token.getStr())));
            if (diamond.compareTo(BigDecimal.ZERO) < 0) {
                baseResp.setSuccess(0);
                baseResp.setErrorMsg("钻石不足");
                return baseResp;
            }
            user.setDiamond(diamond);
        }
        //先判断物品是否存在
        GameItemPlayShop gameItemPlayShop = gameItemPlayShopMapper.selectById(token.getId());
        if (gameItemPlayShop == null) {
            baseResp.setSuccess(0);
            baseResp.setErrorMsg("购买物品不存在或已下架");
            return baseResp;
        }
        if (gameItemPlayShop.getStock() > 0) {
            Integer num = gameItemPlayShop.getStock() - gameShopRecordMapper.isRecord(userId, token.getId());
            if (num == 0) {
                baseResp.setSuccess(0);
                baseResp.setErrorMsg("您已达该商品限购上限，感谢支持！");
                return baseResp;
            }
            if (Integer.parseInt(token.getStr()) > num) {
                baseResp.setSuccess(0);
                baseResp.setErrorMsg("您最多还能购买 " + num + " 个该商品，请调整购买数量后重试");
                return baseResp;
            }
//            if (gameShopRecordMapper.isRecord(userId)>=);
        }
        Map itemMap = new HashMap();
        itemMap.put("item_id", token.getId());
        itemMap.put("user_id", userId);
        itemMap.put("is_delete", "0");
        List<GamePlayerBag> playerBagList = gamePlayerBagMapper.selectByMap(itemMap);
        if (Xtool.isNotNull(playerBagList)) {
            GamePlayerBag playerBag = playerBagList.get(0);
            playerBag.setItemCount(playerBag.getItemCount() + Integer.parseInt(token.getStr()));
            gamePlayerBagMapper.updateById(playerBag);
        } else {
            GamePlayerBag playerBag = new GamePlayerBag();
            playerBag.setUserId(Integer.parseInt(userId));
            playerBag.setItemCount(Integer.parseInt(token.getStr()));
            playerBag.setGridIndex(1);
            playerBag.setItemId(Integer.parseInt(token.getId()));
            gamePlayerBagMapper.insert(playerBag);
        }
        GameShopRecord gameShopRecord = new GameShopRecord();
        gameShopRecord.setNum(Integer.parseInt(token.getStr()));
        gameShopRecord.setUserId(Integer.parseInt(userId));
        gameShopRecord.setItemId(Integer.parseInt(token.getId()));
        gameShopRecordMapper.insert(gameShopRecord);
        userMapper.updateuser(user);
        baseResp.setSuccess(1);
        UserInfo info = new UserInfo();
        BeanUtils.copyProperties(user, info);
        //获取卡牌数据
        List<Characters> characterList = charactersMapper.selectByUserId(user.getUserId());
        info.setCharacterList(formateCharacter(characterList));
        baseResp.setData(info);
        baseResp.setSuccess(1);
        baseResp.setErrorMsg("领取成功");
        return baseResp;
    }


    @Override
    @Transactional
    @NoRepeatSubmit(limitSeconds = 1)
    public BaseResp giftExchangeCode(TokenDto token, HttpServletRequest request) throws Exception {
        BaseResp baseResp = new BaseResp();
        if (token == null || Xtool.isNull(token.getToken())) {
            baseResp.setSuccess(0);
            baseResp.setErrorMsg("登录过期");
            return baseResp;
        }
        String userId = token.getUserId();
//        String userId = (String) redisTemplate.opsForValue().get(token.getToken());
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
        if (gift.getRemainingQuantity() != -1) {
            gift.setRemainingQuantity(gift.getRemainingQuantity() - 1);
            gameGiftMapper.updateByPrimaryKey(gift);
        }
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
    @Transactional
    @NoRepeatSubmit(limitSeconds = 1)
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
        StarSynthesisMain starSynthesisMain = starSynthesisMainMapper.selectById(token.getId());
        if (user.getGold().subtract(starSynthesisMain.getExtraCost()).compareTo(BigDecimal.ZERO) <= 0) {
            baseResp.setSuccess(0);
            baseResp.setErrorMsg("合成金币不足");
            return baseResp;
        }
        user.setGold(user.getGold().subtract(starSynthesisMain.getExtraCost()));
        List<Characters> charactersList = new ArrayList<>();
        if (starSynthesisMain != null) {
            List<StarSynthesisMaterials> materials = starSynthesisMaterialsMapper.selectall(starSynthesisMain.getId());
            for (StarSynthesisMaterials material : materials) {
                Characters characters = charactersMapper.listById(token.getUserId(), material.getId());
                if (characters == null) {
                    baseResp.setSuccess(0);
                    baseResp.setErrorMsg("合成素材不足");
                    return baseResp;
                }
                Integer count = materials.stream().filter(x -> x.getId().equals(material.getId())).collect(Collectors.toList()).size();
                if (characters.getStackCount() + 1 < count) {
                    baseResp.setSuccess(0);
                    baseResp.setErrorMsg("合成素材不足");
                    return baseResp;
                }
                if (characters.getLv() < characters.getMaxLv()) {
                    baseResp.setSuccess(0);
                    baseResp.setErrorMsg("合成素材未满级");
                    return baseResp;
                }
                charactersList.add(characters);
            }
        }

        for (Characters characters : charactersList) {
            Characters characters1 = charactersMapper.listById(token.getUserId(), characters.getId());
            if (characters1.getStackCount() - 1 >= 0) {
                characters1.setStackCount(characters1.getStackCount() - 1);
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
//        ValueOperations opsForValue = redisTemplate.opsForValue();
        if (card1.getStar().compareTo(new BigDecimal(3)) > 0) {
//            Date date = new Date();
            GameNotice gameNotice = new GameNotice();
            gameNotice.setDescription("恭喜 " + user.getNickname() + " 图谱合成获得" + card1.getStar().stripTrailingZeros() + "星" + card1.getName());
            gameNoticeMapper.insert(gameNotice);
//            opsForValue.set("notice_" + date.getTime() + "", "恭喜 " + user.getNickname() + " 图谱合成获得" + card1.getStar().stripTrailingZeros() + "星" + card1.getName(), 3600 * 12, TimeUnit.SECONDS);
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
    @Transactional
//    @NoRepeatSubmit(limitSeconds = 1)
    public BaseResp findHechenCard(TokenDto token, HttpServletRequest request) throws Exception {
        BaseResp baseResp = new BaseResp();
        if (token == null || Xtool.isNull(token.getToken())) {
            baseResp.setSuccess(0);
            baseResp.setErrorMsg("登录过期");
            return baseResp;
        }
        String userId = token.getUserId();
        if (Xtool.isNull(userId)) {
            baseResp.setSuccess(0);
            baseResp.setErrorMsg("登录过期");
            return baseResp;
        }
        User user = userMapper.selectUserByUserId(Integer.parseInt(userId));
        List<String> ids = Arrays.asList(token.getStr().split(","));
        if (ids.size() < 5) {
            baseResp.setSuccess(0);
            baseResp.setErrorMsg("卡牌数量不足");
            return baseResp;
        }
        Map<String, Integer> countMap = ids.stream()
                .collect(Collectors.groupingBy(str -> str, Collectors.summingInt(e -> 1)));
        List<Characters> charactersList = new ArrayList<>();

        for (String id : countMap.keySet()) {
            Characters characters = charactersMapper.listById(token.getUserId(), id);
            Integer count = countMap.get(id);
            if (count > characters.getStackCount() + 1) {
                baseResp.setSuccess(0);
                baseResp.setErrorMsg("卡牌数量不足");
                return baseResp;
            }
            if (count > 1 && characters.getMaxLv() > 1) {
                baseResp.setSuccess(0);
                baseResp.setErrorMsg("卡牌未满级");
                return baseResp;
            }
            charactersList.add(characters);
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
        user.setGold(user.getGold().subtract(gold));
        for (Characters characters : charactersList) {
            Integer count = countMap.get(characters.getId());
            characters.setStackCount(characters.getStackCount() - count);
            if (characters.getStackCount() >= 0) {
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
        userMapper.updateuser(user);
        CardDto dto = new CardDto();
        dto.setHero(drawnCard);
//        ValueOperations opsForValue = redisTemplate.opsForValue();
        if (drawnCard.getStar().compareTo(new BigDecimal(3)) > 0) {
            GameNotice gameNotice = new GameNotice();
            gameNotice.setDescription("恭喜 " + user.getNickname() + " 合成召唤获得" + drawnCard.getStar().stripTrailingZeros() + "星" + drawnCard.getName());
            gameNoticeMapper.insert(gameNotice);
//            opsForValue.set("notice_" + date.getTime() + "", "恭喜 " + user.getNickname() + " 合成召唤获得" + drawnCard.getStar().stripTrailingZeros() + "星" + drawnCard.getName(), 3600 * 12, TimeUnit.SECONDS);
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
//        if (starList.size() != REQUIRED_SIZE) {
//            throw new IllegalArgumentException("星级集合必须包含" + REQUIRED_SIZE + "个元素");
//        }
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
//        String userId = (String) redisTemplate.opsForValue().get(token.getToken());
        String userId = token.getUserId();
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
        //等级以及初级礼包

//        game_gift_exchange_code
        List<GameGift> validGifts = gameGiftMapper.selectValidGifts();
        List<GameGift> validGifts2 = gameGiftMapper.selectValidGifts2(now, userId);
        validGifts.addAll(validGifts2);
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
            if ("4".equals(gift.getGiftType() + "") || "2".equals(gift.getGiftType() + "") || "5".equals(gift.getGiftType() + "")) {
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
//    @NoRepeatSubmit(limitSeconds = 1)
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
//        ValueOperations opsForValue = redisTemplate.opsForValue();
        if (drawnCard.getStar().compareTo(new BigDecimal(3)) > 0) {
            GameNotice gameNotice = new GameNotice();
            gameNotice.setDescription("恭喜 " + user.getNickname() + " 高级召唤获得" + drawnCard.getStar().stripTrailingZeros() + "星" + drawnCard.getName());
            gameNoticeMapper.insert(gameNotice);
//            Date date = new Date();
//            opsForValue.set("notice_" + date.getTime() + "", "恭喜 " + user.getNickname() + " 高级召唤获得" + drawnCard.getStar().stripTrailingZeros() + "星" + drawnCard.getName(), 3600 * 12, TimeUnit.SECONDS);
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
    public BaseResp danChouEq(TokenDto token, HttpServletRequest request) throws Exception {
        BaseResp baseResp = new BaseResp();
        String userId = token.getUserId();
        User user = userMapper.selectUserByUserId(Integer.parseInt(userId));
        //初始1星
        Double start = 1.0;
        if ("1".equals(token.getStr())) {
            BigDecimal gold = new BigDecimal(50000);
            if (gold.compareTo(user.getGold()) > 0) {
                baseResp.setSuccess(0);
                baseResp.setErrorMsg("银两不足");
                return baseResp;
            }
            user.setGold(user.getGold().subtract(gold));
            GamePlayerBag playerBag = gamePlayerBagMapper.goIntoListByIdAndItemId(userId, 13);
            if (playerBag == null || playerBag.getItemCount() < 1000) {
                baseResp.setSuccess(0);
                baseResp.setErrorMsg("材料不足");
                return baseResp;
            } else {
                if (playerBag.getItemCount() - 1000 > 0) {
                    playerBag.setItemCount(playerBag.getItemCount() - 1000);
                } else {
                    playerBag.setIsDelete("1");
                }
                gamePlayerBagMapper.updateById(playerBag);
            }
            start = 1 + 0.5 * (int) (Math.random() * 5);
        } else if ("2".equals(token.getStr())) {
            BigDecimal gold = new BigDecimal(150000);
            if (gold.compareTo(user.getGold()) > 0) {
                baseResp.setSuccess(0);
                baseResp.setErrorMsg("银两不足");
                return baseResp;
            }
            user.setGold(user.getGold().subtract(gold));
            GamePlayerBag playerBag = gamePlayerBagMapper.goIntoListByIdAndItemId(userId, 14);
            if (playerBag == null || playerBag.getItemCount() < 2000) {
                baseResp.setSuccess(0);
                baseResp.setErrorMsg("材料不足");
                return baseResp;
            } else {
                if (playerBag.getItemCount() - 2000 > 0) {
                    playerBag.setItemCount(playerBag.getItemCount() - 2000);
                } else {
                    playerBag.setIsDelete("1");
                }
                gamePlayerBagMapper.updateById(playerBag);
            }
            int temp = (int) (Math.random() * 3);
            start = temp == 2 ? 3 : 3.5; // 0/1→3，2→5 等价【3,3,5】
        } else if ("3".equals(token.getStr())) {
            BigDecimal gold = new BigDecimal(350000);
            if (gold.compareTo(user.getGold()) > 0) {
                baseResp.setSuccess(0);
                baseResp.setErrorMsg("银两不足");
                return baseResp;
            }
            user.setGold(user.getGold().subtract(gold));
            GamePlayerBag playerBag = gamePlayerBagMapper.goIntoListByIdAndItemId(userId, 15);
            if (playerBag == null || playerBag.getItemCount() < 5000) {
                baseResp.setSuccess(0);
                baseResp.setErrorMsg("材料不足");
                return baseResp;
            } else {
                if (playerBag.getItemCount() - 5000 > 0) {
                    playerBag.setItemCount(playerBag.getItemCount() - 5000);
                } else {
                    playerBag.setIsDelete("1");
                }
                gamePlayerBagMapper.updateById(playerBag);
            }
            int temp = (int) (Math.random() * 3);
            start = temp == 2 ? 3.5 : 4; // 0/1→3，2→5 等价【3,3,5】
        } else if ("4".equals(token.getStr())) {
            BigDecimal gold = new BigDecimal(550000);
            if (gold.compareTo(user.getGold()) > 0) {
                baseResp.setSuccess(0);
                baseResp.setErrorMsg("银两不足");
                return baseResp;
            }
            user.setGold(user.getGold().subtract(gold));
            GamePlayerBag playerBag = gamePlayerBagMapper.goIntoListByIdAndItemId(userId, 16);
            if (playerBag == null || playerBag.getItemCount() < 10000) {
                baseResp.setSuccess(0);
                baseResp.setErrorMsg("材料不足");
                return baseResp;
            } else {
                if (playerBag.getItemCount() - 10000 > 0) {
                    playerBag.setItemCount(playerBag.getItemCount() - 10000);
                } else {
                    playerBag.setIsDelete("1");
                }
                gamePlayerBagMapper.updateById(playerBag);
            }
        }
//        List<EqCard> cardList = eqCardMapper.selectByStr(token.getStr());
//        cardList = cardList.stream().filter(x -> x.getWeight() > 0).collect(Collectors.toList());
//        EqCardPool pool = new EqCardPool();
//        for (EqCard card : cardList) {
//            pool.addCard(card);
//        }
//        EqCard drawnCard = pool.draw();
        System.out.println(start);
        EqCard drawnCard = EquipmentGenerateUtil.generateEqCard(start);
        Map map2 = new HashMap();
        map2.put("name", drawnCard.getName());
        map2.put("star", drawnCard.getStar());
        map2.put("camp", drawnCard.getCamp());
        map2.put("profession", drawnCard.getProfession());
        map2.put("eq_type", drawnCard.getEqType());
        map2.put("eq_type2", drawnCard.getEqType2());
        map2.put("wl_atk", drawnCard.getWlAtk());
        map2.put("hy_atk", drawnCard.getHyAtk());
        map2.put("ds_atk", drawnCard.getDsAtk());
        map2.put("fd_atk", drawnCard.getFdAtk());
        map2.put("wl_def", drawnCard.getWlDef());
        map2.put("hy_def", drawnCard.getHyAtk());
        map2.put("ds_def", drawnCard.getDsAtk());
        map2.put("fd_def", drawnCard.getFdDef());
        map2.put("zl_def", drawnCard.getZlDef());
        List<EqCard> eqCards = eqCardMapper.selectByMap(map2);
        if (Xtool.isNotNull(eqCards)) {
            drawnCard.setId(eqCards.get(0).getId());
        } else {
            drawnCard.setId(drawnCard.getId());
            eqCardMapper.insert(drawnCard);
            drawnCard.setId(drawnCard.getId() + drawnCard.getUuid());
            eqCardMapper.updateById(drawnCard);
        }
        EqCharacters characters = new EqCharacters();
        characters.setStackCount(0);
        characters.setId(drawnCard.getId());
        characters.setLv(1);
        characters.setUserId(Integer.parseInt(userId));
        characters.setMaxLv(CardMaxLevelUtils.getMaxLevel(drawnCard.getName(), drawnCard.getStar().doubleValue()));
        eqCharactersMapper.insert(characters);
        EqCardDto dto = new EqCardDto();
        dto.setHero(drawnCard);
//        ValueOperations opsForValue = redisTemplate.opsForValue();
        if (drawnCard.getStar().compareTo(new BigDecimal(3)) > 0) {
            GameNotice gameNotice = new GameNotice();
            gameNotice.setDescription("恭喜 " + user.getNickname() + " 打造获得" + drawnCard.getStar().stripTrailingZeros() + "星" + drawnCard.getName());
            gameNoticeMapper.insert(gameNotice);
//            Date date = new Date();
//            opsForValue.set("notice_" + date.getTime() + "", "恭喜 " + user.getNickname() + " 打造获得" + drawnCard.getStar().stripTrailingZeros() + "星" + drawnCard.getName(), 3600 * 12, TimeUnit.SECONDS);
            EqCharactersRecord eqCharactersRecord = new EqCharactersRecord();
            eqCharactersRecord.setEqImg(drawnCard.getImg());
            eqCharactersRecord.setEqName(drawnCard.getName());
            eqCharactersRecord.setGetTime(new Date());
            eqCharactersRecord.setId(drawnCard.getId());
            eqCharactersRecord.setUserId(Integer.parseInt(userId));
            eqCharactersRecord.setUserName(user.getNickname());
            eqCharactersRecord.setStar(drawnCard.getStar());
            eqCharactersRecord.setImg(user.getGameImg());
            eqCharactersRecordMapper.insert(eqCharactersRecord);
        }
        userMapper.updateuser(user);
        List<EqCharacters> nowCharactersList = eqCharactersMapper.selectByUserId(Integer.parseInt(userId));
        dto.setCharacters(nowCharactersList);
        //卡池数量
        UserInfo info = new UserInfo();
        BeanUtils.copyProperties(user, info);
        info.setBronze(0);
        info.setDarkSteel(0);
        info.setPurpleGold(0);
        info.setCrystal(0);
        GamePlayerBag playerBag = gamePlayerBagMapper.goIntoListByIdAndItemId(userId, 13);
        if (playerBag != null) {
            info.setBronze(playerBag.getItemCount());
        }
        GamePlayerBag playerBag1 = gamePlayerBagMapper.goIntoListByIdAndItemId(userId, 14);
        if (playerBag1 != null) {
            info.setDarkSteel(playerBag1.getItemCount());
        }
        GamePlayerBag playerBag2 = gamePlayerBagMapper.goIntoListByIdAndItemId(userId, 15);
        if (playerBag2 != null) {
            info.setPurpleGold(playerBag2.getItemCount());
        }
        GamePlayerBag playerBag3 = gamePlayerBagMapper.goIntoListByIdAndItemId(userId, 16);
        if (playerBag3 != null) {
            info.setCrystal(playerBag3.getItemCount());
        }
        baseResp.setSuccess(1);
        Map map = new HashMap();
        map.put("user", info);
        map.put("dto", dto);
        baseResp.setData(map);
        baseResp.setErrorMsg("打造成功");
        return baseResp;
    }

    @Override
    @Transactional
    @NoRepeatSubmit(limitSeconds = 1)
    public BaseResp characteSell(TokenDto token, HttpServletRequest request) throws Exception {
        BaseResp baseResp = new BaseResp();
        String userId = token.getUserId();
        User user = userMapper.selectUserByUserId(Integer.parseInt(userId));
        Characters characters1 = charactersMapper.listById(userId, token.getId());
        if (characters1 == null) {
            baseResp.setErrorMsg("卡牌已售罄请勿重复出售");
            baseResp.setSuccess(0);
            return baseResp;
        }
        if (characters1.getStackCount() - 1 >= 0) {
            characters1.setStackCount(characters1.getStackCount() - 1);
        } else {
            characters1.setIsDelete("1");
        }
        charactersMapper.updateByPrimaryKey(characters1);
        BigDecimal gold = new BigDecimal(1000);
        if ("104".equals(token.getId())) {
            gold = new BigDecimal(999999);
        } else if ("1082".equals(token.getId())) {
            gold = new BigDecimal(99999);
        } else if ("1091".equals(token.getId())) {
            gold = new BigDecimal(99999);
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
//        ValueOperations opsForValue = redisTemplate.opsForValue();
        if (drawnCard.getStar().compareTo(new BigDecimal(3)) > 0) {
            GameNotice gameNotice = new GameNotice();
            gameNotice.setDescription("恭喜 " + user.getNickname() + " 魂珠召唤获得" + drawnCard.getStar().stripTrailingZeros() + "星" + drawnCard.getName());
            gameNoticeMapper.insert(gameNotice);
//            Date date = new Date();
//            opsForValue.set("notice_" + date.getTime() + "", "恭喜 " + user.getNickname() + " 魂珠召唤获得" + drawnCard.getStar().stripTrailingZeros() + "星" + drawnCard.getName(), 3600 * 12, TimeUnit.SECONDS);
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
//    @NoRepeatSubmit(limitSeconds = 1)
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
//        ValueOperations opsForValue = redisTemplate.opsForValue();
        for (Card drawnCard : drawnCards) {
            if (drawnCard.getStar().compareTo(new BigDecimal(3)) > 0) {
                GameNotice gameNotice = new GameNotice();
                gameNotice.setDescription("恭喜 " + user.getNickname() + " 高级召唤获得" + drawnCard.getStar().stripTrailingZeros() + "星" + drawnCard.getName());
                gameNoticeMapper.insert(gameNotice);
//                Date date = new Date();
//                opsForValue.set("notice_" + date.getTime(), "恭喜 " + user.getNickname() + " 高级召唤获得" + drawnCard.getStar().stripTrailingZeros() + "星" + drawnCard.getName(), 3600 * 12, TimeUnit.SECONDS);
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

    @Override
    @Transactional
    @NoRepeatSubmit(limitSeconds = 1)
    public BaseResp soulShiChou(TokenDto token, HttpServletRequest request) throws Exception {
        BaseResp baseResp = new BaseResp();
        String userId = token.getUserId();
        User user = userMapper.selectUserByUserId(Integer.parseInt(userId));

        BigDecimal number = new BigDecimal("300");
        if (user.getSoul().compareTo(number) < 0) {
            baseResp.setSuccess(0);
            baseResp.setErrorMsg("当前魂珠小于300");
            return baseResp;
        } else {
            user.setSoul(user.getSoul().subtract(number));
            userMapper.updateuser(user);
        }
        List<Card> cardList = cardMapper.selectAll();
        List<Card> cardList4 = cardList.stream().filter(x -> x.getWeight() > 0 && x.getStar().compareTo(new BigDecimal(4)) == 0).collect(Collectors.toList());
        cardList = cardList.stream().filter(x -> x.getWeight() > 0 && x.getStar().compareTo(new BigDecimal(4)) < 0).collect(Collectors.toList());
        CardPool pool = new CardPool();
        for (Card card : cardList) {
            pool.addCard(card);
        }
        List<Card> drawnCards = new ArrayList<>();
        for (int i = 0; i < 10; i++) {
            Card drawnCard = pool.draw();
            //如果是20倍则获取的女娲石
            if (ProbabilityBooleanUtils.randomByProbability(0.10) && i == 1) {
                Random random = new Random();
                int randomIndex = random.nextInt(cardList.size()); // 生成 0 到 size-1 的随机整数
                drawnCards.add(cardList4.get(randomIndex));
            } else {
                drawnCards.add(drawnCard);
            }
        }
//        ValueOperations opsForValue = redisTemplate.opsForValue();
        for (Card drawnCard : drawnCards) {
            if (drawnCard.getStar().compareTo(new BigDecimal(3)) > 0) {
                GameNotice gameNotice = new GameNotice();
                gameNotice.setDescription("恭喜 " + user.getNickname() + " 魂珠召唤获得" + drawnCard.getStar().stripTrailingZeros() + "星" + drawnCard.getName());
                gameNoticeMapper.insert(gameNotice);
//                Date date = new Date();
//                opsForValue.set("notice_" + date.getTime(), "恭喜 " + user.getNickname() + " 高级召唤获得" + drawnCard.getStar().stripTrailingZeros() + "星" + drawnCard.getName(), 3600 * 12, TimeUnit.SECONDS);
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
//        String userId = (String) redisTemplate.opsForValue().get(token.getToken());
        String userId = token.getId();
        if (Xtool.isNull(userId)) {
            baseResp.setSuccess(0);
            baseResp.setErrorMsg("登录过期");
            return baseResp;
        }
        User user = userMapper.selectUserByUserId(Integer.parseInt(userId));
        if (user.getHuoliCount() - 10 < 0) {
            baseResp.setSuccess(0);
            baseResp.setErrorMsg("活力不足");
            return baseResp;
        }
        //自己的战队
        List<Characters> leftCharacter = charactersMapper.goIntoListById(user.getUserId() + "");
        if (Xtool.isNull(leftCharacter)) {
            baseResp.setSuccess(0);
            baseResp.setErrorMsg("你没有配置战队无法战斗");
            return baseResp;
        }
        for (Characters characters : leftCharacter) {
            List<EqCharacters> eqCharacters = eqCharactersMapper.listByGoOn(user.getUserId() + "", characters.getId());
            if (Xtool.isNotNull(eqCharacters)) {
                characters.setEqCharactersList(formateEqCharacter(eqCharacters));
            }
        }
        Collections.sort(leftCharacter, Comparator.comparing(Characters::getGoIntoNum));
        //对手战队
        User user1 = userMapper.selectUserByUserId(Integer.parseInt(token.getUserId()));
        List<Characters> rightCharacter = charactersMapper.goIntoListById(token.getUserId() + "");
        if (Xtool.isNull(rightCharacter)) {
            Card card = cardMapper.selectByid(3);
            if (card == null) {
                baseResp.setErrorMsg("服务器异常联想管理员");
                baseResp.setSuccess(0);
                return baseResp;
            }
            Characters characters = new Characters();
            BeanUtils.copyProperties(card, characters);
            characters.setId("1002");
            characters.setGoIntoNum(1);
            characters.setLv(1);
            characters.setUserId(Integer.parseInt(userId));
            characters.setStar(new BigDecimal(1));
            characters.setMaxLv(CardMaxLevelUtils.getMaxLevel(card.getName(), card.getStar().doubleValue()));
            rightCharacter.add(characters);
        }

        baseResp.setSuccess(1);
        Battle battle = this.battle(leftCharacter, Integer.parseInt(userId), user.getNickname(), rightCharacter, Integer.parseInt(token.getUserId()), user1.getNickname(), user.getGameImg(), "1");
        if (battle.getIsWin() == 0) {
            user.setWinCount(user.getWinCount() + 1);
            if (user1.getGameRanking() < user.getGameRanking()) {
                Integer gameRank = user.getGameRanking();
                user.setGameRanking(user1.getGameRanking());
                user1.setGameRanking(gameRank);
                userMapper.updateuser(user1);
            }
        }
        user.setHuoliCount(user.getHuoliCount() - 10);
        userMapper.updateuser(user);
        baseResp.setData(battle);
        return baseResp;
    }

    @Override
    public BaseResp blessing(TokenDto token, HttpServletRequest request) throws Exception {
        //先获取当前用户战队
        BaseResp baseResp = new BaseResp();
        if (token == null || Xtool.isNull(token.getToken())) {
            baseResp.setSuccess(0);
            baseResp.setErrorMsg("登录过期");
            return baseResp;
        }
        String userId = token.getUserId();
        if (Xtool.isNull(userId)) {
            baseResp.setSuccess(0);
            baseResp.setErrorMsg("登录过期");
            return baseResp;
        }
        Map map = new HashMap();
        map.put("receiver_id", token.getId());
        LocalDate currentDate = LocalDate.now();
        String dateStr = currentDate.format(DateTimeFormatter.ISO_LOCAL_DATE);
        map.put("send_time", dateStr);
        //先判断是否
        List<FriendBlessing> friendBlessings = friendBlessingMapper.selectByMap(map);
        if (friendBlessings.size() >= 50) {
            baseResp.setSuccess(0);
            baseResp.setErrorMsg("对面祝福已满");
            return baseResp;
        }
        Map map2 = new HashMap();
        map2.put("sender_id", userId);
        map2.put("send_time", dateStr);
        //先判断是否
        List<FriendBlessing> friendBlessings2 = friendBlessingMapper.selectByMap(map2);
        if (friendBlessings2.size() >= 15) {
            baseResp.setSuccess(0);
            baseResp.setErrorMsg("今日15次祝福已送完");
            return baseResp;
        }
        map2.put("receiver_id", token.getId());
        //先判断是否
        List<FriendBlessing> friendBlessings3 = friendBlessingMapper.selectByMap(map2);
        if (Xtool.isNotNull(friendBlessings3)) {
            baseResp.setSuccess(0);
            baseResp.setErrorMsg("请勿重复祝福");
            return baseResp;
        }
        User user = userMapper.selectUserByUserId(Integer.parseInt(userId));
        user.setHuoliCount(user.getHuoliCount() + 10);
        user.setTiliCount(user.getTiliCount() + 10);
        userMapper.updateuser(user);
        FriendBlessing friendBlessing = new FriendBlessing();
        friendBlessing.setIsRead(0);
        friendBlessing.setContent("好友祝福");
        friendBlessing.setReceiverId(Integer.parseInt(token.getId()));
        friendBlessing.setSenderId(Integer.parseInt(userId));
        friendBlessing.setSendTime(new Date());
        friendBlessingMapper.insert(friendBlessing);
        User user1 = userMapper.selectUserByUserId(Integer.parseInt(userId));
        UserInfo userInfo = new UserInfo();
        BeanUtils.copyProperties(user1, userInfo);
        baseResp.setSuccess(1);
        baseResp.setData(userInfo);
        baseResp.setErrorMsg("仙缘祝福已送达！\n 仙友已经收到你的心意～\n 体力 + 10、活力 + 10 \n 已注入你的仙躯，可继续闯荡三界！");
        return baseResp;
    }

    @Override
    public BaseResp reviceblessing(TokenDto token, HttpServletRequest request) throws Exception {
        //先获取当前用户战队
        BaseResp baseResp = new BaseResp();
        if (token == null || Xtool.isNull(token.getToken())) {
            baseResp.setSuccess(0);
            baseResp.setErrorMsg("登录过期");
            return baseResp;
        }
        List<User> users = friendRelationMapper.findByid(token.getUserId(), 1, "1");
        List<UserInfo> userInfoList = new ArrayList<>();
        //可以凝聚的体力、活力
        Integer count = 0;
        Integer total = users.size();
        for (User user : users) {
            UserInfo userInfo = new UserInfo();
            BeanUtils.copyProperties(user, userInfo);
            userInfoList.add(userInfo);
            if (user.getNj() == 0) {
                count = count + 2;
            }
        }
        //今日送出的体力
        Map map = new HashMap();

        baseResp.setSuccess(1);
        Map map2 = new HashMap();
        LocalDate currentDate = LocalDate.now();
        String dateStr = currentDate.format(DateTimeFormatter.ISO_LOCAL_DATE);
        map2.put("sender_id", token.getUserId());
        map2.put("send_time", dateStr);
        //先判断是否
        List<FriendBlessing> friendBlessings2 = friendBlessingMapper.selectByMap(map2);
        map.put("friends", userInfoList);
        map.put("count", count);
        map.put("total", total);
        map.put("sendCount", friendBlessings2.size());
        baseResp.setData(map);
        return baseResp;
    }

    @Override
    public BaseResp njblessing(TokenDto token, HttpServletRequest request) throws Exception {
        //先获取当前用户战队
        BaseResp baseResp = new BaseResp();
        if (token == null || Xtool.isNull(token.getToken())) {
            baseResp.setSuccess(0);
            baseResp.setErrorMsg("登录过期");
            return baseResp;
        }
        User user1 = userMapper.selectUserByUserId(Integer.parseInt(token.getUserId()));
        if (user1 == null) {
            baseResp.setSuccess(0);
            baseResp.setErrorMsg("登录过期");
            return baseResp;
        }
        Map map2 = new HashMap();
        LocalDate currentDate = LocalDate.now();
        String dateStr = currentDate.format(DateTimeFormatter.ISO_LOCAL_DATE);
        map2.put("receiver_id", token.getUserId());
        map2.put("send_time", dateStr);
        map2.put("is_read", 0);
        //先判断是否
        List<FriendBlessing> f = friendBlessingMapper.selectByMap(map2);
        if (Xtool.isNull(f)) {
            baseResp.setSuccess(0);
            baseResp.setErrorMsg("没有可凝聚祝福");
            return baseResp;
        }
        for (FriendBlessing friendBlessing : f) {
            friendBlessing.setIsRead(1);
            friendBlessingMapper.updateById(friendBlessing);
        }
        //判断凝聚点是否正常
        user1.setTiliCount(token.getTiLi() + f.size() * 2);
        user1.setTiliCountTime(new Date());
        user1.setHuoliCount(token.getHuoLi() + f.size() * 2);
        user1.setHuoliCountTime(new Date());
        userMapper.updateuser(user1);
        Map map = new HashMap();
        map.put("tiLi", token.getTiLi() + f.size() * 2);
        map.put("huoLi", token.getHuoLi() + f.size() * 2);
        baseResp.setSuccess(1);
        baseResp.setData(map);
        baseResp.setErrorMsg("凝聚成功");
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
        for (Characters characters : leftCharacter) {
            List<EqCharacters> eqCharacters = eqCharactersMapper.listByGoOn(user.getUserId() + "", characters.getId());
            if (Xtool.isNotNull(eqCharacters)) {
                characters.setEqCharactersList(formateEqCharacter(eqCharacters));
            }
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
        for (Characters characters : rightCharacter) {
            List<EqCharacters> eqCharacters = eqCharactersMapper.listByGoOn(user1.getUserId() + "", characters.getId());
            if (Xtool.isNotNull(eqCharacters)) {
                characters.setEqCharactersList(formateEqCharacter(eqCharacters));
            }
        }
        baseResp.setSuccess(1);
        Battle battle = this.battle(leftCharacter, user.getUserId(), user.getNickname(), rightCharacter, user1.getUserId(), user1.getNickname(), user.getGameImg(), "3");
        baseResp.setData(battle);
        return baseResp;
    }

    @Override
    public BaseResp ranking(TokenDto token, HttpServletRequest request) throws Exception {
        BaseResp baseResp = new BaseResp();
        baseResp.setData(userMapper.selectUserByUserId(Integer.parseInt(token.getUserId())));
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
    public BaseResp mapRanking100(TokenDto token, HttpServletRequest request) throws Exception {
        BaseResp baseResp = new BaseResp();
        Map map = new HashMap();
        List<User> tanglangRanking = userMapper.getMapRanking100();
        map.put("tanglangRanking", tanglangRanking);
        List<User> qingtongRanking = userMapper.getBronzeRanking100("bronzetower");
        map.put("qingtongRanking", qingtongRanking);
        List<User> baiyingRanking = userMapper.getBronzeRanking100("silvertower");
        map.put("baiyingRanking", baiyingRanking);
        List<User> huangjinRanking = userMapper.getBronzeRanking100("goldentower");
        map.put("huangjinRanking", huangjinRanking);
        baseResp.setData(map);
        baseResp.setSuccess(1);
        return baseResp;
    }

    @Override
    public BaseResp arenaRanking100(TokenDto token, HttpServletRequest request) throws Exception {
        BaseResp baseResp = new BaseResp();
        baseResp.setData(userMapper.arenaRanking100(token.getFinalLevel(), ArenaWeekUtils.getCurrentUniqueWeekNum(new Date())));
        baseResp.setSuccess(1);
        return baseResp;
    }


    /**
     * 膜拜功能 - 修复后版本
     * 核心优化：事务控制、空值校验、逻辑修正、异常处理
     */
    @Override
    @NoRepeatSubmit(limitSeconds = 1)
    @Transactional(rollbackFor = Exception.class) // 增加事务控制
    public BaseResp mobai(TokenDto token, HttpServletRequest request) throws Exception {
        BaseResp baseResp = new BaseResp();

        // 1. 基础参数校验
        if (token == null || Xtool.isNull(token.getToken())) {
            baseResp.setSuccess(0);
            baseResp.setErrorMsg("登录过期");
            return baseResp;
        }

        String userId = token.getId();
        if (Xtool.isNull(userId)) {
            baseResp.setSuccess(0);
            baseResp.setErrorMsg("登录过期");
            return baseResp;
        }

        // 2. 业务参数校验（修正错误提示）
        Integer finalLevel = token.getFinalLevel();
        if (finalLevel == null || finalLevel < 1 || finalLevel > 3) {
            baseResp.setSuccess(0);
            baseResp.setErrorMsg("膜拜等级参数异常");
            return baseResp;
        }

        // 3. 查询当前用户并校验存在性
        Integer currentUserId;
        try {
            currentUserId = Integer.parseInt(userId);
        } catch (NumberFormatException e) {
            baseResp.setSuccess(0);
            baseResp.setErrorMsg("用户ID格式异常");
            return baseResp;
        }
        User user = userMapper.selectUserByUserId(currentUserId);
        if (user == null) {
            baseResp.setSuccess(0);
            baseResp.setErrorMsg("当前用户不存在");
            return baseResp;
        }

        // 4. 校验今日是否已膜拜（修正逻辑，避免空指针）
        boolean hasMobaiToday = false;
        switch (finalLevel) {
            case 1:
                hasMobaiToday = user.getWeiwan1Time() != null && isDateToday(user.getWeiwan1Time());
                break;
            case 2:
                hasMobaiToday = user.getWeiwan2Time() != null && isDateToday(user.getWeiwan2Time());
                break;
            case 3:
                hasMobaiToday = user.getWeiwan3Time() != null && isDateToday(user.getWeiwan3Time());
                break;
        }
        if (hasMobaiToday) {
            baseResp.setSuccess(0);
            baseResp.setErrorMsg("你今日已膜拜");
            return baseResp;
        }

        // 5. 校验被膜拜用户参数
        String beMobaiUserIdStr = token.getUserId();
        if (Xtool.isNull(beMobaiUserIdStr)) {
            baseResp.setSuccess(0);
            baseResp.setErrorMsg("被膜拜用户ID为空");
            return baseResp;
        }
        Integer beMobaiUserId;
        try {
            beMobaiUserId = Integer.parseInt(beMobaiUserIdStr);
        } catch (NumberFormatException e) {
            baseResp.setSuccess(0);
            baseResp.setErrorMsg("被膜拜用户ID格式异常");
            return baseResp;
        }

        // 6. 查询被膜拜用户并校验存在性
        User user2 = userMapper.selectUserByUserId(beMobaiUserId);
        if (user2 == null) {
            baseResp.setSuccess(0);
            baseResp.setErrorMsg("被膜拜用户不存在");
            return baseResp;
        }

        // 7. 更新当前用户膜拜时间（修正逻辑）
        switch (finalLevel) {
            case 1:
                user.setWeiwan1Time(new Date());
                break;
            case 2:
                user.setWeiwan2Time(new Date());
                break;
            case 3:
                user.setWeiwan3Time(new Date());
                break;
        }

        // 8. 更新当前用户金币（避免空指针）
        BigDecimal gold = user.getGold() == null ? BigDecimal.ZERO : user.getGold();
        user.setGold(gold.add(new BigDecimal(5000)));
        userMapper.updateuser(user);

        // 9. 更新被膜拜用户次数（避免空指针）
        Integer weiwanCount = user2.getWeiwanCount() == null ? 0 : user2.getWeiwanCount();
        user2.setWeiwanCount(weiwanCount + 1);
        userMapper.updateuser(user2);

        // 10. 组装返回数据
        UserInfo userInfo = new UserInfo();
        userInfo.setWeiwanCount(user2.getWeiwanCount());
        userInfo.setGold(user.getGold());
        baseResp.setData(userInfo);
        baseResp.setSuccess(1);
        baseResp.setErrorMsg("瞻仰大神风姿，幸得垂青！奖励5000金币，望君再攀高峰～");

        return baseResp;
    }

    @Override
    public BaseResp bagItemList(TokenDto token, HttpServletRequest request) throws Exception {
        //先获取当前用户战队
        BaseResp baseResp = new BaseResp();
        if (token == null || Xtool.isNull(token.getToken())) {
            baseResp.setSuccess(0);
            baseResp.setErrorMsg("登录过期");
            return baseResp;
        }
//        String userId = (String) redisTemplate.opsForValue().get(token.getToken());
        String userId = token.getUserId();
        if (Xtool.isNull(userId)) {
            baseResp.setSuccess(0);
            baseResp.setErrorMsg("登录过期");
            return baseResp;
        }
//        User user = userMapper.selectUserByUserId(Integer.parseInt(userId));
        if ("1".equals(token.getStr())) {
            baseResp.setSuccess(1);
            baseResp.setData(gamePlayerBagMapper.goIntoListById(userId));
            return baseResp;
        } else if ("2".equals(token.getStr())) {

        } else {

        }
        return null;
    }

    @Override
    public BaseResp equipmentNew(TokenDto token, HttpServletRequest request) throws Exception {
        EqCharactersRecord record = eqCharactersRecordMapper.getEquipmentNew();
        record.setTimeStr(formatTime(record.getGetTime()));
        BaseResp baseResp = new BaseResp();
        baseResp.setSuccess(1);
        baseResp.setData(record);
        return baseResp;
    }

    @Override
    public BaseResp equipmentMessageList(TokenDto token, HttpServletRequest request) throws Exception {
        List<EqCharactersRecord> record = eqCharactersRecordMapper.getEquipmentList();
        for (EqCharactersRecord eqCharactersRecord : record) {
            eqCharactersRecord.setTimeStr(formatTime(eqCharactersRecord.getGetTime()));
        }

        BaseResp baseResp = new BaseResp();
        baseResp.setSuccess(1);
        baseResp.setData(record);
        return baseResp;
    }

    @Override
    public BaseResp ascensionPillDetai(TokenDto token, HttpServletRequest request) throws Exception {
        //先获取当前用户战队
        BaseResp baseResp = new BaseResp();
        if (token == null || Xtool.isNull(token.getToken())) {
            baseResp.setSuccess(0);
            baseResp.setErrorMsg("登录过期");
            return baseResp;
        }
//        String userId = (String) redisTemplate.opsForValue().get(token.getToken());
        String userId = token.getUserId();
        if (Xtool.isNull(userId)) {
            baseResp.setSuccess(0);
            baseResp.setErrorMsg("登录过期");
            return baseResp;
        }
//        18
//        19
//        20
//        21
//        22
//        23
//        User user = userMapper.selectUserByUserId(Integer.parseInt(userId));
        baseResp.setSuccess(1);
        Map map=new HashMap();
        Map itemMap = new HashMap();
        itemMap.put("item_id", "23");
        itemMap.put("user_id", userId);
        itemMap.put("is_delete", "0");
        List<GamePlayerBag> playerBagList = gamePlayerBagMapper.selectByMap(itemMap);
        if (Xtool.isNotNull(playerBagList)){
            map.put("p1",playerBagList.get(0).getItemCount());
        }else {
            map.put("p1",0);
        }
        Map itemMap2 = new HashMap();
        itemMap2.put("item_id", "22");
        itemMap2.put("user_id", userId);
        itemMap2.put("is_delete", "0");
        List<GamePlayerBag> playerBagList2 = gamePlayerBagMapper.selectByMap(itemMap2);
        if (Xtool.isNotNull(playerBagList2)){
            map.put("p3",playerBagList2.get(0).getItemCount());
        }else {
            map.put("p3",0);
        }

        Map itemMap3 = new HashMap();
        itemMap3.put("item_id", "21");
        itemMap3.put("user_id", userId);
        itemMap3.put("is_delete", "0");
        List<GamePlayerBag> playerBagList3 = gamePlayerBagMapper.selectByMap(itemMap3);
        if (Xtool.isNotNull(playerBagList3)){
            map.put("p2",playerBagList3.get(0).getItemCount());
        }else {
            map.put("p2",0);
        }


        Map itemMap4 = new HashMap();
        itemMap4.put("item_id", "20");
        itemMap4.put("user_id", userId);
        itemMap4.put("is_delete", "0");
        List<GamePlayerBag> playerBagList4 = gamePlayerBagMapper.selectByMap(itemMap4);
        if (Xtool.isNotNull(playerBagList4)){
            map.put("p5",playerBagList4.get(0).getItemCount());
        }else {
            map.put("p5",0);
        }


        Map itemMap5 = new HashMap();
        itemMap5.put("item_id", "19");
        itemMap5.put("user_id", userId);
        itemMap5.put("is_delete", "0");
        List<GamePlayerBag> playerBagList5 = gamePlayerBagMapper.selectByMap(itemMap5);
        if (Xtool.isNotNull(playerBagList5)){
            map.put("p4",playerBagList5.get(0).getItemCount());
        }else {
            map.put("p4",0);
        }


        Map itemMap6 = new HashMap();
        itemMap6.put("item_id", "18");
        itemMap6.put("user_id", userId);
        itemMap6.put("is_delete", "0");
        List<GamePlayerBag> playerBagList6 = gamePlayerBagMapper.selectByMap(itemMap6);
        if (Xtool.isNotNull(playerBagList6)){
            map.put("p6",playerBagList6.get(0).getItemCount());
        }else {
            map.put("p6",0);
        }

        PillRobRecord robRecord = pillRobRecordMapper.seletByUserId2(token.getUserId());
        if (robRecord!=null){
            robRecord.setTimeStr(formatTime(robRecord.getCreateTime()));
        }
        map.put("robRecord",robRecord);
        baseResp.setData(map);
        return baseResp;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    @NoRepeatSubmit(limitSeconds = 1)
    public BaseResp useBagItem(TokenDto token, HttpServletRequest request) throws Exception {
        BaseResp baseResp = new BaseResp();
        // 1. 基础参数校验（同之前）
        if (token == null || Xtool.isNull(token.getToken()) || Xtool.isNull(token.getUserId())) {
            baseResp.setSuccess(0);
            baseResp.setErrorMsg("登录过期");
            return baseResp;
        }
        String userId = token.getUserId();
        Integer itemId = null;
        try {
            itemId = Integer.parseInt(token.getId());
        } catch (NumberFormatException e) {
            baseResp.setSuccess(0);
            baseResp.setErrorMsg("物品ID格式错误");
            return baseResp;
        }

        // 2. 获取用户信息
        User user = userMapper.selectUserByUserId(Integer.parseInt(userId));
        if (user == null) {
            baseResp.setSuccess(0);
            baseResp.setErrorMsg("用户不存在");
            return baseResp;
        }

        // 3. 分布式锁实现（适配基础版setIfAbsent）
        String lockKey = "USE_BAG_ITEM_" + userId + "_" + itemId;
//        Boolean lockSuccess = false;
        try {
            Object countObj = redisTemplate.opsForValue().get(lockKey);
            Long currentCount = null;

// 安全转换：处理 null/字符串/数字等情况
            if (countObj != null) {
                if (countObj instanceof Long) {
                    currentCount = (Long) countObj;
                } else if (countObj instanceof String) {
                    try {
                        currentCount = Long.parseLong((String) countObj);
                    } catch (NumberFormatException e) {
                        // 解析失败，视为无效计数，重置为0
                        currentCount = 0L;
                    }
                }
            }

            if (currentCount == null) {
                // 首次请求，初始化计数并设置过期时间
                redisTemplate.opsForValue().set(lockKey, "1", 1, TimeUnit.SECONDS);
            } else {
                // 超过阈值，抛出异常
                baseResp.setErrorMsg("操作过于频繁");
                baseResp.setSuccess(0);
                return baseResp;
            }

            // 4. 校验并扣减背包物品
            Map<String, Object> map = new HashMap<>();
            map.put("item_id", itemId);
            map.put("user_id", userId);
            map.put("is_delete", "0");
            List<GamePlayerBag> playerBagList = gamePlayerBagMapper.selectByMap(map);
            if (Xtool.isNull(playerBagList)) {
                baseResp.setSuccess(0);
                baseResp.setErrorMsg("物品已用完");
                return baseResp;
            }
            GamePlayerBag playerBag = playerBagList.get(0);
            if (playerBag.getItemCount() <= 0) {
                baseResp.setSuccess(0);
                baseResp.setErrorMsg("物品数量不足");
                return baseResp;
            }
            // 扣减物品数量
            if (playerBag.getItemCount() - 1 > 0) {
                playerBag.setItemCount(playerBag.getItemCount() - 1);
            } else {
                playerBag.setIsDelete("1");
            }
            gamePlayerBagMapper.updateById(playerBag);

            // 5. 处理物品使用逻辑（复用之前的封装方法）
            handleBagItemUse(itemId, user, userId);

            // 6. 更新用户信息并返回结果
            userMapper.updateuser(user);
            User user1 = userMapper.selectUserByUserId(Integer.parseInt(userId));
            UserInfo userInfo = new UserInfo();
            BeanUtils.copyProperties(user1, userInfo);
            baseResp.setSuccess(1);
            List<Characters> characterList = charactersMapper.selectByUserId(user.getUserId());
            userInfo.setCharacterList(formateCharacter(characterList));
            baseResp.setData(userInfo);
            baseResp.setErrorMsg("使用成功");
        } finally {
            // 释放锁（只有加锁成功的线程才释放）
            redisTemplate.delete(lockKey);
        }
        return baseResp;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    @NoRepeatSubmit(limitSeconds = 1)
    public BaseResp chongZhiTower(TokenDto token, HttpServletRequest request) throws Exception {
        BaseResp baseResp = new BaseResp();
        // 1. 基础参数校验（同之前）
        if (token == null || Xtool.isNull(token.getToken()) || Xtool.isNull(token.getUserId())) {
            baseResp.setSuccess(0);
            baseResp.setErrorMsg("登录过期");
            return baseResp;
        }
        String userId = token.getUserId();

        // 2. 获取用户信息
        User user = userMapper.selectUserByUserId(Integer.parseInt(userId));
        if (user == null) {
            baseResp.setSuccess(0);
            baseResp.setErrorMsg("用户不存在");
            return baseResp;
        }

        //先使用用户本身
        if (user.getChongzhiTower() > 0) {
            user.setChongzhiTower(user.getChongzhiTower() - 1);
            user.setBronze1(1);
            user.setSilvertower(1);
            user.setGoldentower(1);
            userMapper.updateuser(user);
            baseResp.setSuccess(1);
            UserInfo info = new UserInfo();
            Map map = new HashMap();
            map.put("userInfo", info);
            BeanUtils.copyProperties(user, info);
            ImageLevelResult result10 = LevelImageCalculator.calculate(user.getBronze1());
            map.put("positionInImage", result10.getPositionInImage() - 1);
            map.put("currentImageNumbers", result10.getCurrentImageNumbers());
            map.put("nextImageNumbers", result10.getNextImageNumbers());
            baseResp.setData(map);
            return baseResp;
        }
        // 4. 校验并扣减背包物品
        Map<String, Object> map2 = new HashMap<>();
        map2.put("item_id", 17);
        map2.put("user_id", userId);
        map2.put("is_delete", "0");
        List<GamePlayerBag> playerBagList = gamePlayerBagMapper.selectByMap(map2);
        if (Xtool.isNull(playerBagList)) {
            baseResp.setSuccess(0);
            baseResp.setErrorMsg("物品已用完");
            return baseResp;
        }
        GamePlayerBag playerBag = playerBagList.get(0);
        if (playerBag.getItemCount() <= 0) {
            baseResp.setSuccess(0);
            baseResp.setErrorMsg("物品数量不足");
            return baseResp;
        }
        // 扣减物品数量
        if (playerBag.getItemCount() - 1 > 0) {
            playerBag.setItemCount(playerBag.getItemCount() - 1);
        } else {
            playerBag.setIsDelete("1");
        }
        gamePlayerBagMapper.updateById(playerBag);
        user.setBronze1(1);
        user.setSilvertower(1);
        user.setGoldentower(1);
        userMapper.updateuser(user);
        baseResp.setSuccess(1);
        UserInfo info = new UserInfo();
        Map map = new HashMap();
        map.put("userInfo", info);
        BeanUtils.copyProperties(user, info);
        ImageLevelResult result10 = LevelImageCalculator.calculate(user.getBronze1());
        map.put("positionInImage", result10.getPositionInImage() - 1);
        map.put("currentImageNumbers", result10.getCurrentImageNumbers());
        map.put("nextImageNumbers", result10.getNextImageNumbers());
        baseResp.setData(map);
        return baseResp;
    }

    /**
     * 处理不同物品的使用逻辑（封装冗余代码）
     */
    private void handleBagItemUse(Integer itemId, User user, String userId) {
        switch (itemId) {
            case 1: // 刷新符
                Calendar calendar = Calendar.getInstance();
                calendar.add(Calendar.MINUTE, -40);
                user.setShopUpdate(calendar.getTime());
                break;
            case 17: // 特殊道具
                user.setBronze1(1);
                user.setSilvertower(1);
                user.setGoldentower(1);
                break;
            case 2: // 活力药水
                user.setHuoliCount(user.getHuoliCount() + 100);
                break;
            case 3: // 体力药水
                user.setTiliCount(user.getTiliCount() + 100);
                break;
            case 4: // 体活力补给包
                addBagItem(userId, 1, 2); // 刷新券+2
                addBagItem(userId, 2, 2); // 活力药水+2
                break;
            case 5: // 体力续航包
                addBagItem(userId, 1, 2); // 刷新券+2
                addBagItem(userId, 3, 2); // 体力药水+2
                break;
            case 6: // 活力袋
                addBagItem(userId, 1, 1); // 刷新券+1
                addBagItem(userId, 2, 1); // 活力药水+1
                break;
            case 7: // 体力续航包（小）
                addBagItem(userId, 1, 1); // 刷新券+1
                addBagItem(userId, 3, 1); // 体力药水+1
                break;
            case 8: // 金币包1
                addBagItem(userId, 1, 1); // 刷新券+1
                user.setGold(user.getGold().add(new BigDecimal("10000")));
                break;
            case 9: // 金币包2
                addBagItem(userId, 1, 1); // 刷新券+1
                user.setGold(user.getGold().add(new BigDecimal("50000")));
                break;
            case 10: // 金币包3
                addBagItem(userId, 1, 2); // 刷新券+2
                user.setGold(user.getGold().add(new BigDecimal("150000")));
                break;
            case 11: // 金币包4
                addBagItem(userId, 1, 5); // 刷新券+5
                user.setGold(user.getGold().add(new BigDecimal("500000")));
                break;
            case 12: // 金币包5
                addBagItem(userId, 1, 10); // 刷新券+10
                user.setGold(user.getGold().add(new BigDecimal("1000000")));
                break;
            case 24: // 仙灵・限定典藏年礼
//                月汐灵石 ×3000、仙灵飞升丹 ×88、至尊金币礼包 ×1、紫金矿 ×200、魂力宝珠 x10
                addBagItem(userId, 18, 3000); // 月汐灵石 ×3000
                addBagItem(userId, 22, 88); // 仙灵飞升丹 ×88
                addBagItem(userId, 12, 1); // 、至尊金币礼包 ×1
                addBagItem(userId, 15, 200); // 紫金矿 ×200
                Characters characters1 = charactersMapper.listById(userId, "105");
                if (characters1 != null) {
//                    魂力宝珠 x10
                    characters1.setStackCount(characters1.getStackCount() + 10);
                    charactersMapper.updateByPrimaryKey(characters1);
                } else {
                    Characters characters = new Characters();
                    characters.setStackCount(9);
                    characters.setId("105");
                    characters.setLv(1);
                    characters.setUserId(Integer.parseInt(userId));
                    characters.setStar(new BigDecimal(1));
                    characters.setMaxLv(1);
                    charactersMapper.insert(characters);
                }
                break;
            case 25: // 神将・限定典藏年礼
//               力量琥珀 ×3000、神将飞升丹 ×88、至尊金币礼包 ×1、紫金矿 ×200、魂力宝珠 x10
                addBagItem(userId, 19, 3000); // 月汐灵石 ×3000
                addBagItem(userId, 23, 88); // 仙灵飞升丹 ×88
                addBagItem(userId, 12, 1); // 、至尊金币礼包 ×1
                addBagItem(userId, 15, 200); // 紫金矿 ×200
                Characters characters2 = charactersMapper.listById(userId, "105");
                if (characters2 != null) {
//                    魂力宝珠 x10
                    characters2.setStackCount(characters2.getStackCount() + 10);
                    charactersMapper.updateByPrimaryKey(characters2);
                } else {
                    Characters characters = new Characters();
                    characters.setStackCount(9);
                    characters.setId("105");
                    characters.setLv(1);
                    characters.setUserId(Integer.parseInt(userId));
                    characters.setStar(new BigDecimal(1));
                    characters.setMaxLv(1);
                    charactersMapper.insert(characters);
                }
                break;
            case 26: // 武圣・限定典藏年礼
//               元神勾玉 ×3000、武圣飞升丹 ×88、至尊金币礼包 ×1、紫金矿 ×200、魂力宝珠 x10
                addBagItem(userId, 20, 3000); // 元神勾玉 ×3000
                addBagItem(userId, 21, 88); // 仙灵飞升丹 ×88
                addBagItem(userId, 12, 1); // 、至尊金币礼包 ×1
                addBagItem(userId, 15, 200); // 紫金矿 ×200
                Characters characters3 = charactersMapper.listById(userId, "105");
                if (characters3 != null) {
//                    魂力宝珠 x10
                    characters3.setStackCount(characters3.getStackCount() + 10);
                    charactersMapper.updateByPrimaryKey(characters3);
                } else {
                    Characters characters = new Characters();
                    characters.setStackCount(9);
                    characters.setId("105");
                    characters.setLv(1);
                    characters.setUserId(Integer.parseInt(userId));
                    characters.setStar(new BigDecimal(1));
                    characters.setMaxLv(1);
                    charactersMapper.insert(characters);
                }
                break;
            case 27: // 九霄至尊宝箱
//               月汐灵石 ×2000、力量琥珀 ×2000、元神勾玉 ×2000、仙灵飞升丹 ×50、神将飞升丹 ×50、
//               武圣飞升丹 ×50、刷新符 ×80、囤货金币礼包 ×3、体力袋 ×20、活力袋 ×20、重置卷 ×10
                addBagItem(userId, 18, 2000); // 月汐灵石 ×2000
                addBagItem(userId, 19, 2000); // 月汐灵石 ×2000
                addBagItem(userId, 20, 2000); // 月汐灵石 ×2000
                addBagItem(userId, 21, 50); // 仙灵飞升丹 ×50
                addBagItem(userId, 22, 50); // 仙灵飞升丹 ×50
                addBagItem(userId, 23, 50); // 仙灵飞升丹 ×50
                addBagItem(userId, 1, 80); // 刷新符 ×80
                addBagItem(userId, 11, 3); // 囤货金币礼包 ×3
                addBagItem(userId, 6, 20); // 体力袋 ×20
                addBagItem(userId, 7, 20); // 活力袋 ×20
                addBagItem(userId, 17, 10); // 重置卷 ×10
                break;
            case 29:
                // 1. 获取当前时间的时间戳（毫秒），和你提供的JS逻辑完全一致
                long now = new Date().getTime();
                // 2. 计算8小时后的时间戳（8*60*60*1000 = 28800000毫秒）
                long protectEndTime = now + 8 * 60 * 60 * 1000;
                // 3. 将时间戳转换为Date对象，赋值给duoTime
                user.setDuoTime(new Date(protectEndTime));
                break;
            case 30:
                user.setDuoCount(user.getDuoCount()+1);
                break;
            default:
                throw new IllegalArgumentException("不支持的物品ID：" + itemId);
        }
    }

    /**
     * 通用添加背包物品方法（消除重复代码）
     */
    private void addBagItem(String userId, Integer itemId, Integer count) {
        Map<String, Object> itemMap = new HashMap<>();
        itemMap.put("item_id", itemId);
        itemMap.put("user_id", userId);
        itemMap.put("is_delete", "0");
        List<GamePlayerBag> playerBags = gamePlayerBagMapper.selectByMap(itemMap);
        if (Xtool.isNotNull(playerBags)) {
            GamePlayerBag gamePlayerBag = playerBags.get(0);
            gamePlayerBag.setItemCount(gamePlayerBag.getItemCount() + count);
            gamePlayerBagMapper.updateById(gamePlayerBag);
        } else {
            GamePlayerBag gamePlayerBag = new GamePlayerBag();
            gamePlayerBag.setUserId(Integer.parseInt(userId));
            gamePlayerBag.setItemCount(count); // 修复原代码写死为2的错误
            gamePlayerBag.setGridIndex(1);
            gamePlayerBag.setItemId(itemId);
            gamePlayerBagMapper.insert(gamePlayerBag);
        }
    }

    @Override
    @Transactional
    @NoRepeatSubmit(limitSeconds = 3)
    public BaseResp yijiantansuo(TokenDto token, HttpServletRequest request) throws Exception {
        Map map = new HashMap();
        //先获取当前用户战队
        BaseResp baseResp = new BaseResp();
        if (token == null || Xtool.isNull(token.getToken())) {
            baseResp.setSuccess(0);
            baseResp.setErrorMsg("登录过期");
            return baseResp;
        }
//        String userId = (String) redisTemplate.opsForValue().get(token.getToken());
        String userId = token.getUserId();
        if (Xtool.isNull(userId)) {
            baseResp.setSuccess(0);
            baseResp.setErrorMsg("登录过期");
            return baseResp;
        }
        User user = userMapper.selectUserByUserId(Integer.parseInt(userId));
        Map map1 = new HashMap();
        map1.put("floor_num", 100);
        map1.put("player_id", userId);
        map1.put("bronze_type", token.getStr());
        List<PlayerBronzeTower> playerBronzeTower = playerBronzeTowerMapper.selectByMap(map);
        if (Xtool.isNull(playerBronzeTower)) {
            baseResp.setErrorMsg("您未通关试炼塔无法一键探索");
            baseResp.setSuccess(0);
            return baseResp;
        }
        if ("bronzetower".equals(token.getStr())) {
            if (user.getBronze1() > 100) {
                baseResp.setSuccess(0);
                baseResp.setErrorMsg("塔已通关，可以选择重置继续试炼");
                return baseResp;
            }
        } else if ("silvertower".equals(token.getStr())) {
            if (user.getSilvertower() > 100) {
                baseResp.setSuccess(0);
                baseResp.setErrorMsg("塔已通关，可以选择重置继续试炼");
                return baseResp;
            }
        } else if ("goldentower".equals(token.getStr())) {
            if (user.getGoldentower() > 100) {
                baseResp.setSuccess(0);
                baseResp.setErrorMsg("塔已通关，可以选择重置继续试炼");
                return baseResp;
            }
        }

        baseResp.setSuccess(1);
        List<PveReward> pveRewards = new ArrayList<>();
        if (1 == 1) {
            PveReward pveReward = new PveReward();
            pveReward.setItemId(0);
            pveReward.setRewardAmount(100000);
            pveReward.setRewardType("2");
            pveRewards.add(pveReward);
        }

        if (1 == 1) {
            PveReward pveReward = new PveReward();
            pveReward.setItemId(0);
            pveReward.setRewardAmount(50);
            pveReward.setRewardType("1");
            pveRewards.add(pveReward);
        }

        if ("bronzetower".equals(token.getStr())) {
            user.setBronze1(101);
            PveReward pveReward = new PveReward();
            GameItemBase gameItemBase = gameItemBaseMapper.selectById(13);
            pveReward.setImg(gameItemBase.getIcon());
            pveReward.setItemName(gameItemBase.getItemName() + 2000);
            pveReward.setItemId(13);
            pveReward.setRewardAmount(2000);
            pveReward.setRewardType("6");
            pveRewards.add(pveReward);
        }
        if ("silvertower".equals(token.getStr())) {
            user.setSilvertower(101);
            PveReward pveReward = new PveReward();
            GameItemBase gameItemBase = gameItemBaseMapper.selectById(14);
            pveReward.setImg(gameItemBase.getIcon());
            pveReward.setItemName(gameItemBase.getItemName() + 1000);
            pveReward.setItemId(14);
            pveReward.setRewardAmount(1000);
            pveReward.setRewardType("6");
            pveRewards.add(pveReward);
        }
        if ("goldentower".equals(token.getStr())) {
            user.setGoldentower(101);
            PveReward pveReward = new PveReward();
            GameItemBase gameItemBase = gameItemBaseMapper.selectById(15);
            pveReward.setImg(gameItemBase.getIcon());
            pveReward.setItemName(gameItemBase.getItemName() + 500);
            pveReward.setItemId(15);
            pveReward.setRewardAmount(500);
            pveReward.setRewardType("6");
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
            } else if ("5".equals(content.getRewardType() + "") || "6".equals(content.getRewardType() + "")) {
                Map itemMap = new HashMap();
                itemMap.put("item_id", content.getItemId());
                itemMap.put("user_id", userId);
                itemMap.put("is_delete", "0");
                List<GamePlayerBag> playerBagList = gamePlayerBagMapper.selectByMap(itemMap);
                if (Xtool.isNotNull(playerBagList)) {
                    GamePlayerBag playerBag = playerBagList.get(0);
                    playerBag.setItemCount(playerBag.getItemCount() + content.getRewardAmount());
                    gamePlayerBagMapper.updateById(playerBag);
                } else {
                    GamePlayerBag playerBag = new GamePlayerBag();
                    playerBag.setUserId(Integer.parseInt(userId));
                    playerBag.setItemCount(content.getRewardAmount());
                    playerBag.setGridIndex(1);
                    playerBag.setItemId(content.getItemId());
                    gamePlayerBagMapper.insert(playerBag);
                }
            }
        }
        map.put("rewards", pveRewards);
        userMapper.updateuser(user);
        User user2 = userMapper.selectUserByUserId(Integer.parseInt(userId));
        UserInfo info = new UserInfo();
        BeanUtils.copyProperties(user2, info);
        info.setBronze(0);
        info.setDarkSteel(0);
        info.setPurpleGold(0);
        info.setCrystal(0);
        GamePlayerBag playerBag = gamePlayerBagMapper.goIntoListByIdAndItemId(userId, 13);
        if (playerBag != null) {
            info.setBronze(playerBag.getItemCount());
        }
        GamePlayerBag playerBag1 = gamePlayerBagMapper.goIntoListByIdAndItemId(userId, 14);
        if (playerBag1 != null) {
            info.setDarkSteel(playerBag1.getItemCount());
        }
        GamePlayerBag playerBag2 = gamePlayerBagMapper.goIntoListByIdAndItemId(userId, 15);
        if (playerBag2 != null) {
            info.setPurpleGold(playerBag2.getItemCount());
        }
        GamePlayerBag playerBag3 = gamePlayerBagMapper.goIntoListByIdAndItemId(userId, 16);
        if (playerBag3 != null) {
            info.setCrystal(playerBag3.getItemCount());
        }
        map.put("user", info);
        baseResp.setData(map);
        baseResp.setSuccess(1);
        return baseResp;
    }

    @Override
    @Transactional
    @NoRepeatSubmit(limitSeconds = 3)
    public BaseResp hongb(TokenDto token, HttpServletRequest request) throws Exception {
        Map map = new HashMap();
        //先获取当前用户战队
        BaseResp baseResp = new BaseResp();
        if (token == null || Xtool.isNull(token.getToken())) {
            baseResp.setSuccess(0);
            baseResp.setErrorMsg("登录过期");
            return baseResp;
        }
//        String userId = (String) redisTemplate.opsForValue().get(token.getToken());
        String userId = token.getUserId();
        if (Xtool.isNull(userId)) {
            baseResp.setSuccess(0);
            baseResp.setErrorMsg("登录过期");
            return baseResp;
        }
        User user = userMapper.selectUserByUserId(Integer.parseInt(userId));

        if (user.getHongbTime() != null) {
            //判断时间是否有10分钟
            Date time1 = new Date(); // 时间1（如当前时间）
            Date time2 = user.getHongbTime(); // 时间2（比时间1晚10分钟）

            // 2. 计算两个时间的毫秒差值（取绝对值，避免顺序影响结果）
            long timeDiff = Math.abs(time1.getTime() - time2.getTime());

            // 3. 判断是否相差>=10分钟（10分钟 = 10 * 60 * 1000 = 600000毫秒）
            if (timeDiff < 10 * 60 * 1000L) {
                baseResp.setSuccess(0);
                baseResp.setErrorMsg("请勿频繁操作");
                return baseResp;
            }
        }
        List<MewYearItemShop> mewYearItemShops = mewYearItemShopMapper.selectByMap(new HashMap<>());
        DynamicMewYearItemPicker picker = new DynamicMewYearItemPicker();
        for (MewYearItemShop mewYearItemShop : mewYearItemShops) {
            picker.addItem(mewYearItemShop);
        }
        List<MewYearItemShop> picked = picker.pickRandomItems(1);
        MewYearItemShop yearItemShop = picked.get(0);
        List<PveReward> rewards = new ArrayList<>();
        PveReward content = new PveReward();
        content.setItemId(yearItemShop.getItemId());
        content.setItemName(yearItemShop.getItemName());
        content.setRewardAmount(yearItemShop.getNum());
        content.setRewardType(yearItemShop.getType());
        content.setImg(yearItemShop.getImg());
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
        } else if ("5".equals(content.getRewardType() + "") || "6".equals(content.getRewardType() + "")) {
            Map itemMap = new HashMap();
            itemMap.put("item_id", content.getItemId());
            itemMap.put("user_id", userId);
            itemMap.put("is_delete", "0");
            List<GamePlayerBag> playerBagList = gamePlayerBagMapper.selectByMap(itemMap);
            if (Xtool.isNotNull(playerBagList)) {
                GamePlayerBag playerBag = playerBagList.get(0);
                playerBag.setItemCount(playerBag.getItemCount() + content.getRewardAmount());
                gamePlayerBagMapper.updateById(playerBag);
            } else {
                GamePlayerBag playerBag = new GamePlayerBag();
                playerBag.setUserId(Integer.parseInt(userId));
                playerBag.setItemCount(content.getRewardAmount());
                playerBag.setGridIndex(1);
                playerBag.setItemId(content.getItemId());
                gamePlayerBagMapper.insert(playerBag);
            }
        }
        rewards.add(content);
        map.put("rewards", rewards);
        user.setHongbTime(new Date());
        userMapper.updateuser(user);
        User emp = userMapper.selectUserByUserId(Integer.parseInt(userId));
        UserInfo info = new UserInfo();
        BeanUtils.copyProperties(emp, info);
        //获取卡牌数据
        List<Characters> characterList = charactersMapper.selectByUserId(emp.getUserId());
        List<EqCharacters> eqCharactersList = eqCharactersMapper.selectByUserId(emp.getUserId());
        info.setBronze(0);
        info.setDarkSteel(0);
        info.setPurpleGold(0);
        info.setCrystal(0);
        GamePlayerBag playerBag = gamePlayerBagMapper.goIntoListByIdAndItemId(emp.getUserId() + "", 13);
        if (playerBag != null) {
            info.setBronze(playerBag.getItemCount());
        }
        GamePlayerBag playerBag1 = gamePlayerBagMapper.goIntoListByIdAndItemId(emp.getUserId() + "", 14);
        if (playerBag1 != null) {
            info.setDarkSteel(playerBag1.getItemCount());
        }
        GamePlayerBag playerBag2 = gamePlayerBagMapper.goIntoListByIdAndItemId(emp.getUserId() + "", 15);
        if (playerBag2 != null) {
            info.setPurpleGold(playerBag2.getItemCount());
        }
        GamePlayerBag playerBag3 = gamePlayerBagMapper.goIntoListByIdAndItemId(emp.getUserId() + "", 16);
        if (playerBag3 != null) {
            info.setCrystal(playerBag3.getItemCount());
        }
        //卡池数量
        List<Card> cardList = cardMapper.selectAll();
        info.setUseCardCount(cardList.size() + "");
        info.setCharacterList(formateCharacter(characterList));
        info.setEqCharactersList(formateEqCharacter(eqCharactersList));
        map.put("user", info);
        baseResp.setData(map);
        baseResp.setSuccess(1);
        return baseResp;
    }

    @Override
    public BaseResp isNewYear(TokenDto token, HttpServletRequest request) throws Exception {
        //先获取当前用户战队
        Date targetDate = new Date();
        LocalDate targetLocalDate = targetDate.toInstant()
                .atZone(ZoneId.systemDefault())
                .toLocalDate();
        BaseResp baseResp = new BaseResp();
        if (token == null || Xtool.isNull(token.getToken())) {
            baseResp.setSuccess(0);
            baseResp.setErrorMsg("登录过期");
            return baseResp;
        }
        if (!LunarAlgorithmChecker.isNewYearsEve(targetLocalDate)){
            targetLocalDate = targetLocalDate.minusDays(1);
            if (!LunarAlgorithmChecker.isNewYearsEve(targetLocalDate)){
                targetLocalDate = targetLocalDate.minusDays(1);
                if (!LunarAlgorithmChecker.isNewYearsEve(targetLocalDate)){
                    targetLocalDate = targetLocalDate.minusDays(1);
                    if (!LunarAlgorithmChecker.isNewYearsEve(targetLocalDate)){
                        targetLocalDate = targetLocalDate.minusDays(1);
                        if (!LunarAlgorithmChecker.isNewYearsEve(targetLocalDate)){
                            targetLocalDate = targetLocalDate.minusDays(1);
                            if (!LunarAlgorithmChecker.isNewYearsEve(targetLocalDate)){
                                targetLocalDate = targetLocalDate.minusDays(1);
                                if (!LunarAlgorithmChecker.isNewYearsEve(targetLocalDate)){
                                    targetLocalDate = targetLocalDate.minusDays(1);
                                    if (!LunarAlgorithmChecker.isNewYearsEve(targetLocalDate)){
                                        baseResp.setSuccess(0);
                                        baseResp.setSuccess(1);
                                        baseResp.setData(false);
                                        return baseResp;
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }
//        String userId = (String) redisTemplate.opsForValue().get(token.getToken());
        String userId = token.getUserId();
        if (Xtool.isNull(userId)) {
            baseResp.setSuccess(0);
            baseResp.setErrorMsg("登录过期");
            return baseResp;
        }
        User user = userMapper.selectUserByUserId(Integer.parseInt(userId));
        //过年的时候放开
//        if (user.getHongb()==0){
//            baseResp.setSuccess(1);
//            baseResp.setData(false);
//            return baseResp;
//        }


        if (user.getHongbTime() == null) {
            baseResp.setSuccess(1);
            baseResp.setData(true);
            return baseResp;
        }
        //判断时间是否有10分钟
        Date time1 = new Date(); // 时间1（如当前时间）
        Date time2 = user.getHongbTime(); // 时间2（比时间1晚10分钟）

        // 2. 计算两个时间的毫秒差值（取绝对值，避免顺序影响结果）
        long timeDiff = Math.abs(time1.getTime() - time2.getTime());

        // 3. 判断是否相差>=10分钟（10分钟 = 10 * 60 * 1000 = 600000毫秒）
        if (timeDiff >= 10 * 60 * 1000L) {
            baseResp.setSuccess(1);
            baseResp.setData(true);
//            user.setHongbTime(new Date());
            return baseResp;
        } else {
            baseResp.setSuccess(1);
            baseResp.setData(false);
            return baseResp;
        }
    }

    /**
     * 判断 Date 对象是否为今天（Java 8+ 推荐方案）
     *
     * @param date 待判断的 Date
     * @return true-是今天，false-不是今天
     */
    public boolean isDateToday(Date date) {
        if (date == null) {
            return false;
        }
        // 1. 获取系统默认时区（也可指定时区，如 ZoneId.of("Asia/Shanghai")）
        ZoneId zoneId = ZoneId.systemDefault();
        // 2. Date 转 LocalDate（剥离时分秒，只保留年月日）
        LocalDate targetDate = date.toInstant().atZone(zoneId).toLocalDate();
        // 3. 获取当前时间的 LocalDate
        LocalDate today = LocalDate.now(zoneId);
        // 4. 比较两个 LocalDate 是否相等
        return targetDate.equals(today);
    }
    // 简化版：detailCode为String类型
    public  boolean isDetailCodeEndsWithMinusFive(String detailCode) {
        if (detailCode == null) {
            return false;
        }
        return detailCode.trim().endsWith("-5");
    }
    @Override
    @Transactional
    @NoRepeatSubmit(limitSeconds = 1)
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
//        String userId = (String) redisTemplate.opsForValue().get(token.getToken());
        String userId = token.getUserId();
        if (Xtool.isNull(userId)) {
            baseResp.setSuccess(0);
            baseResp.setErrorMsg("登录过期");
            return baseResp;
        }
        User user = userMapper.selectUserByUserId(Integer.parseInt(userId));
        if (user.getTiliCount() - 2 < 0) {
            baseResp.setSuccess(0);
            baseResp.setErrorMsg("体力不足");
            return baseResp;
        }
        if (user.getLv().compareTo(new BigDecimal(100)) < 0) {
            BigDecimal exp = user.getExp().add(new BigDecimal(50));
            if (exp.compareTo(new BigDecimal(1000)) >= 0) {
                user.setLv(user.getLv().add(new BigDecimal(1)));
                user.setExp(exp.subtract(new BigDecimal(1000)));
                levelUp = user.getLv().intValue();
                if (Xtool.isNotNull(user.getYaoCode())) {
                    if (levelUp == 50) {
                        List<User> users = userMapper.selectUserByYaoCode(user.getYaoCode());
                        if (Xtool.isNotNull(users)) {
                            //如果少年王天军
                            Characters characters1 = charactersMapper.listById(userId, "132");
                            if (characters1 != null) {
                                characters1.setStackCount(characters1.getStackCount() + 1);
                                charactersMapper.updateByPrimaryKey(characters1);
                            } else {
                                Card card = cardMapper.selectByid(132);
                                if (card == null) {
                                    baseResp.setErrorMsg("服务器异常联想管理员");
                                    baseResp.setSuccess(0);
                                    return baseResp;
                                }
                                Characters characters = new Characters();
                                characters.setStackCount(1);
                                characters.setId("132");
                                characters.setLv(1);
                                characters.setUserId(Integer.parseInt(userId));
                                characters.setStar(new BigDecimal(1));
                                characters.setMaxLv(CardMaxLevelUtils.getMaxLevel(card.getName(), card.getStar().doubleValue()));
                                charactersMapper.insert(characters);
                            }
                        }
                    }
                    if (levelUp == 80) {
                        List<User> users = userMapper.selectUserByYaoCode(user.getYaoCode());
                        List<User> users2 = userMapper.selectUserByYaoCode2(user.getYaoCode());
                        List<User> users1 = users2.stream().filter(x -> x.getLv().compareTo(new BigDecimal(80)) >= 0).collect(Collectors.toList());
                        if (Xtool.isNotNull(users) && users1.size() == 9) {
                            //
                            Characters characters1 = charactersMapper.listById(userId, "105");
                            if (characters1 != null) {
                                characters1.setStackCount(characters1.getStackCount() + 10);
                                charactersMapper.updateByPrimaryKey(characters1);
                            } else {
                                Card card = cardMapper.selectByid(105);
                                if (card == null) {
                                    baseResp.setErrorMsg("服务器异常联想管理员");
                                    baseResp.setSuccess(0);
                                    return baseResp;
                                }
                                Characters characters = new Characters();
                                characters.setStackCount(10);
                                characters.setId("105");
                                characters.setLv(1);
                                characters.setUserId(Integer.parseInt(userId));
                                characters.setStar(new BigDecimal(1));
                                characters.setMaxLv(CardMaxLevelUtils.getMaxLevel(card.getName(), card.getStar().doubleValue()));
                                charactersMapper.insert(characters);
                            }
                        }
                    }

                    if (levelUp == 100) {
                        List<User> users = userMapper.selectUserByYaoCode(user.getYaoCode());
                        List<User> users2 = userMapper.selectUserByYaoCode2(user.getYaoCode());
                        List<User> users1 = users2.stream().filter(x -> x.getLv().compareTo(new BigDecimal(100)) >= 0).collect(Collectors.toList());
                        if (Xtool.isNotNull(users) && users1.size() == 30) {
                            //
                            Characters characters1 = charactersMapper.listById(userId, "100");
                            if (characters1 != null) {
                                characters1.setStackCount(characters1.getStackCount() + 1);
                                charactersMapper.updateByPrimaryKey(characters1);
                            } else {
                                Card card = cardMapper.selectByid(100);
                                if (card == null) {
                                    baseResp.setErrorMsg("服务器异常联想管理员");
                                    baseResp.setSuccess(0);
                                    return baseResp;
                                }
                                Characters characters = new Characters();
                                characters.setStackCount(1);
                                characters.setId("100");
                                characters.setLv(1);
                                characters.setUserId(Integer.parseInt(userId));
                                characters.setStar(new BigDecimal(1));
                                characters.setMaxLv(CardMaxLevelUtils.getMaxLevel(card.getName(), card.getStar().doubleValue()));
                                charactersMapper.insert(characters);
                            }
                        }
                    }
                }
            } else {
                user.setExp(exp);
            }
        } else {
            BigDecimal exp = user.getExp().add(new BigDecimal(50));
            if (exp.compareTo(new BigDecimal(1000)) >= 0) {
                user.setExp(exp.subtract(new BigDecimal(1000)));
                //如果巅峰经验满级获得5个魂力宝珠
                Characters characters1 = charactersMapper.listById(userId, "105");
                if (characters1 != null) {
                    characters1.setStackCount(characters1.getStackCount() + 2);
                    charactersMapper.updateByPrimaryKey(characters1);
                } else {
                    Card card = cardMapper.selectByid(105);
                    if (card == null) {
                        baseResp.setErrorMsg("服务器异常联想管理员");
                        baseResp.setSuccess(0);
                        return baseResp;
                    }
                    Characters characters = new Characters();
                    characters.setStackCount(1);
                    characters.setId("105");
                    characters.setLv(1);
                    characters.setUserId(Integer.parseInt(userId));
                    characters.setStar(new BigDecimal(1));
                    characters.setMaxLv(CardMaxLevelUtils.getMaxLevel(card.getName(), card.getStar().doubleValue()));
                    charactersMapper.insert(characters);
                }
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
        for (Characters characters : leftCharacter) {
            List<EqCharacters> eqCharacters = eqCharactersMapper.listByGoOn(userId, characters.getId());
            if (Xtool.isNotNull(eqCharacters)) {
                characters.setEqCharactersList(formateEqCharacter(eqCharacters));
            }
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
                    .filter(x -> (x.getBossId() + "").equals(pveBossDetail.getBossId() + "")) // 过滤null对象
                    .map(PveBossDetail::getBossId)
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
                    if (num1 + 1 > 7) {

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
                if (!battle.getChapter().equals(user.getChapter())) {
                    user.setChapterTime(new Date());
                }
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
            if (isDetailCodeEndsWithMinusFive(token.getStr())){
                Map map11 = new HashMap();
                map11.put("detail_code", token.getStr());
                map11.put("user_id", userId);
                List<PveRewardRecord> pveRewardsAll2 = pveRewardRecordMapper.selectByMap(map11);
                if (Xtool.isNotNull(pveRewardsAll2)){
                    pveRewards=pveRewards.stream().filter(x->!"4".equals(x.getRewardType())).collect(Collectors.toList());
                }else {
                    List<PveReward> pveRewardsAll3=pveRewards.stream().filter(x->"4".equals(x.getRewardType())).collect(Collectors.toList());
                    if (Xtool.isNotNull(pveRewardsAll3)){
                        PveRewardRecord pveRewardRecord=new PveRewardRecord();
                        BeanUtils.copyProperties(pveRewardsAll3.get(0),pveRewardRecord);
                        pveRewardRecord.setUserId(Integer.parseInt(userId));
                        pveRewardRecordMapper.insert(pveRewardRecord);
                    }
                }
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
                } else if ("5".equals(content.getRewardType() + "") || "6".equals(content.getRewardType() + "")) {
                    GameItemBase gameItemBase = gameItemBaseMapper.selectById(content.getItemId());
                    if (gameItemBase == null) {
                        baseResp.setErrorMsg("服务器异常联想管理员");
                        baseResp.setSuccess(0);
                        return baseResp;
                    }
                    content.setImg(gameItemBase.getIcon());
                    content.setItemName(gameItemBase.getItemName() + content.getRewardAmount());
                    Map itemMap = new HashMap();
                    itemMap.put("item_id", content.getItemId());
                    itemMap.put("user_id", userId);
                    itemMap.put("is_delete", "0");
                    List<GamePlayerBag> playerBagList = gamePlayerBagMapper.selectByMap(itemMap);
                    if (Xtool.isNotNull(playerBagList)) {
                        GamePlayerBag playerBag = playerBagList.get(0);
                        playerBag.setItemCount(playerBag.getItemCount() + content.getRewardAmount());
                        gamePlayerBagMapper.updateById(playerBag);
                    } else {
                        GamePlayerBag playerBag = new GamePlayerBag();
                        playerBag.setUserId(Integer.parseInt(userId));
                        playerBag.setItemCount(content.getRewardAmount());
                        playerBag.setGridIndex(1);
                        playerBag.setItemId(content.getItemId());
                        gamePlayerBagMapper.insert(playerBag);
                    }
                }
            }
            map.put("rewards", pveRewards);
        } else {
            battle.setChapter(token.getStr());
        }
        user.setTiliCount(user.getTiliCount() - 2);
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
        String reward="";
        if (isDetailCodeEndsWithMinusFive(battle.getChapter())){
            Map map11 = new HashMap();
            map11.put("detail_code", battle.getChapter());
            map11.put("user_id", userId);
            List<PveRewardRecord> pveRewardsAll2 = pveRewardRecordMapper.selectByMap(map11);
            if (Xtool.isNull(pveRewardsAll2)){
                Map map22 = new HashMap();
                map22.put("detail_code", battle.getChapter());
                List<PveReward> pveRewardRecords = pveRewardMapper.selectByMap(map22);
                List<PveReward> pveRewardsAll3=pveRewardRecords.stream().filter(x->"4".equals(x.getRewardType())).collect(Collectors.toList());
                if (Xtool.isNotNull(pveRewardsAll3)){
                    reward=pveRewardsAll3.get(0).getItemId()+"";
                }
            }
        }
        map.put("reward", reward);
        userMapper.updateuser(user);
        UserInfo userInfo = new UserInfo();
        BeanUtils.copyProperties(user, userInfo);
        userInfo.setLevelUp(levelUp);
        //获取卡牌数据
        List<Characters> characterList = charactersMapper.selectByUserId(user.getUserId());
        userInfo.setCharacterList(formateCharacter(characterList));
        map.put("levelUp", levelUp);
        map.put("user", userInfo);
        map.put("battle", battle);
        map.put("pveDetail", pveDetail2);
        baseResp.setData(map);
        baseResp.setSuccess(1);
        return baseResp;
    }

    @Override
    @Transactional
    @NoRepeatSubmit(limitSeconds = 1)
    public BaseResp saodan(TokenDto token, HttpServletRequest request) throws Exception {
        Integer levelUp = 0;
        Map map = new HashMap();
        //先获取当前用户战队
        BaseResp baseResp = new BaseResp();
        if (token == null || Xtool.isNull(token.getToken())) {
            baseResp.setSuccess(0);
            baseResp.setErrorMsg("登录过期");
            return baseResp;
        }
//        String userId = (String) redisTemplate.opsForValue().get(token.getToken());
        String userId = token.getUserId();
        if (Xtool.isNull(userId)) {
            baseResp.setSuccess(0);
            baseResp.setErrorMsg("登录过期");
            return baseResp;
        }
        Integer num = token.getTotalSilverSpent();
        if (Xtool.isNull(num)) {
            baseResp.setSuccess(0);
            baseResp.setErrorMsg("请使用扫荡券");
            return baseResp;
        }
        User user = userMapper.selectUserByUserId(Integer.parseInt(userId));
        Map map2=new HashMap();
        map2.put("user_id",userId);
        map2.put("item_id",28);
        map2.put("is_delete",0);
        List<GamePlayerBag> playerBags=gamePlayerBagMapper.selectByMap(map2);
        if (Xtool.isNull(playerBags)){
            baseResp.setSuccess(0);
            baseResp.setErrorMsg("扫荡券不足");
            return baseResp;
        }
        GamePlayerBag gamePlayerBag=playerBags.get(0);
        if (gamePlayerBag.getItemCount()-num<0) {
            baseResp.setSuccess(0);
            baseResp.setErrorMsg("扫荡券不足");
            return baseResp;
        }
        // 扣减物品数量
        if (gamePlayerBag.getItemCount() - num > 0) {
            gamePlayerBag.setItemCount(gamePlayerBag.getItemCount() - num);
        } else {
            gamePlayerBag.setIsDelete("1");
        }
        gamePlayerBagMapper.updateById(gamePlayerBag);
        if (compareSegments(user.getChapter(),token.getStr())) {
            baseResp.setSuccess(0);
            baseResp.setErrorMsg("你还未通关无法扫荡");
            return baseResp;
        }
        if (user.getTiliCount() - 2 * num < 0) {
            baseResp.setSuccess(0);
            baseResp.setErrorMsg("体力不足");
            return baseResp;
        }
        if (user.getLv().compareTo(new BigDecimal(100)) < 0) {
            BigDecimal exp = user.getExp().add(new BigDecimal(50).multiply(new BigDecimal(num)));
            if (exp.compareTo(new BigDecimal(1000)) >= 0) {
                BigDecimal lv = exp.divide(new BigDecimal(1000));
                user.setLv(user.getLv().add(lv));
                user.setExp(exp.subtract(new BigDecimal(1000).multiply(lv)));
                levelUp = user.getLv().intValue();
                if (Xtool.isNotNull(user.getYaoCode())) {
                    if (levelUp == 50) {
                        List<User> users = userMapper.selectUserByYaoCode(user.getYaoCode());
                        if (Xtool.isNotNull(users)) {
                            //如果少年王天军
                            Characters characters1 = charactersMapper.listById(userId, "132");
                            if (characters1 != null) {
                                characters1.setStackCount(characters1.getStackCount() + 1);
                                charactersMapper.updateByPrimaryKey(characters1);
                            } else {
                                Card card = cardMapper.selectByid(132);
                                if (card == null) {
                                    baseResp.setErrorMsg("服务器异常联想管理员");
                                    baseResp.setSuccess(0);
                                    return baseResp;
                                }
                                Characters characters = new Characters();
                                characters.setStackCount(1);
                                characters.setId("132");
                                characters.setLv(1);
                                characters.setUserId(Integer.parseInt(userId));
                                characters.setStar(new BigDecimal(1));
                                characters.setMaxLv(CardMaxLevelUtils.getMaxLevel(card.getName(), card.getStar().doubleValue()));
                                charactersMapper.insert(characters);
                            }
                        }
                    }
                    if (levelUp == 80) {
                        List<User> users = userMapper.selectUserByYaoCode(user.getYaoCode());
                        List<User> users2 = userMapper.selectUserByYaoCode2(user.getYaoCode());
                        List<User> users1 = users2.stream().filter(x -> x.getLv().compareTo(new BigDecimal(80)) >= 0).collect(Collectors.toList());
                        if (Xtool.isNotNull(users) && users1.size() == 9) {
                            //
                            Characters characters1 = charactersMapper.listById(userId, "105");
                            if (characters1 != null) {
                                characters1.setStackCount(characters1.getStackCount() + 10);
                                charactersMapper.updateByPrimaryKey(characters1);
                            } else {
                                Card card = cardMapper.selectByid(105);
                                if (card == null) {
                                    baseResp.setErrorMsg("服务器异常联想管理员");
                                    baseResp.setSuccess(0);
                                    return baseResp;
                                }
                                Characters characters = new Characters();
                                characters.setStackCount(10);
                                characters.setId("105");
                                characters.setLv(1);
                                characters.setUserId(Integer.parseInt(userId));
                                characters.setStar(new BigDecimal(1));
                                characters.setMaxLv(CardMaxLevelUtils.getMaxLevel(card.getName(), card.getStar().doubleValue()));
                                charactersMapper.insert(characters);
                            }
                        }
                    }

                    if (levelUp == 100) {
                        List<User> users = userMapper.selectUserByYaoCode(user.getYaoCode());
                        List<User> users2 = userMapper.selectUserByYaoCode2(user.getYaoCode());
                        List<User> users1 = users2.stream().filter(x -> x.getLv().compareTo(new BigDecimal(100)) >= 0).collect(Collectors.toList());
                        if (Xtool.isNotNull(users) && users1.size() == 30) {
                            //
                            Characters characters1 = charactersMapper.listById(userId, "100");
                            if (characters1 != null) {
                                characters1.setStackCount(characters1.getStackCount() + 1);
                                charactersMapper.updateByPrimaryKey(characters1);
                            } else {
                                Card card = cardMapper.selectByid(100);
                                if (card == null) {
                                    baseResp.setErrorMsg("服务器异常联想管理员");
                                    baseResp.setSuccess(0);
                                    return baseResp;
                                }
                                Characters characters = new Characters();
                                characters.setStackCount(1);
                                characters.setId("100");
                                characters.setLv(1);
                                characters.setUserId(Integer.parseInt(userId));
                                characters.setStar(new BigDecimal(1));
                                characters.setMaxLv(CardMaxLevelUtils.getMaxLevel(card.getName(), card.getStar().doubleValue()));
                                charactersMapper.insert(characters);
                            }
                        }
                    }
                }
            } else {
                user.setExp(exp);
            }
        } else {
            BigDecimal exp = user.getExp().add(new BigDecimal(50).multiply(new BigDecimal(num)));
            if (exp.compareTo(new BigDecimal(1000)) >= 0) {
                BigDecimal lv = exp.divide(new BigDecimal(1000));
                user.setExp(exp.subtract(new BigDecimal(1000).multiply(lv)));
                //如果巅峰经验满级获得5个魂力宝珠
                Characters characters1 = charactersMapper.listById(userId, "105");
                if (characters1 != null) {
                    BigDecimal zhuNum = new BigDecimal(2).multiply(lv);
                    characters1.setStackCount(characters1.getStackCount() + zhuNum.intValue());
                    charactersMapper.updateByPrimaryKey(characters1);
                } else {
                    Card card = cardMapper.selectByid(105);
                    if (card == null) {
                        baseResp.setErrorMsg("服务器异常联想管理员");
                        baseResp.setSuccess(0);
                        return baseResp;
                    }
                    Characters characters = new Characters();
                    BigDecimal zhuNum = new BigDecimal(2).multiply(lv).subtract(new BigDecimal(1));
                    characters1.setStackCount(zhuNum.intValue());
                    characters.setId("105");
                    characters.setLv(1);
                    characters.setUserId(Integer.parseInt(userId));
                    characters.setStar(new BigDecimal(1));
                    characters.setMaxLv(CardMaxLevelUtils.getMaxLevel(card.getName(), card.getStar().doubleValue()));
                    charactersMapper.insert(characters);
                }
            } else {
                user.setExp(exp);
            }
        }
        baseResp.setSuccess(1);
        Map map1 = new HashMap();
        map1.put("detail_code", token.getStr());


// 原有查询逻辑保持不变
        List<PveReward> pveRewardsAll = pveRewardMapper.selectByMap(map1);
        if (isDetailCodeEndsWithMinusFive(token.getStr())){
            Map map11 = new HashMap();
            map11.put("detail_code", token.getStr());
            map11.put("user_id", userId);
            List<PveRewardRecord> pveRewardsAll2 = pveRewardRecordMapper.selectByMap(map11);
            if (Xtool.isNotNull(pveRewardsAll2)){
                pveRewardsAll=pveRewardsAll.stream().filter(x->!"4".equals(x.getRewardType())).collect(Collectors.toList());
            }else {
                List<PveReward> pveRewardsAll3=pveRewardsAll.stream().filter(x->"4".equals(x.getRewardType())).collect(Collectors.toList());
                if (Xtool.isNotNull(pveRewardsAll3)){
                    pveRewardsAll=pveRewardsAll.stream().filter(x->!"4".equals(x.getRewardType())).collect(Collectors.toList());
                    pveRewardsAll.add(pveRewardsAll3.get(0));
                    PveRewardRecord pveRewardRecord=new PveRewardRecord();
                    BeanUtils.copyProperties(pveRewardsAll3.get(0),pveRewardRecord);
                    pveRewardRecord.setUserId(Integer.parseInt(userId));
                    pveRewardRecordMapper.insert(pveRewardRecord);
                }
            }
        }
        List<PveReward> copyResult1 = new ArrayList<>();
        for (int i = 0; i < num; i++) {
            // addAll 是浅拷贝：新集合引用原有PveReward对象，不创建新对象
            copyResult1.addAll(pveRewardsAll);
        }
        List<PveReward> pveRewards1 = new ArrayList<>();
        for (PveReward pveReward : copyResult1) {
            if (!ProbabilityUtils.hitProbability(pveReward.getPrent())) {
                continue;
            }
            pveRewards1.add(pveReward);
        }
        List<PveReward> pveRewards = new ArrayList<>();
        List<PveReward> noGoldRewards=pveRewards1.stream().filter(x->!"1".equals(x.getRewardType())).collect(Collectors.toList());
        Integer czxNum=user.getBaoCount();
        for (PveReward item : pveRewards1) {
            if ("1".equals(item.getRewardType())) {
                if (czxNum > 0) { // 只保留前20个type=1的元素
                    pveRewards.add(item);
                    czxNum--;
                }

            }
        }
        user.setBaoCount(czxNum);
        pveRewards.addAll(noGoldRewards);

// 核心改造：按 PveReward 自身的 id 分组（id 是唯一标识，不会为空）
// key: PveReward 的 id
// value: 该分组下所有 PveReward 对象（用于汇总数量）
        Map<Integer, List<PveReward>> rewardGroupByIdMap = pveRewards.stream()
                .collect(Collectors.groupingBy(PveReward::getId));

// 定义一个Map存储每个分组的总数量（key: PveReward的id，value: 总数量）
        Map<Integer, Integer> rewardTotalAmountMap = new HashMap<>();
// 先汇总每个分组的奖励数量
        for (Map.Entry<Integer, List<PveReward>> entry : rewardGroupByIdMap.entrySet()) {
            int rewardId = entry.getKey();
            List<PveReward> sameIdRewards = entry.getValue();
            // 累加该分组下所有奖励的数量
            int totalAmount = sameIdRewards.stream()
                    .mapToInt(PveReward::getRewardAmount)
                    .sum();
            rewardTotalAmountMap.put(rewardId, totalAmount);
        }

// 遍历分组后的奖励，统一处理
        for (Map.Entry<Integer, List<PveReward>> entry : rewardGroupByIdMap.entrySet()) {
            int rewardId = entry.getKey();
            int totalAmount = rewardTotalAmountMap.get(rewardId); // 该分组的总数量
            // 取该分组下任意一个PveReward对象作为基准（字段值都一样）
            PveReward content = entry.getValue().get(0);

            // 按奖励类型处理（逻辑和原有一致，数量用累加后的totalAmount）
            if ("1".equals(content.getRewardType() + "")) {
                // 钻石
                user.setDiamond(user.getDiamond().add(new BigDecimal(totalAmount)));
            } else if ("2".equals(content.getRewardType() + "")) {
                // 金币
                user.setGold(user.getGold().add(new BigDecimal(totalAmount)));
            } else if ("3".equals(content.getRewardType() + "")) {
                // 魂石
                user.setSoul(user.getSoul().add(new BigDecimal(totalAmount)));
            } else if ("4".equals(content.getRewardType() + "")) {
                // 角色（itemId不为空，否则逻辑本身有问题）
                String itemId = content.getItemId() + "";
                Characters characters1 = charactersMapper.listById(userId, itemId);
                if (characters1 != null) {
                    // 累加总数量，避免多次更新
                    characters1.setStackCount(characters1.getStackCount() + totalAmount);
                    charactersMapper.updateByPrimaryKey(characters1);
                } else {
                    Card card = cardMapper.selectByid(content.getItemId());
                    if (card == null) {
                        baseResp.setErrorMsg("服务器异常，请联系管理员");
                        baseResp.setSuccess(0);
                        return baseResp;
                    }
                    Characters characters = new Characters();
                    characters.setStackCount(totalAmount - 1); // 保持原有逻辑
                    characters.setId(itemId);
                    characters.setLv(1);
                    characters.setUserId(Integer.parseInt(userId));
                    characters.setStar(new BigDecimal(1));
                    characters.setMaxLv(CardMaxLevelUtils.getMaxLevel(card.getName(), card.getStar().doubleValue()));
                    charactersMapper.insert(characters);
                }
            } else if ("5".equals(content.getRewardType() + "") || "6".equals(content.getRewardType() + "")) {
                // 道具/宝石等
                String itemId = content.getItemId() + "";
                GameItemBase gameItemBase = gameItemBaseMapper.selectById(content.getItemId());
                if (gameItemBase == null) {
                    baseResp.setErrorMsg("服务器异常，请联系管理员");
                    baseResp.setSuccess(0);
                    return baseResp;
                }
                // 补充道具展示信息
                content.setImg(gameItemBase.getIcon());
                content.setItemName(gameItemBase.getItemName() + totalAmount);

                // 查询背包中是否已有该道具
                Map itemMap = new HashMap();
                itemMap.put("item_id", itemId);
                itemMap.put("user_id", userId);
                itemMap.put("is_delete", "0");
                List<GamePlayerBag> playerBagList = gamePlayerBagMapper.selectByMap(itemMap);

                if (Xtool.isNotNull(playerBagList)) {
                    // 已有道具，累加总数量
                    GamePlayerBag playerBag = playerBagList.get(0);
                    playerBag.setItemCount(playerBag.getItemCount() + totalAmount);
                    gamePlayerBagMapper.updateById(playerBag);
                } else {
                    // 新增道具
                    GamePlayerBag playerBag = new GamePlayerBag();
                    playerBag.setUserId(Integer.parseInt(userId));
                    playerBag.setItemCount(totalAmount);
                    playerBag.setGridIndex(1);
                    playerBag.setItemId(content.getItemId());
                    gamePlayerBagMapper.insert(playerBag);
                }
            }
        }

// 重构返回的奖励列表（每个分组只保留一个，数量为总和）
        List<PveReward> groupedPveRewards = new ArrayList<>();
        for (Map.Entry<Integer, List<PveReward>> entry : rewardGroupByIdMap.entrySet()) {
            PveReward originReward = entry.getValue().get(0);
            int totalAmount = rewardTotalAmountMap.get(entry.getKey());
            // 复制原有字段，更新数量
            PveReward groupedReward = new PveReward();
            groupedReward.setId(originReward.getId());
            groupedReward.setRewardType(originReward.getRewardType());
            groupedReward.setItemId(originReward.getItemId());
            groupedReward.setRewardAmount(totalAmount);
            groupedReward.setImg(originReward.getImg());
            groupedReward.setItemName(originReward.getItemName());
            groupedReward.setPrent(originReward.getPrent());
            // 其他需要的字段按需复制
            groupedPveRewards.add(groupedReward);
        }
        map.put("rewards", groupedPveRewards);
        user.setTiliCount(user.getTiliCount() - 2 * num);
        userMapper.updateuser(user);
        UserInfo userInfo = new UserInfo();
        BeanUtils.copyProperties(user, userInfo);
        userInfo.setLevelUp(levelUp);
        //获取卡牌数据
        List<Characters> characterList = charactersMapper.selectByUserId(user.getUserId());
        userInfo.setCharacterList(formateCharacter(characterList));
        map.put("levelUp", levelUp);
        map.put("user", userInfo);
        baseResp.setData(map);
        baseResp.setSuccess(1);
        return baseResp;
    }

    public boolean compareSegments(String str1, String str2) {
        // 拆分并解析第一个参数为三个数字
        int[] parts1 = parseAndValidate(str1);
        int part1First = parts1[0];
        int part1Second = parts1[1];
        int part1Third = parts1[2];

        // 拆分并解析第二个参数为三个数字
        int[] parts2 = parseAndValidate(str2);
        int part2First = parts2[0];
        int part2Second = parts2[1];
        int part2Third = parts2[2];

        // 规则1：第一位前面小于后面 → 返回true
        if (part1First < part2First) {
            return true;
        }
        // 第一位相等时进入后续判断
        else if (part1First == part2First) {
            // 规则2：第二位前面小于后面 → 返回true
            if (part1Second < part2Second) {
                return true;
            }
            // 第一位、第二位都相等时
            else if (part1Second == part2Second) {
                // 规则3：后面第三位不是10 → 返回false
                return part2Third == 10; // 等价于：if (part2Third !=10) return false; else 返回true
            }
        }

        // 其他情况（比如第一位更大、第二位更大等）返回false
        return false;
    }


    private int[] parseAndValidate(String str) {
        // 按"-"拆分字符串
        String[] parts = str.split("-");
        // 验证格式是否为3段
        if (parts.length != 3) {
            throw new IllegalArgumentException("参数格式错误，需为x-x-x形式，如5-5-1，当前参数：" + str);
        }

        int[] nums = new int[3];
        try {
            // 转换为整数
            nums[0] = Integer.parseInt(parts[0]);
            nums[1] = Integer.parseInt(parts[1]);
            nums[2] = Integer.parseInt(parts[2]);
        } catch (NumberFormatException e) {
            throw new IllegalArgumentException("参数包含非数字字符，当前参数：" + str, e);
        }

        // 验证第三位不超过10
        if (nums[2] > 10) {
            throw new IllegalArgumentException("第三位数字最大为10，当前参数：" + str);
        }

        return nums;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    @NoRepeatSubmit(limitSeconds = 1)
    public BaseResp start5(TokenDto token, HttpServletRequest request) throws Exception {
        Map map = new HashMap();
        BaseResp baseResp = new BaseResp();

        // ======== 防刷机制 - 开始 ========
        // 1. 基础登录校验
        if (token == null || Xtool.isNull(token.getToken())) {
            baseResp.setSuccess(0);
            baseResp.setErrorMsg("登录过期");
            return baseResp;
        }

        String userId = token.getUserId();
        if (Xtool.isNull(userId)) {
            baseResp.setSuccess(0);
            baseResp.setErrorMsg("登录过期");
            return baseResp;
        }

        // 2. 请求频率限制（防止高频调用）
        String lockKey = "REDIS_KEY_BATTLE_LIMIT_" + userId + "_" + token.getStr();
        try {
            Object countObj = redisTemplate.opsForValue().get(lockKey);
            Long currentCount = null;

// 安全转换：处理 null/字符串/数字等情况
            if (countObj != null) {
                if (countObj instanceof Long) {
                    currentCount = (Long) countObj;
                } else if (countObj instanceof String) {
                    try {
                        currentCount = Long.parseLong((String) countObj);
                    } catch (NumberFormatException e) {
                        // 解析失败，视为无效计数，重置为0
                        currentCount = 0L;
                    }
                }
            }

            if (currentCount == null) {
                // 首次请求，初始化计数并设置过期时间
                redisTemplate.opsForValue().set(lockKey, "1", 1, TimeUnit.SECONDS);
            } else {
                // 超过阈值，抛出异常
                baseResp.setErrorMsg("操作过于频繁");
                baseResp.setSuccess(0);
                return baseResp;
            }

            Integer ceng = 0;

            // 原有业务逻辑 - 开始
            User user = userMapper.selectUserByUserId(Integer.parseInt(userId));
            if (token.getStr().equals("bronzetower")) {
                ceng = user.getBronze1();
                if (user.getBronze1() > 100) {
                    baseResp.setSuccess(0);
                    baseResp.setErrorMsg("塔已通关，可以选择重置继续试炼");
                    return baseResp;
                }
            } else if (token.getStr().equals("silvertower")) {
                ceng = user.getSilvertower();
                if (user.getSilvertower() > 100) {
                    baseResp.setSuccess(0);
                    baseResp.setErrorMsg("塔已通关，可以选择重置继续试炼");
                    return baseResp;
                }
            } else if (token.getStr().equals("goldentower")) {
                ceng = user.getGoldentower();
                if (user.getGoldentower() > 100) {
                    baseResp.setSuccess(0);
                    baseResp.setErrorMsg("塔已通关，可以选择重置继续试炼");
                    return baseResp;
                }
            }

            // ======== 防刷机制 - 结束 ========

            //自己的战队
            List<Characters> leftCharacter = charactersMapper.goIntoListById(user.getUserId() + "");
            if (Xtool.isNull(leftCharacter)) {
                baseResp.setSuccess(0);
                baseResp.setErrorMsg("你没有配置战队无法战斗");
                return baseResp;
            }
            for (Characters characters : leftCharacter) {
                List<EqCharacters> eqCharacters = eqCharactersMapper.listByGoOn(userId, characters.getId());
                if (Xtool.isNotNull(eqCharacters)) {
                    characters.setEqCharactersList(formateEqCharacter(eqCharacters));
                }
            }
            if (ceng > 100) {
                baseResp.setSuccess(0);
                baseResp.setErrorMsg("塔已通关，可以选择重置继续试炼");
                return baseResp;
            }
            baseResp.setSuccess(1);
            List<Characters> rightCharacter = new ArrayList<>();
            Map map1 = new HashMap();
            map1.put("detail_code", ceng);
            map1.put("activity_code", token.getStr());
            List<BronzeBossDetail> bronzeBossDetails = bronzeBossDetailMapper.selectByMap(map1);
            Integer i = 0;
            for (BronzeBossDetail pveBossDetail : bronzeBossDetails) {
                Card card = cardMapper.selectByid(pveBossDetail.getBossId());
                Characters characters = new Characters();
                BeanUtils.copyProperties(card, characters);
                characters.setGoIntoNum(pveBossDetail.getGoIntoNum());
                characters.setLv(pveBossDetail.getDifficultyLevel());
                characters.setUuid(i);
                long originalCount = bronzeBossDetails.stream()
                        .filter(x -> (x.getBossId() + "").equals(pveBossDetail.getBossId() + "")) // 过滤null对象
                        .map(BronzeBossDetail::getBossId)
                        .count();
                if (originalCount > 1) {
                    characters.setName(characters.getName() + i);
                } else {
                    characters.setName(characters.getName());
                }
                rightCharacter.add(characters);
                i++;
            }
            Map map3 = new HashMap();
            map3.put("floor_num", ceng);
            map3.put("activity_code", token.getStr());
            List<BronzeTower> bronzeTower = bronzeTowerMapper.selectByMap(map3);
            Battle battle = this.battle(leftCharacter, Integer.parseInt(userId), user.getNickname(), rightCharacter, 0, bronzeTower.get(0).getBossName(), user.getGameImg(), "0");

            if (battle.getIsWin() == 0) {
                // 防刷：通关后记录结果，防止重复领奖
                if (token.getStr().equals("bronzetower")) {
                    user.setBronze1(user.getBronze1() + 1);
                    if (user.getBronze1() > 100) {
                        user.setBronze1Time(new Date());
                    }
                } else if (token.getStr().equals("silvertower")) {
                    user.setSilvertower(user.getSilvertower() + 1);
                    if (user.getSilvertower() > 100) {
                        user.setSilvertowerTime(new Date());
                    }
                } else if (token.getStr().equals("goldentower")) {
                    user.setGoldentower(user.getGoldentower() + 1);
                    if (user.getGoldentower() > 100) {
                        user.setGoldentowerTime(new Date());
                    }
                }

                Map map2 = new HashMap();
                map2.put("player_id", userId);
                map2.put("bronze_type", token.getStr());
                List<PlayerBronzeTower> playerBronzeTowerList = playerBronzeTowerMapper.selectByMap(map2);
                if (Xtool.isNotNull(playerBronzeTowerList)) {
                    PlayerBronzeTower playerBronzeTower = playerBronzeTowerList.get(0);
                    if (ceng > playerBronzeTower.getFloorNum()) {
                        playerBronzeTower.setFloorNum(ceng);
                        playerBronzeTower.setPassTime(new Date());
                        playerBronzeTowerMapper.updateById(playerBronzeTower);
                    }
                } else {
                    PlayerBronzeTower playerBronzeTower = new PlayerBronzeTower();
                    playerBronzeTower.setFloorNum(ceng);
                    playerBronzeTower.setIsGetReward(0);
                    playerBronzeTower.setPlayerId(userId);
                    playerBronzeTower.setPassTime(new Date());
                    playerBronzeTower.setBronzeType(token.getStr());
                    playerBronzeTowerMapper.insert(playerBronzeTower);
                }

                List<PveReward> pveRewards = new ArrayList<>();
                BronzeTower bronzeTower1 = bronzeTower.get(0);
                if (Xtool.isNotNull(bronzeTower1.getRewardGold()) && bronzeTower1.getRewardGold() > 0) {
                    PveReward pveReward = new PveReward();
                    pveReward.setItemId(0);
                    pveReward.setRewardAmount(bronzeTower1.getRewardGold());
                    pveReward.setRewardType("2");
                    pveRewards.add(pveReward);
                }
                if (ProbabilityBooleanUtils.randomByProbability(0.2)) {
                    if (Xtool.isNotNull(bronzeTower1.getRewardDiamond()) && bronzeTower1.getRewardDiamond() > 0) {
                        PveReward pveReward = new PveReward();
                        pveReward.setItemId(0);
                        pveReward.setRewardAmount(bronzeTower1.getRewardDiamond());
                        pveReward.setRewardType("1");
                        pveRewards.add(pveReward);
                    }
                }


                if (Xtool.isNotNull(bronzeTower1.getRewardItem1())) {
                    PveReward pveReward = new PveReward();
                    GameItemBase gameItemBase = gameItemBaseMapper.selectById(bronzeTower1.getRewardItem1());
                    pveReward.setImg(gameItemBase.getIcon());
                    pveReward.setItemName(gameItemBase.getItemName() + bronzeTower1.getRewardItem1Num());
                    pveReward.setItemId(Integer.parseInt(bronzeTower1.getRewardItem1()));
                    pveReward.setRewardAmount(bronzeTower1.getRewardItem1Num());
                    pveReward.setRewardType("6");
                    pveRewards.add(pveReward);
                }
                if (ProbabilityBooleanUtils.randomByProbability(0.2)) {
                    if (token.getStr().equals("bronzetower")) {
                        PveReward pveReward = new PveReward();
                        pveReward.setItemId(17000107);
                        pveReward.setRewardAmount(1);
                        pveReward.setRewardType("7");
                        pveReward.setItemName("下级铸魂石");
                        pveRewards.add(pveReward);
                    } else if (token.getStr().equals("silvertower")) {
                        PveReward pveReward = new PveReward();
                        pveReward.setItemId(17000108);
                        pveReward.setRewardAmount(1);
                        pveReward.setItemName("中级铸魂石");
                        pveReward.setRewardType("7");
                        pveRewards.add(pveReward);
                    } else if (token.getStr().equals("goldentower")) {
                        PveReward pveReward = new PveReward();
                        pveReward.setItemId(17000109);
                        pveReward.setRewardAmount(1);
                        pveReward.setItemName("高级铸魂石");
                        pveReward.setRewardType("7");
                        pveRewards.add(pveReward);
                    }
                }


                // 防刷：奖励发放增加日志记录（建议接入日志框架如logback/log4j2）
                for (PveReward content : pveRewards) {
                    // 记录奖励发放日志，便于审计
                    // log.info("用户{}领取{}关卡奖励：类型{}，数量{}，物品ID{}", userId, token.getFinalLevel(), content.getRewardType(), content.getRewardAmount(), content.getItemId());

                    if ("1".equals(content.getRewardType() + "")) {
                        //钻石
                        user.setDiamond(user.getDiamond().add(new BigDecimal(content.getRewardAmount())));
                    } else if ("2".equals(content.getRewardType() + "")) {
                        //金币
                        user.setGold(user.getGold().add(new BigDecimal(content.getRewardAmount())));
                    } else if ("3".equals(content.getRewardType() + "")) {
                        //魂
                        user.setSoul(user.getSoul().add(new BigDecimal(content.getRewardAmount())));
                    } else if ("4".equals(content.getRewardType() + "")) {
                        //角色
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
                    } else if ("7".equals(content.getRewardType() + "")) {
                        //装备
                        EqCharacters characters = new EqCharacters();
                        characters.setStackCount(0);
                        characters.setId(content.getItemId()+"");
                        characters.setLv(1);
                        characters.setUserId(Integer.parseInt(userId));
                        characters.setMaxLv(1);
                        eqCharactersMapper.insert(characters);
                    } else  if ("5".equals(content.getRewardType() + "") || "6".equals(content.getRewardType() + "")) {
                        //物品
                        Map itemMap = new HashMap();
                        itemMap.put("item_id", content.getItemId());
                        itemMap.put("user_id", userId);
                        itemMap.put("is_delete", "0");
                        List<GamePlayerBag> playerBagList = gamePlayerBagMapper.selectByMap(itemMap);
                        if (Xtool.isNotNull(playerBagList)) {
                            GamePlayerBag playerBag = playerBagList.get(0);
                            playerBag.setItemCount(playerBag.getItemCount() + content.getRewardAmount());
                            gamePlayerBagMapper.updateById(playerBag);
                        } else {
                            GamePlayerBag playerBag = new GamePlayerBag();
                            playerBag.setUserId(Integer.parseInt(userId));
                            playerBag.setItemCount(content.getRewardAmount());
                            playerBag.setGridIndex(1);
                            playerBag.setItemId(content.getItemId());
                            gamePlayerBagMapper.insert(playerBag);
                        }
                    }
                }
                map.put("rewards", pveRewards);
            }

            userMapper.updateuser(user);
            User user2 = userMapper.selectUserByUserId(Integer.parseInt(userId));
            UserInfo info = new UserInfo();
            BeanUtils.copyProperties(user2, info);
            info.setBronze(0);
            info.setDarkSteel(0);
            info.setPurpleGold(0);
            info.setCrystal(0);
            GamePlayerBag playerBag = gamePlayerBagMapper.goIntoListByIdAndItemId(userId, 13);
            if (playerBag != null) {
                info.setBronze(playerBag.getItemCount());
            }
            GamePlayerBag playerBag1 = gamePlayerBagMapper.goIntoListByIdAndItemId(userId, 14);
            if (playerBag1 != null) {
                info.setDarkSteel(playerBag1.getItemCount());
            }
            GamePlayerBag playerBag2 = gamePlayerBagMapper.goIntoListByIdAndItemId(userId, 15);
            if (playerBag2 != null) {
                info.setPurpleGold(playerBag2.getItemCount());
            }
            GamePlayerBag playerBag3 = gamePlayerBagMapper.goIntoListByIdAndItemId(userId, 16);
            if (playerBag3 != null) {
                info.setCrystal(playerBag3.getItemCount());
            }
            map.put("user", info);
            map.put("battle", battle);
            baseResp.setData(map);

            if ("bronzetower".equals(token.getStr())) {
                ImageLevelResult result10 = LevelImageCalculator.calculate(user2.getBronze1());
                map.put("positionInImage", result10.getPositionInImage() - 1);
                map.put("currentImageNumbers", result10.getCurrentImageNumbers());
                map.put("nextImageNumbers", result10.getNextImageNumbers());
            } else if ("silvertower".equals(token.getStr())) {
                ImageLevelResult result10 = LevelImageCalculator.calculate(user2.getSilvertower());
                map.put("positionInImage", result10.getPositionInImage() - 1);
                map.put("currentImageNumbers", result10.getCurrentImageNumbers());
                map.put("nextImageNumbers", result10.getNextImageNumbers());
            } else if ("goldentower".equals(token.getStr())) {
                ImageLevelResult result10 = LevelImageCalculator.calculate(user2.getGoldentower());
                map.put("positionInImage", result10.getPositionInImage() - 1);
                map.put("currentImageNumbers", result10.getCurrentImageNumbers());
                map.put("nextImageNumbers", result10.getNextImageNumbers());
            }
            baseResp.setSuccess(1);

        } finally {
            // 释放锁（只有加锁成功的线程才释放）
            redisTemplate.delete(lockKey);
        }

        return baseResp;
    }

    @Override
    public BaseResp getTower(TokenDto token, HttpServletRequest request) throws Exception {
        //先获取当前用户战队
        BaseResp baseResp = new BaseResp();
        if (token == null || Xtool.isNull(token.getToken())) {
            baseResp.setSuccess(0);
            baseResp.setErrorMsg("登录过期");
            return baseResp;
        }
//        String userId = (String) redisTemplate.opsForValue().get(token.getToken());
        String userId = token.getUserId();
        if (Xtool.isNull(userId)) {
            baseResp.setSuccess(0);
            baseResp.setErrorMsg("登录过期");
            return baseResp;
        }
        User user = userMapper.selectUserByUserId(Integer.parseInt(userId));
        //如果是
        if ("bronzetower".equals(token.getStr())) {
            //判断是否通关第四图
            List<String> strings = Arrays.asList(user.getChapter().split("-"));
            if (Integer.parseInt(strings.get(0)) < 5) {
                baseResp.setSuccess(0);
                baseResp.setErrorMsg("您还未通关第4章");
                return baseResp;
            }
        }
        if ("silvertower".equals(token.getStr())) {
            //判断是否通关第四图
            List<String> strings = Arrays.asList(user.getChapter().split("-"));
            if (Integer.parseInt(strings.get(0)) < 6) {
                baseResp.setSuccess(0);
                baseResp.setErrorMsg("您还未通关第5章");
                return baseResp;
            }
        }
        if ("goldentower".equals(token.getStr())) {
            //判断是否通关第四图
            List<String> strings = Arrays.asList(user.getChapter().split("-"));
            if (Integer.parseInt(strings.get(0)) < 7) {
                baseResp.setSuccess(0);
                baseResp.setErrorMsg("您还未通关第6章");
                return baseResp;
            }
        }
        baseResp.setSuccess(1);
        UserInfo info = new UserInfo();
        Map map = new HashMap();
        map.put("userInfo", info);
        BeanUtils.copyProperties(user, info);
        if ("bronzetower".equals(token.getStr())) {
            ImageLevelResult result10 = LevelImageCalculator.calculate(user.getBronze1());
            map.put("positionInImage", result10.getPositionInImage() - 1);
            map.put("currentImageNumbers", result10.getCurrentImageNumbers());
            map.put("nextImageNumbers", result10.getNextImageNumbers());
        } else if ("silvertower".equals(token.getStr())) {
            ImageLevelResult result10 = LevelImageCalculator.calculate(user.getSilvertower());
            map.put("positionInImage", result10.getPositionInImage() - 1);
            map.put("currentImageNumbers", result10.getCurrentImageNumbers());
            map.put("nextImageNumbers", result10.getNextImageNumbers());
        } else if ("goldentower".equals(token.getStr())) {
            ImageLevelResult result10 = LevelImageCalculator.calculate(user.getGoldentower());
            map.put("positionInImage", result10.getPositionInImage() - 1);
            map.put("currentImageNumbers", result10.getCurrentImageNumbers());
            map.put("nextImageNumbers", result10.getNextImageNumbers());
        }
        baseResp.setData(map);
        return baseResp;
    }

    @Override
    @Transactional
    @NoRepeatSubmit(limitSeconds = 1)
    public BaseResp start4(TokenDto token, HttpServletRequest request) throws Exception {
        //先获取当前用户战队
        BaseResp baseResp = new BaseResp();
        if (token == null || Xtool.isNull(token.getToken())) {
            baseResp.setSuccess(0);
            baseResp.setErrorMsg("登录过期");
            return baseResp;
        }
        String userId = token.getUserId();
//        String userId = (String) redisTemplate.opsForValue().get(token.getToken());
        if (Xtool.isNull(userId)) {
            baseResp.setSuccess(0);
            baseResp.setErrorMsg("登录过期");
            return baseResp;
        }
        User user = userMapper.selectUserByUserId(Integer.parseInt(userId));
        if (user.getHuoliCount() - 30 < 0) {
            baseResp.setSuccess(0);
            baseResp.setErrorMsg("活力不足");
            return baseResp;
        }
        if (user.getArenaCount() <= 0) {
            baseResp.setSuccess(0);
            baseResp.setErrorMsg("挑战次数不足");
            return baseResp;
        }
        Integer arenaWeek = ArenaWeekUtils.getCurrentUniqueWeekNum(new Date());
        //自己的战队
        Map map = new HashMap();
        map.put("arena_level", token.getFinalLevel());
        map.put("week_num", arenaWeek);
        map.put("user_id", token.getUserId());
        List<GameArenaSignup> gameArenaSignups = gameArenaSignupMapper.selectByMap(map);
        if (Xtool.isNull(gameArenaSignups)) {
            baseResp.setSuccess(0);
            baseResp.setErrorMsg("你没有报名该赛季");
            return baseResp;
        }
        GameArenaSignup gameArenaSignup = gameArenaSignups.get(0);
        List<Characters> leftCharacter = gameArenaBattlecharactersMapper.findCharacters(token.getFinalLevel(), arenaWeek, userId);
        if (Xtool.isNull(leftCharacter)) {
            baseResp.setSuccess(0);
            baseResp.setErrorMsg("你没有配置战队无法战斗");
            return baseResp;
        }
        for (Characters characters : leftCharacter) {
            List<EqCharacters> eqCharacters = eqCharactersMapper.listByGoOn(userId, characters.getId());
            if (Xtool.isNotNull(eqCharacters)) {
                characters.setEqCharactersList(formateEqCharacter(eqCharacters));
            }
        }
        Collections.sort(leftCharacter, Comparator.comparing(Characters::getGoIntoNum));
        List<GameArenaSignup> gameArenaSignups2 = gameArenaSignupMapper.gameArena(gameArenaSignup.getArenaScore(), token.getUserId(), token.getFinalLevel(), arenaWeek);
        if (Xtool.isNull(gameArenaSignups2)) {
            baseResp.setSuccess(0);
            baseResp.setErrorMsg("暂无对手请稍后尝试");
            return baseResp;
        }
        GameArenaSignup gameArenaSignup2 = gameArenaSignups2.get(0);
        Map map2 = new HashMap();
        map2.put("arena_level", token.getFinalLevel());
        map2.put("week_num", arenaWeek);
        map2.put("user_id", gameArenaSignup2.getUserId());
        List<Characters> rightCharacter = gameArenaBattlecharactersMapper.findCharacters(token.getFinalLevel(), arenaWeek, gameArenaSignup2.getUserId() + "");
        if (Xtool.isNull(rightCharacter)) {
            baseResp.setSuccess(0);
            baseResp.setErrorMsg("暂无对手请稍后尝试");
            return baseResp;
        }
        for (Characters characters : rightCharacter) {
            List<EqCharacters> eqCharacters = eqCharactersMapper.listByGoOn(gameArenaSignup2.getUserId() + "", characters.getId());
            if (Xtool.isNotNull(eqCharacters)) {
                characters.setEqCharactersList(formateEqCharacter(eqCharacters));
            }
        }
        Collections.sort(rightCharacter, Comparator.comparing(Characters::getGoIntoNum));
        baseResp.setSuccess(1);
        Battle battle = this.battle(leftCharacter, Integer.parseInt(userId), user.getNickname(), rightCharacter, gameArenaSignup2.getUserId(), gameArenaSignup2.getUserName(), user.getGameImg(), "4");

        if (battle.getIsWin() == 0) {
            gameArenaSignup.setArenaScore(gameArenaSignup.getArenaScore() + 1);
            gameArenaSignup.setWinNum(gameArenaSignup.getWinNum() + 1);
        } else {
            gameArenaSignup.setArenaScore(gameArenaSignup.getArenaScore() - 1);
            gameArenaSignup.setLoseNum(gameArenaSignup.getLoseNum() + 1);
            if (gameArenaSignup.getArenaScore() < 0) {
                gameArenaSignup.setArenaScore(0);
            }
        }
        gameArenaSignupMapper.updateById(gameArenaSignup);
        user.setGold(user.getGold().add(new BigDecimal(5460)));
        GameArenaBattle gameArenaBattle1 = new GameArenaBattle();
        gameArenaBattle1.setIsWin(battle.getIsWin());
        gameArenaBattle1.setImg(user.getGameImg());
        gameArenaBattle1.setUserId(Integer.parseInt(userId));
        gameArenaBattle1.setGameFightId(battle.getId());
        gameArenaBattle1.setToUserId(gameArenaSignup2.getUserId());
        gameArenaBattle1.setArenaLevel(token.getFinalLevel());
        gameArenaBattle1.setWeekNum(arenaWeek);
        gameArenaBattle1.setBattleLastTime(new Date());
        gameArenaBattle1.setCreatetime(new Date());
        gameArenaBattle1.setUserName(gameArenaSignup.getUserName());
        gameArenaBattle1.setToUserName(gameArenaSignup2.getUserName());
        gameArenaBattleMapper.insert(gameArenaBattle1);
        gameArenaSignup.setCount(gameArenaSignup.getCount() - 1);
        user.setHuoliCount(user.getHuoliCount() - 30);
        user.setArenaCount(user.getArenaCount() - 1);
        userMapper.updateuser(user);
        UserInfo userInfo = new UserInfo();
        BeanUtils.copyProperties(user, userInfo);
        map.put("userInfo", userInfo);
        map.put("battle", battle);
        map.put("gameArenaSignup", gameArenaSignup);
        List<GameArenaBattle> gameArenaBattle = gameArenaBattleMapper.selectList(new Wrapper<GameArenaBattle>() {
            @Override
            public String getSqlSegment() {
                return "where arena_level=" + token.getFinalLevel() + " and week_num=" + arenaWeek + " ORDER BY  createtime desc limit 1";
            }
        });
        if (Xtool.isNotNull(gameArenaBattle)) {
            GameArenaBattle gameArenaBattle2 = gameArenaBattle.get(0);
            gameArenaBattle2.setTimeStr(this.formatTime(gameArenaBattle2.getCreatetime()));
            map.put("gameArenaBattle", gameArenaBattle2);
        }
        Integer ranking = gameArenaRankMapper.getArenaRanking(userId, token.getFinalLevel(), arenaWeek);
        map.put("ranking", ranking);
        baseResp.setData(map);
        baseResp.setSuccess(1);
        return baseResp;
    }

    @Override
    @Transactional
    @NoRepeatSubmit(limitSeconds = 1)
    public BaseResp start6(TokenDto token, HttpServletRequest request) throws Exception {
        //先获取当前用户战队
        BaseResp baseResp = new BaseResp();
        if (token == null || Xtool.isNull(token.getToken())) {
            baseResp.setSuccess(0);
            baseResp.setErrorMsg("登录过期");
            return baseResp;
        }
//        String userId = (String) redisTemplate.opsForValue().get(token.getToken());
        String userId = token.getId();
        if (Xtool.isNull(userId)) {
            baseResp.setSuccess(0);
            baseResp.setErrorMsg("登录过期");
            return baseResp;
        }
        User user = userMapper.selectUserByUserId(Integer.parseInt(userId));
        if (user.getHuoliCount() - 10 < 0) {
            baseResp.setSuccess(0);
            baseResp.setErrorMsg("活力不足");
            return baseResp;
        }
        if (user.getDuoCount() <= 0) {
            baseResp.setSuccess(0);
            baseResp.setErrorMsg("挑战次数不足");
            return baseResp;
        }
        //自己的战队
        List<Characters> leftCharacter = charactersMapper.goIntoListById(userId);
        if (Xtool.isNull(leftCharacter)) {
            baseResp.setSuccess(0);
            baseResp.setErrorMsg("你没有配置战队无法战斗");
            return baseResp;
        }
        for (Characters characters : leftCharacter) {
            List<EqCharacters> eqCharacters = eqCharactersMapper.listByGoOn(userId, characters.getId());
            if (Xtool.isNotNull(eqCharacters)) {
                characters.setEqCharactersList(formateEqCharacter(eqCharacters));
            }
        }
        Collections.sort(leftCharacter, Comparator.comparing(Characters::getGoIntoNum));
        //对手战队
        User user1 = userMapper.selectUserByUserId(Integer.parseInt(token.getUserId()));
        if (user1.getDuoTime()!=null&&user1.getDuoTime().compareTo(new Date())>=0){
            baseResp.setSuccess(0);
            baseResp.setErrorMsg("对面还处于抢夺保护期");
            return baseResp;
        }
        List<Characters> rightCharacter = charactersMapper.goIntoListById(token.getUserId() + "");
        if (Xtool.isNull(rightCharacter)) {
            Card card = cardMapper.selectByid(3);
            if (card == null) {
                baseResp.setErrorMsg("服务器异常联想管理员");
                baseResp.setSuccess(0);
                return baseResp;
            }
            Characters characters = new Characters();
            BeanUtils.copyProperties(card, characters);
            characters.setId("1002");
            characters.setGoIntoNum(1);
            characters.setLv(1);
            characters.setUserId(Integer.parseInt(token.getUserId()));
            characters.setStar(new BigDecimal(1));
            characters.setMaxLv(CardMaxLevelUtils.getMaxLevel(card.getName(), card.getStar().doubleValue()));
            rightCharacter.add(characters);
        }
        List<PveReward> pveRewards = new ArrayList<>();
        baseResp.setSuccess(1);
        Battle battle = this.battle(leftCharacter, Integer.parseInt(userId), user.getNickname(), rightCharacter, Integer.parseInt(token.getUserId()), user1.getNickname(), user.getGameImg(), "1");
        if (battle.getIsWin() == 0) {
            if (ProbabilityBooleanUtils.randomByProbability(0.5)) {
                if ("23".equals(token.getStr())){
                    token.setStr("19");
                }else if ("22".equals(token.getStr())){
                    token.setStr("18");
                } else  if ("21".equals(token.getStr())){
                    token.setStr("20");
                }
            }
            Map itemMap = new HashMap();
            itemMap.put("item_id", token.getStr());
            itemMap.put("user_id", token.getUserId());
            itemMap.put("is_delete", "0");
            List<GamePlayerBag> playerBagList = gamePlayerBagMapper.selectByMap(itemMap);

            Map itemMap2 = new HashMap();
            itemMap2.put("item_id", token.getStr());
            itemMap2.put("user_id", userId);
            itemMap2.put("is_delete", "0");
            List<GamePlayerBag> playerBagList2 = gamePlayerBagMapper.selectByMap(itemMap2);

            if (Xtool.isNotNull(playerBagList)) {
                GamePlayerBag playerBag = playerBagList.get(0);
                Integer count = Math.round(playerBag.getItemCount() * 0.2f);
                playerBag.setItemCount(playerBag.getItemCount() - count);
                if (playerBag.getItemCount() <= 0) {
                    playerBag.setIsDelete("0");
                }
                PveReward pveReward = new PveReward();
                GameItemBase gameItemBase = gameItemBaseMapper.selectById(Integer.parseInt(token.getStr()));
                pveReward.setImg(gameItemBase.getIcon());
                pveReward.setItemName(gameItemBase.getItemName() + " " + count);
                pveReward.setItemId(gameItemBase.getItemId());
                pveReward.setRewardAmount(count);
                pveReward.setRewardType("6");
                pveRewards.add(pveReward);
                gamePlayerBagMapper.updateById(playerBag);
                PillRobRecord pillRobRecord = new PillRobRecord();
                pillRobRecord.setRobberId(Integer.parseInt(userId));
                pillRobRecord.setVictimId(Integer.parseInt(token.getUserId()));
                pillRobRecord.setRobTime(new Date());
                pillRobRecord.setRobDate(new Date());
                pillRobRecord.setRobResult(1);
                pillRobRecord.setRobPillNum(count);
                pillRobRecord.setRobMaterial(token.getStr());
                pillRobRecord.setCreateTime(new Date());
                pillRobRecord.setFreeRobCount(1);
                pillRobRecord.setUpdateTime(new Date());
                pillRobRecord.setFightId(battle.getId());
                pillRobRecordMapper.insert(pillRobRecord);
                if (Xtool.isNotNull(playerBagList2)) {
                    GamePlayerBag playerBag2 = playerBagList2.get(0);
                    playerBag2.setItemCount(playerBag2.getItemCount() + count);
                    gamePlayerBagMapper.updateById(playerBag2);
                } else {
                    GamePlayerBag playerBag2 = new GamePlayerBag();
                    playerBag2.setUserId(Integer.parseInt(userId));
                    playerBag2.setItemCount(count);
                    playerBag2.setGridIndex(1);
                    playerBag2.setItemId(Integer.parseInt(token.getStr()));
                    gamePlayerBagMapper.insert(playerBag2);
                }
            }
        }

        Map map = new HashMap();
        map.put("rewards", pveRewards);
        user.setDuoCount(user.getDuoCount() - 1);
        user.setHuoliCount(user.getHuoliCount() - 10);
        userMapper.updateuser(user);
        UserInfo userInfo = new UserInfo();
        BeanUtils.copyProperties(user, userInfo);
        baseResp.setData(battle);
        map.put("user", userInfo);
        map.put("battle", battle);
        map.put("duoCount", user.getDuoCount());
        baseResp.setData(map);
        return baseResp;
    }

    @Override
    @Transactional
    @NoRepeatSubmit(limitSeconds = 1)
    public BaseResp feiShenhechen(TokenDto token, HttpServletRequest request) throws Exception {
        //先获取当前用户战队
        BaseResp baseResp = new BaseResp();
        if (token == null || Xtool.isNull(token.getToken())) {
            baseResp.setSuccess(0);
            baseResp.setErrorMsg("登录过期");
            return baseResp;
        }
//        String userId = (String) redisTemplate.opsForValue().get(token.getToken());
        String userId = token.getUserId();
        if (Xtool.isNull(userId)) {
            baseResp.setSuccess(0);
            baseResp.setErrorMsg("登录过期");
            return baseResp;
        }
        if (Xtool.isNotNull(token.getStr())&&Integer.parseInt(token.getStr())<7){
            baseResp.setSuccess(0);
            baseResp.setErrorMsg("材料至少选择7个材料");
            return baseResp;
        }
        if ("1".equals(token.getId())){
            Map itemMap = new HashMap();
            itemMap.put("item_id", 19);
            itemMap.put("user_id", token.getUserId());
            itemMap.put("is_delete", "0");
            List<GamePlayerBag> playerBagList = gamePlayerBagMapper.selectByMap(itemMap);
            if (Xtool.isNull(playerBagList)){
                baseResp.setSuccess(0);
                baseResp.setErrorMsg("材料不足");
                return baseResp;
            }
            GamePlayerBag playerBag=playerBagList.get(0);
            if (playerBag.getItemCount()-Integer.parseInt(token.getStr())<0){
                baseResp.setSuccess(0);
                baseResp.setErrorMsg("材料不足");
                return baseResp;
            }
            int[] result = MaterialSynthesisUtil.calculate(Integer.parseInt(token.getStr()));
            // 扣减物品数量
            if (playerBag.getItemCount() - Integer.parseInt(token.getStr())+result[1] > 0) {
                playerBag.setItemCount(playerBag.getItemCount() -Integer.parseInt(token.getStr())+result[1]);
            } else {
                playerBag.setIsDelete("1");
            }
            gamePlayerBagMapper.updateById(playerBag);

            Map itemMap2 = new HashMap();
            itemMap2.put("item_id", 23);
            itemMap2.put("user_id", userId);
            itemMap2.put("is_delete", "0");
            List<GamePlayerBag> playerBagList2 = gamePlayerBagMapper.selectByMap(itemMap2);
            if (Xtool.isNotNull(playerBagList2)) {
                GamePlayerBag playerBag2 = playerBagList2.get(0);
                playerBag2.setItemCount(playerBag2.getItemCount() + result[0]);
                gamePlayerBagMapper.updateById(playerBag2);
            } else {
                GamePlayerBag playerBag2 = new GamePlayerBag();
                playerBag2.setUserId(Integer.parseInt(userId));
                playerBag2.setItemCount(result[0]);
                playerBag2.setGridIndex(1);
                playerBag2.setItemId(23);
                gamePlayerBagMapper.insert(playerBag2);
            }
        }else if ("2".equals(token.getId())){
            Map itemMap = new HashMap();
            itemMap.put("item_id", 20);
            itemMap.put("user_id", token.getUserId());
            itemMap.put("is_delete", "0");
            List<GamePlayerBag> playerBagList = gamePlayerBagMapper.selectByMap(itemMap);
            if (Xtool.isNull(playerBagList)){
                baseResp.setSuccess(0);
                baseResp.setErrorMsg("材料不足");
                return baseResp;
            }
            GamePlayerBag playerBag=playerBagList.get(0);
            if (playerBag.getItemCount()-Integer.parseInt(token.getStr())<0){
                baseResp.setSuccess(0);
                baseResp.setErrorMsg("材料不足");
                return baseResp;
            }
            int[] result = MaterialSynthesisUtil.calculate(Integer.parseInt(token.getStr()));
            // 扣减物品数量
            if (playerBag.getItemCount() - Integer.parseInt(token.getStr())+result[1] > 0) {
                playerBag.setItemCount(playerBag.getItemCount() -Integer.parseInt(token.getStr())+result[1]);
            } else {
                playerBag.setIsDelete("1");
            }
            gamePlayerBagMapper.updateById(playerBag);

            Map itemMap2 = new HashMap();
            itemMap2.put("item_id", 21);
            itemMap2.put("user_id", userId);
            itemMap2.put("is_delete", "0");
            List<GamePlayerBag> playerBagList2 = gamePlayerBagMapper.selectByMap(itemMap2);
            if (Xtool.isNotNull(playerBagList2)) {
                GamePlayerBag playerBag2 = playerBagList2.get(0);
                playerBag2.setItemCount(playerBag2.getItemCount() + result[0]);
                gamePlayerBagMapper.updateById(playerBag2);
            } else {
                GamePlayerBag playerBag2 = new GamePlayerBag();
                playerBag2.setUserId(Integer.parseInt(userId));
                playerBag2.setItemCount(result[0]);
                playerBag2.setGridIndex(1);
                playerBag2.setItemId(26);
                gamePlayerBagMapper.insert(playerBag2);
            }
        }else {
            Map itemMap = new HashMap();
            itemMap.put("item_id", 18);
            itemMap.put("user_id", token.getUserId());
            itemMap.put("is_delete", "0");
            List<GamePlayerBag> playerBagList = gamePlayerBagMapper.selectByMap(itemMap);
            if (Xtool.isNull(playerBagList)) {
                baseResp.setSuccess(0);
                baseResp.setErrorMsg("材料不足");
                return baseResp;
            }
            GamePlayerBag playerBag = playerBagList.get(0);
            if (playerBag.getItemCount() - Integer.parseInt(token.getStr()) < 0) {
                baseResp.setSuccess(0);
                baseResp.setErrorMsg("材料不足");
                return baseResp;
            }
            int[] result = MaterialSynthesisUtil.calculate(Integer.parseInt(token.getStr()));
            // 扣减物品数量
            if (playerBag.getItemCount() - Integer.parseInt(token.getStr()) + result[1] > 0) {
                playerBag.setItemCount(playerBag.getItemCount() - Integer.parseInt(token.getStr()) + result[1]);
            } else {
                playerBag.setIsDelete("1");
            }
            gamePlayerBagMapper.updateById(playerBag);

            Map itemMap2 = new HashMap();
            itemMap2.put("item_id", 22);
            itemMap2.put("user_id", userId);
            itemMap2.put("is_delete", "0");
            List<GamePlayerBag> playerBagList2 = gamePlayerBagMapper.selectByMap(itemMap2);
            if (Xtool.isNotNull(playerBagList2)) {
                GamePlayerBag playerBag2 = playerBagList2.get(0);
                playerBag2.setItemCount(playerBag2.getItemCount() + result[0]);
                gamePlayerBagMapper.updateById(playerBag2);
            } else {
                GamePlayerBag playerBag2 = new GamePlayerBag();
                playerBag2.setUserId(Integer.parseInt(userId));
                playerBag2.setItemCount(result[0]);
                playerBag2.setGridIndex(1);
                playerBag2.setItemId(24);
                gamePlayerBagMapper.insert(playerBag2);
            }
        }
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


    @Override
    public BaseResp jingji(TokenDto token, HttpServletRequest request) throws Exception {
        //先获取当前用户战队
        BaseResp baseResp = new BaseResp();
        if (token == null || Xtool.isNull(token.getToken())) {
            baseResp.setSuccess(0);
            baseResp.setErrorMsg("登录过期");
            return baseResp;
        }
//        String userId = (String) redisTemplate.opsForValue().get(token.getToken());
        String userId = token.getUserId();
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
    public BaseResp duoquJingji(TokenDto token, HttpServletRequest request) throws Exception {
        //先获取当前用户战队
        BaseResp baseResp = new BaseResp();
        if (token == null || Xtool.isNull(token.getToken())) {
            baseResp.setSuccess(0);
            baseResp.setErrorMsg("登录过期");
            return baseResp;
        }
//        String userId = (String) redisTemplate.opsForValue().get(token.getToken());
        String userId = token.getUserId();
        if (Xtool.isNull(userId)) {
            baseResp.setSuccess(0);
            baseResp.setErrorMsg("登录过期");
            return baseResp;
        }
        User user=userMapper.selectUserByUserId(Integer.parseInt(userId));
        //随机获取有队伍的5个人
        List<User> users = userMapper.SelectUserItemId(token.getId(), userId);
        List<UserInfo> infos = new ArrayList<>();
        for (User user1 : users) {
            UserInfo info = new UserInfo();
            BeanUtils.copyProperties(user1, info);
            infos.add(info);
        }
        baseResp.setSuccess(1);
        Map map = new HashMap();
        map.put("user", infos);
        map.put("duoCount",user.getDuoCount());
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
        List<User> users = friendRelationMapper.findByid(token.getUserId(), 1, null);
        List<UserInfo> userInfoList = new ArrayList<>();
        for (User user : users) {
            UserInfo userInfo = new UserInfo();
            BeanUtils.copyProperties(user, userInfo);
            userInfoList.add(userInfo);
        }


        baseResp.setSuccess(1);
        Map map = new HashMap();
        map.put("friends", userInfoList);
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
//        String userId = (String) redisTemplate.opsForValue().get(token.getToken());
        String userId = token.getId();
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
        if (friendRelationMapper.findCount(userId) >= 100) {
            baseResp.setSuccess(0);
            baseResp.setErrorMsg("你的好友已上限");
            return baseResp;
        }
        if (friendRelationMapper.findCount(token.getUserId()) >= 100) {
            baseResp.setSuccess(0);
            baseResp.setErrorMsg("对方好友已上限");
            return baseResp;
        }


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
        String userId = token.getUserId();
//        String userId = (String) redisTemplate.opsForValue().get(token.getToken());
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
            friendRelationMapper.deleteById(Long.parseLong(token.getId()));
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

    public List<Character> reasonableData2(List<Characters> charactersList) {

        //TODO 先初始化自身属性
        List<Character> characterList = new ArrayList<>();
        for (Characters characters : charactersList) {
            Character character = new Character();
            BeanUtils.copyProperties(characters, character);
            int[] skillLevel = CardSkillLevelUtil.calculateSkillLevels(character.getLv(), character.getStar().doubleValue());
            //格式化技能介绍
            if (Xtool.isNotNull(character.getPassiveIntroduceOneStr())) {
                character.setPassiveIntroduceOneStr(NumberExtractUtil.replaceNumbersWithLevel(character.getPassiveIntroduceOneStr(), skillLevel[0]));
            }
            if (Xtool.isNotNull(character.getPassiveIntroduceTwoStr())) {
                character.setPassiveIntroduceTwoStr(NumberExtractUtil.replaceNumbersWithLevel(character.getPassiveIntroduceTwoStr(), skillLevel[1]));
            }
            if (Xtool.isNotNull(character.getPassiveIntroduceThreeStr())) {
                character.setPassiveIntroduceThreeStr(NumberExtractUtil.replaceNumbersWithLevel(character.getPassiveIntroduceThreeStr(), skillLevel[2]));
            }
            if (Xtool.isNotNull(character.getPassiveIntroduceFourStr())) {
                character.setPassiveIntroduceFourStr(NumberExtractUtil.replaceNumbersWithLevel(character.getPassiveIntroduceFourStr(), skillLevel[2]));
            }
            BigDecimal lv = new BigDecimal(characters.getLv());
            BigDecimal maxHp = lv.multiply(characters.getHpGrowth().multiply(((characters.getStar().subtract(new BigDecimal(1))).multiply(new BigDecimal("0.15")).add(new BigDecimal(1))).multiply((lv.divide(new BigDecimal(80)).add(new BigDecimal("0.8"))))));
            BigDecimal attack = lv.multiply(characters.getAttackGrowth().multiply(((characters.getStar().subtract(new BigDecimal(1))).multiply(new BigDecimal("0.15")).add(new BigDecimal(1))).multiply((lv.divide(new BigDecimal(80)).add(new BigDecimal("0.8"))))));
            BigDecimal speed = lv.multiply(characters.getSpeedGrowth().multiply(((characters.getStar().subtract(new BigDecimal(1))).multiply(new BigDecimal("0.15")).add(new BigDecimal(1))).multiply((lv.divide(new BigDecimal(80)).add(new BigDecimal("0.8"))))));
            character.setMaxHp(maxHp.intValue());
            character.setHp(maxHp.intValue());
            character.setAttack(attack.intValue());
            character.setSpeed(speed.intValue());
            characterList.add(character);
        }

        return characterList;
    }

    public Character reasonableData(Characters characters, List<Characters> charactersList) {
        //TODO 先初始化自身属性
        Character character = new Character();
        BeanUtils.copyProperties(characters, character);
        int[] skillLevel = CardSkillLevelUtil.calculateSkillLevels(character.getLv(), characters.getStar().doubleValue());
        BigDecimal lv = new BigDecimal(characters.getLv());
        BigDecimal maxHp = lv.multiply(characters.getHpGrowth().multiply(((characters.getStar().subtract(new BigDecimal(1))).multiply(new BigDecimal("0.15")).add(new BigDecimal(1))).multiply((lv.divide(new BigDecimal(80)).add(new BigDecimal("0.8"))))));
        BigDecimal attack = lv.multiply(characters.getAttackGrowth().multiply(((characters.getStar().subtract(new BigDecimal(1))).multiply(new BigDecimal("0.15")).add(new BigDecimal(1))).multiply((lv.divide(new BigDecimal(80)).add(new BigDecimal("0.8"))))));
        BigDecimal speed = lv.multiply(characters.getSpeedGrowth().multiply(((characters.getStar().subtract(new BigDecimal(1))).multiply(new BigDecimal("0.15")).add(new BigDecimal(1))).multiply((lv.divide(new BigDecimal(80)).add(new BigDecimal("0.8"))))));
        character.setHp(maxHp.intValue());
        character.setStar(characters.getStar());
        character.setMaxHp(maxHp.intValue());
        character.setAttack(attack.intValue());
        character.setSpeed(speed.intValue());
        character.setWlAtk(0);
        character.setHyAtk(0);
        character.setDsAtk(0);
        character.setFdAtk(0);
        character.setWlDef(0);
        character.setHyDef(0);
        character.setDsDef(0);
        character.setFdDef(0);
        character.setZlDef(0);
        //TODO 装备属性
        if (Xtool.isNotNull(characters.getEqCharactersList())) {
            List<EqCharacters> eqCharacters = characters.getEqCharactersList();
            //攻击
            character.setWlAtk(eqCharacters.stream().map(EqCharacters::getWlAtk).mapToInt(wlAtk -> Objects.isNull(wlAtk) ? 0 : wlAtk).sum());
            character.setAttack(character.getAttack() + character.getWlAtk());
            character.setHyAtk(eqCharacters.stream().map(EqCharacters::getHyAtk).mapToInt(hyAtk -> Objects.isNull(hyAtk) ? 0 : hyAtk).sum());
            character.setDsAtk(eqCharacters.stream().map(EqCharacters::getDsAtk).mapToInt(dsAtk -> Objects.isNull(dsAtk) ? 0 : dsAtk).sum());
            character.setFdAtk(eqCharacters.stream().map(EqCharacters::getFdAtk).mapToInt(fdAtk -> Objects.isNull(fdAtk) ? 0 : fdAtk).sum());
            character.setWlDef(eqCharacters.stream().map(EqCharacters::getWlDef).mapToInt(wlDef -> Objects.isNull(wlDef) ? 0 : wlDef).sum());
            character.setHyDef(eqCharacters.stream().map(EqCharacters::getHyDef).mapToInt(hyDef -> Objects.isNull(hyDef) ? 0 : hyDef).sum());
            character.setDsDef(eqCharacters.stream().map(EqCharacters::getDsDef).mapToInt(dsDef -> Objects.isNull(dsDef) ? 0 : dsDef).sum());
            character.setFdDef(eqCharacters.stream().map(EqCharacters::getFdDef).mapToInt(fdDef -> Objects.isNull(fdDef) ? 0 : fdDef).sum());
            character.setZlDef(eqCharacters.stream().map(EqCharacters::getZlDef).mapToInt(zlDef -> Objects.isNull(zlDef) ? 0 : zlDef).sum());
        }
        //TODO 再叠加协同属性
        if (Xtool.isNotNull(charactersList)) {
            if (Xtool.isNotNull(characters.getPassiveIntroduceThree())) {
                List<Characters> xieTong = charactersList.stream().filter(x -> characters.getPassiveIntroduceThree().equals(x.getId())).collect(Collectors.toList());
                if (Xtool.isNotNull(xieTong)) {
                    if (skillLevel[2] > 0) {
                        //                453点生命上限，158点攻击，158点速度。
                        if (Xtool.isNotNull(characters.getCollHp())) {
                            character.setMaxHp(character.getMaxHp() + skillLevel[3] * characters.getCollHp());
                            character.setHp(character.getHp() + skillLevel[3] * characters.getCollHp());

                        }
                        if (Xtool.isNotNull(characters.getCollAttack())) {
                            character.setAttack(attack.intValue() + skillLevel[3] * characters.getCollAttack());
                        }
                        if (Xtool.isNotNull(characters.getCollSpeed())) {
                            character.setSpeed(speed.intValue() + skillLevel[3] * characters.getCollSpeed());
                        }
                        //TODO 协同属性加成
                        character.setWlAtk(character.getWlAtk() + characters.getWlAtk() * skillLevel[3]);
                        character.setAttack(character.getAttack() + characters.getWlAtk() * skillLevel[3]);
                        character.setHyAtk(character.getHyAtk() + characters.getHyAtk() * skillLevel[3]);
                        character.setDsAtk(character.getDsAtk() + characters.getDsAtk() * skillLevel[3]);
                        character.setFdAtk(character.getFdAtk() + characters.getFdAtk() * skillLevel[3]);
                        character.setWlDef(character.getWlDef() + characters.getWlDef() * skillLevel[3]);
                        character.setHyDef(character.getHyDef() + characters.getHyDef() * skillLevel[3]);
                        character.setDsDef(character.getDsDef() + characters.getDsDef() * skillLevel[3]);
                        character.setFdDef(character.getDsDef() + characters.getFdDef() * skillLevel[3]);
                        character.setZlDef(character.getZlDef() + characters.getZlDef() * skillLevel[3]);
                    }

                }
            }
        }


        if ("不动如山1".equals(character.getPassiveIntroduceThree()) && characters.getGoIntoNum() == 1) {
            if (skillLevel[1] > 0) {
                //                453点生命上限，158点攻击，158点速度。
                if (Xtool.isNotNull(characters.getCollHp())) {
                    character.setMaxHp(character.getMaxHp() + skillLevel[1] * characters.getCollHp());
                    character.setHp(character.getHp() + skillLevel[1] * characters.getCollHp());

                }
                if (Xtool.isNotNull(characters.getCollAttack())) {
                    character.setAttack(attack.intValue() + skillLevel[1] * characters.getCollAttack());
                }
                if (Xtool.isNotNull(characters.getCollSpeed())) {
                    character.setSpeed(speed.intValue() + skillLevel[1] * characters.getCollSpeed());
                }
            }

        }

        if ("不动如山2".equals(character.getPassiveIntroduceThree()) && characters.getGoIntoNum() == 2) {
            if (skillLevel[1] > 0) {
                //                453点生命上限，158点攻击，158点速度。
                if (Xtool.isNotNull(characters.getCollHp())) {
                    character.setMaxHp(character.getMaxHp() + skillLevel[1] * characters.getCollHp());
                    character.setHp(character.getHp() + skillLevel[1] * characters.getCollHp());

                }
                if (Xtool.isNotNull(characters.getCollAttack())) {
                    character.setAttack(attack.intValue() + skillLevel[1] * characters.getCollAttack());
                }
                if (Xtool.isNotNull(characters.getCollSpeed())) {
                    character.setSpeed(speed.intValue() + skillLevel[1] * characters.getCollSpeed());
                }
                //瑶池仙女物理抗性
                if (characters.getName().equals("瑶池仙女")) {
                    character.setWlDef(character.getWlDef() + 32 * skillLevel[1]);
                }
            }
        }

        if ("不动如山3".equals(character.getPassiveIntroduceThree()) && characters.getGoIntoNum() == 3) {
            if (skillLevel[1] > 0) {
                //                453点生命上限，158点攻击，158点速度。
                if (Xtool.isNotNull(characters.getCollHp())) {
                    character.setMaxHp(character.getMaxHp() + skillLevel[1] * characters.getCollHp());
                    character.setHp(character.getHp() + skillLevel[1] * characters.getCollHp());

                }
                if (Xtool.isNotNull(characters.getCollAttack())) {
                    character.setAttack(attack.intValue() + skillLevel[1] * characters.getCollAttack());
                }
                if (Xtool.isNotNull(characters.getCollSpeed())) {
                    character.setSpeed(speed.intValue() + skillLevel[1] * characters.getCollSpeed());
                }
            }
        }

        if ("不动如山4".equals(character.getPassiveIntroduceThree()) && characters.getGoIntoNum() == 4) {
            if (skillLevel[1] > 0) {
                //                453点生命上限，158点攻击，158点速度。
                if (Xtool.isNotNull(characters.getCollHp())) {
                    character.setMaxHp(character.getMaxHp() + skillLevel[1] * characters.getCollHp());
                    character.setHp(character.getHp() + skillLevel[1] * characters.getCollHp());

                }
                if (Xtool.isNotNull(characters.getCollAttack())) {
                    character.setAttack(attack.intValue() + skillLevel[1] * characters.getCollAttack());
                }
                if (Xtool.isNotNull(characters.getCollSpeed())) {
                    character.setSpeed(speed.intValue() + skillLevel[1] * characters.getCollSpeed());
                }
            }

        }

        if ("不动如山5".equals(character.getPassiveIntroduceThree()) && characters.getGoIntoNum() == 5) {
            if (skillLevel[1] > 0) {
                //                453点生命上限，158点攻击，158点速度。
                if (Xtool.isNotNull(characters.getCollHp())) {
                    character.setMaxHp(character.getMaxHp() + skillLevel[1] * characters.getCollHp());
                    character.setHp(character.getHp() + skillLevel[1] * characters.getCollHp());

                }
                if (Xtool.isNotNull(characters.getCollAttack())) {
                    character.setAttack(attack.intValue() + skillLevel[1] * characters.getCollAttack());
                }
                if (Xtool.isNotNull(characters.getCollSpeed())) {
                    character.setSpeed(speed.intValue() + skillLevel[1] * characters.getCollSpeed());
                }
            }

        }

//        特殊
        if ("真武大帝".equals(character.getName()) && characters.getGoIntoNum() == 2) {
            if (skillLevel[1] > 0) {
                //            山Lv1在第2位时，增加自身生命上限553点；
                character.setMaxHp(character.getMaxHp() + skillLevel[1] * 553);
                character.setHp(character.getHp() + skillLevel[1] * 553);

            }
        }


        return character;
    }

    @Override
    @Transactional
    @NoRepeatSubmit(limitSeconds = 3)
    public BaseResp qiangdao(TokenDto token, HttpServletRequest request) throws Exception {
        BaseResp baseResp = new BaseResp();
        if (token == null || Xtool.isNull(token.getToken())) {
            baseResp.setSuccess(0);
            baseResp.setErrorMsg("登录过期");
            return baseResp;
        }
//        String userId = (String) redisTemplate.opsForValue().get(token.getToken());
        String userId = token.getUserId();
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
            EqCharacters characters1 = eqCharactersMapper.listById(userId, "J1010_F294");
            if (characters1 != null) {
                characters1.setStackCount(characters1.getStackCount() + 1);
                eqCharactersMapper.updateById(characters1);
            } else {
                EqCard card1 = eqCardMapper.selectByid("J1010_F294");
                if (card1 == null) {
                    baseResp.setErrorMsg("服务器异常联想管理员");
                    baseResp.setSuccess(0);
                    return baseResp;
                }
                EqCharacters characters = new EqCharacters();
                characters.setStackCount(0);
                characters.setId("J1010_F294");
                characters.setLv(1);
                characters.setUserId(Integer.parseInt(userId));
                characters.setMaxLv(CardMaxLevelUtils.getMaxLevel(card1.getName(), card1.getStar().doubleValue()));
                eqCharactersMapper.insert(characters);
            }
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
        info.setCharacterList(formateCharacter(characterList));
        List<EqCharacters> nowCharactersList = eqCharactersMapper.selectByUserId(Integer.parseInt(userId));
        info.setEqCharactersList(nowCharactersList);
        baseResp.setData(info);
        baseResp.setErrorMsg("更新成功");
        return baseResp;
    }

    @Override
    public BaseResp notice(HttpServletRequest request) throws Exception {
        BaseResp baseResp = new BaseResp();
//        Set<String> keys = redisTemplate.keys("notice_*");
//        List<String> notices = new ArrayList<>();
        List<String> notices = gameNoticeMapper.getAllNotice();
//        for (String key : keys) {
//            String value = (String) redisTemplate.opsForValue().get(key);
//            notices.add(value);
//        }
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
            campA.add(new Guardian("A" + character.getId(), character.getName(), Camp.A, character.getGoIntoNum(), Profession.fromName(characters.getProfession()),
                    Race.fromName(characters.getCamp()), character.getMaxHp(), character.getAttack(), character.getSpeed(), character.getLv(), character.getStar(),
                    character.getWlAtk(),
                    character.getHyAtk(),
                    character.getDsAtk(),
                    character.getFdAtk(),
                    character.getWlDef(),
                    character.getHyDef(),
                    character.getDsDef(),
                    character.getFdDef(),
                    character.getZlDef(),
                    Xtool.isNotNull(characters.getFlyup()) ? characters.getFlyup() : 0,characters.getSex()));
            character.setUuid("A" + character.getId());
            copyCampA.add(character);
        }
        rightCharacters.sort(Comparator.comparing(Characters::getGoIntoNum,
                Comparator.nullsFirst(Integer::compareTo)));
        for (Characters characters : rightCharacters) {
            // 设置角色
            Character character = reasonableData(characters, rightCharacters);
            campB.add(new Guardian("B" + character.getId(), character.getName(), Camp.B, character.getGoIntoNum(), Profession.fromName(characters.getProfession()),
                    Race.fromName(characters.getCamp()), character.getMaxHp(), character.getAttack(), character.getSpeed(), character.getLv(), character.getStar(),
                    character.getWlAtk(),
                    character.getHyAtk(),
                    character.getDsAtk(),
                    character.getFdAtk(),
                    character.getWlDef(),
                    character.getHyDef(),
                    character.getDsDef(),
                    character.getFdDef(),
                    character.getZlDef(),
                    Xtool.isNotNull(characters.getFlyup()) ? characters.getFlyup() : 0,characters.getSex()));
            character.setUuid("B" + character.getId());
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
        return null;
    }

    @Override
    public BaseResp isSignedUp(TokenDto token, HttpServletRequest request) throws Exception {
        BaseResp baseResp = new BaseResp();
        if (token == null || Xtool.isNull(token.getToken())) {
            baseResp.setSuccess(0);
            baseResp.setErrorMsg("登录过期");
            return baseResp;
        }
//        String userId = (String) redisTemplate.opsForValue().get(token.getToken());
        String userId = token.getUserId();
        if (Xtool.isNull(userId)) {
            baseResp.setSuccess(0);
            baseResp.setErrorMsg("登录过期");
            return baseResp;
        }
        Integer weekNum = ArenaWeekUtils.getCurrentUniqueWeekNum(new Date());
        Map map = new HashMap();
        map.put("week_num", weekNum);
        map.put("user_id", token.getUserId());
        map.put("arena_level", token.getStr());
        List<GameArenaSignup> gameArenaSignup = gameArenaSignupMapper.selectByMap(map);
        List<User> gameArenaRanks = userMapper.arenaLastRanking100(token.getStr(), weekNum - 1);
        Map map1 = new HashMap();
        map1.put("gameArenaRanks", gameArenaRanks);
        if (Xtool.isNotNull(gameArenaSignup)) {
            baseResp.setSuccess(1);
            map1.put("isSignedUp", true);
            List<GameArenaBattlecharacters> gameArenaBattlecharacters = gameArenaBattlecharactersMapper.selectByMap(map);
            map1.put("gameArenaBattlecharacters", gameArenaBattlecharacters);
            baseResp.setData(map1);
            return baseResp;
        }
        baseResp.setSuccess(1);
        map1.put("isSignedUp", false);
        baseResp.setData(map1);
        return baseResp;
    }

    @Override
    @Transactional
    @NoRepeatSubmit(limitSeconds = 5)
    public BaseResp arenaSignup(TokenDto token, HttpServletRequest request) throws Exception {
        BaseResp baseResp = new BaseResp();
        if (token == null || Xtool.isNull(token.getToken())) {
            baseResp.setSuccess(0);
            baseResp.setErrorMsg("登录过期");
            return baseResp;
        }
        String userId = token.getUserId();
//        String userId = (String) redisTemplate.opsForValue().get(token.getToken());
        if (Xtool.isNull(userId)) {
            baseResp.setSuccess(0);
            baseResp.setErrorMsg("登录过期");
            return baseResp;
        }
        User user = userMapper.selectUserByUserId(Integer.parseInt(userId));
        Map map = new HashMap();
        Integer arenaWeek = ArenaWeekUtils.getCurrentUniqueWeekNum(new Date());
        map.put("week_num", arenaWeek);
        map.put("user_id", token.getUserId());
        map.put("arena_level", token.getFinalLevel());
        List<GameArenaSignup> gameArenaSignup = gameArenaSignupMapper.selectByMap(map);
        if (Xtool.isNotNull(gameArenaSignup)) {
            baseResp.setSuccess(0);
            baseResp.setErrorMsg("请勿重复报名");
            return baseResp;
        }
        GameArenaSignup signup = new GameArenaSignup();
        signup.setUserId(user.getUserId());
        signup.setUserName(user.getNickname());
        signup.setIsSignUp(1);
        signup.setWeekNum(arenaWeek);
        signup.setSignUpTime(new Date());
        signup.setArenaLevel(token.getFinalLevel() + "");
        // 1. 获取当前日期的Calendar实例
        Calendar calendar = Calendar.getInstance();

        // 2. 获取本周开始日期（周一）
        // Calendar中，周日是1，周一是2，...，周六是7
        int currentWeekday = calendar.get(Calendar.DAY_OF_WEEK);
        // 计算需要向前偏移的天数，定位到周一
        int offsetToMonday = (currentWeekday - 2 + 7) % 7;
        calendar.add(Calendar.DAY_OF_MONTH, -offsetToMonday);
        Date weekStartDate = calendar.getTime();

        // 3. 获取本周结束日期（周日）：在周一基础上增加6天
        calendar.add(Calendar.DAY_OF_MONTH, 6);
        Date weekEndDate = calendar.getTime();
        signup.setWeekStartDate(weekStartDate);
        signup.setWeekEndDate(weekEndDate);
        gameArenaSignupMapper.insert(signup);
        List<Characters> leftCharacter = charactersMapper.goIntoListById(user.getUserId() + "");
        for (Characters characters : leftCharacter) {
            GameArenaBattlecharacters battlecharacters = new GameArenaBattlecharacters();
            BeanUtils.copyProperties(characters, battlecharacters);
            battlecharacters.setWeekNum(arenaWeek);
            battlecharacters.setArenaLevel(token.getFinalLevel() + "");
            battlecharacters.setCreateTime(new Date());
            gameArenaBattlecharactersMapper.insert(battlecharacters);
        }
        if (Xtool.isNotNull(token.getStr())) {
            Characters characters = charactersMapper.listById(userId, token.getStr());
            GameArenaBattlecharacters battlecharacters = new GameArenaBattlecharacters();
            BeanUtils.copyProperties(characters, battlecharacters);
            battlecharacters.setWeekNum(arenaWeek);
            battlecharacters.setGoIntoNum(6);
            battlecharacters.setArenaLevel(token.getFinalLevel() + "");
            battlecharacters.setCreateTime(new Date());
            gameArenaBattlecharactersMapper.insert(battlecharacters);
        }

        if (Xtool.isNotNull(token.getId())) {
            Characters characters = charactersMapper.listById(userId, token.getId());
            GameArenaBattlecharacters battlecharacters = new GameArenaBattlecharacters();
            BeanUtils.copyProperties(characters, battlecharacters);
            battlecharacters.setWeekNum(arenaWeek);
            battlecharacters.setGoIntoNum(7);
            battlecharacters.setArenaLevel(token.getFinalLevel() + "");
            battlecharacters.setCreateTime(new Date());
            gameArenaBattlecharactersMapper.insert(battlecharacters);
        }
        if (user.getArenaCount() < 0) {
            user.setArenaCount(800);
            userMapper.updateuser(user);
        }
        baseResp.setSuccess(1);
        baseResp.setErrorMsg("报名成功");
        return baseResp;
    }

    @Override
    public BaseResp arenaTem(TokenDto token, HttpServletRequest request) throws Exception {
        BaseResp baseResp = new BaseResp();
        if (token == null || Xtool.isNull(token.getToken())) {
            baseResp.setSuccess(0);
            baseResp.setErrorMsg("登录过期");
            return baseResp;
        }
//        String userId = (String) redisTemplate.opsForValue().get(token.getToken());
        String userId = token.getUserId();
        if (Xtool.isNull(userId)) {
            baseResp.setSuccess(0);
            baseResp.setErrorMsg("登录过期");
            return baseResp;
        }
        User user = userMapper.selectUserByUserId(Integer.parseInt(userId));
        UserInfo userInfo = new UserInfo();
        BeanUtils.copyProperties(user, userInfo);
        Map data = new HashMap();
        Map map = new HashMap();
        Integer arenaWeek = ArenaWeekUtils.getCurrentUniqueWeekNum(new Date());
        map.put("week_num", arenaWeek);
        map.put("user_id", token.getUserId());
        map.put("arena_level", token.getFinalLevel());
        List<GameArenaSignup> gameArenaSignups = gameArenaSignupMapper.selectByMap(map);
        data.put("gameArenaSignup", gameArenaSignups.get(0));
        List<GameArenaBattlecharacters> gameArenaBattlecharacters = gameArenaBattlecharactersMapper.selectByMap(map);
        data.put("gameArenaBattlecharacters", gameArenaBattlecharacters);
        List<GameArenaBattle> gameArenaBattle = gameArenaBattleMapper.selectList(new Wrapper<GameArenaBattle>() {
            @Override
            public String getSqlSegment() {
                return "where arena_level=" + token.getFinalLevel() + " and week_num=" + arenaWeek + " ORDER BY  createtime desc limit 1";
            }
        });
        Integer ranking = gameArenaRankMapper.getArenaRanking(userId, token.getFinalLevel(), arenaWeek);
        if (Xtool.isNotNull(gameArenaBattle)) {
            GameArenaBattle gameArenaBattle2 = gameArenaBattle.get(0);
            gameArenaBattle2.setTimeStr(this.formatTime(gameArenaBattle2.getCreatetime()));
            data.put("gameArenaBattle", gameArenaBattle2);
        }
        data.put("userInfo", userInfo);
        data.put("ranking", ranking);
        baseResp.setSuccess(1);
        baseResp.setData(data);
        return baseResp;
    }

    @Override
    public BaseResp allCardList(TokenDto token, HttpServletRequest request) throws Exception {
        BaseResp baseResp = new BaseResp();
        baseResp.setSuccess(1);
        List<Characters> alls = charactersMapper.selectAllCardList();
        List<Character> characterArrayList = new ArrayList<>();
        for (Characters characters : alls) {
            Character character = reasonableData(characters, null);
            characterArrayList.add(character);
        }
        baseResp.setData(characterArrayList);
        return baseResp;
    }

    @Override
    @Transactional
    public void sendRawrd() {
        //竞技场奖励
        //三个档次第一名
        // 2. 获取Calendar实例，并设置当前日期
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(new Date());

        // 3. 增加一个月（核心：Calendar.MONTH，加1）
        calendar.add(Calendar.MONTH, 1); // 自动处理边界日期

        // 4. 获取加1个月后的Date对象
        Date nextMonthDate = calendar.getTime();
        if (1 == 1) {
            List<User> users = userMapper.getMyRankig100();
            if (1 == 1) {
                String code = RandomCodeGenerator.generateUniqueCode();
                GameGift gameGift = new GameGift();
                gameGift.setGiftCode(code);
                gameGift.setGiftType(2);
                gameGift.setRemainingQuantity(-1);
                gameGift.setTotalQuantity(-1);
                gameGift.setIsActive(1);
                gameGift.setStartTime(new Date());
                gameGift.setUpdateTime(new Date());
                gameGift.setEndTime(nextMonthDate);
                gameGift.setGiftName("竞技场周排名奖励");
                gameGift.setDescription("恭喜少侠本周竞技场排名第一，专属排名奖励已奉上,含5000钻石+刷新符*15。");
                gameGift.setCreateTime(new Date());
                gameGiftMapper.insert(gameGift);
                GameGift gifts = gameGiftMapper.selectByGiftCode(code);
                GameGiftContent gameGiftContent = new GameGiftContent();
                gameGiftContent.setGiftId(gifts.getGiftId());
                gameGiftContent.setItemType(1);
                gameGiftContent.setItemQuantity(5000);
                gameGiftContent.setItemId(Long.parseLong(0 + ""));
                gameGiftContent.setCreateTime(new Date());
                gameGiftContentMapper.insert(gameGiftContent);
                GameGiftContent gameGiftContent2 = new GameGiftContent();
                gameGiftContent2.setGiftId(gifts.getGiftId());
                gameGiftContent2.setItemType(5);
                gameGiftContent2.setItemQuantity(15);
                gameGiftContent2.setItemId(Long.parseLong("1"));
                gameGiftContent2.setCreateTime(new Date());
                gameGiftContentMapper.insert(gameGiftContent2);
                //判断 如果是兑换礼包查询是否有兑换记录
                GameGiftExchangeCode record = new GameGiftExchangeCode();
                record.setGiftId(gifts.getGiftId());
                record.setUseUserId(Long.parseLong(users.get(0).getUserId() + ""));
                record.setExchangeCode(code);
                List<GameGiftExchangeCode> codeList = gameGiftExchangeCodeMapper.selectByUserCode2(record);
                if (Xtool.isNull(codeList)) {
                    record.setCreateTime(new Date());
                    gameGiftExchangeCodeMapper.insertSelective(record);
                }
            }
            //生成
            if (1 == 1) {
                String code = RandomCodeGenerator.generateUniqueCode();
                GameGift gameGift = new GameGift();
                gameGift.setGiftCode(code);
                gameGift.setGiftType(2);
                gameGift.setRemainingQuantity(-1);
                gameGift.setTotalQuantity(-1);
                gameGift.setIsActive(1);
                gameGift.setStartTime(new Date());
                gameGift.setUpdateTime(new Date());
                gameGift.setEndTime(nextMonthDate);
                gameGift.setGiftName("竞技场周排名奖励");
                gameGift.setDescription("恭喜少侠本周竞技场排名前 10，专属排名奖励已奉上,含2000钻石+刷新符*10。");
                gameGift.setCreateTime(new Date());
                gameGiftMapper.insert(gameGift);
                GameGift gifts = gameGiftMapper.selectByGiftCode(code);
                GameGiftContent gameGiftContent = new GameGiftContent();
                gameGiftContent.setGiftId(gifts.getGiftId());
                gameGiftContent.setItemType(1);
                gameGiftContent.setItemQuantity(2000);
                gameGiftContent.setItemId(Long.parseLong(0 + ""));
                gameGiftContent.setCreateTime(new Date());
                gameGiftContentMapper.insert(gameGiftContent);
                GameGiftContent gameGiftContent2 = new GameGiftContent();
                gameGiftContent2.setGiftId(gifts.getGiftId());
                gameGiftContent2.setItemType(5);
                gameGiftContent2.setItemQuantity(10);
                gameGiftContent2.setItemId(Long.parseLong("1"));
                gameGiftContent2.setCreateTime(new Date());
                gameGiftContentMapper.insert(gameGiftContent2);
                for (int i = 1; i < 10; i++) {
                    //判断 如果是兑换礼包查询是否有兑换记录
                    GameGiftExchangeCode record = new GameGiftExchangeCode();
                    record.setGiftId(gifts.getGiftId());
                    record.setUseUserId(Long.parseLong(users.get(i).getUserId() + ""));
                    record.setExchangeCode(code);
                    List<GameGiftExchangeCode> codeList = gameGiftExchangeCodeMapper.selectByUserCode2(record);
                    if (Xtool.isNotNull(codeList)) {
                        continue;
                    }
                    record.setCreateTime(new Date());
                    gameGiftExchangeCodeMapper.insertSelective(record);
                }
            }
            if (1 == 1) {
                String code = RandomCodeGenerator.generateUniqueCode();
                GameGift gameGift = new GameGift();
                gameGift.setGiftCode(code);
                gameGift.setGiftType(2);
                gameGift.setRemainingQuantity(-1);
                gameGift.setTotalQuantity(-1);
                gameGift.setIsActive(1);
                gameGift.setStartTime(new Date());
                gameGift.setUpdateTime(new Date());
                gameGift.setEndTime(nextMonthDate);
                gameGift.setGiftName("竞技场周排名奖励");
                gameGift.setDescription("恭喜少侠本周竞技场排名前 100，专属排名奖励已奉上,含500钻石+刷新符*5。");
                gameGift.setCreateTime(new Date());
                gameGiftMapper.insert(gameGift);
                GameGift gifts = gameGiftMapper.selectByGiftCode(code);
                GameGiftContent gameGiftContent = new GameGiftContent();
                gameGiftContent.setGiftId(gifts.getGiftId());
                gameGiftContent.setItemType(1);
                gameGiftContent.setItemQuantity(500);
                gameGiftContent.setItemId(Long.parseLong(0 + ""));
                gameGiftContent.setCreateTime(new Date());
                gameGiftContentMapper.insert(gameGiftContent);
                GameGiftContent gameGiftContent2 = new GameGiftContent();
                gameGiftContent2.setGiftId(gifts.getGiftId());
                gameGiftContent2.setItemType(5);
                gameGiftContent2.setItemQuantity(5);
                gameGiftContent2.setItemId(Long.parseLong("1"));
                gameGiftContent2.setCreateTime(new Date());
                gameGiftContentMapper.insert(gameGiftContent2);
                for (int i = 11; i < 99; i++) {
                    //判断 如果是兑换礼包查询是否有兑换记录
                    GameGiftExchangeCode record = new GameGiftExchangeCode();
                    record.setGiftId(gifts.getGiftId());
                    record.setUseUserId(Long.parseLong(users.get(i).getUserId() + ""));
                    record.setExchangeCode(code);
                    List<GameGiftExchangeCode> codeList = gameGiftExchangeCodeMapper.selectByUserCode2(record);
                    if (Xtool.isNotNull(codeList)) {
                        continue;
                    }
                    record.setCreateTime(new Date());
                    gameGiftExchangeCodeMapper.insertSelective(record);
                }
            }
        }
        Integer weekNum = ArenaWeekUtils.getCurrentUniqueWeekNum(new Date());
        userMapper.updateuserArena();
        if (1 == 1) {
            //更新初级排名
            List<User> users = userMapper.arenaRanking100(1, weekNum);
            Integer currentRank = 1;
            for (User user : users) {
                GameArenaRank gameArenaRank = new GameArenaRank();
                gameArenaRank.setUserId(user.getUserId());
                gameArenaRank.setUserName(user.getNickname());
                gameArenaRank.setArenaLevel("1");
                gameArenaRank.setWeekNum(weekNum);
                gameArenaRank.setCurrentRank(currentRank);
                gameArenaRank.setImg(user.getGameImg());
                gameArenaRank.setArenaScore(user.getArenaScore());
                currentRank++;
                gameArenaRankMapper.insert(gameArenaRank);
            }
            if (Xtool.isNotNull(users)) {
                String code = RandomCodeGenerator.generateUniqueCode();
                GameGift gameGift = new GameGift();
                gameGift.setGiftCode(code);
                gameGift.setGiftType(2);
                gameGift.setRemainingQuantity(-1);
                gameGift.setTotalQuantity(-1);
                gameGift.setIsActive(1);
                gameGift.setStartTime(new Date());
                gameGift.setUpdateTime(new Date());
                gameGift.setEndTime(nextMonthDate);
                gameGift.setGiftName("初级擂台赛排名奖励");
                gameGift.setDescription("恭喜少侠初级擂台赛排名第一，专属排名奖励已奉上,含5000钻石+魂力宝珠*5。");
                gameGift.setCreateTime(new Date());
                gameGiftMapper.insert(gameGift);
                GameGift gifts = gameGiftMapper.selectByGiftCode(code);
                GameGiftContent gameGiftContent = new GameGiftContent();
                gameGiftContent.setGiftId(gifts.getGiftId());
                gameGiftContent.setItemType(1);
                gameGiftContent.setItemQuantity(5000);
                gameGiftContent.setItemId(Long.parseLong(0 + ""));
                gameGiftContent.setCreateTime(new Date());
                gameGiftContentMapper.insert(gameGiftContent);
                GameGiftContent gameGiftContent2 = new GameGiftContent();
                gameGiftContent2.setGiftId(gifts.getGiftId());
                gameGiftContent2.setItemType(4);
                gameGiftContent2.setItemQuantity(5);
                gameGiftContent2.setItemId(Long.parseLong(105 + ""));
                gameGiftContent2.setCreateTime(new Date());
                gameGiftContentMapper.insert(gameGiftContent2);
                //判断 如果是兑换礼包查询是否有兑换记录
                GameGiftExchangeCode record = new GameGiftExchangeCode();
                record.setGiftId(gifts.getGiftId());
                record.setUseUserId(Long.parseLong(users.get(0).getUserId() + ""));
                record.setExchangeCode(code);
                List<GameGiftExchangeCode> codeList = gameGiftExchangeCodeMapper.selectByUserCode2(record);
                if (Xtool.isNull(codeList)) {
                    record.setCreateTime(new Date());
                    gameGiftExchangeCodeMapper.insertSelective(record);
                }
            }
            //生成
            if (Xtool.isNotNull(users) && users.size() > 1) {
                String code = RandomCodeGenerator.generateUniqueCode();
                GameGift gameGift = new GameGift();
                gameGift.setGiftCode(code);
                gameGift.setGiftType(2);
                gameGift.setRemainingQuantity(-1);
                gameGift.setTotalQuantity(-1);
                gameGift.setIsActive(1);
                gameGift.setStartTime(new Date());
                gameGift.setUpdateTime(new Date());
                gameGift.setEndTime(nextMonthDate);
                gameGift.setGiftName("初级擂台赛排名奖励");
                gameGift.setDescription("恭喜少侠初级擂台赛排名前 10，专属排名奖励已奉上,含1000钻石+魂力宝珠*1。");
                gameGift.setCreateTime(new Date());
                gameGiftMapper.insert(gameGift);
                GameGift gifts = gameGiftMapper.selectByGiftCode(code);
                GameGiftContent gameGiftContent = new GameGiftContent();
                gameGiftContent.setGiftId(gifts.getGiftId());
                gameGiftContent.setItemType(1);
                gameGiftContent.setItemQuantity(1000);
                gameGiftContent.setItemId(Long.parseLong(0 + ""));
                gameGiftContent.setCreateTime(new Date());
                gameGiftContentMapper.insert(gameGiftContent);
                GameGiftContent gameGiftContent2 = new GameGiftContent();
                gameGiftContent2.setGiftId(gifts.getGiftId());
                gameGiftContent2.setItemType(4);
                gameGiftContent2.setItemQuantity(1);
                gameGiftContent2.setItemId(Long.parseLong(105 + ""));
                gameGiftContent2.setCreateTime(new Date());
                gameGiftContentMapper.insert(gameGiftContent2);
                for (int i = 1; i < 10; i++) {
                    if (users.size() <= i) {
                        continue;
                    }
                    //判断 如果是兑换礼包查询是否有兑换记录
                    GameGiftExchangeCode record = new GameGiftExchangeCode();
                    record.setGiftId(gifts.getGiftId());
                    record.setUseUserId(Long.parseLong(users.get(i).getUserId() + ""));
                    record.setExchangeCode(code);
                    List<GameGiftExchangeCode> codeList = gameGiftExchangeCodeMapper.selectByUserCode2(record);
                    if (Xtool.isNotNull(codeList)) {
                        continue;
                    }
                    record.setCreateTime(new Date());
                    gameGiftExchangeCodeMapper.insertSelective(record);
                }
            }
            if (Xtool.isNotNull(users) && users.size() > 10) {
                String code = RandomCodeGenerator.generateUniqueCode();
                GameGift gameGift = new GameGift();
                gameGift.setGiftCode(code);
                gameGift.setGiftType(2);
                gameGift.setRemainingQuantity(-1);
                gameGift.setTotalQuantity(-1);
                gameGift.setIsActive(1);
                gameGift.setStartTime(new Date());
                gameGift.setUpdateTime(new Date());
                gameGift.setEndTime(nextMonthDate);
                gameGift.setGiftName("初级擂台赛排名奖励");
                gameGift.setDescription("恭喜少侠初级擂台赛排名前 100，专属排名奖励已奉上,含500钻石。");
                gameGift.setCreateTime(new Date());
                gameGiftMapper.insert(gameGift);
                GameGift gifts = gameGiftMapper.selectByGiftCode(code);
                GameGiftContent gameGiftContent = new GameGiftContent();
                gameGiftContent.setGiftId(gifts.getGiftId());
                gameGiftContent.setItemType(1);
                gameGiftContent.setItemQuantity(500);
                gameGiftContent.setItemId(Long.parseLong(0 + ""));
                gameGiftContent.setCreateTime(new Date());
                gameGiftContentMapper.insert(gameGiftContent);
                for (int i = 11; i < 99; i++) {
                    if (users.size() <= i) {
                        continue;
                    }
                    //判断 如果是兑换礼包查询是否有兑换记录
                    GameGiftExchangeCode record = new GameGiftExchangeCode();
                    record.setGiftId(gifts.getGiftId());
                    record.setUseUserId(Long.parseLong(users.get(i).getUserId() + ""));
                    record.setExchangeCode(code);
                    List<GameGiftExchangeCode> codeList = gameGiftExchangeCodeMapper.selectByUserCode2(record);
                    if (Xtool.isNotNull(codeList)) {
                        continue;
                    }
                    record.setCreateTime(new Date());
                    gameGiftExchangeCodeMapper.insertSelective(record);
                }
            }
        }
        if (1 == 1) {
            //更新初级排名
            List<User> users = userMapper.arenaRanking100(2, weekNum);
            Integer currentRank = 1;
            for (User user : users) {
                GameArenaRank gameArenaRank = new GameArenaRank();
                gameArenaRank.setUserId(user.getUserId());
                gameArenaRank.setUserName(user.getNickname());
                gameArenaRank.setArenaLevel("2");
                gameArenaRank.setWeekNum(weekNum);
                gameArenaRank.setCurrentRank(currentRank);
                gameArenaRank.setImg(user.getGameImg());
                gameArenaRank.setArenaScore(user.getArenaScore());
                currentRank++;
                gameArenaRankMapper.insert(gameArenaRank);
            }
            if (Xtool.isNotNull(users)) {
                String code = RandomCodeGenerator.generateUniqueCode();
                GameGift gameGift = new GameGift();
                gameGift.setGiftCode(code);
                gameGift.setGiftType(2);
                gameGift.setRemainingQuantity(-1);
                gameGift.setTotalQuantity(-1);
                gameGift.setIsActive(1);
                gameGift.setStartTime(new Date());
                gameGift.setUpdateTime(new Date());
                gameGift.setEndTime(nextMonthDate);
                gameGift.setGiftName("中级擂台赛排名奖励");
                gameGift.setDescription("恭喜少侠中级擂台赛排名第一，专属排名奖励已奉上,含5000钻石+魂力宝珠*5。");
                gameGift.setCreateTime(new Date());
                gameGiftMapper.insert(gameGift);
                GameGift gifts = gameGiftMapper.selectByGiftCode(code);
                GameGiftContent gameGiftContent = new GameGiftContent();
                gameGiftContent.setGiftId(gifts.getGiftId());
                gameGiftContent.setItemType(1);
                gameGiftContent.setItemQuantity(5000);
                gameGiftContent.setItemId(Long.parseLong(0 + ""));
                gameGiftContent.setCreateTime(new Date());
                gameGiftContentMapper.insert(gameGiftContent);
                GameGiftContent gameGiftContent2 = new GameGiftContent();
                gameGiftContent2.setGiftId(gifts.getGiftId());
                gameGiftContent2.setItemType(4);
                gameGiftContent2.setItemQuantity(5);
                gameGiftContent2.setItemId(Long.parseLong(105 + ""));
                gameGiftContent2.setCreateTime(new Date());
                gameGiftContentMapper.insert(gameGiftContent2);
                //判断 如果是兑换礼包查询是否有兑换记录
                GameGiftExchangeCode record = new GameGiftExchangeCode();
                record.setGiftId(gifts.getGiftId());
                record.setUseUserId(Long.parseLong(users.get(0).getUserId() + ""));
                record.setExchangeCode(code);
                List<GameGiftExchangeCode> codeList = gameGiftExchangeCodeMapper.selectByUserCode2(record);
                if (Xtool.isNull(codeList)) {
                    record.setCreateTime(new Date());
                    gameGiftExchangeCodeMapper.insertSelective(record);
                }
            }
            //生成
            if (Xtool.isNotNull(users) && users.size() > 1) {
                String code = RandomCodeGenerator.generateUniqueCode();
                GameGift gameGift = new GameGift();
                gameGift.setGiftCode(code);
                gameGift.setGiftType(2);
                gameGift.setRemainingQuantity(-1);
                gameGift.setTotalQuantity(-1);
                gameGift.setIsActive(1);
                gameGift.setStartTime(new Date());
                gameGift.setUpdateTime(new Date());
                gameGift.setEndTime(nextMonthDate);
                gameGift.setGiftName("中级擂台赛排名奖励");
                gameGift.setDescription("恭喜少侠中级擂台赛排名前 10，专属排名奖励已奉上,含1000钻石+魂力宝珠*1。");
                gameGift.setCreateTime(new Date());
                gameGiftMapper.insert(gameGift);
                GameGift gifts = gameGiftMapper.selectByGiftCode(code);
                GameGiftContent gameGiftContent = new GameGiftContent();
                gameGiftContent.setGiftId(gifts.getGiftId());
                gameGiftContent.setItemType(1);
                gameGiftContent.setItemQuantity(1000);
                gameGiftContent.setItemId(Long.parseLong(0 + ""));
                gameGiftContent.setCreateTime(new Date());
                gameGiftContentMapper.insert(gameGiftContent);
                GameGiftContent gameGiftContent2 = new GameGiftContent();
                gameGiftContent2.setGiftId(gifts.getGiftId());
                gameGiftContent2.setItemType(4);
                gameGiftContent2.setItemQuantity(1);
                gameGiftContent2.setItemId(Long.parseLong(105 + ""));
                gameGiftContent2.setCreateTime(new Date());
                gameGiftContentMapper.insert(gameGiftContent2);
                for (int i = 1; i < 10; i++) {
                    if (users.size() <= i) {
                        continue;
                    }
                    //判断 如果是兑换礼包查询是否有兑换记录
                    GameGiftExchangeCode record = new GameGiftExchangeCode();
                    record.setGiftId(gifts.getGiftId());
                    record.setUseUserId(Long.parseLong(users.get(i).getUserId() + ""));
                    record.setExchangeCode(code);
                    List<GameGiftExchangeCode> codeList = gameGiftExchangeCodeMapper.selectByUserCode2(record);
                    if (Xtool.isNotNull(codeList)) {
                        continue;
                    }
                    record.setCreateTime(new Date());
                    gameGiftExchangeCodeMapper.insertSelective(record);
                }
            }
            if (Xtool.isNotNull(users) && users.size() > 10) {
                String code = RandomCodeGenerator.generateUniqueCode();
                GameGift gameGift = new GameGift();
                gameGift.setGiftCode(code);
                gameGift.setGiftType(2);
                gameGift.setRemainingQuantity(-1);
                gameGift.setTotalQuantity(-1);
                gameGift.setIsActive(1);
                gameGift.setStartTime(new Date());
                gameGift.setUpdateTime(new Date());
                gameGift.setEndTime(nextMonthDate);
                gameGift.setGiftName("中级擂台赛排名奖励");
                gameGift.setDescription("恭喜少侠中级擂台赛排名前 100，专属排名奖励已奉上,含500钻石。");
                gameGift.setCreateTime(new Date());
                gameGiftMapper.insert(gameGift);
                GameGift gifts = gameGiftMapper.selectByGiftCode(code);
                GameGiftContent gameGiftContent = new GameGiftContent();
                gameGiftContent.setGiftId(gifts.getGiftId());
                gameGiftContent.setItemType(1);
                gameGiftContent.setItemQuantity(500);
                gameGiftContent.setItemId(Long.parseLong(0 + ""));
                gameGiftContent.setCreateTime(new Date());
                gameGiftContentMapper.insert(gameGiftContent);
                for (int i = 11; i < 99; i++) {
                    if (users.size() <= i) {
                        continue;
                    }
                    //判断 如果是兑换礼包查询是否有兑换记录
                    GameGiftExchangeCode record = new GameGiftExchangeCode();
                    record.setGiftId(gifts.getGiftId());
                    record.setUseUserId(Long.parseLong(users.get(i).getUserId() + ""));
                    record.setExchangeCode(code);
                    List<GameGiftExchangeCode> codeList = gameGiftExchangeCodeMapper.selectByUserCode2(record);
                    if (Xtool.isNotNull(codeList)) {
                        continue;
                    }
                    record.setCreateTime(new Date());
                    gameGiftExchangeCodeMapper.insertSelective(record);
                }
            }
        }
        if (1 == 1) {
            //更新初级排名
            List<User> users = userMapper.arenaRanking100(3, weekNum);
            Integer currentRank = 1;
            for (User user : users) {
                GameArenaRank gameArenaRank = new GameArenaRank();
                gameArenaRank.setUserId(user.getUserId());
                gameArenaRank.setUserName(user.getNickname());
                gameArenaRank.setArenaLevel("3");
                gameArenaRank.setWeekNum(weekNum);
                gameArenaRank.setCurrentRank(currentRank);
                gameArenaRank.setImg(user.getGameImg());
                gameArenaRank.setArenaScore(user.getArenaScore());
                currentRank++;
                gameArenaRankMapper.insert(gameArenaRank);
            }
            if (Xtool.isNotNull(users)) {
                String code = RandomCodeGenerator.generateUniqueCode();
                GameGift gameGift = new GameGift();
                gameGift.setGiftCode(code);
                gameGift.setGiftType(2);
                gameGift.setRemainingQuantity(-1);
                gameGift.setTotalQuantity(-1);
                gameGift.setIsActive(1);
                gameGift.setStartTime(new Date());
                gameGift.setUpdateTime(new Date());
                gameGift.setEndTime(nextMonthDate);
                gameGift.setGiftName("大师擂台赛排名奖励");
                gameGift.setDescription("恭喜少侠大师擂台赛排名第一，专属排名奖励已奉上,含5000钻石+魂力宝珠*5。");
                gameGift.setCreateTime(new Date());
                gameGiftMapper.insert(gameGift);
                GameGift gifts = gameGiftMapper.selectByGiftCode(code);
                GameGiftContent gameGiftContent = new GameGiftContent();
                gameGiftContent.setGiftId(gifts.getGiftId());
                gameGiftContent.setItemType(1);
                gameGiftContent.setItemQuantity(5000);
                gameGiftContent.setItemId(Long.parseLong(0 + ""));
                gameGiftContent.setCreateTime(new Date());
                gameGiftContentMapper.insert(gameGiftContent);
                GameGiftContent gameGiftContent2 = new GameGiftContent();
                gameGiftContent2.setGiftId(gifts.getGiftId());
                gameGiftContent2.setItemType(4);
                gameGiftContent2.setItemQuantity(5);
                gameGiftContent2.setItemId(Long.parseLong(105 + ""));
                gameGiftContent2.setCreateTime(new Date());
                gameGiftContentMapper.insert(gameGiftContent2);
                //判断 如果是兑换礼包查询是否有兑换记录
                GameGiftExchangeCode record = new GameGiftExchangeCode();
                record.setGiftId(gifts.getGiftId());
                record.setUseUserId(Long.parseLong(users.get(0).getUserId() + ""));
                record.setExchangeCode(code);
                List<GameGiftExchangeCode> codeList = gameGiftExchangeCodeMapper.selectByUserCode2(record);
                if (Xtool.isNull(codeList)) {
                    record.setCreateTime(new Date());
                    gameGiftExchangeCodeMapper.insertSelective(record);
                }
            }
            //生成
            if (Xtool.isNotNull(users) && users.size() > 1) {
                String code = RandomCodeGenerator.generateUniqueCode();
                GameGift gameGift = new GameGift();
                gameGift.setGiftCode(code);
                gameGift.setGiftType(2);
                gameGift.setRemainingQuantity(-1);
                gameGift.setTotalQuantity(-1);
                gameGift.setIsActive(1);
                gameGift.setStartTime(new Date());
                gameGift.setUpdateTime(new Date());
                gameGift.setEndTime(nextMonthDate);
                gameGift.setGiftName("大师擂台赛排名奖励");
                gameGift.setDescription("恭喜少侠大师擂台赛排名前 10，专属排名奖励已奉上,含1000钻石+魂力宝珠*1。");
                gameGift.setCreateTime(new Date());
                gameGiftMapper.insert(gameGift);
                GameGift gifts = gameGiftMapper.selectByGiftCode(code);
                GameGiftContent gameGiftContent = new GameGiftContent();
                gameGiftContent.setGiftId(gifts.getGiftId());
                gameGiftContent.setItemType(1);
                gameGiftContent.setItemQuantity(1000);
                gameGiftContent.setItemId(Long.parseLong(0 + ""));
                gameGiftContent.setCreateTime(new Date());
                gameGiftContentMapper.insert(gameGiftContent);
                GameGiftContent gameGiftContent2 = new GameGiftContent();
                gameGiftContent2.setGiftId(gifts.getGiftId());
                gameGiftContent2.setItemType(4);
                gameGiftContent2.setItemQuantity(1);
                gameGiftContent2.setItemId(Long.parseLong(105 + ""));
                gameGiftContent2.setCreateTime(new Date());
                gameGiftContentMapper.insert(gameGiftContent2);
                for (int i = 1; i < 10; i++) {
                    if (users.size() <= i) {
                        continue;
                    }
                    //判断 如果是兑换礼包查询是否有兑换记录
                    GameGiftExchangeCode record = new GameGiftExchangeCode();
                    record.setGiftId(gifts.getGiftId());
                    record.setUseUserId(Long.parseLong(users.get(i).getUserId() + ""));
                    record.setExchangeCode(code);
                    List<GameGiftExchangeCode> codeList = gameGiftExchangeCodeMapper.selectByUserCode2(record);
                    if (Xtool.isNotNull(codeList)) {
                        continue;
                    }
                    record.setCreateTime(new Date());
                    gameGiftExchangeCodeMapper.insertSelective(record);
                }
            }
            if (Xtool.isNotNull(users) && users.size() > 10) {
                String code = RandomCodeGenerator.generateUniqueCode();
                GameGift gameGift = new GameGift();
                gameGift.setGiftCode(code);
                gameGift.setGiftType(2);
                gameGift.setRemainingQuantity(-1);
                gameGift.setTotalQuantity(-1);
                gameGift.setIsActive(1);
                gameGift.setStartTime(new Date());
                gameGift.setUpdateTime(new Date());
                gameGift.setEndTime(nextMonthDate);
                gameGift.setGiftName("大师擂台赛排名奖励");
                gameGift.setDescription("恭喜少侠大师擂台赛排名前 100，专属排名奖励已奉上,含500钻石。");
                gameGift.setCreateTime(new Date());
                gameGiftMapper.insert(gameGift);
                GameGift gifts = gameGiftMapper.selectByGiftCode(code);
                GameGiftContent gameGiftContent = new GameGiftContent();
                gameGiftContent.setGiftId(gifts.getGiftId());
                gameGiftContent.setItemType(1);
                gameGiftContent.setItemQuantity(500);
                gameGiftContent.setItemId(Long.parseLong(0 + ""));
                gameGiftContent.setCreateTime(new Date());
                gameGiftContentMapper.insert(gameGiftContent);
                for (int i = 11; i < 99; i++) {
                    if (users.size() <= i) {
                        continue;
                    }
                    //判断 如果是兑换礼包查询是否有兑换记录
                    GameGiftExchangeCode record = new GameGiftExchangeCode();
                    record.setGiftId(gifts.getGiftId());
                    record.setUseUserId(Long.parseLong(users.get(i).getUserId() + ""));
                    record.setExchangeCode(code);
                    List<GameGiftExchangeCode> codeList = gameGiftExchangeCodeMapper.selectByUserCode2(record);
                    if (Xtool.isNotNull(codeList)) {
                        continue;
                    }
                    record.setCreateTime(new Date());
                    gameGiftExchangeCodeMapper.insertSelective(record);
                }
            }
        }
        if (1 == 1) {
            List<User> users = userMapper.getMapRanking100();
            if (1 == 1) {
                String code = RandomCodeGenerator.generateUniqueCode();
                GameGift gameGift = new GameGift();
                gameGift.setGiftCode(code);
                gameGift.setGiftType(2);
                gameGift.setRemainingQuantity(-1);
                gameGift.setTotalQuantity(-1);
                gameGift.setIsActive(1);
                gameGift.setStartTime(new Date());
                gameGift.setUpdateTime(new Date());
                gameGift.setEndTime(nextMonthDate);
                gameGift.setGiftName("探险周排名奖励");
                gameGift.setDescription("恭喜少侠本周探险排名第一，专属排名奖励已奉上,含5000钻石+刷新符*15。");
                gameGift.setCreateTime(new Date());
                gameGiftMapper.insert(gameGift);
                GameGift gifts = gameGiftMapper.selectByGiftCode(code);
                GameGiftContent gameGiftContent = new GameGiftContent();
                gameGiftContent.setGiftId(gifts.getGiftId());
                gameGiftContent.setItemType(1);
                gameGiftContent.setItemQuantity(5000);
                gameGiftContent.setItemId(Long.parseLong(0 + ""));
                gameGiftContent.setCreateTime(new Date());
                gameGiftContentMapper.insert(gameGiftContent);
                GameGiftContent gameGiftContent2 = new GameGiftContent();
                gameGiftContent2.setGiftId(gifts.getGiftId());
                gameGiftContent2.setItemType(5);
                gameGiftContent2.setItemQuantity(15);
                gameGiftContent2.setItemId(Long.parseLong("1"));
                gameGiftContent2.setCreateTime(new Date());
                gameGiftContentMapper.insert(gameGiftContent2);
                //判断 如果是兑换礼包查询是否有兑换记录
                GameGiftExchangeCode record = new GameGiftExchangeCode();
                record.setGiftId(gifts.getGiftId());
                record.setUseUserId(Long.parseLong(users.get(0).getUserId() + ""));
                record.setExchangeCode(code);
                List<GameGiftExchangeCode> codeList = gameGiftExchangeCodeMapper.selectByUserCode2(record);
                if (Xtool.isNull(codeList)) {
                    record.setCreateTime(new Date());
                    gameGiftExchangeCodeMapper.insertSelective(record);
                }
            }
            //生成
            if (1 == 1) {
                String code = RandomCodeGenerator.generateUniqueCode();
                GameGift gameGift = new GameGift();
                gameGift.setGiftCode(code);
                gameGift.setGiftType(2);
                gameGift.setRemainingQuantity(-1);
                gameGift.setTotalQuantity(-1);
                gameGift.setIsActive(1);
                gameGift.setStartTime(new Date());
                gameGift.setUpdateTime(new Date());
                gameGift.setEndTime(nextMonthDate);
                gameGift.setGiftName("探险周排名奖励");
                gameGift.setDescription("恭喜少侠本周探险排名前 10，专属排名奖励已奉上,含2000钻石+刷新符*10。");
                gameGift.setCreateTime(new Date());
                gameGiftMapper.insert(gameGift);
                GameGift gifts = gameGiftMapper.selectByGiftCode(code);
                GameGiftContent gameGiftContent = new GameGiftContent();
                gameGiftContent.setGiftId(gifts.getGiftId());
                gameGiftContent.setItemType(1);
                gameGiftContent.setItemQuantity(2000);
                gameGiftContent.setItemId(Long.parseLong(0 + ""));
                gameGiftContent.setCreateTime(new Date());
                gameGiftContentMapper.insert(gameGiftContent);
                GameGiftContent gameGiftContent2 = new GameGiftContent();
                gameGiftContent2.setGiftId(gifts.getGiftId());
                gameGiftContent2.setItemType(5);
                gameGiftContent2.setItemQuantity(10);
                gameGiftContent2.setItemId(Long.parseLong("1"));
                gameGiftContent2.setCreateTime(new Date());
                gameGiftContentMapper.insert(gameGiftContent2);
                for (int i = 1; i < 10; i++) {
                    //判断 如果是兑换礼包查询是否有兑换记录
                    GameGiftExchangeCode record = new GameGiftExchangeCode();
                    record.setGiftId(gifts.getGiftId());
                    record.setUseUserId(Long.parseLong(users.get(i).getUserId() + ""));
                    record.setExchangeCode(code);
                    List<GameGiftExchangeCode> codeList = gameGiftExchangeCodeMapper.selectByUserCode2(record);
                    if (Xtool.isNotNull(codeList)) {
                        continue;
                    }
                    record.setCreateTime(new Date());
                    gameGiftExchangeCodeMapper.insertSelective(record);
                }
            }
            if (1 == 1) {
                String code = RandomCodeGenerator.generateUniqueCode();
                GameGift gameGift = new GameGift();
                gameGift.setGiftCode(code);
                gameGift.setGiftType(2);
                gameGift.setRemainingQuantity(-1);
                gameGift.setTotalQuantity(-1);
                gameGift.setIsActive(1);
                gameGift.setStartTime(new Date());
                gameGift.setUpdateTime(new Date());
                gameGift.setEndTime(nextMonthDate);
                gameGift.setGiftName("探险周排名奖励");
                gameGift.setDescription("恭喜少侠本周探险排名前 100，专属排名奖励已奉上,含500钻石+刷新符*5。");
                gameGift.setCreateTime(new Date());
                gameGiftMapper.insert(gameGift);
                GameGift gifts = gameGiftMapper.selectByGiftCode(code);
                GameGiftContent gameGiftContent = new GameGiftContent();
                gameGiftContent.setGiftId(gifts.getGiftId());
                gameGiftContent.setItemType(1);
                gameGiftContent.setItemQuantity(500);
                gameGiftContent.setItemId(Long.parseLong(0 + ""));
                gameGiftContent.setCreateTime(new Date());
                gameGiftContentMapper.insert(gameGiftContent);
                GameGiftContent gameGiftContent2 = new GameGiftContent();
                gameGiftContent2.setGiftId(gifts.getGiftId());
                gameGiftContent2.setItemType(5);
                gameGiftContent2.setItemQuantity(5);
                gameGiftContent2.setItemId(Long.parseLong("1"));
                gameGiftContent2.setCreateTime(new Date());
                gameGiftContentMapper.insert(gameGiftContent2);
                for (int i = 11; i < 99; i++) {
                    //判断 如果是兑换礼包查询是否有兑换记录
                    GameGiftExchangeCode record = new GameGiftExchangeCode();
                    record.setGiftId(gifts.getGiftId());
                    record.setUseUserId(Long.parseLong(users.get(i).getUserId() + ""));
                    record.setExchangeCode(code);
                    List<GameGiftExchangeCode> codeList = gameGiftExchangeCodeMapper.selectByUserCode2(record);
                    if (Xtool.isNotNull(codeList)) {
                        continue;
                    }
                    record.setCreateTime(new Date());
                    gameGiftExchangeCodeMapper.insertSelective(record);
                }
            }

        }

        //青铜塔
        if (1 == 1) {
            List<User> users = userMapper.getBronzeRanking100("bronzetower");
            if (1 == 1 && Xtool.isNotNull(users)) {
                String code = RandomCodeGenerator.generateUniqueCode();
                GameGift gameGift = new GameGift();
                gameGift.setGiftCode(code);
                gameGift.setGiftType(2);
                gameGift.setRemainingQuantity(-1);
                gameGift.setTotalQuantity(-1);
                gameGift.setIsActive(1);
                gameGift.setStartTime(new Date());
                gameGift.setUpdateTime(new Date());
                gameGift.setEndTime(nextMonthDate);
                gameGift.setGiftName("青铜塔周排名奖励");
                gameGift.setDescription("恭喜少侠本周青铜塔排名第一，专属排名奖励已奉上,含2000青铜矿。");
                gameGift.setCreateTime(new Date());
                gameGiftMapper.insert(gameGift);
                GameGift gifts = gameGiftMapper.selectByGiftCode(code);
                GameGiftContent gameGiftContent = new GameGiftContent();
                gameGiftContent.setGiftId(gifts.getGiftId());
                gameGiftContent.setItemType(6);
                gameGiftContent.setItemQuantity(2000);
                gameGiftContent.setItemId(Long.parseLong(13 + ""));
                gameGiftContent.setCreateTime(new Date());
                gameGiftContentMapper.insert(gameGiftContent);
                //判断 如果是兑换礼包查询是否有兑换记录
                GameGiftExchangeCode record = new GameGiftExchangeCode();
                record.setGiftId(gifts.getGiftId());
                record.setUseUserId(Long.parseLong(users.get(0).getUserId() + ""));
                record.setExchangeCode(code);
                List<GameGiftExchangeCode> codeList = gameGiftExchangeCodeMapper.selectByUserCode2(record);
                if (Xtool.isNull(codeList)) {
                    record.setCreateTime(new Date());
                    gameGiftExchangeCodeMapper.insertSelective(record);
                }
            }
            //生成
            if (1 == 1 && Xtool.isNotNull(users)) {
                String code = RandomCodeGenerator.generateUniqueCode();
                GameGift gameGift = new GameGift();
                gameGift.setGiftCode(code);
                gameGift.setGiftType(2);
                gameGift.setRemainingQuantity(-1);
                gameGift.setTotalQuantity(-1);
                gameGift.setIsActive(1);
                gameGift.setStartTime(new Date());
                gameGift.setUpdateTime(new Date());
                gameGift.setEndTime(nextMonthDate);
                gameGift.setGiftName("青铜塔周排名奖励");
                gameGift.setDescription("恭喜少侠本周青铜塔排名前 10，专属排名奖励已奉上,含1000青铜");
                gameGift.setCreateTime(new Date());
                gameGiftMapper.insert(gameGift);
                GameGift gifts = gameGiftMapper.selectByGiftCode(code);
                GameGiftContent gameGiftContent = new GameGiftContent();
                gameGiftContent.setGiftId(gifts.getGiftId());
                gameGiftContent.setItemType(6);
                gameGiftContent.setItemQuantity(1000);
                gameGiftContent.setItemId(Long.parseLong(13 + ""));
                gameGiftContent.setCreateTime(new Date());
                gameGiftContentMapper.insert(gameGiftContent);
                for (int i = 1; i < 10; i++) {
                    if (users.size() <= i) {
                        continue;
                    }
                    //判断 如果是兑换礼包查询是否有兑换记录
                    GameGiftExchangeCode record = new GameGiftExchangeCode();
                    record.setGiftId(gifts.getGiftId());
                    record.setUseUserId(Long.parseLong(users.get(i).getUserId() + ""));
                    record.setExchangeCode(code);
                    List<GameGiftExchangeCode> codeList = gameGiftExchangeCodeMapper.selectByUserCode2(record);
                    if (Xtool.isNotNull(codeList)) {
                        continue;
                    }
                    record.setCreateTime(new Date());
                    gameGiftExchangeCodeMapper.insertSelective(record);
                }
            }
            if (1 == 1 && Xtool.isNotNull(users)) {
                String code = RandomCodeGenerator.generateUniqueCode();
                GameGift gameGift = new GameGift();
                gameGift.setGiftCode(code);
                gameGift.setGiftType(2);
                gameGift.setRemainingQuantity(-1);
                gameGift.setTotalQuantity(-1);
                gameGift.setIsActive(1);
                gameGift.setStartTime(new Date());
                gameGift.setUpdateTime(new Date());
                gameGift.setEndTime(nextMonthDate);
                gameGift.setGiftName("青铜塔周排名奖励");
                gameGift.setDescription("恭喜少侠本周青铜塔排名前 100，专属排名奖励已奉上,含500青铜矿。");
                gameGift.setCreateTime(new Date());
                gameGiftMapper.insert(gameGift);
                GameGift gifts = gameGiftMapper.selectByGiftCode(code);
                GameGiftContent gameGiftContent = new GameGiftContent();
                gameGiftContent.setGiftId(gifts.getGiftId());
                gameGiftContent.setItemType(6);
                gameGiftContent.setItemQuantity(500);
                gameGiftContent.setItemId(Long.parseLong(13 + ""));
                gameGiftContent.setCreateTime(new Date());
                gameGiftContentMapper.insert(gameGiftContent);
                for (int i = 11; i < 99; i++) {
                    if (users.size() <= i) {
                        continue;
                    }
                    //判断 如果是兑换礼包查询是否有兑换记录
                    GameGiftExchangeCode record = new GameGiftExchangeCode();
                    record.setGiftId(gifts.getGiftId());
                    record.setUseUserId(Long.parseLong(users.get(i).getUserId() + ""));
                    record.setExchangeCode(code);
                    List<GameGiftExchangeCode> codeList = gameGiftExchangeCodeMapper.selectByUserCode2(record);
                    if (Xtool.isNotNull(codeList)) {
                        continue;
                    }
                    record.setCreateTime(new Date());
                    gameGiftExchangeCodeMapper.insertSelective(record);
                }
            }

        }
        //白银塔
        if (1 == 1) {
            List<User> users = userMapper.getBronzeRanking100("silvertower");
            if (1 == 1 && Xtool.isNotNull(users)) {
                String code = RandomCodeGenerator.generateUniqueCode();
                GameGift gameGift = new GameGift();
                gameGift.setGiftCode(code);
                gameGift.setGiftType(2);
                gameGift.setRemainingQuantity(-1);
                gameGift.setTotalQuantity(-1);
                gameGift.setIsActive(1);
                gameGift.setStartTime(new Date());
                gameGift.setUpdateTime(new Date());
                gameGift.setEndTime(nextMonthDate);
                gameGift.setGiftName("白银塔周排名奖励");
                gameGift.setDescription("恭喜少侠本周白银塔排名第一，专属排名奖励已奉上,含2000玄铁矿。");
                gameGift.setCreateTime(new Date());
                gameGiftMapper.insert(gameGift);
                GameGift gifts = gameGiftMapper.selectByGiftCode(code);
                GameGiftContent gameGiftContent = new GameGiftContent();
                gameGiftContent.setGiftId(gifts.getGiftId());
                gameGiftContent.setItemType(6);
                gameGiftContent.setItemQuantity(2000);
                gameGiftContent.setItemId(Long.parseLong(14 + ""));
                gameGiftContent.setCreateTime(new Date());
                gameGiftContentMapper.insert(gameGiftContent);
                //判断 如果是兑换礼包查询是否有兑换记录
                GameGiftExchangeCode record = new GameGiftExchangeCode();
                record.setGiftId(gifts.getGiftId());
                record.setUseUserId(Long.parseLong(users.get(0).getUserId() + ""));
                record.setExchangeCode(code);
                List<GameGiftExchangeCode> codeList = gameGiftExchangeCodeMapper.selectByUserCode2(record);
                if (Xtool.isNull(codeList)) {
                    record.setCreateTime(new Date());
                    gameGiftExchangeCodeMapper.insertSelective(record);
                }
            }
            //生成
            if (1 == 1 && Xtool.isNotNull(users)) {
                String code = RandomCodeGenerator.generateUniqueCode();
                GameGift gameGift = new GameGift();
                gameGift.setGiftCode(code);
                gameGift.setGiftType(2);
                gameGift.setRemainingQuantity(-1);
                gameGift.setTotalQuantity(-1);
                gameGift.setIsActive(1);
                gameGift.setStartTime(new Date());
                gameGift.setUpdateTime(new Date());
                gameGift.setEndTime(nextMonthDate);
                gameGift.setGiftName("白银塔周排名奖励");
                gameGift.setDescription("恭喜少侠本周白银塔排名前 10，专属排名奖励已奉上,含1000玄铁矿");
                gameGift.setCreateTime(new Date());
                gameGiftMapper.insert(gameGift);
                GameGift gifts = gameGiftMapper.selectByGiftCode(code);
                GameGiftContent gameGiftContent = new GameGiftContent();
                gameGiftContent.setGiftId(gifts.getGiftId());
                gameGiftContent.setItemType(6);
                gameGiftContent.setItemQuantity(1000);
                gameGiftContent.setItemId(Long.parseLong(14 + ""));
                gameGiftContent.setCreateTime(new Date());
                gameGiftContentMapper.insert(gameGiftContent);
                for (int i = 1; i < 10; i++) {
                    if (users.size() <= i) {
                        continue;
                    }
                    //判断 如果是兑换礼包查询是否有兑换记录
                    GameGiftExchangeCode record = new GameGiftExchangeCode();
                    record.setGiftId(gifts.getGiftId());
                    record.setUseUserId(Long.parseLong(users.get(i).getUserId() + ""));
                    record.setExchangeCode(code);
                    List<GameGiftExchangeCode> codeList = gameGiftExchangeCodeMapper.selectByUserCode2(record);
                    if (Xtool.isNotNull(codeList)) {
                        continue;
                    }
                    record.setCreateTime(new Date());
                    gameGiftExchangeCodeMapper.insertSelective(record);
                }
            }
            if (1 == 1 && Xtool.isNotNull(users)) {
                String code = RandomCodeGenerator.generateUniqueCode();
                GameGift gameGift = new GameGift();
                gameGift.setGiftCode(code);
                gameGift.setGiftType(2);
                gameGift.setRemainingQuantity(-1);
                gameGift.setTotalQuantity(-1);
                gameGift.setIsActive(1);
                gameGift.setStartTime(new Date());
                gameGift.setUpdateTime(new Date());
                gameGift.setEndTime(nextMonthDate);
                gameGift.setGiftName("白银塔周排名奖励");
                gameGift.setDescription("恭喜少侠本周白银塔排名前 100，专属排名奖励已奉上,含500玄铁矿。");
                gameGift.setCreateTime(new Date());
                gameGiftMapper.insert(gameGift);
                GameGift gifts = gameGiftMapper.selectByGiftCode(code);
                GameGiftContent gameGiftContent = new GameGiftContent();
                gameGiftContent.setGiftId(gifts.getGiftId());
                gameGiftContent.setItemType(6);
                gameGiftContent.setItemQuantity(500);
                gameGiftContent.setItemId(Long.parseLong(14 + ""));
                gameGiftContent.setCreateTime(new Date());
                gameGiftContentMapper.insert(gameGiftContent);
                for (int i = 11; i < 99; i++) {
                    if (users.size() <= i) {
                        continue;
                    }
                    //判断 如果是兑换礼包查询是否有兑换记录
                    GameGiftExchangeCode record = new GameGiftExchangeCode();
                    record.setGiftId(gifts.getGiftId());
                    record.setUseUserId(Long.parseLong(users.get(i).getUserId() + ""));
                    record.setExchangeCode(code);
                    List<GameGiftExchangeCode> codeList = gameGiftExchangeCodeMapper.selectByUserCode2(record);
                    if (Xtool.isNotNull(codeList)) {
                        continue;
                    }
                    record.setCreateTime(new Date());
                    gameGiftExchangeCodeMapper.insertSelective(record);
                }
            }

        }
        //黄金塔
        if (1 == 1) {
            List<User> users = userMapper.getBronzeRanking100("goldentower");
            if (1 == 1 && Xtool.isNotNull(users)) {
                String code = RandomCodeGenerator.generateUniqueCode();
                GameGift gameGift = new GameGift();
                gameGift.setGiftCode(code);
                gameGift.setGiftType(2);
                gameGift.setRemainingQuantity(-1);
                gameGift.setTotalQuantity(-1);
                gameGift.setIsActive(1);
                gameGift.setStartTime(new Date());
                gameGift.setUpdateTime(new Date());
                gameGift.setEndTime(nextMonthDate);
                gameGift.setGiftName("黄金塔周排名奖励");
                gameGift.setDescription("恭喜少侠本周黄金塔排名第一，专属排名奖励已奉上,含2000紫金矿。");
                gameGift.setCreateTime(new Date());
                gameGiftMapper.insert(gameGift);
                GameGift gifts = gameGiftMapper.selectByGiftCode(code);
                GameGiftContent gameGiftContent = new GameGiftContent();
                gameGiftContent.setGiftId(gifts.getGiftId());
                gameGiftContent.setItemType(6);
                gameGiftContent.setItemQuantity(2000);
                gameGiftContent.setItemId(Long.parseLong(15 + ""));
                gameGiftContent.setCreateTime(new Date());
                gameGiftContentMapper.insert(gameGiftContent);
                //判断 如果是兑换礼包查询是否有兑换记录
                GameGiftExchangeCode record = new GameGiftExchangeCode();
                record.setGiftId(gifts.getGiftId());
                record.setUseUserId(Long.parseLong(users.get(0).getUserId() + ""));
                record.setExchangeCode(code);
                List<GameGiftExchangeCode> codeList = gameGiftExchangeCodeMapper.selectByUserCode2(record);
                if (Xtool.isNull(codeList)) {
                    record.setCreateTime(new Date());
                    gameGiftExchangeCodeMapper.insertSelective(record);
                }
            }
            //生成
            if (1 == 1 && Xtool.isNotNull(users)) {
                String code = RandomCodeGenerator.generateUniqueCode();
                GameGift gameGift = new GameGift();
                gameGift.setGiftCode(code);
                gameGift.setGiftType(2);
                gameGift.setRemainingQuantity(-1);
                gameGift.setTotalQuantity(-1);
                gameGift.setIsActive(1);
                gameGift.setStartTime(new Date());
                gameGift.setUpdateTime(new Date());
                gameGift.setEndTime(nextMonthDate);
                gameGift.setGiftName("白银塔周排名奖励");
                gameGift.setDescription("恭喜少侠本周白银塔排名前 10，专属排名奖励已奉上,含1000紫金矿");
                gameGift.setCreateTime(new Date());
                gameGiftMapper.insert(gameGift);
                GameGift gifts = gameGiftMapper.selectByGiftCode(code);
                GameGiftContent gameGiftContent = new GameGiftContent();
                gameGiftContent.setGiftId(gifts.getGiftId());
                gameGiftContent.setItemType(6);
                gameGiftContent.setItemQuantity(1000);
                gameGiftContent.setItemId(Long.parseLong(15 + ""));
                gameGiftContent.setCreateTime(new Date());
                gameGiftContentMapper.insert(gameGiftContent);
                for (int i = 1; i < 10; i++) {
                    if (users.size() <= i) {
                        continue;
                    }
                    //判断 如果是兑换礼包查询是否有兑换记录
                    GameGiftExchangeCode record = new GameGiftExchangeCode();
                    record.setGiftId(gifts.getGiftId());
                    record.setUseUserId(Long.parseLong(users.get(i).getUserId() + ""));
                    record.setExchangeCode(code);
                    List<GameGiftExchangeCode> codeList = gameGiftExchangeCodeMapper.selectByUserCode2(record);
                    if (Xtool.isNotNull(codeList)) {
                        continue;
                    }
                    record.setCreateTime(new Date());
                    gameGiftExchangeCodeMapper.insertSelective(record);
                }
            }
            if (1 == 1 && Xtool.isNotNull(users)) {
                String code = RandomCodeGenerator.generateUniqueCode();
                GameGift gameGift = new GameGift();
                gameGift.setGiftCode(code);
                gameGift.setGiftType(2);
                gameGift.setRemainingQuantity(-1);
                gameGift.setTotalQuantity(-1);
                gameGift.setIsActive(1);
                gameGift.setStartTime(new Date());
                gameGift.setUpdateTime(new Date());
                gameGift.setEndTime(nextMonthDate);
                gameGift.setGiftName("白银塔周排名奖励");
                gameGift.setDescription("恭喜少侠本周白银塔排名前 100，专属排名奖励已奉上,含500紫金矿。");
                gameGift.setCreateTime(new Date());
                gameGiftMapper.insert(gameGift);
                GameGift gifts = gameGiftMapper.selectByGiftCode(code);
                GameGiftContent gameGiftContent = new GameGiftContent();
                gameGiftContent.setGiftId(gifts.getGiftId());
                gameGiftContent.setItemType(6);
                gameGiftContent.setItemQuantity(500);
                gameGiftContent.setItemId(Long.parseLong(15 + ""));
                gameGiftContent.setCreateTime(new Date());
                gameGiftContentMapper.insert(gameGiftContent);
                for (int i = 11; i < 99; i++) {
                    if (users.size() <= i) {
                        continue;
                    }
                    //判断 如果是兑换礼包查询是否有兑换记录
                    GameGiftExchangeCode record = new GameGiftExchangeCode();
                    record.setGiftId(gifts.getGiftId());
                    record.setUseUserId(Long.parseLong(users.get(i).getUserId() + ""));
                    record.setExchangeCode(code);
                    List<GameGiftExchangeCode> codeList = gameGiftExchangeCodeMapper.selectByUserCode2(record);
                    if (Xtool.isNotNull(codeList)) {
                        continue;
                    }
                    record.setCreateTime(new Date());
                    gameGiftExchangeCodeMapper.insertSelective(record);
                }
            }

        }
        //重置排名
        playerBronzeTowerMapper.deleteByMap(new HashMap<>());
    }

    @Override
    public void deleteAll() {
        gameEqRecordMapper.deleteAll();
    }

    @Override
    public void sendTuoRawrd() {
        User user2 = userMapper.selectUserByUserId(1728);
        user2.setDiamond(new BigDecimal(1000000));
        userMapper.updateuser(user2);
        User user3 = userMapper.selectUserByUserId(2735);
        user3.setDiamond(new BigDecimal(1000000));
        userMapper.updateuser(user3);
    }

    @Override
    public void addActCode() {
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(new Date());

        // 3. 增加一个月（核心：Calendar.MONTH，加1）
        calendar.add(Calendar.MONTH, 1); // 自动处理边界日期

        // 4. 获取加1个月后的Date对象
        Date nextMonthDate = calendar.getTime();
        for (int i = 0; i < 10000; i++) {
            String code = RandomCodeGenerator.generateUniqueCode();
            GameGift gameGift = new GameGift();
            gameGift.setGiftCode(code);
            gameGift.setGiftType(4);
            gameGift.setRemainingQuantity(1);
            gameGift.setTotalQuantity(1);
            gameGift.setIsActive(1);
            gameGift.setStartTime(new Date());
            gameGift.setUpdateTime(new Date());
            gameGift.setEndTime(nextMonthDate);
            gameGift.setGiftName("公益捐赠专属礼包");
            gameGift.setDescription("内含：钻石120000 + 金币1200000\n" +
                    "助力仙途，善意永存！");
            gameGift.setCreateTime(new Date());
            gameGiftMapper.insert(gameGift);
            GameGift gifts = gameGiftMapper.selectByGiftCode(code);
            GameGiftContent gameGiftContent = new GameGiftContent();
            gameGiftContent.setGiftId(gifts.getGiftId());
            gameGiftContent.setItemType(1);
            gameGiftContent.setItemQuantity(120000);
            gameGiftContent.setItemId(Long.parseLong(0 + ""));
            gameGiftContent.setCreateTime(new Date());
            gameGiftContentMapper.insert(gameGiftContent);
            GameGiftContent gameGiftContent2 = new GameGiftContent();
            gameGiftContent2.setGiftId(gifts.getGiftId());
            gameGiftContent2.setItemType(2);
            gameGiftContent2.setItemQuantity(1200000);
            gameGiftContent2.setItemId(Long.parseLong(0 + ""));
            gameGiftContent2.setCreateTime(new Date());
            gameGiftContentMapper.insert(gameGiftContent2);
        }
    }

    @Override
    public void syncLastWeekRank() {

    }

}
