class Solution {
    public int totalFruit(int[] fruits) {
        int left = 0, right = 0, count = 0,len = 0;
        Map<Integer,Integer> hashmap = new HashMap<>();
        for( ; right < fruits.length ; right++){
            if(!hashmap.containsKey(fruits[right])){
                count++;
            }
            hashmap.compute(fruits[right],(k,v) -> v == null ? 1:v+1);
            while(count > 2){
                hashmap.put(fruits[left],hashmap.get(fruits[left]) -1);
                if(hashmap.get(fruits[left]) == 0){
                    hashmap.remove(fruits[left]);
                    count--;
                }
                left++;
            }
            len = Math.max(len,right - left +1);
        }
        return len;
    }
}