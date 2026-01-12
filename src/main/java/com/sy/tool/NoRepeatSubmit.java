package com.sy.tool;
import java.lang.annotation.*;

/**
 * 防重复请求注解，默认3秒内只能请求1次
 * 标记在需要限制的Controller方法上即可
 */
@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface NoRepeatSubmit {
    /**
     * 重复请求限制时间，单位：秒，默认3秒
     */
    long limitSeconds() default 3;
}