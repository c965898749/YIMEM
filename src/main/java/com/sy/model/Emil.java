package com.sy.model;

public class Emil {
    private Integer id;

    private Integer userId;

    private String emil;

    public Emil(Integer id, Integer userId, String emil) {
        this.id = id;
        this.userId = userId;
        this.emil = emil;
    }

    public Emil() {
        super();
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public Integer getUserId() {
        return userId;
    }

    public void setUserId(Integer userId) {
        this.userId = userId;
    }

    public String getEmil() {
        return emil;
    }

    public void setEmil(String emil) {
        this.emil = emil == null ? null : emil.trim();
    }
}