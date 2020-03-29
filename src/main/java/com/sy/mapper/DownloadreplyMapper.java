package com.sy.mapper;

import com.sy.model.Downloadreply;
import org.apache.ibatis.annotations.Param;

import java.util.List;

public interface DownloadreplyMapper {
    Integer selectcountByDowid(@Param("dowid") Integer dowid);
    List<Downloadreply> selectAllByDowid(@Param("dowid") Integer dowid);
    Integer insert(Downloadreply downloadreply);
    Integer delete(Integer id);
    List<Downloadreply> selectByDowid(@Param("dowid") Integer dowid);
}
