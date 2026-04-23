package demo2;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.Queue;
import java.util.Stack;

public class Test {
    public static void main(String[] args) {

    }
    public static void main2(String[] args) {
        Queue<Integer> queue = new LinkedList<>();
        queue.offer(1);
        queue.offer(2);
        queue.offer(3);
        queue.offer(4);
        System.out.println(queue.poll());
        System.out.println(queue.poll());
        System.out.println(queue.peek());
        System.out.println(queue.peek());
        queue.clear();



    }
    public static void main1(String[] args) {
        MyCircularQueue test= new MyCircularQueue(3);
        test.enQueue(1);
        test.enQueue(2);
        test.enQueue(3);
        test.enQueue(4);


    }

}

