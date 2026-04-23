import java.lang.reflect.*;
import java.util.*;

/**
 * 反射综合演示 - 5个实用场景
 */
public class ReflectionDemo {

    public static void main(String[] args) throws Exception {
        System.out.println("========== 反射综合演示 ==========\n");

        demo1_动态创建对象();
        demo2_调用私有方法();
        demo3_通用属性复制工具();
        demo4_简易框架_自动注入();
        demo5_查看类的完整信息();
    }

    /**
     * Demo 1: 动态创建对象 (框架的基础)
     */
    static void demo1_动态创建对象() throws Exception {
        System.out.println("【Demo 1】动态创建对象");
        System.out.println("--------------------------------------------------");

        // 模拟 Spring 容器根据配置创建对象
        String[] classNames = {"User", "java.util.Date", "java.util.ArrayList"};

        for (String className : classNames) {
            Class<?> clazz = Class.forName(className);
            Object obj = clazz.getDeclaredConstructor().newInstance();
            System.out.println("✅ 创建了对象: " + obj.getClass().getSimpleName());
        }

        // 使用有参构造
        Class<?> userClass = Class.forName("User");
        Constructor<?> constructor = userClass.getConstructor(String.class, int.class);
        Object user = constructor.newInstance("张三", 25);
        System.out.println("✅ 有参构造创建: " + user);

        System.out.println();
    }

    /**
     * Demo 2: 调用私有方法 (测试/框架常用)
     */
    static void demo2_调用私有方法() throws Exception {
        System.out.println("【Demo 2】调用私有方法");
        System.out.println("--------------------------------------------------");

        User user = new User("李四", 30);

        // 正常情况: user.secretMethod(); // 编译错误!

        // 反射突破访问控制
        Method secretMethod = User.class.getDeclaredMethod("secretMethod");
        secretMethod.setAccessible(true);  // 暴力破解访问权限
        secretMethod.invoke(user);

        System.out.println();
    }

    /**
     * Demo 3: 通用属性复制工具 (类似 Spring BeanUtils)
     */
    static void demo3_通用属性复制工具() throws Exception {
        System.out.println("【Demo 3】通用属性复制工具");
        System.out.println("--------------------------------------------------");

        User source = new User("王五", 28);
        source.setEmail("wangwu@example.com");

        User target = new User();

        // 通用复制方法 - 不管什么类都能用!
        copyProperties(source, target);

        System.out.println("源对象: " + source);
        System.out.println("目标对象: " + target);
        System.out.println();
    }

    // 通用属性复制工具实现
    static void copyProperties(Object source, Object target) throws Exception {
        Class<?> clazz = source.getClass();
        Field[] fields = clazz.getDeclaredFields();

        for (Field field : fields) {
            field.setAccessible(true);
            Object value = field.get(source);
            if (value != null) {
                field.set(target, value);
            }
        }
    }

    /**
     * Demo 4: 简易依赖注入框架 (模拟 Spring @Autowired)
     */
    static void demo4_简易框架_自动注入() throws Exception {
        System.out.println("【Demo 4】简易依赖注入框架");
        System.out.println("--------------------------------------------------");

        UserService service = new UserService();

        // 框架自动注入 userDao (模拟 Spring 容器)
        autoInject(service);

        service.doSomething();
        System.out.println();
    }

    // 简易依赖注入实现
    static void autoInject(Object obj) throws Exception {
        Class<?> clazz = obj.getClass();
        Field[] fields = clazz.getDeclaredFields();

        for (Field field : fields) {
            if (field.isAnnotationPresent(Inject.class)) {
                field.setAccessible(true);
                // 创建需要注入的对象
                Object dependency = field.getType().getDeclaredConstructor().newInstance();
                field.set(obj, dependency);
                System.out.println("✅ 自动注入: " + field.getName() + " -> " + dependency.getClass().getSimpleName());
            }
        }
    }

    /**
     * Demo 5: 查看类的完整信息 (API 文档生成器原理)
     */
    static void demo5_查看类的完整信息() throws Exception {
        System.out.println("【Demo 5】查看类的完整信息");
        System.out.println("--------------------------------------------------");

        Class<?> clazz = User.class;

        System.out.println("类名: " + clazz.getName());

        System.out.println("\n📋 所有字段:");
        for (Field field : clazz.getDeclaredFields()) {
            System.out.println("  - " + Modifier.toString(field.getModifiers())
                + " " + field.getType().getSimpleName()
                + " " + field.getName());
        }

        System.out.println("\n📋 所有方法:");
        for (Method method : clazz.getDeclaredMethods()) {
            System.out.print("  - " + Modifier.toString(method.getModifiers())
                + " " + method.getReturnType().getSimpleName()
                + " " + method.getName() + "(");

            Parameter[] params = method.getParameters();
            for (int i = 0; i < params.length; i++) {
                System.out.print(params[i].getType().getSimpleName());
                if (i < params.length - 1) System.out.print(", ");
            }
            System.out.println(")");
        }

        System.out.println("\n📋 所有构造器:");
        for (Constructor<?> constructor : clazz.getDeclaredConstructors()) {
            System.out.print("  - " + constructor.getName() + "(");
            Parameter[] params = constructor.getParameters();
            for (int i = 0; i < params.length; i++) {
                System.out.print(params[i].getType().getSimpleName());
                if (i < params.length - 1) System.out.print(", ");
            }
            System.out.println(")");
        }
    }
}

// ========== 辅助类 ==========

// 自定义注解 (模拟 Spring @Autowired)
@java.lang.annotation.Retention(java.lang.annotation.RetentionPolicy.RUNTIME)
@interface Inject {
}

// 模拟 Service 层
class UserService {
    @Inject  // 标记需要自动注入
    private UserDao userDao;

    public void doSomething() {
        System.out.println("📢 UserService 正在工作...");
        if (userDao != null) {
            userDao.query();
        }
    }
}

// 模拟 DAO 层
class UserDao {
    public void query() {
        System.out.println("📢 UserDao 查询数据库...");
    }
}
