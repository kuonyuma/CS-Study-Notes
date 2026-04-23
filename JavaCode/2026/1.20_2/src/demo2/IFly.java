package demo2;

public interface IFly {

    default void test(){
        System.out.println("接口中可以有default修饰的方法");
    }
    void fly();
}
