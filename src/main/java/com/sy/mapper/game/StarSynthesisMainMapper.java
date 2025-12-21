package com.sy.mapper.game;

import com.baomidou.mybatisplus.mapper.BaseMapper;
import com.sy.model.game.PveReward;
import com.sy.model.game.StarSynthesisMain;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;
@Mapper
public interface StarSynthesisMainMapper extends BaseMapper<StarSynthesisMain> {
//    int insert(StarSynthesisMain record);
//
//    int insertSelective(StarSynthesisMain record);

    List<StarSynthesisMain> selectAll();
}