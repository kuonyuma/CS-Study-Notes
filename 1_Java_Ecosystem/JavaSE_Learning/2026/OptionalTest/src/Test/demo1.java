package Test;

import Entity.Car;
import Entity.User;

import java.util.Optional;

public class demo1 {

    public static void main(String[] args){
        //t1();
        //t3();

        t6();
    }

    private static void t1(){
        User bean1 = new User("张三",18);
        Optional<User> op = Optional.ofNullable(bean1)
            .filter(bean->bean.getAge() > 19);

        System.out.println(op);
        System.out.println("t1测试完毕...");
    }

    private static void t2(){
        Optional<String> optional = Optional.empty();
        System.out.println(optional.isEmpty());

        String hero = null;
        Optional<String> optionalOf = Optional.of(hero);
        System.out.println(optionalOf.get());

        String t1 = "测试";
        Optional<String> op3 = Optional.ofNullable(t1);
        System.out.println(op3.isEmpty());

        System.out.println("t2测试完毕");
    }

    private static void t3(){
        User bean1 = new User("张三",18);
        Optional<String> op = Optional.ofNullable(bean1)
            .map(User::getName);

        System.out.println(op);
    }

    private static void t4(){
        System.out.println("=== 🚗 开始测试 Optional.flatMap 链式调用 ===");
        Car car1 = new Car("京A88888");
        User user1 = new User("张三", 20);
        user1.setCar(car1);

        String plate1 = Optional.ofNullable(user1)
            .flatMap(User::getCar)          // 返回 Optional<Car>
            .flatMap(Car::getPlateNumber)   // 返回 Optional<String>
            .orElse("无车牌");
        System.out.println(plate1);
    }

    private static void t5(){
        String beverage = "可乐";
        String input  = null;

        String result = Optional
            .ofNullable(input)
            .orElse("白水");
        System.out.println(result);
    }

    private static void t6(){
        //orElse
        String input = null;
        String input2 = "null";
        Optional<String> bean1 = Optional.ofNullable(input);
        String result = bean1.orElse(testOrElse());
        System.out.println(result);
        String result1 = bean1.orElseGet(()->{
            System.out.println("我被执行力");
            return "默认值";
        });
        System.out.println(result);

    }

    private static String testOrElse(){
        System.out.println("orElse被执行了");
        return "默认值1";
    }

}
