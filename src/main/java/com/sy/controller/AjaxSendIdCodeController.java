package com.sy.controller;

import com.sy.mapper.EmilMapper;
import com.sy.model.Emil;
import com.sy.model.User;
import com.sy.model.resp.ResultVO;
import com.sy.service.EmailService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

import javax.mail.MessagingException;
import javax.mail.internet.AddressException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.Date;

@Controller
public class AjaxSendIdCodeController {
    @Autowired
    private EmailService emailService;
    @Autowired
    private EmilMapper emilMapper;
    @RequestMapping("sendIdCode.action")
    @ResponseBody
    public ResultVO AjaxSendIdCode(String mail, HttpServletRequest request) {
        ResultVO resultVO = new ResultVO();
        User user = (User) request.getSession().getAttribute("user");
        Date emilcodetime = (Date) request.getSession().getAttribute("sendIdCode");
        if (emilcodetime == null) {
            Date date = new Date();
            request.getSession().setAttribute("sendIdCode", date);
        } else {
            Date date = new Date();
            if ((date.getTime() - emilcodetime.getTime()) < 60) {
                resultVO.setError(0);
                resultVO.setMessage((date.getTime() - emilcodetime.getTime()) + "秒后重新发送邮件");
                return resultVO;
            }
        }
        if (user == null) {
            resultVO.setError(0);
            resultVO.setMessage("你还未登录！");
            return resultVO;
        }
        Emil emil = emilMapper.selectByPrimaryKey(user.getUserId());
        if (emil != null) {
            resultVO.setError(0);
            resultVO.setMessage("该邮箱已注册过！");
            return resultVO;
        }
        System.out.println(mail);
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
