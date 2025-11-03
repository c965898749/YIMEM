package com.sy.tool;

import com.alibaba.fastjson.JSON;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.sy.model.game.Fightter;

import java.util.List;
import java.util.Map;

public class JsonUtils {
    private static final ObjectMapper objectMapper = new ObjectMapper();

    // 将对象转换为JSON字符串
    public static String toJson(Object obj) {
        try {
            return objectMapper.writeValueAsString(obj);
        } catch (Exception e) {
            throw new RuntimeException("对象转JSON失败", e);
        }
    }

    // 将JSON字符串转换为集合对象（以List<String>为例，其他集合类似）
    public static List<String> fromJsonToList(String json) {
        try {
            // TypeReference用于指定泛型类型（避免类型擦除问题）
            return objectMapper.readValue(json, new TypeReference<List<String>>() {});
        } catch (Exception e) {
            throw new RuntimeException("JSON转集合失败", e);
        }
    }

    // 扩展：转换为自定义对象的集合（例如List<User>）
    public static Object fromJsonToObjList(String json) {
        try {
//            return objectMapper.readValue(json, new TypeReference<Map>() {});
            return  JSON.parse(json);
        } catch (Exception e) {
            throw new RuntimeException("JSON转User集合失败", e);
        }
    }
}
