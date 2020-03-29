package com.sy.service;

import com.fasterxml.jackson.databind.ser.Serializers;
import com.sy.model.Collect;
import com.sy.model.Collectitems;
import com.sy.model.resp.BaseResp;
import org.apache.ibatis.annotations.Param;


public interface CollectService {
//    //通过用户id查询收藏夹
    BaseResp selectByUserId(Integer userid) ;
//    //新增收藏夹
//    BaseResp add(String name, int userid);

    //通过用户id查询收藏夹并包含数据数量和关注人数
    BaseResp selectCollectByUserId(Integer userid) throws Exception ;
    //根据用户ID获取所关注的收藏夹用户信息包含博客、问答等的总数量
    BaseResp findUserAllAtaCollect(Integer usrId) throws Exception;
    //根据收藏夹ID获取收藏夹全部信息
    BaseResp findCollectAllInfor(Integer collectId) throws Exception;
   //根据用户ID和收藏夹ID删除关注记录(收藏夹取消关注)
    BaseResp removeFaAttByUseridAndCollectId(Integer userId,Integer collectId) throws Exception;
    //根据收藏夹ID和收藏博客或问答或论坛Id去除收藏记录
    BaseResp removeCollectitemsByCollectIdAndBidOrAidOrFid(Integer id, Integer collectId,String type) throws Exception;
    //根据收藏夹ID删除收藏夹 并同时删除收藏夹内收藏内容以及收藏夹被关注的记录
    BaseResp removeCollectByCollectId(Integer collectId) throws Exception;
    //根据用户ID查询收藏夹
    BaseResp findAllCollectByuserID ( Integer userid) throws Exception;
    //新增收藏夹
    BaseResp addNewCollect(String name,Integer userid,String collectDescribe) throws Exception;
    //修改收藏夹
    BaseResp modifyCollect(String name,String collectDescribe,Integer collectId,Integer userId) throws Exception;
    //个人主页收藏夹数据
    BaseResp getPerInforCollectData(Integer userId,Integer page);
    BaseResp getPerInforCollectData2(Integer userId,Integer page);
    //个人主页收藏夹收藏事件
    BaseResp addFaAtt(Integer viweUserId,Integer userId,Integer collectId) throws Exception;

    //下方杨
    //通过用户id查询收藏夹
    BaseResp queryByUserId(Integer userid,Integer blogId);
    //新增收藏夹
    BaseResp add(String name,int userid);


}
