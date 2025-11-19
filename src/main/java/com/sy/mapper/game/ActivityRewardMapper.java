package com.sy.mapper.game;

import com.sy.model.game.ActivityReward;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

import java.util.List;

public interface ActivityRewardMapper {
    int deleteByPrimaryKey(Long id);

    int insert(ActivityReward record);

    int insertSelective(ActivityReward record);

    ActivityReward selectByPrimaryKey(Long id);

    int updateByPrimaryKeySelective(ActivityReward record);

    int updateByPrimaryKey(ActivityReward record);

    // 根据活动编码、星级、难度查询有效奖励（支持多个奖励）
    @Select("SELECT * FROM activity_reward " +
            "WHERE activity_code = #{activityCode} " +
            "AND star_level = #{starLevel} " +
            "AND difficulty_level = #{difficultyLevel} " +
            "AND status = 1")
    List<ActivityReward> getRewards(
            @Param("activityCode") String activityCode,
            @Param("starLevel") Integer starLevel,
            @Param("difficultyLevel") String difficultyLevel
    );

    List<ActivityReward> getByCodde(String detailCode);

}