package com.sy.controller;

import com.github.pagehelper.Page;
import com.github.pagehelper.PageHelper;
import com.sy.expection.CsdnExpection;
import com.sy.model.Upload;
import com.sy.model.User;
import com.sy.model.resp.BaseResp;
import com.sy.service.UploadService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.List;

@RestController
public class VerifyUploadController {
    @Autowired
    private UploadService service;
    @RequestMapping(value = "saveUploadResource",method = RequestMethod.POST)
    public BaseResp saveUploadResource(Upload download, HttpServletResponse res, HttpServletRequest request){
       BaseResp resp=new BaseResp();
        User user = (User) request.getSession().getAttribute("user");
        if (user == null) {
            resp.setSuccess(0);
            resp.setErrorMsg("未登入");
            return resp;
        } else {
            try {
                download.setUser(user);
                download.setUserid(user.getUserId());
                Integer count= service.save(download);
                if (count>0){
                    resp.setSuccess(200);
                    res.setStatus(200);
                    return resp;
                }else {
                    resp.setSuccess(400);
                    res.setStatus(400);
                    resp.setErrorMsg("资源插入失败");
                    return resp;
                }
            } catch (CsdnExpection e) {
                resp.setSuccess(500);
                res.setStatus(500);
                resp.setErrorMsg(e.getMessage());
                e.printStackTrace();
                return resp;
            }
        }
    }
    @RequestMapping(value = "findAllResources",method = RequestMethod.POST)
    public BaseResp findAllResources(Upload upload, HttpServletResponse res){
        BaseResp resp=new BaseResp();
        try {
            Integer page=upload.getPage();
            Integer pageSize=upload.getPageSize();
            PageHelper.startPage(page,pageSize);
            List<Upload> lists=service.findAll(upload);
            Page<Upload> uploadPage=(Page<Upload>)lists;
            if (lists!=null){
                resp.setData(lists);
                resp.setSuccess(200);
                res.setStatus(200);
                resp.setCount(uploadPage.getPages());
                return resp;
            }else {
                resp.setSuccess(400);
                res.setStatus(400);
                resp.setErrorMsg("资源查找失败");
                return resp;
            }
        } catch (CsdnExpection e) {
            resp.setSuccess(500);
            res.setStatus(500);
            resp.setErrorMsg(e.getMessage());
            e.printStackTrace();
            return resp;
        }
    }

    @RequestMapping(value = "findAllResourcess",method = RequestMethod.POST)
    public BaseResp findAllResourcess(Upload upload,HttpServletRequest request, HttpServletResponse res){
        BaseResp resp=new BaseResp();
        User user = (User) request.getSession().getAttribute("user");
        if (user == null) {
            resp.setSuccess(400);
            res.setStatus(400);
            resp.setErrorMsg("未登入");
            return resp;
        } else {
            try {
                Integer page=upload.getPage();
                Integer pageSize=upload.getPageSize();
                PageHelper.startPage(page,pageSize);
                upload.setUserid(user.getUserId());
                List<Upload> lists=service.findAll(upload);
                Page<Upload> uploadPage=(Page<Upload>)lists;
                if (lists!=null){
                    resp.setData(lists);
                    resp.setSuccess(200);
                    res.setStatus(200);
                    resp.setCount(uploadPage.getTotal());
                    return resp;
                }else {
                    resp.setSuccess(400);
                    res.setStatus(400);
                    resp.setErrorMsg("资源查找失败");
                    return resp;
                }
            } catch (CsdnExpection e) {
                resp.setSuccess(500);
                res.setStatus(500);
                resp.setErrorMsg(e.getMessage());
                e.printStackTrace();
                return resp;
            }
        }

    }
    @RequestMapping(value = "findByIdResource",method = RequestMethod.GET)
    public BaseResp findByIdResource(Integer id,HttpServletResponse res){
        BaseResp resp=new BaseResp();
        try {
            Upload download=service.findById(id);
            if (download==null){
                resp.setSuccess(400);
                res.setStatus(400);
                resp.setErrorMsg("资源不存在");
                return resp;
            }else {
                resp.setSuccess(200);
                res.setStatus(200);
                resp.setData(download);
                return resp;
            }

        } catch (CsdnExpection csdnExpection) {
            resp.setSuccess(400);
            res.setStatus(400);
            resp.setErrorMsg("资源查找失败");
            csdnExpection.printStackTrace();
            return  resp;
        }
    }

}
