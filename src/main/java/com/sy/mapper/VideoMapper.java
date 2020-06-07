package com.sy.mapper;

import com.sy.model.Video;

import java.util.List;

public interface VideoMapper {
    int deleteByPrimaryKey(Integer videoid);

    int insert(Video record);

    int insertSelective(Video record);

    Video selectByPrimaryKey(Integer videoid);
    Integer selectBytitle(String title);
    int updateByPrimaryKeySelective(Video record);
    List<Video> select(Video video);
    int updateByPrimaryKeyWithBLOBs(Video record);

    int updateByPrimaryKey(Video record);
}