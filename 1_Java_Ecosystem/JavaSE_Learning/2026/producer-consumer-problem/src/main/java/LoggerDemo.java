import java.util.Random;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.ThreadLocalRandom;

public class LoggerDemo {

    private static final Integer CORE_THREAD = 5;//生产者数量
    private static final Integer CONSUMER_COUNT = 1;// 消费者数量
    private static final Integer LOG_COUNT  = 10;// 单位生产者生产日志的数量


    static void main() throws InterruptedException {
        ExecutorService pool =Executors.newFixedThreadPool(CORE_THREAD);
        AsyncLogger asyncLogger = new AsyncLogger(10);
        LogWriter writer = new LogWriter(
            asyncLogger.getQueue(),
            "消费者",
            "app.log");
        writer.start();

        CountDownLatch latch = new CountDownLatch(CORE_THREAD);

        for (int i = 0; i < CORE_THREAD; i++) {
            final int id = i;
            pool.submit(()->{
                try{

                    for (int j = 0; j < LOG_COUNT; j++) {
                        String msg = "生产线程:" + id +"第"+j+"个日志";

                        int rand = ThreadLocalRandom.current().nextInt(3);
                        if(rand == 1) asyncLogger.error(msg);
                        else if(rand == 2) asyncLogger.info(msg);
                        else asyncLogger.warn(msg);

                        Thread.sleep(ThreadLocalRandom.current().nextInt(10, 50));
                    }
                }catch (InterruptedException e) {
                    Thread.currentThread().interrupt();
                }finally{
                    latch.countDown();
                }
            });
        }

        latch.await();
        asyncLogger.shutdown(CORE_THREAD);
        writer.join();

        pool.shutdown();
        System.out.println("test_done");

    }
}