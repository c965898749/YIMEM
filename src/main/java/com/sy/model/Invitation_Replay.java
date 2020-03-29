package com.sy.model;

import com.fasterxml.jackson.annotation.JsonFormat;

import java.util.Date;

public class Invitation_Replay {
    private Integer invitationid;
    private String comment;
    private Integer commentuserid;
    @JsonFormat(pattern ="yyyy-MM-dd ")
    private Date time;
    private User user;

    @Override
    public String toString() {
        return "Invitation_Replay{" +
                "invitationid=" + invitationid +
                ", comment='" + comment + '\'' +
                ", commentuserid=" + commentuserid +
                ", time=" + time +
                ", user=" + user +
                '}';
    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }

    public Integer getInvitationid() {
        return invitationid;
    }

    public void setInvitationid(Integer invitationid) {
        this.invitationid = invitationid;
    }

    public String getComment() {
        return comment;
    }

    public void setComment(String comment) {
        this.comment = comment;
    }

    public Integer getCommentuserid() {
        return commentuserid;
    }

    public void setCommentuserid(Integer commentuserid) {
        this.commentuserid = commentuserid;
    }

    public Date getTime() {
        return time;
    }

    public void setTime(Date time) {
        this.time = time;
    }
}
