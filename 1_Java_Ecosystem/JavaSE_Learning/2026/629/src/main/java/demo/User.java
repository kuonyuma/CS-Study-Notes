package demo;

/**
 * 实体类 —— 对应数据库的 user 表
 *
 * 注解的作用：
 *   @Table("user")   → 告诉框架：这个类映射到名为 "user" 的表
 *   @Id              → 告诉框架：这个字段是主键
 *   @Column("xxx")   → 告诉框架：这个字段映射到名为 "xxx" 的列
 */
@Table("user")
public class User {

    @Id
    @Column("id")
    private int id;

    @Column("name")
    private String name;

    @Column("email")
    private String email;

    // 无参构造器（反射创建对象时必须有）
    public User() {}

    public User(int id, String name, String email) {
        this.id = id;
        this.name = name;
        this.email = email;
    }

    // ---- getter / setter ----

    public int getId() { return id; }
    public void setId(int id) { this.id = id; }

    public String getName() { return name; }
    public void setName(String name) { this.name = name; }

    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }

    @Override
    public String toString() {
        return "User{id=" + id + ", name='" + name + "', email='" + email + "'}";
    }
}
