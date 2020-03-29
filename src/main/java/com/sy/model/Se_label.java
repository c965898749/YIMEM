package com.sy.model;

public class Se_label {
    private Integer seLabelId;
    private String seLabelName;
    private Integer firLabelId;

    @Override
    public String toString() {
        return "Se_label{" +
                "seLabelId=" + seLabelId +
                ", seLabelName='" + seLabelName + '\'' +
                ", firLabelId=" + firLabelId +
                '}';
    }

    public Integer getSeLabelId() {
        return seLabelId;
    }

    public void setSeLabelId(Integer seLabelId) {
        this.seLabelId = seLabelId;
    }

    public String getSeLabelName() {
        return seLabelName;
    }

    public void setSeLabelName(String seLabelName) {
        this.seLabelName = seLabelName;
    }

    public Integer getFirLabelId() {
        return firLabelId;
    }

    public void setFirLabelId(Integer firLabelId) {
        this.firLabelId = firLabelId;
    }
}
