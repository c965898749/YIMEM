package com.sy.mapper.game;

import com.sy.model.game.GameFight;

import java.util.List;

public interface GameFightMapper {
    int deleteByPrimaryKey(Integer id);

    int insert(GameFight record);

    int insertSelective(GameFight record);

    GameFight selectByPrimaryKey(String id);

    List<GameFight> selectAll(String userId);

    int updateByPrimaryKeySelective(GameFight record);

    int updateByPrimaryKey(GameFight record);
}