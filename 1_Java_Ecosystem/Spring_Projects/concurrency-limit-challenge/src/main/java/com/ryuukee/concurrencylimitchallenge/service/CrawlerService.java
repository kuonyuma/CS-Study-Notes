package com.ryuukee.concurrencylimitchallenge.service;

import org.springframework.stereotype.Service;

import java.util.concurrent.ThreadLocalRandom;

@Service
public class CrawlerService {

    public int mockCrawl(String keyword) {
        try {
            long delay = ThreadLocalRandom.current().nextLong(1300, 1700);
            Thread.sleep(delay);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
        return ThreadLocalRandom.current().nextInt(1, 20);
    }
}
