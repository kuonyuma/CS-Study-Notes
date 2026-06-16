package com.ryuukee.reptiledemo.demo2;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import java.io.IOException;

public class MyPretile {

    public static void main(String[] args) {

        // 获取网址
        String url = "https://movie.douban.com/top250";
        String useragent = "Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/120.0.0.0 Safari/537.36";

        try {
            System.out.println("日志：正在连接豆瓣评分网站....");

            // 发起get请求
            Document doc = Jsoup.connect(url)
                    // 伪装成浏览器
                    .userAgent(useragent)
                    // 最大链接时间
                    .timeout(5000)
                    .get();

            // 使用css选择器获取内容
            Elements titleSpans = doc.select(".item");

            System.out.println("遍历结果对象");
            for (Element movie : titleSpans) {
                Element titleElement = movie.selectFirst("span.title");
                String movieName = titleElement.text();// 拿到名字

                Element rateElement = movie.selectFirst(".rating_num");
                String ratting = rateElement.text();

                System.out.println("电影名称:" + movieName + " 评分：" + ratting);
            }

        } catch (IOException e) {
            System.out.println("爬取失败" + e);
            // 打印异常堆栈信息，帮助调试
            e.printStackTrace();
        }
    }
}
