package com.sy.mapper;

import com.sy.model.Music;

import java.util.List;

public interface MusicMapper {
    int insert(Music record);

    int insertSelective(Music record);

    List<Music> findall();

    List<Music> selectByPams(Music music);
}
