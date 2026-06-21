package com.ryuukee.concurrencylimitchallenge2.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import java.util.concurrent.*;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * 自定义线程池配置
 * <p>
 * 线程池大小估算公式（I/O 密集型）：线程数 = CPU核心数 × 20
 * 本例配置核心线程数 100，最大线程数 150，队列容量 1000
 */
@Configuration
public class ThreadPoolConfig {

    @Bean(name = "crawlerExecutor")
    public Executor crawlerExecutor() {
        int corePoolSize = 100; // 核心线程数
        int maxPoolSize = 150; // 最大线程数
        long keepAliveTime = 60L; // 线程空闲存活时间（秒）
        int queueCapacity = 1000; // 队列大小

        return new ThreadPoolExecutor(
                corePoolSize,
                maxPoolSize,
                keepAliveTime,
                TimeUnit.SECONDS,
                new LinkedBlockingQueue<>(queueCapacity),
                new ThreadFactory() {
                    private final AtomicInteger threadNumber = new AtomicInteger(1);

                    @Override
                    public Thread newThread(Runnable r) {
                        Thread t = new Thread(r, "crawler-worker-" + threadNumber.getAndIncrement());
                        t.setDaemon(true); // 设置为守护线程，主线程退出时自动退出
                        return t;
                    }
                },
                new ThreadPoolExecutor.CallerRunsPolicy() // 拒绝策略：如果饱和，由调用线程执行
        );
    }
}
