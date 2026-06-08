class KthLargest {
    PriorityQueue<Integer> heap;
    int _k;
    
    public KthLargest(int k, int[] nums) {
        //建立小根堆，只保留k个数字
        heap = new PriorityQueue<>();
        _k = k;
        for(int e: nums){
            heap.add(e);
            if(heap.size() > k){
                heap.poll();
            }
        }
    }
    
    public int add(int val) {
        //添加一个元素并弹出一个元素
        heap.add(val);
        while(heap.size() > _k){
            heap.poll();
        }
        return heap.peek();
    }
}

/**
 * Your KthLargest object will be instantiated and called as such:
 * KthLargest obj = new KthLargest(k, nums);
 * int param_1 = obj.add(val);
 */