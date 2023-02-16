package com.sy.entity;
import lombok.Data;

import java.util.List;

/**
 * @author honghu
 */
@Data
public class MessageResponseBody {

    private String id;

    private String object;

    private int create;

    private String model;

    private List<Choices> choices;

}
