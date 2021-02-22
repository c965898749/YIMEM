package com.sy.mapper;

import com.sy.model.T8DocManage;

import java.util.List;

public interface T8DocManageMapper {


    int insert(T8DocManage record);




    T8DocManage selectByPrimaryKey(Integer id);

    List<T8DocManage> selectByparentId(Integer id);

    int updateByPrimaryKeySelective(T8DocManage record);

    List<Integer> selectidByparentId(Integer id);

    int deleteByPrimaryKey(Integer id);

    int insertSelective(T8DocManage record);


    


    int updateByPrimaryKey(T8DocManage record);
}
