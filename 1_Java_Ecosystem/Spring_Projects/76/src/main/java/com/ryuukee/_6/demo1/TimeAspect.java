package com.ryuukee._6.demo1;


import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@Aspect
public class TimeAspect {

    @Around("execution(* com.ryuukee._6.*.*(..))")
    public Object recordTime(ProceedingJoinPoint pjp){
        log.info("开始前");
        Long begin = System.currentTimeMillis();
        Object result = null;
        try {
            result = pjp.proceed();
        } catch (Throwable e) {
            throw new RuntimeException(e);
        }
        Long end = System.currentTimeMillis();
        log.info(pjp.getSignature() + "执⾏耗时: {}ms", end - begin);
        log.info("开始后");
        return result;
    }
}
