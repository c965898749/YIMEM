package com.sy.model;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;
import org.springframework.format.annotation.DateTimeFormat;

import java.util.Date;
@Data
public class BlogReplay {
    private Integer id;
    private Integer blogid;
    private String comment;
    private Integer commentuserid;
    @DateTimeFormat(pattern = "yyyy-MM-dd")
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss",timezone = "GMT+8")
    private Date time;
    private User user;
    private Integer status;
//    后添加字段
    private Integer replayUserId;
    private Integer sonreplaycount;
    private Integer blogReplayId;
    private Integer isRead;

    public Integer getBlogReplayId() {
        return blogReplayId;
    }

    public void setBlogReplayId(Integer blogReplayId) {
        this.blogReplayId = blogReplayId;
    }

    public Integer getReplayUserId() {
        return replayUserId;
    }

    public void setReplayUserId(Integer replayUserId) {
        this.replayUserId = replayUserId;
    }

    public Integer getBlogid() {
        return blogid;
    }

    public void setBlogid(Integer blogid) {
        this.blogid = blogid;
    }

    public Integer getSonreplaycount() {
        return sonreplaycount;
    }

    public void setSonreplaycount(Integer sonreplaycount) {
        this.sonreplaycount = sonreplaycount;
    }

    public void setStatus(Integer status) {
        this.status = status;
    }

    public Integer getStatus() {
        return status;
    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }


    public String getComment() {
        return comment;
    }

    public void setComment(String comment) {
        this.comment = comment;
    }

    public Integer getCommentuserid() {
        return commentuserid;
    }

    public void setCommentuserid(Integer commentuserid) {
        this.commentuserid = commentuserid;
    }

    public Date getTime() {
        return time;
    }

    public void setTime(Date time) {
        this.time = time;
    }
}
