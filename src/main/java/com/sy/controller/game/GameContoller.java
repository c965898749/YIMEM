package com.sy.controller.game;

import com.sy.model.User;
import com.sy.model.game.TokenDto;
import com.sy.model.resp.BaseResp;
import com.sy.service.GameServiceService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;

@RestController
public class GameContoller {
    @Autowired
    private GameServiceService gameServiceService;

    @RequestMapping(value = "loginGame", method = RequestMethod.POST)
    @CrossOrigin
    public BaseResp loginGame(@RequestBody User user, HttpServletRequest request) {
        BaseResp baseResp = new BaseResp();
        try {
            baseResp = gameServiceService.loginGame(user, request);
            return baseResp;
        } catch (Exception e) {
            e.printStackTrace();
            baseResp.setSuccess(0);
            return baseResp;
        }
    }

    @RequestMapping(value = "registerGame", method = RequestMethod.POST)
    @CrossOrigin
    public BaseResp registerGame(@RequestBody User user, HttpServletRequest request) {
        BaseResp baseResp = new BaseResp();
        try {
            baseResp = gameServiceService.registerGame(user, request);
            return baseResp;
        } catch (Exception e) {
            e.printStackTrace();
            baseResp.setSuccess(0);
            return baseResp;
        }
    }

    @RequestMapping(value = "danChou", method = RequestMethod.POST)
    @CrossOrigin
    public BaseResp danChou(@RequestBody TokenDto token, HttpServletRequest request) {
        BaseResp baseResp = new BaseResp();
        try {
            baseResp = gameServiceService.danChou(token, request);
            return baseResp;
        } catch (Exception e) {
            e.printStackTrace();
            baseResp.setSuccess(0);
            return baseResp;
        }
    }
    @RequestMapping(value = "soulChou", method = RequestMethod.POST)
    @CrossOrigin
    public BaseResp soulChou(@RequestBody TokenDto token, HttpServletRequest request) {
        BaseResp baseResp = new BaseResp();
        try {
            baseResp = gameServiceService.soulChou(token, request);
            return baseResp;
        } catch (Exception e) {
            e.printStackTrace();
            baseResp.setSuccess(0);
            return baseResp;
        }
    }

    @RequestMapping(value = "shiChou", method = RequestMethod.POST)
    @CrossOrigin
    public BaseResp shiChou(@RequestBody TokenDto token, HttpServletRequest request) {
        BaseResp baseResp = new BaseResp();
        try {
            baseResp = gameServiceService.shiChou(token, request);
            return baseResp;
        } catch (Exception e) {
            e.printStackTrace();
            baseResp.setSuccess(0);
            return baseResp;
        }
    }

    @RequestMapping(value = "updateGame", method = RequestMethod.POST)
    @CrossOrigin
    public BaseResp updateGame(@RequestBody TokenDto token, HttpServletRequest request) {
        BaseResp baseResp = new BaseResp();
        try {
            baseResp = gameServiceService.updateGame(token, request);
            return baseResp;
        } catch (Exception e) {
            e.printStackTrace();
            baseResp.setSuccess(0);
            return baseResp;
        }
    }

    @RequestMapping(value = "cardLevelUp", method = RequestMethod.POST)
    @CrossOrigin
    public BaseResp cardLevelUp(@RequestBody TokenDto token, HttpServletRequest request) {
        BaseResp baseResp = new BaseResp();
        try {
            baseResp = gameServiceService.cardLevelUp(token, request);
            return baseResp;
        } catch (Exception e) {
            e.printStackTrace();
            baseResp.setSuccess(0);
            return baseResp;
        }
    }

    @RequestMapping(value = "changeState", method = RequestMethod.POST)
    @CrossOrigin
    public BaseResp changeState(@RequestBody TokenDto token, HttpServletRequest request) {
        BaseResp baseResp = new BaseResp();
        try {
            baseResp = gameServiceService.changeState(token, request);
            return baseResp;
        } catch (Exception e) {
            e.printStackTrace();
            baseResp.setSuccess(0);
            return baseResp;
        }
    }

    @RequestMapping(value = "pveDetail", method = RequestMethod.POST)
    @CrossOrigin
    public BaseResp pveDetail(@RequestBody TokenDto token, HttpServletRequest request) {
        BaseResp baseResp = new BaseResp();
        try {
            baseResp = gameServiceService.pveDetail(token, request);
            return baseResp;
        } catch (Exception e) {
            e.printStackTrace();
            baseResp.setSuccess(0);
            return baseResp;
        }
    }


    @PostMapping("battle")
    @CrossOrigin
    public BaseResp battle(@RequestBody TokenDto token, HttpServletRequest request) {
        BaseResp baseResp = new BaseResp();
        try {
            baseResp = gameServiceService.start(token, request);
            return baseResp;
        } catch (Exception e) {
            e.printStackTrace();
            baseResp.setSuccess(0);
            return baseResp;
        }
    }

    @PostMapping("battle2")
    @CrossOrigin
    public BaseResp battle2(@RequestBody TokenDto token, HttpServletRequest request) {
        BaseResp baseResp = new BaseResp();
        try {
            baseResp = gameServiceService.start2(token, request);
            return baseResp;
        } catch (Exception e) {
            e.printStackTrace();
            baseResp.setSuccess(0);
            return baseResp;
        }
    }


    @PostMapping("jingji")
    @CrossOrigin
    public BaseResp jingji(@RequestBody TokenDto token, HttpServletRequest request) {
        BaseResp baseResp = new BaseResp();
        try {
            baseResp = gameServiceService.jingji(token, request);
            return baseResp;
        } catch (Exception e) {
            e.printStackTrace();
            baseResp.setSuccess(0);
            return baseResp;
        }
    }

    @PostMapping("playBattle")
    @CrossOrigin
    public BaseResp playBattle(@RequestBody TokenDto token, HttpServletRequest request) {
        BaseResp baseResp = new BaseResp();
        try {
            baseResp = gameServiceService.playBattle(token, request);
            return baseResp;
        } catch (Exception e) {
            e.printStackTrace();
            baseResp.setSuccess(0);
            return baseResp;
        }
    }

    @PostMapping("notice")
    @CrossOrigin
    public BaseResp playBattle(HttpServletRequest request) {
        BaseResp baseResp = new BaseResp();
        try {
            baseResp = gameServiceService.notice(request);
            return baseResp;
        } catch (Exception e) {
            e.printStackTrace();
            baseResp.setSuccess(0);
            return baseResp;
        }
    }

    @PostMapping("signUp")
    @CrossOrigin
    public BaseResp qiangdao(@RequestBody TokenDto token, HttpServletRequest request) {
        BaseResp baseResp = new BaseResp();
        try {
            baseResp = gameServiceService.qiangdao(token, request);
            return baseResp;
        } catch (Exception e) {
            e.printStackTrace();
            baseResp.setSuccess(0);
            return baseResp;
        }
    }

    @RequestMapping(value = "itemUpdate", method = RequestMethod.POST)
    @CrossOrigin
    public BaseResp itemUpdate(@RequestBody TokenDto token, HttpServletRequest request) {
        BaseResp baseResp = new BaseResp();
        try {
            baseResp = gameServiceService.itemUpdate(token, request);
            return baseResp;
        } catch (Exception e) {
            e.printStackTrace();
            baseResp.setSuccess(0);
            return baseResp;
        }
    }

    @PostMapping("changerHeader")
    @CrossOrigin
    public BaseResp changerHeader(@RequestBody TokenDto token, HttpServletRequest request) {
        BaseResp baseResp = new BaseResp();
        try {
            baseResp = gameServiceService.changerHeader(token, request);
            return baseResp;
        } catch (Exception e) {
            e.printStackTrace();
            baseResp.setSuccess(0);
            return baseResp;
        }
    }
}
