package com.sy.model;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;
import org.springframework.format.annotation.DateTimeFormat;

import java.util.Date;
@Data
public class App {
    private Integer id;
    @DateTimeFormat(pattern = "yyyy-MM-dd")
    @JsonFormat(pattern = "yyyy-MM-dd",timezone = "Asia/Shanghai")
    private Date creatime;

    private String describe;

    private String title;

    private Integer downloadid;

    public App(Integer id, Date creatime, String describe, String title, Integer downloadid) {
        this.id = id;
        this.creatime = creatime;
        this.describe = describe;
        this.title = title;
        this.downloadid = downloadid;
    }
}
