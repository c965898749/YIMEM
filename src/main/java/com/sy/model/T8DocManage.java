package com.sy.model;

import lombok.Data;

import java.util.Date;
@Data
public class T8DocManage {
    private Integer id;

    private String folderName;

    private String description;

    private String portLevel;

    private Integer parentId;

    private String isDirectory;

    private String updUsername;

    private String username;

    private Date updDate;

    private Date updTime;

    private String crtUsername;

    private Date crtDate;

    private Date crtTime;
    private String src;
    private String size;
    private String type;
    private Integer readcount;


}
