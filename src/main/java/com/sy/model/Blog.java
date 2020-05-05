package com.sy.model;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;
import org.springframework.format.annotation.DateTimeFormat;

import java.util.Date;
@Data
public class Blog {
    private Integer id;

    private Integer userid;

    private String title;

    private String content;

    private Integer status;

    private Integer readCount;
    @DateTimeFormat(pattern = "yyyy-MM-dd")
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss",timezone = "Asia/Shanghai")
    private Date createtime;
    private Integer timeCount;
    private Integer recomment;

    private String resource;

    private String lable;

    private String publishForm;

    private String category;
    private String type;

    private String img;
    //数据库不用此字段做逻辑用
    private Integer pageNum;

    private String username;

    private String headimg;

    private Integer stick;

    private Integer likeCount;

    private Integer replayCount;

    private String userIndustry;

    private String userDescr;

    private Integer userFansCount;

    private String serachblog;




}
