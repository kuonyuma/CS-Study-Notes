class Solution {


    public List<String> topKFrequent(String[] words, int k) {
        //统计单词个数
        HashMap<String,Integer> map = new HashMap<>();
        for(String e : words){
            map.compute(e,(_k,v) -> v == null ? 1:v + 1); 
        }

        //创建一个容量为k的小根堆
        PriorityQueue<String> heap = new PriorityQueue<>((String x,String y)->{
            if(!map.get(x).equals(map.get(y))){
                return map.get(x) - map.get(y);
            }
            return y.compareTo(x);
        });
        //遍历哈希表w
         for (String word : map.keySet()) {
            heap.offer(word);
            if (heap.size() > k) heap.poll();
        }
        //将剩余元素传递给数组
        List<String> arr = new ArrayList<>();
        while(!heap.isEmpty()){
            arr.add(heap.poll());
        }
       Collections.reverse(arr);
return arr;


    }
}