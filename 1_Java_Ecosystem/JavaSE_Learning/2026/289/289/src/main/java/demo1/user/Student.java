package demo1.user;

public class Student {
    // 字段
    public String name;
    public String email;
    public String id;
    private int age;
    private String gender;

    // 构造方法
    public Student(){}

    public Student(String name, String email, String id, int age, String gender) {
        this.name = name;
        this.email = email;
        this.id = id;
        this.age = age;
        this.gender = gender;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public int getAge() {
        return age;
    }

    public void setAge(int age) {
        this.age = age;
    }

    public String getGender() {
        return gender;
    }

    public void setGender(String gender) {
        this.gender = gender;
    }

    @Override
    public String toString() {
        return "Student{" +
            "name='" + name + '\'' +
            ", email='" + email + '\'' +
            ", id='" + id + '\'' +
            ", age=" + age +
            ", gender='" + gender + '\'' +
            '}';
    }
}
