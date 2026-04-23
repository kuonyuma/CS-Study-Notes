public class demo5 {

    static volatile int x = 0, y = 0;
    static volatile int a = 0, b = 0;

    public static void main(String[] args) throws InterruptedException {
        int count = 0;

        while (true) {
            count++;

            x = y = a = b = 0;

            Thread t1 = new Thread(() -> {
                a = 1;          // ①
                x = b;          // ②
            });

            Thread t2 = new Thread(() -> {
                b = 1;          // ③
                y = a;          // ④
            });

            t1.start();
            t2.start();
            t1.join();
            t2.join();

            if (x == 0 && y == 0) {
                System.out.println("第 " + count + " 次出现 (0,0)！");
                break;
            }
        }
    }
}
