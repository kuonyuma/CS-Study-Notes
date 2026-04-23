public class demo2 {

    public static void main(String[] args) throws InterruptedException {

//        Thread t = new Thread(()->{
//            System.out.println(t.isDaemon());
//        },"枣子姐");

        Thread t = new Thread(){
            @Override
          public void run(){
                while(!this.isInterrupted()){
                    System.out.println(this.isAlive());

                }
                System.out.println("线程结束");
          }
        };

        t.start();
        Thread.sleep(5000);
        t.interrupt();

        //输出 false;

    }
}
class MyThread extends Thread{
    @Override
    public void run(){
        System.out.println(this.isDaemon());
    }
}
