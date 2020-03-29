package com.sy.service.impl;

import com.sy.mapper.IntegralsMapper;
import com.sy.model.Integrals;
import com.sy.service.IntegralsService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
@Service
public class IntegralsServiceImpl implements IntegralsService {
    @Autowired
    private IntegralsMapper mapper;
    @Override
    public Integer save(Integrals integrals) {
        return null;
    }

    @Override
    public List<Integrals> findByUserid(Integer userid, Integer page, Integer pageSize) {
        return mapper.selectByUserid(userid,page,pageSize);
    }

    @Override
    public Integer findAllCount(Integer userid) {
        return mapper.selectAllCount(userid);
    }
}
