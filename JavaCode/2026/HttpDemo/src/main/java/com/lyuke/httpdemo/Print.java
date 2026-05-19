package com.lyuke.httpdemo;


import lombok.Data;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Data
@RequestMapping("/Class")
@RestController
public class Print {

    String name = "lisi";

    @RequestMapping("/put")
    public String put(){

        return name + " " + "：正在说话 ";
    }
}
