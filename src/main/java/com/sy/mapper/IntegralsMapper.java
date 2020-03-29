package com.sy.mapper;

import com.sy.model.Integrals;
import org.apache.ibatis.annotations.Param;

import java.util.List;

public interface IntegralsMapper {
    Integer insert(Integrals integrals);
    List<Integrals> selectByUserid(@Param("userid") Integer userid, @Param("page") Integer page, @Param("pageSize") Integer pageSize);
    Integer selectAllCount(@Param("userid") Integer userid);
}
