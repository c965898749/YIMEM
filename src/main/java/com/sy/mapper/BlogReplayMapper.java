package com.sy.mapper;

import com.sy.model.BlogReplay;
import org.apache.ibatis.annotations.Param;

import java.util.Date;
import java.util.List;

public interface BlogReplayMapper {
    //添加评论
    Integer addReplay(BlogReplay blogReplay);
    //根据博文id查找评论
    List<BlogReplay> queryByBlogId(int blog_id);
    //评论状态
    Integer removecommentreq(List<Integer> list);
    Integer readcommentreq(@Param("userId")Integer userId );
    Integer onclickcommentreq(@Param("blog_id") Integer blog_id);
    Integer queryReplayCountByBlogId(@Param("blog_id") Integer blog_id);
    BlogReplay queryByBlogReplayId(@Param("id") Integer id);
    Integer updateCount(@Param("id") Integer id);
    List<BlogReplay> queryreplayByUserId(@Param("userId") Integer userId);
    Integer insert(BlogReplay blogReplaySon);
    List<BlogReplay> queryBlogReplaySonByReplayId(@Param("blogReplayId")Integer blogReplayId);
}
