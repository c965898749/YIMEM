package com.sy.controller;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.sy.expection.CsdnExpection;
import com.sy.model.*;
import com.sy.model.resp.BaseResp;
import com.sy.model.resp.ResultCode;
import com.sy.service.*;
import com.sy.tool.Constants;
import com.sy.tool.Xtool;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.io.FileUtils;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.*;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Controller
public class DownloadController {
    @Autowired
    private AppService appService;
    @Autowired
    private UploadService service;
    @Autowired
    private UserServic servic;
    @Autowired
    private DownloadService downloadService;
    @Autowired
    private BulletService bulletService;
    private Logger log = Logger.getLogger(DownloadController.class.getName());

//    @RequestMapping(value = "downloadResource", method = RequestMethod.GET)
//    public String download(Integer id,String host, HttpServletRequest request, HttpServletResponse response) throws IOException {
//
////        先验证用户登录
//        User user = (User) request.getSession().getAttribute("user");
//
//        if (user == null) {
//            return "redirect:login.html";
//        }
//
//        //        实时更新用户信息
//        User user1 = new User();
//        try {
//            Integer userId = user.getUserId();
//            user1 = (User) servic.findUserByUserId(userId).getData();
//        } catch (Exception e) {
//            e.printStackTrace();
//        }
//
////    再查看资源是否存在
//        String path = null;
//        Upload upload = new Upload();
//        try {
//            upload = service.findById(id);
//            if (upload == null) {
//                return "redirect:login.html";
//            }
//        } catch (CsdnExpection csdnExpection) {
//            csdnExpection.printStackTrace();
//            return "redirect:login.html";
//        }
////      判断积分是否充足
//        Integer uploadUsrid = upload.getUserid();
//        if (user1.getDownloadmoney() < upload.getPrice()) {
//            return "redirect:login.html";
//        }
////        积分操作
//        Integer count = 0;
//        try {
//            count = servic.downloadMoney(upload.getPrice(), user1.getUserId(), uploadUsrid, id);
//
//            if (count > 0) {
//                path = upload.getSrc();
//                log.info("请求域名"+host);
//                if (Xtool.isNotNull(host)){
//                    path=path.replace(Constants.IMAGE_SERVER_URL,"http://"+host+"/");
//                }
//                //记录资源次数
//                upload.setHot(upload.getHot() + 1);
//                service.updatahot(upload);
//                //        开始资源
//                return "redirect:" + path + "?attname=" + URLEncoder.encode(upload.getName(), "UTF-8");
//            }
//        } catch (CsdnExpection e) {
//            e.printStackTrace();
//            return "redirect:404.html";
//        }
//        return "redirect:404.html";
//    }


    @RequestMapping(value = "downloadResource", method = RequestMethod.GET)
    public String download(Integer id,String host, HttpServletRequest request, HttpServletResponse response) throws Exception {

//        先验证用户登录
        User user = servic.getUserByRedis(request);
        if (user == null) {
            return "redirect:login.html";
        }

        //        实时更新用户信息
        User user1 = new User();
        try {
            user1 = servic.getUserById(user);
        } catch (Exception e) {
            e.printStackTrace();
        }

//    再查看资源是否存在
        String path = null;
        Upload upload = new Upload();
        try {
            upload = service.findById(id);
            if (upload == null) {
                return "redirect:login.html";
            }
        } catch (CsdnExpection csdnExpection) {
            csdnExpection.printStackTrace();
            return "redirect:login.html";
        }
//      判断积分是否充足
        Integer uploadUsrid = upload.getUserid();
        if (user1.getDownloadmoney() < upload.getPrice()) {
            return "redirect:login.html";
        }
//        积分操作
        Integer count = 0;
        try {
            count = servic.downloadMoney(upload.getPrice(), user1.getUserId(), uploadUsrid, id);

            if (count > 0) {
//                path = upload.getSrc();
//                log.info("请求域名"+host);
//                if (Xtool.isNotNull(host)){
//                    path=path.replace(Constants.IMAGE_SERVER_URL,"http://"+host+"/");
//                }
                //记录资源次数
                upload.setHot(upload.getHot() + 1);
                service.updatahot(upload);
                //        开始资源
//                return "redirect:" + path + "?attname=" + URLEncoder.encode(upload.getName(), "UTF-8");
//                return "redirect:/common/static/" +upload.getSrc()+"/"+ URLEncoder.encode(upload.getName(), "UTF-8");
//                System.out.println(host+upload.getSrc());
//                return "redirect:http://"+host+upload.getSrc();
                return "redirect:"+upload.getSrc();
            }
        } catch (CsdnExpection e) {
            e.printStackTrace();
            return "redirect:404.html";
        }
        return "redirect:404.html";
    }



    @RequestMapping(value = "getAllapp", method = RequestMethod.GET)
    @ResponseBody
    public BaseResp getAll(){
        BaseResp baseResp=new BaseResp();
        try {
            List<App> list= appService.selectAll();
            if (Xtool.isNotNull(list)){
                baseResp.setSuccess(200);
                baseResp.setData(list);
                return baseResp;
            }else {
                baseResp.setSuccess(0);
                baseResp.setErrorMsg("为找到资源");
                return baseResp;
            }
        } catch (Exception e) {
            baseResp.setSuccess(0);
            baseResp.setErrorMsg("服务器异常");
           return baseResp;
        }

    }


    @RequestMapping(value = "YiMemapp", method = RequestMethod.GET)
    public String YiMemapp(Integer id)  throws IOException {
//    再查看资源是否存在
        String path = null;
        Upload upload = new Upload();
        try {
            upload = service.findById(id);
            path = upload.getSrc();
            //记录资源次数
            upload.setHot(upload.getHot() + 1);
            service.updatahot(upload);
            return "redirect:" + path + "?attname=" + URLEncoder.encode(upload.getName(), "UTF-8");
        } catch (CsdnExpection csdnExpection) {
            return "redirect:404.html";
        }
    }

    //发送弹幕
    @RequestMapping(value = "kk", method = RequestMethod.POST)
    @ResponseBody
    public BaseResp kk(Bullet bullet) {
        System.out.println(bullet);
        BaseResp baseResp = new BaseResp();
        Integer count = bulletService.insertSelective(bullet);
        if (count > 0) {
            baseResp.setSuccess(200);
            return baseResp;
        } else {
            baseResp.setSuccess(500);
            return baseResp;
        }
    }

    @ResponseBody
    @RequestMapping(value = "bullets/v3", method = RequestMethod.POST)
    public String postv3(@RequestBody Map<String, String> param, HttpServletRequest request) throws Exception {
        Map map = new HashMap();
//        System.out.println(param);
        Bullet bullet = new Bullet();
        User user = servic.getUserByRedis(request);
        if (user != null && param != null && !param.isEmpty()) {
            bullet.setUserid(user.getUserId());
            bullet.setColor(param.get("color"));
            bullet.setCurrenttime(param.get("time"));
            bullet.setMsg(param.get("text"));
            bullet.setType(param.get("type"));
            bullet.setVideoid(Integer.valueOf(param.get("id")));
            Integer count = bulletService.insertSelective(bullet);
        }
        map.put("code", 0);
        map.put("data", param);
        return JSON.toJSONString(map);
//        {id=98, author=DIYgod, time=23.50655, text=你好, color=16777215, type=0}
    }

    //    请求弹幕一
    @RequestMapping(value = "bullets/v3", method = RequestMethod.GET)
    @ResponseBody
    public String getbullets(@RequestParam Integer id) {
        JSONObject jsonObject = new JSONObject();
        JSONArray data = new JSONArray();
        List<Bullet> bullets = null;
        try {
            bullets = bulletService.selectByVideoId(id);
            if (Xtool.isNotNull(bullets)) {
                bullets.forEach(x -> {
                    List list = new ArrayList();
                    list.add(Double.valueOf(x.getCurrenttime()));
                    list.add(Integer.valueOf(x.getType()));
                    list.add(Integer.valueOf(x.getColor()));
                    list.add("a4182861");
                    list.add(x.getMsg());
                    data.add(list);
                });
            }
            jsonObject.put("code", 0);
            jsonObject.put("data", data);
            System.out.println(jsonObject.toString());
            return jsonObject.toString();
        } catch (Exception e) {
            log.info("弹幕加载异常" + e.getMessage());
            jsonObject.put("code", 500);
            return jsonObject.toString();
        }
    }

    //    查询资源最多
    @RequestMapping(value = "selecthot", method = RequestMethod.GET)
    @ResponseBody
    public BaseResp selecthot(HttpServletRequest request) {
        BaseResp baseResp = new BaseResp();
        try {
            List<Upload> list = service.selecthot();
            if (Xtool.isNotNull(list)) {
                baseResp.setSuccess(1);
                baseResp.setData(list);
            } else {
                baseResp.setSuccess(0);
                baseResp.setErrorMsg("未查询到资源");
            }
        } catch (Exception e) {
            baseResp.setSuccess(0);
            baseResp.setErrorMsg("服务器异常");
        }
        return baseResp;
    }

//    资源占比

    @RequestMapping(value = "resourceProp", method = RequestMethod.GET)
    @ResponseBody
    public BaseResp resourceProp(HttpServletRequest request) throws Exception {
        BaseResp baseResp = new BaseResp();
        User user = servic.getUserByRedis(request);
        if (user != null) {


            try {
                Map<String, Integer> map = service.resourceProp(user.getUserId());
                baseResp.setData(map);
                baseResp.setSuccess(1);
            } catch (Exception e) {
                e.printStackTrace();
                baseResp.setSuccess(0);
                baseResp.setErrorMsg("服务器异常");
            }
            return baseResp;
        }
        baseResp.setSuccess(0);
        baseResp.setErrorMsg("未登入");
        return baseResp;
    }
}
