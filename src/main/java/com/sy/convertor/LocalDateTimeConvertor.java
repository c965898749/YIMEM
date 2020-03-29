package com.sy.convertor;

import org.springframework.core.convert.converter.Converter;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public class LocalDateTimeConvertor implements Converter<String, LocalDateTime> {
    @Override
    public LocalDateTime convert(String source) {
        System.out.println("传入字符串：:" + source);
        LocalDateTime localDateTime = LocalDateTime.parse(source, DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"));
        System.out.println("转换成LocalDate:" + localDateTime);
        return localDateTime;
    }
}
