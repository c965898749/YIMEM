package com.sy.model.resp;

import java.io.Serializable;

public class BaseListResp<T> implements Serializable {
    private int ok ;  //1代表成功   0 代表失败
    private String msg ;
    private T data ;

    private int totalCount ;
    private int totalPage ;

    public BaseListResp() {
    }

    public BaseListResp(int ok, String msg, T data, int totalCount, int totalPage) {
        this.ok = ok;
        this.msg = msg;
        this.data = data;
        this.totalCount = totalCount;
        this.totalPage = totalPage;
    }

    public int getOk() {
        return ok;
    }

    public void setOk(int ok) {
        this.ok = ok;
    }

    public String getMsg() {
        return msg;
    }

    public void setMsg(String msg) {
        this.msg = msg;
    }

    public T getData() {
        return data;
    }

    public void setData(T data) {
        this.data = data;
    }


    public int getTotalCount() {
        return totalCount;
    }

    public void setTotalCount(int totalCount) {
        this.totalCount = totalCount;
    }

    public int getTotalPage() {
        return totalPage;
    }

    public void setTotalPage(int totalPage) {
        this.totalPage = totalPage;
    }

    @Override
    public String toString() {
        return "BaseListResp{" +
                "ok=" + ok +
                ", msg='" + msg + '\'' +
                ", data=" + data +
                ", totalCount=" + totalCount +
                ", totalPage=" + totalPage +
                '}';
    }
}
