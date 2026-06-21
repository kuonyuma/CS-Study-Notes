package demo3;


import java.lang.Thread;

public class Test {



    public static void main(String[] args) {
        Runnable runnable = new Runnable() {
            @Override
            public void run() {
                System.out.println("你好");
            }
        };
        java.lang.Thread t1 = new Thread(runnable);
        t1.start();
    }


}
