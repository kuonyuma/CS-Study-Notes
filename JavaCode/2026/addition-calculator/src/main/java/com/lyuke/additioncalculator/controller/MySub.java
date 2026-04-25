package com.lyuke.additioncalculator.controller;


import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/MySub_class")
public class MySub {
    @RequestMapping("/sub")
    public String sub(Integer num1 ,Integer num2){
        int ret = num1 - num2;
        return "num1 - num2 = " + ret;
    }
}
