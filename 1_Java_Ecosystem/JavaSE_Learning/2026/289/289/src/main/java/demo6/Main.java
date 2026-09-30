package demo6;

import demo6.db.Alipay;
import demo6.db.Payment;
import demo6.db.Wxpay;

public class Main{
    public static void main(String[] args) {
        check(new Wxpay());
        check(new Alipay());
    }

    public static void check(Payment payment){
        payment.act();
    }
}
