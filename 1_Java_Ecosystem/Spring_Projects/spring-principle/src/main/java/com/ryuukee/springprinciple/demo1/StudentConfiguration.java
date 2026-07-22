package com.ryuukee.springprinciple.demo1;

import org.springframework.beans.factory.config.ConfigurableBeanFactory;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Scope;
import org.springframework.context.annotation.ScopedProxyMode;

@Configuration
public class StudentConfiguration {

//    @Scope(ConfigurableBeanFactory.SCOPE_PROTOTYPE)
    @Scope(value = "session", proxyMode = ScopedProxyMode.TARGET_CLASS)
    @Bean
    public Student createStudent(){
        return new Student();
    }
}
