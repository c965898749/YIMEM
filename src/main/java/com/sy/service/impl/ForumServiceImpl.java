package com.sy.service.impl;

import com.github.pagehelper.Page;
import com.github.pagehelper.PageHelper;
import com.sy.mapper.ForumMapper;
import com.sy.mapper.UserMapper;
import com.sy.model.Invitation;
import com.sy.model.Invitation_Replay;
import com.sy.model.User;
import com.sy.model.resp.BaseResp;
import com.sy.service.ForumService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.List;

@Service
public class ForumServiceImpl implements ForumService {
    @Autowired
    private ForumMapper forumMapper;
    @Autowired
    private UserMapper userMapper;
    @Override
    public BaseResp queryById(Integer invocationId) {
        BaseResp baseResp = new BaseResp();
        Invitation forumList = forumMapper.queryById(invocationId);
        if (forumList!=null){
            int userid = forumList.getUserid();
            User user = userMapper.selectUserByUserId(userid);
            forumList.setUser(user);
            baseResp.setSuccess(200);
            baseResp.setData(forumList);


        }else {
            baseResp.setSuccess(404);

        }
        return baseResp;
    }

    @Override
    public BaseResp queryInvitationReplayById(Integer invocationId) {
        BaseResp baseResp = new BaseResp();
        List<Invitation_Replay> invitation_replay = forumMapper.queryInvitationReplayId(invocationId);
        if (invitation_replay!=null){
            for (Invitation_Replay invitation_replay1 :invitation_replay){
                int userid = invitation_replay1.getCommentuserid();
                User user = userMapper.selectUserByUserId(userid);
                invitation_replay1.setUser(user);
            }

            baseResp.setSuccess(200);
            baseResp.setData(invitation_replay);
        }else {
            baseResp.setSuccess(404);
        }
        return baseResp;
    }

    @Override
    public BaseResp addReplay(Integer userid, Integer invocationId, String comment) {
        BaseResp baseResp = new BaseResp();
        Integer result = forumMapper.addReplay(userid,invocationId,comment,new Date());
        if (result!=0){
            baseResp.setSuccess(201);

        }else {
            baseResp.setSuccess(400);
        }
        return baseResp;
    }
    @Override
    public BaseResp queryallInvitationByUserId(Integer userId,int pageNum) {


        int pageSize = 10;

        PageHelper.startPage(pageNum,pageSize);
        BaseResp baseResp = new BaseResp();
        List<Invitation> invitationList = forumMapper.queryallInvitationByUserId(userId);
        Page<Invitation> invitationPage = (Page<Invitation>)invitationList;
        if (invitationList.size()!=0){
            for (Invitation invitation :invitationList){
                User user = userMapper.selectUserByUserId(userId);
                invitation.setUser(user);
                int invitationId = invitation.getId();
                int count = forumMapper.queryReplayCount(invitationId);
                invitation.setReplaycount(count);

            }
            baseResp.setData(invitationList);
            baseResp.setSuccess(200);
            baseResp.setCount((int)invitationPage.getTotal());


        }else {
            baseResp.setSuccess(400);
        }
        return baseResp;
    }

}
