package com.sy.controller;

import com.sy.model.User;
import com.sy.model.resp.BaseResp;
import com.sy.service.ForumService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletRequest;

@RestController
public class ForumController {
    @Autowired
    private ForumService forumService;
    //通过论坛Id查找
    @RequestMapping(value = "/queryByInvitation",method = RequestMethod.GET)
    public BaseResp queryByInvitation(Integer invitationId){
        BaseResp baseResp = forumService.queryById(invitationId);
        return baseResp;
    }
    //通过论坛id查找帖子回复
    @RequestMapping(value = "/queryReplayByInvitation",method = RequestMethod.GET)
    public BaseResp queryReplayByInvitation(Integer invitationId){
        BaseResp baseResp = forumService.queryInvitationReplayById(invitationId);
        return baseResp;
    }
    //添加帖子回复
    @RequestMapping(value = "/addInvitationReplay",method = RequestMethod.POST)
    public BaseResp addReplay(Integer invitationId, String comment, HttpServletRequest request){
        BaseResp baseResp = new BaseResp();
        User user = (User) request.getSession().getAttribute("user");
        if (user!=null){
          baseResp = forumService.addReplay(user.getUserId(),invitationId,comment);
        }else {
            baseResp.setSuccess(0);
            baseResp.setErrorMsg("没有登录");
        }

        return baseResp;
    }
    @RequestMapping(value = "/queryAllInvitationByuserId",method = RequestMethod.POST)
    public BaseResp queryAllInvitationByuserId(HttpServletRequest request, Integer pageNum){
        BaseResp baseResp  =  new BaseResp();
        User user = (User) request.getSession().getAttribute("user");
        if (user!=null){
            baseResp = forumService.queryallInvitationByUserId(user.getUserId(), pageNum);
        }else {
            baseResp.setSuccess(404);
            baseResp.setErrorMsg("没有登录");
        }
        return baseResp;
    }

}
