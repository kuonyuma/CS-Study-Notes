// 示例用户类 - 用于反射操作
public class User {
    private String name;
    private int age;
    private String email;

    public User() {
    }

    public User(String name, int age) {
        this.name = name;
        this.age = age;
    }

    // 私有方法 - 演示反射突破访问控制
    private void secretMethod() {
        System.out.println("🔒 这是私有方法! 正常情况调用不了,但反射可以!");
    }

    public void sayHello() {
        System.out.println("Hello, 我是 " + name + ", 今年 " + age + " 岁");
    }

    // Getter/Setter
    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getAge() {
        return age;
    }

    public void setAge(int age) {
        this.age = age;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    @Override
    public String toString() {
        return "User{name='" + name + "', age=" + age + ", email='" + email + "'}";
    }
}
