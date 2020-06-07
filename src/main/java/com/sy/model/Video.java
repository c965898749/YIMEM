package com.sy.model;



public class Video {
    private Integer videoid;

    private Integer userid;

    private String title;

    private String subtitle;

    private Integer clickcount;

    private String coverurl;

    private String videourl;

    private Integer state;

    private Integer likecount;

    private Integer classifyid;

    private String createtime;

    private Integer collectcount;

    private String actor;

    private String type;

    private String region;

    private String director;

    private String douban;

    private String info;

    //后天字段
    private Integer pageNum;

    public Integer getPageNum() {
        return pageNum;
    }

    public void setPageNum(Integer pageNum) {
        this.pageNum = pageNum;
    }

    public Video(Integer videoid, Integer userid, String title, String subtitle, Integer clickcount, String coverurl, String videourl, Integer state, Integer likecount, Integer classifyid, String createtime, Integer collectcount, String actor, String type, String region, String director, String
            douban) {
        this.videoid = videoid;
        this.userid = userid;
        this.title = title;
        this.subtitle = subtitle;
        this.clickcount = clickcount;
        this.coverurl = coverurl;
        this.videourl = videourl;
        this.state = state;
        this.likecount = likecount;
        this.classifyid = classifyid;
        this.createtime = createtime;
        this.collectcount = collectcount;
        this.actor = actor;
        this.type = type;
        this.region = region;
        this.director = director;
        this.douban = douban;
    }

    public Video(Integer videoid, Integer userid, String title, String subtitle, Integer clickcount, String coverurl, String videourl, Integer state, Integer likecount, Integer classifyid, String createtime, Integer collectcount, String actor, String type, String region, String director, String
            douban, String info) {
        this.videoid = videoid;
        this.userid = userid;
        this.title = title;
        this.subtitle = subtitle;
        this.clickcount = clickcount;
        this.coverurl = coverurl;
        this.videourl = videourl;
        this.state = state;
        this.likecount = likecount;
        this.classifyid = classifyid;
        this.createtime = createtime;
        this.collectcount = collectcount;
        this.actor = actor;
        this.type = type;
        this.region = region;
        this.director = director;
        this.douban = douban;
        this.info = info;
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

    public String getCreatetime() {
        return createtime;
    }

    public void setCreatetime(String createtime) {
        this.createtime = createtime;
    }

    public Integer getCollectcount() {
        return collectcount;
    }

    public void setCollectcount(Integer collectcount) {
        this.collectcount = collectcount;
    }

    public String getActor() {
        return actor;
    }

    public void setActor(String actor) {
        this.actor = actor == null ? null : actor.trim();
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type == null ? null : type.trim();
    }

    public String getRegion() {
        return region;
    }

    public void setRegion(String region) {
        this.region = region == null ? null : region.trim();
    }

    public String getDirector() {
        return director;
    }

    public void setDirector(String director) {
        this.director = director == null ? null : director.trim();
    }

    public String getDouban() {
        return douban;
    }

    public void setDouban(String douban) {
        this.douban = douban;
    }

    public String getInfo() {
        return info;
    }

    public void setInfo(String info) {
        this.info = info == null ? null : info.trim();
    }
}