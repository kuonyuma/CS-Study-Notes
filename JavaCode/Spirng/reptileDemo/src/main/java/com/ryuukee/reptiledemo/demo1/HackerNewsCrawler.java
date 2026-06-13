package com.ryuukee.reptiledemo.demo1;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import java.io.IOException;

public class HackerNewsCrawler {

    public static void main(String[] args) {
        // 目标网址：Hacker News 首页
        String url = "https://news.ycombinator.com/";

        try {
            System.out.println("正在连接并爬取: " + url + " ...");

            // 1. 发起 HTTP GET 请求，并获得网页的 DOM 文档对象
            Document doc = Jsoup.connect(url)
                // 伪装浏览器 User-Agent，这是爬虫的良好习惯
                .userAgent("Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/120.0.0.0 Safari/537.36")
                // 设置超时时间（毫秒）
                .timeout(5000)
                .get();

            // 2. 使用 CSS 选择器定位文章行
            // 在 Hacker News 中，文章行的 class 是 a-title，包含标题的包裹层是 span.titleline
            Elements titleSpans = doc.select("span.titleline");

            System.out.println("\n--- 爬取结果展示 ---");
            int count = 1;

            for (Element span : titleSpans) {
                // 3. 在当前的 span.titleline 下，寻找第一个 <a> 标签
                Element linkElement = span.selectFirst("a");
                if (linkElement != null) {
                    // 4. 提取标题文本和链接地址
                    String title = linkElement.text();
                    String link = linkElement.attr("href");

                    // 如果是相对路径，转换为绝对路径
                    if (link.startsWith("item?id=")) {
                        link = url + link;
                    }

                    System.out.printf("[%d] 标题: %s\n    链接: %s\n\n", count++, title, link);
                }
            }

        } catch (IOException e) {
            System.err.println("爬取失败，可能发生了网络超时或地址错误: " + e.getMessage());
            e.printStackTrace();
        }
    }
}