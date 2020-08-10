package com.sy.tool;

import com.alibaba.fastjson.JSON;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.util.EntityUtils;
import org.springframework.web.bind.annotation.PostMapping;

import javax.servlet.http.HttpServletRequest;
import java.util.Map;

/**
 * @author xyd
 * @version V1.0
 * @Package com.demo.util
 * @Description:
 * @date 2018/8/6 17:24
 */
public class WxUtils {

    /**
     * 获取Openid
     *
     * @param appid
     * @param secret
     * @param code
     * @return
     * @throws Exception
     */
    public static String getLoginAcessToken(String appid, String secret, String code) throws Exception {
        HttpClient httpclient = HttpClients.createDefault();
        String smsUrl = "https://api.weixin.qq.com/sns/oauth2/access_token?appid=" + appid + "&SECRET=" + secret + "&code=" + code + "&grant_type=authorization_code";
        HttpGet httpGet = new HttpGet(smsUrl);
        String strResult = "";

        HttpResponse response = httpclient.execute(httpGet);
        if (response.getStatusLine().getStatusCode() == 200) {
            strResult = EntityUtils.toString(response
                    .getEntity(), "UTF-8");
        }
        return strResult;
    }

    /**
     * 2020/8/10 后不再使用的扫码登录方式
     * @param appid
     * @param redirect_uri
     * @param state
     * @return
     * @throws Exception
     */
    public static String getURL(String appid, String redirect_uri, String state) throws Exception {
        String smsUrl = "https://open.weixin.qq.com/connect/oauth2/authorize?appid=" + appid + "&redirect_uri=" + redirect_uri + "&response_type=code&scope=snsapi_base&state=" + state + "#wechat_redirect";
        return smsUrl;
    }


}
