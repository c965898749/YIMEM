package com.sy.model;

import java.util.Date;

public class T8DocManage {
    private Integer id;

    private String folderName;

    private String description;

    private String portLevel;

    private Integer parentId;

    private String isDirectory;

    private String updUsername;

    private Date updDate;

    private Date updTime;

    private String crtUsername;

    private Date crtDate;

    private Date crtTime;

    public T8DocManage(Integer id, String folderName, String description, String portLevel, Integer parentId, String isDirectory, String updUsername, Date updDate, Date updTime, String crtUsername, Date crtDate, Date crtTime) {
        this.id = id;
        this.folderName = folderName;
        this.description = description;
        this.portLevel = portLevel;
        this.parentId = parentId;
        this.isDirectory = isDirectory;
        this.updUsername = updUsername;
        this.updDate = updDate;
        this.updTime = updTime;
        this.crtUsername = crtUsername;
        this.crtDate = crtDate;
        this.crtTime = crtTime;
    }

    public T8DocManage() {
        super();
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getFolderName() {
        return folderName;
    }

    public void setFolderName(String folderName) {
        this.folderName = folderName == null ? null : folderName.trim();
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description == null ? null : description.trim();
    }

    public String getPortLevel() {
        return portLevel;
    }

    public void setPortLevel(String portLevel) {
        this.portLevel = portLevel == null ? null : portLevel.trim();
    }

    public Integer getParentId() {
        return parentId;
    }

    public void setParentId(Integer parentId) {
        this.parentId = parentId;
    }

    public String getIsDirectory() {
        return isDirectory;
    }

    public void setIsDirectory(String isDirectory) {
        this.isDirectory = isDirectory == null ? null : isDirectory.trim();
    }

    public String getUpdUsername() {
        return updUsername;
    }

    public void setUpdUsername(String updUsername) {
        this.updUsername = updUsername == null ? null : updUsername.trim();
    }

    public Date getUpdDate() {
        return updDate;
    }

    public void setUpdDate(Date updDate) {
        this.updDate = updDate;
    }

    public Date getUpdTime() {
        return updTime;
    }

    public void setUpdTime(Date updTime) {
        this.updTime = updTime;
    }

    public String getCrtUsername() {
        return crtUsername;
    }

    public void setCrtUsername(String crtUsername) {
        this.crtUsername = crtUsername == null ? null : crtUsername.trim();
    }

    public Date getCrtDate() {
        return crtDate;
    }

    public void setCrtDate(Date crtDate) {
        this.crtDate = crtDate;
    }

    public Date getCrtTime() {
        return crtTime;
    }

    public void setCrtTime(Date crtTime) {
        this.crtTime = crtTime;
    }
}