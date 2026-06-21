package com.ryuukee._20.config;

import org.springframework.beans.factory.BeanFactory;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import java.util.concurrent.*;
import java.util.concurrent.atomic.AtomicInteger;

@Configuration
public class ThreadPoolConfig {

    @Bean("crawlerExecutor")
    public Executor MyThread(){

        int corePoolSize = 100;//最大核心线程数量
        int maxPoolSize = 150;//最大线程数
        Long keepAliveSeconds = 60L;//临时线程存活时间
        int queueCapacity = 1000; //阻塞队列的大小

        return new ThreadPoolExecutor(
            corePoolSize,
            maxPoolSize,
            keepAliveSeconds,
            TimeUnit.SECONDS,//单位时间
            new LinkedBlockingDeque<>(queueCapacity),//阻塞队列
            new ThreadFactory() {//线程工厂
                private final AtomicInteger threadNumber = new AtomicInteger(1);
                @Override
                public Thread newThread(Runnable r) {
                    Thread t = new Thread(r,"线程代号"+ threadNumber);
                    t.setDaemon(true);//设置为后台线程
                    return t;
                }
            },
            new ThreadPoolExecutor.CallerRunsPolicy()//拒绝策略
        );

    }
}
