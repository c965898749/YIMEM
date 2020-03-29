package com.sy.service;


import com.sy.model.Blog;
import com.sy.model.resp.BaseResp;
import org.springframework.stereotype.Service;


public interface BlogService {
    //新增博客
    BaseResp addBlog(String title, String content, String publishForm, String category, Integer userID) throws Exception;

//下方杨
    BaseResp queryBykEYResult(String category);

    BaseResp queryAll(String pageNum);

    //查找推荐
    BaseResp queryByRecommend(Integer page,Integer pageSize);

    //通过类别查找
    BaseResp queryByCategoryResult(String pageNumStr, String category);

    //通过阅读量查找数据
    BaseResp queryOrderByReadCount(String pageNum);

    //阅读量点击增加
    BaseResp addReadCount(Integer id);

    //通过博客Id查找博主id
    BaseResp queryUserIdById(int id);

    //通过博文Id查找内容
    BaseResp queryById(int id);

    //通过用户查找博客
    BaseResp queryByUserId(Blog blog);

    BaseResp queryBlogIdByUserIdAndReadCount(Integer userId,Integer pageNum);

    Integer StickBlogid(Integer id);

    Integer deleteBlog(Integer id);
    BaseResp modifierBlog(String title, String content, String publishForm, String category, Integer blogId) throws Exception;

}
