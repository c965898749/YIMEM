package com.sy.controller;

import com.sy.model.resp.BaseResp;
import com.sy.service.AskService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class AskController {
    @Autowired
    private AskService askService;
    //根据条件获取问答页面数据
    @RequestMapping(value = "getAskdata")
    public BaseResp getAskdata(Integer solve,Integer award,Integer page, Integer time, Integer askNum){
        BaseResp baseResp =new BaseResp();
        try {
            baseResp=askService.selectAsksByCondition(solve,award,time,askNum,page);
        } catch (Exception e) {
            e.printStackTrace();
            baseResp.setSuccess(0);
            baseResp.setErrorMsg("服务器异常");
        }
        return baseResp;
    }
    //获取问答页面个人信息页面数据
    @RequestMapping(value = "userinfor")
    public BaseResp userinfor(Integer userId){
        BaseResp baseResp =new BaseResp();
        try {
            baseResp=askService.getUserInforByUserId(userId);
        } catch (Exception e) {
            e.printStackTrace();
            baseResp.setSuccess(0);
            baseResp.setErrorMsg("服务器异常");
        }
        return baseResp;
    }
    //添加新问答
    @RequestMapping(value = "addNewAsk",method = RequestMethod.POST)
    public BaseResp addNewAsk(Integer userId,String askName,String askText,Integer integralNeed){
        BaseResp baseResp =new BaseResp();
        try {
            baseResp=askService.addNewAsk(userId,askName,askText,integralNeed);
        } catch (Exception e) {
            e.printStackTrace();
            baseResp.setSuccess(0);
            baseResp.setErrorMsg("服务器异常");
        }
        return baseResp;
    }
    //获取问答详情数据
    @RequestMapping(value = "getAskInforByAskid",method = RequestMethod.POST)
    public BaseResp getAskInforByAskid(Integer askId){
        BaseResp baseResp =new BaseResp();
        try {
            baseResp=askService.getAskInforByAskid(askId);
        } catch (Exception e) {
            e.printStackTrace();
            baseResp.setSuccess(0);
            baseResp.setErrorMsg("服务器异常");
        }
        return baseResp;
    }
    //获取问答详情数据
    @RequestMapping(value = "insertAnswerAsk",method = RequestMethod.POST)
    public BaseResp insertAnswerAsk(Integer askId, Integer userID, String answerAskText){
        BaseResp baseResp =new BaseResp();
        try {
            baseResp=askService.insertAnswerAsk(askId,userID,answerAskText);
        } catch (Exception e) {
            e.printStackTrace();
            baseResp.setSuccess(0);
            baseResp.setErrorMsg("服务器异常");
        }
        return baseResp;
    }

    //添加收藏夹事件
    @RequestMapping(value = "addAskToCollect",method = RequestMethod.POST)
    public BaseResp addAskToCollect(Integer askID, Integer collectid){
        BaseResp baseResp =new BaseResp();
        try {
            baseResp=askService.addAskToCollect(askID,collectid);
        } catch (Exception e) {
            e.printStackTrace();
            baseResp.setSuccess(0);
            baseResp.setErrorMsg("服务器异常");
        }
        return baseResp;
    }
    //添加同问事件
    @RequestMapping(value = "addAlsoask",method = RequestMethod.POST)
    public BaseResp addAlsoask(Integer askID, Integer userId){
        BaseResp baseResp =new BaseResp();
        try {
            baseResp=askService.addAlsoask(askID,userId);
        } catch (Exception e) {
            e.printStackTrace();
            baseResp.setSuccess(0);
            baseResp.setErrorMsg("服务器异常");
        }
        return baseResp;
    }

}
