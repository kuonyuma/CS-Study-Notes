package com.ryuukee.concurrencylimitchallenge.entity;

import lombok.Data;

import java.time.LocalDateTime;

@Data
public class ZhihuKeyword {
    private Long id;
    private String keyword;
    private LocalDateTime createTime;
}
