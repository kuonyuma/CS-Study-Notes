package demo2;

public class Test {
    public static void main(String[] args) {

        StaticClass.Inner inner = new StaticClass.Inner(100);
        inner.printf();
        StaticClass.Inner inner1 = new StaticClass.Inner(1000);

    }

    public static void main1(String[] args){
        TestClass test = new TestClass(10,"liuxie");
         TestClass.Heart heart = test.new Heart(1000);
         heart.print();
         heart.print1();

         TestClass testClass1 =new TestClass(1000,"kuonyuma");
         TestClass.Heart heart1 = testClass1.new Heart(1);


         TestClass aa = new TestClass(1000,"dwa");
         TestClass.Heart  he = aa.new Heart(11);

    }
}
