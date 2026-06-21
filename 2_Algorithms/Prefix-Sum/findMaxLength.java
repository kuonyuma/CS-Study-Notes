class Solution {
    public int findMaxLength(int[] nums) {
        int[] array = java.util.Arrays.stream(nums)
                .map(n -> n == 0 ? -1 : n)
                .toArray();
        HashMap<Integer,Integer> hashMap = new HashMap<>();
        int sum = 0,maxlen = -1;
        hashMap.put(0,-1);
        for (int i = 0; i <nums.length ; i++) {
            sum  = sum + array[i];
            int buf = sum;
            if(hashMap.containsKey(buf)){
                maxlen = Math.max(maxlen,(i - hashMap.get(sum)));
            }
            if(!hashMap.containsKey(sum)){
                hashMap.put(sum,i);
            }
        }
        return maxlen == -1 ? 0 : maxlen;
    }
}