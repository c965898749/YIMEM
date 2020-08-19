package com.sy.service.impl;

import com.fasterxml.jackson.databind.ser.Serializers;
import com.sy.mapper.BlogMapper;
import com.sy.mapper.SearchMapper;
import com.sy.model.*;
import com.sy.model.resp.BaseResp;
import com.sy.service.SearchService;
import com.sy.tool.Xtool;
import jdk.nashorn.internal.runtime.linker.LinkerCallSite;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.CollectionUtils;

import java.util.*;

@Service
@Transactional(readOnly = true)
public class SearchServiceImpl implements SearchService {
    @Autowired
    private BlogMapper blogMapper;
    @Autowired
    private SearchMapper searchMapper;
    private Logger log = Logger.getLogger(SearchServiceImpl.class.getName());
    @Override
    public BaseResp queryBlog(String key) {
        BaseResp baseResp = new BaseResp();
        StringBuffer stringBuffer = new StringBuffer();
        stringBuffer.append("%");
        stringBuffer.append(key);
        stringBuffer.append("%");
        List<Blog> blogList = blogMapper.queryByKey(stringBuffer.toString());
        if (blogList.size() != 0) {
            baseResp.setSuccess(200);
            baseResp.setData(blogList);
        } else {
            baseResp.setSuccess(404);
        }

        return baseResp;
    }

    @Override
    public BaseResp queryDownload(String key) {
        BaseResp baseResp = new BaseResp();
        StringBuffer stringBuffer = new StringBuffer();
        stringBuffer.append("%");
        stringBuffer.append(key);
        stringBuffer.append("%");
        List<Upload> uploadList = searchMapper.queryDownload(stringBuffer.toString());
        if (uploadList.size() != 0) {
            baseResp.setSuccess(200);
            baseResp.setData(uploadList);
        } else {
            baseResp.setSuccess(404);
        }

        return baseResp;
    }

    @Override
    public BaseResp queryForum(String key) {
        BaseResp baseResp = new BaseResp();
        StringBuffer stringBuffer = new StringBuffer();
        stringBuffer.append("%");
        stringBuffer.append(key);
        stringBuffer.append("%");
        List<Invitation> forumList = searchMapper.queryForum(stringBuffer.toString());
        if (forumList.size() != 0) {
            baseResp.setSuccess(200);
            baseResp.setData(forumList);
        } else {
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
        if (askList.size() != 0) {
            baseResp.setSuccess(200);
            baseResp.setData(askList);
        } else {
            baseResp.setSuccess(404);
        }
        return baseResp;
    }

    @Override
    public BaseResp queryVideo(String key) {
        BaseResp baseResp = new BaseResp();
        StringBuffer stringBuffer = new StringBuffer();
        stringBuffer.append("%");
        stringBuffer.append(key);
        stringBuffer.append("%");
        List<Video> askList = searchMapper.queryVideo(stringBuffer.toString());
        if (askList.size() != 0) {
            baseResp.setSuccess(200);
            baseResp.setData(askList);
        } else {
            baseResp.setSuccess(404);
        }
        return baseResp;
    }

    @Override
    public BaseResp queryAll(String key) {
        log.info("查询接受关键词----------"+key);
        //TODO           问题代码
//        List<List<?>> list = new ArrayList<>();
//        BaseResp baseResp = new BaseResp();
//        StringBuffer stringBuffer = new StringBuffer();
//        stringBuffer.append("%");
//        stringBuffer.append(key);
//        stringBuffer.append("%");
//        List<Blog> blogList = blogMapper.queryByKey(stringBuffer.toString());
//        List<Ask> askList = searchMapper.queryAsk(stringBuffer.toString());
//        List<Invitation> forumList = searchMapper.queryForum(stringBuffer.toString());
//        List<Upload> uploadList = searchMapper.queryDownload(stringBuffer.toString());
//        List<Video> videos = searchMapper.queryVideo(stringBuffer.toString());
//        list.add(blogList);
//        list.add(forumList);
//        list.add(askList);
//        list.add(uploadList);
//        list.add(videos);
//        if (!CollectionUtils.isEmpty(list)){
//            baseResp.setSuccess(200);
//            baseResp.setData(list);
//        }else {
//            baseResp.setSuccess(404);
//        }
//
//        return baseResp;
        Map<String,List<?>> map = new HashMap<>();
        BaseResp baseResp = new BaseResp();
        StringBuffer stringBuffer = new StringBuffer();
        stringBuffer.append("%");
        stringBuffer.append(key);
        stringBuffer.append("%");
        List<Blog> blogList = blogMapper.queryByKey(stringBuffer.toString());
        List<Ask> askList = searchMapper.queryAsk(stringBuffer.toString());
        List<Invitation> forumList = searchMapper.queryForum(stringBuffer.toString());
        List<Upload> uploadList = searchMapper.queryDownload(stringBuffer.toString());
        List<Video> videos = searchMapper.queryVideo(stringBuffer.toString());
        if (Xtool.isNotNull(blogList)){
            map.put("Blog",blogList);
        }
      if (Xtool.isNotNull(askList)){
          map.put("Ask",askList);
      }
      if (Xtool.isNotNull(forumList)){
          map.put("Invitation",forumList);
      }
      if (Xtool.isNotNull(uploadList))
      {
          map.put("Upload",uploadList);
      }
      if (Xtool.isNotNull(videos))
      {
          map.put("Video",videos);
      }
        if (!CollectionUtils.isEmpty(map)) {
            baseResp.setSuccess(200);
            baseResp.setData(map);
        } else {
            baseResp.setSuccess(300);
        }

        return baseResp;
    }
}
