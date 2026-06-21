package com.ryuukee.concurrencylimitchallenge2;

import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.function.Supplier;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class demo1 {

    @Test
    void t1(){
        Runnable r1 = new Runnable() {
            @Override
            public void run() {
                System.out.println("测试");
            }
        };

        Thread t1 = new Thread(r1);
        t1.start();

        Thread t2 = new Thread(new Runnable() {
            @Override
            public void run() {
                System.out.println("测试r2");
            }
        });

        t2.start();

        Runnable r3 = ()->{
            System.out.println("测试lambda");
        };

        Thread t3 = new Thread(()->{
            System.out.println("测试lambda2");
        });

        t3.start();
    }
}

class test{

    @Test
    void t1(){
        Consumer<String> print = (s)->{
            System.out.println(s);
        };

        print.accept("你好");

        List<String> listStr = new ArrayList<>();
        for (int i = 0;i < 10;i++){
            listStr.add(i+"");
        }
        listStr.stream().forEach(System.out::println);
    }
    @Test
    void t2(){
        Supplier<Double> supplier = ()->{return Math.random();};
        System.out.println(supplier.get());

        Stream.generate(supplier)
            .limit(10)
            .forEach(System.out::println);
    }
    @Test
    void t3(){
        //函数接口，转换接口
        Function<String,Integer> act = s->s.length();
        System.out.println(act.apply("java8"));

        //Stream中的应用
        List<String> listStr = new ArrayList<>();
        for (int i = 0;i < 10;i++){
            listStr.add(i +"");
        }
        listStr.stream()
            .map(e->e.length()) // map 接收的就是一个 Function
            .forEach(System.out::println);
    }
    @Test
    void t4(){
        //断言型接口_Predicate
        Predicate<String> isEven = (String s)->s.length() % 2 == 0;

        System.out.println(isEven.test("java"));
        System.out.println(isEven.test("java25"));
        System.out.println(isEven.test("java8"));

        //Stream中的应用
        List<String> list = new ArrayList<>();
        list.add("zhangsan");
        list.add("wangwu");
        list.add("zhaoliu");
        list.add("ryuukee");

        list.stream()
            .filter(s->s.length() > 6)
            .forEach(System.out::println);
    }



}