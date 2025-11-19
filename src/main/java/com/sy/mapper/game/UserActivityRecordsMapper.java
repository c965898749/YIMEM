package com.sy.mapper.game;

import com.sy.model.game.UserActivityRecords;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

import java.util.List;

public interface UserActivityRecordsMapper {
    int deleteByPrimaryKey(Long id);

    int insert(UserActivityRecords record);

    int insertSelective(UserActivityRecords record);

    UserActivityRecords selectByPrimaryKey(Long id);

    int updateByPrimaryKeySelective(UserActivityRecords record);

    int updateByPrimaryKey(UserActivityRecords record);

    // 查询当日记录
    @Select("SELECT * FROM user_activity_records " +
            "WHERE user_id = #{userId} " +
            "AND detail_code = #{detailCode} " +
            "AND participation_date = CURDATE() " +
            "AND status = 1 " +
            "ORDER BY participation_time DESC")
    List<UserActivityRecords> listTodayRecords(
            @Param("userId") String userId,
            @Param("detailCode") String detailCode
    );
    // 统计当日有效参与次数
    @Select("SELECT COUNT(*) FROM user_activity_records " +
            "WHERE user_id = #{userId} " +
            "AND detail_code = #{detailCode} " +
            "AND participation_date = CURDATE() " +
            "AND status = 1")
    int countTodayValidRecords(
            @Param("userId") String userId,
            @Param("detailCode") String activityCode
    );
}