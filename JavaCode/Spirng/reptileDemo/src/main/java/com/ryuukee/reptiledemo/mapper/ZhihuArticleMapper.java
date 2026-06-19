package com.ryuukee.reptiledemo.mapper;

import com.ryuukee.reptiledemo.entity.ZhihuArticle;
import com.ryuukee.reptiledemo.entity.ZhihuHot;
import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface ZhihuArticleMapper {

    //存储爬虫数据
    int insertArticle(ZhihuArticle article);

    //存储来自热榜的数据
    int insertHotSearch(ZhihuHot bean);
}
