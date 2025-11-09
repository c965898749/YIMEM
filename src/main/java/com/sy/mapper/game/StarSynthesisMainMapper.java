package com.sy.mapper.game;

import com.sy.model.game.StarSynthesisMain;

import java.util.List;

public interface StarSynthesisMainMapper {
    int insert(StarSynthesisMain record);

    int insertSelective(StarSynthesisMain record);

    List<StarSynthesisMain> selectAll();
}