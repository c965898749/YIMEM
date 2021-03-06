package com.sy.service.impl;

import com.sy.mapper.T8DocManageMapper;
import com.sy.model.T8DocManage;
import com.sy.service.T8DocManageService;
import com.sy.tool.FastDFSClient;
import com.sy.tool.Xtool;
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

    @Override
    public Integer M8610EU001(T8DocManage record) {
        return t8DocManageMapper.M8610EU001(record);
    }

    @Override
    public Integer M8610EQ004(T8DocManage record) {
        return t8DocManageMapper.M8610EQ004(record);
    }

    @Override
    public Integer M8610ED001(T8DocManage record) {
        try {
            FastDFSClient fastDFSClient = new FastDFSClient("classpath:fdfs_client.conf");
            T8DocManage t8DocManage=t8DocManageMapper.M8610EQ008(record);
            if ("0".equals(t8DocManage.getIsDirectory())&& Xtool.isNotNull(t8DocManage.getSrc())){
                fastDFSClient.deleteFile("group1", t8DocManage.getSrc());
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return t8DocManageMapper.M8610ED001(record);
    }

    @Override
    public Integer M8610ES001(T8DocManage record) {
        return t8DocManageMapper.M8610ES001(record);
    }

    @Override
    public T8DocManage M8610EQ008(T8DocManage record) {
        return t8DocManageMapper.M8610EQ008(record);
    }
}
