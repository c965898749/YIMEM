package com.sy.service.impl;

import com.sy.entity.ActivationKey;
import com.sy.mapper.ActivationKeyMapper;
import com.sy.model.resp.BaseResp;
import com.sy.service.ActivationKeyService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class ActivationKeyServiceImpl implements ActivationKeyService {

    @Autowired
    private ActivationKeyMapper activationKeyMapper;
    @Override
    public int insert(ActivationKey record) {
        if (activationKeyMapper.queryBystatus(record)<=0){
            activationKeyMapper.remove(record);
            return activationKeyMapper.insert(record);
        }
        return 0;
    }

    @Override
    public int update(ActivationKey record) {
        return activationKeyMapper.update(record);
    }

    @Override
    public int updateRandomCode(String randomCode) {
        return activationKeyMapper.updateRandomCode(randomCode);
    }

    @Override
    public BaseResp queryBytype(ActivationKey record) {
        BaseResp baseResp = new BaseResp();
        ActivationKey activationKey=activationKeyMapper.queryBytype(record);
        if (activationKey!=null){
            baseResp.setData(activationKey);
            baseResp.setSuccess(1);
            baseResp.setErrorMsg("获取激活码成功");
        }else {
            baseResp.setSuccess(0);
            baseResp.setErrorMsg("获取激活码失败");
        }
        return baseResp;
    }
}
