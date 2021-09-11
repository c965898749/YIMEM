package com.sy.interceptor;

import com.sy.model.User;
import com.sy.tool.Constants;
//import com.sy.tools.RedisAPI;
import com.sy.tool.RedisUtil;
//import org.springframework.amqp.core.AmqpTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.servlet.HandlerInterceptor;
import org.springframework.web.servlet.ModelAndView;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * 对用户想访问的url进行拦截
 */
public class LoginInterceptor implements HandlerInterceptor {


    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {


        System.out.println("------拦截器被调用-------");

        String reqUrl = request.getRequestURI();

        User sessionUser = (User)request.getSession().getAttribute(Constants.SESSION_USER);

        String key = Constants.FUNURLS+sessionUser.getRoleId();

        String allowedUrls = RedisUtil.getJedisInstance().get(key);

        if("".equals(allowedUrls)||allowedUrls==null||!allowedUrls.contains(reqUrl)){
            //跳转到401.jsp
            request.getRequestDispatcher("/WEB-INF/pages/401.jsp").
                    forward(request, response);
            return false;
        }

        return true;
    }

    @Override
    public void postHandle(HttpServletRequest httpServletRequest, HttpServletResponse httpServletResponse, Object o, ModelAndView modelAndView) throws Exception {

    }

    @Override
    public void afterCompletion(HttpServletRequest httpServletRequest, HttpServletResponse httpServletResponse, Object o, Exception e) throws Exception {

    }
}
