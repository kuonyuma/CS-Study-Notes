package demo4;

public class Master{
    public static void Swap(Test a ,Test b){//利用类进行数值上的交换
       int tmp = a.a;
       a.a = b.a;
       b.a = tmp;
    }

    //问题1：我现在想t1把这个类的数据在复制一份
    //克隆接口clone
    public static void main (String[] args)
            throws CloneNotSupportedException{
        Test t1 = new Test(10);
        Test t2 = (Test)t1.clone();
        t1.m1.Money = 100;
        System.out.println(t1.a+" "+ t1.m1.Money );
        System.out.println(t2.a+" "+ t2.m1.Money );

    }

    public static void main1(String[] args) {
        Test t1 = new Test(100);
        Test t2 = new Test(50);
        System.out.println("交换前");
        System.out.println(t1.a+ " "+ t2.a);
        Swap(t1,t2);
        System.out.println("交换后");
        System.out.println(t1.a+" "+ t2.a);
    }

}
