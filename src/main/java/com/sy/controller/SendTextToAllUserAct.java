package com.sy.controller;
import com.alibaba.fastjson.JSONObject;
import com.sy.model.Content;
import com.sy.tool.FileUpload;
//import org.apache.commons.lang.StringUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.StatusLine;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.HttpResponseException;
import org.apache.http.client.ResponseHandler;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.conn.ssl.SSLConnectionSocketFactory;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClientBuilder;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.util.EntityUtils;
//import org.json.JSONException;
//import org.json.JSONObject;
import org.springframework.web.bind.annotation.RequestMapping;

import javax.net.ssl.SSLContext;
import javax.net.ssl.TrustManager;
import javax.net.ssl.X509TrustManager;
import java.io.IOException;
import java.net.URI;
import java.security.cert.CertificateException;
import java.security.cert.X509Certificate;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * 控制器
 */
public class SendTextToAllUserAct {

    /**
     * 群发文章微信
     *
     * @param ids 内容id
     * @return
     */
    @RequestMapping("/content/o_sendToWeixin.do")
    public String sendToWeixin(Integer[] ids) {

        Content[] beans = new Content[ids.length];
        for (int i = 0; i < ids.length; i++) {
//            beans[i] = contentMng.findById(ids[i]); //查询所有要发送的内容
        }
        sendTextToAllUser(beans);
        return null;
    }


    /**
     * 群发
     */
    public void sendTextToAllUser(Content[] beans) {
        String access_token = getToken();
        //上传内容到微信
        /**
         * 上传图文消息素材
         * 官方接口文档：https://developers.weixin.qq.com/doc/offiaccount/Message_Management/Batch_Sends_and_Originality_Checks.html#%E4%B8%8A%E4%BC%A0%E5%9B%BE%E6%96%87%E6%B6%88%E6%81%AF%E7%B4%A0%E6%9D%90%E3%80%90%E8%AE%A2%E9%98%85%E5%8F%B7%E4%B8%8E%E6%9C%8D%E5%8A%A1%E5%8F%B7%E8%AE%A4%E8%AF%81%E5%90%8E%E5%9D%87%E5%8F%AF%E7%94%A8%E3%80%91
         */
        String articalUploadUrl = "https://api.weixin.qq.com/cgi-bin/media/uploadnews";
        String url = articalUploadUrl + "?access_token=" + access_token;

        String[] str = articalUpload(access_token, beans);
        Integer contentCount = 0;
        contentCount = Integer.parseInt(str[1]);
        if (contentCount > 0) {
            HttpClientBuilder httpClientBuilder = HttpClientBuilder.create();
            //HttpClient
            CloseableHttpClient client = httpClientBuilder.build();
            client = (CloseableHttpClient) wrapClient(client);
            HttpPost post = new HttpPost(url);
            try {
                System.out.println("str[0]:" + str[0]);
                StringEntity s = new StringEntity(str[0], "utf-8");
                s.setContentType("application/json");
                post.setEntity(s);
                HttpResponse res = client.execute(post);
                HttpEntity entity = res.getEntity();
                String contentString = EntityUtils.toString(entity, "utf-8");
                System.out.println("contentString:" + contentString);
                JSONObject json = new JSONObject(contentString);
                //输出返回消息
                String media_id = "";
                media_id = json.getString("media_id");
//                if (StringUtils.isNotBlank(media_id)) {
                if (StringUtils.isNotBlank(media_id)) {
                    /**
                     *图文消息群发
                     * 官方接口文档：https://developers.weixin.qq.com/doc/offiaccount/Message_Management/Batch_Sends_and_Originality_Checks.html
                     */
                    String sendAllMessageUrl = "https://api.weixin.qq.com/cgi-bin/message/mass/sendall";
                    String url_send = sendAllMessageUrl + "?access_token=" + token;
                    String str_send = "{\"filter\":{\"is_to_all\":true},\"mpnews\":{\"media_id\":\"" + media_id + "\"},\"msgtype\":\"mpnews\"}";
                    post(url_send, str_send, "application/json");
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    private String post(String url, String json, String contentType) {
        HttpClientBuilder httpClientBuilder = HttpClientBuilder.create();
        //HttpClient
        CloseableHttpClient client = httpClientBuilder.build();
        client = (CloseableHttpClient) wrapClient(client);
        HttpPost post = new HttpPost(url);
        try {
            StringEntity s = new StringEntity(json, "utf-8");
            if (StringUtils.isBlank(contentType)) {
                s.setContentType("application/json");
            }
            s.setContentType(contentType);
            post.setEntity(s);
            HttpResponse res = client.execute(post);
            HttpEntity entity = res.getEntity();
            String str = EntityUtils.toString(entity, "utf-8");
            return str;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    /**
     * @param token
     * @param beans
     * @return
     */
    private String[] articalUpload(String token, Content[] beans) {
        Integer count = 0;
        String str = "{\"articles\":[";
        for (int i = 0; i < beans.length; i++) {
            //将富文本图片先上传到微信服务器上
            String txt = contentHtmlProc(token, beans[i].getHtml());
            String mediaId = "";
            if (!StringUtils.isBlank("文章类型图")) {
                String typeImg = "文章类型图";
                mediaId = uploadFile(token, typeImg, "image");
                System.out.println("typeImg:" + typeImg);
                str = str + "{" +
                        "\"thumb_media_id\":\"" + mediaId + "\"," +
                        "\"author\":\"" + beans[i].getAuthor() + "\"," +
                        "\"title\":\"" + beans[i].getTitle() + "\"," +
                        "\"content_source_url\":\"" + beans[i].getSourceUrl() + "\"," +
                        "\"content\":\"" + txt + "\"," +
                        "\"digest\":\"" + beans[i].getDescription() + "\","
                        + "\"show_cover_pic\":\"0\"" + "}";
                if (i != beans.length - 1) {
                    str = str + ",";
                }
                count++;
            }
        }
        str = str + "]}";
        String[] result = new String[2];
        result[0] = str;
        result[1] = count.toString();
        return result;
    }


    /**
     * 把内容中的图片先上传到微信服务器
     *
     * @param access_token 微信的access_token
     * @param txt          富文本内容 html代码
     * @return
     */
    private String contentHtmlProc(String access_token, String txt) {
        if (StringUtils.isBlank(txt)) {
            return "";
        }

        //从富文本的html中提取img图片路径
        List<String> imgUrls = getImageSrc(txt);
        for (String img : imgUrls) {
            //img路径为图片的绝对路径
            //上传图片到微信
            String imgRealUrl = uploadImg(access_token, img);
            if (StringUtils.isNotBlank(imgRealUrl)) {
                txt = txt.replace(img, imgRealUrl);
            }
        }
        //html标签双引号需要注意
        txt = txt.replaceAll("\"", "\'");
        return txt;
    }

    /**
     * 文件上传到微信服务器
     * 官方接口文档：https://developers.weixin.qq.com/doc/offiaccount/Asset_Management/New_temporary_materials.html
     *
     * @param access_token
     * @param filePath     文件路径 ，绝对地址
     * @param type         文件类型
     * @return
     */
    public String uploadFile(String access_token, String filePath, String type) {
        String sendGetUrl = "https://api.weixin.qq.com/cgi-bin/media/upload";
        String url = sendGetUrl + "?access_token=" + access_token;
        String result = null;
        String mediaId = "";
        FileUpload fileUpload = new FileUpload();
        try {
            result = fileUpload.uploadFile(url, filePath, type);
            System.out.println("result:" + result);
            if (result.startsWith("{") && result.contains("media_id")) {
                JSONObject json = new JSONObject(result);
                mediaId = json.getString("media_id");
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return mediaId;
    }

    /**
     * 把本地图片先上传到服务器 ,上传图文消息内的图片获取URL
     * 官网接口地址 ：https://developers.weixin.qq.com/doc/offiaccount/Asset_Management/Adding_Permanent_Assets.html
     *
     * @param access_token
     * @param filePath     图片路径
     * @return
     */
    private String uploadImg(String access_token, String filePath) {
        String sendGetUrl = "https://api.weixin.qq.com/cgi-bin/media/uploadimg";
        String url = sendGetUrl + "?access_token=" + access_token;
        String result = null;
        String mediaId = "";
        FileUpload fileUpload = new FileUpload();
        try {
            result = fileUpload.uploadFile(url, filePath, null);
            if (result.startsWith("{")) {
                JSONObject json = new JSONObject(result);
                mediaId = json.getString("url");
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return mediaId;
    }

    /**
     * 提取img图片路径
     *
     * @param htmlCode html代码
     * @return
     */
    public static List<String> getImageSrc(String htmlCode) {
        List<String> imageSrcList = new ArrayList<String>();
        String regular = "<img(.*?)src=\"(.*?)\"";
        String img_pre = "(?i)<img(.*?)src=\"";
        String img_sub = "\"";
        Pattern p = Pattern.compile(regular, Pattern.CASE_INSENSITIVE);
        Matcher m = p.matcher(htmlCode);
        String src = null;
        while (m.find()) {
            src = m.group();
            src = src.replaceAll(img_pre, "").replaceAll(img_sub, "").trim();
            imageSrcList.add(src);
        }
        return imageSrcList;
    }

    /**
     * 获取access_token
     *
     * @return
     */
    public String getToken() {
        String tokenGetUrl = "https://api.weixin.qq.com/cgi-bin/token?grant_type=client_credential";//微信提供获取access_token接口地址
        String appid = "";
        String secret = "";

        System.out.println("~~~~~appid:" + appid);
        System.out.println("~~~~~secret:" + secret);
        JSONObject tokenJson = new JSONObject();
        if (StringUtils.isNotBlank(appid) && StringUtils.isNotBlank(secret)) {
            tokenGetUrl += "&appid=" + appid + "&secret=" + secret;
            tokenJson = getUrlResponse(tokenGetUrl);
            System.out.println("~~~~~tokenJson:" + tokenJson.toString());
            try {
                return (String) tokenJson.get("access_token");
            } catch (JSONException e) {
                System.out.println("报错了");
                return null;
            }
        } else {
            System.out.println("appid和secret为空");
            return null;
        }
    }

    private JSONObject getUrlResponse(String url) {
        CharsetHandler handler = new CharsetHandler("UTF-8");
        try {
            HttpGet httpget = new HttpGet(new URI(url));
            HttpClientBuilder httpClientBuilder = HttpClientBuilder.create();
            //HttpClient
            CloseableHttpClient client = httpClientBuilder.build();
            client = (CloseableHttpClient) wrapClient(client);
            return new JSONObject(client.execute(httpget, handler));
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    private static HttpClient wrapClient(HttpClient base) {
        try {
            SSLContext ctx = SSLContext.getInstance("TLSv1");
            X509TrustManager tm = new X509TrustManager() {
                public void checkClientTrusted(X509Certificate[] xcs,
                                               String string) throws CertificateException {
                }

                public void checkServerTrusted(X509Certificate[] xcs,
                                               String string) throws CertificateException {
                }

                public X509Certificate[] getAcceptedIssuers() {
                    return null;
                }
            };
            ctx.init(null, new TrustManager[]{tm}, null);
            SSLConnectionSocketFactory sslsf = new SSLConnectionSocketFactory(ctx, new String[]{"TLSv1"}, null,
                    SSLConnectionSocketFactory.BROWSER_COMPATIBLE_HOSTNAME_VERIFIER);
            CloseableHttpClient httpclient = HttpClients.custom().setSSLSocketFactory(sslsf).build();
            return httpclient;

        } catch (Exception ex) {
            return null;
        }
    }


    private class CharsetHandler implements ResponseHandler<String> {
        private String charset;

        public CharsetHandler(String charset) {
            this.charset = charset;
        }

        public String handleResponse(HttpResponse response)
                throws ClientProtocolException, IOException {
            StatusLine statusLine = response.getStatusLine();
            if (statusLine.getStatusCode() >= 300) {
                throw new HttpResponseException(statusLine.getStatusCode(),
                        statusLine.getReasonPhrase());
            }
            HttpEntity entity = response.getEntity();
            if (entity != null) {
                if (!StringUtils.isBlank(charset)) {
                    return EntityUtils.toString(entity, charset);
                } else {
                    return EntityUtils.toString(entity);
                }
            } else {
                return null;
            }
        }

    }
}