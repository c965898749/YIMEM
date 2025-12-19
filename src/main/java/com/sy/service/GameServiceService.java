package com.sy.service;

import com.sy.model.User;
import com.sy.model.game.LevelUpResult;
import com.sy.model.game.TokenDto;
import com.sy.model.resp.BaseResp;

import javax.servlet.http.HttpServletRequest;

public interface GameServiceService {
    BaseResp loginGame(User user, HttpServletRequest request) throws Exception;
    BaseResp registerGame(User user, HttpServletRequest request) throws Exception;
    BaseResp updateGame(TokenDto token, HttpServletRequest request) throws Exception;
    BaseResp changeState(TokenDto token, HttpServletRequest request) throws Exception;
    BaseResp changeName(TokenDto token, HttpServletRequest request) throws Exception;
    BaseResp getActivityList(TokenDto token, HttpServletRequest request) throws Exception;
    BaseResp getTodayRecords(TokenDto token, HttpServletRequest request) throws Exception;
    BaseResp getUserActivityDetail(TokenDto token, HttpServletRequest request) throws Exception;
    BaseResp pveDetail(TokenDto token, HttpServletRequest request) throws Exception;
    BaseResp participate(TokenDto token, HttpServletRequest request) throws Exception;
    BaseResp cardLevelUp(TokenDto token, HttpServletRequest request) throws Exception;
    BaseResp stopLevel(TokenDto token, HttpServletRequest request) throws Exception;
    BaseResp cardLevelUp2(TokenDto token, HttpServletRequest request) throws Exception;
    BaseResp changerHeader(TokenDto token, HttpServletRequest request) throws Exception;
    BaseResp itemUpdate(TokenDto token, HttpServletRequest request) throws Exception;
    BaseResp danChou(TokenDto token, HttpServletRequest request) throws Exception;
    BaseResp characteSell(TokenDto token, HttpServletRequest request) throws Exception;
    BaseResp messageList(TokenDto token, HttpServletRequest request) throws Exception;
    BaseResp receive(TokenDto token, HttpServletRequest request) throws Exception;
    BaseResp getStore(TokenDto token, HttpServletRequest request) throws Exception;
    BaseResp buyStore(TokenDto token, HttpServletRequest request) throws Exception;
    BaseResp giftExchangeCode(TokenDto token, HttpServletRequest request) throws Exception;
    BaseResp checkHechen(TokenDto token, HttpServletRequest request) throws Exception;
    BaseResp tuPuhenchenList(TokenDto token, HttpServletRequest request) throws Exception;
    BaseResp hechenCard(TokenDto token, HttpServletRequest request) throws Exception;
    BaseResp findHechenCard(TokenDto token, HttpServletRequest request) throws Exception;
    BaseResp soulChou(TokenDto token, HttpServletRequest request) throws Exception;
    BaseResp shiChou(TokenDto token, HttpServletRequest request) throws Exception;
    BaseResp start(TokenDto token, HttpServletRequest request) throws Exception;
    BaseResp start3(TokenDto token, HttpServletRequest request) throws Exception;
    BaseResp ranking(TokenDto token, HttpServletRequest request) throws Exception;
    BaseResp ranking100(TokenDto token, HttpServletRequest request) throws Exception;
    BaseResp start2(TokenDto token, HttpServletRequest request) throws Exception;
    BaseResp jingji(TokenDto token, HttpServletRequest request) throws Exception;
    BaseResp friendAllList(TokenDto token, HttpServletRequest request) throws Exception;
    BaseResp invitationSend(TokenDto token, HttpServletRequest request) throws Exception;
    BaseResp invitationHandle(TokenDto token, HttpServletRequest request) throws Exception;
    BaseResp playBattle(TokenDto token, HttpServletRequest request) throws Exception;
    BaseResp qiangdao(TokenDto token, HttpServletRequest request) throws Exception;
    BaseResp notice(HttpServletRequest request) throws Exception;
    BaseResp playBattle2(TokenDto token, HttpServletRequest request) throws Exception;
}
