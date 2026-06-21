package com.lyuke.iocdemo.v1;


import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

public class CarBody {
    private CarChassis carChassis;

    public CarBody(CarChassis carChassis){
        this.carChassis = carChassis;
        System.out.println("车身造好了");
    }
}
