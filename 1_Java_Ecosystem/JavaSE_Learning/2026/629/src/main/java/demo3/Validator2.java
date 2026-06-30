package demo3;

import java.lang.reflect.Field;

public class Validator2 {

    public static void validate2(Object obj){
        try {
            //通过反射抓住类
            Class<?> clazz = obj.getClass();//任何类通用

            //拿到所有的字段
            Field[] fields = clazz.getDeclaredFields();
            for(Field e : fields){//可以检查多个贴了range的字段
                if(e.isAnnotationPresent(MyRange.class)){
                    e.setAccessible(true);//获取私有访问权
                    MyRange myRange = e.getAnnotation(MyRange.class);
                    int max = myRange.maxAge();
                    int min = myRange.minAge();
                    int value = (Integer) e.get(obj);
                    if(max < value || min > value){
                        System.out.println("输入非法的");
                        continue;
                    }
                    System.out.println("输入是合法的");
                }
            }
        }catch (IllegalAccessException e) {
            throw new RuntimeException(e);
        }
    }
}
