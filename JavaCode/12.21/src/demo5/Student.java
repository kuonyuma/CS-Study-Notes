package demo5;

class Addr implements Cloneable{
    public String addr;
    public Object clone()
            throws CloneNotSupportedException{
        return super.clone();
    }
}

public class Student implements Cloneable{
    public int age;

    Addr addr;//这里的addr其实是一个引用变量。类型是Addr

    public Student(int age){
        this.age = age;
    }
    //clone
    public Object clone()
            throws CloneNotSupportedException{
        Student newStudent = (Student)super.clone();
        newStudent.addr =(Addr)this.clone();
        return newStudent;
    }


}
