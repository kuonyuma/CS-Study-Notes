package demo2;

class Student{

    String name;
    public void run(){
        System.out.println(name);
    }

    static {
        System.out.println("静态代码块执行了");

    }
    public Student(String name){
        this.name = name;
        System.out.println("构造方法执行了");
    }
}

public class test {
    public static void main(String[] args) {
        Student s1 = new Student("张三");
        s1.run();

    }
}
