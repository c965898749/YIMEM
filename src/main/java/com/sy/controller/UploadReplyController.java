package com.sy.controller;

import com.github.pagehelper.Page;
import com.github.pagehelper.PageHelper;
import com.sy.expection.CsdnExpection;
import com.sy.model.Downloadreply;
import com.sy.model.Upload;
import com.sy.model.resp.BaseResp;
import com.sy.service.DownloadreplyService;
import com.sy.service.UploadService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletResponse;
import java.util.List;

@RestController
public class UploadReplyController {
    @Autowired
    private DownloadreplyService service;
    @RequestMapping(value = "findAllReplyById",method = RequestMethod.GET)
    public BaseResp findAllReplyById(Integer dowid,Integer page,Integer pageSize, HttpServletResponse res){
        BaseResp resp=new BaseResp();
        try {
            PageHelper.startPage(page,pageSize);
            List<Downloadreply> list=service.findByDowid(dowid);
            Page<Downloadreply> downloadreplyPage=(Page<Downloadreply>)list;
            if (list==null){
                resp.setSuccess(400);
                res.setStatus(400);
                resp.setErrorMsg("资源不存在");
                return resp;
            }else {
                resp.setSuccess(200);
                res.setStatus(200);
                resp.setData(list);
                resp.setCount(downloadreplyPage.getPages());
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
    @RequestMapping(value = "saveReply",method = RequestMethod.POST)
    public  BaseResp saveReply(Downloadreply downloadreply,HttpServletResponse res){
        BaseResp resp=new BaseResp();
        try {

            Integer count= service.save(downloadreply);

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
