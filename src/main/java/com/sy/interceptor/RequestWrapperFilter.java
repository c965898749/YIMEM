package com.sy.interceptor;

import org.springframework.stereotype.Component;
import javax.servlet.*;
import javax.servlet.annotation.WebFilter;
import javax.servlet.http.HttpServletRequest;
import java.io.IOException;

/**
 * 请求过滤器，将原生request替换为可重复读的RequestWrapper
 * 拦截所有请求，保证AOP读取请求体后，Controller层能正常接收
 */
@Component
@WebFilter(urlPatterns = "/*", filterName = "requestWrapperFilter")
public class RequestWrapperFilter implements Filter {
    @Override
    public void init(FilterConfig filterConfig) throws ServletException {

    }

    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain) throws IOException, ServletException {
        ServletRequest requestWrapper = null;
        if (request instanceof HttpServletRequest) {
            // 替换为包装后的request
            requestWrapper = new RequestWrapper((HttpServletRequest) request);
        }
        // 继续执行请求链
        chain.doFilter(requestWrapper == null ? request : requestWrapper, response);
    }

    @Override
    public void destroy() {

    }
}