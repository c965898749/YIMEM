package com.sy.service.impl;

import com.sy.expection.CsdnExpection;
import com.sy.mapper.DownloadCategoryMapper;
import com.sy.model.DownloadCategory;
import com.sy.model.Downloadreply;
import com.sy.service.DownloadCategoryService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
@Service
public class DownloadCategoryServiceImpl implements DownloadCategoryService {
    @Autowired
    private DownloadCategoryMapper mapper;
    @Override
    public List<DownloadCategory> findAll() throws CsdnExpection {
        return mapper.selectByPid(0);
    }

    @Override
    public List<DownloadCategory> findByPid(Integer pid)throws CsdnExpection {
        return mapper.selectByPid(pid);
    }

    @Override
    public DownloadCategory findByid(Integer id) throws CsdnExpection{
        return mapper.selectByid(id);
    }
}
