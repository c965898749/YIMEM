package com.sy.mapper.game;

import com.sy.model.game.ActivityConfig;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

import java.util.List;

public interface ActivityConfigMapper {
    int deleteByPrimaryKey(Long id);

    int insert(ActivityConfig record);

    int insertSelective(ActivityConfig record);

    ActivityConfig selectByPrimaryKey(Long id);

    int updateByPrimaryKeySelective(ActivityConfig record);

    int updateByPrimaryKey(ActivityConfig record);
    // 通过编码查询活动配置
    @Select("SELECT * FROM activity_config WHERE activity_code = #{activityCode}")
    ActivityConfig getByCode(@Param("activityCode") String activityCode);

    List<ActivityConfig> selectAll();
}