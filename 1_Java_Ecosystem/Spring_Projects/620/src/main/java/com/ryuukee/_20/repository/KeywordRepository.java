package com.ryuukee._20.repository;

import com.ryuukee._20.entity.KeywordTask;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;
import java.util.List;

@Mapper
public interface KeywordRepository {
    @Select("select * from zhihu_keyword")
    List<KeywordTask> selectAll();
}
