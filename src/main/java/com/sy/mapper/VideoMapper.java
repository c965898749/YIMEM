package com.sy.mapper;

import com.sy.model.Video;

import java.util.List;

public interface VideoMapper {
    int deleteByPrimaryKey(Integer videoid);

    int insert(Video record);

    int insertSelective(Video record);

    Video selectByPrimaryKey(Integer videoid);
    Integer selectBytitle(String title);
    List<Video> select();

    int updateByPrimaryKeySelective(Video record);

    int updateByPrimaryKey(Video record);
}