package com.sy.mapper;

import com.sy.model.DownloadCategory;
import org.apache.ibatis.annotations.Param;

import java.util.List;

public interface DownloadCategoryMapper {
    DownloadCategory selcetRoot();
    List<DownloadCategory> selectByPid(@Param("pid") Integer pid);
    DownloadCategory selectByid(@Param("id") Integer id);
}
