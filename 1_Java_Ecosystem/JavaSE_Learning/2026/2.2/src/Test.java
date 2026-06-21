import java.util.*;

class Node {
    int val;
    Node next;
    Node random;

    public Node(int val) {
        this.val = val;
        this.next = null;
        this.random = null;
    }
}

public class Test {
    public static void main1(String[] args) {
       int count= numJewelsInStones("aA","aAAbbbb");
        System.out.println(count);
    }




    public static int singleNumber(int[] nums) {
        Set<Integer> set = new HashSet<>();
        for (int x:nums) {
            if(set.contains(x)){
                set.remove(x);
            }else{
                set.add(x);
            }
        }
        return set.iterator().next();
    }

    public static Node copyRandomList(Node head) {
        Map<Node,Node> hashmap = new HashMap<>();
        Node cur = head;
        while(cur != null){
            Node copynode = new Node(cur.val);
            hashmap.put(cur,copynode);
            cur = cur.next;
        }
        cur = head;
        while(cur != null){
            hashmap.get(cur).next = hashmap.get(cur.next);
            hashmap.get(cur).random = hashmap.get(cur.random);
            cur =cur.next;
        }
        return hashmap.get(head);
    }

    public static int numJewelsInStones(String jewels, String stones) {
        Set<Character> set = new HashSet<>();
        int count = 0;
        for (int i = 0; i < jewels.length(); i++) {
            Character ch = jewels.charAt(i);
            set.add(ch);
        }
        for (int i = 0; i < stones.length(); i++) {
            Character ch = stones.charAt(i);
            if(set.contains(ch)){
                count++;
            }
        }
        return count;
    }

    public static void main(String[] args) {
        String[] words={"the","day","is","sunny","the","the","the","sunny","is","is"};
        topKFrequent(words,7);
    }

    public static List<String> topKFrequent(String[] words, int k) {
        //1.统计单词的次数
        Map<String, Integer> map = new HashMap<>();
        for (String str : words) {
            if (!map.containsKey(str)) {
                map.put(str, 1);
            } else {
                int val = map.get(str) + 1;
                map.put(str, val);
            }
        }

        //2.用堆排,频率高的优先出，相同频率按字典顺序出
        PriorityQueue<String> minheap = new PriorityQueue<>(
                new Comparator<String>() {
                    @Override
                    public int compare(String o1, String o2) {
                        int count1 = map.get(o1);
                        int count2 = map.get(o2);
                        if(count2 != count1){
                            return count1 - count2;
                        }else{
                            return o2.compareTo(o1);
                        }
                    }
                }
        );

        for (String word : map.keySet()) {
            minheap.offer(word);
            if (minheap.size() > k) {
                minheap.poll();
            }
        }
        //取出数据
        List<String> list = new ArrayList<>();
        while(!minheap.isEmpty()){
            list.add(minheap.poll());
        }
        Collections.reverse(list);
        return list;
    }

}






