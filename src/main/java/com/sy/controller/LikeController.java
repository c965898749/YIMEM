package com.sy.controller;

import com.sy.model.User;
import com.sy.model.resp.BaseResp;
import com.sy.service.FansService;
import com.sy.service.LikeService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletRequest;
import java.util.HashMap;
import java.util.Map;

@RestController
public class LikeController {
    @Autowired
    private LikeService likeService;
    @RequestMapping(value = "/queryIsLike",method = RequestMethod.GET)
    public BaseResp queryIsLike(Integer blogId, HttpServletRequest request){

        BaseResp baseResp = new BaseResp();
        User user=(User) request.getSession().getAttribute("user");
        if (user !=null){
            baseResp = likeService.query(blogId,user.getUserId());
        }else {
            baseResp.setSuccess(0);
        }

        return baseResp;
    }

    @RequestMapping("/addLike")
    public BaseResp addLike(Integer userId,Integer blogId){
        BaseResp baseResp = null;
        baseResp = likeService.add(blogId,userId);
        return baseResp;
    }
    @RequestMapping("/deleteLike")
    public BaseResp deleteLike(Integer userId,Integer blogId){
        BaseResp baseResp = null;
        baseResp = likeService.delete(blogId,userId);
        return baseResp;
    }

    //查找所有给我点赞的人
    @RequestMapping(value = "/queryLikeId",method = RequestMethod.GET)
    public Map queryLikeId(HttpServletRequest request,Integer pageNum){
        Map<String, Object> map = new HashMap<>();
        User user = (User) request.getSession().getAttribute("user");
        if(user!=null){
            map = likeService.queryLikeInformation(user.getUserId(),pageNum);
        }else {
            map.put("success", 0);
        }
        return map;
    }

    //清除评论消息
    @RequestMapping(value = "removequeryLikeId", method = RequestMethod.POST)
    public BaseResp removequeryLikeId(HttpServletRequest request) {
        BaseResp baseResp=new BaseResp();
        User user = (User) request.getSession().getAttribute("user");
        if (user != null) {
            Integer userId = user.getUserId();
//            blogReplayService.removecommentreq(userId);
            likeService.removequeryLikeId(userId);
//            servic.readcommentreq(userId);
        }
        baseResp.setSuccess(1);
        baseResp.setErrorMsg("回复已清除");
        return baseResp;
    }

    //点击评论消息已读
    @RequestMapping(value = "onclickqueryLikeId", method = RequestMethod.POST)
    public BaseResp onclickqueryLikeId(Integer blog_id,HttpServletRequest request) {
        BaseResp baseResp=new BaseResp();
        User user = (User) request.getSession().getAttribute("user");
        if (user != null) {
            Integer userId = user.getUserId();
//            blogReplayService.onclickcommentreq(blog_id,userId);
            likeService.onclickqueryLikeId(blog_id,userId);
//            servic.readcommentreq(userId);
        }
        baseResp.setSuccess(1);
        baseResp.setErrorMsg("回复已读");
        return baseResp;
    }
}
