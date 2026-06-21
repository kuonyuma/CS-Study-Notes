package demo5;

public class Master {
    public static void main(String[] args)
            throws CloneNotSupportedException{
        Student stu1 = new Student(100);
        stu1.addr = new Addr();//这里是给这个引用变量在堆区开一块内存
        stu1.addr.addr = "kuonyuma";//（stu1.addr）是一个引用，类型是Addr

        Student stu2 = (Student)stu1.clone();
        stu2.addr = (Addr)stu1.addr.clone();

        stu2.age = 10;
        stu2.addr.addr ="liuxie";

        System.out.println("stu1.age= "+stu1.age+" " +"addr= "+stu1.addr.addr );
        System.out.println("stu2.age= "+stu2.age+" " +"addr= "+stu2.addr.addr );
    }
}
