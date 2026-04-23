public class demo1 {

    public static void main(String[] args) throws InterruptedException {
       // 写一个死锁案例
        Object locker1 = new Object();
        Object locker2 = new Object();

        Thread t1 = new Thread(()->{
            synchronized(locker1){
                System.out.println("三叶_拿起第一把锁");
                try {
                    Thread.sleep(1000);
                } catch (InterruptedException e) {
                    throw new RuntimeException(e);
                }

                synchronized(locker2) {
                    System.out.println("三叶_拿起第二把锁");
                    try {
                        Thread.sleep(1000);
                    } catch (InterruptedException e) {
                        throw new RuntimeException(e);
                    }
                }
            }

        },"三叶");

        Thread t2 = new Thread(()->{
            synchronized(locker2){
                System.out.println("折纸_拿起第二把锁");
                try {
                    Thread.sleep(1000);
                } catch (InterruptedException e) {
                    throw new RuntimeException(e);
                }
                synchronized(locker1) {
                    System.out.println("折纸_拿起第二把锁");
                    try {
                        Thread.sleep(1000);
                    } catch (InterruptedException e) {
                        throw new RuntimeException(e);
                    }
                }
            }

        },"折纸");

        t1.start();
        t2.start();

        t1.join();
        t2.join();

        System.out.println("main 结束");
    }

}
