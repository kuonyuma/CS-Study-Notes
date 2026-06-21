package demo2;

public class Test {
    public static void testAnimal(Animal a){
        a.eat();
    }
    public  static void flyAnimal(IFly f){
        f.fly();
    }
    public static void swimAnimal(Iswim a){
        a.swim();
    }

    public static void main(String[] args) {
        Animal a1 = new Dog("旺财",3);
        Animal a2 = new Dog("旺财",3);
        System.out.println(a1 == a2);
        System.out.println(a1.equals(a2));
        System.out.println(a1.toString());

    }

}
