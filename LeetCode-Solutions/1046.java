import java.util.PriorityQueue;

class Solution {
    public int lastStoneWeight(int[] stones) {
        
        PriorityQueue<Integer> heap = new PriorityQueue<>((Integer x, Integer y) -> y - x);
        
       
        for (int e : stones) {
            heap.add(e);
        }
        
        
        while (heap.size() > 1) {
           
            int num1 = heap.poll();
            int num2 = heap.poll();

            
            if (num1 != num2) {
                heap.add(num1 - num2);
            }
        }
        
       
        return heap.isEmpty() ? 0 : heap.peek();
    }
}