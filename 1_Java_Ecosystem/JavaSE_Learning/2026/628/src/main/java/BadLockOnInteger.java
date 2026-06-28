public class BadLockOnInteger implements Runnable {
    // 共享资源：Integer 计数器
    public static Integer i = 0;

    @Override
    public void run() {
        for (int j = 0; j < 10000; j++) {
            // 新手直觉：既然要修改 i，那我就锁住 i
            synchronized (i) {
                i++;
            }
        }
    }

    public static void main(String[] args) throws InterruptedException {
        BadLockOnInteger task = new BadLockOnInteger();
        Thread t1 = new Thread(task);
        Thread t2 = new Thread(task);
        t1.start();
        t2.start();
        t1.join();
        t2.join();
        // 期望输出 20000，但实际输出会小于 20000
        System.out.println("最终计数结果: " + i);
    }
}