package com.ryuukee.concurrencylimitchallenge2;

import com.ryuukee.concurrencylimitchallenge2.entity.ZhihuKeyword;
import com.ryuukee.concurrencylimitchallenge2.mapper.ZhihuKeywordMapper;
import com.ryuukee.concurrencylimitchallenge2.service.CrawlerService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.Executor;
import java.util.stream.Collectors;

/**
 * 极限速度挑战测试类
 * <p>
 * 对比传统同步串行与线程池异步并发的耗时差距
 */
@SpringBootTest
class ConcurrencyChallengeTest {
    //操作数据层的接口
    @Autowired
    private ZhihuKeywordMapper zhihuKeywordMapper;
    //服务层的接口
    @Autowired
    private CrawlerService crawlerService;

    @Autowired
    @Qualifier("crawlerExecutor")
    private Executor crawlerExecutor;
    
    /**
     * 测试一：传统同步串行方式（测试小样本，推算 1000 个词的总时间）
     */
    @Test
    void testSequentialCrawl() {
        List<ZhihuKeyword> allKeywords = zhihuKeywordMapper.selectAllKeywords();
        // 1000 个太多会跑 25 分钟，我们只取 10 个测试并乘 100 推算
        List<ZhihuKeyword> sampleKeywords = allKeywords.stream()
            .limit(10)
            .collect(Collectors.toList());

        System.out.println("\n--- [测试开始] 传统同步串行抓取 (样本数: " + sampleKeywords.size() + ") ---");
        long startTime = System.currentTimeMillis();

        for (ZhihuKeyword kw : sampleKeywords) {
            int count = crawlerService.mockCrawl(kw.getKeyword());
            System.out.println("   [串行] 关键词 【" + kw.getKeyword() + "】 抓取完成，数据量: " + count);
        }

        long endTime = System.currentTimeMillis();
        long duration = endTime - startTime;
        double avgTime = (double) duration / sampleKeywords.size();

        System.out.println("--- [测试结束] 串行测试完成 ---");
        System.out.printf("样本耗时: %.2f 秒%n", duration / 1000.0);
        System.out.printf("单任务平均耗时: %.2f 秒%n", avgTime / 1000.0);
        System.out.printf("👉 估算 1000 个关键词串行耗时: %.2f 秒 (约 %.1f 分钟)%n%n",
                (avgTime * 1000) / 1000.0, (avgTime * 1000) / 60000.0);
    }

    /**
     * 测试二：开启挑战！线程池 + CompletableFuture.allOf 并发跑完 1000 个任务
     */
    @Test
    void testConcurrentCrawlChallenge() {
        List<ZhihuKeyword> allKeywords = zhihuKeywordMapper.selectAllKeywords();
        int taskSize = allKeywords.size();
        System.out.println("\n🚀🚀🚀 --- [挑战开始] 1000 个关键词极限并发挑战 (任务总数: " + taskSize + ") --- 🚀🚀🚀");

        long startTime = System.currentTimeMillis();

        // 1. 将 1000 个任务包装成 CompletableFuture 异步任务并提交到 crawlerExecutor 线程池
        List<CompletableFuture<Void>> futures = allKeywords.stream()
                .map(kw -> CompletableFuture.runAsync(() -> {
                    String threadName = Thread.currentThread().getName();
                    int count = crawlerService.mockCrawl(kw.getKeyword());
                    // 打印关键进度日志（由于速度极快，这里简短打印）
                    System.out.println("   [" + threadName + "] 已处理: " + kw.getKeyword() + " | 数据量: " + count);
                }, crawlerExecutor))
                .collect(Collectors.toList());

        // 2. 使用 CompletableFuture.allOf 将所有任务聚合成一个
        CompletableFuture<Void> allOf = CompletableFuture.allOf(futures.toArray(new CompletableFuture[0]));

        // 3. 阻塞等待所有任务全部执行完毕
        allOf.join();

        long endTime = System.currentTimeMillis();
        long totalDuration = endTime - startTime;

        System.out.println("🎉🎉🎉 --- [挑战成功] 1000 个关键词全部抓取完毕！ --- 🎉🎉🎉");
        System.out.printf("🏁 极限挑战实际总耗时: %.2f 秒%n", totalDuration / 1000.0);
        System.out.printf("⚡ 吞吐量/QPS: %.2f 次请求/秒%n%n", (double) taskSize / (totalDuration / 1000.0));
    }
}
