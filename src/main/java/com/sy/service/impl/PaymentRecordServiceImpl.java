package com.sy.service.impl;

import com.sy.mapper.PaymentRecordMapper;
import com.sy.model.PaymentRecord;
import com.sy.service.PaymentRecordService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class PaymentRecordServiceImpl implements PaymentRecordService {
    @Autowired
    private PaymentRecordMapper paymentRecordMapper;
    @Override
    public int deleteByPrimaryKey(Integer id) {
        return paymentRecordMapper.deleteByPrimaryKey(id);
    }

    @Override
    public int insert(PaymentRecord record) {
        return paymentRecordMapper.insert(record);
    }

    @Override
    public int insertSelective(PaymentRecord record) {
        return paymentRecordMapper.insertSelective(record);
    }

    @Override
    public PaymentRecord selectByPrimaryKey(Integer id) {
        return paymentRecordMapper.selectByPrimaryKey(id);
    }

    @Override
    public int updateByPrimaryKeySelective(PaymentRecord record) {
        return paymentRecordMapper.updateByPrimaryKeySelective(record);
    }

    @Override
    public int updateByPrimaryKey(PaymentRecord record) {
        return paymentRecordMapper.updateByPrimaryKey(record);
    }
}
