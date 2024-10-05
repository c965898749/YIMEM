package com.sy.controller.monitor;
import com.sy.entity.ActivationKey;
import com.sy.model.resp.BaseResp;
import com.sy.model.resp.ResultVO;
import com.sy.service.ActivationKeyService;
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

    @RequestMapping(value = "serverDetials", method = RequestMethod.GET)
    public AjaxResult getInfo() throws Exception
    {
        Server server = new Server();
        server.copyTo();
        return AjaxResult.success(server);
    }

    @RequestMapping(value = "insertCode", method = RequestMethod.POST)
    public ResultVO insertCode(ActivationKey data)
    {
        ResultVO resultVO = new ResultVO();
        activationKeyService.insert(data);
        return resultVO;
    }

    @RequestMapping(value = "updateCode", method = RequestMethod.POST)
    public ResultVO updateCode(ActivationKey data)
    {
        ResultVO resultVO = new ResultVO();
        data.setStatus("1");
        activationKeyService.update(data);
        return resultVO;
    }


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
}
