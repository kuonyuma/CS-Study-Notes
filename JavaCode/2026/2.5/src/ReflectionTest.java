import java.util.Scanner;

public class ReflectionTest {
    public static void main(String[] args) throws Exception {
        Scanner sc = new Scanner(System.in);
        System.out.println("请输入完整的类名 (比如 java.util.Date):");

        // 1. 获取用户输入的字符串 (这就只是一串字符，不是代码)
        String className = sc.next();

        // 2. 【核心】反射：把字符串变成了类对象
        // 哪怕你输入一个我自己写的 com.test.Dog，这里也能运行，完全不用改代码！
        Class<?> clazz = Class.forName(className);

        // 3. 【核心】反射：现场通过类对象，new 了一个实例
        Object obj = clazz.newInstance();

        System.out.println("反射帮你造出了对象: " + obj);
    }
}
