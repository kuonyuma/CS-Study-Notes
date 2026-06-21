package com.ryuukee._20;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.concurrent.atomic.AtomicInteger;

class ApplicationTests {

    private static final AtomicInteger atomicInteger = new AtomicInteger();

    @Test
    void t2() throws InterruptedException {
        Thread[] ts = new Thread[50];
        //学习AtomicInteger
        for (int i = 0; i < 50; i++) {
            ts[i] = new Thread(){
                @Override
                public void run(){
                    atomicInteger.incrementAndGet();
                }
            };
            ts[i].start();
        }

        for (int i = 0; i < 50; i++) {
            ts[i].join();
        }
        System.out.println(atomicInteger.get());
    }

    @Test
    void contextLoads() {

        System.out.println("时间是："+ System.currentTimeMillis());
    }

}
