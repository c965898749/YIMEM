package com.sy.tool;

import com.baidu.aip.ocr.AipOcr;
import org.json.JSONArray;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

public class ImageUtil {
    //百度AI的个人相关信息
    public static final String APP_ID = "38135852";
    public static final String API_KEY = "GimuujuwA63R1xqYtonSC3Fw";
    public static final String SECRET_KEY = "0K2bfiGowe4cEG9yvRoAr35GUG9dC4YV";
    //借用百度Api实现网络图片文字识别
    public static  String handleImage(Map<String, String> map) {
        // 初始化⼀个AipOcr
        AipOcr client = new AipOcr(APP_ID, API_KEY, SECRET_KEY);
        // ⽹络图⽚⽂字识别, 图⽚参数为远程url图⽚
        String url = map.get("PicUrl");
        JSONObject res = client.webImageUrl(url, new HashMap<String,String>());
        System.out.println(res.toString(2));
        JSONArray wordsResult = res.getJSONArray("words_result");
        StringBuilder stringBuilder = new StringBuilder();
        stringBuilder.append("大佬瞅我眼不眼熟：\r\n\r\n\r\n\r\n");
        Iterator<Object> iterator = wordsResult.iterator();
        while(iterator.hasNext()){
            JSONObject next = (JSONObject) iterator.next();
            stringBuilder.append(next.getString("words"));
        }
        stringBuilder.append("\r\n\r\n\r\n\r\n"+"没错，小弟我啊，还能文字识图，给个关注吧！");
        return  stringBuilder.toString();
    }

}
