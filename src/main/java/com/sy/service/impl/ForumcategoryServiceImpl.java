package com.sy.service.impl;

import com.sy.expection.CsdnExpection;
import com.sy.mapper.ForumcategoryMapper;
import com.sy.model.Forumcategory;
import com.sy.service.ForumcategoryService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
@Service
public class ForumcategoryServiceImpl implements ForumcategoryService {
    @Autowired
    private ForumcategoryMapper mapper;
    @Override
    public List<Forumcategory> findAll() throws CsdnExpection {
        return mapper.selectByPid(0);
    }

    @Override
    public List<Forumcategory> findByPid(Integer pid) throws CsdnExpection {
        return mapper.selectByPid(pid);
    }

    @Override
    public Forumcategory findByid(Integer id) throws CsdnExpection {
        return mapper.selectByid(id);
    }
}
