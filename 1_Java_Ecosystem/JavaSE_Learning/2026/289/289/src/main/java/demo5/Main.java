package demo5;

import demo5.db.Animal;
import demo5.db.Dog;

public class Main {

    public static void main(String[] args) {

        Animal animal = new Dog("小狗");
        animal.eat();
        animal.sleep();
    }
}
