package com.sy.controller;

import com.sy.mapper.SearchMapper;
import com.sy.model.resp.BaseResp;
import com.sy.service.SearchService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

@RestController
public class SearchController {
    @Autowired
    private SearchService searchService;
    @RequestMapping(value = "searchAll",method = RequestMethod.GET)
    public BaseResp queryAll(String key,HttpServletResponse response){
        BaseResp baseResp = searchService.queryAll(key);
        if (baseResp.getSuccess()==404){
              response.setStatus(404);
        }else {
            response.setStatus(200);
        }
        return baseResp;
    }
    @RequestMapping(value = "searchAsk",method = RequestMethod.GET)
    public BaseResp queryAsk(String key,HttpServletResponse response){
        BaseResp baseResp = searchService.queryAsk(key);
        if (baseResp.getSuccess()==404){
            response.setStatus(404);
        }else {
            response.setStatus(200);
        }
        return baseResp;
    }
    @RequestMapping(value = "searchBlog",method = RequestMethod.GET)
    public BaseResp queryBlog(String key,HttpServletResponse response){
        BaseResp baseResp = searchService.queryBlog(key);
        if (baseResp.getSuccess()==404){
            response.setStatus(404);
        }else {
            response.setStatus(200);
        }
        return baseResp;
    }
    @RequestMapping(value = "searchDownLoad",method = RequestMethod.GET)
    public BaseResp queryDownLoad(String key,HttpServletResponse response){
        BaseResp baseResp = searchService.queryDownload(key);
        if (baseResp.getSuccess()==404){
            response.setStatus(404);
        }else {
            response.setStatus(200);
        }
        return baseResp;
    }
    @RequestMapping(value = "searchForum",method = RequestMethod.GET)
    public BaseResp queryForum(String key,HttpServletResponse response){
        BaseResp baseResp = searchService.queryForum(key);
        if (baseResp.getSuccess()==404){
            response.setStatus(404);
        }else {
            response.setStatus(200);
        }
        return baseResp;
    }

}
