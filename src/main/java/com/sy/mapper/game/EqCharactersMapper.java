package com.sy.mapper.game;

import com.baomidou.mybatisplus.mapper.BaseMapper;
import com.sy.model.game.EqCharacters;
import com.sy.model.game.TokenDto;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

@Mapper
public interface EqCharactersMapper extends BaseMapper<EqCharacters> {
    EqCharacters listById(@Param("userId") String userId, @Param("id") String id);

    List<EqCharacters> selectByUserId(Integer userId);

    int changeEqState(@Param("userId")String userId,@Param("id") String id);

    int changeEqState2(@Param("userId")String userId,@Param("id") String id,@Param("itemId")String itemId);
}