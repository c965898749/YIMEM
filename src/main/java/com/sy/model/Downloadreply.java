package com.sy.model;

import com.fasterxml.jackson.annotation.JsonFormat;
import org.springframework.format.annotation.DateTimeFormat;

import java.util.Date;

public class Downloadreply {
    private Integer id;
    private Integer userid;
    private String content;
    @DateTimeFormat(pattern = "yyyy-MM-dd")
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss",timezone = "Alisa/Shanghai")
    private Date createtime;
    private Integer appraise;
    private Integer dowid;
    private User user;

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

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public Date getCreatetime() {
        return createtime;
    }

    public void setCreatetime(Date createtime) {
        this.createtime = createtime;
    }

    public Integer getAppraise() {
        return appraise;
    }

    public void setAppraise(Integer appraise) {
        this.appraise = appraise;
    }

    public Integer getDowid() {
        return dowid;
    }

    public void setDowid(Integer dowid) {
        this.dowid = dowid;
    }

    @Override
    public String toString() {
        return "Downloadreply{" +
                "id=" + id +
                ", userid=" + userid +
                ", content='" + content + '\'' +
                ", createtime=" + createtime +
                ", appraise=" + appraise +
                ", dowid=" + dowid +
                ", user=" + user +
                '}';
    }
}
