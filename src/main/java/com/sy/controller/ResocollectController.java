package com.sy.controller;

import com.sy.model.Resocollect;
import com.sy.model.resp.BaseResp;
import com.sy.service.ResocollectService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletResponse;
import java.util.List;

@RestController
public class ResocollectController {
    @Autowired
    private ResocollectService service;
    @RequestMapping(value = "selectAllResocollect",method = RequestMethod.GET)
    public BaseResp selectAllResocollect(Integer userid, Integer id, HttpServletResponse res){
        BaseResp resp=new BaseResp();
        Resocollect resocollect=service.findAll(userid,id);
        if (resocollect==null){
            resp.setSuccess(0);
            return resp;
        }else {
            resp.setSuccess(1);
            return resp;
        }
    }
    @RequestMapping(value = "saveResocollect",method = RequestMethod.POST)
    public BaseResp saveResocollect(Resocollect resocollect,HttpServletResponse res){
        BaseResp resp=new BaseResp();

        Integer count=service.save(resocollect);
        if (count>0){
            resp.setSuccess(200);
            res.setStatus(200);
            return resp;
        }else {
            resp.setSuccess(400);
//            res.setStatus(400);
            resp.setErrorMsg("插入失败");
            return resp;
        }
    }
    @RequestMapping(value = "selectAllResocollectBypage",method = RequestMethod.GET)
    public BaseResp selectAllResocollect(Integer userid,Integer page,Integer pageSize,HttpServletResponse res){
        BaseResp resp=new BaseResp();
        List<Resocollect> lists=service.findByUserid(userid,(page-1)*pageSize,pageSize);
        Integer count=service.findAllCount(userid);
        if (lists!=null){
            resp.setSuccess(200);
            res.setStatus(200);
            resp.setData(lists);
            resp.setCount(count);
            return resp;
        }else {
            resp.setSuccess(400);
            res.setStatus(400);
            resp.setErrorMsg("插入失败");
            return resp;
        }
    }
}
