package com.sy.mapper.game;

import com.baomidou.mybatisplus.mapper.BaseMapper;
import com.sy.model.game.GameNotice;
import com.sy.model.game.PillRobRecord;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

@Mapper
public interface PillRobRecordMapper extends BaseMapper<PillRobRecord> {

    List<PillRobRecord>  seletByUserId(@Param("userId")String userId);

}