import java.util.concurrent.*;
import java.util.concurrent.atomic.AtomicInteger;

// ====================== 自定义线程工厂 ======================
class MyThreadFactory implements ThreadFactory {

    private final AtomicInteger count = new AtomicInteger(1);
    private final String namePrefix;

    public MyThreadFactory(String namePrefix) {
        this.namePrefix = namePrefix;
    }

    @Override
    public Thread newThread(Runnable r) {
        Thread t = new Thread(r);
        t.setName(namePrefix + "-" + count.getAndIncrement());
        t.setDaemon(false);
        t.setPriority(Thread.NORM_PRIORITY);
        return t;
    }
}

// ====================== Demo 主程序 ======================
public class demo4 {

    public static void main(String[] args) {

        // 1. 核心线程数
        int corePoolSize = 2;

        // 2. 最大线程数
        int maximumPoolSize = 10;

        // 3. 非核心线程最大空闲时间
        long keepAliveTime = 10;

        // 4. 时间单位
        TimeUnit unit = TimeUnit.SECONDS;

        // 5. 队列（容量 2）
        BlockingQueue<Runnable> workQueue = new ArrayBlockingQueue<>(4);

        // 6. 自定义线程工厂
        ThreadFactory threadFactory = new MyThreadFactory("worker");

        // 7. 拒绝策略（抛异常）
        RejectedExecutionHandler handler = new ThreadPoolExecutor.AbortPolicy();

        // ====================== 创建线程池 ======================
        ThreadPoolExecutor executor = new ThreadPoolExecutor(
                corePoolSize,
                maximumPoolSize,
                keepAliveTime,
                unit,
                workQueue,
                threadFactory,
                handler
        );

        // ====================== 提交任务 ======================
        for (int i = 1; i <= 10; i++) {
            int taskId = i;
            try {
                executor.execute(() -> {
                    System.out.println(Thread.currentThread().getName() +
                            " 正在执行任务 " + taskId);
                    try {
                        Thread.sleep(2000);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                });
            } catch (RejectedExecutionException e) {
                System.out.println("任务 " + taskId + " 被拒绝执行！");
            }
        }

        // 关闭线程池
        executor.shutdown();
    }
}
