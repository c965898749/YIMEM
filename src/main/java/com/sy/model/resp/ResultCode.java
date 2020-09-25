package com.sy.model.resp;

import lombok.Data;

import java.io.Serializable;
@Data
public class ResultCode<T> implements Serializable {
    private T data ;
    private Integer code;
}
