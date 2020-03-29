package com.sy.mapper;

import com.sy.model.Forum;
import com.sy.model.Invitation;
import com.sy.model.Invitation_Replay;
import org.apache.ibatis.annotations.Param;

import java.util.Date;
import java.util.List;

public interface ForumMapper {
    //通过论坛文章id查找帖子
    Invitation queryById(Integer invocationId);
    //通过论坛文章Id查找帖子回复
    List<Invitation_Replay> queryInvitationReplayId(Integer invocationId);
    //通过帖子文章添加帖子回复
    Integer addReplay(@Param("commentuserid") Integer userid, @Param("Invitation_id") Integer invocationId, @Param("comment") String comment, @Param("time") Date time);
    //通过userID获取所有的帖子
    List<Invitation> queryallInvitationByUserId(Integer userId);
    //查找帖子的回复数量
    Integer queryReplayCount(Integer invocationId);


}
