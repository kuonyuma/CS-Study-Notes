import com.ryuukee.downloader.ThreadPoolConfig;

import java.util.concurrent.*;

public class Main {

    public static void main() {
        t1();
        System.out.println("测试结束");
    }

    static void t1(){

        Callable<String> callable = ()->{
            System.out.println("正在执行任务...");
            Thread.sleep(1000);
            return "执行完毕";
        };
        FutureTask<String> future = new FutureTask<>(callable);
        new Thread(future).start();
        try {
            String res = future.get();
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        } catch (ExecutionException e) {
            throw new RuntimeException(e);
        }

    }
    static void t2(){
        ThreadPoolExecutor pool = ThreadPoolConfig.createDownloadPool();

    }


}


