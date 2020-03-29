package com.sy.model;

import com.fasterxml.jackson.annotation.JsonFormat;
import org.springframework.format.annotation.DateTimeFormat;

import java.util.Date;

public class Collectitems {
    private Integer id;
    private Integer blogid;
    private Integer uploadID;
    private Integer askID;
    private Integer forumID;
    private Integer collectid;
    @DateTimeFormat(pattern = "yyyy-MM-dd")
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss",timezone = "Asia/Shanghai")
    private Date createTime;

    @Override
    public String toString() {
        return "Collectitems{" +
                "id=" + id +
                ", blogid=" + blogid +
                ", uploadID=" + uploadID +
                ", askID=" + askID +
                ", forumID=" + forumID +
                ", collectid=" + collectid +
                ", createTime=" + createTime +
                '}';
    }

    public Date getCreateTime() {
        return createTime;
    }

    public void setCreateTime(Date createTime) {
        this.createTime = createTime;
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public Integer getBlogid() {
        return blogid;
    }

    public void setBlogid(Integer blogid) {
        this.blogid = blogid;
    }

    public Integer getUploadID() {
        return uploadID;
    }

    public void setUploadID(Integer uploadID) {
        this.uploadID = uploadID;
    }

    public Integer getAskID() {
        return askID;
    }

    public void setAskID(Integer askID) {
        this.askID = askID;
    }

    public Integer getForumID() {
        return forumID;
    }

    public void setForumID(Integer forumID) {
        this.forumID = forumID;
    }

    public Integer getCollectid() {
        return collectid;
    }

    public void setCollectid(Integer collectid) {
        this.collectid = collectid;
    }
}
