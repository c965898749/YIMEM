package com.sy.mapper;

import com.sy.model.Information;
import org.apache.ibatis.annotations.Param;

import java.util.List;

public interface InformationMapper {
    Integer insert(Information information);
    List<Information> select(@Param("userId") Integer userId );
    Integer selectcount(@Param("userId") Integer userId );
    List<Information> selectpage(@Param("userId") Integer userId ,@Param("page")Integer page,@Param("pageSize")Integer pageSize);
    Integer removecommentreq(@Param("userId")Integer userId);
    Integer readcommentreq(@Param("userId")Integer userId );
    Integer onclickcommentreq(@Param("userId")Integer userId ,@Param("id") Integer id);
    Integer selectStatus(@Param("id") Integer id);
}
