package com.sy.model;

import com.fasterxml.jackson.annotation.JsonFormat;
import org.springframework.format.annotation.DateTimeFormat;

import java.util.Date;

public class Upload {
    private Integer id;
    private Integer userid;
    private User user;
    private String title;
    @DateTimeFormat(pattern = "yyyy-MM-dd")
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss",timezone = "Alisa/Shanghai")
    private Date createtime;
    private Integer appraise;
    private Double size;
    private Double price;
    private Integer status;
//    private String category;
    private Integer categoryid;
    private Integer categoryid2;
    private Integer downloadCount;
    private Integer replyCount;
    private String leixin;
    private String src;
    private String intro;
    private Integer page;
    private Integer pageSize;
    private String name;
    private String leixin2;
    private Integer hot;

    public Integer getHot() {
        return hot;
    }

    public void setHot(Integer hot) {
        this.hot = hot;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getLeixin2() {
        return leixin2;
    }

    public void setLeixin2(String leixin2) {
        this.leixin2 = leixin2;
    }

    public Integer getReplyCount() {
        return replyCount;
    }

    public void setReplyCount(Integer replyCount) {
        this.replyCount = replyCount;
    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }

    public String getIntro() {
        return intro;
    }

    public void setIntro(String intro) {
        this.intro = intro;
    }

    public String getSrc() {
        return src;
    }

    public void setSrc(String src) {
        this.src = src;
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

    public Double getSize() {
        return size;
    }

    public void setSize(Double size) {
        this.size = size;
    }

    public Double getPrice() {
        return price;
    }

    public void setPrice(Double price) {
        this.price = price;
    }

    public Integer getStatus() {
        return status;
    }

    public void setStatus(Integer status) {
        this.status = status;
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

    public Integer getDownloadCount() {
        return downloadCount;
    }

    public void setDownloadCount(Integer downloadCount) {
        this.downloadCount = downloadCount;
    }

    public String getLeixin() {
        return leixin;
    }

    public void setLeixin(String leixin) {
        this.leixin = leixin;
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

    @Override
    public String toString() {
        return "Upload{" +
                "id=" + id +
                ", userid=" + userid +
                ", user=" + user +
                ", title='" + title + '\'' +
                ", createtime=" + createtime +
                ", appraise=" + appraise +
                ", size=" + size +
                ", price=" + price +
                ", status=" + status +
                ", categoryid=" + categoryid +
                ", categoryid2=" + categoryid2 +
                ", downloadCount=" + downloadCount +
                ", replyCount=" + replyCount +
                ", leixin='" + leixin + '\'' +
                ", src='" + src + '\'' +
                ", intro='" + intro + '\'' +
                ", page=" + page +
                ", pageSize=" + pageSize +
                '}';
    }
}
