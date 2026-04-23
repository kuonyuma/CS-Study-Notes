
class MyThread extends Thread{
    @Override
    public void run(){
        System.out.println("test");
    }

}

public class demo05 {
    public static void main(String[] args) {

        Thread t = new MyThread();
        t.start();

    }
}
