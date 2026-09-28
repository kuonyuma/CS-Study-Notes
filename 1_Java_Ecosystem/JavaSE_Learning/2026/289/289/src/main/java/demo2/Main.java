package demo2;

import demo2.db.Dog;

public class Main {
    public static void main1(String[] args) {
        Dog bean1 = new Dog("小狗",12);
        bean1.show();
        bean1.eat();
    }

    public static void main(String[] args) {
        Dog dog = new Dog();
    }
}
