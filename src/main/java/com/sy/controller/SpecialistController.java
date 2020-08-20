package com.sy.controller;

import com.sy.model.resp.BaseResp;
import com.sy.service.SpecialistService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/specialist")
public class SpecialistController {
    @Autowired
    private SpecialistService specialistService;
    @RequestMapping(value = "/requestAll",method = RequestMethod.GET)
    public BaseResp queryAll(String page){
        BaseResp baseResp = null;
        baseResp = specialistService.queryAllResult();
        return baseResp;
    }





}
