package com.sy.mapper;

import com.sy.model.UserOrder;
import org.apache.ibatis.annotations.Param;

public interface UserOrderMapper {
    int deleteByPrimaryKey(Integer orderId);

    int insert(UserOrder record);

    int insertSelective(UserOrder record);

    UserOrder selectByPrimaryKey(Integer orderId);

    int updateByPrimaryKeySelective(UserOrder record);

    int updateByPrimaryKey(UserOrder record);

    UserOrder selectOneByorderNo(@Param("orderNo") String orderNo);
}
