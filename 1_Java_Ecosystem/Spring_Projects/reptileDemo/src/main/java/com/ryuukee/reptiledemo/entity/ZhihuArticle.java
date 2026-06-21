package com.ryuukee.reptiledemo.entity;

import lombok.Data;
import java.util.Date;

@Data
public class ZhihuArticle {
    private Long id;
    private String title;
    private String content;
    private Date createTime;
}
