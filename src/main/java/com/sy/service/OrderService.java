package com.sy.service;

import com.alipay.api.AlipayApiException;
import com.sy.model.UserOrder;

import java.math.BigDecimal;

public interface OrderService {
    int deleteByPrimaryKey(Integer orderId);

    int insert(UserOrder record);

    int insertSelective(UserOrder record);

    UserOrder selectByPrimaryKey(Integer orderId);

    int updateByPrimaryKeySelective(UserOrder record);

    int updateByPrimaryKey(UserOrder record);

    String orderPay(BigDecimal orderAmount) throws AlipayApiException;

    UserOrder getOrderByOrderNo(String orderNo);


}

