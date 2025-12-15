package com.sy.model.game;

import lombok.Data;

import java.util.List;

@Data
public class PveDetail {
    private String id;

    private String titleName;

    private String jieName;

    private String guanName;

    private String introduce;

    private List<PveBossDetail> pveBossDetails;

}