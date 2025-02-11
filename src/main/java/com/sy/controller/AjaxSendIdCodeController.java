package com.sy.controller;

import com.sy.mapper.EmilMapper;
import com.sy.model.Emil;
import com.sy.model.User;
import com.sy.model.resp.ResultVO;
import com.sy.service.EmailService;
import com.sy.service.UserServic;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.Date;

@Controller
public class AjaxSendIdCodeController {
    @Autowired
    private EmailService emailService;
    @Autowired
    private EmilMapper emilMapper;
    @Autowired
    UserServic servic;

    @RequestMapping("sendIdCode.action")
    @ResponseBody
    public ResultVO AjaxSendIdCode(String mail, HttpServletRequest request) throws Exception {
        ResultVO resultVO = new ResultVO();
        User user = servic.getUserByRedis(request);
        if (user == null) {
            resultVO.setError(0);
            resultVO.setMessage("你还未登录！");
            return resultVO;
        }
        Emil emil = emilMapper.selectByPrimaryKey(user.getUserId());
        if (emil != null) {
            resultVO.setError(0);
            resultVO.setMessage("你已绑定过邮箱！");
            return resultVO;
        }
        return emailService.emailManage(mail, user, request);


    }

    @RequestMapping("regist.action")
    @ResponseBody
    public ResultVO registSendIdCode(String mail, String idcode, HttpServletResponse response, HttpServletRequest request) {
        ResultVO resultVO = emailService.registSendIdCode(mail, idcode, response, request);

        System.out.println("resultVO");
        return resultVO;


    }

}
