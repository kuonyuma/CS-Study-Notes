public class MyQueue {
    //队列的最大容量
    private final int capcity = 100;
    //循环队列
    private int slow,fast = 0;
    //队列的实际元素个数
    private int size = 0;
    //基于数组实现
    Runnable[] queue;

    public MyQueue(){
        queue = new Runnable[capcity];
    }

    //put
    public void put(Runnable runnable) throws InterruptedException {
        synchronized(this){
            while(size == capcity){
                this.wait();
            }
            queue[fast] = runnable;
            fast = (fast + 1) % capcity;
            size++;
            this.notifyAll();
        }
    }

    //pop
    public Runnable pop() throws InterruptedException {
        synchronized(this){
            while(size == 0){
                this.wait();
            }
            Runnable buf = queue[slow];
            slow = (slow + 1) % capcity;
            size--;
            this.notifyAll();
            return buf;
        }
    }
    //peek
    public synchronized Runnable  peek() throws InterruptedException {
        while(size == 0){
            wait();
        }
        return queue[slow];
    }
}
