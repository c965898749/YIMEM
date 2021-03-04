package com.sy.service.impl;

import com.sy.mapper.T8DocManageMapper;
import com.sy.model.T8DocManage;
import com.sy.service.T8DocManageService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.List;

/**
 * @author CZX
 * @version 1.0
 * @date 2021/2/22 0022 23:46
 */
@Service
public class T8DocManageServiceImpl implements T8DocManageService {
    @Autowired
    private T8DocManageMapper t8DocManageMapper;

    @Override
    public T8DocManage selectByPrimaryKey(Integer id) {
        return t8DocManageMapper.selectByPrimaryKey(id);
    }

    @Override
    public List<T8DocManage> selectByparentId(Integer id) {
        return t8DocManageMapper.selectByparentId(id);
    }

    @Override
    public int updateByPrimaryKeySelective(T8DocManage record) {
        return t8DocManageMapper.updateByPrimaryKeySelective(record);
    }

    @Override
    public List<Integer> selectidByparentId(Integer id) {
        return t8DocManageMapper.selectidByparentId(id);
    }

    @Override
    public int deleteByPrimaryKey(Integer id) {
        return t8DocManageMapper.deleteByPrimaryKey(id);
    }

    @Override
    public int insertSelective(T8DocManage record) {
        return t8DocManageMapper.insertSelective(record);
    }

    @Override
    public List<T8DocManage> M8610EQ006(T8DocManage record) {
        return t8DocManageMapper.M8610EQ006(record);
    }

    @Override
    public List<T8DocManage> M8610EQ005(T8DocManage record) {
        return t8DocManageMapper.M8610EQ005(record);
    }
}
