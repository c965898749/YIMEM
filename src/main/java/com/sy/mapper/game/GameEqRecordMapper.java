package com.sy.mapper.game;

import com.baomidou.mybatisplus.mapper.BaseMapper;
import com.sy.model.game.GameEqRecord;
import com.sy.model.game.GameTimeRecord;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

@Mapper
public interface GameEqRecordMapper  extends BaseMapper<GameEqRecord> {
    int deleteMe(@Param("userId")Integer userId);
    int deleteAll();
}