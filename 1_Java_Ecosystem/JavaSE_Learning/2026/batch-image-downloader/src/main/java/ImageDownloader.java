import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Future;
import java.util.concurrent.ThreadPoolExecutor;

/**
 * 批量图片下载器 —— 程序主入口
 *
 * <p>职责：把其他三个类"组装"在一起，协调整个下载流程。
 * 这个类自己不做任何下载，只负责"指挥"。
 *
 * <p>整体流程：
 *   1. 准备图片 URL 列表
 *   2. 创建线程池（ThreadPoolConfig）
 *   3. 把每个 URL 封装成 DownloadTask，提交给线程池
 *   4. 收集每个任务的 Future，等待全部完成
 *   5. 通过 Future 获取结果，更新 ProgressTracker
 *   6. 打印总耗时，关闭线程池
 */
public class ImageDownloader {

    /** 图片保存到本地的目录 */
    private static final String SAVE_DIR = "downloaded_images";

    public static void main(String[] args) throws InterruptedException {

        // ===== 第一步：准备图片 URL 列表 =====
        // 使用 picsum.photos 提供的随机图片服务，seed 不同图片不同，非常适合测试
        List<String> imageUrls = buildImageUrls(35);
        int total = imageUrls.size();
        System.out.printf("📋 共 %d 张图片待下载，开始任务...%n%n", total);

        // ===== 第二步：初始化进度追踪器 =====
        ProgressTracker tracker = new ProgressTracker(total);

        // ===== 第三步：创建线程池 =====
        ThreadPoolExecutor pool = ThreadPoolConfig.creatDownLoadPool();

        // ===== 第四步：提交所有任务，收集 Future =====
        // Future<Boolean> 是一张"任务收据"，代表"将来某时刻"的执行结果
        // 此刻任务只是被提交进线程池，还没开始执行（或者已经在后台执行了）
        List<Future<Boolean>> futures = new ArrayList<>();

        long startTime = System.currentTimeMillis();

        for (int i = 0; i < imageUrls.size(); i++) {
            DownloadTask task = new DownloadTask(imageUrls.get(i), SAVE_DIR, i + 1);
            Future<Boolean> future = pool.submit(task); // 提交任务，立刻返回 Future
            futures.add(future);
        }

        System.out.printf("%n🚀 %d 个任务已全部提交到线程池，等待执行完毕...%n%n", total);

        // ===== 第五步：等待所有任务完成，统计结果 =====
        // 遍历所有 Future，调用 get() 等待每个任务的结果
        // 注意：future.get() 会阻塞当前线程（主线程），直到这个任务执行完毕
        for (int i = 0; i < futures.size(); i++) {
            try {
                // get(10, TimeUnit.SECONDS)：最多等 10 秒，防止某个任务永久卡住
                boolean success = futures.get(i).get(10, java.util.concurrent.TimeUnit.SECONDS);
                if (success) {
                    tracker.recordSuccess();
                } else {
                    tracker.recordFailure();
                }
            } catch (java.util.concurrent.ExecutionException e) {
                // 任务内部抛出了未捕获的异常（理论上不应发生，因为 DownloadTask 内部已 catch）
                System.err.printf("[任务 #%d] 发生意外异常: %s%n", i + 1, e.getCause().getMessage());
                tracker.recordFailure();
            } catch (java.util.concurrent.TimeoutException e) {
                // 超过 10 秒还没完成，视为失败
                System.err.printf("[任务 #%d] 超时，跳过%n", i + 1);
                futures.get(i).cancel(true); // 取消这个任务
                tracker.recordFailure();
            }
        }

        long elapsed = System.currentTimeMillis() - startTime;

        // ===== 第六步：打印汇总，关闭线程池 =====
        tracker.printSummary(elapsed);

        // shutdown()：优雅关闭，等待已提交的任务全部执行完，再关闭线程池
        // 注意：不是 shutdownNow()（那个会强行中断正在运行的任务）
        pool.shutdown();
        System.out.println("✅ 线程池已关闭");
    }

    /**
     * 生成测试用的图片 URL 列表
     *
     * <p>使用 picsum.photos 服务：
     * https://picsum.photos/seed/{seed}/400/300
     * seed 相同则图片相同，seed 不同则图片不同，非常稳定适合测试。
     *
     * @param count 生成的 URL 数量
     * @return URL 列表
     */
    private static List<String> buildImageUrls(int count) {
        List<String> urls = new ArrayList<>();
        for (int i = 1; i <= count; i++) {
            // 使用 seed 让每个 URL 对应不同的图片
            urls.add("https://picsum.photos/seed/" + i + "/400/300");
        }
        return urls;
    }
}
