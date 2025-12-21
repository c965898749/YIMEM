package com.sy.mapper.game;

import com.baomidou.mybatisplus.mapper.BaseMapper;
import com.sy.model.User;
import com.sy.model.game.FriendRelation;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;
@Mapper
public interface FriendRelationMapper extends BaseMapper<FriendRelation> {



    Integer findCount(@Param("userId") String userId);

    List<FriendRelation> findByUserid(@Param("userId") String userId,@Param("friendId") Integer friendId);

    List<User> findByid(@Param("userId") String userId,@Param("status")Integer status,@Param("fb")String fb);

    List<User> findMessage(@Param("userId") String userId,@Param("status")Integer status);

}