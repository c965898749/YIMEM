package com.sy.service.impl;

import cn.hutool.core.util.IdUtil;
import com.sy.mapper.game.CardMapper;
import com.sy.mapper.game.CharactersMapper;
import com.sy.mapper.UserMapper;
import com.sy.mapper.game.GameFightMapper;
import com.sy.model.User;
import com.sy.model.game.*;
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
        baseResp.setSuccess(1);
        UserInfo info = new UserInfo();
        BeanUtils.copyProperties(emp, info);
        //获取卡牌数据
        List<Characters> characterList = charactersMapper.selectByUserId(emp.getUserId());
        //卡池数量
        List<Card> cardList=cardMapper.selectAll();
        info.setUseCardCount(characterList.size()+"/"+cardList.size());
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
        baseResp.setSuccess(1);
        UserInfo info = new UserInfo();
        BeanUtils.copyProperties(user, info);
        //获取卡牌数据
        List<Characters> characterList = charactersMapper.selectByUserId(user.getUserId());
        info.setCharacterList(characterList);
        //卡池数量
        List<Card> cardList=cardMapper.selectAll();
        info.setUseCardCount(characterList.size()+"/"+cardList.size());
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
    @Transactional
    public BaseResp danChou(TokenDto token, HttpServletRequest request) throws Exception {
        BaseResp baseResp = new BaseResp();
        String userId=token.getUserId();
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
        if (drawnCard.getStar().compareTo(new BigDecimal(3))>0){
            Date date=new Date();
            opsForValue.set("notice_"+date.getTime()+"", "恭喜 "+user.getNickname()+" 高级召唤获得"+drawnCard.getStar().stripTrailingZeros()+"星"+drawnCard.getName(), 120, TimeUnit.SECONDS);
        }
        List<Characters> nowCharactersList = charactersMapper.selectByUserId(Integer.parseInt(userId));
        dto.setCharacters(nowCharactersList);
        //卡池数量
        UserInfo info = new UserInfo();
        BeanUtils.copyProperties(user, info);
        info.setUseCardCount(nowCharactersList.size()+"/"+cardList.size());
        baseResp.setSuccess(1);
        Map map=new HashMap();
        map.put("user",info);
        map.put("dto",dto);
        baseResp.setData(map);
        baseResp.setErrorMsg("单抽成功");
        return baseResp;
    }

    @Override
    @Transactional
    public BaseResp shiChou(TokenDto token, HttpServletRequest request) throws Exception {
        BaseResp baseResp = new BaseResp();
        String userId=token.getUserId();
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
            if (drawnCard.getStar().compareTo(new BigDecimal(3))>0){
                Date date=new Date();
                opsForValue.set("notice_"+date.getTime(), "恭喜 "+user.getNickname()+" 高级召唤获得"+drawnCard.getStar().stripTrailingZeros()+"星"+drawnCard.getName(), 120, TimeUnit.SECONDS);
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
        Map map=new HashMap();
        //卡池数量
        UserInfo info = new UserInfo();
        BeanUtils.copyProperties(user, info);
        info.setUseCardCount(nowCharactersList.size()+"/"+cardList.size());
        map.put("user",info);
        map.put("dto",dto);
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
//        Collections.sort(rightCharacter, Comparator.comparing(Characters::getGoIntoNum));
//        List<CharacterState> leftCharacterState=new ArrayList<>();
//        List<CharacterState> rightCharacterState=new ArrayList<>();
//        // 设置角色
//        for (Characters characters : leftCharacter) {
//            CharacterState characterState=new CharacterState(characters);
//            leftCharacterState.add(characterState);
//        }
//        for (Characters characters : rightCharacter) {
//            CharacterState characterState=new CharacterState(characters);
//            rightCharacterState.add(characterState);
//        }
//        // 当前回合数
//        Integer currentRound = 1;
//
//        // 是否播放动画
//        Boolean isPlayAnimation = true;
//
//        // 所有回合任务
//        Map allRoundQueue = new HashMap();

        // // 所有存活的角色
//    List<HolCharacter> allLiveCharacter = new ArrayList<>();

        // // 所有死亡的角色
        // allDeadCharacter: HolCharacter[] = []

        // 行动等待队列，若队列有未完成任务则等待完成后进入下一个角色行动
//    actionAwaitQueue: Promise<any>[] = []

        // 调用战斗开始回调
//        for (const character of this.allLiveCharacter) {
//            for (const buff of character.state.buff)
//            await buff.OnFightBegan(buff , this)
//            for (const equipment of character.state.equipment)
//            await equipment.OnFightBegan(equipment , this)
//            await character.state.OnFightBegan(character.state , this)
//        }
//        // 回合开始
//        while(currentRound <= 150) {
//            const roundState = new RoundState
//            const allLiveCharacter = [].concat(this.allLiveCharacter).sort((a , b) =>
//            b.state.speed - a.state.speed
//            )
//            // 调用回合任务
//            for (const task of this.allRoundQueue.get(this.currentRound) || []) await task()
//            // 调用回合开始回调
//            for (const character of allLiveCharacter) {
//                if (this.allLiveCharacter.indexOf(character) === -1) break
//                for (const buff of character.state.buff)
//                await buff.OnRoundBegan(buff , roundState , this)
//                for (const equipment of character.state.equipment)
//                await equipment.OnRoundBegan(equipment , roundState , this)
//                await character.state.OnRoundBegan(character.state , roundState , this)
//            }
//            // 角色行动
//            for (const character of allLiveCharacter) {
//                if (this.allLiveCharacter.indexOf(character) === -1) continue
//                await character.action()
//                // 等待行动队列清空
//                await Promise.all(this.actionAwaitQueue)
//                this.actionAwaitQueue = []
//                // 判断是否结束
//                if (this.allLiveCharacter.filter(c => c.direction === "left").length <= 0) return false
//                else if (this.allLiveCharacter.filter(c => c.direction === "right").length <= 0) return true
//            }
//            // 调用回合结束回调
//            for (const character of allLiveCharacter) {
//                if (this.allLiveCharacter.indexOf(character) === -1) break
//                for (const buff of character.state.buff)
//                await buff.OnRoundEnd(buff , roundState , this)
//                for (const equipment of character.state.equipment)
//                await equipment.OnRoundEnd(equipment , roundState , this)
//                await character.state.OnRoundEnd(character.state , roundState , this)
//            }
//            this.currentRound++
//            // 等待
//            if (this.isPlayAnimation) await new Promise(res => setTimeout(res, 300))
//            // 判断是否结束
//            if (this.allLiveCharacter.filter(c => c.direction === "left").length <= 0) return false
//            else if (this.allLiveCharacter.filter(c => c.direction === "right").length <= 0) return true
//        }

////        // 监听进度条完成函数
//        holPreLoad.listenComplete(async () => {
//                await new Promise(res => setTimeout(res, 500))
//        // 战斗开始
//            const result = await this.fightStart()
//        // 战斗胜利结算
//        if (result) this.fightSuccess()
//            // 战斗失败结算
//        else this.fightEnd()
//        })
//        // 设置 100%
//        }
//        return null;
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

    @Override
    public BaseResp playBattle(TokenDto token, HttpServletRequest request) throws Exception {
        GameFight gameFight = gameFightMapper.selectByPrimaryKey(token.getId());
        //自己的战队
        List<Characters> leftCharacter = charactersMapper.goIntoListById(gameFight.getUserId() + "");
        Collections.sort(leftCharacter, Comparator.comparing(Characters::getGoIntoNum));
        //对手战队
        List<Characters> rightCharacter = charactersMapper.goIntoListById(gameFight.getToUserId() + "");
        Collections.sort(rightCharacter, Comparator.comparing(Characters::getGoIntoNum));
        Map map = new HashMap();
        BaseResp baseResp = new BaseResp();
        map.put("leftCharacter", leftCharacter);
        map.put("rightCharacter", rightCharacter);
        baseResp.setSuccess(1);
        baseResp.setData(map);
        return baseResp;
    }

    @Override
    public BaseResp notice(HttpServletRequest request) throws Exception {
        BaseResp baseResp = new BaseResp();
        Set<String> keys = redisTemplate.keys("notice_*");
        List<String> notices=new ArrayList<>();
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

//    private  setCharacter(Characters characters ,String direct) {
////        const holCharacterPrefab = await util.bundle.load("prefab/HolCharacter" , Prefab)
////        const character = instantiate(holCharacterPrefab)
////        this.node.addChild(character)
////        const holCharacter = character.getComponent(HolCharacter)
////        await holCharacter.initCharacter(
////                create , direct , coordinate , this
////        )
////        this.node.on(NodeEventType.NODE_DESTROYED , () => {
////                character.parent.removeChild(character)
////        })
////        this.allLiveCharacter.push(holCharacter)
//    }

//    // 监听回合函数
//    public listenRoundEvent(round: number , call: Function) {
//        let roundEvents = this.allRoundQueue.get(this.currentRound + round + 1)
//        if (!roundEvents)
//            return this.allRoundQueue.set(this.currentRound + round + 1 , roundEvents = [call])
//        roundEvents.push(call)
//    }
//
//    // 战斗开始
//    private async fightStart(): Promise<boolean> {
//        // 调用战斗开始回调
//        for (const character of this.allLiveCharacter) {
//            for (const buff of character.state.buff)
//            await buff.OnFightBegan(buff , this)
//            for (const equipment of character.state.equipment)
//            await equipment.OnFightBegan(equipment , this)
//            await character.state.OnFightBegan(character.state , this)
//        }
//        // 回合开始
//        while(this.currentRound <= 150) {
//            const roundState = new RoundState
//            const allLiveCharacter = [].concat(this.allLiveCharacter).sort((a , b) =>
//            b.state.speed - a.state.speed
//            )
//            // 调用回合任务
//            for (const task of this.allRoundQueue.get(this.currentRound) || []) await task()
//            // 调用回合开始回调
//            for (const character of allLiveCharacter) {
//                if (this.allLiveCharacter.indexOf(character) === -1) break
//                for (const buff of character.state.buff)
//                await buff.OnRoundBegan(buff , roundState , this)
//                for (const equipment of character.state.equipment)
//                await equipment.OnRoundBegan(equipment , roundState , this)
//                await character.state.OnRoundBegan(character.state , roundState , this)
//            }
//            // 角色行动
//            for (const character of allLiveCharacter) {
//                if (this.allLiveCharacter.indexOf(character) === -1) continue
//                await character.action()
//                // 等待行动队列清空
//                await Promise.all(this.actionAwaitQueue)
//                this.actionAwaitQueue = []
//                // 判断是否结束
//                if (this.allLiveCharacter.filter(c => c.direction === "left").length <= 0) return false
//                else if (this.allLiveCharacter.filter(c => c.direction === "right").length <= 0) return true
//            }
//            // 调用回合结束回调
//            for (const character of allLiveCharacter) {
//                if (this.allLiveCharacter.indexOf(character) === -1) break
//                for (const buff of character.state.buff)
//                await buff.OnRoundEnd(buff , roundState , this)
//                for (const equipment of character.state.equipment)
//                await equipment.OnRoundEnd(equipment , roundState , this)
//                await character.state.OnRoundEnd(character.state , roundState , this)
//            }
//            this.currentRound++
//            // 等待
//            if (this.isPlayAnimation) await new Promise(res => setTimeout(res, 300))
//            // 判断是否结束
//            if (this.allLiveCharacter.filter(c => c.direction === "left").length <= 0) return false
//            else if (this.allLiveCharacter.filter(c => c.direction === "right").length <= 0) return true
//        }
//
//    }
//
//    // 设置角色
//    private async setCharacter(create: CharacterStateCreate , direct: "left"|"right" , coordinate: {row: number , col: number}) {
//        const holCharacterPrefab = await util.bundle.load("prefab/HolCharacter" , Prefab)
//        const character = instantiate(holCharacterPrefab)
//        this.node.addChild(character)
//        const holCharacter = character.getComponent(HolCharacter)
//        await holCharacter.initCharacter(
//                create , direct , coordinate , this
//        )
//        this.node.on(NodeEventType.NODE_DESTROYED , () => {
//                character.parent.removeChild(character)
//        })
//        this.allLiveCharacter.push(holCharacter)
//    }
//
//    // 战斗胜利
//    private fightSuccess() {
//        this.node.parent.getChildByName("FightFailure").active = false
//        this.node.parent.getChildByName("FightSuccess").active = true
//    }
//
//    // 战斗失败
//    private fightEnd() {
//        this.node.parent.getChildByName("FightFailure").active = true
//        this.node.parent.getChildByName("FightSuccess").active = false
//    }
}
