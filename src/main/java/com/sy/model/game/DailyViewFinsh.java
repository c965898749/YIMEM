package com.sy.model.game;

import com.baomidou.mybatisplus.annotations.TableField;
import com.baomidou.mybatisplus.annotations.TableId;
import com.baomidou.mybatisplus.annotations.TableName;
import com.baomidou.mybatisplus.enums.IdType;
import lombok.Data;

import java.util.Date;
@Data
@TableName("daily_view_finsh")
public class DailyViewFinsh {
    @TableId(value = "id", type = IdType.AUTO)
    private Long id;
    @TableField("gift_code")
    private String giftCode;
    @TableField("user_id")
    private Integer userId;
    @TableField("get_time")
    private Date getTime;
}