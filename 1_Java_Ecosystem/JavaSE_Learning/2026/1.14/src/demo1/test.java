package demo1;
 interface Base {
    int a = 10;
    int b = 20;
    abstract public void sleep();
}
class sum implements Base{
    int b = 40;
    int c = 50;

    public void print(){
        System.out.println(a+" "+b+" "+c+"儿子的打印函数生效了");
    }
    public void sleep(){
        System.out.println("睡觉");
    }

}
public class test {
    public static void main(String[] args) {
        sum sum = new sum();
        sum.print();
    }

}
