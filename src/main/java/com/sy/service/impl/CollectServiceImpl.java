package com.sy.service.impl;

import com.github.pagehelper.Page;
import com.github.pagehelper.PageHelper;
import com.sy.mapper.CollectItemsMapper;
import com.sy.mapper.CollectMapper;
import com.sy.mapper.UserMapper;
import com.sy.model.*;
import com.sy.model.resp.BaseResp;
import com.sy.service.CollectService;
import jdk.nashorn.internal.runtime.linker.LinkerCallSite;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
@Transactional(readOnly = true)
public class CollectServiceImpl implements CollectService {
    @Autowired
    private CollectMapper collectMapper;
    @Autowired
    private UserMapper userMapper;
    @Autowired
    private CollectItemsMapper collectItemsMapper;
    //通过用户id查询收藏夹
    @Override
    public BaseResp selectByUserId(Integer userid) {
        BaseResp baseResp = new BaseResp();
        List<Collect> collectList = collectMapper.queryByUserId(userid);
        if (collectList.size()!=0){
            baseResp.setSuccess(1);
            baseResp.setData(collectList);
        }else {
            baseResp.setSuccess(0);
            baseResp.setErrorMsg("空空如也");
        }
        return baseResp;
    }
//    //新增收藏夹
//    @Override
//    @Transactional(propagation = Propagation.REQUIRED)
//    public BaseResp add(String name, int userid) {
//        BaseResp baseResp = new BaseResp();
//        int result = collectMapper.add(userid,name);
//        if (result!=0){
//            baseResp.setSuccess(1);
//        }else {
//            baseResp.setSuccess(0);
//        }
//        return baseResp;
//    }


    //通过用户id查询收藏夹
    @Override
    public BaseResp selectCollectByUserId(Integer userid) throws Exception {
        BaseResp baseResp = new BaseResp();
        List<Collect> collectList = collectMapper.queryByUserId(userid);
        if (collectList.size()!=0){
            baseResp.setSuccess(1);
            for (Collect collect:collectList){
                Integer collectID=collect.getId();
                Integer dataCount=collectMapper.dataCountByCollectId(collectID);
                Integer invCount=collectMapper.dataCountByCollectId(collectID);
                collect.setDataCount(dataCount);
                collect.setInvCount(invCount);
            }
            baseResp.setData(collectList);
        }else {
            baseResp.setSuccess(0);
            baseResp.setErrorMsg("空空如也");
        }
        return baseResp;
    }
    //根据用户ID获取所关注的收藏夹用户信息包含博客、问答等的总数量
    @Override
    public BaseResp findUserAllAtaCollect(Integer usrId) throws Exception {
        BaseResp baseResp=new BaseResp();
        List<Fa_att> fa_attList=collectMapper.selectAllFa_attByUserid(usrId);
        List<Collect> collectList=new ArrayList<>();
        if (fa_attList.size()!=0){
            baseResp.setSuccess(1);
            for (Fa_att fa_att:fa_attList){
               Integer collectID=fa_att.getCollectID();
               Integer favoriteId=fa_att.getFavoriteId();
               String username=userMapper.selectUserByUserId(favoriteId).getUsername();
                Collect collect=collectMapper.selectCollectByID(collectID);
                Integer dataCount=collectMapper.dataCountByCollectId(collectID);
                Integer invCount=collectMapper.dataCountByCollectId(collectID);
                collect.setDataCount(dataCount);
                collect.setInvCount(invCount);
                collect.setUsername(username);
               collectList.add(collect);
            }
            baseResp.setData(collectList);
        }else {
            baseResp.setSuccess(0);
            baseResp.setErrorMsg("空空如也");
        }
        return baseResp;
    }
    //根据收藏夹ID获取收藏夹全部信息
    @Override
    public BaseResp findCollectAllInfor(Integer collectId) throws Exception {
        BaseResp baseResp=new BaseResp();
        Collect collect=collectMapper.selectCollectByID(collectId);
        Integer dataCount=collectMapper.dataCountByCollectId(collectId);
        Integer invCount=collectMapper.dataCountByCollectId(collectId);
        collect.setDataCount(dataCount);
        collect.setInvCount(invCount);
       List<Collectitems> collectitemsList=collectMapper.selectAllCollectitemsByCollectId(collectId);
       List<Object> objectList=new ArrayList<>();
//       List<Blog> blogList=new ArrayList<>();
//       List<Forum> forumList=new ArrayList<>();
       for (Collectitems collectitems:collectitemsList){
           if(collectitems.getAskID()!=null){
               Ask ask=collectMapper.selectAskByCollectId(collectitems.getAskID());
               ask.setCreateTime(collectitems.getCreateTime());
               String username=userMapper.selectUserByUserId(ask.getUserId()).getUsername();
               ask.setUsername(username);
               objectList.add(ask);
           }
           if(collectitems.getBlogid()!=null){
               Blog blog=collectMapper.selectBlogByCollectId(collectitems.getBlogid());
               String username=userMapper.selectUserByUserId(blog.getUserid()).getUsername();
              blog.setCreatetime(collectitems.getCreateTime());
               blog.setUsername(username);
               objectList.add(blog);
           }
           if(collectitems.getForumID()!=null){
               Forum forum=collectMapper.selectForumByCollectId(collectitems.getForumID());
               String username=userMapper.selectUserByUserId(forum.getUserId()).getUsername();
               forum.setForumCreatime(collectitems.getCreateTime());
               forum.setUsername(username);
               objectList.add(forum);
           }
       }
       if(objectList.size()!=0){
           collect.setObjectList(objectList);
           baseResp.setSuccess(1);
           baseResp.setData(collect);
       }else {
           baseResp.setSuccess(0);
           baseResp.setErrorMsg("未找到数据");
           baseResp.setData(collect);
       }
        return baseResp;
    }
    //根据用户ID和收藏夹ID删除关注记录(收藏夹取消关注)
    @Override
    @Transactional(propagation = Propagation.REQUIRED)
    public BaseResp removeFaAttByUseridAndCollectId(Integer userId, Integer collectId) throws Exception {
        BaseResp baseResp=new BaseResp();
        Integer result =collectMapper.deleteFaAttByUseridAndCollectId(userId,collectId);
        if(result>0){
            baseResp.setSuccess(1);
            baseResp.setErrorMsg("取消关注成功");
        }else {
            baseResp.setSuccess(0);
            baseResp.setErrorMsg("取消关注失败");
        }
        return baseResp;
    }
    //根据收藏夹ID和收藏博客或问答或论坛Id去除收藏记录
    @Override
    @Transactional(propagation = Propagation.REQUIRED)
    public BaseResp removeCollectitemsByCollectIdAndBidOrAidOrFid(Integer id, Integer collectId,String type) throws Exception {
        BaseResp baseResp=new BaseResp();
        Collectitems collectitems=new Collectitems();
        collectitems.setCollectid(collectId);
        if ("BLOG".equals(type)) {
            collectitems.setBlogid(id);
        }else if("ASK".equals(type)){
            collectitems.setAskID(id);
        }else {
            collectitems.setForumID(id);
        }
        int result=collectMapper.deleteCollectitemsByCollectIdAndBidOrAidOrFid(collectitems);
        if(result>0){
            baseResp.setSuccess(1);
            baseResp.setErrorMsg("取消收藏成功");
        }else {
            baseResp.setSuccess(0);
            baseResp.setErrorMsg("取消收藏失败");
        }
        return baseResp;
    }
    //根据收藏夹ID删除收藏夹 并同时删除收藏夹内收藏内容以及收藏夹被关注的记录
    @Override
    @Transactional(propagation = Propagation.REQUIRED)
    public BaseResp removeCollectByCollectId(Integer collectId) throws Exception {
        BaseResp baseResp=new BaseResp();
        int fa_attResult=collectMapper.delectFaAttByCollectId(collectId);
        int collectitemsResult=collectMapper.delectCollectitemsByCollectId(collectId);
        int collrctResult=collectMapper.delectCollectByCollectId(collectId);
        if(fa_attResult>0&&collectitemsResult>0&&collrctResult>0){
            baseResp.setSuccess(1);
            baseResp.setErrorMsg("删除成功");
        }else {
            baseResp.setSuccess(0);
            baseResp.setErrorMsg("删除失败");
        }
        return baseResp;
    }

    //新增收藏夹
    @Override
    @Transactional(propagation = Propagation.REQUIRED)
    public BaseResp addNewCollect(String name, Integer userid, String collectDescribe) throws Exception {
        BaseResp baseResp = new BaseResp();
        Collect collect =new Collect();
        collect.setName(name);
        collect.setUserid(userid);
        List<String> names=collectMapper.selectCollectNameByUserid(userid);
        if(names.contains(name)){
            baseResp.setSuccess(0);
            baseResp.setErrorMsg("您已存在该名字的收藏夹！！");
        }else {
            if(collectDescribe!=null&&!"".equals(collectDescribe)){
                collect.setCollectDescribe(collectDescribe);
            }
            int result=collectMapper.insertCollect(collect);
            if(result>0){
                baseResp.setSuccess(1);
                baseResp.setErrorMsg("添加成功");
            }else {
                baseResp.setSuccess(0);
                baseResp.setErrorMsg("添加失败");
            }
        }
        return baseResp;
    }
    //修改收藏夹
    @Override
    @Transactional(propagation = Propagation.REQUIRED)
    public BaseResp modifyCollect(String name, String collectDescribe, Integer collectId, Integer userId) throws Exception {
        BaseResp baseResp=new BaseResp();
        List<Collect> collectList=collectMapper.selectAllCollectByuserId(userId);
        Collect collect1=new Collect();
        List<String> names=new ArrayList<>();
        for(Collect collect:collectList){
            if(!collect.getId().equals(collectId)){
                names.add(collect.getName());
            }
        }
        if(names.contains(name)){
            baseResp.setSuccess(0);
            baseResp.setErrorMsg("该名的收藏夹已存在，请重新命名！");
        }else {
            collect1.setName(name);
            collect1.setCollectDescribe(collectDescribe);
            collect1.setId(collectId);
            int result=collectMapper.updateCollect(collect1);
            if(result>0){
                baseResp.setSuccess(1);
                baseResp.setErrorMsg("修改成功");
            }else {
                baseResp.setSuccess(0);
                baseResp.setErrorMsg("修改失败");
            }
        }
        return baseResp;
    }
    //个人主页收藏夹数据
    @Override
    public BaseResp getPerInforCollectData(Integer userId,Integer page) {
        BaseResp baseResp=new BaseResp();
        Integer pageSize = 5;
        PageHelper.startPage(page,pageSize);
        List<Collect> mycollectList=collectMapper.selectAllCollectByuserId(userId);
        Page<Collect> blogPage = (Page<Collect>)mycollectList;
        //获取收藏夹内数据数量个关注人数
        for (Collect collect:mycollectList){
            Integer userId1=collect.getUserid();
            String username=userMapper.selectUserByUserId(userId1).getUsername();
            Integer collectId=collect.getId();
            Integer dataCount=collectMapper.dataCountByCollectId(collectId);
            Integer invCount=collectMapper.invCountByCollectId(collectId);
            collect.setInvCount(invCount);
            collect.setDataCount(dataCount);
            collect.setUsername(username);
        }
        Map<String,Object> map=new HashMap<>();
        map.put("count",blogPage.getPages());
        map.put("myCollect",mycollectList);
        baseResp.setSuccess(1);
        baseResp.setData(map);
        baseResp.setErrorMsg("获取收藏夹数据成功");
        return baseResp;
    }
    @Override
    public BaseResp getPerInforCollectData2(Integer userId,Integer page) {
        BaseResp baseResp=new BaseResp();
        Integer pageSize = 5;
        PageHelper.startPage(page,pageSize);
        List<Fa_att> fa_attList=collectMapper.selectAllFa_attByUserid(userId);
        Page<Fa_att> blogPage2 = (Page<Fa_att>)fa_attList;
        List<Collect> attationCollectList=new ArrayList<>();
        for (Fa_att fa_att:fa_attList){
            Integer collectID=fa_att.getCollectID();
            Collect collect=collectMapper.selectCollectByID(collectID);
            attationCollectList.add(collect);
        }
        //获取收藏夹内数据数量个关注人数
        for (Collect collect:attationCollectList){
            Integer userId1=collect.getUserid();
            String username=userMapper.selectUserByUserId(userId1).getUsername();
            Integer collectId=collect.getId();
            Integer dataCount=collectMapper.dataCountByCollectId(collectId);
            Integer invCount=collectMapper.invCountByCollectId(collectId);
            collect.setInvCount(invCount);
            collect.setDataCount(dataCount);
            collect.setUsername(username);
        }
        Map<String,Object> map=new HashMap<>();
        map.put("count",blogPage2.getPages());
        map.put("attationCollect",attationCollectList);
        baseResp.setSuccess(1);
        baseResp.setData(map);
        baseResp.setErrorMsg("获取收藏夹数据成功");
        return baseResp;
    }
    //个人主页收藏夹收藏事件
    @Transactional(propagation = Propagation.REQUIRED)
    @Override
    public BaseResp addFaAtt(Integer viweUserId, Integer userId, Integer collectId) throws Exception {
        BaseResp baseResp =new BaseResp();
        int isOwn=collectMapper.selectCollectByCollectidAndUserId(collectId,userId);
        int ishas=collectMapper.selectFaAttByUseridAndCollectId(userId,collectId);
        if(isOwn>0){
            baseResp.setErrorMsg("自己的收藏夹无须收藏");
            baseResp.setSuccess(0);
        }else if(ishas>0){
            baseResp.setErrorMsg("您已收藏过该收藏夹无须重复收藏");
            baseResp.setSuccess(0);
        }else {
            int result=collectMapper.insertNewFaAtt(viweUserId,userId,collectId);
            if(result>0){
                baseResp.setErrorMsg("收藏成功");
                baseResp.setSuccess(1);
            }else {
                baseResp.setErrorMsg("收藏失败");
                baseResp.setSuccess(0);
            }
        }
        return baseResp;
    }

    //根据用户ID查询收藏夹
    @Override
    public BaseResp findAllCollectByuserID( Integer userid) throws Exception {
        BaseResp baseResp = new BaseResp();
        List<Collect> collects=collectMapper.queryByUserId(userid);
        if(collects.size()>0){
            baseResp.setErrorMsg("查询成功");
            baseResp.setSuccess(1);
            baseResp.setData(collects);
        }else {
            baseResp.setErrorMsg("无收藏夹");
            baseResp.setSuccess(0);
            baseResp.setData(collects);
        }

        return baseResp;
    }


    //下方杨
    @Override
    public BaseResp queryByUserId(Integer userid,Integer blogId) {
        BaseResp baseResp = new BaseResp();
        List<Collect> collectList = collectMapper.queryByUserId(userid);
        if (collectList.size()!=0){
            for (Collect collect: collectList){
                int  collectId = collect.getId();
                List<Collectitems> collectItems= collectItemsMapper.queryCollect(collectId,blogId);
                if (collectItems.size()!=0){
                    collect.setIsCollectBlogId(1);
                }else {
                    collect.setIsCollectBlogId(0);
                }
            }
            baseResp.setSuccess(1);
            baseResp.setData(collectList);
        }else {
            baseResp.setSuccess(0);
            baseResp.setErrorMsg("空空如也");
        }
        return baseResp;
    }

    @Override
    @Transactional(propagation = Propagation.REQUIRED)
    public BaseResp add(String name, int userid) {
        BaseResp baseResp = new BaseResp();
        int result = collectMapper.add(userid,name);
        if (result!=0){
            baseResp.setSuccess(1);
        }else {
            baseResp.setSuccess(0);
        }
        return baseResp;
    }



}
