package com.sy.tool;


import com.alibaba.fastjson.JSON;

import java.util.Map;

/**
 * 心灵鸡汤
 * @Author ZhaoShuHao
 * @create 2023/08/26
 */
public class SoupUtil {
    //申请接口的请求key
    // TODO: 您需要改为自己的请求key
    public static final String KEY = "dab1d8cc1e8604b7399bdd59d4c3e87d";

    //谜语大全
    public static final String URL_C = "https://apis.juhe.cn/fapig/soup/query?key=%s";

    public static void main(String[] args) {
        getSoup();
    }
    //获取谜语
    public static String getSoup(){
        String url =String.format(URL_C,KEY);
        String result =  "对不起大佬，心灵鸡汤出了点问题，请联系管理员小弟";
        try {
            String soup = HttpUtil.doGet(url);;
            System.out.println(soup);
            Map map = JSON.parseObject(soup, Map.class);
            Map result1 = (Map) map.get("result");
            String quest = (String) result1.get("text");
            result= "Good Good Study,Day Day Up;"+"\r\n\r\n\r\n"+"大佬正能量哦："+"\r\n\r\n\r\n"+quest+"\r\n\r\n\r\n"+"传播新思想，弘扬正能量，大佬要开心哦！";
            System.out.println(result);
        }catch (Exception e){
            e.printStackTrace();
        }
        return result;
    }
}
