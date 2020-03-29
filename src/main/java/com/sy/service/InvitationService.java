package com.sy.service;

import com.sy.model.Invitation;
import com.sy.model.resp.BaseResp;

import java.util.List;

public interface InvitationService {
    Integer save(Invitation invitation);
    BaseResp findAll(Invitation invitation);
    Integer findAllCount(Invitation invitation);
    Invitation findById(Integer id);

    List<Invitation> selectMaxreadCount(Integer page,Integer pageSize);
    Integer findMaxreadCountCount();
    List<Invitation> findAspam(Integer page,Integer pageSize);
    List<Invitation> findNotice(Integer page,Integer pageSize);
}
