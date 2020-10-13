package com.sy.service.impl;

import com.github.pagehelper.PageHelper;
import com.sy.mapper.AppMapper;
import com.sy.model.App;
import com.sy.service.AppService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class AppServiceImpl implements AppService {
    @Autowired
    private AppMapper appMapper;
    @Override
    public int deleteByPrimaryKey(Integer id) {
        return 0;
    }

    @Override
    public int insert(App record) {
        return 0;
    }

    @Override
    public int insertSelective(App record) {
        return 0;
    }

    @Override
    public App selectByPrimaryKey(Integer id) {
        return null;
    }

    @Override
    public int updateByPrimaryKeySelective(App record) {
        return 0;
    }

    @Override
    public int updateByPrimaryKey(App record) {
        return 0;
    }

    @Override
    public List<App> selectAll() {
        return appMapper.selectAll();
    }
}
