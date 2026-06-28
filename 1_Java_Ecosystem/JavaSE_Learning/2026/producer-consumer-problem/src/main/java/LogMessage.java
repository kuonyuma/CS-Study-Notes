import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

/**
 * 日志消息（数据载体）
 *
 * <p>职责：表示"一条日志"长什么样，只装数据，不干任何事情。
 * 这种只有字段、没有业务逻辑的类叫做 VO（Value Object）。
 *
 * <p>特别注意：POISON_PILL（毒丸）
 * 这是一个特殊的单例常量，用来通知消费者线程"任务结束，该退出了"。
 * 消费者从队列取到这个对象时，判断 == POISON_PILL，然后退出循环。
 * 这个设计模式叫做"毒丸模式（Poison Pill）"，是优雅关闭消费者线程的标准做法。
 */
public class LogMessage {
    /**
     * 这个类的主要作用是定义了日志打印的格式。
     * 格式为：时间，线程名字，日志等级，日志内容。
     */

    private static final DateTimeFormatter FORMATTER =
            DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss.SSS");

    /**
     * 毒丸常量：用于通知消费者线程退出。
     * 用 null 也能实现类似效果，但毒丸更明确，语义更清晰。
     */
    public static final LogMessage POISON_PILL = new LogMessage("SYSTEM", "SHUTDOWN");

    /** 日志级别：INFO / WARN / ERROR */
    private final String level;

    /** 日志内容 */
    private final String message;

    /** 产生这条日志的线程名 */
    private final String threadName;

    /** 日志产生时间 */
    private final String timestamp;

    public LogMessage(String level, String message) {
        this.level = level;
        this.message = message;
        this.threadName = Thread.currentThread().getName();
        this.timestamp = LocalDateTime.now().format(FORMATTER);
    }

    /**
     * 格式化成可以直接写入文件的一行文本
     * 格式：[时间] [线程名] [级别] 消息内容
     */
    public String format() {
        return String.format("[%s] [%s] [%s] %s", timestamp, threadName, level, message);
    }

    public String getLevel() { return level; }
    public String getMessage() { return message; }
}
