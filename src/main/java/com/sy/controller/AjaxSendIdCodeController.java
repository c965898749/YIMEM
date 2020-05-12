package com.sy.controller;

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

@Controller
public class AjaxSendIdCodeController {
    @Autowired
    private EmailService emailService;

    @RequestMapping("sendIdCode.action")
    @ResponseBody
    public ResultVO AjaxSendIdCode(String mail, HttpServletResponse response, HttpServletRequest request) {

        System.out.println(mail);
        return emailService.emailManage(mail, response,request);


    }
    @RequestMapping("regist.action")
    @ResponseBody
    public ResultVO registSendIdCode(String mail, String idcode,HttpServletResponse response, HttpServletRequest request) {
        ResultVO resultVO = emailService.registSendIdCode(mail,idcode, response,request);

        System.out.println("resultVO");
        return resultVO;


    }

}
