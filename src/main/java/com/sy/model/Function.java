package com.sy.model;

import java.util.Date;

/**
 * Function表,功能URL链接
 */
public class Function {


    private Integer id;
    private String functionCode;
    private String functionName;
    //访问的URL链接
    private String funcUrl;
    //关联本类ID
    private int parentId;
    private Date creationTime;

    //关联authority
    private Integer roleId;

    public Integer getRoleId() {
        return roleId;
    }
    public void setRoleId(Integer roleId) {
        this.roleId = roleId;
    }
    public Integer getId() {
        return id;
    }
    public void setId(Integer id) {
        this.id = id;
    }
    public String getFunctionCode() {
        return functionCode;
    }
    public void setFunctionCode(String functionCode) {
        this.functionCode = functionCode;
    }
    public String getFunctionName() {
        return functionName;
    }
    public void setFunctionName(String functionName) {
        this.functionName = functionName;
    }
    public String getFuncUrl() {
        return funcUrl;
    }
    public void setFuncUrl(String funcUrl) {
        this.funcUrl = funcUrl;
    }
    public int getParentId() {
        return parentId;
    }
    public void setParentId(int parentId) {
        this.parentId = parentId;
    }
    public Date getCreationTime() {
        return creationTime;
    }
    public void setCreationTime(Date creationTime) {
        this.creationTime = creationTime;
    }

}
