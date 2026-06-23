package Entity;

import java.util.Optional;

public class User {
    private String name;
    private Integer age;
    private Car car; // 1. 将类型从 String 改为 Car

    public User(String name, Integer age) {
        this.age = age;
        this.name = name;
    }

    // 2. 构造函数入参改为 Car
    public User(Car car) {
        this.car = car;
    }

    public User() {}

    public String getName() {
        return name;
    }

    // 3. 使用 ofNullable，因为车可能是 null
    public Optional<Car> getCar() {
        return Optional.ofNullable(car);
    }

    // 4. Setter 入参改为 Car
    public void setCar(Car car) {
        this.car = car;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Integer getAge() {
        return age;
    }

    public void setAge(Integer age) {
        this.age = age;
    }

    @Override
    public String toString() {
        return "User{" +
            "name='" + name + '\'' +
            ", age=" + age +
            '}';
    }
}

