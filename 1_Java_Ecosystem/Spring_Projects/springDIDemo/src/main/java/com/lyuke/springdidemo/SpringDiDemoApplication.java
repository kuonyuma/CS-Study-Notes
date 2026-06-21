package com.lyuke.springdidemo;

import com.lyuke.springdidemo.Service.Pirnt;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.ApplicationContext;

@SpringBootApplication
public class SpringDiDemoApplication {

    public static void main(String[] args) {
        ApplicationContext context = SpringApplication.run(
                SpringDiDemoApplication.class, args);

        Pirnt bean1 = context.getBean(Pirnt.class);

        bean1.print();

    }


}
