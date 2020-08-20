package com.sy.controller;

import com.sy.model.User;
import com.sy.model.resp.BaseResp;
import com.sy.service.CollectService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletRequest;

@RestController
public class CollectController {
    @Autowired
    private CollectService collectService;
    BaseResp baseResp = new BaseResp();
//    @RequestMapping("/queryCollectByUserId")
//    public BaseResp queryAll(Integer userId){
//        BaseResp baseResp = collectService.selectByUserId(userId);
//        return baseResp;
//    }
//    @RequestMapping("/addCollect")
//    public BaseResp add(String name,Integer userid){
//        BaseResp baseResp = collectService.add(name,userid);
//        return baseResp;
//    }
//用户信息 根据用户ID渲染收藏接口
    @RequestMapping("myCollect")
    public BaseResp myCollect(Integer userId){
        try {
            baseResp = collectService.selectCollectByUserId(userId);
        } catch (Exception e) {
            e.printStackTrace();
            baseResp.setSuccess(0);
            baseResp.setErrorMsg("服务器异常");
        }
        return baseResp;
    }
    //用户信息 根据用户ID渲染收藏接口
    @RequestMapping("myAtaCollect")
    public BaseResp myAtaCollect(Integer userId){
        try {
            baseResp = collectService.findUserAllAtaCollect(userId);
        } catch (Exception e) {
            e.printStackTrace();
            baseResp.setSuccess(0);
            baseResp.setErrorMsg("服务器异常");
        }
        return baseResp;
    }
    //根据收藏夹ID获取收藏夹全部信息
    @RequestMapping("findCollectAllInfor")
    public BaseResp findCollectAllInfor(Integer collectId){
        try {
            baseResp = collectService.findCollectAllInfor(collectId);
        } catch (Exception e) {
            e.printStackTrace();
            baseResp.setSuccess(0);
            baseResp.setErrorMsg("服务器异常");
        }
        return baseResp;
    }
//根据用户ID和收藏夹ID删除关注记录(收藏夹取消关注)
@RequestMapping(value = "unsubscribe",method = RequestMethod.POST)
public BaseResp unsubscribe(Integer userId,Integer collectId){
    try {
        baseResp = collectService.removeFaAttByUseridAndCollectId(userId,collectId);
    } catch (Exception e) {
        e.printStackTrace();
        baseResp.setSuccess(0);
        baseResp.setErrorMsg("服务器异常");
    }
    return baseResp;
}
    //根据收藏夹ID和收藏博客或问答或论坛Id去除收藏记录(取消收藏夹内收藏内容)
    @RequestMapping(value = "cancelCollection",method = RequestMethod.POST)
    public BaseResp cancelCollection(Integer id, Integer collectId,String type){
        try {
            baseResp = collectService.removeCollectitemsByCollectIdAndBidOrAidOrFid(id,collectId,type);
        } catch (Exception e) {
            e.printStackTrace();
            baseResp.setSuccess(0);
            baseResp.setErrorMsg("服务器异常");
        }
        return baseResp;
    }
    //根据收藏夹ID删除收藏夹 并同时删除收藏夹内收藏内容以及收藏夹被关注的记录
    @RequestMapping(value = "removeCollect",method = RequestMethod.POST)
    public BaseResp removeCollect(Integer collectId){
        try {
            baseResp = collectService.removeCollectByCollectId(collectId);
        } catch (Exception e) {
            e.printStackTrace();
            baseResp.setSuccess(0);
            baseResp.setErrorMsg("服务器异常");
        }
        return baseResp;
    }
    //根据用户ID查询收藏夹
    @RequestMapping(value = "findAllCollectByuserID")
    public BaseResp findAllCollectByuserID(Integer userid){
        BaseResp baseResp=new BaseResp();
        try {
            baseResp = collectService.findAllCollectByuserID(userid);
        } catch (Exception e) {
            e.printStackTrace();
            baseResp.setSuccess(0);
            baseResp.setErrorMsg("服务器异常");
        }
        return baseResp;
    }

    //新增收藏夹
    @RequestMapping(value = "addNewCollect",method = RequestMethod.POST)
    public BaseResp addNewCollect(String name, Integer userid, String collectDescribe){
        BaseResp baseResp=new BaseResp();
        try {
            baseResp = collectService.addNewCollect(name,userid,collectDescribe);
        } catch (Exception e) {
            e.printStackTrace();
            baseResp.setSuccess(0);
            baseResp.setErrorMsg("服务器异常");
        }
        return baseResp;
    }
    //修改收藏夹
    @RequestMapping(value = "modifyCollect",method = RequestMethod.POST)
    public BaseResp modifyCollect(String name, String collectDescribe, Integer collectId, Integer userId){
        BaseResp baseResp=new BaseResp();
        try {
            baseResp = collectService.modifyCollect(name,collectDescribe,collectId,userId);
        } catch (Exception e) {
            e.printStackTrace();
            baseResp.setSuccess(0);
            baseResp.setErrorMsg("服务器异常");
        }
        return baseResp;
    }
    //个人主页收藏夹数据
    @RequestMapping(value = "getPerInforCollectData")
    public BaseResp getPerInforCollectData(Integer userId,Integer page){
        BaseResp baseResp=new BaseResp();
        try {
            baseResp = collectService.getPerInforCollectData(userId,page);
        } catch (Exception e) {
            e.printStackTrace();
            baseResp.setSuccess(0);
            baseResp.setErrorMsg("服务器异常");
        }
        return baseResp;
    }

    @RequestMapping(value = "getPerInforCollectData2")
    public BaseResp getPerInforCollectData2(Integer userId,Integer page){
        BaseResp baseResp=new BaseResp();
        try {
            baseResp = collectService.getPerInforCollectData2(userId,page);
        } catch (Exception e) {
            e.printStackTrace();
            baseResp.setSuccess(0);
            baseResp.setErrorMsg("服务器异常");
        }
        return baseResp;
    }
    //个人主页收藏关注事件
    @RequestMapping(value = "addFaAtt")
    public BaseResp addFaAtt(Integer viweUserId, Integer userId, Integer collectId){
        BaseResp baseResp=new BaseResp();
        try {
            baseResp = collectService.addFaAtt(viweUserId,userId,collectId);
        } catch (Exception e) {
            e.printStackTrace();
            baseResp.setSuccess(0);
            baseResp.setErrorMsg("服务器异常");
        }
        return baseResp;
    }

    //杨
    @RequestMapping(value = "/queryCollectByUserId",method = RequestMethod.GET)
    public BaseResp queryAll(Integer blogId, HttpServletRequest request){
        BaseResp baseResp = new BaseResp();
        User user=(User) request.getSession().getAttribute("user");
        if (user!=null){
            baseResp = collectService.queryByUserId(user.getUserId(),blogId);
        }else {
            baseResp.setSuccess(0);
            baseResp.setErrorMsg("没有登录");
        }

        return baseResp;
    }
    @RequestMapping(value = "/addCollect")
    public BaseResp add(String name,Integer userid){
        BaseResp baseResp = collectService.add(name,userid);
        return baseResp;
    }

}
