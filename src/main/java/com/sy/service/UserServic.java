package com.sy.service;

import com.sy.expection.CsdnExpection;
import com.sy.model.User;
import com.sy.model.resp.BaseResp;
import org.apache.ibatis.annotations.Param;

import javax.servlet.http.HttpServletRequest;
import java.util.List;

public interface UserServic {
    //登录验证
    BaseResp loginVerification(String username, String userpassword, HttpServletRequest request) throws Exception;

    void updateUserRedis(Integer userId, HttpServletRequest request) throws Exception;

    User getUserByRedis(HttpServletRequest request) throws Exception;

    //注册新用户
    BaseResp addUser(String username, String userpassword) throws Exception;

    //修改用户头像
    BaseResp modifyHeadImgByUserid(Integer userId, String headImg,HttpServletRequest request) throws Exception;

    //修改用户信息
    BaseResp modifyUserInfor(User user) throws Exception;

    //根据用户ID获取用户信息
    BaseResp findUserByUserId(Integer userId) throws Exception;

    //根据用户ID获取用户信息包含博客、问答等的总数量
    BaseResp findUserInforIncludeMsg(Integer userId) throws Exception;

    //根据userId查询粉丝
    BaseResp findAllFansByUserid(Integer userId);

    //根据userId查询关注的人
    BaseResp findAllreFansByUserId(Integer userId);

    //个人主页渲染数据
    BaseResp perInfordata(Integer viewUserId, Integer userId) throws Exception;

    //个人主页细节数据
    BaseResp perInforDetailData(Integer userId, String type, Integer pageNum) throws Exception;

    //修改账号
    BaseResp midifyUserByUserId(Integer userId, String userpassword, String username) throws Exception;


    //下方陈
    //    增加用户积分
    Integer plusUserMoney(Double downloadmoney, Integer userId) throws CsdnExpection;

    //    减少用户积分
    Integer minuUserMoney(Double downloadmoney, Integer userId) throws CsdnExpection;

    //用户资源积分操作
    Integer downloadMoney(Double downloadmoney, Integer userId, Integer upLoadId, Integer id) throws CsdnExpection;

    //已读回复
    void readcommentreq(Integer userId);

    //已读点赞
    void readqueryLikeId(Integer userId);

    //已读关注
    void readfansaa(Integer userId);


    int updateUserMoney(User user);

    /**
     * 查询用户列表
     *
     * @return
     */
    public List<User> getUserList(User user) throws Exception;

    /**
     * 搜索用户
     *
     * @param user
     * @return
     */
    public List<User> getUserListBySearch(User user) throws Exception;

    /**
     * 查询登录的用户
     *
     * @param user
     * @return
     */
    public User getLoginUser(User user) throws Exception;

    /**
     * 按主键查询用户
     *
     * @param user
     * @return
     */
    public User getUserById(User user) throws Exception;

    /**
     * 新增用户
     *
     * @param user
     * @return
     */
    public int addUser(User user) throws Exception;

    /**
     * 修改用户
     *
     * @param user
     * @return
     */
    public int modifyUser(User user);

    /**
     * 根据用户角色修改用户
     *
     * @param user
     * @return
     */
    public int modifyUserRole(User user);

    /**
     * 删除用户图片
     *
     * @param user
     * @return
     */
    public int delUserPic(User user);

    /**
     * 删除用户
     *
     * @param user
     * @return
     */
    public int deleteUser(User user);

    /**
     * 分页查询用户数
     *
     * @param user
     * @return
     * @throws Exception
     */
    public int count(User user) throws Exception;

    /**
     * 用户名是否存在
     *
     * @param user
     * @return
     * @throws Exception
     */
    public int loginCodeIsExit(User user) throws Exception;

    public BaseResp modifyUserStatus(Integer status, Integer id) throws Exception;

    public User getUserByLoginCode(User user) throws Exception;

    User findUserByName(User user);

    User getUserByopenid(String openid);

    Integer delUserByopenid(String openid);

    Integer updateuser(User user);
}
