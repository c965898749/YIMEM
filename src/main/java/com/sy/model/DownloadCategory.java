package com.sy.model;

import java.util.List;

public class DownloadCategory {
    private Integer id;
    private String categoryname;
    private Integer pid;
    private List<DownloadCategory> children;

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }


    public String getCategoryname() {
        return categoryname;
    }

    public void setCategoryname(String categoryname) {
        this.categoryname = categoryname;
    }

    public Integer getPid() {
        return pid;
    }

    public void setPid(Integer pid) {
        this.pid = pid;
    }

    public List<DownloadCategory> getChildren() {
        return children;
    }

    public void setChildren(List<DownloadCategory> children) {
        this.children = children;
    }

    @Override
    public String toString() {
        return "DownloadCategory{" +
                "id=" + id +
                ", categoryname='" + categoryname + '\'' +
                ", pid=" + pid +
                ", children=" + children +
                '}';
    }
}
