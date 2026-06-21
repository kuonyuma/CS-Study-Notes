public class demo3 {
    public static void main(String[] args) throws InterruptedException {

        Thread t1 = new Thread(()->{
            while(true){
                System.out.println("我是折纸");
                try{
                    Thread.sleep(3000);
                } catch (InterruptedException e) {
                    throw new RuntimeException(e);
                }
            }
        },"折纸");

        Thread t2 = new Thread(()->{
            while(true){
                System.out.println("我是三叶");
                try{
                    Thread.sleep(3000);
                } catch (InterruptedException e) {
                    throw new RuntimeException(e);
                }
            }
        },"三叶");

        Thread t = new Thread(() -> {
            System.out.println("快递开始派送");

            for(int i = 0; i < 3;i++) {

                System.out.println("派送中...");
                try {
                    Thread.sleep(1000); // 模拟任务耗时
                } catch (InterruptedException e) {
                }
            }
            System.out.println("快递到了");
        });

        t.start();
        //join
        t.join();
        // 我猜快递 3 秒后能到，那我就睡 3 秒
        //Thread.sleep(3000);
        System.out.println("取快递");

    }
}
