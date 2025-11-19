package com.sy.mapper.game;

import com.sy.model.game.ActivityDetail;
import org.apache.ibatis.annotations.Param;

import java.util.List;

public interface ActivityDetailMapper {
    int deleteByPrimaryKey(Integer id);

    int insert(ActivityDetail record);

    int insertSelective(ActivityDetail record);

    ActivityDetail selectByPrimaryKey(Integer id);

    int updateByPrimaryKeySelective(ActivityDetail record);

    int updateByPrimaryKey(ActivityDetail record);

    List<ActivityDetail> getByCodde(String activityCode);

    List<ActivityDetail> getByCodde2(@Param("activityCode") String activityCode,@Param("day") Integer day);

    ActivityDetail getByCodde3(@Param("detailCode") String detailCode);

}