package demo4;

import java.lang.reflect.Constructor;

public class main {

    public static void main(String[] args) throws NoSuchMethodException {
        Class<User> clazz = User.class;
        
        // 获取第一个无参数构造方法
        Constructor<User> constructor1 = clazz.getDeclaredConstructor();
        System.out.println("No-args constructor: " + constructor1);
        
        // 获取第二个带参数的构造方法
        Constructor<User> constructor2 = clazz.getDeclaredConstructor(Long.class, String.class, Integer.class, String.class);
        System.out.println("Parameterized constructor: " + constructor2);
    }
}
    