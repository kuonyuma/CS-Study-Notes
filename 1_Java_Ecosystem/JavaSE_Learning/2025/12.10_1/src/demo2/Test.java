package demo2;

public class Test {

    public static void test2(Father f){
        f.pay();
        f.Sleep();
    }
    public static void main(String[] args) {
//        test2(new Son2());
//        test2(new Son());
        Father f = new Son2();
        f.pay();
        Father f1 = new Son();
        f1.pay();


    }
}
