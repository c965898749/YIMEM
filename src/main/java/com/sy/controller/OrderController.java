package com.sy.controller;
import com.alibaba.fastjson.JSONArray;
import com.sy.model.resp.BaseResp;
import com.sy.tool.Constants;
import org.apache.log4j.Logger;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import com.alipay.api.AlipayApiException;
import javax.servlet.http.HttpServletRequest;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import com.alipay.api.internal.util.AlipaySignature;
@Controller
@RequestMapping
public class OrderController {
    private Logger log = Logger.getLogger(OrderController.class.getName());
    @RequestMapping("alipay_callback.do")
    @ResponseBody
    private BaseResp callBack(HttpServletRequest request) throws AlipayApiException {
        log.info("进入支付接口");
        BaseResp baseResp=new BaseResp();
        // 取出支付宝回调携带的所有参数并进行转换，数组转换为字符串
        Map<String, String[]> tempParams = request.getParameterMap();
        JSONArray jArray = new JSONArray();
        jArray.add(tempParams);
        log.info("支付传入参数："+jArray.toString());
        //  参数存放 Map
        Map<String, String> requestParams = new HashMap<>();
        for (Iterator<String> iterator = tempParams.keySet().iterator(); iterator.hasNext(); ) {
            String key = iterator.next();
            String[] strs = tempParams.get(key);
            String str = "";
            // 这里如果数组的长度是1，说明只有一个，直接赋值就好，如果超过一个，后面加一个逗号来隔离
            for (int i = 0; i < strs.length; i++) {
                str = strs.length - 1 == i ? str + strs[i] : str + strs[i] + ",";
            }
            requestParams.put(key, str);
        }
        // 去除sign_type
        requestParams.remove("sign_type");
        try {
            // 验证签名
//            boolean result = AlipaySignature.rsaCheckV2(requestParams, Configs.getPublicKey(), "utf-8", Configs.getSignType());
            //公钥-对数据验签
            boolean result = AlipaySignature.rsaCheckV1(requestParams, Constants.PUBLIC_KEY, Constants.CHAR_SET, Constants.SIGN_TYPE);
            if (!result) {
                baseResp.setSuccess(1);
                log.info("未支付成功");
                return baseResp;
            }
        } catch (AlipayApiException e) {
            log.error("支付宝回调验证异常", e);
            e.printStackTrace();
            throw e;
        }
        // 调用Service 方法进行处理
//        ServerResponse serverResponse = orderService.alipayCallBack(requestParams);
//        if (!serverResponse.isSuccess()) {
//            log.info("OrderController.callBack()数据操作失败");
//            return ServerResponse.createBySuccess(Const.AlipayCallback.RESPONSE_FAILED);
//        }
        log.info("支付宝支付回调完成，没有异常");
        baseResp.setSuccess(0);
        return baseResp;
    }
}
