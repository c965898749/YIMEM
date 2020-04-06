package com.sy.model;

import java.util.Date;

/**
 * 权限类(对应中间表)
 */
public class Authority {

    //关联Role
    private int roleId;
    //关联Function
    private int functionId;
    private int userTypeId;
    private Date creationTime;
    private String createdBy;

    public String getCreatedBy() {
        return createdBy;
    }
    public void setCreatedBy(String createdBy) {
        this.createdBy = createdBy;
    }
    public int getRoleId() {
        return roleId;
    }
    public void setRoleId(int roleId) {
        this.roleId = roleId;
    }
    public int getFunctionId() {
        return functionId;
    }
    public void setFunctionId(int functionId) {
        this.functionId = functionId;
    }
    public int getUserTypeId() {
        return userTypeId;
    }
    public void setUserTypeId(int userTypeId) {
        this.userTypeId = userTypeId;
    }
    public Date getCreationTime() {
        return creationTime;
    }
    public void setCreationTime(Date creationTime) {
        this.creationTime = creationTime;
    }

}
