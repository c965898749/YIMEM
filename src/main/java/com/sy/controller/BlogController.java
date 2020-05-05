package com.sy.controller;


import com.sy.model.Blog;
import com.sy.model.User;
import com.sy.model.resp.BaseResp;
import com.sy.service.BlogService;
import com.sy.service.UserServic;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import java.util.ArrayList;
import java.util.List;

@RestController
@RequestMapping("/blog")
public class BlogController {
    @Autowired
    BlogService blogService;
    @Autowired
    private UserServic userServic;

    @RequestMapping(value = "addBlog", method = RequestMethod.POST)
    public BaseResp addBlog(String title, String content, String publishForm, String category, Integer userID) {
        BaseResp baseResp = new BaseResp();
        try {
            baseResp = blogService.addBlog(title, content, publishForm, category, userID);
        } catch (Exception e) {
            e.printStackTrace();
            baseResp.setSuccess(0);
            baseResp.setErrorMsg("服务器异常");
        }
        return baseResp;
    }

    @RequestMapping(value = "modifierBlog", method = RequestMethod.POST)
    public BaseResp modifierBlog(HttpServletRequest request,String title, String content, String publishForm, String category, Integer blogId) {
        BaseResp baseResp = new BaseResp();
        User user = (User) request.getSession().getAttribute("user");
        if (user == null) {
            baseResp.setSuccess(0);
            baseResp.setErrorMsg("未登入");
            return baseResp;
        } else {
            try {
                baseResp = blogService.modifierBlog(title, content, publishForm, category, blogId);
            } catch (Exception e) {
                e.printStackTrace();
                baseResp.setSuccess(0);
                baseResp.setErrorMsg("服务器异常");
            }
            return baseResp;
        }

    }

    //下方杨
    //首页加载所有
    @RequestMapping(value = "/requestAll", method = RequestMethod.GET)
    public BaseResp queryAll(String page) {
        BaseResp baseResp = new BaseResp();
        baseResp = blogService.queryAll(page);
        return baseResp;
    }

    //通过阅读量排序查询
    @RequestMapping(value = "/requestOrderByReadCount", method = RequestMethod.GET)
    public BaseResp requestOrderByReadCount(String page) {

        BaseResp baseResp = new BaseResp();
        baseResp = blogService.queryOrderByReadCount(page);
        return baseResp;
    }

    //查找分类
    @RequestMapping(value = "/requestByCategory", method = RequestMethod.GET)
    public BaseResp requestByCategory(String page, String category) {
        BaseResp baseResp = new BaseResp();
        baseResp = blogService.queryByCategoryResult(page, category);
        return baseResp;
    }

    //查找推荐
    @RequestMapping(value = "/requestByRecommend", method = RequestMethod.GET)
    public BaseResp requestByRecommend(Integer page, Integer pageSize) {
        if (pageSize == null) {
            pageSize = 15;
        }
        BaseResp baseResp = new BaseResp();
        baseResp = blogService.queryByRecommend(page, pageSize);
        return baseResp;
    }

    //增加阅读数量
    @RequestMapping(value = "/addReadCount")
    public BaseResp addReadCount(Integer id) {
        BaseResp baseResp = new BaseResp();
        baseResp = blogService.addReadCount(id);
        return baseResp;
    }

    //通过博文ID查找博主id
    @RequestMapping(value = "/queryUserIdById", method = RequestMethod.GET)
    public BaseResp queryUserIdById(Integer blogId) {
        BaseResp baseResp = new BaseResp();
        baseResp = blogService.queryUserIdById(blogId);
        return baseResp;
    }

    //查找博主的信息
    @RequestMapping(value = "/blog/{blogUserId}", method = RequestMethod.GET)
    public BaseResp queryInformationByUserId(@PathVariable("blogUserId") Integer blogUserId) {
        BaseResp baseResp = new BaseResp();
        try {
            baseResp = userServic.findUserByUserId(blogUserId);
        } catch (Exception e) {
            baseResp.setSuccess(0);
        }
        return baseResp;
    }

    //获取博文正文，标题，博主的信息等
    @RequestMapping(value = "/{blogId}", method = RequestMethod.GET)
    public BaseResp queryBlogByBlogId(@PathVariable("blogId") Integer blogId) {
        BaseResp baseResp = new BaseResp();
        baseResp = blogService.queryById(blogId);
        return baseResp;
    }

    //通过userId查找博文
    @RequestMapping(value = "/queryByUserId", method = RequestMethod.GET)
    public BaseResp queryBlogByUserId(Blog blog) {
        BaseResp baseResp = new BaseResp();
        baseResp = blogService.queryByUserId(blog);
        return baseResp;
    }

    //通过userId查找热门博文
    @RequestMapping(value = "/queryBlogIdByUserIdAndReadCount", method = RequestMethod.GET)
    public BaseResp queryBlogIdByUserIdAndReadCount(Integer userId, Integer pageNum) {
        BaseResp baseResp = new BaseResp();
        baseResp = blogService.queryBlogIdByUserIdAndReadCount(userId, pageNum);
        return baseResp;
    }

    //通过blogId置顶帖子
    @RequestMapping(value = "/StickBlogid", method = RequestMethod.GET)
    public BaseResp StickBlogid(Integer blogid, HttpServletRequest request) {
        BaseResp baseResp = new BaseResp();
        User user = (User) request.getSession().getAttribute("user");
        if (user == null) {
            baseResp.setSuccess(0);
            baseResp.setErrorMsg("未登入");
            return baseResp;
        } else {
            baseResp.setSuccess(1);
            baseResp.setErrorMsg("已登入");
            blogService.StickBlogid(blogid);
            return baseResp;
        }

    }
//    删除帖子
@RequestMapping(value = "/deleteBlog", method = RequestMethod.GET)
public BaseResp deleteBlog(Integer blogid, HttpServletRequest request) {
    BaseResp baseResp = new BaseResp();
    User user = (User) request.getSession().getAttribute("user");
    if (user == null) {
        baseResp.setSuccess(0);
        baseResp.setErrorMsg("未登入");
        return baseResp;
    } else {
        baseResp.setSuccess(1);
        baseResp.setErrorMsg("已登入");
        blogService.deleteBlog(blogid);
        return baseResp;
    }

}
}
