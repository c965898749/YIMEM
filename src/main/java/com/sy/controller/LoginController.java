package com.sy.controller;

import com.sy.model.SysLogininfor;
import com.sy.model.User;
import com.sy.model.resp.BaseResp;
import com.sy.service.ISysLogininforService;
import com.sy.service.MenuService;

import com.sy.service.UserServic;
import com.sy.tool.*;
import eu.bitwalker.useragentutils.UserAgent;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;
import sun.misc.MessageUtils;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;


@Controller
public class LoginController {

    //private Logger logger = Logger.getLogger(LoginController.class);

    @Autowired
    private UserServic userService;
    @Autowired
    private MenuService menuService;

    //2.织入公告service
    //3.织入资讯service


    /**
     * 跳转到登录页面
     *
     * @return
     */
//    @RequestMapping("/admin.html")
//    public String toIndex(){
//
//        return "index";
//    }
    @RequestMapping("/kk")
    public String kk() {

        return "zf";
    }

    /**
     * 跳转到主页
     *
     * @return
     */
    @RequestMapping("/main.html")
    public String toMain() {
        return "main";
    }

    /**
     * 用户登录
     *
     * @return
     */
    @RequestMapping("/login.do")
    @ResponseBody
    public String doLogin(User user, HttpSession session) {

        try {
            User currentUser = userService.getLoginUser(user);
            if (null != currentUser) {
                //跳转到main.jsp
                //把List<Menu>转化为json,前端通过JS解析该数据
                String menus = menuService.makeMenus(currentUser.getRoleId());
                session.setAttribute("menus", menus);
                session.setAttribute(Constants.SESSION_USER, currentUser);
                return Constants.LOGIN_SUCCESS;
            }
        } catch (Exception e) {
            e.printStackTrace();
            //写入日志文件
            //logger.error("用户登录异常", e);
        }
        return Constants.LOGIN_FAILED;

    }

    @RequestMapping(value = "syslogininfor", method = RequestMethod.POST)
    @ResponseBody
    public void syslogininfor(String cip,String name,String msg,HttpServletRequest request) {
        User user=(User) request.getSession().getAttribute("user");
//        String ip = IpUtils.getIpAddr(ServletUtils.getRequest());
//        String ip = IpUtils.getIpAddr(request);
        UserAgent userAgent = UserAgent.parseUserAgentString(ServletUtils.getRequest().getHeader("User-Agent"));
        String address = AddressUtils.getRealAddressByIP(cip);
//        StringBuilder s = new StringBuilder();
//                s.append(LogUtils.getBlock(ip));
//                s.append(address);
//                s.append(LogUtils.getBlock(username));
//                s.append(LogUtils.getBlock(status));
//                s.append(LogUtils.getBlock(message));
//                // 打印信息到日志
//                sys_user_logger.info(s.toString(), args);
        // 获取客户端操作系统
        String os = userAgent.getOperatingSystem().getName();
        // 获取客户端浏览器
        String browser = userAgent.getBrowser().getName();
        // 封装对象
        SysLogininfor logininfor = new SysLogininfor();
        if (user == null) {
            logininfor.setUserName("");
            logininfor.setStatus(Constants.FAIL);
        } else {
            logininfor.setUserName(user.getUsername());
            logininfor.setStatus(Constants.SUCCESS);

        }
        logininfor.setIpaddr(cip);
        logininfor.setLoginLocation(address);
//        if (Xtool.isNull(address)){
//            logininfor.setLoginLocation(name);
//        }else {
//            logininfor.setLoginLocation(address);
//        }
        logininfor.setBrowser(browser);
        logininfor.setOs(os);
        logininfor.setMsg("访问 "+msg+" 页面");
        // 插入数据
        SpringUtils.getBean(ISysLogininforService.class).insertLogininfor(logininfor);
    }

    @RequestMapping(value = "isLogin", method = RequestMethod.POST)
    @ResponseBody
    public BaseResp isLogin(HttpServletResponse response, HttpServletRequest request) {

        BaseResp baseResp = new BaseResp();
        baseResp.setErrorMsg("你好");
        User user = (User) request.getSession().getAttribute(Constants.SESSION_USER);
//        String menus=(String) request.getSession().getAttribute("menus");
        if (user == null) {
            baseResp.setSuccess(0);
            baseResp.setErrorMsg("未登入");
            return baseResp;
        } else {
            baseResp.setSuccess(1);
            baseResp.setErrorMsg("已登入");
            try {
                String menus = menuService.makeMenus(user.getRoleId());
                System.out.println(menus);
//                实时更新用户
                User user1 = userService.getLoginUser(user);
                user1.setMenus(menus);
                baseResp.setData(user1);
            } catch (Exception e) {
                e.printStackTrace();
                baseResp.setData(user);
            }
            return baseResp;
        }
    }

    @RequestMapping(value = "/logout.html")
    public String logout(HttpServletRequest request) {
        request.getSession().invalidate();
        return "index";
    }

//    /**
//     * 用户微信绑定
//     */
//    @RequestMapping(value = "bangding",method = RequestMethod.POST)
//    @ResponseBody
//    public BaseResp bangding(HttpServletResponse response, HttpServletRequest request){
//
//    }
}
