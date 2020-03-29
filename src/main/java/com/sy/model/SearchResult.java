package com.sy.model;

import java.util.Date;

public class SearchResult {
    private String tittle;
    private String userName;
    private Date date ;
    private String content;

    @Override
    public String toString() {
        return "SearchResult{" +
                "tittle='" + tittle + '\'' +
                ", userName='" + userName + '\'' +
                ", date=" + date +
                ", content='" + content + '\'' +
                '}';
    }

    public String getTittle() {
        return tittle;
    }

    public void setTittle(String tittle) {
        this.tittle = tittle;
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public Date getDate() {
        return date;
    }

    public void setDate(Date date) {
        this.date = date;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }
}
