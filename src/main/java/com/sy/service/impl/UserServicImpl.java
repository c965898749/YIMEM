package com.sy.service.impl;

import com.github.pagehelper.Page;
import com.github.pagehelper.PageHelper;
import com.sy.expection.CsdnExpection;
import com.sy.mapper.*;
import com.sy.model.*;
import com.sy.model.resp.BaseResp;
import com.sy.service.UserServic;
import com.sy.tool.Constants;
import com.sy.tool.RandomName;
import com.sy.tool.RedisCache;
import com.sy.tool.Xtool;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Isolation;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.RequestMapping;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

@Service
@Transactional(readOnly = true)
public class UserServicImpl implements UserServic {
    @Autowired
    private UserMapper userMapper;
    @Autowired
    private DownloadMapper downloadMapper;
    @Autowired
    private IntegralsMapper integralsMapper;
    @Autowired
    private UploadMapper uploadMapper;
    @Autowired
    private BlogReplayMapper blogReplayMapper;
    @Autowired
    private BlogMapper blogMapper;
    @Autowired
    private LikeMapper likeMapper;
    @Autowired
    private FansMapper fansMapper;
    @Autowired
    private InformationMapper informationMapper;

    @Override
    public BaseResp loginVerification(String username, String userpassword) throws Exception {

        BaseResp baseResp = new BaseResp();
        User user = new User();
        user.setUsername(username);
        user.setUserpassword(userpassword);
        List<User> userList = userMapper.SelectAllUser();
        List<String> usernamelist = new ArrayList<>();
        for (User user1 : userList) {
            usernamelist.add(user1.getUsername());
        }
        if (!usernamelist.contains(user.getUsername())) {
            baseResp.setSuccess(0);
            baseResp.setErrorMsg("您输入的账号有误");
            return baseResp;
        } else {
            for (User user1 : userList) {
                if (username.equals(user1.getUsername())) {
                    if (userpassword.equals(user1.getUserpassword())) {
                        baseResp.setData(user1);
                        baseResp.setSuccess(1);
                        baseResp.setErrorMsg("登入成功");
                        return baseResp;
                    } else {
                        baseResp.setSuccess(0);
                        baseResp.setErrorMsg("您输入的密码有误");
                        return baseResp;
                    }
                }
            }
            baseResp.setSuccess(0);
            baseResp.setErrorMsg("您输入的密码有误");
            return baseResp;
        }
    }

    //注册新用户
    @Override
    @Transactional(propagation = Propagation.REQUIRED)
    public BaseResp addUser(String username, String userpassword) throws Exception {
        BaseResp baseResp = new BaseResp();
        User user = new User();
        user.setUsername(username);
        user.setUserpassword(userpassword);
        List<User> userList = userMapper.SelectAllUser();
        List<String> usernamelist = new ArrayList<>();
        for (User user1 : userList) {
            usernamelist.add(user1.getUsername());
        }
        if (usernamelist.contains(username)) {
            baseResp.setSuccess(0);
            baseResp.setErrorMsg("您输入的账号已存在，请重新输入");
            return baseResp;
        } else {
            //设置昵称
            String nickname = null;
            Integer flag = 1;
            Integer len = 4;
            userList.clear();
            for (User user1 : userList) {
                usernamelist.add(user1.getNickname());
            }
            while (flag != 0) {
                nickname = RandomName.randomName(false, len);
                if (!usernamelist.contains(nickname)) {
                    flag = 0;
                } else {
                    flag++;
                    if (flag > 1100000000 && flag < 2100000000) {
                        len++;
                        flag = 1;
                    }
                }
            }
            user.setNickname(nickname);
            int result = userMapper.insertUser(user);
            if (result > 0) {
                baseResp.setSuccess(1);
                baseResp.setErrorMsg("注册成功！");
                return baseResp;
            } else {
                baseResp.setSuccess(1);
                baseResp.setErrorMsg("注册成功！");
                return baseResp;
            }
        }
    }

    //修改用户头像
    @Override
    @Transactional(propagation = Propagation.REQUIRED)
    public BaseResp modifyHeadImgByUserid(Integer userId, String headImg) throws Exception {
        BaseResp baseResp = new BaseResp();
        int result = userMapper.updateUserHeadImgByID(userId, headImg);
        if (result > 0) {
            baseResp.setSuccess(1);
            baseResp.setErrorMsg("修改成功");
        } else {
            baseResp.setSuccess(0);
            baseResp.setErrorMsg("修改失败");
        }
        return baseResp;
    }

    //修改用户信息
    @Override
    @Transactional(propagation = Propagation.REQUIRED)
    public BaseResp modifyUserInfor(User user) throws Exception {
        BaseResp baseResp = new BaseResp();

        int result = userMapper.updateUserInfor(user);
        if (result > 0) {
            baseResp.setSuccess(1);
            baseResp.setErrorMsg("更新成功");
        } else {
            baseResp.setSuccess(0);
            baseResp.setErrorMsg("更新失败");
        }
        return baseResp;
    }

    @Override
    public BaseResp findUserByUserId(Integer userId) throws Exception {
        BaseResp baseResp = new BaseResp();
        User user = userMapper.selectUserByUserId(userId);
        if (user != null) {
            baseResp.setSuccess(1);
            baseResp.setErrorMsg("获取成功");
            baseResp.setData(user);
        } else {
            baseResp.setSuccess(0);
            baseResp.setErrorMsg("获取失败");
        }
        return baseResp;
    }
    //根据用户ID获取用户信息包含博客、问答等的总数量

    @Override
    public BaseResp findUserInforIncludeMsg(Integer userId) throws Exception {
        BaseResp baseResp = new BaseResp();
        User user = userMapper.selectUserByUserId(userId);
        //获取所关注的总人数
        Integer attentionCount = userMapper.selectAllreFansByUserId(userId).size();
        //获取粉丝的总人数
        Integer fansCount = userMapper.selectFansCountbyUserId(userId);
        //博客的数量
        Integer blogCount = userMapper.selectBlogCountbyUserId(userId);
        //资源的数量
        Integer resourceCount = userMapper.selectResourceCountbyUserId(userId);
//        //论坛的数量
//        Integer forumCount=

        //问答的数量
        Integer askCount = userMapper.selectAskCountbyUserId(userId);
        //收藏夹数量
        Integer collectCount = userMapper.selectCollectCountbyUserId(userId) + userMapper.selectAttentionCollectCountbyUserId(userId);
        user.setAttentionCount(attentionCount);
        user.setFansCount(fansCount);
        user.setBlogCount(blogCount);
        user.setResourceCount(resourceCount);
        user.setAskCount(askCount);
        user.setCollectCount(collectCount);
        if (user != null) {
            baseResp.setSuccess(1);
            baseResp.setErrorMsg("获取成功");
            baseResp.setData(user);
        } else {
            baseResp.setSuccess(0);
            baseResp.setErrorMsg("获取失败");
        }

        return baseResp;
    }

    //根据userId查询粉丝
    @Override
    public BaseResp findAllFansByUserid(Integer userId) {
        BaseResp baseResp = new BaseResp();
        List<Fans> fans = userMapper.selectAllFansByUserid(userId);
        List<User> users = new ArrayList<>();
        if (fans.size() > 0) {
            for (Fans fans1 : fans) {
                User user1 = new User();
                User user = userMapper.selectUserByUserId(fans1.getFansid());
                user1.setHeadImg(user.getHeadImg());
                user1.setUserId(user.getUserId());
                user1.setNickname(user.getNickname());
                users.add(user1);
            }
            baseResp.setSuccess(1);
            baseResp.setErrorMsg("获取成功");
            baseResp.setData(users);
        } else {
            baseResp.setSuccess(0);
            baseResp.setErrorMsg("获取失败");
            baseResp.setData(users);
        }
        return baseResp;
    }

    //根据userId查询关注的人
    @Override
    public BaseResp findAllreFansByUserId(Integer userId) {
        BaseResp baseResp = new BaseResp();
        List<User> users = new ArrayList<>();
        List<Fans> fans = userMapper.selectAllreFansByUserId(userId);
        if (fans.size() > 0) {
            for (Fans fans1 : fans) {
                User user1 = new User();
                User user = userMapper.selectUserByUserId(fans1.getFansedid());
                user1.setNickname(user.getNickname());
                user1.setUserId(user.getUserId());
                user1.setHeadImg(user.getHeadImg());
                users.add(user1);
            }
            baseResp.setSuccess(1);
            baseResp.setErrorMsg("获取成功");
            baseResp.setData(users);
        } else {
            baseResp.setSuccess(0);
            baseResp.setErrorMsg("获取失败");
            baseResp.setData(users);
        }
        return baseResp;
    }

    //个人主页渲染数据
    @Override
    public BaseResp perInfordata(Integer viewUserId, Integer userId) throws Exception {
        BaseResp baseResp = new BaseResp();
        User user = userMapper.selectUserByUserId(viewUserId);
        int isFans = userMapper.selectFansByFansedidAndFansid(viewUserId, userId);
        //获取所关注的总人数
        Integer attentionCount = userMapper.selectAllreFansByUserId(viewUserId).size();

        //获取粉丝的总人数
        Integer fansCount = userMapper.selectFansCountbyUserId(viewUserId);

        if (user != null) {
            user.setFansCount(fansCount);
            user.setAttentionCount(attentionCount);
            if (isFans > 0) {
                baseResp.setCount(1);
            } else {
                baseResp.setCount(0);
            }
            baseResp.setSuccess(1);
            baseResp.setErrorMsg("获取成功");
            baseResp.setData(user);
        } else {
            baseResp.setSuccess(0);
            baseResp.setErrorMsg("获取失败");
        }
        return baseResp;
    }

    //个人主页细节数据
    @Override
    public BaseResp perInforDetailData(Integer userId, String type, Integer pageNum) throws Exception {
        Integer pageSize = 4;
        PageHelper.startPage(pageNum, pageSize);
        BaseResp baseResp = new BaseResp();
        if (type.equals("blog")) {
            List<Blog> blogList = userMapper.selectAllBlogByUserid(userId);
            Page<Blog> blogPage = (Page<Blog>) blogList;
            baseResp.setSuccess(1);
            baseResp.setErrorMsg("获取博客列表成功");
            baseResp.setData(blogList);
            baseResp.setCount(blogPage.getPages());
        } else if (type.equals("upload")) {
            List<Upload> uploadList = userMapper.selectAllUploadByUserid(userId);
            Page<Upload> blogPage = (Page<Upload>) uploadList;
            baseResp.setSuccess(1);
            baseResp.setErrorMsg("获取资源列表成功");
            baseResp.setData(uploadList);
            baseResp.setCount(blogPage.getPages());
        } else if (type.equals("forum")) {
            List<Forum> forumList = userMapper.selectAllForumByUserid(userId);
            Page<Forum> blogPage = (Page<Forum>) forumList;
            baseResp.setSuccess(1);
            baseResp.setErrorMsg("获取论坛列表成功");
            baseResp.setData(forumList);
            baseResp.setCount(blogPage.getPages());
        } else if (type.equals("ask")) {
            List<Ask> askList = userMapper.selectAllAskByUserid(userId);
            Page<Ask> blogPage = (Page<Ask>) askList;
            baseResp.setSuccess(1);
            baseResp.setErrorMsg("获取问答列表成功");
            baseResp.setData(askList);
            baseResp.setCount(blogPage.getPages());
        } else {
            baseResp.setSuccess(0);
            baseResp.setErrorMsg("无效的请求");
        }
        return baseResp;
    }

    //修改账号
    @Override
    @Transactional(propagation = Propagation.REQUIRED)
    public BaseResp midifyUserByUserId(Integer userId, String userpassword, String username) throws Exception {
        User user = new User();
        BaseResp baseResp = new BaseResp();
        user.setUserId(userId);
        user.setUsername(username);
        user.setUserpassword(userpassword);
        List<User> userList = userMapper.SelectAllUser();
        List<String> usernameList = new ArrayList<>();
        for (User user1 : userList) {
            if (!user1.getUserId().equals(userId)) {
                usernameList.add(user1.getUsername());
            }
        }
        if (usernameList.contains(username)) {
            baseResp.setSuccess(0);
            baseResp.setErrorMsg("用户名已存在");
        } else {
            int result = userMapper.updateuser(user);
            if (result > 0) {
                baseResp.setSuccess(1);
                baseResp.setErrorMsg("修改成功");
            } else {
                baseResp.setSuccess(0);
                baseResp.setErrorMsg("修改失败");
            }

        }
        return baseResp;
    }


    //下方陈

    @Override
    @Transactional(isolation = Isolation.DEFAULT, propagation = Propagation.REQUIRED)
    public Integer plusUserMoney(Double downloadmoney, Integer userId) throws CsdnExpection {
        return null;
    }

    @Override
    @Transactional(isolation = Isolation.DEFAULT, propagation = Propagation.REQUIRED)
    public Integer minuUserMoney(Double downloadmoney, Integer userId) throws CsdnExpection {
        return null;
    }

    //    下载操作的捆绑事件
    @Override
    @Transactional(isolation = Isolation.DEFAULT, propagation = Propagation.REQUIRED)
    public Integer downloadMoney(Double downloadmoney, Integer userId, Integer upLoadId, Integer id) throws CsdnExpection {
        if (upLoadId != userId) {
            User user1 = userMapper.selectUserByUserId(userId);
            User user2 = userMapper.selectUserByUserId(upLoadId);
            if (user1 == null || user2 == null) {
                throw new CsdnExpection("用户不存在");
            }
            if (user1.getDownloadmoney() < downloadmoney) {
                throw new CsdnExpection("积分不足");
            }
            Double money1 = user1.getDownloadmoney() - downloadmoney;
            Double money2 = user2.getDownloadmoney() + downloadmoney;
            Integer count = userMapper.updateUserMoney(money1, userId);
            Integer count2 = userMapper.updateUserMoney(money2, upLoadId);
//资源下载记录
            Upload upload = uploadMapper.selectById(id);
            if (upload == null) {
                throw new CsdnExpection("资源不存在");
            }
            Download download = new Download();
            download.setLeixin(upload.getLeixin());
            download.setLeixin2(upload.getLeixin2());
            download.setTitle(upload.getTitle());
            download.setUserid(userId);
            download.setPrice(upload.getPrice());
            download.setSize(upload.getSize());
            download.setDowid(id);
            downloadMapper.insert(download);
////        积分流向一·
            Integrals integrals = new Integrals();
            Integrals integrals1 = new Integrals();
            integrals.setPrice("-" + upload.getPrice());
            integrals.setTitle(upload.getTitle());
            integrals.setUserid(userId);
            integralsMapper.insert(integrals);

////        积分流向二
            integrals1.setPrice("+" + upload.getPrice());
            integrals1.setTitle(upload.getTitle());
            integrals1.setUserid(upLoadId);
            integralsMapper.insert(integrals1);
////        用户下载量更新
            Integer downCount = user1.getDownCount();
            userMapper.updatedownCount((downCount + 1), userId);
            return count2;
        } else {
            return 1;
        }


    }

    @RedisCache(Constants.REPLAY_INFORMATION)
    @Override
    @Transactional(isolation = Isolation.DEFAULT, propagation = Propagation.REQUIRED)
    public void readcommentreq(Integer userId) {
        informationMapper.readcommentreq(userId);
    }

    @RedisCache(Constants.LIKE_INFORMATION)
    @Override
    @Transactional(isolation = Isolation.DEFAULT, propagation = Propagation.REQUIRED)
    public void readqueryLikeId(Integer userId) {
        userMapper.readqueryLikeId(userId);
        List<Integer> list = blogMapper.queryBlogIdByUserId(userId);
        likeMapper.readqueryLikeId(list);
    }

    @RedisCache(Constants.FANS_INFORMATION)
    @Override
    @Transactional(isolation = Isolation.DEFAULT, propagation = Propagation.REQUIRED)
    public void readfansaa(Integer userId) {
        userMapper.removefansaa(userId);
        fansMapper.readfansaa(userId);
    }


}
