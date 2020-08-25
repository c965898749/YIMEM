package com.sy.tool;

import com.alibaba.fastjson.JSON;
import com.alipay.api.AlipayApiException;
import com.alipay.api.AlipayClient;
import com.alipay.api.DefaultAlipayClient;
import com.alipay.api.request.AlipayTradePagePayRequest;
import com.sy.model.AlipayBean;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;

@Component
public class AliPayUtil {

    @Resource
    private AlipayProperties alipayProperties;
    /**
     * 支付接口
     *
     * @param alipayBean 封装的支付宝入参
     * @return 返回支付结果
     * @throws AlipayApiException 抛出异常
     */
    public String pay(AlipayBean alipayBean) throws AlipayApiException {
        // 1、获得初始化的AlipayClient
        String serverUrl = alipayProperties.getGatewayUrl();
        String appId = alipayProperties.getAppId();
        String privateKey = alipayProperties.getPrivateKey();
        String format = "json";
        String charset = alipayProperties.getCharset();
        String alipayPublicKey = alipayProperties.getPublicKey();
        String signType = alipayProperties.getSignType();
        String returnUrl = alipayProperties.getReturnUrl();
        String notifyUrl = alipayProperties.getNotifyUrl();
        AlipayClient alipayClient = new DefaultAlipayClient(serverUrl, appId, privateKey, format, charset, alipayPublicKey, signType);
        // 2、设置请求参数
        AlipayTradePagePayRequest alipayRequest = new AlipayTradePagePayRequest();
        // 页面跳转同步通知页面路径
        alipayRequest.setReturnUrl(returnUrl);
        // 服务器异步通知页面路径
        alipayRequest.setNotifyUrl(notifyUrl);
        // 封装参数
        alipayRequest.setBizContent(JSON.toJSONString(alipayBean));
        // 3、请求支付宝进行付款，并获取支付结果
        return alipayClient.pageExecute(alipayRequest).getBody();
    }

}
