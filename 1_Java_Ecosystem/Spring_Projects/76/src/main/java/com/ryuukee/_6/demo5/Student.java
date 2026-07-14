package com.ryuukee._6.demo5;

import lombok.extern.slf4j.Slf4j;
import org.springframework.aop.framework.AopContext;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Service;

@Slf4j
@Service
public class Student {

    @Lazy
    @Autowired
    private Student self;

    public String doWork(String subject, int hours) {
        log.info("正在写作业：科目={}, 预计{}小时", subject, hours);
        //this.play("famine");
//        self.play("famine");
//        Student object = (Student)AopContext.currentProxy();
//        object.play("famine");
        return subject + " 作业完成！";
    }

    public void play(String game) {
        log.info("正在玩：{}", game);
    }
}
