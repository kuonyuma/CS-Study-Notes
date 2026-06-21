package com.ryuukee.concurrencylimitchallenge;

import com.ryuukee.concurrencylimitchallenge.entity.ZhihuKeyword;
import com.ryuukee.concurrencylimitchallenge.mapper.ZhihuKeywordMapper;
import com.ryuukee.concurrencylimitchallenge.service.CrawlerService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.Executor;
import java.util.stream.Collectors;

@SpringBootTest
class ConcurrencyChallengeTest {

    @Autowired
    private ZhihuKeywordMapper zhihuKeywordMapper;

    @Autowired
    private CrawlerService crawlerService;

    @Autowired
    @Qualifier("crawlerExecutor")
    private Executor crawlerExecutor;

    @Test
    void testSequentialCrawl() {
        List<ZhihuKeyword> allKeywords = zhihuKeywordMapper.selectAllKeywords();
        List<ZhihuKeyword> sampleKeywords = allKeywords.stream().limit(10).collect(Collectors.toList());

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
        System.out.printf("样本耗时: %.2f 秒\n", duration / 1000.0);
        System.out.printf("单任务平均耗时: %.2f 秒\n", avgTime / 1000.0);
        System.out.printf("估算 1000 个关键词串行耗时: %.2f 秒 (约 %.1f 分钟)\n\n",
                avgTime * 1000 / 1000.0, avgTime * 1000 / 60000.0);
    }

    @Test
    void testConcurrentCrawlChallenge() {
        List<ZhihuKeyword> allKeywords = zhihuKeywordMapper.selectAllKeywords();
        int taskSize = allKeywords.size();
        System.out.println("\n🚀🚀🚀 --- [挑战开始] 1000 个关键词极限并发挑战 (任务总数: " + taskSize + ") --- 🚀🚀🚀");

        long startTime = System.currentTimeMillis();

        List<CompletableFuture<Void>> futures = allKeywords.stream()
                .map(kw -> CompletableFuture.runAsync(() -> {
                    String threadName = Thread.currentThread().getName();
                    int count = crawlerService.mockCrawl(kw.getKeyword());
                    System.out.println("   [" + threadName + "] 已处理: " + kw.getKeyword() + " | 数据量: " + count);
                }, crawlerExecutor))
                .collect(Collectors.toList());

        CompletableFuture<Void> allOf = CompletableFuture.allOf(futures.toArray(new CompletableFuture[0]));

        allOf.join();

        long endTime = System.currentTimeMillis();
        long totalDuration = endTime - startTime;

        System.out.println("🎉🎉🎉 --- [挑战成功] 1000 个关键词全部抓取完毕！ --- 🎉🎉🎉");
        System.out.printf("🏁 极限挑战实际总耗时: %.2f 秒\n", totalDuration / 1000.0);
        System.out.printf("⚡ 吞吐量/QPS: %.2f 次请求/秒\n\n", (double) taskSize / (totalDuration / 1000.0));
    }
}
