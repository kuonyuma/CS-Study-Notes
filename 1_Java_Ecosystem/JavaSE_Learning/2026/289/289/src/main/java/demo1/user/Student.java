package demo1.user;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class Student implements Cloneable {
    // 字段
    public String name;
    private int age;
    public List<Integer> list;

    // 构造方法
    public Student() {
    }

    public Student(String name,int age) {
        this.name = name;
        this.age = age;

    }

    public List<Integer> getList() {
        return list;
    }

    public void setList(List<Integer> list) {
        this.list = list;
    }

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

    @Override
    public String toString() {
        return "Student{" +
            "name='" + name + '\'' +
            ", age=" + age +
            '}';
    }

    @Override
    public boolean equals(Object o) {
        //return super.equals(o);
        if (o == null || getClass() != o.getClass()) return false;
        Student student = (Student) o;
        return age == student.age && Objects.equals(name, student.name);
    }

    @Override
    public int hashCode() {
        return Objects.hash(name, age);
    }

    @Override
    public Student clone() throws CloneNotSupportedException {
        // 先复制 Student 对象，此时 list 仍然指向原来的列表。
        Student copy = (Student) super.clone();
        // 再创建独立的列表；Integer 不可变，元素可以安全共享。
        copy.list = this.list == null ? null : new ArrayList<>(this.list);
        return copy;
    }
}
