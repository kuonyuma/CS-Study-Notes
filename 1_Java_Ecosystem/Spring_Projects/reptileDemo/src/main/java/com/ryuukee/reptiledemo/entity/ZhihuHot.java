package com.ryuukee.reptiledemo.entity;

import lombok.Data;

import java.time.LocalDateTime;

@Data
public class ZhihuHot {
    public String query;
    public String hot_show;
    private LocalDateTime creatTime;
}
