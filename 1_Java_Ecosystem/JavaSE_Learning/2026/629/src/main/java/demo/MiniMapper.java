package demo;

import java.lang.reflect.Field;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class MiniMapper<T> {

    private final Class<T> clazz;
    private final Connection conn;

    public MiniMapper(Class<T> clazz, Connection conn) {
        this.clazz = clazz;
        this.conn = conn;
    }

    // ══════════════════════════════════════════════════════
    //  INSERT：你自己写的，已完善空格和泛型
    // ══════════════════════════════════════════════════════
    public boolean insert(Object obj) {
        try {
            Table table = clazz.getDeclaredAnnotation(Table.class);
            String tableName = table.value();

            Field[] fields = clazz.getDeclaredFields();
            List<String> columns = new ArrayList<>();
            List<Object> values  = new ArrayList<>();

            for (Field e : fields) {
                if (e.isAnnotationPresent(Column.class)) {
                    e.setAccessible(true);
                    String column = e.getAnnotation(Column.class).value();
                    Object val    = e.get(obj);
                    columns.add(column);
                    values.add(val);
                }
            }

            String columPart   = String.join(", ", columns);
            String placeholder = "?, ".repeat(columns.size());
            placeholder = placeholder.substring(0, placeholder.length() - 2);

            // 修复：values 前后加空格，防止 SQL 语法错误
            String sql = "insert into " + tableName
                       + " (" + columPart + ")"
                       + " values (" + placeholder + ")";

            System.out.println("  [insert] SQL → " + sql);

            PreparedStatement ps = conn.prepareStatement(sql);
            for (int i = 0; i < values.size(); i++) {
                ps.setObject(i + 1, values.get(i));
            }
            ps.executeUpdate();

        } catch (IllegalAccessException e) {
            System.out.println(e.getMessage());
            return false;
        } catch (SQLException e) {
            System.out.println(e.getMessage());
            return false;
        }
        return true;
    }

    // ══════════════════════════════════════════════════════
    //  FIND BY ID：根据主键查一条记录，返回实体对象
    // ══════════════════════════════════════════════════════
    public T findById(Object idValue) {
        try {
            // ① 从 @Table 拿表名
            String tableName = clazz.getDeclaredAnnotation(Table.class).value();

            // ② 找到标了 @Id 的字段，拿主键列名
            String idColumnName = null;
            for (Field field : clazz.getDeclaredFields()) {
                if (field.isAnnotationPresent(Id.class)) {
                    idColumnName = field.getAnnotation(Column.class).value();
                    break;
                }
            }
            if (idColumnName == null) {
                throw new RuntimeException("实体类里没有找到 @Id 注解！");
            }

            // ③ 拼 SQL → select * from user where id = ?
            String sql = "select * from " + tableName + " where " + idColumnName + " = ?";
            System.out.println("  [findById] SQL → " + sql);

            // ④ JDBC 执行，拿到 ResultSet
            PreparedStatement ps = conn.prepareStatement(sql);
            ps.setObject(1, idValue);
            ResultSet rs = ps.executeQuery();

            // ⑤ 结果集为空，直接返回 null
            if (!rs.next()) {
                System.out.println("  [findById] 没有找到 id=" + idValue + " 的记录");
                return null;
            }

            // ⑥ 反射 new 一个空对象
            T result = clazz.getDeclaredConstructor().newInstance();

            // ⑦ 遍历字段，把 ResultSet 里的数据逐个塞回对象
            for (Field field : clazz.getDeclaredFields()) {
                if (!field.isAnnotationPresent(Column.class)) continue;

                field.setAccessible(true);
                String colName = field.getAnnotation(Column.class).value();
                Object value   = rs.getObject(colName); // 按列名从结果集取值
                field.set(result, value);               // 反射塞进对象
            }

            return result;

        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    // ══════════════════════════════════════════════════════
    //  FIND ALL：查询全表，返回实体对象列表
    // ══════════════════════════════════════════════════════
    public List<T> findAll() {
        List<T> resultList = new ArrayList<>();
        try {
            // ① 从 @Table 拿表名
            String tableName = clazz.getDeclaredAnnotation(Table.class).value();

            // ② 拼 SQL → select * from user
            String sql = "select * from " + tableName;
            System.out.println("  [findAll] SQL → " + sql);

            // ③ JDBC 执行
            PreparedStatement ps = conn.prepareStatement(sql);
            ResultSet rs = ps.executeQuery();

            // ④ 循环遍历结果集，每一行都装回一个对象
            while (rs.next()) {  // rs.next() 每次移动到下一行，没有数据时退出循环
                // 反射 new 一个空对象
                T obj = clazz.getDeclaredConstructor().newInstance();

                // 把这一行的每一列数据塞进对象
                for (Field field : clazz.getDeclaredFields()) {
                    if (!field.isAnnotationPresent(Column.class)) continue;

                    field.setAccessible(true);
                    String colName = field.getAnnotation(Column.class).value();
                    Object value   = rs.getObject(colName);
                    field.set(obj, value);
                }

                resultList.add(obj); // 把装好数据的对象加入列表
            }

        } catch (Exception e) {
            throw new RuntimeException(e);
        }
        return resultList;
    }
}