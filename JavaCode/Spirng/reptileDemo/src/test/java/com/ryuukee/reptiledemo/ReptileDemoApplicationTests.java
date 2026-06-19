package com.ryuukee.reptiledemo;

import com.alibaba.fastjson2.JSON;
import com.alibaba.fastjson2.JSONArray;
import com.alibaba.fastjson2.JSONObject;
import com.ryuukee.reptiledemo.dao.ZhihuArticleDao;
import com.ryuukee.reptiledemo.entity.ZhihuArticle;
import com.ryuukee.reptiledemo.mapper.ZhihuArticleMapper;
import org.jsoup.Jsoup;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.context.SpringBootTest;

import java.io.IOException;

@SpringBootTest
class ReptileDemoApplicationTests {

    @Autowired
    private ZhihuArticleMapper zhihuArticleMapper;

    @Value("${zhihu.search-url}")
    String searchUrl;

    @Value("${zhihu.cookie}")
    String myCookie;

    @Test
    void contextLoads() {

    }

    /**
     * 知乎爬虫实战测试
     * 第一步：通过搜索 API 拿到文章列表（动态 JSON）
     * 第二步：直接从 JSON 里提取正文（无需第二次请求）
     * 第三步：存入 MySQL 数据库（MyBatis）
     */
    @Test
    void crawlZhihuArticles() throws Exception {

        // ========== 第一步：请求搜索 API，拿到文章列表 ==========
        String jsonStr;
        try {
            jsonStr = Jsoup.connect(searchUrl)
                    .userAgent("Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/120.0.0.0 Safari/537.36")
                    .header("Cookie", myCookie)
                    .ignoreContentType(true)
                    .timeout(10000)
                    .execute()
                    .body();
        } catch (IOException e) {
            System.out.println("❌ 搜索 API 请求失败: " + e.getMessage());
            return;
        }

        // 解析 JSON，拿到搜索结果列表
        JSONObject rootObj = JSON.parseObject(jsonStr);
        JSONArray dataArray = rootObj.getJSONArray("data");

        if (dataArray == null || dataArray.isEmpty()) {
            System.out.println("❌ 没有拿到搜索结果，可能是 Cookie 过期或被风控了。");
            System.out.println("返回的原始 JSON：" + jsonStr.substring(0, Math.min(500, jsonStr.length())));
            return;
        }

        System.out.println("✅ 成功获取到 " + dataArray.size() + " 条搜索结果！");
        System.out.println();

        int savedCount = 0;

        // ========== 第二步：遍历每一条搜索结果 ==========
        for (int i = 0; i < dataArray.size(); i++) {

            JSONObject item = dataArray.getJSONObject(i);

            // 从 object 字段中获取文章数据
            JSONObject objectData = item.getJSONObject("object");
            if (objectData == null) {
                continue;
            }

            // 提取标题
            String title = objectData.getString("title");
            if (title == null || title.isEmpty()) {
                continue;
            }
            // 清理标题里的 HTML 高亮标签（如 <em>计算机</em>）
            title = title.replaceAll("<[^>]+>", "");

            System.out.println("📄 [" + (i + 1) + "] " + title);

            // ========== 第三步：直接从 JSON 里拿正文，用 Jsoup 清洗 HTML 标签 ==========
            String rawContent = objectData.getString("content");
            String content = "";
            if (rawContent != null && !rawContent.isEmpty()) {
                // Jsoup.parse().text() 能自动把 HTML 转成纯文字
                content = Jsoup.parse(rawContent).text();
            }

            // ========== 第四步：存入 MySQL 数据库 ==========
            if (!content.isEmpty()) {
                ZhihuArticle article = new ZhihuArticle();
                article.setTitle(title);
                article.setContent(content);
                zhihuArticleMapper.insertArticle(article);
                savedCount++;
                System.out.println("   ✅ 已保存！正文前50字：" + content.substring(0, Math.min(50, content.length())) + "...");
            } else {
                System.out.println("   ⚠️ 正文为空（此条目可能是问答类型，无摘要），跳过。");
            }
        }

        System.out.println();
        System.out.println("========================================");
        System.out.println("🎉 爬虫任务完成！共成功保存 " + savedCount + " 篇文章到数据库。");
        System.out.println("========================================");
    }
    @Value("${zhihu.hotsearch-url}")
    String hotSearchUrl;
    @Test
    void t3(){
        // 发请求并拿到body
        String jsonStr;
        try {
            jsonStr = Jsoup.connect(hotSearchUrl)
                .userAgent(
                    "Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/149.0.0.0 Safari/537.36")
                .ignoreContentType(true)
                .timeout(5000)
                .execute()
                .body();
        } catch (IOException e) {
            System.out.println("日志：api请求失败" + e.getMessage());
            return;
        }

        // 解析json
        JSONObject rootobj = JSON.parseObject(jsonStr);
        JSONArray rootArray = rootobj.getJSONArray("hot_search_queries");

        if (rootArray == null || rootArray.isEmpty()) {
            System.out.println("日志：未获取到数据");
        }

        for (int i = 0; i < rootArray.size(); i++) {

            JSONObject item = rootArray.getJSONObject(i);
            // 获取标题
            String title = item.getString("query");
            // 获取热度
            String hotShow = item.getString("hot_show");

            if (title != null && title.length() != 0 &&
                hotShow != null && hotShow.length() != 0) {
                System.out.println("标题：" + title + "热度:" + hotShow);
            }

        }

    }
}
