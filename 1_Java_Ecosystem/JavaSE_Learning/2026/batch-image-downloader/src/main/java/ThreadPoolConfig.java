import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.ThreadFactory;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;

public class ThreadPoolConfig{
    private static final Integer CORE_THREAD = 10;
    private static final Integer MAX_THREAD = 20;

    private static final Long KEEP_TIME = 60L;
    private static final TimeUnit timeUnit = TimeUnit.SECONDS;

    private static final Integer QUEUE_CAPACITY = 10;

    public static ThreadPoolExecutor creatDownLoadPool(){

        return new ThreadPoolExecutor(
            CORE_THREAD,
            MAX_THREAD,
            KEEP_TIME,
            timeUnit,
            new ArrayBlockingQueue<>(QUEUE_CAPACITY),
            new ThreadFactory(){
                private final AtomicInteger atomicInteger = new AtomicInteger(0);

                @Override
                public Thread newThread(Runnable r){

                    Thread t = new Thread(r);
                    t.setName("平台线程"+atomicInteger.incrementAndGet()+"号");
                    return t;
                }
            },
            new ThreadPoolExecutor.CallerRunsPolicy()
        );
    }
}