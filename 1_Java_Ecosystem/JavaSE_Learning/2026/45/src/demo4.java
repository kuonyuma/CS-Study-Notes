public class demo4 {

    private static int count = 0;

    public static void main(String[] args) throws InterruptedException {

        Object locker = new Object();

        Thread t1 = new Thread(()->{
            System.out.println("我是折纸");
            for(int i = 0; i< 50000;i++){
                synchronized(locker) {
                    count++;
                }
            }
        },"折纸");

        Thread t2 = new Thread(()->{
            System.out.println("我是三叶");
            for(int i = 0; i< 50000;i++){
                synchronized(locker) {
                    count++;
                }
            }
        },"三叶");

        t1.start();
        t2.start();

        t2.join();
        t1.join();
        System.out.println("count = " + count);

        System.out.println("main线程 结束");

    }
}
