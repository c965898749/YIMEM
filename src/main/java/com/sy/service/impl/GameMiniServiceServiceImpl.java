package com.sy.service.impl;

import com.sy.model.User;
import com.sy.model.resp.BaseResp;
import com.sy.service.GameMiniServiceService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import javax.servlet.http.HttpServletRequest;

@Slf4j
@Service
public class GameMiniServiceServiceImpl implements GameMiniServiceService {

    @Override
    public BaseResp loginGame(User user, HttpServletRequest request) throws Exception {
        return null;
    }
}
