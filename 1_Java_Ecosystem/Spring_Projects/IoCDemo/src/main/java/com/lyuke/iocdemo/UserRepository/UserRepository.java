package com.lyuke.iocdemo.UserRepository;

import org.springframework.stereotype.Repository;

@Repository
public class UserRepository {

    public void print(){
        System.out.println("正在使用 UserRepository");
    }
}
