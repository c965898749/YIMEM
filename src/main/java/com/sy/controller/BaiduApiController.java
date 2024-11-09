package com.sy.controller;
import com.sy.mapper.ActivationKeyMapper;
import com.sy.tool.BaiduApiUtil;
import com.sy.tool.Constants;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.util.Random;

/**
 * @author CZX
 * @version 1.0
 * @date 2021/5/5 0005 7:11
 */
@Component
public class BaiduApiController {
    @Autowired
    private ActivationKeyMapper activationKeyMapper;
    /**
     * 每天定时向百度推送主页
     */
    @Scheduled(cron = "0 0 1 * * ?")
    public  void pushsite() {
        //需要提交的资源链接
        String[] urlsArr = {
                "http://www.yimem.com/index.html"
        };
        //将urlsArr数组转化为字符串形式
        String urlsStr = BaiduApiUtil.urlsArrToString(urlsArr);
        //打印结果
        System.out.println(BaiduApiUtil.Post(Constants.SITE, Constants.BAITOKEN, urlsStr));
    }

    /**
     * 每天定时跟新4位码
     */
//    @Scheduled(cron = "0 0 1 * * ?")
    @Scheduled(cron = "0 0 */1 * * ?" )  // 每小时的第0分触发
    public  void updateCode() {
        String randomString = generateRandomString(4);
        activationKeyMapper.updateRandomCode(randomString);
    }

    public  String generateRandomString(int length) {
        Random random = new Random();
        StringBuilder sb = new StringBuilder(length);
        for (int i = 0; i < length; i++) {
            // 可以根据需要调整字符范围
            char c = (char) ('a' + random.nextInt(26));
            sb.append(c);
        }
        return sb.toString();
    }
}
