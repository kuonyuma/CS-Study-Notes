package com.ryuukee.reptiledemo.mapper;

import com.ryuukee.reptiledemo.entity.ZhihuArticle;
import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface ZhihuArticleMapper {

    //存储爬虫数据
    int insertArticle(ZhihuArticle article);
}
