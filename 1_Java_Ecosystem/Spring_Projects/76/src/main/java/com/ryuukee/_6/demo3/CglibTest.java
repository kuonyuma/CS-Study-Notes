package com.ryuukee._6.demo3;

import org.springframework.cglib.proxy.Enhancer;
import org.springframework.cglib.proxy.MethodInterceptor;
import org.springframework.cglib.proxy.MethodProxy;

public class CglibTest {

    public static void main(String[] args) {

        Enhancer enhancer = new Enhancer();
        enhancer.setSuperclass(Student.class);

        enhancer.setCallback((MethodInterceptor)(
            obj,
            method,
            methodArgs,
            proxy)->{
            System.out.println("开始前");
            Object ret = proxy.invokeSuper(obj,methodArgs);
            System.out.println("开始后");
            return ret;
        });

        Student student = (Student) enhancer.create(
            new Class[]{String.class},
            new Object[]{"张三"});

        student.doWork();
    }
}
