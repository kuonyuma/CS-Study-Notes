public class demo6 {

    public static void main(String[] args) throws RuntimeException{
        Object ob = new Object();
        Thread t1 = new Thread(() -> {
            synchronized (ob) {
                System.out.println("t1线程获取锁ob");
                try {
                    Thread.sleep(1000);
                    System.out.println("t1 由于wait释放锁");
                    Thread.sleep(1000);

                    ob.wait();
                    System.out.println("t1 被notify唤醒");
                } catch (InterruptedException e) {
                    throw new RuntimeException(e);
                }

            }
        });

        Thread t2 = new Thread(() -> {
            try {
                Thread.sleep(1000);
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }
            synchronized(ob){
                System.out.println("t2 拿到锁");
                try {
                    System.out.println("t2 5秒后启动notify");
                    Thread.sleep(5000);
                } catch (InterruptedException e) {
                    throw new RuntimeException(e);
                }

                ob.notify();
                try {
                    Thread.sleep(5000);
                } catch (InterruptedException e) {
                    throw new RuntimeException(e);
                }
                System.out.println("t2 线程释放锁");
            }
        });

        t1.start();
        t2.start();

    }
}