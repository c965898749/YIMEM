package com.sy.mapper;


import com.sy.model.Blog;
import org.apache.ibatis.annotations.Param;

import java.util.List;

public interface BlogMapper {

    //新增博客
    int insertNewBlog(Blog blog);

//下方杨


    //通过关键词查询
    List<Blog> queryByKey(String key);
    //查找所有
    List<Blog> queryAll();
    //查找推荐
    List<Blog> queryByRecomment();

    List<Blog> queryByCategory(String category);

    //原生分页查找类别

    List<Blog> queryByCategoryByPage(@Param("category") String category, @Param("initNum") int initNum, @Param("pageSize") int pageSize);
    //通过userId去查找博客
    List<Blog> queryByUserId(Blog blog);

    List<Blog> selectpage(@Param("userId") Integer userId ,@Param("page")Integer page,@Param("pageSize")Integer pageSize);

   Integer selectcount(@Param("userId") Integer userId);
    //通过阅读量查找数据
    List<Blog> queryOrderByReadCount();
    //阅读量点击增加
    Integer addReadCount(int id);
    //查找专家，通过userId查找博客数量最多的用户，即为专家
    Integer queryMaxCountByUserId();

    //通过博客Id查找博主id
    Integer queryUserIdById(int id);
    //通过博客ID查找博客
    Blog queryBlogByBlogId(int id);
    //根据用户id查找博客id
    List<Integer> queryBlogIdByUserId(@Param("userid") Integer userid);

    List<Blog> queryBlogIdByUserIdAndReadCount(@Param("userid") Integer userid);

    Integer StickBlogid(@Param("stick") Integer stick,@Param("id") Integer id);

    Integer queryStickBlogid(@Param("id") Integer id);

    Integer deleteBlog(@Param("id") Integer id);

    int modifierBlog(Blog blog);
}
