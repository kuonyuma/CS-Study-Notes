package com.lyuke.iocdemo.v2;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class Tire {
    private Integer size;

    public Tire(){
        this.size = 19;
    }

}
