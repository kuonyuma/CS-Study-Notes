package com.ryuukee.reptiledemo.demo4;

import com.alibaba.fastjson2.JSON;
import com.alibaba.fastjson2.JSONArray;
import com.alibaba.fastjson2.JSONObject;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;

import java.io.IOException;

public class ZhiHuDemo {
    public static void main(String[] args){

        String apiUrl = "https://www.zhihu.com/api/v4/search_v3?gk_version=gz-gaokao&t=general&q=%E8%AE%A1%E7%AE%97%E6%9C%BA%E5%B0%B1%E4%B8%9A%E6%83%85%E5%86%B5&correction=1&offset=0&limit=20&filter_fields=&lc_idx=0&show_all_topics=0&search_source=Normal";
        String myCookie = "_zap=821443e3-d539-45e6-9baa-ed8f0bb2aec9; d_c0=P-CX1gZ3LhyPTuNIgWfDimjtHfzABkGdKuA=|1776934547; q_c1=e26edb0c53184c398828410b3125b52b|1776934620000|1776934620000; z_c0=2|1:0|10:1779711768|4:z_c0|92:Mi4xb0xDdGV3QUFBQUFfNEpmV0JuY3VIQ1lBQUFCZ0FsVk5sLUR5YWdESTlLMmhrM09KY3dDTjcydDUwZGc2ZFkxT1d3|213c446687025f20aa8bd8f1e4e56275ab55dc26583b59c3bd0c9a8bb6872f09; tst=r; __zse_ck=005_1wWNoElSMtwgZ4BYQuse=7c17esbdeeMGPUbcTILmJgBiRvegAMFCJ4J=0=TeVQXhrwTGT1nazRSWlK225DB8vutGorwRDpBk2fFQ=NfWREdUPVgKS9DQt95VMFAqZAG-s3sAKvl+jmVBqRQDQYLd7Z4sIKRVDMSPHncqRoKvFTK+2fQUsbSGyvRwgw0Y0OyDxEZDocpyTxyr2pTXdMGOO66OkYWmsbnDZhROc22cUgT3G2ZQ6jP4hIM67pPvC69f; Hm_lvt_98beee57fd2ef70ccdd5ca52b9740c49=1780466502,1780550746,1780660556,1781185937; _xsrf=7868df68-1e17-430f-8cd2-b5545d1610a2; SESSIONID=OnPt6nsQcnz2gbRuHPHKi59NiO5laQlfx97FBP7A3Dc; JOID=VV0UBE96mMdGO2_rBF1LFrs5GMQeN_KRMQ4DnFMa9LlyXw67dfEX7Cw6aekBQACRmb2ZPRqHmznC2nSCtEy8Jww=; osd=UVkQAUt-nMNDP2vvAFhPEr89HcAaM_aUNQoHmFYe8L12Wgq_cfUS6Cg-bewFRASVnLmdOR6Cnz3G3nGGsEi4Igg=; BEC=7e33fec1f95d805b0b89c2974da3470f";

        try{
            System.out.println("正在访问知乎...");
            // 步骤 1：去知乎搜索 API 拿文章列表（动态 JSON 抓包）
            String jsonStr = Jsoup.connect(apiUrl).
                header("Cookie", myCookie).
                ignoreContentType(true).
                execute().
                body();

            // 把拿到的 JSON 列表转成数组
            JSONArray articleList = JSON.
                parseObject(jsonStr).
                getJSONArray("data");

            // 步骤 2：遍历每一篇文章，进去抠字（静态 HTML 解析）
            for(int i = 0; i < articleList.size(); i++) {
                JSONObject item = articleList.getJSONObject(i);

                // 从 JSON 中拿到文章标题和具体网址
                String title = item.getString("title");
                String detailUrl = item.getString("url");

                System.out.println("准备爬取文章: " + title);

                // 进入具体网址，抠出正文
                Document doc = Jsoup.connect(detailUrl).header("Cookie", myCookie).get();
                String content = doc.select(".RichContent-inner").text();

                // 步骤 3：保存到本地 TXT 文件

                // ⚠️ 安全防风控：每爬一篇文章，让程序睡 2 秒钟，防止封号！
                Thread.sleep(5000);
            }


        }catch(IOException e){
            System.out.println(e);
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
    }

}
