package com.ryuukee._6.demo5;


import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Before;
import org.aspectj.lang.annotation.Pointcut;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;


@Order(1)
@Component
@Slf4j
@Aspect
public class AspectDemo2 {

    @Pointcut("execution(* com.ryuukee._6.demo5.Student.*(..))")
    private void pointcut(){}


    @Before("pointcut()")
    public void before(JoinPoint joinPoint){
        String method = joinPoint.getSignature().getName();
        log.info("来自demo2{}",method);
    }
}
