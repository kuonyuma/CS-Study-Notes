import java.lang.reflect.*;
import java.util.*;

/**
 * 交互式反射实验室 - 自己动手体验反射
 */
public class ReflectionPlayground {

    public static void main(String[] args) throws Exception {
        Scanner sc = new Scanner(System.in);

        System.out.println("========== 反射实验室 ==========");
        System.out.println("你可以用反射操作任何类!");
        System.out.println();

        while (true) {
            System.out.println("请选择实验:");
            System.out.println("1. 创建对象并设置属性");
            System.out.println("2. 调用对象的方法");
            System.out.println("3. 查看类的所有信息");
            System.out.println("4. 暴力调用私有方法");
            System.out.println("0. 退出");
            System.out.print("\n你的选择: ");

            int choice = sc.nextInt();
            sc.nextLine(); // 消耗换行符

            if (choice == 0) {
                System.out.println("再见!");
                break;
            }

            System.out.println();

            switch (choice) {
                case 1:
                    experiment1_创建并设置属性(sc);
                    break;
                case 2:
                    experiment2_调用方法(sc);
                    break;
                case 3:
                    experiment3_查看类信息(sc);
                    break;
                case 4:
                    experiment4_调用私有方法(sc);
                    break;
                default:
                    System.out.println("无效选择!");
            }

            System.out.println("\n" + "=".repeat(50) + "\n");
        }
    }

    /**
     * 实验 1: 创建对象并设置属性
     */
    static void experiment1_创建并设置属性(Scanner sc) throws Exception {
        System.out.println("【实验1】创建对象并设置属性");
        System.out.println("提示: 试试输入 User 或 java.util.Date");
        System.out.print("请输入类名: ");
        String className = sc.nextLine();

        // 动态创建对象
        Class<?> clazz = Class.forName(className);
        Object obj = clazz.getDeclaredConstructor().newInstance();
        System.out.println("✅ 成功创建: " + obj.getClass().getSimpleName());

        if (className.equals("User")) {
            // 反射设置 User 的属性
            Field nameField = clazz.getDeclaredField("name");
            nameField.setAccessible(true);
            nameField.set(obj, "反射设置的名字");

            Field ageField = clazz.getDeclaredField("age");
            ageField.setAccessible(true);
            ageField.set(obj, 99);

            System.out.println("✅ 反射设置了属性: " + obj);
        } else {
            System.out.println("对象创建成功: " + obj);
        }
    }

    /**
     * 实验 2: 调用方法
     */
    static void experiment2_调用方法(Scanner sc) throws Exception {
        System.out.println("【实验2】调用对象的方法");

        // 创建一个 User 对象
        User user = new User("测试用户", 25);

        // 获取所有公开方法
        Method[] methods = User.class.getMethods();
        System.out.println("\n可用的方法:");
        List<Method> userMethods = new ArrayList<>();
        int index = 1;
        for (Method method : methods) {
            if (method.getDeclaringClass() == User.class) {
                System.out.println(index + ". " + method.getName());
                userMethods.add(method);
                index++;
            }
        }

        System.out.print("\n选择要调用的方法 (输入序号): ");
        int choice = sc.nextInt();
        sc.nextLine();

        if (choice > 0 && choice <= userMethods.size()) {
            Method method = userMethods.get(choice - 1);

            // 准备参数
            Parameter[] params = method.getParameters();
            Object[] args = new Object[params.length];

            for (int i = 0; i < params.length; i++) {
                System.out.print("输入参数 " + (i+1) + " (" + params[i].getType().getSimpleName() + "): ");
                if (params[i].getType() == String.class) {
                    args[i] = sc.nextLine();
                } else if (params[i].getType() == int.class) {
                    args[i] = sc.nextInt();
                    sc.nextLine();
                }
            }

            // 反射调用方法
            Object result = method.invoke(user, args);
            System.out.println("✅ 方法执行完成!");
            if (result != null) {
                System.out.println("返回值: " + result);
            }
            System.out.println("对象状态: " + user);
        }
    }

    /**
     * 实验 3: 查看类的完整信息
     */
    static void experiment3_查看类信息(Scanner sc) throws Exception {
        System.out.println("【实验3】查看类的所有信息");
        System.out.print("请输入类名 (比如 User, java.lang.String): ");
        String className = sc.nextLine();

        Class<?> clazz = Class.forName(className);

        System.out.println("\n" + "=".repeat(50));
        System.out.println("类: " + clazz.getName());
        System.out.println("=".repeat(50));

        // 字段
        System.out.println("\n📦 字段:");
        for (Field field : clazz.getDeclaredFields()) {
            System.out.printf("  %s %s %s\n",
                Modifier.toString(field.getModifiers()),
                field.getType().getSimpleName(),
                field.getName());
        }

        // 构造器
        System.out.println("\n🔧 构造器:");
        for (Constructor<?> constructor : clazz.getDeclaredConstructors()) {
            System.out.print("  " + clazz.getSimpleName() + "(");
            Parameter[] params = constructor.getParameters();
            for (int i = 0; i < params.length; i++) {
                System.out.print(params[i].getType().getSimpleName());
                if (i < params.length - 1) System.out.print(", ");
            }
            System.out.println(")");
        }

        // 方法
        System.out.println("\n⚙️ 方法 (只显示本类定义的):");
        for (Method method : clazz.getDeclaredMethods()) {
            System.out.print("  " + Modifier.toString(method.getModifiers()) + " ");
            System.out.print(method.getReturnType().getSimpleName() + " ");
            System.out.print(method.getName() + "(");
            Parameter[] params = method.getParameters();
            for (int i = 0; i < params.length; i++) {
                System.out.print(params[i].getType().getSimpleName());
                if (i < params.length - 1) System.out.print(", ");
            }
            System.out.println(")");
        }
    }

    /**
     * 实验 4: 暴力调用私有方法
     */
    static void experiment4_调用私有方法(Scanner sc) throws Exception {
        System.out.println("【实验4】暴力调用私有方法");
        System.out.println("我们有一个 User 对象,它有一个私有方法 secretMethod()");
        System.out.println("正常情况下无法调用,但反射可以突破访问控制!\n");

        User user = new User("神秘用户", 30);

        System.out.println("按回车键,用反射调用私有方法...");
        sc.nextLine();

        // 反射调用私有方法
        Method secretMethod = User.class.getDeclaredMethod("secretMethod");
        secretMethod.setAccessible(true);  // 关键: 突破访问控制
        secretMethod.invoke(user);

        System.out.println("\n💡 这就是反射的威力!");
        System.out.println("   框架/测试工具常用这个特性访问内部实现");
    }
}
