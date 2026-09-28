package demo2.db;

public class Dog extends Animal{

    private int age;

    public Dog(){

        System.out.println("子类构造方法开始执行");
        System.out.println("子类构造方法执行完毕");

    }
    public Dog(String name,int age){
        super(name);
        System.out.println("子类构造方法开始执行");
        this.age = age;
        System.out.println("子类构造方法执行完毕");
    }
    @Override
    public void eat(){
        System.out.println(name+"正在高兴的吃");
    }
    public void show(){
        System.out.println(name+"已经"+age+"岁了");
    }
}
