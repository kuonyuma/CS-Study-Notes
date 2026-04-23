public class Test {
    public static void main(String[] args) {
        MyCircularQueue myCircularQueue = new MyCircularQueue(5);
        myCircularQueue.enQueue(10);
        myCircularQueue.enQueue(20);
        myCircularQueue.enQueue(30);
        myCircularQueue.enQueue(40);
        boolean b = myCircularQueue.enQueue(50);
        boolean a = myCircularQueue.enQueue(60);
        System.out.println(a);
        System.out.println(b);
        System.out.println(myCircularQueue.Front());
        System.out.println(myCircularQueue.Front());
        System.out.println(myCircularQueue.Front());
        System.out.println(myCircularQueue.Front());




    }
}
