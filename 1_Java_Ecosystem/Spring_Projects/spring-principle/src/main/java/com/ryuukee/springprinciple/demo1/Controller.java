package com.ryuukee.springprinciple.demo1;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/demo1")
public class Controller {

    @Autowired
    private Student stu;

    @RequestMapping("/test")
    public void test(){
        System.out.println(stu);
    }
}
