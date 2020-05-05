package com.sy.model.resp;

import java.io.Serializable;

public class Page<T> implements Serializable {
    private T items;
    private Integer page;
    private Integer prevPages;
    private Integer nextPages;
    private Integer pageCount;

    public T getItems() {
        return items;
    }

    public void setItems(T items) {
        this.items = items;
    }

    public Integer getPage() {
        return page;
    }

    public void setPage(Integer page) {
        this.page = page;
    }

    public Integer getPrevPages() {
        return prevPages;
    }

    public void setPrevPages(Integer prevPages) {
        this.prevPages = prevPages;
    }

    public Integer getNextPages() {
        return nextPages;
    }

    public void setNextPages(Integer nextPages) {
        this.nextPages = nextPages;
    }

    public Integer getPageCount() {
        return pageCount;
    }

    public void setPageCount(Integer pageCount) {
        this.pageCount = pageCount;
    }
}
