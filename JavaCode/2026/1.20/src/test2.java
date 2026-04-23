public class test2 {
    public static void main(String[] args) {
        //继承可以使用子类没有父类有的方法
        Cat cat = new Cat();
        cat.eat();
        //也可以使用父类的字段
        cat.age();
        Animal animal = new Animal();
        Cat cat1 = (Cat)animal;


    }
}


class Animal{
    String name;
    int age = 20;
    public void eat(){
        System.out.println("吃饭");
    }
}
class Cat extends Animal{
    int age;
    public void age(){
        System.out.println(age);
    }
    public void eat(){
    super.eat();
    }
}

class Dog{

}
