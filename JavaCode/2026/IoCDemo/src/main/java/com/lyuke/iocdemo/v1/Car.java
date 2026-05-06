package com.lyuke.iocdemo.v1;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

public class Car {
    private CarBody carBody;
    public Car(CarBody carBody){
        this.carBody = carBody;
        System.out.println("车造好了");
    }

    public void run(){
        System.out.println("汽车运行中...");
    }
}
