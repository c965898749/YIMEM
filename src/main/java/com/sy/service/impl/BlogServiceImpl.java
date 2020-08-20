package com.sy.service.impl;

import com.github.pagehelper.Page;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;

import com.sy.mapper.*;
import com.sy.model.Blog;
import com.sy.model.User;
import com.sy.model.resp.BaseResp;
import com.sy.service.BlogService;
import com.sy.tool.HTMLSpirit;
import org.apache.commons.lang3.StringEscapeUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@Transactional(readOnly = true)
public class BlogServiceImpl implements BlogService {
    @Autowired
    private BlogMapper blogMapper;
    @Autowired
    private UserMapper userMapper;
    @Autowired
    private LikeMapper likeMapper;
    @Autowired
    private BlogReplayMapper blogReplayMapper;


    //新增博客
    @Override
    @Transactional(propagation = Propagation.REQUIRED)
    public BaseResp addBlog(String title, String content, String publishForm, String category, Integer userID) throws Exception {
        BaseResp baseResp = new BaseResp();
        Blog blog = new Blog();
        blog.setTitle(title);
        blog.setContent(content);
        blog.setPublishForm(publishForm);
        blog.setCategory(category);
        blog.setUserid(userID);
        if ("原创".equals(publishForm)) {
            User user = userMapper.selectUserByUserId(userID);
            Integer BlogCount = user.getBlogCount() + 1;
            user.setBlogCount(BlogCount);
            userMapper.updateuser(user);
        }
        blog.setTitle(StringEscapeUtils.escapeHtml4(blog.getTitle()));
        int result = blogMapper.insertNewBlog(blog);
        if (result > 0) {
            baseResp.setData(blog.getId());
            baseResp.setSuccess(1);
            baseResp.setErrorMsg("发布成功");
        } else {
            baseResp.setSuccess(0);
            baseResp.setErrorMsg("发布失败");
        }
        return baseResp;
    }


    //下方杨

    public BaseResp queryBykEYResult(String key) {
        BaseResp baseResp = new BaseResp();
        StringBuffer stringBuffer = new StringBuffer();
        stringBuffer.append("%");
        stringBuffer.append(key);
        stringBuffer.append("%");
        List<Blog> blogList = blogMapper.queryByKey(stringBuffer.toString());
        if (blogList.size() != 0) {
            baseResp.setSuccess(1);
            baseResp.setData(blogList);
        }

        return baseResp;
    }

    //查找所有
    public BaseResp queryAll(String pageNumStr) {
        BaseResp baseResp = new BaseResp();

        Integer pageNum = 1;
        Integer pageSize = 12;
        pageNum = Integer.parseInt(pageNumStr);
        PageHelper.startPage(pageNum, pageSize);
        List<Blog> blogList = blogMapper.queryAll();
        Page<Blog> blogPage = (Page<Blog>) blogList;


//        List<Blog> blogList = blogMapper.queryAll();
        if (blogList.size() != 0) {
            baseResp.setSuccess(1);
            baseResp.setData(this.filtration(blogList));
            baseResp.setCount(blogPage.getPageSize());
            return baseResp;
        }
        return null;
    }


    @Override
    public BaseResp queryByCategoryResult(String pageNumStr, String category) {
        BaseResp baseResp = new BaseResp();
        Integer pageNum = 1;
        try {
            pageNum = Integer.parseInt(pageNumStr);
        } catch (NumberFormatException e) {
            e.printStackTrace();
        }
        PageHelper.startPage(pageNum, 12);
        List<Blog> blogList = blogMapper.queryByCategory(category);
        if (!blogList.isEmpty() && blogList != null) {
            PageInfo<Blog> pageInfo = new PageInfo<Blog>(blogList);
            baseResp.setSuccess(1);
            baseResp.setCount(pageInfo.getTotal());
            baseResp.setData(this.filtration(blogList));
            return baseResp;
        }
        return null;
    }

    @Override
    public BaseResp queryOrderByReadCount(String pageNumStr) {
        BaseResp baseResp = new BaseResp();

        Integer pageNum = 1;
        Integer pageSize = 12;
        pageNum = Integer.parseInt(pageNumStr);
        PageHelper.startPage(pageNum, pageSize);
        List<Blog> blogList = blogMapper.queryOrderByReadCount();
        Page<Blog> blogPage = (Page<Blog>) blogList;
        if (blogList.size() != 0) {
            baseResp.setSuccess(1);
            baseResp.setData(this.filtration(blogList));
            baseResp.setCount(blogPage.getPageSize());
            return baseResp;
        }
        return null;
    }

    @Override
    @Transactional(propagation = Propagation.REQUIRED)
    public BaseResp addReadCount(Integer id) {
        BaseResp baseResp = new BaseResp();
        Integer i = blogMapper.addReadCount(id);
        Integer userId = blogMapper.queryUserIdById(id);
        User user = userMapper.selectUserByUserId(userId);
        Integer visitorCount = user.getVisitorCount() + 1;
        user.setVisitorCount(visitorCount);
        userMapper.updateuser(user);
        if (i != 0) {

            baseResp.setSuccess(1);

        } else {
            baseResp.setSuccess(0);
        }
        return baseResp;
    }

    @Override
    public BaseResp queryUserIdById(int id) {
        BaseResp baseResp = new BaseResp();
        Integer userId = blogMapper.queryUserIdById(id);
        if (userId != 0) {
            baseResp.setSuccess(1);
            baseResp.setData(userId);
        } else {
            baseResp.setSuccess(0);
        }
        return baseResp;
    }

    @Override
    public BaseResp queryById(int id) {
        BaseResp baseResp = new BaseResp();
        Blog blogList = blogMapper.queryBlogByBlogId(id);
        if (blogList != null) {
            baseResp.setSuccess(200);
            baseResp.setData(blogList);

        } else {
            baseResp.setSuccess(404);
            baseResp.setErrorMsg("没有找到该博文");
        }
        return baseResp;
    }

    @Override
    public BaseResp queryByUserId(Blog blog) {
        BaseResp baseResp = new BaseResp();
        Integer pageSize = 15;
        PageHelper.startPage(blog.getPageNum(), pageSize);
        List<Blog> blogList = blogMapper.queryByUserId(blog);
        Page<Blog> blogPage = (Page<Blog>) blogList;

        if (blogList.size() != 0) {
            baseResp.setSuccess(1);
            baseResp.setData(this.filtration(blogList));
            baseResp.setCount(blogPage.getTotal());
            return baseResp;
        }
        return null;

    }

    @Override
    public BaseResp queryBlogIdByUserIdAndReadCount(Integer userId, Integer pageNum) {
        BaseResp baseResp = new BaseResp();
        PageHelper.startPage(pageNum, 5);
        List<Blog> blogList = blogMapper.queryBlogIdByUserIdAndReadCount(userId);
        if (blogList.size() != 0) {
            baseResp.setSuccess(200);
            baseResp.setData(blogList);
        } else {
            baseResp.setSuccess(404);
            baseResp.setErrorMsg("没有找到该博文");
        }
        return baseResp;
    }

    //    置顶
    @Override
    @Transactional(propagation = Propagation.REQUIRED)
    public Integer StickBlogid(Integer id) {
        System.out.println(id);
        Integer stick = blogMapper.queryStickBlogid(id);
        System.out.println(stick);
        if (stick == 1) {
            stick = 0;
        } else {
            stick = 1;
        }
        return blogMapper.StickBlogid(stick, id);
    }

    //删除
    @Override
    @Transactional(propagation = Propagation.REQUIRED)
    public Integer deleteBlog(Integer id) {
        return blogMapper.deleteBlog(id);
    }

    @Override
    @Transactional(propagation = Propagation.REQUIRED)
    public BaseResp modifierBlog(String title, String content, String publishForm, String category, Integer blogId) throws Exception {
        BaseResp baseResp = new BaseResp();
        Blog blog = new Blog();
        blog.setTitle(title);
        blog.setContent(content);
        blog.setPublishForm(publishForm);
        blog.setCategory(category);
        blog.setId(blogId);
        int result = blogMapper.modifierBlog(blog);
        if (result > 0) {
            baseResp.setData(blog.getId());
            baseResp.setSuccess(1);
            baseResp.setErrorMsg("发布成功");
        } else {
            baseResp.setSuccess(0);
            baseResp.setErrorMsg("发布失败");
        }
        return baseResp;
    }


    @Override
    public BaseResp queryByRecommend(Integer page, Integer pageSize) {
        BaseResp baseResp = new BaseResp();
        PageHelper.startPage(page, pageSize);
        List<Blog> blogList = blogMapper.queryByRecomment();
        Page<Blog> blogPage = (Page<Blog>) blogList;
        if (blogList.size() != 0) {
            baseResp.setSuccess(1);
            baseResp.setData(this.filtration(blogList));
            baseResp.setCount(blogPage.getPages());
            return baseResp;
        }
        return null;
    }

    //    过滤博客
    public List<Blog> filtration(List<Blog> blogList) {
//        System.out.println("进行过滤");
        if (!blogList.isEmpty() && blogList != null) {
            for (int i = 0; i < blogList.size(); i++) {
                Blog blog = blogList.get(i);
                String s="<script[^>]*>[\\\\d\\\\D]*?</script>";
                User user = userMapper.selectUserByUserId(blog.getUserid());
                Integer likeCount = likeMapper.queryCountByBlogId(blog.getId());
                Integer replayCount = blogReplayMapper.queryReplayCountByBlogId(blog.getId());
                Integer fansCount=userMapper.selectFansCountbyUserId(blog.getUserid());
                if (user != null) {
                    blog.setContent(HTMLSpirit.delHTMLTag(blogList.get(i).getContent()));
                    blog.setLikeCount(likeCount);
                    blog.setUserFansCount(fansCount);
                    blog.setUsername(user.getNickname());
                    blog.setHeadimg(user.getHeadImg());
                    blog.setReplayCount(replayCount);
                    blog.setUserIndustry(user.getIndustry());
                    blog.setUserDescr(user.getDescription());
                }
                blogList.set(i, blog);
            }
        }
        return blogList;
    }

}
