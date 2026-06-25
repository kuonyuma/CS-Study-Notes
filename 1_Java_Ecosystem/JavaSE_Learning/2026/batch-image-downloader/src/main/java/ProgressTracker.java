import java.util.concurrent.atomic.AtomicInteger;

/**
 * 进度追踪器（线程安全）
 *
 * <p>职责：在多线程环境下，安全地统计"已完成下载的图片数量"，并打印进度。
 *
 * <p>核心问题：为什么不直接用 int count++？
 *
 * <p>因为 count++ 在 JVM 里不是一个原子操作，它其实分三步：
 *   第一步：从内存读取 count 的值
 *   第二步：把值加 1
 *   第三步：把新值写回内存
 *
 * <p>想象两个线程同时执行 count++，当前 count = 5：
 *   线程A 读到 5，还没来得及写回 6
 *   线程B 也读到 5，也计算出 6
 *   线程A 写回 6，线程B 也写回 6
 *   结果 count = 6，但实际上执行了两次++，应该是 7！
 *   这就是"线程安全问题"，也叫"竞态条件（Race Condition）"。
 *
 * <p>解决方案：用 AtomicInteger 代替 int。
 * AtomicInteger 的 incrementAndGet() 是原子操作，底层用 CAS（Compare-And-Swap）
 * 指令保证多线程下操作的正确性，且不需要加锁，性能比 synchronized 更好。
 */
public class ProgressTracker {

    /** 已成功下载的数量，使用 AtomicInteger 保证线程安全 */
    private final AtomicInteger successCount = new AtomicInteger(0);

    /** 下载失败的数量 */
    private final AtomicInteger failCount = new AtomicInteger(0);

    /** 总任务数，用于计算进度百分比 */
    private final int totalCount;

    public ProgressTracker(int totalCount) {
        this.totalCount = totalCount;
    }

    /**
     * 记录一次成功下载，并打印当前进度。
     *
     * <p>incrementAndGet()：先加 1，再返回新值（相当于 ++count）
     * getAndIncrement()：先返回当前值，再加 1（相当于 count++）
     * 这里用 incrementAndGet 是因为我们需要的是加完之后的最新数字。
     */
    public void recordSuccess() {
        int current = successCount.incrementAndGet();
        int done = current + failCount.get();
        System.out.printf("📦 进度: %d / %d（成功 %d，失败 %d）%n",
                done, totalCount, current, failCount.get());
    }

    /**
     * 记录一次失败下载，并打印当前进度。
     */
    public void recordFailure() {
        failCount.incrementAndGet();
        int done = successCount.get() + failCount.get();
        System.out.printf("📦 进度: %d / %d（成功 %d，失败 %d）%n",
                done, totalCount, successCount.get(), failCount.get());
    }

    /**
     * 打印最终汇总报告
     */
    public void printSummary(long elapsedMillis) {
        System.out.println("\n========== 下载完成 ==========");
        System.out.printf("总任务数：%d%n", totalCount);
        System.out.printf("成功：%d  失败：%d%n", successCount.get(), failCount.get());
        System.out.printf("总耗时：%.2f 秒%n", elapsedMillis / 1000.0);
        System.out.println("==============================");
    }
}
