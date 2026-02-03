package com.sy.model.game;

import com.baomidou.mybatisplus.annotations.TableField;
import com.baomidou.mybatisplus.annotations.TableId;
import com.baomidou.mybatisplus.annotations.TableName;
import com.baomidou.mybatisplus.enums.IdType;
import lombok.Data;

import java.math.BigDecimal;
import java.util.Date;
@Data
@TableName("eq_characters_record")
public class EqCharactersRecord {
    @TableId(value = "record_id", type = IdType.AUTO)
    private Long recordId;
    @TableField("user_id")
    private Integer userId;
    @TableField("id")
    private String id;
    @TableField("eq_name")
    private String eqName;
    @TableField("get_time")
    private Date getTime;
    @TableField("status")
    private Byte status;
    @TableField("fail_reason")
    private String failReason;
    @TableField("platform")
    private String platform;
    @TableField("ip_address")
    private String ipAddress;
    @TableField("user_name")
    private String userName;
    @TableField("img")
    private String img;
    @TableField("eq_img")
    private String eqImg;
    @TableField("star")
    private BigDecimal star;
    @TableField(exist = false)
    private String timeStr;
    @TableField(exist = false)
    private Integer wlAtk;
    @TableField(exist = false)
    private Integer hyAtk;
    @TableField(exist = false)
    private Integer dsAtk;
    @TableField(exist = false)
    private Integer fdAtk;
    @TableField(exist = false)
    private Integer wlDef;
    @TableField(exist = false)
    private Integer hyDef;
    @TableField(exist = false)
    private Integer dsDef;
    @TableField(exist = false)
    private Integer fdDef;
    @TableField(exist = false)
    private Integer zlDef;
    @TableField(exist = false)
    private String profession;
    @TableField(exist = false)
    private String introduce;
    @TableField(exist = false)
    private String camp;
    @TableField(exist = false)
    private String name;
    @TableField(exist = false)
    private Integer eqType;
    @TableField(exist = false)
    private Integer eqType2;
    @TableField(exist = false)
    private Integer lv;
}