package demo3;

public class test2 {

    public static void main(String[] args) {
        Father father = new Father();
        father.test(20);

    }
}
class Father{

    public  void test(int age) {
        Runnable r = new Runnable() {
            @Override
            public void run() {
                System.out.println(age);
            }
        };
        r.run();
        System.out.println(age);
    }
}


