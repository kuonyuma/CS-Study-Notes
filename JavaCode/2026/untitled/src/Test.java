import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.ThreadFactory;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;

public class Test {
    public static void main() {
        int ch = '中';
        System.out.println(ch);
        char ch1 = '中';
        System.out.println(ch1);
        String ch2 = "中";
        System.out.println(ch2);
    }
    public static void main1(String[] args) throws InterruptedException {
//        Thread t1 = new Thread(()->{
//            System.out.println("t1 线程");
//        });
//
//        t1.start();
        MyThreadPool pool = new MyThreadPool(2);
        for (int i = 1; i <= 10; i++) {
            final int taskId = i;
            pool.submit(() -> {
                String threadName = Thread.currentThread().getName();
                System.out.println("任务 " + taskId + " 正在执行，线程：" + threadName);
                try {
                    Thread.sleep(1000);
                } catch (InterruptedException e) {
                    Thread.currentThread().interrupt();
                }
            });
        }

    }


    public static void main3(String[] args) {


        BlockingQueue<Runnable> queue = new ArrayBlockingQueue<>(2);
        AtomicInteger threadIndex = new AtomicInteger(1);

        ThreadFactory threadFactory = runnable -> {
            Thread thread = new Thread(runnable);
            thread.setName("demo-pool-" + threadIndex.getAndIncrement());
            return thread;
        };

        ThreadPoolExecutor t1 = new ThreadPoolExecutor(
                2,
                4,
                60L,
                TimeUnit.SECONDS,
                queue,
                threadFactory,
                new ThreadPoolExecutor.CallerRunsPolicy()
        );

        for (int i = 1; i <= 6; i++) {
            final int taskId = i;
            t1.execute(() -> {
                String threadName = Thread.currentThread().getName();
                System.out.println("任务 " + taskId + " 开始执行，所在线程：" + threadName);
                try {
                    TimeUnit.SECONDS.sleep(3);
                } catch (InterruptedException e) {
                    Thread.currentThread().interrupt();
                    System.out.println("任务 " + taskId + " 被中断");
                    return;
                }

            });
        }
    }

}

