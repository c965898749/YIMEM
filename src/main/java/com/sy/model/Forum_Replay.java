package com.sy.model;

import com.fasterxml.jackson.annotation.JsonFormat;

import java.util.Date;

public class Forum_Replay {
    private Integer id;
    private String content;
    private Integer replayuserid;
    @JsonFormat(pattern ="yyyy-MM-dd ")
    private Date replaytime;

    @Override
    public String toString() {
        return "Forum_Replay{" +
                "id=" + id +
                ", content='" + content + '\'' +
                ", replayuserid=" + replayuserid +
                ", replaytime=" + replaytime +
                '}';
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public Integer getReplayuserid() {
        return replayuserid;
    }

    public void setReplayuserid(Integer replayuserid) {
        this.replayuserid = replayuserid;
    }

    public Date getReplaytime() {
        return replaytime;
    }

    public void setReplaytime(Date replaytime) {
        this.replaytime = replaytime;
    }
}
