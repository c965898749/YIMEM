package com.sy.mapper;

import com.sy.model.Download;
import org.apache.ibatis.annotations.Param;

import java.util.List;

public interface DownloadMapper {
    Integer insert(Download download);
    Integer delete(Integer userid, Integer id);
    List<Download> selectAll(@Param("page") Integer page, @Param("pageSize") Integer pageSize);
    List<Download>  selectByUserid(@Param("userid") Integer userid, @Param("page") Integer page, @Param("pageSize") Integer pageSize);
    Integer selectAllCount(@Param("userid") Integer userid);
}
