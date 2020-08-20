package com.sy.service.impl;

import com.sy.mapper.ResocollectMapper;
import com.sy.model.Resocollect;
import com.sy.service.ResocollectService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Isolation;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
@Service
public class ResocollectServiceImpl implements ResocollectService {
    @Autowired
    private ResocollectMapper mapper;
    @Override
    @Transactional(isolation = Isolation.DEFAULT,propagation = Propagation.REQUIRED)
    public Integer save(Resocollect download) {
        if (download.getDowid()==null||download.getUserid()==null){
            return 0;
        }

        Resocollect resocollect =mapper.selectAll(download.getUserid(),download.getDowid());

        if (resocollect==null){
            return mapper.insert(download);
        }else{
            return mapper.delete(download);
        }


    }

    @Override
    public Integer remove(Integer userid, Integer id) {
        return null;
    }

    @Override
    public Resocollect findAll(Integer userid,Integer id) {
        if (userid==null||id==null){
            return null;
        }
        return mapper.selectAll(userid,id);
    }

    @Override
    public List<Resocollect> findByUserid(Integer userid, Integer page, Integer pageSize) {
        return mapper.selectByUserid(userid,page,pageSize);
    }

    @Override
    public Integer findAllCount(Integer userid) {
        return mapper.selectAllCount(userid);
    }
}
