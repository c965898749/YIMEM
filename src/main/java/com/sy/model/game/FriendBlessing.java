package com.sy.model.game;

import com.baomidou.mybatisplus.annotations.TableField;
import com.baomidou.mybatisplus.annotations.TableId;
import com.baomidou.mybatisplus.annotations.TableName;
import com.baomidou.mybatisplus.enums.IdType;
import lombok.Data;

import java.util.Date;
@Data
@TableName("friend_blessing")
public class FriendBlessing {
    @TableId(value = "id", type = IdType.AUTO)
    private Integer id;
    @TableField("sender_id")
    private Integer senderId;
    @TableField("receiver_id")
    private Integer receiverId;
    @TableField("content")
    private String content;
    @TableField("send_time")
    private Date sendTime;
    @TableField("is_read")
    private Integer isRead;

}