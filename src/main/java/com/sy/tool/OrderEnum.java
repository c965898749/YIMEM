package com.sy.tool;

public enum OrderEnum {
    ORDER_STATUS_PAID(1, "已支付"),
    ORDER_STATUS_NOT_PAY(0, "待支付"),

    ORDER_STATUS_CANCEL(2, "已取消");


    private int status;
    private String statusName;

    OrderEnum(int status, String statusName) {
        this.status = status;
        this.statusName = statusName;
    }

    public int getStatus(){
        return this.status;
    }


}
