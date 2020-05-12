package com.sy.mapper;

import com.sy.model.Emil;

public interface EmilMapper {
    int deleteByPrimaryKey(Integer id);

    int insert(Emil record);

    int insertSelective(Emil record);

    Emil selectByPrimaryKey(Integer id);

    int updateByPrimaryKeySelective(Emil record);

    int updateByPrimaryKey(Emil record);
}