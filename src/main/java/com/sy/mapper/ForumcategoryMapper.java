package com.sy.mapper;

import com.sy.model.DownloadCategory;
import com.sy.model.Forumcategory;
import com.sy.model.Upload;
import org.apache.ibatis.annotations.Param;

import java.util.List;

public interface ForumcategoryMapper {
    Forumcategory selcetRoot();
    List<Forumcategory> selectByPid(@Param("pid") Integer pid);
    Forumcategory selectByid(@Param("id") Integer id);
}
