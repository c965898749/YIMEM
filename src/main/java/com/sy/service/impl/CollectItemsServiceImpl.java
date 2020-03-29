package com.sy.service.impl;

import com.sy.mapper.CollectItemsMapper;
import com.sy.model.resp.BaseResp;
import com.sy.service.CollectItemsService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional(readOnly = true)
public class CollectItemsServiceImpl implements CollectItemsService {
    @Autowired
    private CollectItemsMapper collectItemsMapper;
    @Override
    @Transactional(propagation = Propagation.REQUIRED)
    public BaseResp addToCollectResult(int blogid, int collectid) {
        BaseResp baseResp = new BaseResp();
        int result = collectItemsMapper.addToCollect(blogid,collectid);
        if (result!=0){
            baseResp.setSuccess(1);
        }else {
            baseResp.setSuccess(0);
        }
        return baseResp;
    }

    @Override
    @Transactional(propagation = Propagation.REQUIRED)
    public BaseResp delectCollectImemsResult(int blogid, int collectid) {
        BaseResp baseResp = new BaseResp();
        int result = collectItemsMapper.deleteCollect(blogid,collectid);
        if (result!=0){
            baseResp.setSuccess(1);
        }else {
            baseResp.setSuccess(0);
        }
        return baseResp;

    }
}
