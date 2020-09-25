package com.sy.service.impl;

import com.sy.mapper.BulletMapper;
import com.sy.model.Bullet;
import com.sy.service.BulletService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
@Service
public class BulletServiceImpl implements BulletService {
    @Autowired
    private BulletMapper bulletMapper;

    @Override
    public int deleteByPrimaryKey(Integer bulletid) {
        return 0;
    }

    @Override
    public int insert(Bullet record) {
        return 0;
    }

    @Override
    public int insertSelective(Bullet record) {
        return bulletMapper.insert(record);
    }

    @Override
    public Bullet selectByPrimaryKey(Integer bulletid) {
        return null;
    }

    @Override
    public List<Bullet> selectByVideoId(Integer videoId) {
        return bulletMapper.selectByVideoId(videoId);
    }

    @Override
    public int updateByPrimaryKeySelective(Bullet record) {
        return 0;
    }

    @Override
    public int updateByPrimaryKey(Bullet record) {
        return 0;
    }
}
