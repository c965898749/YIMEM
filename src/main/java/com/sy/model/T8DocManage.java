package com.sy.model;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;
import org.springframework.format.annotation.DateTimeFormat;

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
    @DateTimeFormat(pattern = "yyyy-MM-dd")
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss",timezone = "Alisa/Shanghai")
    private Date updDate;
    @DateTimeFormat(pattern = "yyyy-MM-dd")
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss",timezone = "Alisa/Shanghai")
    private Date updTime;

    private String crtUsername;
    @DateTimeFormat(pattern = "yyyy-MM-dd")
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss",timezone = "Alisa/Shanghai")
    private Date crtDate;
    @DateTimeFormat(pattern = "yyyy-MM-dd")
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss",timezone = "Alisa/Shanghai")
    private Date crtTime;
    private String src;
    private String size;
    private String type;
    private Integer readcount;


}
