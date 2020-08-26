package com.sy.service;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

public interface WeixinPostService {
    String weixinPost(HttpServletRequest request);
    String getTicketData(String SessionId)throws IOException;
    void sendTemplate(String fromUserName,String nickName,String time)throws IOException;
    void sendTemplate2(String fromUserName,String username,String nickname,String time)throws IOException;
    void sendTemplate3(String fromUserName,String nickname,String username,String time)throws IOException;
    void sendTemplate4(String fromUserName,String nickname,String outTradeNo,String money,String totalAmount)throws IOException;
}
