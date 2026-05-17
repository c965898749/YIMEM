package com.sy.model;

import lombok.Data;

import java.time.LocalDateTime;
import java.util.List;

@Data
public class DailyListItemVO {
    private Long giftId;
    private String giftCode;
    private String giftName;
    private String description;
    private Integer giftType; // 礼包类型（对应枚举）
    private LocalDateTime startTime; // 生效时间
    private LocalDateTime endTime; // 过期时间
    private List<DailyContentVO> contents; // 礼包内容（简化展示）
    private String isFinsh;
    private Integer totalQuantity;
    private Integer remainingQuantity;
}
