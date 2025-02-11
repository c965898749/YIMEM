package com.sy.service.impl;

import com.github.pagehelper.Page;
import com.github.pagehelper.PageHelper;
import com.sy.mapper.MusicMapper;
import com.sy.model.Invitation;
import com.sy.model.Music;
import com.sy.model.resp.BaseResp;
import com.sy.service.MusicService;
import com.sy.tool.Xtool;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class MusicServiceImpl implements MusicService {
    @Autowired
    private MusicMapper musicMapper;
    @Override
    public int insert(Music record) {
        return 0;
    }

    @Override
    public int insertSelective(Music record) {
        return musicMapper.insertSelective(record);
    }

    @Override
    public List<Music> findall() {
        return null;
    }

    @Override
    public BaseResp selectByPams(Map<String,String> param) {
        BaseResp baseResp=new BaseResp();
        Music music=new Music();
        List<Music> list=musicMapper.selectByPams(music);
        baseResp.setSuccess(200);
        baseResp.setData(list);
        return baseResp;
    }
}
