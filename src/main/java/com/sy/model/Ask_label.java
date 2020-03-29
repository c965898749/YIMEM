package com.sy.model;

public class Ask_label {
    private Integer id;
    private Integer askId;
    private Integer labelId;

    @Override
    public String toString() {
        return "Ask_label{" +
                "id=" + id +
                ", askId=" + askId +
                ", labelId=" + labelId +
                '}';
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public Integer getAskId() {
        return askId;
    }

    public void setAskId(Integer askId) {
        this.askId = askId;
    }

    public Integer getLabelId() {
        return labelId;
    }

    public void setLabelId(Integer labelId) {
        this.labelId = labelId;
    }
}
