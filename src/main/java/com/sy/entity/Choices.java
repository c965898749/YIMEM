package com.sy.entity;

import lombok.Data;

@Data
public class Choices {

    private String text;

    private String index;

    private String logprobs;

    private String finish_reason;

}
