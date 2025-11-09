package com.sy.mapper.game;

import com.sy.model.game.GameGiftExchangeCode;

public interface GameGiftExchangeCodeMapper {
    int deleteByPrimaryKey(Long exchangeId);

    int insert(GameGiftExchangeCode record);

    int insertSelective(GameGiftExchangeCode record);

    GameGiftExchangeCode selectByPrimaryKey(Long exchangeId);

    int updateByPrimaryKeySelective(GameGiftExchangeCode record);

    int updateByPrimaryKey(GameGiftExchangeCode record);
}