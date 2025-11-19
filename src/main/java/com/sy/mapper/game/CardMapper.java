package com.sy.mapper.game;

import com.sy.model.game.Card;

import java.util.List;

public interface CardMapper {
    int deleteByPrimaryKey(Integer uuid);

    int insert(Card record);

    int insertSelective(Card record);

    Card selectByPrimaryKey(Integer uuid);

    int updateByPrimaryKeySelective(Card record);

    int updateByPrimaryKey(Card record);

    List<Card> selectAll();

    Card selectByid(Integer id);

}