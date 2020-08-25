//package com.sy.controller;
//
//import com.alibaba.fastjson.JSONArray;
//import com.alipay.api.AlipayResponse;
//import com.alipay.api.domain.TradeFundBill;
//import com.alipay.api.response.AlipayTradePrecreateResponse;
//import com.alipay.api.response.AlipayTradeQueryResponse;
//import com.alipay.api.response.MonitorHeartbeatSynResponse;
//import com.alipay.demo.trade.config.Configs;
//import com.alipay.demo.trade.model.ExtendParams;
//import com.alipay.demo.trade.model.GoodsDetail;
//import com.alipay.demo.trade.model.builder.*;
//import com.alipay.demo.trade.model.hb.*;
//import com.alipay.demo.trade.model.result.AlipayF2FPayResult;
//import com.alipay.demo.trade.model.result.AlipayF2FPrecreateResult;
//import com.alipay.demo.trade.model.result.AlipayF2FQueryResult;
//import com.alipay.demo.trade.model.result.AlipayF2FRefundResult;
//import com.alipay.demo.trade.service.AlipayMonitorService;
//import com.alipay.demo.trade.service.AlipayTradeService;
//import com.alipay.demo.trade.service.impl.AlipayMonitorServiceImpl;
//import com.alipay.demo.trade.service.impl.AlipayTradeServiceImpl;
//import com.alipay.demo.trade.service.impl.AlipayTradeWithHBServiceImpl;
//import com.alipay.demo.trade.utils.Utils;
//import com.sy.model.ScanRecord;
//import com.sy.model.User;
//import com.sy.model.resp.BaseResp;
//import com.sy.service.PaymentRecordService;
//import com.sy.service.ScanRecordService;
//import com.sy.service.UserServic;
//import com.sy.tool.Constants;
//import com.sy.tool.MySessionContext;
//import org.apache.commons.lang.StringUtils;
//import org.apache.log4j.Logger;
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.stereotype.Controller;
//import org.springframework.web.bind.annotation.RequestMapping;
//import com.alipay.api.AlipayApiException;
//
//import javax.servlet.http.HttpServletRequest;
//import javax.servlet.http.HttpServletResponse;
//import javax.servlet.http.HttpSession;
//import java.math.BigDecimal;
//import java.text.DecimalFormat;
//import java.util.*;
//
//import com.alipay.api.internal.util.AlipaySignature;
//import org.springframework.web.bind.annotation.ResponseBody;
//
//@Controller
//public class Kkk {
//    private Logger log = Logger.getLogger(Kkk.class.getName());
//    @Autowired
//    private PaymentRecordService paymentRecordService;
//    @Autowired
//    private ScanRecordService scanRecordService;
//    @Autowired
//    private UserServic userServic;
//
//
//    // 支付宝当面付2.0服务
//    private static AlipayTradeService tradeService;
//
//    // 支付宝当面付2.0服务（集成了交易保障接口逻辑）
//    private static AlipayTradeService tradeWithHBService;
//
//    // 支付宝交易保障接口服务，供测试接口api使用，请先阅读readme.txt
//    private static AlipayMonitorService monitorService;
//
//    static {
//        /** 一定要在创建AlipayTradeService之前调用Configs.init()设置默认参数
//         *  Configs会读取classpath下的zfbinfo.properties文件配置信息，如果找不到该文件则确认该文件是否在classpath目录
//         */
//        Configs.init("zfbinfo.properties");
//
//        /** 使用Configs提供的默认参数
//         *  AlipayTradeService可以使用单例或者为静态成员对象，不需要反复new
//         */
//        tradeService = new AlipayTradeServiceImpl.ClientBuilder().build();
//
//        // 支付宝当面付2.0服务（集成了交易保障接口逻辑）
//        tradeWithHBService = new AlipayTradeWithHBServiceImpl.ClientBuilder().build();
//
//        /** 如果需要在程序中覆盖Configs提供的默认参数, 可以使用ClientBuilder类的setXXX方法修改默认参数 否则使用代码中的默认设置 */
//        monitorService = new AlipayMonitorServiceImpl.ClientBuilder()
//                .setGatewayUrl("http://mcloudmonitor.com/gateway.do").setCharset("GBK")
//                .setFormat("json").build();
//    }
//
//    // 简单打印应答
//    private void dumpResponse(AlipayResponse response) {
//        if (response != null) {
//            log.info(String.format("code:%s, msg:%s", response.getCode(), response.getMsg()));
//            if (StringUtils.isNotEmpty(response.getSubCode())) {
//                log.info(String.format("subCode:%s, subMsg:%s", response.getSubCode(),
//                        response.getSubMsg()));
//            }
//            log.info("body:" + response.getBody());
//        }
//    }
//
//
////    public static void main(String[] args) {
//
//    // 系统商商测试交易保障接口api
//    //        main.test_monitor_sys();
//
//    // POS厂商测试交易保障接口api
//    //        main.test_monitor_pos();
//
//    // 测试交易保障接口调度
////                main.test_monitor_schedule_logic();
//
//    // 测试当面付2.0支付（使用未集成交易保障接口的当面付2.0服务）
//    //        main.test_trade_pay(tradeService);
//
//    // 测试查询当面付2.0交易
////                main.test_trade_query();
//
//    // 测试当面付2.0退货
//    //        main.test_trade_refund();
//
////         测试当面付2.0生成支付二维码
////        main.test_trade_precreate();
////    }
//
//    // 测试系统商交易保障调度
//    public void test_monitor_schedule_logic() {
//        // 启动交易保障线程
//        DemoHbRunner demoRunner = new DemoHbRunner(monitorService);
//        demoRunner.setDelay(5); // 设置启动后延迟5秒开始调度，不设置则默认3秒
//        demoRunner.setDuration(10); // 设置间隔10秒进行调度，不设置则默认15 * 60秒
//        demoRunner.schedule();
//
//        // 启动当面付，此处每隔5秒调用一次支付接口，并且当随机数为0时交易保障线程退出
//        while (Math.random() != 0) {
//            test_trade_pay(tradeWithHBService);
//            Utils.sleep(5 * 1000);
//        }
//
//        // 满足退出条件后可以调用shutdown优雅安全退出
//        demoRunner.shutdown();
//    }
//
//    // 系统商的调用样例，填写了所有系统商商需要填写的字段
//    public void test_monitor_sys() {
//        // 系统商使用的交易信息格式，json字符串类型
//        List<SysTradeInfo> sysTradeInfoList = new ArrayList<SysTradeInfo>();
//        sysTradeInfoList.add(SysTradeInfo.newInstance("00000001", 5.2, HbStatus.S));
//        sysTradeInfoList.add(SysTradeInfo.newInstance("00000002", 4.4, HbStatus.F));
//        sysTradeInfoList.add(SysTradeInfo.newInstance("00000003", 11.3, HbStatus.P));
//        sysTradeInfoList.add(SysTradeInfo.newInstance("00000004", 3.2, HbStatus.X));
//        sysTradeInfoList.add(SysTradeInfo.newInstance("00000005", 4.1, HbStatus.X));
//
//        // 填写异常信息，如果有的话
//        List<ExceptionInfo> exceptionInfoList = new ArrayList<ExceptionInfo>();
//        exceptionInfoList.add(ExceptionInfo.HE_SCANER);
//        //        exceptionInfoList.add(ExceptionInfo.HE_PRINTER);
//        //        exceptionInfoList.add(ExceptionInfo.HE_OTHER);
//
//        // 填写扩展参数，如果有的话
//        Map<String, Object> extendInfo = new HashMap<String, Object>();
//        //        extendInfo.put("SHOP_ID", "BJ_ZZ_001");
//        //        extendInfo.put("TERMINAL_ID", "1234");
//
//        String appAuthToken = "应用授权令牌";//根据真实值填写
//
//        AlipayHeartbeatSynRequestBuilder builder = new AlipayHeartbeatSynRequestBuilder()
//                .setAppAuthToken(appAuthToken).setProduct(Product.FP).setType(Type.CR)
//                .setEquipmentId("cr1000001").setEquipmentStatus(EquipStatus.NORMAL)
//                .setTime(Utils.toDate(new Date())).setStoreId("store10001").setMac("0a:00:27:00:00:00")
//                .setNetworkType("LAN").setProviderId("2088911212323549") // 设置系统商pid
//                .setSysTradeInfoList(sysTradeInfoList) // 系统商同步trade_info信息
//                //                .setExceptionInfoList(exceptionInfoList)  // 填写异常信息，如果有的话
//                .setExtendInfo(extendInfo) // 填写扩展信息，如果有的话
//                ;
//
//        MonitorHeartbeatSynResponse response = monitorService.heartbeatSyn(builder);
//        dumpResponse(response);
//    }
//
//    // POS厂商的调用样例，填写了所有pos厂商需要填写的字段
//    public void test_monitor_pos() {
//        // POS厂商使用的交易信息格式，字符串类型
//        List<PosTradeInfo> posTradeInfoList = new ArrayList<PosTradeInfo>();
//        posTradeInfoList.add(PosTradeInfo.newInstance(HbStatus.S, "1324", 7));
//        posTradeInfoList.add(PosTradeInfo.newInstance(HbStatus.X, "1326", 15));
//        posTradeInfoList.add(PosTradeInfo.newInstance(HbStatus.S, "1401", 8));
//        posTradeInfoList.add(PosTradeInfo.newInstance(HbStatus.F, "1405", 3));
//
//        // 填写异常信息，如果有的话
//        List<ExceptionInfo> exceptionInfoList = new ArrayList<ExceptionInfo>();
//        exceptionInfoList.add(ExceptionInfo.HE_PRINTER);
//
//        // 填写扩展参数，如果有的话
//        Map<String, Object> extendInfo = new HashMap<String, Object>();
//        //        extendInfo.put("SHOP_ID", "BJ_ZZ_001");
//        //        extendInfo.put("TERMINAL_ID", "1234");
//
//        AlipayHeartbeatSynRequestBuilder builder = new AlipayHeartbeatSynRequestBuilder()
//                .setProduct(Product.FP)
//                .setType(Type.SOFT_POS)
//                .setEquipmentId("soft100001")
//                .setEquipmentStatus(EquipStatus.NORMAL)
//                .setTime("2015-09-28 11:14:49")
//                .setManufacturerPid("2088000000000009")
//                // 填写机具商的支付宝pid
//                .setStoreId("store200001").setEquipmentPosition("31.2433190000,121.5090750000")
//                .setBbsPosition("2869719733-065|2896507033-091").setNetworkStatus("gggbbbgggnnn")
//                .setNetworkType("3G").setBattery("98").setWifiMac("0a:00:27:00:00:00")
//                .setWifiName("test_wifi_name").setIp("192.168.1.188")
//                .setPosTradeInfoList(posTradeInfoList) // POS厂商同步trade_info信息
//                //                .setExceptionInfoList(exceptionInfoList) // 填写异常信息，如果有的话
//                .setExtendInfo(extendInfo) // 填写扩展信息，如果有的话
//                ;
//
//        MonitorHeartbeatSynResponse response = monitorService.heartbeatSyn(builder);
//        dumpResponse(response);
//    }
//
//    // 测试当面付2.0支付
//    public void test_trade_pay(AlipayTradeService service) {
//        // (必填) 商户网站订单系统中唯一订单号，64个字符以内，只能包含字母、数字、下划线，
//        // 需保证商户系统端不能重复，建议通过数据库sequence生成，
//        String outTradeNo = "tradepay" + System.currentTimeMillis()
//                + (long) (Math.random() * 10000000L);
//
//        // (必填) 订单标题，粗略描述用户的支付目的。如“xxx品牌xxx门店消费”
//        String subject = "xxx品牌xxx门店当面付消费";
//
//        // (必填) 订单总金额，单位为元，不能超过1亿元
//        // 如果同时传入了【打折金额】,【不可打折金额】,【订单总金额】三者,则必须满足如下条件:【订单总金额】=【打折金额】+【不可打折金额】
//        String totalAmount = "0.01";
//
//        // (必填) 付款条码，用户支付宝钱包手机app点击“付款”产生的付款条码
//        String authCode = "用户自己的支付宝付款码"; // 条码示例，286648048691290423
//        // (可选，根据需要决定是否使用) 订单可打折金额，可以配合商家平台配置折扣活动，如果订单部分商品参与打折，可以将部分商品总价填写至此字段，默认全部商品可打折
//        // 如果该值未传入,但传入了【订单总金额】,【不可打折金额】 则该值默认为【订单总金额】- 【不可打折金额】
//        //        String discountableAmount = "1.00"; //
//
//        // (可选) 订单不可打折金额，可以配合商家平台配置折扣活动，如果酒水不参与打折，则将对应金额填写至此字段
//        // 如果该值未传入,但传入了【订单总金额】,【打折金额】,则该值默认为【订单总金额】-【打折金额】
//        String undiscountableAmount = "0.0";
//
//        // 卖家支付宝账号ID，用于支持一个签约账号下支持打款到不同的收款账号，(打款到sellerId对应的支付宝账号)
//        // 如果该字段为空，则默认为与支付宝签约的商户的PID，也就是appid对应的PID
//        String sellerId = "";
//
//        // 订单描述，可以对交易或商品进行一个详细地描述，比如填写"购买商品3件共20.00元"
//        String body = "购买商品3件共20.00元";
//
//        // 商户操作员编号，添加此参数可以为商户操作员做销售统计
//        String operatorId = "test_operator_id";
//
//        // (必填) 商户门店编号，通过门店号和商家后台可以配置精准到门店的折扣信息，详询支付宝技术支持
//        String storeId = "test_store_id";
//
//        // 业务扩展参数，目前可添加由支付宝分配的系统商编号(通过setSysServiceProviderId方法)，详情请咨询支付宝技术支持
//        String providerId = "2088100200300400500";
//        ExtendParams extendParams = new ExtendParams();
//        extendParams.setSysServiceProviderId(providerId);
//
//        // 支付超时，线下扫码交易定义为5分钟
//        String timeoutExpress = "5m";
//
//        // 商品明细列表，需填写购买商品详细信息，
//        List<GoodsDetail> goodsDetailList = new ArrayList<GoodsDetail>();
//        // 创建一个商品信息，参数含义分别为商品id（使用国标）、名称、单价（单位为分）、数量，如果需要添加商品类别，详见GoodsDetail
//        GoodsDetail goods1 = GoodsDetail.newInstance("goods_id001", "xxx面包", 1000, 1);
//        // 创建好一个商品后添加至商品明细列表
//        goodsDetailList.add(goods1);
//
//        // 继续创建并添加第一条商品信息，用户购买的产品为“黑人牙刷”，单价为5.00元，购买了两件
//        GoodsDetail goods2 = GoodsDetail.newInstance("goods_id002", "xxx牙刷", 500, 2);
//        goodsDetailList.add(goods2);
//
//        String appAuthToken = "应用授权令牌";//根据真实值填写
//
//        // 创建条码支付请求builder，设置请求参数
//        AlipayTradePayRequestBuilder builder = new AlipayTradePayRequestBuilder()
//                //            .setAppAuthToken(appAuthToken)
//                .setOutTradeNo(outTradeNo).setSubject(subject).setAuthCode(authCode)
//                .setTotalAmount(totalAmount).setStoreId(storeId)
//                .setUndiscountableAmount(undiscountableAmount).setBody(body).setOperatorId(operatorId)
//                .setExtendParams(extendParams).setSellerId(sellerId)
//                .setGoodsDetailList(goodsDetailList).setTimeoutExpress(timeoutExpress);
//
//        // 调用tradePay方法获取当面付应答
//        AlipayF2FPayResult result = service.tradePay(builder);
//        switch (result.getTradeStatus()) {
//            case SUCCESS:
//                log.info("支付宝支付成功: )");
//                break;
//
//            case FAILED:
//                log.error("支付宝支付失败!!!");
//                break;
//
//            case UNKNOWN:
//                log.error("系统异常，订单状态未知!!!");
//                break;
//
//            default:
//                log.error("不支持的交易状态，交易返回异常!!!");
//                break;
//        }
//    }
//
//    // 测试当面付2.0查询订单
//    public void test_trade_query() {
//        // (必填) 商户订单号，通过此商户订单号查询当面付的交易状态
//        String outTradeNo = "tradepay14817938139942440181";
//
//        // 创建查询请求builder，设置请求参数
//        AlipayTradeQueryRequestBuilder builder = new AlipayTradeQueryRequestBuilder()
//                .setOutTradeNo(outTradeNo);
//
//        AlipayF2FQueryResult result = tradeService.queryTradeResult(builder);
//        switch (result.getTradeStatus()) {
//            case SUCCESS:
//                log.info("查询返回该订单支付成功: )");
//
//                AlipayTradeQueryResponse response = result.getResponse();
//                dumpResponse(response);
//
//                log.info(response.getTradeStatus());
//                if (Utils.isListNotEmpty(response.getFundBillList())) {
//                    for (TradeFundBill bill : response.getFundBillList()) {
//                        log.info(bill.getFundChannel() + ":" + bill.getAmount());
//                    }
//                }
//                break;
//
//            case FAILED:
//                log.error("查询返回该订单支付失败或被关闭!!!");
//                break;
//
//            case UNKNOWN:
//                log.error("系统异常，订单支付状态未知!!!");
//                break;
//
//            default:
//                log.error("不支持的交易状态，交易返回异常!!!");
//                break;
//        }
//    }
//
//    // 测试当面付2.0退款
//    public void test_trade_refund() {
//        // (必填) 外部订单号，需要退款交易的商户外部订单号
//        String outTradeNo = "tradepay14817938139942440181";
//
//        // (必填) 退款金额，该金额必须小于等于订单的支付金额，单位为元
//        String refundAmount = "0.01";
//
//        // (可选，需要支持重复退货时必填) 商户退款请求号，相同支付宝交易号下的不同退款请求号对应同一笔交易的不同退款申请，
//        // 对于相同支付宝交易号下多笔相同商户退款请求号的退款交易，支付宝只会进行一次退款
//        String outRequestNo = "";
//
//        // (必填) 退款原因，可以说明用户退款原因，方便为商家后台提供统计
//        String refundReason = "正常退款，用户买多了";
//
//        // (必填) 商户门店编号，退款情况下可以为商家后台提供退款权限判定和统计等作用，详询支付宝技术支持
//        String storeId = "test_store_id";
//
//        // 创建退款请求builder，设置请求参数
//        AlipayTradeRefundRequestBuilder builder = new AlipayTradeRefundRequestBuilder()
//                .setOutTradeNo(outTradeNo).setRefundAmount(refundAmount).setRefundReason(refundReason)
//                .setOutRequestNo(outRequestNo).setStoreId(storeId);
//
//        AlipayF2FRefundResult result = tradeService.tradeRefund(builder);
//        switch (result.getTradeStatus()) {
//            case SUCCESS:
//                log.info("支付宝退款成功: )");
//                break;
//
//            case FAILED:
//                log.error("支付宝退款失败!!!");
//                break;
//
//            case UNKNOWN:
//                log.error("系统异常，订单退款状态未知!!!");
//                break;
//
//            default:
//                log.error("不支持的交易状态，交易返回异常!!!");
//                break;
//        }
//    }
//
//    /**
//     * 查询订单getScanRecord
//     */
//    @RequestMapping("getScanRecord")
//    @ResponseBody
//    public BaseResp getScanRecord(String outTradeNo) {
//        BaseResp baseResp=new BaseResp();
//        try {
//            log.info("进入查询");
//            ScanRecord order = scanRecordService.findOrderByOuttradeno(outTradeNo);
//            baseResp.setSuccess(1);
//            baseResp.setData(order);
//            return baseResp;
//        } catch (Exception e) {
//           log.info("查询订单异常");
//           baseResp.setErrorMsg("查询订单异常"+e.getMessage());
//           baseResp.setSuccess(0);
//           return baseResp;
//        }
//
//    }
//
//    // 测试当面付2.0生成支付二维码
//    @RequestMapping("trade_precreate.do")
//    @ResponseBody
//    public BaseResp trade_precreate(Double totalAmount, HttpServletRequest request, HttpServletResponse res) {
//        BaseResp baseResp=new BaseResp();
//        User user = (User) request.getSession().getAttribute("user");
//        ScanRecord scanRecord = new ScanRecord();
//        if (user == null) {
//            baseResp.setSuccess(0);
//            baseResp.setErrorMsg("用户未登录");
//            return baseResp;
//        } else {
//            DecimalFormat df = new DecimalFormat("#.00");
//            scanRecord.setUserid(user.getUserId());
////            植入sessionid
//            scanRecord.setSellerid(request.getSession().getId());
//            // (必填) 商户网站订单系统中唯一订单号，64个字符以内，只能包含字母、数字、下划线，
//            // 需保证商户系统端不能重复，建议通过数据库sequence生成，
//            String outTradeNo = "tradeprecreate" + System.currentTimeMillis()
//                    + (long) (Math.random() * 10000000L);
//            scanRecord.setOuttradeno(outTradeNo);
//            // (必填) 订单标题，粗略描述用户的支付目的。如“xxx品牌xxx门店当面付扫码消费”
//            String subject = "一梦工作室";
//            scanRecord.setSubject(subject);
//            scanRecord.setTotalamount(new BigDecimal(df.format(totalAmount)));
//            scanRecord.setCreateTime(new Date());
////            String totalAmount="8888";
//            // (必填) 订单总金额，单位为元，不能超过1亿元
//            // 如果同时传入了【打折金额】,【不可打折金额】,【订单总金额】三者,则必须满足如下条件:【订单总金额】=【打折金额】+【不可打折金额】
////         map.put("totalAmount",totalAmount);
//            // (可选) 订单不可打折金额，可以配合商家平台配置折扣活动，如果酒水不参与打折，则将对应金额填写至此字段
//            // 如果该值未传入,但传入了【订单总金额】,【打折金额】,则该值默认为【订单总金额】-【打折金额】
//            String undiscountableAmount = "0";
//            // 卖家支付宝账号ID，用于支持一个签约账号下支持打款到不同的收款账号，(打款到sellerId对应的支付宝账号)
//            // 如果该字段为空，则默认为与支付宝签约的商户的PID，也就是appid对应的PID
//            String sellerId = "";
//
//            // 订单描述，可以对交易或商品进行一个详细地描述，比如填写"购买商品2件共15.00元"
////            计算积分
//            String body = "充值" + scanRecord.getTotalamount() + "元 -赠送" + scanRecord.getTotalamount().multiply(new BigDecimal(1000)) + "积分";
//            scanRecord.setBody(body);
////            Integer  loadmoney= user.getDownloadmoney()+Integer.parseInt(scanRecord.getTotalamount().multiply(new BigDecimal(1000)).toString());
////           用户积分计算
////            BigDecimal money=new BigDecimal(user.getDownloadmoney()).add(scanRecord.getTotalamount().multiply(new BigDecimal(1000)));
//////            user.setDownloadmoney(money.doubleValue());
//
//            // 商户操作员编号，添加此参数可以为商户操作员做销售统计
//            String operatorId = "test_operator_id";
//
//            // (必填) 商户门店编号，通过门店号和商家后台可以配置精准到门店的折扣信息，详询支付宝技术支持
//            String storeId = "test_store_id";
//
//            // 业务扩展参数，目前可添加由支付宝分配的系统商编号(通过setSysServiceProviderId方法)，详情请咨询支付宝技术支持
//            ExtendParams extendParams = new ExtendParams();
//            extendParams.setSysServiceProviderId("2088100200300400500");
//
//            // 支付超时，定义为120分钟
//            String timeoutExpress = "120m";
//
//            // 商品明细列表，需填写购买商品详细信息，
//            List<GoodsDetail> goodsDetailList = new ArrayList<GoodsDetail>();
//            // 创建一个商品信息，参数含义分别为商品id（使用国标）、名称、单价（单位为分）、数量，如果需要添加商品类别，详见GoodsDetail
//            GoodsDetail goods1 = GoodsDetail.newInstance("goods_id001", "梦网1000积分", 1, 1);
//            // 创建好一个商品后添加至商品明细列表
//            goodsDetailList.add(goods1);
//
//            // 继续创建并添加第一条商品信息，用户购买的产品为“黑人牙刷”，单价为5.00元，购买了两件
////        GoodsDetail goods2 = GoodsDetail.newInstance("goods_id002", "xxx牙刷", 500, 2);
////        goodsDetailList.add(goods2);
//
//            // 创建扫码支付请求builder，设置请求参数
//            AlipayTradePrecreateRequestBuilder builder = new AlipayTradePrecreateRequestBuilder()
//                    .setSubject(subject).setTotalAmount(totalAmount + "").setOutTradeNo(outTradeNo)
//                    .setUndiscountableAmount(undiscountableAmount).setSellerId(sellerId).setBody(body)
//                    .setOperatorId(operatorId).setStoreId(storeId).setExtendParams(extendParams)
//                    .setTimeoutExpress(timeoutExpress)
//                    .setNotifyUrl("http://www.yimem.com/alipay_callback.do")//支付宝服务器主动通知商户服务器里指定的页面http路径,根据需要设置
//                    .setGoodsDetailList(goodsDetailList);
//            AlipayF2FPrecreateResult result = tradeService.tradePrecreate(builder);
//            switch (result.getTradeStatus()) {
//                case SUCCESS:
//                    log.info("支付宝预下单成功: )");
//
//                    AlipayTradePrecreateResponse response = result.getResponse();
//                    dumpResponse(response);
//                    scanRecord.setQrcode(response.getQrCode());
//                    scanRecordService.insertSelective(scanRecord);
//                    // 需要修改为运行机器上的路径
////                String filePath = String.format("D:\\qr-%s.png",
////                        response.getOutTradeNo());
////                log.info("filePath:" + filePath);
////                ZxingUtils.getQRCodeImge(response.getQrCode(), 256, filePath);
//                    System.out.println(response.getQrCode());
//                    break;
//
//                case FAILED:
//                    log.error("支付宝预下单失败!!!");
//                    break;
//
//                case UNKNOWN:
//                    log.error("系统异常，预下单状态未知!!!");
//                    break;
//
//                default:
//                    log.error("不支持的交易状态，交易返回异常!!!");
//                    break;
//            }
////            request.getSession().setAttribute("scanRecord", scanRecord);
//            baseResp.setSuccess(1);
//            baseResp.setData(scanRecord);
//            return baseResp;
//        }
//
//    }
//
//
//    @RequestMapping("alipay_callback.do")
//    private String callBack(HttpServletRequest request) throws AlipayApiException {
//        log.info("收到支付宝异步通知！");
////        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
//        // 取出支付宝回调携带的所有参数并进行转换，数组转换为字符串
//        Map<String, String[]> tempParams = request.getParameterMap();
//        JSONArray jArray = new JSONArray();
//        jArray.add(tempParams);
//        log.info("支付传入参数：" + jArray.toString());
//        //  参数存放 Map
//        Map<String, String> requestParams = new HashMap<>();
//        for (Iterator<String> iterator = tempParams.keySet().iterator(); iterator.hasNext(); ) {
//            String key = iterator.next();
//            String[] strs = tempParams.get(key);
//            String str = "";
//            // 这里如果数组的长度是1，说明只有一个，直接赋值就好，如果超过一个，后面加一个逗号来隔离
//            for (int i = 0; i < strs.length; i++) {
//                str = strs.length - 1 == i ? str + strs[i] : str + strs[i] + ",";
//            }
//            requestParams.put(key, str);
//        }
//        // 去除sign_type
//        requestParams.remove("sign_type");
//        try {
//            //公钥-对数据验签
//            boolean result = AlipaySignature.rsaCheckV1(requestParams, Constants.PUBLIC_KEY, Constants.CHAR_SET, Constants.SIGN_TYPE);
//            if (!result) {
//                log.info("未支付成功");
//                return "failed";
//            }
//            log.info("验证通过");
//            //若参数中的appid和填入的appid不相同，则为异常通知
//            if (!Configs.getAppid().equals(requestParams.get("app_id"))) {
//                log.info("与付款时的appid不同，此为异常通知，应忽略！");
//                return "failed";
//            }
//            //在数据库中查找订单号对应的订单，并将其金额与数据库中的金额对比，若对不上，也为异常通知
//            ScanRecord order = scanRecordService.findOrderByOuttradeno(requestParams.get("out_trade_no"));
//            log.info("实体类参数"+order.toString());
//            if (order == null) {
//                log.warn(requestParams.get("out_trade_no") + "查无此订单！");
//                return "failed";
//            }
//            if (order.getTotalamount().doubleValue() != Double.parseDouble(requestParams.get("total_amount"))) {
//                log.info("与付款时的金额不同，此为异常通知，应忽略！");
//                return "failed";
//            }
//            if ("TRADE_SUCCESS".equals(order.getStatus())) {
//                log.info("如果订单已经支付成功了，就直接忽略这次通知");
//                return "success"; //如果订单已经支付成功了，就直接忽略这次通知
//            }
////            try {
////                order.setGmtCreate(sdf.parse(requestParams.get("gmt_create")));
////                order.setGmtPayment(sdf.parse(requestParams.get("gmt_payment")));
////            } catch (ParseException e) {
////                e.printStackTrace();
////            }
//            order.setGmtPayment(new Date());
//            order.setNotifyId(requestParams.get("notify_id"));
//            order.setBuyerId(requestParams.get("buyer_id"));
//            order.setBuyerLogonId(requestParams.get("buyer_logon_id"));
//            String status = requestParams.get("trade_status");
//            order.setStatus(status);
//            if (status.equals("WAIT_BUYER_PAY")) { //如果状态是正在等待用户付款
////                if ("WAIT_BUYER_PAY".equals(order.getStatus())) {
//                    log.info("如果状态是正在等待用户付款");
//                    scanRecordService.modifyTradeStatus(order);
////                }
//            } else if (status.equals("TRADE_CLOSED")) { //如果状态是未付款交易超时关闭，或支付完成后全额退款
////                if ("TRADE_CLOSED".equals(order.getStatus())) {
//                    log.info("如果状态是未付款交易超时关闭，或支付完成后全额退款");
//                    scanRecordService.modifyTradeStatus(order);
////                }
//            } else if (status.equals("TRADE_SUCCESS") || status.equals("TRADE_FINISHED")) { //如果状态是已经支付成功
////                if ("TRADE_SUCCESS".equals(order.getStatus())) {
//                    log.info("如果状态是已经支付成功");
//                    scanRecordService.modifyTradeStatus(order);
//                    User u=new User();
//                    u.setUserId(order.getUserid());
//                    User user= null;
//                    try {
//                        user = userServic.getUserById(u);
//                        log.info("用户积分"+user.getDownloadmoney());
//                        BigDecimal money = new BigDecimal(user.getDownloadmoney()).add(order.getTotalamount().multiply(new BigDecimal(1000)));
//                        log.info("增加后的积分"+money.doubleValue());
//                        user.setDownloadmoney(money.doubleValue());
//                        userServic.updateUserMoney(user);
////                        通知前端他跳转
//                        HttpSession session = MySessionContext.getSession(order.getSellerid());
//                        session.setAttribute(order.getOuttradeno(),status);
//                    } catch (Exception e) {
//                        e.printStackTrace();
//                    }
//
////                }
//            } else {
//                scanRecordService.modifyTradeStatus(order);
//            }
//            return "success";
//        } catch (AlipayApiException e) {
//            log.info("支付宝回调验证异常", e);
//            return "failed";
//        }
//    }
//}
