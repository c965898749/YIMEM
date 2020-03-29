package com.sy.controller;

import com.sy.model.Download;
import com.sy.model.User;
import com.sy.model.resp.BaseResp;
import com.sy.service.DownloadService;
import com.sy.service.UserServic;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.List;

@RestController
public class UserController {
    @Autowired
    UserServic servic;
    BaseResp baseResp = new BaseResp();
    @Autowired
    private DownloadService downloadService;
    //登录接口
    @RequestMapping(value = "loginVerification", method = RequestMethod.POST)
    public BaseResp loginVerification(String username, String userpassword, HttpServletRequest request) {
        try {
            baseResp = servic.loginVerification(username, userpassword);
            if (baseResp.getSuccess() == 1) {
                request.getSession().setAttribute("user", baseResp.getData());
            }
            return baseResp;
        } catch (Exception e) {
            e.printStackTrace();
            baseResp.setSuccess(0);
            return baseResp;
        }
    }

    ;

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
        User user = (User) request.getSession().getAttribute("user");
        if (user == null) {
            baseResp.setSuccess(0);
            baseResp.setErrorMsg("未登入");
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
    public BaseResp modifyHeadImg(String headImg,HttpServletRequest request) {
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
    public BaseResp modifyUserInfor(User user) {
        try {
            baseResp = servic.modifyUserInfor(user);
        } catch (Exception e) {
            e.printStackTrace();
            baseResp.setSuccess(0);
            baseResp.setErrorMsg("服务器异常");
        }
        return baseResp;
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
    public BaseResp perInforDetailData(Integer userId, String type,Integer pageNum) {
        try {

            baseResp = servic.perInforDetailData(userId, type,pageNum);
        } catch (Exception e) {
            e.printStackTrace();
            baseResp.setSuccess(0);
            baseResp.setErrorMsg("服务器异常");
        }
        return baseResp;
    }

    //修改账户
    @RequestMapping(value = "midifyUser", method = RequestMethod.POST)
    public BaseResp midifyUser(Integer userId, String userpassword, String username) {
        try {
            baseResp = servic.midifyUserByUserId(userId, userpassword, username);
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

    @RequestMapping(value = "downloadResourceMixi",method =RequestMethod.GET)
    public BaseResp downloadResourceMixi(Integer userid,Integer page,Integer pageSize,HttpServletResponse res){
        BaseResp resp=new BaseResp();
        List<Download> lists=downloadService.findByUserid(userid,(page-1)*pageSize,pageSize);
        Integer count=downloadService.findAllCount(userid);
        if (lists!=null){
            resp.setSuccess(200);
            res.setStatus(200);
            resp.setData(lists);
            resp.setCount(count);
            return resp;
        }else {
            res.setStatus(400);
            resp.setSuccess(400);
            resp.setErrorMsg("资源为找到");
            return resp;
        }
    }
}
