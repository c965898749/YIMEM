package com.sy.tool;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import org.apache.commons.codec.digest.DigestUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.HashMap;
import java.util.Map;

/**
 * 获取地址类
 * 
 * @author ruoyi
 */
public class AddressUtils
{
    private static final Logger log = LoggerFactory.getLogger(AddressUtils.class);

    // IP地址查询
    public static final String IP_URL = "http://whois.pconline.com.cn/ipJson.jsp";

    // 未知地址
    public static final String UNKNOWN = "XX XX";

    public static String getRealAddressByIP(String ip)
    {
        String address = UNKNOWN;
        // 内网不查询
        if (IpUtils.internalIp(ip))
        {
            return "内网IP";
        }
        if (true)
        {
            try
            {
                String rspStr = HttpUtils.sendGet(IP_URL, "ip=" + ip + "&json=true", Constants.GBK);
                if (StringUtils.isEmpty(rspStr))
                {
                    log.error("获取地理位置异常 {}", ip);
                    return UNKNOWN;
                }
                JSONObject obj = JSONObject.parseObject(rspStr);
                String region = obj.getString("pro");
                String city = obj.getString("city");
                return String.format("%s %s", region, city);
            }
            catch (Exception e)
            {
                log.error("获取地理位置异常 {}", ip);
            }
        }
        return address;
    }

    public static void main(String[] args) {
        AddressUtils utils=new AddressUtils();
//        System.out.println(utils.getRealAddressByIP("66.249.66.44"));
        System.out.println(utils.getAlibaba("112.4.205.119"));
    }

    /**
     * description ali地域查询
     *
     * @param ip ip地址
     * @return java.lang.String
     * @version 1.0
     */
    public static String getAlibaba(String ip) {
        Map map = new HashMap();
        map.put("ip", ip);
        map.put("key", "OB4BZ-D4W3U-B7VVO-4PJWW-6TKDJ-WPB77");
//        JSONObject JSONObj = JSONObject.parseObject(JSON.toJSONString(map));
        String md5str = DigestUtils.md5Hex("/ws/location/v1/ip?ip="+ip+"&key=LGIBZ-O7AWD-SXK4L-H5T57-2OP55-FRBO3Zs0kDAPIbdXVrb7NEet4H0CeE36eEIX");
        String result = HttpUtils.sendGet2("https://apis.map.qq.com/ws/location/v1/ip?ip="+ip+"&key=LGIBZ-O7AWD-SXK4L-H5T57-2OP55-FRBO3&sig="+md5str, "utf-8");
        Map valueMap = JSONObject.parseObject(result, Map.class);

        // 请求成功，解析响应数据
        if ("Success".equals(valueMap.get("message"))) {
            Map<String, Map<String, String>> dataMap = (Map<String, Map<String, String>>) valueMap.get("result");
            Map adInfo = dataMap.get("ad_info");
//            String region = dataMap.get("region");
//            String city = dataMap.get("city");
//            return country + region + city;
            return ""+adInfo.get("nation")+adInfo.get("province")+adInfo.get("city")+adInfo.get("district");
        }
        return "";
    }
}
