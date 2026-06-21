package demo2;

public class StaticClass {
    private int a;
    private static int b;
    public StaticClass(int num1,int num2){
        this.a= num1;
        b = num2;
    }
    static class Inner{
        public Inner(int num){
            b =num;
        }
        public void printf(){
//            System.out.println( a);
            System.out.println(b);
        }

    }
}
