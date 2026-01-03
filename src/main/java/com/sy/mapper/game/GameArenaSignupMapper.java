package com.sy.mapper.game;

import com.baomidou.mybatisplus.mapper.BaseMapper;
import com.sy.model.game.GameArenaSignup;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

@Mapper
public interface GameArenaSignupMapper extends BaseMapper<GameArenaSignup> {
    List<GameArenaSignup> gameArena(@Param("arenaScore")Integer arenaScore,@Param("userId")String userId,@Param("arenaLevel") Integer arenaLevel,@Param("weekNum") Integer weekNum);
}