package com.sy.model;

import com.fasterxml.jackson.annotation.JsonFormat;
import org.springframework.format.annotation.DateTimeFormat;

import java.util.Date;

public class Invitation {
    private Integer id ;
    private Integer userid;
    private String title;
    private String content;
    private Integer status;
    private Integer readCount;
    @DateTimeFormat(pattern = "yyyy-MM-dd")
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss",timezone = "Alisa/Shanghai")
    private Date createtime;
    private Integer recomment;
    private String resource;
    private String lable;
    private String publishForm;
    private Integer categoryid;
    private Integer categoryid2;
    private String img;
    private User user;
    private Integer page;
    private Integer pageSize;
    private Integer replaycount;

    @Override
    public String toString() {
        return "Invitation{" +
                "id=" + id +
                ", userid=" + userid +
                ", title='" + title + '\'' +
                ", content='" + content + '\'' +
                ", status=" + status +
                ", readCount=" + readCount +
                ", createtime=" + createtime +
                ", recomment=" + recomment +
                ", resource='" + resource + '\'' +
                ", lable='" + lable + '\'' +
                ", publishForm='" + publishForm + '\'' +
                ", categoryid=" + categoryid +
                ", categoryid2=" + categoryid2 +
                ", img='" + img + '\'' +
                ", user=" + user +
                ", page=" + page +
                ", pageSize=" + pageSize +
                ", replaycount=" + replaycount +
                '}';
    }

    public Integer getReplaycount() {
        return replaycount;
    }

    public void setReplaycount(Integer replaycount) {
        this.replaycount = replaycount;
    }

    public Integer getPage() {
        return page;
    }

    public void setPage(Integer page) {
        this.page = page;
    }

    public Integer getPageSize() {
        return pageSize;
    }

    public void setPageSize(Integer pageSize) {
        this.pageSize = pageSize;
    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }

    public String getImg() {
        return img;
    }

    public void setImg(String img) {
        this.img = img;
    }

    public Integer getCategoryid() {
        return categoryid;
    }

    public void setCategoryid(Integer categoryid) {
        this.categoryid = categoryid;
    }

    public Integer getCategoryid2() {
        return categoryid2;
    }

    public void setCategoryid2(Integer categoryid2) {
        this.categoryid2 = categoryid2;
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public Integer getUserid() {
        return userid;
    }

    public void setUserid(Integer userid) {
        this.userid = userid;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public Integer getStatus() {
        return status;
    }

    public void setStatus(Integer status) {
        this.status = status;
    }

    public Integer getReadCount() {
        return readCount;
    }

    public void setReadCount(Integer readCount) {
        this.readCount = readCount;
    }

    public Date getCreatetime() {
        return createtime;
    }

    public void setCreatetime(Date createtime) {
        this.createtime = createtime;
    }

    public Integer getRecomment() {
        return recomment;
    }

    public void setRecomment(Integer recomment) {
        this.recomment = recomment;
    }

    public String getResource() {
        return resource;
    }

    public void setResource(String resource) {
        this.resource = resource;
    }

    public String getLable() {
        return lable;
    }

    public void setLable(String lable) {
        this.lable = lable;
    }

    public String getPublishForm() {
        return publishForm;
    }

    public void setPublishForm(String publishForm) {
        this.publishForm = publishForm;
    }


}
