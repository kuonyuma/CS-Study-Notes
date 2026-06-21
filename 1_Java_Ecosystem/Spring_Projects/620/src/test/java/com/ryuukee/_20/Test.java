package com.ryuukee._20;

import com.mysql.cj.log.Log;
import com.ryuukee._20.config.ThreadPoolConfig;
import com.ryuukee._20.entity.KeywordTask;
import com.ryuukee._20.repository.KeywordRepository;
import com.ryuukee._20.service.MockSpiderClient;
import lombok.extern.slf4j.Slf4j;
import org.assertj.core.error.ShouldBeAfterYear;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.Arrays;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.Executor;
import java.util.stream.Collectors;

@Slf4j
@SpringBootTest
public class Test {

    @Autowired
    @Qualifier("crawlerExecutor")
    Executor crawlerExecutor;

    @Autowired
    MockSpiderClient mockSpiderClient;

    @Autowired
    KeywordRepository keywordRepository;

    @org.junit.jupiter.api.Test
    void t1(){//单线程抓取
        List<KeywordTask> keywordTasksAll = keywordRepository.selectAll();

        List<KeywordTask> keywords = keywordTasksAll.stream()
            .limit(10).collect(Collectors.toList());

        long startTime = System.currentTimeMillis();
        keywords.stream().forEach(mockSpiderClient::fetchDelay);
        long endTime = System.currentTimeMillis();

        long time = endTime - startTime;

        double result = time / 10.0 * keywordTasksAll.size() / 6000.0;
        System.out.println("爬取时间为:" + result+ "分钟");

    }

    @org.junit.jupiter.api.Test
    void t2(){//多线程抓取
        List<KeywordTask> keywordAll = keywordRepository.selectAll();
        Long startTime = System.currentTimeMillis();

        List<CompletableFuture<Void>> futures = keywordAll.stream()
            .map(k->CompletableFuture.runAsync(()->{
                mockSpiderClient.fetchDelay(k);
                }, crawlerExecutor))
            .collect(Collectors.toList());

        Long endTime = System.currentTimeMillis();

        Long time = endTime - startTime;

        System.out.println("多线程爬取所花费时间:"+ time / 6000.0 + "分钟");
    }
}   
