package Demo1;

public class Cat extends Animal{
    public int a =2;



    public void mimi(){
        System.out.println(this.name+"正在猫叫");
    }

    public void show(){
        Animal ani =new Animal();
        ani.a =10;
        ani.name ="小黑";
        ani.eat();
        System.out.println(a);
    }
}
