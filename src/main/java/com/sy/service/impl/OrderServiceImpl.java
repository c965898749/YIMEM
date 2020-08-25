package com.sy.service.impl;

import com.alipay.api.AlipayApiException;
import com.sy.mapper.UserOrderMapper;
import com.sy.model.AlipayBean;
import com.sy.model.UserOrder;
import com.sy.service.OrderService;
import com.sy.tool.AliPayUtil;
import com.sy.tool.OrderEnum;
import org.apache.commons.lang3.time.DateFormatUtils;
import org.springframework.stereotype.Service;
import javax.annotation.Resource;
import java.math.BigDecimal;
import java.util.Date;
import java.util.UUID;
@Service
public class OrderServiceImpl implements OrderService {
    @Override
    public int deleteByPrimaryKey(Integer orderId) {
        return 0;
    }

    @Override
    public int insert(UserOrder record) {
        return 0;
    }

    @Override
    public int insertSelective(UserOrder record) {
        return 0;
    }

    @Override
    public UserOrder selectByPrimaryKey(Integer orderId) {
        return null;
    }

    @Override
    public int updateByPrimaryKeySelective(UserOrder record) {
        return 0;
    }

    @Override
    public int updateByPrimaryKey(UserOrder record) {
        return 0;
    }

    @Resource
    private UserOrderMapper userOrderMapper;

    @Resource
    private AliPayUtil aliPayUtil;

    /**
     * 下单
     *
     * @param orderAmount 订单金额
     * @return 返回支付结果页面内容
     * @throws AlipayApiException
     */
    public String orderPay(BigDecimal orderAmount) throws AlipayApiException {

        //1. 产生订单
        UserOrder order = new UserOrder();
        String outTradeNo = "tradepay" + System.currentTimeMillis()+ (long) (Math.random() * 10000000L);
        order.setOrderNo(System.currentTimeMillis() + "");
        order.setUserId(UUID.randomUUID().toString());
        order.setOrderAmount(orderAmount);
        order.setOrderStatus(OrderEnum.ORDER_STATUS_NOT_PAY.getStatus());
        String format = "yyyy-MM-dd HH:mm:ss";
        order.setCreateTime(DateFormatUtils.format(new Date(), format));
        order.setLastUpdateTime(DateFormatUtils.format(new Date(), format));

        userOrderMapper.insert(order);

        //2. 调用支付宝
        AlipayBean alipayBean = new AlipayBean();
        alipayBean.setOut_trade_no(order.getOrderNo());
        alipayBean.setSubject("充值:" + order.getOrderAmount());
        alipayBean.setTotal_amount(orderAmount.toString());
        String pay = aliPayUtil.pay(alipayBean);
        return pay;
    }

    /**
     * 根据订单号查询订单
     *
     * @param orderNo
     * @return 返回订单信息
     */
    public UserOrder getOrderByOrderNo(String orderNo) {
        return userOrderMapper.selectOneByorderNo(orderNo);
    }
}
