package com.sy.mapper.game;

import com.sy.model.game.GameGiftExchangeCode;
import org.apache.ibatis.annotations.Param;

import java.util.List;

public interface GameGiftExchangeCodeMapper {
    int deleteByPrimaryKey(Long exchangeId);

    int insert(GameGiftExchangeCode record);

    int insertSelective(GameGiftExchangeCode record);

    GameGiftExchangeCode selectByPrimaryKey(Long exchangeId);

    int updateByPrimaryKeySelective(GameGiftExchangeCode record);

    int updateByPrimaryKey(GameGiftExchangeCode record);

    List<GameGiftExchangeCode> selectByUserCode(GameGiftExchangeCode record);

    List<GameGiftExchangeCode> selectByUserCode2(GameGiftExchangeCode record);
}