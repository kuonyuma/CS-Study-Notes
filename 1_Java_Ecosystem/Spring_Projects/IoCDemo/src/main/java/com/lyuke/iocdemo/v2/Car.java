package com.lyuke.iocdemo.v2;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class Car {

    private CarBody carBody;
@Autowired
    public Car(CarBody carBody){
        this.carBody = carBody;
        System.out.println("车造好了");
    }

    public void run(){
        System.out.println("车启动....");
    }
}
