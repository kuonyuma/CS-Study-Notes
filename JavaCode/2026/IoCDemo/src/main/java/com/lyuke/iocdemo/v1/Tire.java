package com.lyuke.iocdemo.v1;


import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;


public class Tire {

    private Integer size;
    private String color;
    public Tire(Integer size,String color){
        this.size = size;
        this.color = color;
        System.out.println("轮胎造好了");
    }

}
