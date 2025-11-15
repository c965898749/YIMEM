package com.sy.mapper.game;

import com.sy.model.game.PlayerBackpack;

public interface PlayerBackpackMapper {
    int insert(PlayerBackpack record);

    int insertSelective(PlayerBackpack record);
}