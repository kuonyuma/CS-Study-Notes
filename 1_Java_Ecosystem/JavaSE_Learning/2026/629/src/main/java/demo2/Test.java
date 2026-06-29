package demo2;

import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

public class Test {
    static void main() {

        try {
            //获取到类对象
            Class<?> clazz = Class.forName("demo2.Student");
            //获取到构造器
            Constructor<?> constructors = clazz.getDeclaredConstructor(String.class);
            constructors.setAccessible(true);
            Student stu = (Student) constructors.newInstance("张三");
            System.out.println(stu.getName());
            //获取成员变量
            Field nameField = clazz.getDeclaredField("name");
            nameField.setAccessible(true);

            nameField.set(stu,"小明");
            System.out.println(stu.getName());

            Method sleepMethod = clazz.getDeclaredMethod("sleep");
            Method sayMethod = clazz.getDeclaredMethod("say");
            sleepMethod.setAccessible(true);
            sayMethod.setAccessible(true);
            sayMethod.invoke(stu);
            sleepMethod.invoke(null);//静态


        } catch (ClassNotFoundException e) {
            e.getMessage();
        } catch (NoSuchMethodException | InstantiationException | IllegalAccessException | InvocationTargetException e) {
            throw new RuntimeException(e);
        }catch (NoSuchFieldException e){
            e.getMessage();
        }
    }
}
