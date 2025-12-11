package com.sy.mapper.game;

import com.sy.model.game.FriendBlessing;

public interface FriendBlessingMapper {
    int deleteByPrimaryKey(Long id);

    int insert(FriendBlessing record);

    int insertSelective(FriendBlessing record);

    FriendBlessing selectByPrimaryKey(Long id);

    int updateByPrimaryKeySelective(FriendBlessing record);

    int updateByPrimaryKey(FriendBlessing record);
}