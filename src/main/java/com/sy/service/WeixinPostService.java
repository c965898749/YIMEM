package com.sy.service;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

public interface WeixinPostService {
    String weixinPost(HttpServletRequest request);
    String getTicketData(String SessionId)throws IOException;
}
