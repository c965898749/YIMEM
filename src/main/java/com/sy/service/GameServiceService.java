package com.sy.service;

import com.sy.model.User;
import com.sy.model.game.TokenDto;
import com.sy.model.resp.BaseResp;

import javax.servlet.http.HttpServletRequest;

public interface GameServiceService {
    BaseResp loginGame(User user, HttpServletRequest request) throws Exception;
    BaseResp registerGame(User user, HttpServletRequest request) throws Exception;
    BaseResp updateGame(TokenDto token, HttpServletRequest request) throws Exception;
    BaseResp changeState(TokenDto token, HttpServletRequest request) throws Exception;
    BaseResp itemUpdate(TokenDto token, HttpServletRequest request) throws Exception;
    BaseResp danChou(TokenDto token, HttpServletRequest request) throws Exception;
    BaseResp shiChou(TokenDto token, HttpServletRequest request) throws Exception;
    BaseResp start(TokenDto token, HttpServletRequest request) throws Exception;
    BaseResp jingji(TokenDto token, HttpServletRequest request) throws Exception;
    BaseResp playBattle(TokenDto token, HttpServletRequest request) throws Exception;
    BaseResp qiangdao(TokenDto token, HttpServletRequest request) throws Exception;
    BaseResp notice(HttpServletRequest request) throws Exception;
}
