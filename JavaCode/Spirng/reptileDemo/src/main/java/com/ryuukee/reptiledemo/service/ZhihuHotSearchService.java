package com.ryuukee.reptiledemo.service;

import com.alibaba.fastjson2.JSON;
import com.alibaba.fastjson2.JSONArray;
import com.alibaba.fastjson2.JSONObject;
import com.ryuukee.reptiledemo.dao.ZhihuArticleDao;
import com.ryuukee.reptiledemo.entity.ZhihuHot;
import org.jsoup.Jsoup;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.io.IOException;

@Service
public class ZhihuHotSearchService {

    // 数据存储
    @Autowired
    private ZhihuArticleDao zhihuArticleDao;

    // 导入api
    @Value("${zhihu.hotsearch-url}")
    private String hotSearchUrl;

    // 自动抓取热榜
    @Scheduled(initialDelay = 1000, fixedDelayString = "${zhihu.crawl-delay}")
    public void scheduledCrawl() {
        int count = 0;
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

            //将数据存入到数据库中
            ZhihuHot bean = new ZhihuHot();
            bean.setQuery(title);
            bean.setHot_show(hotShow);
            zhihuArticleDao.insertHotSearch(bean);
            count++;
        }
        System.out.println("已经存储了"+count+"条数据");
    }

}
