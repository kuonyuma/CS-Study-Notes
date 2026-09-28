package demo2.db;

public class Animal {

    protected String name;

    public Animal(){
        System.out.println("父类构造方法开始执行");
        System.out.println("父类构造方法执行完毕");
    }
    public Animal(String name){
        System.out.println("父类构造方法开始执行");
        this.name = name;
        System.out.println("父类构造方法执行完毕");
    }

    public void eat(){
        System.out.println(this.name+"正在吃东西");
    }
}
