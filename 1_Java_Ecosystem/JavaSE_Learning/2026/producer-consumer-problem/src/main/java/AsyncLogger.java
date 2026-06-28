import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;

/**
 * 异步日志记录器（生产者入口）
 *
 * <p>职责：提供给业务代码调用的日志 API。
 * 业务线程调用 info()/warn()/error() 时，只是把消息放入队列就立刻返回，
 * 不会等待消息真正被写入文件。这就是"异步"的含义。
 *
 * <p>为什么不直接在业务线程里写文件？
 * 文件 I/O 很慢（相对于内存操作慢几百倍），如果每条日志都同步写文件，
 * 业务线程会被 I/O 拖慢，用户请求响应变慢。
 * 用队列解耦：业务线程只管把消息放进队列（极快），I/O 交给专门的消费者线程。
 *
 * <p>这个类是整个系统的"前台"，LogWriter 是"后台"。
 */
public class AsyncLogger {

    /**
     * 消息队列：连接生产者和消费者的桥梁。
     *
     * <p>为什么用 BlockingQueue 接口而不是 ArrayBlockingQueue？
     * 面向接口编程：将来想换成 LinkedBlockingQueue 或其他实现，
     * 只需改这一行，调用方代码完全不用动。
     */

    /**
     * 该类只有一个类型为阻塞队列的字段
     * 构造方法的作用是创建一个大小为capacity的有界数组队列
     * 并将这个阻塞队列通过getter方法暴漏了出去。
     * 有三个等级不同的写入日志的接口，一个私有的实现类。
     * 一个终止消费线程的方法。
     */
    private final BlockingQueue<LogMessage> queue;

    /**
     * @param capacity 队列容量，即最多缓冲多少条待写入的日志
     *                 太小：生产者频繁阻塞；太大：内存占用高
     *                 工业界一般设置为 1000~10000，根据业务量决定
     */
    public AsyncLogger(int capacity) {
        this.queue = new ArrayBlockingQueue<>(capacity);
    }

    /**
     * 记录一条 INFO 级别的日志。
     * 调用方无需关心日志如何被写入，只管调用这个方法。
     */
    public void info(String message) {
        log("INFO", message);
    }

    public void warn(String message) {
        log("WARN", message);
    }

    public void error(String message) {
        log("ERROR", message);
    }

    /**
     * 内部方法：把日志消息放入队列。
     *
     * <p>使用 put() 而不是 offer()：
     * - put()：队列满时阻塞等待，日志不丢失（我们需要这个行为）
     * - offer()：队列满时直接返回 false，日志丢失
     *
     * <p>InterruptedException：put() 是阻塞操作，等待期间可能被中断。
     * 这里选择恢复中断标志并打印警告，实际项目中可能需要更复杂的处理。
     */
    private void log(String level, String message) {
        try {
            queue.put(new LogMessage(level, message));
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            System.err.println("[AsyncLogger] 日志写入被中断，消息丢失：" + message);
        }
    }

    /**
     * 发送关闭信号：往队列里放一颗"毒丸"。
     * 消费者线程取到毒丸后会自动退出。
     *
     * <p>为什么需要这个方法？
     * 消费者线程在无限循环地 take()，不会自己停下来。
     * 调用此方法相当于告诉消费者："没有新任务了，处理完这个就退出吧。"
     *
     * @param consumerCount 消费者线程的数量，每个消费者需要一颗毒丸
     */
    public void shutdown(int consumerCount) throws InterruptedException {
        for (int i = 0; i < consumerCount; i++) {
            queue.put(LogMessage.POISON_PILL);
        }
    }

    /**
     * 暴露队列给消费者使用。
     * 消费者线程需要从这个队列里 take() 消息。
     */
    public BlockingQueue<LogMessage> getQueue() {
        return queue;
    }
}
