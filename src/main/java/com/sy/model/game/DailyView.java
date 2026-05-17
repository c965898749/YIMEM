package com.sy.model.game;

import com.baomidou.mybatisplus.annotations.TableField;
import com.baomidou.mybatisplus.annotations.TableId;
import com.baomidou.mybatisplus.annotations.TableName;
import com.baomidou.mybatisplus.enums.IdType;
import lombok.Data;

import java.util.Date;
@Data
@TableName("daily_view")
public class DailyView {
    @TableId(value = "gift_id", type = IdType.AUTO)
    private Long giftId;
    @TableField("gift_code")
    private String giftCode;
    @TableField("gift_name")
    private String giftName;
    @TableField("gift_type")
    private Byte giftType;
    @TableField("total_quantity")
    private Integer totalQuantity;
    @TableField("remaining_quantity")
    private Integer remainingQuantity;
    @TableField("start_time")
    private Date startTime;
    @TableField("end_time")
    private Date endTime;
    @TableField("is_active")
    private Byte isActive;
    @TableField("create_time")
    private Date createTime;
    @TableField("update_time")
    private Date updateTime;
    @TableField("description")
    private String description;


}