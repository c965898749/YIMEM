package com.sy.controller;
import com.sy.tool.BaiduApiUtil;
import com.sy.tool.Constants;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Controller;
/**
 * @author CZX
 * @version 1.0
 * @date 2021/5/5 0005 7:11
 */
@Controller
public class BaiduApiController {

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


}
