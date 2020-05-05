package com.sy.controller;

//import com.sy.model.common.Menu;

import com.sy.model.Role;
import com.sy.model.User;
import com.sy.model.resp.BaseResp;
import com.sy.model.resp.Page;
import com.sy.service.MenuService;
import com.sy.service.RoleService;
import com.sy.service.UserServic;
import com.sy.tool.Constants;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.util.List;

//import com.sy.service.common.UserService;
@Controller
public class RoleController {

    @Autowired
    private UserServic userServic;
    @Autowired
    private RoleService roleService;

    /**
     * 用户管理
     * @param request
     * @return
     * @throws Exception
     */
    @RequestMapping("/backend/{name}")
    public String userlist(@PathVariable("name")String  name,HttpServletRequest request) throws Exception {
        System.out.println(name);
        List<Role> roleList = roleService.getRoleList();
        request.setAttribute("roleList", roleList);
        Page page = new Page();
        List<User> users = userServic.getUserList(new User());
        page.setItems(users);
        request.setAttribute("page", page);
        return "/pages/backend/"+name;
    }

    @RequestMapping("/backend/getuser")
    @ResponseBody
    public BaseResp getuser(Integer id,HttpServletRequest request) throws Exception {
        User user=new User();
        BaseResp baseResp=new BaseResp();
        user.setUserId(id);
        User user2=userServic.getUserById(user);
         if (user2!=null){
             baseResp.setSuccess(0);
             baseResp.setData(user2);
         }else {
             baseResp.setSuccess(1);
             baseResp.setErrorMsg("未找到用户");
         }
        return baseResp;
    }
//
//    @RequestMapping("/backend/rolelist")
//    public String rolelist(HttpServletRequest request) throws Exception {
//        Page page = new Page();
//        List<User> users = userServic.getUserList(new User());
//        page.setItems(users);
//        request.setAttribute("page", page);
//        return "/pages/backend/rolelist";
//    }
//    @RequestMapping("/backend/authoritymanage")
//    public String authoritymanage(HttpServletRequest request) throws Exception {
//        return "/pages/backend/authoritymanage";
//    }
//    @RequestMapping("/backend/dicmanage")
//    public String dicmanage(HttpServletRequest request) throws Exception {
//        return "/pages/backend/dicmanage";
//    }




}
