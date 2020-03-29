package com.sy.service;

import com.sy.model.Forum;
import com.sy.model.resp.BaseResp;

import java.util.Date;
import java.util.List;

public interface ForumService {
    //通过论坛文章id查找帖子
    BaseResp queryById(Integer invocationId);
    //通过论坛帖子Id查找帖子回复
    BaseResp queryInvitationReplayById(Integer invocationId);
    //添加帖子回复
    BaseResp addReplay(Integer userid, Integer invocationId, String comment);
    //通过userID获取所有的帖子
    BaseResp queryallInvitationByUserId(Integer userId,int pageNum);
}
