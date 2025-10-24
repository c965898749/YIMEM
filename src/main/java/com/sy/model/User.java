package com.sy.model;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;
import org.springframework.format.annotation.DateTimeFormat;

import java.math.BigDecimal;
import java.util.Date;
@Data
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
    private Integer unReadQueryLikecount;
    private Integer unreadfanscount;
    private Integer roleId;
    private String menus;
//    后天字段
    private Integer isStart;
    private Integer referCode;
    private Integer referId;
    private String userType;
    private Date createTime;
    private String roleName;
    private String userTypeName;
    private String isEmil;
    private String openid;
    private Integer status;

//游戏字段
    private BigDecimal lv;
    private BigDecimal exp;
    private BigDecimal gold;
    private BigDecimal diamond;
    private BigDecimal soul;

    //20251007
    private Integer gameRanking;
    private Integer winCount;

    //20251016
    private Integer signCount;
    private Date signTime;
}
