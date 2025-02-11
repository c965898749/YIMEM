package com.sy.tool;

import com.sy.service.UpdateMessage;
import org.apache.commons.lang3.ArrayUtils;
import org.apache.commons.lang3.StringUtils;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.*;
import org.aspectj.lang.reflect.MethodSignature;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;


/**
 * 切面类：只要编写一个普通的pojo类即可
 */
//@Component("myAspect")
//标记当前类是一个切面类
@Component
@Aspect
public class MyAspect {

    @Autowired
    private UpdateMessage updateMessage;

    //    @Pointcut("bean(*ServiceImpl)")
    @Pointcut("@annotation(com.sy.tool.RedisCache)")
    public void method() {
        //该方法中不需要实现任何的功能，目的是为了能够在
        //方法上打上@Pointcut注解，从而声明切面对应的切入点
        //后面具体的通知方法中，通过method()  来指定这个方法，从而获悉切入点
    }

        /**
     * @param joinPoint
     */
    @Before("method()")
    public void afterAdvice(JoinPoint joinPoint) {
        MethodSignature methodSignature = (MethodSignature) joinPoint.getSignature();
        RedisCache systemLog = ((MethodSignature)joinPoint.getSignature()).getMethod().getAnnotation(RedisCache.class);
        String value= systemLog.value();
        System.out.println("注解值"+value);
        String[] parameterNames = methodSignature.getParameterNames();
                // 获取userId的下表
        int userId = ArrayUtils.indexOf(parameterNames, "userId");
        /**
         * 方法参数的值，返回的数组按照方法定义的顺序，对于null值的，在debug时，不会显示null的数组下表，例如：
         * public Object list(String accessToken, @RequestParam("appId") String groupAppId, String subGroupAppId) {}
         * 传参：accessToken=xxx&subGroupAppId=xxx
         * Object[] args = joinPoint.getArgs();获取的值，在debug时
         *      args[0] = xxx
         *      args[2] = xxx
         * 对于args[1]，虽然debug时没有显示这个变量，但实际上它是存在的，值为null
         */
        Object[] args = joinPoint.getArgs();
        String userIdNum= null;
        try {
            userIdNum = String.valueOf(args[userId]);
        } catch (Exception e) {
            e.printStackTrace();
        }
//        不为空清除缓存
        if (!StringUtils.equals(userIdNum, "null")) {
            Integer id=Integer.parseInt(userIdNum);
            String key=value+id;
//            if (RedisUtil.getJedisInstance().exists(key)){
//                RedisUtil.getJedisInstance().del(key);
//                RedisUtil.closeJedisInstance();
//            }
        }

    }

    /**
     * 此时的value相当于xml中的poincut-ref,指定了切入点
     */
//    @Before(value = "method()")
//    public void beforeAdvice(JoinPoint joinpoint) {
//        System.out.println("前置通知...");
//        System.out.println("方法名：" + joinpoint.getSignature().getName());
//        System.out.println("方法参数：" + Arrays.toString(joinpoint.getArgs()));
//    }

//    /**
//     * @param joinPoint
//     * @param returnVal 目标方法调用完以后的返回值
//     */
//    @AfterReturning(value = "method()", returning = "returnVal")
//    public void afterAdvice(JoinPoint joinPoint, Object returnVal) {
//        System.out.println("后置通知");
//        System.out.println("方法名称：" + joinPoint.getSignature().getName());
//        System.out.println("方法参数：" + Arrays.toString(joinPoint.getArgs()));
//        System.out.println("返回值：" + returnVal);
//        System.out.println("=====================================================");
//    }

//    @Around(value = "method()")
//    public Object aroundAdvice(ProceedingJoinPoint joinPoint) throws Throwable {
//        System.out.println("环绕通知的前置部分...");
//        //环绕通知中，目标方法需要通过ProceedingJoinPoint来调用
//        Object result = joinPoint.proceed(joinPoint.getArgs());
//        System.out.println("result:" + result);
//        System.out.println("环绕通知的后置部分...");
//        //环绕通知的返回值可以决定最终方法调用后的返回值
//        //return false;
//
//        return result;
//    }

//    @AfterThrowing(value = "method()", throwing = "e")
//    public void afterThrowingAdvice(JoinPoint joinPoint, Throwable e) {
//        System.out.println("发生了异常...");
//        System.out.println("错误信息：" + e.getMessage());
//
//    }

//    @After(value = "method()")
//    public void finallyAdvice(JoinPoint joinPoint) {
//        System.out.println("最终通知");
//    }
}
