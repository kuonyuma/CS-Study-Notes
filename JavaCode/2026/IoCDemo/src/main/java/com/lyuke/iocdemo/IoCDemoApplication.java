package com.lyuke.iocdemo;

import com.lyuke.iocdemo.Configuration.UserConfiguration;
import com.lyuke.iocdemo.Student.Student;
import com.lyuke.iocdemo.UserComponent.UserComponent;
import com.lyuke.iocdemo.UserController.UserController;
import com.lyuke.iocdemo.UserRepository.UserRepository;
import com.lyuke.iocdemo.UserService.UserService;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.ApplicationContext;


@SpringBootApplication
public class IoCDemoApplication {
    //测试Bean
    public static void main(String[] args){
        ApplicationContext context = SpringApplication.run(
                IoCDemoApplication.class,args
        );

        Student bean1 = (Student) context.getBean("s1");
//       Student bean1 = (Student) context.getBean(Student.class);
        System.out.println("获取到的 Student Bean: " + bean1);
    }


    //测试Configuration
    public static void main5(String[] args){
        ApplicationContext context = SpringApplication.run(
                IoCDemoApplication.class,args
        );

        UserConfiguration bean1 = context.getBean(UserConfiguration.class);
        bean1.print();
    }
    //测试Component
    public static void main4(String[] args){
        ApplicationContext context = SpringApplication.run(
                IoCDemoApplication.class,args
        );

        UserComponent bean1 = context.getBean(UserComponent.class);
        bean1.print();
    }

    //测试Repository
    public static void main3(String[] args){
        ApplicationContext context = SpringApplication.run(
                IoCDemoApplication.class,args
        );

        UserRepository bean1 = context.getBean(UserRepository.class);
        bean1.print();
    }

    //测试Service
    public static void main2(String[] args){
        ApplicationContext context = SpringApplication.run(
                IoCDemoApplication.class,args
        );

        UserService bean1 = context.getBean(UserService.class);
        bean1.print();
    }

    //测试Controller
    public static void main1(String[] args) {
        ApplicationContext context = SpringApplication.run(
                IoCDemoApplication.class, args);

        UserController uc1 = (UserController)context.getBean(
                "userController");

        uc1.print();

        UserController uc2 = context.getBean(UserController.class);
        uc2.print();

        System.out.println(uc2);
        System.out.println(uc1);


    }

}
