# Spring Boot 极限速度挑战项目设计方案与实现指南
> **目标**：在一个全新的 Spring Boot 项目中，利用**自定义线程池**与 **`CompletableFuture.allOf`**，在 30 秒内并发执行 1000 个模拟网络请求任务（每个任务模拟延时 1.5 秒），对比传统串行 `for` 循环的巨大耗时差距。

---

## 一、 项目技术栈选型

* **基础框架**：Spring Boot 3.x / 2.x
* **数据库操作**：Spring Data JPA 或 MyBatis (本项目以 MyBatis + MySQL 为例)
* **网络请求与解析**：Jsoup (用于真实网页抓取)
* **JSON 解析**：Fastjson2
* **工具库**：Lombok
* **测试框架**：Spring Boot Test + JUnit 5

### Maven 依赖配置 (`pom.xml`)
在新项目的 `pom.xml` 中，除了 Spring Boot Web Starter 外，需要引入以下依赖：
```xml
<dependencies>
    <!-- Spring Boot Web Starter -->
    <dependency>
        <groupId>org.springframework.boot</groupId>
        <artifactId>spring-boot-starter-web</artifactId>
    </dependency>

    <!-- MyBatis Starter -->
    <dependency>
        <groupId>org.mybatis.spring.boot</groupId>
        <artifactId>mybatis-spring-boot-starter</artifactId>
        <version>3.0.3</version>
    </dependency>

    <!-- MySQL Driver -->
    <dependency>
        <groupId>com.mysql</groupId>
        <artifactId>mysql-connector-j</artifactId>
        <scope>runtime</scope>
    </dependency>

    <!-- Lombok -->
    <dependency>
        <groupId>org.projectlombok</groupId>
        <artifactId>lombok</artifactId>
        <optional>true</optional>
    </dependency>

    <!-- Jsoup (网络爬虫利器) -->
    <dependency>
        <groupId>org.jsoup</groupId>
        <artifactId>jsoup</artifactId>
        <version>1.17.2</version>
    </dependency>

    <!-- Fastjson2 -->
    <dependency>
        <groupId>com.alibaba.fastjson2</groupId>
        <artifactId>fastjson2</artifactId>
        <version>2.0.47</version>
    </dependency>

    <!-- Spring Boot Test -->
    <dependency>
        <groupId>org.springframework.boot</groupId>
        <artifactId>spring-boot-starter-test</artifactId>
        <scope>test</scope>
    </dependency>
</dependencies>
```

---

## 二、 数据库设计与数据初始化

### 1. 创建关键词表 `zhihu_keyword`
在 MySQL 数据库中执行以下 DDL 创建表：
```sql
CREATE TABLE `zhihu_keyword` (
    `id` BIGINT AUTO_INCREMENT PRIMARY KEY,
    `keyword` VARCHAR(255) NOT NULL UNIQUE COMMENT '抓取关键词',
    `create_time` TIMESTAMP DEFAULT CURRENT_TIMESTAMP
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;
```

### 2. 插入 1000 个模拟测试关键词
使用 MySQL 存储过程或临时查询生成 1000 条测试数据：
```sql
-- 定义存储过程快速插入 1000 条数据
DELIMITER $$
CREATE PROCEDURE InsertKeywords()
BEGIN
    DECLARE i INT DEFAULT 1;
    WHILE i <= 1000 DO
        INSERT IGNORE INTO zhihu_keyword (keyword) VALUES (CONCAT('模拟关键词_', i));
        SET i = i + 1;
    END WHILE;
END$$
DELIMITER ;

-- 调用存储过程
CALL InsertKeywords();

-- 删除存储过程（清理）
DROP PROCEDURE InsertKeywords;
```

---

## 三、 持久层实现 (MyBatis)

### 1. 实体类 `ZhihuKeyword.java`
```java
package com.example.demo.entity;

import lombok.Data;
import java.time.LocalDateTime;

@Data
public class ZhihuKeyword {
    private Long id;
    private String keyword;
    private LocalDateTime createTime;
}
```

### 2. Mapper 接口 `ZhihuKeywordMapper.java`
```java
package com.example.demo.mapper;

import com.example.demo.entity.ZhihuKeyword;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;
import java.util.List;

@Mapper
public interface ZhihuKeywordMapper {
    @Select("SELECT * FROM zhihu_keyword")
    List<ZhihuKeyword> selectAllKeywords();
}
```

---

## 四、 核心并发层设计

### 1. 自定义线程池配置 (`ThreadPoolConfig.java`)
> [!TIP]
> **线程池大小估算公式**：对于 I/O 密集型任务（如爬虫网络请求），线程大部分时间处于等待状态。线程数可以设得相对较高。
> $$\text{线程池核心数} = \text{CPU核心数} \times 20$$
> 本例中，我们配置核心线程数为 **100**，最大线程数为 **150**，队列容量为 **1000**。

```java
package com.example.demo.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import java.util.concurrent.*;
import java.util.concurrent.atomic.AtomicInteger;

@Configuration
public class ThreadPoolConfig {

    @Bean(name = "crawlerExecutor")
    public Executor crawlerExecutor() {
        int corePoolSize = 100; // 核心线程数
        int maxPoolSize = 150;  // 最大线程数
        long keepAliveTime = 60L; // 线程空闲存活时间
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
```

### 2. 模拟爬虫服务类 (`CrawlerService.java`)
本类提供两种模式：
1. **真实请求模式**：使用 Jsoup 请求知乎搜索 API（为防止封禁，只用于测试极少关键词）。
2. **模拟请求模式**：使用 `Thread.sleep(1500)` 模拟 1.5 秒网络延迟（用于 1000 关键词极限速度挑战）。

```java
package com.example.demo.service;

import org.springframework.stereotype.Service;
import java.util.concurrent.ThreadLocalRandom;

@Service
public class CrawlerService {

    /**
     * 模拟单次爬虫抓取（无真实网络请求，无封禁风险）
     * @param keyword 关键词
     * @return 抓取结果条数
     */
    public int mockCrawl(String keyword) {
        try {
            // 模拟 1.3 秒 到 1.7 秒的网络抖动延迟，平均约 1.5 秒
            long delay = ThreadLocalRandom.current().nextLong(1300, 1700);
            Thread.sleep(delay);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
        return ThreadLocalRandom.current().nextInt(1, 20); // 随机返回抓取条数
    }
}
```

---

## 五、 测试对比与极限挑战验证

编写 Spring Boot 测试类，对比**同步串行**与**线程池异步并发**的耗时差距。

### 单元测试类 `ConcurrencyChallengeTest.java`
```java
package com.example.demo;

import com.example.demo.entity.ZhihuKeyword;
import com.example.demo.mapper.ZhihuKeywordMapper;
import com.example.demo.service.CrawlerService;
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

    /**
     * 测试一：传统同步串行方式（测试小样本，推算 1000 个词的总时间）
     */
    @Test
    void testSequentialCrawl() {
        List<ZhihuKeyword> allKeywords = zhihuKeywordMapper.selectAllKeywords();
        // 1000 个太多会跑 25 分钟，我们只取 10 个测试并乘 100 推算
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
        System.out.printf("👉 估算 1000 个关键词串行耗时: %.2f 秒 (约 %.1f 分钟)\n\n", 
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
        System.out.printf("🏁 极限挑战实际总耗时: %.2f 秒\n", totalDuration / 1000.0);
        System.out.printf("⚡ 吞吐量/QPS: %.2f 次请求/秒\n\n", (double) taskSize / (totalDuration / 1000.0));
    }
}
```

---

## 六、 进阶思考与扩展（亮点）

通过本方案，新项目的开发 Agent 能够直接照猫画虎搭好环境，并产出明显的测试数据对比。你可以让 Agent 进一步思考并实现以下方向：
1. **数据合并批量插入**：如果并发爬虫实时把数据一条一条插进数据库，MySQL 的连接池会成为新的瓶颈。如何使用**内存缓冲队列 (BlockingQueue)** + **定时批量入库**来优化？
2. **熔断与降级**：如果由于并发太高网络请求大面积超时或报错，如何利用 `CompletableFuture.exceptionally()` 捕获异常，保证主流程不会崩溃？
3. **动态线程池监控**：如何实时监控 `ThreadPoolExecutor` 的活动线程数、队列排队任务数，以动态调整线程池参数？
