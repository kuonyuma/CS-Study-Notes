package Trhead;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.ThreadPoolExecutor;

public class demo2 {
    private static int i = 0;
    public static void main(String[] args) {
        ExecutorService executorService = Executors.newCachedThreadPool();

        while(i++ < 100) {
            executorService.execute(() -> print());
        }
    }
    private static void print(){
        System.out.println("你好"+ i);
    }
}
