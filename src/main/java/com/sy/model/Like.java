package com.sy.model;

public class Like {
    private Integer id;
    private Integer blog_id  ;
    private Integer userid;
    private Integer status;

    @Override
    public String toString() {
        return "Like{" +
                "id=" + id +
                ", blog_id=" + blog_id +
                ", userid=" + userid +
                '}';
    }

    public Integer getStatus() {
        return status;
    }

    public void setStatus(Integer status) {
        this.status = status;
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public Integer getBlog_id() {
        return blog_id;
    }

    public void setBlog_id(Integer blog_id) {
        this.blog_id = blog_id;
    }

    public Integer getUserid() {
        return userid;
    }

    public void setUserid(Integer userid) {
        this.userid = userid;
    }
}
