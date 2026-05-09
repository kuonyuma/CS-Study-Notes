package com.lyuke.springdidemo.Configuration;

import com.lyuke.springdidemo.Student.Student;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class AppConfig {

    @Bean
    public Student student() {
        return new Student("Tom", "male",
                "tom@example.com", 18);
    }

//    @Bean
//    public Student student2() {
//        return new Student("小明", "男",
//                "小明@example.com", 28);
//    }
}
