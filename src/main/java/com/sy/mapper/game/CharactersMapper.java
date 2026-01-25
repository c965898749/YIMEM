package com.sy.mapper.game;

import com.sy.model.game.Characters;
import com.sy.model.game.TokenDto;
import org.apache.ibatis.annotations.Param;

import java.util.List;

public interface CharactersMapper {
    int deleteByPrimaryKey(Integer uuid);

    int insert(Characters record);

    int insertSelective(Characters record);

    Characters selectByPrimaryKey(Integer uuid);

    int updateByPrimaryKeySelective(Characters record);

    int updateByPrimaryKey(Characters record);

    List<Characters> selectAllCardList();

    List<Characters> selectByUserId(Integer userId);

    int updateGoNuM(String userId);

    int updateDelte(Integer uuid);

    int updateGoNuM2(@Param("num") Integer num,@Param("id") String id,@Param("userId") String userId);

    Characters listById(@Param("userId") String userId,@Param("id") String id);

    List<Characters> goIntoListById(@Param("userId") String userId);

    List<Characters> goIntoListByIds(@Param("userIds") String userIds);
}