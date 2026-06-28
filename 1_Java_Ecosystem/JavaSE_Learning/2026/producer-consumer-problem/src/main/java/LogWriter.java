import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.util.concurrent.BlockingDeque;
import java.util.concurrent.BlockingQueue;

public class LogWriter extends Thread{

    private final BlockingQueue<LogMessage> blockingQueue;
    private final String path;

    public LogWriter(BlockingQueue<LogMessage> queue, String ThreadName, String path){
        super(ThreadName);
        this.blockingQueue = queue;
        this.path = path;

    }
    @Override
    public void run(){
        try(BufferedWriter input = new BufferedWriter(new FileWriter(path,true))){
            while(true){

                LogMessage message = blockingQueue.take();
                System.out.println(Thread.currentThread().getName()+"拿到信息开始写入...");

                if(message == LogMessage.POISON_PILL){
                    System.out.println(Thread.currentThread().getName()+"结束运行");
                    break;
                }
                input.write(message.format());
                input.newLine();
                input.flush();
                System.out.println(Thread.currentThread().getName()+"写入完毕...");

            }
        }catch(IOException e){
            e.getMessage();//随便处理一下，实则没处理.
        }catch(InterruptedException e){
            e.getMessage();
        }
    }
}