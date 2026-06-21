package com.ryuukee._20.entity;

import lombok.Data;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;

@Component
@Data
public class KeywordTask {
    private Integer id;
    private String query;
    private LocalDateTime creatTime;
}
