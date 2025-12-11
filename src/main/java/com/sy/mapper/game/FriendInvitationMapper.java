package com.sy.mapper.game;

import com.sy.model.game.FriendInvitation;

public interface FriendInvitationMapper {
    int deleteByPrimaryKey(Long id);

    int insert(FriendInvitation record);

    int insertSelective(FriendInvitation record);

    FriendInvitation selectByPrimaryKey(Long id);

    int updateByPrimaryKeySelective(FriendInvitation record);

    int updateByPrimaryKey(FriendInvitation record);
}