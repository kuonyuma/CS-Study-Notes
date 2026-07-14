package com.ryuukee._6.demo5;

import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.Before;
import org.aspectj.lang.annotation.Pointcut;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;


@Order(3)
@Slf4j
@Component
@org.aspectj.lang.annotation.Aspect
public class AspectDemo1 {

    @Pointcut("execution(* com.ryuukee._6.demo5.Student.*(..))")
    public void pointcut() {}

    @Before("pointcut()")
    public void before(JoinPoint joinPoint) {
        String method = joinPoint.getSignature().getName();
        log.info("来自demo1-方法名称{}",method);
//        Object[] args = joinPoint.getArgs();
//        log.info("参数{}",args);
//
//        Object target = joinPoint.getTarget();
//        log.info("目标对象{}",target.getClass().getSimpleName());
//        Object aThis = joinPoint.getThis();
//        log.info("代理对象{}",aThis.getClass().getSimpleName());
    }


//    @AfterReturning(pointcut = "pointcut()", returning = "result")
//    public void afterReturning(JoinPoint joinPoint, Object result) {
//        String methodName = joinPoint.getSignature().getName();
//        // result 就是目标方法的返回值（如果方法返回 void，这里是 null）
//        log.info("━━━━━━━ @AfterReturning ━━━━━━━");
//        log.info("方法 [{}] 正常结束，返回值 = {}", methodName, result);
//    }

//    // ─────────────────────────────────────────────
//    // AfterThrowing：方法抛出异常后，能拿到异常对象
//    // ─────────────────────────────────────────────
//    @AfterThrowing(pointcut = "pointcut()", throwing = "ex")
//    public void afterThrowing(JoinPoint joinPoint, Exception ex) {
//        String methodName = joinPoint.getSignature().getName();
//        log.error("━━━━━━━ @AfterThrowing ━━━━━━━");
//        log.error("方法 [{}] 抛出异常，类型={}，信息={}", methodName,
//                ex.getClass().getSimpleName(), ex.getMessage());
//    }

//    @After("pointcut()")
//    public void after(JoinPoint joinPoint) {
//        String methodName = joinPoint.getSignature().getName();
//        log.info("━━━━━━━ @After ━━━━━━━");
//        log.info("方法 [{}] 执行结束（finally）", methodName);
//    }
}
