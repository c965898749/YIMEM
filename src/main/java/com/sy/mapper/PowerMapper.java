package com.sy.mapper;

import com.sy.model.Power;
import org.apache.ibatis.annotations.Param;

public interface PowerMapper {
    Power getPowerByUserId(@Param("userId") Integer userId);
}
