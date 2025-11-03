package com.sy.mapper.game;

import com.sy.model.game.QqCardExp;

import java.util.List;

public interface QqCardExpMapper {
    int deleteByPrimaryKey(Integer id);

    int insert(QqCardExp record);

    int insertSelective(QqCardExp record);

    QqCardExp selectByPrimaryKey(Integer id);

    int updateByPrimaryKeySelective(QqCardExp record);

    int updateByPrimaryKey(QqCardExp record);

    List<QqCardExp> findbyStar(String star);
}