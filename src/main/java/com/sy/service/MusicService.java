package com.sy.service;

import com.sy.model.Music;
import com.sy.model.resp.BaseResp;

import java.util.List;
import java.util.Map;

public interface MusicService {

    int insert(Music record);

    int insertSelective(Music record);

    List<Music> findall();

    BaseResp selectByPams(Map<String,String> param);
}
