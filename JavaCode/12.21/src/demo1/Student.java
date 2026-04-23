package demo1;

public class Student implements Comparable <Student> {

    public int age;//学生年龄
    public String name;

    public Student(int age,String name){
        this.age = age;
        this.name = name;
    }
    @Override
    public int compareTo(Student other){
        return this.age - other.age;
    }
    @Override
    public String toString(){
        return "Student{ age = "+ age +", name "+ name + "}";
    }




}
