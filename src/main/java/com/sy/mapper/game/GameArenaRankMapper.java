package com.sy.mapper.game;

import com.baomidou.mybatisplus.mapper.BaseMapper;
import com.sy.model.game.GameArenaRank;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

@Mapper
public interface GameArenaRankMapper extends BaseMapper<GameArenaRank> {
    Integer getArenaRanking(@Param("userId")String userId, @Param("arenaLevel") Integer arenaLevel, @Param("weekNum") Integer weekNum);
}