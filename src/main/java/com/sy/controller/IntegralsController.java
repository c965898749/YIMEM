package com.sy.controller;

import com.sy.model.Integrals;
import com.sy.model.resp.BaseResp;
import com.sy.service.IntegralsService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletResponse;
import java.util.List;

@RestController
public class IntegralsController {
    @Autowired
    private IntegralsService service;
    @RequestMapping(value = "findIntegralsByUserid",method = RequestMethod.GET)
    public BaseResp findIntegralsByUserid(Integer userid, Integer page, Integer pageSize, HttpServletResponse res){
        BaseResp resp=new BaseResp();
        List<Integrals> lists=service.findByUserid(userid,(page-1)*pageSize,pageSize);
        Integer count =service.findAllCount(userid);
        if (lists!=null){
            resp.setSuccess(200);
            res.setStatus(200);
            resp.setData(lists);
            resp.setCount(count);
            return resp;
        }else {
            res.setStatus(400);
            resp.setSuccess(400);
            resp.setErrorMsg("资源为找到");
            return resp;
        }
    }
}
