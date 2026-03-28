package com.sy.mapper.game;

import com.baomidou.mybatisplus.mapper.BaseMapper;
import com.sy.model.game.PveReward;
import com.sy.model.game.PveRewardRecord;
import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface PveRewardRecordMapper extends BaseMapper<PveRewardRecord> {
    int deleteAll();


}