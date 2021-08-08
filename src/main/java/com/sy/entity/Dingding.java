package com.sy.entity;

import java.io.Serializable;

/**
 * (Dingding)实体类
 *
 * @author makejava
 * @since 2021-08-08 15:29:28
 */
public class Dingding implements Serializable {
    private static final long serialVersionUID = -42462200035568141L;

    private Integer id;

    private Integer status;


    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public Integer getStatus() {
        return status;
    }

    public void setStatus(Integer status) {
        this.status = status;
    }

}
