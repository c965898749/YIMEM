package com.sy.entity;

import com.alibaba.fastjson.annotation.JSONField;
import lombok.Data;

import java.util.List;

/**
 * @author honghu
 * 发送前配置
 */
@Data
public class MessageSendBody {

    private String model;

    private String prompt;

    private double temperature;
    /**
     * 貌似指的是语句的最大字符数
     */
    @JSONField(name = "max_tokens")
    private int maxTokens;

    @JSONField(name = "top_p")
    private int topP;

    @JSONField(name = "frequency_penalty")
    private double frequencyPenalty;

    @JSONField(name = "presence_penalty")
    private double presencePenalty;

    /**
     * 表明语句结束的标志
     */
    List<String> stop;

}
