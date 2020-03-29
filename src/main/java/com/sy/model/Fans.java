package com.sy.model;

public class Fans {
    private Integer id;
    private Integer fansedid ;
    private Integer fansid;
    private Integer status;


    private String fansname;

    @Override
    public String toString() {
        return "Fans{" +
                "id=" + id +
                ", fansedid=" + fansedid +
                ", fansid=" + fansid +
                ", fansname='" + fansname + '\'' +
                '}';
    }

    public Integer getStatus() {
        return status;
    }

    public void setStatus(Integer status) {
        this.status = status;
    }

    public String getFansname() {
        return fansname;
    }

    public void setFansname(String fansname) {
        this.fansname = fansname;
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public Integer getFansedid() {
        return fansedid;
    }

    public void setFansedid(Integer fansedid) {
        this.fansedid = fansedid;
    }

    public Integer getFansid() {
        return fansid;
    }

    public void setFansid(Integer fansid) {
        this.fansid = fansid;
    }
}
