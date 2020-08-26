package com.sy.controller;

import com.alibaba.fastjson.JSONArray;
import com.alipay.api.AlipayResponse;
import com.alipay.api.response.AlipayTradePrecreateResponse;
import com.alipay.demo.trade.config.Configs;
import com.alipay.demo.trade.model.ExtendParams;
import com.alipay.demo.trade.model.GoodsDetail;
import com.alipay.demo.trade.model.builder.*;
import com.alipay.demo.trade.model.result.AlipayF2FPayResult;
import com.alipay.demo.trade.model.result.AlipayF2FPrecreateResult;
import com.alipay.demo.trade.service.AlipayMonitorService;
import com.alipay.demo.trade.service.AlipayTradeService;
import com.alipay.demo.trade.service.impl.AlipayMonitorServiceImpl;
import com.alipay.demo.trade.service.impl.AlipayTradeServiceImpl;
import com.alipay.demo.trade.service.impl.AlipayTradeWithHBServiceImpl;
import com.sy.model.ScanRecord;
import com.sy.model.User;
import com.sy.model.resp.BaseResp;
import com.sy.service.PaymentRecordService;
import com.sy.service.ScanRecordService;
import com.sy.service.UserServic;
import com.sy.service.WeixinPostService;
import com.sy.tool.Constants;
import com.sy.tool.Xtool;
import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import com.alipay.api.AlipayApiException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.math.BigDecimal;
import java.text.DecimalFormat;
import java.util.*;

import com.alipay.api.internal.util.AlipaySignature;
import org.springframework.web.bind.annotation.ResponseBody;

@Controller
public class OrderController {
    private Logger log = Logger.getLogger(OrderController.class.getName());
    @Autowired
    private ScanRecordService scanRecordService;
    @Autowired
    private UserServic userServic;
    @Autowired
    private WeixinPostService weixinPostService;

    // 支付宝当面付2.0服务
    private static AlipayTradeService tradeService;

    // 支付宝当面付2.0服务（集成了交易保障接口逻辑）
    private static AlipayTradeService tradeWithHBService;

    // 支付宝交易保障接口服务，供测试接口api使用，请先阅读readme.txt
    private static AlipayMonitorService monitorService;

    static {
        /** 一定要在创建AlipayTradeService之前调用Configs.init()设置默认参数
         *  Configs会读取classpath下的zfbinfo.properties文件配置信息，如果找不到该文件则确认该文件是否在classpath目录
         */
        Configs.init("zfbinfo.properties");

        /** 使用Configs提供的默认参数
         *  AlipayTradeService可以使用单例或者为静态成员对象，不需要反复new
         */
        tradeService = new AlipayTradeServiceImpl.ClientBuilder().build();

        // 支付宝当面付2.0服务（集成了交易保障接口逻辑）
        tradeWithHBService = new AlipayTradeWithHBServiceImpl.ClientBuilder().build();

        /** 如果需要在程序中覆盖Configs提供的默认参数, 可以使用ClientBuilder类的setXXX方法修改默认参数 否则使用代码中的默认设置 */
        monitorService = new AlipayMonitorServiceImpl.ClientBuilder()
                .setGatewayUrl("http://mcloudmonitor.com/gateway.do").setCharset("GBK")
                .setFormat("json").build();
    }

    // 简单打印应答
    private void dumpResponse(AlipayResponse response) {
        if (response != null) {
            log.info(String.format("code:%s, msg:%s", response.getCode(), response.getMsg()));
            if (StringUtils.isNotEmpty(response.getSubCode())) {
                log.info(String.format("subCode:%s, subMsg:%s", response.getSubCode(),
                        response.getSubMsg()));
            }
            log.info("body:" + response.getBody());
        }
    }






    // 测试当面付2.0支付
    public void test_trade_pay(AlipayTradeService service) {
        // (必填) 商户网站订单系统中唯一订单号，64个字符以内，只能包含字母、数字、下划线，
        // 需保证商户系统端不能重复，建议通过数据库sequence生成，
        String outTradeNo = "tradepay" + System.currentTimeMillis()
                + (long) (Math.random() * 10000000L);

        // (必填) 订单标题，粗略描述用户的支付目的。如“xxx品牌xxx门店消费”
        String subject = "xxx品牌xxx门店当面付消费";

        // (必填) 订单总金额，单位为元，不能超过1亿元
        // 如果同时传入了【打折金额】,【不可打折金额】,【订单总金额】三者,则必须满足如下条件:【订单总金额】=【打折金额】+【不可打折金额】
        String totalAmount = "0.01";

        // (必填) 付款条码，用户支付宝钱包手机app点击“付款”产生的付款条码
        String authCode = "用户自己的支付宝付款码"; // 条码示例，286648048691290423
        // (可选，根据需要决定是否使用) 订单可打折金额，可以配合商家平台配置折扣活动，如果订单部分商品参与打折，可以将部分商品总价填写至此字段，默认全部商品可打折
        // 如果该值未传入,但传入了【订单总金额】,【不可打折金额】 则该值默认为【订单总金额】- 【不可打折金额】
        //        String discountableAmount = "1.00"; //

        // (可选) 订单不可打折金额，可以配合商家平台配置折扣活动，如果酒水不参与打折，则将对应金额填写至此字段
        // 如果该值未传入,但传入了【订单总金额】,【打折金额】,则该值默认为【订单总金额】-【打折金额】
        String undiscountableAmount = "0.0";

        // 卖家支付宝账号ID，用于支持一个签约账号下支持打款到不同的收款账号，(打款到sellerId对应的支付宝账号)
        // 如果该字段为空，则默认为与支付宝签约的商户的PID，也就是appid对应的PID
        String sellerId = "";

        // 订单描述，可以对交易或商品进行一个详细地描述，比如填写"购买商品3件共20.00元"
        String body = "购买商品3件共20.00元";

        // 商户操作员编号，添加此参数可以为商户操作员做销售统计
        String operatorId = "test_operator_id";

        // (必填) 商户门店编号，通过门店号和商家后台可以配置精准到门店的折扣信息，详询支付宝技术支持
        String storeId = "test_store_id";

        // 业务扩展参数，目前可添加由支付宝分配的系统商编号(通过setSysServiceProviderId方法)，详情请咨询支付宝技术支持
        String providerId = "2088100200300400500";
        ExtendParams extendParams = new ExtendParams();
        extendParams.setSysServiceProviderId(providerId);

        // 支付超时，线下扫码交易定义为5分钟
        String timeoutExpress = "5m";

        // 商品明细列表，需填写购买商品详细信息，
        List<GoodsDetail> goodsDetailList = new ArrayList<GoodsDetail>();
        // 创建一个商品信息，参数含义分别为商品id（使用国标）、名称、单价（单位为分）、数量，如果需要添加商品类别，详见GoodsDetail
        GoodsDetail goods1 = GoodsDetail.newInstance("goods_id001", "xxx面包", 1000, 1);
        // 创建好一个商品后添加至商品明细列表
        goodsDetailList.add(goods1);

        // 继续创建并添加第一条商品信息，用户购买的产品为“黑人牙刷”，单价为5.00元，购买了两件
        GoodsDetail goods2 = GoodsDetail.newInstance("goods_id002", "xxx牙刷", 500, 2);
        goodsDetailList.add(goods2);

        String appAuthToken = "应用授权令牌";//根据真实值填写

        // 创建条码支付请求builder，设置请求参数
        AlipayTradePayRequestBuilder builder = new AlipayTradePayRequestBuilder()
                //            .setAppAuthToken(appAuthToken)
                .setOutTradeNo(outTradeNo).setSubject(subject).setAuthCode(authCode)
                .setTotalAmount(totalAmount).setStoreId(storeId)
                .setUndiscountableAmount(undiscountableAmount).setBody(body).setOperatorId(operatorId)
                .setExtendParams(extendParams).setSellerId(sellerId)
                .setGoodsDetailList(goodsDetailList).setTimeoutExpress(timeoutExpress);

        // 调用tradePay方法获取当面付应答
        AlipayF2FPayResult result = service.tradePay(builder);
        switch (result.getTradeStatus()) {
            case SUCCESS:
                log.info("支付宝支付成功: )");
                break;

            case FAILED:
                log.error("支付宝支付失败!!!");
                break;

            case UNKNOWN:
                log.error("系统异常，订单状态未知!!!");
                break;

            default:
                log.error("不支持的交易状态，交易返回异常!!!");
                break;
        }
    }


    /**
     * 查询订单getScanRecord
     */
    @RequestMapping("getScanRecord")
    @ResponseBody
    public BaseResp getScanRecord(String outTradeNo) {
        BaseResp baseResp=new BaseResp();
        try {
            log.info("进入查询");
            ScanRecord order = scanRecordService.findOrderByOuttradeno(outTradeNo);
            baseResp.setSuccess(1);
            baseResp.setData(order);
            return baseResp;
        } catch (Exception e) {
           log.info("查询订单异常");
           baseResp.setErrorMsg("查询订单异常"+e.getMessage());
           baseResp.setSuccess(0);
           return baseResp;
        }

    }

    // 测试当面付2.0生成支付二维码
    @RequestMapping("trade_precreate.do")
    @ResponseBody
    public BaseResp trade_precreate(Double totalAmount, HttpServletRequest request, HttpServletResponse res) {
        BaseResp baseResp=new BaseResp();
        User user = (User) request.getSession().getAttribute("user");
        ScanRecord scanRecord = new ScanRecord();
        if (user == null) {
            baseResp.setSuccess(0);
            baseResp.setErrorMsg("用户未登录");
            return baseResp;
        } else {
            DecimalFormat df = new DecimalFormat("#.00");
            scanRecord.setUserid(user.getUserId());
//            植入sessionid
//            scanRecord.setSellerid(request.getSession().getId());
            // (必填) 商户网站订单系统中唯一订单号，64个字符以内，只能包含字母、数字、下划线，
            // 需保证商户系统端不能重复，建议通过数据库sequence生成，
            String outTradeNo =""+System.currentTimeMillis()
                    + (long) (Math.random() * 10000000L);
            scanRecord.setOuttradeno(outTradeNo);
            // (必填) 订单标题，粗略描述用户的支付目的。如“xxx品牌xxx门店当面付扫码消费”
            String subject = "一梦工作室";
            scanRecord.setSubject(subject);
            scanRecord.setTotalamount(new BigDecimal(df.format(totalAmount)));
            scanRecord.setCreateTime(new Date());
//            String totalAmount="8888";
            // (必填) 订单总金额，单位为元，不能超过1亿元
            // 如果同时传入了【打折金额】,【不可打折金额】,【订单总金额】三者,则必须满足如下条件:【订单总金额】=【打折金额】+【不可打折金额】
//         map.put("totalAmount",totalAmount);
            // (可选) 订单不可打折金额，可以配合商家平台配置折扣活动，如果酒水不参与打折，则将对应金额填写至此字段
            // 如果该值未传入,但传入了【订单总金额】,【打折金额】,则该值默认为【订单总金额】-【打折金额】
            String undiscountableAmount = "0";
            // 卖家支付宝账号ID，用于支持一个签约账号下支持打款到不同的收款账号，(打款到sellerId对应的支付宝账号)
            // 如果该字段为空，则默认为与支付宝签约的商户的PID，也就是appid对应的PID
            String sellerId = "";

            // 订单描述，可以对交易或商品进行一个详细地描述，比如填写"购买商品2件共15.00元"
//            计算积分
            String body = "充值" + scanRecord.getTotalamount() + "元 -赠送" + scanRecord.getTotalamount().multiply(new BigDecimal(1000)) + "积分";
            scanRecord.setBody(body);
//            Integer  loadmoney= user.getDownloadmoney()+Integer.parseInt(scanRecord.getTotalamount().multiply(new BigDecimal(1000)).toString());
//           用户积分计算
//            BigDecimal money=new BigDecimal(user.getDownloadmoney()).add(scanRecord.getTotalamount().multiply(new BigDecimal(1000)));
////            user.setDownloadmoney(money.doubleValue());

            // 商户操作员编号，添加此参数可以为商户操作员做销售统计
            String operatorId = "test_operator_id";

            // (必填) 商户门店编号，通过门店号和商家后台可以配置精准到门店的折扣信息，详询支付宝技术支持
            String storeId = "test_store_id";

            // 业务扩展参数，目前可添加由支付宝分配的系统商编号(通过setSysServiceProviderId方法)，详情请咨询支付宝技术支持
            ExtendParams extendParams = new ExtendParams();
            extendParams.setSysServiceProviderId("2088100200300400500");

            // 支付超时，定义为120分钟
            String timeoutExpress = "120m";

            // 商品明细列表，需填写购买商品详细信息，
            List<GoodsDetail> goodsDetailList = new ArrayList<GoodsDetail>();
            // 创建一个商品信息，参数含义分别为商品id（使用国标）、名称、单价（单位为分）、数量，如果需要添加商品类别，详见GoodsDetail
            GoodsDetail goods1 = GoodsDetail.newInstance("goods_id001", "梦网1000积分", 1, 1);
            // 创建好一个商品后添加至商品明细列表
            goodsDetailList.add(goods1);

            // 继续创建并添加第一条商品信息，用户购买的产品为“黑人牙刷”，单价为5.00元，购买了两件
//        GoodsDetail goods2 = GoodsDetail.newInstance("goods_id002", "xxx牙刷", 500, 2);
//        goodsDetailList.add(goods2);

            // 创建扫码支付请求builder，设置请求参数
            AlipayTradePrecreateRequestBuilder builder = new AlipayTradePrecreateRequestBuilder()
                    .setSubject(subject).setTotalAmount(totalAmount + "").setOutTradeNo(outTradeNo)
                    .setUndiscountableAmount(undiscountableAmount).setSellerId(sellerId).setBody(body)
                    .setOperatorId(operatorId).setStoreId(storeId).setExtendParams(extendParams)
                    .setTimeoutExpress(timeoutExpress)
                    .setNotifyUrl("http://www.yimem.com/alipay_callback.do")//支付宝服务器主动通知商户服务器里指定的页面http路径,根据需要设置
                    .setGoodsDetailList(goodsDetailList);
            AlipayF2FPrecreateResult result = tradeService.tradePrecreate(builder);
            switch (result.getTradeStatus()) {
                case SUCCESS:
                    log.info("支付宝预下单成功: )");

                    AlipayTradePrecreateResponse response = result.getResponse();
                    dumpResponse(response);
                    scanRecord.setQrcode(response.getQrCode());
                    scanRecordService.insertSelective(scanRecord);
                    // 需要修改为运行机器上的路径
//                String filePath = String.format("D:\\qr-%s.png",
//                        response.getOutTradeNo());
//                log.info("filePath:" + filePath);
//                ZxingUtils.getQRCodeImge(response.getQrCode(), 256, filePath);
                    break;

                case FAILED:
                    log.error("支付宝预下单失败!!!");
                    break;

                case UNKNOWN:
                    log.error("系统异常，预下单状态未知!!!");
                    break;

                default:
                    log.error("不支持的交易状态，交易返回异常!!!");
                    break;
            }
            baseResp.setSuccess(1);
            baseResp.setData(scanRecord);
            return baseResp;
        }

    }


    @RequestMapping("alipay_callback.do")
    private String callBack(HttpServletRequest request) throws AlipayApiException {
        log.info("收到支付宝异步通知！");
//        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        // 取出支付宝回调携带的所有参数并进行转换，数组转换为字符串
        Map<String, String[]> tempParams = request.getParameterMap();
        JSONArray jArray = new JSONArray();
        jArray.add(tempParams);
        log.info("支付传入参数：" + jArray.toString());
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
            //公钥-对数据验签
            boolean result = AlipaySignature.rsaCheckV1(requestParams, Constants.PUBLIC_KEY, Constants.CHAR_SET, Constants.SIGN_TYPE);
            if (!result) {
                log.info("未支付成功");
                return "failed";
            }
            log.info("验证通过");
            //若参数中的appid和填入的appid不相同，则为异常通知
            if (!Configs.getAppid().equals(requestParams.get("app_id"))) {
                log.info("与付款时的appid不同，此为异常通知，应忽略！");
                return "failed";
            }
            //在数据库中查找订单号对应的订单，并将其金额与数据库中的金额对比，若对不上，也为异常通知
            ScanRecord order = scanRecordService.findOrderByOuttradeno(requestParams.get("out_trade_no"));
            log.info("实体类参数"+order.toString());
            if (order == null) {
                log.warn(requestParams.get("out_trade_no") + "查无此订单！");
                return "failed";
            }
            if (order.getTotalamount().doubleValue() != Double.parseDouble(requestParams.get("total_amount"))) {
                log.info("与付款时的金额不同，此为异常通知，应忽略！");
                return "failed";
            }
            if ("TRADE_SUCCESS".equals(order.getStatus())) {
                log.info("如果订单已经支付成功了，就直接忽略这次通知");
                return "success"; //如果订单已经支付成功了，就直接忽略这次通知
            }
            order.setGmtPayment(new Date());
            order.setNotifyId(requestParams.get("notify_id"));
            order.setBuyerId(requestParams.get("buyer_id"));
            order.setBuyerLogonId(requestParams.get("buyer_logon_id"));
            String status = requestParams.get("trade_status");
            order.setStatus(status);
            if (status.equals("WAIT_BUYER_PAY")) { //如果状态是正在等待用户付款
                    log.info("如果状态是正在等待用户付款");
                    scanRecordService.modifyTradeStatus(order);
            } else if (status.equals("TRADE_CLOSED")) { //如果状态是未付款交易超时关闭，或支付完成后全额退款
                    log.info("如果状态是未付款交易超时关闭，或支付完成后全额退款");
                    scanRecordService.modifyTradeStatus(order);
            } else if (status.equals("TRADE_SUCCESS") || status.equals("TRADE_FINISHED")) { //如果状态是已经支付成功
                    log.info("如果状态是已经支付成功");
                    scanRecordService.modifyTradeStatus(order);
                    User u=new User();
                    u.setUserId(order.getUserid());
                    User user= null;
                    try {
                        user = userServic.getUserById(u);
                        log.info("用户积分"+user.getDownloadmoney());
                        BigDecimal money = new BigDecimal(user.getDownloadmoney()).add(order.getTotalamount().multiply(new BigDecimal(1000)));
                        log.info("增加后的积分"+money.doubleValue());
                        user.setDownloadmoney(money.doubleValue());
                        userServic.updateUserMoney(user);
//                        微信通知到账
                        if (Xtool.isNotNull(user.getOpenid())){
                            weixinPostService.sendTemplate4(user.getOpenid(),user.getNickname(),order.getOuttradeno(),order.getTotalamount().multiply(new BigDecimal(1000)).toString(),order.getTotalamount().toString());
                        }
                    } catch (Exception e) {
                        log.info("收款异常"+e.getMessage());
                    }

//                }
            } else {
                scanRecordService.modifyTradeStatus(order);
            }
            return "success";
        } catch (AlipayApiException e) {
            log.info("支付宝回调验证异常", e);
            return "failed";
        }
    }
}
