import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.util.concurrent.BlockingQueue;

/**
 * 日志写入线程（消费者）
 *
 * <p>职责：不断从队列里取出日志消息，写入本地文件。
 * 这个类本身就是一个线程（extends Thread），启动后在后台持续运行。
 *
 * <p>工作模式：
 *   while(true) {
 *       取出一条消息
 *       如果是毒丸 → 退出
 *       否则 → 写入文件
 *   }
 *
 * <p>为什么用 BufferedWriter 而不是直接用 FileWriter？
 * FileWriter 每次写入都直接操作磁盘，非常慢。
 * BufferedWriter 在内存里积攒一批数据，再一次性写入磁盘，大幅提升性能。
 * 这叫"批量刷盘"，是 I/O 操作的经典优化。
 */
public class LogWriter extends Thread {

    /**
     * 对LogWriter的总结
     * 1. 构造方法对阻塞队列，文件保存路径进行初始化。
     * 2. 从阻塞队列拿出一个元素，利用的take,如果队列为空这该线程阻塞等待。
     * 3. 这个类继承了Thread，重写了run方法。
     * 4. run方法的作用是，从阻塞队列中take一个元素，如果为空则等待，如果信息为毒丸，则不写进文件中。
     * 5. 为了加快读写的效率用上了BufferedWriter，先将文件写入内存中，在flush
     */

    private final BlockingQueue<LogMessage> queue;
    private final String logFilePath;

    /**
     * @param queue       从 AsyncLogger 获取的共享队列
     * @param logFilePath 日志文件保存路径
     * @param writerName  消费者线程名，方便在日志里区分多个消费者
     */
    public LogWriter(BlockingQueue<LogMessage> queue, String logFilePath, String writerName) {
        super(writerName); // 设置线程名
        this.queue = queue;
        this.logFilePath = logFilePath;
    }

    @Override
    public void run() {
        System.out.printf("[%s] 日志写入线程启动，写入文件：%s%n", getName(), logFilePath);

        // try-with-resources：BufferedWriter 用完自动关闭，不需要手动 close()
        // true 表示追加写入，不覆盖已有内容
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(logFilePath, true))) {

            while (true) {
                // take()：队列为空时，线程阻塞等待。这是消费者的核心行为。
                // 注意：take() 是可中断的，会抛出 InterruptedException
                LogMessage message = queue.take();

                // 收到毒丸：退出循环，线程结束
                if (message == LogMessage.POISON_PILL) {
                    System.out.printf("[%s] 收到关闭信号，退出%n", getName());
                    break;
                }

                // 写入一行日志
                writer.write(message.format());
                writer.newLine();

                // flush()：把缓冲区里的内容立刻写入磁盘。
                // 不 flush 的话，程序突然崩溃时缓冲区里的日志会丢失。
                // 工业界通常不是每条都 flush（太慢），而是定时批量 flush。
                // 这里每条都 flush 是为了方便你观察效果。
                writer.flush();

                System.out.printf("[%s] 写入日志：%s%n", getName(), message.format());
            }

        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            System.err.printf("[%s] 线程被中断%n", getName());
        } catch (IOException e) {
            System.err.printf("[%s] 文件写入失败：%s%n", getName(), e.getMessage());
        }

        System.out.printf("[%s] 日志写入线程已停止%n", getName());
    }
}
