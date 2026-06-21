class Solution {
    public int subarraysDivByK(int[] nums, int k) {

        HashMap<Integer,Integer> map = new HashMap<>();
        int sum = 0, count = 0;
        map.put(0,1);
        for (int i = 0; i < nums.length; i++) {
            sum = sum + nums[i];
            int mod = (sum % k + k) % k;
            if(map.containsKey(mod)){
                count += map.get(mod);
            }
            map.compute(mod,(x,y)-> y == null ? 1 : y + 1);
        }
        return count;
    }
}