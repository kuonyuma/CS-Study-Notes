//TIP To <b>Run</b> code, press <shortcut actionId="Run"/> or
// click the <icon src="AllIcons.Actions.Execute"/> icon in the gutter.
public class Main {
    public static void main(String[] args) {
        Thread t = new Thread("枣子姐"){
            @Override
           public void run() {
                try{
                    while(true){
                        System.out.println(" hello Thread");
                        Thread.sleep(1000);
                    }
                } catch (InterruptedException e) {
                    throw new RuntimeException(e);
                }
            }
        };

        Thread t2 = new Thread(
                ()->{
                    try{
                        while(true){
                            System.out.println("hello Thread");
                            Thread.sleep(1000);
                        }
                    } catch (Exception e) {
                        throw new RuntimeException(e);
                    }

                }

        );
        t.start();
        try {

            while (true) {
                System.out.println("hello main");
                Thread.sleep(1000);
            }
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
    }
}