package com.sy.tool;


import com.alibaba.fastjson.JSON;

import java.util.Map;

/**
 * 谜语大全
 * @Author ZhaoShuHao
 * @create 2023/08/26
 */
public class RddleUtil {
    //申请接口的请求key
    // TODO: 您需要改为自己的请求key
    public static final String KEY = "536491fb7c76998bc12ca75a99febb21";

    //谜语大全
    public static final String URL_C = "http://apis.juhe.cn/fapigx/riddle/query?key=%s";

    public static void main(String[] args) {
        getRddle();
    }
    //获取谜语
    public static String getRddle(){
        String url =String.format(URL_C,KEY);
        String result =  "对不起大佬，谜语大全出了点问题，请联系管理员小弟";
        try {
            String rddle = HttpUtil.doGet(url);;
            Map map = JSON.parseObject(rddle, Map.class);
            Map result1 = (Map) map.get("result");
            String quest = (String) result1.get("quest");
            String answer = (String) result1.get("answer");
            result= "大佬请猜猜谜语："+"\r\n\r\n\r\n"+quest+"\r\n\r\n\r\n"+"您猜到了吗？"+"\r\n\r\n\r\n\r\n\r\n"+"让小子为您揭晓谜底吧，它就是："+"\r\n\r\n\r\n\r\n\r\n"+answer;
            System.out.println(result);
        }catch (Exception e){
            e.printStackTrace();
        }
        return result;
    }
}
