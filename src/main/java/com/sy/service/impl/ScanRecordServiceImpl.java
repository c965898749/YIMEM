package com.sy.service.impl;

import com.sy.mapper.ScanRecordMapper;
import com.sy.model.ScanRecord;
import com.sy.service.ScanRecordService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class ScanRecordServiceImpl implements ScanRecordService {
    @Autowired
    private ScanRecordMapper scanRecordMapper;
    @Override
    public int deleteByPrimaryKey(Integer id) {
        return 0;
    }

    @Override
    public int insert(ScanRecord record) {
        return 0;
    }

    @Override
    public int insertSelective(ScanRecord record) {
        return scanRecordMapper.insertSelective(record);
    }

    @Override
    public ScanRecord selectByPrimaryKey(Integer id) {
        return null;
    }

    @Override
    public int updateByPrimaryKeySelective(ScanRecord record) {
        return 0;
    }

    @Override
    public int updateByPrimaryKey(ScanRecord record) {
        return 0;
    }

    @Override
    public ScanRecord findOrderByOuttradeno(String outTradeNo) {
        return scanRecordMapper.findOrderByOuttradeno(outTradeNo);
    }

    @Override
    public int modifyTradeStatus(ScanRecord record) {
        return scanRecordMapper.modifyTradeStatus(record);
    }
}
