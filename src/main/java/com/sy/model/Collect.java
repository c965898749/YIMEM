package com.sy.model;

import java.util.List;

public class Collect {
    private Integer id;
    private String name;
    private Integer userid;
    private String collectDescribe;
    //收藏夹数据总记录
    private Integer dataCount;
    //收藏夹关注总人数
    private Integer invCount;
    //用户姓名(数据库不用加 作为service业务用)
    private String username;

   private List<Ask> askList;
   private List<Blog> blogList;
   private List<Forum> forumList;
   private List<Object> objectList;
//杨
    private Integer isCollectBlogId;

    @Override
    public String toString() {
        return "Collect{" +
                "id=" + id +
                ", name='" + name + '\'' +
                ", userid=" + userid +
                ", collectDescribe='" + collectDescribe + '\'' +
                ", dataCount=" + dataCount +
                ", invCount=" + invCount +
                ", username='" + username + '\'' +
                ", askList=" + askList +
                ", blogList=" + blogList +
                ", forumList=" + forumList +
                ", objectList=" + objectList +
                ", isCollectBlogId=" + isCollectBlogId +
                '}';
    }

    public Integer getIsCollectBlogId() {
        return isCollectBlogId;
    }

    public void setIsCollectBlogId(Integer isCollectBlogId) {
        this.isCollectBlogId = isCollectBlogId;
    }

    public List<Ask> getAskList() {
        return askList;
    }

    public void setAskList(List<Ask> askList) {
        this.askList = askList;
    }

    public List<Blog> getBlogList() {
        return blogList;
    }

    public void setBlogList(List<Blog> blogList) {
        this.blogList = blogList;
    }

    public List<Forum> getForumList() {
        return forumList;
    }

    public void setForumList(List<Forum> forumList) {
        this.forumList = forumList;
    }

    public List<Object> getObjectList() {
        return objectList;
    }

    public void setObjectList(List<Object> objectList) {
        this.objectList = objectList;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Integer getUserid() {
        return userid;
    }

    public void setUserid(Integer userid) {
        this.userid = userid;
    }

    public String getCollectDescribe() {
        return collectDescribe;
    }

    public void setCollectDescribe(String collectDescribe) {
        this.collectDescribe = collectDescribe;
    }

    public Integer getDataCount() {
        return dataCount;
    }

    public void setDataCount(Integer dataCount) {
        this.dataCount = dataCount;
    }

    public Integer getInvCount() {
        return invCount;
    }

    public void setInvCount(Integer invCount) {
        this.invCount = invCount;
    }
}
