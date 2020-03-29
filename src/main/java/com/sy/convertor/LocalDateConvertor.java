package com.sy.convertor;

import org.springframework.core.convert.converter.Converter;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

/**
 * 从前端传过去的字符串，转换为对应LocalDate格式，
 * 放到controller的自定义类里面。
 */
public class LocalDateConvertor implements Converter<String, LocalDate> {

    @Override
    public LocalDate convert(String source) {
        System.out.println("传入字符串：:" + source);
        LocalDate localDate = LocalDate.parse(source, DateTimeFormatter.ofPattern("yyyy-MM-dd"));
        System.out.println("转换成LocalDate:" + localDate);
        return localDate;
    }
}
