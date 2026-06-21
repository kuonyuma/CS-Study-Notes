package com.lyuke.iocdemo.v2;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class CarChassis {
    private Tire tire;
@Autowired
    public CarChassis(Tire tire){
        this.tire = tire;

        System.out.println("车底盘造好了");
    }
}
