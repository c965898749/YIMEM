package com.sy.model;

import com.fasterxml.jackson.annotation.JsonFormat;
import org.springframework.format.annotation.DateTimeFormat;

import java.util.Date;
import java.util.List;

public class Ask {
    private Integer askId;

    private String askName;
    @DateTimeFormat(pattern = "yyyy-MM-dd")
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss",timezone = "Asia/Shanghai")
    private Date createTime;

    private Integer solve;

    private Integer award;

    private Integer integralNeed;

    private Integer userId;

    private String askText;
    //数据库不用此字段做逻辑用
    private String username;
    private String type;

    private Integer answerCount;
    private Integer attentionCount;
    private Integer alsoAskCount;

    private List<Se_label> se_labelList;
    private List<Answer_ask> answer_askList;

    @Override
    public String toString() {
        return "Ask{" +
                "askId=" + askId +
                ", askName='" + askName + '\'' +
                ", createTime=" + createTime +
                ", solve=" + solve +
                ", award=" + award +
                ", integralNeed=" + integralNeed +
                ", userId=" + userId +
                ", askText='" + askText + '\'' +
                ", username='" + username + '\'' +
                ", type='" + type + '\'' +
                ", answerCount=" + answerCount +
                ", attentionCount=" + attentionCount +
                ", alsoAskCount=" + alsoAskCount +
                ", se_labelList=" + se_labelList +
                ", answer_askList=" + answer_askList +
                '}';
    }

    public List<Answer_ask> getAnswer_askList() {
        return answer_askList;
    }

    public void setAnswer_askList(List<Answer_ask> answer_askList) {
        this.answer_askList = answer_askList;
    }

    public Integer getAttentionCount() {
        return attentionCount;
    }

    public void setAttentionCount(Integer attentionCount) {
        this.attentionCount = attentionCount;
    }

    public Integer getAlsoAskCount() {
        return alsoAskCount;
    }

    public void setAlsoAskCount(Integer alsoAskCount) {
        this.alsoAskCount = alsoAskCount;
    }

    public List<Se_label> getSe_labelList() {
        return se_labelList;
    }

    public void setSe_labelList(List<Se_label> se_labelList) {
        this.se_labelList = se_labelList;
    }

    public Integer getAnswerCount() {
        return answerCount;
    }

    public void setAnswerCount(Integer answerCount) {
        this.answerCount = answerCount;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public Integer getAskId() {
        return askId;
    }

    public void setAskId(Integer askId) {
        this.askId = askId;
    }

    public String getAskName() {
        return askName;
    }

    public void setAskName(String askName) {
        this.askName = askName;
    }

    public Date getCreateTime() {
        return createTime;
    }

    public void setCreateTime(Date createTime) {
        this.createTime = createTime;
    }

    public Integer getSolve() {
        return solve;
    }

    public void setSolve(Integer solve) {
        this.solve = solve;
    }

    public Integer getAward() {
        return award;
    }

    public void setAward(Integer award) {
        this.award = award;
    }

    public Integer getIntegralNeed() {
        return integralNeed;
    }

    public void setIntegralNeed(Integer integralNeed) {
        this.integralNeed = integralNeed;
    }

    public Integer getUserId() {
        return userId;
    }

    public void setUserId(Integer userId) {
        this.userId = userId;
    }

    public String getAskText() {
        return askText;
    }

    public void setAskText(String askText) {
        this.askText = askText;
    }
}