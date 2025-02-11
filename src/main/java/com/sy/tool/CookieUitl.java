package com.sy.tool;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;

public class CookieUitl {
    public static String getToken(HttpServletRequest request){
        Cookie[] cookies = request.getCookies();
        String token = "";
        if (cookies != null && cookies.length > 0) {
            for (Cookie cookie : cookies) {

                switch (cookie.getName()) {
                    //对cookie进行解码
                    case "token":
                        token = cookie.getValue();
                        break;
                    default:
                        break;
                }
            }
        }
        return token;
    }
}
