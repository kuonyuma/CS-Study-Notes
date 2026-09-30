package demo5.db;

abstract public class Animal {
    String name;

    public Animal(String name){
        this.name = name;
    }
    abstract public void eat();
    public void sleep(){
        System.out.println("animal is sleeping");
    }
}
