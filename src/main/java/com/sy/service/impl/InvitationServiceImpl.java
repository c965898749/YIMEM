package com.sy.service.impl;

import com.sy.mapper.ForumMapper;
import com.sy.mapper.InvitationMapper;
import com.sy.model.Invitation;
import com.sy.model.resp.BaseResp;
import com.sy.service.InvitationService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
@Service
public class InvitationServiceImpl implements InvitationService {
    @Autowired
    private ForumMapper forumMapper;
    @Autowired
    private InvitationMapper mapper;
    @Override
    public Integer save(Invitation invitation) {
        return mapper.insert(invitation);
    }

    @Override
    public BaseResp findAll(Invitation invitation) {
        BaseResp baseResp = new BaseResp();

        List<Invitation>  invitationList = mapper.selectAll(invitation);

        if (invitationList.size()!=0){
            for (Invitation invitation1 :invitationList){
                int invitationId = invitation1.getId();
                int count = forumMapper.queryReplayCount(invitationId);
                invitation1.setReplaycount(count);
            }
            baseResp.setSuccess(200);
            baseResp.setData(invitationList);
        }else {
            baseResp.setSuccess(404);
        }
        return baseResp;
    }

    @Override
    public Integer findAllCount(Invitation invitation) {
        return mapper.selectAllCount(invitation);
    }

    @Override
    public Invitation findById(Integer id) {
        return mapper.selectById(id);
    }

    @Override
    public List<Invitation> selectMaxreadCount(Integer page, Integer pageSize) {
        return mapper.selectMaxreadCount(page,pageSize);
    }

    @Override
    public Integer findMaxreadCountCount() {
        return mapper.selectMaxreadCountCount();
    }

    @Override
    public List<Invitation> findAspam(Integer page, Integer pageSize) {
        return mapper.selectAspam(page,pageSize);
    }

    @Override
    public List<Invitation> findNotice(Integer page, Integer pageSize) {
        return mapper.selectNotice(page,pageSize);
    }


}
