package com.lyuke.iocdemo.v1;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;


public class CarChassis {
    private Tire tire;

    public CarChassis(Tire tire){
        this.tire = tire;
        System.out.println("车底盘造好了");
    }
}
