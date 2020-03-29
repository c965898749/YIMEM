package com.sy.service;

import com.sy.model.resp.BaseResp;

import java.util.Map;

public interface LikeService {
    //查询like表
    BaseResp query(Integer blog_id, Integer userid);
    //新增
    BaseResp add(Integer blog_id, Integer userid);
    //删除
    BaseResp delete(Integer blog_id, Integer userid);
    //查询所有给我点赞的人的信息以及点赞的文章名
    Map queryLikeInformation(int userId,int page);

    void removequeryLikeId(Integer userId);
    void onclickqueryLikeId(Integer blog_id,Integer userId);

}
