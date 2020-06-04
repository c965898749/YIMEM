package com.sy.model;

import java.util.Date;

public class Video {
    private Integer videoid;

    private Integer userid;

    private String title;

    private String subtitle;

    private Integer clickcount;

    private String coverurl;

    private String videourl;

    private String info;

    private Integer state;

    private Integer likecount;

    private Integer classifyid;

    private Date createtime;

    private Integer collectcount;

    public Video(Integer videoid, Integer userid, String title, String subtitle, Integer clickcount, String coverurl, String videourl, String info, Integer state, Integer likecount, Integer classifyid, Date createtime, Integer collectcount) {
        this.videoid = videoid;
        this.userid = userid;
        this.title = title;
        this.subtitle = subtitle;
        this.clickcount = clickcount;
        this.coverurl = coverurl;
        this.videourl = videourl;
        this.info = info;
        this.state = state;
        this.likecount = likecount;
        this.classifyid = classifyid;
        this.createtime = createtime;
        this.collectcount = collectcount;
    }

    public Video() {
        super();
    }

    public Integer getVideoid() {
        return videoid;
    }

    public void setVideoid(Integer videoid) {
        this.videoid = videoid;
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
        this.title = title == null ? null : title.trim();
    }

    public String getSubtitle() {
        return subtitle;
    }

    public void setSubtitle(String subtitle) {
        this.subtitle = subtitle == null ? null : subtitle.trim();
    }

    public Integer getClickcount() {
        return clickcount;
    }

    public void setClickcount(Integer clickcount) {
        this.clickcount = clickcount;
    }

    public String getCoverurl() {
        return coverurl;
    }

    public void setCoverurl(String coverurl) {
        this.coverurl = coverurl == null ? null : coverurl.trim();
    }

    public String getVideourl() {
        return videourl;
    }

    public void setVideourl(String videourl) {
        this.videourl = videourl == null ? null : videourl.trim();
    }

    public String getInfo() {
        return info;
    }

    public void setInfo(String info) {
        this.info = info == null ? null : info.trim();
    }

    public Integer getState() {
        return state;
    }

    public void setState(Integer state) {
        this.state = state;
    }

    public Integer getLikecount() {
        return likecount;
    }

    public void setLikecount(Integer likecount) {
        this.likecount = likecount;
    }

    public Integer getClassifyid() {
        return classifyid;
    }

    public void setClassifyid(Integer classifyid) {
        this.classifyid = classifyid;
    }

    public Date getCreatetime() {
        return createtime;
    }

    public void setCreatetime(Date createtime) {
        this.createtime = createtime;
    }

    public Integer getCollectcount() {
        return collectcount;
    }

    public void setCollectcount(Integer collectcount) {
        this.collectcount = collectcount;
    }
}