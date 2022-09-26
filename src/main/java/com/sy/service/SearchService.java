package com.sy.service;


import com.sy.model.resp.BaseResp;

public interface SearchService {
    //搜索博客
    BaseResp queryBlog(String key);
    //搜索资源
    BaseResp queryDownload(String key);
    //搜索论坛
    BaseResp queryForum(String key);
    //搜索问答
    BaseResp queryAsk(String key);
    //查询所有
    BaseResp queryAll(String key);
    //查询视频
    BaseResp queryVideo(String key);
    //查看面试
    BaseResp queryinterview(String key);
}
