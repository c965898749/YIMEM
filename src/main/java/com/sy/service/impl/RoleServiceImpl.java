package com.sy.service.impl;


import com.sy.mapper.RoleMapper;
import com.sy.model.Role;
import com.sy.model.resp.BaseResp;
import com.sy.service.RoleService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Isolation;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@Transactional(readOnly = true)
public class RoleServiceImpl implements RoleService {

    @Autowired
    private RoleMapper roleMapper;

    @Override
    public List<Role> getRoleList() throws Exception {
        return roleMapper.getRoleList();
    }

    @Override
    public Role getRole(Role role) throws Exception {
        return roleMapper.getRole(role);
    }

    @Override
    public Role getRoleR(Role role) throws Exception {
        return roleMapper.getRoleR(role);
    }

    @Override
    public List<Role> getRoleIdAndNameList() throws Exception {
        return roleMapper.getRoleIdAndNameList();
    }
    @Transactional(isolation = Isolation.DEFAULT,propagation = Propagation.REQUIRED)
    @Override
    public BaseResp modifyUserStatus(Integer status, Integer id) throws Exception {
        BaseResp baseResp = new BaseResp();
        Integer result = roleMapper.madifyRoleStatus(status,id);
        if (result!=0){
            baseResp.setSuccess(1);
        }else {
            baseResp.setSuccess(0);
        }
        return baseResp;
    }

    @Transactional(isolation = Isolation.DEFAULT,propagation = Propagation.REQUIRED)
    @Override
    public int addRole(Role role) throws Exception {
        return roleMapper.addRole(role);
    }

    @Transactional(isolation = Isolation.DEFAULT,propagation = Propagation.REQUIRED)
    @Override
    public int modifyRole(Role role) throws Exception {
        return roleMapper.modifyRole(role);
    }

    @Transactional(isolation = Isolation.DEFAULT,propagation = Propagation.REQUIRED)
    @Override
    public int deleteRole(Role role) throws Exception {
        return roleMapper.deleteRole(role);
    }


}
