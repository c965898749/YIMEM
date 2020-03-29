package com.sy.model;

import java.util.List;

/**
 * @Author 施鸿烨
 * 分类表，同小米中的Category表。
 * 自关联
 */
public class Classify {
    private Integer id;

    private String name;

    private String level;

    private Integer pid;

    private Integer state;

    private List<Classify> AllClassify;

    public List<Classify> getAllClassify() {
        return AllClassify;
    }

    public void setAllClassify(List<Classify> allClassify) {
        AllClassify = allClassify;
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getLevel() {
        return level;
    }

    public void setLevel(String level) {
        this.level = level;
    }

    public Integer getPid() {
        return pid;
    }

    public void setPid(Integer pid) {
        this.pid = pid;
    }

    public Integer getState() {
        return state;
    }

    public void setState(Integer state) {
        this.state = state;
    }

    @Override
    public String toString() {
        return "Classify{" +
                "id=" + id +
                ", name='" + name + '\'' +
                ", level='" + level + '\'' +
                ", pid=" + pid +
                ", state=" + state +
                ", AllClassify=" + AllClassify +
                '}';
    }
}
