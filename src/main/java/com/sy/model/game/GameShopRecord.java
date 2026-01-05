package com.sy.model.game;

import com.baomidou.mybatisplus.annotations.TableField;
import com.baomidou.mybatisplus.annotations.TableId;
import com.baomidou.mybatisplus.annotations.TableName;
import com.baomidou.mybatisplus.enums.IdType;
import lombok.Data;

import java.util.Date;
@Data
@TableName("game_shop_record")
public class GameShopRecord {
    @TableId(value = "record_id", type = IdType.AUTO)
    private Integer recordId;
    @TableField("user_id")
    private Integer userId;
    @TableField("item_id")
    private Integer itemId;
    @TableField("get_time")
    private Date getTime;
    @TableField("status")
    private String status;
    @TableField("fail_reason")
    private String failReason;
    @TableField("platform")
    private String platform;
    @TableField("ip_address")
    private String ipAddress;
    @TableField("num")
    private Integer num;

}