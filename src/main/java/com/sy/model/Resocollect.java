package com.sy.model;

import com.fasterxml.jackson.annotation.JsonFormat;

import java.util.Date;

public class Resocollect {
    private Integer id;
    private Integer userid;
    private Integer dowid;
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss" ,timezone = "Asia/Shanghai")
    private Date createtime;
    private Integer replystate;
    private String title;
    private Double price;
    private Double size;
    private String leixin;
    private String leixin2;

    public String getLeixin2() {
        return leixin2;
    }

    public void setLeixin2(String leixin2) {
        this.leixin2 = leixin2;
    }

    public Double getPrice() {
        return price;
    }

    public void setPrice(Double price) {
        this.price = price;
    }

    public Double getSize() {
        return size;
    }

    public void setSize(Double size) {
        this.size = size;
    }

    public String getLeixin() {
        return leixin;
    }

    public void setLeixin(String leixin) {
        this.leixin = leixin;
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

    public Integer getDowid() {
        return dowid;
    }

    public void setDowid(Integer dowid) {
        this.dowid = dowid;
    }

    public Date getCreatetime() {
        return createtime;
    }

    public void setCreatetime(Date createtime) {
        this.createtime = createtime;
    }

    public Integer getReplystate() {
        return replystate;
    }

    public void setReplystate(Integer replystate) {
        this.replystate = replystate;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    @Override
    public String toString() {
        return "Download{" +
                "id=" + id +
                ", userid=" + userid +
                ", dowid=" + dowid +
                ", createtime=" + createtime +
                ", replystate=" + replystate +
                ", title='" + title + '\'' +
                ", price=" + price +
                ", size=" + size +
                ", leixin='" + leixin + '\'' +
                '}';
    }
}
