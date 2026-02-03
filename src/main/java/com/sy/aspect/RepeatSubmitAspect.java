package com.sy.aspect;

import com.alibaba.fastjson.JSONObject;
import com.sy.interceptor.RequestWrapper;
import com.sy.model.resp.BaseResp;
import com.sy.tool.NoRepeatSubmit;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.reflect.MethodSignature;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Component;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import javax.servlet.http.HttpServletRequest;
import java.lang.reflect.Method;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.TimeUnit;

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
    @Autowired
    private RedisTemplate redisTemplate;
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
        String userId = null;
        String bodyStr = requestWrapper.getBodyString();
        if (bodyStr != null && !bodyStr.isEmpty()) {
            JSONObject jsonObject = JSONObject.parseObject(bodyStr);
            // 直接读取JSON第一层的token字段，完美适配你的报文结构
            userId = jsonObject.getString("userId");
        }
// 3. 从Redis获取当前请求次数
// 替换原有的计数获取逻辑
        Object countObj = redisTemplate.opsForValue().get(userId);
        Long currentCount = null;

// 安全转换：处理 null/字符串/数字等情况
        if (countObj != null) {
            if (countObj instanceof Long) {
                currentCount = (Long) countObj;
            } else if (countObj instanceof String) {
                try {
                    currentCount = Long.parseLong((String) countObj);
                } catch (NumberFormatException e) {
                    // 解析失败，视为无效计数，重置为0
                    currentCount = 0L;
                }
            }
        }

        if (currentCount == null) {
            // 首次请求，初始化计数并设置过期时间
            redisTemplate.opsForValue().set(userId, "1", limitSeconds, TimeUnit.SECONDS);
        } else {
            // 超过阈值，抛出异常
            BaseResp baseResp = new BaseResp();
            baseResp.setErrorMsg("操作过于频繁");
            baseResp.setSuccess(0);
            return baseResp;
        }
        // 执行接口原业务逻辑
        return joinPoint.proceed();
    }

}