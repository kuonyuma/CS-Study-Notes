package com.ryuukee.concurrencylimitchallenge2.service;

import org.springframework.stereotype.Service;
import java.util.concurrent.ThreadLocalRandom;

/**
 * 模拟爬虫服务类
 * <p>
 * 提供模拟请求模式：使用 Thread.sleep 模拟 1.5 秒网络延迟
 * 用于 1000 关键词极限速度挑战
 */
@Service
public class CrawlerService {

    /**
     * 模拟单次爬虫抓取（无真实网络请求，无封禁风险）
     * @param keyword 关键词
     * @return 抓取结果条数
     */
    /**
     *阶段1：知道这个类在干嘛。
     * 阶段2：深度解析ThreadLocalRandom。
     */

    public int mockCrawl(String keyword) {
        try {
            // 模拟 1.3 秒 到 1.7 秒的网络抖动延迟，平均约 1.5 秒
            long delay = ThreadLocalRandom.current().nextLong(1300, 1700);//问题1
            Thread.sleep(delay);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
        return ThreadLocalRandom.current().nextInt(1, 20); // 随机返回抓取条数
    }
}
