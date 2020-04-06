package com.sy.model;

import java.util.List;

/**
 * 封装具有菜单功能URL链接
 */
public class Menu {

    //主菜单
    private Function mainFunction;

    //子菜单
    private List<Function> subsFunction;


    public Function getMainFunction() {
        return mainFunction;
    }

    public void setMainFunction(Function mainFunction) {
        this.mainFunction = mainFunction;
    }

    public List<Function> getSubsFunction() {
        return subsFunction;
    }

    public void setSubsFunction(List<Function> subsFunction) {
        this.subsFunction = subsFunction;
    }
}
