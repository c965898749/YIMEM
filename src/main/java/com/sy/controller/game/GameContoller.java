package com.sy.controller.game;

import com.sy.model.User;
import com.sy.model.game.GiftListItemVO;
import com.sy.model.game.LevelUpResult;
import com.sy.model.game.TokenDto;
import com.sy.model.resp.BaseResp;
import com.sy.service.GameServiceService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import java.util.List;

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
    @RequestMapping(value = "cardLevelUp2", method = RequestMethod.POST)
    @CrossOrigin
    public BaseResp cardLevelUp2(@RequestBody TokenDto token, HttpServletRequest request) {
        BaseResp baseResp = new BaseResp();
        try {
            baseResp = gameServiceService.cardLevelUp2(token, request);
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
    @PostMapping("ranking")
    @CrossOrigin
    public BaseResp ranking(@RequestBody TokenDto token, HttpServletRequest request) {
        BaseResp baseResp = new BaseResp();
        try {
            baseResp = gameServiceService.ranking(token, request);
            return baseResp;
        } catch (Exception e) {
            e.printStackTrace();
            baseResp.setSuccess(0);
            return baseResp;
        }
    }

    @PostMapping("ranking100")
    @CrossOrigin
    public BaseResp ranking100(@RequestBody TokenDto token, HttpServletRequest request) {
        BaseResp baseResp = new BaseResp();
        try {
            baseResp = gameServiceService.ranking100(token, request);
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
    @PostMapping("stopLevel")
    @CrossOrigin
    public BaseResp stopLevel(@RequestBody TokenDto token, HttpServletRequest request) {
        BaseResp baseResp = new BaseResp();
        try {
            baseResp = gameServiceService.stopLevel(token, request);
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

    @PostMapping("messageList")
    @CrossOrigin
    public BaseResp messageList(@RequestBody TokenDto token, HttpServletRequest request) {
        BaseResp baseResp = new BaseResp();
        try {
            baseResp = gameServiceService.messageList(token, request);
            return baseResp;
        } catch (Exception e) {
            e.printStackTrace();
            baseResp.setSuccess(0);
            return baseResp;
        }
    }

    @PostMapping("receive")
    @CrossOrigin
    public BaseResp receive(@RequestBody TokenDto token, HttpServletRequest request) {
        BaseResp baseResp = new BaseResp();
        try {
            baseResp = gameServiceService.receive(token, request);
            return baseResp;
        } catch (Exception e) {
            e.printStackTrace();
            baseResp.setSuccess(0);
            return baseResp;
        }
    }

    @PostMapping("giftExchangeCode")
    @CrossOrigin
    public BaseResp giftExchangeCode(@RequestBody TokenDto token, HttpServletRequest request) {
        BaseResp baseResp = new BaseResp();
        try {
            baseResp = gameServiceService.giftExchangeCode(token, request);
            return baseResp;
        } catch (Exception e) {
            e.printStackTrace();
            baseResp.setSuccess(0);
            return baseResp;
        }
    }

    @PostMapping("checkHechen")
    @CrossOrigin
    public BaseResp checkHechen(@RequestBody TokenDto token, HttpServletRequest request) {
        BaseResp baseResp = new BaseResp();
        try {
            baseResp = gameServiceService.checkHechen(token, request);
            return baseResp;
        } catch (Exception e) {
            e.printStackTrace();
            baseResp.setSuccess(0);
            return baseResp;
        }
    }
    @PostMapping("findHechenCard")
    @CrossOrigin
    public BaseResp findHechenCard(@RequestBody TokenDto token, HttpServletRequest request) {
        BaseResp baseResp = new BaseResp();
        try {
            baseResp = gameServiceService.findHechenCard(token, request);
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
    @PostMapping("tuPuhenchenList")
    @CrossOrigin
    public BaseResp tuPuhenchenList(@RequestBody TokenDto token, HttpServletRequest request) {
        BaseResp baseResp = new BaseResp();
        try {
            baseResp = gameServiceService.tuPuhenchenList(token, request);
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
