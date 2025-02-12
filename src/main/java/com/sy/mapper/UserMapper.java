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

    User selectUserByusername(@Param("username") String username);
    //修改用户信息
    int updateUserInfor(User user);
    //根据用户ID获取用户信息
    User selectUserByUserId(Integer userId);
//    //根据用户ID查询所关注的总人数
//    int selectAttentionCountbyUserId(Integer userId);
    //根据用户ID查询粉丝的总人数
    int selectFansCountbyUserId(Integer userId);
    //关注未读数
    int selectFansUnRreadCountbyUserId(Integer userId);
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

    //更新用户资源量
    Integer updatedownCount(@Param("downCount") Integer downCount, @Param("userId") Integer userId);

    //    更新用户上传量
    Integer resourceCount(@Param("resourceCount") Integer resourceCount, @Param("userId") Integer userId);

    //已读回复
    Integer readcommentreq(@Param("userId") Integer userId);
    Integer readqueryLikeId(@Param("userId") Integer userId);
    Integer removefansaa(@Param("userId") Integer userId);


    /**
     * 查询用户列表
     * @return
     */
    public List<User> getUserList(User user) throws Exception;

    /**
     * 搜索用户
     * @param user
     * @return
     */
    public List<User> getUserListBySearch(User user) throws Exception;

    /**
     * 查询登录的用户
     * @param user
     * @return
     */
    public User getLoginUser(User user) throws Exception;

    /**
     * 查询登录的用户
     * @param user
     * @return
     */
    public User getUserByLoginCode(User user) throws Exception;

    /**
     * 按主键查询用户
     * @param user
     * @return
     */
    public User getUserById(User user) throws Exception;

    /**
     * 新增用户
     * @param user
     * @return
     */
    public int addUser(User user) throws Exception;

    /**
     * 修改用户
     * @param user
     * @return
     */
    public int modifyUser(User user);

    /**
     * 根据用户角色修改用户
     * @param user
     * @return
     */
    public int modifyUserRole(User user);

    /**
     * 删除用户图片
     * @param user
     * @return
     */
    public int delUserPic(User user);

    /**
     * 删除用户
     * @param user
     * @return
     */
    public int deleteUser(User user);

    public int deleteUserByopenId(String openid);

    /**
     * 分页查询用户数
     * @param user
     * @return
     * @throws Exception
     */
    public int count(User user) throws Exception;

    /**
     * 用户名是否存在
     * @param user
     * @return
     * @throws Exception
     */
    public int loginCodeIsExit(User user) throws Exception;

    /**
     * 修改用户状态
     * @param status
     * @param id
     * @return
     * @throws Exception
     */
    public Integer madifyUserStatus(@Param("status") Integer status,@Param("id") Integer id) throws Exception;

    User getUserByloginCode(User user);

//    微信直接登录
    User getUserByopenid(@Param("openid") String openid);
//    解除微信绑定
    Integer delUserByopenid(@Param("openid")String openid);
}
