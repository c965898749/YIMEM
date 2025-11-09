package com.sy.mapper.game;

import com.sy.model.game.GameGiftContent;

import java.util.List;

public interface GameGiftContentMapper {
    int deleteByPrimaryKey(Long contentId);

    int insert(GameGiftContent record);

    int insertSelective(GameGiftContent record);

    GameGiftContent selectByPrimaryKey(Long contentId);

    int updateByPrimaryKeySelective(GameGiftContent record);

    int updateByPrimaryKey(GameGiftContent record);

    List<GameGiftContent> selectByGiftId(Long id);
}