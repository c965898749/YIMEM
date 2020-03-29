package com.sy.service;

import com.sy.model.Ask;
import com.sy.model.resp.BaseResp;
import org.apache.ibatis.annotations.Param;

import java.util.List;

public interface AskService {
    //根据条件获取问答页面数据
    BaseResp selectAsksByCondition(Integer solve, Integer award, Integer time, Integer askNum,Integer page) throws Exception;
    //获取问答页面个人信息页面数据
    BaseResp getUserInforByUserId(Integer userId) throws Exception;
    //新增问答
    BaseResp addNewAsk(Integer userId,String askName,String askText,Integer integralNeed) throws Exception;
    //获取问答详情数据
    BaseResp getAskInforByAskid(Integer askId) throws Exception;
    //添加问答回复
    BaseResp insertAnswerAsk(Integer askId,Integer userID,String answerAskText);
    //添加收藏夹事件
    BaseResp addAskToCollect(Integer askID,Integer collectid )throws Exception;
    //添加同问事件
    BaseResp addAlsoask(Integer askID,Integer userId) throws Exception;
}
