package com.sy.tool;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.URL;
import java.net.URLConnection;

/**
 * @author CZX
 * @version 1.0
 * @date 2021/5/5 0005 7:23
 */
public class BaiduApiUtil {
    private static final String URL = "http://data.zz.baidu.com/urls";

    public static String Post(String site, String token, String urlsStr) {
        String result = "";
        PrintWriter out = null;
        BufferedReader in = null;
        try {
            //建立URL之间的连接
            URLConnection conn = new URL(URL + "?site=" + site + "&token=" + token).openConnection();
            //设置通用的请求属性
            conn.setRequestProperty("Host", "data.zz.baidu.com");
            conn.setRequestProperty("User-Agent", "curl/7.12.1");
            conn.setRequestProperty("Content-Length", "83");
            conn.setRequestProperty("Content-Type", "text/plain");

            //发送POST请求必须设置如下两行
            conn.setDoInput(true);
            conn.setDoOutput(true);

            //获取conn对应的输出流
            out = new PrintWriter(conn.getOutputStream());

            out.print(urlsStr);
            //进行输出流的缓冲
            out.flush();
            //通过BufferedReader输入流来读取Url的响应
            in = new BufferedReader(new InputStreamReader(conn.getInputStream()));
            String line;
            while ((line = in.readLine()) != null) {
                result += line;
            }
        } catch (Exception e) {
            System.out.println("发送post请求出现异常！" + e);
        } finally {
            try {
                if (out != null) {
                    out.close();
                }
                if (in != null) {
                    in.close();
                }

            } catch (IOException ex) {
                ex.printStackTrace();
            }
        }
        return result;
    }

    //Array转String
    public static String urlsArrToString(String[] urlsArr) {
        String tempResult = "";
        for (int i = 0; i < urlsArr.length; i++) {
            if (i == urlsArr.length - 1) {
                tempResult += urlsArr[i].trim();
            } else {
                tempResult += (urlsArr[i].trim() + "\n");
            }
        }
        return tempResult;
    }
}
