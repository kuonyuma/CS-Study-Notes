import java.util.ArrayList;
import java.util.List;

public class ArrayListMultiThread {
    public static List<Integer> al = new ArrayList<>();

    public static class AddThread implements Runnable {
        @Override
        public void run() {
            for (int i = 0; i < 10000; i++) {
                al.add(i);
            }
        }
    }

    public static void main(String[] args) throws InterruptedException {
        Thread t1 = new Thread(new AddThread());
        Thread t2 = new Thread(new AddThread());
        t1.start();
        t2.start();
        t1.join();
        t2.join();
        // 很大可能输出小于 20000 的值，或者直接抛出越界异常
        System.out.println("最终大小: " + al.size());
    }
}