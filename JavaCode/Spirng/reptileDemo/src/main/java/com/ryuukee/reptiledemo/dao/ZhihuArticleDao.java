package com.ryuukee.reptiledemo.dao;

import com.ryuukee.reptiledemo.entity.ZhihuArticle;
import com.ryuukee.reptiledemo.mapper.ZhihuArticleMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

@Repository
public class ZhihuArticleDao {

    @Autowired
    private ZhihuArticleMapper zhihuArticleMapper;

    /**
     * 插入一篇文章到数据库
     * @param article 文章实体
     * @return 影响的行数
     */
    public int insertArticle(ZhihuArticle article) {
        return zhihuArticleMapper.insertArticle(article);
    }
}
