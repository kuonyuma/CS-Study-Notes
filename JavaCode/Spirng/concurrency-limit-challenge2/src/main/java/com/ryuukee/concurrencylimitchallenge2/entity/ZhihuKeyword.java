package com.ryuukee.concurrencylimitchallenge2.entity;

import lombok.Data;
import java.time.LocalDateTime;

/**
 * 知乎关键词实体类，映射 zhihu_keyword 表
 */
@Data
public class ZhihuKeyword {
    private Long id;
    private String keyword;
    private LocalDateTime createTime;
}
