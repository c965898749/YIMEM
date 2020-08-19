package com.sy.controller;

import com.sy.expection.CsdnExpection;
import com.sy.model.Download;
import com.sy.model.Upload;
import com.sy.model.User;
import com.sy.model.resp.BaseResp;
import com.sy.service.DownloadService;
import com.sy.service.UploadService;
import com.sy.service.UserServic;
import com.sy.tool.Xtool;
import org.apache.commons.io.FileUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.*;
import java.net.URLEncoder;
import java.util.List;
import java.util.Map;

@Controller
public class DownloadController {
    @Autowired
    private UploadService service;
    @Autowired
    private UserServic servic;
    @Autowired
    private DownloadService downloadService;


    @RequestMapping(value = "downloadResource", method = RequestMethod.GET)
    public String download(Integer id, HttpServletRequest request, HttpServletResponse response) throws IOException {

//        先验证用户登录
        User user = (User) request.getSession().getAttribute("user");

        if (user == null) {
            return "redirect:login.html";
        }

        //        实时更新用户信息
        User user1 = new User();
        try {
            Integer userId = user.getUserId();
            user1 = (User) servic.findUserByUserId(userId).getData();
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
            count = servic.downloadMoney(upload.getPrice(), user1.getUserId(), uploadUsrid,id);

            if (count>0) {
                path = upload.getSrc();

//                return "redirect:"+path+"?attname="+upload.getName();
                return "redirect:"+path+"?attname="+URLEncoder.encode(upload.getName(),"UTF-8");
            }
        } catch (CsdnExpection e) {
            e.printStackTrace();
            return "redirect:404.html";
        }
        //记录下载次数
        upload.setHot(upload.getHot()+1);
        service.updatahot(upload.getHot());
        return "redirect:404.html";
        //        开始下载
    }

//    查询下载最多
    @RequestMapping(value = "selecthot", method = RequestMethod.GET)
    @ResponseBody
    public BaseResp selecthot(HttpServletRequest request){
        BaseResp baseResp=new BaseResp();
        try {
            List<Upload> list=service.selecthot();
            if (Xtool.isNotNull(list)){
                baseResp.setSuccess(1);
                baseResp.setData(list);
            }else {
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
    public BaseResp resourceProp(HttpServletRequest request){
        BaseResp baseResp=new BaseResp();
        User user = (User) request.getSession().getAttribute("user");
        if (user == null) {
            baseResp.setSuccess(0);
            baseResp.setErrorMsg("未登入");
            return baseResp;
        }else {

            try {
                Map<String,Integer> map=service.resourceProp(user.getUserId());
                baseResp.setData(map);
                baseResp.setSuccess(1);
            } catch (Exception e) {
                e.printStackTrace();
                baseResp.setSuccess(0);
                baseResp.setErrorMsg("服务器异常");
            }
            return baseResp;
        }
    }
}
