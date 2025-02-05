package com.sy.tool;

import okhttp3.*;
import okhttp3.Request.Builder;

import java.io.*;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.util.Base64;
import java.util.Map;
import java.util.concurrent.TimeUnit;

/**
 * @author honghu
 */
public class HttpUtil {
    static OkHttpClient client = new OkHttpClient();

    /**
     * post请求xml入参
     *
     * @param url
     * @param data
     * @return
     * @throws IOException
     */
    public static String doPostXml(String url, String data) throws IOException {
        MediaType mediaType = MediaType.parse("application/octet-stream");
        RequestBody body = RequestBody.create(mediaType, data);
        Request request = new Builder()
                .url(url)
                .post(body)
                .build();
        Response response = client.newCall(request).execute();
        return response.body().string();
    }

    /**
     * post请求json入参
     *
     * @param url
     * @return
     * @throws IOException
     */
    public static String doPostJson(String url, String data) throws IOException {
        MediaType mediaType = MediaType.parse("application/json");
        RequestBody body = RequestBody.create(mediaType, data);
        Request request = new Builder()
                .url(url)
                .post(body)
                .addHeader("Content-Type", "application/json")
                .build();
        Response response = client.newCall(request).execute();
        return response.body().string();
    }

    public static String doPostJson(String url, String data, Map<String, String> headerMap) {
        MediaType mediaType = MediaType.parse("application/json");
        RequestBody body = RequestBody.create(mediaType, data);
        try {
            // 拼接参数
            OkHttpClient client2 = client.newBuilder().readTimeout(60, TimeUnit.SECONDS).build();
            Builder builder = new Builder().url(url).post(body);
            if (headerMap != null && !headerMap.isEmpty()) {
                headerMap.forEach(builder::addHeader);
            }
            // 发送请求
            Request request = builder.build();
            Response response = client2.newCall(request).execute();
            return response.body().string();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return "error";
    }

    /**
     * get请求
     *
     * @param url
     * @return
     */
    public static String httpGet(String url) {
        Response response = null;
        String result = "";
        Request request = new Builder().url(url).build();
        try {
            response = client.newCall(request).execute();
            result = response.body().string();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return result;
    }

    /**
     * 获取信贷接口请求密钥
     *
     * @param userName
     * @param passwd
     * @return
     */
    public static String getUserCreditUrlKey(String userName, String passwd) {
        try {
            final Base64.Encoder encoder = Base64.getEncoder();
            final String text = userName + ":" + passwd;
            final byte[] textByte = text.getBytes("UTF-8");
            final String encodedText = encoder.encodeToString(textByte);
            return "Basic " + encodedText;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return "error";
    }

    /**
     * get⽅式的http请求
     *
     * @param httpUrl 请求地址
     * @return 返回结果
     */
    public static String doGet(String httpUrl) {
        HttpURLConnection connection = null;
        InputStream inputStream = null;
        BufferedReader bufferedReader = null;
        String result = null;// 返回结果字符串
        try {
            // 创建远程url连接对象
            URL url = new URL(httpUrl);
            // 通过远程url连接对象打开⼀个连接，强转成httpURLConnection类
            connection = (HttpURLConnection)
                    url.openConnection();
            // 设置连接⽅式：get
            connection.setRequestMethod("GET");
            // 设置连接主机服务器的超时时间：15000毫秒
            connection.setConnectTimeout(15000);
            // 设置读取远程返回的数据时间：60000毫秒
            connection.setReadTimeout(60000);
            // 发送请求
            connection.connect();
            // 通过connection连接，获取输⼊流
            if (connection.getResponseCode() == 200) {
                inputStream = connection.getInputStream();
                // 封装输⼊流，并指定字符集
                bufferedReader = new BufferedReader(new
                        InputStreamReader(inputStream, StandardCharsets.UTF_8));
                // 存放数据
                StringBuilder sbf = new StringBuilder();
                String temp;
                while ((temp = bufferedReader.readLine()) !=
                        null) {
                    sbf.append(temp);

                    sbf.append(System.getProperty("line.separator"));
                }
                result = sbf.toString();
            }
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            // 关闭资源
            if (null != bufferedReader) {
                try {
                    bufferedReader.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
            if (null != inputStream) {
                try {
                    inputStream.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
            if (connection != null) {
                connection.disconnect();// 关闭远程连接
            }
        }
        return result;
    }
    /**
     * post⽅式的http请求
     *
     * @param httpUrl 请求地址
     * @param param 请求参数
     * @return 返回结果
     */
    public static String doPost(String httpUrl, String param,String contentType) {
        HttpURLConnection connection = null;
        InputStream inputStream = null;
        OutputStream outputStream = null;
        BufferedReader bufferedReader = null;
        String result = null;
        try {
            java.net.URL url = new URL(httpUrl);
            // 通过远程url连接对象打开连接
            connection = (HttpURLConnection)
                    url.openConnection();
            // 设置连接请求⽅式
            connection.setRequestMethod("POST");
            // 设置连接主机服务器超时时间：15000毫秒
            connection.setConnectTimeout(15000);
            // 设置读取主机服务器返回数据超时时间：60000毫秒
            connection.setReadTimeout(60000);
            // 默认值为：false，当向远程服务器传送数据/写数据时，需要设置为true
            connection.setDoOutput(true);
            // 设置传⼊参数的格式:请求参数应该是name1=value1&name2=value2 的形式。
            connection.setRequestProperty("Content-Type",contentType);
            // 通过连接对象获取⼀个输出流
            outputStream = connection.getOutputStream();
            // 通过输出流对象将参数写出去/传输出去,它是通过字节数组写出的
            outputStream.write(param.getBytes());
            // 通过连接对象获取⼀个输⼊流，向远程读取
            if (connection.getResponseCode() == 200) {
                inputStream = connection.getInputStream();
                // 对输⼊流对象进⾏包装:charset根据⼯作项⽬组的要求来设置
                bufferedReader = new BufferedReader(new
                        InputStreamReader(inputStream, StandardCharsets.UTF_8));
                StringBuilder sbf = new StringBuilder();
                String temp;
                // 循环遍历⼀⾏⼀⾏读取数据
                while ((temp = bufferedReader.readLine()) !=
                        null) {
                    sbf.append(temp);

                    sbf.append(System.getProperty("line.separator"));
                }
                result = sbf.toString();
            }
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            // 关闭资源
            if (null != bufferedReader) {
                try {
                    bufferedReader.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
            if (null != outputStream) {
                try {
                    outputStream.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
            if (null != inputStream) {
                try {
                    inputStream.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
            if (connection != null) {
                connection.disconnect();
            }
        }
        return result;
    }

}



