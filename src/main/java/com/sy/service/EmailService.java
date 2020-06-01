package com.sy.service;

import com.sy.model.MailModel;
import com.sy.model.User;
import com.sy.model.resp.ResultVO;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

public interface EmailService {
    ResultVO sendEmail(MailModel mail);
    ResultVO emailManage(String mail, User user, HttpServletRequest request);
    ResultVO registSendIdCode(String mail, String idcode,HttpServletResponse response, HttpServletRequest request);
}
