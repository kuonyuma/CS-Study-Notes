package demo2;

import java.lang.reflect.Field;

public class Validator {

    public static void validate(User user){

        try{
            //拿到User类
            Class<User> clazz = User.class;
            //拿到特定的字段
            Field ageField = clazz.getDeclaredField("age");
            //通过反射获取到注解的实例化对象
            MyRange myRange = ageField.getAnnotation(MyRange.class);
            int max = myRange.max();
            int min = myRange.min();

            if(user.age < min || user.age > max){
                System.out.println("输入的参数不合法");
                return;
            }
            System.out.println("参数是合法的");

        }catch(NoSuchFieldException e){
            e.getMessage();
        }
    }
}
