import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;


public class AsyncLogger {

    private final BlockingQueue<LogMessage> queue;


    public AsyncLogger(int capacity) {
        this.queue = new ArrayBlockingQueue<>(capacity);
    }

    public void info(String message) {
        log("INFO", message);
    }

    public void warn(String message) {
        log("WARN", message);
    }

    public void error(String message) {
        log("ERROR", message);
    }


    private void log(String level, String message) {
        try {
            queue.put(new LogMessage(level, message));
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            System.err.println("[AsyncLogger] 日志写入被中断，消息丢失：" + message);
        }
    }

    public void shutdown(int consumerCount) throws InterruptedException {
        for (int i = 0; i < consumerCount; i++) {
            queue.put(LogMessage.POISON_PILL);
        }
    }

    public BlockingQueue<LogMessage> getQueue() {
        return queue;
    }
}
