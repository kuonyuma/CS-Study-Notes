package com.lyuke.iocdemo.UserBean;

import com.lyuke.iocdemo.Student.Student;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class UserBean {
    @Bean
    public Student s1(){
        return new Student("李四",19);
    }

    @Bean
    public Student s2(){
        return new Student("老六",29);
    }
}
