package com.ryuukee.reptiledemo;

import com.alibaba.fastjson2.JSON;
import com.alibaba.fastjson2.JSONArray;
import com.alibaba.fastjson2.JSONObject;

import java.util.List;




public class Test {
   static class Bean{
        String id;
        String title;
        String author;
        double price;
        boolean inStock;
    }

    @org.junit.jupiter.api.Test
    void t1(){

        String jsonString = "{\"name\":\"超级游戏本\", \"price\":8999.5}"; // 你爬到的数据
        JSONObject jsonObject = JSON.parseObject(jsonString);
        String productName = jsonObject.getString("name");   // 拿到 "超级游戏本"
        double productPrice = jsonObject.getDoubleValue("price"); // 拿到 8999.5
        System.out.println("名字: "+ productName+ " 价格： "+ productPrice);
    }

    @org.junit.jupiter.api.Test
    void t2(){
        String jsonString = "{\n" +
            "  \"status\": 200,\n" +
            "  \"message\": \"success\",\n" +
            "  \"data\": {\n" +
            "    \"total\": 2,\n" +
            "    \"books\": [\n" +
            "      {\n" +
            "        \"id\": \"b001\",\n" +
            "        \"title\": \"Java爬虫从入门到精通\",\n" +
            "        \"author\": \"爬虫老法师\",\n" +
            "        \"price\": 45.5,\n" +
            "        \"inStock\": true\n" +
            "      },\n" +
            "      {\n" +
            "        \"id\": \"b002\",\n" +
            "        \"title\": \"fastjson2高效解析指南\",\n" +
            "        \"author\": \"JSON大师\",\n" +
            "        \"price\": 68.0,\n" +
            "        \"inStock\": false\n" +
            "      }\n" +
            "    ]\n" +
            "  }\n" +
            "}";
        //将json字符串
        JSONObject j1 = JSON.parseObject(jsonString);

        JSONObject j2 = j1.getJSONObject("data");

        //获取里面一部分的元素
        JSONArray jsonArray = j2.getJSONArray("books");
        if(jsonArray == null){
            System.out.println("未获取到数组");
            return;
        }



        //遍历数组
        for(int i = 0;i < jsonArray.size();i++){

            //获取数组单个元素
            JSONObject item = jsonArray.getJSONObject(i);
            Bean b1 = item.toJavaObject(Bean.class);

            System.out.println(b1.title);

            //获取书的标题
            String title = item.getString("title");
            System.out.println(title);
        }

        List<Bean> list = jsonArray.toJavaList(Bean.class);

        for(Bean e:list){
            System.out.println(e.title);
        }

    }

}
