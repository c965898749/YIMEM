package com.sy.controller.game;

import com.sy.model.User;
import com.sy.model.game.GiftListItemVO;
import com.sy.model.game.LevelUpResult;
import com.sy.model.game.TokenDto;
import com.sy.model.resp.BaseResp;
import com.sy.service.GameServiceService;
import com.sy.tool.NoRepeatSubmit;
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

    /**
     * 单抽
     *
     * @param token
     * @param request
     * @return
     */
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

    /**
     * 打造
     *
     * @param token
     * @param request
     * @return
     */
    @RequestMapping(value = "danChouEq", method = RequestMethod.POST)
    @CrossOrigin
    public BaseResp danChouEq(@RequestBody TokenDto token, HttpServletRequest request) {
        BaseResp baseResp = new BaseResp();
        try {
            baseResp = gameServiceService.danChouEq(token, request);
            return baseResp;
        } catch (Exception e) {
            e.printStackTrace();
            baseResp.setSuccess(0);
            return baseResp;
        }
    }

    /**
     * 出售
     *
     * @param token
     * @param request
     * @return
     */
    @RequestMapping(value = "characteSell", method = RequestMethod.POST)
    @CrossOrigin
    public BaseResp characteSell(@RequestBody TokenDto token, HttpServletRequest request) {
        BaseResp baseResp = new BaseResp();
        try {
            baseResp = gameServiceService.characteSell(token, request);
            return baseResp;
        } catch (Exception e) {
            e.printStackTrace();
            baseResp.setSuccess(0);
            return baseResp;
        }
    }

    /**
     * 魂抽
     *
     * @param token
     * @param request
     * @return
     */
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

    /**
     * 十抽
     *
     * @param token
     * @param request
     * @return
     */
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

    @RequestMapping(value = "updateTli", method = RequestMethod.POST)
    @CrossOrigin
    public BaseResp updateTli(@RequestBody TokenDto token, HttpServletRequest request) {
        BaseResp baseResp = new BaseResp();
        try {
            baseResp = gameServiceService.updateTli(token, request);
            return baseResp;
        } catch (Exception e) {
            e.printStackTrace();
            baseResp.setSuccess(0);
            return baseResp;
        }
    }

    @RequestMapping(value = "updateTli3", method = RequestMethod.POST)
    @CrossOrigin
    public BaseResp updateTli3(@RequestBody TokenDto token, HttpServletRequest request) {
        BaseResp baseResp = new BaseResp();
        try {
            baseResp = gameServiceService.updateTli3(token, request);
            return baseResp;
        } catch (Exception e) {
            e.printStackTrace();
            baseResp.setSuccess(0);
            return baseResp;
        }
    }


    @RequestMapping(value = "updateTli2", method = RequestMethod.POST)
    @CrossOrigin
    public BaseResp updateTli2(@RequestBody TokenDto token, HttpServletRequest request) {
        BaseResp baseResp = new BaseResp();
        try {
            baseResp = gameServiceService.updateTli2(token, request);
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
    @RequestMapping(value = "eqCardLevelUp", method = RequestMethod.POST)
    @CrossOrigin
    public BaseResp eqCardLevelUp(@RequestBody TokenDto token, HttpServletRequest request) {
        BaseResp baseResp = new BaseResp();
        try {
            baseResp = gameServiceService.eqCardLevelUp(token, request);
            return baseResp;
        } catch (Exception e) {
            e.printStackTrace();
            baseResp.setSuccess(0);
            return baseResp;
        }
    }
    /**
     * 强化卡牌
     *
     * @param token
     * @param request
     * @return
     */
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
    /**
     * 强化卡牌
     *
     * @param token
     * @param request
     * @return
     */
    @RequestMapping(value = "eqCardLevelUp2", method = RequestMethod.POST)
    @CrossOrigin
    public BaseResp eqCardLevelUp2(@RequestBody TokenDto token, HttpServletRequest request) {
        BaseResp baseResp = new BaseResp();
        try {
            baseResp = gameServiceService.eqCardLevelUp2(token, request);
            return baseResp;
        } catch (Exception e) {
            e.printStackTrace();
            baseResp.setSuccess(0);
            return baseResp;
        }
    }

    /**
     * 上阵
     * @param token
     * @param request
     * @return
     */
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

    /**
     * 卸下
     * @param token
     * @param request
     * @return
     */
    @RequestMapping(value = "changeEqState", method = RequestMethod.POST)
    @CrossOrigin
    public BaseResp changeEqState(@RequestBody TokenDto token, HttpServletRequest request) {
        BaseResp baseResp = new BaseResp();
        try {
            baseResp = gameServiceService.changeEqState(token, request);
            return baseResp;
        } catch (Exception e) {
            e.printStackTrace();
            baseResp.setSuccess(0);
            return baseResp;
        }
    }

    /**
     * 上装
     * @param token
     * @param request
     * @return
     */
    @RequestMapping(value = "changeEqState2", method = RequestMethod.POST)
    @CrossOrigin
    public BaseResp changeEqState2(@RequestBody TokenDto token, HttpServletRequest request) {
        BaseResp baseResp = new BaseResp();
        try {
            baseResp = gameServiceService.changeEqState2(token, request);
            return baseResp;
        } catch (Exception e) {
            e.printStackTrace();
            baseResp.setSuccess(0);
            return baseResp;
        }
    }


    @RequestMapping(value = "changeName", method = RequestMethod.POST)
    @CrossOrigin
    public BaseResp changeName(@RequestBody TokenDto token, HttpServletRequest request) {
        BaseResp baseResp = new BaseResp();
        try {
            baseResp = gameServiceService.changeName(token, request);
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

    @RequestMapping(value = "getActivityList", method = RequestMethod.POST)
    @CrossOrigin
    public BaseResp getActivityList(@RequestBody TokenDto token, HttpServletRequest request) {
        BaseResp baseResp = new BaseResp();
        try {
            baseResp = gameServiceService.getActivityList(token, request);
            return baseResp;
        } catch (Exception e) {
            e.printStackTrace();
            baseResp.setSuccess(0);
            return baseResp;
        }
    }


    @RequestMapping(value = "getUserActivityDetail", method = RequestMethod.POST)
    @CrossOrigin
    public BaseResp getUserActivityDetail(@RequestBody TokenDto token, HttpServletRequest request) {
        BaseResp baseResp = new BaseResp();
        try {
            baseResp = gameServiceService.getUserActivityDetail(token, request);
            return baseResp;
        } catch (Exception e) {
            e.printStackTrace();
            baseResp.setSuccess(0);
            return baseResp;
        }
    }

    /**
     * 七曜星活动
     *
     * @param token
     * @param request
     * @return
     */
    @RequestMapping(value = "participate", method = RequestMethod.POST)
    @CrossOrigin
    public BaseResp participate(@RequestBody TokenDto token, HttpServletRequest request) {
        BaseResp baseResp = new BaseResp();
        try {
            baseResp = gameServiceService.participate(token, request);
            return baseResp;
        } catch (Exception e) {
            e.printStackTrace();
            baseResp.setSuccess(0);
            return baseResp;
        }
    }

    /**
     * 热门活动
     *
     * @param token
     * @param request
     * @return
     */
    @RequestMapping(value = "participate2", method = RequestMethod.POST)
    @CrossOrigin
    public BaseResp participate2(@RequestBody TokenDto token, HttpServletRequest request) {
        BaseResp baseResp = new BaseResp();
        try {
            baseResp = gameServiceService.participate2(token, request);
            return baseResp;
        } catch (Exception e) {
            e.printStackTrace();
            baseResp.setSuccess(0);
            return baseResp;
        }
    }

    /**
     * 竞技场
     *
     * @param token
     * @param request
     * @return
     */

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

    /**
     * 祝福
     *
     * @param token
     * @param request
     * @return
     */

    @PostMapping("blessing")
    @CrossOrigin
    public BaseResp blessing(@RequestBody TokenDto token, HttpServletRequest request) {
        BaseResp baseResp = new BaseResp();
        try {
            baseResp = gameServiceService.blessing(token, request);
            return baseResp;
        } catch (Exception e) {
            e.printStackTrace();
            baseResp.setSuccess(0);
            return baseResp;
        }
    }

    /**
     * 收到祝福
     *
     * @param token
     * @param request
     * @return
     */

    @PostMapping("reviceblessing")
    @CrossOrigin
    public BaseResp reviceblessing(@RequestBody TokenDto token, HttpServletRequest request) {
        BaseResp baseResp = new BaseResp();
        try {
            baseResp = gameServiceService.reviceblessing(token, request);
            return baseResp;
        } catch (Exception e) {
            e.printStackTrace();
            baseResp.setSuccess(0);
            return baseResp;
        }
    }

    /**
     * 收到祝福
     *
     * @param token
     * @param request
     * @return
     */

    @PostMapping("njblessing")
    @CrossOrigin
    public BaseResp njblessing(@RequestBody TokenDto token, HttpServletRequest request) {
        BaseResp baseResp = new BaseResp();
        try {
            baseResp = gameServiceService.njblessing(token, request);
            return baseResp;
        } catch (Exception e) {
            e.printStackTrace();
            baseResp.setSuccess(0);
            return baseResp;
        }
    }

    /**
     * 好有切磋
     *
     * @param token
     * @param request
     * @return
     */
    @PostMapping("battle3")
    @CrossOrigin
    public BaseResp battle3(@RequestBody TokenDto token, HttpServletRequest request) {
        BaseResp baseResp = new BaseResp();
        try {
            baseResp = gameServiceService.start3(token, request);
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

    @PostMapping("mapRanking100")
    @CrossOrigin
    public BaseResp mapRanking100(@RequestBody TokenDto token, HttpServletRequest request) {
        BaseResp baseResp = new BaseResp();
        try {
            baseResp = gameServiceService.mapRanking100(token, request);
            return baseResp;
        } catch (Exception e) {
            e.printStackTrace();
            baseResp.setSuccess(0);
            return baseResp;
        }
    }
    @PostMapping("arenaRanking100")
    @CrossOrigin
    public BaseResp arenaRanking100(@RequestBody TokenDto token, HttpServletRequest request) {
        BaseResp baseResp = new BaseResp();
        try {
            baseResp = gameServiceService.arenaRanking100(token, request);
            return baseResp;
        } catch (Exception e) {
            e.printStackTrace();
            baseResp.setSuccess(0);
            return baseResp;
        }
    }


    /**
     * 刷图、探险
     *
     * @param token
     * @param request
     * @return
     */
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

    /**
     * 塔
     *
     * @param token
     * @param request
     * @return
     */
    @PostMapping("battle5")
    @CrossOrigin
    public BaseResp battle5(@RequestBody TokenDto token, HttpServletRequest request) {
        BaseResp baseResp = new BaseResp();
        try {
            baseResp = gameServiceService.start5(token, request);
            return baseResp;
        } catch (Exception e) {
            e.printStackTrace();
            baseResp.setSuccess(0);
            return baseResp;
        }
    }
    /**
     * 判断是否能刷塔
     *
     * @param token
     * @param request
     * @return
     */
    @PostMapping("getTower")
    @CrossOrigin
    public BaseResp getTower(@RequestBody TokenDto token, HttpServletRequest request) {
        BaseResp baseResp = new BaseResp();
        try {
            baseResp = gameServiceService.getTower(token, request);
            return baseResp;
        } catch (Exception e) {
            e.printStackTrace();
            baseResp.setSuccess(0);
            return baseResp;
        }
    }


    /**
     * 擂台
     *
     * @param token
     * @param request
     * @return
     */
    @PostMapping("battle4")
    @CrossOrigin
    public BaseResp battle4(@RequestBody TokenDto token, HttpServletRequest request) {
        BaseResp baseResp = new BaseResp();
        try {
            baseResp = gameServiceService.start4(token, request);
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

    @PostMapping("friendAllList")
    @CrossOrigin
    public BaseResp friendAllList(@RequestBody TokenDto token, HttpServletRequest request) {
        BaseResp baseResp = new BaseResp();
        try {
            baseResp = gameServiceService.friendAllList(token, request);
            return baseResp;
        } catch (Exception e) {
            e.printStackTrace();
            baseResp.setSuccess(0);
            return baseResp;
        }
    }

    @PostMapping("invitationSend")
    @CrossOrigin
    public BaseResp invitationSend(@RequestBody TokenDto token, HttpServletRequest request) {
        BaseResp baseResp = new BaseResp();
        try {
            baseResp = gameServiceService.invitationSend(token, request);
            return baseResp;
        } catch (Exception e) {
            e.printStackTrace();
            baseResp.setSuccess(0);
            return baseResp;
        }
    }

    @PostMapping("invitationHandle")
    @CrossOrigin
    public BaseResp invitationHandle(@RequestBody TokenDto token, HttpServletRequest request) {
        BaseResp baseResp = new BaseResp();
        try {
            baseResp = gameServiceService.invitationHandle(token, request);
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

    @PostMapping("arenaMessageList")
    @CrossOrigin
    public BaseResp arenaMessageList(@RequestBody TokenDto token, HttpServletRequest request) {
        BaseResp baseResp = new BaseResp();
        try {
            baseResp = gameServiceService.arenaMessageList(token, request);
            return baseResp;
        } catch (Exception e) {
            e.printStackTrace();
            baseResp.setSuccess(0);
            return baseResp;
        }
    }

    /**
     * 邮件接收
     * @param token
     * @param request
     * @return
     */
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

    @PostMapping("getStore")
    @CrossOrigin
    public BaseResp getStore(@RequestBody TokenDto token, HttpServletRequest request) {
        BaseResp baseResp = new BaseResp();
        try {
            baseResp = gameServiceService.getStore(token, request);
            return baseResp;
        } catch (Exception e) {
            e.printStackTrace();
            baseResp.setSuccess(0);
            return baseResp;
        }
    }

    /**
     *
     * @param token
     * @param request
     * @return
     */
    @PostMapping("chongzhi")
    @CrossOrigin
    public BaseResp chongzhi(@RequestBody TokenDto token, HttpServletRequest request) {
        BaseResp baseResp = new BaseResp();
        try {
            baseResp = gameServiceService.chongzhi(token, request);
            return baseResp;
        } catch (Exception e) {
            e.printStackTrace();
            baseResp.setSuccess(0);
            return baseResp;
        }
    }
    @PostMapping("getStore2")
    @CrossOrigin
    public BaseResp getStore2(@RequestBody TokenDto token, HttpServletRequest request) {
        BaseResp baseResp = new BaseResp();
        try {
            baseResp = gameServiceService.getStore2(token, request);
            return baseResp;
        } catch (Exception e) {
            e.printStackTrace();
            baseResp.setSuccess(0);
            return baseResp;
        }
    }
    /**
     * 购买
     * @param token
     * @param request
     * @return
     */
    @PostMapping("buyStore")
    @CrossOrigin
    public BaseResp buyStore(@RequestBody TokenDto token, HttpServletRequest request) {
        BaseResp baseResp = new BaseResp();
        try {
            baseResp = gameServiceService.buyStore(token, request);
            return baseResp;
        } catch (Exception e) {
            e.printStackTrace();
            baseResp.setSuccess(0);
            return baseResp;
        }
    }

    /**
     * 购买
     * @param token
     * @param request
     * @return
     */
    @PostMapping("buyStore2")
    @CrossOrigin
    public BaseResp buyStore2(@RequestBody TokenDto token, HttpServletRequest request) {
        BaseResp baseResp = new BaseResp();
        try {
            baseResp = gameServiceService.buyStore2(token, request);
            return baseResp;
        } catch (Exception e) {
            e.printStackTrace();
            baseResp.setSuccess(0);
            return baseResp;
        }
    }

    /**
     * 兑换
     * @param token
     * @param request
     * @return
     */
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


    /**
     * 刷图
     * @param token
     * @param request
     * @return
     */
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

    @PostMapping("playBattle2")
    @CrossOrigin
    public BaseResp playBattle2(@RequestBody TokenDto token, HttpServletRequest request) {
        BaseResp baseResp = new BaseResp();
        try {
            baseResp = gameServiceService.playBattle2(token, request);
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

    /**
     * 图谱合成
     *
     * @param token
     * @param request
     * @return
     */

    @PostMapping("hechenCard")
    @CrossOrigin
    public BaseResp hechenCard(@RequestBody TokenDto token, HttpServletRequest request) {
        BaseResp baseResp = new BaseResp();
        try {
            baseResp = gameServiceService.hechenCard(token, request);
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

    @RequestMapping(value = "arenaItemUpdate", method = RequestMethod.POST)
    @CrossOrigin
    public BaseResp arenaItemUpdate(@RequestBody TokenDto token, HttpServletRequest request) {
        BaseResp baseResp = new BaseResp();
        try {
            baseResp = gameServiceService.arenaItemUpdate(token, request);
            return baseResp;
        } catch (Exception e) {
            e.printStackTrace();
            baseResp.setSuccess(0);
            return baseResp;
        }
    }

    @PostMapping("isSignedUp")
    @CrossOrigin
    public BaseResp isSignedUp(@RequestBody TokenDto token, HttpServletRequest request) {
        BaseResp baseResp = new BaseResp();
        try {
            baseResp = gameServiceService.isSignedUp(token, request);
            return baseResp;
        } catch (Exception e) {
            e.printStackTrace();
            baseResp.setSuccess(0);
            return baseResp;
        }
    }

    @PostMapping("arenaSignup")
    @CrossOrigin
    public BaseResp arenaSignup(@RequestBody TokenDto token, HttpServletRequest request) {
        BaseResp baseResp = new BaseResp();
        try {
            baseResp = gameServiceService.arenaSignup(token, request);
            return baseResp;
        } catch (Exception e) {
            e.printStackTrace();
            baseResp.setSuccess(0);
            return baseResp;
        }
    }

    @PostMapping("arenaTem")
    @CrossOrigin
    public BaseResp arenaTem(@RequestBody TokenDto token, HttpServletRequest request) {
        BaseResp baseResp = new BaseResp();
        try {
            baseResp = gameServiceService.arenaTem(token, request);
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

    @PostMapping("mobai")
    @CrossOrigin
    public BaseResp getWeiwanCount(@RequestBody TokenDto token, HttpServletRequest request) {
        BaseResp baseResp = new BaseResp();
        try {
            baseResp = gameServiceService.mobai(token, request);
            return baseResp;
        } catch (Exception e) {
            e.printStackTrace();
            baseResp.setSuccess(0);
            return baseResp;
        }
    }

    /**
     * 背包
     * @param token
     * @param request
     * @return
     */
    @PostMapping("bagItemList")
    @CrossOrigin
    public BaseResp bagItemList(@RequestBody TokenDto token, HttpServletRequest request) {
        BaseResp baseResp = new BaseResp();
        try {
            baseResp = gameServiceService.bagItemList(token, request);
            return baseResp;
        } catch (Exception e) {
            e.printStackTrace();
            baseResp.setSuccess(0);
            return baseResp;
        }
    }

    /**
     * 装备消息
     * @param token
     * @param request
     * @return
     */
    @PostMapping("equipmentNew")
    @CrossOrigin
    public BaseResp equipmentNew(@RequestBody TokenDto token, HttpServletRequest request) {
        BaseResp baseResp = new BaseResp();
        try {
            baseResp = gameServiceService.equipmentNew(token, request);
            return baseResp;
        } catch (Exception e) {
            e.printStackTrace();
            baseResp.setSuccess(0);
            return baseResp;
        }
    }
    /**
     * 装备消息列表
     * @param token
     * @param request
     * @return
     */
    @PostMapping("equipmentMessageList")
    @CrossOrigin
    public BaseResp equipmentMessageList(@RequestBody TokenDto token, HttpServletRequest request) {
        BaseResp baseResp = new BaseResp();
        try {
            baseResp = gameServiceService.equipmentMessageList(token, request);
            return baseResp;
        } catch (Exception e) {
            e.printStackTrace();
            baseResp.setSuccess(0);
            return baseResp;
        }
    }

    /**
     * 使用
     * @param token
     * @param request
     * @return
     */
    @PostMapping("useBagItem")
    @CrossOrigin
    public BaseResp useBagItem(@RequestBody TokenDto token, HttpServletRequest request) {
        BaseResp baseResp = new BaseResp();
        try {
            baseResp = gameServiceService.useBagItem(token, request);
            return baseResp;
        } catch (Exception e) {
            e.printStackTrace();
            baseResp.setSuccess(0);
            return baseResp;
        }
    }
    /**
     * 使用
     * @param token
     * @param request
     * @return
     */
    @PostMapping("yijiantansuo")
    @CrossOrigin
    public BaseResp yijiantansuo(@RequestBody TokenDto token, HttpServletRequest request) {
        BaseResp baseResp = new BaseResp();
        try {
            baseResp = gameServiceService.yijiantansuo(token, request);
            return baseResp;
        } catch (Exception e) {
            e.printStackTrace();
            baseResp.setSuccess(0);
            return baseResp;
        }
    }
    /**
     * 使用
     * @param token
     * @param request
     * @return
     */
    @PostMapping("allCardList")
    @CrossOrigin
    public BaseResp allCardList(@RequestBody TokenDto token, HttpServletRequest request) {
        BaseResp baseResp = new BaseResp();
        try {
            baseResp = gameServiceService.allCardList(token, request);
            return baseResp;
        } catch (Exception e) {
            e.printStackTrace();
            baseResp.setSuccess(0);
            return baseResp;
        }
    }


}
