package com.sy.service;


import com.sy.model.Integrals;
import org.apache.ibatis.annotations.Param;

import java.util.List;

public interface IntegralsService {
    Integer save(Integrals integrals);
    List<Integrals> findByUserid(@Param("userid") Integer userid, @Param("page") Integer page, @Param("pageSize") Integer pageSize);
    Integer findAllCount(@Param("userid") Integer userid);
}
