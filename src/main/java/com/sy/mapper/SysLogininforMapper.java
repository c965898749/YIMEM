package com.sy.mapper;

import com.sy.model.SysLogininfor;
import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface SysLogininforMapper {
    int deleteByPrimaryKey(Long infoId);

    int insert(SysLogininfor record);

    int insertSelective(SysLogininfor record);

    SysLogininfor selectByPrimaryKey(Long infoId);

    int updateByPrimaryKeySelective(SysLogininfor record);

    int updateByPrimaryKey(SysLogininfor record);
}