package com.sy.model;

import com.fasterxml.jackson.annotation.JsonFormat;
import org.springframework.format.annotation.DateTimeFormat;

import java.util.Date;

public class Answer_ask {
    private Integer answerAskId;
    private String answerAskText;
    private Integer askId;
    private Integer userId;
    @DateTimeFormat(pattern = "yyyy-MM-dd")
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss",timezone = "Asia/Shanghai")
    private Date createtime;

    private String answername;

    @Override
    public String toString() {
        return "Answer_ask{" +
                "answerAskId=" + answerAskId +
                ", answerAskText='" + answerAskText + '\'' +
                ", askId=" + askId +
                ", userId=" + userId +
                ", createtime=" + createtime +
                ", answername='" + answername + '\'' +
                '}';
    }

    public String getAnswername() {
        return answername;
    }

    public void setAnswername(String answername) {
        this.answername = answername;
    }

    public Integer getAnswerAskId() {
        return answerAskId;
    }

    public void setAnswerAskId(Integer answerAskId) {
        this.answerAskId = answerAskId;
    }

    public String getAnswerAskText() {
        return answerAskText;
    }

    public void setAnswerAskText(String answerAskText) {
        this.answerAskText = answerAskText;
    }

    public Integer getAskId() {
        return askId;
    }

    public void setAskId(Integer askId) {
        this.askId = askId;
    }

    public Integer getUserId() {
        return userId;
    }

    public void setUserId(Integer userId) {
        this.userId = userId;
    }

    public Date getCreatetime() {
        return createtime;
    }

    public void setCreatetime(Date createtime) {
        this.createtime = createtime;
    }
}
