package com.sy.controller;

import cn.hutool.core.util.IdUtil;
import cn.hutool.json.JSONUtil;
import com.sy.mapper.EmilMapper;
import com.sy.mapper.UserMapper;
import com.sy.model.Download;
import com.sy.model.Emil;
import com.sy.model.User;
import com.sy.model.resp.BaseResp;
import com.sy.model.resp.ResultVO;
import com.sy.model.weixin.WeiXinUser;
import com.sy.service.DownloadService;
import com.sy.service.EmailService;
import com.sy.service.UserServic;
import com.sy.service.WeixinPostService;
//import com.sy.tool.DESUtil;
import com.sy.tool.CookieUitl;
import com.sy.tool.Xtool;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.ValueOperations;
import org.springframework.util.DigestUtils;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.net.URLEncoder;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.concurrent.TimeUnit;
import java.util.logging.XMLFormatter;

@RestController
public class UserController {
    @Autowired
    UserServic servic;
    @Autowired
    private EmilMapper emilMapper;
    @Autowired
    private EmailService emailService;
    private Logger log = Logger.getLogger(UserController.class.getName());
    @Autowired

    private DownloadService downloadService;
    @Autowired
    private WeixinPostService weixinPostService;

    @Autowired
    private UserMapper userMapper;
    @Autowired
    public RedisTemplate redisTemplate;

    //登录接口
    @RequestMapping(value = "loginVerification", method = RequestMethod.POST)
    public BaseResp loginVerification(String username, String userpassword, HttpServletRequest request) {
        BaseResp baseResp = new BaseResp();
        try {
            Date day = new Date();
            SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
            baseResp = servic.loginVerification(username, userpassword,request);
            if (baseResp.getSuccess() == 1) {
                User user = (User) baseResp.getData();
                if (Xtool.isNotNull(user.getOpenid())) {
                    weixinPostService.sendTemplate(user.getOpenid(), user.getUsername(), df.format(day));
                }
            }
            return baseResp;
        } catch (Exception e) {
            e.printStackTrace();
            baseResp.setSuccess(0);
            return baseResp;
        }
    }



    //发送邮箱验证码
    @RequestMapping(value = "emilcode", method = RequestMethod.POST)
    public BaseResp emilcode(String username, HttpServletRequest request) {
        Date emilcodetime = (Date) request.getSession().getAttribute("emilcodetime");
        BaseResp baseResp = new BaseResp();
        if (emilcodetime == null) {
            Date date = new Date();
            request.getSession().setAttribute("emilcodetime", date);
        } else {
            Date date = new Date();
            if ((date.getTime() - emilcodetime.getTime()) < 60) {
                baseResp.setSuccess(0);
                baseResp.setErrorMsg((date.getTime() - emilcodetime.getTime()) + "秒后重新发送邮件");
                return baseResp;
            }
        }
//        System.out.println("发送");
        User user = new User();
        user.setUsername(username);
        try {
            User tt = servic.getUserByLoginCode(user);
            if ("0".equals(user.getIsEmil())) {
                baseResp.setSuccess(0);
                baseResp.setErrorMsg("该账号未绑定邮箱");
                return baseResp;
            }
            Emil emil = emilMapper.selectByPrimaryKey(tt.getUserId());
            emailService.emailManage(emil.getEmil(), user, request);
            baseResp.setSuccess(1);
            return baseResp;
        } catch (Exception e) {
            e.printStackTrace();
            baseResp.setSuccess(0);
            return baseResp;
        }
    }

    //修改密码
    //修改账户
    @RequestMapping(value = "midifyUser", method = RequestMethod.POST)
    public BaseResp midifyUser(String emilcode, String userpassword, String username, HttpServletRequest request) {
        String idcode = (String) request.getSession().getAttribute("idcode");
        BaseResp baseResp = new BaseResp();
        if (Xtool.isNull(emilcode) || Xtool.isNull(userpassword) || Xtool.isNull(username) || Xtool.isNull(idcode)) {
            baseResp.setSuccess(0);
            baseResp.setErrorMsg("信息不全请重新刷新输入");
            return baseResp;
        }
        if (!idcode.equals(emilcode)) {
            baseResp.setSuccess(0);
            baseResp.setErrorMsg("验证码不正确");
            return baseResp;
        }
//        System.out.println(idcode);
//        System.out.println(emilcode);
        try {
            User user = new User();
            user.setUsername(username);
            User tt = servic.getUserByLoginCode(user);
            String password = DigestUtils.md5DigestAsHex(userpassword.getBytes());
            baseResp = servic.midifyUserByUserId(tt.getUserId(), password, username);
        } catch (Exception e) {
            e.printStackTrace();
            baseResp.setSuccess(0);
            baseResp.setErrorMsg("服务器异常");
        }
        return baseResp;
    }

    //注册接口
    @RequestMapping(value = "registerServlet", method = RequestMethod.POST)
    public BaseResp registerServlet(String username, String userpassword, HttpServletRequest request) {
        BaseResp baseResp = new BaseResp();
        try {
            baseResp = servic.addUser(username, userpassword);
            return baseResp;
        } catch (Exception e) {
            e.printStackTrace();
            baseResp.setSuccess(0);
            return baseResp;
        }
    }

    /**
     * 微信注册接口
     *
     * @param
     * @return
     */
    @RequestMapping(value = "weixinRegist", method = RequestMethod.POST)
    public BaseResp weixinRegist(String username, String userpassword, HttpServletRequest request) {
        BaseResp baseResp = new BaseResp();
        User user = new User();
        user.setUsername(username);
        user.setUserpassword(userpassword);
        List<User> userList = userMapper.SelectAllUser();
        List<String> usernamelist = new ArrayList<>();
        WeiXinUser weiXinUser = (WeiXinUser) request.getSession().getAttribute("weiXinUser");
        if (weiXinUser == null) {
            baseResp.setSuccess(0);
            baseResp.setErrorMsg("暂未授权该公众号！");
            return baseResp;
        }
        User xx = servic.getUserByopenid(weiXinUser.getOpenid());
        if (xx != null) {
            baseResp.setSuccess(0);
            baseResp.setErrorMsg("该微信号已绑定过公众号！");
            return baseResp;
        }
        for (User user1 : userList) {
            usernamelist.add(user1.getUsername());
        }
        if (usernamelist.contains(username)) {
            baseResp.setSuccess(0);
            baseResp.setErrorMsg("您输入的账号已存在，请重新输入");
            return baseResp;
        }
        user.setNickname(weiXinUser.getNickname());
        user.setSex(weiXinUser.getSex());
        user.setHeadImg(weiXinUser.getHeadimgurl());
        user.setCity(weiXinUser.getCity());
        user.setProvinces(weiXinUser.getProvince());
        user.setOpenid(weiXinUser.getOpenid());
        user.setDownloadmoney((double) 0);
        user.setRanking(9999);
        user.setLevel(2);
        user.setCollectCount(0);
        user.setBlogCount(0);
        user.setAttentionCount(0);
        user.setFansCount(0);
        user.setResourceCount(0);
        user.setForumCount(0);
        user.setAskCount(0);
        user.setCommentCount(0);
        user.setLikeCount(0);
        user.setVisitorCount(0);
        user.setDownCount(0);
        user.setUnreadreplaycount(0);
        user.setReadquerylikecount(0);
        user.setUnreadfanscount(0);
        user.setIsEmil("0");
        int result = userMapper.insertUser(user);
        if (result > 0) {
            baseResp.setSuccess(1);
            baseResp.setErrorMsg("注册成功！");
            return baseResp;
        } else {
            baseResp.setSuccess(0);
            baseResp.setErrorMsg("注册失败！");
            return baseResp;
        }
    }

    //判断是否登入的接口
    @RequestMapping(value = "isEnter")
    public BaseResp isEnter(HttpServletRequest request) throws Exception {
        BaseResp baseResp = new BaseResp();
        User user=servic.getUserByRedis(request);
        if (user!=null){
            baseResp.setSuccess(1);
            baseResp.setData(user);
            return baseResp;
        }
        baseResp.setSuccess(0);
        baseResp.setErrorMsg("未登入");
        return baseResp;
    }

    //注销登入
    @RequestMapping(value = "logout")
    public BaseResp logout(HttpServletRequest request) {
        String token= CookieUitl.getToken(request);
        if (Xtool.isNotNull(token)&&redisTemplate.hasKey(token)){
            redisTemplate.delete(token);
        }
        BaseResp baseResp = new BaseResp();
        baseResp.setSuccess(1);
        baseResp.setErrorMsg("注销成功");
        return baseResp;

    }

    //修改用户头像
    @RequestMapping(value = "modifyHeadImg", method = RequestMethod.POST)
    public BaseResp modifyHeadImg(String headImg, HttpServletRequest request) {
        BaseResp baseResp = new BaseResp();
        try {
            User user=servic.getUserByRedis(request);
            if (user!=null){
                baseResp = servic.modifyHeadImgByUserid(user.getUserId(), headImg,request);
                return baseResp;
            }

        } catch (Exception e) {
            e.printStackTrace();
            baseResp.setSuccess(0);
            baseResp.setErrorMsg("服务器异常");
            return baseResp;
        }
        baseResp.setSuccess(0);
        baseResp.setErrorMsg("未登入");
        return baseResp;
    }

    //修改用户信息
    @RequestMapping(value = "modifyUserInfor", method = RequestMethod.POST)
    public BaseResp modifyUserInfor(User user, HttpServletRequest request) {
        BaseResp baseResp = new BaseResp();
        try {
            User user2=servic.getUserByRedis(request);
            if (user!=null){
                user.setUserId(user2.getUserId());
                baseResp = servic.modifyUserInfor(user);
                return baseResp;
            }
        } catch (Exception e) {
            e.printStackTrace();
            baseResp.setSuccess(0);
            baseResp.setErrorMsg("服务器异常");
            return baseResp;
        }
        baseResp.setSuccess(0);
        baseResp.setErrorMsg("未登入");
        return baseResp;
    }

    //个人资料渲染接口
    @RequestMapping(value = "personalData", method = RequestMethod.GET)
    public BaseResp personalData(Integer userId) {
        BaseResp baseResp = new BaseResp();
        try {
            baseResp = servic.findUserInforIncludeMsg(userId);
        } catch (Exception e) {
            e.printStackTrace();
            baseResp.setSuccess(0);
            baseResp.setErrorMsg("服务器异常");
        }
        return baseResp;
    }

    //根据userId查询粉丝
    @RequestMapping(value = "myFans")
    public BaseResp findAllFansByUserid(Integer userId) {
        BaseResp baseResp = new BaseResp();
        try {
            baseResp = servic.findAllFansByUserid(userId);
        } catch (Exception e) {
            e.printStackTrace();
            baseResp.setSuccess(0);
            baseResp.setErrorMsg("服务器异常");
        }
        return baseResp;
    }

    //根据userId查询关注的人
    @RequestMapping(value = "myInterest")
    public BaseResp findAllreFansByUserId(Integer userId) {
        BaseResp baseResp = new BaseResp();
        try {
            baseResp = servic.findAllreFansByUserId(userId);
        } catch (Exception e) {
            e.printStackTrace();
            baseResp.setSuccess(0);
            baseResp.setErrorMsg("服务器异常");
        }
        return baseResp;
    }

    //个人主页渲染数据
    @RequestMapping(value = "perInfordata")
    public BaseResp perInfordata(Integer viewUserId, Integer userId) {
        BaseResp baseResp = new BaseResp();
        try {
            baseResp = servic.perInfordata(viewUserId, userId);
        } catch (Exception e) {
            e.printStackTrace();
            baseResp.setSuccess(0);
            baseResp.setErrorMsg("服务器异常");
        }
        return baseResp;
    }

    //个人主页细节数据
    @RequestMapping(value = "perInforDetailData")
    public BaseResp perInforDetailData(Integer userId, String type, Integer pageNum) {
        BaseResp baseResp = new BaseResp();
        try {

            baseResp = servic.perInforDetailData(userId, type, pageNum);
        } catch (Exception e) {
            e.printStackTrace();
            baseResp.setSuccess(0);
            baseResp.setErrorMsg("服务器异常");
        }
        return baseResp;
    }


    //已读回复
    @RequestMapping(value = "readcommentreq", method = RequestMethod.POST)
    public BaseResp readcommentreq(HttpServletRequest request) throws Exception {
        BaseResp baseResp = new BaseResp();
        User user=servic.getUserByRedis(request);
        if (user!=null){
            servic.readcommentreq(user.getUserId());
            servic.updateUserRedis(user.getUserId(),request);
        }
        baseResp.setSuccess(1);
        baseResp.setErrorMsg("已读回复");
        return baseResp;
    }

    //已读点赞
    @RequestMapping(value = "readqueryLikeId", method = RequestMethod.POST)
    public BaseResp readqueryLikeId(HttpServletRequest request) throws Exception {
        BaseResp baseResp = new BaseResp();
        User user=servic.getUserByRedis(request);
        if (user!=null){
            servic.readqueryLikeId(user.getUserId());
            servic.updateUserRedis(user.getUserId(),request);
        }
        baseResp.setSuccess(1);
        baseResp.setErrorMsg("已读点赞");
        return baseResp;
    }

    //已读关注
    @RequestMapping(value = "readfansaa", method = RequestMethod.POST)
    public BaseResp readfansaa(HttpServletRequest request) throws Exception {
        BaseResp baseResp = new BaseResp();
        User user=servic.getUserByRedis(request);
        if (user!=null){
            servic.readfansaa(user.getUserId());
            servic.updateUserRedis(user.getUserId(),request);
        }
        baseResp.setSuccess(1);
        baseResp.setErrorMsg("已读点赞");
        return baseResp;
    }

    @RequestMapping(value = "downloadResourceMixi", method = RequestMethod.GET)
    public BaseResp downloadResourceMixi(Integer userid, Integer page, Integer pageSize, HttpServletResponse res) {
        BaseResp resp = new BaseResp();
        List<Download> lists = downloadService.findByUserid(userid, (page - 1) * pageSize, pageSize);
        Integer count = downloadService.findAllCount(userid);
        if (lists != null) {
            resp.setSuccess(200);
            res.setStatus(200);
            resp.setData(lists);
            resp.setCount(count);
            return resp;
        } else {
            res.setStatus(400);
            resp.setSuccess(400);
            resp.setErrorMsg("资源为找到");
            return resp;
        }
    }


    @RequestMapping(value = "isnormal")
    public void isnormal(HttpServletResponse res) {
        res.setStatus(200);
    }

}
