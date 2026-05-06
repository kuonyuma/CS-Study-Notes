package com.lyuke.iocdemo.UserComponent;

import org.springframework.stereotype.Component;

@Component
public class UserComponent {

    public void print(){
        System.out.println("正在使用 UserComponent");
    }
}
