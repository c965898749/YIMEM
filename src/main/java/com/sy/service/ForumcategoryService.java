package com.sy.service;

import com.sy.expection.CsdnExpection;
import com.sy.model.DownloadCategory;
import com.sy.model.Forumcategory;
import org.apache.ibatis.annotations.Param;

import java.util.List;

public interface ForumcategoryService {
    List<Forumcategory> findAll() throws CsdnExpection;
    List<Forumcategory> findByPid(@Param("pid") Integer pid)throws CsdnExpection;
    Forumcategory findByid(@Param("id") Integer id) throws CsdnExpection;
    List<Forumcategory> selcetRoot();
}
