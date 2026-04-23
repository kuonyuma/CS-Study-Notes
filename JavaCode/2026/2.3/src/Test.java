import java.util.*;

public class Test {


    public static List<String> topKFrequent(String[] words, int k) {
        //先统计每个单词出现的次数
        Map<String, Integer> map = new HashMap<>();
        for(String str: words){
            if(!map.containsKey(str)){
                map.put(str,1);
            }else{
                int val = map.get(str) + 1;
                map.put(str,val);
            }
        }

        //利用堆排序
        PriorityQueue<String>minheap = new PriorityQueue<>(
                new Comparator<String>() {
                    @Override
                    public int compare(String o1,String o2) {
                        int count_o1 = map.get(o1);
                        int count_o2 = map.get(o2);
                        if(count_o2 != count_o1){
                            return count_o1 - count_o2;
                        }
                        else{
                            return o2.compareTo(o1);
                        }
                    }
                }
        );

        for(String word : map.keySet()){

            minheap.offer(word);

            if(minheap.size() > k){
                minheap.poll();
            }
        }

        //输出

        List<String> list = new ArrayList<>();
        while(!minheap.isEmpty()){
            list.add(minheap.poll());
        }
       Collections.reverse(list);
        return list;
    }

    public static void main(String[] args) {

    }
}
