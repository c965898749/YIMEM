package com.sy.service.impl;

import com.sy.entity.GameMesage;
import com.sy.mapper.GameMesageMapper;
import com.sy.service.GameMesageService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class GameMesageServiceImpl implements GameMesageService {

    @Autowired
    private GameMesageMapper gameMesageMapper;
    @Override
    public int insert(GameMesage record) {
        return gameMesageMapper.insert(record);
    }
}
