package com.sy.model.game;

import com.baomidou.mybatisplus.annotations.TableField;
import com.baomidou.mybatisplus.annotations.TableId;
import com.baomidou.mybatisplus.annotations.TableName;
import com.baomidou.mybatisplus.enums.IdType;
import lombok.Data;

import java.math.BigDecimal;
import java.util.Date;
import java.util.List;

@Data
@TableName("star_synthesis_main")
public class StarSynthesisMain {
    @TableId(value = "id", type = IdType.AUTO)
    private String id;
    @TableField("target_star")
    private BigDecimal targetStar;
    @TableField("success_probability")
    private BigDecimal successProbability;
    @TableField("fail_penalty")
    private String failPenalty;
    @TableField("extra_item_required")
    private String extraItemRequired;
    @TableField("extra_cost")
    private BigDecimal extraCost;
    @TableField("unlock_condition")
    private String unlockCondition;
    @TableField("create_time")
    private Date createTime;
    @TableField("update_time")
    private Date updateTime;
    @TableField(exist = false)
    private List<StarSynthesisMaterials> materials;

}