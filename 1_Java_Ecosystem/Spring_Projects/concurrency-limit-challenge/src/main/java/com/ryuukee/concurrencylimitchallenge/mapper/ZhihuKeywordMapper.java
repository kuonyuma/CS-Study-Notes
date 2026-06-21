package com.ryuukee.concurrencylimitchallenge.mapper;

import com.ryuukee.concurrencylimitchallenge.entity.ZhihuKeyword;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;

import java.util.List;

@Mapper
public interface ZhihuKeywordMapper {
    @Select("SELECT * FROM zhihu_keyword")
    List<ZhihuKeyword> selectAllKeywords();
}
