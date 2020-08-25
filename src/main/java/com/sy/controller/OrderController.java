package com.sy.controller;

import com.alipay.api.AlipayApiException;
import com.alipay.api.internal.util.AlipaySignature;
import com.sy.model.UserOrder;
import com.sy.service.OrderService;
import com.sy.tool.AlipayProperties;
import com.sy.tool.OrderEnum;
import org.apache.commons.lang3.time.DateFormatUtils;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.math.BigDecimal;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.logging.Logger;

@Controller
@RequestMapping("/order")
public class OrderController {
    private Logger log = Logger.getLogger(OrderController.class.getName());
    @Resource
    private OrderService orderService;
    @Resource
    private AlipayProperties alipayProperties;

    /**
     * 跳转到下单页面
     *
     * @return
     */
//    @RequestMapping("/goPay")
//    public String goPay() {
//        return "redirect:userHomepage.html";
//    }

    /**
     * 下单，并调用支付宝
     *
     * @param orderAmount
     * @return
     * @throws AlipayApiException
     */
//    @PostMapping("/pay")
    @GetMapping("/pay")
    public void pay(BigDecimal orderAmount, HttpServletResponse httpResponse) throws Exception {
        String payResult = orderService.orderPay(orderAmount);
        httpResponse.setContentType("text/html;charset=" + alipayProperties.getCharset());
        httpResponse.getWriter().write(payResult);
        httpResponse.getWriter().flush();
        httpResponse.getWriter().close();
    }


    /**
     * 支付成功的跳转页面
     *
     * @return
     */
    @RequestMapping("/goPaySuccPage")
    public String goPaySuccPage() {
        return "redirect:/userHomepage.html";
    }

    /**
     * 支付成功的回调接口
     *
     * @return
     */
    @ResponseBody
    @RequestMapping("/notifyPayResult")
    public String notifyPayResult(HttpServletRequest request) {
        log.info("<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<进入支付宝回调->>>>>>>>>>>>>>>>>>>>>>>>>");
        // 1.从支付宝回调的request域中取值放到map中
        Map<String, String[]> requestParams = request.getParameterMap();

        Map<String, String> params = new HashMap();
        for (String name : requestParams.keySet()) {
            String[] values = requestParams.get(name);
            String valueStr = "";
            for (int i = 0; i < values.length; i++) {
                valueStr = (i == values.length - 1) ? valueStr + values[i] : valueStr + values[i] + ",";
            }
            params.put(name, valueStr);
        }
        //2.封装必须参数
        // 商户订单号
        String outTradeNo = params.get("out_trade_no");
        //交易状态
        String tradeStatus = params.get("trade_status");

        log.info("outTradeNo:" + outTradeNo + " tradeStatus:" + tradeStatus);

        //3.签名验证(对支付宝返回的数据验证，确定是支付宝返回的)
        boolean signVerified = false;
        try {
            //3.1调用SDK验证签名
            signVerified = AlipaySignature.rsaCheckV1(params, alipayProperties.getPublicKey(), alipayProperties.getCharset(), alipayProperties.getSignType());

        } catch (Exception e) {
            e.printStackTrace();
        }
        log.info("--------------->验签结果:" + signVerified);

        //4.对验签进行处理

        if (signVerified) {
            //验签通过
            //只处理支付成功的订单: 修改交易表状态,支付成功
            if ("TRADE_FINISHED".equals(tradeStatus) || "TRADE_SUCCESS".equals(tradeStatus)) {
                //根据订单号查找订单,防止多次回调的问题
                UserOrder orderByOrder = orderService.getOrderByOrderNo(outTradeNo);
                if (orderByOrder != null && orderByOrder.getOrderStatus() == OrderEnum.ORDER_STATUS_NOT_PAY.getStatus()) {
                    //修改订单状态
                    orderByOrder.setOrderStatus(OrderEnum.ORDER_STATUS_PAID.getStatus());
                    orderByOrder.setLastUpdateTime(DateFormatUtils.format(new Date(), "yyyy-MM-dd HH:mm:ss"));
                    log.info("更新订单");
                    orderService.updateByPrimaryKey(orderByOrder);
                }
                return "success";
            } else {
                return "failure";
            }
        } else {
            //验签不通过
            log.info("-------------------->验签失败");
            return "failure";
        }
    }


}
