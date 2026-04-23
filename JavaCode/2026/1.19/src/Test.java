public class Test {

    public static void main(String[] args) {
        int[] array = { 27,15,19,18,28,34,65,49,25,37 };

        MyPriorityQueue heap = new MyPriorityQueue();
         array = heap.heapSort(array);
        for(int a: array){
            System.out.print(a+" ");
        }
    }

    public static void main1(String[] args) {
        int[] array = { 27,15,19,18,28,34,65,49,25,37 };

        MyPriorityQueue heap = new MyPriorityQueue();
        heap.initHeap(array);
        int a = heap.poll();
        System.out.println(a);
    }
}
