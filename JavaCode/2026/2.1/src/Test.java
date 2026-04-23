import java.util.Objects;

public class Test {
    public static void tset1(String[] args) {
        MyHashBucket<String,Integer> test = new MyHashBucket<>();
        test.offer("xiaoming",1);
        test.offer("lisi",2);
        test.offer("zhangsan",5);
        test.offer("wangwu",4);
        System.out.println(test.get("xiaoming"));
    }

    public static void test2(){
        Student stu = new Student(111);
        MyHashBucket<Student,String> test = new MyHashBucket<>();
        test.offer(new Student(11),"一花");
        test.offer(new Student(22),"二乃");
        test.offer(new Student(33),"三叶");
        System.out.println(test.get(new Student(11)));
    }

    public static void main(String[] args) {
        test2();
    }
}
class Student{
    int id;
    public Student(int id){
        this.id = id;
    }
    @Override
    public boolean equals(Object o) {
        if (o == null || getClass() != o.getClass()) return false;
        Student student = (Student) o;
        return id == student.id;
    }
    @Override
    public int hashCode() {
        return Objects.hashCode(id);
    }
}

