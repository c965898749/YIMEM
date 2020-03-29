package com.sy.service.impl;

import com.sy.mapper.PowerMapper;
import com.sy.model.Power;
import com.sy.service.PowerService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class PowerServiceImpl implements PowerService {
    @Autowired
    private PowerMapper powerMapper;
    @Override
    public Power getPowerByUserId(Integer userId) {
        return powerMapper.getPowerByUserId(userId);
    }
}
