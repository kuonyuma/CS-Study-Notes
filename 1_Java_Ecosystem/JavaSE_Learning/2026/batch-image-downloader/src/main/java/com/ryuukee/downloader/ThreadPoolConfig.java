package com.ryuukee.downloader;

import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

/**
 * 线程池配置类
 *
 * <p>职责：统一管理线程池的创建，遵循阿里巴巴开发规范：
 * 禁止使用 Executors 工厂方法，必须手动 new ThreadPoolExecutor() 并明确每个参数。
 *
 * <p>为什么禁止 Executors.newFixedThreadPool()？
 * 因为它内部使用无界的 LinkedBlockingQueue，当任务提交速度大于消费速度时，
 * 队列会无限增长，最终导致内存溢出（OOM）。
 */
public class ThreadPoolConfig {

    // ===================== 线程池参数常量（先读懂这些，再看下面的方法）=====================

    /**
     * 核心线程数（corePoolSize）
     *
     * <p>线程池始终保持存活的线程数量，即使这些线程处于空闲状态也不会被销毁。
     *
     * <p>工业界怎么定这个值？
     * - I/O 密集型任务（比如我们的下载器）：线程大部分时间在等网络，CPU 空闲
     *   公式参考：核心线程数 = CPU核心数 * 2
     * - CPU 密集型任务（比如加密计算）：线程大部分时间在算，CPU 很忙
     *   公式参考：核心线程数 = CPU核心数 + 1
     *
     * <p>我们的机器 CPU 核心数：{@link Runtime#availableProcessors()}
     * 这里先固定写 10，方便你观察效果，之后可以改成动态获取。
     */
    private static final int CORE_POOL_SIZE = 10;

    /**
     * 最大线程数（maximumPoolSize）
     *
     * <p>当核心线程全忙、队列也满了，线程池才会创建额外的线程，
     * 但总数不能超过这个值。超过后触发拒绝策略。
     *
     * <p>对于下载场景，我们允许最多同时 20 个线程并发工作。
     */
    private static final int MAX_POOL_SIZE = 20;

    /**
     * 空闲线程存活时间（keepAliveTime + unit）
     *
     * <p>当线程数超过 corePoolSize，多余的空闲线程在等待 60 秒后会被销毁。
     * 核心线程默认不受此影响（除非设置 allowCoreThreadTimeOut(true)）。
     */
    private static final long KEEP_ALIVE_TIME = 60L;
    private static final TimeUnit TIME_UNIT = TimeUnit.SECONDS;

    /**
     * 任务队列容量（workQueue）
     *
     * <p>当所有核心线程都在工作时，新来的任务先进队列等待。
     * 我们用 ArrayBlockingQueue（有界队列，容量固定），
     * 这样当任务堆积过多时，会触发拒绝策略而不是无限占用内存。
     *
     * <p>队列容量设多少？这是个权衡：
     * - 太大：内存占用高，但任务不容易被拒绝
     * - 太小：容易触发拒绝策略
     * 这里设为 50，你之后可以把它改小（比如 2），故意触发拒绝策略来观察效果。
     */
    private static final int QUEUE_CAPACITY = 50;

    /**
     * 创建并返回一个用于图片下载的线程池
     *
     * <p>拒绝策略使用 CallerRunsPolicy：
     * 当队列满、线程数也达到上限时，不抛异常也不丢弃任务，
     * 而是让"提交任务的那个线程"（通常是主线程）自己来执行这个任务。
     * 相当于一种"背压"机制，自动减慢任务提交速度。
     *
     * @return 配置好的 ThreadPoolExecutor 实例
     */
    public static ThreadPoolExecutor createDownloadPool() {
        return new ThreadPoolExecutor(
                CORE_POOL_SIZE,                          // 参数1：核心线程数
                MAX_POOL_SIZE,                           // 参数2：最大线程数
                KEEP_ALIVE_TIME,                         // 参数3：空闲存活时间
                TIME_UNIT,                               // 参数4：时间单位
                new ArrayBlockingQueue<>(QUEUE_CAPACITY),// 参数5：任务队列（有界）
                new ThreadPoolExecutor.CallerRunsPolicy()// 参数6（+7）：拒绝策略（ThreadFactory 用默认）
        );
    }
}
