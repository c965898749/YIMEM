package com.sy.mapper;

import com.sy.model.Invitation;
import org.apache.ibatis.annotations.Param;

import java.util.List;

public interface InvitationMapper {
    Integer insert(Invitation invitation);
    List<Invitation> selectAll(Invitation invitation);
    Integer selectAllCount(Invitation invitation);
    Invitation selectById(Integer id);

    List<Invitation> selectMaxreadCount(@Param("page") Integer page, @Param("pageSize") Integer pageSize);
    Integer selectMaxreadCountCount();
    List<Invitation> selectAspam(@Param("page") Integer page,@Param("pageSize") Integer pageSize);
    List<Invitation> selectNotice(@Param("page") Integer page,@Param("pageSize") Integer pageSize);
}
