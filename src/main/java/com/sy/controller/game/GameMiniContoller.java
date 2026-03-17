package com.sy.controller.game;

import com.sy.model.User;
import com.sy.model.resp.BaseResp;
import com.sy.service.GameMiniServiceService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;

@RestController
public class GameMiniContoller {
    @Autowired
    private GameMiniServiceService gameMiniServiceService;
    @RequestMapping(value = "loginMiniGame", method = RequestMethod.POST)
    @CrossOrigin
    public BaseResp loginGame(@RequestBody User user, HttpServletRequest request) {
        BaseResp baseResp = new BaseResp();
        try {
            baseResp = gameMiniServiceService.loginGame(user, request);
            return baseResp;
        } catch (Exception e) {
            e.printStackTrace();
            baseResp.setSuccess(0);
            return baseResp;
        }
    }
}
