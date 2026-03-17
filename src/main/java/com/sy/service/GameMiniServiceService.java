package com.sy.service;

import com.sy.model.User;
import com.sy.model.resp.BaseResp;

import javax.servlet.http.HttpServletRequest;

public interface GameMiniServiceService {
    BaseResp loginGame(User user, HttpServletRequest request) throws Exception;

}
