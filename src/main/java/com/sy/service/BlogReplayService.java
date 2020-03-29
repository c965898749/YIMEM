package com.sy.service;


import com.sy.model.BlogReplay;
import com.sy.model.resp.BaseResp;


import java.util.Date;
import java.util.Map;


public interface BlogReplayService {
    //添加评论
    BaseResp addReplay(BlogReplay blogReplay);
    //根据博文id查找评论
    BaseResp queryByBlogId(int blog_id,int pageNum);
    //查找评论用户信息
    Map<String,Object> queryByUserId(int userId,int page);
    //清除评论信息
    void removecommentreq(int userId);
    //点击评论已读
    void onclickcommentreq(int blog_id,int userId);


}
