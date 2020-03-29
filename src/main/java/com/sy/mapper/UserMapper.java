package com.sy.mapper;

import com.sy.model.*;
import org.apache.ibatis.annotations.Param;

import java.util.List;

public interface UserMapper {
    //查询所有用户
    List<User> SelectAllUser();
    //新增用户
    int insertUser(User user);
    //修改头像
    int updateUserHeadImgByID(@Param("userId") Integer userId ,@Param("headImg") String headImg);
    //修改用户信息
    int updateUserInfor(User user);
    //根据用户ID获取用户信息
    User selectUserByUserId(Integer userId);
//    //根据用户ID查询所关注的总人数
//    int selectAttentionCountbyUserId(Integer userId);
    //根据用户ID查询粉丝的总人数
    int selectFansCountbyUserId(Integer userId);
    //根据用户ID查询博客的数量
    int selectBlogCountbyUserId(Integer userId);
    //根据用户ID查询资源的数量
    int selectResourceCountbyUserId(Integer userId);
//    //根据用户ID查询论坛的数量  还没建
//    int selectForumCountbyUserId(Integer userId);
    //根据用户ID查询问答的数量
    int selectAskCountbyUserId(Integer userId);
    //根据用户ID查询自己收藏的数量
    int selectCollectCountbyUserId(Integer userId);
    //根据用户ID查询关注的收藏夹的数量
    int selectAttentionCollectCountbyUserId(Integer userId);
    //根据用户ID扣除积分
    int updateAskMoneyByUserID(@Param("userID")Integer userID,@Param("askMoney")Integer askMoney);

   //根据userId查询粉丝
    List<Fans> selectAllFansByUserid(Integer userId);
    //根据userId查询关注的人
    List<Fans> selectAllreFansByUserId(Integer userId);
    //根据关注者ID和被关注者ID查看是否有关注记录
    int selectFansByFansedidAndFansid(@Param("fansedid")Integer fansedid,@Param("fansid")Integer fansid);
    //根据用户ID获取所有博客
    List<Blog> selectAllBlogByUserid(Integer userID);
    //根据用户ID获取所有资源
    List<Upload> selectAllUploadByUserid(Integer userID);
    //根据用户ID获取所有论坛
    List<Forum> selectAllForumByUserid(Integer userID);
    //根据用户ID获取所有问答
    List<Ask> selectAllAskByUserid(Integer userID);
    //根据用户ID查询用户问答积分
    double selectAskmoneybyUserID(Integer userId);
    //修改用户
    int updateuser(User user);




    //下方陈
    //更新用户积分
    Integer updateUserMoney(@Param("downloadmoney") Double downloadmoney, @Param("userId") Integer userId);

    //更新用户下载量
    Integer updatedownCount(@Param("downCount") Integer downCount, @Param("userId") Integer userId);

    //    更新用户上传量
    Integer resourceCount(@Param("resourceCount") Integer resourceCount, @Param("userId") Integer userId);

    //已读回复
    Integer readcommentreq(@Param("userId") Integer userId);
    Integer readqueryLikeId(@Param("userId") Integer userId);
    Integer removefansaa(@Param("userId") Integer userId);
}
