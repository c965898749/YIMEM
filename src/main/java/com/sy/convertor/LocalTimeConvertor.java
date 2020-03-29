package com.sy.convertor;

import org.springframework.core.convert.converter.Converter;

import java.time.LocalTime;
import java.time.format.DateTimeFormatter;

/**
 * 从前端传过去的字符串，转换为对应LocalTime格式，
 * 放到controller的自定义类里面。
 */
public class LocalTimeConvertor implements Converter<String, LocalTime> {
    @Override
    public LocalTime convert(String source) {
        System.out.println("传入字符串：:" + source);
        LocalTime localTime = LocalTime.parse(source, DateTimeFormatter.ofPattern("HH:mm:ss"));
        System.out.println("转换成LocalTime:" + localTime);
        return localTime;
    }

    public static void main(String[] args){
        String source = "00:05";
        System.out.println(new LocalTimeConvertor().convert(source));
//        System.out.println(LocalDateTime.now().toLocalTime());
    }

}
