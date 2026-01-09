package com.sy.controller.monitor;
import com.sy.entity.ActivationKey;
import com.sy.entity.GameMesage;
import com.sy.model.resp.BaseResp;
import com.sy.model.resp.ResultVO;
import com.sy.service.ActivationKeyService;
import com.sy.service.GameMesageService;
import com.sy.service.GameServiceService;
import com.sy.service.impl.Server;
import com.sy.vo.AjaxResult;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

/**
 * 服务器监控
 *
 * @author ruoyi
 */
@RestController
@RequestMapping("/monitor/server")
public class ServerController
{
    @Autowired
    private ActivationKeyService activationKeyService;
    @Autowired
    private GameMesageService gameMesageService;
    @Autowired
    private GameServiceService gameServiceService;

    @RequestMapping(value = "serverDetials", method = RequestMethod.GET)
    public AjaxResult getInfo() throws Exception
    {
        Server server = new Server();
        server.copyTo();
        return AjaxResult.success(server);
    }

    //插入挂机消息
    @RequestMapping(value = "insertMessage", method = RequestMethod.POST)
    public ResultVO insertMessage(GameMesage mesage)
    {
        ResultVO resultVO = new ResultVO();
        gameMesageService.insert(mesage);
        return resultVO;
    }


    //插入激活码
    @RequestMapping(value = "insertCode", method = RequestMethod.POST)
    public ResultVO insertCode(ActivationKey data)
    {
        ResultVO resultVO = new ResultVO();
        activationKeyService.insert(data);
        return resultVO;
    }

    //更新激活码
    @RequestMapping(value = "updateCode", method = RequestMethod.POST)
    public ResultVO updateCode(ActivationKey data)
    {
        ResultVO resultVO = new ResultVO();
        data.setStatus("1");
        activationKeyService.update(data);
        return resultVO;
    }

    //获取激活码
    @RequestMapping(value = "getActCode", method = RequestMethod.GET)
    public BaseResp getActCode(ActivationKey data)
    {
        BaseResp baseResp = new BaseResp();
        try {
            baseResp=activationKeyService.queryBytype(data);
        } catch (Exception e) {
            e.printStackTrace();
            baseResp.setSuccess(0);
            baseResp.setErrorMsg("服务器异常");
        }
        return baseResp;
    }

    //卡密生成接口
    @RequestMapping(value = "addActCode", method = RequestMethod.GET)
    public BaseResp addActCode()
    {
        BaseResp baseResp = new BaseResp();
        try {
           gameServiceService.addActCode();
        } catch (Exception e) {
            e.printStackTrace();
            baseResp.setSuccess(0);
            baseResp.setErrorMsg("服务器异常");
        }
        return baseResp;
    }
}
