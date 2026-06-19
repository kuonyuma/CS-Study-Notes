package com.ryuukee.reptiledemo.service;

import com.alibaba.fastjson2.JSON;
import com.alibaba.fastjson2.JSONArray;
import com.alibaba.fastjson2.JSONObject;
import com.ryuukee.reptiledemo.entity.ZhihuArticle;
import com.ryuukee.reptiledemo.dao.ZhihuArticleDao;
import org.jsoup.Jsoup;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.concurrent.ThreadLocalRandom;

//@Service
public class ZhihuCrawlerService {

    @Autowired
    private ZhihuArticleDao zhihuArticleDao;

    @Value("${zhihu.cookie}")
    private String myCookie;

    @Value("${zhihu.base-search-url}")
    private String baseSearchUrl;

    @Value("#{'${zhihu.keywords}'.split(',')}")
    private List<String> keywords;

    /**
     * 定时爬取任务
     * fixedDelayString 从 yml 读取间隔时间
     * 上一次执行完毕后，等待指定时间再执行下一次
     */
    @Scheduled(fixedDelayString = "${zhihu.crawl-delay}")
    public void scheduledCrawl() {
        String now = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"));
        System.out.println();
        System.out.println("========================================");
        System.out.println("⏰ [" + now + "] 定时任务触发！");
        System.out.println("📋 共有 " + keywords.size() + " 个关键词待抓取");
        System.out.println("========================================");

        int totalSaved = 0;

        // 遍历每一个关键词
        for (String keyword : keywords) {
            System.out.println("\n🔍 正在抓取关键词：【" + keyword + "】");

            try {
                int count = crawlByKeyword(keyword);
                totalSaved += count;
                System.out.println("   📊 本关键词保存了 " + count + " 篇文章");
            } catch (Exception e) {
                System.out.println("   ❌ 抓取关键词【" + keyword + "】时出错: " + e.getMessage());
            }

            // 每个关键词搜完后，随机休息 3 到 8 秒，模拟人类浏览，防止频率过高被封
            try {
                long randomSleepTime = ThreadLocalRandom.current().nextLong(3000, 8000);
                Thread.sleep(randomSleepTime);
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
                return;
            }
        }

        System.out.println();
        System.out.println("========================================");
        System.out.println("🎉 本轮抓取完成！共保存 " + totalSaved + " 篇文章到数据库");
        System.out.println("⏳ 等待下一轮定时触发...");
        System.out.println("========================================");
    }

    /**
     * 根据关键词抓取知乎文章并存入数据库
     * 
     * @param keyword 搜索关键词
     * @return 成功保存的文章数量
     */
    private int crawlByKeyword(String keyword) throws IOException {
        // 把中文关键词编码成 URL 参数
        String encodedKeyword = URLEncoder.encode(keyword, StandardCharsets.UTF_8);
        String searchUrl = baseSearchUrl + encodedKeyword;

        // 请求搜索 API
        String jsonStr = Jsoup.connect(searchUrl)
                .userAgent(
                        "Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/120.0.0.0 Safari/537.36")
                .header("Cookie", myCookie)
                .ignoreContentType(true)
                .timeout(10000)
                .execute()
                .body();

        // 解析 JSON
        JSONObject rootObj = JSON.parseObject(jsonStr);
        JSONArray dataArray = rootObj.getJSONArray("data");

        if (dataArray == null || dataArray.isEmpty()) {
            System.out.println("   ⚠️ 没有搜索到结果");
            return 0;
        }

        System.out.println("   ✅ 获取到 " + dataArray.size() + " 条搜索结果");

        int savedCount = 0;

        for (int i = 0; i < dataArray.size(); i++) {
            JSONObject item = dataArray.getJSONObject(i);
            JSONObject objectData = item.getJSONObject("object");

            if (objectData == null) {
                continue;
            }

            // 提取标题
            String title = objectData.getString("title");
            if (title == null || title.isEmpty()) {
                continue;
            }
            // 清理 HTML 高亮标签
            title = title.replaceAll("<[^>]+>", "");

            // 直接从 JSON 里拿正文，用 Jsoup 清洗 HTML 标签
            String rawContent = objectData.getString("content");
            String content = "";
            if (rawContent != null && !rawContent.isEmpty()) {
                content = Jsoup.parse(rawContent).text();
            }

            // 存入数据库
            if (!content.isEmpty()) {
                ZhihuArticle article = new ZhihuArticle();
                article.setTitle(title);
                article.setContent(content);
                zhihuArticleDao.insertArticle(article);
                savedCount++;
                System.out.println("   📄 [" + (i + 1) + "] " + title);
            }
        }

        return savedCount;
    }
}
