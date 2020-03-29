package com.sy.model;

import com.fasterxml.jackson.annotation.JsonFormat;
import org.springframework.format.annotation.DateTimeFormat;

import java.util.Date;

public class Forum {
    private Integer forumId;

    private String forumTitle;

    @DateTimeFormat(pattern = "yyyy-MM-dd")
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "Asia/Shanghai")
    private Date forumCreatime;
    private String forumContext;
    private Integer userId;
    //数据库不用此字段做逻辑用
    private String username;
    private String type;
    private User user;


    @Override
    public String toString() {
        return "Forum{" +
                "forumId=" + forumId +
                ", forumTitle='" + forumTitle + '\'' +
                ", forumCreatime=" + forumCreatime +
                ", forumContext='" + forumContext + '\'' +
                ", userId=" + userId +
                ", username='" + username + '\'' +
                ", type='" + type + '\'' +
                ", user=" + user +
                '}';
    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }

    public String getForumContext() {
        return forumContext;
    }

    public void setForumContext(String forumContext) {
        this.forumContext = forumContext;
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

    public Integer getForumId() {
        return forumId;
    }

    public void setForumId(Integer forumId) {
        this.forumId = forumId;
    }

    public String getForumTitle() {
        return forumTitle;
    }

    public void setForumTitle(String forumTitle) {
        this.forumTitle = forumTitle;
    }

    public Date getForumCreatime() {
        return forumCreatime;
    }

    public void setForumCreatime(Date forumCreatime) {
        this.forumCreatime = forumCreatime;
    }

    public Integer getUserId() {
        return userId;
    }

    public void setUserId(Integer userId) {
        this.userId = userId;
    }
}