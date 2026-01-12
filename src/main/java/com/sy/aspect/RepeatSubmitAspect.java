package com.sy.aspect;
import com.alibaba.fastjson.JSONObject;
import com.sy.interceptor.RequestWrapper;
import com.sy.model.resp.BaseResp;
import com.sy.tool.NoRepeatSubmit;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.reflect.MethodSignature;
import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import javax.servlet.http.HttpServletRequest;
import java.lang.reflect.Method;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * 防重复请求核心切面
 * ✅ 适配你的JSON格式：从body中解析第一层的token字段
 * ✅ 解决流只能读一次的问题
 * ✅ 基于token做唯一标识，无IP依赖
 * ✅ 3秒内同一个用户+同一个接口只能请求1次
 */
@Aspect
@Component
@Order(Ordered.HIGHEST_PRECEDENCE)
public class RepeatSubmitAspect {

    // 线程安全的JVM缓存，单机足够用，无需清理，自动覆盖
    private static final Map<String, Long> REPEAT_REQUEST_CACHE = new ConcurrentHashMap<>(2048);

    // 切入点：拦截所有添加了@NoRepeatSubmit注解的接口方法
    @Around("@annotation(com.sy.tool.NoRepeatSubmit)")// 替换成你的注解全路径
    public Object around(ProceedingJoinPoint joinPoint) throws Throwable {
        // 1. 获取包装后的request对象
        ServletRequestAttributes attributes = (ServletRequestAttributes) RequestContextHolder.getRequestAttributes();
        HttpServletRequest request = attributes.getRequest();
        RequestWrapper requestWrapper = (RequestWrapper) request;

        // 2. 获取注解配置的限制时间（默认3秒）
        MethodSignature signature = (MethodSignature) joinPoint.getSignature();
        Method method = signature.getMethod();
        NoRepeatSubmit annotation = method.getAnnotation(NoRepeatSubmit.class);
        long limitSeconds = annotation.limitSeconds();

        // 3. ✅【核心】解析你的JSON请求体，获取token
        String token = null;
        String bodyStr = requestWrapper.getBodyString();
        if (bodyStr != null && !bodyStr.isEmpty()) {
            JSONObject jsonObject = JSONObject.parseObject(bodyStr);
            // 直接读取JSON第一层的token字段，完美适配你的报文结构
            token = jsonObject.getString("token");
        }

        // 4. 构建唯一防重KEY = token(用户唯一标识) + 请求接口路径
        // 兜底：如果token为空（极端情况），用sessionId，不影响功能
        String uniqueKey = token == null || token.isEmpty() ? request.getSession().getId() : token;
        String requestUri = request.getRequestURI();
        String cacheKey = uniqueKey + "_" + requestUri;

        // 5. ✅【核心防重逻辑】3秒内重复请求判断
        long currentTime = System.currentTimeMillis();
        if (REPEAT_REQUEST_CACHE.containsKey(cacheKey)) {
            long lastRequestTime = REPEAT_REQUEST_CACHE.get(cacheKey);
            // 当前时间 - 上次请求时间 < 限制时间 → 重复请求，直接拦截
            if (currentTime - lastRequestTime < limitSeconds * 1000) {
                BaseResp baseResp = new BaseResp();
                baseResp.setErrorMsg("操作过于频繁");
                baseResp.setSuccess(0);
                return baseResp;
            }
        }

        // 6. 放行请求，更新缓存的请求时间戳
        REPEAT_REQUEST_CACHE.put(cacheKey, currentTime);
        // 执行接口原业务逻辑
        return joinPoint.proceed();
    }

}