package com.sy.mapper.game;

import com.sy.model.game.StarSynthesisMaterials;

import java.util.List;

public interface StarSynthesisMaterialsMapper {
    int insert(StarSynthesisMaterials record);

    int insertSelective(StarSynthesisMaterials record);

    List<StarSynthesisMaterials> selectall(Integer synthesisId);
}