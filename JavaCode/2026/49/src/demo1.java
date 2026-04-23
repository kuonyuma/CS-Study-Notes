public class demo1 {
    private static int count;
    public static void main(String[] args) throws InterruptedException {

        Object locker = new Object();


        Thread t1 = new Thread(()->{
            System.out.println("折纸开始工作");
            for(int i = 0 ; i < 500000;i++){
                add();
            }
        },"折纸");

        Thread t2 = new Thread(()->{
            System.out.println("三叶开始工作");
            for(int i = 0 ; i < 500000;i++){
                add();
            }
        },"三叶");

        t1.start();
        t2.start();

        t1.join();
        t2.join();

        System.out.println("count = " + count);
    }

    public static void add(){
        synchronized(demo1.class){
            count++;
        }
    }


}

class MyThread extends Thread{
    @Override
    public void run(){
        System.out.println("test");
    }
}

