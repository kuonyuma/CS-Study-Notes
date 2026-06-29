package demo;

import java.lang.annotation.*;

/**
 * 标记主键字段
 * 需要和 @Column 配合使用
 */
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.FIELD)
public @interface Id {
}
