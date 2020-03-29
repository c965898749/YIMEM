package com.sy.model;

public class Fa_att {
    private Integer faAttId;
    private Integer favoriteId;
    private Integer userId;
    private Integer collectID;

    @Override
    public String toString() {
        return "Fa_att{" +
                "faAttId=" + faAttId +
                ", favoriteId=" + favoriteId +
                ", userId=" + userId +
                ", collectID=" + collectID +
                '}';
    }

    public Integer getFaAttId() {
        return faAttId;
    }

    public void setFaAttId(Integer faAttId) {
        this.faAttId = faAttId;
    }

    public Integer getFavoriteId() {
        return favoriteId;
    }

    public void setFavoriteId(Integer favoriteId) {
        this.favoriteId = favoriteId;
    }

    public Integer getUserId() {
        return userId;
    }

    public void setUserId(Integer userId) {
        this.userId = userId;
    }

    public Integer getCollectID() {
        return collectID;
    }

    public void setCollectID(Integer collectID) {
        this.collectID = collectID;
    }
}
