package com.sy.mapper;

import com.sy.model.Fans;

import org.apache.ibatis.annotations.Param;

import java.util.List;

public interface FansMapper {
    List<String> queryByfansid(Integer fansId);
    //查看一个用户是否关注另一个用户
    List<Fans> queryIsFocus(@Param("fansedid") int fansedid, @Param("fansid") int fansid);
    //增加 //该接口我使用过 个人详情页
    Integer addFocus(@Param("fansedid") int fansedid, @Param("fansid") int fansid);
    //删除  //该接口我使用过 个人详情页
    Integer deleteFocus(@Param("fansedid") int fansedid, @Param("fansid") int fansid);
    //查找一个用户的所有粉丝
    List<Fans> queryAllFans(int userId);
    //消除消息状态
    Integer removefansaa(@Param("fansedid") Integer fansedid);
    //已读消息
    Integer readfansaa(@Param("fansedid") Integer fansedid);

    Integer queryStatusByFocus(@Param("fansedid") int fansedid, @Param("fansid") int fansid);
}
