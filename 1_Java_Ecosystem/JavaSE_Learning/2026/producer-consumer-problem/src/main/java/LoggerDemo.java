import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.ThreadLocalRandom;

/**
 * 演示入口：模拟多个业务线程并发产生日志
 *
 * <p>场景模拟：
 * 假设我们有 5 个业务线程（模拟 5 个并发请求处理器），
 * 每个线程产生 10 条日志。
 * 1 个消费者线程负责把所有日志写入文件。
 *
 * <p>整体流程：
 *   1. 启动消费者线程（LogWriter）
 *   2. 启动生产者线程池，模拟业务日志产生
 *   3. 等待所有生产者完成（CountDownLatch）
 *   4. 发送毒丸，关闭消费者
 *   5. 等待消费者线程退出
 */
public class LoggerDemo {

    private static final int PRODUCER_COUNT = 5;   // 生产者（业务线程）数量
    private static final int LOGS_PER_PRODUCER = 10; // 每个生产者产生的日志数
    private static final int CONSUMER_COUNT = 1;   // 消费者（写文件线程）数量
    private static final String LOG_FILE = "app.log"; // 日志文件路径

    public static void main(String[] args) throws InterruptedException {

        // ===== 第一步：创建 AsyncLogger（队列容量 100）=====
        AsyncLogger logger = new AsyncLogger(100);

        // ===== 第二步：启动消费者线程 =====
        LogWriter logWriter = new LogWriter(logger.getQueue(), LOG_FILE, "log-writer-1");
        logWriter.start(); // 消费者线程开始在后台等待日志

        // ===== 第三步：用 CountDownLatch 等待所有生产者完成 =====
        // CountDownLatch(5)：计数器初始为 5，每个生产者完成后 countDown() 减 1
        // 主线程调用 await() 阻塞，等计数器归零（即所有生产者都完成）才继续
        CountDownLatch latch = new CountDownLatch(PRODUCER_COUNT);

        // ===== 第四步：创建生产者线程池，模拟业务线程并发写日志 =====
        ExecutorService producerPool = Executors.newFixedThreadPool(PRODUCER_COUNT);

        System.out.println("===== 开始模拟并发日志写入 =====\n");

        for (int i = 1; i <= PRODUCER_COUNT; i++) {
            final int producerId = i;
            producerPool.submit(() -> {
                try {
                    for (int j = 1; j <= LOGS_PER_PRODUCER; j++) {
                        String msg = String.format("业务线程 #%d 的第 %d 条日志", producerId, j);

                        // 随机选择日志级别，模拟真实业务
                        int rand = ThreadLocalRandom.current().nextInt(3);
                        if (rand == 0) logger.warn(msg);
                        else if (rand == 1) logger.error(msg);
                        else logger.info(msg);

                        // 模拟业务处理时间（随机 10~50 毫秒）
                        Thread.sleep(ThreadLocalRandom.current().nextInt(10, 50));
                    }
                    System.out.printf("[生产者 #%d] 完成，共产生 %d 条日志%n",
                            producerId, LOGS_PER_PRODUCER);
                } catch (InterruptedException e) {
                    Thread.currentThread().interrupt();
                } finally {
                    latch.countDown(); // 无论成功还是失败，都要 countDown
                }
            });
        }

        // ===== 第五步：等待所有生产者完成 =====
        latch.await(); // 主线程在此阻塞，直到 5 个生产者全部 countDown
        System.out.println("\n===== 所有生产者已完成，发送关闭信号 =====");

        // ===== 第六步：发送毒丸，通知消费者退出 =====
        logger.shutdown(CONSUMER_COUNT);

        // ===== 第七步：等待消费者线程完全退出 =====
        // join()：主线程等待 logWriter 线程执行完毕
        logWriter.join();

        // ===== 第八步：关闭生产者线程池 =====
        producerPool.shutdown();

        System.out.println("\n===== 系统关闭完成，日志已写入 " + LOG_FILE + " =====");
    }
}
