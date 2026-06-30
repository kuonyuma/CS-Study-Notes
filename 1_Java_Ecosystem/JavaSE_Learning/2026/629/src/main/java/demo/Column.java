package demo;

import java.lang.annotation.*;

/**
 * 标记字段对应的数据库列名
 * 用法：@Column("name")
 */
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.FIELD)
public @interface Column {
    String value();
}
