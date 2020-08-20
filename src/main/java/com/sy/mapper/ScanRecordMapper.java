package com.sy.mapper;

import com.sy.model.ScanRecord;
import org.apache.ibatis.annotations.Param;

public interface ScanRecordMapper {
    int deleteByPrimaryKey(Integer id);

    int insert(ScanRecord record);

    int insertSelective(ScanRecord record);

    ScanRecord selectByPrimaryKey(Integer id);

    ScanRecord findOrderByOuttradeno(@Param("outtradeno") String outtradeno);

    int updateByPrimaryKeySelective(ScanRecord record);

    int updateByPrimaryKey(ScanRecord record);

    int modifyTradeStatus(ScanRecord record);
}