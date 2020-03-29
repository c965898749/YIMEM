package com.sy.service.impl;

import com.github.pagehelper.Page;
import com.github.pagehelper.PageHelper;
import com.sy.mapper.AskMapper;
import com.sy.mapper.UserMapper;
import com.sy.model.*;
import com.sy.model.resp.BaseResp;
import com.sy.service.AskService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;

@Service
@Transactional(readOnly = true)
public class AskServiceImpl implements AskService {
    @Autowired
    private AskMapper askMapper;
    @Autowired
    private UserMapper userMapper;
    //根据条件获取问答页面数据
    @Override
    public BaseResp selectAsksByCondition(Integer solve, Integer award,Integer time, Integer askNum,Integer page)throws Exception {
        BaseResp baseResp=new BaseResp();
        Ask ask=new Ask();
        ask.setSolve(solve);
        ask.setAward(award);
        PageHelper.startPage(page, 5);
        List<Ask> askList=askMapper.selectAsksByCondition(ask,time,askNum);

        Page<Ask> pageInfo = (Page<Ask>) askList;
        long count=pageInfo.getTotal();
        int allpage=pageInfo.getPages();

        if(askList.size()>0){
            for (Ask ask1:askList){
                List<Se_label> se_labelList=new ArrayList<>();
                List<Ask_label> ask_labelList=askMapper.selectAsklabelsByAskId(ask1.getAskId());
                for(Ask_label askLabel:ask_labelList){
                    Se_label se_label=askMapper.selectSelabelByLabelId(askLabel.getLabelId());
                    se_labelList.add(se_label);
                }
                User user=userMapper.selectUserByUserId(ask1.getUserId());
                ask1.setSe_labelList(se_labelList);
                ask1.setUsername(user.getUsername());
                Integer answerCount=askMapper.selectAnswerCountByAskId(ask1.getAskId());
                Integer attentionCount=askMapper.selectAskInvPerCountByAskID(ask1.getAskId());
                Integer alsoAskCount=askMapper.selectAllAlsoAskConuntByAskID(ask1.getAskId());
                ask1.setAlsoAskCount(alsoAskCount);
                ask1.setAnswerCount(answerCount);
                ask1.setAttentionCount(attentionCount);
            }
        }

        if (time==0&&askNum==1){
            Collections.sort(askList, new Comparator<Ask>() {
                @Override
                public int compare(Ask o1, Ask o2) {
                    return o2.getAnswerCount()-o1.getAnswerCount();
                }
            });
        }
        if(askList.size()>0){
            baseResp.setSuccess(1);
            baseResp.setData(askList);
            baseResp.setCount(count);
            baseResp.setPage(allpage);
            baseResp.setErrorMsg("查找成功");
        }else {
            baseResp.setSuccess(0);
            baseResp.setErrorMsg("查找失败");
        }
        return baseResp;
    }
    //获取问答页面个人信息页面数据
    @Override
    public BaseResp getUserInforByUserId(Integer userId) throws Exception {
        BaseResp baseResp=new BaseResp();
        List<Ask> askList=askMapper.selectAsksByUserId(userId);
        User user=userMapper.selectUserByUserId(userId);
        int askCount=askList.size();
        int myAnswerCount=askMapper.selectAnswerCountByUserId(userId);
        int alsoCount=askMapper.selectAllAlsoAskConuntByUserID(userId);
        int hasAnswerCount=0;
        for (Ask ask:askList){
            hasAnswerCount=hasAnswerCount+askMapper.selectAnswerCountByAskId(ask.getAskId());
        }
        Map<String,Object> inforMap=new HashMap<>();
        inforMap.put("askCount",askCount);
        inforMap.put("myAnswerCount",myAnswerCount);
        inforMap.put("alsoCount",alsoCount);
        inforMap.put("hasAnswerCount",hasAnswerCount);
        inforMap.put("user",user);
        baseResp.setSuccess(1);
        baseResp.setData(inforMap);
        baseResp.setErrorMsg("问答页个人信息获取成功");
        return baseResp;
    }
    //新增问答
    @Override
    @Transactional(propagation = Propagation.REQUIRED)
    public BaseResp addNewAsk(Integer userId, String askName, String askText, Integer integralNeed) throws Exception {
      BaseResp baseResp=new BaseResp();
       Ask ask=new Ask();
       ask.setUserId(userId);
       ask.setAskName(askName);
       ask.setAskText(askText);
       ask.setIntegralNeed(integralNeed);
       if(integralNeed!=0){
           double askmoney=userMapper.selectAskmoneybyUserID(userId);
           if(askmoney<integralNeed){
               baseResp.setSuccess(0);
               baseResp.setErrorMsg("余额不足");
           }else {
               int result=askMapper.insertNewAsk(ask);
               userMapper.updateAskMoneyByUserID(userId,integralNeed);
               if(result>0){
                   baseResp.setSuccess(1);
                   baseResp.setData(ask.getAskId());
                   baseResp.setErrorMsg("问答添加成功");
               }else {
                   baseResp.setSuccess(0);
                   baseResp.setErrorMsg("问答添加失败");
               }
           }
       }else {
           int result=askMapper.insertNewAsk(ask);
           userMapper.updateAskMoneyByUserID(userId,integralNeed);
           if(result>0){
               baseResp.setSuccess(1);
               baseResp.setData(ask.getAskId());
               baseResp.setErrorMsg("问答添加成功");
           }else {
               baseResp.setSuccess(0);
               baseResp.setErrorMsg("问答添加失败");
           }
       }
        return baseResp;
    }
    //获取问答详情数据
    @Override
    public BaseResp getAskInforByAskid(Integer askId) throws Exception {
        Ask ask=askMapper.selectAskByAskid(askId);
        BaseResp baseResp=new BaseResp();
        if (ask!=null){
            List<Answer_ask> answer_askList=askMapper.selectAllAnswerByAskid(askId);
            for (Answer_ask answerAsk:answer_askList){
                Integer answerId=answerAsk.getUserId();
                User user=userMapper.selectUserByUserId(answerId);
                answerAsk.setAnswername(user.getUsername());
            }
            List<Ask_label> ask_labelList=askMapper.selectAsklabelsByAskId(askId);
            List<Se_label> se_labelList=new ArrayList<>();
            for (Ask_label askLabel:ask_labelList){
                Integer labelId=askLabel.getLabelId();
                Se_label se_label=askMapper.selectSelabelByLabelId(labelId);
               se_labelList.add(se_label);
            }
            ask.setSe_labelList(se_labelList);
            User user=userMapper.selectUserByUserId(ask.getUserId());
            ask.setAnswerCount(answer_askList.size());
            ask.setUsername(user.getUsername());
            ask.setAnswer_askList(answer_askList);
            baseResp.setErrorMsg("问答详情获取成功");
            baseResp.setSuccess(1);
            baseResp.setData(ask);
        }else {
            baseResp.setErrorMsg("问答详情获取失败");
            baseResp.setSuccess(0);
            baseResp.setData(ask);
        }
        return baseResp;
    }
    //添加问答回复
    @Override
    @Transactional(propagation = Propagation.REQUIRED)
    public BaseResp insertAnswerAsk(Integer askId, Integer userID, String answerAskText) {
        BaseResp baseResp=new BaseResp();
        Ask ask=askMapper.selectAskByAskid(askId);
        if (userID==ask.getUserId()){
            baseResp.setErrorMsg("自己的回答就不要回复了嘛！！");
            baseResp.setSuccess(0);
        }else {
            int result=askMapper.insertAnswerAsk(askId,userID,answerAskText);
            if(result>0){
                baseResp.setErrorMsg("回复成功");
                baseResp.setSuccess(1);
            }else {
                baseResp.setErrorMsg("回复失败");
                baseResp.setSuccess(2);
            }
        }

        return baseResp;
    }
    //添加收藏夹事件
    @Override
    @Transactional(propagation = Propagation.REQUIRED)
    public BaseResp addAskToCollect(Integer askID, Integer collectid) throws Exception {
        BaseResp baseResp=new BaseResp();
        int count=askMapper.selectByAskIdAndCollectid(askID,collectid);
        if(count>0){
            baseResp.setSuccess(0);
            baseResp.setErrorMsg("该收藏夹已收藏过无需重复收藏！");
        }else {
            int result=askMapper.addAskTocollect(askID,collectid);
            if (result>0){
                baseResp.setSuccess(1);
                baseResp.setErrorMsg("添加成功！");
            }else {
                baseResp.setSuccess(0);
                baseResp.setErrorMsg("添加失败！");
            }
        }
        return baseResp;
    }
    //添加同问事件
    @Override
    @Transactional(propagation = Propagation.REQUIRED)
    public BaseResp addAlsoask(Integer askID, Integer userId) throws Exception {
        BaseResp baseResp=new BaseResp();
        Ask ask=askMapper.selectAskByAskid(askID);
        if(ask.getUserId().equals(userId)){
            baseResp.setSuccess(0);
            baseResp.setErrorMsg("自己的问提无须同问");
        }else {
            int count=askMapper.selectalsoaskByaskidAndUserId(askID,userId);
            if(count>0){
                baseResp.setSuccess(0);
                baseResp.setErrorMsg("该问题您已同问过无须重复同问");
            }else {
                int result=askMapper.insertNewAlsoask(askID,userId);
                if(result>0){
                    baseResp.setSuccess(1);
                    baseResp.setErrorMsg("同问成功");
                }else {
                    baseResp.setSuccess(1);
                    baseResp.setErrorMsg("同问失败");
                }
            }
        }
        return baseResp;
    }
}
