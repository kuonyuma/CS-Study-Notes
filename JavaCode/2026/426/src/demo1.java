

public class demo1 {
    int a = 10;

    public void FatherPrint(String str){
        System.out.println(str);
    }

    private class Sum{
        int a = 20;
        String b = "子类的b";
        public void print1(){
            System.out.println(demo1.this.a);
        }
    }
    public static void main(String[] args) {
        demo1.Sum sum = new demo1().new Sum();
        sum.print1();//输出20

        demo1 demo  = new demo1();
        demo1.Sum sum2 = demo.new Sum();

    }
}


