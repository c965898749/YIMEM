package com.sy.mapper.game;

import com.baomidou.mybatisplus.mapper.BaseMapper;
import com.sy.model.game.GameTimeRecord;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

@Mapper
public interface GameTimeRecordMapper extends BaseMapper<GameTimeRecord> {
    int deleteMe(@Param("userId")Integer userId);
}