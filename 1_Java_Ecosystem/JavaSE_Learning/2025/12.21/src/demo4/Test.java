package demo4;

class Money implements Cloneable{
    public double Money;
    public Money(double m){
        this.Money = m;
    }
    @Override
    public Object clone()
            throws CloneNotSupportedException {
        return super.clone();
    }
}
public class Test implements Cloneable {
    public int a;
    public Test(int num){
        this.a = num;
    }
    Money m1 = new Money(10);
    @Override
    public Object clone()
            throws CloneNotSupportedException{
        Test tmp = (Test)super.clone();
        tmp.m1 = (Money)this.m1.clone();
        return tmp;

    }

}
