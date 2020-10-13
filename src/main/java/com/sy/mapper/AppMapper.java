package com.sy.mapper;

import com.sy.model.App;

import java.util.List;

public interface AppMapper {
    int deleteByPrimaryKey(Integer id);

    int insert(App record);

    int insertSelective(App record);

    App selectByPrimaryKey(Integer id);

    int updateByPrimaryKeySelective(App record);

    int updateByPrimaryKey(App record);

    List<App> selectAll();
}
