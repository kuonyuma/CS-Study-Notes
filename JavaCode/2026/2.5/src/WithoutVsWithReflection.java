/**
 * 对比: 不用反射 vs 用反射
 * 直观感受反射的威力
 */
public class WithoutVsWithReflection {

    public static void main(String[] args) throws Exception {
        System.out.println("========================================");
        System.out.println("    对比: 不用反射 vs 用反射");
        System.out.println("========================================\n");

        demo1_创建对象的对比();
        demo2_复制对象的对比();
        demo3_配置化的对比();
    }

    /**
     * 对比 1: 创建对象
     */
    static void demo1_创建对象的对比() {
        System.out.println("【对比 1】创建对象");
        System.out.println("--------------------------------------------------");

        System.out.println("❌ 不用反射 - 写死了,只能创建特定类:");
        System.out.println("   User user = new User();");
        User user = new User();
        System.out.println("   创建了: " + user.getClass().getSimpleName());

        System.out.println("\n✅ 用反射 - 灵活,可以创建任意类:");
        System.out.println("   String className = \"User\";  // 可以从配置文件读取");
        System.out.println("   Class<?> clazz = Class.forName(className);");
        System.out.println("   Object obj = clazz.newInstance();");

        try {
            String className = "User";  // 假设从配置文件读取
            Class<?> clazz = Class.forName(className);
            Object obj = clazz.getDeclaredConstructor().newInstance();
            System.out.println("   创建了: " + obj.getClass().getSimpleName());
        } catch (Exception e) {
            e.printStackTrace();
        }

        System.out.println("\n💡 区别: 反射可以根据字符串动态创建,不用改代码!");
        System.out.println();
    }

    /**
     * 对比 2: 复制对象属性
     */
    static void demo2_复制对象的对比() {
        System.out.println("【对比 2】复制对象属性");
        System.out.println("--------------------------------------------------");

        User source = new User("张三", 25);
        source.setEmail("zhangsan@example.com");

        System.out.println("❌ 不用反射 - 要为每个类写复制方法:");
        System.out.println("   public void copyUser(User source, User target) {");
        System.out.println("       target.setName(source.getName());");
        System.out.println("       target.setAge(source.getAge());");
        System.out.println("       target.setEmail(source.getEmail());");
        System.out.println("   }");
        System.out.println("   // 如果有 Order、Product 类,还要写 copyOrder、copyProduct...");

        User target1 = new User();
        copyUserWithoutReflection(source, target1);
        System.out.println("   结果: " + target1);

        System.out.println("\n✅ 用反射 - 一个方法处理所有类:");
        System.out.println("   public void copyProperties(Object source, Object target) {");
        System.out.println("       // 通用实现,不管什么类都能用!");
        System.out.println("   }");

        User target2 = new User();
        try {
            copyPropertiesWithReflection(source, target2);
            System.out.println("   结果: " + target2);
        } catch (Exception e) {
            e.printStackTrace();
        }

        System.out.println("\n💡 区别: 反射写一个方法就能复制所有类,代码更少更通用!");
        System.out.println();
    }

    /**
     * 对比 3: 配置化创建对象
     */
    static void demo3_配置化的对比() throws Exception {
        System.out.println("【对比 3】根据配置创建不同对象");
        System.out.println("--------------------------------------------------");

        System.out.println("场景: 根据配置文件创建不同的数据库连接");
        System.out.println();

        System.out.println("❌ 不用反射 - 要用 if-else 判断:");
        System.out.println("   String dbType = config.get(\"db.type\");");
        System.out.println("   if (dbType.equals(\"mysql\")) {");
        System.out.println("       conn = new MySQLConnection();");
        System.out.println("   } else if (dbType.equals(\"oracle\")) {");
        System.out.println("       conn = new OracleConnection();");
        System.out.println("   } else if (dbType.equals(\"postgres\")) {");
        System.out.println("       conn = new PostgresConnection();");
        System.out.println("   }");
        System.out.println("   // 新增数据库类型,要改代码!");

        System.out.println("\n✅ 用反射 - 直接根据类名创建:");
        System.out.println("   String className = config.get(\"db.class\");");
        System.out.println("   Class<?> clazz = Class.forName(className);");
        System.out.println("   Connection conn = (Connection) clazz.newInstance();");
        System.out.println("   // 新增数据库类型,只需改配置文件!");

        // 模拟配置
        String[] configs = {"User", "java.util.Date", "java.util.ArrayList"};
        System.out.println("\n   实际运行:");
        for (String config : configs) {
            Class<?> clazz = Class.forName(config);
            Object obj = clazz.getDeclaredConstructor().newInstance();
            System.out.println("   - 配置 \"" + config + "\" -> 创建了 " + obj.getClass().getSimpleName());
        }

        System.out.println("\n💡 区别: 反射让代码更灵活,新增类型不用改代码!");
        System.out.println();
    }

    // ========== 辅助方法 ==========

    // 不用反射的复制 (只能复制 User)
    static void copyUserWithoutReflection(User source, User target) {
        target.setName(source.getName());
        target.setAge(source.getAge());
        target.setEmail(source.getEmail());
    }

    // 用反射的复制 (可以复制任意类)
    static void copyPropertiesWithReflection(Object source, Object target) throws Exception {
        Class<?> clazz = source.getClass();
        java.lang.reflect.Field[] fields = clazz.getDeclaredFields();

        for (java.lang.reflect.Field field : fields) {
            field.setAccessible(true);
            Object value = field.get(source);
            if (value != null) {
                field.set(target, value);
            }
        }
    }
}
