package demo2;

public class Duck extends Animal implements IFly, Iswim{
    public void eat(){
        System.out.println("鸭子吃粮食");
    }
    public void fly(){
        System.out.println("鸭子飞翔");
    }
    public void swim(){
        System.out.println("鸭子游泳");
    }
}
