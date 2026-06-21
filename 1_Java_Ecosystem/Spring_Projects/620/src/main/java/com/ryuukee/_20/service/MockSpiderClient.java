package com.ryuukee._20.service;

import com.ryuukee._20.entity.KeywordTask;
import org.springframework.stereotype.Service;

import java.util.concurrent.ThreadLocalRandom;

@Service
public class MockSpiderClient {
    //模拟爬虫返沪数据时的延迟
    public int fetchDelay(KeywordTask keywordTask){
        try {
            System.out.println("正在抓取数据...");
            Long Delay = ThreadLocalRandom.current().nextLong(1300,1800);
            Thread.sleep(Delay);
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
        return ThreadLocalRandom.current().nextInt(1,20);
    }
}
