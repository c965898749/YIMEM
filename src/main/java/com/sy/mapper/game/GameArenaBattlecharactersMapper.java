package com.sy.mapper.game;

import com.baomidou.mybatisplus.mapper.BaseMapper;
import com.sy.model.game.Characters;
import com.sy.model.game.GameArenaBattlecharacters;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

@Mapper
public interface GameArenaBattlecharactersMapper extends BaseMapper<GameArenaBattlecharacters> {

    int updateGoNuM2(@Param("num") Integer num,@Param("arenaLevel") Integer arenaLevel,@Param("weekNum") Integer weekNum, @Param("id") String id, @Param("userId") String userId);

    List<Characters>  findCharacters(@Param("arenaLevel") Integer arenaLevel,@Param("weekNum") Integer weekNum, @Param("userId") String userId);
}