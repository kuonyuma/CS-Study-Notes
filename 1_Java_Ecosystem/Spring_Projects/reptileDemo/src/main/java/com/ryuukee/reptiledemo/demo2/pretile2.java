package com.ryuukee.reptiledemo.demo2;

import java.io.IOException;
import java.util.concurrent.TimeUnit;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

// 这是预习课的第二个爬虫示例，爬取咨询
public class pretile2 {

    public static void main(String[] args) {

        String url = "https://36kr.com/information/technology/";
        String useragent = "Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/120.0.0.0 Safari/537.36";

        try {
            System.out.println("正在爬取科技网...");

            // 获取请求
            Document doc = Jsoup.connect(url)
                    .userAgent(useragent)
                    .timeout(50000)
                    .get();

            Elements beans = doc.select(".information-flow-item");

            // 遍历得到的结果
            for (Element e : beans) {
                Element title = e.selectFirst(".title-wrapper.ellipsis-2");
                if (title != null) {
                    String tmp = title.text();
                    TimeUnit.SECONDS.sleep(1);
                    System.out.println("咨询标题：" + tmp);
                }
            }

        } catch (IOException e) {
            System.out.println("爬取失败...+ " + e);
            e.printStackTrace();
        } catch (InterruptedException e) {
            System.out.println("" + e);
        }
    }
}
