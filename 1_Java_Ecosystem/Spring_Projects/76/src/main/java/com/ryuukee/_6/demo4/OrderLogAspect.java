package com.ryuukee._6.demo4;

import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.*;
import org.springframework.stereotype.Component;

/**
 * 切面（Aspect）= 切入点（Pointcut）+ 五种通知（Advice）的集合体
 *
 * 这个切面的功能：自动给 OrderService 的所有方法添加日志记录，
 * 不需要修改 OrderService 的任何一行代码。
 */
@Slf4j
@Aspect    // 声明这是一个切面
@Component // 注册为 Spring Bean，否则切面不会生效
public class OrderLogAspect {

    /**
     * ① 定义切入点（Pointcut）—— 统一管理"在哪里插手"
     *
     * execution 表达式解读：
     *   *                          → 任意返回值
     *   com.ryuukee._6.demo4.*    → demo4 包下任意类
     *   .*                         → 任意方法名
     *   (..)                       → 任意参数
     *
     * 定义成一个方法后，下面的通知可以直接复用 orderMethods()，
     * 避免重复写表达式字符串。
     */
    @Pointcut("execution(* com.ryuukee._6.demo4.*.*(..))")
    public void orderMethods() {
        // 这个方法体不需要写任何代码，仅作为切入点的"锚点"使用
    }

    // =========================================================
    // ② @Before —— 在目标方法执行【之前】触发
    // 场景：记录"谁在什么时候调用了什么方法"
    // =========================================================
    @Before("orderMethods()")
    public void beforeAdvice(JoinPoint joinPoint) {
        String methodName = joinPoint.getSignature().getName();
        Object[] args = joinPoint.getArgs();
        log.info("━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━");
        log.info("【@Before】方法 [{}] 即将执行，参数：{}", methodName, args);
    }

    // =========================================================
    // ③ @AfterReturning —— 在目标方法【正常返回】后触发
    // 场景：拿到返回值，记录操作结果（注意：抛异常时不触发）
    // =========================================================
    @AfterReturning(pointcut = "orderMethods()", returning = "result")
    public void afterReturningAdvice(JoinPoint joinPoint, Object result) {
        String methodName = joinPoint.getSignature().getName();
        log.info("【@AfterReturning】方法 [{}] 正常结束，返回值：{}", methodName, result);
    }

    // =========================================================
    // ④ @AfterThrowing —— 在目标方法【抛出异常】后触发
    // 场景：捕获异常信息用于告警，注意：这里只是"观察"，不能阻止异常继续传播
    // =========================================================
    @AfterThrowing(pointcut = "orderMethods()", throwing = "ex")
    public void afterThrowingAdvice(JoinPoint joinPoint, Exception ex) {
        String methodName = joinPoint.getSignature().getName();
        log.error("【@AfterThrowing】方法 [{}] 抛出了异常！原因：{}", methodName, ex.getMessage());
    }

    // =========================================================
    // ⑤ @After —— 无论正常还是异常，方法结束后【都会触发】
    // 类比 Java 的 finally 块，常用于释放资源、清理上下文
    // =========================================================
    @After("orderMethods()")
    public void afterAdvice(JoinPoint joinPoint) {
        String methodName = joinPoint.getSignature().getName();
        log.info("【@After】方法 [{}] 执行完毕（不管成功还是失败，这里都会执行）", methodName);
        log.info("━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━");
    }

    // =========================================================
    // ⑥ @Around —— 环绕通知，前后全包，最强大也最需小心
    // 必须：① 接收 ProceedingJoinPoint 参数
    //        ② 手动调用 pjp.proceed() 来触发目标方法
    //        ③ 返回目标方法的返回值（或自定义返回值）
    //
    // 注意：@Around 和其他通知同时存在时，@Around 是最外层的"包裹"
    //        执行顺序：@Around前半段 → @Before → 目标方法
    //                   → @AfterReturning/@AfterThrowing → @After → @Around后半段
    // =========================================================
    @Around("orderMethods()")
    public Object aroundAdvice(ProceedingJoinPoint pjp) throws Throwable {
        String methodName = pjp.getSignature().getName();
        long startTime = System.currentTimeMillis();
        log.info("【@Around - 前】开始计时，方法：[{}]", methodName);

        // 必须调用 proceed()，否则目标方法永远不会执行！
        Object result = pjp.proceed();

        long cost = System.currentTimeMillis() - startTime;
        log.info("【@Around - 后】方法 [{}] 耗时：{}ms", methodName, cost);
        return result;
    }
}
