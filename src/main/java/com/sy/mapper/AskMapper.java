package com.sy.mapper;

import com.sy.model.*;
import org.apache.ibatis.annotations.Param;

import java.util.List;

public interface AskMapper {
    //根据条件查询所有问答明细
    List<Ask> selectAsksByCondition(@Param("ask") Ask ask,@Param("time")Integer time,@Param("askNum")Integer askNum);
    //根据问答Id查询标签
    List<Ask_label> selectAsklabelsByAskId(Integer askId);
   //根据标签ID查询标签
    Se_label selectSelabelByLabelId(Integer labelId);
    //根据问答Id获取回答总数量
    int selectAnswerCountByAskId(Integer askId);
    //根据问答ID获取同问数量
    int selectAllAlsoAskConuntByAskID(Integer askId);
    //根据userID获取同问数量
    int selectAllAlsoAskConuntByUserID(Integer userId);
    //根据问答ID获取问答关注人数
    int selectAskInvPerCountByAskID(Integer askId);
    //根据UserId获取所有问答
    List<Ask> selectAsksByUserId(Integer userId);
    //根据UserId获取回答总数
    int selectAnswerCountByUserId(Integer userId);
    //新增问答
    int insertNewAsk(Ask ask);
    //根据问答ID获取问答明细
    Ask selectAskByAskid(Integer askId);
    //根据问答ID获取所有回答
    List<Answer_ask> selectAllAnswerByAskid(Integer askId);
    //添加问答回复
    int insertAnswerAsk(@Param("askId")Integer askId,@Param("userID")Integer userID,@Param("answerAskText")String answerAskText);
    //添加收藏夹事件
    int addAskTocollect(@Param("askID")Integer askID,@Param("collectid")Integer collectid);
    //根据问答ID和收藏夹ID查看是否收藏过记录
    int selectByAskIdAndCollectid(@Param("askID")Integer askID,@Param("collectid")Integer collectid);
    //新增同问记录
    int insertNewAlsoask(@Param("askId")Integer askId,@Param("userId")Integer userId);
    //根据问答ID和userID查询是否同问过
    int selectalsoaskByaskidAndUserId(@Param("askId")Integer askId,@Param("userId")Integer userId);







}
