package com.sy.controller;

import com.sy.model.resp.BaseResp;
import com.sy.service.CollectItemsService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class CollectItemsController {
    @Autowired
    private CollectItemsService collectItemsService;

    @RequestMapping(value = "/addCollectitems")
    public BaseResp add(Integer collectId ,Integer blogId){
        BaseResp baseResp = collectItemsService.addToCollectResult(blogId,collectId);
        return baseResp;
    }
    @RequestMapping(value = "/deleteCollectitems")
    public BaseResp delete(Integer collectId ,Integer blogId){
        BaseResp baseResp = collectItemsService.delectCollectImemsResult(blogId,collectId);
        return baseResp;
    }





}
