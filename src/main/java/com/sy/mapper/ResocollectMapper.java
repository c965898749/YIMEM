package com.sy.mapper;

import com.sy.model.Download;
import com.sy.model.Resocollect;
import org.apache.ibatis.annotations.Param;

import java.util.List;

public interface ResocollectMapper {
    Integer insert(Resocollect download);
    Integer delete(Resocollect download);
     Resocollect selectAll(@Param("userid") Integer userid,@Param("dowid") Integer dowid);
    List<Resocollect>  selectByUserid(@Param("userid") Integer userid, @Param("page") Integer page, @Param("pageSize") Integer pageSize);
    Integer selectAllCount(@Param("userid") Integer userid);
}
