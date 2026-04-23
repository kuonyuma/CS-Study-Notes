import java.util.Scanner;

public class demo3 {
    private static volatile boolean flag = false;
    public static void main(String[] args) throws InterruptedException {
        Thread t1 = new Thread(()->{
            System.out.println("t1 开始运行");
            while(!flag){
                ;
            }
            System.out.println("flag 已经被修改");
        },"折纸");
        Thread t2 = new Thread(()->{
            Scanner in = new Scanner(System.in);
            flag = in.nextBoolean();
            System.out.println("t2 已完成修改");
        },"三叶");
        t1.start();
        t2.start();
        t1.join();
        t2.join();
        System.out.println("main 已经结束");
    }
}
