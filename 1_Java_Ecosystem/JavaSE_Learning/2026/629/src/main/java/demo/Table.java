package demo;

import java.lang.annotation.*;

/**
 * 标记实体类对应的数据库表名
 * 用法：@Table("user")
 */
@Retention(RetentionPolicy.RUNTIME)  // 运行时保留，反射才能读到
@Target(ElementType.TYPE)            // 只能贴在类上
public @interface Table {
    String value();  // 表名
}
