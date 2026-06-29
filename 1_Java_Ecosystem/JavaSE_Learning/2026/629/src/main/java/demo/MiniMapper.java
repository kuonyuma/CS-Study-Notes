package demo;

import java.lang.reflect.Field;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

/**
 * 核心引擎：MiniMapper
 *
 * 原理：
 *   1. 用反射读取实体类上的 @Table 注解 → 知道表名
 *   2. 用反射遍历所有字段，读取 @Column 注解 → 知道列名
 *   3. 用反射读取字段的实际值 → 知道要插入什么数据
 *   4. 自动拼 SQL，用 JDBC 执行
 *
 * @param <T> 实体类类型，例如 User
 */
public class MiniMapper<T> {

    private final Class<T> clazz;
    private final Connection connection;

    public MiniMapper(Class<T> clazz, Connection connection) {
        this.clazz = clazz;
        this.connection = connection;
    }

    // ══════════════════════════════════════════════════════
    //  INSERT：把一个 Java 对象插入数据库
    // ══════════════════════════════════════════════════════
    public void insert(T obj) throws Exception {
        // ① 从 @Table 注解读出表名
        Table tableAnno = clazz.getAnnotation(Table.class);
        String tableName = tableAnno.value();          // → "user"

        // ② 遍历所有字段，收集 列名 和 对应的值
        Field[] fields = clazz.getDeclaredFields();
        List<String> columns = new ArrayList<>();
        List<Object> values  = new ArrayList<>();

        for (Field field : fields) {
            // 跳过没有 @Column 注解的字段（框架不管它）
            if (!field.isAnnotationPresent(Column.class)) {
                continue;
            }

            field.setAccessible(true);   // 突破 private 访问限制

            String columnName = field.getAnnotation(Column.class).value();
            Object value      = field.get(obj);   // 反射：读取这个字段在 obj 对象里的值

            columns.add(columnName);
            values.add(value);
        }

        // ③ 拼 SQL，用 ? 作占位符（防 SQL 注入）
        // → "insert into user (id, name, email) values (?, ?, ?)"
        String columnPart   = String.join(", ", columns);
        String placeholder  = "?, ".repeat(columns.size());
        placeholder = placeholder.substring(0, placeholder.length() - 2); // 去掉末尾 ", "

        String sql = "insert into " + tableName
                   + " (" + columnPart + ")"
                   + " values (" + placeholder + ")";

        System.out.println("  [MiniMapper] 生成 SQL → " + sql);

        // ④ 用 JDBC 的 PreparedStatement 执行 SQL
        PreparedStatement ps = connection.prepareStatement(sql);
        for (int i = 0; i < values.size(); i++) {
            ps.setObject(i + 1, values.get(i));  // 依次填充每个 ?
        }
        ps.executeUpdate();
        System.out.println("  [MiniMapper] insert 执行成功 ✓");
    }

    // ══════════════════════════════════════════════════════
    //  FIND BY ID：根据主键查询，把结果装回 Java 对象
    // ══════════════════════════════════════════════════════
    public T findById(Object idValue) throws Exception {
        // ① 读出表名
        Table tableAnno = clazz.getAnnotation(Table.class);
        String tableName = tableAnno.value();

        // ② 找到标有 @Id 的字段，获取主键的列名
        String idColumnName = null;
        for (Field field : clazz.getDeclaredFields()) {
            if (field.isAnnotationPresent(Id.class)) {
                idColumnName = field.getAnnotation(Column.class).value();
                break;
            }
        }
        if (idColumnName == null) {
            throw new RuntimeException("实体类没有找到 @Id 注解！");
        }

        // ③ 拼 SQL → "select * from user where id = ?"
        String sql = "select * from " + tableName + " where " + idColumnName + " = ?";
        System.out.println("  [MiniMapper] 生成 SQL → " + sql);

        // ④ 执行查询，拿到 ResultSet（结果集）
        PreparedStatement ps = connection.prepareStatement(sql);
        ps.setObject(1, idValue);
        ResultSet rs = ps.executeQuery();

        if (!rs.next()) {
            System.out.println("  [MiniMapper] 没有找到 id=" + idValue + " 的记录");
            return null;
        }

        // ⑤ 反射：创建一个空的实体对象
        T result = clazz.getDeclaredConstructor().newInstance();

        // ⑥ 遍历字段，把 ResultSet 里的数据逐个塞进对象
        for (Field field : clazz.getDeclaredFields()) {
            if (!field.isAnnotationPresent(Column.class)) {
                continue;
            }

            field.setAccessible(true);
            String columnName = field.getAnnotation(Column.class).value();
            Object value = rs.getObject(columnName);   // 按列名从结果集取值
            field.set(result, value);                   // 反射：把值塞进字段
        }

        System.out.println("  [MiniMapper] findById 执行成功 ✓");
        return result;
    }
}
