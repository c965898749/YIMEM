package com.sy.controller;


import com.sy.model.BlogReplay;
import com.sy.model.User;
import com.sy.model.resp.BaseResp;
import com.sy.service.BlogReplayService;
import com.sy.service.BlogReplaySonService;
import com.sy.tool.RedisCache;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletRequest;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

@RestController
public class BlogReplayController {
    @Autowired
    private BlogReplayService blogReplayService;
    @Autowired
    private BlogReplaySonService blogReplaySonService;

    @RequestMapping(value = "/addBlogReplay", method = RequestMethod.GET)
//    public BaseResp addBlogReplay(String replay_value, Integer userid, Integer blogid) {
    public BaseResp addBlogReplay(BlogReplay blogReplay,HttpServletRequest request) {
        BaseResp baseResp=new BaseResp();
        User user = (User) request.getSession().getAttribute("user");
        if (user == null) {
            baseResp.setSuccess(0);
            baseResp.setErrorMsg("未登入");
            return baseResp;
        } else {
            blogReplay.setCommentuserid(user.getUserId());
            return blogReplayService.addReplay(blogReplay);
        }

    }

    @RequestMapping(value = "/queryReplayByBlogId", method = RequestMethod.GET)
    public BaseResp queryByBlogId(Integer blogid, Integer pageNum) {
        BaseResp baseResp = blogReplayService.queryByBlogId(blogid, pageNum);
        return baseResp;
    }

    //查找评论用户的信息
    @RequestMapping(value = "/queryReplayInformation", method = RequestMethod.GET)
    public Map queryReplayInformation(HttpServletRequest request,Integer pageNum) {
        User user = (User) request.getSession().getAttribute("user");
        Map<String, Object> map = new HashMap<>();
        if (user != null) {
            map = blogReplayService.queryByUserId(user.getUserId(),pageNum);
        } else {
            map.put("success", 0);
        }
        return map;
    }

    //清除评论消息
    @RequestMapping(value = "removecommentreq", method = RequestMethod.POST)
    public BaseResp removecommentreq(HttpServletRequest request) {
        BaseResp baseResp = new BaseResp();
        User user = (User) request.getSession().getAttribute("user");
        if (user != null) {
            Integer userId = user.getUserId();
            blogReplayService.removecommentreq(userId);
        }

        baseResp.setSuccess(1);
        baseResp.setErrorMsg("回复已清除");
        return baseResp;
    }

    //点击评论消息已读
    @RequestMapping(value = "onclickcommentreq", method = RequestMethod.POST)
    public BaseResp onclickcommentreq(Integer blog_id, HttpServletRequest request) {
        BaseResp baseResp = new BaseResp();
        User user = (User) request.getSession().getAttribute("user");
        if (user != null) {
            Integer userId = user.getUserId();
            System.out.println(blog_id);
            blogReplayService.onclickcommentreq(blog_id, userId);
//            servic.readcommentreq(userId);
        }
        baseResp.setSuccess(1);
        baseResp.setErrorMsg("回复已读");
        return baseResp;
    }

    //评论对评论插入
    @RequestMapping(value = "blogReplaySonsave", method = RequestMethod.POST)
    public BaseResp blogReplaySonsave(BlogReplay blogReplaySon, HttpServletRequest request){
        BaseResp baseResp = new BaseResp();
        User user = (User) request.getSession().getAttribute("user");
        if (user == null) {
            baseResp.setSuccess(0);
            baseResp.setErrorMsg("未登入");
            return baseResp;
        } else {
            blogReplaySon.setCommentuserid(user.getUserId());
            return blogReplaySonService.insert(blogReplaySon);
        }

    }
    //评论对评论查找
    @RequestMapping(value = "queryBlogReplaySonByReplayId", method = RequestMethod.POST)
    public BaseResp queryBlogReplaySonByReplayId(Integer blogReplayId){
        BaseResp baseResp = new BaseResp();
        return blogReplaySonService.queryBlogReplaySonByReplayId(blogReplayId);

    }

}
