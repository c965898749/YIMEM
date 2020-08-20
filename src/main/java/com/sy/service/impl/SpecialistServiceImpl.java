package com.sy.service.impl;

import com.sy.mapper.SpecialistMapper;
import com.sy.model.Specialist;
import com.sy.model.resp.BaseResp;
import com.sy.service.SpecialistService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@Transactional(readOnly = true)
public class SpecialistServiceImpl implements SpecialistService {
    @Autowired
    private SpecialistMapper specialistMapper;

    @Override
    public BaseResp queryAllResult() {
        BaseResp baseResp = new BaseResp();
        List<Specialist> specialistList = specialistMapper.queryAll();
        if (specialistList!=null){
            baseResp.setData(specialistList);
            baseResp.setSuccess(1);
            return baseResp;
        }else {
            baseResp.setSuccess(0);
            baseResp.setErrorMsg("没有专家");
        }
        return null;
    }
}
