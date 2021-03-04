package com.sy.service;

import com.sy.model.T8DocManage;

import java.util.List;

/**
 * @author CZX
 * @version 1.0
 * @date 2021/2/22 0022 23:45
 */
public interface T8DocManageService {
    T8DocManage selectByPrimaryKey(Integer id);

    List<T8DocManage> selectByparentId(Integer id);

    int updateByPrimaryKeySelective(T8DocManage record);

    List<Integer> selectidByparentId(Integer id);

    int deleteByPrimaryKey(Integer id);

    int insertSelective(T8DocManage record);

    List<T8DocManage> M8610EQ006(T8DocManage record);
    List<T8DocManage> M8610EQ005(T8DocManage record);
}
