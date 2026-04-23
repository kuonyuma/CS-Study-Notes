package demo3;

import java.util.Comparator;

public class Student implements Comparable<Student> {
    int age;
    String name;
    public Student(int gae, String gname) {
         this.age = gae;
         this.name = gname;
    }
    public void study() {
        System.out.println("学习中...");
    }
    @Override
    public boolean equals(Object obj) {
        //判断obj是否是Student类型
        if(obj == null){
            return false;
        }
        if(obj == this){
            return true;
        }
        if (obj instanceof Student) {
            Student stu = (Student) obj;
            //比较两个对象的属性值是否相等
            return this.age == stu.age && this.name.equals(stu.name);
        }else{
            return false;
        }
    }
    @Override
    public int compareTo(Student stu) {
           return stu.age - this.age;
    }
}
