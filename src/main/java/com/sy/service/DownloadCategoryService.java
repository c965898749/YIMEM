package com.sy.service;

import com.sy.expection.CsdnExpection;
import com.sy.model.DownloadCategory;
import org.apache.ibatis.annotations.Param;

import java.util.List;

public interface DownloadCategoryService {
    List<DownloadCategory> findAll() throws CsdnExpection;
    List<DownloadCategory> findByPid(@Param("pid") Integer pid)throws CsdnExpection;
    DownloadCategory findByid(@Param("id") Integer id) throws CsdnExpection;
}
