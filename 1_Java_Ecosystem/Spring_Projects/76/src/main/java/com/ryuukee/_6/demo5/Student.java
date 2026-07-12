package com.ryuukee._6.demo5;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Slf4j
@Service
public class Student {

    public String doWork(String subject, int hours) {
        log.info("正在写作业：科目={}, 预计{}小时", subject, hours);
        return subject + " 作业完成！";
    }

    public void play(String game) {
        log.info("正在玩：{}", game);
    }
}
