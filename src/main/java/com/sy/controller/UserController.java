package com.sy.controller;

import com.sy.mapper.EmilMapper;
import com.sy.model.Download;
import com.sy.model.Emil;
import com.sy.model.User;
import com.sy.model.resp.BaseResp;
import com.sy.model.resp.ResultVO;
import com.sy.service.DownloadService;
import com.sy.service.EmailService;
import com.sy.service.UserServic;
import com.sy.tool.DESUtil;
import com.sy.tool.Xtool;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.logging.XMLFormatter;

@RestController
public class UserController {
    @Autowired
    UserServic servic;
    @Autowired
    private EmilMapper emilMapper;
    @Autowired
    private EmailService emailService;

    BaseResp baseResp = new BaseResp();
    @Autowired
    private DownloadService downloadService;

    //登录接口
    @RequestMapping(value = "loginVerification", method = RequestMethod.POST)
    public BaseResp loginVerification(String username, String userpassword, HttpServletRequest request) {
        try {
            baseResp = servic.loginVerification(username, userpassword);
            if (baseResp.getSuccess() == 1) {
                Date date = new Date();
                //时间+账号+密码进行DES加密
                String token = DESUtil.getEncryptString(date + ";" + username + ";" + userpassword);
                baseResp.setErrorMsg(token);
//                System.out.println("生成token:"+token);
                request.getSession().setAttribute("user", baseResp.getData());
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
        System.out.println("发送");
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
        System.out.println(idcode);
        System.out.println(emilcode);
        try {
            User user = new User();
            user.setUsername(username);
            User tt = servic.getUserByLoginCode(user);
            baseResp = servic.midifyUserByUserId(tt.getUserId(), userpassword, username);
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
        try {
            baseResp = servic.addUser(username, userpassword);
            return baseResp;
        } catch (Exception e) {
            e.printStackTrace();
            baseResp.setSuccess(0);
            return baseResp;
        }
    }

    //判断是否登入的接口
    @RequestMapping(value = "isEnter")
    public BaseResp isEnter(HttpServletRequest request) {
        Cookie[] cookies = request.getCookies();
        String token = "";
        if (cookies != null && cookies.length > 0) {
            for (Cookie cookie : cookies) {
                switch (cookie.getName()) {
                    case "token":
                        token = cookie.getValue();
                        break;
                    default:
                        break;
                }
            }
        }
        User user = (User) request.getSession().getAttribute("user");
        if (user == null) {
            if (Xtool.isNotNull(token)) {
                String key=DESUtil.getDecryptString(token);
                String[] str = key.split(";");
//                SimpleDateFormat df=new SimpleDateFormat("yyyy-MM-dd");
//                try {
                    Date ss=new Date(str[0]);
//                    Date sdate=df.parse(ss);
                    Date edate=new Date();
                    long betweendays=(long) ((edate.getTime()-ss.getTime())/(1000 * 60 * 60 *24)+0.5);//天数间隔
                    System.out.println(betweendays);
                    if (betweendays>30){
                        baseResp.setSuccess(0);
                        baseResp.setErrorMsg("token已过期");
                    }
                    try {
                        baseResp = servic.loginVerification(str[1], str[2]);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
//                } catch (ParseException e) {
//                    e.printStackTrace();
//                }
            }else {
                baseResp.setSuccess(0);
                baseResp.setErrorMsg("未登入");
            }
            return baseResp;
        } else {
            baseResp.setSuccess(1);
            baseResp.setErrorMsg("已登入");
            try {
                Integer userId = user.getUserId();
                baseResp.setData(servic.findUserByUserId(userId).getData());
            } catch (Exception e) {
                e.printStackTrace();
                baseResp.setData(user);
            }
            return baseResp;
        }
    }

    //注销登入
    @RequestMapping(value = "logout")
    public BaseResp logout(HttpServletRequest request) {
        request.getSession().invalidate();
        baseResp.setSuccess(1);
        baseResp.setErrorMsg("注销成功");
        return baseResp;
    }

    //修改用户头像
    @RequestMapping(value = "modifyHeadImg", method = RequestMethod.POST)
    public BaseResp modifyHeadImg(String headImg, HttpServletRequest request) {
        User user = (User) request.getSession().getAttribute("user");
        if (user == null) {
            baseResp.setSuccess(0);
            baseResp.setErrorMsg("未登入");
            return baseResp;
        } else {
            try {
                baseResp = servic.modifyHeadImgByUserid(user.getUserId(), headImg);
                return baseResp;
            } catch (Exception e) {
                e.printStackTrace();
                baseResp.setSuccess(0);
                baseResp.setErrorMsg("服务器异常");
                return baseResp;
            }
        }
    }

    //修改用户信息
    @RequestMapping(value = "modifyUserInfor", method = RequestMethod.POST)
    public BaseResp modifyUserInfor(User user, HttpServletRequest request) {
        User user2 = (User) request.getSession().getAttribute("user");
        if (user2 == null) {
            baseResp.setSuccess(0);
            baseResp.setErrorMsg("未登入");
            return baseResp;
        } else {
            try {
                user.setUserId(user2.getUserId());
                baseResp = servic.modifyUserInfor(user);
//                System.out.println(baseResp);
            } catch (Exception e) {
                e.printStackTrace();
                baseResp.setSuccess(0);
                baseResp.setErrorMsg("服务器异常");
            }
            return baseResp;
        }


    }

    //个人资料渲染接口
    @RequestMapping(value = "personalData", method = RequestMethod.GET)
    public BaseResp personalData(Integer userId) {
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
    public BaseResp readcommentreq(HttpServletRequest request) {
        User user = (User) request.getSession().getAttribute("user");
        if (user != null) {
            Integer userId = user.getUserId();
            servic.readcommentreq(userId);
        }
        baseResp.setSuccess(1);
        baseResp.setErrorMsg("已读回复");
        return baseResp;
    }

    //已读点赞
    @RequestMapping(value = "readqueryLikeId", method = RequestMethod.POST)
    public BaseResp readqueryLikeId(HttpServletRequest request) {
        User user = (User) request.getSession().getAttribute("user");
        if (user != null) {
            Integer userId = user.getUserId();
            servic.readqueryLikeId(userId);
        }
        baseResp.setSuccess(1);
        baseResp.setErrorMsg("已读点赞");
        return baseResp;
    }

    //已读关注
    @RequestMapping(value = "readfansaa", method = RequestMethod.POST)
    public BaseResp readfansaa(HttpServletRequest request) {
        User user = (User) request.getSession().getAttribute("user");
        if (user != null) {
            Integer userId = user.getUserId();
            servic.readfansaa(userId);
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
}
