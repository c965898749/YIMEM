package com.sy.service;


import com.alibaba.fastjson.JSON;
import com.sy.model.Authority;
import com.sy.model.Function;
import com.sy.model.Menu;
import com.sy.model.Role;
import com.sy.tool.Constants;
//import com.sy.tools.RedisUtil.getJedisInstance();
import com.sy.tool.RedisUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.management.Query;
import java.util.ArrayList;
import java.util.List;

@Service
public class MenuService {


    @Autowired
    private UserServic userService;

    @Autowired
    private FunctionService functionService;



    /**
     * 根据登录用户的roleId返回可以访问菜单(主菜单和子菜单)
     *
     * 把List<Menu>放入redis中,缓存左菜单栏
     * @param roleId
     * @return
     */
    public String makeMenus(int roleId) throws Exception{
        String key = Constants.MENUS +roleId;
        String json = null;
        if(RedisUtil.getJedisInstance().exists(key)){
            json = RedisUtil.getJedisInstance().get(key);
        }else{
            List<Menu> menus = new ArrayList<>();
            //1.生成主菜单和子菜单
            Authority authority = new Authority();
            authority.setRoleId(roleId);
            List<Function> mainFunctions = functionService.getMainFunctionList(authority);
            for(Function mainFun:mainFunctions){
                //2.查询每个主菜单对应的子菜单
                mainFun.setRoleId(roleId);
                List<Function> subFunctions = functionService.getSubFunctionList(mainFun);
                Menu menu = new Menu();
                menu.setMainFunction(mainFun);
                menu.setSubsFunction(subFunctions);
                menus.add(menu);
            }

            //3.转化为JSON
            json = JSON.toJSONString(menus);
            //4.放入缓存
            RedisUtil.getJedisInstance().set(key, json);

            //5.调用创建可访问的所有URL
            this.makeFunctions(roleId);
        }

        return json;

    }

    /**
     * 根据登录用户的roleId返回可以访问其他功能Function
     * 放入redis中
     * 把List<Functions>放入redis中
     * @param roleId
     * @return
     */
   public void makeFunctions(int roleId) throws Exception{

       Authority authority  = new Authority();
       authority.setRoleId(roleId);
       List<Function> allowedFunctions=functionService.getFunctionListByRoId(authority);
       StringBuffer stringBuffer = new StringBuffer();
       for(Function fun:allowedFunctions){
            stringBuffer.append(fun.getFuncUrl());
       }
       String allowedUrls = stringBuffer.toString();
       String key = Constants.FUNURLS+roleId;
       RedisUtil.getJedisInstance().set(key, allowedUrls);
   }

}
