import java.util.concurrent.Executor;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.ThreadFactory;

public class MainTest {

    static void main() throws InterruptedException {
        System.out.println("开始测试");
//        t1();
//        t2();
//        t3();
//        t4();
        t5();
        System.out.println("结束测试");
    }

    static void t1(){
        Student st1 = new Student("张三","123@qq.com",18);

        Integer age = st1.age();
        String name = st1.name();

        System.out.println(st1.email()+" "+age + " "+name);
    }

    static void t2(){
        Student stu1 = new Student("test","test@qq.com",1);
        Student stu2 = new Student("test","test@qq.com",2);
        boolean res = stu1.equals(stu2);
        System.out.println(res);
    }
    static void t3(){
        Student stu = new Student(null,"test@qq.com",2);
        System.out.println(stu.name());
    }

    static void t4() throws InterruptedException {
        Thread vtStarted = Thread.ofVirtual()
            .name("mia")
            .start(()->{
                System.out.println(Thread.currentThread().getName()+"第一个虚拟线程启动:" + Thread.currentThread().isVirtual());
            });
        vtStarted.join();
    }
    static void t5() throws InterruptedException {
        ThreadFactory myFactory = Thread.ofVirtual()
            .name("虚拟线程", 1)
            .factory();

        long begin = System.currentTimeMillis();
        try (ExecutorService executorService =
                 Executors.newThreadPerTaskExecutor(myFactory)) {
            for (int i = 0; i < 4000000; i++) {
                executorService.submit(() -> {

                    String name = Thread.currentThread().getName();
                    try {
                        Thread.sleep(10000);
                    } catch (InterruptedException e) {
                        throw new RuntimeException(e);
                    }
                });
            }
        }
        long end = System.currentTimeMillis();

        System.out.println("2百万个线程同时创建消耗了"+(end - begin) / 1000.0 + " 秒");
        //Executor
        ExecutorService

    }

}
