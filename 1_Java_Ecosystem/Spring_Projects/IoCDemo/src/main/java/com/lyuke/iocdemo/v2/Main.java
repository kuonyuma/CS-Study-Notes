package com.lyuke.iocdemo.v2;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.ApplicationContext;
// 1. 添加这个注解，告诉 Spring 这是一个启动类，并且让它自动扫描当前包(v2)下的 @Component
@SpringBootApplication
public class Main {
    public static void main(String[] args) {
        // 2. 启动 Spring 容器，并获取上下文 (Context，也就是我们说的“仓库/容器”)
        ApplicationContext context = SpringApplication.run(Main.class, args);
        // 3. 从容器中获取类型为 Car 的 Bean
        Car myCar = context.getBean(Car.class);
        // 4. 调用方法，测试是否装配成功
        myCar.run();
    }
}