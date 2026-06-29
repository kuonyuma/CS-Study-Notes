package demo;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.Statement;

/**
 * 程序入口 —— 演示玩具版 ORM 的完整流程
 *
 * 使用 H2 内存数据库：
 *   - 无需安装 MySQL
 *   - 程序启动时数据库自动创建，程序结束后自动销毁
 *   - 非常适合学习和测试
 */
public class Main {

    public static void main(String[] args) throws Exception {

        // ══════════════════════════════════════════════
        //  第一步：连接 H2 内存数据库
        // ══════════════════════════════════════════════
        // "jdbc:h2:mem:testdb" → 在内存里创建名为 testdb 的数据库
        Connection conn = DriverManager.getConnection(
                "jdbc:h2:mem:testdb;DB_CLOSE_DELAY=-1",
                "sa",   // 用户名（H2 默认）
                ""      // 密码（H2 默认为空）
        );
        System.out.println("✅ 数据库连接成功！（H2 内存数据库）");
        System.out.println();

        // ══════════════════════════════════════════════
        //  第二步：手动建表（真实 ORM 框架也会做这步）
        // ══════════════════════════════════════════════
        Statement stmt = conn.createStatement();
        stmt.execute("""
                create table user (
                    id    int primary key,
                    name  varchar(50),
                    email varchar(100)
                )
                """);
        System.out.println("✅ 建表成功：user 表已创建");
        System.out.println();

        // ══════════════════════════════════════════════
        //  第三步：创建 MiniMapper（类比 MyBatis 的 UserMapper）
        // ══════════════════════════════════════════════
        MiniMapper<User> userMapper = new MiniMapper<>(User.class, conn);

        // ══════════════════════════════════════════════
        //  第四步：INSERT —— 把 Java 对象写入数据库
        // ══════════════════════════════════════════════
        System.out.println("──────────────────────────────────────");
        System.out.println("【测试 insert】");

        User user1 = new User(1, "张三", "zhangsan@qq.com");
        User user2 = new User(2, "李四", "lisi@gmail.com");

        userMapper.insert(user1);
        userMapper.insert(user2);

        System.out.println();

        // ══════════════════════════════════════════════
        //  第五步：FIND BY ID —— 从数据库查询，自动装回对象
        // ══════════════════════════════════════════════
        System.out.println("──────────────────────────────────────");
        System.out.println("【测试 findById】");

        User found1 = userMapper.findById(1);
        System.out.println("  查询结果: " + found1);

        User found2 = userMapper.findById(2);
        System.out.println("  查询结果: " + found2);

        // 查一个不存在的 id
        User notFound = userMapper.findById(999);
        System.out.println("  不存在的记录: " + notFound);

        System.out.println();
        System.out.println("══════════════════════════════════════");
        System.out.println("🎉 Demo 结束！");
        System.out.println("核心原理：注解提供配置 → 反射读取配置和字段值 → JDBC 执行 SQL");

        conn.close();
    }
}
