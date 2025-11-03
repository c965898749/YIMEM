package com.sy.mapper.game;

import com.sy.model.game.PveDetail;

public interface PveDetailMapper {
    int insert(PveDetail record);

    int insertSelective(PveDetail record);

    PveDetail selectById(String id);
}