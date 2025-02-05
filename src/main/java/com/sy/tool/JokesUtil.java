package com.sy.tool;

import net.sf.json.JSONArray;
import net.sf.json.JSONObject;

import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.util.ArrayList;
import java.util.List;


/**
 * 笑话大全工具类
 * @Author ZhaoShuHao
 * @create 2023/08/25
 */
public class JokesUtil {
    //接口请求地址
    //按更新时间查询笑话
    public static final String URL_A = "http://v.juhe.cn/joke/content/list.php?key=%s&&time=%d&pagesize=%d";

    //最新笑话
    public static final String URL_B = "http://v.juhe.cn/joke/content/text.php?key=%s&pagesize=%d";

    //随机笑话
    public static final String URL_C = "http://v.juhe.cn/joke/randJoke.php?key=%s";

    //申请接口的请求key
    // TODO: 您需要改为自己的请求key
    public static final String KEY = "0b2273f36f7be68b3a78bd9c4d2279b4";


    public static void main(String[] args) {

        // TODO: 日期

        //时间戳
        long time = LocalDateTime.now().toEpochSecond(ZoneOffset.of("+8"));
        //每页数量
        int pageSize = 2;

       /* System.out.println("------------按更新时间查询笑话-----------------");
        printA(time, pageSize);
        System.out.println("------------最新笑话-----------------");
        printB(pageSize);*/
        System.out.println("------------随机笑话-----------------");
        String s = printC();
        System.out.println(s);
    }
    /**
     * 随机笑话
     *
     */
    public static String printC() {
        //发送http请求的url
        String url = String.format(URL_C, KEY);
        final String response = HttpUtil.doGet(url);
        StringBuilder contents = new StringBuilder();
        List<String> list = new ArrayList<>();
        System.out.println("接口返回：" + response);
        final int[] num = {0};
        try {
            JSONObject jsonObject = JSONObject.fromObject(response);
            int error_code = jsonObject.getInt("error_code");
            if (error_code == 0) {
                System.out.println("调用接口成功");
                JSONArray result = jsonObject.getJSONArray("result");
                result.stream().map(JSONObject::fromObject).forEach(hour -> {
                    list.add(((JSONObject) hour).getString("content"));
                    System.out.println("content：" + ((JSONObject) hour).getString("content"));
                    System.out.println("hashId：" + ((JSONObject) hour).getString("hashId"));
                    System.out.println("unixtime：" + ((JSONObject) hour).getString("unixtime"));
                });

            } else {
                System.out.println("调用接口失败：" + jsonObject.getString("reason"));
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        contents.append("1、"+ list.get(0)+"\r\n")
                .append("2、"+ list.get(3)+"\r\n")
                .append("3、"+ list.get(5)+"\r\n")
                .append("4、"+ list.get(7)+"\r\n")
                .append("5、"+ list.get(9)+"\r\n");;
        return String.valueOf(contents);
    }
    /**
     * 最新笑话
     *
     * @param pageSize int 每页数量
     */
    public static void printB( int pageSize) {
        //发送http请求的url
        String url = String.format(URL_B, KEY, pageSize);
        final String response = HttpUtil.doGet(url);
        System.out.println("接口返回：" + response);
        try {
            JSONObject jsonObject = JSONObject.fromObject(response);
            int error_code = jsonObject.getInt("error_code");
            if (error_code == 0) {
                System.out.println("调用接口成功");
                JSONArray result = jsonObject.getJSONObject("result").getJSONArray("data");
                result.stream().map(JSONObject::fromObject).forEach(hour -> {
                    System.out.println("content：" + ((JSONObject) hour).getString("content"));
                    System.out.println("hashId：" + ((JSONObject) hour).getString("hashId"));
                    System.out.println("unixtime：" + ((JSONObject) hour).getString("unixtime"));
                    System.out.println("updatetime：" + ((JSONObject) hour).getString("updatetime"));
                });

            } else {
                System.out.println("调用接口失败：" + jsonObject.getString("reason"));
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * 按更新时间查询笑话
     *
     * @param time     long 时间戳
     * @param pageSize int 每页数量
     */
    public static void printA(long time, int pageSize) {
        //发送http请求的url
        String url = String.format(URL_A, KEY, time, pageSize);

        final String response = HttpUtil.doGet(url);
        System.out.println("接口返回：" + response);
        try {
            JSONObject jsonObject = JSONObject.fromObject(response);
            int error_code = jsonObject.getInt("error_code");
            if (error_code == 0) {
                System.out.println("调用接口成功");
                JSONArray result = jsonObject.getJSONObject("result").getJSONArray("data");
                result.stream().map(JSONObject::fromObject).forEach(hour -> {
                    System.out.println("content：" + ((JSONObject) hour).getString("content"));
                    System.out.println("hashId：" + ((JSONObject) hour).getString("hashId"));
                    System.out.println("unixtime：" + ((JSONObject) hour).getString("unixtime"));
                    System.out.println("updatetime：" + ((JSONObject) hour).getString("updatetime"));
                });

            } else {
                System.out.println("调用接口失败：" + jsonObject.getString("reason"));
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }



}
