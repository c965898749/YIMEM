package com.sy.controller.game;

import com.sy.mapper.game.GameFightMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@Component
public class GameScheduled {
    //定时器
    @Autowired
    private GameFightMapper gameFightMapper;
    /**
     * 每天定时清除游戏过多消息
     */
    @Scheduled(cron = "0 0 1 * * ?")
    public  void pushsite() {
        gameFightMapper.deleteByTime();
    }
}
