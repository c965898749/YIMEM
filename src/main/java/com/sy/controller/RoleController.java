package com.sy.controller;

//import com.sy.model.common.Menu;

import com.sy.model.User;
import com.sy.model.resp.BaseResp;
import com.sy.service.MenuService;
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

//import com.sy.service.common.UserService;

@Controller
public class RoleController {

    @RequestMapping("/backend/{name}")
    public String pages(@PathVariable("name") String name) {
       System.out.println(name);
//        int count = name.indexOf(".");
//        String path = name.substring(0, count);
//        System.out.println(path);
        return "/pages/backend/"+name;
    }


}
