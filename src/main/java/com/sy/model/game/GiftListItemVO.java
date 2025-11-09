package com.sy.model.game;

import lombok.Data;

import java.time.LocalDateTime;
import java.util.List;

@Data
public class GiftListItemVO {
    private Long giftId;
    private String giftCode;
    private String giftName;
    private String description;
    private Integer giftType; // 礼包类型（对应枚举）
    private LocalDateTime startTime; // 生效时间
    private LocalDateTime endTime; // 过期时间
    private List<GiftContentVO> contents; // 礼包内容（简化展示）
}
