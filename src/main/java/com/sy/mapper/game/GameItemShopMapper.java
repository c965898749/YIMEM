package com.sy.mapper.game;

import com.sy.model.game.GameItemShop;

import java.util.List;

public interface GameItemShopMapper {
    int insert(GameItemShop record);

    int insertSelective(GameItemShop record);

    List<GameItemShop> selectAll();

    GameItemShop selectByItemId(String itemId);
}