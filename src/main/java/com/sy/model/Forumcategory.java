package com.sy.model;

import java.util.List;

public class Forumcategory {
    private Integer id;
    private String title;
    private Integer pid;
    private Integer status;
    private List<Forumcategory> children;

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public Integer getPid() {
        return pid;
    }

    public void setPid(Integer pid) {
        this.pid = pid;
    }

    public List<Forumcategory> getChildren() {
        return children;
    }

    public void setChildren(List<Forumcategory> children) {
        this.children = children;
    }

    @Override
    public String toString() {
        return "Forumcategory{" +
                "id=" + id +
                ", title='" + title + '\'' +
                ", pid=" + pid +
                ", children=" + children +
                '}';
    }
}
