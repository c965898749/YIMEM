package com.sy.model;

import com.fasterxml.jackson.annotation.JsonFormat;
import org.springframework.format.annotation.DateTimeFormat;

import java.time.LocalDate;
import java.time.LocalDateTime;

/**
 * @Author 施鸿烨
 * 用户表
 */
public class Users {
    private Integer userId;
    private String nickname;  //昵称
    private String username;  //用户名 系统自动生成，不可以修改
    @DateTimeFormat(pattern = "yyyy-MM-dd")
    @JsonFormat(pattern = "yyyy-MM-dd")
    private LocalDate birth;       //生日
    private Integer sex;      //性别
    @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime createTime;  //注册时间
    private String icon;      //头像
    private Integer state;    //状态
    private String remark;    //个性签名
    private String password;  //密码
    private String phonenum;  //手机号

    public Users() {
    }

    public Users(String nickname, String username, LocalDate birth, Integer sex, LocalDateTime createTime, Integer state, String remark, String password, String phonenum) {
        this.nickname = nickname;
        this.username = username;
        this.birth = birth;
        this.sex = sex;
        this.createTime = createTime;
        this.state = state;
        this.remark = remark;
        this.password = password;
        this.phonenum = phonenum;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getPhoneNum() {
        return phonenum;
    }

    public void setPhoneNum(String phoneNum) {
        this.phonenum = phoneNum;
    }

    public Integer getUserId() {
        return userId;
    }

    public void setUserId(Integer userId) {
        this.userId = userId;
    }

    public String getNickname() {
        return nickname;
    }

    public void setNickname(String nickname) {
        this.nickname = nickname;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    @JsonFormat(pattern = "yyyy-MM-dd")
    public LocalDate getBirth() {
        return birth;
    }

    public void setBirth(LocalDate birth) {
        this.birth = birth;
    }

    public Integer getSex() {
        return sex;
    }

    public void setSex(Integer sex) {
        this.sex = sex;
    }

    public LocalDateTime getCreateTime() {
        return createTime;
    }

    public void setCreateTime(LocalDateTime createTime) {
        this.createTime = createTime;
    }

    public String getIcon() {
        return icon;
    }

    public void setIcon(String icon) {
        this.icon = icon;
    }

    public Integer getState() {
        return state;
    }

    public void setState(Integer state) {
        this.state = state;
    }

    public String getRemark() {
        return remark;
    }

    public void setRemark(String remark) {
        this.remark = remark;
    }

    @Override
    public String toString() {
        return "Users{" +
                "userId=" + userId +
                ", nickname='" + nickname + '\'' +
                ", username='" + username + '\'' +
                ", birth=" + birth +
                ", sex=" + sex +
                ", createTime=" + createTime +
                ", icon='" + icon + '\'' +
                ", state=" + state +
                ", remark='" + remark + '\'' +
                ", password='" + password + '\'' +
                ", phonenum='" + phonenum + '\'' +
                '}';
    }
}
