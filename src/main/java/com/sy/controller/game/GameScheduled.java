package com.sy.controller.game;

import com.sy.mapper.UserMapper;
import com.sy.mapper.game.GameFightMapper;
import com.sy.mapper.game.GameNoticeMapper;
import com.sy.mapper.game.PlayerBronzeTowerMapper;
import com.sy.model.game.GameNotice;
import com.sy.service.GameServiceService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;

@Component
public class GameScheduled {
    //定时器
    @Autowired
    private GameFightMapper gameFightMapper;
    @Autowired
    private GameServiceService gameServiceService;
    @Autowired
    private GameNoticeMapper gameNoticeMapper;
    @Autowired
    private UserMapper userMapper;
    /**
     * 每天定时清除游戏过多消息
     */
    @Scheduled(cron = "0 0 1 * * ?")
    public  void pushsite() {
        gameFightMapper.deleteByTime();
        gameNoticeMapper.deleteByMap(new HashMap<>());
        userMapper.updateBronze1();

    }

    @Scheduled(cron = "0 0 22 ? * 7")
//    @Scheduled(cron = "0 0/2 * * * ?")
    public void executeWeeklyTask() {
        // 任务逻辑
        System.out.println("奖励发放");
        gameServiceService.sendRawrd();

    }
//    @Scheduled(cron = "0 0 0 ? * MON")
//    public void arenaWeekSettle() {
//        // 1. 同步上周排名（将上周currentRank赋值到本周lastWeekRank）
//        gameServiceService.syncLastWeekRank();
//    }



}
