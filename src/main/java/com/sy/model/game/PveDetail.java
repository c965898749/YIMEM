package com.sy.model.game;

public class PveDetail {
    private String id;

    private String titleName;

    private String jieName;

    private String guanName;

    private String introduce;

    public PveDetail(String id, String titleName, String jieName, String guanName, String introduce) {
        this.id = id;
        this.titleName = titleName;
        this.jieName = jieName;
        this.guanName = guanName;
        this.introduce = introduce;
    }

    public PveDetail() {
        super();
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id == null ? null : id.trim();
    }

    public String getTitleName() {
        return titleName;
    }

    public void setTitleName(String titleName) {
        this.titleName = titleName == null ? null : titleName.trim();
    }

    public String getJieName() {
        return jieName;
    }

    public void setJieName(String jieName) {
        this.jieName = jieName == null ? null : jieName.trim();
    }

    public String getGuanName() {
        return guanName;
    }

    public void setGuanName(String guanName) {
        this.guanName = guanName == null ? null : guanName.trim();
    }

    public String getIntroduce() {
        return introduce;
    }

    public void setIntroduce(String introduce) {
        this.introduce = introduce == null ? null : introduce.trim();
    }
}