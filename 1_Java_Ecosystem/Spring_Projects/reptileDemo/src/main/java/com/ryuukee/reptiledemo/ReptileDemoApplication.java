package com.ryuukee.reptiledemo;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication
@EnableScheduling  // 开启定时任务功能
public class ReptileDemoApplication {
    public static void main(String[] args) {
        SpringApplication.run(ReptileDemoApplication.class, args);
    }
}
