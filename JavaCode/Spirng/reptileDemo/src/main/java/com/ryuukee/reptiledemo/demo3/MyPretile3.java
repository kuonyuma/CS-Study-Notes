package com.ryuukee.reptiledemo.demo3;

import com.alibaba.fastjson2.JSON;
import com.alibaba.fastjson2.JSONArray;
import com.alibaba.fastjson2.JSONObject;
import org.jsoup.Jsoup;

import java.io.IOException;

public class MyPretile3 {

    public static void main(String[] args){

        //标注api
        String aipUrl = "https://pokeapi.co/api/v2/pokemon/pikachu";

        try{
            System.out.println("正在尝试访问网站...");

            //发起请求
            String jsonStr = Jsoup.connect(aipUrl)
                .ignoreContentType(true)
                .execute()
                .body();

            //将字符串转化为对象

            JSONObject rootObj = JSON.parseObject(jsonStr);

            JSONArray productsArray = rootObj.getJSONArray("abilities");

            for(int i = 0; i < productsArray.size();i++){
                JSONObject Item = productsArray.getJSONObject(i);
                // 3. 拆开包裹，取出里面标着 ability 的小盒子
                JSONObject abilityObj = Item.getJSONObject("ability");
                // 4. 从小盒子拿到 name 属性
                String skillName = abilityObj.getString("name");

                System.out.println("拿到的技能是: " + skillName);
            }

        }catch(IOException e){
            System.out.println("网络请求失败 " + e.getMessage());
        }
    }
}

