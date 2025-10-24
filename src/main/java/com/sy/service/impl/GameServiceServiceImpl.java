package com.sy.service.impl;

import cn.hutool.core.util.IdUtil;
import com.sy.mapper.game.CardMapper;
import com.sy.mapper.game.CharactersMapper;
import com.sy.mapper.UserMapper;
import com.sy.mapper.game.GameFightMapper;
import com.sy.model.User;
import com.sy.model.game.*;
import com.sy.model.game.Character;
import com.sy.model.resp.BaseResp;
import com.sy.service.GameServiceService;
import com.sy.service.UserServic;
import com.sy.tool.Xtool;
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
    UserServic servic;

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
        info.setUseCardCount(characterList.size() + "/" + cardList.size());
        info.setCharacterList(characterList);
        String token = IdUtil.simpleUUID();
        info.setToken(token);
        ValueOperations opsForValue = redisTemplate.opsForValue();
        opsForValue.set(token, emp.getUserId() + "", 2592000, TimeUnit.SECONDS);
        baseResp.setData(info);
        baseResp.setErrorMsg("登录成功");
        return baseResp;
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
        info.setUseCardCount(characterList.size() + "/" + cardList.size());
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
        List<Characters> charactersList = charactersMapper.listById(userId, token.getId());
        if (Xtool.isNull(charactersList)) {
            baseResp.setSuccess(0);
            baseResp.setErrorMsg("卡牌不存在");
            return baseResp;
        }
        Characters characters = charactersList.get(0);
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
        CardPool pool = new CardPool();
        for (Card card : cardList) {
            pool.addCard(card);
        }
        Card drawnCard = pool.draw();
        List<Characters> charactersList = charactersMapper.listById(userId, drawnCard.getId());
        if (Xtool.isNotNull(charactersList)) {
            Characters characters1 = charactersList.get(0);
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
        info.setUseCardCount(nowCharactersList.size() + "/" + cardList.size());
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
            List<Characters> charactersList = charactersMapper.listById(userId, drawnCard.getId());
            if (Xtool.isNotNull(charactersList)) {
                Characters characters1 = charactersList.get(0);
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
        info.setUseCardCount(nowCharactersList.size() + "/" + cardList.size());
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
        List<Characters> leftCharacter = charactersMapper.selectByUserId(user.getUserId());
        if (Xtool.isNull(leftCharacter)) {
            baseResp.setSuccess(0);
            baseResp.setErrorMsg("你没有配置战队无法战斗");
            return baseResp;
        }
        Collections.sort(leftCharacter, Comparator.comparing(Characters::getGoIntoNum));
        //对手战队
        List<Characters> rightCharacter = charactersMapper.selectByUserId(user.getUserId());
        if (Xtool.isNull(rightCharacter)) {
            baseResp.setSuccess(0);
            baseResp.setErrorMsg("对方没有配置战队无法战斗");
            return baseResp;
        }
        GameFight fight = new GameFight();
        String simpleUUID = IdUtil.simpleUUID();
        fight.setId(simpleUUID);
        fight.setToUserId(Integer.parseInt(token.getUserId()));
        fight.setUserId(Integer.parseInt(userId));
        gameFightMapper.insert(fight);
        baseResp.setSuccess(1);
        baseResp.setData(fight);
        return baseResp;
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
        Map map = new HashMap();
        //战斗过程
        List<Map> list = new ArrayList<>();
        // 所有存活的角色
        List<Character> allLiveCharacter = new ArrayList();
        List<Character> leftCharacter = new ArrayList<>();
        List<Character> rightCharacter = new ArrayList<>();
        // 当前回合数
        Integer currentRound = 1;

        GameFight gameFight = gameFightMapper.selectByPrimaryKey(token.getId());
//        gameFight.setToUserId(593);
        //自己的战队
        User user = userMapper.selectUserByUserId(gameFight.getUserId());
        map.put("name0", user.getNickname());
        User user1 = userMapper.selectUserByUserId(gameFight.getToUserId());
        map.put("name1", user1.getNickname());
        List<Characters> leftCharacters = charactersMapper.goIntoListById(gameFight.getUserId() + "");
        for (Characters characters : leftCharacters) {
            // 设置角色
            Character character = reasonableData(characters, leftCharacters);
            character.setDirection("0");
            Character character333 = new Character();
            BeanUtils.copyProperties(character, character333);
            leftCharacter.add(character333);
            allLiveCharacter.add(character);
        }
        //对手战队
        List<Characters> rightCharacters = charactersMapper.goIntoListById(gameFight.getToUserId() + "");
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
            for (Character character : allLiveCharacterNew) {
                if (!allLiveCharacter.contains(character)) break;
                if (Xtool.isNotNull(character.getBuff())) {
                    for (int i = character.getBuff().size() - 1; i >= 0; i--) {
                        Buff buff = character.getBuff().get(i);
                        if ("中毒".equals(buff.getName())) {
                            Fightter fightter = new Fightter();
                            Integer hurt = buff.getRoundReduceBleed();
                            fightter.setStr("中毒" + "-" + hurt);
                            fightter.setDirection(character.getDirection());
                            fightter.setGoON(character.getGoON());
                            fightter.setAttack(character.getAttack());
                            if (character.getHp() - hurt <= 0) {
                                character.setHp(0);
                                dead(character, allLiveCharacter);
                            } else {
                                character.setHp(character.getHp() - hurt);
                            }
                            if (buff.getRoundNum() - 1 <= 0) {
                                //buff失效则移除
                                character.getBuff().remove(i);
                                fightter.setIsbuff(0);
                            } else {
                                buff.setRoundNum(buff.getRoundNum() - 1);
                                fightter.setIsbuff(1);
                            }
                            fightter.setHp(character.getHp());
                            fightter.setSpeed(character.getSpeed());
                            fightter.setIsAction(0);
                            fightter.setIsDead("1");
                            fightterList.add(fightter);
                        } else if ("眩晕".equals(buff.getName())) {
                            Fightter fightter = new Fightter();
                            fightter.setDirection(character.getDirection());
                            fightter.setGoON(character.getGoON());
                            fightter.setAttack(character.getAttack());
                            if (buff.getRoundNum() - 1 <= 0) {
                                //buff失效则移除
                                character.getBuff().remove(i);
                                fightter.setIsbuff(0);
                                character.setIsAction("1");
                            } else {
                                buff.setRoundNum(buff.getRoundNum() - 1);
                                fightter.setIsbuff(1);
                                character.setIsAction("0");
                            }
                            fightter.setHp(character.getHp());
                            fightter.setSpeed(character.getSpeed());
                            fightter.setIsAction(0);
                            fightterList.add(fightter);
                        } else {


                        }
                    }
                }

            }


//            苦痛箭 Lv1
//            场下每回合对同位置敌人造成35点真实伤害

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
                        if ("仙塔庇护".equals(character.getPassiveIntroduceOne()) || "仙塔庇护".equals(character.getPassiveIntroduceOne())) {
                            if ("0".equals(character.getDirection())) {
                                //大于0时才能治疗
                                if (leftCharters1.getHp() > 0 && "sacred".equals(leftCharters1.getCamp())) {
                                    Fightter fightter = new Fightter();
                                    fightter.setDirection(leftCharters1.getDirection());
                                    character.setHp(leftCharters1.getHp() + 25);
                                    fightter.setGoON(leftCharters1.getGoON());
                                    fightter.setAttack(leftCharters1.getAttack());
                                    fightter.setHp(leftCharters1.getHp());
                                    fightter.setMaxHp(leftCharters1.getMaxHp());
                                    fightter.setSpeed(leftCharters1.getSpeed());
                                    fightter.setIsAction(1);
                                    fightter.setGoIntoNum(leftCharters1.getGoIntoNum());
                                    fightter.setStr("+25");
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
                                    character.setHp(rightCharters1.getHp() + 25);
                                    fightter.setGoON(rightCharters1.getGoON());
                                    fightter.setAttack(rightCharters1.getAttack());
                                    fightter.setHp(rightCharters1.getHp());
                                    fightter.setMaxHp(rightCharters1.getMaxHp());
                                    fightter.setSpeed(rightCharters1.getSpeed());
                                    fightter.setIsAction(1);
                                    fightter.setGoIntoNum(rightCharters1.getGoIntoNum());
                                    fightter.setStr("+25");
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
                        break outerLoop; // 直接跳出外层循环
                    } else if (allLiveCharacter.stream().filter(x -> "1".equals(x.getDirection())).count() <= 0) {
                        mapProsse.put("fightterList", fightterList);
                        list.add(mapProsse);
                        map.put("result", true);
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
            for (Character character : allLiveCharacterNew) {
                if (!allLiveCharacter.contains(character)) continue;
                if ("0".equals(character.getGoON())) {
                    if ("续命".equals(character.getPassiveIntroduceOne()) || "续命".equals(character.getPassiveIntroduceOne())) {
                        if ("0".equals(character.getDirection())) {
                            //大于0时才能治疗
                            if (leftCharters1.getHp() > 0 && "sacred".equals(leftCharters1.getCamp())) {
                                Fightter fightter = new Fightter();
                                if (character.getHp() - 40 <= 0) {
                                    character.setHp(0);
                                    //死亡
                                    dead(character, allLiveCharacter);
                                    fightter.setIsDead("1");
                                } else {
                                    character.setHp(character.getHp() - 40);
                                }
                                if (leftCharters1.getHp() + 40 > leftCharters1.getMaxHp()) {
                                    leftCharters1.setHp(leftCharters1.getMaxHp());
                                } else {
                                    leftCharters1.setHp(leftCharters1.getHp() + 40);
                                }
                                fightter.setDirection(character.getDirection());
                                fightter.setGoON(character.getGoON());
                                fightter.setAttack(character.getAttack());
                                fightter.setHp(character.getHp());
                                fightter.setMaxHp(character.getMaxHp());
                                fightter.setSpeed(character.getSpeed());
                                fightter.setIsAction(1);
                                fightter.setGoIntoNum(character.getGoIntoNum());
                                fightter.setStr("-40");
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
                                fightter.setStr("40");
                                fightter.setBuff("续命");
                                fightterList.add(fightter);
                            }
                        } else {
                            //大于0时才能治疗
                            if (rightCharters1.getHp() > 0 && "sacred".equals(rightCharters1.getCamp())) {
                                Fightter fightter = new Fightter();
                                if (character.getHp() - 40 <= 0) {
                                    character.setHp(0);
                                    //死亡
                                    dead(character, allLiveCharacter);
                                    fightter.setIsDead("1");
                                } else {
                                    character.setHp(character.getHp() - 40);
                                }
                                if (rightCharters1.getHp() + 40 > rightCharters1.getMaxHp()) {
                                    rightCharters1.setHp(rightCharters1.getMaxHp());
                                } else {
                                    rightCharters1.setHp(rightCharters1.getHp() + 40);
                                }
                                fightter.setDirection(character.getDirection());
                                fightter.setGoON(character.getGoON());
                                fightter.setAttack(character.getAttack());
                                fightter.setHp(character.getHp());
                                fightter.setMaxHp(character.getMaxHp());
                                fightter.setSpeed(character.getSpeed());
                                fightter.setGoIntoNum(character.getGoIntoNum());
                                fightter.setIsAction(1);
                                fightter.setStr("-40");
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
                                fightter.setStr("40");
                                fightter.setBuff("续命");
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
                    break outerLoop; // 直接跳出外层循环
                } else if (allLiveCharacter.stream().filter(x -> "1".equals(x.getDirection())).count() <= 0) {
                    mapProsse.put("fightterList", fightterList);
                    list.add(mapProsse);
                    map.put("result", true);
                    break outerLoop; // 直接跳出外层循环
                }
            }

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
                break outerLoop; // 直接跳出外层循环
            } else if (allLiveCharacter.stream().filter(x -> "1".equals(x.getDirection())).count() <= 0) {
                map.put("result", true);
                break outerLoop; // 直接跳出外层循环
            }

        }

        map.put("fightProcess", list);
        map.put("leftCharacter", leftCharacter);
        map.put("rightCharacter", rightCharacter);
        BaseResp baseResp = new BaseResp();
        baseResp.setData(map);
        baseResp.setSuccess(1);
        return baseResp;
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
        allLiveCharacter.removeIf(x -> x.getDirection().equals(character.getDirection()) && x.getId().equals(character.getId()));
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
            List<Characters> charactersList = charactersMapper.listById(userId, "1030");
            if (Xtool.isNotNull(charactersList)) {
                Characters characters = charactersList.get(0);
                characters.setStackCount(characters.getStackCount() + 1);
                charactersMapper.updateByPrimaryKey(characters);
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
            List<Characters> charactersList = charactersMapper.listById(userId, "1040");
            if (Xtool.isNotNull(charactersList)) {
                Characters characters = charactersList.get(0);
                characters.setStackCount(characters.getStackCount() + 1);
                charactersMapper.updateByPrimaryKey(characters);
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
        //卡池数量
        List<Card> cardList = cardMapper.selectAll();
        info.setUseCardCount(characterList.size() + "/" + cardList.size());
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
