package com.ryuukee.concurrencylimitchallenge2.mapper;

import com.ryuukee.concurrencylimitchallenge2.entity.ZhihuKeyword;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;
import java.util.List;

/**
 * 知乎关键词 Mapper 接口
 */
@Mapper
public interface ZhihuKeywordMapper {

    @Select("SELECT * FROM zhihu_keyword")
    List<ZhihuKeyword> selectAllKeywords();
}
