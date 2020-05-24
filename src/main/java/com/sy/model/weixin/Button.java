package com.sy.model.weixin;

import lombok.Data;

@Data
public class Button {
    //菜单类型
    private String type;
    //菜单名称
    private String name;
    //二级菜单
    private Button[] sub_button;

}
