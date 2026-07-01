package demo;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.Statement;

/**
 * 程序入口 —— 演示玩具版 ORM 的完整流程
 *
 * 使用 MySQL 数据库：
 *   - 数据库名：mini_orm_demo（会自动创建）
 *   - 数据持久化：程序结束后数据依然保留
 */
public class Main {

    public static void main(String[] args) throws Exception {

        // ══════════════════════════════════════════════
        //  第一步：连接 MySQL 数据库
        //  createDatabaseIfNotExist=true → 数据库不存在时自动创建
        // ══════════════════════════════════════════════
        Connection conn = DriverManager.getConnection(
                "jdbc:mysql://localhost:3306/mini_orm_demo" +
                "?createDatabaseIfNotExist=true" +
                "&useSSL=false" +
                "&serverTimezone=Asia/Shanghai" +
                "&characterEncoding=utf8",
                "root",
                "123456789"
        );
        System.out.println("✅ MySQL 连接成功！数据库：mini_orm_demo");
        System.out.println();

        // ══════════════════════════════════════════════
        //  第二步：建表（如果已存在则跳过，不会报错）
        // ══════════════════════════════════════════════
        Statement stmt = conn.createStatement();
        stmt.execute("""
                create table if not exists user (
                    id    int primary key,
                    name  varchar(50),
                    email varchar(100)
                )
                """);
        System.out.println("✅ 建表成功（或已存在）：user 表就绪");

        // 清空旧数据，避免重复运行时主键冲突
        stmt.execute("delete from user");
        System.out.println("✅ 旧数据已清空，准备写入新数据");
        System.out.println();

        // ══════════════════════════════════════════════
        //  第三步：创建 MiniMapper（类比 MyBatis 的 UserMapper）
        // ══════════════════════════════════════════════
        MiniMapper<User> userMapper = new MiniMapper<>(User.class, conn);

        // ══════════════════════════════════════════════
        //  第四步：INSERT —— 把 Java 对象插入 MySQL
        // ══════════════════════════════════════════════
        System.out.println("──────────────────────────────────────");
        System.out.println("【测试 insert】");

        User user1 = new User(1, "张三", "zhangsan@qq.com");
        User user2 = new User(2, "李四", "lisi@gmail.com");
        User user3 = new User(3, "王五", "wangwu@163.com");
        User user4 = new User(4, "赵六", "zhaoliu@outlook.com");
        User user5 = new User(5, "孙七", "sunqi@hotmail.com");

        userMapper.insert(user1);
        userMapper.insert(user2);
        userMapper.insert(user3);
        userMapper.insert(user4);
        userMapper.insert(user5);

        System.out.println();

        // ══════════════════════════════════════════════
        //  第五步：FIND BY ID —— 从 MySQL 查询，自动装回对象
        // ══════════════════════════════════════════════
        System.out.println("──────────────────────────────────────");
        System.out.println("【测试 findById】");

        User found1 = userMapper.findById(1);
        System.out.println("  查询结果: " + found1);

        User found3 = userMapper.findById(3);
        System.out.println("  查询结果: " + found3);

        User found5 = userMapper.findById(5);
        System.out.println("  查询结果: " + found5);

        // 查一个不存在的 id
        User notFound = userMapper.findById(999);
        System.out.println("  不存在的记录: " + notFound);

        System.out.println();
        System.out.println("══════════════════════════════════════");
        System.out.println("🎉 Demo 结束！数据已持久化到 MySQL 的 mini_orm_demo 数据库中");
        System.out.println("核心原理：注解提供配置 → 反射读取配置和字段值 → JDBC 执行 SQL");

        conn.close();
    }
}
