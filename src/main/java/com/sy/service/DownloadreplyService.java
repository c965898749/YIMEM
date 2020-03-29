package com.sy.service;

import com.sy.expection.CsdnExpection;
import com.sy.model.Downloadreply;
import org.apache.ibatis.annotations.Param;

import java.util.List;

public interface DownloadreplyService {
    Integer save(Downloadreply downloadreply) throws CsdnExpection;
    Integer remove(Integer id);
    List<Downloadreply> findByDowid(Integer dowid) throws CsdnExpection;
}
