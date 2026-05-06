package com.lyuke.iocdemo.v2;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class CarBody {

    private CarChassis carChassis;
    @Autowired
    public CarBody(CarChassis carChassis){
        this.carChassis = carChassis;
        System.out.println("车身造好了");
    }
}
