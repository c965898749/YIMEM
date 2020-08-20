package com.sy.controller;

import com.sy.model.User;
import com.sy.model.resp.BaseResp;
import com.sy.service.MenuService;

import com.sy.service.UserServic;
import com.sy.tool.Constants;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

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
     * @return
     */
    @RequestMapping("/admin.html")
    public String toIndex(){

        return "index";
    }

    @RequestMapping("/kk")
    public String kk(){

        return "zf";
    }

    /**
     * 跳转到主页
     * @return
     */
    @RequestMapping("/main.html")
    public String toMain(){
        return "main";
    }

    /**
     * 用户登录
     * @return
     */
    @RequestMapping("/login.do")
    @ResponseBody
    public String doLogin(User user, HttpSession session){

        try {
            User currentUser = userService.getLoginUser(user);
            if(null!=currentUser){
                //跳转到main.jsp
                //把List<Menu>转化为json,前端通过JS解析该数据
                String menus= menuService.makeMenus(currentUser.getRoleId());
                session.setAttribute("menus", menus);
                session.setAttribute(Constants.SESSION_USER, currentUser);
                return Constants.LOGIN_SUCCESS;
            }
        }catch (Exception e){
            e.printStackTrace();
            //写入日志文件
            //logger.error("用户登录异常", e);
        }
        return Constants.LOGIN_FAILED;

    }
    @RequestMapping(value = "isLogin",method = RequestMethod.POST)
    @ResponseBody
    public BaseResp isLogin(HttpServletResponse response, HttpServletRequest request){

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
                String menus= menuService.makeMenus(user.getRoleId());
                System.out.println(menus);
//                实时更新用户
                User user1=userService.getLoginUser(user);
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
