package com.sy.model;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;
import org.springframework.format.annotation.DateTimeFormat;

import java.time.LocalDateTime;
import java.time.LocalTime;

@Data
public class Bullet {
    private Integer bulletid;

    private Integer videoid;
    @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime createtime;
//    @DateTimeFormat(pattern = "HH:mm:ss")
//    @JsonFormat(pattern = "HH:mm:ss")
//    private LocalTime currenttime;
    private String currenttime;

    private Integer userid;

    private String msg;

    private String color;

    private String type;


}
