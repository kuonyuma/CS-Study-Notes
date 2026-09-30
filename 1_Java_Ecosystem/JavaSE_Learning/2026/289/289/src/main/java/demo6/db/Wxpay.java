package demo6.db;

public class Wxpay implements Payment{
    @Override
    public void act(){
        System.out.println("WxPay finished");
    }
}
