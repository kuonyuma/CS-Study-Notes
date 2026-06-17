package com.ryuukee.reptiledemo.service;

import com.alibaba.fastjson2.JSON;
import com.alibaba.fastjson2.JSONArray;
import com.alibaba.fastjson2.JSONObject;
import com.ryuukee.reptiledemo.dao.ZhihuArticleDao;
import org.jsoup.Jsoup;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.io.IOException;

@Service
public class ZhihuHotSearchService {

    //数据存储
    @Autowired
    private ZhihuArticleDao zhihuArticleDao;

    //导入api
    @Value("${zhihu.hotsearch-url}")
    private String hotSearchUrl;

    //自动抓取热榜
    public void scheduledCrawl(){

        //发请求并拿到body
        String jsonStr;
        try {
            jsonStr = Jsoup.connect(hotSearchUrl)
                .userAgent("Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/149.0.0.0 Safari/537.36")
                .ignoreContentType(true)
                .timeout(5000)
                .execute()
                .body();
        } catch (IOException e) {
            System.out.println("日志：api请求失败" + e.getMessage());
            return;
        }

        //解析json
        JSONObject rootobj = JSON.parseObject(jsonStr);
        JSONArray rootArray = rootobj.getJSONArray("hot_search_queries");

        if(rootArray == null || rootArray.isEmpty()){
            System.out.println("日志：未获取到数据");
        }

        for(int i = 0 ; i< rootArray.size();i++){

            JSONObject item = rootArray.getJSONObject(i);


        }

    }



}
