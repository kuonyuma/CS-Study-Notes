package com.lyuke.iocdemo.Configuration;

import org.springframework.context.annotation.Configuration;

@Configuration
public class UserConfiguration {

    public void print(){
        System.out.println("正在使用 Configuration");
    }
}
