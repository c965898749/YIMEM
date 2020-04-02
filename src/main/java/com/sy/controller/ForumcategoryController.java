package com.sy.controller;

import com.sy.expection.CsdnExpection;
import com.sy.model.DownloadCategory;
import com.sy.model.Forumcategory;
import com.sy.model.resp.BaseResp;
import com.sy.service.DownloadCategoryService;
import com.sy.service.ForumcategoryService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletResponse;
import java.util.List;

@RestController
public class ForumcategoryController {
    @Autowired
    private ForumcategoryService service;

    @RequestMapping(value = "findAllforumCategory", method = RequestMethod.GET)
    public BaseResp findAllCategory(HttpServletResponse res) {
        BaseResp resp = new BaseResp();
        try {
            List<Forumcategory> lists = service.findAll();
//            Integer count=service.findAllCount();
            if (lists != null) {
                resp.setData(lists);
                resp.setSuccess(200);
                res.setStatus(200);
//                resp.setCount(count);
                return resp;
            } else {
                resp.setSuccess(400);
                res.setStatus(400);
                resp.setErrorMsg("查找失败");
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

    @RequestMapping(value = "findforumCategoryByPid", method = RequestMethod.GET)
    public BaseResp findCategoryByPid(Integer pid, HttpServletResponse res) {
        BaseResp resp = new BaseResp();
        try {
            List<Forumcategory> lists = service.findByPid(pid);
//            Integer count=service.findAllCount();
            if (lists != null) {
                resp.setData(lists);
                resp.setSuccess(200);
                res.setStatus(200);
//                resp.setCount(count);
                return resp;
            } else {
                resp.setSuccess(400);
                res.setStatus(400);
                resp.setErrorMsg("查找失败");
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

    @RequestMapping(value = "findforumCategoryByid", method = RequestMethod.GET)
    public BaseResp findCategoryByid(Integer id, HttpServletResponse res) {
        BaseResp resp = new BaseResp();
        try {
            Forumcategory category = service.findByid(id);

//            Integer count=service.findAllCount();
            if (category != null) {
                resp.setData(category);
                resp.setSuccess(200);
                res.setStatus(200);
//                resp.setCount(count);
                return resp;
            } else {
                resp.setSuccess(400);
                res.setStatus(400);
                resp.setErrorMsg("查找失败");
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

    @RequestMapping(value = "selcetRoot", method = RequestMethod.GET)
    public BaseResp selcetRoot(HttpServletResponse res) {
        BaseResp resp = new BaseResp();
        try {
            List<Forumcategory> category = service.selcetRoot();
//            Integer count=service.findAllCount();
            if (category != null) {
                resp.setData(category);
                resp.setSuccess(200);
                res.setStatus(200);
                //                resp.setCount(count);
                return resp;
            } else {
                resp.setSuccess(400);
                res.setStatus(400);
                resp.setErrorMsg("查找失败");
                return resp;
            }
        } catch (Exception e) {
            resp.setSuccess(500);
            res.setStatus(500);
            resp.setErrorMsg(e.getMessage());
            e.printStackTrace();
            return resp;
        }

    }
}
