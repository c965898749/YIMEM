package com.sy.controller;

import com.sy.model.Invitation;
import com.sy.model.User;
import com.sy.model.resp.BaseResp;
import com.sy.service.InvitationService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.List;

@RestController
public class InvitationController {
    @Autowired
    private InvitationService service;
    @Autowired
    private InvitationService invitationService;

    @RequestMapping(value = "saveInvitation", method = RequestMethod.POST)
    public BaseResp saveInvitation(Invitation invitation, HttpServletResponse res, HttpServletRequest request) {
        BaseResp resp = new BaseResp();
        User user = (User) request.getSession().getAttribute("user");
        if (user == null) {
            resp.setSuccess(0);
            resp.setErrorMsg("未登入");
            return resp;
        } else {
            invitation.setUserid(user.getUserId());
            Integer count = service.save(invitation);
            if (count > 0) {
                resp.setSuccess(200);
                res.setStatus(200);
                return resp;
            } else {
                resp.setSuccess(400);
                res.setStatus(400);
                resp.setErrorMsg("插入失败");
                return resp;
            }
        }
    }

    @RequestMapping(value = "findMaxreadCountInvitation", method = RequestMethod.GET)
    public BaseResp findMaxreadCount(Integer page, Integer pageSize, HttpServletResponse res) {
        BaseResp resp = new BaseResp();
        List<Invitation> lists = service.selectMaxreadCount((page - 1) * pageSize, pageSize);
        Integer count = service.findMaxreadCountCount();
        if (lists != null) {
            resp.setSuccess(200);
            res.setStatus(200);
            resp.setData(lists);
            resp.setCount(count);
            return resp;
        } else {
            res.setStatus(400);
            resp.setSuccess(400);
            resp.setErrorMsg("资源为找到");
            return resp;
        }
    }

    @RequestMapping(value = "findAllInvitations", method = RequestMethod.GET)
    public BaseResp findAllInvitations(Invitation invitation, HttpServletResponse res) {
        BaseResp resp = service.findAll(invitation);
        return resp;
    }

    @RequestMapping(value = "findAspam", method = RequestMethod.GET)
    public BaseResp findAspam(Integer page, Integer pageSize, HttpServletResponse res) {
        BaseResp resp = new BaseResp();
        List<Invitation> list = service.findAspam((page - 1) * pageSize, pageSize);
        if (list != null) {
            resp.setSuccess(200);
            res.setStatus(200);
            resp.setData(list);
            return resp;
        } else {
            resp.setSuccess(400);
            res.setStatus(400);
            resp.setErrorMsg("资源未找到");
            return resp;
        }
    }

    @RequestMapping(value = "findNotice", method = RequestMethod.GET)
    public BaseResp findNotice(Integer page, Integer pageSize, HttpServletResponse res) {
        BaseResp resp = new BaseResp();
        List<Invitation> list = service.findNotice((page - 1) * pageSize, pageSize);
        Invitation invitation = new Invitation();
        invitation.setCategoryid(24);
        invitation.setCategoryid2(226);
        Integer count = service.findAllCount(invitation);
        if (list != null) {
            resp.setSuccess(200);
            resp.setCount(count);
            res.setStatus(200);
            resp.setData(list);
            return resp;
        } else {
            resp.setSuccess(400);
            res.setStatus(400);
            resp.setErrorMsg("资源未找到");
            return resp;
        }
    }

    @RequestMapping(value = "InvitaitonGetByCate", method = RequestMethod.GET)
    public BaseResp queryInvitationAll(Invitation invitation, HttpServletResponse res) {
        BaseResp resp = service.findAll(invitation);
        Integer count = service.findAllCount(invitation);
        if (resp.getData() != null) {
            resp.setSuccess(200);
            resp.setCount(count);
            res.setStatus(200);
            return resp;

        } else {
            resp.setSuccess(400);
            res.setStatus(400);
            resp.setErrorMsg("资源未找到");
            return resp;
        }

    }




}
