package Trhead;

public class demo1 {

    public static void main(String[] args) {

        Runnable runnable = new Runnable() {
            @Override
            public void run() {
                System.out.println("你好");
            }
        };

        Runnable run2 = () -> {
            System.out.println("你好");
        };

        Thread t1 = new Thread(runnable);
        t1.start();

        Thread t2 = new Thread(runnable);
        t2.start();


        Thread t3 = new Thread(()->{
            System.out.println("你好");
        },"t3");
        t3.start();




    }
}
