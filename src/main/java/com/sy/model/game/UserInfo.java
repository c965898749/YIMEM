package com.sy.model.game;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;
import org.springframework.format.annotation.DateTimeFormat;

import java.math.BigDecimal;
import java.util.Date;
import java.util.List;

@Data
public class UserInfo {
    private Integer userId;



    private String sex;

    private String nickname;
    @DateTimeFormat(pattern = "yyyy-MM-dd")
    @JsonFormat(pattern = "yyyy-MM-dd", timezone = "Alisa/Shanghai")
    private Date birthday;

    private String provinces;

    private String city;

    private String county;

    private String industry;



//游戏字段
    private BigDecimal lv;
    private BigDecimal exp;
    private BigDecimal gold;
    private BigDecimal diamond;
    private BigDecimal soul;

    //卡牌
    List<Characters> characterList;
    private String token;
    //卡池数量
    private String useCardCount;
    private Integer signCount;

    //20251028
    private String gameImg;
    //20251112
    private Integer tiliCount;
    private Date tiliCountTime;
    //20251109
    private Integer huoliCount;
    private Date huoliCountTime;
    //20251103
    private String chapter;
    //20251104
    private Integer levelUp;
    private Integer winCount;
    private String stopLevel;
    //
    private String id;
    //
    private Integer rate;

    //
    private Integer friendStatus;


    //
    private Integer fbId;
    private Integer nj;
}
