package com.ryuukee.reptiledemo.demo3;
import com.alibaba.fastjson2.JSON;
import com.alibaba.fastjson2.JSONArray;
import com.alibaba.fastjson2.JSONObject;
import org.jsoup.Jsoup;

import java.io.IOException;

public class DummyJsonCrawler{
    public static void main(String[] args) {
        // 专门用于练习的无反爬 API
        String apiUrl = "https://dummyjson.com/products";

        try {
            System.out.println("正在请求商品 API...");

            // 1. 发起请求获取 JSON 字符串
            String jsonStr = Jsoup.connect(apiUrl)
                // 同样必须要这行，告诉 Jsoup 我们要接收纯文本 JSON
                .ignoreContentType(true)
                .execute()
                .body();

            // 💡 调试小技巧：你可以把下面这行取消注释，看看拿到的原始 JSON 长什么样
            // System.out.println("服务器返回的 JSON：" + jsonStr);

            // 2. 将 JSON 字符串反序列化为最外层的大括号对象
            JSONObject rootObj = JSON.parseObject(jsonStr);

            // 3. 开始解析！
            // 观察 JSON 结构，商品都在一个叫 "products" 的中括号 [] 列表里
            // 注意：因为是列表，所以这里用 getJSONArray，而不是 getJSONObject
            JSONArray productsArray = rootObj.getJSONArray("products");

            System.out.println("\n--- 成功获取商品列表，共 " + productsArray.size() + " 件商品 ---");

            // 4. 遍历提取每一个商品的信息
            for (int i = 0; i < productsArray.size(); i++) {
                // 拿到列表里的第 i 个商品对象（也就是单个小括号 {} 里的内容）
                JSONObject product = productsArray.getJSONObject(i);

                // 提取具体字段
                String title = product.getString("title");   // 商品名称
                double price = product.getDoubleValue("price"); // 价格
                double rating = product.getDoubleValue("rating"); // 评分
                int stock = product.getIntValue("stock");     // 库存

                // 打印出来
                System.out.printf("【%d】商品: %-30s | 价格: $%-6.2f | 评分: %.2f | 库存: %d件\n",
                    (i + 1), title, price, rating, stock);
            }

        } catch (IOException e) {
            System.err.println("网络请求失败：" + e.getMessage());
        }
    }
}