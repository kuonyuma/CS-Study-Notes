public class demo1 {
    public static void main(String[] args) throws InterruptedException {
        Thread t1 = new Thread(() -> {
            for (int i = 0; i < 5; i++) {
                try{
                    Thread.sleep(1000);
                } catch (InterruptedException e) {
                    throw new RuntimeException(e);
                }
                System.out.println("Thread 1: " + i);
            }
        });
        t1.start();
        t1.join();

        System.out.println("线程t1已完成，继续执行主线程");
    }
}
