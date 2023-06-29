package com.sy.entity;

import java.io.Serializable;

/**
 * (ChatGptToken)实体类
 *
 * @author makejava
 * @since 2023-06-28 17:02:00
 */
public class ChatGptToken implements Serializable {
    private static final long serialVersionUID = 415959876697589366L;
    
    private Integer id;
    
    private String token;


    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getToken() {
        return token;
    }

    public void setToken(String token) {
        this.token = token;
    }

}

