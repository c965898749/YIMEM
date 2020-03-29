package com.sy.service.impl;

import com.fasterxml.jackson.databind.ser.Serializers;
import com.sy.mapper.BlogMapper;
import com.sy.mapper.SearchMapper;
import com.sy.model.*;
import com.sy.model.resp.BaseResp;
import com.sy.service.SearchService;
import jdk.nashorn.internal.runtime.linker.LinkerCallSite;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.CollectionUtils;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
@Service
@Transactional(readOnly = true)
public class SearchServiceImpl implements SearchService {
    @Autowired
    private BlogMapper blogMapper;
    @Autowired
    private SearchMapper searchMapper;

    @Override
    public BaseResp queryBlog(String key) {
        BaseResp baseResp = new BaseResp();
        StringBuffer stringBuffer = new StringBuffer();
        stringBuffer.append("%");
        stringBuffer.append(key);
        stringBuffer.append("%");
        List<Blog> blogList = blogMapper.queryByKey(stringBuffer.toString());
        if (blogList.size()!=0){
            baseResp.setSuccess(200);
            baseResp.setData(blogList);
        }else {
            baseResp.setSuccess(404);
        }

        return  baseResp;
    }

    @Override
    public BaseResp queryDownload(String key) {
        BaseResp baseResp = new BaseResp();
        StringBuffer stringBuffer = new StringBuffer();
        stringBuffer.append("%");
        stringBuffer.append(key);
        stringBuffer.append("%");
        List<Upload> uploadList = searchMapper.queryDownload(stringBuffer.toString());
        if (uploadList.size()!=0){
            baseResp.setSuccess(200);
            baseResp.setData(uploadList);
        }else {
            baseResp.setSuccess(404);
        }

        return  baseResp;
    }

    @Override
    public BaseResp queryForum(String key) {
        BaseResp baseResp = new BaseResp();
        StringBuffer stringBuffer = new StringBuffer();
        stringBuffer.append("%");
        stringBuffer.append(key);
        stringBuffer.append("%");
        List<Invitation> forumList = searchMapper.queryForum(stringBuffer.toString());
        if (forumList.size()!=0){
            baseResp.setSuccess(200);
            baseResp.setData(forumList);
        }else {
            baseResp.setSuccess(404);
        }
        return baseResp;
    }

    @Override
    public BaseResp queryAsk(String key) {
        BaseResp baseResp = new BaseResp();
        StringBuffer stringBuffer = new StringBuffer();
        stringBuffer.append("%");
        stringBuffer.append(key);
        stringBuffer.append("%");
        List<Ask> askList = searchMapper.queryAsk(stringBuffer.toString());
        if (askList.size()!=0){
            baseResp.setSuccess(200);
            baseResp.setData(askList);
        }else {
            baseResp.setSuccess(404);
        }
        return baseResp;
    }

    @Override
    public BaseResp queryAll(String key) {
        List<List<?>> list = new ArrayList<>();
        BaseResp baseResp = new BaseResp();
        StringBuffer stringBuffer = new StringBuffer();
        stringBuffer.append("%");
        stringBuffer.append(key);
        stringBuffer.append("%");
        List<Blog> blogList = blogMapper.queryByKey(stringBuffer.toString());
        List<Ask> askList = searchMapper.queryAsk(stringBuffer.toString());
        List<Invitation> forumList = searchMapper.queryForum(stringBuffer.toString());
        List<Upload> uploadList = searchMapper.queryDownload(stringBuffer.toString());
        list.add(blogList);
        list.add(forumList);
        list.add(askList);
        list.add(uploadList);

        if (!CollectionUtils.isEmpty(list)){
            baseResp.setSuccess(200);
            baseResp.setData(list);
        }else {
            baseResp.setSuccess(404);
        }

        return baseResp;
    }
}
