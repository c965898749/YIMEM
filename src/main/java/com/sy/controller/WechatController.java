package com.sy.controller;

import com.alibaba.fastjson.JSONObject;
import com.sy.model.User;
import com.sy.model.WeiXin;
import com.sy.model.resp.BaseResp;
import com.sy.service.UserServic;
import com.sy.service.WeixinPostService;
import com.sy.tool.Constants;
import com.sy.tool.MySessionContext;
import com.sy.tool.SignUtil;
import com.sy.tool.WxUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.io.IOException;
import java.io.PrintWriter;
import java.security.MessageDigest;
import java.util.Arrays;

@Controller
@RequestMapping("/wechat")
public class WechatController {
    @Value("${DNBX_TOKEN}")
    private String DNBX_TOKEN;

    @Autowired
    private WeixinPostService weixinPostService;
    @Autowired
    private UserServic userServic;

    @RequestMapping(value = "/getURL", method = RequestMethod.GET)
    @ResponseBody
    public String getURL(HttpServletRequest request) throws Exception {

        String state = WxUtils.getURL(Constants.APPID, Constants.REDIRECT_URI, request.getSession().getId());
        return state;
    }

    @RequestMapping(value = "/login", method = RequestMethod.GET)
    public String login(String code, String state, HttpServletRequest request, HttpServletResponse response, Model model) throws Exception {
        request.setCharacterEncoding("UTF-8");  //微信服务器POST消息时用的是UTF-8编码，在接收时也要用同样的编码，否则中文会乱码；
        response.setCharacterEncoding("UTF-8"); //在响应消息（回复消息给用户）时，也将编码方式设置为UTF-8，原理同上；
        HttpSession session = MySessionContext.getSession(state);
        String s = WxUtils.getLoginAcessToken(Constants.APPID, Constants.APPSECRET, code);
        WeiXin weiXin = JSONObject.parseObject(s, WeiXin.class);
        String openid = weiXin.getOpenid();
        User user = userServic.getUserByopenid(openid);
        if (user == null) {
            return "no";
        }

        session.setAttribute("user", user);
        model.addAttribute("user", user);
        return "yes";
    }

    @RequestMapping(value = "/connect", method = {RequestMethod.GET, RequestMethod.POST})
    @ResponseBody
    public void connectWeixin(HttpServletRequest request, HttpServletResponse response) throws IOException {
        // 将请求、响应的编码均设置为UTF-8（防止中文乱码）
        request.setCharacterEncoding("UTF-8");  //微信服务器POST消息时用的是UTF-8编码，在接收时也要用同样的编码，否则中文会乱码；
        response.setCharacterEncoding("UTF-8"); //在响应消息（回复消息给用户）时，也将编码方式设置为UTF-8，原理同上；
        boolean isGet = request.getMethod().toLowerCase().equals("get");
        PrintWriter out = response.getWriter();
        try {
            if (isGet) {
                String signature = request.getParameter("signature");// 微信加密签名
                String timestamp = request.getParameter("timestamp");// 时间戳
                String nonce = request.getParameter("nonce");// 随机数
                String echostr = request.getParameter("echostr");//随机字符串

                // 通过检验signature对请求进行校验，若校验成功则原样返回echostr，表示接入成功，否则接入失败
                String[] params = new String[]{DNBX_TOKEN, timestamp, nonce};
                Arrays.sort(params);
                // 将三个参数字符串拼接成一个字符串进行sha1加密
                String clearText = params[0] + params[1] + params[2];
                String algorithm = "SHA-1";
                String sign = new String(
                        org.apache.commons.codec.binary.Hex.encodeHex(MessageDigest.getInstance(algorithm).digest((clearText).getBytes()), true));
                // 开发者获得加密后的字符串可与signature对比，标识该请求来源于微信
                if (signature.equals(sign)) {
                    System.out.println("Connect the weixin server is successful.");
                    response.getWriter().print(echostr);
                } else {
                    System.out.println("Failed to verify the signature!");
                }

            } else {
                String respMessage = "异常消息！";

                try {
                    respMessage = weixinPostService.weixinPost(request);
                    out.write(respMessage);
                    System.out.println("The request completed successfully");
                    System.out.println("to weixin server " + respMessage);
                } catch (Exception e) {
                    System.out.println("Failed to convert the message from weixin!");
                }

            }
        } catch (Exception e) {
            System.out.println("Connect the weixin server is error.");
        } finally {
            out.close();
        }
    }

}
