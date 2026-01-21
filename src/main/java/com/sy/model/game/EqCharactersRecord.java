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
}