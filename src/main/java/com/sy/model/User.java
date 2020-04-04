package com.sy.model;

import com.fasterxml.jackson.annotation.JsonFormat;
import org.springframework.format.annotation.DateTimeFormat;

import java.util.Date;

public class User {
    private Integer userId;

    private String username;

    private String userpassword;

    private String sex;

    private String nickname;
    @DateTimeFormat(pattern = "yyyy-MM-dd")
    @JsonFormat(pattern = "yyyy-MM-dd", timezone = "Alisa/Shanghai")
    private Date birthday;

    private String provinces;

    private String city;

    private String county;

    private String industry;

    private String job;
    private Double askmoney;

    private Integer askSuminter;
    private String headImg;
    private Integer blogCount;
    private String description;
    private Integer attentionCount;
    private Integer fansCount;
    private Integer resourceCount;
    private Integer forumCount;
    private Integer askCount;
    private Integer collectCount;
    private Double downloadmoney;
    private Integer visitorCount;
    private Integer ranking;
    private Integer likeCount;
    private Integer commentCount;
    private Integer level;
    private Integer downCount;
    private Integer unreadreplaycount;
    private Integer readquerylikecount;
    private Integer unreadfanscount;

    public void setUnreadfanscount(Integer unreadfanscount) {
        this.unreadfanscount = unreadfanscount;
    }

    public Integer getUnreadfanscount() {
        return unreadfanscount;
    }

    public void setReadquerylikecount(Integer readquerylikecount) {
        this.readquerylikecount = readquerylikecount;
    }

    public Integer getReadquerylikecount() {
        return readquerylikecount;
    }

    @Override
    public String toString() {
        return "User{" +
                "userId=" + userId +
                ", username='" + username + '\'' +
                ", userpassword='" + userpassword + '\'' +
                ", sex='" + sex + '\'' +
                ", nickname='" + nickname + '\'' +
                ", birthday=" + birthday +
                ", provinces='" + provinces + '\'' +
                ", city='" + city + '\'' +
                ", county='" + county + '\'' +
                ", industry='" + industry + '\'' +
                ", job='" + job + '\'' +
                ", askmoney=" + askmoney +
                ", askSuminter=" + askSuminter +
                ", headImg='" + headImg + '\'' +
                ", blogCount=" + blogCount +
                ", description='" + description + '\'' +
                ", attentionCount=" + attentionCount +
                ", fansCount=" + fansCount +
                ", resourceCount=" + resourceCount +
                ", forumCount=" + forumCount +
                ", askCount=" + askCount +
                ", collectCount=" + collectCount +
                ", downloadmoney=" + downloadmoney +
                ", visitorCount=" + visitorCount +
                ", ranking=" + ranking +
                ", likeCount=" + likeCount +
                ", commentCount=" + commentCount +
                ", level=" + level +
                ", downCount=" + downCount +
                ", unreadreplaycount=" + unreadreplaycount +
                '}';
    }

    public Integer getUnreadreplaycount() {
        return unreadreplaycount;
    }

    public void setUnreadreplaycount(Integer unreadreplaycount) {
        this.unreadreplaycount = unreadreplaycount;
    }

    public Integer getDownCount() {
        return downCount;
    }

    public void setDownCount(Integer downCount) {
        this.downCount = downCount;
    }

    public Integer getLikeCount() {
        return likeCount;
    }

    public void setLikeCount(Integer likeCount) {
        this.likeCount = likeCount;
    }

    public Integer getCommentCount() {
        return commentCount;
    }

    public void setCommentCount(Integer commentCount) {
        this.commentCount = commentCount;
    }

    public Integer getLevel() {
        return level;
    }

    public void setLevel(Integer level) {
        this.level = level;
    }

    public Double getAskmoney() {
        return askmoney;
    }

    public void setAskmoney(Double askmoney) {
        this.askmoney = askmoney;
    }

    public Integer getVisitorCount() {
        return visitorCount;
    }

    public void setVisitorCount(Integer visitorCount) {
        this.visitorCount = visitorCount;
    }

    public Integer getRanking() {
        return ranking;
    }

    public void setRanking(Integer ranking) {
        this.ranking = ranking;
    }

    public Integer getUserId() {
        return userId;
    }

    public void setUserId(Integer userId) {
        this.userId = userId;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getUserpassword() {
        return userpassword;
    }

    public void setUserpassword(String userpassword) {
        this.userpassword = userpassword;
    }

    public String getSex() {
        return sex;
    }

    public void setSex(String sex) {
        this.sex = sex;
    }

    public String getNickname() {
        return nickname;
    }

    public void setNickname(String nickname) {
        this.nickname = nickname;
    }

    public Date getBirthday() {
        return birthday;
    }

    public void setBirthday(Date birthday) {
        this.birthday = birthday;
    }

    public String getProvinces() {
        return provinces;
    }

    public void setProvinces(String provinces) {
        this.provinces = provinces;
    }

    public String getCity() {
        return city;
    }

    public void setCity(String city) {
        this.city = city;
    }

    public String getCounty() {
        return county;
    }

    public void setCounty(String county) {
        this.county = county;
    }

    public String getIndustry() {
        return industry;
    }

    public void setIndustry(String industry) {
        this.industry = industry;
    }

    public String getJob() {
        return job;
    }

    public void setJob(String job) {
        this.job = job;
    }

    public Integer getAskSuminter() {
        return askSuminter;
    }

    public void setAskSuminter(Integer askSuminter) {
        this.askSuminter = askSuminter;
    }

    public String getHeadImg() {
        return headImg;
    }

    public void setHeadImg(String headImg) {
        this.headImg = headImg;
    }

    public Integer getBlogCount() {
        return blogCount;
    }

    public void setBlogCount(Integer blogCount) {
        this.blogCount = blogCount;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public Integer getAttentionCount() {
        return attentionCount;
    }

    public void setAttentionCount(Integer attentionCount) {
        this.attentionCount = attentionCount;
    }

    public Integer getFansCount() {
        return fansCount;
    }

    public void setFansCount(Integer fansCount) {
        this.fansCount = fansCount;
    }

    public Integer getResourceCount() {
        return resourceCount;
    }

    public void setResourceCount(Integer resourceCount) {
        this.resourceCount = resourceCount;
    }

    public Integer getForumCount() {
        return forumCount;
    }

    public void setForumCount(Integer forumCount) {
        this.forumCount = forumCount;
    }

    public Integer getAskCount() {
        return askCount;
    }

    public void setAskCount(Integer askCount) {
        this.askCount = askCount;
    }

    public Integer getCollectCount() {
        return collectCount;
    }

    public void setCollectCount(Integer collectCount) {
        this.collectCount = collectCount;
    }

    public Double getDownloadmoney() {
        return downloadmoney;
    }

    public void setDownloadmoney(Double downloadmoney) {
        this.downloadmoney = downloadmoney;
    }


}
