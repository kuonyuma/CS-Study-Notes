public class MyThreadPool {


    //创建一个阻塞队列
    private final MyQueue queue;
    //工作线程数组
    private  Thread[] works;
    //核心线程数量
    private int MyQueueCount;

    public MyThreadPool(int count){
        //阻塞队列固定大小为100
        queue = new MyQueue();
        MyQueueCount = count;
        works = new Thread[MyQueueCount];

        for (int i = 0; i < MyQueueCount; i++) {
            works[i] = new Thread(()->{


                try {
                    while(true){
                        Runnable task = queue.pop();
                        if(task != null){
                            task.run();
                        }
                    }
                } catch (InterruptedException e) {
                    throw new RuntimeException(e);
                }

                //具体逻辑
            },"线程 : " + i);
            works[i].start();


        }

    }
    //将任务扔进阻塞队列中
    public void submit(Runnable runnable) throws InterruptedException {
        queue.put(runnable);
    }

}

