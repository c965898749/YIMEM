package com.sy.controller;

import com.sy.model.resp.BaseResp;
import com.sy.service.SearchService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletResponse;

@RestController
public class SearchController {
//    private Logger log = Logger.getLogger(SearchController.class.getName());
    @Autowired
    private SearchService searchService;
    @RequestMapping(value = "searchAll",method = RequestMethod.GET)
    public BaseResp queryAll(String key,HttpServletResponse response){
        BaseResp baseResp = searchService.queryAll(key);
        return baseResp;
    }
    @RequestMapping(value = "searchAsk",method = RequestMethod.GET)
    public BaseResp queryAsk(String key,HttpServletResponse response){
        BaseResp baseResp = searchService.queryAsk(key);
        return baseResp;
    }
    @RequestMapping(value = "searchBlog",method = RequestMethod.GET)
    public BaseResp queryBlog(String key,HttpServletResponse response){
        BaseResp baseResp = searchService.queryBlog(key);
        return baseResp;
    }
    @RequestMapping(value = "searchDownLoad",method = RequestMethod.GET)
    public BaseResp queryDownLoad(String key,HttpServletResponse response){
        BaseResp baseResp = searchService.queryDownload(key);
        return baseResp;
    }
    @RequestMapping(value = "searchForum",method = RequestMethod.GET)
    public BaseResp queryForum(String key,HttpServletResponse response){
        BaseResp baseResp = searchService.queryForum(key);
        return baseResp;
    }
    @RequestMapping(value = "searchVideo",method = RequestMethod.GET)
    public BaseResp queryVideo(String key,HttpServletResponse response){
        BaseResp baseResp = searchService.queryVideo(key);
        return baseResp;
    }

    @RequestMapping(value = "searchinterview",method = RequestMethod.GET)
    public BaseResp querysearchinterview(String key,HttpServletResponse response){
        BaseResp baseResp = searchService.queryinterview(key);
        return baseResp;
    }

}
