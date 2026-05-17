package com.sy.model.game;

import com.baomidou.mybatisplus.annotations.TableField;
import com.baomidou.mybatisplus.annotations.TableId;
import com.baomidou.mybatisplus.annotations.TableName;
import com.baomidou.mybatisplus.enums.IdType;
import lombok.Data;

import java.util.Date;
@Data
@TableName("daily_view_record")
public class DailyViewRecord {
    @TableId(value = "record_id", type = IdType.AUTO)
    private Long recordId;
    @TableField("user_id")
    private Long userId;
    @TableField("gift_id")
    private Long giftId;
    @TableField("gift_code")
    private String giftCode;
    @TableField("get_time")
    private Date getTime;
    @TableField("status")
    private Integer status;
    @TableField("fail_reason")
    private String failReason;
    @TableField("platform")
    private String platform;
    @TableField("ip_address")
    private String ipAddress;


}